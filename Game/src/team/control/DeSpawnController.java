package team.control;

import java.util.Map;
import team.model.BoardModel;
import team.model.Character;
import team.model.Circle;
import team.model.Point;
import team.model.LifeModel;
import team.model.ScoreModel;
import shared.ui_ports.GameUiPort;

public class DeSpawnController {
    private final BoardModel boardModel;
    private final ScoreModel scoreModel;
    private final LifeModel lifeModel;

    public DeSpawnController(BoardModel boardModel, ScoreModel scoreModel, LifeModel lifeModel) {
        this.boardModel = boardModel;
        this.scoreModel = scoreModel;
        this.lifeModel = lifeModel;
    }

    /**
     * הסרת דמות בלחיצה לפי קואורדינטות (x, y) טהורות מהמסך
     */
    public synchronized Character destroyByLocation(double x, double y) {
        // יצירת נקודה גיאומטרית זמנית ממיקום הלחיצה של העכבר
        Point clickPoint = new Point(-1, x, y);

        // עוברים חור-חור בלוח
        for (Map.Entry<Circle, Character> entry : boardModel.getHoleOccupancy().entrySet()) {
            Circle hole = entry.getKey();
            Character characterInHole = entry.getValue();

            // הבדיקה הגיאומטרית הטהורה: האם הלחיצה בתוך העיגול הספציפי והאם יש בו דמות?
            if (hole.contains(clickPoint) && characterInHole != null) {
                
                boolean isDestroyed = characterInHole.registerClick();

                if (isDestroyed) {
                    // עדכון המודלים
                    this.scoreModel.updateScore(characterInHole, true);
                    this.lifeModel.updateLife(characterInHole, true);
                    
                    // פינוי החור במודל
                    boardModel.getHoleOccupancy().put(hole, null);

                    // עדכון ה-UI בזמן אמת דרך ה-Port
                    GameUiPort ui = GameUiPort.getInstance();
                    ui.removeCharacter(hole.getId()); 
                    ui.updateStats(this.scoreModel.getCurrentScore(), this.lifeModel.getCurrentLife()); 

                    if (this.lifeModel.isGameOver()) {
                        handleGameOver(ui);
                    }

                    System.out.println("The hit in character [Hit] ' " + hole.getId());
                    return characterInHole;
                }
            }
        }
        return null;
    }


    public synchronized void destroyByTimeout(Character character) {
        for (Map.Entry<Circle, Character> entry : boardModel.getHoleOccupancy().entrySet()) {
            if (entry.getValue() != null && entry.getValue().equals(character)) {
                Circle hole = entry.getKey();
                
                this.scoreModel.updateScore(character, false);
                this.lifeModel.updateLife(character, false);
                boardModel.getHoleOccupancy().put(hole, null);
                
                GameUiPort ui = GameUiPort.getInstance();
                ui.removeCharacter(hole.getId()); 
                ui.updateStats(this.scoreModel.getCurrentScore(), this.lifeModel.getCurrentLife()); 
                
                if (this.lifeModel.isGameOver()) {
                    handleGameOver(ui);
                }
                break;
            }
        }
    }

    private void handleGameOver(GameUiPort ui) {
        // 1. ניקוי מוחלט של כל הדמויות מהמסך
        for (Circle hole : boardModel.getHoleOccupancy().keySet()) {
            ui.removeCharacter(hole.getId());
        }

        // 2. איפוס טקסט הסטטוס שיישאר נקי לחלוטין
        ui.setStatusText("");
        
        // 3. הצגת הדיאלוג היחיד של סיום משחק
        ui.showGameOverDialog();
    }
}
