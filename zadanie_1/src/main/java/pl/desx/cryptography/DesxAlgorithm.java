/**
 * Autorzy: Krzysztof Kata (254776) i Mateusz Kisielewski (254779)
 */
package pl.desx.cryptography;

import java.security.SecureRandom;

public class DesxAlgorithm {
   private final SecureRandom sr = new SecureRandom();

    private long key_1;
    private long key_2;
    private long key_3;

    public void generate_keys() {
        this.key_1 = sr.nextLong();
        this.key_2 = sr.nextLong();
        this.key_3 = sr.nextLong();
    }

    public long get_key_1() {
        return key_1;
    }

    public long get_key_2() {
        return key_2;
    }

    public long get_key_3() {
        return key_3;
    }

    public void set_keys(long k1, long k2, long k3) {
        this.key_1 = k1;
        this.key_2 = k2;
        this.key_3 = k3;
    }

    public long main_desx_block_encrypt(long plain_text) {
        DesAlgorithm des = new DesAlgorithm();
        long first_xor = key_1 ^ plain_text;
        long des_result = des.main_des_block_encrypt(first_xor, key_2);
        long second_xor = key_3 ^ des_result;

        return second_xor;
    }

    public long main_desx_block_decrypt(long cipher_text) {
        DesAlgorithm des = new DesAlgorithm();
        long first_decrypt = key_3 ^ cipher_text;
        long second_decrypt = des.main_des_block_decrypt(first_decrypt, key_2);
        long third_decrypt = key_1 ^ second_decrypt;

        return third_decrypt;
    }


}
