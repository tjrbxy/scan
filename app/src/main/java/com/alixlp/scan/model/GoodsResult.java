package com.alixlp.scan.model;

import com.alixlp.scan.json.Goods;

import java.util.List;
import java.util.ArrayList;

public class GoodsResult {
    int status;
    String message;
    List<Goods> GoodsInfo = new ArrayList<>();

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Goods> getGoodsInfo() {
        return GoodsInfo;
    }

    public void setGoodsInfo(List<Goods> goodsInfo) {
        GoodsInfo = goodsInfo;
    }
}
