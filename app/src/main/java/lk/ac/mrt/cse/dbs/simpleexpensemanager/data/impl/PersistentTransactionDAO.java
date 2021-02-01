package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.database.DbHandler;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistentTransactionDAO implements TransactionDAO {
    private DbHandler database;
    private DateFormat dateFormat;
    public PersistentTransactionDAO(DbHandler database) {
        this.database = database;
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        SQLiteDatabase db= database.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(database.getTransactionDate(), this.dateFormat.format(date));
        contentValues.put(database.getAccountNo(), accountNo);
        contentValues.put(database.getTransactionType(), String.valueOf(expenseType));
        contentValues.put(database.getTransactionAmount(), amount);

        //insert new row to Trans table
        db.insert(database.getTransactionTableName(), null, contentValues);

    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        SQLiteDatabase db = database.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + database.getTransactionTableName() + " ORDER BY " + database.getTransactionDate() + " DESC ",null);

        ArrayList<Transaction> transactions = new ArrayList<>();
        if(cursor.moveToFirst()){
            do {
                // Create new Transaction object
                Transaction transaction= new Transaction();
                try {
                    transaction.setDate(this.dateFormat.parse(cursor.getString(0)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                transaction.setAccountNo(cursor.getString(1));
                transaction.setExpenseType(ExpenseType.valueOf(cursor.getString(2).toUpperCase()));
                transaction.setAmount(cursor.getDouble(3));


                transactions.add(transaction);
            }while (cursor.moveToNext());
        }

        cursor.close();
        return transactions;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {

        SQLiteDatabase db = database.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + database.getTransactionTableName() + " ORDER BY " + database.getTransactionDate() + " DESC " +" LIMIT ?;", new String[]{Integer.toString(limit)});

        ArrayList<Transaction> transactions = new ArrayList<>();
        if(cursor.moveToFirst()){
            do {
                // Create new Transaction object
                Transaction transaction= new Transaction();
                try {
                    transaction.setDate(this.dateFormat.parse(cursor.getString(0)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                transaction.setAccountNo(cursor.getString(1));
                transaction.setExpenseType(ExpenseType.valueOf(cursor.getString(2).toUpperCase()));
                transaction.setAmount(cursor.getDouble(3));


                transactions.add(transaction);
            }while (cursor.moveToNext());
        }

        cursor.close();
        return transactions;
    }
}