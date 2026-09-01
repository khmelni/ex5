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

    public static final String GAME_BACKGROUND = "game_background"; // רקע המשחק / לוח החורים
    public static final String LIFE = "Heart";                       // סמל הלב/חיים
    public static final String TIMON = "timon";                     // סוריקטה רגילה (טימון)
    public static final String LIFE_GIFT = "life_gift";             // מתנת חיים
    public static final String LIFE_OBSTACLE = "life_obstacle";     // מכשול חיים
    public static final String SCORE_OBSTACLE = "score_obstacle";   // מכשול ניקוד

    // ----- Internal storage -----

    private static final Map<String, String> assets;

    static {
        Map<String, String> map = new HashMap<>();

        // -------------------------------
        // רקעים ולוח המשחק
        // -------------------------------
        map.put(GAME_BACKGROUND, "resources/whack_a_mole_board.jpg");
        map.put(LIFE, "resources/Heart.jpg");

        // -------------------------------
        // דמויות וסוריקטות (Sprites)
        // -------------------------------
        map.put(TIMON, "resources/timon.png");
        map.put(LIFE_GIFT, "resources/Heart.jpg");
        map.put(LIFE_OBSTACLE, "resources/LifeObstacle.jpg");
        map.put(SCORE_OBSTACLE, "resources/explosion.jpg");

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
