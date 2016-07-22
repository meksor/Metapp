package com.list.app.metappv2.screeninvader;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.list.app.metappv2.R;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONObject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;



public class ScreenFragment extends Fragment
implements ImageButton.OnClickListener, SeekBar.OnSeekBarChangeListener{

    private ToggleButton playButton;
    private ToggleButton fwdButton;
    private ToggleButton backButton;
    private ToggleButton volumeButton;
    private ToggleButton airplayButton;
    private SeekBar volumeBar;
    ListView playlistView;

    ScreeninvaderAPI SIAPI = new ScreeninvaderAPI(this);

    public ScreenFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_screeninvader, container, false);

        playButton = (ToggleButton) rootView.findViewById(R.id.btn_play);
        fwdButton = (ToggleButton) rootView.findViewById(R.id.btn_forw);
        backButton = (ToggleButton) rootView.findViewById(R.id.btn_back);
        volumeButton = (ToggleButton) rootView.findViewById(R.id.btn_volume);
        airplayButton = (ToggleButton) rootView.findViewById(R.id.btn_airplay);
        playlistView = (ListView) rootView.findViewById(R.id.listview_screeninvader);

        //volumeBar = (SeekBar) rootView.findViewById(R.id.volumeBar);
        //volumeBar.setMax(100);
        playButton.setOnClickListener(this);
        fwdButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
        volumeButton.setOnClickListener(this);
        airplayButton.setOnClickListener(this);
        //volumeBar.setOnSeekBarChangeListener(this);

        SIAPI.connectWebSocket();

        return rootView;
    }

    public void onWebsocketLoaded() {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                playlistView.setAdapter(new PlaylistAdapter(getActivity(), R.layout.listitem_screeninvader, getPlaylist(SIAPI.playlistArray), SIAPI));
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        updatePlayerUI();
    }

    // Button OnClick Listener
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_play) {
            if (!SIAPI.playerPaused) {
                SIAPI.sendSICommand("playerPause");
            } else {
                SIAPI.sendSICommand("playerPlay");
            }
            updatePlayerUI();
        } else if (id == R.id.btn_forw) {
            SIAPI.sendSICommand("playerNext");
        } else if (id == R.id.btn_back) {
            SIAPI.sendSICommand("playerPrevious");

        } else if (id == R.id.btn_airplay) {
            if (!SIAPI.shairportActive) {
                SIAPI.sendSICommand("shairportStart");
            } else {
                SIAPI.sendSICommand("shairportStop");
            }
        } else if (id == R.id.btn_volume) {}

    }

    //SeekBar Listener
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser){
            Log.d("WS Debug", Integer.toString(progress));
            SIAPI.sendSICommand("/sound/volume", Integer.toString(progress));
        } else {
        }
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void updatePlayerUI() {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                playButton.setChecked(SIAPI.playerPaused);
                airplayButton.setChecked(SIAPI.shairportActive);
            }
        });
    }

    public ArrayList<PlaylistItem> getPlaylist(JSONArray itemArray) {
        ArrayList<PlaylistItem> data = new ArrayList<PlaylistItem>();
        try {
            for (int i = 0; i < itemArray.length(); i++) {

                JSONObject currentJSON = itemArray.getJSONObject(i);
                PlaylistItem currentPlaylistItem = new PlaylistItem();
                currentPlaylistItem.title = currentJSON.getString("title");
                currentPlaylistItem.url = currentJSON.getString("source");
                currentPlaylistItem.source_url = currentJSON.getString("url");
                currentPlaylistItem.id = currentPlaylistItem.url.substring(currentPlaylistItem.url.length() - 11);
                currentPlaylistItem.category = itemArray.getJSONObject(i).getString("category");
                data.add(i, currentPlaylistItem);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }


    public void notifyUser(String s) {
        Toast.makeText(getActivity(), s ,Toast.LENGTH_SHORT).show();
    }
}
