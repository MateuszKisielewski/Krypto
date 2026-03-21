package pl.desx.cryptography;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class BlockCutter {

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

    public long[] bytes_to_blocks_without_padding(byte[] data) {
        long[] blocks = new long[data.length / 8];
        for (int i = 0; i < blocks.length; i++) {
            blocks[i] = ByteBuffer.wrap(data, i * 8, 8).getLong();
        }
        return blocks;
    }

    public byte[] blocks_to_bytes(long[] blocks) {
        ByteBuffer buffer = ByteBuffer.allocate(blocks.length * 8);
        for (long block : blocks) {
            buffer.putLong(block);
        }
        byte[] data = buffer.array();
        int padding = data[data.length - 1];
        return Arrays.copyOf(data, data.length - padding);
    }

    public byte[] to_bytes(long[] blocks) {
        ByteBuffer buffer = ByteBuffer.allocate(blocks.length * 8);
        for (long block : blocks) {
            buffer.putLong(block);
        }
        return buffer.array();
    }
}
