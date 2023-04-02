package repositories;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import domain.Transaction;
import exceptions.RecordNotStored;
import exceptions.TransactionNotFound;

public class TransactionRepository implements IRepository<Transaction> {
    private String fileDB;

    public TransactionRepository(String fileDB) {
        try {
            DriverManager.registerDriver(new org.sqlite.JDBC());
            this.fileDB = "jdbc:sqlite:"+fileDB;
            this.connect();
        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }
    }

    @Override
    public void connect() {
        Connection connection = null;
        try{
            connection = DriverManager.getConnection(this.fileDB);
            if (connection != null) {
                DatabaseMetaData meta = connection.getMetaData();
                System.out.println("Database connected: " + meta.getDatabaseProductName());
                System.out.println("The driver name is " + meta.getDriverName());
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }finally{
            this.disconnect(connection);
        }
    }

    public void disconnect(Connection connection) {
        try {
            if(connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }
    }

    @Override
    public void executeQuery(String query) {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(this.fileDB);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            statement.executeUpdate(query);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }finally{
            this.disconnect(connection);
        }
    }

    @Override
    public void createTable() {
        this.executeQuery(Transaction.DDL);
    }

    @Override
    public void removeTable() {
        this.executeQuery("DROP TABLE IF EXISTS TRANSACCIONES");
    }

    @Override
    public void insert(Transaction entity) throws RecordNotStored {
        Connection connection = null;
        String sql = "INSERT INTO TRANSACCIONES(FECHA,HORA,TIPO_TRANSACCION,MONTO,TIPO_CUENTA_DESTINO,ID_CUENTA) VALUES(?,?,?,?,?,?)";
        try {
            connection = DriverManager.getConnection(this.fileDB);
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, entity.getDate());
            pstmt.setString(2, entity.getHour());
            pstmt.setString(3, entity.getTransactionType());
            pstmt.setDouble(4, entity.getBalance());
            pstmt.setString(5, entity.getDestinationAccountType());
            pstmt.setInt(6, entity.getAccountID());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RecordNotStored(e.getMessage());
        }finally{
            this.disconnect(connection);
        }
    }

    @Override
    public List<Transaction> selectAll() {
        Connection connection = null;
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM TRANSACCIONES";
        try {
            connection = DriverManager.getConnection(this.fileDB);
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Transaction transaction = new Transaction(
                        rs.getString("FECHA"),
                        rs.getString("HORA"),
                        rs.getString("TIPO_TRANSACCION"),
                        rs.getDouble("MONTO"),
                        rs.getString("TIPO_CUENTA_DESTINO"),
                        rs.getInt("ID_CUENTA")
                );
                transactions.add(transaction);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }finally{
            this.disconnect(connection);
        }
        return transactions;
    }

    @Override
    public Transaction selectById(int id) throws TransactionNotFound {
        Transaction transaction = null;
        Connection connection = null;
        String sql = "SELECT * FROM TRANSACCIONES WHERE ID = ?";
        try {
            connection = DriverManager.getConnection(this.fileDB);
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, id);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                transaction = new Transaction(
                        rs.getString("FECHA"),
                        rs.getString("HORA"),
                        rs.getString("TIPO_TRANSACCION"),
                        rs.getDouble("MONTO"),
                        rs.getString("TIPO_CUENTA_DESTINO"),
                        rs.getInt("ID_CUENTA")
                );
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }finally{
            this.disconnect(connection);
        }
        if(transaction == null) {
            throw new TransactionNotFound("La transacción no existe");
        }else{
            return transaction;
        }
    }

    public List<Transaction> selectByAccount(int id) {
        Connection connection = null;
        List<Transaction> cuentas = new ArrayList<>();
        String sql = "SELECT * FROM TRANSACCIONES WHERE ID_CUENTA = ?";
        try {
            connection = DriverManager.getConnection(this.fileDB);
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Transaction transaction = new Transaction(
                        rs.getString("FECHA"),
                        rs.getString("HORA"),
                        rs.getString("TIPO_TRANSACCION"),
                        rs.getDouble("MONTO"),
                        rs.getString("TIPO_CUENTA_DESTINO"),
                        rs.getInt("ID_CUENTA")
                );
                cuentas.add(transaction);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }finally{
            this.disconnect(connection);
        }
        return cuentas;
    }

    @Override
    public void update(Transaction entity, int id) throws TransactionNotFound {
        int response = 0;
        Connection connection = null;
        String sql = "UPDATE TRANSACCIONES SET FECHA=?, HORA=?, TIPO_TRANSACCION=?, MONTO=?, TIPO_CUENTA_DESTINO=?, ID_CUENTA=?  WHERE ID=?";
        try {
            connection = DriverManager.getConnection(this.fileDB);
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, entity.getDate());
            pstmt.setString(2, entity.getHour());
            pstmt.setString(3, entity.getTransactionType());
            pstmt.setDouble(4, entity.getBalance());
            pstmt.setString(5, entity.getDestinationAccountType());
            pstmt.setInt(6, entity.getAccountID());
            pstmt.setInt(7, id);
            response = pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }finally{
            this.disconnect(connection);
        }
        if(response == 0){
            throw new TransactionNotFound("Transacción no encontrada");
        }
    }

    @Override
    public void delete(int id) throws TransactionNotFound {
        int response = 0;
        Connection connection = null;
        String sql = "DELETE FROM TRANSACCIONES WHERE ID = ?";
        try {
            connection = DriverManager.getConnection(this.fileDB);
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, id);
            response = pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }finally{
            this.disconnect(connection);
        }
        if(response == 0){
            throw new TransactionNotFound("Transacción no encontrada");
        }
    }

}

