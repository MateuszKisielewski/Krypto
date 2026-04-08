//Autorzy:
//Mateusz Kisielewski 254779
//Krzysztof Kata 254776

package pl.rsa.files;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Klasa pomocnicza do zarządzania plikami
 */
public class FileManager {

    /**
     * Wczytuje całą zawartość pliku w postaci surowych bajtów
     * @param file Plik który użytkownik wybrał do odczytu
     * @return Tablica bajtów reprezentująca dokładną zawartość pliku
     * @throws IOException Rzuca wyjątek IO jeśli wystąpi błąd
     */
    public static byte[] readFile(File file) throws IOException {
        return Files.readAllBytes(file.toPath());
    }

    /**
     * Zapisuje podany ciąg znaków do pliku
     * @param file Plik docelowy w którym chcemy zapisać dane
     * @param content String który ma zostać zapisany w pliku
     * @throws IOException Rzuca wyjątek w razie problemów z zapisem
     */
    public static void saveTextToFile(File file, String content) throws IOException {
        Files.writeString(file.toPath(), content);
    }

    /**
     * Odczytuje zawartość pliku tekstowego i zwraca ją jako String
     * @param file Plik tekstowy który chcemy przeczytać
     * @return Cały tekst zawarty w pliku
     * @throws IOException Rzuca wyjątek w razie problemów z odczytem
     */
    public static String readTextFromFile(File file) throws IOException {
        return Files.readString(file.toPath());
    }
}