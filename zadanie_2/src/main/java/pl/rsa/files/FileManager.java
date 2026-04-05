package pl.rsa.files;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileManager {

    // Wczytuje cały plik jako surowe bajty
    public static byte[] readFile(File file) throws IOException {
        return Files.readAllBytes(file.toPath());
    }

    // Zapisuje tekst (np. wygenerowany podpis) do pliku tekstowego
    public static void saveTextToFile(File file, String content) throws IOException {
        Files.writeString(file.toPath(), content);
    }

    // Odczytuje tekst z pliku (przydatne przy wczytywaniu podpisu do weryfikacji)
    public static String readTextFromFile(File file) throws IOException {
        return Files.readString(file.toPath());
    }
}