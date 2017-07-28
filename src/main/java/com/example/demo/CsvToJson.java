package com.example.demo;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;


/**
 * Created by nicola on 26.07.17.
 */
public class CsvToJson {
    public List<Map<?, ?>> getJson(File input) throws Exception {
        List<Map<?, ?>> data = readObjectsFromCsv(input);
        return data;
    }

    public List<Map<?, ?>> readObjectsFromCsv(File file) throws IOException {
        CsvSchema bootstrap = CsvSchema.emptySchema().withHeader();
        CsvMapper csvMapper = new CsvMapper();
        MappingIterator<Map<?, ?>> mappingIterator = csvMapper.reader(Map.class).with(bootstrap).readValues(file);
        return mappingIterator.readAll();
    }
}
