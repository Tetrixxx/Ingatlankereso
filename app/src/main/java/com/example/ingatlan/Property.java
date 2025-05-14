package com.example.ingatlan;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Property implements Parcelable {
    private String id;
    private String title;
    private String price;
    private String address;
    private String type;
    private int bedrooms;
    private int bathrooms;
    private double squareFootage;
    private List<String> imageUrls;
    private String description;

    // Empty constructor needed for Firestore
    public Property() {
    }

    public Property(String title, String price, String address,String type, int bedrooms,
                    int bathrooms, double squareFootage, List<String> imageUrls) {
        this.title = title;
        this.price = price;
        this.address = address;
        this.type = type;
        this.bedrooms = bedrooms;
        this.bathrooms = bathrooms;
        this.squareFootage = squareFootage;
        this.imageUrls = imageUrls;
    }

    protected Property(Parcel in) {
        id = in.readString();
        title = in.readString();
        price = in.readString();
        address = in.readString();
        bedrooms = in.readInt();
        bathrooms = in.readInt();
        squareFootage = in.readDouble();
        imageUrls = in.createStringArrayList();
        description = in.readString();
    }

    public static final Creator<Property> CREATOR = new Creator<Property>() {
        @Override
        public Property createFromParcel(Parcel in) {
            return new Property(in);
        }

        @Override
        public Property[] newArray(int size) {
            return new Property[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(price);
        dest.writeString(address);
        dest.writeInt(bedrooms);
        dest.writeInt(bathrooms);
        dest.writeDouble(squareFootage);
        dest.writeStringList(imageUrls);
        dest.writeString(description);
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getBedrooms() { return bedrooms; }
    public void setBedrooms(int bedrooms) { this.bedrooms = bedrooms; }

    public int getBathrooms() { return bathrooms; }
    public void setBathrooms(int bathrooms) { this.bathrooms = bathrooms; }

    public double getSquareFootage() { return squareFootage; }
    public void setSquareFootage(double squareFootage) { this.squareFootage = squareFootage; }

    public List<String> getImageUrls() { return imageUrls; }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return "Property{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", price='" + price + '\'' +
                ", address='" + address + '\'' +
                ", bedrooms=" + bedrooms +
                ", bathrooms=" + bathrooms +
                ", squareFootage=" + squareFootage +
                ", imageUrls=" + imageUrls +
                ", description='" + description + '\'' +
                '}';
    }
}
