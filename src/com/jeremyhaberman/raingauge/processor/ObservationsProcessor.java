package com.jeremyhaberman.raingauge.processor;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.jeremyhaberman.raingauge.R;
import com.jeremyhaberman.raingauge.Service;
import com.jeremyhaberman.raingauge.ServiceManager;
import com.jeremyhaberman.raingauge.activity.RainGaugeActivity;
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

	private static final int RAINFALL_NOTIFICATION_ID = 4692;

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
				.getRestMethod(ObservationsTable.CONTENT_URI, Method.GET, params);

		RestMethodResult<Observations> result = method.execute();

		try {
			addToContentProvider(result.getResource());
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

		scheduleNotification(observations.getRainfall());

		if (uri == null) {
			throw new IOException("Error inserting Observations into ContentProvider");
		}
	}

	private void scheduleNotification(double rainfall) {
		NotificationManager notificationManager =
				(NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

		Notification notification = buildNotification(rainfall);

		notificationManager.notify(RAINFALL_NOTIFICATION_ID, notification);
	}

	private Notification buildNotification(double rainfall) {
		int icon = R.drawable.status_bar_icon_23;
		CharSequence contentText = "Yesterday's Rainfall: " + rainfall + " in";
		long when = getNextNotificationTime();
		Notification notification = new Notification(icon, contentText, when);
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		CharSequence title = mContext.getString(R.string.app_name);
		Intent notificationIntent = new Intent(mContext, RainGaugeActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, notificationIntent,
				PendingIntent.FLAG_CANCEL_CURRENT);
		notification.setLatestEventInfo(mContext, title, contentText, contentIntent);
		return notification;

	}

	private long getNextNotificationTime() {

		Calendar date = new GregorianCalendar();

		date.set(Calendar.HOUR_OF_DAY, 5);
		date.set(Calendar.MINUTE, 0);
		date.set(Calendar.SECOND, 0);
		date.set(Calendar.MILLISECOND, 0);

		date.add(Calendar.DAY_OF_MONTH, 1);

		return date.getTimeInMillis();
	}
}