package com.example.geonchang.myemoticontestapp;

public class EmoticonList {

    int index;
    String name;
    int count;

    public EmoticonList(int index, String name, int count) {
        this.index = index;
        this.name = name;
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
