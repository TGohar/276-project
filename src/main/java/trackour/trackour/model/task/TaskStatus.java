package trackour.trackour.model.task;

public enum TaskStatus {
    NOT_STARTED("NOT_STARTED"),
    IN_PROGRESS("IN_PROGRESS"),
    COMPLETED("COMPLETED");

    private final String value;

    private TaskStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
