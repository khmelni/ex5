package ai.ui.panels;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.BiConsumer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ai.ui.assets.ImageLoader;
import shared.AssetRegistry;

/**
 * לוח המשחק: רקע, 9 תוויות (אחת לכל חור) שמציגות את הדמויות, ותפיסת לחיצות עכבר.
 * הפאנל לא יודע מה זה "משחק" - הוא רק מדווח על קואורדינטות לחיצה דרך ה-callback.
 */
public class GameBoardPanel extends JPanel {

    private static final int HOLES_COUNT = 9;
    private static final int CHARACTER_SIZE = 120;

    private final JLabel[] characterLabels = new JLabel[HOLES_COUNT];
    private final Image backgroundImage;

    public GameBoardPanel(BiConsumer<Double, Double> onBoardClicked) {
        super(null); // null layout - מיקום חופשי של הדמויות לפי קואורדינטות
        setBackground(new Color(139, 69, 19)); // צבע גיבוי - יוצג רק אם התמונה לא נטענה
        backgroundImage = ImageLoader.loadImage(AssetRegistry.GAME_BACKGROUND);

        for (int i = 0; i < HOLES_COUNT; i++) {
            characterLabels[i] = new JLabel();
            characterLabels[i].setVisible(false);
            add(characterLabels[i]);
        }

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                onBoardClicked.accept((double) e.getX(), (double) e.getY());
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            // מותחים את התמונה כך שתכסה בדיוק את שטח הלוח, בכל גודל שהוא
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    /** מציגה דמות בחור id כך שמרכז התמונה יושב על הנקודה (x, y). */
    public void showCharacter(int id, String assetKey, double x, double y) {
        if (id < 0 || id >= characterLabels.length) return;

        ImageIcon icon = ImageLoader.loadScaledIcon(assetKey, CHARACTER_SIZE, CHARACTER_SIZE);
        if (icon != null) {
            characterLabels[id].setIcon(icon);
            characterLabels[id].setText("");
        } else {
            characterLabels[id].setIcon(null);
            characterLabels[id].setText(assetKey.toUpperCase());
        }

        int half = CHARACTER_SIZE / 2;
        characterLabels[id].setBounds((int) x - half, (int) y - half, CHARACTER_SIZE, CHARACTER_SIZE);
        characterLabels[id].setVisible(true);
    }

    public void hideCharacter(int id) {
        if (id < 0 || id >= characterLabels.length) return;
        characterLabels[id].setVisible(false);
        characterLabels[id].setIcon(null);
    }
}
