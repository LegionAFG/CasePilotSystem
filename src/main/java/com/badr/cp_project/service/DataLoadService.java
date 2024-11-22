package com.badr.cp_project.service;

import com.badr.cp_project.dao.FileDAO;
import com.badr.cp_project.dao.HistoryDAO;
import com.badr.cp_project.dao.ClientDAO;
import com.badr.cp_project.dao.AppointmentDAO;
import com.badr.cp_project.model.File;
import com.badr.cp_project.model.History;
import com.badr.cp_project.model.Client;
import com.badr.cp_project.model.Appointment;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataLoadService {

    private static final Logger LOGGER = Logger.getLogger(DataLoadService.class.getName());
    private final UtilityService utilityService;

    public DataLoadService(UtilityService utilityService) {

        this.utilityService = utilityService;
    }

    private <T> ObservableList<T> toObservableList(List<T> list) {

        return FXCollections.observableArrayList(list);
    }

    public ObservableList<Client> loadClientData(ClientDAO clientDAO) {
        return executeWithLogging(() -> toObservableList(clientDAO.getAllClient()), "Klientendaten");
    }

    public ObservableList<Appointment> loadAllAppointment(AppointmentDAO appointmentDAO) {
        return executeWithLogging(() -> toObservableList(appointmentDAO.getAllAppointment()), "Termindaten");
    }

    public ObservableList<Appointment> loadOffeneAppointment(AppointmentDAO appointmentDAO) {
        return executeWithLogging(() -> {
            List<Appointment> appointment = appointmentDAO.getAllAppointment();
            List<Appointment> openAppointments = List.copyOf(
                    appointment.stream()
                            .filter(appointments -> "Offen".equalsIgnoreCase(appointments.getAppointmentStatus()))
                            .toList()
            );
            return toObservableList(openAppointments);
        }, "offene Termine");
    }

    public ObservableList<Appointment> loadTermineByClientIfaNumber(AppointmentDAO appointmentDAO, String ifaNumber) {
        return executeWithLogging(() -> toObservableList(appointmentDAO.getAppointmentByClientIfa(ifaNumber)),
                "Termine für Klient mit Ifa-Nummer: " + ifaNumber);
    }

    public ObservableList<History> loadDocuments(HistoryDAO historyDAO) {
        return executeWithLogging(() -> toObservableList(historyDAO.getAllHistory()), "Dokumentationen");
    }

    public void saveOrUpdateClient(Client client, ClientDAO clientDAO) {
        try {
            if (clientDAO.existsClientByIfa(client.getClientIfaNumber())) {
                clientDAO.updateClient(client);
                LOGGER.info("Klient wurde erfolgreich aktualisiert.");
            } else {
                clientDAO.addClient(client);
                LOGGER.info("Neuer Klient wurde erfolgreich gespeichert.");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Fehler beim Speichern oder Aktualisieren des Klienten", e);
        }
    }

    public void resetClientForm(TextField firstnameField, TextField lastnameField, DatePicker dateOfBirthPicker,
                                TextField nationalityField, ChoiceBox<String> genderChoiceBox,
                                ChoiceBox<String> relationshipStatusField, Label ifaNumberLabel) {
        firstnameField.clear();
        lastnameField.clear();
        dateOfBirthPicker.setValue(null);
        nationalityField.clear();
        genderChoiceBox.setValue("Bitte auswählen...");
        relationshipStatusField.setValue("Bitte auswählen...");
        ifaNumberLabel.setText(utilityService.generateRandomIfaNumber());
    }

    public void updateAppointment(Appointment appointment, AppointmentDAO appointmentDAO) {
        try {
            appointmentDAO.updateAppointment(appointment);
            LOGGER.info("Termin wurde erfolgreich aktualisiert.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Fehler beim Aktualisieren des Termins", e);
        }
    }

    public void saveAppointment(Appointment appointment, AppointmentDAO appointmentDAO) {
        try {
            appointmentDAO.addAppointment(appointment);
            LOGGER.info("Neuer Termin wurde erfolgreich gespeichert.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Fehler beim Speichern des Termins", e);
        }
    }

    public void deleteAppointmentById(int appointmentId, AppointmentDAO appointmentDAO) {
        try {
            appointmentDAO.deleteAppointmentById(appointmentId);
            LOGGER.info("Termin wurde erfolgreich gelöscht.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Fehler beim Löschen des Termins mit ID: " + appointmentId, e);
        }
    }

    @FunctionalInterface
    private interface ActionWithResult<T> {
        T execute() throws ActionExecutionException;
    }

    public ObservableList<File> loadFileByClientIfaNumber(FileDAO fileDAO, String ifaNumber) {
        return executeWithLogging(() -> {
            List<File> files = fileDAO.getFileByIfa(ifaNumber);
            if (files == null || files.isEmpty()) {
                throw new ActionExecutionException("Keine Dateien gefunden für IFA-Nummer: " + ifaNumber);
            }
            return FXCollections.observableArrayList(files);
        }, "Dateien für IFA-Nummer " + ifaNumber);
    }

    private <T> T executeWithLogging(ActionWithResult<T> action, String contextMessage) {
        try {
            return action.execute();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Fehler beim Laden der " + contextMessage, e);
            return getDefaultValue();
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T getDefaultValue() {
        if (ObservableList.class.isAssignableFrom(Object.class)) {
            return (T) FXCollections.observableArrayList();
        }
        return null;

    }

    public static class ActionExecutionException extends Exception {
        public ActionExecutionException(String message) {
            super(message);
        }

    }
}