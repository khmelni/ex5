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

            // 4. לחיצה: ה-UI שולח רק מיקום יחסי בתוך הלוח (0..1), וה-Backend בודק פגיעה
            case "/click": {
                double clickXRatio = p.getDouble(0);
                double clickYRatio = p.getDouble(1);

                backend.handleMouseClick(clickXRatio, clickYRatio);
                return null;
            }

            // 5. הגדרת הלוח הנוכחי (BoardLayout) עבור ה-UI
            case "/board-info": {
                return backend.getBoardInfo();
            }

            
            default:
                throw new RuntimeException("Unknown game route: " + subPath);
        }
    }
}