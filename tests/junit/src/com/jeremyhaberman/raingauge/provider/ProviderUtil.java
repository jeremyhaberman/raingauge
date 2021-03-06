package com.jeremyhaberman.raingauge.provider;

import android.content.ContentResolver;
import android.net.Uri;
import com.jeremyhaberman.raingauge.model.Watering;
import com.jeremyhaberman.raingauge.provider.RainGaugeProviderContract.ObservationsTable;
import com.jeremyhaberman.raingauge.rest.resource.Observations;

public class ProviderUtil {

	public static Uri[] insertObservations(ContentResolver resolver,
										  Observations... observations) {

		Uri[] uris = new Uri[observations.length];

		int index = 0;
		for (Observations observations1 : observations) {
			uris[index] = resolver.insert(ObservationsTable.CONTENT_URI,
					observations1.toContentValues());
			index++;
		}

		return uris;
	}

	public static Uri[] insertWaterings(ContentResolver contentResolver, Watering... waterings) {

		Uri[] uris = new Uri[waterings.length];

		int index = 0;
		for (Watering watering : waterings) {
			uris[index] = contentResolver.insert(RainGaugeProviderContract.WateringsTable.CONTENT_URI,
					watering.toContentValues());
			index++;
		}

		return uris;
	}
}

