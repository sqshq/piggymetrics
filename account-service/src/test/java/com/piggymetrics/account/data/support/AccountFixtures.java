package com.piggymetrics.account.data.support;

import com.piggymetrics.account.domain.Account;
import com.piggymetrics.account.domain.Currency;
import com.piggymetrics.account.domain.Item;
import com.piggymetrics.account.domain.Saving;
import com.piggymetrics.account.domain.TimePeriod;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Datos de prueba deterministas. Ninguna fecha sale del reloj del sistema: todas son instantes fijos.
 */
public final class AccountFixtures {

	public static final Date FIXED_LAST_SEEN = Date.from(Instant.parse("2026-01-15T10:30:00.123Z"));

	private AccountFixtures() {
	}

	public static Saving saving() {
		Saving saving = new Saving();
		saving.setAmount(new BigDecimal("1500"));
		saving.setCurrency(Currency.USD);
		saving.setInterest(new BigDecimal("3.32"));
		saving.setDeposit(true);
		saving.setCapitalization(false);
		return saving;
	}

	public static Item item(String title, String amount, Currency currency, TimePeriod period, String icon) {
		Item item = new Item();
		item.setTitle(title);
		item.setAmount(new BigDecimal(amount));
		item.setCurrency(currency);
		item.setPeriod(period);
		item.setIcon(icon);
		return item;
	}

	/** Cuenta con todos los campos llenos. */
	public static Account full(String name) {
		Account account = new Account();
		account.setName(name);
		account.setNote("nota de " + name);
		account.setLastSeen(FIXED_LAST_SEEN);
		account.setSaving(saving());
		account.setExpenses(new ArrayList<>(Arrays.asList(
				item("Grocery", "10", Currency.USD, TimePeriod.DAY, "meal"),
				item("Vacation", "3400", Currency.EUR, TimePeriod.YEAR, "tourism"))));
		account.setIncomes(new ArrayList<>(Arrays.asList(
				item("Salary", "9100", Currency.USD, TimePeriod.MONTH, "wallet"))));
		return account;
	}

	/** Cuenta con solo los campos que AccountServiceImpl.create() llena (nombre y ahorro). */
	public static Account minimal(String name) {
		Account account = new Account();
		account.setName(name);
		account.setSaving(saving());
		return account;
	}

	public static String repeat(char c, int times) {
		char[] chars = new char[times];
		Arrays.fill(chars, c);
		return new String(chars);
	}

	/** Compara campo por campo; BigDecimal con equals (valor y escala). */
	public static void assertSameContent(Account expected, Account actual) {
		assertNotNull("la cuenta leída no debe ser null", actual);
		assertEquals("name", expected.getName(), actual.getName());
		assertEquals("lastSeen", expected.getLastSeen(), actual.getLastSeen());
		assertEquals("note", expected.getNote(), actual.getNote());
		assertSameItems("incomes", expected.getIncomes(), actual.getIncomes());
		assertSameItems("expenses", expected.getExpenses(), actual.getExpenses());
		assertSameSaving(expected.getSaving(), actual.getSaving());
	}

	public static void assertSameSaving(Saving expected, Saving actual) {
		if (expected == null) {
			assertNull("saving", actual);
			return;
		}
		assertNotNull("saving", actual);
		assertEquals("saving.amount", expected.getAmount(), actual.getAmount());
		assertEquals("saving.currency", expected.getCurrency(), actual.getCurrency());
		assertEquals("saving.interest", expected.getInterest(), actual.getInterest());
		assertEquals("saving.deposit", expected.getDeposit(), actual.getDeposit());
		assertEquals("saving.capitalization", expected.getCapitalization(), actual.getCapitalization());
	}

	public static void assertSameItems(String field, List<Item> expected, List<Item> actual) {
		if (expected == null) {
			assertNull(field, actual);
			return;
		}
		assertNotNull(field, actual);
		assertEquals(field + ".size", expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			Item e = expected.get(i);
			Item a = actual.get(i);
			String prefix = field + "[" + i + "].";
			assertEquals(prefix + "title", e.getTitle(), a.getTitle());
			assertEquals(prefix + "amount", e.getAmount(), a.getAmount());
			assertEquals(prefix + "currency", e.getCurrency(), a.getCurrency());
			assertEquals(prefix + "period", e.getPeriod(), a.getPeriod());
			assertEquals(prefix + "icon", e.getIcon(), a.getIcon());
		}
	}
}
