package es.caib.ripea.service.resourcehelper.inici;

import static es.caib.ripea.service.resourcehelper.inici.IndicadorInici.*;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import es.caib.ripea.service.intf.config.BaseConfig;

/**
 * Catàleg de targetes de la pantalla d'inici per rol (issue #1510, versió bàsica). Les targetes que porten a una
 * pantalla a la qual el rol no té accés no hi poden ser.
 */
class IndicadorIniciTest {

	@Test
	void usuari() {
		assertThat(IndicadorInici.targetes(BaseConfig.ROLE_USER, true)).containsExactly(
				EXPEDIENTS_AGAFATS, EXPEDIENTS_ANTICS, ALERTES_NO_LLEGIDES, ANOTACIONS_PENDENTS,
				TASQUES_EN_CURS, TASQUES_PENDENTS, TASQUES_VENCIMENT_PROPER, TASQUES_FORA_TERMINI);
	}

	@Test
	void adminOrganSenseTasques() {
		assertThat(IndicadorInici.targetes(BaseConfig.ROLE_ORGAN_ADMIN, true)).containsExactly(
				EXPEDIENTS_AGAFATS, EXPEDIENTS_ANTICS, ALERTES_NO_LLEGIDES, ANOTACIONS_PENDENTS);
	}

	@Test
	void admin() {
		assertThat(IndicadorInici.targetes(BaseConfig.ROLE_ADMIN, true)).containsExactly(
				EXPEDIENTS_OBERTS, EXPEDIENTS_TANCATS_RECENTS, ANOTACIONS_PENDENTS, TASQUES_SENSE_ACTIVITAT);
	}

	@Test
	void adminLecturaNomesExpedients() {
		assertThat(IndicadorInici.targetes(BaseConfig.ROLE_ADMIN_LECTURA, true)).containsExactly(
				EXPEDIENTS_OBERTS, EXPEDIENTS_TANCATS_RECENTS);
	}

	@Test
	void superadmin() {
		assertThat(IndicadorInici.targetes(BaseConfig.ROLE_SUPER, true)).containsExactly(
				INTEGRACIONS_ERROR, EXCEPCIONS, MEMORIA_LLIURE, DISC_LLIURE);
	}

	@Test
	void revisorIDissenyadorAmbRevisioActiva() {
		assertThat(IndicadorInici.targetes(BaseConfig.ROLE_REVISIO, true)).containsExactly(
				PROCEDIMENTS_PENDENTS_REVISIO, PROCEDIMENTS_EN_DISSENY, PROCEDIMENTS_REBUTJATS, PROCEDIMENTS_REVISATS);
		assertThat(IndicadorInici.targetes(BaseConfig.ROLE_DISSENY, true)).containsExactly(
				PROCEDIMENTS_PENDENTS_REVISIO, PROCEDIMENTS_EN_DISSENY, PROCEDIMENTS_REBUTJATS, PROCEDIMENTS_REVISATS);
	}

	@Test
	void revisorIDissenyadorSenseRevisioNoTenenTargetes() {
		assertThat(IndicadorInici.targetes(BaseConfig.ROLE_REVISIO, false)).isEmpty();
		assertThat(IndicadorInici.targetes(BaseConfig.ROLE_DISSENY, false)).isEmpty();
	}

	@Test
	void rolDesconegutONul() {
		assertThat(IndicadorInici.targetes("IPA_DESCONEGUT", true)).isEmpty();
		assertThat(IndicadorInici.targetes(null, true)).isEmpty();
	}

}
