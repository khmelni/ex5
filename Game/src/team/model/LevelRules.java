package team.model;

/**
 * חוקי הגדילה של הלוח לפי ניקוד.
 *
 * כל pointsPerLevel נקודות הלוח גדל בצעד אחד, לסירוגין: קודם עמודה, אחר כך שורה.
 * עם ברירת המחדל (3x3, צעד 50): 0 -> 3x3, 50 -> 3 שורות 4 עמודות, 100 -> 4x4,
 * 150 -> 4 שורות 5 עמודות, 200 -> 5x5 ... עד maxRows x maxColumns (9x9).
 *
 * levelIndex מתחיל מ-0 (הלוח ההתחלתי).
 */
public final class LevelRules {

    private final int pointsPerLevel;
    private final int startRows;
    private final int startColumns;
    private final int maxRows;
    private final int maxColumns;

    /** חוקי ברירת המחדל: צעד כל 50 נקודות, מלוח 3x3 ועד 9x9. כאן משנים אותם. */
    public static LevelRules defaultRules() {
        int pointsPerLevel = 50;   // X: כל כמה נקודות הלוח גדל
        int startRows = 3;         // גודל הלוח ההתחלתי
        int startColumns = 3;
        int maxRows = 9;           // גודל הלוח הסופי
        int maxColumns = 9;
        return new LevelRules(pointsPerLevel, startRows, startColumns, maxRows, maxColumns);
    }

    
    public LevelRules(int pointsPerLevel, int startRows, int startColumns, int maxRows, int maxColumns) {
        if (pointsPerLevel <= 0) throw new IllegalArgumentException("pointsPerLevel must be > 0");
        if (maxRows < startRows || maxColumns < startColumns) {
            throw new IllegalArgumentException("max board size must be >= start board size");
        }
        this.pointsPerLevel = pointsPerLevel;
        this.startRows = startRows;
        this.startColumns = startColumns;
        this.maxRows = maxRows;
        this.maxColumns = maxColumns;
    }

    public int getPointsPerLevel() { return pointsPerLevel; }

    /** האינדקס של השלב האחרון (זה שבו הלוח הגיע לגודל המקסימלי). */
    public int getMaxLevelIndex() {
        int columnsReachMaxAt = 2 * (maxColumns - startColumns) - 1;
        int rowsReachMaxAt = 2 * (maxRows - startRows);
        return Math.max(0, Math.max(columnsReachMaxAt, rowsReachMaxAt));
    }

    /** באיזה שלב צריך להיות הלוח עבור ניקוד נתון (ניקוד שלילי = שלב ראשון). */
    public int levelIndexForScore(int score) {
        if (score <= 0) return 0;
        return Math.min(score / pointsPerLevel, getMaxLevelIndex());
    }

    /** גודל הלוח בשלב נתון: עמודה, שורה, עמודה, שורה... */
    public BoardSize sizeForLevelIndex(int levelIndex) {
        int columns = Math.min(maxColumns, startColumns + (levelIndex + 1) / 2);
        int rows = Math.min(maxRows, startRows + levelIndex / 2);
        return new BoardSize(rows, columns);
    }
}
