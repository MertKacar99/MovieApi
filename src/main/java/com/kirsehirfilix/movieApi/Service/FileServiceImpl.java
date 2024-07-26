package com.kirsehirfilix.movieApi.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class FileServiceImpl implements FileService {
    @Override
    public String uploadFile(String path, MultipartFile file) throws IOException {
        // Dosyanın adını al
        String fileName = file.getOriginalFilename();

        // Dosya yolunu oluştur
        String filePath = path + File.separator + fileName;

        // Dosya nesnesi oluştur
        File f = new File(path);
        if(!f.exists()) {
            f.mkdir();  // Dizin yoksa oluşturulur
        }

        // Dosyayı belirtilen yere kopyala
        Files.copy(file.getInputStream(), Paths.get(filePath));

        return fileName;  // Yüklenen dosyanın adını döndür
    }

    @Override
    public InputStream getResourceFile(String path, String fileName) throws FileNotFoundException {
        String filePath = path + File.separator + fileName;  // Dosya yolunu oluştur
        return new FileInputStream(filePath);  // Dosyayı okumak için InputStream döndür
    }
}
