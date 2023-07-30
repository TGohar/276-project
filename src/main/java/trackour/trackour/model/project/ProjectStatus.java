package trackour.trackour.model.project;

public enum ProjectStatus {
    IN_PROGRESS(0),
    COMPLETED(1);

    private final int value;

    private ProjectStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
