package pl.desx.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import pl.desx.cryptography.DesAlgorithm;
import pl.desx.cryptography.DesxAlgorithm;

public class AppWindow {

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
        DesxAlgorithm desx = new DesxAlgorithm();
        desx.generate_keys();
    }

    @FXML
    void onStart(ActionEvent event) {
        DesxAlgorithm desx = new DesxAlgorithm();
        if (choice.getSelectedToggle() == deszyfrujButton) {
            desx.main_desx_block_decrypt(desx.);
        }
        else {
            desx.main_desx_block_encrypt();
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
