package es.caib.ripea.service.helper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests unitaris de EmailHelper.normalitzarAdreces: els destinataris es separen, es netegen i es validen abans
 * d'enviar el correu, perquè una adreça incorrecta no impedeixi l'enviament a la resta.
 */
class EmailHelperAdrecesTest {

	@Test
	void separaLesAdrecesPerComaIPuntIComaITreuElsEspais() {
		List<String> incorrectes = new ArrayList<>();

		List<String> adreces = EmailHelper.normalitzarAdreces(
				Arrays.asList("dmoreno@limit.es, daniel.morenolinares@gmail.com", " a@b.com ;c@d.org;"),
				incorrectes);

		assertThat(adreces).containsExactly("dmoreno@limit.es", "daniel.morenolinares@gmail.com", "a@b.com", "c@d.org");
		assertThat(incorrectes).isEmpty();
	}

	@Test
	void descartaLesAdrecesAmbFormatIncorrecteILesRepetides() {
		List<String> incorrectes = new ArrayList<>();

		List<String> adreces = EmailHelper.normalitzarAdreces(
				Arrays.asList("bona@domini.es", "sense-arroba.es", "nom@domini", "nom@@domini.es", "nom amb espai@domini.es",
						"bona@domini.es", null, "  "),
				incorrectes);

		assertThat(adreces).containsExactly("bona@domini.es");
		assertThat(incorrectes).containsExactly("sense-arroba.es", "nom@domini", "nom@@domini.es", "nom amb espai@domini.es");
	}

	@Test
	void admetCaractersHabitualsDeLaPartLocalISubdominis() {
		List<String> incorrectes = new ArrayList<>();

		List<String> adreces = EmailHelper.normalitzarAdreces(
				Arrays.asList("mmestre.temp@caib.es", "nom+etiqueta@ibdigital.caib.es", "o'neill_1@sub-domini.gov.uk"),
				incorrectes);

		assertThat(adreces).containsExactly("mmestre.temp@caib.es", "nom+etiqueta@ibdigital.caib.es", "o'neill_1@sub-domini.gov.uk");
		assertThat(incorrectes).isEmpty();
	}

	@Test
	void ambLlistaNullNoFalla() {
		assertThat(EmailHelper.normalitzarAdreces(null, new ArrayList<>())).isEmpty();
	}
}
