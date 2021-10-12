package com.example.CourseworkSpring.Controller;

import com.example.CourseworkSpring.Comporators.CompKeyAscPriceDesc;
import com.example.CourseworkSpring.Comporators.CompKeyAscStoreNameAsc;
import com.example.CourseworkSpring.Comporators.CompStoreNameAscPriceDesc;
import com.example.CourseworkSpring.Entity.Goods;
import com.example.CourseworkSpring.Global.IO;
import com.example.CourseworkSpring.Global.Transfer;
import com.example.CourseworkSpring.Repository.GoodsRepo;
import com.example.CourseworkSpring.Service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


import javax.servlet.http.HttpServletRequest;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

@Controller
public class UserController {
    @Value("${upload.path}")
    private String uploadPath;

    @Autowired
    private GoodsService goodsService;

    // POST: Создание файла.
    @PostMapping("/new_file")
    public String newFile(@RequestParam String text_file,
                          @RequestParam String filename, Model model) throws IOException {
        File file = goodsService.addFile(filename,text_file);
        List<Goods> goodsList = goodsService.getList(file);
        model.addAttribute("goodsList",goodsList);
        return "redirect:/";
    }
    // GET: скачивание файла.
    @GetMapping("/download")
    public ResponseEntity<FileSystemResource> download() throws IOException {
        return goodsService.downloadFile();
    }
    @GetMapping("/select")
    public String selectFile(HttpServletRequest request,Model model) throws IOException {
        String file = request.getParameter("file");
        String del = request.getParameter("del");
        if(file!=null){
            File f = new File(uploadPath+"/"+file);
            System.out.println("Был выбран файл = "+f.getName());
            goodsService.getList(f);
        }
        if(del!=null){
            String fileName = del.trim();
            System.out.println(fileName);
            goodsService.delFileFromServer(fileName);
        }
        model.addAttribute("fileList",goodsService.getFileNameList());
        return "redirect:"+request.getHeader("referer");
    }
    @GetMapping("/filter")
    public String filterStoreName(HttpServletRequest request,
                                  @RequestParam String filter, Model model) throws IOException {
        List<Goods> filterGoodsList;
        GoodsRepo goodsRepo = goodsService.getGoodsRepo();;
        if (!filter.isEmpty()) {
            filterGoodsList = goodsService.getFilter(filter);
            goodsRepo.setName(filter.equals("none") ? "Цены товаров в магазинах города" : "Магазины начинающиеся на " + filter);
            model.addAttribute("goodsList", filterGoodsList);
        }
        else{
            goodsRepo.setName("Цены товаров в магазинах города");
            model.addAttribute("goodsList", goodsRepo.getGoodsList());
        }
            model.addAttribute("fileList",goodsService.getFileNameList());
            model.addAttribute("fileName", goodsRepo.getFile().getAbsolutePath());
            model.addAttribute("text", goodsRepo.getName());
            return "main";
        }

    @GetMapping("/between")
    public String betweenCost(@RequestParam String range1,
                              @RequestParam String range2, Model model){
        GoodsRepo goodsRepo = goodsService.getGoodsRepo();
        try {
            List <Goods> betweenGoodsList = goodsService.betweenPrice(Double.valueOf(range1),Double.valueOf(range2));
            model.addAttribute("goodsList",betweenGoodsList);
            goodsRepo.setName("Цены в диапазоне = ["+range1+";"+range2+"]");
            model.addAttribute("Error",false);

        }catch (Exception e){
            model.addAttribute("Error",true);
            model.addAttribute("goodsList",goodsRepo.getGoodsList());
            goodsRepo.setName("Цены в диапазоне = [неверный ввод]");
            System.err.println(new NumberFormatException());
        }
        model.addAttribute("fileList",goodsService.getFileNameList());
        model.addAttribute("fileName",goodsRepo.getFile().getAbsolutePath());
        model.addAttribute("text",goodsRepo.getName());

        return "main";
    }
    //GET: открывает файл по умолчанию и перенаправляет на главную страницу,
    // если это не страница редактирования
    @GetMapping("/open_default")
    public String _default(HttpServletRequest request,Model model) throws IOException {
        List<Goods> goodsList = goodsService.getList(null);
        GoodsRepo goodsRepo = goodsService.getGoodsRepo();
        goodsRepo.setName("Цены товаров в магазинах города");
        model.addAttribute("goodsList",goodsList);
        model.addAttribute("text",goodsRepo.getName());
        model.addAttribute("fileList",goodsService.getFileNameList());
        String referer = request.getHeader("referer");
        System.out.println("предыдущая страница = "+referer);
        return referer.equals("http://localhost:8082/edit") ? "redirect:"+referer : "redirect:/";
    }
    //GET: Сортировки
    //Ключ(возрастание) цена(убывание)
    @GetMapping("/sort_key_costs")
    public String sortKeyCosts(Model model) throws IOException {
        List<Goods> goodsList = goodsService.getSort(new CompKeyAscPriceDesc());
        GoodsRepo goodsRepo = goodsService.getGoodsRepo();
        goodsRepo.setName("ключ(возрастание) цена(убывание)");
        model.addAttribute("text",goodsRepo.getName());
        model.addAttribute("fileName",goodsRepo.getFile().getAbsolutePath());
        model.addAttribute("goodsList",goodsList);
        model.addAttribute("fileList",goodsService.getFileNameList());
        return "main";
    }
    //Ключ(возрастание) название магазина(возрастание)
    @GetMapping("/sort_key_storename")
    public String sortKeyStoreName(Model model) throws IOException {
        List<Goods> goodsList = goodsService.getSort(new CompKeyAscStoreNameAsc());
        GoodsRepo goodsRepo = goodsService.getGoodsRepo();
        goodsRepo.setName("ключ(возрастание) название магазина(возрастание)");
        model.addAttribute("text",goodsRepo.getName());
        model.addAttribute("fileName",goodsRepo.getFile().getAbsolutePath());
        model.addAttribute("goodsList",goodsList);
        model.addAttribute("fileList",goodsService.getFileNameList());
        return "main";
    }
    //Название магазина(возрастание) цена(убывание)
    @GetMapping("/sort_storename_costs")
    public String sortStoreNameCost(Model model) throws IOException {
        List<Goods> goodsList = goodsService.getSort(new CompStoreNameAscPriceDesc());
        GoodsRepo goodsRepo = goodsService.getGoodsRepo();
        goodsRepo.setName("название магазина(возрастание) цена(убывание)");
        model.addAttribute("text",goodsRepo.getName());
        model.addAttribute("fileName",goodsRepo.getFile().getAbsolutePath());
        model.addAttribute("goodsList",goodsList);
        model.addAttribute("fileList",goodsService.getFileNameList());
        return "main";
    }
    //в случайном порядке
    @GetMapping("/shuffle")
    public String sortShuffle(Model model) throws IOException {
        List<Goods> goodsList = goodsService.getShuffle();
        GoodsRepo goodsRepo = goodsService.getGoodsRepo();
        goodsRepo.setName("В случайном порядке");
        model.addAttribute("text",goodsRepo.getName());
        model.addAttribute("fileName",goodsRepo.getFile().getAbsolutePath());
        model.addAttribute("goodsList",goodsList);
        model.addAttribute("fileList",goodsService.getFileNameList());
        return "main";
    }

    @GetMapping("/above_avg")
    public String aboveAvg(Model model){
        List<Goods> goodsList = goodsService.getAboveAvg();
        GoodsRepo goodsRepo = goodsService.getGoodsRepo();
        goodsRepo.setName("Цены товаров в магазинах города, выше средней цены = "+String.format(Locale.US,"%.2f",goodsService.getAvgPrice()));
        model.addAttribute("text",goodsRepo.getName());
        model.addAttribute("fileName",goodsRepo.getFile().getAbsolutePath());
        model.addAttribute("goodsList",goodsList);
        model.addAttribute("fileList",goodsService.getFileNameList());
        return "main";
    }
    @GetMapping("/avg_price")
    public String avgPrice(Model model){
        GoodsRepo goodsRepo = goodsService.getGoodsRepo();

        List<Goods> goodsList = goodsRepo.getGoodsList();
        model.addAttribute("text",goodsRepo.getName());
        model.addAttribute("fileName",goodsRepo.getFile().getAbsolutePath());
        model.addAttribute("goodsList",goodsList);
        model.addAttribute("avgPrice",String.format(Locale.US,"%.2f",goodsService.getAvgPrice()));
        model.addAttribute("fileList",goodsService.getFileNameList());
        return "main";
    }
    // POST: Страница редактирования.
    @PostMapping("/edit")
    public String edit(HttpServletRequest request, //
                       @ModelAttribute("goods") Goods goods,
                       BindingResult errors,
                       Model model) throws IOException {
        GoodsRepo goodsRepo = goodsService.getGoodsRepo();
        goodsRepo.setName("Цены товаров в магазинах города");
        String add = request.getParameter("add");
        String delete = request.getParameter("delete");
        String update = request.getParameter("update");
        String deleteGroup = request.getParameter("delete_group");
        System.out.println(add);
        if(!errors.hasErrors()) {
            model.addAttribute("Error",false);
            if (goods != null && add != null && goods.getId() != 0 && goods.getStoreName() != null) {
                goodsService.add(goods);
            }
            if (delete != null) {
                goodsService.delete(goods);
            }
            if (update != null) {
                goodsService.updateGoodsPrice(goods);
            }
            if(goods.getStoreName().isEmpty()) model.addAttribute("storeNameNull",true);
            else model.addAttribute("storeNameNull",false);
        }
        else {
            if(goods.getStoreName().isEmpty()) model.addAttribute("storeNameNull",true);
            else model.addAttribute("storeNameNull",false);
            model.addAttribute("Error",true);
            System.err.println(new NumberFormatException());
        }
        if (deleteGroup != null) {
            if(goods.getStoreName().isEmpty()) model.addAttribute("storeNameNull",true);
            else model.addAttribute("storeNameNull",false);
            model.addAttribute("Error",false);
            goodsService.deleteGroup(goods.getStoreName());
        }
        model.addAttribute("fileList",goodsService.getFileNameList());
        model.addAttribute("text",goodsRepo.getName());
        model.addAttribute("fileName",goodsRepo.getFile().getAbsolutePath());
        model.addAttribute("goodsList",goodsRepo.getGoodsList());
        return "edit";
    }

    @GetMapping("/min_max_cost")
    public String minCost(Model model) throws IOException {
        GoodsRepo goodsRepo = goodsService.getGoodsRepo();
        goodsRepo.setName("Минимальная и максимальная стоимость каждого товара");
        List<Goods> goodsList = goodsService.getMinMaxCostsOfGoods();
        model.addAttribute("text",goodsRepo.getName());
        model.addAttribute("fileName",goodsRepo.getFile().getAbsolutePath());
        model.addAttribute("goodsList",goodsList);
        model.addAttribute("fileList",goodsService.getFileNameList());
        return "itog_1";
    }

    @GetMapping("/endEdit")
    public String endEdit(Model model) throws IOException {
        IO.outpLines(goodsService.getGoodsRepo().getFile(), Transfer.GoodsToString(goodsService.getGoodsRepo().getGoodsList()));
        return "redirect:/";
    }
    @GetMapping("/number_store")
    public String numberStore(Model model){
        GoodsRepo goodsRepo = goodsService.getGoodsRepo();
        model.addAttribute("text",goodsRepo.getName());
        model.addAttribute("fileName",goodsRepo.getFile().getAbsolutePath());
        model.addAttribute("goodsList",goodsRepo.getGoodsList());
        model.addAttribute("number_store","Количество магазинов: "+ goodsService.getNumberStore());
        model.addAttribute("fileList",goodsService.getFileNameList());
        return "main";
    }
}
