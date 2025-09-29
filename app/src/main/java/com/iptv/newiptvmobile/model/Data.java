package com.iptv.newiptvmobile.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Data {
    String title;
    String type;
    String id;
    String tid;
    String subid;
    String logo;
    String detail;
    String logo_landscape;
    String cover;
    String wallpaper;
    String rating;
    String runtime;
    String genre;
    String country;
    String year;
    String writer;
    String actor;
    String released;
    String background;
    String name;
    String sport_logo;
    String datetime;
    String team_a;
    String team_b;
    String event;
    String uid;
    String vipday;
    String point;
    String points;
    Boolean expire;
    String befor_vip;
    String after_vip;
    String befor_point;
    String after_point;
    String comment;
    boolean live;
    boolean watcheds;
    boolean update;
    String remotetv;
    String cid;
    List<SubData> data;
    List<Actorlist> actorlist;
    String livecat;
    boolean topten;
    int label;
    public Data(String title,
                String type,
                boolean update,
                String id,
                String logo,
                String detail,
                String logo_landscape,
                String cover,
                String wallpaper,
                String rating,
                String runtime,
                String genre,
                String country,
                String year,
                String writer,
                String actor,
                List<Actorlist> actorlist,
                String released) {
        this.title = title;
        this.type = type;
        this.update = update;
        this.id = id;
        this.logo = logo;
        this.detail = detail;
        this.logo_landscape = logo_landscape;
        this.cover = cover;
        this.wallpaper = wallpaper;
        this.rating = rating;
        this.runtime = runtime;
        this.genre = genre;
        this.country = country;
        this.year = year;
        this.writer = writer;
        this.actor = actor;
        this.released = released;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getBefor_point() {
        return befor_point;
    }

    public void setBefor_point(String befor_point) {
        this.befor_point = befor_point;
    }

    public String getAfter_point() {
        return after_point;
    }

    public void setAfter_point(String after_point) {
        this.after_point = after_point;
    }

    public int getLabel() {
        return label;
    }

    public void setLabel(int label) {
        this.label = label;
    }

    public boolean isTopten() {
        return topten;
    }

    public void setTopten(boolean topten) {
        this.topten = topten;
    }

    public List<Actorlist> getActorlist() {
        return actorlist;
    }

    public void setActorlist(List<Actorlist> actorlist) {
        this.actorlist = actorlist;
    }

    public String getLivecat() {
        return livecat;
    }

    public void setLivecat(String livecat) {
        this.livecat = livecat;
    }

    public String getRemotetv() {
        return remotetv;
    }

    public void setRemotetv(String remotetv) {
        this.remotetv = remotetv;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getBefor_vip() {
        return befor_vip;
    }

    public void setBefor_vip(String befor_vip) {
        this.befor_vip = befor_vip;
    }

    public String getAfter_vip() {
        return after_vip;
    }

    public void setAfter_vip(String after_vip) {
        this.after_vip = after_vip;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isUpdate() {
        return update;
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }

    public boolean isWatcheds() {
        return watcheds;
    }

    public void setWatcheds(boolean watcheds) {
        this.watcheds = watcheds;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getVipday() {
        return vipday;
    }

    public void setVipday(String vipday) {
        this.vipday = vipday;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    public Boolean getExpire() {
        return expire;
    }

    public void setExpire(Boolean expire) {
        this.expire = expire;
    }

    public boolean isLive() {
        return live;
    }

    public void setLive(boolean live) {
        this.live = live;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSport_logo() {
        return sport_logo;
    }

    public void setSport_logo(String sport_logo) {
        this.sport_logo = sport_logo;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getTeam_a() {
        return team_a;
    }

    public void setTeam_a(String team_a) {
        this.team_a = team_a;
    }

    public String getTeam_b() {
        return team_b;
    }

    public void setTeam_b(String team_b) {
        this.team_b = team_b;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getLogo_landscape() {
        return logo_landscape;
    }

    public void setLogo_landscape(String logo_landscape) {
        this.logo_landscape = logo_landscape;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getWallpaper() {
        return wallpaper;
    }

    public void setWallpaper(String wallpaper) {
        this.wallpaper = wallpaper;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getRuntime() {
        return runtime;
    }

    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public String getReleased() {
        return released;
    }

    public void setReleased(String released) {
        this.released = released;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getSubid() {
        return subid;
    }

    public void setSubid(String subid) {
        this.subid = subid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<SubData> getData() {
        return data;
    }

    public void setData(List<SubData> data) {
        this.data = data;
    }
}
