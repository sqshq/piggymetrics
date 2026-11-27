package com.piggymetrics.account.data;

import com.piggymetrics.account.data.support.AbstractAccountDataTierTest;
import com.piggymetrics.account.data.support.TestCaseId;
import com.piggymetrics.account.domain.Account;
import org.bson.Document;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static com.piggymetrics.account.data.support.AccountFixtures.full;
import static com.piggymetrics.account.data.support.AccountFixtures.minimal;
import static com.piggymetrics.account.data.support.AccountFixtures.repeat;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Componente DB-2: consulta derivada AccountRepository.findByName(String),
 * que Spring Data traduce a { "_id": name } porque name es el @Id.
 */
public class FindByNameQueryDbTest extends AbstractAccountDataTierTest {

	@Before
	public void seedThreeAccounts() {
		repository.saveAll(Arrays.asList(full("demo"), full("alice"), full("a.b")));
	}

	@Test
	@TestCaseId("DB-Q-01")
	public void findsExistingAccountByExactName() {
		Account found = repository.findByName("demo");

		assertThat(found).isNotNull();
		assertThat(found.getName()).isEqualTo("demo");
		assertThat(found.getNote()).isEqualTo("nota de demo");
	}

	@Test
	@TestCaseId("DB-Q-02")
	public void returnsNullForNameNotStored() {
		assertThat(repository.findByName("unknown")).isNull();
	}

	@Test
	@TestCaseId("DB-Q-03")
	public void returnsNullOnEmptyCollection() {
		mongo.dropCollection(COLLECTION);

		assertThat(repository.findByName("demo")).isNull();
	}

	@Test
	@TestCaseId("DB-Q-04")
	public void matchIsCaseSensitive() {
		assertThat(repository.findByName("Demo")).isNull();
		assertThat(repository.findByName("DEMO")).isNull();
	}

	@Test
	@TestCaseId("DB-Q-05")
	public void whitespaceIsNotTrimmed() {
		assertThat(repository.findByName(" demo")).isNull();
		assertThat(repository.findByName("demo ")).isNull();
		assertThat(repository.findByName("de mo")).isNull();
	}

	@Test
	@TestCaseId("DB-Q-06")
	public void specialCharactersAreMatchedLiterally() {
		assertThat(repository.findByName("a.b")).as("nombre con punto, coincidencia exacta").isNotNull();
		assertThat(repository.findByName("aXb")).as("'.' no actúa como comodín").isNull();
		assertThat(repository.findByName(".*")).isNull();
		assertThat(repository.findByName("^d")).isNull();
		assertThat(repository.findByName("{\"$ne\": null}")).as("operador Mongo como texto").isNull();
	}

	@Test
	@TestCaseId("DB-Q-07")
	public void substringsAndExtensionsDoNotMatch() {
		assertThat(repository.findByName("dem")).as("prefijo, longitud - 1").isNull();
		assertThat(repository.findByName("emo")).as("sufijo").isNull();
		assertThat(repository.findByName("demo1")).as("extensión, longitud + 1").isNull();
	}

	@Test
	@TestCaseId("DB-Q-08")
	public void nullAndEmptyNameReturnNullWithoutError() {
		assertThat(repository.findByName(null)).isNull();
		assertThat(repository.findByName("")).isNull();
	}

	@Test
	@TestCaseId("DB-Q-09")
	public void dataTierImposesNoLengthLimitOnNames() {
		// Límites de User.username (@Length 3..20) y valores fuera de ellos.
		List<String> names = Arrays.asList(
				repeat('k', 1), repeat('k', 2), repeat('k', 3),
				repeat('k', 20), repeat('k', 21), repeat('k', 1000));
		for (String name : names) {
			repository.save(minimal(name));
		}

		for (String name : names) {
			Account found = repository.findByName(name);
			assertThat(found).as("nombre de longitud %d", name.length()).isNotNull();
			assertThat(found.getName()).isEqualTo(name);
		}
	}

	@Test
	@TestCaseId("DB-Q-10")
	public void nonAsciiNamesMatchExactlyAndAccentSensitive() {
		repository.saveAll(Arrays.asList(minimal("josé"), minimal("数据"), minimal("piggy🐷")));

		assertThat(repository.findByName("josé")).isNotNull();
		assertThat(repository.findByName("数据")).isNotNull();
		assertThat(repository.findByName("piggy🐷")).isNotNull();
		assertThat(repository.findByName("jose")).as("sin tilde es otro nombre").isNull();
	}

	@Test
	@TestCaseId("DB-Q-11")
	public void queryDoesNotModifyStoredData() {
		List<Document> before = allRaw();

		repository.findByName("demo");
		repository.findByName("unknown");
		repository.findByName(null);

		assertThat(allRaw()).isEqualTo(before);
	}

	@Test
	@TestCaseId("DB-Q-12")
	public void returnsTheMatchingDocumentAmongMany() {
		for (int i = 0; i < 50; i++) {
			Account account = minimal(String.format("user-%02d", i));
			account.setNote("nota " + i);
			repository.save(account);
		}

		for (int i : new int[]{0, 1, 25, 48, 49}) {
			Account found = repository.findByName(String.format("user-%02d", i));
			assertThat(found.getNote()).isEqualTo("nota " + i);
		}
	}
}
