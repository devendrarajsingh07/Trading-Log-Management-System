package com.tradinglog.app.model;

public class Trade {

    private int id;
    private String symbol;
    private String side;
    private String strategy;
    private String remarks;
    private String tradeDate;
    private double entryPrice;
    private double exitPrice;
    private int quantity;
    private double livePrice;

    public Trade() {
    }

    public Trade(String symbol, String side, String strategy, String remarks,
                 String tradeDate, double entryPrice, double exitPrice, int quantity) {
        this(0, symbol, side, strategy, remarks, tradeDate, entryPrice, exitPrice, quantity, 0.0);
    }

    public Trade(String symbol, String side, String strategy, String remarks,
                 String tradeDate, double entryPrice, double exitPrice, int quantity, double livePrice) {
        this(0, symbol, side, strategy, remarks, tradeDate, entryPrice, exitPrice, quantity, livePrice);
    }

    public Trade(int id, String symbol, String side, String strategy, String remarks,
                 String tradeDate, double entryPrice, double exitPrice, int quantity) {
        this(id, symbol, side, strategy, remarks, tradeDate, entryPrice, exitPrice, quantity, 0.0);
    }

    public Trade(int id, String symbol, String side, String strategy, String remarks,
                 String tradeDate, double entryPrice, double exitPrice, int quantity, double livePrice) {
        this.id = id;
        this.symbol = symbol;
        this.side = side;
        this.strategy = strategy;
        this.remarks = remarks;
        this.tradeDate = tradeDate;
        this.entryPrice = entryPrice;
        this.exitPrice = exitPrice;
        this.quantity = quantity;
        this.livePrice = livePrice;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getSide() {
        return side;
    }

    public String getStrategy() {
        return strategy;
    }

    public String getRemarks() {
        return remarks;
    }

    public String getTradeDate() {
        return tradeDate;
    }

    public double getEntryPrice() {
        return entryPrice;
    }

    public double getExitPrice() {
        return exitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getLivePrice() {
        return livePrice;
    }

    public void setLivePrice(double livePrice) {
        this.livePrice = livePrice;
    }

    public double calculateProfit() {
        if ("BUY".equalsIgnoreCase(side)) {
            return (exitPrice - entryPrice) * quantity;
        } else {
            return (entryPrice - exitPrice) * quantity;
        }
    }

    public double calculateLiveProfit() {
        if (livePrice <= 0) {
            return 0;
        }

        if ("BUY".equalsIgnoreCase(side)) {
            return (livePrice - entryPrice) * quantity;
        } else {
            return (entryPrice - livePrice) * quantity;
        }
    }
}