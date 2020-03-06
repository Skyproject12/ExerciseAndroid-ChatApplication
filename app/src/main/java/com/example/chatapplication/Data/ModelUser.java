package com.example.chatapplication.Data;

public class ModelUser {
    String name;
    String email;
    String phone;
    String image;
    String cover;
    String uid;

    public ModelUser() {
    }

    public ModelUser(String name, String email, String phone, String image, String cover, String uid) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.image = image;
        this.cover = cover;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
