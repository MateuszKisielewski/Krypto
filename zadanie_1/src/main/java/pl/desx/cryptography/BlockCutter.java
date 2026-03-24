/**
 * Autorzy:
 * Mateusz Kisielewski
 * Krzysztof Kata
 */
package pl.desx.cryptography;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class BlockCutter {

    /**
     * Tablica bajtów jest konwertowana na tablicę 64-bitowych bloków (typu long) z paddingiem, czyli uzupełnieniem bloków do 8 bajtów
     * uzupełnianie odbywa się zawsze od 1 do 8 bajtów
     *
     * @param data - przekazujemy tablicę bajtów którą chcemy zamienić na bloki
     * @return - zwracamy tablicę bloków (typu long)
     */
    public long[] bytes_to_blocks_with_padding(byte[] data) {
        int padding = 8 - (data.length % 8);
        byte[] padded = Arrays.copyOf(data, data.length + padding);
        Arrays.fill(padded, data.length, padded.length, (byte) padding);

        long[] blocks = new long[padded.length / 8];
        for (int i = 0; i < blocks.length; i++) {
            blocks[i] = ByteBuffer.wrap(padded, i * 8, 8).getLong();
        }
        return blocks;
    }

    /**
     * Tablica bajtów jest konwertowana na 64-bitowe tablicę bloków typu long
     *
     * @param data - przekazujemy tablicę bajtów które chcemy zamienić na bloki
     * @return - zwracamy tablicę z blokami (typu long)
     */
    public long[] bytes_to_blocks_without_padding(byte[] data) {
        long[] blocks = new long[data.length / 8];
        for (int i = 0; i < blocks.length; i++) {
            blocks[i] = ByteBuffer.wrap(data, i * 8, 8).getLong();
        }
        return blocks;
    }

    /**
     * Konwertuje tablicę 64-bitowych bloków na tablicę bajtów usuwając przy tym padding, robi to przy pomocy wartości ostatniego bajtu
     * Jeśli padding nie mieści się pomiędzy 1 a 8 to zwracamy bez usuwania paddingu
     *
     * @param blocks - tablica bloków typu long które chcemy przekonwertować na bajty
     * @return - zwracamy tablice bajtów z usuniętym paddingiem
     */
    public byte[] blocks_to_bytes_with_padding(long[] blocks) {
        ByteBuffer buffer = ByteBuffer.allocate(blocks.length * 8);
        for (long block : blocks) {
            buffer.putLong(block);
        }
        byte[] data = buffer.array();
        int padding = data[data.length - 1];

        if (padding < 1 || padding > 8) {
            return data;
        }

        return Arrays.copyOf(data, data.length - padding);
    }

    /**
     * Konwertuje tablice 64-bitowych bloków na tablicę bajtów, bez usuwania paddingów
     *
     * @param blocks - tablica bloków typu long które chcemy przekonwertować na bajty
     * @return - zwracamy tablicę bajtów bez usuwania paddingów
     */
    public byte[] blocks_to_bytes_without_padding(long[] blocks) {
        ByteBuffer buffer = ByteBuffer.allocate(blocks.length * 8);
        for (long block : blocks) {
            buffer.putLong(block);
        }
        return buffer.array();
    }
}
