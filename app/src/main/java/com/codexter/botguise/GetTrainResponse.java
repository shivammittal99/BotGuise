package com.codexter.botguise;

import android.util.Log;

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
import java.util.ArrayList;

public class GetTrainResponse {

    private static final String PBC_URL = "http://botguise3134.cloudapp.net/PBC";
    private static final String TRAIN_URL = "http://botguise3134.cloudapp.net/train";
    private static final String LINK_URL = "http://botguise3134.cloudapp.net/link";

    public GetTrainResponse() {
    }

    public static ArrayList<String> getMessage(String user, String part, boolean train, String message) {
        URL url = createUrl(PBC_URL);
        String JSONResponse = "";

        try {
            JSONResponse = makeHttpRequest(url, user, part, train, message);
            Log.v("getMessage", JSONResponse);
        } catch (IOException e) {
            JSONResponse = null;
        }
        ArrayList<String> replies = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(JSONResponse);
            String reply = jsonObject.getString("reply");
            replies.add(reply);
        } catch (JSONException e) {
            Log.v("Parsing JSON", "Error in getting responses user JSON");
            replies = null;
        }

        return replies;
    }

    public static void changeMessage(String user, String part, String prevblob, String currblob, String trainblob) {
        URL url = null;
        if (trainblob == null) {
            url = createUrl(LINK_URL);
        } else {
            url = createUrl(TRAIN_URL);
        }
        try {
            makeHttpRequestToChange(url, user, part, prevblob, currblob, trainblob);
        } catch (IOException e) {
        }
    }

    private static URL createUrl(String urlString) {
        try {
            return new URL(urlString);
        } catch (MalformedURLException e) {
            Log.v("MalformedUrlException", "true");
            return null;
        }
    }

    private static String makeHttpRequest(URL url, String user, String part, boolean train, String message) throws IOException {
        String JSONResponse = "";
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");

            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("user", user);
                jsonObject.put("part", part);
                jsonObject.put("train", train);
                jsonObject.put("message", message);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(urlConnection.getOutputStream());
                outputStreamWriter.write(jsonObject.toString());
                outputStreamWriter.close();
            } catch (JSONException e) {
                Log.v("makeHttpRequest", "Error in setting Parameters to POST request");
            }
            urlConnection.connect();

            inputStream = urlConnection.getInputStream();
            JSONResponse = readFromStream(inputStream);
        } catch (IOException e) {
            Log.v("makeHttpRequest", "Error in getting connection");
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

    private static void makeHttpRequestToChange(URL url, String user, String part, String prevblob, String currblob, String trainblob) throws IOException {
        HttpURLConnection urlConnection = null;
        String JSONResponse = "";
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");

            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("user", user);
                jsonObject.put("part", part);
                jsonObject.put("prevblob", prevblob);
                jsonObject.put("currblob", currblob);
                if (trainblob != null) {
                    jsonObject.put("trainblob", trainblob);
                }
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(urlConnection.getOutputStream());
                outputStreamWriter.write(jsonObject.toString());
                outputStreamWriter.close();
            } catch (JSONException e) {
                Log.v("makeHttpRequest", "Error in setting Parameters to POST request");
            }
            urlConnection.connect();
            inputStream = urlConnection.getInputStream();
            JSONResponse = readFromStream(inputStream);
            Log.v("Request change response", JSONResponse);
        } catch (IOException e) {
            Log.v("makeHttpRequest", "Error in getting connection");
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder response = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            while (line != null) {
                response.append(line);
                line = bufferedReader.readLine();
            }
        }
        return response.toString();
    }
}
