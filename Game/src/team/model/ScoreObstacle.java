package team.model;

public class ScoreObstacle extends Character {
    
    private static final int lifeEffect = 0;      
    private static final int scoreEffect = -5;
    private static final int requiredClicks = 1;

    public ScoreObstacle() {
        super (lifeEffect, scoreEffect, requiredClicks);
    }
}