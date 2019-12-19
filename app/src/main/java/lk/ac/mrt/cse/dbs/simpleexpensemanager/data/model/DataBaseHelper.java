package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DataBaseHelper extends SQLiteOpenHelper {

    static final String DATABASE_NAME = "170066V";
    public  static final String account_table = "account_table";
    public  static final String transaction_table = "transaction_table";

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 2);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+account_table+" (accountNo TEXT(50) PRIMARY KEY,bankName TEXT(50),accountHolderName TEXT(50),balance REAL) ");
        db.execSQL("create table "+transaction_table+" (accountNo TEXT(50) ,date date, expenseType TEXT(20),amount REAL,FOREIGN KEY (accountNo) REFERENCES "+account_table+"(accountNo))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS "+account_table);
        db.execSQL("DROP TABLE IF EXISTS "+transaction_table);
        onCreate(db);
    }

    public void insertAccount(Account account){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("accountNo",account.getAccountNo());
        values.put("bankName",account.getBankName());
        values.put("accountHolderName",account.getAccountHolderName());
        values.put("balance",account.getBalance());

        db.insert(account_table,null,values);

        db.close();

    }

    public boolean updateAccount(Account account){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("accountNo",account.getAccountNo());
        values.put("bankName",account.getBankName());
        values.put("accountHolderName",account.getAccountHolderName());
        values.put("balance",account.getBalance());


        long result = db.update(account_table,values,"accountNo = ?",new String[]{account.getAccountNo()});
        if(result == -1){
            return false;
        }else{
            return true;
        }
    }

    public Account getAccount(String accountNo){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery( "SELECT * FROM "+account_table+" WHERE accountNo = ? ",new String[]{accountNo});

        if (cursor!=null)
            cursor.moveToFirst();

        Account account = new Account(
                cursor.getString(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getDouble(3));

        return account;
    }

    public ArrayList<Account> getAllAccounts(){
        ArrayList<Account> accountArr = new ArrayList<Account>();

        String selectQuery = "SELECT * FROM "+account_table;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery , null);

        if (cursor.moveToFirst()){
            do {
                String accountNo = cursor.getString(0);
                String bankName = cursor.getString(1);
                String accountHolderName = cursor.getString(2);
                double balance = cursor.getDouble(3);

                accountArr.add(new Account(accountNo , bankName , accountHolderName , balance));

            }while (cursor.moveToNext());
        }

        return accountArr;
    }

    public boolean deleteAccount(String accountNo){
        SQLiteDatabase db = this.getWritableDatabase();

        int result = db.delete(account_table,"accountNo = "+accountNo,null);

        if(result >0){
            return true;
        }else{
            return false;
        }

    }




    /////////////transaction metods

    public void logTransaction(Transaction transaction){
        SQLiteDatabase db = this.getWritableDatabase();

        DateFormat format = new SimpleDateFormat("m-d-yyyy", Locale.ENGLISH);

        ContentValues values = new ContentValues();
        values.put("accountNo" , transaction.getAccountNo());
        values.put("date" , format.format(transaction.getDate()));
        values.put("expenseType" , transaction.getExpenseType().toString());
        values.put("amount" , transaction.getAmount());

        db.insert(transaction_table,null,values);
    }

    public ArrayList<Transaction> getAllTransactionLogs(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+transaction_table,null);
        ArrayList<Transaction> result = assignTransaction(cursor);
        return  result;
    }

    public ArrayList<Transaction> getPaginatedTransactionLogs(int limit){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+transaction_table+" LIMIT "+limit,null);
        ArrayList<Transaction> result = assignTransaction(cursor);
        return  result;
    }





    public ArrayList<Transaction> assignTransaction(Cursor cursor){
        ArrayList<Transaction> transationArr = new ArrayList<>();
        DateFormat format = new SimpleDateFormat("m-d-yyyy", Locale.ENGLISH);

        if (cursor.getCount()!=0){
            while (cursor.moveToNext()){
                String accountNo = cursor.getString(0);
                Date date = new Date();
                ExpenseType expenseType = ExpenseType.INCOME;

                try{
                    date= format.parse(cursor.getString(1));
                }
                catch (ParseException e){e.printStackTrace();}

                if (cursor.getString(2)=="INCOME"){
                    expenseType = ExpenseType.INCOME;
                }
                else if(cursor.getString(2) == "EXPENSE"){
                    expenseType = ExpenseType.EXPENSE;
                }

                double amount = cursor.getDouble(3);

                transationArr.add(new Transaction(date , accountNo , expenseType , amount));
            }
        }
        return transationArr;
    }



}
