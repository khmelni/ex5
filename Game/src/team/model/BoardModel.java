package team.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoardModel {
    // מיפוי בין חור (Circle) לבין הדמות שנמצאת בו כרגע (null אם החור ריק)
    private final Map<Circle, Character> holeOccupancy = new HashMap<>();

    public BoardModel() {
        initBoardHoles();
    }

    /**
     * אתחול המיקומים והרדיוסים הקבועים של החורים על הלוח
     */
    private void initBoardHoles() {
        // דוגמה להגדרת 9 חורים (ניתן להתאים לפי קואורדינטות ה-UI שלכם)
        holeOccupancy.put(new Circle(0, new Point(0, 100, 100), 40), null);
        holeOccupancy.put(new Circle(1, new Point(1, 300, 100), 40), null);
        holeOccupancy.put(new Circle(2, new Point(2, 500, 100), 40), null);
        holeOccupancy.put(new Circle(3, new Point(3, 100, 300), 40), null);
        holeOccupancy.put(new Circle(4, new Point(4, 300, 300), 40), null);
        holeOccupancy.put(new Circle(5, new Point(5, 500, 300), 40), null);
        holeOccupancy.put(new Circle(6, new Point(6, 100, 500), 40), null);
        holeOccupancy.put(new Circle(7, new Point(7, 300, 500), 40), null);
        holeOccupancy.put(new Circle(8, new Point(8, 500, 500), 40), null);
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
     * איפוס הלוח - פינוי כל החורים מדמויות
     */
    public void reset() {
        for (Circle circle : holeOccupancy.keySet()) {
            holeOccupancy.put(circle, null);
        }
    }
}