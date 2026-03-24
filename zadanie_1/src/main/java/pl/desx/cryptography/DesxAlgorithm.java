/**
 * Autorzy:
 * Mateusz Kisielewski
 * Krzysztof Kata
 */
package pl.desx.cryptography;

import java.security.SecureRandom;

public class DesxAlgorithm {
    private final SecureRandom sr = new SecureRandom();

    private long key_1;
    private long key_2;
    private long key_3;

    /**
     * Generujemy trzy losowe klucze przy pomocy klasy SecureRandom, które przypisujemy odpowiednio do zmiennych
     */
    public void generate_keys() {
        this.key_1 = sr.nextLong();
        this.key_2 = sr.nextLong();
        this.key_3 = sr.nextLong();
    }

    /**
     * getter dla klucza pierwszego
     *
     * @return - zwracana jest wartość pierwszego klucza
     */
    public long get_key_1() {
        return key_1;
    }

    /**
     * getter dla klucza drugiego
     *
     * @return - zwracana jest wartość drugiego klucza
     */
    public long get_key_2() {
        return key_2;
    }

    /**
     * getter dla klucza trzeciego
     *
     * @return - zwracana jest wartość trzeciego klucza
     */
    public long get_key_3() {
        return key_3;
    }

    /**
     * Setter dla kluczy. Jako parametr podajemy wartości kluczy, które chcemy ustawić
     *
     * @param k1 - podajemy pierwszy klucz
     * @param k2 - podajemy drugi klucz
     * @param k3 - podajemy trzeci klucz
     */
    public void set_keys(long k1, long k2, long k3) {
        this.key_1 = k1;
        this.key_2 = k2;
        this.key_3 = k3;
    }

    /**
     * Główna metoda szyfrująca przekazywany ciąg znaków,
     * pierwszy etap to XOR pierwszego klucza z tekstem jawnym,
     * drugi etap to wywołanie na wyniku poprzedniego etapu szyfrowania DES
     * trzeci etap to XOR z klucza trzeciego z wynikiem DES-a
     *
     * @param plain_text - jest to tekst jawny, który przekazujemy do algorytmu szyfrującego
     * @return - zwracamy zaszyfrowany wynik
     */
    public long main_desx_block_encrypt(long plain_text) {
        DesAlgorithm des = new DesAlgorithm();
        long first_xor = key_1 ^ plain_text;
        long des_result = des.main_des_block_encrypt(first_xor, key_2);
        long second_xor = key_3 ^ des_result;

        return second_xor;
    }

    /**
     * Główna metoda deszyfrująca przekazany zaszyfrowany ciąg znaków,
     * pierwszy etap to XOR trzeciego klucza z zaszyfrowanym tekstem,
     * drugi etap to deszyfrowanie poprzez odwrócenie algorytmu DES-a wykonane na wyniku pierwszego etapu
     * trzeci etap to XOR klucza pierwszego z wynikiem etapu drugiego
     *
     * @param cipher_text
     * @return
     */
    public long main_desx_block_decrypt(long cipher_text) {
        DesAlgorithm des = new DesAlgorithm();
        long first_decrypt = key_3 ^ cipher_text;
        long second_decrypt = des.main_des_block_decrypt(first_decrypt, key_2);
        long third_decrypt = key_1 ^ second_decrypt;

        return third_decrypt;
    }
}
