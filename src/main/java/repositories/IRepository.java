package repositories;

import java.util.List;

import exceptions.AccountNotFound;
import exceptions.RecordNotStored;
import exceptions.TransactionNotFound;
import exceptions.UserNotFound;

public interface IRepository<T> {
    final static String FILE_DB = "banco.db";
    void connect();
    void createTable();
    void removeTable();
    void executeQuery(String query);
    List<T> selectAll();
    void insert(T entity) throws RecordNotStored;
    T selectById(int id) throws AccountNotFound, UserNotFound, TransactionNotFound;
    void update(T entity , int id) throws AccountNotFound, UserNotFound, TransactionNotFound;
    void delete(int id) throws AccountNotFound, UserNotFound, TransactionNotFound;
}

