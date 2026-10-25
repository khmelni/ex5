package shared.ui_ports;

import shared.BoardLayout;

public abstract class GameUiPort {

    private static GameUiPort instance;

    public static void setInstance(GameUiPort ui) {
        if (ui == null) throw new IllegalArgumentException("GameUiPort instance cannot be null");
        if (instance != null) throw new IllegalStateException("GameUiPort instance already set");
        instance = ui;
    }

    public static GameUiPort getInstance() {
        if (instance == null) throw new IllegalStateException("GameUiPort instance not set yet");
        return instance;
    }

    public abstract void spawnCharacter(int id, String assetKey, double x, double y);
    public abstract void removeCharacter(int id);
    public abstract void updateStats(int score, int lives);
    public abstract void setStatusText(String text);

    // בניית הלוח מחדש (הלוח גדל / איפוס): ה-Backend שולח את ההגדרה וה-UI מצייר
    public abstract void setupBoard(BoardLayout layout);
    
    // מתודה חדשה עבור הדיאלוג המותאם
    public abstract void showGameOverDialog();

    // מתודה ישנה בשימוש
    public abstract void hideRestartButton();

    // ---- צלילים: ה-Backend מחליט מתי לנגן, וה-UI מנגן בפועל ----

    // מתחיל לנגן את מוזיקת הרקע (בלולאה) מההתחלה
    public abstract void startMusic();

    // עוצר את מוזיקת הרקע
    public abstract void stopMusic();

    // מנגן צליל חד-פעמי לפי מפתח מ-AssetRegistry (למשל AssetRegistry.hammerHit)
    public abstract void playSound(String soundKey);
}
