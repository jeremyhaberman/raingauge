package com.jeremyhaberman.raingauge.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.jeremyhaberman.raingauge.provider.RainGaugeProviderContract.ObservationsTable;
import com.jeremyhaberman.raingauge.provider.RainGaugeProviderContract.WateringsTable;

/**
 * This creates, updates, and opens the database. Opening is handled by the
 * superclass, we handle the create & upgrade steps
 */
public class ProviderDbHelper extends SQLiteOpenHelper {

	public final String TAG = getClass().getSimpleName();

	// Name of the database file
	private static final String DATABASE_NAME = "raingauge.db";
	private static final int DATABASE_VERSION = 4;

	public ProviderDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		createTables(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + ObservationsTable.TABLE_NAME + ";");
		db.execSQL("DROP TABLE IF EXISTS " + WateringsTable.TABLE_NAME + ";");
		createTables(db);
	}

	private void createTables(SQLiteDatabase db) {
		/* Create rainfall table */
		StringBuilder rainfallBuilder = new StringBuilder();
		rainfallBuilder.append("CREATE TABLE " + ObservationsTable.TABLE_NAME + " (");
		rainfallBuilder.append(ObservationsTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, ");
		rainfallBuilder.append(ObservationsTable.TIMESTAMP + " INTEGER, ");
		rainfallBuilder.append(ObservationsTable.RAINFALL + " REAL");
		rainfallBuilder.append(");");
		String sql = rainfallBuilder.toString();
		Log.i(TAG, "Creating DB table with string: '" + sql + "'");
		db.execSQL(sql);
		
		/* Create waterings table */
		StringBuilder wateringsBuilder = new StringBuilder();
		wateringsBuilder.append("CREATE TABLE " + WateringsTable.TABLE_NAME + " (");
		wateringsBuilder.append(WateringsTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, ");
		wateringsBuilder.append(WateringsTable.TIMESTAMP + " INTEGER, ");
		wateringsBuilder.append(WateringsTable.AMOUNT + " REAL");
		wateringsBuilder.append(");");
		sql = wateringsBuilder.toString();
		Log.i(TAG, "Creating DB table with string: '" + sql + "'");
		db.execSQL(sql);
	}

}
