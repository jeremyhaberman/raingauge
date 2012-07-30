package com.jeremyhaberman.raingauge.provider.test;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.InstrumentationTestCase;
import android.test.RenamingDelegatingContext;
import android.test.suitebuilder.annotation.MediumTest;
import com.jeremyhaberman.raingauge.provider.ProviderDbHelper;
import com.jeremyhaberman.raingauge.provider.RainGaugeProviderContract;
import com.jeremyhaberman.raingauge.provider.RainGaugeProviderContract.ObservationsTable;
import com.jeremyhaberman.raingauge.provider.RainGaugeProviderContract.WateringsTable;

public class ProviderDbHelperTest extends InstrumentationTestCase {

	public void testProviderDbHelper() {
		MyMockContext context = new MyMockContext(getInstrumentation().getTargetContext());
		ProviderDbHelper helper = new ProviderDbHelper(context);
		assertNotNull(helper);
	}

	@MediumTest
	public void testOnCreate() {
		MyMockContext context = new MyMockContext(getInstrumentation().getTargetContext());
		ProviderDbHelper helper = new ProviderDbHelper(context);
		assertNotNull(helper);

		SQLiteDatabase db = helper.getWritableDatabase();
		assertNotNull(db);

		// test table names
		Cursor cursor =
				db.query("sqlite_master", new String[]{"name"}, "type='table'", null, null, null,
						"name");

		String[] expectedTableNames =
				new String[]{"android_metadata", "observations", "sqlite_sequence", "waterings"};

		String actualTableName = null;
		for (int i = 0; i < expectedTableNames.length; i++) {
			cursor.moveToNext();
			actualTableName = cursor.getString(0);
			assertEquals(expectedTableNames[i], actualTableName);
		}
		cursor.close();


		String where =
				String.format("tbl_name='%s' AND type='table'", ObservationsTable.TABLE_NAME);
		cursor = db.query("sqlite_master", new String[]{"sql"}, where, null, null, null, null);
		assertTrue(cursor.getCount() == 1);
		cursor.moveToFirst();
		String expectedSql =
				"CREATE TABLE observations (_id INTEGER PRIMARY KEY AUTOINCREMENT, timestamp INTEGER, rainfall REAL)";
		String actualSql = cursor.getString(0);
		assertEquals(expectedSql, actualSql);

		where = String.format("tbl_name='%s' AND type='table'",
				RainGaugeProviderContract.WateringsTable.TABLE_NAME);
		cursor = db.query("sqlite_master", new String[]{"sql"}, where, null, null, null, null);
		assertTrue(cursor.getCount() == 1);
		cursor.moveToFirst();
		expectedSql =
				"CREATE TABLE waterings (_id INTEGER PRIMARY KEY AUTOINCREMENT, timestamp INTEGER, amount REAL)";
		actualSql = cursor.getString(0);
		assertEquals(expectedSql, actualSql);

		cursor.close();

		db.close();
	}

	@MediumTest
	public void testOnUpgrade() {
		MyMockContext context = new MyMockContext(getInstrumentation().getTargetContext());

		/*
		 * The current onUpgrade() method drops and recreates the tables.  For this test,
		 * we create the DB at version 1, add records, open DB version 2 and test that the records
		 * no longer exist.
		 */

		ProviderDbHelper helper = new ProviderDbHelper(context, 1);
		SQLiteDatabase db = helper.getWritableDatabase();
		Cursor cursor = db.query(ObservationsTable.TABLE_NAME, null, null, null, null, null, null);
		assertEquals(0, cursor.getCount());
		cursor = db.query(WateringsTable.TABLE_NAME, null, null, null, null, null, null);
		assertEquals(0, cursor.getCount());

		ContentValues values = new ContentValues();
		values.put(ObservationsTable.RAINFALL, 0.5f);
		long rowId = db.insert(ObservationsTable.TABLE_NAME, null, values);
		assertTrue(rowId != -1);

		values = new ContentValues();
		values.put(RainGaugeProviderContract.WateringsTable.AMOUNT, 0.5f);
		rowId = db.insert(WateringsTable.TABLE_NAME, null, values);
		assertTrue(rowId != -1);

		db.close();
		helper.close();


		helper = new ProviderDbHelper(context, 2);
		db = helper.getReadableDatabase();

		cursor = db.query(ObservationsTable.TABLE_NAME, null, null, null, null, null, null);
		assertEquals(0, cursor.getCount());

		cursor = db.query(WateringsTable.TABLE_NAME, null, null, null, null, null, null);
		assertEquals(0, cursor.getCount());

		db.close();
		helper.close();
	}

	public class MyMockContext extends RenamingDelegatingContext {

		private static final String TAG = "MyMockContext";

		private static final String MOCK_FILE_PREFIX = "test.";

		public MyMockContext(Context context) {
			super(context, MOCK_FILE_PREFIX);
		}

		@Override
		public Resources getResources() {
			return super.getResources();
		}


	}
}

