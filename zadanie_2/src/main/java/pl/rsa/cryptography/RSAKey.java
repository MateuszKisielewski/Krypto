package pl.rsa.cryptography;

import java.math.BigInteger;

public class RSAKey {
    public BigInteger e;
    public BigInteger d;
    public BigInteger n;

    public RSAKey(BigInteger e, BigInteger d, BigInteger n) {
        this.e = e;
        this.d = d;
        this.n = n;
    }

    public BigInteger getE() {
        return e;
    }

    public BigInteger getD() {
        return d;
    }

    public BigInteger getN() {
        return n;
    }
}