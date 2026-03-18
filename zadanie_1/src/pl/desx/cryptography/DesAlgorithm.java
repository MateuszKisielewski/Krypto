package pl.desx.cryptography;

import java.security.SecureRandom;

public class DesAlgorithm {

    public long permute(long input_plain_text, byte[] table_bytes, int important_bytes){
        long output_plain_text = 0;
        for (int i=0; i<table_bytes.length; i++){
            output_plain_text <<= 1;
            int bit_position = table_bytes[i];
            long extracted_bit = (input_plain_text >>> (important_bytes - bit_position)) & 1L;
            output_plain_text |= extracted_bit;
        }
        return output_plain_text;
    }

    public long[] split_LPT_RPT(long output_IP) {
        long[] LPT_RPT = new long[2];
        LPT_RPT[0] = output_IP >>> 32; //Wywala te 32 bity z prawej
        LPT_RPT[1] = (output_IP << 32) >>> 32; //Wywala te 32 bity z lewej i przesuwa spowrotem na prawo, lewa strona to same zera
        return LPT_RPT; //zwracamy tablicę w której mamy lewą i prawą część osobno
    }

    public long XOR(long LPT, long fourSteppedRPT){
        return LPT ^ fourSteppedRPT; //po prostu xor z LPT i z tego RPT po tych 4 krokach
    }

    public long[] generate_sub_keys(long key_64) {
        long[] sub_keys = new long[16];

        long key_56 = permute(key_64, DesConstants.permuted_choice_1, 64);

        long left_key_28 = key_56 >>> 28;
        long right_key_28 = (key_56 << 36) >>> 36;
        int shift = 0;

        for (int i = 1; i<=16; i++){
            if (i == 1 || i == 2 || i == 9 || i == 16){
                shift = 1;
            }
            else {
                shift = 2;
            }

            long temp_bit = left_key_28 >>> (28 - shift);
            left_key_28 <<= shift;
            left_key_28 &= 0xFFFFFFFL;
            left_key_28 |= temp_bit;

            temp_bit = right_key_28 >>> (28 - shift);
            right_key_28 <<= shift;
            right_key_28 &= 0xFFFFFFFL;
            right_key_28 |= temp_bit;

            long shifted_key_56 = left_key_28;
            shifted_key_56 <<= 28;
            shifted_key_56 |= right_key_28;

            long key_48 = permute(shifted_key_56, DesConstants.permuted_choice_2, 56);

            sub_keys[i-1] = key_48;
        }

        return sub_keys;
    }

    public long main_algorythm(long plain_text) {
        long after_IP = permute(plain_text, DesConstants.initial_permutation, 64);
        for (int i=0; i<16; i++){
            long[] parts = split_LPT_RPT(after_IP);
            long LPT = parts[0];
            long RPT = parts[1];

            SecureRandom sr = new SecureRandom();
            long key_64 = sr.nextLong();
            long[] transformed_keys_48 = generate_sub_keys(key);
            long expanded_text_48 = permute(RPT, DesConstants.expansion_permutation, 32)
        }
    }
}