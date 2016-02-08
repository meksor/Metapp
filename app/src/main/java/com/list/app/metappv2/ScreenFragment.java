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
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONObject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;



public class ScreenFragment extends Fragment
implements FloatingActionButton.OnClickListener{

    String currentItemUrl;
    boolean playerPaused;
    WebSocketClient mWebSocketClient;
    FloatingActionButton playButton;
    FloatingActionButton fwdButton;
    FloatingActionButton backButton;
    JSONObject playerObject;

    public ScreenFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.content_screen, container, false);

        playButton = (FloatingActionButton) rootView.findViewById(R.id.play);
        fwdButton = (FloatingActionButton) rootView.findViewById(R.id.forw);
        backButton = (FloatingActionButton) rootView.findViewById(R.id.back);

        playButton.setOnClickListener(this);
        fwdButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
        connectWebSocket();

        return rootView;

    }




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

        }
    }

    public void sendSICommand(String command, String param) {
        mWebSocketClient.send("[" + "publish," + command + ",W," + param + "]");
    }

    public void sendSICommand(String command) {
        String fullcommand = "[" + "\"publish\", \"" + command + "\",\"W\" " + "]";
        try {
            mWebSocketClient.send(fullcommand);
        }catch (Exception e){
            notifyUser("Websocket Fuckup");
            e.printStackTrace();
        }
    }
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
            JSONArray itemArray = syncObj.getJSONObject("playlist").getJSONArray("items");
            playerObject = syncObj.getJSONObject("player");
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
                    playButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_white_18dp, getContext().getTheme()));
            } else {
                    playButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_white_18dp, getContext().getTheme()));
            }

            }
        });
    }

    public ArrayList<playItem> updatePlaylist(JSONArray itemArray){
        ArrayList<playItem> data = new ArrayList<playItem>();
        try {
            for (int i = 0; i < itemArray.length() -1 ; i++ ){
                playItem current = data.get(i);
                current.title = itemArray.getJSONObject(i).getString("title");
                data.add(i, current);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }
}
