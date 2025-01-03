package com.kevin.todo_app.mongo;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;

@Configuration
public class MongoConfig extends AbstractReactiveMongoConfiguration {

    @Override
    protected String getDatabaseName() {
        return "todo_app";
    }

    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create();
    }

    /* CONFIG DE ROUTER */
    @Bean
    public WebProperties.Resources resources() {
        return new WebProperties.Resources();
    }

}
