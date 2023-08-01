package trackour.trackour.model;

public enum Role {
    ADMIN("ADMIN"),
    USER("USER");
    // manually add more roles

    private String value;

    private Role(String value){
        this.value = value;
    }

    public String roleToString() {
        return this.value;
    }

    public String roleToRoleString() {
        return "ROLE_" + this.value;
    }
}
