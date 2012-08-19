
package com.jeremyhaberman.raingauge.fragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import com.jeremyhaberman.raingauge.R;
import com.jeremyhaberman.raingauge.rest.resource.Observations;

public class SetupFragment extends Fragment {

    OnSetupCompleteListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnSetupCompleteListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(
                    activity.toString() + " must implement OnSetupCompleteListener");
        }
    }

    public interface OnSetupCompleteListener {
        public void onSetupComplete();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        return inflater.inflate(R.layout.setup_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final EditText zip = (EditText) getActivity().findViewById(R.id.zip_code);
        final Button go = (Button) getActivity().findViewById(R.id.go);

        zip.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                go.setEnabled(editable.length() == 5);
            }
        });

        go.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                saveZip(zip.getText().toString());
                mListener.onSetupComplete();
            }
        });
    }

    protected void saveZip(String zip) {
        SharedPreferences.Editor editor =
                PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
        editor.putInt(Observations.ZIP_CODE, Integer.parseInt(zip)).commit();
    }

}
