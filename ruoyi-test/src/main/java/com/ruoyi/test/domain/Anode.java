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
    private String parentId;
    //曼哈顿算法 f = x+y;
    private Integer f;


    public Anode(Integer x, Integer y) {
        this.x = x;
        this.y = y;
        this.f= x+y;
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
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
}
