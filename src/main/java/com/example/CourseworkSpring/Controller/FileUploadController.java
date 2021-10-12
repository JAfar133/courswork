package com.example.CourseworkSpring.Controller;


import com.example.CourseworkSpring.Repository.MyUploadForm;
import com.example.CourseworkSpring.Service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Controller
public class FileUploadController {

    @Value("${upload.path}")
    private String uploadPath;

    @Autowired
    private FileUploadService fileUploadService;
    // GET: Страница загрузки файла.
    @GetMapping("/uploadOneFile")
    public String uploadOneFileHandler(Model model) {
        MyUploadForm myUploadForm = new MyUploadForm();
        model.addAttribute("myUploadForm", myUploadForm);
        return "uploadOneFile";
    }
    // POST: Загрузка файла..
    @PostMapping("/uploadOneFile")
    public String uploadOneFileHandlerPOST(HttpServletRequest request, //
                                           Model model, //
                                           @ModelAttribute("myUploadForm") MyUploadForm myUploadForm) throws IOException {
        return fileUploadService.doUpload(request, model, myUploadForm);
    }

}
