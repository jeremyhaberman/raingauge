package com.jeremyhaberman.raingauge.activity;

import android.app.Activity;
import android.os.Bundle;
import com.jeremyhaberman.raingauge.R;
import com.jeremyhaberman.raingauge.fragment.SetupFragment;

public class SetupActivity extends Activity implements SetupFragment.OnSetupCompleteListener {

	public static final int CONFIGURE_ZIP = 1234;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setup);
	}

	@Override
	public void onSetupComplete() {
		setResult(Activity.RESULT_OK);
		finish();
	}
}
