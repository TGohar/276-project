package trackour.trackour.views.api;

public enum ClientKeys {
    CLIENT_ID("5d3433cc338a403d965f77d06e65ce86"),
    CLIENT_SECRET("02323e07bb74463c84d2f425f1a8439f");

    private String key;

    ClientKeys(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}