package com.piggymetrics.account.data.support;

import com.mongodb.client.MongoCollection;
import com.piggymetrics.account.domain.Account;
import com.piggymetrics.account.repository.AccountRepository;
import org.bson.Document;
import org.bson.RawBsonDocument;
import org.bson.codecs.DocumentCodec;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * Base de las suites de la capa de datos de account-service.
 *
 * <p>Aislamiento:
 * <ul>
 *   <li>{@code @DataMongoTest} carga solo la porción de Mongo del contexto: no se crean los clientes Feign
 *   (auth-service, statistics-service), ni seguridad OAuth2, ni Eureka, ni Hystrix, ni RabbitMQ.</li>
 *   <li>La base es un MongoDB 3.6.23 embebido (misma versión que la imagen de producción), en un puerto
 *   aleatorio, sin autenticación y sin datos persistentes entre ejecuciones.</li>
 *   <li>Antes de cada prueba se elimina la colección, así ninguna prueba depende del orden de ejecución.</li>
 * </ul>
 * Las cuatro suites usan la misma configuración, por lo que comparten un único contexto y un único mongod.
 */
@RunWith(SpringRunner.class)
@DataMongoTest
@TestPropertySource(locations = "classpath:data-tier.properties")
public abstract class AbstractAccountDataTierTest {

	protected static final String COLLECTION = "accounts";

	@Autowired
	protected AccountRepository repository;

	@Autowired
	protected MongoTemplate mongo;

	@Before
	public void startWithEmptyCollection() {
		mongo.dropCollection(COLLECTION);
	}

	protected MongoCollection<Document> raw() {
		return mongo.getCollection(COLLECTION);
	}

	/** Documento BSON tal como quedó guardado, sin pasar por el mapeo de Spring. */
	protected Document rawById(String id) {
		return raw().find(new Document("_id", id)).first();
	}

	protected List<Document> allRaw() {
		return raw().find().sort(new Document("_id", 1)).into(new ArrayList<>());
	}

	protected long count() {
		return raw().count();
	}

	/** Tamaño en bytes del documento BSON que el mapeo de Spring genera para la cuenta. */
	protected int bsonSize(Account account) {
		Document document = new Document();
		mongo.getConverter().write(account, document);
		return new RawBsonDocument(document, new DocumentCodec()).getByteBuffer().remaining();
	}

	/** Límite de tamaño de documento que informa el servidor (16 MiB en MongoDB 3.6). */
	protected int maxBsonObjectSize() {
		return mongo.getDb().runCommand(new Document("isMaster", 1)).getInteger("maxBsonObjectSize");
	}
}
