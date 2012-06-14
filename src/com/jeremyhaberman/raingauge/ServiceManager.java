package com.jeremyhaberman.raingauge;

import java.util.HashMap;
import java.util.Map;

import com.jeremyhaberman.raingauge.util.Logger;

/**
 * ServiceManager is a repository of services used throughout the app. Services should be initialized at app start-up in
 * onCreate of the base application.
 */
public class ServiceManager {

	private static final String TAG = ServiceManager.class.getSimpleName();

	@SuppressWarnings("rawtypes")
	private Map services = new HashMap();

	private static ServiceManager mSingleton;
	
	private ServiceManager() {
		
	}
	
	public static ServiceManager createServiceManager() {
		mSingleton = new ServiceManager();
		return mSingleton;
	}

	/**
	 * Loads an instance of a ServiceLocator
	 * 
	 * @param locator
	 */
	public static void load(ServiceManager manager) {
		Logger.debug(TAG, "Loading service manager: " + manager);
		mSingleton = manager;
	}

	/**
	 * Loads a instance of the implementation of a service into the locator
	 * 
	 * @param key
	 *            the name of the service (use the static methods on {@link ServiceManager}).
	 * @param service
	 *            the implementation of the service to load
	 */
	@SuppressWarnings("unchecked")
	public void loadService(Service key, Object service) {
		services.put(key, service);
	}

	/**
	 * Retrieve a service by name. Static variables on ServiceLocator (e.g. {@link ServiceManager#USER_MANAGER}) can be
	 * used for ease and reliability.
	 * 
	 * @param key
	 * @return the service
	 */
	public static Object getService(Service key) {		
		if (mSingleton != null && mSingleton.services != null) {
			return mSingleton.services.get(key);
		} else {
			// TODO throw exception instead of returning null;
			return null;
		}
	}

	/**
	 * Returns the single instance of the ServiceLocator
	 * 
	 * @return
	 */
	public static ServiceManager get() {
		return mSingleton;
	}

}
