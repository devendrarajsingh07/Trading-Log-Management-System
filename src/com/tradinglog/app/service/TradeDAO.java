package com.tradinglog.app.service;

import com.tradinglog.app.model.Trade;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TradeDAO {

    public void saveTrade(Trade trade) {
        String sql = "INSERT INTO trades " +
                "(symbol, side, strategy, remarks, trade_date, entry_price, exit_price, quantity, live_price, pnl) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, trade.getSymbol());
            ps.setString(2, trade.getSide());
            ps.setString(3, trade.getStrategy());
            ps.setString(4, trade.getRemarks());
            ps.setString(5, trade.getTradeDate());
            ps.setDouble(6, trade.getEntryPrice());
            ps.setDouble(7, trade.getExitPrice());
            ps.setInt(8, trade.getQuantity());
            ps.setDouble(9, trade.getLivePrice());
            ps.setDouble(10, trade.calculateProfit());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error saving trade: " + e.getMessage(), e);
        }
    }

    public List<Trade> getAllTrades() {
        List<Trade> trades = new ArrayList<>();
        String sql = "SELECT * FROM trades ORDER BY id DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Trade trade = new Trade(
                        rs.getInt("id"),
                        rs.getString("symbol"),
                        rs.getString("side"),
                        rs.getString("strategy"),
                        rs.getString("remarks"),
                        rs.getString("trade_date"),
                        rs.getDouble("entry_price"),
                        rs.getDouble("exit_price"),
                        rs.getInt("quantity"),
                        rs.getDouble("live_price")
                );
                trades.add(trade);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error loading trades: " + e.getMessage(), e);
        }

        return trades;
    }

    public Trade getTradeById(int id) {
        String sql = "SELECT * FROM trades WHERE id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Trade(
                            rs.getInt("id"),
                            rs.getString("symbol"),
                            rs.getString("side"),
                            rs.getString("strategy"),
                            rs.getString("remarks"),
                            rs.getString("trade_date"),
                            rs.getDouble("entry_price"),
                            rs.getDouble("exit_price"),
                            rs.getInt("quantity"),
                            rs.getDouble("live_price")
                    );
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching trade: " + e.getMessage(), e);
        }

        return null;
    }

    public void updateTradeById(int id, Trade trade) {
        String sql = "UPDATE trades SET " +
                "symbol = ?, side = ?, strategy = ?, remarks = ?, trade_date = ?, " +
                "entry_price = ?, exit_price = ?, quantity = ?, live_price = ?, pnl = ? " +
                "WHERE id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, trade.getSymbol());
            ps.setString(2, trade.getSide());
            ps.setString(3, trade.getStrategy());
            ps.setString(4, trade.getRemarks());
            ps.setString(5, trade.getTradeDate());
            ps.setDouble(6, trade.getEntryPrice());
            ps.setDouble(7, trade.getExitPrice());
            ps.setInt(8, trade.getQuantity());
            ps.setDouble(9, trade.getLivePrice());
            ps.setDouble(10, trade.calculateProfit());
            ps.setInt(11, id);

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error updating trade: " + e.getMessage(), e);
        }
    }

    public void deleteTradeById(int id) {
        String sql = "DELETE FROM trades WHERE id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting trade: " + e.getMessage(), e);
        }
    }

    public List<Trade> getTradesBySymbol(String symbol) {
        List<Trade> trades = new ArrayList<>();
        String sql = "SELECT * FROM trades WHERE LOWER(symbol) LIKE LOWER(?) ORDER BY id DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + symbol.trim() + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Trade trade = new Trade(
                            rs.getInt("id"),
                            rs.getString("symbol"),
                            rs.getString("side"),
                            rs.getString("strategy"),
                            rs.getString("remarks"),
                            rs.getString("trade_date"),
                            rs.getDouble("entry_price"),
                            rs.getDouble("exit_price"),
                            rs.getInt("quantity"),
                            rs.getDouble("live_price")
                    );
                    trades.add(trade);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error searching trades: " + e.getMessage(), e);
        }

        return trades;
    }

    public List<Trade> getTradesBySide(String side) {
        List<Trade> trades = new ArrayList<>();
        String sql = "SELECT * FROM trades WHERE LOWER(side) = LOWER(?) ORDER BY id DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, side);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Trade trade = new Trade(
                            rs.getInt("id"),
                            rs.getString("symbol"),
                            rs.getString("side"),
                            rs.getString("strategy"),
                            rs.getString("remarks"),
                            rs.getString("trade_date"),
                            rs.getDouble("entry_price"),
                            rs.getDouble("exit_price"),
                            rs.getInt("quantity"),
                            rs.getDouble("live_price")
                    );
                    trades.add(trade);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error filtering trades: " + e.getMessage(), e);
        }

        return trades;
    }

    public List<Trade> getTradesBetweenDates(String fromDate, String toDate) {
        List<Trade> trades = new ArrayList<>();
        String sql = "SELECT * FROM trades WHERE trade_date BETWEEN ? AND ? ORDER BY id DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, fromDate);
            ps.setString(2, toDate);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Trade trade = new Trade(
                            rs.getInt("id"),
                            rs.getString("symbol"),
                            rs.getString("side"),
                            rs.getString("strategy"),
                            rs.getString("remarks"),
                            rs.getString("trade_date"),
                            rs.getDouble("entry_price"),
                            rs.getDouble("exit_price"),
                            rs.getInt("quantity"),
                            rs.getDouble("live_price")
                    );
                    trades.add(trade);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error filtering by date: " + e.getMessage(), e);
        }

        return trades;
    }
}