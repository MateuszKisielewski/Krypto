package pl.rsa.cryptography;

import java.math.BigInteger;
import java.security.SecureRandom;

public class RSAKeyGenerator {
    SecureRandom rnd = new SecureRandom();
    
    RSAKey generateRSAKey(int bitLenght) {
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
