package com.list.app.metappv2.screeninvader;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by meks on 21.07.2016.
 */
public class ScreeninvaderAPI {

    private WebSocketClient mWebSocketClient;

    private String currentItemUrl;
    public boolean playerPaused;
    public boolean shairportActive;
    public boolean playlistIsReady;

    private JSONObject playerObject;
    private JSONObject soundObject;
    public JSONArray playlistArray;

    private ScreenFragment sFragment;

    public ScreeninvaderAPI (ScreenFragment fragment){
        sFragment = fragment;
    }

    // Screeninvader API
        public void sendSICommand(String command, String param) {
            String fullcommand = "[" + "\"publish\", \"" + command + "\",\"W\",\" " + param + "\"]";
            try {
                if (mWebSocketClient.getConnection() != null) {
                    mWebSocketClient.send(fullcommand);
                }
            }catch (Exception e){
                sFragment.notifyUser("Websocket Fuckup");
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
                sFragment.notifyUser("Websocket Fuckup");
                e.printStackTrace();
            }
        }
        //WebSockets! foar Screeninvader
        public void connectWebSocket() {
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
                    Looper.prepare();
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
                    sFragment.notifyUser("Can't Connect to Screeninvader");
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
                        case "notifySend": sFragment.notifyUser(event.getString(2));
                            break;
                        case "playerTimePos":
                            break;
                        case "/player/paused": playerPaused = event.getString(2).equals("true"); sFragment.updatePlayerUI();
                            break;
                        case "/shairport/active": shairportActive = event.getString(2).equals("true"); sFragment.updatePlayerUI();
                            break;
                        default:
                            return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        public void fullSync(JSONObject syncObj) {
            try {
                shairportActive = syncObj.getJSONObject("shairport").getString("active").equals("true");
                playlistArray = syncObj.getJSONObject("playlist").getJSONArray("items");
                playerObject = syncObj.getJSONObject("player");
                soundObject = syncObj.getJSONObject("sound");
                playlistIsReady = true;
                sFragment.onWebsocketLoaded();
                //updatePlaylist(itemArray);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
}
