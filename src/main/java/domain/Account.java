package domain;

public class Account {
    protected String numberAccount;
    protected double balance;
    protected String typeAccount;
    protected int userId;

    public static final String DDL = String.join("\n",
            "CREATE TABLE IF NOT EXISTS CUENTAS(",
            "ID INTEGER PRIMARY KEY AUTOINCREMENT,",
            "NUMERO_CUENTA TEXT NOT NULL UNIQUE,",
            "SALDO REAL NOT NULL,",
            "TIPO_CUENTA TEXT NOT NULL,",
            "ID_USUARIO INTEGER NOT NULL,",
            "FOREIGN KEY(ID_USUARIO) REFERENCES USUARIOS(ID)",
            "ON UPDATE CASCADE ON DELETE CASCADE",
            ");"
    );

    public Account() {}

    public Account(String numberAccount, double balance, String typeAccount, int userId) {
        this.numberAccount = numberAccount;
        this.balance = balance;
        this.typeAccount = typeAccount;
        this.userId = userId;
    }

    public void withdrawMoney(double amount) {
        if(amount > this.balance) {
            System.out.println("No tiene saldo suficiente");
        }else {
            this.balance -= amount;
        }
    }

    public void depositMoney(double amount) {
        this.balance += amount;
    }

    public String getNumberAccount() {
        return numberAccount;
    }

    public void setNumberAccount(String numberAccount) {
        this.numberAccount = numberAccount;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getTypeAccount() {
        return typeAccount;
    }

    public void setTypeAccount(String typeAccount) {
        this.typeAccount = typeAccount;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public static String getDdl() {
        return DDL;
    }

    @Override
    public String toString() {
        return "Account [numberAccount=" + numberAccount + ", balance=" + balance + ", typeAccount=" + typeAccount
                + ", userId=" + userId + "]";
    }


}
