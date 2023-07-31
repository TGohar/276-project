package trackour.trackour.model.project;

public enum ProjectStatus {
    IN_PROGRESS("IN_PROGRESS"),
    COMPLETED("COMPLETED");

    private final String value;

    private ProjectStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
