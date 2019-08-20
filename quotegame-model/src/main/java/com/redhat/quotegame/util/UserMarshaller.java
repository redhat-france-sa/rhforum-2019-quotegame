package com.redhat.quotegame.util;

import java.io.IOException;

import com.redhat.quotegame.model.User;

import org.infinispan.protostream.MessageMarshaller;

/**
 * Infinispan Protobuf marshaller for User class.
 * @author laurent
 */
public class UserMarshaller implements MessageMarshaller<User> {

    @Override
    public String getTypeName() {
        return "quotegame.User";
    }

    @Override
    public Class<? extends User> getJavaClass() {
        return User.class;
    }

    @Override
    public void writeTo(ProtoStreamWriter writer, User user) throws IOException {
        writer.writeString("name", user.getName());
        writer.writeString("email", user.getEmail());
    }

    @Override
    public User readFrom(ProtoStreamReader reader) throws IOException {
        String name = reader.readString("name");
        String email = reader.readString("email");
        return new User(name, email);
    }
}