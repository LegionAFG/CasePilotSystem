package com.badr.cp_project.dao;

import com.badr.cp_project.model.Appointment;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AppointmentDAO {

    private static final String SELECT_ALL_APPOINTMENT =
            "SELECT a.appointmentId, a.date, a.time, a.address, a.institution, a.priority, a.status, a.clientIfaNumber, " +
                    "k.lastName AS clientLastName, k.firstName AS clientFirstName " +
                    "FROM appointment a " +
                    "INNER JOIN client k ON a.clientIfaNumber = k.ifaNumber";

    private static final String SELECT_APPOINTMENT_BY_IFA_NUMBER =
            SELECT_ALL_APPOINTMENT + " WHERE a.clientIfaNumber = ?";

    private static final String INSERT_APPOINTMENT =
            "INSERT INTO appointment (date, time, address, institution, priority, status, clientIfaNumber) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static final String UPDATE_APPOINTMENT =
            "UPDATE appointment SET date = ?, time = ?, address = ?, institution = ?, priority = ?, status = ? WHERE appointmentId = ?";

    private static final String DELETE_APPOINTMENT_BY_ID =
            "DELETE FROM appointment WHERE appointmentId = ?";

    private final Connection conn;
    private final Logger logger = Logger.getLogger(AppointmentDAO.class.getName());

    public AppointmentDAO(Connection conn) {
        this.conn = conn;
    }

    public List<Appointment> getAllAppointment() {
        return executeAppointmentQuery(SELECT_ALL_APPOINTMENT);
    }

    public List<Appointment> getAppointmentByClientIfa(String ifaNumber) {
        return executeAppointmentQuery(SELECT_APPOINTMENT_BY_IFA_NUMBER, ifaNumber);
    }

    public void addAppointment(Appointment appointment) {
        try (PreparedStatement stmt = conn.prepareStatement(INSERT_APPOINTMENT)) {
            setAppointmentStatement(stmt, appointment);
            stmt.executeUpdate();
            logger.log(Level.INFO,"Termin erfolgreich gespeichert.");
        } catch (SQLException e) {
            logger.log(Level.SEVERE,"Fehler beim Speichern des Termins: ", e);
        }
    }

    public void updateAppointment(Appointment appointment) {
        try (PreparedStatement stmt = conn.prepareStatement(UPDATE_APPOINTMENT)) {
            setAppointmentStatement(stmt, appointment);
            stmt.setInt(7, appointment.getAppointmentId());
            stmt.executeUpdate();
            logger.log(Level.INFO,"Termin erfolgreich aktualisiert.");
        } catch (SQLException e) {
            logger.log(Level.SEVERE,"Fehler beim Aktualisieren des Termins: ", e);
        }
    }

    public void deleteAppointmentById(int appointmentId) {
        try (PreparedStatement stmt = conn.prepareStatement(DELETE_APPOINTMENT_BY_ID)) {
            stmt.setInt(1, appointmentId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.log(Level.INFO, "Termin erfolgreich gelöscht.");
            } else {
                logger.log(Level.INFO, "Kein Termin mit der angegebenen ID gefunden.");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Fehler beim Löschen des Termins: ", e);
        }
    }

    private List<Appointment> executeAppointmentQuery(String query, Object... params) {
        List<Appointment> appointmentList = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            setStatementParameters(stmt, params);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    appointmentList.add(mapResultSetToAppointment(rs));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE,"Fehler bei der Abfrage der Termine: ",e);
        }
        return appointmentList;
    }

    private Appointment mapResultSetToAppointment(ResultSet rs) throws SQLException {
        int id = rs.getInt("appointmentId");
        String clientIfaNumber = rs.getString("clientIfaNumber");
        String address = rs.getString("address");
        String institution = rs.getString("institution");
        LocalDate date = rs.getDate("date").toLocalDate();
        LocalTime time = rs.getTime("time").toLocalTime();
        String priority = rs.getString("priority");
        String status = rs.getString("status");
        String clientLastname = rs.getString("clientLastName");
        String clientFirstname = rs.getString("clientFirstName");

        return new Appointment(id, clientIfaNumber, address, institution, date, priority, status, time, clientLastname, clientFirstname);
    }

    private void setAppointmentStatement(PreparedStatement stmt, Appointment appointment) throws SQLException {
        stmt.setDate(1, Date.valueOf(appointment.getAppointmentDate()));
        stmt.setTime(2, Time.valueOf(appointment.getAppointmentTime()));
        stmt.setString(3, appointment.getAppointmentAddress());
        stmt.setString(4, appointment.getAppointmentInstitution());
        stmt.setString(5, appointment.getAppointmentPriority());
        stmt.setString(6, appointment.getAppointmentStatus());
        stmt.setString(7, appointment.getAppointmentClientIfaNumber());
    }

    private void setStatementParameters(PreparedStatement stmt, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
    }
}
