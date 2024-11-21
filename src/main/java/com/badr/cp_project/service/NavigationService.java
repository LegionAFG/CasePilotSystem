package com.badr.cp_project.service;

import com.badr.cp_project.controller.FileController;
import com.badr.cp_project.controller.HistoryController;
import com.badr.cp_project.controller.ClientController;
import com.badr.cp_project.controller.AppointmentController;
import com.badr.cp_project.model.History;
import com.badr.cp_project.model.Client;
import com.badr.cp_project.model.Appointment;
import javafx.stage.Stage;

import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NavigationService {

    private static final Logger LOGGER = Logger.getLogger(NavigationService.class.getName());
    private final WindowsNaviService windowsNaviService;

    public NavigationService() {

        this.windowsNaviService = new WindowsNaviService();
    }

    public void navigateTo(Stage stage, WindowsNaviService.Page page) {

        navigateTo(stage, page, null);
    }

    public <T> void navigateTo(Stage stage, WindowsNaviService.Page page, Consumer<T> controllerConsumer) {
        try {
            windowsNaviService.navigateTo(stage, page, controllerConsumer);
            LOGGER.log(Level.INFO, "Erfolgreich zu Seite: {0} navigiert.", page.name());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Fehler beim Navigieren zu Seite: " + page.name(), e);
        }
    }

    public boolean showConfirmDeleteDialog() {
        LOGGER.log(Level.INFO, "Zeige Bestätigungsdialog für das Löschen an.");
        return windowsNaviService.showConfirmDeleteDialog();
    }

    public <T> void navigateToDetail(Stage stage, WindowsNaviService.Page page, T ignored, Consumer<Object> controllerSetup) {
        try {
            windowsNaviService.navigateTo(stage, page, controller -> {
                if (controller != null && controllerSetup != null) {
                    controllerSetup.accept(controller);
                }
            });
            LOGGER.log(Level.INFO, "Erfolgreich zu Seite: {0} navigiert.", page.name());

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Fehler beim Navigieren zu Seite: " + page.name(), e);
        }
    }

    public void navigateToClientDetail(Stage stage, Client client) {
        navigateToDetail(stage, WindowsNaviService.Page.CLIENT, client, controller -> {
            if (controller instanceof ClientController clientController) {
                clientController.setClientData(
                        client.getClientIfaNumber(),
                        client.getClientLastname(),
                        client.getClientFirstname(),
                        client.getClientDateOfBirth(),
                        client.getClientNationality(),
                        client.getClientGender(),
                        client.getClientRelationshipStatus()
                );
            }
        });
    }

    public void navigateToDocumentDetail(Stage stage, String ifaNumber) {
        navigateToDetail(stage, WindowsNaviService.Page.HISTORY, ifaNumber, controller -> {
            if (controller instanceof HistoryController historyController) {
                historyController.setHistoryByClientIfaNumber(ifaNumber);
            }
        });
    }

    public void navigateToDocumentDetail(Stage stage, History history) {
        navigateToDetail(stage, WindowsNaviService.Page.HISTORY, history, controller -> {
            if (controller instanceof HistoryController historyController) {
                historyController.setHistoryByClientIfaNumber(history.getHistoryClientIfaNumber());
            }
        });
    }

    public void navigateToAppointmentDetail(Stage stage, Appointment appointment) {
        navigateToDetail(stage, WindowsNaviService.Page.APPOINTMENT, appointment, controller -> {
            if (controller instanceof AppointmentController appointmentController) {
                appointmentController.setClientData(
                        appointment.getAppointmentClientIfaNumber(),
                        appointment.getAppointmentClientLastname(),
                        appointment.getAppointmentClientFirstname()
                );
            }
        });
    }

    public void navigateToAppointmentDetail(Stage stage, String ifaNumber, String lastname, String firstname) {
        navigateToDetail(stage, WindowsNaviService.Page.APPOINTMENT, new String[]{ifaNumber, lastname, firstname}, controller -> {
            if (controller instanceof AppointmentController appointmentController) {
                appointmentController.setClientData(ifaNumber, lastname, firstname);
            }
        });
    }

    public void navigateToFileDetail(Stage stage, String ifaNumber) {
        navigateToDetail(stage, WindowsNaviService.Page.FILE, ifaNumber, controller -> {
            if (controller instanceof FileController fileController) {
                fileController.setFileClientIfaNumber(ifaNumber);
            }
        });
    }
}
