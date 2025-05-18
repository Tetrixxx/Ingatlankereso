package com.example.ingatlan;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.List;

public class Property implements Parcelable {
    private String title;
    private String address;
    private String type;
    private int price;
    private String city;
    private List<String> searchTerms;

    // Firestore-hoz kötelező üres konstruktor
    public Property() {
        searchTerms = new ArrayList<>();
    }

    public Property(String title, String address, String type, int price, String city) {
        this.title = title;
        this.address = address;
        this.type = type;
        this.price = price;
        this.city = city;
        this.searchTerms = generateSearchTerms();
    }

    // Parcelable implementáció
    protected Property(Parcel in) {
        title = in.readString();
        address = in.readString();
        type = in.readString();
        price = in.readInt();
        city = in.readString();
        searchTerms = in.createStringArrayList();
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
        dest.writeStringList(searchTerms);
    }

    // Keresési kifejezések generálása
    public List<String> generateSearchTerms() {
        List<String> searchTerms = new ArrayList<>();
        // Tegyük fel, hogy a keresendő mező a title (címet) tartalmazza
        String title = getTitle().toLowerCase();
        String address = getAddress().toLowerCase();
        String type = getType().toLowerCase();
        String city = getCity().toLowerCase();
        title += " " + address + " " + type + " " + city;
        // Szavakra bontás szóköz alapján
        String[] words = title.split(" ");
        for (String word : words) {
            // Minden egyes karakter prefixét hozzáadjuk a listához
            StringBuilder prefix = new StringBuilder();
            for (int i = 0; i < word.length(); i++) {
                prefix.append(word.charAt(i));
                searchTerms.add(prefix.toString());
            }
        }
        return searchTerms;
    }

    // Getterek
    public String getTitle() { return title; }
    public String getAddress() { return address; }
    public String getType() { return type; }
    public int getPrice() { return price; }
    public String getCity() { return city; }
    public List<String> getSearchTerms() { return searchTerms; }

    // Setterek (opcionális, Firestore-hoz nem szükséges)
    public void setTitle(String title) { this.title = title; }
    public void setAddress(String address) { this.address = address; }
    public void setType(String type) { this.type = type; }
    public void setPrice(int price) { this.price = price; }
    public void setCity(String city) { this.city = city; }
}