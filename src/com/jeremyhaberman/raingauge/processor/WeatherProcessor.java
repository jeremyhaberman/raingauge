package com.jeremyhaberman.raingauge.processor;

import android.content.Context;
import android.os.Bundle;

/**
 * The DefaultCatPicturesProcessor is a POJO for processing cat pictures
 * requests. For this pattern, there is one Processor for each resource type.
 * 
 * @author Peter Pascale
 */
public class WeatherProcessor implements ResourceProcessor {

	protected static final String TAG = WeatherProcessor.class.getSimpleName();

	private Context mContext;

	public WeatherProcessor(Context context) {
		mContext = context;
	}

	@Override
	public void getResource(ResourceProcessorCallback callback, Bundle params) {

		// String newestId = getNewestCatPictureId();
		// RestMethod<CatPictures> method = new GetCatPicturesRestMethod(mContext, newestId);

		// RestMethodResult<CatPictures> result = method.execute();

		// updateContentProvider(result.getResource());

		// callback.send(result.getStatusCode(), null);
	}
}