package ai.ui;

import shared.ui_ports.GameUiPort;

public class GameUiPortImpl extends GameUiPort {

    public GameUiPortImpl() {
        // רישום המופע הנוכחי ב-GameUiPort כדי לאפשר גישה גלובלית במידת הצורך
        GameUiPort.setInstance(this);
    }

    @Override
    public void setBackground(String key) {
        GameUiPort instance = GameUiPort.getInstance();
        if (instance != null && instance != this) {
            instance.setBackground(key);
        } else {
            log("setBackground called for key: " + key);
        }
    }

    @Override
    public void spawnCharacter(int id, String assetKey, double x, double y) {
        GameUiPort instance = GameUiPort.getInstance();
        if (instance != null && instance != this) {
            instance.spawnCharacter(id, assetKey, x, y);
        } else {
            log("spawnCharacter called for ID: " + id + " with asset: " + assetKey);
        }
    }

    @Override
    public void removeCharacter(int id) {
        GameUiPort instance = GameUiPort.getInstance();
        if (instance != null && instance != this) {
            instance.removeCharacter(id);
        } else {
            log("removeCharacter called for ID: " + id);
        }
    }

    @Override
    public void updateStats(int score, int lives) {
        GameUiPort instance = GameUiPort.getInstance();
        if (instance != null && instance != this) {
            instance.updateStats(score, lives);
        } else {
            log("updateStats called - Score: " + score + ", Lives: " + lives);
        }
    }

    @Override
    public void setStatusText(String text) {
        GameUiPort instance = GameUiPort.getInstance();
        if (instance != null && instance != this) {
            instance.setStatusText(text);
        } else {
            log("setStatusText called: " + text);
        }
    }

    // --- הוספת המתודה החסרה כדי לפתור את שגיאת הקומפילציה ---
    @Override
    public void showGameOverDialog() {
        GameUiPort instance = GameUiPort.getInstance();
        if (instance != null && instance != this) {
            instance.showGameOverDialog();
        } else {
            log("showGameOverDialog called.");
        }
    }

    @Override
    public void showRestartButton() {
        GameUiPort instance = GameUiPort.getInstance();
        if (instance != null && instance != this) {
            instance.showRestartButton();
        } else {
            log("showRestartButton called.");
        }
    }

    @Override
    public void hideRestartButton() {
        GameUiPort instance = GameUiPort.getInstance();
        if (instance != null && instance != this) {
            instance.hideRestartButton();
        } else {
            log("hideRestartButton called.");
        }
    }

    @Override
    public void log(String message) {
        System.out.println("🔌 [GameUiPortImpl Log] " + message);
    }
}
