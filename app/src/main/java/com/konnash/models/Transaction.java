package com.konnash.models;

public class Transaction {

    public static final String TYPE_INCOME  = "income";
    public static final String TYPE_EXPENSE = "expense";

    private long   id;
    private String type;          // "income" or "expense"
    private double amount;
    private double balanceAfter;  // running balance after this tx
    private long   timestamp;     // epoch millis

    // ---------- constructors ----------

    public Transaction() {}

    public Transaction(String type, double amount, double balanceAfter, long timestamp) {
        this.type         = type;
        this.amount       = amount;
        this.balanceAfter = balanceAfter;
        this.timestamp    = timestamp;
    }

    // ---------- getters / setters ----------

    public long getId()                      { return id; }
    public void setId(long id)               { this.id = id; }

    public String getType()                  { return type; }
    public void setType(String type)         { this.type = type; }

    public double getAmount()                { return amount; }
    public void setAmount(double amount)     { this.amount = amount; }

    public double getBalanceAfter()              { return balanceAfter; }
    public void setBalanceAfter(double b)        { this.balanceAfter = b; }

    public long getTimestamp()               { return timestamp; }
    public void setTimestamp(long ts)        { this.timestamp = ts; }

    public boolean isIncome()  { return TYPE_INCOME.equals(type); }
    public boolean isExpense() { return TYPE_EXPENSE.equals(type); }
}
