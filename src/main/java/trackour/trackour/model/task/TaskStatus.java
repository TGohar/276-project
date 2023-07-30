package trackour.trackour.model.task;

public enum TaskStatus {
    NOT_STARTED(0),
    IN_PROGRESS(1),
    COMPLETED(2);

    private final int value;

    private TaskStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
