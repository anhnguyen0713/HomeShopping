package com.anhnguyen.homeshopping.model;

public class Channel {
    private int id;
    private String name;
    private String logo;
    private String homepage;
    private String hotline;
    private String description;

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getLogo(){
        return logo;
    }

    public void setLogo(String logo){
        this.logo = logo;
    }

    public String getHomepage(){
        return homepage;
    }

    public void setHomepage(String homepage){
        this.homepage = homepage;
    }

    public String getHotline(){
        return hotline;
    }

    public void setHotline(String hotline){
        this.hotline = hotline;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description){
        this.description = description;
    }
}
