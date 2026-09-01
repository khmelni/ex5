package base;

public abstract class IdentifiedObject {
    protected final int id;

    protected IdentifiedObject(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}