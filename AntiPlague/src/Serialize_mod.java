import java.io.*;
import java.util.HashMap;

public class Serialize_mod {
    // Method to serialize the ListModel to a file
    public void serializeMapModel(HashMap<String, Long> mapScore, String fileName) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            // Directly serialize the HashMap
            oos.writeObject(mapScore);
            // System.out.println("HashMap serialized to " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public HashMap<String, Long> deserializeMapModel(String fileName) {
        HashMap<String, Long> mapScore = null;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            mapScore = (HashMap<String, Long>) ois.readObject();
            // System.out.println("HashMap deserialized from " + fileName);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return mapScore;
    }
}
