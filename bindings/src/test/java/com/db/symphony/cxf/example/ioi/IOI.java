package com.db.symphony.cxf.example.ioi;

public class IOI {

    private Action action;
    private String symbol;
    private String shares;
    private String sender;
    private String target;

    public IOI(Action action, String symbol, String shares, String sender, String target) {
        this.action = action;
        this.symbol = symbol;
        this.shares = shares;
        this.sender = sender;
        this.target = target;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getShares() {
        return shares;
    }

    public void setShares(String shares) {
        this.shares = shares;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}
