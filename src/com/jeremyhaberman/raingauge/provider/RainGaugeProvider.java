package com.jeremyhaberman.raingauge.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import com.jeremyhaberman.raingauge.provider.RainGaugeProviderContract.ObservationsTable;
import com.jeremyhaberman.raingauge.provider.RainGaugeProviderContract.WateringsTable;

import java.util.HashMap;

public class RainGaugeProvider extends ContentProvider {

	public static final String TAG = RainGaugeProvider.class.getSimpleName();

	// "projection" map of all the cat pictures table columns
	private static HashMap<String, String> rainfallProjectionMap;

	private static HashMap<String, String> wateringsProjectionMap;

	// URI matcher for validating URIs
	private static final UriMatcher uriMatcher;

	private static final int OBSERVATIONS_URI_CODE = 1;

	// URI matcher ID for the single cat picture ID pattern
	private static final int OBSERVATIONS_ID_URI_CODE = 2;

	private static final int WATERINGS_URI_CODE = 3;

	private static final int WATERINGS_ID_URI_CODE = 4;

	// Handle to our ProviderDbHelper.
	private ProviderDbHelper mDbHelper;

	private enum Function {
		INSERT, UPDATE, DELETE
	}


	// static 'setup' block
	static {
		// Build up URI matcher
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

		uriMatcher.addURI(RainGaugeProviderContract.AUTHORITY, ObservationsTable.TABLE_NAME,
				OBSERVATIONS_URI_CODE);

		uriMatcher.addURI(RainGaugeProviderContract.AUTHORITY,
				ObservationsTable.TABLE_NAME + "/#", OBSERVATIONS_ID_URI_CODE);

		uriMatcher.addURI(RainGaugeProviderContract.AUTHORITY, WateringsTable.TABLE_NAME,
				WATERINGS_URI_CODE);

		uriMatcher.addURI(RainGaugeProviderContract.AUTHORITY,
				WateringsTable.TABLE_NAME + "/#", WATERINGS_ID_URI_CODE);

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
		this.mDbHelper = new ProviderDbHelper(this.getContext());
		/* if there are any issues, they'll be reported as exceptions */
		return true;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {

		int uriCode = uriMatcher.match(uri);
		String tableName = null;

		switch (uriCode) {
			case UriMatcher.NO_MATCH:
				throw new IllegalArgumentException("Unknown Uri: " + uri.toString());
			case OBSERVATIONS_ID_URI_CODE:
			case WATERINGS_ID_URI_CODE:
				throw new IllegalArgumentException("Cannot insert into Uri: " + uri.toString());
			case OBSERVATIONS_URI_CODE:
				verifyObservationsValues(Function.INSERT, values);
				tableName = ObservationsTable.TABLE_NAME;
				break;
			default:
				break;
		}

		SQLiteDatabase db = mDbHelper.getWritableDatabase();

		db.beginTransaction();

		try {
			long rowId = db.insert(tableName, null, values);

			Uri newUri = null;

			if (rowId > 0) {
				db.setTransactionSuccessful();
				newUri = ContentUris.withAppendedId(uri, rowId);
				getContext().getContentResolver().notifyChange(newUri, null);
			}

			return newUri;

		} finally {
			db.endTransaction();
		}
	}

	@Override
	public int delete(Uri uri, String whereClause, String[] whereValues) {
		SQLiteDatabase db = this.mDbHelper.getWritableDatabase();
		int deletedRowsCount;
		String finalWhere;
		String tableName = null;

		db.beginTransaction();
		// Perform the update based on the incoming URI's pattern
		try {
			switch (uriMatcher.match(uri)) {

				case OBSERVATIONS_URI_CODE:
					// Perform the update and return the number of rows updated.
					tableName = ObservationsTable.TABLE_NAME;
					break;

				case OBSERVATIONS_ID_URI_CODE:
					tableName = ObservationsTable.TABLE_NAME;
					String id = uri.getPathSegments()
							.get(ObservationsTable.RAINFALL_ID_PATH_POSITION);
					finalWhere = ObservationsTable._ID + " = " + id;

					// if we were passed a 'where' arg, add that to our 'finalWhere'
					if (whereClause != null) {
						finalWhere = finalWhere + " AND " + whereClause;
					}
					break;

				case WATERINGS_URI_CODE:
					// Perform the update and return the number of rows updated.
					tableName = WateringsTable.TABLE_NAME;
					break;

				case WATERINGS_ID_URI_CODE:
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

	private void verifyObservationsValues(Function function, ContentValues values) {

		String failureCause = null;

		switch (function) {
			case INSERT:
				if (values == null) {
					failureCause = "Values is null";
				} else if (!values.containsKey(ObservationsTable.TIMESTAMP)) {
					failureCause = "Missing timestamp value";
				} else if (!values.containsKey(ObservationsTable.RAINFALL)) {
					failureCause = "Missing rainfall value";
				}
				break;
			case UPDATE:
				break;
			default:
				break;
		}

		if (failureCause != null) {
			throw new IllegalArgumentException(failureCause);
		}
	}

	@Override
	public Cursor query(Uri uri, String[] selectedColumns, String whereClause,
						String[] whereValues, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		// Choose the projection and adjust the "where" clause based on URI
		// pattern-matching.
		switch (uriMatcher.match(uri)) {
			case OBSERVATIONS_URI_CODE:
				qb.setTables(ObservationsTable.TABLE_NAME);
				qb.setProjectionMap(rainfallProjectionMap);
				break;

			/*
			 * asking for a single cat picture - use the cat pictures projection,
			 * but add a where clause to only return the one cat picture
			 */
			case OBSERVATIONS_ID_URI_CODE:
				qb.setTables(ObservationsTable.TABLE_NAME);
				qb.setProjectionMap(rainfallProjectionMap);
				// Find the cat picture ID itself in the incoming URI
				String catPicId =
						uri.getPathSegments().get(ObservationsTable.RAINFALL_ID_PATH_POSITION);
				qb.appendWhere(ObservationsTable._ID + "=" + catPicId);
				break;
			case WATERINGS_URI_CODE:
				qb.setTables(WateringsTable.TABLE_NAME);
				qb.setProjectionMap(wateringsProjectionMap);
				break;

			/*
			 * asking for a single cat picture - use the cat pictures projection,
			 * but add a where clause to only return the one cat picture
			 */
			case WATERINGS_ID_URI_CODE:
				qb.setTables(WateringsTable.TABLE_NAME);
				qb.setProjectionMap(wateringsProjectionMap);
				// Find the cat picture ID itself in the incoming URI
				String wateringId =
						uri.getPathSegments().get(WateringsTable.WATERING_ID_PATH_POSITION);
				qb.appendWhere(WateringsTable._ID + "=" + wateringId);
				break;

			default:
				// If the URI doesn't match any of the known patterns, throw an
				// exception.
				throw new IllegalArgumentException("Unknown URI " + uri);
		}

		SQLiteDatabase db = this.mDbHelper.getReadableDatabase();

		// the two nulls here are 'grouping' and 'filtering by group'
		Cursor cursor = qb.query(db, selectedColumns, whereClause, whereValues, null, null,
				sortOrder);

		// Tell the Cursor about the URI to watch, so it knows when its source
		// data changes
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues updateValues, String whereClause,
					  String[] whereValues) {
		SQLiteDatabase db = this.mDbHelper.getWritableDatabase();
		int updatedRowsCount;
		String finalWhere = null;
		String tableName = null;

		db.beginTransaction();
		// Perform the update based on the incoming URI's pattern
		try {
			switch (uriMatcher.match(uri)) {

				case OBSERVATIONS_URI_CODE:
					// Perform the update and return the number of rows updated.
					tableName = ObservationsTable.TABLE_NAME;
					break;

				case OBSERVATIONS_ID_URI_CODE:
					tableName = ObservationsTable.TABLE_NAME;
					String id = uri.getPathSegments()
							.get(ObservationsTable.RAINFALL_ID_PATH_POSITION);
					finalWhere = ObservationsTable._ID + " = " + id;

					// if we were passed a 'where' arg, add that to our 'finalWhere'
					if (whereClause != null) {
						finalWhere = finalWhere + " AND " + whereClause;
					}
					break;

				case WATERINGS_URI_CODE:
					// Perform the update and return the number of rows updated.
					tableName = WateringsTable.TABLE_NAME;
					break;

				case WATERINGS_ID_URI_CODE:
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
			case OBSERVATIONS_URI_CODE:
				return "vnd.android.cursor.dir/vnd.com.jeremyhaberman.raingauge.observations";
			case OBSERVATIONS_ID_URI_CODE:
				return "vnd.android.cursor.dir/vnd.com.jeremyhaberman.raingauge.observation";
			case WATERINGS_URI_CODE:
				return "vnd.android.cursor.dir/vnd.com.jeremyhaberman.raingauge.waterings";
			case WATERINGS_ID_URI_CODE:
				return "vnd.android.cursor.dir/vnd.com.jeremyhaberman.raingauge.watering";
			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}
}
