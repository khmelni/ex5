package team.model;

/**
 * הגיאומטריה של הלוח הנקי (תמונת הרקע היחידה): גודל מערכת הקואורדינטות,
 * האזור הצהוב שבו מפוזרים החורים, ויחסי הגודל של החור.
 * זה המקום היחיד שמגדיר איפה החורים יושבים - ה-UI לא יודע על זה כלום.
 */
public final class BoardGeometry {

    private final int boardWidth;            // רוחב מערכת הקואורדינטות (= רוחב תמונת הרקע)
    private final int boardHeight;           // גובה מערכת הקואורדינטות (= גובה תמונת הרקע)
    private final double playAreaX;          // פינה שמאלית עליונה של אזור החול
    private final double playAreaY;
    private final double playAreaWidth;
    private final double playAreaHeight;
    private final double holeAspectRatio;    // רוחב/גובה של תמונת החור
    private final double holeFillRatio;      // איזה חלק מהתא החור תופס (פחות מ-1 משאיר רווח)
    private final double hitRadiusRatio;     // רדיוס זיהוי הלחיצה ביחס לגובה החור

    public BoardGeometry(int boardWidth, int boardHeight,
                         double playAreaX, double playAreaY,
                         double playAreaWidth, double playAreaHeight,
                         double holeAspectRatio, double holeFillRatio, double hitRadiusRatio) {
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        this.playAreaX = playAreaX;
        this.playAreaY = playAreaY;
        this.playAreaWidth = playAreaWidth;
        this.playAreaHeight = playAreaHeight;
        this.holeAspectRatio = holeAspectRatio;
        this.holeFillRatio = holeFillRatio;
        this.hitRadiusRatio = hitRadiusRatio;
    }

    /** הגיאומטריה של Board_no_holes.png (1024 x 890) ושל hole.png (440 x 385). */
    public static BoardGeometry defaultGeometry() {
        return new BoardGeometry(1024, 890, 98, 99, 830, 735, 440.0 / 385.0, 0.72, 0.55);
    }

    public int getBoardWidth() { return boardWidth; }
    public int getBoardHeight() { return boardHeight; }
    public double getPlayAreaX() { return playAreaX; }
    public double getPlayAreaY() { return playAreaY; }
    public double getPlayAreaWidth() { return playAreaWidth; }
    public double getPlayAreaHeight() { return playAreaHeight; }
    public double getHoleAspectRatio() { return holeAspectRatio; }
    public double getHoleFillRatio() { return holeFillRatio; }
    public double getHitRadiusRatio() { return hitRadiusRatio; }
}
