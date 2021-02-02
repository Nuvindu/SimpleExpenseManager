package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

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

        //add query data into a content object
        ContentValues val = new ContentValues();
        val.put(database.getAccountNo(), accountNo);
        val.put(database.getTransactionDate(), this.dateFormat.format(date));
        val.put(database.getTransactionAmount(), amount);
        val.put(database.getTransactionType(), String.valueOf(expenseType));
        //insert transaction data into table when a transaction happened
        db.insert(database.getTransactionTableName(), null, val);

//        String expense = String.valueOf(expenseType);
//        String dates = this.dateFormat.format(date);
//        String INSERT_QUERY = "INSERT INTO " + database.getTransactionTableName() + " "
//                + "(" + database.getAccountNo()
//                +" ,"+database.getTransactionDate()
//                +" ,"+database.getTransactionAmount()
//                +" ,"+database.getTransactionType()
//                +" )"
//                + " VALUES ('"
//                + accountNo + "', '"
//                + dates + "',"
//                + amount + ",'"
//                + expense +"' );";
//        db.execSQL(INSERT_QUERY);
//        db.close();
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        SQLiteDatabase db = database.getReadableDatabase();
        //query to get all the transactions
        Cursor cursor = db.rawQuery("SELECT * FROM " + database.getTransactionTableName() + " ORDER BY " + database.getTransactionDate() + " DESC ",null);

        ArrayList<Transaction> transactions = new ArrayList<>();
        if(cursor.moveToFirst()){
            do {
                Transaction transaction= new Transaction();
                try {
                    //set the date of transaction occurred.
                    transaction.setDate(this.dateFormat.parse(cursor.getString(0)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                //add details to the object
                transaction.setAccountNo(cursor.getString(1));
                transaction.setExpenseType(ExpenseType.valueOf(cursor.getString(2).toUpperCase()));
                transaction.setAmount(cursor.getDouble(3));

                //add objects to the object list
                transactions.add(transaction);
            }while (cursor.moveToNext());
        }

        cursor.close();
        return transactions;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {

        SQLiteDatabase db = database.getReadableDatabase();
        //select all transaction according to the date with a given limit
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

                //add details to the object
                transaction.setAccountNo(cursor.getString(1));
                transaction.setExpenseType(ExpenseType.valueOf(cursor.getString(2).toUpperCase()));
                transaction.setAmount(cursor.getDouble(3));

                //add objects to the object list
                transactions.add(transaction);
            }while (cursor.moveToNext());
        }

        cursor.close();
        return transactions;
    }
}