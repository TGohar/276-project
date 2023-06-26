package trackour.trackour.models;

public enum Role {
    ADMIN("ADMIN"),
    USER("USER");
    // manually add more roles

    private String value;

    private Role(String value){
        this.value = value;
    }

    public String getName() {
        return this.value;
    }
}
