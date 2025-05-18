package com.example.ingatlan;

import android.os.Parcel;
import android.os.Parcelable;

public class Property implements Parcelable {
    private String title;
    private String address;
    private String type;
    private int price;
    private String city;

    // Firestore-hoz kötelező üres konstruktor
    public Property() {}

    public Property(String title, String address, String type, int price, String city) {
        this.title = title;
        this.address = address;
        this.type = type;
        this.price = price;
        this.city = city;
    }

    // Parcelable implementáció
    protected Property(Parcel in) {
        title = in.readString();
        address = in.readString();
        type = in.readString();
        price = in.readInt();
        city = in.readString();
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
        dest.writeString(title);
        dest.writeString(address);
        dest.writeString(type);
        dest.writeInt(price);
        dest.writeString(city);
    }

    // Getterek
    public String getTitle() { return title; }
    public String getAddress() { return address; }
    public String getType() { return type; }
    public int getPrice() { return price; }
    public String getCity() { return city; }
}