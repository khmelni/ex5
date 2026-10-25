package ai.ui.assets;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import shared.AssetRegistry;

/**
 * אחראית על דבר אחד: לנגן צלילים לפי מפתח מ-AssetRegistry (כמו ImageLoader לתמונות).
 *
 * - מוזיקת רקע: playLoop / stopLoop (בלולאה, מההתחלה בכל הפעלה).
 * - צליל חד-פעמי (חבטה, הפסד): playOnce.
 *
 * כל הפעולות רצות על thread יחיד ייעודי: ה-UI והציור לא נתקעים בזמן טעינת קובץ,
 * הפעולות מתבצעות לפי הסדר (stopLoop ואז playOnce נשמעים בסדר הזה),
 * ואין צורך בנעילות. אם הצליל נכשל (אין התקן שמע, קובץ חסר) - רושמים ללוג והמשחק ממשיך בלי קול.
 */
public final class SoundPlayer {

    private static final ExecutorService audioThread = Executors.newSingleThreadExecutor(task -> {
        Thread thread = new Thread(task, "sound-player");
        thread.setDaemon(true); // לא מונע מהתוכנית להיסגר
        return thread;
    });

    // המצב הבא נגיש רק מתוך audioThread
    private static final Map<String, Clip> clips = new HashMap<>();
    private static final Set<String> failedKeys = new HashSet<>();
    private static Clip musicClip;

    private SoundPlayer() {}

    /** טוענת צלילים מראש, כדי שההשמעה הראשונה לא תתעכב. */
    public static void preload(String... soundKeys) {
        audioThread.execute(() -> {
            for (String soundKey : soundKeys) {
                getClip(soundKey);
            }
        });
    }

    /** מנגנת צליל בלולאה אינסופית מההתחלה (מוזיקת רקע). מוזיקה קודמת נעצרת. */
    public static void playLoop(String soundKey) {
        audioThread.execute(() -> {
            stopMusicNow();
            Clip clip = getClip(soundKey);
            if (clip == null) return;
            clip.setFramePosition(0);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            musicClip = clip;
        });
    }

    /** עוצרת את מוזיקת הרקע. */
    public static void stopLoop() {
        audioThread.execute(SoundPlayer::stopMusicNow);
    }

    /** מנגנת צליל פעם אחת. אם הוא כבר מתנגן (לחיצות מהירות) - מתחיל מההתחלה. */
    public static void playOnce(String soundKey) {
        audioThread.execute(() -> {
            Clip clip = getClip(soundKey);
            if (clip == null) return;
            clip.stop();
            clip.setFramePosition(0);
            clip.start();
        });
    }

    private static void stopMusicNow() {
        if (musicClip != null) {
            musicClip.stop();
            musicClip = null;
        }
    }

    /** מחזירה את ה-Clip של המפתח (נטען פעם אחת ונשמר), או null אם הטעינה נכשלה. */
    private static Clip getClip(String soundKey) {
        Clip clip = clips.get(soundKey);
        if (clip != null) return clip;
        if (failedKeys.contains(soundKey)) return null; // לא מנסים שוב ושוב ולא מציפים את הלוג

        Clip newClip = null;
        try {
            File file = new File(AssetRegistry.get(soundKey));
            try (AudioInputStream stream = AudioSystem.getAudioInputStream(file)) {
                newClip = AudioSystem.getClip();
                newClip.open(stream); // ה-Clip טוען את כל הצליל לזיכרון
            }
            clips.put(soundKey, newClip);
            return newClip;
        } catch (Exception e) {
            if (newClip != null) newClip.close();
            failedKeys.add(soundKey);
            System.out.println("LOG: לא הצלחתי לנגן צליל '" + soundKey + "': " + e);
            return null;
        }
    }
}
