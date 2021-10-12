package com.example.CourseworkSpring.Repository;

import com.example.CourseworkSpring.Entity.Goods;
import com.example.CourseworkSpring.Global.IO;
import com.example.CourseworkSpring.Global.Transfer;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class GoodsRepo {
    @Value("${upload.path}")
    private String uploadPath;

    private String name;
    private File file;
    private List<Goods> goodsList;
    public GoodsRepo(String name, List goodsList) {
        this.goodsList = goodsList;
        this.name = name;
    }

    public GoodsRepo() {
        file=new File(uploadPath+"/file.txt");
        name="";
        goodsList=new ArrayList<>();
    }
    public GoodsRepo(String name) {
        file=new File(uploadPath+"/file.txt");
        this.name = name;
        goodsList=new ArrayList<>();
    }
    public GoodsRepo(String name, File file, List<Goods> goodsList) {
        this.name = name;
        this.file = file;
        this.goodsList = goodsList;
    }
    public GoodsRepo(File file, List<Goods> goodsList) {
        this.file = file;
        this.goodsList = goodsList;
    }
    public boolean addGoods(Goods g) throws IOException {
        if(goodsList.contains(g))return false;
        if(g.getStoreName()!=null&&!g.getStoreName().isEmpty()&&g.getId()!=0&&goodsList.add(g)) {
//            IO.outpLines(file, Transfer.GoodsToString(goodsList));
            return true;
        }
        else return false;
    }
    public boolean delGoods(Goods goods) throws IOException {
        if(goodsList.remove(goods)){
        return true;
    }
    return false;
    }

    public GoodsRepo getSort(Comparator comp){
        GoodsRepo gp = new GoodsRepo(name,file,goodsList);
        Collections.sort(gp.getGoodsList(),comp);
        return gp;
    }
    public Goods getGoods(Goods goods){
        return goodsList.stream().filter(goods1 ->
                goods1.equals(goods)).findAny().orElse(null);
    }
    public boolean updatePrice(Goods goods){
        Goods g = getGoods(goods);
        if(g!=null){
            g.setPrice(goods.getPrice());
            return true;
        }
        return false;
    }
    public boolean delGroupStoreName(String storeName) throws IOException {
        List<Goods> delList = new ArrayList<>();
        for (Goods g:goodsList) {
            if(g.getStoreName().equals(storeName))
                delList.add(g);
        }
        return goodsList.removeAll(delList);
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public List<Goods> getGoodsList() {
        return goodsList;
    }

    public void setGoodsList(List<Goods> goodsList) {
        this.goodsList = goodsList;
    }

}
