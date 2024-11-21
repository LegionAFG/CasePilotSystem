package com.badr.cp_project.service;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UtilityService {

    private static final Logger LOGGER = Logger.getLogger(UtilityService.class.getName());
    private final Random random = new Random();

    public String generateRandomIfaNumber() {
        String ifaNumber = String.valueOf(100000 + random.nextInt(900000));
        LOGGER.log(Level.INFO, "Generierte zufällige IFA-Nummer: {0}", ifaNumber);
        return ifaNumber;
    }

    public <T> void addDoubleClickListener(TableView<T> tableView, Consumer<T> onItemDoubleClick) {
        tableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                T selectedItem = tableView.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    LOGGER.log(Level.INFO, "Element im TableView doppelt angeklickt: {0}", selectedItem);
                    onItemDoubleClick.accept(selectedItem);
                } else {
                    LOGGER.log(Level.WARNING, "Kein Element im TableView ausgewählt.");
                }
            }
        });
    }

    public <T> void handleDoubleClick(TableView<T> tableView, String itemType, String detailInfo, BiConsumer<Stage, T> navigateFunction) {
        addDoubleClickListener(tableView, item -> {
            LOGGER.info(String.format("Doppelklick auf %s: %s", itemType, detailInfo));
            Stage stage = (Stage) tableView.getScene().getWindow();
            navigateFunction.accept(stage, item);
        });
    }

    public <T> void loadData(TableView<T> tableView, ObservableList<T> dataList, String errorMessage) {
        try {
            tableView.setItems(dataList);
            LOGGER.log(Level.INFO, "Daten erfolgreich in TableView geladen: {0} Einträge.", dataList.size());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, errorMessage, e);
        }
    }

    public <T> Callback<TableColumn<T, LocalDate>, TableCell<T, LocalDate>> dateCellFactory(DateTimeFormatter dateFormatter) {
        return ignore -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : dateFormatter.format(item));
            }
        };
    }

    public <T> Callback<TableColumn<T, LocalTime>, TableCell<T, LocalTime>> timeCellFactory(DateTimeFormatter timeFormatter) {
        return ignore -> new TableCell<>() {
            @Override
            protected void updateItem(LocalTime item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : timeFormatter.format(item));
            }
        };
    }

    public <T> void addSearchFilter(TextField searchField, TableView<T> tableView, FilteredList<T> filteredList, BiPredicate<T, String> filterFunction) {

        filteredList.setPredicate(ignore -> true);

        searchField.textProperty().addListener((ignore, ignore2, newValue) -> {
            filteredList.setPredicate(item -> {

                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();
                return filterFunction.test(item, lowerCaseFilter);
            });

            SortedList<T> sortedList = new SortedList<>(filteredList);
            sortedList.comparatorProperty().bind(tableView.comparatorProperty());
            tableView.setItems(sortedList);
        });

        SortedList<T> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedList);
    }
}
