package com.example.webhd.model;


public class Text {
    private String user;
    private String pass;

    //setter&getter
    public String getUser(){
        return user;
    }
    public void setUser(String user){
        this.user = user;
    }
    public String getPass(){
        return pass;
    }
    public void setPass(String pass){
        this.pass = pass;
    }

    @Override
    public String toString() {
        return "Text{" +
                "user='" + user + '\'' +
                ", pass='" + pass + '\'' +
                '}';
    }
}
