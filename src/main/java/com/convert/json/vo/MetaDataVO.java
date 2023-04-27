package com.json.convert.vo;

public class MetaDataVO {

    private String date;

    private String organ_name;

    private String organ_class;

    private String title;

    private String charge;

    private String word_cnt;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getOrgan_name() {
        return organ_name;
    }

    public void setOrgan_name(String organ_name) {
        this.organ_name = organ_name;
    }

    public String getOrgan_class() {
        return organ_class;
    }

    public void setOrgan_class(String organ_class) {
        this.organ_class = organ_class;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCharge() {
        return charge;
    }

    public void setCharge(String charge) {
        this.charge = charge;
    }

    public String getWord_cnt() {
        return word_cnt;
    }

    public void setWord_cnt(String word_cnt) {
        this.word_cnt = word_cnt;
    }

    @Override
    public String toString() {
        return "MetaDataVO{" +
                "date='" + date + '\'' +
                ", organ_name='" + organ_name + '\'' +
                ", organ_class='" + organ_class + '\'' +
                ", title='" + title + '\'' +
                ", charge='" + charge + '\'' +
                ", word_cnt='" + word_cnt + '\'' +
                '}';
    }
}
