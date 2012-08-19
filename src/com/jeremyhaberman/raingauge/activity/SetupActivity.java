
package com.jeremyhaberman.raingauge.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.jeremyhaberman.raingauge.R;
import com.jeremyhaberman.raingauge.fragment.SetupFragment;

public class SetupActivity extends FragmentActivity implements
        SetupFragment.OnSetupCompleteListener {

    /**
     * Activity request code for configuring the ZIP code for the app
     */
    public static final int REQUEST_CODE_CONFIGURE_ZIP = 1234;

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
