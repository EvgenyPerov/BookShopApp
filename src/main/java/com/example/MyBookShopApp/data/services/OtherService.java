package com.example.MyBookShopApp.data.services;

import com.example.MyBookShopApp.data.dto.ContactMessageDto;
import com.example.MyBookShopApp.data.repo.*;
import com.example.MyBookShopApp.struct.book.book.Book;
import com.example.MyBookShopApp.struct.book.review.MessageEntity;
import com.example.MyBookShopApp.struct.other.DocumentEntity;
import com.example.MyBookShopApp.struct.other.FaqEntity;
import com.example.MyBookShopApp.struct.other.Tag;
import com.example.MyBookShopApp.struct.user.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OtherService {

    private TagsRepository tagsRepository;
    private BookRepository bookRepository;

    private final UserService userService;

    private final BookService bookService;

    private final DocumentRepository documentRepository;

    private final FaqRepository faqRepository;

    private final MessageRepository messageRepository;

    @Autowired
    public OtherService(TagsRepository tagsRepository, BookRepository bookRepository, UserService userService, BookService bookService, DocumentRepository documentRepository, FaqRepository faqRepository, MessageRepository messageRepository) {
        this.tagsRepository = tagsRepository;
        this.bookRepository = bookRepository;
        this.userService = userService;
        this.bookService = bookService;
        this.documentRepository = documentRepository;
        this.faqRepository = faqRepository;
        this.messageRepository = messageRepository;
    }

    public List<Tag> getAllTags() {
        return tagsRepository.findAll();
    }

    public List<String> getAllTagsName(){
        List<String> list =  getAllTags().stream()
                .map(Tag::getName)
                .sorted()
                .collect(Collectors.toList());
        list.add(0,"");
        return list;
    }

    public Map<Tag, Integer> getTagsAndSizesMap(){
        List<Tag> tagList = getAllTags();
        Map<Tag, Integer> tagMap = new HashMap<>();
        int min, max;

        if (tagList != null && !tagList.isEmpty()){
                min = tagList.get(0).getBookList().size();
                max = min;
                for (Tag tag : tagList) {
                    min = tag.getBookList().size() < min ? tag.getBookList().size() : min;
                    max = tag.getBookList().size() > max ? tag.getBookList().size() : max;
                }
                float variance = (min == 0) ? max : (max - min + 1);
                for (Tag tag : tagList) {
                    float sizePercent = variance == 0? 0 : Float.valueOf(tag.getBookList().size()) / variance * 10;
                    tagMap.putIfAbsent(tag, (int) sizePercent);
                }
            }
        return tagMap;
    }

    public List<Book> getPageOfTagBooks(Integer tagId, Integer offset, Integer limit){
        Pageable nextPage = PageRequest.of(offset, limit);
        var tag = tagsRepository.findTagByIdIs(tagId);
        Page<Book> page = bookRepository.findBooksByTagListContains(tag, nextPage);

        UserEntity user = userService.getCurrentUser();
        if (user != null) {bookService.updateStatusOfBook(page.getContent(), user);}

        return page.getContent();
    }

    public String getTagNameById(Integer tagId){
        return tagsRepository.findTagByIdIs(tagId).getName();
    }

    public List<DocumentEntity> getAllDocuments(){
        return documentRepository.findAll();
    }

    public DocumentEntity getDocumentBySlug(String slug){
        return documentRepository.findBySlug(slug);
    }

    public Map<String, List<String>> getMapAllFaq(){
        List<FaqEntity> faqEntityList = faqRepository.findAll();
        Map<String, List<String>> map = new HashMap<>();

        for (FaqEntity faq : faqEntityList) {
            map.put(faq.getQuestion(), new ArrayList<>());
        }

        for (FaqEntity faq : faqEntityList) {
            map.get(faq.getQuestion()).add(faq.getAnswer());
        }

        return map;
    }

    public void addMessageToSupport(ContactMessageDto form){
        UserEntity user = userService.getCurrentUser();
        String name;
        String email;
        var userId = 0;

        if (user != null) {
            name = user.getName();
            email = user.getEmail();
            userId = user.getId();
        } else {
            name = form.getName();
            email = form.getEmail();
        }

        var message = new MessageEntity();
        message.setTime(LocalDateTime.now());
        message.setName(name);
        message.setEmail(email);
        message.setSubject(form.getSubject());
        message.setText(form.getText());
        message.setUserId(userId);
        messageRepository.save(message);
    }
}
