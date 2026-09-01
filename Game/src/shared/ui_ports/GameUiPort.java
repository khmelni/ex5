package shared.ui_ports;

public abstract class GameUiPort {

    private static GameUiPort instance;

    public static void setInstance(GameUiPort ui) {
        if (ui == null) throw new IllegalArgumentException("GameUiPort instance cannot be null");
        if (instance != null) throw new IllegalStateException("GameUiPort instance already set");
        instance = ui;
    }

    public static GameUiPort getInstance() {
        if (instance == null) throw new IllegalStateException("GameUiPort instance not set yet");
        return instance;
    }

    public abstract void setBackground(String key);
    public abstract void spawnCharacter(int id, String assetKey, double x, double y);
    public abstract void removeCharacter(int id);
    public abstract void updateStats(int score, int lives);
    public abstract void setStatusText(String text);
    public abstract void log(String message);
    
    // מתודה חדשה עבור הדיאלוג המותאם
    public abstract void showGameOverDialog();

    // נשארים בפורט למען תאימות קוד, אך לא יעשו דבר ב-UI
    public abstract void showRestartButton();
    public abstract void hideRestartButton();
}
