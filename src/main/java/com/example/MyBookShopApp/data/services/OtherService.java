package com.example.MyBookShopApp.data.services;

import com.example.MyBookShopApp.data.repo.BookFileTypeRepository;
import com.example.MyBookShopApp.data.repo.BookRepository;
import com.example.MyBookShopApp.data.repo.TagsRepository;
import com.example.MyBookShopApp.struct.book.book.Book;
import com.example.MyBookShopApp.struct.other.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OtherService {

    private TagsRepository tagsRepository;
    private BookRepository bookRepository;

    private BookFileTypeRepository bookFileTypeRepository;

    @Autowired
    public OtherService(TagsRepository tagsRepository, BookRepository bookRepository, BookFileTypeRepository bookFileTypeRepository) {
        this.tagsRepository = tagsRepository;
        this.bookRepository = bookRepository;
        this.bookFileTypeRepository = bookFileTypeRepository;
    }

    public List<Tag> getTagsList() {
        return tagsRepository.findAll();
    }

    public Map<Tag, Integer> getTagsAndSizesMap(){
        List<Tag> tagList = getTagsList();
        Map<Tag, Integer> tagMap = new HashMap<>();
        int min, max;

        if (tagList != null || !tagList.isEmpty() ){
            min = tagList.get(0).getBookList().size();
            max= min;
            for (Tag tag : tagList) {
                min = tag.getBookList().size() < min ? tag.getBookList().size() : min;
                max = tag.getBookList().size() > max ? tag.getBookList().size() : max;
            }
            float variance = (min == 0) ? (max-min) : (max-min+1);
            for (Tag tag : tagList) {
                float sizePercent = Float.valueOf(tag.getBookList().size()) /variance*10;
                tagMap.putIfAbsent(tag, (int) sizePercent);
            }
        }
        return tagMap;
    }

    public Page<Book> getPageOfTagBooks(Integer tagId, Integer offset, Integer limit){
        Pageable nextPage = PageRequest.of(offset, limit);
        Tag tag = tagsRepository.findTagByIdIs(tagId);
        Page<Book> page = bookRepository.findBooksByTagListContains(tag, nextPage);
        System.out.println("Количество подгруженных сервисом книг по тэгу = "+ page.stream().count());
        return page;
    }

    public String getTagNameById(Integer tagId){
        return tagsRepository.findTagByIdIs(tagId).getName();
    }

//    public String getExtensionStringByTypeId(Integer id){
//        return bookFileTypeRepository.findByIdIs(id).getName();
//    }
}
