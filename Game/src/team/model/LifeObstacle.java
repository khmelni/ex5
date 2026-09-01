package team.model;

public class LifeObstacle extends Character {
    
    private static final int lifeEffect = -1;      
    private static final int scoreEffect = 0;
    private static final int requiredClicks = 1;

    public LifeObstacle() {
        super (lifeEffect, scoreEffect, requiredClicks);
    }
}