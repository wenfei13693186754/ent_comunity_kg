package com.kg.platform.config;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Config;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.ogm.session.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.transaction.Neo4jTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author xyf
 * @CREATE 2017-04-13 11:54 AM
 */
@Configuration
@EnableTransactionManagement
public class Neo4jConfig {

    @Value("${db.neo4j.driver}")
    private String driver;
    @Value("${db.neo4j.connection.poll.size:10}")
    private int pollSize;
    @Value("${db.neo4j.user}")
    private String user;
    @Value("${db.neo4j.password}")
    private String password;
    @Value("${db.neo4j.server.uri}")
    private String server;
    @Value("${db.neo4j.dbPath}")
    private String dbPath;
    @Value("${forkjoin.Pool.thread.max:0}")
    private String forkjoinMaxThread;
    private final static Logger log = LoggerFactory.getLogger(Neo4jConfig.class);

    @Bean
    public SessionFactory sessionFactory() {
        // with domain entity base package(s)
        return new SessionFactory(configuration(), "com.kg.platform.entities");
    }

    @Bean
    public org.neo4j.ogm.config.Configuration configuration() {
        org.neo4j.ogm.config.Configuration configuration = new org.neo4j.ogm.config.Configuration.Builder()
                .uri(server)
                .credentials(user, password)
                .connectionPoolSize(pollSize)
                .build();
        return configuration;
    }

    @Bean
    public Neo4jTransactionManager transactionManager() {
        return new Neo4jTransactionManager(sessionFactory());

    }

    public Driver getDriver() {
        Driver driver = GraphDatabase.driver(server, AuthTokens.basic(user, password), Config.builder()
                .withMaxTransactionRetryTime(30, TimeUnit.SECONDS)
                .build());
        return driver;
    }
}
