package es.caib.ripea.service.helper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import es.caib.ripea.service.intf.dto.InteressatDocumentTipusEnumDto;
import es.caib.ripea.service.intf.dto.InteressatTipusEnumDto;
import es.caib.ripea.service.intf.dto.PinbalServeiDocPermesEnumDto;
import es.caib.ripea.service.intf.utils.PinbalTitularUtils;

/**
 * Interessats que poden ser el titular d'una consulta PINBAL segons els tipus de document que admet el servei (el
 * mateix criteri als formularis JSP i REACT i en fer la consulta).
 */
class PinbalTitularUtilsTest {

	@Test
	void senseTipusPermesos_qualsevolInteressat() {
		assertThat(PinbalTitularUtils.isTitularPermes(
				InteressatTipusEnumDto.PERSONA_FISICA, InteressatDocumentTipusEnumDto.ALTRES_DE_PERSONA_FISICA, Collections.emptyList())).isTrue();
		assertThat(PinbalTitularUtils.isTitularPermes(InteressatTipusEnumDto.ADMINISTRACIO, null, null)).isTrue();
	}

	@Test
	void personaFisicaAmbNif_siElServeiAdmetDniONif() {
		// SVDDELSEXWS01: NIF, NIE i passaport (sense DNI: PINBAL rep NIF)
		List<PinbalServeiDocPermesEnumDto> senseDni = Arrays.asList(
				PinbalServeiDocPermesEnumDto.NIF, PinbalServeiDocPermesEnumDto.NIE, PinbalServeiDocPermesEnumDto.PASSAPORT);
		assertThat(PinbalTitularUtils.isTitularPermes(
				InteressatTipusEnumDto.PERSONA_FISICA, InteressatDocumentTipusEnumDto.NIF, senseDni)).isTrue();
		assertThat(PinbalTitularUtils.isTitularPermes(
				InteressatTipusEnumDto.PERSONA_FISICA, InteressatDocumentTipusEnumDto.NIF,
				Collections.singletonList(PinbalServeiDocPermesEnumDto.DNI))).isTrue();
		// SVDDGPRESIDENCIALEGALDOCWS01: només NIE i passaport
		List<PinbalServeiDocPermesEnumDto> estrangers = Arrays.asList(
				PinbalServeiDocPermesEnumDto.NIE, PinbalServeiDocPermesEnumDto.PASSAPORT);
		assertThat(PinbalTitularUtils.isTitularPermes(
				InteressatTipusEnumDto.PERSONA_FISICA, InteressatDocumentTipusEnumDto.NIF, estrangers)).isFalse();
		assertThat(PinbalTitularUtils.isTitularPermes(
				InteressatTipusEnumDto.PERSONA_FISICA, InteressatDocumentTipusEnumDto.DOCUMENT_IDENTIFICATIU_ESTRANGERS, estrangers)).isTrue();
		assertThat(PinbalTitularUtils.isTitularPermes(
				InteressatTipusEnumDto.PERSONA_FISICA, InteressatDocumentTipusEnumDto.PASSAPORT, estrangers)).isTrue();
	}

	@Test
	void ambRestriccio_altresDocumentsIAdministracionsNoPermesos() {
		List<PinbalServeiDocPermesEnumDto> tots = Arrays.asList(PinbalServeiDocPermesEnumDto.values());
		assertThat(PinbalTitularUtils.isTitularPermes(
				InteressatTipusEnumDto.PERSONA_FISICA, InteressatDocumentTipusEnumDto.ALTRES_DE_PERSONA_FISICA, tots)).isFalse();
		assertThat(PinbalTitularUtils.isTitularPermes(InteressatTipusEnumDto.PERSONA_FISICA, null, tots)).isFalse();
		assertThat(PinbalTitularUtils.isTitularPermes(
				InteressatTipusEnumDto.ADMINISTRACIO, InteressatDocumentTipusEnumDto.CODI_ORIGEN, tots)).isFalse();
	}

	@Test
	void personaJuridica_siElServeiAdmetCifONif() {
		assertThat(PinbalTitularUtils.isTitularPermes(
				InteressatTipusEnumDto.PERSONA_JURIDICA, InteressatDocumentTipusEnumDto.NIF,
				Collections.singletonList(PinbalServeiDocPermesEnumDto.CIF))).isTrue();
		assertThat(PinbalTitularUtils.isTitularPermes(
				InteressatTipusEnumDto.PERSONA_JURIDICA, InteressatDocumentTipusEnumDto.NIF,
				Collections.singletonList(PinbalServeiDocPermesEnumDto.NIF))).isTrue();
		assertThat(PinbalTitularUtils.isTitularPermes(
				InteressatTipusEnumDto.PERSONA_JURIDICA, InteressatDocumentTipusEnumDto.NIF,
				Arrays.asList(PinbalServeiDocPermesEnumDto.DNI, PinbalServeiDocPermesEnumDto.NIE))).isFalse();
	}

	@Test
	void documentsPersonaFisicaPermesos_perAlFiltreDelFormulari() {
		assertThat(PinbalTitularUtils.documentsPersonaFisicaPermesos(Arrays.asList(
				PinbalServeiDocPermesEnumDto.DNI, PinbalServeiDocPermesEnumDto.NIF, PinbalServeiDocPermesEnumDto.CIF)))
				.containsExactly(InteressatDocumentTipusEnumDto.NIF);
		assertThat(PinbalTitularUtils.documentsPersonaFisicaPermesos(Collections.singletonList(PinbalServeiDocPermesEnumDto.CIF))).isEmpty();
		assertThat(PinbalTitularUtils.documentsPersonaFisicaPermesos(null)).isEmpty();
	}

}
