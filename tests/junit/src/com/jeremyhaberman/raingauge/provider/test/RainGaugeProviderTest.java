package com.jeremyhaberman.raingauge.provider.test;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.ProviderTestCase2;
import android.test.suitebuilder.annotation.MediumTest;
import com.jeremyhaberman.raingauge.model.Watering;
import com.jeremyhaberman.raingauge.provider.ProviderUtil;
import com.jeremyhaberman.raingauge.provider.RainGaugeProvider;
import com.jeremyhaberman.raingauge.provider.RainGaugeProviderContract;
import com.jeremyhaberman.raingauge.provider.RainGaugeProviderContract.ObservationsTable;
import com.jeremyhaberman.raingauge.provider.RainGaugeProviderContract.WateringsTable;
import com.jeremyhaberman.raingauge.rest.resource.Observations;

public class RainGaugeProviderTest extends ProviderTestCase2<RainGaugeProvider> {

	public static final String INVALID_URI_PATH =
			"content://com.jeremyhaberman.raingauge.raingaugeprovider/invalid";

	public RainGaugeProviderTest() {
		this(RainGaugeProvider.class, RainGaugeProviderContract.AUTHORITY);
	}

	public RainGaugeProviderTest(Class<RainGaugeProvider> providerClass, String providerAuthority) {
		super(providerClass, providerAuthority);
	}

	@MediumTest
	public void testShouldNotInsertForIllegalUri() {
		try {
			getMockContentResolver().insert(Uri.parse(INVALID_URI_PATH), null);
			fail("Should have thrown IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}
	}

	@MediumTest
	public void testShouldNotInsertObservationsForIdUri() {
		try {
			getMockContentResolver()
					.insert(ContentUris.withAppendedId(ObservationsTable.CONTENT_URI, 1),
							null);
			fail("Should have thrown IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}
	}

	@MediumTest
	public void testShouldNotInsertWateringForIdUri() {
		try {
			getMockContentResolver()
					.insert(ContentUris.withAppendedId(WateringsTable.CONTENT_URI, 1),
							null);
			fail("Should have thrown IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}
	}

	@MediumTest
	public void testShouldAddValidObservations() {

		Observations expectedObservations =
				Observations.createObservations(System.currentTimeMillis(), 0.5);

		Uri observationsUri =
				getMockContentResolver().insert(ObservationsTable.CONTENT_URI,
						expectedObservations.toContentValues());

		assertNotNull(observationsUri);

		Cursor cursor = getMockContentResolver().query(observationsUri, null, null, null, null);
		cursor.moveToFirst();
		Observations actual = Observations.fromCursor(cursor);
		cursor.close();

		assertEquals(expectedObservations.getTimeStamp(), actual.getTimeStamp());
		assertEquals(expectedObservations.getRainfall(), actual.getRainfall());
	}

	@MediumTest
	public void testShouldInsertValidWatering() {

		Watering expected = Watering.createWatering(System.currentTimeMillis(), 0.4);

		Uri wateringUri =
				getMockContentResolver().insert(WateringsTable.CONTENT_URI,
						expected.toContentValues());

		assertNotNull(wateringUri);

		Cursor cursor = getMockContentResolver().query(wateringUri, null, null, null, null);
		cursor.moveToFirst();
		Watering actual = Watering.fromCursor(cursor);
		cursor.close();

		assertEquals(expected, actual);
	}

	@MediumTest
	public void testShouldNotAddObservationsWithNullValues() {
		try {
			getMockContentResolver().insert(ObservationsTable.CONTENT_URI, null);
			fail("Should have thrown IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}
	}

	@MediumTest
	public void testShouldNotAddWateringWithNullValues() {
		try {
			getMockContentResolver().insert(WateringsTable.CONTENT_URI, null);
			fail("Should have thrown IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}
	}

	@MediumTest
	public void testShouldNotAddObservationsWithoutTimestamp() {
		ContentValues values = new ContentValues();
		values.put(ObservationsTable.RAINFALL, 0.5);

		try {
			getMockContentResolver().insert(ObservationsTable.CONTENT_URI, values);
			fail("Should have thrown IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}
	}

	@MediumTest
	public void testShouldNotAddObservationsWithoutRainfall() {
		ContentValues values = new ContentValues();
		values.put(ObservationsTable.TIMESTAMP, System.currentTimeMillis());

		try {
			getMockContentResolver().insert(ObservationsTable.CONTENT_URI, values);
			fail("Should have thrown IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}
	}

	@MediumTest
	public void testShouldNotAddWateringWithoutTimestamp() {
		ContentValues values = new ContentValues();
		values.put(WateringsTable.AMOUNT, 0.5);

		try {
			getMockContentResolver().insert(WateringsTable.CONTENT_URI, values);
			fail("Should have thrown IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}
	}

	@MediumTest
	public void testShouldNotAddWateringWithoutAmount() {
		ContentValues values = new ContentValues();
		values.put(ObservationsTable.TIMESTAMP, System.currentTimeMillis());

		try {
			getMockContentResolver().insert(WateringsTable.CONTENT_URI, values);
			fail("Should have thrown IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}
	}

	@MediumTest
	public void testDeleteShouldReturnZeroWhenTableIsEmpty() {
		int deleted = getMockContentResolver().delete(ObservationsTable.CONTENT_URI, null, null);
		assertEquals(0, deleted);

		deleted = getMockContentResolver().delete(WateringsTable.CONTENT_URI, null, null);
		assertEquals(0, deleted);
	}

	@MediumTest
	public void testDeleteShouldThrowExceptionForInvalidUri() {

		try {
			getMockContentResolver().delete(Uri.parse(INVALID_URI_PATH), null, null);
			fail("Should have thrown IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}
	}

	@MediumTest
	public void testQueryWaterings() {

		Watering expected = Watering.createWatering(System.currentTimeMillis(), 0.2);

		Uri[] uris = ProviderUtil.insertWaterings(getMockContentResolver(), expected);

		Cursor cursor = getMockContentResolver().query(uris[0], null, null, null, null);
		cursor.moveToFirst();
		Watering actual = Watering.fromCursor(cursor);
		cursor.close();

		assertEquals(expected, actual);
	}

	@MediumTest
	public void testShouldDeleteAllObservationsRecords() {

		ProviderUtil.insertObservations(getMockContentResolver(),
				Observations.createObservations(System.currentTimeMillis(), 0.5));
		int deleted = getMockContentResolver().delete(ObservationsTable.CONTENT_URI, null, null);
		assertEquals(1, deleted);

		ProviderUtil.insertObservations(getMockContentResolver(),
				Observations.createObservations(System.currentTimeMillis(), 0.5),
				Observations.createObservations(System.currentTimeMillis(), 0.4));
		deleted = getMockContentResolver().delete(ObservationsTable.CONTENT_URI, null, null);
		assertEquals(2, deleted);
	}

	@MediumTest
	public void testShouldDeleteAllWateringsRecords() {

		ProviderUtil.insertWaterings(getMockContentResolver(),
				Watering.createWatering(System.currentTimeMillis(), 0.5));
		int deleted = getMockContentResolver().delete(WateringsTable.CONTENT_URI, null, null);
		assertEquals(1, deleted);

		ProviderUtil.insertWaterings(getMockContentResolver(),
				Watering.createWatering(System.currentTimeMillis(), 0.5),
				Watering.createWatering(System.currentTimeMillis(), 0.4));
		deleted = getMockContentResolver().delete(WateringsTable.CONTENT_URI, null, null);
		assertEquals(2, deleted);
	}

	@MediumTest
	public void testDeleteSingleObservationsRecord() {
		Observations observations1 =
				Observations.createObservations(System.currentTimeMillis(), 0.5);
		Observations observations2 =
				Observations.createObservations(System.currentTimeMillis(), 0.1);

		Uri observations1Uri = getMockContentResolver()
				.insert(ObservationsTable.CONTENT_URI, observations1.toContentValues());
		Uri observations2Uri = getMockContentResolver()
				.insert(ObservationsTable.CONTENT_URI, observations2.toContentValues());

		int deleted = getMockContentResolver().delete(observations1Uri, null, null);
		assertEquals(1, deleted);

		Cursor cursor = getMockContentResolver().query(observations2Uri, null, null, null, null);
		assertEquals(1, cursor.getCount());
		cursor.moveToNext();
		Observations observations2b = Observations.fromCursor(cursor);
		cursor.close();
		assertEquals(observations2, observations2b);
	}

	@MediumTest
	public void testDeleteSingleWateringRecord() {
		Watering watering1 =
				Watering.createWatering(System.currentTimeMillis(), 0.5);
		Watering watering2 =
				Watering.createWatering(System.currentTimeMillis(), 0.4);

		Uri watering1Uri = getMockContentResolver()
				.insert(WateringsTable.CONTENT_URI, watering1.toContentValues());
		Uri watering2Uri = getMockContentResolver()
				.insert(WateringsTable.CONTENT_URI, watering2.toContentValues());

		int deleted = getMockContentResolver().delete(watering1Uri, null, null);
		assertEquals(1, deleted);

		Cursor cursor = getMockContentResolver().query(watering2Uri, null, null, null, null);
		assertEquals(1, cursor.getCount());
		cursor.moveToNext();
		Watering watering2b = Watering.fromCursor(cursor);
		cursor.close();
		assertEquals(watering2, watering2b);
	}

	@MediumTest
	public void testDeleteObservationsWithWhereClause() {
		Observations observations1 =
				Observations.createObservations(System.currentTimeMillis(), 0.5);
		Observations observations2 =
				Observations.createObservations(System.currentTimeMillis(), 0.1);

		Uri observations1Uri = getMockContentResolver()
				.insert(ObservationsTable.CONTENT_URI, observations1.toContentValues());
		Uri observations2Uri = getMockContentResolver()
				.insert(ObservationsTable.CONTENT_URI, observations2.toContentValues());

		int deleted = getMockContentResolver()
				.delete(ObservationsTable.CONTENT_URI, ObservationsTable._ID + "=?",
						new String[]{Long.toString(ContentUris.parseId(observations1Uri))});
		assertEquals(1, deleted);

		Cursor cursor = getMockContentResolver().query(observations2Uri, null, null, null, null);
		assertEquals(1, cursor.getCount());
		cursor.moveToNext();
		Observations observations2b = Observations.fromCursor(cursor);
		cursor.close();

		assertEquals(observations2, observations2b);


	}

	@MediumTest
	public void testDeleteObservationsByIdWithWhereClause() {
		Observations observations1 =
				Observations.createObservations(System.currentTimeMillis(), 0.5);
		Observations observations2 =
				Observations.createObservations(System.currentTimeMillis(), 0.1);

		Uri observations1Uri = getMockContentResolver()
				.insert(ObservationsTable.CONTENT_URI, observations1.toContentValues());
		Uri observations2Uri = getMockContentResolver()
				.insert(ObservationsTable.CONTENT_URI, observations2.toContentValues());

		int deleted = getMockContentResolver()
				.delete(observations1Uri, ObservationsTable.RAINFALL + "=?",
						new String[]{Double.toString(0.5)});
		assertEquals(1, deleted);

		Cursor cursor = getMockContentResolver().query(observations2Uri, null, null, null, null);
		assertEquals(1, cursor.getCount());
		cursor.moveToNext();
		Observations observations2b = Observations.fromCursor(cursor);
		cursor.close();

		assertEquals(observations2, observations2b);
	}

	@MediumTest
	public void testShouldNotAllowMassObservationsUpdate() {
		assertAllMassUpdatesThrowException(ObservationsTable.CONTENT_URI);
	}

	@MediumTest
	public void testShouldNotAllowMassWateringsUpdate() {
		assertAllMassUpdatesThrowException(WateringsTable.CONTENT_URI);
	}

	@MediumTest
	public void testShouldNotUpdateIllegalUri() {
		try {
			getMockContentResolver().update(Uri.parse(INVALID_URI_PATH), null, null, null);
			fail("Should have thrown IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}
	}

	@MediumTest
	public void testShouldUpdateSingleObservations() {

		Observations observations =
				Observations.createObservations(System.currentTimeMillis(), 0.5);

		Uri uri = getMockContentResolver()
				.insert(ObservationsTable.CONTENT_URI, observations.toContentValues());

		observations = Observations.createObservations(System.currentTimeMillis(), 0.4);

		int count =
				getMockContentResolver().update(uri, observations.toContentValues(), null, null);

		assertEquals(1, count);

		Cursor cursor = getMockContentResolver().query(uri, null, null, null, null);
		cursor.moveToNext();

		Observations actual = Observations.fromCursor(cursor);

		assertEquals(observations, actual);
	}

	@MediumTest
	public void testShouldUpdateSingleWatering() {

		Watering originalWatering =
				Watering.createWatering(System.currentTimeMillis(), 0.5);

		Uri uri = getMockContentResolver()
				.insert(WateringsTable.CONTENT_URI, originalWatering.toContentValues());

		Watering expectedWatering = Watering.createWatering(System.currentTimeMillis(), 0.4);

		int count =
				getMockContentResolver()
						.update(uri, expectedWatering.toContentValues(), null, null);

		assertEquals(1, count);

		Cursor cursor = getMockContentResolver().query(uri, null, null, null, null);
		cursor.moveToNext();

		Watering actual = Watering.fromCursor(cursor);

		assertEquals(expectedWatering, actual);
	}

	@MediumTest
	public void testGetType() {

		assertEquals("vnd.android.cursor.dir/vnd.com.jeremyhaberman.raingauge.observations",
				getMockContentResolver().getType(ObservationsTable.CONTENT_URI));

		assertEquals("vnd.android.cursor.dir/vnd.com.jeremyhaberman.raingauge.observation",
				getMockContentResolver()
						.getType(ContentUris.withAppendedId(ObservationsTable.CONTENT_URI, 1)));

		assertEquals("vnd.android.cursor.dir/vnd.com.jeremyhaberman.raingauge.waterings",
				getMockContentResolver().getType(WateringsTable.CONTENT_URI));

		assertEquals("vnd.android.cursor.dir/vnd.com.jeremyhaberman.raingauge.watering",
				getMockContentResolver()
						.getType(ContentUris
								.withAppendedId(WateringsTable.CONTENT_URI, 1)));

		assertNull(getMockContentResolver().getType(Uri.parse(INVALID_URI_PATH)));
	}

	private void assertAllMassUpdatesThrowException(Uri contentUri) {
		try {
			getMockContentResolver().update(contentUri, null, null, null);
			fail("Should have thrown IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}

		try {
			getMockContentResolver()
					.update(contentUri, new ContentValues(), null, null);
			fail("Should have thrown IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}

		try {
			getMockContentResolver()
					.update(contentUri, new ContentValues(), "_id=?", new String[]{"1"});
			fail("Should have thrown IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}
	}
}

