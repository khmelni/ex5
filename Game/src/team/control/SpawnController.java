package team.control;

import java.util.List;
import java.util.Random;
import team.model.BoardModel;
import team.model.Character;
import team.model.Circle;
import team.model.Timon;
import team.model.LifeGift;
import team.model.ScoreObstacle;
import team.model.LifeObstacle;
import shared.ui_ports.GameUiPort; 

public class SpawnController {
    private final BoardModel boardModel;
    private final Random random;

    public SpawnController(BoardModel boardModel) {
        this.boardModel = boardModel;
        this.random = new Random();
    }

    public Character spawnRandomCharacter() {
        // 1. קבלת רשימת החורים שפנויים כרגע בלוח
        List<Circle> freeHoles = boardModel.getFreeHoles();

        if (freeHoles.isEmpty()) {
            System.out.println(" No empty holes [Spawn]");
            return null; // הלוח מלא, לא ניתן להוציא דמות חדשה
        }

        // 2. הגרלת דמות לפי אחוזי ההסתברות הקיימים שלכם
        Character newChar;
        int chance = random.nextInt(100); // מספר אקראי בין 0 ל-99

        //newChar = new Timon(); // 100% סיכוי לטימון (לצרכי בדיקה כפי שציינת)

        if (chance < 50) {
            newChar = new Timon(); // 50% סיכוי לטימון
        } else if (chance < 70) {
            newChar = new ScoreObstacle(); // 20% סיכוי לפגיעה בניקוד
        } else if (chance < 90) {
            newChar = new LifeObstacle(); // 20% סיכוי  לפגיעה בחיים
        } else {
            newChar = new LifeGift(); // 10% סיכוי לחיים
        }

        // 3. הגרלת חור פנוי מתוך הרשימה
        Circle chosenHole = freeHoles.get(random.nextInt(freeHoles.size()));

        // 4. השמת הדמות בחור שנבחר ב-BoardModel
        boardModel.getHoleOccupancy().put(chosenHole, newChar);

        // 5. עדכון ה-UI בזמן אמת - הדמות מוצגת גרפית מיד עם יצירתה!
        // הופך את שם המחלקה (למשל LifeObstacle) לשם עם קו תחתון (life_obstacle)
        String className = newChar.getClass().getSimpleName();
        String assetKey = className.replaceAll("(?<!^)(?=[A-Z])", "_").toLowerCase();

        GameUiPort ui = GameUiPort.getInstance();
        ui.spawnCharacter(
            chosenHole.getId(),        // מזהה החור
            assetKey,     // מפתח התמונה של הדמות
            chosenHole.getCenter().getX(),         // מיקום X
            chosenHole.getCenter().getY()          // מיקום Y
        );

        System.out.println("Character is created successfully [Spawn]: " + newChar.getClass().getSimpleName() + 
                           " in hole " + chosenHole.getId());

        return newChar;
    }
}
