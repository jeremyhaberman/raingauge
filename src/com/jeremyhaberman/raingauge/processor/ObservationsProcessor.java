package com.jeremyhaberman.raingauge.processor;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import com.jeremyhaberman.raingauge.Service;
import com.jeremyhaberman.raingauge.ServiceManager;
import com.jeremyhaberman.raingauge.notification.NotificationHelper;
import com.jeremyhaberman.raingauge.provider.RainGaugeProviderContract.ObservationsTable;
import com.jeremyhaberman.raingauge.rest.Method;
import com.jeremyhaberman.raingauge.rest.method.RestMethod;
import com.jeremyhaberman.raingauge.rest.method.RestMethodFactory;
import com.jeremyhaberman.raingauge.rest.method.RestMethodResult;
import com.jeremyhaberman.raingauge.rest.resource.Observations;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class ObservationsProcessor implements ResourceProcessor {

	protected static final String TAG = ObservationsProcessor.class.getSimpleName();

	private Context mContext;

	private RestMethodFactory mRestMethodFactory;

	private ObservationsProcessor(Context context) {
		mContext = context;

		mRestMethodFactory = (RestMethodFactory) ServiceManager
				.getService(context, Service.REST_METHOD_FACTORY);
	}

	public static ObservationsProcessor createProcessor(Context context) {
		return new ObservationsProcessor(context);
	}

	@Override
	public void getResource(ResourceProcessorCallback callback, Bundle params) {

		@SuppressWarnings("unchecked")
		RestMethod<Observations> method = (RestMethod<Observations>) mRestMethodFactory
				.getRestMethod(RestMethodFactory.RESOURCE_TYPE_OBSERVATIONS, Method.GET, params);

		RestMethodResult<Observations> result = method.execute();

		try {
			addToContentProvider(result.getResource());
			scheduleNotification(mContext, result.getResource().getRainfall());
			callback.send(result.getStatusCode(), ResourceProcessor.SUCCESS);
		} catch (IOException e) {
			callback.send(result.getStatusCode(), ResourceProcessor.IO_ERROR);
		}
	}

	private void addToContentProvider(Observations observations) throws IOException {

		ContentValues values = new ContentValues();
		values.put(ObservationsTable.TIMESTAMP, System.currentTimeMillis());
		values.put(ObservationsTable.RAINFALL, observations.getRainfall());
		Uri uri = mContext.getContentResolver().insert(
				ObservationsTable.CONTENT_URI, values);

		if (uri == null) {
			throw new IOException("Error inserting Observations into ContentProvider");
		}
	}

	private void scheduleNotification(Context context, double rainfall) {
		NotificationHelper
				notificationHelper = (NotificationHelper) ServiceManager
				.getService(mContext, Service.NOTIFICATION_HELPER);
		notificationHelper.scheduleRainfallNotification(context, getNextNotificationTime(), rainfall);
	}

	private long getNextNotificationTime() {

		Calendar date = new GregorianCalendar();

		date.set(Calendar.HOUR_OF_DAY, 0);
		date.set(Calendar.MINUTE, 0);
		date.set(Calendar.SECOND, 0);
		date.set(Calendar.MILLISECOND, 0);

		date.add(Calendar.DAY_OF_MONTH, 1);

		return date.getTimeInMillis();
	}
}