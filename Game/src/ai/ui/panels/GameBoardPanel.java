package ai.ui.panels;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ai.ui.assets.ImageLoader;
import shared.AssetRegistry;
import shared.BoardLayout;
import shared.HoleLayout;

/**
 * לוח המשחק (UI בלבד): מצייר את הרקע, את החורים ואת הדמויות לפי BoardLayout שה-Backend שולח,
 * ומדווח על לחיצות.
 *
 * ה-UI לא מכיר גיאומטריה ולא בודק פגיעה:
 *  - מספר החורים, מיקומם וגדלם מגיעים מה-Backend (configure) ויכולים להשתנות בזמן ריצה.
 *  - לחיצה מדווחת רק כמיקום יחסי בתוך הפאנל (0..1 בכל ציר); ה-Backend מחליט מה נפגע.
 *
 * כל המתודות נקראות מה-EDT (Ui עוטף אותן ב-invokeLater).
 */
public class GameBoardPanel extends JPanel {

    private BoardLayout layout;

    private JLabel[] characterLabels;
    private String[] assetKeys;   // null = החור ריק
    private double[] characterX;  // מרכז הדמות, במערכת הקואורדינטות של הלוח
    private double[] characterY;

    private final Image backgroundImage;
    private final Image holeImage;

    // מטמון אייקונים מוקטנים - כדי לא לטעון מהדיסק בכל יצירת דמות
    private final Map<String, ImageIcon> iconCache = new HashMap<>();
    private int cachedIconSize = -1;

    public GameBoardPanel(BoardLayout layout, BiConsumer<Double, Double> onBoardClicked) {
        super(null); // null layout - מיקום חופשי של הדמויות לפי קואורדינטות
        setBackground(new Color(139, 69, 19)); // צבע גיבוי - יוצג רק אם התמונה לא נטענה
        backgroundImage = ImageLoader.loadImage(AssetRegistry.gameBackground);
        holeImage = ImageLoader.loadImage(AssetRegistry.hole);

        configure(layout);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (getWidth() == 0 || getHeight() == 0) return;
                // מדווחים רק מיקום יחסי בתוך הלוח - ה-Backend ממיר ובודק פגיעה
                onBoardClicked.accept(e.getX() / (double) getWidth(), e.getY() / (double) getHeight());
            }
        });

        // כשהחלון משנה גודל - ממקמים מחדש את הדמויות שמוצגות כרגע
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                for (int id = 0; id < characterLabels.length; id++) {
                    placeCharacter(id);
                }
            }
        });
    }

    /** מחליפה את הלוח לפי הגדרה חדשה מה-Backend. כל הדמויות הקודמות נעלמות. */
    public void configure(BoardLayout newLayout) {
        this.layout = newLayout;
        removeAll();

        int holesCount = newLayout.getHoles().size();
        characterLabels = new JLabel[holesCount];
        assetKeys = new String[holesCount];
        characterX = new double[holesCount];
        characterY = new double[holesCount];
        for (int i = 0; i < holesCount; i++) {
            characterLabels[i] = new JLabel();
            characterLabels[i].setVisible(false);
            add(characterLabels[i]);
        }

        iconCache.clear();
        cachedIconSize = -1;
        revalidate();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        if (backgroundImage != null) {
            // הרקע הנקי (אחד לכל השלבים) נמתח כך שיכסה בדיוק את שטח הלוח
            g2.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
        }

        // החורים מצוירים לפי ההגדרה מה-Backend
        if (holeImage != null && getWidth() > 0 && getHeight() > 0) {
            double scaleX = getWidth() / (double) layout.getBoardWidth();
            double scaleY = getHeight() / (double) layout.getBoardHeight();
            int width = Math.max(1, (int) Math.round(layout.getHoleWidth() * scaleX));
            int height = Math.max(1, (int) Math.round(layout.getHoleHeight() * scaleY));
            List<HoleLayout> holes = layout.getHoles();
            for (HoleLayout hole : holes) {
                int centerX = (int) Math.round(hole.getCenterX() * scaleX);
                int centerY = (int) Math.round(hole.getCenterY() * scaleY);
                g2.drawImage(holeImage, centerX - width / 2, centerY - height / 2, width, height, null);
            }
        }
        g2.dispose();
    }

    /** מציגה דמות בחור id כך שמרכז התמונה יושב על הנקודה (x, y) של מערכת הלוח. */
    public void showCharacter(int id, String assetKey, double x, double y) {
        if (id < 0 || id >= characterLabels.length) return;
        assetKeys[id] = assetKey;
        characterX[id] = x;
        characterY[id] = y;
        placeCharacter(id);
    }

    public void hideCharacter(int id) {
        if (id < 0 || id >= characterLabels.length) return;
        assetKeys[id] = null;
        characterLabels[id].setVisible(false);
        characterLabels[id].setIcon(null);
    }

    /** מציבה את התווית של חור id לפי הגודל הנוכחי של הפאנל. */
    private void placeCharacter(int id) {
        if (assetKeys[id] == null || getWidth() == 0 || getHeight() == 0) return;

        double scaleX = getWidth() / (double) layout.getBoardWidth();
        double scaleY = getHeight() / (double) layout.getBoardHeight();
        // גודל אחיד (ריבוע) לפי הציר הקטן, כדי שהדמות לא תתעוות
        int size = Math.max(1, (int) Math.round(layout.getCharacterSize() * Math.min(scaleX, scaleY)));

        JLabel label = characterLabels[id];
        ImageIcon icon = getIcon(assetKeys[id], size);
        if (icon != null) {
            label.setIcon(icon);
            label.setText("");
        } else {
            label.setIcon(null);
            label.setText(assetKeys[id].toUpperCase());
        }

        int centerX = (int) Math.round(characterX[id] * scaleX);
        int centerY = (int) Math.round(characterY[id] * scaleY);
        label.setBounds(centerX - size / 2, centerY - size / 2, size, size);
        label.setVisible(true);
    }

    private ImageIcon getIcon(String assetKey, int size) {
        if (size != cachedIconSize) {
            iconCache.clear();
            cachedIconSize = size;
        }
        ImageIcon icon = iconCache.get(assetKey);
        if (icon == null) {
            icon = ImageLoader.loadScaledIcon(assetKey, size, size);
            if (icon != null) iconCache.put(assetKey, icon);
        }
        return icon;
    }
}
