package com.jeremyhaberman.raingauge.rest.method;

import android.net.Uri;
import android.os.Bundle;

import com.jeremyhaberman.raingauge.rest.Method;
import com.jeremyhaberman.raingauge.rest.resource.Resource;

public interface RestMethodFactory {

	/**
	 * 
	 * @param contentUri
	 * @param method
	 * @param params
	 * @return
	 * 
	 * @throws IllegalArgumentException if contentUri
	 */
	public abstract RestMethod<? extends Resource> getRestMethod(Uri contentUri, Method method,
			Bundle params);

}