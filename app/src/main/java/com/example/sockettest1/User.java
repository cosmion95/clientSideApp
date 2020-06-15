package com.example.sockettest1;

public class User {
    private String id;
    private String nume;

    public User(String id, String nume) {
        this.id = id;
        this.nume = nume;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }
}
