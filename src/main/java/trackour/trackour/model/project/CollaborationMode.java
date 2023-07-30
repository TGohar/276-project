package trackour.trackour.model.project;

public enum CollaborationMode {
    SOLO(0),
    TEAM(1);

    private final int value;

    private CollaborationMode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}