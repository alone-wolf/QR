package com.wh.qr;

import android.util.Log;

public class UrlBuilder {

    public static class BaseUrl{
        private String protocol;
        private String host;
        private String port;

        public BaseUrl setProtocol(String protocol) {
            this.protocol = protocol;
            return this;
        }

        public BaseUrl setHost(String host) {
            this.host = host;
            return this;
        }

        public BaseUrl setPort(String port) {
            this.port = port;
            return this;
        }

        public StringBuilder ToBaseUrl(){
            StringBuilder stringBuilder = new StringBuilder(protocol).append("://").append(host);
            if(port!=null){
                stringBuilder.append(":").append(port);
            }
            return stringBuilder;
        }
    }
    private StringBuilder stringBuilder;

    public UrlBuilder setBaseUrl(StringBuilder baseUrlStringBuilder) {
        this.stringBuilder = baseUrlStringBuilder;
        return this;
    }

    public UrlBuilder setBaseUrl(String baseUrl){
        this.stringBuilder = new StringBuilder(baseUrl);
        return this;
    }

    public UrlBuilder addPath(String path) {
        stringBuilder.append("/").append(path);
        return this;
    }

    public UrlBuilder addFirstArg(String key,String value){
        stringBuilder.append("?").append(key).append("=").append(value);
        return this;
    }

    public UrlBuilder addMoreArg(String key,String value){
        stringBuilder.append("&").append(key).append("=").append(value);
        return this;
    }

    public UrlBuilder addOthers(String others){
        stringBuilder.append(others);
        return this;
    }

    public String getString(){
        return stringBuilder.toString();
    }

    public String getString(String TAG){
        String tmp = stringBuilder.toString();
        Log.d(TAG, "getString: "+tmp);
        return tmp;
    }

}
