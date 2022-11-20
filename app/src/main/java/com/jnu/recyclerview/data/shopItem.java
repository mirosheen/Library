package com.jnu.recyclerview.data;

import java.io.Serializable;
//这里继承序列化，可以进行序列化写入到文件
public class shopItem implements Serializable {
    private String title;
    private String author;
    private int resourceId;
    private String BookShelf;
    private String Translator;
    private String Publisher;
    private String PubDate;
    private String ISBN;
    private String Note;
    private String Label;
    private String url;
    private String state;
    public shopItem(){

    }
    public shopItem(shopItem ShopItem) {
        this.title=ShopItem.getTitle();
        this.author=ShopItem.getAuthor();
        this.resourceId=ShopItem.getResourceId();
        this.BookShelf=ShopItem.getBookShelf();
        this.Translator=ShopItem.getTranslator();
        this.Publisher=ShopItem.getPublisher();
        this.PubDate=ShopItem.getPubDate();
        this.ISBN=ShopItem.getISBN();
        this.Note=ShopItem.getNote();
        this.Label=ShopItem.getLabel();
        this.url=ShopItem.getUrl();
        this.state=ShopItem.getState();
    }
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTranslator() {
        return Translator;
    }

    public void setTranslator(String translator) {
        Translator = translator;
    }

    public String getPublisher() {
        return Publisher;
    }

    public void setPublisher(String publisher) {
        Publisher = publisher;
    }

    public String getPubDate() {
        return PubDate;
    }

    public void setPubDate(String pubDate) {
        PubDate = pubDate;
    }

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public String getNote() {
        return Note;
    }

    public void setNote(String note) {
        Note = note;
    }

    public String getLabel() {
        return Label;
    }

    public void setLabel(String label) {
        Label = label;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public String getBookShelf() {
        return BookShelf;
    }

    public void setBookShelf(String bookShelf) {
        BookShelf = bookShelf;
    }


}