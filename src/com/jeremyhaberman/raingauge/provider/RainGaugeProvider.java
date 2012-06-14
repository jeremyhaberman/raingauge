package com.jeremyhaberman.raingauge.provider;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import com.jeremyhaberman.raingauge.provider.RainGaugeProviderContract.ObservationsTable;
import com.jeremyhaberman.raingauge.provider.RainGaugeProviderContract.WateringsTable;

public class RainGaugeProvider extends ContentProvider {

	public static final String TAG = RainGaugeProvider.class.getSimpleName();

	// "projection" map of all the cat pictures table columns
	private static HashMap<String, String> rainfallProjectionMap;
	
	private static HashMap<String, String> wateringsProjectionMap;

	// URI matcher for validating URIs
	private static final UriMatcher uriMatcher;

	// URI matcher ID for the cat pictures pattern
	private static final int MATCHER_RAINFALL = 1;

	// URI matcher ID for the single cat picture ID pattern
	private static final int MATCHER_RAINFALL_ID = 2;
	
	private static final int MATCHER_WATERINGS = 3;
	
	private static final int MATCHER_WATERING_ID = 4;

	// Handle to our ProviderDbHelper.
	private ProviderDbHelper dbHelper;


	// static 'setup' block
	static {
		// Build up URI matcher
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

		uriMatcher.addURI(RainGaugeProviderContract.AUTHORITY, ObservationsTable.TABLE_NAME,
				MATCHER_RAINFALL);

		uriMatcher.addURI(RainGaugeProviderContract.AUTHORITY,
				ObservationsTable.TABLE_NAME + "/#", MATCHER_RAINFALL_ID);
		
		uriMatcher.addURI(RainGaugeProviderContract.AUTHORITY, WateringsTable.TABLE_NAME, MATCHER_WATERINGS);
		
		uriMatcher.addURI(RainGaugeProviderContract.AUTHORITY,
				WateringsTable.TABLE_NAME + "/#", MATCHER_WATERING_ID);

		// Create and initialize a projection map that returns all columns,
		// This map returns a column name for a given string. The two are
		// usually equal, but we need this structure
		// later, down in .query()
		rainfallProjectionMap = new HashMap<String, String>();
		for (String column : ObservationsTable.ALL_COLUMNS) {
			rainfallProjectionMap.put(column, column);
		}
		
		wateringsProjectionMap = new HashMap<String, String>();
		for (String column : WateringsTable.ALL_COLUMNS) {
			wateringsProjectionMap.put(column, column);
		}
	}

	@Override
	public boolean onCreate() {
		this.dbHelper = new ProviderDbHelper(this.getContext());
		/* if there are any issues, they'll be reported as exceptions */
		return true;
	}

	@Override
	public int delete(Uri uri, String whereClause, String[] whereValues) {
		SQLiteDatabase db = this.dbHelper.getWritableDatabase();
		int deletedRowsCount;
		String finalWhere;
		String tableName=null;

		db.beginTransaction();
		// Perform the update based on the incoming URI's pattern
		try {
			switch (uriMatcher.match(uri)) {

			case MATCHER_RAINFALL:
				// Perform the update and return the number of rows updated.
				tableName = ObservationsTable.TABLE_NAME;
				break;

			case MATCHER_RAINFALL_ID:
				tableName = ObservationsTable.TABLE_NAME;
				String id = uri.getPathSegments()
						.get(ObservationsTable.RAINFALL_ID_PATH_POSITION);
				finalWhere = ObservationsTable._ID + " = " + id;

				// if we were passed a 'where' arg, add that to our 'finalWhere'
				if (whereClause != null) {
					finalWhere = finalWhere + " AND " + whereClause;
				}
				break;
				
			case MATCHER_WATERINGS:
				// Perform the update and return the number of rows updated.
				tableName = WateringsTable.TABLE_NAME;
				break;

			case MATCHER_WATERING_ID:
				tableName = WateringsTable.TABLE_NAME;
				id = uri.getPathSegments()
						.get(WateringsTable.WATERING_ID_PATH_POSITION);
				finalWhere = WateringsTable._ID + " = " + id;

				// if we were passed a 'where' arg, add that to our 'finalWhere'
				if (whereClause != null) {
					finalWhere = finalWhere + " AND " + whereClause;
				}
				break;

			default:
				// Incoming URI pattern is invalid: halt & catch fire.
				throw new IllegalArgumentException("Unknown URI " + uri);
			}

			deletedRowsCount = db.delete(tableName, whereClause, whereValues);
			
			if (deletedRowsCount > 0) {
				db.setTransactionSuccessful();
				// Notify observers of the the change
				getContext().getContentResolver().notifyChange(uri, null);
			}
			
		} finally {
			db.endTransaction();
		}

		// Returns the number of rows deleted.
		return deletedRowsCount;
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {

		ContentValues values;
		if (initialValues != null) {
			values = new ContentValues(initialValues);
		} else {
			throw new SQLException("ContentValues arg for .insert() is null, cannot insert row.");
		}

		SQLiteDatabase db = this.dbHelper.getWritableDatabase();

		// Validate the incoming URI.
		db.beginTransaction();
		// Perform the update based on the incoming URI's pattern
		String insertTable = null;
		Uri baseUri = null;
		try {
			switch (uriMatcher.match(uri)) {

			case MATCHER_RAINFALL:
				insertTable = ObservationsTable.TABLE_NAME;
				baseUri = ObservationsTable.CONTENT_ID_URI_BASE;
				break;
				
			case MATCHER_WATERINGS:
				insertTable = WateringsTable.TABLE_NAME;
				baseUri = WateringsTable.CONTENT_ID_URI_BASE;
				break;

			default:
				// Incoming URI pattern is invalid: halt & catch fire.
				throw new IllegalArgumentException("Unknown URI " + uri);
			}

			long newRowId = db.insert(insertTable,null, values);

			if (newRowId > 0) { // if rowID is -1, it means the insert failed
				// Build a new URI with the new resource's ID
				// appended to it.
				db.setTransactionSuccessful();
				Uri notifyUri = ContentUris.withAppendedId(baseUri,newRowId);
				// Notify observers that our data changed.
				getContext().getContentResolver().notifyChange(notifyUri, null);
				return notifyUri;
			}
		} finally {
			db.endTransaction();
		}	

		/* insert failed; halt and catch fire */
		throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public Cursor query(Uri uri, String[] selectedColumns, String whereClause,
			String[] whereValues, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		// Choose the projection and adjust the "where" clause based on URI
		// pattern-matching.
		switch (uriMatcher.match(uri)) {
		case MATCHER_RAINFALL:
			qb.setTables(ObservationsTable.TABLE_NAME);
			qb.setProjectionMap(rainfallProjectionMap);
			break;

			/*
			 * asking for a single cat picture - use the cat pictures projection,
			 * but add a where clause to only return the one cat picture
			 */
		case MATCHER_RAINFALL_ID:
			qb.setTables(ObservationsTable.TABLE_NAME);
			qb.setProjectionMap(rainfallProjectionMap);
			// Find the cat picture ID itself in the incoming URI
			String catPicId = uri.getPathSegments().get(ObservationsTable.RAINFALL_ID_PATH_POSITION);
			qb.appendWhere(ObservationsTable._ID + "=" + catPicId);
			break;
		case MATCHER_WATERINGS:
			qb.setTables(WateringsTable.TABLE_NAME);
			qb.setProjectionMap(wateringsProjectionMap);
			break;

			/*
			 * asking for a single cat picture - use the cat pictures projection,
			 * but add a where clause to only return the one cat picture
			 */
		case MATCHER_WATERING_ID:
			qb.setTables(WateringsTable.TABLE_NAME);
			qb.setProjectionMap(wateringsProjectionMap);
			// Find the cat picture ID itself in the incoming URI
			String wateringId = uri.getPathSegments().get(WateringsTable.WATERING_ID_PATH_POSITION);
			qb.appendWhere(WateringsTable._ID + "=" + wateringId);
			break;

		default:
			// If the URI doesn't match any of the known patterns, throw an
			// exception.
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		SQLiteDatabase db = this.dbHelper.getReadableDatabase();

		// the two nulls here are 'grouping' and 'filtering by group'
		Cursor cursor = qb.query(db, selectedColumns, whereClause, whereValues, null, null,
				sortOrder);

		// Tell the Cursor about the URI to watch, so it knows when its source
		// data changes
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues updateValues, String whereClause, String[] whereValues) {
		SQLiteDatabase db = this.dbHelper.getWritableDatabase();
		int updatedRowsCount;
		String finalWhere=null;
		String tableName=null;

		db.beginTransaction();
		// Perform the update based on the incoming URI's pattern
		try {
			switch (uriMatcher.match(uri)) {

			case MATCHER_RAINFALL:
				// Perform the update and return the number of rows updated.
				tableName = ObservationsTable.TABLE_NAME;
				break;

			case MATCHER_RAINFALL_ID:
				tableName = ObservationsTable.TABLE_NAME;
				String id = uri.getPathSegments()
						.get(ObservationsTable.RAINFALL_ID_PATH_POSITION);
				finalWhere = ObservationsTable._ID + " = " + id;

				// if we were passed a 'where' arg, add that to our 'finalWhere'
				if (whereClause != null) {
					finalWhere = finalWhere + " AND " + whereClause;
				}
				break;
				
			case MATCHER_WATERINGS:
				// Perform the update and return the number of rows updated.
				tableName = WateringsTable.TABLE_NAME;
				break;

			case MATCHER_WATERING_ID:
				tableName = WateringsTable.TABLE_NAME;
				id = uri.getPathSegments()
						.get(WateringsTable.WATERING_ID_PATH_POSITION);
				finalWhere = WateringsTable._ID + " = " + id;

				// if we were passed a 'where' arg, add that to our 'finalWhere'
				if (whereClause != null) {
					finalWhere = finalWhere + " AND " + whereClause;
				}
				break;

			default:
				// Incoming URI pattern is invalid: halt & catch fire.
				throw new IllegalArgumentException("Unknown URI " + uri);
			}

			updatedRowsCount = db.update(tableName, updateValues,
					finalWhere, whereValues);
			
			if (updatedRowsCount > 0) {
				db.setTransactionSuccessful();
				
				/*
				 * Gets a handle to the content resolver object for the current context,
				 * and notifies it that the incoming URI changed. The object passes this
				 * along to the resolver framework, and observers that have registered
				 * themselves for the provider are notified.
				 */
				getContext().getContentResolver().notifyChange(uri, null);
			}
			
		} finally {
			db.endTransaction();
		}



		// Returns the number of rows updated.
		return updatedRowsCount;
	}

	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)) {
		case MATCHER_RAINFALL:
			return "vnd.android.cursor.dir/vnd.com.jeremyhaberman.raingauge.rainfalls";
		case MATCHER_RAINFALL_ID:
			return "vnd.android.cursor.dir/vnd.com.jeremyhaberman.raingauge.rainfall";
		case MATCHER_WATERINGS:
			return "vnd.android.cursor.dir/vnd.com.jeremyhaberman.raingauge.waterings";
		case MATCHER_WATERING_ID:
			return "vnd.android.cursor.dir/vnd.com.jeremyhaberman.raingauge.watering";
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
		File file = new File(this.getContext().getFilesDir(), uri.getPath());
		ParcelFileDescriptor parcel = ParcelFileDescriptor.open(file,
				ParcelFileDescriptor.MODE_READ_ONLY);
		return parcel;
	}
}
