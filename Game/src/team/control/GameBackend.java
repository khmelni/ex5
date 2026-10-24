package team.control;

import my_base.App;
import team.model.BoardModel;
import team.model.Point;
import team.model.LifeModel;
import team.model.ScoreModel;
import shared.BoardLayout;
import shared.ui_ports.GameUiPort;

public class GameBackend {

    private final ScoreModel scoreModel; //Leora was here
    private final LifeModel lifeModel; //Michael was here
    private final BoardModel boardModel; //yosef was here
    private final GameTimerController timerController; //noga was here

    /**
     * הבנאי שמקבל את אובייקטי ה-Model וה-Controller
     */
    public GameBackend(ScoreModel scoreModel, LifeModel lifeModel, BoardModel boardModel, 
                       GameTimerController timerController) {
        this.scoreModel = scoreModel;
        this.lifeModel = lifeModel;
        this.boardModel = boardModel; 
        this.timerController = timerController;
    }

    /**
     * הפעלת המשחק לראשונה
     */
    public void start() {
        System.out.println("Starting Game [Backend]");
        this.timerController.startGameLoop();
    }

    /**
     * איפוס המשחק (Start Over)
     */
    public void reset() {
        System.out.println("Start Over [Backend] ...");
        
        // 1. איפוס המודלים של הניקוד, החיים והלוח בזיכרון
        this.scoreModel.reset();
        this.lifeModel.reset();
        this.boardModel.reset();       

        // 2. עצירת תהליכונים, ניקוי ה-BoardModel והחזרת הטיימרים למצב התחלתי
        this.timerController.reset(); 
        
        // 3. עדכון מיידי של ה-UI עם הערכים המאופסים (0 ניקוד, מקסימום חיים)
        // בנוסף, ננקה את הודעות הסטטוס ונבטיח שכפתורי ה-START GAME/OVER הישנים מוסתרים
        GameUiPort ui = GameUiPort.getInstance();
        ui.setupBoard(boardModel.getLayout()); // חזרה ללוח ההתחלתי
        ui.updateStats(scoreModel.getCurrentScore(), lifeModel.getCurrentLife());
        ui.hideRestartButton();
        ui.setStatusText("");
        
        // 4. הפעלה מחדש של לולאת המשחק המתוזמנת
        this.timerController.startGameLoop();
        
        System.out.println("Start New Game began [Backend]");
    }

    /**
     * טיפול בלחיצת עכבר. ה-UI מדווח רק על המיקום היחסי בתוך הלוח (xRatio, yRatio בין 0 ל-1);
     * ה-Backend ממיר אותו למערכת הקואורדינטות של הלוח ובודק פגיעה בחור ובדמות.
     */
    public void handleMouseClick(double xRatio, double yRatio) {
        Point boardPoint = boardModel.toBoardPoint(xRatio, yRatio);
        App.content().destroyer().destroyByLocation(boardPoint.getX(), boardPoint.getY());
    }

    /**
     * הגדרת הלוח הנוכחי עבור ה-UI (נשלפת דרך ה-Router): חורים, מיקומים וגדלים.
     */
    public BoardLayout getBoardInfo() {
        return boardModel.getLayout();
    }

    /**
     * מתודת גשר (Hook)
     */
    public void tick(double dt) {
        // הזמנים מנוהלים בתוך ה-GameTimerController
    }
        // שמירה של ניקוד לטבלת ניקוד
    public void saveScoreToHallOfFame(String playerName) {
    int finalScore = this.scoreModel.getCurrentScore();
    App.content().hallOfFameController().addScore(playerName, finalScore);
    }

    public String[][] getHallOfFameTop(int n) {
        return App.content().hallOfFameController().getTopScores(n);
    }

}
