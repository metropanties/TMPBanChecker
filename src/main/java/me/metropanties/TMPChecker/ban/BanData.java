package me.metropanties.TMPChecker.ban;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BanData {

    private String name;
    private String avatar;
    private Boolean banned;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date bannedUntil;

    public BanData() { }

    public BanData(String name, String avatar, Boolean banned, Date bannedUntil) {
        this.name = name;
        this.avatar = avatar;
        this.banned = banned;
        this.bannedUntil = bannedUntil;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setBanned(Boolean banned) {
        this.banned = banned;
    }

    public void setBannedUntil(Date bannedUntil) {
        this.bannedUntil = bannedUntil;
    }

    public String getName() {
        return name;
    }

    public String getAvatar() {
        return avatar;
    }

    public Boolean getBanned() {
        return banned;
    }

    public Date getBannedUntil() {
        return bannedUntil;
    }

}
