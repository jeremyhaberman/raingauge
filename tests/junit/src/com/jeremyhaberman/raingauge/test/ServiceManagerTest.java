package com.jeremyhaberman.raingauge.test;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;
import com.jeremyhaberman.raingauge.Service;
import com.jeremyhaberman.raingauge.ServiceManager;
import com.jeremyhaberman.raingauge.rest.method.RestMethodFactory;

public class ServiceManagerTest extends AndroidTestCase {

	@SmallTest
	public void testGetService() {

		RestMethodFactory factory = (RestMethodFactory) ServiceManager
				.getService(getContext(), Service.REST_METHOD_FACTORY);
		assertNotNull(factory);
	}
}

