package trackour.trackour.views.components.camelotwheel;

// Use an enum instead of a class for the Key enum
public enum Key {
    // Declare the 24 keys as constants with their associated values
    A_FLAT_MINOR("Ab/G#m", "1A", "4m", 4, 0),
    B_MAJOR("B", "1B", "4d", 11, 1),
    F_SHARP_MAJOR("F#/Gb", "2B", "5d", 6, 1),
    D_FLAT_MINOR("Db/C#m", "2A", "5m", 1, 0),
    E_MAJOR("E", "12B", "9d", 4, 1),
    C_SHARP_MINOR("C#m/Dbm", "12A", "9m", 1, 0),
    A_MAJOR("A", "11B", "8d", 9, 1),
    F_SHARP_MINOR("F#m/Gbm","11A","8m",6,0),
    D_MAJOR("D","10B","7d",2,1),
    B_MINOR("Bm","10A","7m",11,0),
    G_MAJOR("G","9B","6d",7,1),
    E_MINOR("Em","9A","6m",4,0),
    C_MAJOR("C","8B","3d",0,1),
    A_MINOR("Am","8A","3m",9,0),
    F_MAJOR("F","7B","2d",5,1),
    D_MINOR("Dm","7A","2m",2,0),
    B_FLAT_MAJOR("Bb/A#","6B","1d" ,10 ,1 ),
    G_MINOR("Gm/F#m" ,"6A" ,"1m" ,7 ,0 ),
    E_FLAT_MAJOR("Eb/D#" ,"5B" ,"12d" ,3 ,1 ),
    C_MINOR("Cm/B#m" ,"5A" ,"12m" ,0 ,0 ),
    A_FLAT_MAJOR("Ab/G#" ,"4B" ,"11d" ,8 ,1 ),
    F_MINOR("Fm/E#m" ,"4A" ,"11m" ,5 ,0 ),
    D_FLAT_MAJOR("Db/C#" ,"3B" ,"10d" ,1 ,1 ),
    B_FLAT_MINOR("Bbm/A#m" ,"3A" ,"10m" ,10 ,0 );

    // Declare the fields for the key attributes
    public final String name; // the unique key string used to map this record (like a primary key/id)
    public final String camelot; // the camelot notation of this key
    public final String open; // the open key notation of this key
    public final String camelotLetter; // the letter of the camekot key notation of this key
    public final int camelotNumber; // the number of the camelot key notation of this key
    public final String openLetter; // the letter of the open key notation of this key
    public final int openNumber; // the number of the open key notaiton of this key
    public final int spotifyKey; // the pitch class (int) notation of this key and for the key's pitch that spotify track api uses 
    public final int spotifyMode; // the mode (int), which is 1/0, of this key that spotify track api uses

    // Define a constructor for the enum that takes the same parameters as the fields and assigns them
    private Key(String name, String camelot, String open, int spotifyKey, int spotifyMode) {
        this.name = name;
        this.camelot = camelot;
        this.open = open;
        this.spotifyKey = spotifyKey;
        this.spotifyMode = spotifyMode;
        // Derive the camelotLetter, camelotNumber, openLetter, and openNumber from the camelot and open values
        this.camelotLetter = camelot.substring(camelot.length() - 1); // the last character of the camelot value
        this.camelotNumber = Integer.parseInt(camelot.substring(0, camelot.length() - 1)); // the first part of the camelot value as an integer
        this.openLetter = open.substring(open.length() - 1); // the last character of the open value
        this.openNumber = Integer.parseInt(open.substring(0, open.length() - 1)); // the first part of the open value as an integer
    }

    // Optionally, define a toString() method to return a custom string representation of the enum constants
    @Override
    public String toString() {
        return "Key{" +
                "name='" + name + '\'' +
                ", camelot='" + camelot + '\'' +
                ", open='" + open + '\'' +
                ", spotifyKey=" + spotifyKey +
                ", spotifyMode=" + spotifyMode +
                ", camelotLetter=" + camelotLetter +
                ", camelotNumber=" + camelotNumber +
                ", openLetter=" + openLetter +
                ", openNumber=" + openNumber +
                '}';
    }
}
