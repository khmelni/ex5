package team.control;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import team.model.BoardModel;
import team.model.Character;
import team.model.Circle;
import team.model.LifeModel;

public class GameTimerController {
    private final SpawnController spawner;       
    private final DeSpawnController destroyer;   
    private final LifeModel lifeModel;
    private final BoardModel boardModel; 
    private ScheduledExecutorService scheduler;

    private static final int SpawnIntervalSeconds = 2; 
    private static final int CharacterLifeTimeSeconds = 2; 

    public GameTimerController(SpawnController spawner, DeSpawnController destroyer, 
                               LifeModel lifeModel, BoardModel boardModel) {
        this.spawner = spawner;
        this.destroyer = destroyer;
        this.lifeModel = lifeModel;
        this.boardModel = boardModel;
    }

    /**
     * מפעיל את לולאת תזמון המשחק בצורה בטוחה
     */
    public void startGameLoop() {
        stopGameController();

        this.scheduler = Executors.newScheduledThreadPool(2);

        this.scheduler.scheduleAtFixedRate(() -> {
            
            if (this.lifeModel.isGameOver()) {
                stopGameController();
                return;
            }

            // הגרלת דמות ושיבוצה בחור פנוי ב-BoardModel
            Character spawnedChar = this.spawner.spawnRandomCharacter();

            // אם הוגרלה דמות (כלומר היה חור פנוי)
            if (spawnedChar != null) {
                this.scheduler.schedule(() -> {
                    this.destroyer.destroyByTimeout(spawnedChar);
                    
                    if (this.lifeModel.isGameOver()) {
                        stopGameController();
                    }
                }, CharacterLifeTimeSeconds, TimeUnit.SECONDS);
            }

        }, 0, SpawnIntervalSeconds, TimeUnit.SECONDS);
    }

    /**
     * פונקציית איפוס מרכזית - נקראת בלחיצה על START OVER
     */
    public void reset() {
        // 1. עצירת הטיימרים הקיימים
        stopGameController();
        
        // 2. ריקון כל החורים ב-BoardModel madמויות שנשארו
        for (Circle hole : boardModel.getHoleOccupancy().keySet()) {
            boardModel.getHoleOccupancy().put(hole, null);
        }
        
        System.out.println("Board and timer was reset [Timer] successfully.");
    }

    /**
     * עוצר את כל הטיימרים בצורה בטוחה
     */
    public void stopGameController() {
        if (this.scheduler != null && !this.scheduler.isShutdown()) {
            this.scheduler.shutdownNow(); 
            this.scheduler = null;
            System.out.println("Timer stopped [Timer] successfully.");
        }
    }
}