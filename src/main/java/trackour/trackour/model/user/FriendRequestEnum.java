package trackour.trackour.model.user;

public enum FriendRequestEnum {
    SUCCESS(0),
    USER_DOES_NOT_EXIST(1),
    CANNOT_ADD_YOURSELF(2),
    REQUEST_ALREADY_SENT(3),
    ALREADY_FRIENDS(4);

    private int value;

    private FriendRequestEnum(int value){
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}

