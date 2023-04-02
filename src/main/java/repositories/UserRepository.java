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

import domain.User;
import exceptions.RecordNotStored;
import exceptions.UserNotFound;

public class UserRepository implements IRepository<User> {
    private String fileDB;

    public UserRepository(String fileDB) {
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
        this.executeQuery(User.DDL);
    }

    @Override
    public void removeTable() {
        this.executeQuery("DROP TABLE IF EXISTS USUARIOS");
    }

    @Override
    public void insert(User entity) throws RecordNotStored {
        Connection connection = null;
        String sql = "INSERT INTO USUARIOS(NOMBRE, APELLIDO, CEDULA) VALUES(?,?,?)";
        try {
            connection = DriverManager.getConnection(this.fileDB);
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, entity.getName());
            pstmt.setString(2, entity.getLastName());
            pstmt.setString(3, entity.getIdentification());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RecordNotStored(e.getMessage());
        }finally{
            this.disconnect(connection);
        }
    }

    @Override
    public List<User> selectAll() {
        Connection connection = null;
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM USUARIOS";
        try {
            connection = DriverManager.getConnection(this.fileDB);
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                User user = new User(
                        rs.getString("NOMBRE"),
                        rs.getString("APELLIDO"),
                        rs.getString("CEDULA")
                );
                users.add(user);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }finally{
            this.disconnect(connection);
        }
        return users;
    }

    @Override
    public User selectById(int id) throws UserNotFound {
        User user = null;
        Connection connection = null;
        String sql = "SELECT * FROM USUARIOS WHERE CEDULA = ?";
        try {
            connection = DriverManager.getConnection(this.fileDB);
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, id + "");

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                user = new User(
                        rs.getString("NOMBRE"),
                        rs.getString("APELLIDO"),
                        rs.getString("CEDULA")
                );
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }finally{
            this.disconnect(connection);
        }
        if(user == null) {
            throw new UserNotFound("El usuario no existe");
        }else{
            return user;
        }
    }

    @Override
    public void update(User entity, int id) throws UserNotFound {
        int response = 0;
        Connection connection = null;
        String sql = "UPDATE USUARIOS SET NOMBRE=?, APELLIDO=?, CEDULA=? WHERE CEDULA=?";
        try {
            connection = DriverManager.getConnection(this.fileDB);
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, entity.getName());
            pstmt.setString(2, entity.getLastName());
            pstmt.setString(3, entity.getIdentification());
            pstmt.setInt(4, id);
            response = pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }finally{
            this.disconnect(connection);
        }
        if(response == 0){
            throw new UserNotFound("Usuario no encontrado");
        }
    }

    @Override
    public void delete(int id) throws UserNotFound {
        int response = 0;
        Connection connection = null;
        String sql = "DELETE FROM USUARIOS WHERE CEDULA = ?";
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
            throw new UserNotFound("Usuario no encontrada");
        }
    }
}

