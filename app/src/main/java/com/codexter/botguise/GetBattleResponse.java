package com.codexter.botguise;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

public final class GetBattleResponse {

    private static final String URL =
            "https://directline.botframework.com/api/conversations/";
    private static String mConversationID;
    private static int mWatermark = -1;

    private GetBattleResponse() {
    }

    public static String getConversationId(int code) {
        String JSONResponse = "";
        URL queryUrl = createUrl(URL);
        String key = Keys.getKey(code);

        try {
            JSONResponse = makeHttpRequestForConversationId(queryUrl, key);
            Log.v("Conv id ", JSONResponse);
        } catch (IOException e) {
            return null;
        }
        try {
            JSONObject jsonObject = new JSONObject(JSONResponse);
            mConversationID = jsonObject.getString("conversationId");
        } catch (JSONException e) {
        }

        return mConversationID;
    }

    public static void postMessage(String from, String message, int code) {
        String JSONResponse = "";
        URL queryUrl = createUrl(URL + mConversationID + "/messages/");
        String key = Keys.getKey(code);

        try {
            JSONResponse = makeHttpRequestToPostMessage(queryUrl, from, message, key);
            mWatermark += 1;
        } catch (IOException e) {
            return;
        }
    }

    public static String getMessage(int code) {
        String JSONResponse = "";
        URL queryUrl = createUrl(URL + mConversationID + "/messages/");
        String message = "";
        String key = Keys.getKey(code);

        try {
            JSONResponse = makeHttpRequestToGetMessage(queryUrl, key);
            Log.v("Message", JSONResponse);
        } catch (IOException e) {
            return null;
        }
        try {
            JSONObject jsonObject = new JSONObject(JSONResponse);
            int watermark = jsonObject.getInt("watermark");
            JSONArray JSONMessages = jsonObject.getJSONArray("messages");
            message = JSONMessages.getJSONObject(watermark).getString("text");
            mWatermark = watermark;
        } catch (JSONException e) {
        }
        return message;
    }

    private static URL createUrl(String urlString) {
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            return null;
        }
        Log.v("createUrl", String.valueOf(url));
        return url;
    }

    private static String makeHttpRequestForConversationId(URL url, String key) throws IOException {
        String JSONResponse = "";
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");

            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            urlConnection.setRequestProperty("Authorization", "BotConnector " + key);

            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(50000);
            urlConnection.connect();
            Log.v("makeHttpConversationId", String.valueOf(urlConnection.getResponseCode()));
            Log.v("makeHttpConversationId", "Connection Established");
            inputStream = urlConnection.getInputStream();
            JSONResponse = readFromStream(inputStream);
            Log.v("makeHttpConversationId", JSONResponse);
        } catch (IOException e) {
            Log.e("QueryUtils", "IO Exception in url data download", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }

        return JSONResponse;
    }

    private static String makeHttpRequestToPostMessage(URL url, String from, String message, String key) throws IOException {
        String JSONResponse = "Placeholder";
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");

            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            urlConnection.setRequestProperty("Authorization", "BotConnector " + key);

            try {
                JSONObject jsonParam = new JSONObject();
                jsonParam.put("from", from);
                jsonParam.put("text", message);
                jsonParam.put("id", mConversationID + String.format("%07d", mWatermark + 1));
                OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
                out.write(jsonParam.toString());
                out.close();
            } catch (JSONException e) {
            }

            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(50000);
            urlConnection.connect();
            Log.v("makeHttpRequestPost", String.valueOf(urlConnection.getResponseCode()));
            Log.v("makeHttpRequestPost", "Connection Established");
            inputStream = urlConnection.getInputStream();
            JSONResponse = readFromStream(inputStream);
            Log.v("makeHttpRequestPost", JSONResponse);
        } catch (IOException e) {
            Log.e("QueryUtils", "IO Exception in url data download", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }

        return JSONResponse;
    }

    private static String makeHttpRequestToGetMessage(URL url, String key) throws IOException {

        String JSONResponse = "";
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");

            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            urlConnection.setRequestProperty("Authorization", "BotConnector " + key);

            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(50000);
            urlConnection.connect();
            Log.v("makeHTTPRequestGet", String.valueOf(urlConnection.getResponseCode()));
            Log.v("makeHTTPRequestGet", "Connection Established");
            inputStream = urlConnection.getInputStream();
            JSONResponse = readFromStream(inputStream);
            Log.v("makeHTTPRequestGet", JSONResponse);
        } catch (IOException e) {
            Log.e("QueryUtils", "IO Exception in url data download", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }

        return JSONResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            while (line != null) {
                output.append(line);
                line = bufferedReader.readLine();
            }
        }
        return output.toString();
    }
}
