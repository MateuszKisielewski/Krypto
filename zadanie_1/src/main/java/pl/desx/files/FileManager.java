/**
 * Autorzy: Krzysztof Kata (254776) i Mateusz Kisielewski (254779)
 */
package pl.desx.files;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileManager {

    /**
     * Wczytuje całą zawartość pliku
     */
    public byte[] read_file(String path) throws IOException {
        return Files.readAllBytes(Paths.get(path));
    }

    /**
     * Zapisuje przetworzoną (zaszyfrowaną lub zdeszyfrowaną) tablicę bajtów z powrotem do pliku
     */
    public void write_file(String path, byte[] data) throws IOException {
        Files.write(Paths.get(path), data);
    }

    /**
     * Zapisuje zestaw trzech kluczy DESX do pliku
     * Wykorzystuje ByteBuffer do zamiany trzech 64-bitowych liczb (typ long) na  24-bajtową tablicę
     */
    public void save_key(String path, long k1, long k2, long k3) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(24);
        buffer.putLong(k1);
        buffer.putLong(k2);
        buffer.putLong(k3);
        Files.write(Paths.get(path), buffer.array());
    }

    /**
     * Wczytuje zestaw trzech kluczy DESX z pliku
     * Sprawdza czy ma dokładnie 24 bajty
     */
    public long[] load_key(String path) throws IOException {
        byte[] data = Files.readAllBytes(Paths.get(path));

        if (data.length != 24) {
            throw new IOException("Zły plik. Klucz powinien mieć dokładnie 24 bajty, a miał: " + data.length);
        }

        ByteBuffer buffer = ByteBuffer.wrap(data);
        return new long[]{
                buffer.getLong(),
                buffer.getLong(),
                buffer.getLong()
        };
    }

    /**
     * Sprawdza czy plik pod podaną ścieżką fizycznie istnieje na dysku; walidacja
     */
    public boolean file_exists(String path) {
        return Files.exists(Paths.get(path));
    }

    /**
     * Konwertuje wprowadzony ręcznie tekst na tablicę bajtów
     * Wymusza kodowanie UTF-8
     */
    public byte[] string_to_bytes(String str) {
        return str.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Konwertuje bajty z algorytmu z powrotem na czytelny tekst
     * Wymusza kodowanie UTF-8
     */
    public String bytes_to_string(byte[] bytes) {
        return new String(bytes, StandardCharsets.UTF_8);
    }
}