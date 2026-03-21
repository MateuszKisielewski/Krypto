package pl.desx.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import pl.desx.cryptography.BlockCutter;
import pl.desx.cryptography.DesAlgorithm;
import pl.desx.cryptography.DesxAlgorithm;
import pl.desx.files.FileManager;

import java.io.File;
import java.io.IOException;
import java.util.function.UnaryOperator;

public class AppWindow {
    BlockCutter blockCutter = new BlockCutter();
    DesxAlgorithm desxAlgorithm = new DesxAlgorithm();
    FileManager fileManager = new FileManager();
    String input_file_path = null;
    byte[] last_result_bytes;

    @FXML
    private ToggleGroup wprowadzanie;

    @FXML
    private RadioButton in_reczne;

    @FXML
    private RadioButton in_plik;

    @FXML
    private ToggleGroup choice;

    @FXML
    private RadioButton deszyfrujButton;

    @FXML
    private Button generujKlucze;

    @FXML
    private TextField kluczDrugi;

    @FXML
    private TextField kluczPierwszy;

    @FXML
    private TextField kluczTrzeci;

    @FXML
    private Button start;

    @FXML
    private RadioButton szyfrujButton;

    @FXML
    private TextArea tekstJawny;

    @FXML
    private TextArea tekstZaszyfrowany;

    @FXML
    private Button wczytajKlucze;

    @FXML
    private Button wczytajPlik;

    @FXML
    private Button zapiszKlucze;

    @FXML
    private Button zapiszPlik;

    @FXML
    public void initialize(){
        UnaryOperator<TextFormatter.Change> input_filter = change -> {
            String new_text = change.getControlNewText();
            if (new_text.matches("[0-9a-fA-F]{0,16}"))
                return change;
            else
                return null;
        };
        kluczPierwszy.setTextFormatter(new TextFormatter<>(input_filter));
        kluczDrugi.setTextFormatter(new TextFormatter<>(input_filter));
        kluczTrzeci.setTextFormatter(new TextFormatter<>(input_filter));
    }

    @FXML
    void onGenerujKlucze(ActionEvent event) {
        desxAlgorithm.generate_keys();
        kluczPierwszy.setText(Long.toHexString(desxAlgorithm.get_key_1()));
        kluczDrugi.setText(Long.toHexString(desxAlgorithm.get_key_2()));
        kluczTrzeci.setText(Long.toHexString(desxAlgorithm.get_key_3()));
    }

    @FXML
    void onStart(ActionEvent event) throws IOException {
        byte[] data = fileManager.read_file(input_file_path);
        long[] blocks = blockCutter.to_blocks(data);
        long[] result = new long[blocks.length];

        if (szyfrujButton.isSelected()) {
            for (int i = 0; i < blocks.length; i++) {
                result[i] = desxAlgorithm.main_desx_block_encrypt(blocks[i]);
                last_result_bytes = blockCutter.from_blocks(result);
            }
        }
        else if (deszyfrujButton.isSelected()) {
            for (int i = 0; i < blocks.length; i++) {
                result[i] = desxAlgorithm.main_desx_block_decrypt(blocks[i]);
                last_result_bytes = blockCutter.from_blocks(result);
            }
        }
        else {
            show_alert("Nie wybrano opcji");
            return;
        }
        tekstZaszyfrowany.setText(new String(last_result_bytes));
    }

    @FXML
    void onWczytajKlucze(ActionEvent event) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Otwieranie pliku");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Pliki kluczy", "*.key"));
        File file = fileChooser.showOpenDialog(get_window(event));

        if (file != null) {
            long[] key = fileManager.load_key(file.getAbsolutePath());
            desxAlgorithm.set_keys(key[0], key[1], key[2]);
            kluczPierwszy.setText(Long.toBinaryString(key[0]));
            kluczDrugi.setText(Long.toBinaryString(key[1]));
            kluczTrzeci.setText(Long.toBinaryString(key[2]));
        }
    }

    @FXML
    void onZapiszKlucze(ActionEvent event) throws IOException {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Zapisz plik klucza");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Pliki klucza", "*.key"));
        if (input_file_path != null) {
            chooser.setInitialFileName(new File(input_file_path).getName() + ".key");
        }
        File file = chooser.showSaveDialog(get_window(event));

        if (file != null) {
            fileManager.save_key(file.getAbsolutePath(),
                        desxAlgorithm.get_key_1(),
                        desxAlgorithm.get_key_2(),
                        desxAlgorithm.get_key_3());
        }
    }

    @FXML
    void onWczytajPlik(ActionEvent event) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wybierz plik");
        File file = fileChooser.showOpenDialog(get_window(event));

        if (file != null) {
            input_file_path = file.getAbsolutePath();
            byte[] data = fileManager.read_file(input_file_path);
            tekstJawny.setText(new String(data));
        }
    }

    @FXML
    void onZapiszPlik(ActionEvent event) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Zapisz plik");
        if (input_file_path != null) {
            fileChooser.setInitialFileName(new File(input_file_path).getName() + ".enc");
        }
        File file = fileChooser.showSaveDialog(get_window(event));

        if (file != null) {
            fileManager.write_file(file.getAbsolutePath(), last_result_bytes);
        }
    }

    private Window get_window(ActionEvent event) {
        return ((javafx.scene.Node) event.getSource()).getScene().getWindow();
    }

    private void show_alert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
