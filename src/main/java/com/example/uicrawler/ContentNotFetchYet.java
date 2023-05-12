package com.example.uicrawler;

public class ContentNotFetchYet extends RuntimeException{
    public ContentNotFetchYet() {
        super("Webpage content haven't fetch yet");
    }
}
