package com.wh.qr;

public class RegexMatcher {
    private RegexMatcher instance;

    public RegexMatcher(){

    }

    public RegexMatcher getInstance(){
        if(instance==null){
            instance = new RegexMatcher();
        }
        return instance;
    }
}
