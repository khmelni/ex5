package shared;

import java.util.Collections;
import java.util.List;

/**
 * הגדרת הלוח כפי שה-Backend קובע אותה ושולח ל-UI: כמה חורים, איפה כל אחד, ואיזה גודל לצייר.
 * ה-UI רק מצייר לפי המידע הזה; הוא לא מחשב גיאומטריה בעצמו.
 *
 * כל המידות הן במערכת הקואורדינטות של הלוח (boardWidth x boardHeight, שהיא גודל תמונת הרקע).
 * ה-UI מתרגם אותן לפיקסלים של המסך לפי גודל הפאנל.
 *
 * האובייקט עובר מה-Backend ל-UI דרך ה-Router (בהתחלה) ודרך GameUiPort.setupBoard (בכל שינוי לוח),
 * ולכן הוא ב-shared ולא תלוי במחלקות המודל.
 */
public final class BoardLayout {

    private final int levelNumber;
    private final int boardWidth;
    private final int boardHeight;
    private final List<HoleLayout> holes;
    private final double holeWidth;
    private final double holeHeight;
    private final double characterSize;

    public BoardLayout(int levelNumber, int boardWidth, int boardHeight, List<HoleLayout> holes,
                       double holeWidth, double holeHeight, double characterSize) {
        this.levelNumber = levelNumber;
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        this.holes = Collections.unmodifiableList(holes);
        this.holeWidth = holeWidth;
        this.holeHeight = holeHeight;
        this.characterSize = characterSize;
    }

    public int getLevelNumber() { return levelNumber; }
    public int getBoardWidth() { return boardWidth; }
    public int getBoardHeight() { return boardHeight; }
    public List<HoleLayout> getHoles() { return holes; }
    public double getHoleWidth() { return holeWidth; }
    public double getHoleHeight() { return holeHeight; }
    public double getCharacterSize() { return characterSize; }
}
