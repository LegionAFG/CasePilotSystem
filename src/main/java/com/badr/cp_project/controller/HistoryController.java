package com.badr.cp_project.controller;

import com.badr.cp_project.dao.HistoryDAO;
import com.badr.cp_project.model.History;
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
import javafx.stage.Stage;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

public class HistoryController {

    private final NavigationService navigationService;
    private final UtilityService utilityService;
    private final DataLoadService dataLoadService;

    private final HistoryDAO historyDAO;
    private History selectedHistory;

    @FXML
    private TextArea historyDescriptionTextArea;
    @FXML
    private DatePicker historyDatePicker;
    @FXML
    private TextField historyTimeField;
    @FXML
    private TextField historyClientIfaNumberField;
    @FXML
    private TextField historyTitelField;

    @FXML
    private TableView<History> historyTableView;
    @FXML
    private TableColumn<History, LocalDate> historyLocalDateTableColumn;
    @FXML
    private TableColumn<History, LocalTime> historyLocalTimeTableColumn;
    @FXML
    private TableColumn<History, String> historyStringTableColumn;
    @FXML
    private TableColumn<History, String> historyStringTableColumn1;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    public HistoryController() {
        this.utilityService = new UtilityService();
        this.navigationService = new NavigationService();
        this.dataLoadService = new DataLoadService(utilityService);
        Connection connection = new DatabaseConnection().connect();
        this.historyDAO = new HistoryDAO(connection);
    }

    @FXML
    private void initialize() {
        historyDatePicker.setValue(LocalDate.now());
        historyTimeField.setText(LocalTime.now().format(timeFormatter));


        setCellFactories();
        addDoubleClickListener();
        loadHistoryData();
    }

    private void setCellFactories() {
        historyLocalDateTableColumn.setCellValueFactory(cellData -> cellData.getValue().historyDateProperty());
        historyLocalDateTableColumn.setCellFactory(utilityService.dateCellFactory(dateFormatter));
        historyLocalTimeTableColumn.setCellValueFactory(cellData -> cellData.getValue().historyTimeProperty());
        historyLocalTimeTableColumn.setCellFactory(utilityService.timeCellFactory(timeFormatter));
        historyStringTableColumn.setCellValueFactory(cellData -> cellData.getValue().historyTitelProperty());
        historyStringTableColumn1.setCellValueFactory(cellData -> cellData.getValue().historyDescriptionProperty());
    }

    private void addDoubleClickListener() {
        utilityService.addDoubleClickListener(historyTableView, this::loadDocumentIntoFields);
    }

    private void loadHistoryData() {
        ObservableList<History> dokuList = dataLoadService.loadDocuments(historyDAO);
        historyTableView.setItems(dokuList);
    }

    private void loadDocumentIntoFields(History history) {
        this.selectedHistory = history;
        historyClientIfaNumberField.setText(history.getHistoryClientIfaNumber());
        historyDescriptionTextArea.setText(history.getHistoryDescription());
        historyDatePicker.setValue(history.getHistoryDate());
        historyTimeField.setText(history.getHistoryTime().toString());
        historyTitelField.setText(history.getHistoryTitel());
    }

    public void setHistoryByClientIfaNumber(String ifaNumber) {
        historyClientIfaNumberField.setText(ifaNumber);
        historyClientIfaNumberField.setDisable(true);

    }

    @FXML
    protected void onSaveButtonClick(ActionEvent ignoredEvent) {
        String historyDescriptionTextAreaText = historyDescriptionTextArea.getText();
        LocalDate datePickerValue = historyDatePicker.getValue();
        LocalTime historyTime;
        try {
            historyTime = LocalTime.parse(historyTimeField.getText());
        } catch (DateTimeParseException e) {
            showAlert("Bitte geben Sie eine gültige Uhrzeit im Format HH:mm ein.");
            return;
        }
        String historyClientIfaNumberFieldText = historyClientIfaNumberField.getText();
        String historyTitelFieldText = historyTitelField.getText();

        if (!isHistoryValid(historyDescriptionTextAreaText, historyClientIfaNumberFieldText, historyTitelFieldText, datePickerValue, historyTime)) {
            showAlert("Bitte füllen Sie alle erforderlichen Felder aus.");
            return;
        }

        History history = createHistoryObject(historyDescriptionTextAreaText, datePickerValue, historyTime, historyClientIfaNumberFieldText, historyTitelFieldText);
        if (selectedHistory != null) {
            history.setHistoryId(selectedHistory.getHistoryId());
            historyDAO.updateHistory(history);
        } else {
            historyDAO.addHistory(history);
        }

        loadHistoryData();
        clearForm();
    }

    private History createHistoryObject(String historyDeskription, LocalDate historyDate, LocalTime historyTime, String historyClientIfaNumber, String historyTitel) {
        return new History(0, historyDate, historyTime, historyDeskription, historyClientIfaNumber, historyTitel);
    }

    private boolean isHistoryValid(String historyDeskription, String historyClientIfaNumber, String historyTitle, LocalDate historyDate, LocalTime historyTime) {
        return !historyDeskription.isEmpty() && historyClientIfaNumber != null && !historyClientIfaNumber.isEmpty() && historyTitle != null && !historyTitle.isEmpty() && historyDate != null && historyTime != null;
    }

    @FXML
    protected void onHomeButtonClick(ActionEvent event) {
        navigateToHome(event);
    }

    private void navigateToHome(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        navigationService.navigateTo(stage, WindowsNaviService.Page.HOME);
    }

    @FXML
    private void onDeleteButtonClick(ActionEvent ignoredEvent) {
        History selectedDoku = historyTableView.getSelectionModel().getSelectedItem();
        if (selectedDoku == null) {
            showAlert("Bitte wählen Sie eine Dokumentation aus, die Sie löschen möchten.");
            return;
        }

        boolean confirm = showConfirmationDialog();
        if (!confirm) {
            return;
        }

        historyDAO.deleteHistoryById(selectedDoku.getHistoryId());
        loadHistoryData();
    }

    private boolean showConfirmationDialog() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Bestätigung");
        alert.setHeaderText(null);
        alert.setContentText("Sind Sie sicher, dass Sie diese Dokumentation löschen möchten?");
        alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.YES;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearForm() {
        historyDescriptionTextArea.clear();
        historyDatePicker.setValue(null);
        historyTimeField.clear();
        historyTitelField.clear();
        selectedHistory = null;
    }

}
