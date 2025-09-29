package com.iptv.newiptvmobile.model;

import java.util.List;

public class Api {
    String status;
    boolean promotion;
    String message;
    String logo;
    String url;
    String price;
    String account;
    String image;
    String notice;
    int extime;
    boolean favorite;
    List<Data> data;
    List<Lists> lists;
    List<ListSound> listsound;
    String id;
    String type;
    String token;
    List<Row> rowone;
    List<Row> rowtwo;
    List<Row> rowthree;


    public boolean isPromotion() {
        return promotion;
    }

    public void setPromotion(boolean promotion) {
        this.promotion = promotion;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public List<Row> getRowone() {
        return rowone;
    }

    public void setRowone(List<Row> rowone) {
        this.rowone = rowone;
    }

    public List<Row> getRowtwo() {
        return rowtwo;
    }

    public void setRowtwo(List<Row> rowtwo) {
        this.rowtwo = rowtwo;
    }

    public List<Row> getRowthree() {
        return rowthree;
    }

    public void setRowthree(List<Row> rowthree) {
        this.rowthree = rowthree;
    }

    public List<ListSound> getListsound() {
        return listsound;
    }

    public void setListsound(List<ListSound> listsound) {
        this.listsound = listsound;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getExtime() {
        return extime;
    }

    public void setExtime(int extime) {
        this.extime = extime;
    }

    public List<Lists> getLists() {
        return lists;
    }

    public void setLists(List<Lists> lists) {
        this.lists = lists;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }
}
