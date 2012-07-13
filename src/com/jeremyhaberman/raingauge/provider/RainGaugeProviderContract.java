package com.jeremyhaberman.raingauge.provider;

import android.net.Uri;

import java.lang.reflect.InvocationTargetException;

public final class RainGaugeProviderContract {

	public static final String AUTHORITY = "com.jeremyhaberman.raingauge.raingaugeprovider";

	public static final class ObservationsTable implements ResourceTable {

		public static final String TABLE_NAME = "observations";

		public static final String[] ALL_COLUMNS;

		static {
			ALL_COLUMNS = new String[]{
					ObservationsTable._ID,
					ObservationsTable.TIMESTAMP,
					ObservationsTable.RAINFALL
			};
		}

		// URI DEFS
		static final String SCHEME = "content://";
		public static final String URI_PREFIX = SCHEME + AUTHORITY;
		private static final String URI_PATH_RAINFALL = "/" + TABLE_NAME;

		public static final int OBSERVATIONS_ID_PATH_POSITION = 1;

		// content://mn.aug.restfulandroid.catpicturesprovider/catpictures
		public static final Uri CONTENT_URI = Uri.parse(URI_PREFIX + URI_PATH_RAINFALL);

		// content://mn.aug.restfulandroid.catpicturesprovider/catpictures/#
		public static final Uri CONTENT_ID_URI_PATTERN = Uri.parse(SCHEME + AUTHORITY
				+ URI_PATH_RAINFALL + "#");

		public static final String TIMESTAMP = "timestamp";
		public static final String RAINFALL = "rainfall";

		public static final int TIMESTAMP_COLUMN_INDEX = 1;
		public static final int RAINFALL_COLUMN_INDEX = 2;

		// Prevent instantiation of this class
		private ObservationsTable() throws InvocationTargetException {
			throw new InvocationTargetException(
					new InstantiationException("Instantiation forbidden"));
		}
	}

	public static final class WateringsTable implements ResourceTable {

		public static final String TABLE_NAME = "waterings";

		public static final String[] ALL_COLUMNS;

		static {
			ALL_COLUMNS = new String[]{
					WateringsTable._ID,
					WateringsTable.TIMESTAMP,
					WateringsTable.AMOUNT
			};
		}

		// URI DEFS
		static final String SCHEME = "content://";
		public static final String URI_PREFIX = SCHEME + AUTHORITY;
		private static final String URI_PATH_WATERINGS = "/" + TABLE_NAME;

		public static final int WATERING_ID_PATH_POSITION = 1;

		public static final Uri CONTENT_URI = Uri.parse(URI_PREFIX + URI_PATH_WATERINGS);

		public static final Uri CONTENT_ID_URI_BASE = Uri.parse(SCHEME + AUTHORITY
				+ URI_PATH_WATERINGS);

		public static final Uri CONTENT_ID_URI_PATTERN = Uri.parse(SCHEME + AUTHORITY
				+ URI_PATH_WATERINGS + "#");

		public static final String TIMESTAMP = "timestamp";
		public static final String AMOUNT = "amount";

		public static final int TIMESTAMP_COLUMN_INDEX = 1;
		public static final int AMOUNT_COLUMN_INDEX = 2;

		// Prevent instantiation of this class
		private WateringsTable() throws InvocationTargetException {
			throw new InvocationTargetException(
					new InstantiationException("Instantiation forbidden"));
		}
	}

	private RainGaugeProviderContract() throws InvocationTargetException {
		throw new InvocationTargetException(new InstantiationException("Instantiation forbidden"));
	}

}
