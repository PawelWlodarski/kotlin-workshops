package lodz.jug.kotlin.spring.functionalwebstart.answers;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.reactive.server.WebTestClient;

class SpringPart1JavaTest {

    WebTestClient client = WebTestClient.bindToApplicationContext(SpringPart1Answers.INSTANCE.getCtx()).build();

    @Test
    void simpleGet() {
            client.get().uri("/exercise1get")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("Hello exercise 1");
    }



    @Test
    void putAndGetUsers(){
        client.put().uri("/datastore/user/1")
                .exchange()
                .expectStatus().isOk();

        client.put().uri("/datastore/user/2")
                .exchange()
                .expectStatus().isOk();

        client.put().uri("/datastore/user/4")
                .exchange()
                .expectStatus().isOk();

        client.get().uri("/datastore/users")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("User1,User2,User4");

    }

    @Test
    void receiveBadRequestForMonoError(){
            client.put().uri("/datastore/user/3")
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody(String.class)
                    .isEqualTo("ERROR:"+SpringPart1Answers.ERROR_MESSAGE);
    }
}
