package com.app.tourguide.database.converter;

import android.arch.persistence.room.TypeConverter;

import com.app.tourguide.ui.avaliableplaces.model.DataItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;

public class DataConverter implements Serializable {

    @TypeConverter // note this annotation
    public String fromOptionValuesList(List<DataItem> optionValues) {
        if (optionValues == null) {
            return (null);
        }

        Gson gson = new Gson();
        Type type = new TypeToken<List<DataItem>>() {
        }.getType();
        String json = gson.toJson(optionValues, type);
        return json;
    }

    @TypeConverter // note this annotation
    public List<DataItem> toOptionValuesList(String optionValuesString) {
        if (optionValuesString == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<DataItem>>() {
        }.getType();
        List<DataItem> productCategoriesList = gson.fromJson(optionValuesString, type);
        return productCategoriesList;
    }

}
