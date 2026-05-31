package com.tradinglog.app.ui;

import com.tradinglog.app.model.Trade;
import com.tradinglog.app.service.MarketPriceService;
import com.tradinglog.app.service.TradeDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class ViewTradesFrame extends JFrame {

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> sideFilterBox;
    private JTextField fromDateField;
    private JTextField toDateField;

    private final TradeDAO tradeDAO = new TradeDAO();

    public ViewTradesFrame() {

        setTitle("View Trades");
        setSize(1450, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        String[] columns = {
                "ID", "Symbol", "Side", "Strategy", "Remarks",
                "Trade Date", "Entry Price", "Exit Price",
                "Quantity", "Live Price", "Profit"
        };

        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        table.setRowHeight(24);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        searchField = new JTextField(12);
        sideFilterBox = new JComboBox<>(new String[]{"ALL", "BUY", "SELL"});
        fromDateField = new JTextField(10);
        toDateField = new JTextField(10);

        fromDateField.setText(LocalDate.now().minusDays(30).toString());
        toDateField.setText(LocalDate.now().toString());

        JButton searchBtn = new JButton("Search Symbol");
        JButton filterBtn = new JButton("Filter Side");
        JButton dateFilterBtn = new JButton("Filter Date");
        JButton resetBtn = new JButton("Reset");
        JButton refreshLiveBtn = new JButton("Refresh Live Price");

        topPanel.add(new JLabel("Symbol:"));
        topPanel.add(searchField);
        topPanel.add(searchBtn);

        topPanel.add(new JLabel("Side:"));
        topPanel.add(sideFilterBox);
        topPanel.add(filterBtn);

        topPanel.add(new JLabel("From:"));
        topPanel.add(fromDateField);
        topPanel.add(new JLabel("To:"));
        topPanel.add(toDateField);
        topPanel.add(dateFilterBtn);

        topPanel.add(resetBtn);
        topPanel.add(refreshLiveBtn);

        add(topPanel, BorderLayout.NORTH);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton editBtn = new JButton("Edit Selected");
        JButton deleteBtn = new JButton("Delete Selected");
        JButton refreshBtn = new JButton("Refresh");
        JButton closeBtn = new JButton("Close");

        bottomPanel.add(editBtn);
        bottomPanel.add(deleteBtn);
        bottomPanel.add(refreshBtn);
        bottomPanel.add(closeBtn);

        add(bottomPanel, BorderLayout.SOUTH);

        searchBtn.addActionListener(e -> searchBySymbol());
        filterBtn.addActionListener(e -> filterBySide());
        dateFilterBtn.addActionListener(e -> filterByDateRange());
        resetBtn.addActionListener(e -> refreshTable());

        editBtn.addActionListener(e -> editSelectedTrade());
        deleteBtn.addActionListener(e -> deleteSelectedTrade());
        refreshBtn.addActionListener(e -> refreshTable());
        refreshLiveBtn.addActionListener(e -> refreshSelectedLivePrice());
        closeBtn.addActionListener(e -> dispose());

        refreshTable();
        ThemeUtil.applyDarkTheme(this);
    }

    public void refreshTable() {
        loadTrades(tradeDAO.getAllTrades());
    }

    private void loadTrades(List<Trade> trades) {
        tableModel.setRowCount(0);

        if (trades == null || trades.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No trades found.");
            return;
        }

        for (Trade t : trades) {
            addRow(t);
        }
    }

    private void addRow(Trade t) {
        Object[] row = {
                t.getId(),
                t.getSymbol(),
                t.getSide(),
                t.getStrategy(),
                t.getRemarks(),
                t.getTradeDate(),
                t.getEntryPrice(),
                t.getExitPrice(),
                t.getQuantity(),
                String.format("%.2f", t.getLivePrice()),
                String.format("%.2f", t.calculateProfit())
        };
        tableModel.addRow(row);
    }

    private void searchBySymbol() {
        String symbol = searchField.getText().trim();

        if (symbol.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a symbol to search.");
            return;
        }

        loadTrades(tradeDAO.getTradesBySymbol(symbol));
    }

    private void filterBySide() {
        String side = sideFilterBox.getSelectedItem().toString();

        if (side.equals("ALL")) {
            refreshTable();
            return;
        }

        loadTrades(tradeDAO.getTradesBySide(side));
    }

    private void filterByDateRange() {
        try {
            String from = fromDateField.getText().trim();
            String to = toDateField.getText().trim();

            if (from.isEmpty() || to.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter both from date and to date.");
                return;
            }

            LocalDate fromDate = LocalDate.parse(from);
            LocalDate toDate = LocalDate.parse(to);

            if (fromDate.isAfter(toDate)) {
                JOptionPane.showMessageDialog(this, "From date must be before or equal to To date.");
                return;
            }

            loadTrades(tradeDAO.getTradesBetweenDates(from, to));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use yyyy-MM-dd.");
        }
    }

    private void refreshSelectedLivePrice() {
        int row = table.getSelectedRow();

        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a trade first.");
            return;
        }

        int tradeId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
        Trade trade = tradeDAO.getTradeById(tradeId);

        if (trade == null) {
            JOptionPane.showMessageDialog(this, "Trade not found.");
            return;
        }

        String symbol = trade.getSymbol().trim();

        new SwingWorker<Double, Void>() {
            @Override
            protected Double doInBackground() throws Exception {
                return MarketPriceService.getLivePrice(symbol);
            }

            @Override
            protected void done() {
                try {
                    double livePrice = get();
                    trade.setLivePrice(livePrice);
                    tradeDAO.updateTradeById(tradeId, trade);
                    refreshTable();
                    JOptionPane.showMessageDialog(ViewTradesFrame.this, "Live price updated.");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(ViewTradesFrame.this,
                            "Failed to refresh live price: " + ex.getMessage());
                }
            }
        }.execute();
    }

    private void editSelectedTrade() {
        int row = table.getSelectedRow();

        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a trade first.");
            return;
        }

        int tradeId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
        new EditTradeFrame(tradeId, this).setVisible(true);
    }

    private void deleteSelectedTrade() {
        int row = table.getSelectedRow();

        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a trade first.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete selected trade?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            int tradeId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
            tradeDAO.deleteTradeById(tradeId);
            refreshTable();
        }
    }
}