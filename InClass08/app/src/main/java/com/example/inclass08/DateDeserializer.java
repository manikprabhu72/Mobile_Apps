package com.example.inclass08;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateDeserializer implements JsonDeserializer<Date> {
    private static final SimpleDateFormat datePattern = new SimpleDateFormat("yyyy-MM-dd");
    //private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
    public Date deserialize(JsonElement dateStr, Type typeOfSrc, JsonDeserializationContext context)
    {
        try
        {
            return datePattern.parse(dateStr.getAsString());
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
