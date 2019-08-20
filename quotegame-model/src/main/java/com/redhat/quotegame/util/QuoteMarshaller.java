package com.redhat.quotegame.util;

import java.io.IOException;

import com.redhat.quotegame.model.Quote;

import org.infinispan.protostream.MessageMarshaller;

/**
 * Infinispan Protobuf marshaller for Quote class.
 * @author laurent
 */
public class QuoteMarshaller implements MessageMarshaller<Quote> {

    @Override
    public String getTypeName() {
        return "quotegame.Quote";
    }

    @Override
    public Class<? extends Quote> getJavaClass() {
        return Quote.class;
    }

    @Override
    public void writeTo(ProtoStreamWriter writer, Quote quote) throws IOException {
        writer.writeString("symbol", quote.getSymbol());
        writer.writeDouble("price", quote.getPrice());
    }

    @Override
    public Quote readFrom(ProtoStreamReader reader) throws IOException {
        String symbol = reader.readString("symbol");
        Double price = reader.readDouble("price");
        return new Quote(symbol, price);
    }
}