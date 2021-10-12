package com.example.CourseworkSpring.Controller;

import com.example.CourseworkSpring.Entity.Goods;
import com.example.CourseworkSpring.Repository.GoodsRepo;
import com.example.CourseworkSpring.Service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@Controller
public class MainController {
    @Value("${upload.path}")
    private String uploadPath;

    @Autowired
    private GoodsService goodsService;

    // GET: Главная страница.
    @GetMapping
    public String mainPage(HttpServletRequest request, Model model) throws IOException {
        List<Goods> goodsList;
        if (goodsService.getGoodsRepo()==null){
            goodsList = goodsService.getList(null);
        }
        else{
            goodsList = goodsService.getGoodsRepo().getGoodsList();
        }
        model.addAttribute("fileName",goodsService.getGoodsRepo().getFile().getAbsolutePath());
        model.addAttribute("Error",false);
        goodsService.getGoodsRepo().setName("Цены товаров в магазинах города");
        model.addAttribute("text",goodsService.getGoodsRepo().getName());
        model.addAttribute("goodsList", goodsList);
        model.addAttribute("fileList",goodsService.getFileNameList());
        return "main";
    }
    // GET: Страница для создания файла.
    @GetMapping("/new_file")
    public String newFile(Model model){
        return "new_file";
    }
    //
    @GetMapping("/back")
    public String back(Model model) throws IOException {
        List<Goods> goodsList = goodsService.getList(goodsService.getGoodsRepo().getFile());
        model.addAttribute("fileName",goodsService.getGoodsRepo().getFile().getAbsolutePath());
        model.addAttribute("Error",false);
        goodsService.getGoodsRepo().setName("Цены товаров в магазинах города");
        model.addAttribute("text",goodsService.getGoodsRepo().getName());
        model.addAttribute("goodsList", goodsList);
        model.addAttribute("fileList",goodsService.getFileNameList());
        return "main";
    }

    // GET: Страница редактирования.
    @GetMapping("/edit")
    public String edit(Model model) throws IOException {
        GoodsRepo goodsRepo = goodsService.getGoodsRepo();
        List<Goods> goodsList = goodsRepo.getGoodsList();
        goodsRepo.setName("Страница редактирования товаров");
        model.addAttribute("Error",false);
        model.addAttribute("goods",new Goods());
        model.addAttribute("goodsList",goodsList);
        model.addAttribute("text",goodsRepo.getName());
        model.addAttribute("fileName",goodsRepo.getFile().getAbsolutePath());
        model.addAttribute("fileList",goodsService.getFileNameList());
        return "edit";
    }
    @GetMapping("/error")
    public String error(Model model){
        return "error";
    }
    @GetMapping("/about_program")
    public String about(Model model){
        return "aboutProgram";
    }
    @GetMapping("/description")
    public String description(Model model){
        return "description";
    }


}
