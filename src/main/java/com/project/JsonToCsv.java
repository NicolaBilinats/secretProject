package com.project;

/**
 * Created by nicola on 26.07.17.
 */

import java.io.File;
import java.io.IOException;
import java.util.*;

import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;


public class JsonToCsv {

    private void writeToFile(String csvString, String fileName) {
        try {
            FileUtils.write(new File(fileName), csvString);
        } catch (IOException e) {
            System.out.println("IOException: " + e);
        }
    }

    private String getCSV(List<Map<String, String>> flatJson) {
        return getCSV(flatJson, ",");
    }

    private String getCSV(List<Map<String, String>> flatJson, String separator) {
        Set<String> headers = collectHeaders(flatJson);
        String csvString = StringUtils.join(headers.toArray(), separator).concat("\n");

        for (Map<String, String> map : flatJson) {
            csvString = csvString.concat(getSeperatedColumns(headers, map, separator).concat("\n"));
        }
        return csvString;
    }

    private Set<String> collectHeaders(List<Map<String, String>> flatJson) {
        Set<String> headers = new LinkedHashSet<String>();

        for (Map<String, String> map : flatJson) {
            headers.addAll(map.keySet());
        }
        return headers;
    }

    private String getSeperatedColumns(Set<String> headers, Map<String, String> map, String separator) {
        List<String> items = new ArrayList<String>();
        for (String header : headers) {
            String value = map.get(header) == null ? "null" : map.get(header).replaceAll("[\\,\\;\\r\\n\\t\\s]+", " ");
            items.add(value);
        }

        return StringUtils.join(items.toArray(), separator);
    }

    public void rewrite(List list, String path) {
        String json = new Gson().toJson(list);
        List<Map<String, String>> flatJson = JSONFlattener.parseJson(json);
        writeToFile(getCSV(flatJson), path);
    }


}