package pl.rsa.cryptography;

import java.math.BigInteger;
import java.security.SecureRandom;

public class BlindSignature {
    private final SecureRandom sr = new SecureRandom();

    BigInteger[] blindingText(BigInteger m, BigInteger e, BigInteger n){
        BigInteger r;

        do {
            r = new BigInteger(n.bitLength(), sr);
        }
        while (r.compareTo(BigInteger.ONE) <= 0 || r.compareTo(n) >= 0 || !r.gcd(n).equals(BigInteger.ONE));

        BigInteger rToPowerOFe = r.modPow(e, n);

        BigInteger blindText = m.multiply(rToPowerOFe).mod(n);

        return new BigInteger[]{blindText, r};
    }
}
