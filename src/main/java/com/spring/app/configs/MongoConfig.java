package com.spring.app.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

@Configuration
public class MongoConfig {

	@Bean
	public MongoTemplate mongoTemplate() {
		String uri = "mongodb://localhost:27017";
		String database = "hr-management";
		ServerApi serverApi = ServerApi.builder().version(ServerApiVersion.V1).build();
		MongoClientSettings settings = MongoClientSettings
				.builder().applyConnectionString(new ConnectionString(uri))
				.serverApi(serverApi).build();
		MongoClient clients = MongoClients.create(settings);
		MongoDatabaseFactory factory = new SimpleMongoClientDatabaseFactory(clients, database);
		return new MongoTemplate(factory);
	}
	
}
