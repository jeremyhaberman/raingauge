package com.jeremyhaberman.raingauge.test.mock;

import android.app.PendingIntent;
import android.content.Context;
import android.util.Log;
import com.jeremyhaberman.raingauge.android.AndroidAlarmManager;
import org.json.JSONException;
import org.json.JSONObject;

public class MockAlarmManager implements AndroidAlarmManager {

	private Context mContext;
	private static final String TAG = MockAlarmManager.class.getSimpleName();

	public MockAlarmManager(Context context) {
		mContext = context;
	}

	@Override
	public void setRepeating(int type, long triggerAtMillis, long intervalMillis,
							 PendingIntent operation) {

		JSONObject object = new JSONObject();
		try {
			object.put("type", type);
			object.put("triggerAtMillis", triggerAtMillis);
			object.put("intervalMillis", intervalMillis);
			object.put("operation", operation.toString());
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}


		Log.d(TAG, object.toString());
	}
}
