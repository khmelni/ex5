package ai.ui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import ai.ui.assets.CursorFactory;
import ai.ui.dialogs.GameOverDialog;
import ai.ui.panels.GameBoardPanel;
import ai.ui.panels.StatusBarPanel;
import ai.ui.panels.TopBarPanel;
import base.Params;
import shared.AssetRegistry;
import shared.MainRouter;
import shared.ui_ports.GameUiPort;

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

        SwingUtilities.invokeLater(() -> {
            window = new JFrame("Whack-a-Mole Game");
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.setSize(750, 750);
            window.setLayout(new BorderLayout());

            topBar = new TopBarPanel(this::restartGame);
            gameBoard = new GameBoardPanel(
                    (x, y) -> mainRouter.route("/game/click", Params.of(x, y)));
            statusBar = new StatusBarPanel();

            window.add(topBar, BorderLayout.NORTH);
            window.add(gameBoard, BorderLayout.CENTER);
            window.add(statusBar, BorderLayout.SOUTH);

            window.setLocationRelativeTo(null);
            window.setVisible(true);
            setPumbaaCursor();
        });
    }

    private void setPumbaaCursor() {
        Cursor cursor = CursorFactory.createFromAsset(AssetRegistry.PUMBAA, "pumbaaCursor");
        if (cursor != null) window.setCursor(cursor);
    }

    private void restartGame() {
        hideRestartButton();
        mainRouter.route("/game/reset", Params.of(new Object[0]));
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
        SwingUtilities.invokeLater(() ->
                GameOverDialog.show(window, currentScore, this::restartGame, () -> System.exit(0)));
    }

    @Override
    public void hideRestartButton() {
        SwingUtilities.invokeLater(() -> {
            if (topBar != null) topBar.hideStartButton();
        });
    }

}
