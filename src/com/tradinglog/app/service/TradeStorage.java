package com.tradinglog.app.service;

import com.tradinglog.app.model.Trade;

import java.io.*;
import java.util.ArrayList;

public class TradeStorage {

    public static ArrayList<Trade> trades = new ArrayList<>();
    private static final String FILE_NAME = "trades.csv";

    public static void saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Trade t : trades) {
                writer.write(
                        escape(t.getSymbol()) + "," +
                        escape(t.getSide()) + "," +
                        escape(t.getStrategy()) + "," +
                        escape(t.getRemarks()) + "," +
                        escape(t.getTradeDate()) + "," +
                        t.getEntryPrice() + "," +
                        t.getExitPrice() + "," +
                        t.getQuantity()
                );
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving file: " + e.getMessage());
        }
    }

    public static void loadFromFile() {
        trades.clear();

        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = splitCsv(line);

                if (parts.length == 8) {
                    Trade t = new Trade(
                            unescape(parts[0]),
                            unescape(parts[1]),
                            unescape(parts[2]),
                            unescape(parts[3]),
                            unescape(parts[4]),
                            Double.parseDouble(parts[5]),
                            Double.parseDouble(parts[6]),
                            Integer.parseInt(parts[7])
                    );
                    trades.add(t);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading file: " + e.getMessage());
        }
    }

    public static void deleteTradeAt(int index) {
        if (index >= 0 && index < trades.size()) {
            trades.remove(index);
            saveToFile();
        }
    }

    public static void updateTradeAt(int index, Trade updatedTrade) {
        if (index >= 0 && index < trades.size()) {
            trades.set(index, updatedTrade);
            saveToFile();
        }
    }

    private static String escape(String text) {
        return text == null ? "" : text.replace("\\", "\\\\").replace(",", "\\,");
    }

    private static String unescape(String text) {
        return text == null ? "" : text.replace("\\,", ",").replace("\\\\", "\\");
    }

    private static String[] splitCsv(String line) {
        ArrayList<String> parts = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean escape = false;

        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);

            if (escape) {
                current.append(ch);
                escape = false;
            } else if (ch == '\\') {
                escape = true;
            } else if (ch == ',') {
                parts.add(current.toString());
                current.setLength(0);
            } else {
                current.append(ch);
            }
        }

        parts.add(current.toString());
        return parts.toArray(new String[0]);
    }
}