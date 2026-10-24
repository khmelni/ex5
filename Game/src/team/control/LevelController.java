package team.control;

import shared.ui_ports.GameUiPort;
import team.model.BoardModel;
import team.model.BoardSize;
import team.model.ScoreModel;

/**
 * אחראי על גדילת הלוח: אחרי כל שינוי בניקוד בודק באיזה שלב הלוח אמור להיות,
 * ואם הוא מאחור - בונה לוח גדול יותר, מעדכן את ה-UI ומפעיל את המשחק מחדש.
 * השלב אף פעם לא יורד, גם אם הניקוד יורד.
 */
public class LevelController {

    private final BoardModel boardModel;
    private final ScoreModel scoreModel;
    private final GameTimerController timerController;

    public LevelController(BoardModel boardModel, ScoreModel scoreModel,
                           GameTimerController timerController) {
        this.boardModel = boardModel;
        this.scoreModel = scoreModel;
        this.timerController = timerController;
    }

    /** נקראת אחרי כל שינוי בניקוד. */
    public void checkLevelUp() {
        int targetLevelIndex = boardModel.getLevelIndexForScore(scoreModel.getCurrentScore());
        if (targetLevelIndex <= boardModel.getLevelIndex()) return;

        // 1. עוצרים את יצירת הדמויות והטיימרים של הלוח הישן
        timerController.stopGameController();

        // 2. בונים לוח חדש (באותה נעילה ש-SpawnController משתמש בה, כדי שלא יתערבבו)
        synchronized (boardModel) {
            boardModel.advanceToLevel(targetLevelIndex);
        }

        // 3. מעדכנים את ה-UI: הלוח החדש (התוויות הישנות נמחקות) והודעה
        BoardSize size = boardModel.getBoardSize();
        GameUiPort ui = GameUiPort.getInstance();
        ui.setupBoard(boardModel.getLayout());
        ui.setStatusText("Level " + boardModel.getLevelNumber()
                + " (" + size.getRows() + " x " + size.getColumns() + ")");

        System.out.println("Board grew -> level " + boardModel.getLevelNumber() + " [Level]");

        // 4. ממשיכים במשחק על הלוח החדש
        timerController.startGameLoop();
    }
}
