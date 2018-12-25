package com.alixlp.scan;

/**
 * Created by Administrator on 2018-09-28.
 */

public class CodeInfo {
    private int id;
    private int goodsId;
    private String boxNum;
    private String codeNum;


    public CodeInfo(int id, String goodsId, String boxNum, String codeNum) {
    }

    public CodeInfo(int id, int goodsId, String boxNum, String codeNum) {
        this.id = id;
        this.goodsId = goodsId;
        this.boxNum = boxNum;
        this.codeNum = codeNum;
    }

    public CodeInfo(int id, String boxNum, String codeNum) {
        this.id = id;
        this.boxNum = boxNum;
        this.codeNum = codeNum;
    }

    public int getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(int goodsId) {
        this.goodsId = goodsId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBoxNum() {
        return boxNum;
    }

    public void setBoxNum(String boxNum) {
        this.boxNum = boxNum;
    }

    public String getCodeNum() {
        return codeNum;
    }

    public void setCodeNum(String codeNum) {
        this.codeNum = codeNum;
    }

    @Override
    public String toString() {
        return "CodeInfo{" +
                "id=" + id +
                ", goodsId=" + goodsId +
                ", boxNum='" + boxNum + '\'' +
                ", codeNum='" + codeNum + '\'' +
                '}';
    }
}
