package model;

import androidx.annotation.Keep;

import java.io.Serializable;
import java.util.UUID;

@Keep
public class StoreItem implements Serializable {
    private String id;
    private String name;
    private String type;
    private Double price;
    private String imageUrl;
    private String description;
    private int quantity = 0;

    public StoreItem() {}

    // construtor oficial (String)
    public StoreItem(String id, String name, String type, Double price, String imageUrl, String description) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.price = price;
        this.imageUrl = imageUrl;
        this.description = description;
    }

    // ✅ overload para compatibilidade com builds antigos que esperam UUID
    public StoreItem(UUID id, String name, String type, Double price, String imageUrl, String description) {
        this(id != null ? id.toString() : null, name, type, price, imageUrl, description);
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public Double getPrice() { return price; }
    public String getImageUrl() { return imageUrl; }
    public String getDescription() { return description; }
    public int getQuantity() { return quantity; }

    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setType(String type) { this.type = type; }
    public void setPrice(Double price) { this.price = price; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setDescription(String description) { this.description = description; }
    public void setQuantity(int q) { this.quantity = Math.max(0, q); }
}