package team.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import shared.BoardLayout;
import shared.HoleLayout;

/**
 * מודל הלוח: מחזיק את החורים (גיאומטריה) ואת הדמות שנמצאת בכל חור.
 * גודל הלוח משתנה לפי שלבים (ראו LevelRules); הגיאומטריה עצמה מוגדרת ב-BoardGeometry.
 */
public class BoardModel {

    private final BoardGeometry geometry;
    private final LevelRules levelRules;

    private int levelIndex = 0;
    private BoardSize boardSize;
    private double holeWidth;
    private double holeHeight;

    // מיפוי בין חור (Circle) לבין הדמות שנמצאת בו כרגע (null אם החור ריק).
    // בשינוי לוח מחליפים את כל המפה באחת חדשה (ולא מרוקנים אותה), כך שמי שעדיין
    // עובר על המפה הישנה לא נתקל ב-ConcurrentModificationException.
    private volatile Map<Circle, Character> holeOccupancy = new HashMap<>();

    public BoardModel(BoardGeometry geometry, LevelRules levelRules) {
        this.geometry = geometry;
        this.levelRules = levelRules;
        loadLevel(0);
    }

    /** בונה מחדש את כל החורים לפי השלב: מחלק את אזור החול לגריד של שורות x עמודות. */
    private synchronized void loadLevel(int newLevelIndex) {
        levelIndex = newLevelIndex;
        boardSize = levelRules.sizeForLevelIndex(newLevelIndex);

        double cellWidth = geometry.getPlayAreaWidth() / boardSize.getColumns();
        double cellHeight = geometry.getPlayAreaHeight() / boardSize.getRows();

        // החור מוגבל גם ברוחב התא וגם בגובה התא, ושומר על יחס התמונה
        double fill = geometry.getHoleFillRatio();
        holeWidth = Math.min(cellWidth * fill, cellHeight * fill * geometry.getHoleAspectRatio());
        holeHeight = holeWidth / geometry.getHoleAspectRatio();
        double hitRadius = holeHeight * geometry.getHitRadiusRatio();

        Map<Circle, Character> newHoles = new HashMap<>();
        int id = 0;
        for (int row = 0; row < boardSize.getRows(); row++) {
            for (int column = 0; column < boardSize.getColumns(); column++) {
                double x = geometry.getPlayAreaX() + (column + 0.5) * cellWidth;
                double y = geometry.getPlayAreaY() + (row + 0.5) * cellHeight;
                newHoles.put(new Circle(id, new Point(id, x, y), hitRadius), null);
                id++;
            }
        }
        holeOccupancy = newHoles;
    }

    // ---------------- שלבים ----------------

    /** אינדקס השלב הנוכחי (מתחיל מ-0). */
    public synchronized int getLevelIndex() {
        return levelIndex;
    }

    /** מספר השלב להצגה (מתחיל מ-1). */
    public synchronized int getLevelNumber() {
        return levelIndex + 1;
    }

    public synchronized BoardSize getBoardSize() {
        return boardSize;
    }

    /** באיזה שלב צריך להיות הלוח עבור ניקוד נתון. */
    public int getLevelIndexForScore(int score) {
        return levelRules.levelIndexForScore(score);
    }

    /** מעבר לשלב נתון: הלוח נבנה מחדש וכל הדמויות נעלמות. */
    public synchronized void advanceToLevel(int newLevelIndex) {
        loadLevel(newLevelIndex);
    }

    // ---------------- גיאומטריה ----------------

    /**
     * ממיר מיקום יחסי בתוך הלוח (0..1 בכל ציר, כפי שה-UI מדווח על לחיצה)
     * לנקודה במערכת הקואורדינטות של הלוח, כדי לבדוק אותה מול החורים.
     */
    public Point toBoardPoint(double xRatio, double yRatio) {
        return new Point(-1, xRatio * geometry.getBoardWidth(), yRatio * geometry.getBoardHeight());
    }

    /** תיאור הלוח הנוכחי עבור ה-UI (מיקום כל חור וגדלים). */
    public synchronized BoardLayout getLayout() {
        HoleLayout[] byId = new HoleLayout[holeOccupancy.size()];
        for (Circle hole : holeOccupancy.keySet()) {
            byId[hole.getId()] = new HoleLayout(hole.getId(),
                    hole.getCenter().getX(), hole.getCenter().getY());
        }
        List<HoleLayout> holes = new ArrayList<>(List.of(byId));
        return new BoardLayout(getLevelNumber(), geometry.getBoardWidth(), geometry.getBoardHeight(),
                holes, holeWidth, holeHeight, holeWidth);
    }

    // ---------------- חורים ודמויות ----------------

    /** מספר החורים בשלב הנוכחי. */
    public int getHolesCount() {
        return holeOccupancy.size();
    }

    public Map<Circle, Character> getHoleOccupancy() {
        return holeOccupancy;
    }

    /**
     * מחזיר את רשימת החורים שכרגע פנויים להגרלת דמות חדשה
     */
    public List<Circle> getFreeHoles() {
        List<Circle> freeHoles = new ArrayList<>();
        for (Map.Entry<Circle, Character> entry : holeOccupancy.entrySet()) {
            if (entry.getValue() == null) {
                freeHoles.add(entry.getKey());
            }
        }
        return freeHoles;
    }

    /**
     * איפוס הלוח - חזרה לשלב הראשון עם חורים ריקים
     */
    public void reset() {
        loadLevel(0);
    }
}
