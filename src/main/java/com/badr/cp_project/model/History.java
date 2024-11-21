package com.badr.cp_project.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.time.LocalDate;
import java.time.LocalTime;

public class History {
    private final SimpleObjectProperty<LocalDate> HistoryDate;
    private final SimpleObjectProperty<LocalTime> HistoryTime;
    private final SimpleStringProperty HistoryDescription;
    private final SimpleStringProperty HistoryClientIfaNumber;
    private final SimpleStringProperty HistoryTitel;
    private int HistoryId;

    public History(int HistoryId, LocalDate HistoryDatum, LocalTime HistoryUhrzeit, String HistoryDeskription, String HistoryClientIfaNumber, String HistoryTitel) {
        this.HistoryId = HistoryId;
        this.HistoryDate = new SimpleObjectProperty<>(HistoryDatum);
        this.HistoryTime = new SimpleObjectProperty<>(HistoryUhrzeit);
        this.HistoryDescription = new SimpleStringProperty(HistoryDeskription);
        this.HistoryClientIfaNumber = new SimpleStringProperty(HistoryClientIfaNumber);
        this.HistoryTitel = new SimpleStringProperty(HistoryTitel);
    }

    public int getHistoryId() {
        return HistoryId;
    }

    public void setHistoryId(int historyId) {
        this.HistoryId = historyId;
    }

    public LocalDate getHistoryDate() {
        return HistoryDate.get();
    }

    public SimpleObjectProperty<LocalDate> historyDateProperty() {
        return HistoryDate;
    }

    public LocalTime getHistoryTime() {
        return HistoryTime.get();
    }

    public SimpleObjectProperty<LocalTime> historyTimeProperty() {
        return HistoryTime;
    }

    public String getHistoryDescription() {
        return HistoryDescription.get();
    }

    public SimpleStringProperty historyDescriptionProperty() {
        return HistoryDescription;
    }

    public String getHistoryClientIfaNumber() {
        return HistoryClientIfaNumber.get();
    }

    public String getHistoryTitel() {
        return HistoryTitel.get();
    }

    public SimpleStringProperty historyTitelProperty() {
        return HistoryTitel;
    }
}
