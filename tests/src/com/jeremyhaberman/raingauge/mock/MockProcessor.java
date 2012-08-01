package com.jeremyhaberman.raingauge.mock;

import android.os.Bundle;
import com.jeremyhaberman.raingauge.processor.ResourceProcessor;
import com.jeremyhaberman.raingauge.processor.ResourceProcessorCallback;

public class MockProcessor implements ResourceProcessor {

	private Bundle mParams;
	private ResourceProcessorCallback mCallback;

	@Override
	public void getResource(ResourceProcessorCallback callback, Bundle params) {
		mockCallback(callback, params);
	}

	private void mockCallback(ResourceProcessorCallback callback, Bundle params) {
		mCallback = callback;
		mParams = params;
	}

	public ResourceProcessorCallback getCallback() {
		return mCallback;
	}

	public Bundle getParams() {
		return mParams;
	}
}
