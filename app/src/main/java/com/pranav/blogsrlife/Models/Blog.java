package com.pranav.blogsrlife.Models;

public class Blog {
    private String title;
    private String desc;
    private String image;
    private String timeID;
    private String userid;

    public Blog(String title, String desc, String image, String timeID, String userid) {
        this.title = title;
        this.desc = desc;
        this.image = image;
        this.timeID = timeID;
        this.userid = userid;

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTimeID() {
        return timeID;
    }

    public void setTimeID(String timeID) {
        this.timeID = timeID;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }


}
