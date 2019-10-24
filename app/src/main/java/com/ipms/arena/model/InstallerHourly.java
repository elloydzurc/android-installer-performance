package com.ipms.arena.model;

import java.util.ArrayList;

/**
 * Created by t-ebcruz on 5/10/2016.
 */
public class InstallerHourly
{
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

    private int r7;
    private int r8;
    private int r9;
    private int r10;
    private int r11;
    private int r12;
    private int r13;
    private int r14;
    private int r15;
    private int r16;
    private int r17;

    public InstallerHourly() {
    }

    public void setH10(int h10) {
        this.h10 = h10;
    }

    public void setH11(int h11) {
        this.h11 = h11;
    }

    public void setH12(int h12) {
        this.h12 = h12;
    }

    public void setH13(int h13) {
        this.h13 = h13;
    }

    public void setH14(int h14) {
        this.h14 = h14;
    }

    public void setH15(int h15) {
        this.h15 = h15;
    }

    public void setH16(int h16) {
        this.h16 = h16;
    }

    public void setH17(int h17) {
        this.h17 = h17;
    }

    public void setH7(int h7) {
        this.h7 = h7;
    }

    public void setH8(int h8) {
        this.h8 = h8;
    }

    public void setH9(int h9) {
        this.h9 = h9;
    }

    public void setR10(int r10) {
        this.r10 = r10;
    }

    public void setR11(int r11) {
        this.r11 = r11;
    }

    public void setR12(int r12) {
        this.r12 = r12;
    }

    public void setR13(int r13) {
        this.r13 = r13;
    }

    public void setR14(int r14) {
        this.r14 = r14;
    }

    public void setR15(int r15) {
        this.r15 = r15;
    }

    public void setR16(int r16) {
        this.r16 = r16;
    }

    public void setR17(int r17) {
        this.r17 = r17;
    }

    public void setR7(int r7) {
        this.r7 = r7;
    }

    public void setR8(int r8) {
        this.r8 = r8;
    }

    public void setR9(int r9) {
        this.r9 = r9;
    }

    public ArrayList<Integer> getClosed()
    {
        ArrayList<Integer> closed = new ArrayList<>();

        closed.add(h7);
        closed.add(h8);
        closed.add(h9);
        closed.add(h10);
        closed.add(h11);
        closed.add(h12);
        closed.add(h13);
        closed.add(h14);
        closed.add(h15);
        closed.add(h16);
        closed.add(h17);

        return closed;
    }

    public ArrayList<Integer> getRso()
    {
        ArrayList<Integer> rso = new ArrayList<>();

        rso.add(r7);
        rso.add(r8);
        rso.add(r9);
        rso.add(r10);
        rso.add(r11);
        rso.add(r12);
        rso.add(r13);
        rso.add(r14);
        rso.add(r15);
        rso.add(r16);
        rso.add(r17);

        return rso;
    }

    @Override
    public String toString() {
        return "InstallerHourly{" +
                "h10=" + h10 +
                ", h7=" + h7 +
                ", h8=" + h8 +
                ", h9=" + h9 +
                ", h11=" + h11 +
                ", h12=" + h12 +
                ", h13=" + h13 +
                ", h14=" + h14 +
                ", h15=" + h15 +
                ", h16=" + h16 +
                ", h17=" + h17 +
                ", r7=" + r7 +
                ", r8=" + r8 +
                ", r9=" + r9 +
                ", r10=" + r10 +
                ", r11=" + r11 +
                ", r12=" + r12 +
                ", r13=" + r13 +
                ", r14=" + r14 +
                ", r15=" + r15 +
                ", r16=" + r16 +
                ", r17=" + r17 +
                '}';
    }
}
