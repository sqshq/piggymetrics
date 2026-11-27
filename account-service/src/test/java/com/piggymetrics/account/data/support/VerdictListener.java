package com.piggymetrics.account.data.support;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Imprime un veredicto (PASS / FAIL / SKIP) por cada prueba, con el ID de su caso de prueba,
 * y lo agrega a target/data-tier-verdicts.txt. Se registra en el perfil Maven "data-tier".
 */
public class VerdictListener extends RunListener {

	private static final Path OUTPUT = Paths.get("target", "data-tier-verdicts.txt");

	private final Map<Description, String> outcome = Collections.synchronizedMap(new HashMap<>());

	@Override
	public void testFailure(Failure failure) {
		outcome.put(failure.getDescription(), "FAIL");
	}

	@Override
	public void testAssumptionFailure(Failure failure) {
		outcome.put(failure.getDescription(), "SKIP");
	}

	@Override
	public void testIgnored(Description description) {
		record(description, "SKIP");
	}

	@Override
	public void testFinished(Description description) {
		record(description, outcome.getOrDefault(description, "PASS"));
	}

	@Override
	public void testRunFinished(Result result) {
		System.out.println(String.format("[VEREDICTO] total=%d fallidas=%d ignoradas=%d",
				result.getRunCount(), result.getFailureCount(), result.getIgnoreCount()));
	}

	private void record(Description description, String verdict) {
		if (description.getMethodName() == null) {
			return; // descripción de clase, no de prueba
		}
		TestCaseId id = description.getAnnotation(TestCaseId.class);
		String testClass = description.getTestClass() != null
				? description.getTestClass().getSimpleName()
				: description.getClassName();
		String line = String.format("%-4s  %-9s  %s.%s", verdict, id != null ? id.value() : "-",
				testClass, description.getMethodName());
		System.out.println("[VEREDICTO] " + line);
		try {
			Files.createDirectories(OUTPUT.getParent());
			Files.write(OUTPUT, (line + System.lineSeparator()).getBytes(StandardCharsets.UTF_8),
					StandardOpenOption.CREATE, StandardOpenOption.APPEND);
		} catch (IOException e) {
			System.err.println("No se pudo escribir " + OUTPUT + ": " + e.getMessage());
		}
	}
}
