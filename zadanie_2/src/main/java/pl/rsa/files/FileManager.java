package pl.rsa.files;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileManager {

    public static byte[] readFile(File file) throws IOException {
        return Files.readAllBytes(file.toPath());
    }

    public static void saveTextToFile(File file, String content) throws IOException {
        Files.writeString(file.toPath(), content);
    }

    public static String readTextFromFile(File file) throws IOException {
        return Files.readString(file.toPath());
    }
}