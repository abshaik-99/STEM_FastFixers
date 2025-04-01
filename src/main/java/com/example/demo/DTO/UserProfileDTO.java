package com.example.demo.DTO;

import java.util.List;

import com.example.demo.models.User;

public class UserProfileDTO {

    private String name;
    private String headline;
    private String location;
    private String about;
    public UserProfileDTO(String name, String headline, String location, String about, String profilePicture,
            List<String> skills) {
        this.name = name;
        this.headline = headline;
        this.location = location;
        this.about = about;
        this.profilePicture = profilePicture;
        this.skills = skills;
    }

    public UserProfileDTO() {}

    public UserProfileDTO(User user) {
        this.name = user.getName();
        this.headline = user.getHeadline();
        this.location = user.getLocation();
        this.about = user.getAbout();
        this.profilePicture = user.getProfilePic();
        this.skills = user.getSkills();
    }

    private String profilePicture;
    private List<String> skills;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getHeadline() {
        return headline;
    }
    public void setHeadline(String headline) {
        this.headline = headline;
    }
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public String getAbout() {
        return about;
    }
    public void setAbout(String about) {
        this.about = about;
    }
    public String getProfilePicture() {
        return profilePicture;
    }
    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
    public List<String> getSkills() {
        return skills;
    }
    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

}
