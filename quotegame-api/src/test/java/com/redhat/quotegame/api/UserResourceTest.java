package com.redhat.quotegame.api;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.equalTo;

import com.redhat.quotegame.InfinispanServerTestResource;
import com.redhat.quotegame.model.User;

@QuarkusTest
@QuarkusTestResource(InfinispanServerTestResource.class)
public class UserResourceTest {

    @Test
    public void testCreateUserEndpoint() {
        User laurent = new User("Laurent", "laurent.broudoux@gmail.com");

        given()
          .body(laurent) 
          .contentType("application/json")
          .when().post("/api/user")
          .then()
             .statusCode(200)
             .body("name", equalTo("Laurent"));
    }

}