package com.example.goe;

public class SessionData {

    private String timestamp;
    private double eto;
    private int amp;
    private double wh;
    private double cdi;

    public double getEto() {
        return eto;
    }

    public void setEto(double eto) {
        this.eto = eto;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getAmp() {
        return amp;
    }

    public void setAmp(int amp) {
        this.amp = amp;
    }

    public double getWh() {
        return wh;
    }

    public void setWh(double wh) {
        this.wh = wh;
    }

    public double getCdi() {
        return cdi;
    }

    public void setCdi(double cdi) {
        this.cdi = cdi;
    }
}
