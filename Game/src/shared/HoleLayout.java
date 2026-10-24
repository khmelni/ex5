package shared;

/** מיקום מרכז של חור אחד על הלוח, במערכת הקואורדינטות של הלוח (ראו BoardLayout). */
public final class HoleLayout {

    private final int id;
    private final double centerX;
    private final double centerY;

    public HoleLayout(int id, double centerX, double centerY) {
        this.id = id;
        this.centerX = centerX;
        this.centerY = centerY;
    }

    public int getId() { return id; }
    public double getCenterX() { return centerX; }
    public double getCenterY() { return centerY; }
}
