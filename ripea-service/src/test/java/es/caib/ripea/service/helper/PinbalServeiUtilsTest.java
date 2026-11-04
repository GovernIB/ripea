package es.caib.ripea.service.helper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import es.caib.pinbal.client.recobriment.v2.Entitat;
import es.caib.pinbal.client.recobriment.v2.Titular.DocumentTipus;
import es.caib.pinbal.client.serveis.ServeiBasic;
import es.caib.ripea.service.intf.dto.PinbalServeiDocPermesEnumDto;

/**
 * Alta de serveis de PINBAL: quines entitats i serveis de PINBAL s'ofereixen i com es converteixen les seves dades
 * als camps del servei a RIPEA.
 */
class PinbalServeiUtilsTest {

	@Test
	void trobarEntitatPerCif_comparaSenseMajusculesNiEspais() {
		List<Entitat> entitats = Arrays.asList(
				entitat("GOVERN", "Govern de les Illes Balears", "S0711001H"),
				entitat("CONSELL", "Consell Insular", " q0700528c "));

		assertThat(PinbalServeiUtils.trobarEntitatPerCif(entitats, "s0711001h ").getCodi()).isEqualTo("GOVERN");
		assertThat(PinbalServeiUtils.trobarEntitatPerCif(entitats, "Q0700528C").getCodi()).isEqualTo("CONSELL");
	}

	@Test
	void trobarEntitatPerCif_senseCoincidenciaOAmbValorsNul_tornaNull() {
		List<Entitat> entitats = Collections.singletonList(entitat("GOVERN", "Govern", "S0711001H"));

		assertThat(PinbalServeiUtils.trobarEntitatPerCif(entitats, "Q0000000J")).isNull();
		assertThat(PinbalServeiUtils.trobarEntitatPerCif(entitats, null)).isNull();
		assertThat(PinbalServeiUtils.trobarEntitatPerCif(entitats, " ")).isNull();
		assertThat(PinbalServeiUtils.trobarEntitatPerCif(null, "S0711001H")).isNull();
	}

	@Test
	void filtrarServeisNous_nomesActiusIQueNoSiguinARipea_ordenatsPerCodi() {
		List<ServeiBasic> serveis = Arrays.asList(
				servei("SVDDGPVIWS02", Boolean.TRUE, Boolean.FALSE),
				servei("SVDDGPCIWS02", Boolean.TRUE, Boolean.TRUE),
				servei("CDISFWS01", Boolean.FALSE, Boolean.FALSE),
				servei("SCDCPAJU", Boolean.TRUE, Boolean.TRUE),
				servei("NIVRENTI", null, Boolean.FALSE),
				servei(null, Boolean.TRUE, Boolean.FALSE));

		List<ServeiBasic> resultat = PinbalServeiUtils.filtrarServeisNous(
				serveis,
				Collections.singletonList(" scdcpaju "));

		assertThat(resultat).extracting(ServeiBasic::getCodi).containsExactly("SVDDGPCIWS02", "SVDDGPVIWS02");
	}

	@Test
	void filtrarServeisNous_senseCodisDeRipea_tornaTotsElsActius() {
		List<ServeiBasic> resultat = PinbalServeiUtils.filtrarServeisNous(
				Arrays.asList(servei("SCDCPAJU", Boolean.TRUE, Boolean.FALSE), servei("CDISFWS01", Boolean.FALSE, Boolean.FALSE)),
				null);

		assertThat(resultat).extracting(ServeiBasic::getCodi).containsExactly("SCDCPAJU");
	}

	@Test
	void toDocsPermesos_converteixElsDeRipeaIIgnoraLaResta() {
		List<PinbalServeiDocPermesEnumDto> resultat = PinbalServeiUtils.toDocsPermesos(Arrays.asList(
				DocumentTipus.DNI,
				DocumentTipus.NIF,
				DocumentTipus.CIF,
				DocumentTipus.NIE,
				DocumentTipus.Pasaporte,
				DocumentTipus.CSV,
				DocumentTipus.NumeroIdentificacion,
				DocumentTipus.Otros,
				DocumentTipus.DNI,
				null));

		assertThat(resultat).containsExactly(
				PinbalServeiDocPermesEnumDto.DNI,
				PinbalServeiDocPermesEnumDto.NIF,
				PinbalServeiDocPermesEnumDto.CIF,
				PinbalServeiDocPermesEnumDto.NIE,
				PinbalServeiDocPermesEnumDto.PASSAPORT);
	}

	@Test
	void toDocsPermesos_senseTipus_tornaBuida() {
		assertThat(PinbalServeiUtils.toDocsPermesos(null)).isEmpty();
		assertThat(PinbalServeiUtils.toDocsPermesos(Collections.emptyList())).isEmpty();
	}

	@Test
	void retallarNom_retallaALaLongitudDeLaColumna() {
		String llarg = Collections.nCopies(PinbalServeiUtils.NOM_MAX_LENGTH + 10, "a").stream().collect(Collectors.joining());

		assertThat(PinbalServeiUtils.retallarNom(llarg)).hasSize(PinbalServeiUtils.NOM_MAX_LENGTH);
		assertThat(PinbalServeiUtils.retallarNom("  Consulta de datos de identidad  ")).isEqualTo("Consulta de datos de identidad");
		assertThat(PinbalServeiUtils.retallarNom(null)).isNull();
	}

	private static Entitat entitat(String codi, String nom, String cif) {
		Entitat entitat = new Entitat();
		entitat.setCodi(codi);
		entitat.setNom(nom);
		entitat.setCif(cif);
		return entitat;
	}

	private static ServeiBasic servei(String codi, Boolean actiu, Boolean permis) {
		ServeiBasic servei = new ServeiBasic();
		servei.setCodi(codi);
		servei.setDescripcio("Servei " + codi);
		servei.setActiu(actiu);
		servei.setPermis(permis);
		return servei;
	}

}
