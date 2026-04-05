package pl.rsa.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.math.BigInteger;

// Zakładam, że Twoje klasy są w pakiecie pl.rsa.cryptography
import pl.rsa.cryptography.BlindSignature;
import pl.rsa.cryptography.HashUtils;
import pl.rsa.cryptography.RSAKey;
import pl.rsa.cryptography.RSAKeyGenerator;
import pl.rsa.files.FileManager;

public class MainController {

    @FXML private Button generujButton;
    @FXML private Button odkryjButton;
    @FXML private Button podpisButton;
    @FXML private Button wczytajButton;
    @FXML private Button zakrywanieButton;
    @FXML private Button zapiszButton;
    @FXML private Button zweryfikujButton;

    @FXML private ToggleGroup input_type;
    @FXML private ToggleGroup szyfrujweryfukuj;

    @FXML private RadioButton szyfrujRadio;
    @FXML private RadioButton weryfikujRadio;
    @FXML private RadioButton wprowadzPlikRadio;
    @FXML private RadioButton wprowadzTekstRadio;

    @FXML private TextArea textKomunikaty;
    @FXML private TextArea textOryginalny;
    @FXML private TextArea textRobocza;

    @FXML private TextField textPrywatnyKlucz;
    @FXML private TextField textPublicznyKlucza;

    // --- ZMIENNE PRZECHOWUJĄCE STAN APLIKACJI ---
    private RSAKey rsaKey;               // Wygenerowane klucze
    private byte[] fileData;             // Załadowane bajty z pliku
    private BigInteger currentHash_m;    // Hash m
    private BigInteger blindedMessage;   // m'
    private BigInteger blindingFactor_r; // r
    private BigInteger blindedSignature; // s'
    private BigInteger finalSignature;   // s

    @FXML
    public void initialize() {
        // Ta metoda wykonuje się automatycznie po załadowaniu okna.
        log("Witaj w programie! Wygeneruj klucze, aby rozpocząć.");

        // Ustawiamy domyślny stan interfejsu (np. blokujemy przycisk wczytywania, bo wybrano tekst)
        wczytajButton.setDisable(true);
        textOryginalny.setDisable(false);
    }

    // --- METODY OBSŁUGI RADIO BUTTONÓW ---

    @FXML
    void onWprowadzTekstRadio(ActionEvent event) {
        wczytajButton.setDisable(true);
        textOryginalny.setDisable(false);
        fileData = null; // Czyścimy dane z pliku
        log("Tryb wejścia: Wpisywanie ręczne tekstu.");
    }

    @FXML
    void onWprowadzPlikRadio(ActionEvent event) {
        wczytajButton.setDisable(false);
        textOryginalny.setDisable(true);
        textOryginalny.clear();
        log("Tryb wejścia: Wczytywanie z pliku.");
    }

    @FXML
    void onSzyfrujRadio(ActionEvent event) {
        log("Tryb operacji: Generowanie Ślepego Podpisu.");
        zakrywanieButton.setDisable(false);
        podpisButton.setDisable(false);
        odkryjButton.setDisable(false);
    }

    @FXML
    void onWeryfikujRadio(ActionEvent event) {
        log("Tryb operacji: Weryfikacja. Wklej wygenerowany podpis do pola 'Tekst roboczy / Podpis'.");
        zakrywanieButton.setDisable(true);
        podpisButton.setDisable(true);
        odkryjButton.setDisable(true);
    }

    // --- METODY OBSŁUGI PRZYCISKÓW ---

    @FXML
    void onGeneruj(ActionEvent event) {
        log("Generowanie kluczy 1024-bitowych. Proszę czekać...");
        try {
            RSAKeyGenerator generator = new RSAKeyGenerator();
            rsaKey = generator.generateRSAKey(1024);

            // Wyświetlenie kluczy w polach tekstowych (w skróconej formie dla czytelności)
            textPublicznyKlucza.setText("e: " + rsaKey.getE() + ", n: " + truncate(rsaKey.getN().toString()));
            textPrywatnyKlucz.setText("d: " + truncate(rsaKey.getD().toString()));

            log("Klucze wygenerowane pomyślnie!");
        } catch (Exception e) {
            log("Błąd generowania kluczy: " + e.getMessage());
        }
    }

    @FXML
    void onWczytaj(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wybierz plik");
        File file = fileChooser.showOpenDialog(getStage());

        if (file != null) {
            try {
                fileData = FileManager.readFile(file);
                textOryginalny.setText("Załadowano plik: " + file.getName() + " (" + fileData.length + " bajtów)");
                log("Wczytano plik: " + file.getName());
            } catch (Exception e) {
                log("Błąd wczytywania pliku: " + e.getMessage());
            }
        }
    }

    @FXML
    void onZakrywanie(ActionEvent event) {
        if (rsaKey == null) {
            log("Najpierw wygeneruj klucze!"); return;
        }

        try {
            byte[] dataToProcess = getInputData();
            if (dataToProcess == null || dataToProcess.length == 0) {
                log("Brak danych do zaślepienia!"); return;
            }

            // Hashowanie
            currentHash_m = HashUtils.dataToHash(dataToProcess);
            log("1. Wyliczono hash z wiadomości: " + truncate(currentHash_m.toString()));

            // Zaślepianie
            BlindSignature blindSignatureClass = new BlindSignature();
            BigInteger[] blinded = blindSignatureClass.blindingText(currentHash_m, rsaKey.getE(), rsaKey.getN());

            blindedMessage = blinded[0];
            blindingFactor_r = blinded[1];

            textRobocza.setText(blindedMessage.toString());
            log("2. Wiadomość pomyślnie zaślepiona (m').");

        } catch (Exception e) {
            log("Błąd podczas zaślepiania: " + e.getMessage());
        }
    }

    @FXML
    void onPodpis(ActionEvent event) {
        if (rsaKey == null || blindedMessage == null) {
            log("Brak zaślepionej wiadomości do podpisania!"); return;
        }

        try {
            BlindSignature blindSignatureClass = new BlindSignature();
            blindedSignature = blindSignatureClass.signBlindText(blindedMessage, rsaKey.getD(), rsaKey.getN());

            textRobocza.setText(blindedSignature.toString());
            log("3. Serwer podpisał zaślepioną wiadomość (s').");

        } catch (Exception e) {
            log("Błąd podpisywania: " + e.getMessage());
        }
    }

    @FXML
    void onOdkryj(ActionEvent event) {
        if (rsaKey == null || blindedSignature == null || blindingFactor_r == null) {
            log("Brak zaślepionego podpisu do odkrycia!"); return;
        }

        try {
            BlindSignature blindSignatureClass = new BlindSignature();
            finalSignature = blindSignatureClass.unblindSignature(blindedSignature, blindingFactor_r, rsaKey.getN());

            textRobocza.setText(finalSignature.toString());
            log("4. Użytkownik zdjął zaślepienie. Otrzymano finalny podpis (s).");

        } catch (Exception e) {
            log("Błąd odkrywania podpisu: " + e.getMessage());
        }
    }

    @FXML
    void onZweryfikuj(ActionEvent event) {
        if (rsaKey == null) {
            log("Brak klucza publicznego (e, n) do weryfikacji!"); return;
        }

        try {
            // Pobieramy dane wejściowe
            byte[] dataToProcess = getInputData();
            if (dataToProcess == null || dataToProcess.length == 0) {
                log("Brak oryginalnych danych do zweryfikowania!"); return;
            }

            // Pobieramy podpis z pola roboczego
            String signatureText = textRobocza.getText().trim();
            if (signatureText.isEmpty()) {
                log("Podpisz tekst lub wklej istniejący podpis w pole robocze!"); return;
            }

            BigInteger signatureToVerify = new BigInteger(signatureText);
            BigInteger originalHash = HashUtils.hashData(dataToProcess);

            // Weryfikacja
            BlindSignature blindSignatureClass = new BlindSignature();
            boolean isValid = blindSignatureClass.verifySignature(signatureToVerify, originalHash, rsaKey.getE(), rsaKey.getN());

            if (isValid) {
                log(">>> [SUKCES] Podpis JEST PRAWIDŁOWY!");
            } else {
                log(">>> [BŁĄD] Podpis NIE JEST PRAWIDŁOWY!");
            }

        } catch (Exception e) {
            log("Błąd podczas weryfikacji (zły format podpisu?): " + e.getMessage());
        }
    }

    @FXML
    void onZapisz(ActionEvent event) {
        String textToSave = textRobocza.getText();
        if (textToSave == null || textToSave.isEmpty()) {
            log("Brak danych (podpisu) do zapisania!"); return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Zapisz wynik");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Plik Tekstowy", "*.txt"));
        File file = fileChooser.showSaveDialog(getStage());

        if (file != null) {
            try {
                FileManager.saveTextToFile(file, textToSave);
                log("Pomyślnie zapisano do pliku: " + file.getName());
            } catch (Exception e) {
                log("Błąd podczas zapisywania: " + e.getMessage());
            }
        }
    }

    // --- FUNKCJE POMOCNICZE ---

    // Wypisywanie zdarzeń na konsole w GUI
    private void log(String message) {
        textKomunikaty.appendText(message + "\n");
    }

    // Skracanie długich liczb BigInteger, by nie "rozsadziły" interfejsu
    private String truncate(String text) {
        if (text.length() > 30) {
            return text.substring(0, 15) + "..." + text.substring(text.length() - 15);
        }
        return text;
    }

    // Pobranie danych do hashowania (zależnie czy wybrano plik, czy wpisano tekst)
    private byte[] getInputData() {
        if (wprowadzPlikRadio.isSelected()) {
            return fileData;
        } else {
            String text = textOryginalny.getText();
            return text != null ? text.getBytes() : null;
        }
    }

    // Pobranie głównego okna aplikacji dla okienek wyboru plików (FileChooser)
    private Stage getStage() {
        return (Stage) generujButton.getScene().getWindow();
    }
}