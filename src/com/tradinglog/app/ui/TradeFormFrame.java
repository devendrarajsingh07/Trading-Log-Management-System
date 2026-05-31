package com.tradinglog.app.ui;

import com.tradinglog.app.model.Trade;
import com.tradinglog.app.service.MarketPriceService;
import com.tradinglog.app.service.TradeDAO;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class TradeFormFrame extends JFrame {

    private JTextField symbolField;
    private JComboBox<String> sideBox;
    private JTextField strategyField;
    private JTextField remarksField;
    private JTextField dateField;
    private JTextField entryField;
    private JTextField exitField;
    private JTextField quantityField;

    private JTextField livePriceField;
    private JLabel livePnlLabel;
    private JButton fetchPriceBtn;

    private final TradeDAO tradeDAO = new TradeDAO();

    public TradeFormFrame() {

        setTitle("Add Trade");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(11, 2, 10, 10));

        add(new JLabel("Symbol"));
        symbolField = new JTextField();
        add(symbolField);

        add(new JLabel("Side"));
        sideBox = new JComboBox<>(new String[]{"BUY", "SELL"});
        add(sideBox);

        add(new JLabel("Strategy"));
        strategyField = new JTextField();
        add(strategyField);

        add(new JLabel("Remarks"));
        remarksField = new JTextField();
        add(remarksField);

        add(new JLabel("Trade Date"));
        dateField = new JTextField(LocalDate.now().toString());
        add(dateField);

        add(new JLabel("Entry Price"));
        entryField = new JTextField();
        add(entryField);

        add(new JLabel("Exit Price"));
        exitField = new JTextField();
        add(exitField);

        add(new JLabel("Quantity"));
        quantityField = new JTextField();
        add(quantityField);

        add(new JLabel("Live Market Price"));
        livePriceField = new JTextField();
        livePriceField.setEditable(false);
        add(livePriceField);

        fetchPriceBtn = new JButton("Get Live Price");
        livePnlLabel = new JLabel("Live P/L: 0.00");
        add(fetchPriceBtn);
        add(livePnlLabel);

        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");
        add(saveBtn);
        add(cancelBtn);

        fetchPriceBtn.addActionListener(e -> fetchLivePrice());

        saveBtn.addActionListener(e -> {
            try {
                String symbol = symbolField.getText().trim();
                String side = sideBox.getSelectedItem().toString();
                String strategy = strategyField.getText().trim();
                String remarks = remarksField.getText().trim();
                String tradeDate = dateField.getText().trim();
                String entryText = entryField.getText().trim();
                String exitText = exitField.getText().trim();
                String qtyText = quantityField.getText().trim();

                if (symbol.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Symbol cannot be empty.");
                    return;
                }

                if (tradeDate.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Trade date cannot be empty.");
                    return;
                }

                double entry = Double.parseDouble(entryText);
                double exit = Double.parseDouble(exitText);
                int qty = Integer.parseInt(qtyText);

                if (entry <= 0 || exit < 0 || qty <= 0) {
                    JOptionPane.showMessageDialog(this, "Entry must be > 0, exit must be >= 0, quantity must be > 0.");
                    return;
                }

                double livePrice = parseLivePriceOrZero();

                Trade trade = new Trade(symbol, side, strategy, remarks, tradeDate, entry, exit, qty, livePrice);
                tradeDAO.saveTrade(trade);

                JOptionPane.showMessageDialog(this, "Trade Saved Successfully");
                dispose();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Entry, Exit, and Quantity must be valid numbers.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error saving trade: " + ex.getMessage());
            }
        });

        cancelBtn.addActionListener(e -> dispose());

        ThemeUtil.applyDarkTheme(this);
    }

    private void fetchLivePrice() {
        String symbol = symbolField.getText().trim();

        if (symbol.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter symbol first.");
            return;
        }

        fetchPriceBtn.setEnabled(false);
        livePriceField.setText("Fetching...");

        new SwingWorker<Double, Void>() {
            @Override
            protected Double doInBackground() throws Exception {
                return MarketPriceService.getLivePrice(symbol);
            }

            @Override
            protected void done() {
                try {
                    double livePrice = get();
                    livePriceField.setText(String.format("%.2f", livePrice));
                    updateLivePnL();
                } catch (Exception ex) {
                    livePriceField.setText("");
                    livePnlLabel.setText("Live P/L: 0.00");
                    JOptionPane.showMessageDialog(
                            TradeFormFrame.this,
                            "Failed to fetch live price: " + ex.getMessage()
                    );
                } finally {
                    fetchPriceBtn.setEnabled(true);
                }
            }
        }.execute();
    }

    private void updateLivePnL() {
        try {
            String side = sideBox.getSelectedItem().toString();
            double entry = Double.parseDouble(entryField.getText().trim());
            double livePrice = Double.parseDouble(livePriceField.getText().trim());
            int qty = Integer.parseInt(quantityField.getText().trim());

            double pnl;
            if ("BUY".equalsIgnoreCase(side)) {
                pnl = (livePrice - entry) * qty;
            } else {
                pnl = (entry - livePrice) * qty;
            }

            livePnlLabel.setText(String.format("Live P/L: %.2f", pnl));
        } catch (Exception ex) {
            livePnlLabel.setText("Live P/L: 0.00");
        }
    }

    private double parseLivePriceOrZero() {
        try {
            String txt = livePriceField.getText().trim();
            if (txt.isEmpty() || txt.equalsIgnoreCase("Fetching...")) {
                return 0.0;
            }
            return Double.parseDouble(txt);
        } catch (Exception ex) {
            return 0.0;
        }
    }
}