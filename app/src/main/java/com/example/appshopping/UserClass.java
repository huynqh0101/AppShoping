package com.example.appshopping;

public class UserClass extends PersonClass{
    private String ID;

    public UserClass(String name, String email, String phoneNumber, String username, String address, String ID) {
        super(name, email, phoneNumber, username, address);
        this.ID = ID;
    }

    public UserClass(String ID) {
        this.ID = ID;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
}
