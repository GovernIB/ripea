package es.caib.ripea.service.resourcehelper.inici;

import static es.caib.ripea.service.resourcehelper.inici.LlistaInici.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.junit.jupiter.api.Test;

import es.caib.ripea.persistence.entity.resourceentity.ExpedientResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.ExpedientTascaResourceEntity;
import es.caib.ripea.service.intf.config.BaseConfig;

/**
 * Catàleg de llistes resum de la pantalla d'inici per rol (issue #1510, versió bàsica) i marques de data límit de
 * les files de tasques. Les llistes que porten a una pantalla a la qual el rol no té accés no hi poden ser.
 */
class LlistaIniciTest {

	@Test
	void usuari() {
		assertThat(LlistaInici.llistes(BaseConfig.ROLE_USER, true)).containsExactly(
				EXPEDIENTS_LLESTOS_TANCAR, TASQUES_PER_DATA_LIMIT);
	}

	@Test
	void adminOrganSenseTasques() {
		assertThat(LlistaInici.llistes(BaseConfig.ROLE_ORGAN_ADMIN, true)).containsExactly(EXPEDIENTS_LLESTOS_TANCAR);
	}

	@Test
	void admin() {
		assertThat(LlistaInici.llistes(BaseConfig.ROLE_ADMIN, true)).containsExactly(
				EXPEDIENTS_LLESTOS_TANCAR, TASQUES_SENSE_ACTIVITAT);
	}

	@Test
	void adminLecturaSenseLlistes() {
		assertThat(LlistaInici.llistes(BaseConfig.ROLE_ADMIN_LECTURA, true)).isEmpty();
	}

	@Test
	void superadmin() {
		assertThat(LlistaInici.llistes(BaseConfig.ROLE_SUPER, true)).containsExactly(
				DARRERES_INTEGRACIONS, DARRERES_EXCEPCIONS);
	}

	@Test
	void revisorIDissenyadorAmbRevisioActiva() {
		assertThat(LlistaInici.llistes(BaseConfig.ROLE_REVISIO, true)).containsExactly(
				DARRERS_COMENTARIS, PROCEDIMENTS_PENDENTS_REVISIO);
		assertThat(LlistaInici.llistes(BaseConfig.ROLE_DISSENY, true)).containsExactly(
				DARRERS_COMENTARIS, PROCEDIMENTS_REBUTJATS);
	}

	@Test
	void revisorIDissenyadorSenseRevisioNomesComentaris() {
		assertThat(LlistaInici.llistes(BaseConfig.ROLE_REVISIO, false)).containsExactly(DARRERS_COMENTARIS);
		assertThat(LlistaInici.llistes(BaseConfig.ROLE_DISSENY, false)).containsExactly(DARRERS_COMENTARIS);
	}

	@Test
	void senseRol() {
		assertThat(LlistaInici.llistes(null, true)).isEmpty();
	}

	@Test
	void tascaVencudaAvui() {
		// La data límit és un dia sense hora: la d'avui ja és fora de termini (com al llistat de tasques)
		Map<String, Object> fila = FilesLlistesInici.tascaPerDataLimit(tasca(dia(0)), dia(1), dia(4));
		assertThat(fila).containsEntry("foraTermini", true).containsEntry("vencimentProper", false);
	}

	@Test
	void tascaDinsDelPreavis() {
		Map<String, Object> fila = FilesLlistesInici.tascaPerDataLimit(tasca(dia(3)), dia(1), dia(4));
		assertThat(fila).containsEntry("foraTermini", false).containsEntry("vencimentProper", true);
	}

	@Test
	void tascaForaDelPreavis() {
		Map<String, Object> fila = FilesLlistesInici.tascaPerDataLimit(tasca(dia(4)), dia(1), dia(4));
		assertThat(fila).containsEntry("foraTermini", false).containsEntry("vencimentProper", false);
	}

	@Test
	void tascaSenseDataLimit() {
		Map<String, Object> fila = FilesLlistesInici.tascaPerDataLimit(tasca(null), dia(1), dia(4));
		assertThat(fila).containsEntry("foraTermini", false).containsEntry("vencimentProper", false);
	}

	@Test
	void tascaSenseActivitatAmbResponsables() {
		LocalDateTime activitat = LocalDateTime.now().minusDays(10);
		Map<String, Object> fila = FilesLlistesInici.tascaSenseActivitat(tasca(null), activitat);
		assertThat(fila).containsEntry("darreraActivitat", activitat).containsKey("responsables");
	}

	private static ExpedientTascaResourceEntity tasca(Date dataLimit) {
		ExpedientTascaResourceEntity tasca = new ExpedientTascaResourceEntity();
		tasca.setExpedient(new ExpedientResourceEntity());
		tasca.setTitol("Tasca de prova");
		tasca.setDataLimit(dataLimit);
		return tasca;
	}

	/** Inici del dia d'avui més els dies indicats. */
	private static Date dia(int dies) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.add(Calendar.DATE, dies);
		return calendar.getTime();
	}

}
