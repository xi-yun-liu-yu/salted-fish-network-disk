package com.xiyun.saltedfishnetdish.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimestampSerializer extends JsonSerializer<Timestamp> {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy - MM - dd HH:mm:ss");

    @Override
    public void serialize(Timestamp value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        Date date = new Date(value.getTime());
        gen.writeString(sdf.format(date));
    }
}
