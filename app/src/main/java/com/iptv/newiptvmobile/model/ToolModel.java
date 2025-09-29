package com.iptv.newiptvmobile.model;

public class ToolModel {
    int ic_w;
    int ic_b;
    String titleEN;
    String titleTH;
    String type;
    String lang;

    public ToolModel(int ic_w, int ic_b, String titleTH, String type, String lang,String titleEN) {
        this.ic_w = ic_w;
        this.ic_b = ic_b;
        this.titleEN = titleEN;
        this.titleTH = titleTH;
        this.type = type;
        this.lang = lang;
    }

    public int getIc_w() {
        return ic_w;
    }

    public void setIc_w(int ic_w) {
        this.ic_w = ic_w;
    }

    public int getIc_b() {
        return ic_b;
    }

    public void setIc_b(int ic_b) {
        this.ic_b = ic_b;
    }

    public String getTitleEN() {
        return titleEN;
    }

    public void setTitleEN(String titleEN) {
        this.titleEN = titleEN;
    }

    public String getTitleTH() {
        return titleTH;
    }

    public void setTitleTH(String titleTH) {
        this.titleTH = titleTH;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }
}
