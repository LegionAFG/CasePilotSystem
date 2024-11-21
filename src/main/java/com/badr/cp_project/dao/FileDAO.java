package com.badr.cp_project.dao;

import com.badr.cp_project.model.File;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileDAO {

    private static final String INSERT_FILE =
            "INSERT INTO document (fileName, fileType, uploadDate, filePath, clientIfaNumber) VALUES (?, ?, ?, ?, ?)";

    private static final String DELETE_FILE_BY_ID =
            "DELETE FROM document WHERE documentId = ?";

    private static final String SELECT_FILE_BY_IFA_NUMBER =
            "SELECT * " + " FROM document WHERE clientIfaNumber = ?";

    private final Logger logger = Logger.getLogger(FileDAO.class.getName());

    private final Connection conn;

    public FileDAO(Connection conn) {
        this.conn = conn;
    }

    public void addFile(File datei) {
        try (PreparedStatement stmt = conn.prepareStatement(INSERT_FILE, Statement.RETURN_GENERATED_KEYS)) {
            setDateiInsertParameters(stmt, datei);
            stmt.executeUpdate();
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    datei.setFileId(generatedKeys.getInt(1));
                }
            }
            logger.log(Level.INFO,"Datei erfolgreich gespeichert.");
        } catch (SQLException e) {
            logger.log(Level.SEVERE,"Fehler beim Speichern der Datei: ", e);
        }
    }

    public boolean deleteFileById(int fileId) {
        try (PreparedStatement stmt = conn.prepareStatement(DELETE_FILE_BY_ID)) {
            stmt.setInt(1, fileId);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                logger.log(Level.INFO, "Datei erfolgreich gelöscht.");
                return true;
            } else {
                logger.log(Level.WARNING, () -> "Keine Datei mit der ID " + fileId + " gefunden.");
                return false;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Fehler beim Löschen der Datei mit ID: " + fileId, e);
            return false;
        }
    }

    public List<File> getFileByIfa(String ifaNumber) {
        List<File> dateien = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_FILE_BY_IFA_NUMBER)) {
            stmt.setString(1, ifaNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    dateien.add(mapResultSetToFile(rs));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE,"Fehler beim Laden der Dateien: ", e);
        }
        return dateien;
    }

    private File mapResultSetToFile(ResultSet rs) throws SQLException {
        int documentId = rs.getInt("documentId");
        String fileName = rs.getString("fileName");
        String fileType = rs.getString("fileType");
        Date uploadDate = rs.getDate("uploadDate");
        String filePath = rs.getString("filePath");
        String clientIfaNumber = rs.getString("clientIfaNumber");

        return new File(documentId, uploadDate.toLocalDate(), fileType, fileName, filePath, clientIfaNumber);
    }

    private void setDateiInsertParameters(PreparedStatement stmt, File file) throws SQLException {
        stmt.setString(1, file.getFileName());
        stmt.setString(2, file.getFileTyp());
        stmt.setDate(3, java.sql.Date.valueOf(file.getFileUploadDate()));
        stmt.setString(4, file.getFilePath());
        stmt.setString(5, file.getFileClientIfaNumber());
    }
}
