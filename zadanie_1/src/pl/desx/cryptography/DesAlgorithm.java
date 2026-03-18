package pl.desx.cryptography;

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
        return LPT_RPT; //pomyśleć jak zwrócić bo java niby nie obsługuje dwóch returnow
    }

    public long XOR(long LPT, long fourSteppedRPT){
        return LPT ^ fourSteppedRPT; //po prostu xor z LPT i z tego RPT po tych 4 krokach
    }
}