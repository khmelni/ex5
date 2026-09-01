package team.model;

public class Timon extends Character {
           
    private static final int lifeEffect = 1;      
    private static final int scoreEffect = 10;
    private static final int requiredClicks = 1;

    public Timon() {
        super (lifeEffect, scoreEffect, requiredClicks);
    }
}