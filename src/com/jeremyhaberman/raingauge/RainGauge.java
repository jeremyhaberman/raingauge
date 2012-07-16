package com.jeremyhaberman.raingauge;

import android.app.Application;
import android.content.Intent;
import com.jeremyhaberman.raingauge.processor.DefaultProcessorFactory;
import com.jeremyhaberman.raingauge.processor.ProcessorFactory;
import com.jeremyhaberman.raingauge.rest.method.DefaultRestMethodFactory;
import com.jeremyhaberman.raingauge.rest.method.RestMethodFactory;
import com.jeremyhaberman.raingauge.util.Logger;

public class RainGauge extends Application implements ServiceManager.Initializer {

	@Override
	public void onCreate() {
		super.onCreate();

		Logger.setAppTag("RainGauge");
		Logger.setLevel(Logger.DEBUG);

		initializeServiceManager();

		sendBroadcast(new Intent(WeatherUpdateScheduler.ACTION_SCHEDULE_WEATHER_UPDATES));
	}

	@Override
	public void initializeServiceManager() {
		RestMethodFactory restMethodFactory = DefaultRestMethodFactory.getInstance(this);
		ProcessorFactory processorFactory = DefaultProcessorFactory.getInstance(this);

		ServiceManager serviceManager = ServiceManager.createServiceManager();
		serviceManager.loadService(Service.REST_METHOD_FACTORY, restMethodFactory);
		serviceManager.loadService(Service.PROCESSOR_FACTORY, processorFactory);

		ServiceManager.load(serviceManager);
	}
}
