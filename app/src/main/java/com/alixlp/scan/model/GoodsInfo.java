package com.alixlp.scan.model;

public class GoodsInfo {

    int goodsId;
    int goodsNum;
    String goodsName;

    public int getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(int goodsId) {
        this.goodsId = goodsId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public int getGoodsNum() {
        return goodsNum;
    }

    public void setGoodsNum(int goodsNum) {
        this.goodsNum = goodsNum;
    }

    @Override
    public String toString() {
        return "商品ID:" + goodsId + "装箱数：" + goodsNum + "商品名：" + goodsName;
    }
}
