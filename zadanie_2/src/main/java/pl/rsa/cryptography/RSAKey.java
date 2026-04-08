//Autorzy:
//Mateusz Kisielewski 254779
//Krzysztof Kata 254776
package pl.rsa.cryptography;

import java.math.BigInteger;

public class RSAKey {
    private final BigInteger e;
    private final BigInteger d;
    private final BigInteger n;

    /**
     * konstruktor klasy RSAKey
     * @param e - klucz publiczny
     * @param d - klucz prywatny
     * @param n - liczba naturalna (iloczyn p i q)
     */
    public RSAKey(BigInteger e, BigInteger d, BigInteger n) {
        this.e = e;
        this.d = d;
        this.n = n;
    }

    /**
     * getter dla klucza publicznego
     * @return zwraca klucz publiczny w postaci BigInteger
     */
    public BigInteger getE() {
        return e;
    }

    /**
     * getter dla klucza prywatnego
     * @return zwraca klucz prywatny w postaci BigInteger
     */
    public BigInteger getD() {
        return d;
    }

    /**
     * getter dla liczby naturalnej (iloczyn p i q)
     * @return - zwraca liczbę naturalną w postaci BigInteger
     */
    public BigInteger getN() {
        return n;
    }
}