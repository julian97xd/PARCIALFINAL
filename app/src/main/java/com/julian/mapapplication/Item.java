package com.julian.mapapplication;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.UUID;

public class Item {
    String id;
    String name, cordillera, country, height;
    Location location;

    public Item(String name, String cordillera, String country, String height, Location location) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.cordillera = cordillera;
        this.height = height;
        this.location = location;
    }

    public Item(String id,String name, String cordillera, String country, String height, Location location) {
        this.id = id;
        this.name = name;
        this.cordillera = cordillera;
        this.height = height;
        this.location = location;
    }

    public Item() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCordillera() {
        return cordillera;
    }

    public void setCordillera(String cordillera) {
        this.cordillera = cordillera;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Item(Cursor cursor) {
        id = cursor.getString(cursor.getColumnIndex(ItemsContract.ItemsEntry.ID));
        name = cursor.getString(cursor.getColumnIndex(ItemsContract.ItemsEntry.NAME));
        cordillera = cursor.getString(cursor.getColumnIndex(ItemsContract.ItemsEntry.CORDILLERA));
        country = cursor.getString(cursor.getColumnIndex(ItemsContract.ItemsEntry.COUNTRY));
        height = cursor.getString(cursor.getColumnIndex(ItemsContract.ItemsEntry.HEIGHT));
        location = new Location(
                Double.parseDouble(cursor.getString(cursor.getColumnIndex(ItemsContract.ItemsEntry.LAT))),
                Double.parseDouble(cursor.getString(cursor.getColumnIndex(ItemsContract.ItemsEntry.LNG)))
        );
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(ItemsContract.ItemsEntry.ID, id);
        values.put(ItemsContract.ItemsEntry.NAME, name);
        values.put(ItemsContract.ItemsEntry.CORDILLERA, cordillera);
        values.put(ItemsContract.ItemsEntry.COUNTRY, country);
        values.put(ItemsContract.ItemsEntry.HEIGHT, height);
        values.put(ItemsContract.ItemsEntry.LAT, location.getLat());
        values.put(ItemsContract.ItemsEntry.LNG, location.getLng());
        return values;
    }

    public ArrayList<Item> toItems(Cursor cursor) {
        ArrayList<Item> ItemsArrayList = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            String id = cursor.getString(cursor.getColumnIndex(ItemsContract.ItemsEntry.ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(ItemsContract.ItemsEntry.NAME));
            String cordillera = cursor.getString(cursor.getColumnIndexOrThrow(ItemsContract.ItemsEntry.CORDILLERA));
            String country = cursor.getString(cursor.getColumnIndexOrThrow(ItemsContract.ItemsEntry.COUNTRY));
            String height = cursor.getString(cursor.getColumnIndexOrThrow(ItemsContract.ItemsEntry.HEIGHT));
            String lat = cursor.getString(cursor.getColumnIndexOrThrow(ItemsContract.ItemsEntry.LAT));
            String lng = cursor.getString(cursor.getColumnIndexOrThrow(ItemsContract.ItemsEntry.LNG));

            ItemsArrayList.add(new Item(id,name,cordillera,country,height, new Location(Double.parseDouble(lat), Double.parseDouble(lng))));
            cursor.moveToNext();
        }
        return ItemsArrayList;
    }

    public String getId() {
        return id;
    }
}
