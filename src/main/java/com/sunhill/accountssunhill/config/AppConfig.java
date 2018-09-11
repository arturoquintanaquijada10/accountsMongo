package com.sunhill.accountssunhill.config;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;

import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.types.Decimal128;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.env.Environment;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.lang.NonNull;

@Configuration
public class AppConfig {

	@Autowired
	private Environment env;	
	

	public @Bean MongoClient mongoClient() {
		CodecRegistry codecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(), fromProviders(
				PojoCodecProvider.builder().register("com.sunhill.accountssunhill.dto").build()));
		MongoClientOptions.Builder options = new MongoClientOptions.Builder().codecRegistry(codecRegistry);
		MongoClientURI uri = new MongoClientURI(env.getProperty("app.connection.mongodb.replicaset"), options);
		return new MongoClient(uri);
	}

	public @Bean MongoDbFactory mongoDbFactory() {
		return new SimpleMongoDbFactory(mongoClient(), env.getProperty("app.mongodb.database.name"));
	}

	public @Bean MongoTemplate mongoTemplate() {
		
		MongoTemplate mongoTemplate=new MongoTemplate(mongoDbFactory());		
		MappingMongoConverter mongoMapping = (MappingMongoConverter) mongoTemplate.getConverter();
        mongoMapping.setCustomConversions(mongoCustomConversions()); 
        mongoMapping.setTypeMapper(new DefaultMongoTypeMapper(null));
        mongoMapping.afterPropertiesSet();		
		return mongoTemplate;
	}
	
	
	@Bean
	public MongoCustomConversions mongoCustomConversions() {
	    return new MongoCustomConversions(Arrays.asList(
	        new BigDecimalDecimal128Converter(),
	        new Decimal128BigDecimalConverter()
	    ));
	}

	@WritingConverter
	private static class BigDecimalDecimal128Converter implements Converter<BigDecimal, Decimal128> {

	    @Override
	    public Decimal128 convert(@NonNull BigDecimal source) {
	        return new Decimal128(source);
	    }
	}

	@ReadingConverter
	private static class Decimal128BigDecimalConverter implements Converter<Decimal128, BigDecimal> {

	    @Override
	    public BigDecimal convert(@NonNull Decimal128 source) {
	        return source.bigDecimalValue().setScale(10, RoundingMode.CEILING);
	    }

	}
	
	
	
	
}