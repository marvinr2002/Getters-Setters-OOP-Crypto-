import java.io.*;
import java.util.*;
import javax.crypto.AEADBadTagException;

public class PersonRepository {
    private final File file;

    public PersonRepository(String encryptedFilePath) {
        this.file = new File(encryptedFilePath);
    }

    @SuppressWarnings("unchecked")
    public List<Person> load(char[] password) throws Exception {
        if (!file.exists()) return new ArrayList<>();
        byte[] fileBytes = readAllBytes(file);
        byte[] plain = CryptoUtils.decrypt(fileBytes, password);
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(plain))) {
            Object obj = ois.readObject();
            if (obj instanceof List<?>) return (List<Person>) obj;
            throw new IOException("Contenido no reconocido.");
        }
    }

    public void save(List<Person> people, char[] password) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(people);
        }
        byte[] encrypted = CryptoUtils.encrypt(bos.toByteArray(), password);
        writeAllBytes(file, encrypted);
        try { // permisos estrictos (Unix)
            file.setReadable(false, false); file.setWritable(false, false); file.setExecutable(false, false);
            file.setReadable(true, true); file.setWritable(true, true);
        } catch (Exception ignored) {}
    }

    private static byte[] readAllBytes(File f) throws IOException {
        try (FileInputStream in = new FileInputStream(f)) { return in.readAllBytes(); }
    }
    private static void writeAllBytes(File f, byte[] data) throws IOException {
        try (FileOutputStream out = new FileOutputStream(f)) { out.write(data); }
    }
}
