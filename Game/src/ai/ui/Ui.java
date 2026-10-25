package ai.ui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import ai.ui.assets.CursorFactory;
import ai.ui.assets.SoundPlayer;
import ai.ui.dialogs.GameOverDialog;
import ai.ui.panels.GameBoardPanel;
import ai.ui.panels.LandingPanel;
import ai.ui.panels.StatusBarPanel;
import ai.ui.panels.TopBarPanel;
import base.Params;
import shared.AssetRegistry;
import shared.BoardLayout;
import shared.MainRouter;
import shared.ui_ports.GameUiPort;
import java.awt.CardLayout;



/**
 * ה-Ui הראשי: מרכיב את החלון מהחלקים (TopBar, Board, StatusBar),
 * מממש את GameUiPort (מה שה-Backend קורא לו) ומעביר אירועי משתמש לראוטר.
 * כל הציור עצמו נעשה במחלקות שבתיקיות panels / dialogs.
 */
public class Ui extends GameUiPort {

    private JFrame window;
    private TopBarPanel topBar;
    private GameBoardPanel gameBoard;
    private StatusBarPanel statusBar;

    private MainRouter mainRouter;
    private int currentScore = 0;
    // תוספת של הטבלת ניקוד
    private LandingPanel landingPanel;
    private CardLayout cardLayout;
    private JPanel rootPanel;

    

    public Ui() {
        GameUiPort.setInstance(this);
    }

    public void setUiPorts() {
        System.out.println("LOG: UI Ports and environment configuration set.");
    }

    // ------------------------------------------------------------------
    // בניית החלון
    // ------------------------------------------------------------------

    public void start(MainRouter router) {
        this.mainRouter = router;

        // טוענים את הצלילים מראש (ברקע), כדי שהחבטה הראשונה לא תתעכב
        SoundPlayer.preload(AssetRegistry.backgroundMusic, AssetRegistry.hammerHit, AssetRegistry.gameOver);


        SwingUtilities.invokeLater(() -> {
            window = new JFrame("Whack-a-Mole Game");
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.setSize(750, 750);
            window.setLayout(new BorderLayout());

            // הגדרת הלוח (חורים, מיקומים, גדלים) נשאלת מה-Backend דרך ה-Router
            BoardLayout layout = (BoardLayout) mainRouter.route("/game/board-info", Params.of(new Object[0]));

            topBar = new TopBarPanel(this::restartGame);
            gameBoard = new GameBoardPanel(
                    layout,
                    (x, y) -> mainRouter.route("/game/click", Params.of(x, y)));
            statusBar = new StatusBarPanel();

            //window.add(topBar, BorderLayout.NORTH);
            //window.add(gameBoard, BorderLayout.CENTER);
            //window.add(statusBar, BorderLayout.SOUTH);

            JPanel gamePanel = new JPanel(new BorderLayout());
            gamePanel.add(topBar, BorderLayout.NORTH);
            gamePanel.add(gameBoard, BorderLayout.CENTER);
            gamePanel.add(statusBar, BorderLayout.SOUTH);

            landingPanel = new LandingPanel(this::startGameFromLanding);

            cardLayout = new CardLayout();
            rootPanel = new JPanel(cardLayout);
            rootPanel.add(landingPanel, "landing");
            rootPanel.add(gamePanel, "game");

            window.setContentPane(rootPanel);
            cardLayout.show(rootPanel, "landing");

            window.setLocationRelativeTo(null);
            window.setVisible(true);
            refreshHallOfFame();
            setCursor();
        });
    }

    private void setCursor() {
        Cursor cursor = CursorFactory.createFromAsset(AssetRegistry.hammer, "hammerCursor");
        if (cursor != null) window.setCursor(cursor);
    }

    private void restartGame() {
        hideRestartButton();
        mainRouter.route("/game/reset", Params.of(new Object[0]));
    }


    private void startGameFromLanding() {
    cardLayout.show(rootPanel, "game");
    restartGame();
    }

    private void refreshHallOfFame() {
        Object result = mainRouter.route("/game/hof", Params.of(10));
        if (landingPanel != null && result instanceof String[][]) {
            landingPanel.setHallOfFameRows((String[][]) result);
        }
    }

    // ------------------------------------------------------------------
    // מימוש GameUiPort - נקראים מה-Backend (ת'רדים אחרים), לכן invokeLater
    // ------------------------------------------------------------------

    @Override
    public void spawnCharacter(int id, String assetKey, double x, double y) {
        SwingUtilities.invokeLater(() -> gameBoard.showCharacter(id, assetKey, x, y));
    }

    @Override
    public void removeCharacter(int id) {
        SwingUtilities.invokeLater(() -> gameBoard.hideCharacter(id));
    }

    @Override
    public void setupBoard(BoardLayout layout) {
        SwingUtilities.invokeLater(() -> {
            if (gameBoard != null) gameBoard.configure(layout);
        });
    }

    @Override
    public void updateStats(int score, int lives) {
        this.currentScore = score;
        SwingUtilities.invokeLater(() -> {
            if (topBar == null) return;
            topBar.setScore(score);
            topBar.setLives(lives);
        });
    }

    @Override
    public void setStatusText(String text) {
        SwingUtilities.invokeLater(() -> {
            if (statusBar != null) statusBar.setStatus(text);
        });
    }

    @Override
    public void showGameOverDialog() {
        SwingUtilities.invokeLater(() -> {
            promptAndSaveScore();
            GameOverDialog.show(window, currentScore, this::restartGame, () -> System.exit(0));
        });
    }

    /** מבקשת מהשחקן שם ושומרת את הציון הסופי ב-Hall of Fame (רק אם נצבר ניקוד). */
    private void promptAndSaveScore() {
        if (currentScore <= 0) return;

        String playerName = JOptionPane.showInputDialog(
                window,
                "Game Over! Final Score: " + currentScore + "\nEnter your name for the Hall of Fame:",
                "Hall of Fame",
                JOptionPane.PLAIN_MESSAGE
        );

        if (playerName != null && !playerName.trim().isEmpty()) {
            mainRouter.route("/game/save_score", Params.of(playerName.trim()));
            refreshHallOfFame(); // מרענן את הטבלה בדף הנחיתה כדי שהציון החדש יופיע שם
        }
    }

    @Override
    public void hideRestartButton() {
        SwingUtilities.invokeLater(() -> {
            if (topBar != null) topBar.hideStartButton();
        });
    }

    // ---- צלילים: SoundPlayer מנגן ב-thread משלו, ולכן אין צורך ב-invokeLater ----

    @Override
    public void startMusic() {
        SoundPlayer.playLoop(AssetRegistry.backgroundMusic);
    }

    @Override
    public void stopMusic() {
        SoundPlayer.stopLoop();
    }

    @Override
    public void playSound(String soundKey) {
        SoundPlayer.playOnce(soundKey);
    }

}