package com.badr.cp_project.dao;

import com.badr.cp_project.model.Client;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientDAO {

    private static final String SELECT_ALL_CLIENT =
            "SELECT ifaNumber, lastName, firstName, birthDate, gender, nationality, relationshipStatus FROM client";


    private static final String INSERT_CLIENT =
            "INSERT INTO client (ifaNumber, lastName, firstName, birthDate, nationality, gender, relationshipStatus) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static final String UPDATE_CLIENT =
            "UPDATE client SET lastName = ?, firstName = ?, birthDate = ?, nationality = ?, gender = ?, relationshipStatus = ? " +
                    "WHERE ifaNumber = ?";

    private static final String DELETE_CLIENT_BY_IFA_NUMBER =
            "DELETE FROM client WHERE ifaNumber = ?";

    private static final String EXISTS_BY_IFA_NUMBER =
            "SELECT COUNT(*) FROM client WHERE ifaNumber = ?";

    private final Logger logger = Logger.getLogger(ClientDAO.class.getName());

    private final Connection conn;

    public ClientDAO(Connection conn) {
        this.conn = conn;
    }

    public List<Client> getAllClient() {
        List<Client> klientenListe = new ArrayList<>();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL_CLIENT)) {
            while (rs.next()) {
                klientenListe.add(mapResultSetToClient(rs));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE,"Fehler beim Abrufen der Klienten: ", e );
        }
        return klientenListe;
    }

    public void addClient(Client client) {
        try (PreparedStatement stmt = conn.prepareStatement(INSERT_CLIENT)) {
            setClientInsertStatement(stmt, client);
            stmt.executeUpdate();
            logger.log(Level.INFO,"Klient erfolgreich gespeichert.");
        } catch (SQLException e) {
            logger.log(Level.SEVERE,"Fehler beim Speichern des Klienten: ", e );
        }
    }

    public void updateClient(Client client) {
        try (PreparedStatement stmt = conn.prepareStatement(UPDATE_CLIENT)) {
            setClientUpdateStatement(stmt, client);
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                logger.info("Klient erfolgreich aktualisiert.");
            } else {
                logger.warning("Kein Klient zum Aktualisieren gefunden.");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE,"Fehler beim Aktualisieren des Klienten: ", e );
        }
    }

    public boolean existsClientByIfa(String ifaNumber) {
        try (PreparedStatement stmt = conn.prepareStatement(EXISTS_BY_IFA_NUMBER)) {
            stmt.setString(1, ifaNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE,"Fehler bei der Überprüfung der IfaNummer: ", e );
        }
        return false;
    }

    public void deleteClientByIfa(String ifaNumber) {
        try (PreparedStatement stmt = conn.prepareStatement(DELETE_CLIENT_BY_IFA_NUMBER)) {
            stmt.setString(1, ifaNumber);
            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                logger.info("Klient erfolgreich aktualisiert.");
            } else {
                logger.warning("Kein Klient zum Aktualisieren gefunden.");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE,"Fehler beim Löschen des Klienten: ", e );
        }
    }

    private Client mapResultSetToClient(ResultSet rs) throws SQLException {
        String ifaNumber = rs.getString("ifaNumber");
        String lastname = rs.getString("lastName");
        String firstname = rs.getString("firstName");
        LocalDate dateOfBirth = rs.getDate("birthDate").toLocalDate();
        String gender = rs.getString("gender");
        String nationality = rs.getString("nationality");
        String relationship = rs.getString("relationshipStatus");

        return new Client(ifaNumber, lastname, firstname, dateOfBirth, nationality, gender, relationship);

    }

    private void setClientInsertStatement(PreparedStatement stmt, Client client) throws SQLException {
        stmt.setString(1, client.getClientIfaNumber());
        stmt.setString(2, client.getClientLastname());
        stmt.setString(3, client.getClientFirstname());
        stmt.setDate(4, java.sql.Date.valueOf(client.getClientDateOfBirth()));
        stmt.setString(5, client.getClientNationality());
        stmt.setString(6, client.getClientGender());
        stmt.setString(7, client.getClientRelationshipStatus());
    }

    private void setClientUpdateStatement(PreparedStatement stmt, Client client) throws SQLException {
        stmt.setString(1, client.getClientLastname());
        stmt.setString(2, client.getClientFirstname());
        stmt.setDate(3, java.sql.Date.valueOf(client.getClientDateOfBirth()));
        stmt.setString(4, client.getClientNationality());
        stmt.setString(5, client.getClientGender());
        stmt.setString(6, client.getClientRelationshipStatus());
        stmt.setString(7, client.getClientIfaNumber());
    }
}
