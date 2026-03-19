package pl.desx.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import pl.desx.cryptography.BlockCutter;
import pl.desx.cryptography.DesAlgorithm;
import pl.desx.cryptography.DesxAlgorithm;
import pl.desx.files.FileManager;

public class AppWindow {
    BlockCutter blockCutter = new BlockCutter();
    DesxAlgorithm desxAlgorithm = new DesxAlgorithm();
    FileManager fileManager = new FileManager();

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
    void onGenerujKlucze(ActionEvent event) {
        DesxAlgorithm des = new DesxAlgorithm();
        des.generate_keys();
    }

    @FXML
    void onStart(ActionEvent event) {
        if (deszyfrujButton.isSelected()) {

            desxAlgorithm.main_desx_block_decrypt();
        }

        if (szyfrujButton.isSelected()) {
            long[] keys = fileManager.load_key();
            desxAlgorithm.main_desx_block_encrypt();
        }
    }

    @FXML
    void onWczytajKlucze(ActionEvent event) {

    }

    @FXML
    void onWczytajPlik(ActionEvent event) {

    }

    @FXML
    void onZapiszKlucze(ActionEvent event) {

    }

    @FXML
    void onZapiszPlik(ActionEvent event) {

    }

}
