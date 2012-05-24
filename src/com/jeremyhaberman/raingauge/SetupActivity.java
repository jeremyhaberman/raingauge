package com.jeremyhaberman.raingauge;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class SetupActivity extends Activity {

	public static final int CONFIGURE_ZIP = 1234;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.setup);
		
		final EditText zip = (EditText) findViewById(R.id.zip_code);
		
		Button go = (Button) findViewById(R.id.go);
		go.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				saveZip(zip.getText().toString());
				setResult(Activity.RESULT_OK);
				finish();
			}
		});
	}

	protected void saveZip(String zip) {
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
		editor.putInt(Weather.ZIP_CODE, Integer.parseInt(zip)).commit();
	}
	
	
	
	

}
