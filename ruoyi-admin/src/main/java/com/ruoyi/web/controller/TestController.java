package com.ruoyi.web.controller;

import com.ruoyi.test.TestService;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class TestController {
    private static  ThreadLocal<Integer> local ;
    private static int a =10;
    public static void main(String[] args) throws ParseException {
        TestService testService = new TestService();
        String s = testService.helloTest();
        System.out.println(s);
//        new Thread(()->{
//            local = new ThreadLocal<>();
//            local.set(a+10);
//            System.out.println(local.get());
//        },"A").start();
        new Thread(()->{
            local = new ThreadLocal<>();
//            local.set(a+10);
            System.out.println(local.get());
        },"B").start();

    }
}
