//Autorzy:
//Mateusz Kisielewski 254779
//Krzysztof Kata 254776
package pl.rsa.cryptography;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Klasa implementująca matematyczne operacje ślepego podpisu cyfrowego RSA
 */
public class BlindSignature {

    /**
     * Obiekt do bezpiecznego losowania wartości kryptograficznych (czynnika r)
     */
    private final SecureRandom sr = new SecureRandom();

    /**
     * Zaślepia oryginalną wiadomość za pomocą losowego czynnika r
     * @param m Oryginalna wiadomość w postaci liczby całkowitej (zazwyczaj hash pliku)
     * @param e Klucz publiczny serwera
     * @param n Moduł klucza publicznego serwera
     * @return Tablica dwuelementowa zawierająca zaślepioną wiadomość oraz użyty czynnik r
     */
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

    /**
     * Podpisuje zaślepioną wiadomość przy użyciu klucza prywatnego serwera
     * @param blindedText Zaślepiona wiadomość otrzymana od klienta
     * @param d Klucz prywatny serwera
     * @param n Moduł klucza publicznego
     * @return Zaślepiony podpis wygenerowany przez serwer
     */
    public BigInteger signBlindText(BigInteger blindedText, BigInteger d, BigInteger n){
        return blindedText.modPow(d, n);
    }

    /**
     * Zdejmuje czynnik zaślepiający z podpisu otrzymanego od serwera
     * @param signedBlindedText Zaślepiony podpis od serwera
     * @param r Czynnik zaślepiający wylosowany w kroku zaślepiania
     * @param n Moduł klucza publicznego
     * @return Ostateczny, czysty podpis gotowy do dołączenia do pliku
     */
    public BigInteger unblindSignedBlindedText (BigInteger signedBlindedText, BigInteger r, BigInteger n){
        BigInteger rInverse = r.modInverse(n);
        BigInteger signedText = signedBlindedText.multiply(rInverse).mod(n);
        return signedText;
    }

    /**
     * Weryfikuje autentyczność czystego podpisu dla danej wiadomości
     * @param signedText Podpis do zweryfikowania
     * @param m Oryginalna wiadomość (hash weryfikowanego pliku)
     * @param e Klucz publiczny serwera
     * @param n Moduł klucza publicznego
     * @return Wartość true jeśli podpis jest prawidłowy, false w przeciwnym wypadku
     */
    public boolean verifySignedText (BigInteger signedText, BigInteger m, BigInteger e, BigInteger n){
        BigInteger decrypted = signedText.modPow(e, n);
        return decrypted.equals(m);
    }
}