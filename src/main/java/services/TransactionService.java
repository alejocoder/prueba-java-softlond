package services;

import java.util.List;

import domain.Account;
import domain.Transaction;
import exceptions.RecordNotStored;
import exceptions.TransactionNotFound;
import repositories.IRepository;
import repositories.TransactionRepository;

public class TransactionService implements IService<Transaction> {
    private IRepository<Transaction> transactionRepository;
    private IRepository<Account> accountRepository;

    public TransactionService(IRepository<Transaction> transactionRepository, IRepository<Account> accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    public void createDDL() {
        this.transactionRepository.createTable();
    }

    @Override
    public void removeDDL() {
        this.transactionRepository.removeTable();
    }

    @Override
    public boolean save(Transaction entity) {
        try {
            if(entity.getTransactionType().equals("deposito")){
                Account account = this.accountRepository.selectById(entity.getAccountID());
                account.setBalance(account.getBalance() + entity.getBalance());
                this.accountRepository.update(account, entity.getAccountID());
            }else if(entity.getTransactionType().equals("retiro")){
                Account account = this.accountRepository.selectById(entity.getAccountID());
                account.setBalance(account.getBalance() - entity.getBalance());
                this.accountRepository.update(account, entity.getAccountID());
            }
            this.transactionRepository.insert(entity);
            return true;
        } catch (RecordNotStored e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    @Override
    public List<Transaction> getAll() {
        return this.transactionRepository.selectAll();
    }

    public List<Transaction> getByAccount(int id) {
        return ((TransactionRepository) this.transactionRepository).selectByAccount(id);
    }

    @Override
    public Transaction getOne(int id) {
        try {
            return this.transactionRepository.selectById(id);
        } catch (TransactionNotFound e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Override
    public boolean update(Transaction entity, int id) {
        try {
            this.transactionRepository.update(entity, id);
            return true;
        } catch (TransactionNotFound e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    @Override
    public boolean remove(int id) {
        try {
            this.transactionRepository.delete(id);
            return true;
        } catch (TransactionNotFound e) {
            System.out.println(e.getMessage());
        }
        return false;
    }
}
