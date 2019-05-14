package com.mongo.Codec;


import org.bson.BsonReader;
import org.bson.BsonTimestamp;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import java.sql.Timestamp;

public class DateCodec implements Codec<Timestamp> {

    public DateCodec() {
    }

    @Override
    public Timestamp decode(BsonReader bsonReader, DecoderContext decoderContext) {
        BsonTimestamp bsonTimestamp = bsonReader.readTimestamp();
        return new Timestamp(bsonTimestamp.getValue());
    }

    @Override
    public void encode(BsonWriter bsonWriter, Timestamp timestamp, EncoderContext encoderContext) {
        bsonWriter.writeTimestamp(new BsonTimestamp(timestamp.getTime()));
    }

    @Override
    public Class<Timestamp> getEncoderClass() {
        return Timestamp.class;
    }
}
