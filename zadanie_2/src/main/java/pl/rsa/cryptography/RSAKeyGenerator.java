//Autorzy:
//Mateusz Kisielewski 254779
//Krzysztof Kata 254776
package pl.rsa.cryptography;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Klasa odpowiedzialna za generowanie pary kluczy do algorytmu RSA
 */
public class RSAKeyGenerator {

    /**
     * Obiekt służący do bezpiecznego losowania wartości kryptograficznych
     */
    SecureRandom rnd = new SecureRandom();

    /**
     * Generuje nową parę kluczy RSA o zadanej wielkości
     * @param bitLenght Oczekiwana całkowita długość generowanego modułu n w bitach
     * @return Obiekt klasy RSAKey zawierający wygenerowany klucz publiczny, klucz prywatny oraz moduł n
     */
    public RSAKey generateRSAKey(int bitLenght) {
        BigInteger p = BigInteger.probablePrime(bitLenght / 2, rnd);
        BigInteger q = BigInteger.probablePrime(bitLenght / 2, rnd);

        BigInteger n = p.multiply(q);
        BigInteger e = BigInteger.valueOf(65537);

        BigInteger eulerFunction = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));

        while (!e.gcd(eulerFunction).equals(BigInteger.ONE)) {
            e = e.add(BigInteger.TWO);
        }

        BigInteger d = e.modInverse(eulerFunction);

        return new RSAKey(e,d,n);
    }
}