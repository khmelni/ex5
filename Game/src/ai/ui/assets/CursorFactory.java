package ai.ui.assets;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;

/** יוצרת סמן עכבר מותאם אישית מתמונה (למשל פומבה). */
public final class CursorFactory {

    private CursorFactory() {}

    /** מחזירה סמן מתמונת ה-assetKey, או null אם הטעינה נכשלה. */
    public static Cursor createFromAsset(String assetKey, String cursorName) {
        Image original = ImageLoader.loadImage(assetKey);
        if (original == null) return null;

        // גודל אופטימלי לסמן שהמערכת תומכת בו
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension bestSize = toolkit.getBestCursorSize(48, 48);
        Image scaled = original.getScaledInstance(
                (int) bestSize.getWidth(), (int) bestSize.getHeight(), Image.SCALE_SMOOTH);

        // נקודת ה"קליק" בתוך התמונה (0,0 = פינה שמאלית עליונה)
        return toolkit.createCustomCursor(scaled, new Point(0, 0), cursorName);
    }
}
