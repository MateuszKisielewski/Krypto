package pl.desx.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import pl.desx.cryptography.BlockCutter;
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
    byte[] proceeded_bytes;

    @FXML
    private ToggleGroup wprowadzanie;

    @FXML
    private RadioButton in_reczne;

    @FXML
    private RadioButton in_plik;

    @FXML
    private RadioButton deszyfrujButton;

    @FXML
    private TextField kluczDrugi;

    @FXML
    private TextField kluczPierwszy;

    @FXML
    private TextField kluczTrzeci;

    @FXML
    private RadioButton szyfrujButton;

    @FXML
    private TextArea tekst_do_przetworzenia;

    @FXML
    private TextArea pole_z_wynikiem_algorytmu;

    @FXML
    private Button wczytajPlik;

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

        tekst_do_przetworzenia.editableProperty().bind(in_reczne.selectedProperty());
        wczytajPlik.disableProperty().bind(in_reczne.selectedProperty());
    }

    private String format_hex_key(long key) {
        String hex = Long.toHexString(key).toUpperCase();
        while (hex.length() < 16) {
            hex = "0" + hex;
        }
        return hex;
    }

    @FXML
    void onGenerujKlucze(ActionEvent event) {
        desxAlgorithm.generate_keys();
        kluczPierwszy.setText(format_hex_key(desxAlgorithm.get_key_1()));
        kluczDrugi.setText(format_hex_key(desxAlgorithm.get_key_2()));
        kluczTrzeci.setText(format_hex_key(desxAlgorithm.get_key_3()));
    }

    @FXML
    void onStart(ActionEvent event) throws IOException {
        byte[] data;

        if (wprowadzanie.getSelectedToggle() == null) {
            show_alert("Nie wybrano żadnej z opcji wyboru tekstu do szyfrowania");
            return;
        }

        String wybor_wprowadzania = ((RadioButton) wprowadzanie.getSelectedToggle()).getId();

        switch (wybor_wprowadzania) {
            case "in_reczne":
                String wpisanyTekst = tekst_do_przetworzenia.getText();
                if (wpisanyTekst == null || wpisanyTekst.isEmpty()) {
                    show_alert("Wpisz tekst do zaszyfrowania!");
                    return;
                }
                data = fileManager.string_to_bytes(wpisanyTekst);
                break;

            case "in_plik":
                if (input_file_path == null || !fileManager.file_exists(input_file_path)) {
                    show_alert("Nie wybrano pliku");
                    return;
                }
                data = fileManager.read_file(input_file_path);
                break;

            default:
                show_alert("Dokonaj wyboru typu wprowadzania danych");
                return;
        }


        String k1 = kluczPierwszy.getText();
        String k2 = kluczDrugi.getText();
        String k3 = kluczTrzeci.getText();

        if (k1 == null || k1.length() != 16 ||
                k2 == null || k2.length() != 16 ||
                k3 == null || k3.length() != 16) {

            show_alert("Nie wprowadzono poprawnie kluczy");
            return;
        }

        long key1_long = Long.parseUnsignedLong(k1, 16);
        long key2_long = Long.parseUnsignedLong(k2, 16);
        long key3_long = Long.parseUnsignedLong(k3, 16);
        desxAlgorithm.set_keys(key1_long, key2_long, key3_long);

        if (szyfrujButton.isSelected()) {
            long[] blocks = blockCutter.bytes_to_blocks_with_padding(data);
            long[] result = new long[blocks.length];
            for (int i = 0; i < blocks.length; i++) {
                result[i] = desxAlgorithm.main_desx_block_encrypt(blocks[i]);
            }
            proceeded_bytes = blockCutter.to_bytes(result);
        }
        else if (deszyfrujButton.isSelected()) {
            long[] blocks = blockCutter.bytes_to_blocks_without_padding(data);
            long[] result = new long[blocks.length];
            for (int i = 0; i < blocks.length; i++) {
                result[i] = desxAlgorithm.main_desx_block_decrypt(blocks[i]);
            }
            proceeded_bytes = blockCutter.blocks_to_bytes(result);
        }
        else {
            show_alert("Nie wybrano opcji Szyfruj / Deszyfruj");
            return;
        }

        String operacja;
        if (in_plik.isSelected()) {
            if(szyfrujButton.isSelected())
                operacja = "zaszyfrowany";
            else {
                operacja = "zdeszyfrowany";
            }
            pole_z_wynikiem_algorytmu.setText("Plik został " + operacja + ". Możesz zapisać plik");
        } else {
            pole_z_wynikiem_algorytmu.setText(fileManager.bytes_to_string(proceeded_bytes));
        }
    }

    @FXML
    void onWczytajKlucze(ActionEvent event) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wybierz klucz");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Pliki kluczy", "*.key"));
        File file = fileChooser.showOpenDialog(get_window(event));

        if (file != null) {
            long[] key = fileManager.load_key(file.getAbsolutePath());
            desxAlgorithm.set_keys(key[0], key[1], key[2]);
            kluczPierwszy.setText(format_hex_key(key[0]));
            kluczDrugi.setText(format_hex_key(key[1]));
            kluczTrzeci.setText(format_hex_key(key[2]));
        }
    }

    @FXML
    void onZapiszKlucze(ActionEvent event) throws IOException {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Zapisz plik klucza");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Pliki klucza", "*.key"));
        chooser.setInitialFileName("klucz.key");

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
            tekst_do_przetworzenia.setText("Wybrano plik:\n" + input_file_path);
        }
    }

    @FXML
    void onZapiszPlik(ActionEvent event) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Zapisz plik");

        if (input_file_path != null) {
            String original_name = new File(input_file_path).getName();

            if (szyfrujButton.isSelected()) {
                fileChooser.setInitialFileName(original_name + ".enc");
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Pliki zaszyfrowane (.enc)", "*.enc"));

            } else if (deszyfrujButton.isSelected()) {
                if (original_name.endsWith(".enc")) {
                    String nazwa_bez_enc = original_name.substring(0, original_name.length() - 4);
                    if (nazwa_bez_enc.equals("zaszyfrowana"))
                        fileChooser.setInitialFileName("odszyfrowana");
                    else
                        fileChooser.setInitialFileName(nazwa_bez_enc);
                }
            }
        } else {
            if (szyfrujButton.isSelected()) {
                fileChooser.setInitialFileName("zaszyfrowana.enc");
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Pliki zaszyfrowane(enc)", "*.enc"));
            } else {
                fileChooser.setInitialFileName("odszyfrowana.txt");
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Pliki odszyfrowane(txt)", "*.txt"));
            }
        }

        File file = fileChooser.showSaveDialog(get_window(event));

        if (file != null) {
            fileManager.write_file(file.getAbsolutePath(), proceeded_bytes);
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
