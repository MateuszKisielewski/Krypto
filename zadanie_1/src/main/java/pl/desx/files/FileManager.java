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
     * Wczytuje całą zawartość pliku.
     * @param path Ścieżka do pliku, który ma zostać wczytany
     * @return Tablica bajtów zawierająca całą zawartość pliku
     * @throws IOException Jeśli wystąpi problem z odczytem pliku
     */
    public byte[] read_file(String path) throws IOException {
        return Files.readAllBytes(Paths.get(path));
    }

    /**
     * Zapisuje przetworzoną (zaszyfrowaną lub zdeszyfrowaną) tablicę bajtów z powrotem do pliku.
     * @param path Ścieżka docelowa pod którą plik ma zostać zapisany
     * @param data Tablica przetworzonych bajtów do zapisania
     * @throws IOException Jeśli wystąpi problem z zapisem pliku
     */
    public void write_file(String path, byte[] data) throws IOException {
        Files.write(Paths.get(path), data);
    }

    /**
     * Zapisuje zestaw trzech kluczy DESX do pliku.
     * Wykorzystuje ByteBuffer do zamiany trzech 64-bitowych liczb (typ long) na 24-bajtową tablicę.
     * * @param path Ścieżka docelowa pod którą zostanie zapisany plik .key
     * @param k1 Pierwszy 64-bitowy klucz
     * @param k2 Drugi 64-bitowy klucz
     * @param k3 Trzeci 64-bitowy klucz
     * @throws IOException Jeśli wystąpi błąd podczas zapisu kluczy
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
     * Sprawdza, czy plik ma dokładnie 24 bajty (wymiar trzech kluczy 64-bitowych)
     * * @param path Ścieżka do pliku z zapisanymi kluczami
     * @return Tablica trzech kluczy typu long.
     * @throws IOException Jeśli plik ma zły rozmiar lub wystąpi problem z jego odczytem
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
     * Sprawdza, czy plik pod podaną ścieżką fizycznie istnieje na dysku; walidacja
     * * @param path Ścieżka do sprawdzanego pliku
     * @return true, jeśli plik istnieje w przeciwnym razie false
     */
    public boolean file_exists(String path) {
        return Files.exists(Paths.get(path));
    }

    /**
     * Konwertuje wprowadzony ręcznie tekst na tablicę bajtów
     * Wymusza kodowanie UTF-8
     * @param str Tekst jawny wprowadzony przez użytkownika
     * @return Tablica bajtów zakodowana w standardzie UTF-8
     */
    public byte[] string_to_bytes(String str) {
        return str.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Konwertuje bajty z algorytmu z powrotem na czytelny tekst
     * Wymusza kodowanie UTF-8
     * * @param bytes Tablica zdeszyfrowanych bajtów
     * @return Ciąg znaków w UTF-8
     */
    public String bytes_to_string(byte[] bytes) {
        return new String(bytes, StandardCharsets.UTF_8);
    }
}