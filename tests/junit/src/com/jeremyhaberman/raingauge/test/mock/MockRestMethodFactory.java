package com.jeremyhaberman.raingauge.test.mock;

import android.net.Uri;
import android.os.Bundle;
import com.jeremyhaberman.raingauge.rest.Method;
import com.jeremyhaberman.raingauge.rest.method.RestMethod;
import com.jeremyhaberman.raingauge.rest.method.RestMethodFactory;
import com.jeremyhaberman.raingauge.rest.resource.Observations;
import com.jeremyhaberman.raingauge.rest.resource.Resource;

public class MockRestMethodFactory implements RestMethodFactory {

	@SuppressWarnings("unchecked")
	@Override
	public RestMethod<? extends Resource> getRestMethod(Uri resourceUri, Method method,
														Bundle params) {

		return new MockRestMethod(200,
				Observations.createObservations(System.currentTimeMillis(), .75));
	}

}
