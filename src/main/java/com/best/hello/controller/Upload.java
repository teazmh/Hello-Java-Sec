package com.best.hello.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/UPLOAD")
public class Upload {

    // 设置保存文件的路径，不安全的web路径下
    private static final String UPLOADED_FOLDER = System.getProperty("user.dir") + "/src/main/resources/static/file/";


    @GetMapping("/upload")
    public String uploadStatus() {
        return "upload";
    }

    /**
     * @vul 上传文件，未做任何处理
     * @poc http://127.0.0.1:8888/UPLOAD/vul
     */
    @PostMapping("/uploadVul")
    public String singleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "请选择要上传的文件");
            return "redirect:upload";
        }

        try {
            byte[] bytes = file.getBytes();
            Path dir = Paths.get(UPLOADED_FOLDER);
            Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());

            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }
            Files.write(path, bytes);
            System.out.println("[+] 上传文件成功：" + path);
            redirectAttributes.addFlashAttribute("message",
                    "上传文件成功：" + path + "");

        } catch (Exception e) {
            return e.toString();
        }
        return "redirect:upload";
    }


    /**
     * @safe 白名单后缀名处理
     * @poc http://127.0.0.1:8888/UPLOAD/safe
     */
    @PostMapping("/uploadSafe")
    public String singleFileUploadSafe(@RequestParam("file") MultipartFile file,
                                       RedirectAttributes redirectAttributes) {

        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "请选择要上传的文件");
            return "redirect:upload";
        }

        try {
            byte[] bytes = file.getBytes();
            String fileName = file.getOriginalFilename();
            Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());

            // 获取文件后缀名，并且索引到最后一个，避免使用.jpg.jsp来绕过
            String Suffix = fileName.substring(fileName.lastIndexOf("."));

            String[] SuffixSafe = {".jpg", ".png", ".jpeg", ".gif", ".bmp", ".ico"};
            boolean flag = false;

            // 如果满足后缀名单，返回true
            for (String s : SuffixSafe) {
                if (Suffix.toLowerCase().equals(s)) {
                    flag = true;
                    break;
                }
            }

            System.out.println("[*] 尝试上传文件：" + fileName);

            if (!flag) {
                redirectAttributes.addFlashAttribute("message",
                        "只允许上传图片，[.jpg, .png, .jpeg, .gif, .bmp, .ico]");
            } else {
                Files.write(path, bytes);
                redirectAttributes.addFlashAttribute("message",
                        "上传文件成功：" + path + "");
            }

        } catch (Exception e) {
            return e.toString();
        }
        return "redirect:upload";
    }


}
