package com.redhat.quotegame.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.redhat.quotegame.model.Portfolio;

import org.infinispan.protostream.MessageMarshaller;

/**
 * Infinispan Protobuf marshaller for Portfolio class.
 * @author laurent
 */
public class PortfolioMarshaller implements MessageMarshaller<Portfolio> {

    @Override
    public String getTypeName() {
        return "quotegame.Portfolio";
    }

    @Override
    public Class<? extends Portfolio> getJavaClass() {
        return Portfolio.class;
    }

    @Override
    public void writeTo(ProtoStreamWriter writer, Portfolio portfolio) throws IOException {
        writer.writeString("username", portfolio.getUsername());
        writer.writeDouble("money", portfolio.getMoney());
        // Serialize map as Infinispan protobuf does not support yet map.
        List<Portfolio.QuotesMapEntry> quotes = portfolio.getQuotes().entrySet().stream()
            .map(entry -> new Portfolio.QuotesMapEntry(entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());
        writer.writeCollection("quotes", quotes, Portfolio.QuotesMapEntry.class);
    }

    @Override
    public Portfolio readFrom(ProtoStreamReader reader) throws IOException {
        String username = reader.readString("username");
        Double money = reader.readDouble("money");
        List<Portfolio.QuotesMapEntry> quotes = new ArrayList<>();
        reader.readCollection("quotes", quotes, Portfolio.QuotesMapEntry.class);
        return new Portfolio(username, money, quotes);
    }
}