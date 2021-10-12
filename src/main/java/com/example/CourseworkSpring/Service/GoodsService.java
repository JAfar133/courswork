package com.example.CourseworkSpring.Service;

import com.example.CourseworkSpring.Comporators.CompKeyAscPriceAsc;
import com.example.CourseworkSpring.Entity.Goods;
import com.example.CourseworkSpring.Global.IO;
import com.example.CourseworkSpring.Global.Transfer;
import com.example.CourseworkSpring.Repository.GoodsRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
public class GoodsService {

    @Value("${upload.path}")
    private String uploadPath;

    private GoodsRepo goodsRepo;

    //Файл, который будет отображаться по умолчанию
    private String defaultFilePath="txt_files/sevastopol.txt";
    private File defaultFile = new File(defaultFilePath);
    private File currentFile;

    //Получить список объектов из файла
    public List<Goods> getList(File file) throws IOException {
        //Если пользователь передает файл
        if(file!=null&&file.exists()){
            currentFile = file;
            List<String> strLines = IO.inpLines(file);
            List<Goods> goodsList = Transfer.StringToGoods(strLines);
            goodsRepo = new GoodsRepo(file,goodsList);
        }
        //Если файл не передан, работаем с файлом по умолчанию
        else{
            currentFile = defaultFile;
            List<String> strLines = IO.inpLines(currentFile);
            List<Goods> goodsList = Transfer.StringToGoods(strLines);
            goodsRepo = new GoodsRepo(currentFile,goodsList);
        }
        return goodsRepo.getGoodsList();
    }

    //Создать файл на сервере и записать в него текст
    public File addFile(String filename,String text) throws IOException {
        StringBuilder sb = new StringBuilder(uploadPath);
        sb.append("/").append(filename);
        File file = new File(sb.toString());
        System.out.println("Путь сохранения файла = "+sb);
        //Создаем файл
        if (file.createNewFile()){
            System.out.println("Файл = "+file.getName()+ " Создан!");
        }
        //Если файл существует информация перезапишется
        else{
            System.out.println("Файл уже существует");
        }
        //Записываем текст
        if(IO.textToFile(file,text)) {
            return file;
        }
        else return null;
    }

    //Фильтр поиска
    public List<Goods> getFilter(String subStr) throws IOException {
        if(goodsRepo.getGoodsList()==null) return null;
        List<Goods> filterGoodsList = new ArrayList<>();
            for (Goods goods : goodsRepo.getGoodsList()) {
                if (goods.getStoreName().trim().toUpperCase().startsWith(subStr.toUpperCase()))
                    filterGoodsList.add(goods);
            }
        if (subStr.equals("none")) return goodsRepo.getGoodsList();
        else return filterGoodsList;
    }
    //Список товаров, цена, которых находится в заданном диапазоне
    public List<Goods> betweenPrice(double d1,double d2) throws IOException {
        if(goodsRepo.getGoodsList()==null) return null;
        List<Goods> betweenGoods = new ArrayList<>();
            for (Goods goods : goodsRepo.getGoodsList()) {
                if (goods.getPrice()>=d1 && goods.getPrice()<=d2)
                    betweenGoods.add(goods);
            }
        return betweenGoods;
    }
    //Удаление товара
    public boolean delete(Goods goods) throws IOException {
        if(goodsRepo.getGoodsList()==null) return false;
            return goodsRepo.delGoods(goods);
    }
    //Добавление товара
    public boolean add(Goods goods) throws IOException {
        if(goodsRepo.getGoodsList()==null) return false;
            return goodsRepo.addGoods(goods);
    }
    //Обновление (неключевого поля) по ключу
    public boolean updateGoodsPrice(Goods goods){
        if(goodsRepo.getGoodsList()==null) return false;
        return goodsRepo.updatePrice(goods);
    }
    //Удаление группы товаров с заданным именем магазина
    public boolean deleteGroup(String storeName) throws IOException {
        if(goodsRepo.getGoodsList()==null) return false;
        return goodsRepo.delGroupStoreName(storeName);
    }
    //Получить отсортированный список
    public List<Goods> getSort(Comparator comp) throws IOException {
        if (goodsRepo.getGoodsList()==null) return null;
        return goodsRepo.getSort(comp).getGoodsList();
    }
    public List<Goods> getShuffle(){
        if (goodsRepo.getGoodsList()==null) return null;
        Collections.shuffle(goodsRepo.getGoodsList());
        return goodsRepo.getGoodsList();
    }
    //Метод для скачивания файла.
    public ResponseEntity<FileSystemResource> downloadFile() throws IOException {
        File file = new File(goodsRepo.getFile().getAbsolutePath());
        HttpHeaders respHeaders = new HttpHeaders();
        respHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        respHeaders.setContentLength(file.length());
        respHeaders.setContentDispositionFormData("attachment", goodsRepo.getFile().getName());
        return new ResponseEntity<FileSystemResource>(new FileSystemResource(file), respHeaders, HttpStatus.OK);
    }
    //Получить список товаров, цена которых выше среднего
    public List<Goods> getAboveAvg(){
        if (goodsRepo.getGoodsList()==null) return null;
        List<Goods> aboveAvgGoodsList = new ArrayList<>();
        for (Goods g:goodsRepo.getGoodsList()) {
            if(g.getPrice()>getAvgPrice()) aboveAvgGoodsList.add(g);
        }
        return aboveAvgGoodsList;
    }
    //Средняя цена
    public double getAvgPrice(){
        double sum=0;
        if (goodsRepo.getGoodsList()==null) return 0;
        for (Goods g: goodsRepo.getGoodsList()) {
            sum+=g.getPrice();
        }
        try{
            return sum/goodsRepo.getGoodsList().size();}
        catch (ArithmeticException e){
            return sum;
        }
    }
    public List<Goods> getMinMaxCostsOfGoods() throws IOException {
        if(goodsRepo.getGoodsList()==null||goodsRepo.getGoodsList().size()==0) return null;
        //Сортируем по возрастанию цены
        List<Goods> sortGoodsList = getSort(new CompKeyAscPriceAsc());
        List<Goods> maxMaxCostGoodsList = new ArrayList<>();
        //Переменная сравнения равна первому элементу отсортированного списка
        Goods goodsOne = sortGoodsList.get(0);
        int count = 0;
        for (Goods g:sortGoodsList) {
            //Если товар одинаковый, считаем его количество и устанавливаем цену для максимальных значений
            //Последний встреченный товар, среди группы одинаковых товаров, будет с самой большой ценой
            if(g.getId()==goodsOne.getId())
            {
                goodsOne.setPrice1(g.getPrice());
                count++;
                continue;
            }
            else{
                /*Если в списке только один товар с данным ключом, то его минимальная
                и максимальная стоимость будет одинаковая*/
                if (count==1){goodsOne.setPrice1(goodsOne.getPrice());}
                //Первый новый встреченный товар будет с минимальной ценой
                maxMaxCostGoodsList.add(goodsOne);
                //Присваиваем значение нового товара в переменную сравнения
                goodsOne=g;
                count=1;
            }
        }
        //Так как условие, когда товары разные не выполнится для последнего элемента мы
        // добавляем первый товар, среди последней группы одинаковых товаров
        if (count==1){goodsOne.setPrice1(goodsOne.getPrice());}
        maxMaxCostGoodsList.add(sortGoodsList.get(sortGoodsList.size()-count));

        return maxMaxCostGoodsList;
    }

    public int getNumberStore(){
        Set<String> storeNameSet = new TreeSet<>();
        if(goodsRepo.getGoodsList()==null) return 0;
        for (Goods g: goodsRepo.getGoodsList()) {
            storeNameSet.add(g.getStoreName());
        }
        return storeNameSet.size();
    }
    public List<String> getFileNameList(){
        File f = new File(uploadPath);
        String[] fileNameArray = f.list();
        List<String>fileNameList = Arrays.asList(fileNameArray);
        return fileNameList;
    }
    public boolean delFileFromServer(String filename){
        File f = new File(uploadPath+"/"+filename);
        if(f.delete()){
            System.out.println("файл " + f.getName()+" удален");
            return true;}
        else {
            System.out.println("файл " + f.getName()+" не удален");
            return false;
        }
    }

    //Геттеры
    public String getDefaultFilePath() {
        return defaultFilePath;
    }
    public File getCurrentFile() {
        return currentFile;
    }

    public GoodsRepo getGoodsRepo() {
        return goodsRepo;
    }
}
