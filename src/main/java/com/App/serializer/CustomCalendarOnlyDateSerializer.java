package com.App.serializer;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class CustomCalendarOnlyDateSerializer extends JsonSerializer<Calendar> {
	
    public static final Locale LOCALE_BRASIL = new Locale("pt", "BR");
    public static final TimeZone LOCAL_TIME_ZONE = TimeZone.getTimeZone("Brazil/East");

    @Override
    public void serialize(Calendar value, JsonGenerator gen, SerializerProvider arg2)
            throws IOException, JsonProcessingException {
        if (value == null) {
            gen.writeNull();
        } else {
        	Locale.setDefault(LOCALE_BRASIL);
        	SimpleDateFormat FORMATTER = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        	FORMATTER.setTimeZone(LOCAL_TIME_ZONE);
            gen.writeString(FORMATTER.format(value.getTime()));
        }
    }
}
