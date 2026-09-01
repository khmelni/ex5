package my_base;

import base.PeriodicLoop;

public class MyPeriodicLoop extends PeriodicLoop {

    // 1. הגדרת משך הזמן של כל פעימה בשניות (20 מילישניות = 0.02 שניות)
    private static final double DELTA_TIME = 0.02;

    @Override
    public void execute() {
        // Let the super class do its work first
        super.execute();
        
        // 2. עדכון מנוע המשחק (ה-Backend) בכל פעימה של השעון הראשי
        // זה מה שיגרום לטיימרים לרוץ ולדמויות לצוץ ולהיעלם במסך!
        App.content().gameBackend().tick(DELTA_TIME);
    }
}
