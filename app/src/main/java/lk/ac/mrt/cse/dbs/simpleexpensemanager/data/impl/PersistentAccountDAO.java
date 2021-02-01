package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.database.DbHandler;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistentAccountDAO implements AccountDAO {
    private DbHandler database;

    public PersistentAccountDAO(DbHandler database) {
        this.database = database;
    }
    @Override
    public List<String> getAccountNumbersList() {
        SQLiteDatabase db = database.getReadableDatabase();
        String query="SELECT " + database.getAccountNo() + " FROM " + database.getTableName();
        Cursor cursor = db.rawQuery(query,null);
        ArrayList<String> accountNumbers = new ArrayList<>();

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            accountNumbers.add(cursor.getString(0));
        }

        cursor.close();
        return accountNumbers;
    }

    @Override
    public List<Account> getAccountsList() {
        SQLiteDatabase db = database.getReadableDatabase();
        String SELECT_QUERY = "SELECT * FROM "+database.getTableName();

        ArrayList<Account> accounts = new ArrayList<>();
        Cursor cursor = db.rawQuery(SELECT_QUERY,null);

        if(cursor.moveToFirst()){
            do{
                Account account = new Account(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getDouble(3));
//                appUsage.setApp_name(cursor.getString(0));
//                accounts.setDate(cursor.getString(0));
//                appUsage.setUsage(cursor.getInt(1));
                accounts.add(account);

            }while(cursor.moveToNext());
        }

        return accounts;

    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase db = database.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + database.getTableName() + " WHERE " + database.getAccountNo()+ "=?;", new String[]{accountNo});
        Account account;
        if (cursor != null && cursor.moveToFirst()) {

            account = new Account(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getDouble(3));

        } else {
            throw new InvalidAccountException("Invalid Account");
        }
        cursor.close();
        return account;
    }

    @Override
    public void addAccount(Account account) {
        SQLiteDatabase db = database.getReadableDatabase();


        Cursor cursor = db.rawQuery("SELECT * FROM "
                + database.getTableName() + " WHERE "
                + database.getAccountNo()+" =?;", new String[]{account.getAccountNo()});
        if (cursor != null && cursor.moveToFirst()) {

            String accountNumber = cursor.getString(0);

        } else {
            SQLiteDatabase sql = database.getWritableDatabase();
            account.getBankName().replace("'"," ");
            String INSERT_QUERY = "INSERT INTO " + database.getTableName() + " "
                    + "(" + database.getAccountNo()
                    +" ,"+database.getBankName()
                    +" ,"+database.getHolderName()
                    +" ,"+database.getBalance()
                    +" )"
                    + " VALUES ('"
                    + account.getAccountNo() + "', '"
                    + account.getBankName() + "', '"
                    + account.getAccountHolderName()+ "', '"
                    + account.getBalance() +"' )";

            sql.execSQL(INSERT_QUERY);
            sql.close();
        }
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase db = database.getWritableDatabase();
       //get the array of accounts which needs to be deleted
        Cursor cursor = db.rawQuery("SELECT * FROM " + database.getTableName() + " WHERE " + database.getAccountNo() + "=?;", new String[]{accountNo});

        if (cursor.moveToFirst()) {
            //remove the accounts from the database
            db.delete(
                    database.getTableName(),
                    database.getAccountNo() + " = ?",
                    new String[]{accountNo}
            );
        } else {
            throw new InvalidAccountException("Invalid Account");
        }
        cursor.close();
    }

    @Override
    public boolean updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        SQLiteDatabase db = database.getWritableDatabase();

        Account account = this.getAccount(accountNo);

        if (account != null) {

            switch (expenseType) {
                case EXPENSE:
                    account.setBalance(account.getBalance() - amount);
                    break;
                case INCOME:
                    account.setBalance(account.getBalance() + amount);
                    break;
            }

            if(account.getBalance()<0){
                throw new InvalidAccountException("Insufficient Credit");
            }
            else {
                db.execSQL("UPDATE " + database.getTableName() + " SET " + database.getBalance() + " = ?" + " WHERE " + database.getAccountNo() + " = ?",
                        new String[]{Double.toString(account.getBalance()), accountNo});
                return true;
            }

        } else {
            throw new InvalidAccountException("Invalid Account");
        }

    }
}