package team.control;

import my_base.App;
import team.model.BoardModel;
import team.model.LifeModel;
import team.model.ScoreModel;
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
        ui.updateStats(scoreModel.getCurrentScore(), lifeModel.getCurrentLife());
        ui.hideRestartButton();
        ui.setStatusText("");
        
        // 4. הפעלה מחדש של לולאת המשחק המתוזמנת
        this.timerController.startGameLoop();
        
        System.out.println("Start New Game began [Backend]");
    }

    /**
     * טיפול בלחיצת עכבר לפי קואורדינטות (x, y) שנשלחו מה-GameRouter
     */
    public void handleMouseClick(double x, double y) {
        // שליחת המיקום הגיאומטרי הטהור ל-DeSpawnController לבדיקת פגיעה בחור ובדמות.
        // ה-DeSpawnController כבר דואג לעדכן את ה-UI בצורה מסונכרנת, להסיר את הדמות
        // ולשנות את הסטטיסטיקות מיד עם החבטה, לכן אין צורך בכפל קריאות (if-updateStats) כאן!
        App.content().destroyer().destroyByLocation(x, y);
    }

    /**
     * מתודת גשר (Hook)
     */
    public void tick(double dt) {
        // הזמנים מנוהלים בתוך ה-GameTimerController
    }
}
