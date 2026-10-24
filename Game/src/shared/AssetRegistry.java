package shared;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * AssetRegistry maps symbolic keys to actual asset files.
 *
 * The backend must NEVER use raw URLs.
 * The UI reads this registry and loads the real images.
 */
public final class AssetRegistry {

    // ----- מפתחות סימבוליים ציבוריים (בשימוש ה-Backend וה-UI) -----

    public static final String gameBackground = "game_background"; // רקע המשחק / לוח החורים
    public static final String life = "Heart";                       // סמל הלב/חיים
    public static final String timon = "timon";                     // סוריקטה רגילה (טימון)
    public static final String lifeGift = "life_gift";             // מתנת חיים
    public static final String lifeObstacle = "life_obstacle";     // מכשול חיים
    public static final String scoreObstacle = "score_obstacle";   // מכשול ניקוד
    public static final String hole = "hole";                       // חור (מצויר בכל מיקום חור)
    public static final String hammer = "hammer";  // פטיש הלחצן של העכבר

    // ----- Internal storage -----

    private static final Map<String, String> assets;

    static {
        Map<String, String> map = new HashMap<>();

        // -------------------------------
        // רקעים ולוח המשחק
        // -------------------------------
        map.put(gameBackground, "resources/Board_no_holes.png");
        map.put(life, "resources/Heart.png");
        map.put(hole, "resources/hole.png");

        // -------------------------------
        // דמויות וסוריקטות (Sprites)
        // -------------------------------
        map.put(timon, "resources/timon.png");
        map.put(lifeGift, "resources/Heart.png");
        map.put(lifeObstacle, "resources/LifeObstacle.png");
        map.put(scoreObstacle, "resources/ScoreObstacle.png");
        map.put(hammer, "resources/Hammer.png");

        assets = Collections.unmodifiableMap(map);
    }

    /**
     * Returns the URL or local path associated with a symbolic key.
     */
    public static String get(String key) {
        String value = assets.get(key);
        if (value == null) {
            throw new IllegalArgumentException("No asset registered for key: " + key);
        }
        return value;
    }

    /**
     * Returns true if the registry contains the key.
     */
    public static boolean contains(String key) {
        return assets.containsKey(key);
    }
}
