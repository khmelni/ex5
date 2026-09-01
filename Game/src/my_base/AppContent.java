package my_base;

// Imports מעודכנים
import team.control.GameBackend;
import team.control.GameTimerController;
import team.control.SpawnController;
import team.control.DeSpawnController;
import team.model.BoardModel;
import team.model.LifeModel;
import team.model.ScoreModel;

public class AppContent {

    // הגדרת משתני המצב והקונטרולרים של המשחק
    private BoardModel boardModel;
    private ScoreModel scoreModel;
    private LifeModel lifeModel;
    private SpawnController spawner;
    private DeSpawnController destroyer;
    private GameTimerController gameTimerController;
    private GameBackend gameBackend;

    public void initContent() {
        // 1. אתחול מודל הלוח המרכזי והמודלים של הניקוד והחיים
        this.boardModel = new BoardModel();
        this.scoreModel = new ScoreModel();
        this.lifeModel = new LifeModel();

        // 2. אתחול הקונטרולרים עם BoardModel
        this.spawner = new SpawnController(this.boardModel);
        this.destroyer = new DeSpawnController(this.boardModel, this.scoreModel, this.lifeModel);
        
        // 3. אתחול הטיימר וחיבורו לרכיבי הבקרה והלוח
        this.gameTimerController = new GameTimerController(
            this.spawner, 
            this.destroyer, 
            this.lifeModel, 
            this.boardModel
        );

        // 4. אתחול מנוע המשחק הראשי (GameBackend)
        this.gameBackend = new GameBackend(
            this.scoreModel, 
            this.lifeModel, 
            this.boardModel, 
            this.gameTimerController
        );
    }

    // --- Getters נקיים עבור ה-UI וה-Routers ---

    public BoardModel boardModel() { 
        return this.boardModel; 
    }

    public ScoreModel scoreModel() { 
        return this.scoreModel; 
    }

    public LifeModel lifeModel() { 
        return this.lifeModel; 
    }

    public SpawnController spawner() { 
        return this.spawner; 
    }

    public DeSpawnController destroyer() { 
        return this.destroyer; 
    }

    public GameTimerController gameTimerController() {
        return this.gameTimerController;
    }

    public GameBackend gameBackend() { 
        return this.gameBackend; 
    }
}