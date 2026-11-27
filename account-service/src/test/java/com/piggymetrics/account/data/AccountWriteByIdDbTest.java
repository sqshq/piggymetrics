package com.piggymetrics.account.data;

import com.piggymetrics.account.data.support.AbstractAccountDataTierTest;
import com.piggymetrics.account.data.support.TestCaseId;
import com.piggymetrics.account.domain.Account;
import com.piggymetrics.account.domain.Currency;
import com.piggymetrics.account.domain.TimePeriod;
import org.bson.BsonSerializationException;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;

import java.util.Collections;

import static com.piggymetrics.account.data.support.AccountFixtures.assertSameContent;
import static com.piggymetrics.account.data.support.AccountFixtures.full;
import static com.piggymetrics.account.data.support.AccountFixtures.item;
import static com.piggymetrics.account.data.support.AccountFixtures.minimal;
import static com.piggymetrics.account.data.support.AccountFixtures.repeat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Componente DB-3: escritura por clave primaria _id (save / delete del repositorio y la
 * restricción de unicidad de _id). Modelo de estados de una cuenta con nombre N:
 *   S0 = no existe, S1 = existe con versión v1, S2 = existe con versión v2.
 */
public class AccountWriteByIdDbTest extends AbstractAccountDataTierTest {

	private static final int MONGO_MAX_DOCUMENT_BYTES = 16 * 1024 * 1024;

	// ---------------------------------------------------------------- transiciones de estado

	@Test
	@TestCaseId("DB-W-01")
	public void saveOnAbsentNameInserts() {                         // T1: S0 --save(v1)--> S1
		Account v1 = full("alice");

		repository.save(v1);

		assertThat(count()).isEqualTo(1);
		assertSameContent(v1, repository.findByName("alice"));
	}

	@Test
	@TestCaseId("DB-W-02")
	public void saveOnExistingNameReplacesWholeDocument() {         // T2: S1 --save(v2)--> S2
		repository.save(full("alice"));
		Account v2 = minimal("alice");
		v2.setExpenses(Collections.singletonList(item("Rent", "800", Currency.EUR, TimePeriod.MONTH, "home")));

		repository.save(v2);

		assertThat(count()).isEqualTo(1);
		assertSameContent(v2, repository.findByName("alice"));
		assertThat(rawById("alice").keySet())
				.as("los campos que v2 no trae se eliminan: no hay fusión")
				.doesNotContain("note", "incomes", "lastSeen");
	}

	@Test
	@TestCaseId("DB-W-03")
	public void deleteOnExistingNameRemovesIt() {                   // T3: S1 --delete--> S0
		repository.save(full("alice"));

		repository.deleteById("alice");

		assertThat(count()).isZero();
		assertThat(repository.findByName("alice")).isNull();
		assertThat(repository.existsById("alice")).isFalse();
	}

	@Test
	@TestCaseId("DB-W-04")
	public void deleteOnAbsentNameIsANoOp() {                       // T4: S0 --delete--> S0
		repository.save(full("bob"));

		repository.deleteById("ghost");
		repository.delete(full("ghost"));

		assertThat(count()).isEqualTo(1);
		assertThat(repository.findByName("bob")).isNotNull();
	}

	@Test
	@TestCaseId("DB-W-05")
	public void oversizedSaveOnExistingNameIsRejectedAndKeepsV1() { // T5: S1 --save(>16MiB)--> S1
		Account v1 = full("alice");
		repository.save(v1);
		Account oversized = full("alice");
		oversized.setNote(repeat('x', MONGO_MAX_DOCUMENT_BYTES + 1024));

		assertThatThrownBy(() -> repository.save(oversized)).isInstanceOf(DataIntegrityViolationException.class);

		assertThat(count()).isEqualTo(1);
		assertSameContent(v1, repository.findByName("alice"));
	}

	@Test
	@TestCaseId("DB-W-06")
	public void fullLifecycleLeavesNoResidue() {                    // S0 -> S1 -> S2 -> S0 -> S1
		repository.save(full("alice"));
		assertThat(count()).isEqualTo(1);

		Account v2 = full("alice");
		v2.setNote("v2");
		repository.save(v2);
		assertThat(repository.findByName("alice").getNote()).isEqualTo("v2");

		repository.deleteById("alice");
		assertThat(repository.findByName("alice")).isNull();

		Account recreated = minimal("alice");
		repository.save(recreated);
		assertThat(count()).isEqualTo(1);
		assertSameContent(recreated, repository.findByName("alice"));
		assertThat(rawById("alice")).doesNotContainKey("note");
	}

	// ---------------------------------------------------------------- tabla de decisión de save()

	@Test
	@TestCaseId("DB-W-07")
	public void saveWithoutNameGeneratesObjectIdAsName() {          // R3: name = null, tamaño válido
		Account first = minimal(null);
		Account second = minimal(null);

		repository.save(first);
		repository.save(second);

		assertThat(first.getName()).as("se asigna un ObjectId en hexadecimal").matches("[0-9a-f]{24}");
		assertThat(ObjectId.isValid(first.getName())).isTrue();
		assertThat(second.getName()).isNotEqualTo(first.getName());
		assertThat(count()).isEqualTo(2);
		assertThat(repository.findByName(first.getName())).isNotNull();
	}

	@Test
	@TestCaseId("DB-W-08")
	public void documentSizeBoundaryIsSixteenMebibytesInclusive() { // R1 / R4 en el borde exacto
		assertThat(maxBsonObjectSize()).as("precondición: límite del servidor").isEqualTo(MONGO_MAX_DOCUMENT_BYTES);

		Account atLimit = sizedAccount("at-limit", MONGO_MAX_DOCUMENT_BYTES);
		Account overLimit = sizedAccount("over-limit", MONGO_MAX_DOCUMENT_BYTES + 1);

		repository.save(atLimit);
		assertThat(repository.findByName("at-limit").getNote()).hasSize(atLimit.getNote().length());

		assertThatThrownBy(() -> repository.save(overLimit)).isInstanceOf(DataIntegrityViolationException.class);
		assertThat(repository.existsById("over-limit")).isFalse();
		assertThat(count()).isEqualTo(1);
	}

	@Test
	@TestCaseId("DB-W-09")
	public void oversizedSaveWithoutNameFailsWithUntranslatedDriverException() { // R6
		Account oversized = minimal(null);
		oversized.setNote(repeat('x', MONGO_MAX_DOCUMENT_BYTES + 1024));

		assertThatThrownBy(() -> repository.save(oversized))
				.as("el driver rechaza el documento antes de enviarlo; Spring no traduce la excepción")
				.isInstanceOf(BsonSerializationException.class);
		assertThat(count()).isZero();
	}

	@Test
	@TestCaseId("DB-W-10")
	public void atMostOneDocumentPerNameLastWriteWins() {           // invariante de unicidad
		for (int i = 1; i <= 3; i++) {
			Account version = minimal("alice");
			version.setNote("versión " + i);
			repository.save(version);
		}

		assertThat(count()).isEqualTo(1);
		assertThat(repository.findByName("alice").getNote()).isEqualTo("versión 3");
	}

	@Test
	@TestCaseId("DB-W-11")
	public void concurrentCopiesLoseTheFirstUpdate() {              // sin bloqueo optimista
		repository.save(full("alice"));
		Account copyA = repository.findByName("alice");
		Account copyB = repository.findByName("alice");

		copyA.setNote("cambio de A");
		repository.save(copyA);
		copyB.setIncomes(Collections.emptyList());
		repository.save(copyB);

		Account stored = repository.findByName("alice");
		assertThat(stored.getIncomes()).isEmpty();
		assertThat(stored.getNote()).as("la escritura de A se pierde sin error").isEqualTo("nota de alice");
	}

	@Test
	@TestCaseId("DB-W-12")
	public void rawInsertOfDuplicateIdViolatesUniqueConstraint() {  // excepción de la restricción _id
		repository.save(full("alice"));

		assertThatThrownBy(() -> mongo.insert(full("alice")))
				.isInstanceOf(DuplicateKeyException.class);
		assertThatThrownBy(() -> raw().insertOne(new Document("_id", "alice")))
				.hasMessageContaining("E11000");
		assertThat(count()).isEqualTo(1);
	}

	/** Cuenta cuyo documento BSON mide exactamente targetBytes. */
	private Account sizedAccount(String name, int targetBytes) {
		Account account = minimal(name);
		account.setNote("");
		int base = bsonSize(account);
		account.setNote(repeat('x', targetBytes - base));
		assertThat(bsonSize(account)).as("tamaño construido").isEqualTo(targetBytes);
		return account;
	}
}
