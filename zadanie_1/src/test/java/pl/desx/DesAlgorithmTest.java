package pl.desx;

import org.junit.jupiter.api.Test;
import pl.desx.cryptography.DesAlgorithm;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DesAlgorithmTest {

    DesAlgorithm des = new DesAlgorithm();

    @Test
    public void testXor() {
        long left = 0b10101010L;
        long right = 0b01010101L;
        long expected = 0b11111111L;

        long result = des.xor(left, right);

        assertEquals(expected, result, "XOR nie działa prawidłowo!");
    }

    @Test
    public void testSplitLptRpt() {
        long input = 0x123456789ABCDEF0L;
        long expectedLPT = 0x12345678L;
        long expectedRPT = 0x9ABCDEF0L;

        long[] result = des.split_LPT_RPT(input);

        assertEquals(expectedLPT, result[0], "Lewa strona (LPT) źle wycięta!");
        assertEquals(expectedRPT, result[1], "Prawa strona (RPT) źle wycięta!");
    }

    @Test
    public void testMainAlgorithmWithStandardVector() {
        long plainText = 0x0123456789ABCDEFL;
        long key = 0x133457799BBCDFF1L;
        long expectedCipherText = 0x85E813540F0AB405L;

        long actualCipherText = des.main_encryption_algorithm(plainText, key);

        assertEquals(expectedCipherText, actualCipherText,
                String.format("Błąd DES! Oczekiwano: %X, Otrzymano: %X", expectedCipherText, actualCipherText));
    }
}