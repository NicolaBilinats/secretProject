package com.project;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Created by nicola on 24.07.17.
 */

@RestController
@RequestMapping(value = "/sender")
public class Controller {
    JsonToCsv js = new JsonToCsv();
    CsvToJson cs = new CsvToJson();
    HashMap<String, String> map;
    @Value("${pathCsv}")
    String pathCsv;
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
        File file = new File(pathCsv);
        Collection<String> collection = Files.lines(Paths.get(file.getAbsolutePath()))
                .collect(Collectors.toList());
        return collection;
    }

    @RequestMapping(value = "/{owner}/{name}", method = RequestMethod.GET)
    @ResponseBody
    public Object getCsv(@PathVariable String owner, @PathVariable String name) throws Exception {
        File file = new File(pathCsv);
        String[] record = (owner + "," + name).split(",");
        Collection<String> collection = Files.lines(Paths.get(file.getAbsolutePath()))
                .filter(value -> {
                    if (name.equals("*")) {
                        if (owner.equals("*")) {
                            return value.contains("");
                        } else {
                            return value.split(",")[0].equals(record[0]);
                        }
                    } else if (owner.equals("*")) {
                        return value.split(",")[1].equals(record[1]);
                    } else {
                        return value.split(",")[0].equals(record[0]) && value.split(",")[1].equals(record[1]);
                    }
                })
                .collect(Collectors.toList());
        return collection;
    }

    @RequestMapping(value = "/{owner}/{name}/{secret}", method = RequestMethod.POST)
    @ResponseBody
    public Collection<String> postCsv(@PathVariable String owner, @PathVariable String name, @PathVariable String secret) throws IOException {
        File file = new File(pathCsv);
        String record = (owner + "," + name + "," + secret);
        PrintWriter writer = new PrintWriter(new FileOutputStream(file, true));
        writer.println(record);
        writer.close();
        Collection<String> collection = Files.lines(Paths.get(file.getAbsolutePath()))
                .filter(value -> value.equals(record))
                .collect(Collectors.toList());
        return collection;
    }

    @RequestMapping(value = "/{owner}/{name}/{secret}", method = RequestMethod.DELETE)
    public @ResponseBody
    Collection<String> deleteCsv(@PathVariable("owner") String owner, @PathVariable("name") String name, @PathVariable("secret") String secret) throws Exception {
        File file = new File(pathCsv);
        list = cs.getJson(new java.io.File(pathCsv));
        for (int i = 0; i < list.size(); i++) {
            if (owner.equals(readJson(String.valueOf(list.get(i)), "owner")) && name.equals(readJson(String.valueOf(list.get(i)), "name")) && secret.equals(readJson(String.valueOf(list.get(i)), "secret"))) {
                list.remove(i);
                i--;
            }
        }
        js.rewrite(list, pathCsv);
        Collection<String> collection = Files.lines(Paths.get(file.getAbsolutePath()))
                .collect(Collectors.toList());
        return collection;
    }

    @RequestMapping(value = "/{owner}/{name}/{secret}", method = RequestMethod.PATCH)
    public @ResponseBody
    Collection<String> updateCsv(@PathVariable("owner") String owner, @PathVariable("name") String name, @PathVariable("secret") String secret) throws Exception {
        File file = new File(pathCsv);
        list = cs.getJson(new java.io.File(pathCsv));
        for (int i = 0; i < list.size(); i++) {
            if (owner.equals(readJson(String.valueOf(list.get(i)), "owner")) && name.equals(readJson(String.valueOf(list.get(i)), "name"))) {
                map = (HashMap<String, String>) list.get(i);
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    if (entry.getKey().equals("secret")) {
                        entry.setValue(secret);
                    }
                }
            }
        }
        js.rewrite(list, pathCsv);
        Collection<String> collection = Files.lines(Paths.get(file.getAbsolutePath()))
                .collect(Collectors.toList());
        return collection;
    }
}
