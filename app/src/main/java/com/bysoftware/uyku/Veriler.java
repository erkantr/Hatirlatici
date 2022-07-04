package com.bysoftware.uyku;

public class Veriler {
    String hadis;
    String video1;
    String video2;

    public Veriler(String hadis,String video1,String video2) {
        this.hadis = hadis;
        this.video1 = video1;
        this.video2 = video2;
    }

    public Veriler(){

    }

    public String getHadis() {
        return hadis;
    }

    public void setHadis(String hadis) {
        this.hadis = hadis;
    }

    public String getVideo1() {
        return video1;
    }

    public void setVideo1(String video1) {
        this.video1 = video1;
    }

    public String getVideo2() {
        return video2;
    }

    public void setVideo2(String video2) {
        this.video2 = video2;
    }
}
