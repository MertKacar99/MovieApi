package com.kirsehirfilix.movieApi.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

@Service
public interface FileService {

    String uploadFile(String path, MultipartFile file) throws IOException;

    InputStream getResourceFile(String path, String filename) throws FileNotFoundException;
}
