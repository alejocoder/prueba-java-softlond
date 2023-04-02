package services;

import java.util.List;

import domain.User;
import exceptions.RecordNotStored;
import exceptions.UserNotFound;
import repositories.IRepository;

public class UserService implements IService<User> {
    private IRepository<User> userRepository;

    public UserService(IRepository<User> userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void createDDL() {
        this.userRepository.createTable();
    }

    @Override
    public void removeDDL() {
        this.userRepository.removeTable();
    }

    @Override
    public boolean save(User entity) {
        try {
            this.userRepository.insert(entity);
            return true;
        } catch (RecordNotStored e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    @Override
    public List<User> getAll() {
        return this.userRepository.selectAll();
    }

    @Override
    public User getOne(int id) {
        try {
            return this.userRepository.selectById(id);
        } catch (UserNotFound e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Override
    public boolean update(User entity, int id) {
        try {
            this.userRepository.update(entity, id);
            return true;
        } catch (UserNotFound e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    @Override
    public boolean remove(int id) {
        try {
            this.userRepository.delete(id);
            return true;
        } catch (UserNotFound e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

}
