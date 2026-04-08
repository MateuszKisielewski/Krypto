package pl.rsa.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.math.BigInteger;

import pl.rsa.cryptography.BlindSignature;
import pl.rsa.cryptography.HashUtils;
import pl.rsa.cryptography.RSAKey;
import pl.rsa.cryptography.RSAKeyGenerator;
import pl.rsa.files.FileManager;

public class MainController {

    @FXML private TextArea textOryginalny;
    @FXML private TextArea textKomunikaty;
    @FXML private TextArea textRobocza;
    @FXML private TextField e;
    @FXML private TextField d;
    @FXML private TextField n;
    @FXML private RadioButton wprowadzTekstRadio;
    @FXML private RadioButton wprowadzPlikRadio;
    @FXML private RadioButton weryfikujRadio;
    @FXML private RadioButton szyfrujRadio;
    @FXML private ToggleGroup input_type;
    @FXML private ToggleGroup szyfrujweryfukuj;
    @FXML private Button zakrywanieButton;
    @FXML private Button podpisButton;
    @FXML private Button odkryjButton;
    @FXML private Button zweryfikujButton;
    @FXML private Button generujButton;
    @FXML private Button wczytajOryginlanyTekstButton;
    @FXML private Button wczytajRoboczyTekstButton;
    @FXML private Button zapiszButton;
    @FXML private Button wczytajKluczeButton;
    @FXML private Button zapiszKluczeButton;

    private RSAKey rsaKey;
    private BigInteger currentR;
    private byte[] originalData;
    private final BlindSignature blindSignature = new BlindSignature();

    @FXML
    public void initialize() {
        wprowadzTekstRadio.setSelected(true);
        szyfrujRadio.setSelected(true);
        onWprowadzTekstRadio(null);
        onSzyfrujRadio(null);
        textKomunikaty.setEditable(false);
    }

    @FXML
    void onWprowadzTekstRadio(ActionEvent event) {
        textOryginalny.setDisable(false);
        wczytajOryginlanyTekstButton.setDisable(true);
        log("Tryb: Wprowadzanie tekstu ręcznie");
    }

    @FXML
    void onWprowadzPlikRadio(ActionEvent event) {
        textOryginalny.setDisable(true);
        wczytajOryginlanyTekstButton.setDisable(false);
        log("Tryb: Wprowadzanie z pliku");
    }

    @FXML
    void onSzyfrujRadio(ActionEvent event) {
        zakrywanieButton.setDisable(false);
        podpisButton.setDisable(false);
        odkryjButton.setDisable(false);
        zweryfikujButton.setDisable(true);
        log("Tryb: Ślepy podpis cyfrowy");
    }

    @FXML
    void onWeryfikujRadio(ActionEvent event) {
        zakrywanieButton.setDisable(true);
        podpisButton.setDisable(true);
        odkryjButton.setDisable(true);
        zweryfikujButton.setDisable(false);
        log("Tryb: Weryfikacja podpisu");
    }

    @FXML
    void onGeneruj(ActionEvent event) {
        RSAKeyGenerator generator = new RSAKeyGenerator();
        rsaKey = generator.generateRSAKey(2048);

        e.setText(rsaKey.getE().toString(16));
        d.setText(rsaKey.getD().toString(16));
        n.setText(rsaKey.getN().toString(16));

        log("Wygenerowano nową parę kluczy RSA (2048-bit)");
    }

    @FXML
    void onZakrywanie(ActionEvent event) throws Exception {
        if (!updateOriginalData()) return;
        if (rsaKey == null) {
            log("Błąd: Brak kluczy! Wygeneruj je lub wczytaj");
            return;
        }

        BigInteger m_hash = HashUtils.dataToHash(originalData);
        BigInteger[] result = blindSignature.blindingText(m_hash, rsaKey.getE(), rsaKey.getN());
        currentR = result[1];

        textRobocza.setText(result[0].toString(16));
        log("Zakończono zakrywanie tekstu");
    }

    @FXML
    void onPodpis(ActionEvent event) {
        if (rsaKey == null || textRobocza.getText().isEmpty()) {
            log("Błąd: Brak kluczy lub przestrzeń robocza jest pusta");
            return;
        }

        BigInteger blindedText = new BigInteger(textRobocza.getText().trim(), 16);
        BigInteger signedBlindedText = blindSignature.signBlindText(blindedText, rsaKey.getD(), rsaKey.getN());

        textRobocza.setText(signedBlindedText.toString(16));
        log("Wykonano ślepy podpis przez serwer");
    }

    @FXML
    void onOdkryj(ActionEvent event) {
        if (rsaKey == null || currentR == null || textRobocza.getText().isEmpty()) {
            log("Błąd: Brakuje kluczy, czynnika 'r' lub przestrzeni roboczej");
            return;
        }

        BigInteger signedBlindedText = new BigInteger(textRobocza.getText().trim(), 16);
        BigInteger signedText = blindSignature.unblindSignedBlindedText(signedBlindedText, currentR, rsaKey.getN());

        textRobocza.setText(signedText.toString(16));
        log("Odkryto wiadomość z podpisem");
    }

    @FXML
    void onZweryfikuj(ActionEvent event) throws Exception {
        if (rsaKey == null || textRobocza.getText().isEmpty() || !updateOriginalData()) {
            log("Błąd: Nie można zweryfikować (brak kluczy, danych lub podpisu)");
            return;
        }

        BigInteger signedText = new BigInteger(textRobocza.getText().trim(), 16);
        BigInteger m_hash = HashUtils.dataToHash(originalData);

        boolean isVerified = blindSignature.verifySignedText(signedText, m_hash, rsaKey.getE(), rsaKey.getN());

        if (isVerified) {
            log("WYNIK: Weryfikacja POZYTYWNA!");
        } else {
            log("WYNIK: Weryfikacja NEGATYWNA!");
        }
    }

    @FXML
    void onWczytaj(ActionEvent event) throws Exception {
        Button clickedBtn = (Button) event.getSource();
        File file = showFileChooser(false);
        if (file == null) return;

        if (clickedBtn == wczytajOryginlanyTekstButton) {
            originalData = FileManager.readFile(file);
            textOryginalny.setText("wczytano plik " + file.getName());
            log("Wczytano oryginalny plik: " + file.getName());
        } else if (clickedBtn == wczytajRoboczyTekstButton) {
            textRobocza.setText(FileManager.readTextFromFile(file).trim());
            log("Wczytano do przestrzeni roboczej: " + file.getName());
        }
    }

    @FXML
    void onZapisz(ActionEvent event) throws Exception {
        File file = showFileChooser(true);
        if (file == null) return;

        FileManager.saveTextToFile(file, textRobocza.getText());
        log("Zapisano przestrzeń roboczą do: " + file.getName());
    }

    @FXML
    void onZapiszKlucze(ActionEvent event) throws Exception {
        File file = showFileChooser(true);
        if (file == null) return;

        String eStr = this.e.getText().trim();
        String dStr = this.d.getText().trim();
        String nStr = this.n.getText().trim();

        if (eStr.isEmpty() || dStr.isEmpty() || nStr.isEmpty()) {
            log("Błąd: Brak kompletnych kluczy do zapisania (wypełnij e, d oraz n)");
            return;
        }

        String klucze = "e:\n" + eStr + "\nd:\n" + dStr + "\nn:\n" + nStr;

        FileManager.saveTextToFile(file, klucze);
        log("Zapisano klucze do: " + file.getName());
    }

    @FXML
    void onWczytajKlucze(ActionEvent event) throws Exception {
        File file = showFileChooser(false);
        if (file == null) return;

        String keys = FileManager.readTextFromFile(file);
        String[] lines = keys.split("\n");

        if (lines.length >= 6) {
            String eStr = lines[1].trim();
            String dStr = lines[3].trim();
            String nStr = lines[5].trim();

            this.e.setText(eStr);
            this.d.setText(dStr);
            this.n.setText(nStr);

            BigInteger pubE = new BigInteger(eStr, 16);
            BigInteger privD = new BigInteger(dStr, 16);
            BigInteger modN = new BigInteger(nStr, 16);

            rsaKey = new RSAKey(pubE, privD, modN);
            log("Pomyślnie wczytano klucze z pliku: " + file.getName());
        } else {
            log("Błąd: Nieprawidłowy format pliku z kluczami.");
        }
    }

    private boolean updateOriginalData() {
        if (wprowadzTekstRadio.isSelected()) {
            String text = textOryginalny.getText();
            if (text == null || text.isEmpty()) {
                log("Błąd: Pole tekstu oryginalnego jest puste");
                return false;
            }
            originalData = text.getBytes();
        } else {
            if (originalData == null) {
                log("Błąd: Najpierw wczytaj plik z danymi wejściowymi");
                return false;
            }
        }
        return true;
    }

    private void log(String message) {
        textKomunikaty.appendText(message + "\n");
    }

    private File showFileChooser(boolean isSave) {
        FileChooser fileChooser = new FileChooser();
        Window window = textOryginalny.getScene().getWindow();

        if (isSave) {
            return fileChooser.showSaveDialog(window);
        } else {
            return fileChooser.showOpenDialog(window);
        }
    }
}