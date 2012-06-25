package com.jeremyhaberman.raingauge.rest.method;

import android.content.Context;
import android.content.UriMatcher;
import android.net.Uri;
import android.os.Bundle;
import com.jeremyhaberman.raingauge.provider.RainGaugeProviderContract;
import com.jeremyhaberman.raingauge.provider.RainGaugeProviderContract.ObservationsTable;
import com.jeremyhaberman.raingauge.rest.Method;
import com.jeremyhaberman.raingauge.rest.resource.Observations;
import com.jeremyhaberman.raingauge.rest.resource.Resource;
import com.jeremyhaberman.raingauge.util.Logger;

public class DefaultRestMethodFactory implements RestMethodFactory {

	private static final String TAG = DefaultRestMethodFactory.class.getSimpleName();

	private static RestMethodFactory instance;
	private static Object lock = new Object();
	private UriMatcher uriMatcher;
	private Context mContext;

	private static final int OBSERVATIONS = 1;

	private DefaultRestMethodFactory(Context context) {
		mContext = context.getApplicationContext();
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(RainGaugeProviderContract.AUTHORITY, ObservationsTable.TABLE_NAME,
				OBSERVATIONS);
	}

	public static RestMethodFactory getInstance(Context context) {
		synchronized (lock) {
			if (instance == null) {
				instance = new DefaultRestMethodFactory(context);
			}
		}

		return instance;
	}

	@Override
	public RestMethod<? extends Resource> getRestMethod(Uri resourceUri, Method method,
														Bundle params) {

		switch (uriMatcher.match(resourceUri)) {
			case OBSERVATIONS:
				if (method == Method.GET) {
					if (params.containsKey(Observations.ZIP_CODE)) {
						return GetObservationsRestMethod.newInstance(mContext,
								params.getString(Observations.ZIP_CODE));
					} else {
						Logger.error(TAG, "Missing parameter in params for zip code");
						throw new IllegalArgumentException("Missing zip code in params");

					}
				} else {
					throw new IllegalArgumentException("Invalid method (" + method + ") for Uri "
							+ resourceUri);
				}
			default:
				throw new IllegalArgumentException("Unknown Uri " + resourceUri);
		}
	}
}
