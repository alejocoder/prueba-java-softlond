package services;

import java.util.List;

public interface IService<T> {
    public void createDDL();
    public void removeDDL();
    public boolean save(T entity);
    public List<T> getAll();
    public T getOne(int id);
    public boolean update(T entity, int id);
    public boolean remove(int id);
}