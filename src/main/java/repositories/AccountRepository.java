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

import domain.Account;
import exceptions.AccountNotFound;
import exceptions.RecordNotStored;

public class AccountRepository implements IRepository<Account> {
    private String fileDB;

    public AccountRepository(String fileDB) {
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
    public void insert(Account entity) throws RecordNotStored {
        Connection connection = null;
        String sql = "INSERT INTO CUENTAS(NUMERO_CUENTA, SALDO, TIPO_CUENTA, ID_USUARIO) VALUES(?,?,?,?)";
        try {
            connection = DriverManager.getConnection(this.fileDB);
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, entity.getNumberAccount());
            pstmt.setDouble(2, entity.getBalance());
            pstmt.setString(3, entity.getTypeAccount());
            pstmt.setInt(4, entity.getUserId());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RecordNotStored(e.getMessage());
        }finally{
            this.disconnect(connection);
        }
    }

    @Override
    public List<Account> selectAll() {
        Connection connection = null;
        List<Account> cuentas = new ArrayList<>();
        String sql = "SELECT * FROM CUENTAS";
        try {
            connection = DriverManager.getConnection(this.fileDB);
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Account cuenta = new Account(
                        rs.getString("NUMERO_CUENTA"),
                        rs.getDouble("SALDO"),
                        rs.getString("TIPO_CUENTA"),
                        rs.getInt("ID_USUARIO")
                );
                cuentas.add(cuenta);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }finally{
            this.disconnect(connection);
        }
        return cuentas;
    }

    @Override
    public Account selectById(int id) throws AccountNotFound {
        Account cuenta = null;
        Connection connection = null;
        String sql = "SELECT * FROM CUENTAS WHERE ID = ?";
        try {
            connection = DriverManager.getConnection(this.fileDB);
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, id);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                cuenta = new Account(
                        rs.getString("NUMERO_CUENTA"),
                        rs.getDouble("SALDO"),
                        rs.getString("TIPO_CUENTA"),
                        rs.getInt("ID_USUARIO")
                );
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }finally{
            this.disconnect(connection);
        }
        if(cuenta == null) {
            throw new AccountNotFound("La cuenta no existe");
        }else{
            return cuenta;
        }
    }

    public List<Account> selectByUser(int id) {
        Connection connection = null;
        List<Account> cuentas = new ArrayList<>();
        String sql = "SELECT * FROM CUENTAS WHERE ID_USUARIO = ?";
        try {
            connection = DriverManager.getConnection(this.fileDB);
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Account cuenta = new Account(
                        rs.getString("NUMERO_CUENTA"),
                        rs.getDouble("SALDO"),
                        rs.getString("TIPO_CUENTA"),
                        rs.getInt("ID_USUARIO")
                );
                cuentas.add(cuenta);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }finally{
            this.disconnect(connection);
        }
        return cuentas;
    }

    @Override
    public void update(Account entity, int id) throws AccountNotFound {
        int response = 0;
        Connection connection = null;
        String sql = "UPDATE CUENTAS SET NUMERO_CUENTA=?, SALDO=?, TIPO_CUENTA=?, ID_USUARIO=? WHERE ID=?";
        try {
            connection = DriverManager.getConnection(this.fileDB);
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, entity.getNumberAccount());
            pstmt.setDouble(2, entity.getBalance());
            pstmt.setString(3, entity.getTypeAccount());
            pstmt.setInt(4, entity.getUserId());
            pstmt.setInt(5, id);
            response = pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }finally{
            this.disconnect(connection);
        }
        if(response == 0){
            throw new AccountNotFound("Cuenta no encontrada");
        }
    }

    @Override
    public void delete(int id) throws AccountNotFound {
        int response = 0;
        Connection connection = null;
        String sql = "DELETE FROM CUENTAS WHERE ID = ?";
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
            throw new AccountNotFound("Cuenta no encontrada");
        }
    }

    @Override
    public void createTable(){
        this.executeQuery(Account.DDL);
    }

    @Override
    public void removeTable(){
        this.executeQuery("DROP TABLE IF EXISTS CUENTAS");
    }
}

