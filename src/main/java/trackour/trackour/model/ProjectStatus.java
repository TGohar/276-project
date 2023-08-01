package trackour.trackour.model;

public enum ProjectStatus {
    IN_PROGRESS("IN_PROGRESS"),
    COMPLETED("COMPLETED");

    private String value;

    private ProjectStatus(String value){
        this.value = value;
    }

    public String toString() {
        return this.name();
    }

    public String statusToString() {
        return this.value;
    }
}
