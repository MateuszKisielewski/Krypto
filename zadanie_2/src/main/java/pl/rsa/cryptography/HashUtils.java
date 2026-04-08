//Autorzy:
//Mateusz Kisielewski 254779
//Krzysztof Kata 254776
package pl.rsa.cryptography;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtils {
    /**
     * Metoda hashująca dane żeby podpisywać 256 bitów a nie cały plik
     * @param data - tablica bajtów do hashowania
     * @return - zwraca hash danych w postaci BigInteger (signum 1 odpowiada ze pewność że bajty są dodatnie czyli interpretowane bez znaku)
     * @throws NoSuchAlgorithmException - gdy nie ma takiej metody hashowania rzuca błędem
     */
    public static BigInteger dataToHash(byte[] data) throws NoSuchAlgorithmException {
        MessageDigest hashData;
        hashData = MessageDigest.getInstance("SHA-256");
        byte[] hashedData = hashData.digest(data);
        return new BigInteger(1, hashedData);
    }
}
