package com.example.appshopping.Domain;

import java.io.Serializable;
import java.util.ArrayList;

public class ItemsDomain implements Serializable {
    private String title;
    private String description; //Mô tả
    private ArrayList<String> picUrl;
    private double price;
    private double discount;
    private int review;
    private double rating;
    private int NumberinCart;
    private String productId;
    private int quantity;
    private String category;
    private double oldPrice;

    public ItemsDomain() {
    }

    public ItemsDomain(String title, String description, ArrayList<String> picUrl, double price, double discount, int review, double rating, String productId, int quantity, String category, double oldPrice) {
        this.title = title;
        this.description = description;
        this.picUrl = picUrl;
        this.price = price;
        this.discount = discount;
        this.review = review;
        this.rating = rating;
        this.quantity = quantity;
        this.productId = productId;
        this.category = category;
        this.oldPrice = oldPrice;
    }

    public double getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(double oldPrice) {
        this.oldPrice = oldPrice;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getNumberinCart() {
        return NumberinCart;
    }

    public void setNumberinCart(int numberInCart) {
        this.NumberinCart = numberInCart;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getReview() {
        return review;
    }

    public void setReview(int review) {
        this.review = review;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public ArrayList<String> getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(ArrayList<String> picUrl) {
        this.picUrl = picUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
