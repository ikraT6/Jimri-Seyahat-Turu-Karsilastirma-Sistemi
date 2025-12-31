package com.travel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OfferService {

    /* ================= COUNTRY (TOUR) ================= */

    // TÜM ÜLKELER
    public List<String> getAllCountries() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT tour_name FROM tour ORDER BY tour_name";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(rs.getString("tour_name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // YENİ ÜLKE EKLE (UNIQUE)
    public boolean addCountry(String country) {
        String sql = "INSERT INTO tour (tour_name) VALUES (?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, country);
            ps.executeUpdate();
            return true;

        } catch (SQLException ex) {
            return false; // zaten varsa
        }
    }

    /* ================= COMPANY ================= */

    // TÜM FİRMALAR
    public List<String> getAllCompanies() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT company_name FROM company ORDER BY company_name";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(rs.getString("company_name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /* ================= OFFER ================= */

    // ÜLKEYE GÖRE OFFER LİSTELE
    public List<Offer> getOffersByCountry(String country, boolean asc) {

        List<Offer> list = new ArrayList<>();

        String sql =
            "SELECT c.company_name, o.flight_price, o.hotel_price, o.total_price " +
            "FROM offer o " +
            "JOIN company c ON o.company_id = c.company_id " +
            "JOIN tour t ON o.tour_id = t.tour_id " +
            "WHERE t.tour_name = ? " +
            "ORDER BY o.total_price " + (asc ? "ASC" : "DESC");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, country);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new Offer(
                        rs.getString("company_name"),
                        rs.getDouble("flight_price"),
                        rs.getDouble("hotel_price"),
                        rs.getDouble("total_price")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // OFFER EKLE (AYNI ÜLKE + FİRMA OLAMAZ)
    public boolean addOfferByName(String country, String company,
                                  double flight, double hotel) {

        String sql =
            "INSERT INTO offer (tour_id, company_id, flight_price, hotel_price, total_price) " +
            "VALUES ( " +
            "(SELECT tour_id FROM tour WHERE tour_name = ?), " +
            "(SELECT company_id FROM company WHERE company_name = ?), ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, country);
            ps.setString(2, company);
            ps.setDouble(3, flight);
            ps.setDouble(4, hotel);
            ps.setDouble(5, flight + hotel);

            ps.executeUpdate();
            return true;

        } catch (SQLException ex) {
            return false; // duplicate
        }
    }

    // OFFER SİL
    public void deleteOfferByName(String country, String company) {

        String sql =
            "DELETE o FROM offer o " +
            "JOIN tour t ON o.tour_id = t.tour_id " +
            "JOIN company c ON o.company_id = c.company_id " +
            "WHERE t.tour_name = ? AND c.company_name = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, country);
            ps.setString(2, company);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // OFFER GÜNCELLE
    public void updateOfferByName(String country, String company,
                                  double flight, double hotel) {

        String sql =
            "UPDATE offer o " +
            "JOIN tour t ON o.tour_id = t.tour_id " +
            "JOIN company c ON o.company_id = c.company_id " +
            "SET o.flight_price = ?, o.hotel_price = ?, o.total_price = ? " +
            "WHERE t.tour_name = ? AND c.company_name = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, flight);
            ps.setDouble(2, hotel);
            ps.setDouble(3, flight + hotel);
            ps.setString(4, country);
            ps.setString(5, company);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
 

}
