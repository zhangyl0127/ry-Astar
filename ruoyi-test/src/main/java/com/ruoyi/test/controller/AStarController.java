package com.ruoyi.test.controller;


import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.bean.BeanUtils;
import com.ruoyi.test.domain.Anode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/AStar")
public class AStarController {


    @GetMapping("/get")
    public AjaxResult getRoute(Anode startNode,Anode endNode){
        startNode = new Anode();
        startNode.setX(3);
        startNode.setY(5);
        endNode = new Anode();
        startNode.setParentId("0");//代表是起点，父顶端
        endNode.setX(8);
        endNode.setY(4);
        boolean sBool = checkNode(startNode);
        boolean eBool = checkNode(endNode);
        if (!sBool || !eBool){
            return AjaxResult.error("节点传入格式校验失败");
        }
        //定义一个开列表存放待检查的节点点位
        List<Anode> openList = new ArrayList<>();
        //定义一个关列表存放不再需要的点位
        List<Anode> closeList = new ArrayList<>();
        //定义一个存放障碍物的节点点位
        List<Anode> hinderList = new ArrayList<>();
        Anode anode1 = new Anode(6, 3);
        Anode anode2 = new Anode(6, 4);
        Anode anode3 = new Anode(6, 5);
        Anode anode4 = new Anode(6, 6);
        Anode anode5 = new Anode(6, 7);
        Anode anode6 = new Anode(6, 8);
        hinderList.add(anode1);
        hinderList.add(anode2);
        hinderList.add(anode3);
        hinderList.add(anode4);
        hinderList.add(anode5);
        hinderList.add(anode6);
        Anode anode = setNodeByNode(startNode, endNode, openList, closeList, hinderList);


        //以给定一个起点开始


        return AjaxResult.success("A->B->C");
    }

    /**对传进去的参数节点 做个简单的校验*/
    public boolean checkNode(Anode anode){
        if (null==anode.getX() || null==anode.getY()){
            return false;
        }
        //简单的判断下x，y测试的坐标不能太长
        if (anode.getX() >20 || anode.getY() >20){
            return false;
        }
        return true;
    }

    /***/
    public Anode setNodeByNode(Anode anode,Anode endNode,List<Anode> openList,List<Anode> closeList,List<Anode> hinderList){
        Integer x = anode.getX();
        Integer y = anode.getY();

        Anode returnNode = new Anode();
        //将anode相邻的4个点加入到openlist
        if (x+1<=20){
            Anode right_node = new Anode(x+1,y);
            calculateAdjoinNode(right_node, anode,endNode,openList,closeList, hinderList);
        }
        if (y-1>=0){
            Anode down_node = new Anode(x,y-1);
            calculateAdjoinNode(down_node, anode,endNode,openList,closeList, hinderList);
        }
        if (x-1>=0){
            Anode left_node = new Anode(x-1,y);
            calculateAdjoinNode(left_node, anode,endNode,openList,closeList, hinderList);
        }
        if (y+1<=20){
            Anode up_node = new Anode(x,y+1);
            calculateAdjoinNode(up_node, anode,endNode,openList,closeList, hinderList);
        }
        //按F的从小到大排序
        openList.sort(new Comparator<Anode>() {
            @Override
            public int compare(Anode o1, Anode o2) {
                return o1.getF()-o2.getF();
            }
        });
       // openList.stream().sorted(Comparator.comparing(Anode::getF)).collect(Collectors.toList());
        //第一个点丢到无用列表
        closeList.add(anode);


        //取出第一个点
        Anode nextNode = openList.get(0);


        for (Anode openNode : openList) {
            if (endNode.getX()==openNode.getX() && endNode.getY() == openNode.getY()) {
                returnNode = openNode;
                break;
            }else{
                 setNodeByNode(nextNode,endNode,openList,closeList,hinderList);
            }
        }
        return returnNode;
    }

    //判断对象的一个属性是否存在该对象的List中
    public static  boolean judgeExit(Anode anode, List<Anode> anodeList){
        if(anodeList.stream().filter(w->w.getX()==anode.getX() && w.getY()==anode.getY()).findAny().isPresent()){
            return true;
        }else{
            return false;
        }
    }

    //递归根据当前节点获取距离原节点startNode的x值
    public Integer getXByStartNodeToCurrentNode(Anode anode,List<Anode> openList,List<Anode> closeList){

        Integer finalX = 0;
        String parentId = anode.getParentId();

        Anode nextNode = null;
        if (!"0".equals(parentId)){
            for (Anode openNode : openList) {
                if (openNode.getId().equals(parentId)){
                    nextNode = new Anode();
                    BeanUtils.copyProperties(openNode,nextNode);
                   Double x = Math.sqrt(Math.pow(Math.abs(openNode.getX() - anode.getX()), 2));
                    finalX = finalX + x.intValue();
                    break;
                }
            }
            for (Anode closeNode : closeList) {
                if (closeNode.getId().equals(parentId)){
                    nextNode = new Anode();
                    BeanUtils.copyProperties(closeNode,nextNode);
                    finalX = finalX + closeNode.getX();
                    break;
                }
            }
            getXByStartNodeToCurrentNode(nextNode,openList,closeList);
        }
        //代表到了起始节点startNode了
        return finalX;
    }
    public static void main(String[] args) {
        Anode anode = new Anode(2, 3);
        Anode anode2 = new Anode(12, 3);
        List<Anode> objects = new ArrayList<>();
        objects.add(anode);
        boolean b = judgeExit(anode2, objects);
        System.out.println(b);
    }

    //计算相邻节点的X Y F
    public void calculateAdjoinNode(Anode calculatedAnode,Anode lastNode,Anode endNode,List<Anode> openList,List<Anode> closeList,
                                    List<Anode> hinderList){
        //1.判断邻居节点是否在closeList中，是的话忽略
        if (closeList.size() != 0){
            for (int i = 0; i < closeList.size(); i++) {
                if (!(closeList.get(i).getX()==calculatedAnode.getX() && closeList.get(i).getY()==calculatedAnode.getY())){
                    if (i==closeList.size()-1){
                        // 如果邻居已经在 Open List 中（即该邻居已有父节点），计算从当前节点移动到该邻居是否能使其得到更小的 x 值。
                        // 如果能，则把该邻居的父节点重设为当前节点，并更新其 x 和 F 值。
                        if (judgeExit(calculatedAnode,openList)){
                            //递归寻找当前节点的所有父亲节点的 x的长度和
                            Integer xByStartNodeToCurrentNode = getXByStartNodeToCurrentNode(lastNode, openList, closeList);
                            Integer rightNodeX = calculatedAnode.getX();
                            //比较当前节点到起始的x和右节点的x大小
                            if (rightNodeX >= xByStartNodeToCurrentNode+1){
                                //f会自动计算
                                calculatedAnode.setX(xByStartNodeToCurrentNode+1);
                                calculatedAnode.setParentId(lastNode.getId());
                            }

                        }else{//3.如果邻居不在 Open List 中，计算 x、y、F，设置父节点，并将其加入 Open List
                            for (int j = 0; j < hinderList.size(); j++) {
                                if (!(calculatedAnode.getX() == hinderList.get(j).getX() &&
                                        calculatedAnode.getY() == hinderList.get(j).getY())){
                                    if (j == hinderList.size()-1){
                                        //设置父亲节点
                                        calculatedAnode.setParentId(lastNode.getId());
                                        openList.add(calculatedAnode);
//                                    if (calculatedAnode.getX()==endNode.getX() && calculatedAnode.getY()==endNode.getY()){
//                                        return ;
//                                    }
//                                    break;
                                    }
                                }
                            }

                        }

                    }
                }
            }
        }else{
            // 如果邻居已经在 Open List 中（即该邻居已有父节点），计算从当前节点移动到该邻居是否能使其得到更小的 x 值。
            // 如果能，则把该邻居的父节点重设为当前节点，并更新其 x 和 F 值。
            if (judgeExit(calculatedAnode,openList)){
                //递归寻找当前节点的所有父亲节点的 x的长度和
                Integer xByStartNodeToCurrentNode = getXByStartNodeToCurrentNode(lastNode, openList, closeList);
                Integer rightNodeX = calculatedAnode.getX();
                //比较当前节点到起始的x和右节点的x大小
                if (rightNodeX >= xByStartNodeToCurrentNode+1){
                    //f会自动计算
                    calculatedAnode.setX(xByStartNodeToCurrentNode+1);
                    calculatedAnode.setParentId(lastNode.getId());
                }

            }else{//3.如果邻居不在 Open List 中，计算 x、y、F，设置父节点，并将其加入 Open List
                for (int j = 0; j < hinderList.size(); j++) {
                    if (!(calculatedAnode.getX() == hinderList.get(j).getX() &&
                            calculatedAnode.getY() == hinderList.get(j).getY())){
                        if (j == hinderList.size()-1){
                            //设置父亲节点
                            calculatedAnode.setParentId(lastNode.getId());
                            openList.add(calculatedAnode);
//                                    if (calculatedAnode.getX()==endNode.getX() && calculatedAnode.getY()==endNode.getY()){
//                                        return ;
//                                    }
//                                    break;
                        }
                    }
                }

            }
        }

    }
}
