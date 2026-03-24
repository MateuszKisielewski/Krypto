/**
 * Autorzy:
 * Mateusz Kisielewski
 * Krzysztof Kata
 */
package pl.desx.cryptography;

public class DesAlgorithm {

    /**
     * Uniwersalna metoda wykonująca permutacje
     *
     * @param input_base_text - wartość której bitami operujemy
     * @param table_bytes     - tablica mówiąca w jaki sposób przestawić bity którymi chcemy operować
     * @param important_bytes - ilość znaczących bitów, inaczej: numer największego bitu (np. w 32-bitowej tablicy będzie to numer 32)
     * @return - zwracany jest wynik permutacji jako long
     */
    public long permute(long input_base_text, byte[] table_bytes, int important_bytes) {
        long output_permutate_text = 0;
        for (int i = 0; i < table_bytes.length; i++) {
            output_permutate_text <<= 1;
            int bit_position = table_bytes[i];
            long extracted_bit = (input_base_text >>> (important_bytes - bit_position)) & 1L;
            output_permutate_text |= extracted_bit;
        }
        return output_permutate_text;
    }

    /**
     * Metoda rozdzielająca 64-bitowy blok na dwa bloki po 32-bity każdy.
     *
     * @param output_IP - przekazujemy wynik Initial Permutation na którym chcemy przeprowadzić rozdzielenie
     * @return zwracamy tablicę dwóch elementów, gdzie na indeksie 0 jest LPT a na indeksie 1 jest RPT
     */
    public long[] split_LPT_RPT(long output_IP) {
        long[] LPT_RPT = new long[2];
        LPT_RPT[0] = output_IP >>> 32;
        LPT_RPT[1] = (output_IP << 32) >>> 32;
        return LPT_RPT;
    }

    /**
     * Metoda wykonująca operację XOR na przekazywanych wartościach
     *
     * @param left  - pierwsza wartość podawana do operacji XOR
     * @param right - druga wartość podawana do operacji XOR
     * @return - zwracana jest wartość po wykonaniu operacji XOR
     */
    public long xor(long left, long right) {
        return left ^ right;
    }

    /**
     * Metoda generująca podklucze używane w 16 rundach szyfrowania w sieci Feistela
     * @param key_64 - przekazujemy klucz na podstawie którego będą wykonywane operacje w metodzie
     * @return - przekazywana jest tablica 16 elementów w której pod każdym indeksem jest podklucz dla danej iteracji szyfrowania
     */
    public long[] generate_sub_keys(long key_64) {
        long[] sub_keys = new long[16];

        long key_56 = permute(key_64, DesConstants.permuted_choice_1, 64);

        long left_key_28;
        long right_key_28;
        int shift = 0;
        long shifted_key_56 = key_56;

        for (int i = 1; i <= 16; i++) {
            left_key_28 = shifted_key_56 >>> 28;
            right_key_28 = (shifted_key_56 << 36) >>> 36;
            shift = 0;

            if (i == 1 || i == 2 || i == 9 || i == 16) {
                shift = 1;
            } else {
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

            sub_keys[i - 1] = key_48;
        }

        return sub_keys;
    }

    /**
     * Metoda rozszyfrowująca 6-bitowe bloki na 4-bitowe bloki z odpowiednimi wartościami
     * @param xored_text - jako parametr podajemy wynik operacji XOR z odpowiadającej indeksem rundy
     * @return - zwracany jest ciąg złączonych 4-bitowych bloków
     */
    public long s_box(long xored_text) {
        long sboxed_value = 0;
        for (int j = 0; j < 8; j++) {
            long first_byte = xored_text >>> (48 - (j * 6) - 1) & 1L;
            long sixth_byte = xored_text >>> (48 - (j * 6) - 6) & 1L;
            long four_middle_bytes = xored_text >>> (48 - (j * 6) - 5) & 15L;

            long row_bytes_sbox = (first_byte << 1) | sixth_byte;
            long column_bytes_sbox = four_middle_bytes;

            long value = DesConstants.s_box[j][(int) row_bytes_sbox][(int) column_bytes_sbox];

            sboxed_value <<= 4;
            sboxed_value |= value;

        }
        return sboxed_value;
    }

    /**
     * Główna funkcja szyfrująca dla algorytmu DES
     * @param plain_text - tekst jawny przekazywany do zaszyfrowania
     * @param key_64 - klucz 64-bitowy wygenerowany lub wczytany, używany do zaszyfrowania pliku
     * @return - zwracany jest zaszyfrowany blok
     */
    public long main_des_block_encrypt(long plain_text, long key_64) {
        long ciphered_text = 0;

        long after_IP = permute(plain_text, DesConstants.initial_permutation, 64);
        long[] parts = split_LPT_RPT(after_IP);
        long LPT = parts[0];
        long RPT = parts[1];

        long[] transformed_keys_48 = generate_sub_keys(key_64);

        for (int i = 0; i < 16; i++) {
            long expanded_right_text_48 = permute(RPT, DesConstants.expansion_permutation, 32);

            long text_after_xor = xor(expanded_right_text_48, transformed_keys_48[i]);

            long sboxed_text = s_box(text_after_xor);

            long pboxed_text = permute(sboxed_text, DesConstants.p_box_permutation, 32);

            long xored_text = xor(LPT, pboxed_text);

            long temp = RPT;
            RPT = xored_text;
            LPT = temp;
        }

        long swapped_output = (RPT << 32) | LPT;
        ciphered_text = permute(swapped_output, DesConstants.final_permutation, 64);

        return ciphered_text;
    }

    /**
     * Główna funkcja deszyfrująca dla algorytmu DES
     * @param ciphered_text - tekst zaszyfrowany przekazywany do odszyfrowania
     * @param key_64 - klucz 64-bitowy, używany do odszyfrowania pliku
     * @return - zwracany jest odszyfrowany blok
     */
    public long main_des_block_decrypt(long ciphered_text, long key_64) {
        long plain_text = 0;

        long after_IP = permute(ciphered_text, DesConstants.initial_permutation, 64);
        long[] parts = split_LPT_RPT(after_IP);
        long LPT = parts[0];
        long RPT = parts[1];

        long[] transformed_keys_48 = generate_sub_keys(key_64);

        for (int i = 0; i < 16; i++) {
            long expanded_right_text_48 = permute(RPT, DesConstants.expansion_permutation, 32);

            long text_after_xor = xor(expanded_right_text_48, transformed_keys_48[15 - i]);

            long sboxed_text = s_box(text_after_xor);

            long pboxed_text = permute(sboxed_text, DesConstants.p_box_permutation, 32);

            long xored_text = xor(LPT, pboxed_text);

            long temp = RPT;
            RPT = xored_text;
            LPT = temp;
        }

        long swapped_output = (RPT << 32) | LPT;
        plain_text = permute(swapped_output, DesConstants.final_permutation, 64);

        return plain_text;
    }
}