package com.badr.cp_project.dao;

import com.badr.cp_project.model.History;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HistoryDAO {

    private static final String SELECT_ALL_HISTORY =
            "SELECT documentationId, date, time, description, title, clientIfaNumber FROM documentation";

    private static final String INSERT_HISTORY =
            "INSERT INTO documentation (date, time, description, title, clientIfaNumber) VALUES (?, ?, ?, ?, ?)";

    private static final String UPDATE_HISTORY =
            "UPDATE documentation SET date = ?, time = ?, description = ?, title = ? WHERE documentationId = ?";

    private static final String DELETE_HISTORY_BY_ID =
            "DELETE FROM documentation WHERE documentationId = ?";

    private static final String SELECT_HISTORY_BY_IFA_NUMBER =
            "SELECT *" +
                    "FROM documentation WHERE clientIfaNumber = ?";

    private final Connection conn;
    private final Logger logger = Logger.getLogger(getClass().getName());

    public HistoryDAO(Connection conn) {
        this.conn = conn;
    }

    public List<History> getAllHistory() {
        List<History> dokuList = new ArrayList<>();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL_HISTORY)) {

            while (rs.next()) {
                dokuList.add(mapResultSetToHistory(rs));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE,"Fehler beim Abrufen der Historie: ", e);
        }
        return dokuList;
    }

    public void addHistory(History history) {
        try (PreparedStatement stmt = conn.prepareStatement(INSERT_HISTORY)) {
            setHistoryInsertParameters(stmt, history);
            stmt.executeUpdate();
            logger.log(Level.INFO,"Historie erfolgreich gespeichert.");
        } catch (SQLException e) {
            logger.log(Level.SEVERE,"Fehler beim Speichern der Historie:: ", e);
        }
    }

    public void updateHistory(History history) {
        try (PreparedStatement stmt = conn.prepareStatement(UPDATE_HISTORY)) {
            setHistoryUpdateParameters(stmt, history);
            stmt.executeUpdate();
            logger.log(Level.INFO,"Historie erfolgreich aktualisiert.");
        } catch (SQLException e) {
            logger.log(Level.SEVERE,"Fehler beim Aktualisieren der Historie: ", e);
        }
    }

    public void deleteHistoryById(int historyId) {
        try (PreparedStatement stmt = conn.prepareStatement(DELETE_HISTORY_BY_ID)) {
            stmt.setInt(1, historyId);
            stmt.executeUpdate();
            logger.log(Level.INFO,"Historie erfolgreich gelöscht.");
        } catch (SQLException e) {
            logger.log(Level.SEVERE,"Fehler beim Löschen der Historie: ", e);
        }
    }

    public List<History> getHistoryByIfa(String ifaNumber) {
        List<History> history = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_HISTORY_BY_IFA_NUMBER)) {
            stmt.setString(1, ifaNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    history.add(mapResultSetToHistory(rs));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE,"Fehler beim Laden der Historie: ", e);
        }
        return history;
    }

    private History mapResultSetToHistory(ResultSet rs) throws SQLException {
        int documentId = rs.getInt("documentationId");
        LocalDate documentDate = rs.getDate("date").toLocalDate();
        LocalTime documentTime = rs.getTime("time").toLocalTime();
        String documentDescription = rs.getString("description");
        String documentClientIfaNumber = rs.getString("clientIfaNumber");
        String documentTitle = rs.getString("title");

        return new History(documentId, documentDate, documentTime, documentDescription, documentClientIfaNumber, documentTitle);
    }

    private void setHistoryInsertParameters(PreparedStatement stmt, History history) throws SQLException {
        stmt.setDate(1, java.sql.Date.valueOf(history.getHistoryDate()));
        stmt.setTime(2, java.sql.Time.valueOf(history.getHistoryTime()));
        stmt.setString(3, history.getHistoryDescription());
        stmt.setString(4, history.getHistoryTitel());
        stmt.setString(5, history.getHistoryClientIfaNumber());
    }

    private void setHistoryUpdateParameters(PreparedStatement stmt, History history) throws SQLException {
        stmt.setDate(1, java.sql.Date.valueOf(history.getHistoryDate()));
        stmt.setTime(2, java.sql.Time.valueOf(history.getHistoryTime()));
        stmt.setString(3, history.getHistoryDescription());
        stmt.setString(4, history.getHistoryTitel());
        stmt.setInt(5, history.getHistoryId());
    }
}
