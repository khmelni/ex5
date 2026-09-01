package team.model;

//import team.model.Character;
//import team.model.Timon;
//import team.model.LifeGift;
//import team.model.ScoreObstacle;
//import team.model.LifeObstacle;

public class LifeModel {
    private static final int InitialLife = 3;
    private int currentLife;

    public LifeModel() {
        this.currentLife = InitialLife;
    }

    public int getCurrentLife() {
        return this.currentLife;
    }

    public void updateLife(Character character, boolean clicked) {
        if (clicked) {
            // אם זו לחיצה על Timon - לא עושים כלום (החיים לא משתנים)
            if (!(character instanceof Timon)) {
                // עבור שאר הדמויות - החיים משתנים לפי ההשפעה שלהן
                this.currentLife += character.getLifeEffect();
            }
        } else {
            // אם לא לוחצים בזמן וזו דמות של Timon - מורידים חיים
            if (character instanceof Timon) {
                this.currentLife -= Math.abs(character.getLifeEffect());
            }
        }

        // הגנה: החיים לא יירדו מתחת ל-0
        if (this.currentLife < 0) {
            this.currentLife = 0;
        }
    }

    public boolean isGameOver() {
        return this.currentLife == 0;
    }

    public void reset() {
        this.currentLife = InitialLife;
    }
}
