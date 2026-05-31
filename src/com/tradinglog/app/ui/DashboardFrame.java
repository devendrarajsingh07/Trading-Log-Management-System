package com.tradinglog.app.ui;

import com.tradinglog.app.model.Trade;
import com.tradinglog.app.service.ExportCSVUtil;
import com.tradinglog.app.service.TradeDAO;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class DashboardFrame extends JFrame {

    private JLabel totalTradesLabel;
    private JLabel totalProfitLabel;
    private JLabel liveProfitLabel;
    private JLabel buyProfitLabel;
    private JLabel sellProfitLabel;
    private JLabel winningTradesLabel;
    private JLabel losingTradesLabel;

    private final TradeDAO tradeDAO = new TradeDAO();

    public DashboardFrame() {

        setTitle("Trading Log Dashboard");
        setSize(1100, 780);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(15, 15));

        JLabel title = new JLabel("Trading Log Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 32));
        add(title, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(2, 3, 20, 20));

        JButton addTradeBtn = new JButton("Add Trade");
        JButton viewTradeBtn = new JButton("View Trades");
        JButton reportsBtn = new JButton("Reports");
        JButton exportBtn = new JButton("Export CSV");
        JButton logoutBtn = new JButton("Logout");

        addTradeBtn.setFont(new Font("Arial", Font.BOLD, 20));
        viewTradeBtn.setFont(new Font("Arial", Font.BOLD, 20));
        reportsBtn.setFont(new Font("Arial", Font.BOLD, 20));
        exportBtn.setFont(new Font("Arial", Font.BOLD, 20));
        logoutBtn.setFont(new Font("Arial", Font.BOLD, 20));

        centerPanel.add(addTradeBtn);
        centerPanel.add(viewTradeBtn);
        centerPanel.add(reportsBtn);
        centerPanel.add(exportBtn);
        centerPanel.add(logoutBtn);

        add(centerPanel, BorderLayout.CENTER);

        JPanel statsPanel = new JPanel(new GridLayout(4, 2, 20, 10));

        totalTradesLabel = new JLabel("Total Trades: 0");
        totalProfitLabel = new JLabel("Total Profit: 0.00");
        liveProfitLabel = new JLabel("Live Profit: 0.00");
        buyProfitLabel = new JLabel("BUY Profit: 0.00");
        sellProfitLabel = new JLabel("SELL Profit: 0.00");
        winningTradesLabel = new JLabel("Winning Trades: 0");
        losingTradesLabel = new JLabel("Losing Trades: 0");

        JButton refreshBtn = new JButton("Refresh Stats");
        JButton clearBtn = new JButton("Clear Summary");

        statsPanel.add(totalTradesLabel);
        statsPanel.add(totalProfitLabel);
        statsPanel.add(liveProfitLabel);
        statsPanel.add(buyProfitLabel);
        statsPanel.add(sellProfitLabel);
        statsPanel.add(winningTradesLabel);
        statsPanel.add(losingTradesLabel);
        statsPanel.add(refreshBtn);

        JPanel southPanel = new JPanel(new BorderLayout(10, 10));
        southPanel.add(statsPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(clearBtn);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(southPanel, BorderLayout.SOUTH);

        addTradeBtn.addActionListener(e -> new TradeFormFrame().setVisible(true));
        viewTradeBtn.addActionListener(e -> new ViewTradesFrame().setVisible(true));
        reportsBtn.addActionListener(e -> showReport());

        exportBtn.addActionListener(e -> {
            try {
                List<Trade> trades = tradeDAO.getAllTrades();
                ExportCSVUtil.exportTradesToCSV(trades);
                JOptionPane.showMessageDialog(this, "Trades exported successfully to trades_export.csv");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Export Failed: " + ex.getMessage());
            }
        });

        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        refreshBtn.addActionListener(e -> loadStatistics());

        clearBtn.addActionListener(e -> {
            totalTradesLabel.setText("Total Trades: 0");
            totalProfitLabel.setText("Total Profit: 0.00");
            liveProfitLabel.setText("Live Profit: 0.00");
            buyProfitLabel.setText("BUY Profit: 0.00");
            sellProfitLabel.setText("SELL Profit: 0.00");
            winningTradesLabel.setText("Winning Trades: 0");
            losingTradesLabel.setText("Losing Trades: 0");
        });

        loadStatistics();
        ThemeUtil.applyDarkTheme(this);
    }

    private void loadStatistics() {
        try {
            List<Trade> trades = tradeDAO.getAllTrades();

            if (trades.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No trades found.");
            }

            int totalTrades = trades.size();
            double totalProfit = 0;
            double totalLiveProfit = 0;
            double buyProfit = 0;
            double sellProfit = 0;
            int winningTrades = 0;
            int losingTrades = 0;

            for (Trade trade : trades) {
                double profit = trade.calculateProfit();
                double liveProfit = trade.calculateLiveProfit();

                totalProfit += profit;
                totalLiveProfit += liveProfit;

                if ("BUY".equalsIgnoreCase(trade.getSide())) {
                    buyProfit += profit;
                } else if ("SELL".equalsIgnoreCase(trade.getSide())) {
                    sellProfit += profit;
                }

                if (profit > 0) {
                    winningTrades++;
                } else if (profit < 0) {
                    losingTrades++;
                }
            }

            totalTradesLabel.setText("Total Trades: " + totalTrades);
            totalProfitLabel.setText(String.format("Total Profit: %.2f", totalProfit));
            liveProfitLabel.setText(String.format("Live Profit: %.2f", totalLiveProfit));
            buyProfitLabel.setText(String.format("BUY Profit: %.2f", buyProfit));
            sellProfitLabel.setText(String.format("SELL Profit: %.2f", sellProfit));
            winningTradesLabel.setText("Winning Trades: " + winningTrades);
            losingTradesLabel.setText("Losing Trades: " + losingTrades);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading statistics:\n" + e.getMessage());
        }
    }

    private void showReport() {
        try {
            List<Trade> trades = tradeDAO.getAllTrades();

            int totalTrades = trades.size();
            double totalProfit = 0;
            double totalLiveProfit = 0;
            double buyProfit = 0;
            double sellProfit = 0;
            int winningTrades = 0;
            int losingTrades = 0;

            for (Trade trade : trades) {
                double profit = trade.calculateProfit();
                double liveProfit = trade.calculateLiveProfit();

                totalProfit += profit;
                totalLiveProfit += liveProfit;

                if ("BUY".equalsIgnoreCase(trade.getSide())) {
                    buyProfit += profit;
                } else if ("SELL".equalsIgnoreCase(trade.getSide())) {
                    sellProfit += profit;
                }

                if (profit > 0) {
                    winningTrades++;
                } else if (profit < 0) {
                    losingTrades++;
                }
            }

            String report =
                    "TRADING REPORT\n\n" +
                    "Total Trades: " + totalTrades + "\n" +
                    "Winning Trades: " + winningTrades + "\n" +
                    "Losing Trades: " + losingTrades + "\n" +
                    "BUY Profit: " + String.format("%.2f", buyProfit) + "\n" +
                    "SELL Profit: " + String.format("%.2f", sellProfit) + "\n" +
                    "Total Profit: " + String.format("%.2f", totalProfit) + "\n" +
                    "Live Profit: " + String.format("%.2f", totalLiveProfit);

            JOptionPane.showMessageDialog(
                    this,
                    report,
                    "Trading Report",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error generating report:\n" + e.getMessage());
        }
    }
}