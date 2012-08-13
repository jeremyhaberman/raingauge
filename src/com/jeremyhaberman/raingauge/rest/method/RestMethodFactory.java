package com.jeremyhaberman.raingauge.rest.method;

import android.os.Bundle;
import com.jeremyhaberman.raingauge.rest.Method;
import com.jeremyhaberman.raingauge.rest.resource.Resource;

public interface RestMethodFactory {

	public static final int RESOURCE_TYPE_OBSERVATIONS = 1;
	public static final int RESOURCE_TYPE_FORECAST = 2;

	/**
	 * 
	 * @param resourceType
	 * @param method
	 * @param params
	 * @return
	 * 
	 * @throws IllegalArgumentException if contentUri
	 */
	public abstract RestMethod<? extends Resource> getRestMethod(int resourceType, Method method,
			Bundle params);

}