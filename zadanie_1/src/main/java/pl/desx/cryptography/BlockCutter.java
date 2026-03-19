package pl.desx.cryptography;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class BlockCutter {

    public long[] to_blocks(byte[] data) {
        int padding = 8 - (data.length % 8);
        byte[] padded = Arrays.copyOf(data, data.length + padding);
        Arrays.fill(padded, data.length, padded.length, (byte) padding);

        long[] blocks = new long[padded.length / 8];
        for (int i = 0; i < blocks.length; i++) {
            blocks[i] = ByteBuffer.wrap(padded, i * 8, 8).getLong();
        }
        return blocks;
    }

    public byte[] from_blocks(long[] blocks) {
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
        for (long block : blocks) buffer.putLong(block);
        return buffer.array();
    }
}
