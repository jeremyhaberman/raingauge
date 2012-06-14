package com.jeremyhaberman.raingauge.rest.method;

import com.jeremyhaberman.raingauge.rest.resource.Resource;

public interface RestMethod<T extends Resource>{

	public RestMethodResult<T> execute();
}
