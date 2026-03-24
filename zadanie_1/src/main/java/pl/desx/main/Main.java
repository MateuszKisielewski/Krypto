/**
 * Autorzy: Krzysztof Kata (254776) i Mateusz Kisielewski (254779)
 */
package pl.desx.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {

    /**
     * Wywoływana automatycznie po zainicjalizowaniu środowiska graficznego
     * @param primaryStage Główne okno (Stage) dostarczone przez platformę JavaFX
     * @throws Exception Jeśli wystąpi błąd podczas ładowania pliku widoku .fxml
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/pl/desx/gui/main_window.fxml")));

        Scene scene = new Scene(root, 600, 450);

        primaryStage.setTitle("DESX");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * W projekcie metoda ta jest wywoływana bezpiecznie przez klasę Launcher
     * Wywołuje wbudowaną metodę launch() która budzi do życia wątek graficzny (JavaFX)
     * @param args Argumenty wiersza poleceń przekazane podczas uruchamiania programu
     */
    public static void main(String[] args) {
        launch(args);
    }
}