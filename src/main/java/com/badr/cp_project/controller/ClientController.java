package com.badr.cp_project.controller;

import com.badr.cp_project.dao.FileDAO;
import com.badr.cp_project.dao.HistoryDAO;
import com.badr.cp_project.dao.ClientDAO;
import com.badr.cp_project.dao.AppointmentDAO;
import com.badr.cp_project.model.File;
import com.badr.cp_project.model.History;
import com.badr.cp_project.model.Client;
import com.badr.cp_project.model.Appointment;
import com.badr.cp_project.service.DataLoadService;
import com.badr.cp_project.service.NavigationService;
import com.badr.cp_project.service.UtilityService;
import com.badr.cp_project.service.WindowsNaviService;
import com.badr.cp_project.database.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.io.IOException;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientController {

    private static final Logger LOGGER = Logger.getLogger(ClientController.class.getName());


    private final UtilityService utilityService = new UtilityService();
    private final NavigationService navigationService;
    private final DataLoadService dataLoadService;
    private final ClientDAO klientDAO;
    private final AppointmentDAO terminDAO;
    private final HistoryDAO dokuDAO;
    private final FileDAO dateiDAO;


    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");


    @FXML
    private TextField clientLastnameField;
    @FXML
    private TextField clientFirstnameField;
    @FXML
    private TextField nationalityField;
    @FXML
    private Label clientIfaNumber;
    @FXML
    private ChoiceBox<String> clientGenderChoiceBox;
    @FXML
    private ChoiceBox<String> relationshipStatusChoiceBox;
    @FXML
    private DatePicker dateOfBirthPicker;


    @FXML
    private TableView<Appointment> appointmentTableView;
    @FXML
    private TableColumn<Appointment, String> clientAdressColumn;
    @FXML
    private TableColumn<Appointment, String> clientInstitutionColumn;
    @FXML
    private TableColumn<Appointment, String> clientStatusColumn;
    @FXML
    private TableColumn<Appointment, String> clientPriorityColumn;
    @FXML
    private TableColumn<Appointment, LocalDate> clientDateColumn;
    @FXML
    private TableColumn<Appointment, LocalTime> clientTimeColumn;


    @FXML
    private TableView<History> documentTableView;
    @FXML
    private TableColumn<History, LocalDate> documentDateColumn;
    @FXML
    private TableColumn<History, LocalTime> documentTimeColumn;
    @FXML
    private TableColumn<History, String> documentTitelColumn;
    @FXML
    private TableColumn<History, String>  documentDescriptionColumn;


    @FXML
    private TableView<File> fileTableView;
    @FXML
    private TableColumn<File, String> fileNameColumn;
    @FXML
    private TableColumn<File, String> fileTypColumn;
    @FXML
    private TableColumn<File, LocalDate> fileUploadColumn;


    public ClientController() {
        DatabaseConnection dbConnection = new DatabaseConnection();
        Connection conn = dbConnection.connect();
        this.klientDAO = new ClientDAO(conn);
        this.dokuDAO = new HistoryDAO(conn);
        this.terminDAO = new AppointmentDAO(conn);
        this.dateiDAO = new FileDAO(conn);
        this.dataLoadService = new DataLoadService(utilityService);
        this.navigationService = new NavigationService();
    }

    private static final String DEFAULT_SELECTION_PROMPT = "Bitte auswählen...";

    @FXML
    private void initialize() {
        clientGenderChoiceBox.setItems(FXCollections.observableArrayList(DEFAULT_SELECTION_PROMPT, "Männlich", "Weiblich", "Diverses"));
        relationshipStatusChoiceBox.setItems(FXCollections.observableArrayList(DEFAULT_SELECTION_PROMPT, "Verheiratet", "Ledig", "Verwitwet"));

        clientGenderChoiceBox.setValue(DEFAULT_SELECTION_PROMPT);
        relationshipStatusChoiceBox.setValue(DEFAULT_SELECTION_PROMPT);
        dateOfBirthPicker.setValue(LocalDate.of(1900, 1, 1));

        nationalityField.clear();
        clientIfaNumber.setText(utilityService.generateRandomIfaNumber());

        initializeDokuTable();
        initializeTerminTable();
        initializeDateiTable();
        addTableListeners();
    }





    private void initializeDokuTable() {
        documentDateColumn.setCellValueFactory(cellData -> cellData.getValue().historyDateProperty());
        documentDateColumn.setCellFactory(utilityService.dateCellFactory(dateFormatter));
        documentTimeColumn.setCellValueFactory(cellData -> cellData.getValue().historyTimeProperty());
        documentTimeColumn.setCellFactory(utilityService.timeCellFactory(timeFormatter));
        documentTitelColumn.setCellValueFactory(cellData -> cellData.getValue().historyTitelProperty());
        documentDescriptionColumn.setCellValueFactory(cellData -> cellData.getValue().historyDescriptionProperty());
    }

    private void initializeTerminTable() {
        clientDateColumn.setCellValueFactory(cellData -> cellData.getValue().appointmentDateProperty());
        clientDateColumn.setCellFactory(utilityService.dateCellFactory(dateFormatter));
        clientTimeColumn.setCellValueFactory(cellData -> cellData.getValue().appointmentTimeProperty());
        clientTimeColumn.setCellFactory(utilityService.timeCellFactory(timeFormatter));
        clientAdressColumn.setCellValueFactory(cellData -> cellData.getValue().appointmentAddressProperty());
        clientInstitutionColumn.setCellValueFactory(cellData -> cellData.getValue().appointmentInstitutionProperty());
        clientStatusColumn.setCellValueFactory(cellData -> cellData.getValue().appointmentStatusProperty());
        clientPriorityColumn.setCellValueFactory(cellData -> cellData.getValue().appointmentPriorityProperty());
    }

    private void initializeDateiTable() {
        fileNameColumn.setCellValueFactory(cellData -> cellData.getValue().fileNameProperty());
        fileTypColumn.setCellValueFactory(cellData -> cellData.getValue().fileTypProperty());
        fileUploadColumn.setCellValueFactory(cellData -> cellData.getValue().fileUploadDateProperty());
        fileUploadColumn.setCellFactory(utilityService.dateCellFactory(dateFormatter));
    }


    private void addTableListeners() {
        utilityService.addDoubleClickListener(appointmentTableView, this::onAppointmentDoubleClick);
        utilityService.addDoubleClickListener(documentTableView, this::onDocumentDoubleClick);
        utilityService.addDoubleClickListener(fileTableView, this::onFileDoubleClick);
    }


    private void loadAppointmentForKlient(String ifaNummer) {
        ObservableList<Appointment> terminListe = dataLoadService.loadTermineByClientIfaNumber(terminDAO, ifaNummer);
        appointmentTableView.setItems(terminListe);
    }

    private void loadDocumentForKlient(String ifaNummer) {
        List<History> dokumentationList = dokuDAO.getHistoryByIfa(ifaNummer);
        ObservableList<History> dokumentationObservableList = FXCollections.observableArrayList(dokumentationList);
        documentTableView.setItems(dokumentationObservableList);
    }

    private void loadFileForKlient(String ifaNummer) {
        ObservableList<File> dateiListe = dataLoadService.loadFileByClientIfaNumber(dateiDAO, ifaNummer);
        fileTableView.setItems(dateiListe);
    }


    private void onAppointmentDoubleClick(Appointment termin) {
        utilityService.handleDoubleClick(
                appointmentTableView,
                "termin",
                termin.getAppointmentAddress(),
                navigationService::navigateToAppointmentDetail
        );
    }

    private void onDocumentDoubleClick(History doku) {
        utilityService.handleDoubleClick(
                documentTableView,
                "doku",
                doku.getHistoryTitel(),
                navigationService::navigateToDocumentDetail
        );
    }

    private void onFileDoubleClick(File datei) {
        openFileInBrowser(datei);
    }

    private void openFileInBrowser(File datei) {
        try {
            java.io.File file = new java.io.File(datei.getFilePath());
            if (file.exists()) {

                Desktop.getDesktop().browse(file.toURI());
            } else {
                showAlert("Die Datei wurde nicht gefunden.", "Datei öffnen");
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Fehler beim Öffnen der Datei im Browser.", e);
            showAlert("Fehler beim Öffnen der Datei im Browser.", "Datei öffnen");
        }
    }


    @FXML
    private void onSaveButtonClick(ActionEvent ignoredEvent) {
        try {
            String ifaNumber = clientIfaNumber.getText();
            String lastname = clientLastnameField.getText();
            String firstname = clientFirstnameField.getText();
            LocalDate dateOfBirth = dateOfBirthPicker.getValue();
            String nationality = nationalityField.getText();
            String gender = clientGenderChoiceBox.getValue();
            String relationshipStatus = relationshipStatusChoiceBox.getValue();


            if (!isInputValid(lastname, firstname, dateOfBirth, gender, relationshipStatus)) return;

            Client klient = new Client(ifaNumber, lastname, firstname, dateOfBirth, nationality, gender, relationshipStatus);
            boolean isNewKlient = !klientDAO.existsClientByIfa(ifaNumber);

            dataLoadService.saveOrUpdateClient(klient, klientDAO);
            LOGGER.log(Level.INFO, "Klient gespeichert: {0} {1}", new Object[]{lastname, firstname});

            loadAppointmentForKlient(ifaNumber);
            loadDocumentForKlient(ifaNumber);
            loadFileForKlient(ifaNumber);

            if (isNewKlient) {
                dataLoadService.resetClientForm(clientLastnameField, clientFirstnameField, dateOfBirthPicker, nationalityField,
                        clientGenderChoiceBox, relationshipStatusChoiceBox, clientIfaNumber);
                LOGGER.info("Neuer Klient, Formular zurückgesetzt.");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Fehler beim Speichern des Klienten", e);
            showAlert("Speicherfehler", "Fehler beim Speichern der Klientendaten.");
        }
    }

    private boolean isInputValid(String lastname, String firstname, LocalDate dateOfBirth, String gender, String relationshipStatus) {
        if (lastname.isEmpty() || firstname.isEmpty() || dateOfBirth == null) {
            showAlert("Eingabefehler", "Bitte füllen Sie alle erforderlichen Felder aus.");
            return false;
        }
        if (DEFAULT_SELECTION_PROMPT.equals(gender) || DEFAULT_SELECTION_PROMPT.equals(relationshipStatus)) {
            showAlert("Eingabefehler", "Bitte wählen Sie ein gültiges Geschlecht und einen Beziehungsstatus aus.");
            return false;
        }
        return true;
    }

    private void showAlert(String message, String title) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    protected void onHomeButtonClick(ActionEvent event) {
        navigateToPage(event, String.valueOf(WindowsNaviService.Page.HOME));
    }


    @FXML
    protected void onFileButtonClick(ActionEvent event) {
        String ifaNummer = clientIfaNumber.getText();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        navigationService.navigateToFileDetail(stage, ifaNummer);
    }

    @FXML
    protected void onAppointmentButtonClick(ActionEvent event) {
        String ifaNumber = clientIfaNumber.getText();
        String lastname = clientLastnameField.getText();
        String firstname = clientFirstnameField.getText();


        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();


        navigationService.navigateToAppointmentDetail(stage, ifaNumber, lastname, firstname);
    }

    @FXML
    protected void onDocumentButtonClick(ActionEvent event) {
        String ifaNumber = clientIfaNumber.getText();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        navigationService.navigateToDocumentDetail(stage, ifaNumber);
    }

    private void navigateToPage(ActionEvent event, String pageKey) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        navigationService.navigateTo(stage, WindowsNaviService.Page.valueOf(pageKey));
    }

    @FXML
    private void onDeleteButtonClick(ActionEvent event) {
        String ifaNumber = clientIfaNumber.getText();

        if (ifaNumber.isEmpty()) {
            showAlert("Fehler", "Keine gültige Ifa-Nummer ausgewählt.");
            return;
        }

        if (!navigationService.showConfirmDeleteDialog()) {
            return;
        }

        klientDAO.deleteClientByIfa(ifaNumber);
        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.log(Level.INFO, String.format("Klient gelöscht: %s", ifaNumber));
        }

        dataLoadService.resetClientForm(clientLastnameField, clientFirstnameField, dateOfBirthPicker, nationalityField, clientGenderChoiceBox, relationshipStatusChoiceBox, clientIfaNumber);
        navigateToHome(event);
    }

    private void navigateToHome(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        navigationService.navigateTo(stage, WindowsNaviService.Page.HOME);
    }

    public void setClientData(String ifaNumber, String lastname, String firstname, LocalDate dateOfBirth,  String nationality,String gender, String relationshipStatus) {
        clientIfaNumber.setText(ifaNumber);
        clientLastnameField.setText(lastname);
        clientFirstnameField.setText(firstname);
        dateOfBirthPicker.setValue(dateOfBirth);

        nationalityField.setText(nationality);
        clientGenderChoiceBox.setValue(gender);

        relationshipStatusChoiceBox.setValue(relationshipStatus);

        loadAppointmentForKlient(ifaNumber);
        loadDocumentForKlient(ifaNumber);
        loadFileForKlient(ifaNumber);
    }

}
