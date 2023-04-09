package com.example.MyBookShopApp.struct.book.book;

public enum BookFileType {

    PDF(".pdf"), EPUB(".epub"), FB2(".fb2");

    private final String fileExtensionString;

    BookFileType(String fileExtensionString) {
        this.fileExtensionString = fileExtensionString;
    }

    public static String getExtensionStringByTypeId(Integer id){
        switch (id){
            case 1: return BookFileType.PDF.fileExtensionString;
            case 2: return BookFileType.EPUB.fileExtensionString;
            case 3: return BookFileType.FB2.fileExtensionString;
            default: return "";
        }
    }
}
