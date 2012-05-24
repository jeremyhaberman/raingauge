package com.jeremyhaberman.raingauge.provider;

import android.net.Uri;

public final class RainGaugeProviderContract {

	public static final String AUTHORITY = "com.jeremyhaberman.raingauge.raingaugeprovider";

	public static final class RainfallTable implements ResourceTable {

		public static final String TABLE_NAME = "rainfalls";
		
		public static final String[] ALL_COLUMNS;

		static {
			ALL_COLUMNS = new String[] {
					RainfallTable._ID,
					RainfallTable.TIMESTAMP,
					RainfallTable.RAINFALL
				};
		}

		// URI DEFS
		static final String SCHEME = "content://";
		public static final String URI_PREFIX = SCHEME + AUTHORITY;
		private static final String URI_PATH_RAINFALL = "/" + TABLE_NAME;

		public static final int RAINFALL_ID_PATH_POSITION = 1;

		// content://mn.aug.restfulandroid.catpicturesprovider/catpictures
		public static final Uri CONTENT_URI = Uri.parse(URI_PREFIX + URI_PATH_RAINFALL);

		// content://mn.aug.restfulandroid.catpicturesprovider/catpictures/ -- used
		// for content provider insert() call

		public static final Uri CONTENT_ID_URI_BASE = Uri.parse(SCHEME + AUTHORITY
				+ URI_PATH_RAINFALL);
		
		// content://mn.aug.restfulandroid.catpicturesprovider/catpictures/#
		public static final Uri CONTENT_ID_URI_PATTERN = Uri.parse(SCHEME + AUTHORITY
				+ URI_PATH_RAINFALL + "#");

		public static final String TIMESTAMP = "timestamp";

		public static final String RAINFALL = "rainfall";

		// Prevent instantiation of this class
		private RainfallTable() {
		}
	}
	
	public static final class WateringsTable implements ResourceTable {

		public static final String TABLE_NAME = "waterings";
		
		public static final String[] ALL_COLUMNS;

		static {
			ALL_COLUMNS = new String[] {
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

		// content://mn.aug.restfulandroid.catpicturesprovider/catpictures
		public static final Uri CONTENT_URI = Uri.parse(URI_PREFIX + URI_PATH_WATERINGS);

		// content://mn.aug.restfulandroid.catpicturesprovider/catpictures/ -- used
		// for content provider insert() call

		public static final Uri CONTENT_ID_URI_BASE = Uri.parse(SCHEME + AUTHORITY
				+ URI_PATH_WATERINGS);
		
		// content://mn.aug.restfulandroid.catpicturesprovider/catpictures/#
		public static final Uri CONTENT_ID_URI_PATTERN = Uri.parse(SCHEME + AUTHORITY
				+ URI_PATH_WATERINGS + "#");

		public static final String TIMESTAMP = "timestamp";

		public static final String AMOUNT = "amount";

		// Prevent instantiation of this class
		private WateringsTable() {
		}
	}

	private RainGaugeProviderContract() {
		// disallow instantiation
	}

}
