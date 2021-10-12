package com.example.CourseworkSpring.Global;

import com.example.CourseworkSpring.Entity.Goods;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Transfer {
    //Из списка строк в объект
    public static List<Goods> StringToGoods(List <String> lines){
        if(lines==null||lines.isEmpty()) return null;
        List<Goods> goodsList = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            String[] words = lines.get(i).split(",");
            if (words.length!=3)continue;
            Goods goods = new Goods();
            goods.setStoreName(words[1].trim());
            try {
                goods.setId(Integer.parseInt(words[0].trim()));
                goods.setPrice(Double.parseDouble(words[2].trim()));
            }catch (NumberFormatException e){
                return null;
            }
            goodsList.add(goods);
        }
        return goodsList;
    }
    //Из объекта в список строк
    public static List<String> GoodsToString(List <Goods> goods){
        if(goods==null||goods.isEmpty())return null;
        List<String>lines = new ArrayList<>();
        for (Goods g:goods){
            lines.add(String.format(Locale.US,"%d, %s, %.2f",g.getId(),g.getStoreName(),g.getPrice()));
        }
        return lines;
    }

}
