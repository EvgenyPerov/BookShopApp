package com.example.MyBookShopApp.data.services;

import com.example.MyBookShopApp.data.repo.BookFileRepository;
import com.example.MyBookShopApp.data.repo.FileDownloadRepository;
import com.example.MyBookShopApp.struct.book.book.Book;
import com.example.MyBookShopApp.struct.book.file.FileDownloadEntity;
import com.example.MyBookShopApp.struct.user.UserEntity;
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

    private final FileDownloadRepository fileDownloadRepository;

    @Autowired
    public ResourceStorage(BookFileRepository bookFileRepository, FileDownloadRepository fileDownloadRepository) {
        this.bookFileRepository = bookFileRepository;
        this.fileDownloadRepository = fileDownloadRepository;
    }

    @Value(value = "${download.path}")
    private String downloadPath;

    public String saveNewBookImage(MultipartFile file, String slug, String uploadPath) {
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
            var path = Paths.get(uploadPath, fileName);
            resourceURI = uploadPath.substring(uploadPath.lastIndexOf('/')) + "/" + fileName;
            try {
                file.transferTo(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resourceURI;
    }

    public Path getBookPathFile(String hash) {
        var bookFile = bookFileRepository.findBookFileEntityByHash(hash);
        String fileName = bookFile.getPath();
        String fileType = bookFile.getBookFileTypeEntity().getName();
        return Paths.get(fileName + "." + fileType);
    }

    public MediaType getBookFileMime(String hash) {
        var bookFile = bookFileRepository.findBookFileEntityByHash(hash);
        String mimeType = URLConnection.guessContentTypeFromName(Paths.get(bookFile.getPath()).getFileName().toString());
        if (mimeType != null){
            return MediaType.parseMediaType(mimeType);
        } else return MediaType.APPLICATION_OCTET_STREAM;
    }

    public byte[] getBookFileByteArray(String hash) {
        var bookFile = bookFileRepository.findBookFileEntityByHash(hash);
        String fileName = bookFile.getPath();
        String fileType = bookFile.getBookFileTypeEntity().getName();

        var path = Paths.get(downloadPath, fileName + "." + fileType);
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    public void addFileDownload(String hash, UserEntity user){
        var book = bookFileRepository.findBookFileEntityByHash(hash).getBook();

        FileDownloadEntity fileDownload = fileDownloadRepository.findByBookIdAndUserId(book.getId() ,user.getId());
        if (fileDownload != null){
            fileDownload.increaseCount();
        } else {
            fileDownload = new FileDownloadEntity();
            fileDownload.setBookId(book.getId());
            fileDownload.setUserId(user.getId());
        }

        fileDownloadRepository.save(fileDownload);
    }

    public int getCountFileDownloadByUser(Book book, UserEntity user){
        FileDownloadEntity fileDownload = fileDownloadRepository.findByBookIdAndUserId(book.getId(), user.getId());
        return (fileDownload != null)? fileDownload.getCount() : 0;
    }

}
