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
import java.util.ArrayList;

public class GetTrainResponse {

    private static final String PBC_URL = "http://botguise8478.cloudapp.net/PBC";
    private static final String TRAIN_URL = "http://botguise8478.cloudapp.net/train";

    public GetTrainResponse() {
    }

    public static ArrayList<String> getMessage(String from, String to, String category, boolean train, String message) {
        URL url = createUrl(PBC_URL);
        String JSONResponse = "";

        try {
            JSONResponse = makeHttpRequest(url, from, to, category, train, message);
            Log.v("getMessage", JSONResponse);
        } catch (IOException e) {
            JSONResponse = null;
        }
        ArrayList<String> replies = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(JSONResponse);
            JSONArray reply = jsonObject.getJSONArray("reply");

            for (int i = 0; i < reply.length(); i++) {
                replies.add(reply.getString(i));
            }
        } catch (JSONException e) {
            Log.v("Parsing JSON", "Error in getting responses from JSON");
            replies = null;
        }

        return replies;
    }

    public static void changeMessage(String from, String to, String category, String statement1, String statement2) {
        URL url = createUrl(TRAIN_URL);

        try {
            makeHttpRequestToChange(url, from, to, category, statement1, statement2);
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

    private static String makeHttpRequest(URL url, String from, String to, String category, boolean train, String message) throws IOException {
        String JSONResponse = "";
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");

            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("from", from);
                jsonObject.put("to", to);
                jsonObject.put("category", category);
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

    private static void makeHttpRequestToChange(URL url, String from, String to, String category, String statement1, String statement2) throws IOException {
        HttpURLConnection urlConnection = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");

            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("from", from);
                jsonObject.put("to", to);
                jsonObject.put("category", category);
                jsonObject.put("statement1", statement1);
                jsonObject.put("statement2", statement2);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(urlConnection.getOutputStream());
                outputStreamWriter.write(jsonObject.toString());
                outputStreamWriter.close();
            } catch (JSONException e) {
                Log.v("makeHttpRequest", "Error in setting Parameters to POST request");
            }
            urlConnection.connect();
        } catch (IOException e) {
            Log.v("makeHttpRequest", "Error in getting connection");
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
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
