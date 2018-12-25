package com.alixlp.scan.model;

import java.util.List;
import java.util.ArrayList;

public class GoodsResult {
    int status;
    String message;
    List<GoodsInfo> GoodsInfo = new ArrayList<>();

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

    public List<com.alixlp.scan.model.GoodsInfo> getGoodsInfo() {
        return GoodsInfo;
    }

    public void setGoodsInfo(List<com.alixlp.scan.model.GoodsInfo> goodsInfo) {
        GoodsInfo = goodsInfo;
    }
}
