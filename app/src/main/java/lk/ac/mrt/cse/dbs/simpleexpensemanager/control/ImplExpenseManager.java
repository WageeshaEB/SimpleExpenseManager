package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;

import android.content.Context;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.ExpenseManager;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.exception.ExpenseManagerException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.ImplAccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.ImplTransactionDAO;

public class ImplExpenseManager extends ExpenseManager {

    private Context context;

    public ImplExpenseManager(Context context){
        this.context = context;
        try {
            setup();
        } catch (ExpenseManagerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setup() throws ExpenseManagerException {
        TransactionDAO persistentTransactionDAO = new ImplTransactionDAO(context);
        setTransactionsDAO(persistentTransactionDAO);

        AccountDAO persistentAccountDAO = new ImplAccountDAO(context);
        setAccountsDAO(persistentAccountDAO);
    }
}
