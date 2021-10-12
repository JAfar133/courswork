package com.example.CourseworkSpring.Comporators;

import com.example.CourseworkSpring.Entity.Goods;

import java.util.Comparator;

public class CompKeyAscStoreNameAsc implements Comparator {

    @Override
    public int compare(Object o1, Object o2) {
        Goods g1 = (Goods) o1;
        Goods g2 = (Goods) o2;
        if(g1.getId()<g2.getId()) return -1;
        else if(g1.getId()>g2.getId()) return 1;
        else if(g1.getStoreName().compareTo(g2.getStoreName())<0) return -1;
        else if(g1.getStoreName().equals(g2.getStoreName())) return 0;
        else return -1;
    }
}
