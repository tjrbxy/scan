package com.example.scan.res;

import com.example.scan.Goods;

import java.util.ArrayList;
import java.util.List;

public class GoodsRes {

    private int status;
    private String message;
    private List<Goods> data = new ArrayList<>();

    public GoodsRes() {
    }

    public GoodsRes(int status, String message, List<Goods> goods) {
        this.status = status;
        this.message = message;
        this.data = goods;
    }

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

    public List<Goods> getData() {
        return data;
    }

    public void setData(List<Goods> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "GoodsRes{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
