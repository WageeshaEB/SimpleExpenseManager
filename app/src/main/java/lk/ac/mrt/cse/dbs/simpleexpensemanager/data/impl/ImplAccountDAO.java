package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.DataBaseHelper;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class ImplAccountDAO implements AccountDAO {

    private DataBaseHelper sqlhelper;

    public ImplAccountDAO(Context context){
        sqlhelper = new DataBaseHelper(context);
    }

    @Override
    public List<String> getAccountNumbersList() {
        ArrayList<String> accountNumberArr = new ArrayList<>();
        ArrayList<Account> accountArr = sqlhelper.getAllAccounts();

        if(accountArr.size()!=0){
            for (Account acc:accountArr){
                accountNumberArr.add(acc.getAccountNo());
            }
        }
        return accountNumberArr;
    }

    @Override
    public List<Account> getAccountsList() {
        return sqlhelper.getAllAccounts();
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        return sqlhelper.getAccount(accountNo);
    }

    @Override
    public void addAccount(Account account) {
        sqlhelper.insertAccount(account);
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        sqlhelper.deleteAccount(accountNo);
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        Account account = sqlhelper.getAccount(accountNo);

        double balance = account.getBalance();

        if(expenseType == ExpenseType.INCOME){
            account.setBalance(balance+amount);
        }
        else if (expenseType == ExpenseType.EXPENSE){
            account.setBalance(balance-amount);
        }


        if(account.getBalance()<0 ){
            throw new InvalidAccountException("Credit limit exeeded!!!");
        }
        else{
            sqlhelper.updateAccount(account);
        }
    }
}
