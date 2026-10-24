package ai.ui.assets;

import java.awt.Image;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import shared.AssetRegistry;

/**
 * אחראית על דבר אחד: לטעון תמונה לפי מפתח מ-AssetRegistry
 * (במקור או מוקטנת לגודל מבוקש).
 */
public final class ImageLoader {

    private ImageLoader() {}

    /** טוענת את התמונה כמו שהיא, או מחזירה null אם הטעינה נכשלה. */
    public static Image loadImage(String assetKey) {
        try {
            String path = AssetRegistry.get(assetKey);
            return ImageIO.read(new File(path));
        } catch (Exception e) {
            System.out.println("LOG: לא הצלחתי לטעון תמונה '" + assetKey + "': " + e.getMessage());
            return null;
        }
    }

    /** מחזירה אייקון בגודל width x height, או null אם הטעינה נכשלה. */
    public static ImageIcon loadScaledIcon(String assetKey, int width, int height) {
        Image original = loadImage(assetKey);
        if (original == null) return null;
        return new ImageIcon(original.getScaledInstance(width, height, Image.SCALE_SMOOTH));
    }
}
