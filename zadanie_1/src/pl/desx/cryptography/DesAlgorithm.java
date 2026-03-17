package pl.desx.cryptography;

public class DesAlgorithm {

    public long permute(long input_plain_block, byte[] table_bytes, int important_bytes){
        long output_plain_block = 0;
        for (int i=0; i<table_bytes.length; i++){
            output_plain_block <<= 1;
            int bit_position = table_bytes[i];
            long extracted_bit = (input_plain_block >>> (important_bytes - bit_position)) & 1L;
            output_plain_block |= extracted_bit;
        }
        return output_plain_block;
    }
}
