package com.piggymetrics.serverless;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

/** In-process Mongo wire-protocol server, no Docker or downloaded mongod required. */
@TestConfiguration
public class MongoTestConfiguration {
    @Bean(destroyMethod = "shutdownNow")
    MongoServer mongoServer() {
        MongoServer server = new MongoServer(new MemoryBackend());
        server.bind();
        return server;
    }

    @Bean
    MongoClient mongoClient(MongoServer server) {
        return MongoClients.create("mongodb://" + server.getLocalAddress().getHostString()
                + ":" + server.getLocalAddress().getPort());
    }
}
