package main.java.pl.desx.cryptography;

import java.security.SecureRandom;

public class DesAlgorithm {

    public long permute(long input_plain_text, byte[] table_bytes, int important_bytes){
        long output_permutate_text = 0;
        for (int i=0; i<table_bytes.length; i++){
            output_permutate_text <<= 1;
            int bit_position = table_bytes[i];
            long extracted_bit = (input_plain_text >>> (important_bytes - bit_position)) & 1L;
            output_permutate_text |= extracted_bit;
        }
        return output_permutate_text;
    }

    public long[] split_LPT_RPT(long output_IP) {
        long[] LPT_RPT = new long[2];
        LPT_RPT[0] = output_IP >>> 32; //Wywala te 32 bity z prawej
        LPT_RPT[1] = (output_IP << 32) >>> 32; //Wywala te 32 bity z lewej i przesuwa spowrotem na prawo, lewa strona to same zera
        return LPT_RPT; //zwracamy tablicę w której mamy lewą i prawą część osobno
    }

    public long xor(long left, long right){
        return left ^ right;
    }

    public long[] generate_sub_keys(long key_64) {
        long[] sub_keys = new long[16];

        long key_56 = permute(key_64, DesConstants.permuted_choice_1, 64);

        long left_key_28;
        long right_key_28;
        int shift = 0;
        long shifted_key_56 = key_56;

        for (int i = 1; i<=16; i++){
            left_key_28 = shifted_key_56 >>> 28;
            right_key_28 = (shifted_key_56 << 36) >>> 36;
            shift = 0;

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

            shifted_key_56 = left_key_28;
            shifted_key_56 <<= 28;
            shifted_key_56 |= right_key_28;

            long key_48 = permute(shifted_key_56, DesConstants.permuted_choice_2, 56);

            sub_keys[i-1] = key_48;
        }

        return sub_keys;
    }

    public long s_boxed_values(long xored_text) {
        long sboxed_value = 0;
        for (int j=0; j<8; j++){
            long first_byte = xored_text >>> (48 - (j * 6) - 1) & 1L;
            long sixth_byte = xored_text >> (48 - (j * 6) - 6) & 1L;
            long four_middle_bytes = xored_text >>> (48 - (j * 6) - 5) & 15L;

            long row_bytes_sbox = (first_byte << 1) | sixth_byte ;
            long column_bytes_sbox = four_middle_bytes;

            long value = DesConstants.s_box[j][(int)column_bytes_sbox][(int)row_bytes_sbox];

            sboxed_value <<= 4;
            sboxed_value |= value;

        }
        return sboxed_value;
    }

    public long main_algorythm(long plain_text) {
        SecureRandom sr = new SecureRandom();
        long key_64 = sr.nextLong();

        long ciphered_text = 0;

        long after_IP = permute(plain_text, DesConstants.initial_permutation, 64);
        for (int i=0; i<16; i++){
            long[] parts = split_LPT_RPT(after_IP);
            long LPT = parts[0];
            long RPT = parts[1];

            long[] transformed_keys_48 = generate_sub_keys(key_64);
            long expanded_right_text_48 = permute(RPT, DesConstants.expansion_permutation, 32);

            long text_after_xor = xor(expanded_right_text_48, transformed_keys_48[i]);

            long sboxed_text = s_boxed_values(text_after_xor);

            long pboxed_text = permute(sboxed_text, DesConstants.p_box_permutation, 32);
        }
        return ciphered_text;
    }
}