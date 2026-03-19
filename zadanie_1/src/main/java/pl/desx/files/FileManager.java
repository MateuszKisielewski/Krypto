package pl.desx.files;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileManager {

    public byte[] read_file(String path) throws IOException {
        return Files.readAllBytes(Paths.get(path));
    }

    public void write_file(String path, byte[] data) throws IOException {
        Files.write(Paths.get(path), data);
    }

    public void save_key(String path, long k1, long k2, long k3) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(24);
        buffer.putLong(k1);
        buffer.putLong(k2);
        buffer.putLong(k3);
        Files.write(Paths.get(path), buffer.array());
    }

    public long[] load_key(String path) throws IOException {
        byte[] data = Files.readAllBytes(Paths.get(path));

        if (data.length != 24) {
            throw new IOException("Nieprawidłowy plik klucza! Oczekiwano 24 bajtów, otrzymano: " + data.length);
        }

        ByteBuffer buffer = ByteBuffer.wrap(data);
        return new long[]{
                buffer.getLong(),
                buffer.getLong(),
                buffer.getLong()
        };
    }

    public boolean file_exists(String path) {
        return Files.exists(Paths.get(path));
    }

    public String build_output_path(String input_path, boolean encrypting) {
        return input_path + (encrypting ? ".enc" : ".dec");
    }

    public String build_key_path(String input_path) {
        Path p = Paths.get(input_path).toAbsolutePath();
        String name = p.getFileName().toString();
        int dot = name.lastIndexOf('.');
        String base_name = (dot != -1) ? name.substring(0, dot) : name;
        return p.getParent().resolve(base_name + ".key").toString();
    }
}
