package team.model;

public class LifeGift extends Character {
    
    private static final int lifeEffect = 1;      
    private static final int scoreEffect = 5;
    private static final int requiredClicks = 1;

    public LifeGift() {
        super (lifeEffect, scoreEffect, requiredClicks);
    }
}