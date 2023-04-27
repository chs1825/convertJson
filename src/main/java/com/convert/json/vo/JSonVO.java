package com.json.convert.vo;


import java.util.List;

public class JSonVO {

    private String id;

    private MetaDataVO metaData;

    private List<UtteranceVO> utterance;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public MetaDataVO getMetaData() {
        return metaData;
    }

    public void setMetaData(MetaDataVO metaData) {
        this.metaData = metaData;
    }

    public List<UtteranceVO> getUtterance() {
        return utterance;
    }

    public void setUtterance(List<UtteranceVO> utterance) {
        this.utterance = utterance;
    }



    @Override
    public String toString() {
        return "JSonVO{" +
                "id='" + id + '\'' +
                ", metaData=" + metaData +
                ", utterance=" + utterance +
                '}';
    }
}
