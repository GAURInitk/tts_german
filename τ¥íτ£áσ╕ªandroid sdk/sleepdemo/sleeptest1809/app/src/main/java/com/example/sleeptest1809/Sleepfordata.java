package com.example.sleeptest1809;


import java.io.Serializable;

public class Sleepfordata implements Serializable {
    float startX;
    float startY;
    float stopX;
    float stopY;
    int index;//3-0 //深睡 0, //浅睡 1 //清醒2 //rem 3
    int count;//当前的数量
    String begintime;//开始时间
    String endtime;//结束时间
    int id=0;
    int color;//颜色值
    boolean isture=true;
    String P_INDEX=null;

    public String getP_INDEX() {
        return P_INDEX;
    }

    public void setP_INDEX(String p_INDEX) {
        P_INDEX = p_INDEX;
    }

    public Sleepfordata(float startXAxis, float yAxis, float endXAxis, float axis, int index) {
        this.startX = startXAxis;
        this.startY = yAxis;
        this.stopX = endXAxis;
        this.stopY = axis;
        this.index = index;
    }
    public Sleepfordata(int index, int color) {
        this.color = color;
        this.index = index;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Sleepfordata() {
    }


    public String getBegintime() {
        return begintime==null?"":begintime;
    }

    public void setBegintime(String begintime) {
        this.begintime = begintime;
    }

    public String getEndtime() {
        return endtime==null?"":endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
    public float getStartX() {
        return startX;
    }

    public void setStartX(float startX) {
        this.startX = startX;
    }

    public float getStartY() {
        return startY;
    }

    public void setStartY(float startY) {
        this.startY = startY;
    }

    public float getStopX() {
        return stopX;
    }

    public void setStopX(float stopX) {
        this.stopX = stopX;
    }

    public float getStopY() {
        return stopY;
    }

    public void setStopY(float stopY) {
        this.stopY = stopY;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isIsture() {
        return isture;
    }

    public void setIsture(boolean isture) {
        this.isture = isture;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }


}
