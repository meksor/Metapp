package com.list.app.metappv2;

import android.app.Fragment;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Switch;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class SlackoFragment extends Fragment
implements RadioGroup.OnCheckedChangeListener, Switch.OnCheckedChangeListener{

    String ip;

    public SlackoFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.content_slacko, container, false);

        RadioGroup inputradioGroup = (RadioGroup) rootView.findViewById(R.id.input);
        RadioGroup lightingradioGroup = (RadioGroup) rootView.findViewById(R.id.lighting);
        inputradioGroup.setOnCheckedChangeListener(this);
        lightingradioGroup.setOnCheckedChangeListener(this);


        // Switches bh
        Switch switchProjector = (Switch) rootView.findViewById(R.id.switchProjector);
        Switch switchProjectorBlank = (Switch) rootView.findViewById(R.id.switchProjectorBlank);
        Switch switchTV = (Switch) rootView.findViewById(R.id.switchTV);
        Switch switchYamaha = (Switch) rootView.findViewById(R.id.switchYamaha);
        Switch switchMetacade= (Switch) rootView.findViewById(R.id.switchMetacade);
        Switch switchLamp = (Switch) rootView.findViewById(R.id.switchLamp);
        Switch switchSpaceInvaders = (Switch) rootView.findViewById(R.id.switchSpaceInvaders);

        switchProjector.setOnCheckedChangeListener(this);
        switchProjectorBlank.setOnCheckedChangeListener(this);
        switchTV.setOnCheckedChangeListener(this);
        switchYamaha.setOnCheckedChangeListener(this);
        switchMetacade.setOnCheckedChangeListener(this);
        switchLamp.setOnCheckedChangeListener(this);
        switchSpaceInvaders.setOnCheckedChangeListener(this);


        return rootView;
    }
    public void onCheckedChanged (RadioGroup rg, int s) {
        // Is the button now checked?

        int id = rg.getCheckedRadioButtonId();
        if (id == -1){
            //no item selected
        } else if (id == R.id.inputScreenivader){
            sendSlackoCommand("/rooms/lounge/devices/screeninvader");
        } else if (id == R.id.inputChromecast) {
            sendSlackoCommand("/rooms/lounge/devices/chromecast");
        } else if (id == R.id.inputPS2) {
            sendSlackoCommand("/rooms/lounge/devices/ps2");
        } else if (id == R.id.inputPS3) {
            sendSlackoCommand("/rooms/lounge/devices/ps3");
        } else if (id == R.id.inputWii) {
            sendSlackoCommand("/rooms/lounge/devices/wii");
        } else if (id == R.id.inputHDMI) {
            sendSlackoCommand("/rooms/lounge/devices/own_hdmi");
        } else if (id == R.id.inputKlinke) {
            sendSlackoCommand("/rooms/lounge/devices/own_audio_klinke");
        } else if (id == R.id.inputBT) {
            sendSlackoCommand("/rooms/lounge/devices/bt");
        } else if (id == R.id.lightingNight){
            sendSlackoCommand("/rooms/lounge/lighting/off");
        } else if (id == R.id.lightingSppoky) {
            sendSlackoCommand("/rooms/lounge/lighting/super_chillig");
        } else if (id == R.id.lightingSmooth) {
            sendSlackoCommand("/rooms/lounge/lighting/chillig");
        } else if (id == R.id.lightingNormal) {
            sendSlackoCommand("/rooms/lounge/lighting/normal");
        } else if (id == R.id.lightingNoSlack) {
            sendSlackoCommand("/rooms/lounge/lighting/chinese_sweatshop");
        }

    }
    public void onCheckedChanged(CompoundButton b, boolean n) {
        int id = b.getId();
        if (id == -1){

        } else if (id == R.id.switchMaster){
            if (n){
                sendSlackoCommand("/rooms/lounge/power/on");
            } else {
                sendSlackoCommand("/rooms/lounge/power/off");
            }
        } else if (id == R.id.switchProjector){
            if (n){
                sendSlackoCommand("/rooms/lounge/powersaving/projector/power/on");
            } else {
                sendSlackoCommand("/rooms/lounge/powersaving/projector/power/off");
            }
        } else if (id == R.id.switchProjectorBlank){
            if (n){
                sendSlackoCommand("/rooms/lounge/powersaving/projector/blank/on");

            } else {
                sendSlackoCommand("/rooms/lounge/powersaving/projector/blank/off");

            }
        } else if (id == R.id.switchTV){
            if (n){
                sendSlackoCommand("/rooms/lounge/powersaving/tv/power/on");

            } else {
                sendSlackoCommand("/rooms/lounge/powersaving/tv/power/off");

            }
        } else if (id == R.id.switchYamaha){
            if (n){
                sendSlackoCommand("/rooms/lounge/powersaving/yamaha/power/on");

            } else {
                sendSlackoCommand("/rooms/lounge/powersaving/yamaha/power/off");

            }
        } else if (id == R.id.switchMetacade){
            if (n){
                sendSlackoCommand("/rooms/lounge/powersaving/metacade/power/on");

            } else {
                sendSlackoCommand("/rooms/lounge/powersaving/metacade/power/off");

            }
        } else if (id == R.id.switchLamp){
            if (n){
                sendSlackoCommand("/rooms/lounge/powersaving/lamp1/power/on");

            } else {
                sendSlackoCommand("/rooms/lounge/powersaving/lamp1/power/off");

            }
        } else if (id == R.id.switchSpaceInvaders){
            if (n){
                sendSlackoCommand("/rooms/lounge/lighting/spaceinvaders/on");
            } else {
                sendSlackoCommand("/rooms/lounge/lighting/spaceinvaders/off");

            }

        }

    }
    public void sendSlackoCommand (final String command) {
        ip = "10.20.30.90:8080";
        new Thread(new Runnable() {
            public void run() {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);


                try {
                    URL url = new URL("http://" + ip + "/slackomatic"+ command);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    try {
                        InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                        in.read();
                    }
                    finally {
                        urlConnection.disconnect();
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
