package com.badr.cp_project.service;

import com.badr.cp_project.run.MainApp;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WindowsNaviService {

    private static final Logger LOGGER = Logger.getLogger(WindowsNaviService.class.getName());

    public enum Page {
        CLIENT("/com/badr/cp_project/Client.fxml"),
        HOME("/com/badr/cp_project/Home.fxml"),
        HISTORY("/com/badr/cp_project/History.fxml"),
        FILE("/com/badr/cp_project/File.fxml"),
        APPOINTMENT("/com/badr/cp_project/Appointment.fxml");

        private final String fxmlPath;

        Page(String fxmlPath) {

            this.fxmlPath = fxmlPath;
        }

        public String getFxmlPath() {

            return fxmlPath;
        }
    }

    public <T> void navigateTo(Stage stage, Page page, Consumer<T> controllerConsumer) {
        String fxmlPath = page.getFxmlPath();

        try {

            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource(fxmlPath));
            Parent root = fxmlLoader.load();

            T controller = fxmlLoader.getController();
            if (controllerConsumer != null && controller != null) {
                controllerConsumer.accept(controller);
            }

            Scene scene = new Scene(root);
            stage.setTitle("CasePilot - " + page.name());
            stage.setScene(scene);
            stage.show();
            stage.setResizable(false);

            LOGGER.log(Level.INFO, "Seite erfolgreich geladen: {0}", page.name());
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Fehler beim Laden der Seite: " + page.name(), e);
        }
    }

    public boolean showConfirmDeleteDialog() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Löschen bestätigen");
        alert.setHeaderText("Möchten Sie diesen Eintrag wirklich löschen?");
        alert.setContentText("Dieser Vorgang kann nicht rückgängig gemacht werden.");

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}
