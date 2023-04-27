package com.json.convert.vo;

public class UtteranceVO {


    private String id;

    private String form;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    @Override
    public String toString() {
        return "UtteranceVO{" +
                "id='" + id + '\'' +
                ", form='" + form + '\'' +
                '}';
    }
}
