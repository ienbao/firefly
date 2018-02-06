package com.dmsoft.firefly.core.utils;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.util.logging.Level;
import java.util.logging.Logger;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

/**
 * Created by Lucien.Chen on 2018/2/6.
 */
public class MongoUtil {
    private static String host = "localhost";
    private static String dbName = "test";

    private static MongoClient mongoClient;
    private static MongoDatabase database;

    static {
        Logger mongoLogger = Logger.getLogger( "org.mongodb.driver" );
        mongoLogger.setLevel(Level.INFO);

        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        mongoClient = new MongoClient(host, MongoClientOptions.builder().
                codecRegistry(pojoCodecRegistry).build());

        database = mongoClient.getDatabase(dbName);
        database = database.withCodecRegistry(pojoCodecRegistry);

    }
    public static MongoCollection getCollection(String collectionName) {

        return database.getCollection(collectionName);
    }

    public static MongoCollection getCollection(String collectionName, Class document) {

        return database.getCollection(collectionName,document);
    }
}
