package com.example.MyBookShopApp.data.services;

import com.example.MyBookShopApp.data.repo.BookFileRepository;
import com.example.MyBookShopApp.struct.book.file.BookFileEntity;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ResourceStorage {

    private BookFileRepository bookFileRepository;

    @Autowired
    public ResourceStorage(BookFileRepository bookFileRepository) {
        this.bookFileRepository = bookFileRepository;
    }

    @Value(value = "${upload.path}")
    private String uploadPath;

    @Value(value = "${download.path}")
    private String downloadPath;
    public String saveNewBookImage(MultipartFile file, String slug) {
        String resourceURI = null;

        if (!file.isEmpty()) {
            if (!new File(uploadPath).exists()) {
                try {
                    Files.createDirectories(Paths.get(uploadPath));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            String fileName = slug + "." + FilenameUtils.getExtension(file.getOriginalFilename());
            Path path = Paths.get(uploadPath, fileName);
            resourceURI = "/book-covers/" + fileName;
            try {
                file.transferTo(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resourceURI;
    }

    public Path getBookPathFile(String hash) {
       return Paths.get(bookFileRepository.findBookFileEntityByHash(hash).getPath());
    }

    public MediaType getBookFileMime(String hash) {
        BookFileEntity bookFile = bookFileRepository.findBookFileEntityByHash(hash);
        String mimeType = URLConnection.guessContentTypeFromName(Paths.get(bookFile.getPath()).getFileName().toString());
        if (mimeType != null){
            return MediaType.parseMediaType(mimeType);
        } else return MediaType.APPLICATION_OCTET_STREAM;
    }

    public byte[] getBookFileByteArray(String hash) {
        BookFileEntity bookFile = bookFileRepository.findBookFileEntityByHash(hash);
        Path path = Paths.get(downloadPath, bookFile.getPath());
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }
}
