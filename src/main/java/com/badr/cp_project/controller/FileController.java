package com.badr.cp_project.controller;

import com.badr.cp_project.dao.FileDAO;
import com.badr.cp_project.model.File;
import com.badr.cp_project.service.DataLoadService;
import com.badr.cp_project.service.NavigationService;
import com.badr.cp_project.service.UtilityService;
import com.badr.cp_project.service.WindowsNaviService;
import com.badr.cp_project.database.DatabaseConnection;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileController {

    private static final Logger LOGGER = Logger.getLogger(FileController.class.getName());

    private final FileDAO fileDAO;
    private final DataLoadService dataLoadService;
    private final NavigationService navigationService;

    @FXML
    private TableView<File> fileTableView;
    @FXML
    private TableColumn<File, String> fileNameColumn;
    @FXML
    private TableColumn<File, String> fileTypColumn;
    @FXML
    private TableColumn<File, LocalDate> dateUploadColumn;
    @FXML
    private TextField fileClientIfaNumberField;


    public FileController() {
        Connection connection = new DatabaseConnection().connect();
        this.fileDAO = new FileDAO(connection);
        this.dataLoadService = new DataLoadService(new UtilityService());
        this.navigationService = new NavigationService();
    }

    @FXML
    private void initialize() {
        setCellFactories();


        fileTableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                File selectedDatei = fileTableView.getSelectionModel().getSelectedItem();
                if (selectedDatei != null) {
                    openFileInBrowser(selectedDatei);
                }
            }
        });
    }

    private void setCellFactories() {
        fileNameColumn.setCellValueFactory(cellData -> cellData.getValue().fileNameProperty());
        fileTypColumn.setCellValueFactory(cellData -> cellData.getValue().fileTypProperty());
        dateUploadColumn.setCellValueFactory(cellData -> cellData.getValue().fileUploadDateProperty());
    }

    public void setFileClientIfaNumber(String ifaNummer) {
        fileClientIfaNumberField.setText(ifaNummer);
        fileClientIfaNumberField.setDisable(true);
        loadFileForClient(ifaNummer);
    }

    private void loadFileForClient(String ifaNummer) {
        ObservableList<File> dateiListe = dataLoadService.loadFileByClientIfaNumber(fileDAO, ifaNummer);
        fileTableView.setItems(dateiListe);
    }

    @FXML
    protected void onHomeButtonClick(ActionEvent event) {

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        navigationService.navigateTo(stage, WindowsNaviService.Page.HOME);
    }

    @FXML
    protected void onUploadButtonClick(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        java.io.File file = fileChooser.showOpenDialog(((Node) event.getSource()).getScene().getWindow());
        if (file != null) {
            LocalDate hochladedatum = LocalDate.now(); // Datum des Hochladens
            String ifaNummer = fileClientIfaNumberField.getText(); // Ifa-Nummer des Klienten

            try {
                String dateiTyp = Files.probeContentType(file.toPath()); // Dateityp (MIME-Typ)


                String targetDirectory = "uploads";
                java.io.File dir = new java.io.File(targetDirectory);


                if (!dir.exists()) {
                    boolean isCreated = dir.mkdirs();
                    if (!isCreated) {
                        if (LOGGER.isLoggable(Level.SEVERE)) {
                            LOGGER.severe(String.format("Das Verzeichnis '%s' konnte nicht erstellt werden.", targetDirectory));
                        }
                        return;
                    }
                }


                java.io.File targetFile = new java.io.File(dir, file.getName());


                Files.copy(file.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);


                String dateiPfad = targetFile.getAbsolutePath();


                File datei = new File(hochladedatum, dateiTyp, file.getName(), dateiPfad, ifaNummer);


                fileDAO.addFile(datei);


                loadFileForClient(ifaNummer);

            } catch (IOException e) {

                LOGGER.log(Level.SEVERE, "Fehler beim Verarbeiten der Datei: " + file.getName(), e);
            }
        }
    }

    @FXML
    protected void onDownloadButtonClick(ActionEvent event) {
        File selectedDatei = fileTableView.getSelectionModel().getSelectedItem();
        if (selectedDatei != null) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Datei speichern unter");
            fileChooser.setInitialFileName(selectedDatei.getFileName());
            java.io.File saveFile = fileChooser.showSaveDialog(((Node) event.getSource()).getScene().getWindow());

            if (saveFile != null) {
                try {
                    java.io.File sourceFile = new java.io.File(selectedDatei.getFilePath());
                    if (sourceFile.exists()) {

                        Files.copy(sourceFile.toPath(), saveFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        showAlert("Datei erfolgreich heruntergeladen.");
                    } else {
                        showAlert("Die Datei wurde nicht gefunden.");
                    }
                } catch (IOException e) {
                    showAlert("Fehler beim Herunterladen der Datei.");
                    LOGGER.log(Level.SEVERE, "Fehler beim Herunterladen der Datei.", e);
                }
            }
        } else {
            showAlert("Bitte wählen Sie eine Datei aus der Tabelle zum Herunterladen aus.");
        }
    }


    @FXML
    private void onDeleteButtonClick(ActionEvent ignoredEvent) {
        File selectedDatei = fileTableView.getSelectionModel().getSelectedItem();
        if (selectedDatei != null) {
            int dokumentId = selectedDatei.getFileId();

            if (dokumentId <= 0) {
                showAlert("Die ausgewählte Datei hat eine ungültige ID und kann nicht gelöscht werden.");
                return;
            }

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Bestätigung");
            alert.setHeaderText(null);
            alert.setContentText("Möchten Sie die ausgewählte Datei wirklich löschen?");
            alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);

            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.YES) {

                boolean isDeleted = fileDAO.deleteFileById(dokumentId);
                if (isDeleted) {
                    showAlert("Datei erfolgreich gelöscht.");
                    loadFileForClient(selectedDatei.getFileClientIfaNumber());
                } else {
                    showAlert("Löschen der Datei ist fehlgeschlagen. Möglicherweise existiert sie nicht mehr.");
                }
            }
        } else {
            showAlert("Bitte wählen Sie eine Datei aus der Tabelle aus, die gelöscht werden soll.");
        }
    }


    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void openFileInBrowser(File datei) {
        try {
            java.io.File file = new java.io.File(datei.getFilePath());
            if (file.exists()) {

                Desktop.getDesktop().browse(file.toURI());
            } else {
                showAlert("Die Datei wurde nicht gefunden.");
            }
        } catch (IOException e) {
            showAlert("Fehler beim Öffnen der Datei.");
            LOGGER.log(Level.SEVERE, "Fehler beim Öffnen der Datei.", e);
        }
    }

}
