package com.example.demo;

import org.junit.Test;

import java.io.IOException;

/**
 * Created by nicola on 26.07.17.
 */
public class ControllerTest {
    Controller controller = new Controller();

    public ControllerTest() throws Exception {
    }

    @Test
    public void readJsonOwnerTest(){
        try {
            System.out.println(controller.readJson("{\n" +
                    "    \"owner\": \"16778240\",\n" +
                    "    \"name\": \"16779263\",\n" +
                    "    \"secret\": \"AU\",\n" +
                    "    \"value\": \"Australia\"\n" +
                    "  }", "owner"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
