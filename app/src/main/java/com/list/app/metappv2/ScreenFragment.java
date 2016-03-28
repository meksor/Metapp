package com.list.app.metappv2;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONObject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;



public class ScreenFragment extends Fragment
implements ImageButton.OnClickListener, SeekBar.OnSeekBarChangeListener{

    String currentItemUrl;
    boolean playerPaused;
    boolean shairportActive;

    WebSocketClient mWebSocketClient;

    ImageButton playButton;
    ImageButton fwdButton;
    ImageButton backButton;
    ImageButton shairportButton;
    ImageButton postButton;
    SeekBar volumeBar;

    JSONObject playerObject;
    JSONObject soundObject;

    public ScreenFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.content_screen, container, false);

        playButton = (ImageButton) rootView.findViewById(R.id.play);
        fwdButton = (ImageButton) rootView.findViewById(R.id.forw);
        backButton = (ImageButton) rootView.findViewById(R.id.back);
        shairportButton = (ImageButton) rootView.findViewById(R.id.airplay);
        postButton = (ImageButton) rootView.findViewById(R.id.post);
        volumeBar = (SeekBar) rootView.findViewById(R.id.volumeBar);
        volumeBar.setMax(100);

        playButton.setOnClickListener(this);
        fwdButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
        shairportButton.setOnClickListener(this);
        postButton.setOnClickListener(this);
        volumeBar.setOnSeekBarChangeListener(this);

        connectWebSocket();
        return rootView;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        updatePlayerUI();
    }
//Listeners

    // Button OnClick
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.play) {
            if (!playerPaused) {
                sendSICommand("playerPause");
            } else {
                sendSICommand("playerPlay");
            }
            updatePlayerUI();
        } else if (id == R.id.forw) {
            sendSICommand("playerNext");
        } else if (id == R.id.back) {
            sendSICommand("playerPrevious");

        } else if (id == R.id.airplay) {
            if (!shairportActive) {
                sendSICommand("shairportStart");
            } else {
                sendSICommand("shairportStop");
            }
        } else if (id == R.id.post) {}

    }

    //SeekBar Listener
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser){
            Log.d("WS Debug", Integer.toString(progress));
            sendSICommand("/sound/volume", Integer.toString(progress));
        } else {
        }
    }
    public void onStartTrackingTouch(SeekBar seekBar) {

    }
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    // Screeninvader API
    public void sendSICommand(String command, String param) {
        String fullcommand = "[" + "\"publish\", \"" + command + "\",\"W\",\" " + param + "\"]";
        try {
            if (mWebSocketClient.getConnection() != null) {
                mWebSocketClient.send(fullcommand);
            }
        }catch (Exception e){
            notifyUser("Websocket Fuckup");
            e.printStackTrace();
        }
    }

    public void sendSICommand(String command) {
        String fullcommand = "[" + "\"publish\", \"" + command + "\",\"W\" " + "]";
        try {
            if (mWebSocketClient.getConnection() != null) {
                mWebSocketClient.send(fullcommand);
            }
        }catch (Exception e){
            notifyUser("Websocket Fuckup");
            e.printStackTrace();
        }
    }
    //WebSockets! foar Screeninvader
    private void connectWebSocket() {

        URI uri;
        try {
            uri = new URI("ws://10.20.30.40:8080");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.i("Websocket", "Opened");
                mWebSocketClient.send("setup");
            }

            @Override
            public void onMessage(String s) {
                parseMessage(s);
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.i("Websocket", "Closed " + s);
            }

            @Override
            public void onError(Exception e) {
                Log.i("Websocket", "Error " + e.getMessage());
                notifyUser("Can't Connect to Screeninvader");
            }
        };
        mWebSocketClient.connect();
        mWebSocketClient.getReadyState();
    }

    public void parseMessage (String s) {
        JSONObject syncObj;
        JSONArray event;

        if (s.startsWith("{")) {
            String result = s.replaceAll("\n","");
            try {
                syncObj = new JSONObject(result);
                fullSync(syncObj);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            try {
                event = new JSONArray(s);
                switch (event.getString(0)) {
                    case "notifySend": notifyUser(event.getString(2));
                        break;
                    case "playerTimePos":
                        break;
                    case "/player/paused": playerPaused = event.getString(2).equals("true"); updatePlayerUI();
                        break;
                    case "/shairport/active": shairportActive = event.getString(2).equals("true"); updatePlayerUI();
                        break;
                    default:
                        return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void notifyUser(String s) {
        Snackbar.make(getView(), s ,Snackbar.LENGTH_SHORT).setAction("Action", null).show();
    }

    public void fullSync(JSONObject syncObj) {
        try {
            shairportActive = syncObj.getJSONObject("shairport").getString("active").equals("true");
            JSONArray itemArray = syncObj.getJSONObject("playlist").getJSONArray("items");
            playerObject = syncObj.getJSONObject("player");
            soundObject = syncObj.getJSONObject("sound");
            //updatePlaylist(itemArray);



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void updatePlayerUI() {
        try {
            currentItemUrl = playerObject.getString("url");
        } catch (Exception e){
            notifyUser("Websocket Fuckup");
            e.printStackTrace();
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

            if (playerPaused) {
                    playButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_black_36dp, getActivity().getTheme()));
            } else {
                    playButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_black_36dp, getActivity().getTheme()));
            }
            if (shairportActive){
                shairportButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_airplay_blue_a700_24dp, getActivity().getTheme()));
            } else {
                shairportButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_airplay_black_24dp, getActivity().getTheme()));
            }
            }
        });
    }

    public ArrayList<playItem> updatePlaylist(JSONArray itemArray){
        ArrayList<playItem> data = new ArrayList<playItem>();
        try {
            for (int i = 0; i < itemArray.length() ; i++ ){
                playItem current = data.get(i);
                current.title = itemArray.getJSONObject(i).getString("title");
                current.category = itemArray.getJSONObject(i).getString("category");
                data.add(i, current);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }
}
