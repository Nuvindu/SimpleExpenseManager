package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class DbHandler extends SQLiteOpenHelper {
    private static final int VERSION = 8;
    private static final String DB_NAME = "180239N";
    private static final String TABLE_NAME = "AccountData";
    private static final String TRANSACTION_TABLE_NAME = "TransactionData";

    // Column names of Account table
    private static final String accountNo ="AccountNumber";
    private static final String bankName ="Bank";
    private static final String holderName ="Holder";
    private static final String balance = "Balance";

    //Column names of Trans table
    private static final String transactionDate ="Date";

    public static int getVERSION() {
        return VERSION;
    }

    public static String getDbName() {
        return DB_NAME;
    }

    public static String getTableName() {
        return TABLE_NAME;
    }

    public static String getTransactionTableName() {
        return TRANSACTION_TABLE_NAME;
    }

    public static String getAccountNo() {
        return accountNo;
    }

    public static String getBankName() {
        return bankName;
    }

    public static String getHolderName() {
        return holderName;
    }

    public static String getBalance() {
        return balance;
    }

    public static String getTransactionDate() {
        return transactionDate;
    }

    public static String getTransactionType() {
        return transactionType;
    }

    public static String getTransactionAmount() {
        return transactionAmount;
    }

    public static final String transactionType ="Type";
    public static final String transactionAmount = "amount";


    public DbHandler(@Nullable Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String TABLE_CREATE_QUERY = "CREATE TABLE IF NOT EXISTS "+ TABLE_NAME +" " +
                "("
                + accountNo +" TEXT PRIMARY KEY, "
                + bankName + " TEXT, "
                + holderName + " TEXT, "
                + balance +" REAL CHECK ("
                + balance +" > 0)"
                +");";


        db.execSQL(TABLE_CREATE_QUERY);
        String TABLE_CREATE_QUERY2 = "CREATE TABLE IF NOT EXISTS "+ TRANSACTION_TABLE_NAME +" " +
                "("
                + transactionDate + " TEXT , "
                + accountNo +" TEXT , "
                + transactionType + " TEXT , "
                + transactionAmount +" REAL CHECK ("
                + transactionAmount +" > 0),"
                + " FOREIGN KEY ("+ accountNo +") REFERENCES "+ TABLE_NAME +"("+ accountNo +")ON DELETE CASCADE\n" +
                "ON UPDATE CASCADE);";
        db.execSQL(TABLE_CREATE_QUERY2);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        String DROP_TABLE_QUERY = "DROP TABLE IF EXISTS "+ TABLE_NAME;
        // Drop older table if existed
        db.execSQL(DROP_TABLE_QUERY);
        db.execSQL("DROP TABLE IF EXISTS '" + TRANSACTION_TABLE_NAME + "'");
        // Create tables again
        onCreate(db);
    }


}