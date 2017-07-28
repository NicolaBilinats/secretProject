package com.example.demo;

import au.com.bytecode.opencsv.CSVWriter;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Created by nicola on 24.07.17.
 */

@RestController
@RequestMapping(value = "/sender")
public class Controller {
    JsonToCsv js = new JsonToCsv();
    CsvToJson cs = new CsvToJson();
    StringBuilder builder = new StringBuilder();
    List list = cs.getJson(new java.io.File("/home/nicola/Workspace/gs-consuming-rest/secretProject/src/main/resources/static/country.csv"));

    public Controller() throws Exception {
    }

    public String readJson(String json, String column) throws IOException {
        JsonParser parser = new JsonParser();
        JsonElement jsonElement = parser.parse(json);
        JsonObject rootObject = jsonElement.getAsJsonObject();
        String message = rootObject.get(column).getAsString();
        return message;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ResponseBody
    public Object getFullCsv() throws Exception {
        return cs.getJson(new java.io.File("/home/nicola/Workspace/gs-consuming-rest/secretProject/src/main/resources/static/country.csv"));
    }

    @RequestMapping(value = "/{owner}/{name}", method = RequestMethod.GET)
    @ResponseBody
    public Object getCsv(@PathVariable String owner, @PathVariable String name) throws Exception {
        for (int i = 0; i < list.size(); i++) {
            if (owner.equals(readJson(String.valueOf(list.get(i)), "owner"))) {
                if (name.equals(readJson(String.valueOf(list.get(i)), "name"))) {
                    System.out.println(readJson(String.valueOf(list.get(i)), "secret").concat(" "));
                    builder.append(readJson(String.valueOf(list.get(i)), "secret").concat(" "));
                }
            }
        }
        String[] a = String.valueOf(builder).split(" ");
        builder.setLength(0);
        return a;
    }

    @RequestMapping(value = "/{owner}/{name}", method = RequestMethod.POST)
    @ResponseBody
    public void postCsv(@PathVariable String owner, @PathVariable String name) throws IOException {
        String csv = "/home/nicola/Workspace/gs-consuming-rest/secretProject/src/main/resources/static/country.csv";
        CSVWriter writer = new CSVWriter(new FileWriter(csv, true));
        String[] record = (owner + "," + name + ",null,null").split(",");
        writer.writeNext(record);
        writer.close();
    }

    @RequestMapping(value = "/{owner}/{name}/{secret}", method = RequestMethod.DELETE)
    public @ResponseBody
    void deleteCsv(@PathVariable("owner") String owner, @PathVariable("name") String name, @PathVariable("secret") String secret) throws IOException {

        for (int i = 0; i < list.size(); i++) {
            if (owner.equals(readJson(String.valueOf(list.get(i)), "owner")) && name.equals(readJson(String.valueOf(list.get(i)), "name")) && secret.equals(readJson(String.valueOf(list.get(i)), "secret"))) {
                list.remove(i);
            }
        }
        String json = new Gson().toJson(list);
        List<Map<String, String>> flatJson = JSONFlattener.parseJson(json);
        writeToFile(getCSV(flatJson), "/home/nicola/Workspace/gs-consuming-rest/secretProject/src/main/resources/static/country.csv");

    }

    @RequestMapping(value = "/{owner}/{name}/{secret}", method = RequestMethod.PATCH)
    public @ResponseBody
    void updateCsv(@PathVariable("owner") String owner, @PathVariable("name") String name, @PathVariable("secret") String secret) throws IOException {
        for (int i = 0; i < list.size(); i++) {
            if (owner.equals(readJson(String.valueOf(list.get(i)), "owner")) && name.equals(readJson(String.valueOf(list.get(i)), "name")) && secret.equals(readJson(String.valueOf(list.get(i)), "secret"))) {
                System.out.println(list.get(i));
            }
        }
    }


    public void writeToFile(String csvString, String fileName) {
        try {
            FileUtils.write(new File(fileName), csvString);
        } catch (IOException e) {
            System.out.println("CSVWriter#writeToFile(csvString, fileName) IOException: " + e);
        }
    }

    public String getCSV(List<Map<String, String>> flatJson) {
        return getCSV(flatJson, ",");
    }

    public String getCSV(List<Map<String, String>> flatJson, String separator) {
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
            String value = map.get(header) == null ? "" : map.get(header).replaceAll("[\\,\\;\\r\\n\\t\\s]+", " ");
            items.add(value);
        }

        return StringUtils.join(items.toArray(), separator);
    }


}
