package team.model;

//import team.model.Character;
//import team.model.Timon;
//import team.model.LifeGift;
//import team.model.ScoreObstacle;
//import team.model.LifeObstacle;

public class ScoreModel {
    private int currentScore;

    public ScoreModel() {
        this.currentScore = 0;
    }

    // --- Getters & Setters ---
    public int getCurrentScore() {
        return this.currentScore;
    }

    public void updateScore(Character character, boolean clicked) {

        if (clicked) {            
            this.currentScore += character.getScoreEffect();
        }

    }

    public void reset() {
        this.currentScore = 0;
    }
}
