package trackour.trackour.model.project;

public enum CollaborationMode {
    SOLO("SOLO"),
    TEAM("TEAM");

    private final String value;

    private CollaborationMode(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}