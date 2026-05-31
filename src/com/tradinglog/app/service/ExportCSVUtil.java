package com.tradinglog.app.service;

import com.tradinglog.app.model.Trade;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ExportCSVUtil {

    public static void exportTradesToCSV(List<Trade> trades) {
        try (FileWriter writer = new FileWriter("trades_export.csv")) {

            writer.append("ID,Symbol,Side,Strategy,Remarks,Trade Date,Entry Price,Exit Price,Quantity,Live Price,Profit,Live Profit\n");

            for (Trade trade : trades) {
                writer.append(String.valueOf(trade.getId())).append(",");
                writer.append(csv(trade.getSymbol())).append(",");
                writer.append(csv(trade.getSide())).append(",");
                writer.append(csv(trade.getStrategy())).append(",");
                writer.append(csv(trade.getRemarks())).append(",");
                writer.append(csv(trade.getTradeDate())).append(",");
                writer.append(String.valueOf(trade.getEntryPrice())).append(",");
                writer.append(String.valueOf(trade.getExitPrice())).append(",");
                writer.append(String.valueOf(trade.getQuantity())).append(",");
                writer.append(String.valueOf(trade.getLivePrice())).append(",");
                writer.append(String.valueOf(trade.calculateProfit())).append(",");
                writer.append(String.valueOf(trade.calculateLiveProfit())).append("\n");
            }

            System.out.println("CSV Export Successful");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String csv(String value) {
        if (value == null) {
            return "\"\"";
        }
        String escaped = value.replace("\"", "\"\"");
        return "\"" + escaped + "\"";
    }
}