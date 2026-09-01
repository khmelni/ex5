package team.model;

public class Character {
    private int lifeEffect;
    private int scoreEffect;
    private int requiredClicks;     

    public Character(int lifeEffect, int scoreEffect, int requiredClicks) {
        this.lifeEffect = lifeEffect;
        this.scoreEffect = scoreEffect;
        this.requiredClicks = requiredClicks;
    }

    // --- Getters & Setters 
    
    public int getLifeEffect() {
        return this.lifeEffect;
    }
    
    public void setLifeEffect(int value) {
        this.lifeEffect = value;
    }

    public int getScoreEffect() {
        return this.scoreEffect;
    }
    
    public void setScoreEffect(int value) {
        this.scoreEffect = value;
    }

    public int getRequiredClicks() {
        return this.requiredClicks;
    }
    
    public void setRequiredClicks(int value) {
        // הגנה ארכיטקטונית: מונע ערך שלילי של לחיצות
        this.requiredClicks = value < 1 ? 1 : value;
    }

    // --- מתודות לוגיקה (הוחזרה לפעילות מההערה) ---
    
//    /**
//     * מוריד לחיצה אחת מהדמות.
//     * @return True אם הדמות הגיעה ל-0 לחיצות וצריכה להיעלם מהמסך, אחרת False.
//     */
    public boolean registerClick() {
        if (this.requiredClicks > 0) {
            this.requiredClicks--;
        }
        return this.requiredClicks == 0;
    }
}
