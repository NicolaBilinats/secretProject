package com.example.demo;

import java.io.Serializable;

/**
 * Created by nicola on 26.07.17.
 */
public class Columns implements Serializable {

    private String owner;
    private String name;
    private String secret;
    private String value;

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString()
    {
        return "[owner=" + owner + ", name=" + name + ", secret=" + secret + ", value=" + value + "]";
    }
}
