package com.ipms.arena.model;

import java.util.ArrayList;

/**
 * Created by t-ebcruz on 5/10/2016.
 */
public class Hourly
{
    private int h0;
    private int h1;
    private int h2;
    private int h3;
    private int h4;
    private int h5;
    private int h6;
    private int h7;
    private int h8;
    private int h9;
    private int h10;
    private int h11;
    private int h12;
    private int h13;
    private int h14;
    private int h15;
    private int h16;
    private int h17;
    private int h18;
    private int h19;
    private int h20;
    private int h21;
    private int h22;
    private int h23;

    public Hourly() {
    }

    public Hourly(int h0, int h1, int h2, int h3, int h4, int h5, int h6, int h7, int h8, int h9, int h10, int h11, int h12, int h13, int h14, int h15, int h16, int h17, int h18, int h19, int h20, int h21, int h22, int h23)
    {
        this.h10 = h10;
        this.h11 = h11;
        this.h12 = h12;
        this.h13 = h13;
        this.h14 = h14;
        this.h15 = h15;
        this.h16 = h16;
        this.h17 = h17;
        this.h18 = h18;
        this.h19 = h19;
        this.h20 = h20;
        this.h21 = h21;
        this.h22 = h22;
        this.h23 = h23;
        this.h7 = h7;
        this.h8 = h8;
        this.h9 = h9;
        this.h0 = h0;
        this.h1 = h1;
        this.h2 = h2;
        this.h3 = h3;
        this.h4 = h4;
        this.h5 = h5;
        this.h6 = h6;
    }

    public ArrayList<Integer> toArray()
    {
        ArrayList<Integer> set = new ArrayList<>();

        set.add(h0);
        set.add(h1);
        set.add(h2);
        set.add(h3);
        set.add(h4);
        set.add(h5);
        set.add(h6);
        set.add(h7);
        set.add(h8);
        set.add(h9);
        set.add(h10);
        set.add(h11);
        set.add(h12);
        set.add(h13);
        set.add(h14);
        set.add(h15);
        set.add(h16);
        set.add(h17);
        set.add(h18);
        set.add(h19);
        set.add(h20);
        set.add(h21);
        set.add(h22);
        set.add(h23);

        return set;
    }

    @Override
    public String toString() {
        return "Hourly{" +
                "h0=" + h0 +
                ", h1=" + h1 +
                ", h2=" + h2 +
                ", h3=" + h3 +
                ", h4=" + h4 +
                ", h5=" + h5 +
                ", h6=" + h6 +
                ", h7=" + h7 +
                ", h8=" + h8 +
                ", h9=" + h9 +
                ", h10=" + h10 +
                ", h11=" + h11 +
                ", h12=" + h12 +
                ", h13=" + h13 +
                ", h14=" + h14 +
                ", h15=" + h15 +
                ", h16=" + h16 +
                ", h17=" + h17 +
                ", h18=" + h18 +
                ", h19=" + h19 +
                ", h20=" + h20 +
                ", h21=" + h21 +
                ", h22=" + h22 +
                ", h23=" + h23 +
                '}';
    }
}
