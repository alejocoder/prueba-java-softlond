package domain;

public class User {
    protected String name;
    protected String lastName;
    protected String identification;

    public static final String DDL = String.join("\n",
            "CREATE TABLE IF NOT EXISTS USUARIOS(",
            "ID INTEGER PRIMARY KEY AUTOINCREMENT,",
            "NOMBRE TEXT NOT NULL,",
            "APELLIDO TEXT NOT NULL,",
            "CEDULA TEXT NOT NULL UNIQUE);"
    );

    public User(){}

    public User(String name, String lastName, String identification) {
        this.name = name;
        this.lastName = lastName;
        this.identification = identification;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getIdentification() {
        return identification;
    }

    public void setIdentification(String identification) {
        this.identification = identification;
    }

    public static String getDdl() {
        return DDL;
    }

    @Override
    public String toString() {
        return "User [name=" + name + ", lastName=" + lastName + ", identification=" + identification + "]";
    }



}
