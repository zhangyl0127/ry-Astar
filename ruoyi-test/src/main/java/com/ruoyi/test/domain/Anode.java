package com.ruoyi.test.domain;

import com.ruoyi.common.utils.uuid.UUID;

/** 节点对象 */
public class Anode {

    //uuid
   //private String id;
    private String id = UUID.randomUUID().toString();
    //x坐标
    private Integer x;
    //y坐标
    private Integer y;
    //g 距离起始点的步长
    private Integer g;
    //h 距离终点的步长
    private Integer h;
    private String parentId;
    //曼哈顿算法 f = x+y;
    private Integer f;


    public Anode(Integer x, Integer y) {
        this.x = x;
        this.y = y;
    }
    public Anode() {
    }
    public Integer getF() {
        return f;
    }

    public void setF(Integer f) {
        this.f = f;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
//        if (null!=this.y){
//            this.f = x + this.y;
//        }

    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
//        if (null!=this.x){
//            this.f = y + this.x;
//        }

    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public Integer getG() {
        return g;
    }

    public void setG(Integer g) {
        this.g = g;
    }

    public Integer getH() {
        return h;
    }

    public void setH(Integer h) {
        this.h = h;
    }
}
