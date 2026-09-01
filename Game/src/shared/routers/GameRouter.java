package shared.routers;

import base.Params;
import base.SubRouter;
import my_base.App;
import team.control.GameBackend;

public class GameRouter implements SubRouter {

    private final GameBackend backend;

    public GameRouter() {
        this.backend = App.content().gameBackend();
    }

    @Override
    public Object route(String subPath, Params p) {

        switch (subPath) {

            // 1. הפעלת המשחק
            case "/start":
                backend.start();
                return null;

            // 2. כפתור START OVER - מאפס את הניקוד, החיים והדמויות
            case "/reset":
                backend.reset();
                return null;

            // 3. עדכון זמן ידני במידה וה-UI מזרים פעימות שעון
            case "/tick": {
                double dt = p.getDouble(0);
                backend.tick(dt);
                return null;
            }

            // 4. זיהוי לחיצה לפי קואורדינטות העכבר (x, y) שנשלחו מה-UI
            case "/click": {
                double clickX = p.getDouble(0);
                double clickY = p.getDouble(1);

                backend.handleMouseClick(clickX, clickY);
                return null;
            }
            
            default:
                throw new RuntimeException("Unknown game route: " + subPath);
        }
    }
}