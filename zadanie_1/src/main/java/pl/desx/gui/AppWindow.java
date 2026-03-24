/**
 * Autorzy: Krzysztof Kata (254776) i Mateusz Kisielewski (254779)
 */
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

    /** Obiekt odpowiedzialny za cięcie danych na 64-bitowe bloki i obsługę paddingu */
    BlockCutter blockCutter = new BlockCutter();

    /** Główny algorytm DESX */
    DesxAlgorithm desxAlgorithm = new DesxAlgorithm();

    /** Menedżer operacji wejścia/wyjścia dla plików */
    FileManager fileManager = new FileManager();

    /** Ścieżka do wczytanego pliku wejściowego */
    String input_file_path = null;

    /** Bufor przechowujący wynik działania algorytmu */
    byte[] proceeded_bytes;

    @FXML private ToggleGroup wprowadzanie;
    @FXML private RadioButton in_reczne;
    @FXML private RadioButton in_plik;
    @FXML private RadioButton deszyfrujButton;
    @FXML private RadioButton szyfrujButton;

    @FXML private TextField kluczPierwszy;
    @FXML private TextField kluczDrugi;
    @FXML private TextField kluczTrzeci;

    @FXML private TextArea tekst_do_przetworzenia;
    @FXML private TextArea pole_z_wynikiem_algorytmu;
    @FXML private Button wczytajPlik;

    /**
     * Metoda wywoływana automatycznie podczas startu interfejsu JavaFX
     * Inicjalizuje filtry które pozwalają na wpisanie wyłącznie do 16 znaków szesnastkowych dla kluczy
     * Dodatkowo blokuje dane elementy w zależności od wyboru yżytkownika
     */
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

    /**
     * Funkcja pomocnicza zapewniająca poprawne formatowanie kluczy
     * Zapobiega ucinaniu wiodących zer przez system operacyjny
     */
    private String format_hex_key(long key) {
        String hex = Long.toHexString(key).toUpperCase();
        while (hex.length() < 16) {
            hex = "0" + hex;
        }
        return hex;
    }

    /**
     * Obsługuje zdarzenie kliknięcia przycisku generowania kluczy
     * Wywołuje metode z klasy DesxAlgorithm i uzupełnia pola tekstowe w GUI
     */
    @FXML
    void onGenerujKlucze(ActionEvent event) {
        desxAlgorithm.generate_keys();
        kluczPierwszy.setText(format_hex_key(desxAlgorithm.get_key_1()));
        kluczDrugi.setText(format_hex_key(desxAlgorithm.get_key_2()));
        kluczTrzeci.setText(format_hex_key(desxAlgorithm.get_key_3()));
    }

    /**
     * Główna metoda przeprowadza walidację wprowadzania danych, weryfikację długości i poprawności kluczy
     * Uruchamia konkretne metody do szyfracji/deszyfracji
     * Wyświetla poprawne dane w wyniku algorytmu
     */
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

    /**
     * Obsługuje ładowanie kluczy szyfrujących z zewnętrznego pliku (.key)
     */
    @FXML
    void onWczytajKlucze(ActionEvent event) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wybierz klucz");
        File file = fileChooser.showOpenDialog(get_window(event));

        if (file != null) {
            long[] key = fileManager.load_key(file.getAbsolutePath());
            desxAlgorithm.set_keys(key[0], key[1], key[2]);
            kluczPierwszy.setText(format_hex_key(key[0]));
            kluczDrugi.setText(format_hex_key(key[1]));
            kluczTrzeci.setText(format_hex_key(key[2]));
        }
    }

    /**
     * Obsługuje eksport aktualnie wprowadzonych kluczy do pliku (.key)
     * Posiada wbudowaną blokadę zapobiegającą zapisaniu pustych kluczy
     */
    @FXML
    void onZapiszKlucze(ActionEvent event) throws IOException {
        String k1 = kluczPierwszy.getText();
        String k2 = kluczDrugi.getText();
        String k3 = kluczTrzeci.getText();

        if (k1 == null || k1.isEmpty() || k2 == null || k2.isEmpty() || k3 == null || k3.isEmpty()) {
            show_alert("Nie można zapisać pustych kluczy");
            return;
        }

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Zapisz plik klucza");
        chooser.setInitialFileName("klucz.key");

        File file = chooser.showSaveDialog(get_window(event));

        if (file != null) {
            fileManager.save_key(file.getAbsolutePath(),
                    desxAlgorithm.get_key_1(),
                    desxAlgorithm.get_key_2(),
                    desxAlgorithm.get_key_3());
        }
    }

    /**
     * Wczytuje ścieżkę pliku przeznaczonego do modyfikacji przez algorytm
     */
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

    /**
     * Obsługuje zapis wyniku algorytmu
     * Automatycznie zarządza dodawaniem rozszerzenia ".enc" podczas szyfrowania i ucinania podczas deszyfracji
     */
    @FXML
    void onZapiszPlik(ActionEvent event) throws IOException {
        String file_out = pole_z_wynikiem_algorytmu.getText();
        if (file_out == null || file_out.isEmpty()) {
            show_alert("Nie możesz zapisać pustego pliku");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Zapisz plik");

        if (input_file_path != null) {
            String original_name = new File(input_file_path).getName();

            if (szyfrujButton.isSelected()) {
                fileChooser.setInitialFileName(original_name + ".enc");

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
            } else {
                fileChooser.setInitialFileName("odszyfrowana.txt");
            }
        }

        File file = fileChooser.showSaveDialog(get_window(event));

        if (file != null) {
            fileManager.write_file(file.getAbsolutePath(), proceeded_bytes);
        }
    }

    /**
     * Funkcja pomocnicza zwracająca główne okno aplikacji (niezbędne dla FileChooser)
     */
    private Window get_window(ActionEvent event) {
        return ((javafx.scene.Node) event.getSource()).getScene().getWindow();
    }

    /**
     * Funkcja pomocnicza wyświetlająca okna dialogowe
     */
    private void show_alert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }
}