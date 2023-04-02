package domain;

public class Transaction {
    protected String date;
    protected String hour;
    protected String transactionType;
    protected double balance;
    protected String destinationAccountType;
    protected int accountID;

    public static final String DDL = String.join("\n",
            "CREATE TABLE IF NOT EXISTS TRANSACCIONES(",
            "ID INTEGER PRIMARY KEY AUTOINCREMENT,",
            "FECHA TEXT NOT NULL,",
            "HORA TEXT NOT NULL,",
            "TIPO_TRANSACCION TEXT NOT NULL,",
            "MONTO REAL NOT NULL,",
            "ID_CUENTA INTEGER NOT NULL,",
            "TIPO_CUENTA_DESTINO TEXT,",
            "FOREIGN KEY(ID_CUENTA) REFERENCES CUENTAS(ID)",
            "ON UPDATE CASCADE ON DELETE CASCADE",
            ");"
    );

    public Transaction() {}

    public Transaction(String date, String hour, String transactionType, double balance, String destinationAccountType,
                       int accountID) {
        this.date = date;
        this.hour = hour;
        this.transactionType = transactionType;
        this.balance = balance;
        this.destinationAccountType = destinationAccountType;
        this.accountID = accountID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getDestinationAccountType() {
        return destinationAccountType;
    }

    public void setDestinationAccountType(String destinationAccountType) {
        this.destinationAccountType = destinationAccountType;
    }

    public int getAccountID() {
        return accountID;
    }

    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }

    public static String getDdl() {
        return DDL;
    }

    @Override
    public String toString() {
        return "Transaction [date=" + date + ", hour=" + hour + ", transactionType=" + transactionType + ", balance="
                + balance + ", destinationAccountType=" + destinationAccountType + ", accountID=" + accountID + "]";
    }

}
