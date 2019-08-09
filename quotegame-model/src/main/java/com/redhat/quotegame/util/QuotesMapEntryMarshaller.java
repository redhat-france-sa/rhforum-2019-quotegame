package com.redhat.quotegame.util;

import java.io.IOException;

import com.redhat.quotegame.model.Portfolio;

import org.infinispan.protostream.MessageMarshaller;

/**
 * Infinispan Protobuf marshaller for QuotesMapEntry class.
 * 
 * @author laurent
 */
public class QuotesMapEntryMarshaller implements MessageMarshaller<Portfolio.QuotesMapEntry> {
    
    @Override
    public String getTypeName() {
        return "quotegame.QuotesMapEntry";
    }

    @Override
    public Class<? extends Portfolio.QuotesMapEntry> getJavaClass() {
        return Portfolio.QuotesMapEntry.class;
    }

    @Override
    public void writeTo(ProtoStreamWriter writer, Portfolio.QuotesMapEntry entry) throws IOException {
        writer.writeString("key", entry.getKey());
        writer.writeLong("value", entry.getValue());
    }

    @Override
    public Portfolio.QuotesMapEntry readFrom(ProtoStreamReader reader) throws IOException {
        String key = reader.readString("key");
        Long value = reader.readLong("value");
        return new Portfolio.QuotesMapEntry(key, value);
    }
}