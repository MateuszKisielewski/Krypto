//Autorzy:
//Mateusz Kisielewski 254779
//Krzysztof Kata 254776
package pl.rsa.cryptography;

import java.math.BigInteger;
import java.security.SecureRandom;

public class BlindSignature {
    private final SecureRandom sr = new SecureRandom();

    public BigInteger[] blindingText(BigInteger m, BigInteger e, BigInteger n){
        BigInteger r;

        do {
            r = new BigInteger(n.bitLength(), sr);
        }
        while (r.compareTo(BigInteger.ONE) <= 0 || r.compareTo(n) >= 0 || !r.gcd(n).equals(BigInteger.ONE));

        BigInteger rToPowerOFe = r.modPow(e, n);

        BigInteger blindedText = m.multiply(rToPowerOFe).mod(n);

        return new BigInteger[]{blindedText, r};
    }

    public BigInteger signBlindText(BigInteger blindedText, BigInteger d, BigInteger n){
        return blindedText.modPow(d, n);
    }

    public BigInteger unblindSignedBlindedText (BigInteger signedBlindedText, BigInteger r, BigInteger n){
        BigInteger rInverse = r.modInverse(n);
        BigInteger signedText = signedBlindedText.multiply(rInverse).mod(n);
        return signedText;
    }

    public boolean verifySignedText (BigInteger signedText, BigInteger m, BigInteger e, BigInteger n){
        BigInteger decrypted = signedText.modPow(e, n);
        return decrypted.equals(m);
    }
}
