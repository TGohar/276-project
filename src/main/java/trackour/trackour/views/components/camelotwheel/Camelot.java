package trackour.trackour.views.components.camelotwheel;
import java.util.ArrayList;
import java.util.List;

public final class Camelot {

    // No need to declare a map as a field, since the enum already stores the keys

    // Use a constructor instead of a static block to initialize the key map
    public Camelot() {
        // No need to create the key records or put them into the map, since the enum already does that
    }

    // Define a method that returns a list of all keys in a given notation
    public static List<String> getAllKeys(Notation notation) {
        // Create an empty list to store the keys
        List<String> keys = new ArrayList<>();
        // Loop through all the enum constants
        for (Key key : Key.values()) {
            // Switch on the notation and add the corresponding value to the list
            switch (notation) {
                case CAMELOT_KEY -> keys.add(key.camelot);
                case OPEN_KEY -> keys.add(key.open);
                case STANDARD_KEY -> keys.add(key.name);
                case SPOTIFY -> keys.add(key.spotifyKey + "," + key.spotifyMode);
            }
        }
        // Return the list of keys
        return keys;
    }

    public static List<Key> getAllKeys() {
        // Create an empty list to store the keys
        List<Key> keys = new ArrayList<>();
        // Loop through all the enum constants
        for (Key key : Key.values()) {
            // Switch on the notation and add the corresponding value to the list
            keys.add(key);
        }
        // Return the list of keys
        return keys;
    }

    // Define a method that returns a key by its name in any notation
    public static Key getKeyByName(String name, Notation notation) {
        // Loop through all the enum constants
        for (Key key : Key.values()) {
            // Switch on the notation and compare the name with the corresponding value
            switch (notation) {
                case CAMELOT_KEY -> { if (key.camelot.contains(name)) return key; }
                case OPEN_KEY -> { if (key.open.contains(name)) return key; }
                case STANDARD_KEY -> { if (key.name.contains(name)) return key; }
                default -> throw new IllegalArgumentException("Unexpected value: " + notation);
            }
        }
        // If no match is found, throw an exception
        return null;
    }

    public static Key getKeyBySpotify(int spotifyKey, int spotifyMode) {
        // Loop through all the enum constants
        for (Key key : Key.values()) {
            if (key.spotifyKey == spotifyKey && key.spotifyMode == spotifyMode){
                return key;
            }
        }
        // If no match is found, throw an exception
        return null;
    }

    // Define the methods
public static boolean isSameKey(Key outputKey, Key inputKey) {
    return outputKey.camelot.equals(inputKey.camelot);
}

public static boolean isRelativeKey(Key outputKey, Key inputKey) {
    int outputNumber = outputKey.camelotNumber;
    String outputLetter = outputKey.camelotLetter;
    int inputNumber = inputKey.camelotNumber;
    String inputLetter = inputKey.camelotLetter;
    return outputNumber == inputNumber && outputLetter.equals(inputLetter.equals("A") ? "B" : "A");
}

public static boolean isOneStepAway(Key outputKey, Key inputKey) {
    int outputNumber = outputKey.camelotNumber;
    String outputLetter = outputKey.camelotLetter;
    int inputNumber = inputKey.camelotNumber;
    String inputLetter = inputKey.camelotLetter;
    int oneStepUp =  (inputNumber + 1) % 12 == 0 ? 12 : (inputNumber + 1) % 12;
    int oneStepDown =  (inputNumber - 1) % 12 == 0 ? 12 : (inputNumber - 1) % 12;
    Boolean sameLetter = outputLetter.equals(inputLetter);
    return (sameLetter && outputNumber == oneStepUp) || (sameLetter && outputNumber == oneStepDown);
}

    // Define a method that returns a list of compatible keys in a given notation for a given key name in any notation
    public static List<Key> getCompatibleKeys(String name, Notation inputNotation) {
        // Get the key object by its name and input notation
        Key inputKey = getKeyByName(name, inputNotation);
        // Create an empty list to store the compatible keys
        List<Key> compatibleKeys = new ArrayList<>();

        if (inputKey == null) {
            return compatibleKeys;
        }
        // Loop through all the enum constants
        for (Key outputKey : Key.values()) {
            // Check if the output key is harmonically compatible with the input key
            if (isSameKey(outputKey, inputKey) || isRelativeKey(outputKey, inputKey) || isOneStepAway(outputKey, inputKey)) {
                // Use the output notation interface to get the name of the key in that notation and add it to the list
                compatibleKeys.add(outputKey);
            }
        }
        // Return the list of compatible keys
        return compatibleKeys;
    }

    public static List<Key> getCompatibleKeysBySpotify(String name, Notation inputNotation) {
        // Get the key object by its name and input notation
        Key inputKey = getKeyByName(name, inputNotation);
        // Create an empty list to store the compatible keys
        List<Key> compatibleKeys = new ArrayList<>();
        // Loop through all the enum constants
        for (Key outputKey : Key.values()) {
            // Check if the output key is harmonically compatible with the input key
            if (isSameKey(outputKey, inputKey) || isRelativeKey(outputKey, inputKey) || isOneStepAway(outputKey, inputKey)) {
                // Use the output notation interface to get the name of the key in that notation and add it to the list
                compatibleKeys.add(outputKey);
            }
        }
        // Return the list of compatible keys
        return compatibleKeys;
    }

    // Create a method to print the keys
    public static void printKeys() {
        System.out.println("keys List:");
        // Loop through the key map entries
        for (Key key : Key.values()) {
            System.out.println(key.name);
        }
    }
}
