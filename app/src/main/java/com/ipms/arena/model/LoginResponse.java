package com.ipms.arena.model;

/**
 * Created by t-ebcruz on 3/29/2016.
 */
public class LoginResponse
{
    public final int id;
    public final String username;
    public final String error;

    public LoginResponse(int id, String username, String error)
    {
        this.id = id;
        this.username = username;
        this.error = error;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getError() {
        return error;
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "error='" + error + '\'' +
                ", id=" + id +
                ", username='" + username + '\'' +
                '}';
    }
}