package com.convert.json.vo;

import java.util.List;

public class InfoAboutJsonVO {
    private String path;
    private int dataSize;
    private int utterenceNum;
    private List<String> utterenceNumId;
    private int noEnterUtterenceNum;
    private List<String> noEnterUtterenceNumId;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getDataSize() {
        return dataSize;
    }

    public void setDataSize(int dataSize) {
        this.dataSize = dataSize;
    }

    public int getUtterenceNum() {
        return utterenceNum;
    }

    public void setUtterenceNum(int utterenceNum) {
        this.utterenceNum = utterenceNum;
    }

    public List<String> getUtterenceNumId() {
        return utterenceNumId;
    }

    public void setUtterenceNumId(List<String> utterenceNumId) {
        this.utterenceNumId = utterenceNumId;
    }

    public int getNoEnterUtterenceNum() {
        return noEnterUtterenceNum;
    }

    public void setNoEnterUtterenceNum(int noEnterUtterenceNum) {
        this.noEnterUtterenceNum = noEnterUtterenceNum;
    }

    public List<String> getNoEnterUtterenceNumId() {
        return noEnterUtterenceNumId;
    }

    public void setNoEnterUtterenceNumId(List<String> noEnterUtterenceNumId) {
        this.noEnterUtterenceNumId = noEnterUtterenceNumId;
    }

    @Override
    public String toString() {
        return "InfoAboutJsonVO{" +
                "path='" + path + '\'' +
                ", dataSize=" + dataSize +
                ", utterenceNum=" + utterenceNum +
                ", utterenceNumId=" + utterenceNumId +
                ", noEnterUtterenceNum=" + noEnterUtterenceNum +
                ", noEnterUtterenceNumId=" + noEnterUtterenceNumId +
                '}';
    }
}
