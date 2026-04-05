package pl.rsa.cryptography;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtils {
    public static BigInteger dataToHash(byte[] data) throws NoSuchAlgorithmException {
        MessageDigest hashData;
        hashData = MessageDigest.getInstance("SHA-256");
        byte[] hashedData = hashData.digest(data);
        return new BigInteger(1, hashedData);
    }
}
