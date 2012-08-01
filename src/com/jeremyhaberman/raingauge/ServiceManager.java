package com.jeremyhaberman.raingauge;

import android.content.Context;
import com.jeremyhaberman.raingauge.android.AndroidAlarmManager;
import com.jeremyhaberman.raingauge.android.DefaultAndroidAlarmManager;
import com.jeremyhaberman.raingauge.android.DefaultNotificationManager;
import com.jeremyhaberman.raingauge.android.NotificationManager;
import com.jeremyhaberman.raingauge.notification.DefaultNotificationHelper;
import com.jeremyhaberman.raingauge.notification.NotificationHelper;
import com.jeremyhaberman.raingauge.processor.DefaultProcessorFactory;
import com.jeremyhaberman.raingauge.processor.ProcessorFactory;
import com.jeremyhaberman.raingauge.rest.method.DefaultRestMethodFactory;
import com.jeremyhaberman.raingauge.rest.method.RestMethodFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * ServiceManager is a repository of services used throughout the app. Services should be initialized at app start-up in
 * onCreate of the base application.
 */
public class ServiceManager {

	private static final String TAG = ServiceManager.class.getSimpleName();

	@SuppressWarnings("rawtypes")
	private static Map services;

	private ServiceManager() {
	}

	private static void initialize(Context context) {
		RestMethodFactory restMethodFactory = DefaultRestMethodFactory.getInstance(context);
		ProcessorFactory processorFactory = DefaultProcessorFactory.getInstance(context);
		AndroidAlarmManager androidAlarmManager = new DefaultAndroidAlarmManager(context);
		NotificationManager notificationManager = new DefaultNotificationManager(context);
		NotificationHelper notificationHelper =
				DefaultNotificationHelper.newNotificationHelper(context);

		services = new HashMap();

		services.put(Service.REST_METHOD_FACTORY, restMethodFactory);
		services.put(Service.PROCESSOR_FACTORY, processorFactory);
		services.put(Service.ALARM_SERVICE, androidAlarmManager);
		services.put(Service.NOTIFICATION_MANAGER, notificationManager);
		services.put(Service.NOTIFICATION_HELPER, notificationHelper);
	}

	public static void reset(Context context) {
		initialize(context);
	}

	/**
	 * Loads a instance of the implementation of a service into the locator
	 *
	 * @param key     the name of the service (use the static methods on {@link ServiceManager}).
	 * @param service the implementation of the service to load
	 */
	@SuppressWarnings("unchecked")
	public static void loadService(Context context, Service key, Object service) {

		if (services == null) {
			initialize(context);
		}

		services.put(key, service);
	}

	/**
	 * Retrieve a service by name. Static variables on ServiceLocator (e.g. {@link Service#REST_METHOD_FACTORY}) can be
	 * used for ease and reliability.
	 *
	 * @param key
	 * @return the service or null if not found
	 */
	public static Object getService(Context context, Service key) {

		if (services == null) {
			initialize(context);
		}

		return services.get(key);
	}
}
