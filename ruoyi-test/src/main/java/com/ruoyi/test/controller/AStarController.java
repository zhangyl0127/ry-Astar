package com.ruoyi.test.controller;


import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.bean.BeanUtils;
import com.ruoyi.test.domain.Anode;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.crypto.dsig.keyinfo.KeyInfo;
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
        startNode.setY(8);
        startNode.setG(0);
        endNode = new Anode();
        startNode.setParentId("0");//代表是起点，父顶端
       endNode.setX(3);
        endNode.setY(1);
        endNode.setH(0);
        startNode.setH(Math.abs(endNode.getY()-startNode.getY() )+ Math.abs(endNode.getX()-startNode.getX()));
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
        hinderList = getTesthinderList(hinderList);

        Anode anode = setNodeByNode(startNode, endNode, openList, closeList, hinderList);

        List<Anode> searchList = new ArrayList<>();
        searchList.addAll(openList);
        searchList.addAll(closeList);
        String route = "";
        String s = getRoute(anode.getParentId(), searchList, route,startNode);

        //以给定一个起点开始


        return AjaxResult.success(s);
    }

    public String getRoute(String partentId,List<Anode> searchList,String route,Anode startNode){
        for (Anode anode7 : searchList) {
            Integer x = anode7.getX();
            Integer y = anode7.getY();
            if (anode7.getId().equals(partentId)){
                route = route + "["+x+","+y+"]->";
                partentId = anode7.getParentId();
                break;
            }
        }
        if (partentId.equals("0")){
            return route;
//            return route = route + "["+startNode.getX()+","+startNode.getY()+"]->";
        }
        return getRoute(partentId,searchList,route,startNode);
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
        Integer g = anode.getG();

        Anode returnNode = new Anode();
        //将anode相邻的4个点加入到openlist
        if (x+1<20){
            Anode right_node = new Anode(x+1,y);
            calculateAdjoinNode(right_node, anode,endNode,openList,closeList, hinderList);
        }
        if (y-1>0){
            Anode down_node = new Anode(x,y-1);
            calculateAdjoinNode(down_node, anode,endNode,openList,closeList, hinderList);
        }
        if (x-1>0){
            Anode left_node = new Anode(x-1,y);
            calculateAdjoinNode(left_node, anode,endNode,openList,closeList, hinderList);
        }
        if (y+1<20){
            Anode up_node = new Anode(x,y+1);
            calculateAdjoinNode(up_node, anode,endNode,openList,closeList, hinderList);
        }
        //按F的从小到大排序
        openList.sort(new Comparator<Anode>() {
            @Override
            public int compare(Anode o1, Anode o2) {
                if (null == o1.getF() || null ==o2.getF()){
                    System.out.println(1);
                    String a = "1";
                }
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
                return returnNode;
            }
        }
        openList.remove(0);
        return setNodeByNode(nextNode,endNode,openList,closeList,hinderList);

    }

    //判断对象的一个属性是否存在该对象的List中
    public static Integer judgeExit(Anode anode, List<Anode> anodeList){

        for (int i = 0; i < anodeList.size(); i++) {
            if (anodeList.get(i).getX()==anode.getX() && anodeList.get(i).getY()==anode.getY()){
                return i;
            }
        }
        return 404;

    }
    //判断对象的一个属性是否存障碍物List中
    public static boolean judgeHinderListExit(Anode anode, List<Anode> HinderList){

        if(HinderList.stream().filter(w->w.getX()==anode.getX() && w.getY()==anode.getY()).findAny().isPresent()){
            return true;
        }else{
            return false;
        }

    }
    //递归根据当前节点获取距离原节点startNode的g值
    public Integer getXByStartNodeToCurrentNode(Anode anode,List<Anode> openList,List<Anode> closeList){

        Integer finalX = 0;
        String parentId = anode.getParentId();

        Anode lastNode = null;
        if (!"0".equals(parentId)){
            for (Anode openNode : openList) {
                if (openNode.getId().equals(parentId)){
                    lastNode = new Anode();
                    BeanUtils.copyProperties(openNode,lastNode);
                   //Double x = Math.sqrt(Math.pow(Math.abs(openNode.getX() - anode.getX()), 2));
                    finalX = 1+ finalX ;//1代表当前节点到上个节点的距离1
                    break;
                }
            }
            for (Anode closeNode : closeList) {
                if (closeNode.getId().equals(parentId)){
                    lastNode = new Anode();
                    finalX = 1+ finalX;//1代表当前节点到上个节点的距离1
                    BeanUtils.copyProperties(closeNode,lastNode);
//                    finalX = finalX + closeNode.getX();
                    break;
                }
            }
            getXByStartNodeToCurrentNode(lastNode,openList,closeList);
        }else{
            finalX = 1+ finalX;
            return finalX;
        }
        //代表到了起始节点startNode了
        return finalX;
    }
    public static void main(String[] args) {
        Anode anode = new Anode(2, 3);
        Anode anode2 = new Anode(12, 3);
        List<Anode> objects = new ArrayList<>();
        objects.add(anode);

    }

    //计算相邻节点的X Y F
    public void calculateAdjoinNode(Anode calculatedAnode,Anode lastNode,Anode endNode,List<Anode> openList,List<Anode> closeList,
                                    List<Anode> hinderList){
        //1.判断邻居节点是否在closeList中，是的话忽略
        if (calculatedAnode.getX() == 6 && calculatedAnode.getY()==5){
            System.out.println(1);
        }
        //判断是否存在于障碍物中
       if(judgeHinderListExit(calculatedAnode,hinderList)){
           return;
       }

        if (closeList.size() != 0){
            for (int i = 0; i < closeList.size(); i++) {
                if (!(closeList.get(i).getX()==calculatedAnode.getX() && closeList.get(i).getY()==calculatedAnode.getY())){
                    if (i==closeList.size()-1){
                        // 2.如果邻居已经在 Open List 中（即该邻居已有父节点），计算从当前节点移动到该邻居是否能使其得到更小的 x 值。
                        // 如果能，则把该邻居的父节点重设为当前节点，并更新其 g 和 f 和 h值。
                        Integer flag = judgeExit(calculatedAnode, openList);
                        if (flag!=404){
                            //递归寻找从当前节点移动到原点的长度和
                            Integer xByStartNodeToCurrentNode = getXByStartNodeToCurrentNode(lastNode, openList, closeList);
                            Integer rightNodeX = openList.get(flag).getG();
                            //比较当前节点到起始的x和右节点的x大小
                            if (rightNodeX > xByStartNodeToCurrentNode+1 ){
                                //f会自动计算
                                calculatedAnode.setG(xByStartNodeToCurrentNode+1);
                                calculatedAnode.setH(Math.abs(endNode.getY()-calculatedAnode.getY() )+ Math.abs(endNode.getX()-calculatedAnode.getX()));
                                calculatedAnode.setParentId(lastNode.getId());
                            }else{
                                return;
                            }

                        }else{//3.如果邻居不在 Open List 中，计算 x、y、F，设置父节点，并将其加入 Open List
//                            for (int j = 0; j < hinderList.size(); j++) {
//                                if (!(calculatedAnode.getX() == hinderList.get(j).getX() &&
//                                        calculatedAnode.getY() == hinderList.get(j).getY())){
//                                    if (j == hinderList.size()-1){
//                                        //递归寻找从当前节点移动到原点的长度和
//                                        Integer xByStartNodeToCurrentNode = getXByStartNodeToCurrentNode(lastNode, openList, closeList);
//                                        //f会自动计算
//                                        calculatedAnode.setG(xByStartNodeToCurrentNode+1);
//                                        calculatedAnode.setH(Math.abs(endNode.getY()-calculatedAnode.getY() )+ Math.abs(endNode.getX()-calculatedAnode.getX()));
//                                        calculatedAnode.setParentId(lastNode.getId());
//
//                                    }
//                                }
//                            }
                                //递归寻找从当前节点移动到原点的长度和
                                Integer xByStartNodeToCurrentNode = getXByStartNodeToCurrentNode(lastNode, openList, closeList);
                                //f会自动计算
                                calculatedAnode.setG(xByStartNodeToCurrentNode+1);
                                calculatedAnode.setH(Math.abs(endNode.getY()-calculatedAnode.getY() )+ Math.abs(endNode.getX()-calculatedAnode.getX()));
                                calculatedAnode.setParentId(lastNode.getId());

                        }

                    }
                }else{//在closeList存在就不计算这个相邻点位了
                    return;
                }
            }
        }else{
            // 如果邻居已经在 Open List 中（即该邻居已有父节点），计算从当前节点移动到该邻居是否能使其得到更小的 x 值。
            // 如果能，则把该邻居的父节点重设为当前节点，并更新其 x 和 F 值。
            Integer flag = judgeExit(calculatedAnode, openList);
            if (flag!=404){//存在
                //递归寻找从当前节点移动到原点的长度和
                Integer xByStartNodeToCurrentNode = getXByStartNodeToCurrentNode(lastNode, openList, closeList);
                Integer rightNodeX = openList.get(flag).getG();
                //比较当前节点到起始的x和右节点的x大小
                if (rightNodeX > xByStartNodeToCurrentNode+1 ){
                    //f会自动计算
                    calculatedAnode.setG(xByStartNodeToCurrentNode+1);
                    calculatedAnode.setH(Math.abs(endNode.getY()-calculatedAnode.getY() )+ Math.abs(endNode.getX()-calculatedAnode.getX()));
                    calculatedAnode.setParentId(lastNode.getId());
                }else{
                    return;
                }

            }else{//3.如果邻居不在 Open List 中，计算 x、y、F，设置父节点，并将其加入 Open List
                //递归寻找从当前节点移动到该邻居的长度和
                Integer xByStartNodeToCurrentNode = getXByStartNodeToCurrentNode(lastNode, openList, closeList);
                calculatedAnode.setG(xByStartNodeToCurrentNode);
                calculatedAnode.setH(Math.abs(endNode.getY()-calculatedAnode.getY() )+ Math.abs(endNode.getX()-calculatedAnode.getX()));
                calculatedAnode.setParentId(lastNode.getId());

//                for (int j = 0; j < hinderList.size(); j++) {
//                    if (!(calculatedAnode.getX() == hinderList.get(j).getX() &&
//                            calculatedAnode.getY() == hinderList.get(j).getY())){
//                        if (j == hinderList.size()-1){
//                            //递归寻找从当前节点移动到该邻居的长度和
//                            Integer xByStartNodeToCurrentNode = getXByStartNodeToCurrentNode(lastNode, openList, closeList);
//                            calculatedAnode.setG(xByStartNodeToCurrentNode);
//                            calculatedAnode.setH(Math.abs(endNode.getY()-calculatedAnode.getY() )+ Math.abs(endNode.getX()-calculatedAnode.getX()));
//                            calculatedAnode.setParentId(lastNode.getId());
//                        }
//                    }
//                }

            }
        }
        openList.add(calculatedAnode);
    }


    public List<Anode>getTesthinderList( List<Anode> hinderList){
        Anode anode1 = new Anode(1, 1);
        Anode anode2 = new Anode(1, 5);
        Anode anode3 = new Anode(1, 6);
        Anode anode4 = new Anode(1, 8);
        Anode anode5 = new Anode(1, 9);
        Anode anode6 = new Anode(1, 16);
        Anode anode21 = new Anode(2, 2);
        Anode anode22 = new Anode(2, 3);
        Anode anode23 = new Anode(2, 12);
        Anode anode24 = new Anode(2, 13);
        Anode anode25 = new Anode(2, 14);
        Anode anode26 = new Anode(2, 17);
        Anode anode27 = new Anode(2, 18);
        Anode anode31 = new Anode(3, 4);
        Anode anode32 = new Anode(3, 10);
        Anode anode33 = new Anode(3, 11);
        Anode anode41 = new Anode(4, 4);
        Anode anode42 = new Anode(4, 10);
        Anode anode43 = new Anode(4, 11);
        Anode anode44 = new Anode(4, 14);
        Anode anode45 = new Anode(4, 15);
        Anode anode46 = new Anode(4, 16);
        Anode anode47 = new Anode(4, 17);
        Anode anode48 = new Anode(4, 18);
        Anode anode49 = new Anode(4, 19);
        Anode anode51 = new Anode(5, 5);
        Anode anode61 = new Anode(5, 6);
        Anode anode62 = new Anode(5, 7);
        Anode anode63 = new Anode(5, 8);
        Anode anode64= new Anode(5, 9);
        Anode anode65 = new Anode(5, 13);
        Anode anode66 = new Anode(5, 14);
        Anode anode67 = new Anode(5, 15);

        hinderList.add(anode1);
        hinderList.add(anode2);
        hinderList.add(anode3);
        hinderList.add(anode4);
        hinderList.add(anode5);
        hinderList.add(anode6);
        hinderList.add(anode21);
        hinderList.add(anode22);
        hinderList.add(anode23);
        hinderList.add(anode24);
        hinderList.add(anode25);
        hinderList.add(anode26);
        hinderList.add(anode27);
        hinderList.add(anode31);
        hinderList.add(anode32);
        hinderList.add(anode33);
        hinderList.add(anode41);
        hinderList.add(anode42);
        hinderList.add(anode43);
        hinderList.add(anode44);
        hinderList.add(anode45);
        hinderList.add(anode46);
        hinderList.add(anode47);
        hinderList.add(anode48);
        hinderList.add(anode49);
        hinderList.add(anode51);
        hinderList.add(anode61);
        hinderList.add(anode62);
        hinderList.add(anode63);
        hinderList.add(anode64);
        hinderList.add(anode65);
        hinderList.add(anode66);
        hinderList.add(anode67);
        return  hinderList;
    }
}
