package com.example.CourseworkSpring.Service;

import com.example.CourseworkSpring.Entity.Goods;
import com.example.CourseworkSpring.Repository.GoodsRepo;
import com.example.CourseworkSpring.Repository.MyUploadForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileUploadService {

    @Autowired
    private GoodsService goodsService;

    @Value("${upload.path}")
    private String uploadPath;

    public String doUpload(HttpServletRequest request, Model model, //
                           MyUploadForm myUploadForm) throws IOException {
        System.out.println("Путь загрузки=" + uploadPath);
        File uploadRootDir = new File(uploadPath);
        // Создание директории, если не существует
        if (!uploadRootDir.exists()) {
            uploadRootDir.mkdirs();
        }
        MultipartFile[] fileDatas = myUploadForm.getFileDatas();
        //Файлов может быть несколько.
        List<File> uploadedFiles = new ArrayList<>();
        List<String> failedFiles = new ArrayList<>();

        for (MultipartFile fileData : fileDatas) {
            // Название клиентского файла
            String name = fileData.getOriginalFilename();
            System.out.println("Название клиентского файла = " + name);
            if (name != null && name.length() > 0) {
                try {
                    // Создание файла на сервере
                    File serverFile = new File(uploadRootDir.getAbsolutePath() + File.separator + name);
                    BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
                    stream.write(fileData.getBytes());
                    stream.close();
                    //
                    uploadedFiles.add(serverFile);
                    System.out.println("Запись файла: " + serverFile);
                } catch (Exception e) {
                    System.err.println("Ошибка записи файла: " + name);
                    failedFiles.add(name);

                }
            }
        }
        System.out.println("Успешно загружены:"+uploadedFiles+"\nОшибка при загрузке: "+failedFiles);
        List<Goods> goodsList = goodsService.getList(uploadedFiles.get(0));
        model.addAttribute("fileName", goodsService.getGoodsRepo().getFile().getAbsolutePath());
        model.addAttribute("goodsList", goodsList);
        model.addAttribute("uploadedFiles", uploadedFiles);
        model.addAttribute("failedFiles", failedFiles);
        return "redirect:/";
    }


}
