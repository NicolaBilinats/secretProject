package com.example.demo;

/**
 * Created by nicola on 26.07.17.
 */
import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.json.CDL;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonToCsv {
    public void getCsvFromJson(String jsonString){
        JSONObject output;
        try {
            output = new JSONObject(jsonString);
            System.out.println("Output: "+ output);
            JSONArray docs = output.getJSONArray("$");
            File file=new File("//home/nicola/Workspace/gs-consuming-rest/secretProject/src/main/resources/static/country.csv");
            String csv = CDL.toString(docs);
            FileUtils.writeStringToFile(file, csv);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}