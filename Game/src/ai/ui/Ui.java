package ai.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import base.Params;
import shared.AssetRegistry;
import shared.MainRouter;
import shared.ui_ports.GameUiPort;

public class Ui extends GameUiPort {

    private JFrame window;
    private JLabel scoreLabel;
    private JPanel livesPanel;
    private JLabel statusLabel;
    private JButton restartButton;
    
    // נשנה את המערך הקבוע למערך גמיש של תוויות (JLabels) להצגת תמונות
    private JLabel[] characterLabels;
    private MainRouter mainRouter;
    private ImageIcon heartIcon;
    
    private int currentScore = 0; 

    public Ui() {
        GameUiPort.setInstance(this);
    }

    public void setUiPorts() {
        System.out.println("LOG: UI Ports and environment configuration set.");
    }

    public void start(MainRouter router) {
        this.mainRouter = router;
        loadHeartIcon();

        SwingUtilities.invokeLater(() -> {
            window = new JFrame("Whack-a-Mole Game");
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.setSize(750, 750);
            window.setLayout(new BorderLayout());

            // 1. יצירת ה-TopBar
            JPanel topBar = new JPanel(new java.awt.GridLayout(1, 3));
            topBar.setBackground(new Color(240, 240, 240));
            topBar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            scoreLabel = new JLabel("Score: 0", SwingConstants.CENTER);
            scoreLabel.setFont(new Font("Arial", Font.BOLD, 18));
            
            livesPanel = new JPanel();
            livesPanel.setOpaque(false);

            restartButton = new JButton("START GAME");
            restartButton.setFont(new Font("Arial", Font.BOLD, 14));
            restartButton.setBackground(Color.GREEN);
            restartButton.setFocusable(false);
            restartButton.setVisible(true);
            
            restartButton.addActionListener(e -> restartGame());

            topBar.add(scoreLabel);
            topBar.add(livesPanel);
            topBar.add(restartButton);

            // 2. יצירת לוח המשחק - משתמשים ב-null layout כדי לתמוך במיקומים חופשיים ודינמיים!
            JPanel gameBoard = new JPanel(null); 
            gameBoard.setBackground(new Color(139, 69, 19));

            // יצירת 9 תוויות ריקות שממתינות להצגת הדמויות
            characterLabels = new JLabel[9];
            for (int i = 0; i < 9; i++) {
                characterLabels[i] = new JLabel();
                characterLabels[i].setVisible(false); // מוסתרות כברירת מחדל
                gameBoard.add(characterLabels[i]);
            }

            // מאזין יחיד על הלוח כולו - תופס ושולח אך ורק קואורדינטות X ו-Y טהורות של הלחיצה
            gameBoard.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mousePressed(java.awt.event.MouseEvent e) {
                    double clickX = e.getX();
                    double clickY = e.getY();
                    
                    // הזרקה ישירה ועיוורת של המיקום הפיזי לראוטר (למשל 250, 330)
                    mainRouter.route("/game/click", Params.of(clickX, clickY));
                }
            });

            // 3. יצירת ה-StatusBar התחתון
            statusLabel = new JLabel("", SwingConstants.CENTER);
            statusLabel.setFont(new Font("Arial", Font.ITALIC, 14));
            statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            window.add(topBar, BorderLayout.NORTH);
            window.add(gameBoard, BorderLayout.CENTER);
            window.add(statusLabel, BorderLayout.SOUTH);

            window.setLocationRelativeTo(null);
            window.setVisible(true);
        });
    }

    private void loadHeartIcon() {
        try {
            String heartPath = AssetRegistry.get(AssetRegistry.LIFE);
            Image img = ImageIO.read(new File(heartPath));
            Image scaledImg = img.getScaledInstance(25, 25, Image.SCALE_SMOOTH);
            this.heartIcon = new ImageIcon(scaledImg);
        } catch (Exception e) {
            this.heartIcon = null;
        }
    }

    private void restartGame() {
        hideRestartButton(); 
        mainRouter.route("/game/reset", Params.of(new Object[0]));
    }

    @Override
    public void showGameOverDialog() {
        SwingUtilities.invokeLater(() -> {
            JPanel gameOverPanel = new JPanel(new java.awt.GridLayout(2, 1, 10, 10));

            JLabel titleLabel = new JLabel("Game Over", SwingConstants.CENTER);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
            titleLabel.setForeground(Color.RED);

            JLabel scoreResultLabel = new JLabel("Final Score: " + currentScore, SwingConstants.CENTER);
            scoreResultLabel.setFont(new Font("Arial", Font.BOLD, 18));

            gameOverPanel.add(titleLabel);
            gameOverPanel.add(scoreResultLabel);

            Object[] options = {"Start Again", "Exit"};

            int choice = JOptionPane.showOptionDialog(
                    window, gameOverPanel, "Game Over",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                    null, options, options
            );

            if (choice == 0) {
                restartGame(); 
            } else if (choice == 1 || choice == JOptionPane.CLOSED_OPTION) {
                System.exit(0); 
            }
        });
    }

    @Override
    public void setBackground(String key) {}

    /**
     * פונקציה זו מקבלת כעת את ה-X וה-Y האמיתיים מהמודל וממקמת את הדמות בדיוק שם!
     */
    @Override
    public void spawnCharacter(int id, String assetKey, double x, double y) {
        SwingUtilities.invokeLater(() -> {
            if (id >= 0 && id < characterLabels.length) {
                try {
                    String imagePath = AssetRegistry.get(assetKey);
                    Image originalImg = ImageIO.read(new File(imagePath));
                    Image scaledImg = originalImg.getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                    
                    characterLabels[id].setIcon(new ImageIcon(scaledImg));
                    characterLabels[id].setText("");
                } catch (Exception e) {
                    characterLabels[id].setIcon(null);
                    characterLabels[id].setText(assetKey.toUpperCase());
                }
                
                // מיקום דינמי לחלוטין על המסך לפי ה-X וה-Y שהגיעו מה-SpawnController!
                // נחסיר 60 (חצי מגודל התמונה 120) כדי שמרכז התמונה יישב בול על נקודת המרכז של החור במודל
                int posX = (int) x - 60;
                int posY = (int) y - 60;
                
                characterLabels[id].setBounds(posX, posY, 120, 120);
                characterLabels[id].setVisible(true); // הצגת הדמות
            }
        });
    }

    @Override
    public void removeCharacter(int id) {
        SwingUtilities.invokeLater(() -> {
            if (id >= 0 && id < characterLabels.length) {
                characterLabels[id].setVisible(false); // הסתרת התווית כשהדמות נעלמת
                characterLabels[id].setIcon(null);
            }
        });
    }

    @Override
    public void updateStats(int score, int lives) {
        this.currentScore = score;
        SwingUtilities.invokeLater(() -> {
            if (scoreLabel != null) scoreLabel.setText("Score: " + score);
            if (livesPanel != null) {
                livesPanel.removeAll();
                for (int i = 0; i < lives; i++) {
                    livesPanel.add(new JLabel(heartIcon != null ? heartIcon : new ImageIcon()));
                }
                livesPanel.revalidate();
                livesPanel.repaint();
            }
        });
    }

    @Override
    public void setStatusText(String text) {
        SwingUtilities.invokeLater(() -> {
            if (statusLabel != null) statusLabel.setText(text);
        });
    }

    @Override
    public void log(String message) {
        System.out.println("[UI Log] " + message);
    }

    @Override
    public void showRestartButton() {}

    @Override
    public void hideRestartButton() {
        SwingUtilities.invokeLater(() -> {
            if (restartButton != null) restartButton.setVisible(false);
        });
    }
}
