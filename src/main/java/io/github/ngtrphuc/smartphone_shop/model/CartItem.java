package io.github.ngtrphuc.smartphone_shop.model;
public class CartItem {
    private Long id;
    private String name;
    private Double price;
    private int quantity;
    public CartItem() {}
    public CartItem(Long id, String name, Double price, int quantity) {
        this.id = id;
        this.name = name;
        this.price = price != null ? price : 0.0;
        this.quantity = quantity;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public Double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setPrice(Double price) { this.price = price; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}