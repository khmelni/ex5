package ai.ui.panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import ai.ui.assets.ImageLoader;
import shared.AssetRegistry;

/**
 * הפס העליון: ניקוד, חיים (לבבות) וכפתור START.
 * הפאנל לא מכיר את הראוטר - כשלוחצים על הכפתור הוא רק מפעיל את ה-callback שקיבל.
 * כל המתודות כאן אמורות לרוץ על ה-EDT של Swing (ה-Ui דואג לזה).
 */
public class TopBarPanel extends JPanel {

    private final JLabel scoreLabel;
    private final JPanel livesPanel;
    private final JButton startButton;
    private final ImageIcon heartIcon;

    public TopBarPanel(Runnable onStartClicked) {
        super(new GridLayout(1, 3));
        setBackground(new Color(240, 240, 240));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        heartIcon = ImageLoader.loadScaledIcon(AssetRegistry.life, 25, 25);

        scoreLabel = new JLabel("Score: 0", SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 18));

        livesPanel = new JPanel();
        livesPanel.setOpaque(false);

        startButton = new JButton("START GAME");
        startButton.setFont(new Font("Arial", Font.BOLD, 14));
        startButton.setBackground(Color.GREEN);
        startButton.setFocusable(false);
        startButton.addActionListener(e -> onStartClicked.run());

        add(scoreLabel);
        add(livesPanel);
        add(startButton);
    }

    public void setScore(int score) {
        scoreLabel.setText("Score: " + score);
    }

    public void setLives(int lives) {
        livesPanel.removeAll();
        for (int i = 0; i < lives; i++) {
            livesPanel.add(new JLabel(heartIcon != null ? heartIcon : new ImageIcon()));
        }
        livesPanel.revalidate();
        livesPanel.repaint();
    }

    public void hideStartButton() {
        startButton.setVisible(false);
    }
}
