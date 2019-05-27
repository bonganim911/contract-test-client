package com.example.consumer;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.common.io.Closeables;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.io.InputStreamReader;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SchemaValidationTests {

    @Autowired
    RestTemplate restTemplate;

    @LocalServerPort
    int port;

    @Test
    public void testShouldKnowThatTheSchemaValidatesTheExpectedVariable() throws Exception {
        InputStream resourceAsStream = SchemaValidationTests.class.getResourceAsStream("/schema.json");
        String contentString = CharStreams.toString(new InputStreamReader(resourceAsStream, Charsets.UTF_8));
        Closeables.closeQuietly(resourceAsStream);
        JSONObject jsonSchema =
                new JSONObject(new JSONTokener(contentString));

        
        String responseBody = bodyOfResponse("3");
        JSONObject responseObject = new JSONObject(new JSONTokener(responseBody));

        Schema schema = SchemaLoader.load(jsonSchema);
        schema.validate(responseObject);
    }


    private String bodyOfResponse(String number){
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json");


        ResponseEntity<String> responseEntity = restTemplate.exchange(
                "http://localhost:"  + port + "/calculate?number=" + number,
                HttpMethod.GET,
                new HttpEntity<>(httpHeaders),
                String.class);

        return responseEntity.getBody();
    }
}
