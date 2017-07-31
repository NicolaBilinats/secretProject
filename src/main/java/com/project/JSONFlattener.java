package com.project;

/**
 * Created by nicola on 27.07.17.
 */

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONFlattener {

    private static final Class<?> JSON_OBJECT = JSONObject.class;

    private static final Class<?> JSON_ARRAY = JSONArray.class;

    private static final Logger LOGGER = Logger.getLogger(JSONFlattener.class);

    public static List<Map<String, String>> parseJson(File file) {
        return parseJson(file, "UTF-8");
    }

    public static List<Map<String, String>> parseJson(File file, String encoding) {
        List<Map<String, String>> flatJson = null;
        String json = "";

        try {
            json = FileUtils.readFileToString(file, encoding);
            flatJson = parseJson(json);
        } catch (IOException e) {
            LOGGER.error("IOException: ", e);
        } catch (Exception ex) {
            LOGGER.error("Exception: ", ex);
        }

        return flatJson;
    }

    public static List<Map<String, String>> parseJson(String json) {
        List<Map<String, String>> flatJson = null;

        try {
            JSONObject jsonObject = new JSONObject(json);
            flatJson = new ArrayList<Map<String, String>>();
            flatJson.add(parse(jsonObject));
        } catch (JSONException je) {
            flatJson = handleAsArray(json);
        }

        return flatJson;
    }

    public static Map<String, String> parse(JSONObject jsonObject) throws JSONException {
        Map<String, String> flatJson = new LinkedHashMap<String, String>();
        flatten(jsonObject, flatJson, "");

        return flatJson;
    }

    public static List<Map<String, String>> parse(JSONArray jsonArray) throws JSONException {
        JSONObject jsonObject = null;
        List<Map<String, String>> flatJson = new ArrayList<Map<String, String>>();
        int length = jsonArray.length();

        for (int i = 0; i < length; i++) {
            try {
                jsonObject = jsonArray.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Map<String, String> stringMap = parse(jsonObject);
            flatJson.add(stringMap);
        }

        return flatJson;
    }

    private static List<Map<String, String>> handleAsArray(String json) {
        List<Map<String, String>> flatJson = null;

        try {
            JSONArray jsonArray = new JSONArray(json);
            flatJson = parse(jsonArray);
        } catch (Exception e) {
            // throw new Exception("Json might be malformed");
            LOGGER.error("JSON might be malformed, Please verify that your JSON is valid");
        }

        return flatJson;
    }

    private static void flatten(JSONObject obj, Map<String, String> flatJson, String prefix) throws JSONException {
        Iterator<?> iterator = obj.keys();
        String _prefix = prefix != "" ? prefix + "." : "";

        while (iterator.hasNext()) {
            String key = iterator.next().toString();

            if (obj.get(key).getClass() == JSON_OBJECT) {
                JSONObject jsonObject = (JSONObject) obj.get(key);
                flatten(jsonObject, flatJson, _prefix + key);
            } else if (obj.get(key).getClass() == JSON_ARRAY) {
                JSONArray jsonArray = (JSONArray) obj.get(key);

                if (jsonArray.length() < 1) {
                    continue;
                }

                flatten(jsonArray, flatJson, _prefix + key);
            } else {
                String value = obj.get(key).toString();

                if (value != null && !value.equals("null")) {
                    flatJson.put(_prefix + key, value);
                }
            }
        }

    }

    private static void flatten(JSONArray obj, Map<String, String> flatJson, String prefix) throws JSONException {
        int length = obj.length();

        for (int i = 0; i < length; i++) {
            if (obj.get(i).getClass() == JSON_ARRAY) {
                JSONArray jsonArray = (JSONArray) obj.get(i);

                if (jsonArray.length() < 1) {
                    continue;
                }

                flatten(jsonArray, flatJson, prefix + "[" + i + "]");
            } else if (obj.get(i).getClass() == JSON_OBJECT) {
                JSONObject jsonObject = (JSONObject) obj.get(i);
                flatten(jsonObject, flatJson, prefix + "[" + (i + 1) + "]");
            } else {
                String value = obj.get(i).toString();

                if (value != null) {
                    flatJson.put(prefix + "[" + (i + 1) + "]", value);
                }
            }
        }
    }
}
