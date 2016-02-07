package com.list.app.metappv2;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * Created by meks on 2/7/16.
 */
public class SettingsFragment extends Fragment {

    private static String slackoIp;
    private static String screenIp;

    public SettingsFragment(){
        String screenIp = getScreenIp();
        String slackoIp = getSlackoIp();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.content_settings, container, false);

        EditText slackoIPText = (EditText) rootView.findViewById(R.id.slackoIP);
        EditText screenIPText = (EditText) rootView.findViewById(R.id.slackoIP);

        slackoIPText.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                getSlackoIp();
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
        screenIPText.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                getScreenIp();
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        return rootView;

    }

            public String getSlackoIp() {
                EditText slackoIPText = (EditText) getView().findViewById(R.id.slackoIP);
                slackoIp = slackoIPText.getText().toString();
                return slackoIp;
            }

            public static void setSlackoIp(String slackoIp) {
                SettingsFragment.slackoIp = slackoIp;
            }

            public String getScreenIp() {
                EditText screenIPText = (EditText) getView().findViewById(R.id.screenIP);
                screenIp = screenIPText.getText().toString();
                return screenIp;
            }

            public static void setScreenIp(String screenIp) {
                SettingsFragment.screenIp = screenIp;
            }


        }