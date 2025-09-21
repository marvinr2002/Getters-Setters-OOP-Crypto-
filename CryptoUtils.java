import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.AEADBadTagException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

public class CryptoUtils {
    private static final int GCM_TAG_BITS = 128;
    private static final int GCM_IV_BYTES = 12;
    private static final int PBKDF2_ITER = 100_000;
    private static final int KEY_BITS = 256;
    private static final int SALT_BYTES = 16;

    public static byte[] encrypt(byte[] plaintext, char[] password) throws Exception {
        SecureRandom rnd = new SecureRandom();
        byte[] salt = new byte[SALT_BYTES]; rnd.nextBytes(salt);
        SecretKey key = deriveKey(password, salt);

        byte[] iv = new byte[GCM_IV_BYTES]; rnd.nextBytes(iv);

        Cipher c = Cipher.getInstance("AES/GCM/NoPadding");
        c.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_BITS, iv));
        byte[] cipher = c.doFinal(plaintext);

        ByteBuffer buf = ByteBuffer.allocate(4 + 4 + salt.length + 4 + iv.length + cipher.length);
        buf.put("ENC1".getBytes(StandardCharsets.US_ASCII));
        buf.putInt(salt.length); buf.put(salt);
        buf.putInt(iv.length);   buf.put(iv);
        buf.put(cipher);
        return buf.array();
    }

    public static byte[] decrypt(byte[] packaged, char[] password) throws Exception {
        ByteBuffer buf = ByteBuffer.wrap(packaged);
        byte[] magic = new byte[4]; buf.get(magic);
        if (!new String(magic, StandardCharsets.US_ASCII).equals("ENC1"))
            throw new SecurityException("Formato inválido.");

        int saltLen = buf.getInt(); byte[] salt = new byte[saltLen]; buf.get(salt);
        int ivLen = buf.getInt();   byte[] iv = new byte[ivLen];     buf.get(iv);
        byte[] cipher = new byte[buf.remaining()]; buf.get(cipher);

        SecretKey key = deriveKey(password, salt);
        Cipher c = Cipher.getInstance("AES/GCM/NoPadding");
        c.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_BITS, iv));
        return c.doFinal(cipher);
    }

    private static SecretKey deriveKey(char[] password, byte[] salt) throws Exception {
        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password, salt, PBKDF2_ITER, KEY_BITS);
        byte[] keyBytes = f.generateSecret(spec).getEncoded();
        return new SecretKeySpec(keyBytes, "AES");
    }
}
