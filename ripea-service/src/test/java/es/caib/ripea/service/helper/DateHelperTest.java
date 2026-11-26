package es.caib.ripea.service.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Calendar;
import java.util.Date;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests unitaris per a DateHelper.dataLimitConservacio: la data límit és la de fa N dies naturals i un període inferior
 * a 1 dia es rebutja (s'esborraria tot).
 */
class DateHelperTest {

	@Test
	void dataLimitConservacioEsLaDeFaElsDiesIndicats() {
		Date abans = faDies(30);
		Date dataLimit = DateHelper.dataLimitConservacio(30);
		Date despres = faDies(30);

		assertThat(dataLimit).isBetween(abans, despres, true, true);
	}

	@ParameterizedTest
	@ValueSource(ints = {0, -1})
	void dataLimitConservacioSenseDiesEsRebutja(int dies) {
		assertThatThrownBy(() -> DateHelper.dataLimitConservacio(dies)).isInstanceOf(IllegalArgumentException.class);
	}

	private static Date faDies(int dies) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, -dies);
		return cal.getTime();
	}

}
