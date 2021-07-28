package abs.sf.ads.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import abs.sf.ads.db.mongo.MongoDb;
import abs.sf.ads.db.mysql.MysqlDatabase;

@Configuration
public class DatabaseLoader {
	private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseLoader.class);

	@Bean
	@ConditionalOnProperty(name = "db.name", havingValue = "MYSQL")
	public Database setupMysqlDb() {
		LOGGER.info("Loading Mysql database bean");
		return new MysqlDatabase();

	}

	@Bean
	@ConditionalOnProperty(name = "db.name", havingValue = "MONGO")
	public Database setupMongoDb() {
		LOGGER.info("Loading mongodb bean");
		return new MongoDb();
	}

}
