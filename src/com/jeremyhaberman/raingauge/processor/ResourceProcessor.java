package com.jeremyhaberman.raingauge.processor;
import android.os.Bundle;

public interface ResourceProcessor {

	int SUCCESS = 0;
	int IO_ERROR = -1;

	void getResource(ResourceProcessorCallback callback, Bundle params);
}