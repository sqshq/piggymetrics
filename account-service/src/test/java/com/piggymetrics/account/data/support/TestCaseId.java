package com.piggymetrics.account.data.support;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Identificador del caso de prueba documentado en docs/pruebas-capa-datos.md (p. ej. "DB-Q-04").
 * {@link VerdictListener} lo usa para reportar un veredicto por caso.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TestCaseId {
	String value();
}
