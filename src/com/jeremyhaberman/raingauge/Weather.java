package com.jeremyhaberman.raingauge;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONObject;

import android.util.Log;

public class Weather {

	private static final String TAG = Weather.class.getSimpleName();
	public static final String WEEKLY_RAINFALL = "com.jeremyhaberman.raingauge.WEEKLY_RAINFALL";
	public static final String WEEKLY_WATERING= "com.jeremyhaberman.raingauge.WEEKLY_WATERING";
	public static final String ZIP_CODE = "com.jeremyhaberman.raingauge.ZIP_CODE";

	public double getTodaysRainfall(int zip) throws Exception {

		URL url = null;
		try {
			url = buildUrl(zip);
		} catch (MalformedURLException e) {
			throw new Exception(e);
		}

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		InputStream stream = connection.getInputStream();
		String body = getBody(stream);

		Log.d(TAG, body);
		
		JSONObject obj = new JSONObject(body);
		return obj.getDouble("rainDaily");
	}

	private String getBody(InputStream stream) throws IOException {
		BufferedInputStream bis = new BufferedInputStream(stream);
		ByteArrayBuffer baf = new ByteArrayBuffer(50);
		int read = 0;
		int bufSize = 512;
		byte[] buffer = new byte[bufSize];
		while (true) {
			read = bis.read(buffer);
			if (read == -1) {
				break;
			}
			baf.append(buffer, 0, read);
		}
		return new String(baf.toByteArray());

	}

	private URL buildUrl(int zip) throws MalformedURLException {

		StringBuilder builder = new StringBuilder("http://i.wxbug.net/REST/Direct/GetObs.ashx?");
		builder.append("zip=").append(zip);
		builder.append("&units=0&ic=1&api_key=qxktcbwgncg9acgwbk45d9jb");

		return new URL(builder.toString());
	}

}
