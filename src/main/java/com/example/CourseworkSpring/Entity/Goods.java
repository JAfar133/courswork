package com.example.CourseworkSpring.Entity;


public class Goods {

    private int id;
    private String storeName;
    private double price;
    //Переменная для присвоения ей максимальной цены(необходима для задания)
    private double price1;

    public Goods() {

    }
    public Goods(int id, String storeName, double price) {
        this.id = id;
        this.storeName = storeName.trim();
        this.price = price;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName.trim();
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Goods goods = (Goods) o;
        return id == goods.id  && storeName.equals(goods.storeName);
    }

    public double getPrice1() {
        return price1;
    }

    public void setPrice1(double price1) {
        this.price1 = price1;
    }

    @Override
    public int hashCode() {
        return 7*(new Integer(id)).hashCode()+
                11 * storeName.hashCode();
    }
}
