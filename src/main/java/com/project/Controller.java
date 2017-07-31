package com.project;

import au.com.bytecode.opencsv.CSVWriter;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.web.bind.annotation.*;

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
    String path = "/home/nicola/Workspace/secretProject/src/main/resources/static/text.csv";
    List list;

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
        return cs.getJson(new java.io.File(path));
    }

    @RequestMapping(value = "/{owner}/{name}", method = RequestMethod.GET)
    @ResponseBody
    public Object getCsv(@PathVariable String owner, @PathVariable String name) throws Exception {
        list = cs.getJson(new java.io.File(path));
        for (int i = 0; i < list.size(); i++) {
            if (owner.equals(readJson(String.valueOf(list.get(i)), "owner"))) {
                if (name.equals(readJson(String.valueOf(list.get(i)), "name"))) {
                    builder.append(readJson(String.valueOf(list.get(i)), "secret").concat(" "));
                }
            }
        }
        String[] a = String.valueOf(builder).split(" ");
        builder.setLength(0);
        return a;
    }

    @RequestMapping(value = "/{owner}/{name}/{secret}", method = RequestMethod.POST)
    @ResponseBody
    public void postCsv(@PathVariable String owner, @PathVariable String name, @PathVariable String secret) throws IOException {
        CSVWriter writer = new CSVWriter(new FileWriter(path, true));
        String[] record = (owner + "," + name + "," + secret).split(",");
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
        js.rewrite(list,path);
    }

    @RequestMapping(value = "/{owner}/{name}/{secret}", method = RequestMethod.PATCH)
    public @ResponseBody
    void updateCsv(@PathVariable("owner") String owner, @PathVariable("name") String name, @PathVariable("secret") String secret) throws IOException {
        for (int i = 0; i < list.size(); i++) {
            if (owner.equals(readJson(String.valueOf(list.get(i)), "owner")) && name.equals(readJson(String.valueOf(list.get(i)), "name"))) {
                HashMap<String, String> map = (HashMap<String, String>) list.get(i);
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    if (entry.getKey().equals("secret")) {
                        entry.setValue(secret);
                        System.out.println("Keys: " + entry.getKey());
                        System.out.println("Values: " + entry.getValue());
                    }
                }
            }
        }
        js.rewrite(list,path);
    }

}
