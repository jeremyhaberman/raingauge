package com.jeremyhaberman.raingauge.processor;
import android.os.Bundle;

public interface ResourceProcessor {

	void getResource(ResourceProcessorCallback callback, Bundle params);
}