package com.piggymetrics.account.data;

import com.piggymetrics.account.data.support.AbstractAccountDataTierTest;
import com.piggymetrics.account.data.support.TestCaseId;
import com.piggymetrics.account.domain.Account;
import com.piggymetrics.account.domain.Currency;
import com.piggymetrics.account.domain.Item;
import com.piggymetrics.account.domain.TimePeriod;
import org.bson.Document;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import static com.piggymetrics.account.data.support.AccountFixtures.assertSameContent;
import static com.piggymetrics.account.data.support.AccountFixtures.full;
import static com.piggymetrics.account.data.support.AccountFixtures.item;
import static com.piggymetrics.account.data.support.AccountFixtures.minimal;
import static com.piggymetrics.account.data.support.AccountFixtures.repeat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Componente DB-1: esquema del documento de la colección "accounts"
 * (mapeo Account / Item / Saving <-> BSON que hace Spring Data MongoDB).
 */
public class AccountDocumentMappingDbTest extends AbstractAccountDataTierTest {

	@Test
	@TestCaseId("DB-M-01")
	public void fullAccountSurvivesRoundTrip() {
		Account original = full("alice");

		repository.save(original);

		Account read = repository.findByName("alice");
		assertSameContent(original, read);
		// Oráculo independiente de los getters (refuerzo por mutación, ver 3.6): assertSameContent
		// lee lo esperado y lo obtenido con el mismo getter, y un getter mutado pasa en ambos lados.
		assertThat(read.getSaving().getCurrency()).isEqualTo(Currency.USD);
		assertThat(read.getExpenses().get(0).getIcon()).isEqualTo("meal");
		assertThat(read.getExpenses().get(1).getIcon()).isEqualTo("tourism");
		assertThat(read.getIncomes().get(0).getIcon()).isEqualTo("wallet");
	}

	@Test
	@TestCaseId("DB-M-13")
	public void savingBooleansRoundTripWithBothValues() {
		boolean[][] combinations = {{true, false}, {false, true}};   // (deposit, capitalization)

		for (int i = 0; i < combinations.length; i++) {
			boolean deposit = combinations[i][0];
			boolean capitalization = combinations[i][1];
			Account account = minimal("bool-" + i);
			account.getSaving().setDeposit(deposit);
			account.getSaving().setCapitalization(capitalization);
			repository.save(account);

			Document rawSaving = (Document) rawById("bool-" + i).get("saving");
			assertThat(rawSaving.get("deposit")).isEqualTo(deposit);
			assertThat(rawSaving.get("capitalization")).isEqualTo(capitalization);
			Account read = repository.findByName("bool-" + i);
			assertThat(read.getSaving().getDeposit()).as("deposit").isEqualTo(deposit);
			assertThat(read.getSaving().getCapitalization()).as("capitalization").isEqualTo(capitalization);
		}
	}

	@Test
	@TestCaseId("DB-M-02")
	public void documentUsesNameAsIdAndStoresTypeHint() {
		repository.save(full("alice"));

		Document raw = rawById("alice");
		assertThat(raw).as("documento en la colección accounts con _id = name").isNotNull();
		assertThat(raw.keySet()).containsExactlyInAnyOrder(
				"_id", "lastSeen", "note", "incomes", "expenses", "saving", "_class");
		assertThat(raw.get("_class")).isEqualTo(Account.class.getName());
		assertThat(raw.get("lastSeen")).isInstanceOf(Date.class);

		Document saving = (Document) raw.get("saving");
		assertThat(saving.keySet()).containsExactlyInAnyOrder(
				"amount", "currency", "interest", "deposit", "capitalization");
		Document firstExpense = (Document) ((List<?>) raw.get("expenses")).get(0);
		assertThat(firstExpense.keySet()).containsExactlyInAnyOrder(
				"title", "amount", "currency", "period", "icon");
	}

	@Test
	@TestCaseId("DB-M-03")
	public void bigDecimalIsStoredAsStringKeepingValueAndScale() {
		List<BigDecimal> values = Arrays.asList(
				new BigDecimal("0"),                                  // cero
				new BigDecimal("3.320"),                              // escala con ceros a la derecha
				new BigDecimal("-0.01"),                              // negativo
				new BigDecimal("12345678901234567890.123456789"),     // más precisión que un double
				new BigDecimal("1E-21"));                             // notación científica

		for (int i = 0; i < values.size(); i++) {
			BigDecimal value = values.get(i);
			Account account = minimal("bd-" + i);
			account.getSaving().setAmount(value);
			repository.save(account);

			Object stored = ((Document) rawById("bd-" + i).get("saving")).get("amount");
			assertThat(stored).as("representación guardada de %s", value).isEqualTo(value.toString());
			assertThat(repository.findByName("bd-" + i).getSaving().getAmount())
					.as("valor leído de %s (valor y escala)", value).isEqualTo(value);
		}
	}

	@Test
	@TestCaseId("DB-M-04")
	public void everyEnumValueIsStoredByName() {
		List<Item> items = new ArrayList<>();
		for (Currency currency : Currency.values()) {
			for (TimePeriod period : TimePeriod.values()) {
				items.add(item(currency + "-" + period, "1", currency, period, "icon"));
			}
		}
		Account account = minimal("enums");
		account.setExpenses(items);

		repository.save(account);

		List<?> rawItems = (List<?>) rawById("enums").get("expenses");
		assertThat(rawItems).hasSize(Currency.values().length * TimePeriod.values().length);
		List<Item> read = repository.findByName("enums").getExpenses();
		for (int i = 0; i < items.size(); i++) {
			Document rawItem = (Document) rawItems.get(i);
			assertThat(rawItem.get("currency")).isEqualTo(items.get(i).getCurrency().name());
			assertThat(rawItem.get("period")).isEqualTo(items.get(i).getPeriod().name());
			assertThat(read.get(i).getCurrency()).isEqualTo(items.get(i).getCurrency());
			assertThat(read.get(i).getPeriod()).isEqualTo(items.get(i).getPeriod());
		}
	}

	@Test
	@TestCaseId("DB-M-05")
	public void lastSeenKeepsMillisecondPrecisionAtTheLimits() {
		List<Date> dates = Arrays.asList(
				Date.from(Instant.parse("1970-01-01T00:00:00.000Z")),  // época
				Date.from(Instant.parse("1969-12-31T23:59:59.999Z")),  // 1 ms antes de la época
				Date.from(Instant.parse("2026-01-15T10:30:00.123Z")),
				Date.from(Instant.parse("9999-12-31T23:59:59.999Z")));

		for (int i = 0; i < dates.size(); i++) {
			Account account = minimal("date-" + i);
			account.setLastSeen(dates.get(i));
			repository.save(account);

			assertThat(rawById("date-" + i).get("lastSeen")).isEqualTo(dates.get(i));
			assertThat(repository.findByName("date-" + i).getLastSeen()).isEqualTo(dates.get(i));
		}
	}

	@Test
	@TestCaseId("DB-M-06")
	public void nullFieldsAreNotWritten() {
		repository.save(minimal("bob"));

		assertThat(rawById("bob").keySet()).containsExactlyInAnyOrder("_id", "saving", "_class");
		Account read = repository.findByName("bob");
		assertThat(read.getIncomes()).isNull();
		assertThat(read.getExpenses()).isNull();
		assertThat(read.getNote()).isNull();
		assertThat(read.getLastSeen()).isNull();
	}

	@Test
	@TestCaseId("DB-M-07")
	public void listsKeepCardinalityAndOrder() {
		Account empty = minimal("list-0");
		empty.setIncomes(Collections.emptyList());
		Account one = minimal("list-1");
		one.setIncomes(Collections.singletonList(item("Salary", "1", Currency.USD, TimePeriod.MONTH, "wallet")));
		Account many = minimal("list-100");
		List<Item> hundred = new ArrayList<>();
		for (int i = 0; i < 100; i++) {
			hundred.add(item(String.format("item-%03d", i), String.valueOf(i), Currency.EUR, TimePeriod.DAY, "x"));
		}
		many.setIncomes(hundred);

		repository.saveAll(Arrays.asList(empty, one, many));

		assertThat((List<?>) rawById("list-0").get("incomes")).as("lista vacía se guarda como []").isEmpty();
		assertThat(repository.findByName("list-0").getIncomes()).isNotNull().isEmpty();
		assertThat(repository.findByName("list-1").getIncomes()).hasSize(1);
		List<Item> read = repository.findByName("list-100").getIncomes();
		assertThat(read).hasSize(100);
		for (int i = 0; i < 100; i++) {
			assertThat(read.get(i).getTitle()).isEqualTo(String.format("item-%03d", i));
		}
	}

	@Test
	@TestCaseId("DB-M-08")
	public void noteLengthIsNotConstrainedByTheDataTier() {
		int[] lengths = {0, 20_000, 20_001};

		for (int length : lengths) {
			Account account = minimal("note-" + length);
			account.setNote(repeat('n', length));
			repository.save(account);

			assertThat(rawById("note-" + length)).as("la clave note existe aunque esté vacía").containsKey("note");
			assertThat(repository.findByName("note-" + length).getNote()).hasSize(length);
		}
	}

	@Test
	@TestCaseId("DB-M-09")
	public void numbersWrittenByOtherClientsAreReadAsBigDecimal() {
		raw().insertOne(new Document("_id", "legacy")
				.append("saving", new Document("amount", 1300)               // Int32
						.append("currency", "USD")
						.append("interest", 3.32)                            // Double
						.append("deposit", true)
						.append("capitalization", false))
				.append("expenses", Arrays.asList(
						new Document("title", "Gas").append("amount", 5_000_000_000L)   // Int64
								.append("currency", "EUR").append("period", "MONTH").append("icon", "gas"),
						new Document("title", "Tea").append("amount", "10.50")          // String
								.append("currency", "USD").append("period", "DAY").append("icon", "meal"))));

		Account read = repository.findByName("legacy");

		assertThat(read.getSaving().getAmount()).isEqualByComparingTo("1300");
		assertThat(read.getSaving().getInterest()).as("Double 3.32 sin error binario").isEqualTo(new BigDecimal("3.32"));
		assertThat(read.getExpenses().get(0).getAmount()).isEqualByComparingTo("5000000000");
		assertThat(read.getExpenses().get(1).getAmount()).isEqualTo(new BigDecimal("10.50"));
	}

	@Test
	@TestCaseId("DB-M-10")
	public void unknownEnumValueInStoredDocumentFailsOnRead() {
		raw().insertOne(new Document("_id", "bad-currency")
				.append("saving", new Document("amount", "1").append("currency", "GBP")
						.append("interest", "0").append("deposit", false).append("capitalization", false)));
		raw().insertOne(new Document("_id", "bad-period")
				.append("expenses", Collections.singletonList(new Document("title", "Rent").append("amount", "1")
						.append("currency", "USD").append("period", "WEEK").append("icon", "home"))));

		assertThatThrownBy(() -> repository.findByName("bad-currency"))
				.isInstanceOf(IllegalArgumentException.class).hasMessageContaining("GBP");
		assertThatThrownBy(() -> repository.findByName("bad-period"))
				.isInstanceOf(IllegalArgumentException.class).hasMessageContaining("WEEK");
	}

	@Test
	@TestCaseId("DB-M-11")
	public void documentWithoutTypeHintAndWithUnknownFieldsIsRead() {
		raw().insertOne(new Document("_id", "foreign")
				.append("note", "escrito por otro cliente")
				.append("legacyField", 42));

		Account read = repository.findByName("foreign");

		assertThat(read.getName()).isEqualTo("foreign");
		assertThat(read.getNote()).isEqualTo("escrito por otro cliente");
		assertThat(new HashSet<>(rawById("foreign").keySet())).doesNotContain("_class");
	}

	@Test
	@TestCaseId("DB-M-12")
	public void nonNumericAmountInStoredDocumentFailsOnRead() {
		raw().insertOne(new Document("_id", "bad-amount")
				.append("saving", new Document("amount", "abc").append("currency", "USD")
						.append("interest", "0").append("deposit", false).append("capitalization", false)));

		assertThatThrownBy(() -> repository.findByName("bad-amount"))
				.isInstanceOf(RuntimeException.class)
				.satisfies(e -> assertThat(rootCause(e)).isInstanceOf(NumberFormatException.class));
	}

	private static Throwable rootCause(Throwable t) {
		Throwable current = t;
		while (current.getCause() != null && current.getCause() != current) {
			current = current.getCause();
		}
		return current;
	}
}
