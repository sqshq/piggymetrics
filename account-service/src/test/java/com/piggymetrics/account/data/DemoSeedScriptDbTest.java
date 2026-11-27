package com.piggymetrics.account.data;

import com.piggymetrics.account.data.support.AbstractAccountDataTierTest;
import com.piggymetrics.account.data.support.TestCaseId;
import com.piggymetrics.account.domain.Account;
import com.piggymetrics.account.domain.Item;
import org.bson.Document;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static com.piggymetrics.account.data.support.AccountFixtures.full;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Componente DB-4: script de semilla mongodb/dump/account-service-dump.js, que crea la cuenta "demo".
 *
 * <p>En producción init.sh lo ejecuta con la shell "mongo". Aquí se ejecuta el mismo archivo, sin
 * cambios, con el comando de servidor "eval" de MongoDB 3.6 (ver supuestos en la documentación).
 * La ruta del script se puede cambiar con -Dseed.script=... (lo usa el script de mutación).
 */
public class DemoSeedScriptDbTest extends AbstractAccountDataTierTest {

	private static final Path SEED_SCRIPT =
			Paths.get(System.getProperty("seed.script", "../mongodb/dump/account-service-dump.js"));

	private static final long CLOCK_TOLERANCE_MS = 1_000;

	@Test
	@TestCaseId("DB-S-01")
	public void createsDemoAccountOnEmptyDatabase() throws IOException {
		runSeed();

		assertThat(count()).isEqualTo(1);
		Account demo = repository.findByName("demo");
		assertThat(demo).isNotNull();
		assertThat(demo.getExpenses()).hasSize(7);
		assertThat(demo.getIncomes()).hasSize(2);
		assertThat(demo.getSaving()).isNotNull();
		assertThat(demo.getNote()).isEqualTo("demo note");
	}

	@Test
	@TestCaseId("DB-S-02")
	public void runningTheSeedRepeatedlyIsIdempotent() throws IOException {
		runSeed();
		Document afterFirst = withoutLastSeen(rawById("demo"));

		runSeed();
		runSeed();

		assertThat(count()).isEqualTo(1);
		assertThat(withoutLastSeen(rawById("demo"))).isEqualTo(afterFirst);
	}

	@Test
	@TestCaseId("DB-S-03")
	public void seedLeavesOtherAccountsUntouched() throws IOException {
		repository.saveAll(Arrays.asList(full("alice"), full("bob")));
		Document alice = rawById("alice");
		Document bob = rawById("bob");

		runSeed();

		assertThat(count()).isEqualTo(3);
		assertThat(rawById("alice")).isEqualTo(alice);
		assertThat(rawById("bob")).isEqualTo(bob);
	}

	@Test
	@TestCaseId("DB-S-04")
	public void seedResetsAModifiedDemoAccount() throws IOException {
		raw().insertOne(new Document("_id", "demo")
				.append("note", "modificada")
				.append("expenses", Collections.emptyList())
				.append("legacyField", true));

		runSeed();

		Document demo = rawById("demo");
		assertThat(demo.get("note")).isEqualTo("demo note");
		assertThat((List<?>) demo.get("expenses")).hasSize(7);
		assertThat(demo).as("reemplazo completo: no quedan campos viejos").doesNotContainKey("legacyField");
	}

	@Test
	@TestCaseId("DB-S-05")
	public void lastSeenIsTheExecutionTime() throws IOException {
		long before = System.currentTimeMillis();
		runSeed();
		long after = System.currentTimeMillis();

		Date lastSeen = repository.findByName("demo").getLastSeen();
		assertThat(lastSeen).isNotNull();
		assertThat(lastSeen.getTime()).isBetween(before - CLOCK_TOLERANCE_MS, after + CLOCK_TOLERANCE_MS);
	}

	@Test
	@TestCaseId("DB-S-06")
	public void seededAccountSatisfiesDomainConstraints() throws IOException {
		runSeed();
		Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

		Set<ConstraintViolation<Account>> violations = validator.validate(repository.findByName("demo"));

		assertThat(violations).as("violaciones de las anotaciones de Account/Item/Saving").isEmpty();
	}

	@Test
	@TestCaseId("DB-S-07")
	public void seededNumbersAreDoublesReadableAsNonNegativeBigDecimal() throws IOException {
		runSeed();

		Document rawSaving = (Document) rawById("demo").get("saving");
		assertThat(rawSaving.get("amount")).as("la shell guarda números JS como Double, no como String")
				.isInstanceOf(Double.class);
		assertThat(rawById("demo")).as("el script no escribe _class").doesNotContainKey("_class");
		// Refuerzo por mutación (SM12, ver 3.6): al leer, Spring convierte "yes" en true, así que el tipo
		// guardado se verifica en el documento crudo y no en el objeto leído.
		assertThat(rawSaving.get("deposit")).as("tipo BSON de deposit").isInstanceOf(Boolean.class);
		assertThat(rawSaving.get("capitalization")).as("tipo BSON de capitalization").isInstanceOf(Boolean.class);
		assertThat(rawSaving.get("currency")).as("tipo BSON de currency").isInstanceOf(String.class);

		Account demo = repository.findByName("demo");
		List<BigDecimal> amounts = new ArrayList<>();
		amounts.add(demo.getSaving().getAmount());
		amounts.add(demo.getSaving().getInterest());
		for (Item item : demo.getExpenses()) {
			amounts.add(item.getAmount());
		}
		for (Item item : demo.getIncomes()) {
			amounts.add(item.getAmount());
		}
		assertThat(amounts).allSatisfy(amount -> assertThat(amount).isNotNull().isGreaterThanOrEqualTo(BigDecimal.ZERO));
	}

	private void runSeed() throws IOException {
		String script = new String(Files.readAllBytes(SEED_SCRIPT), StandardCharsets.UTF_8);
		Document result = mongo.getDb().runCommand(new Document("eval", script));
		assertThat(result.get("ok")).as("resultado de eval: %s", result.toJson()).isEqualTo(1.0);
	}

	private static Document withoutLastSeen(Document document) {
		Document copy = new Document(document);
		copy.remove("lastSeen");
		return copy;
	}
}
