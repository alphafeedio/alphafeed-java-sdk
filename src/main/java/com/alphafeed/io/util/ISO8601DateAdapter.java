package com.alphafeed.io.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class ISO8601DateAdapter implements JsonSerializer<Date>, JsonDeserializer<Date> {

    @Override
    public JsonElement serialize(Date date, Type type, JsonSerializationContext context) {
        return new JsonPrimitive(DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(
                date.toInstant().atZone(java.time.ZoneId.systemDefault())
        ));
    }

    @Override
    public Date deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        try {
            String dateStr = element.getAsString();
            OffsetDateTime dateTime = OffsetDateTime.parse(dateStr);
            return Date.from(dateTime.toInstant());
        } catch (Exception e) {
            throw new JsonParseException("Cannot deserialize date: " + element, e);
        }
    }
}
