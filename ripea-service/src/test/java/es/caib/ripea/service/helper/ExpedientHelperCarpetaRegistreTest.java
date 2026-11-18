package es.caib.ripea.service.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.caib.plugins.arxiu.api.Carpeta;
import es.caib.ripea.persistence.entity.CarpetaEntity;
import es.caib.ripea.persistence.entity.ContingutEntity;
import es.caib.ripea.persistence.entity.DocumentEntity;
import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.ExpedientEntity;
import es.caib.ripea.persistence.entity.ExpedientPeticioEntity;
import es.caib.ripea.persistence.entity.RegistreAnnexEntity;
import es.caib.ripea.persistence.entity.RegistreEntity;
import es.caib.ripea.persistence.repository.CarpetaRepository;
import es.caib.ripea.persistence.repository.ContingutRepository;
import es.caib.ripea.service.intf.dto.ContingutTipusEnumDto;
import es.caib.ripea.service.intf.exception.SistemaExternException;
import es.caib.ripea.service.intf.exception.ValidationException;

/**
 * Tests unitaris per a la carpeta del registre d'una anotació (ExpedientHelper.findCarpetaRegistre) i per a la
 * recol·locació del justificant a dins (ExpedientHelper.recolocarJustificantRegistre).
 */
@ExtendWith(MockitoExtension.class)
class ExpedientHelperCarpetaRegistreTest {

	private static final Long EXPEDIENT_ID = 100L;
	private static final String IDENTIFICADOR = "GOIBE1/2026";
	private static final String NOM_CARPETA = "Registre entrada: " + IDENTIFICADOR;

	@Mock private ContingutRepository contingutRepository;
	@Mock private CarpetaRepository carpetaRepository;
	@Mock private ContingutHelper contingutHelper;
	@Mock private PluginHelper pluginHelper;
	@InjectMocks private ExpedientHelper helper;

	private ExpedientEntity expedient;
	private ExpedientPeticioEntity peticio;
	private RegistreEntity registre;
	private final List<RegistreAnnexEntity> annexos = new ArrayList<>();

	@BeforeEach
	void setUp() {
		expedient = mock(ExpedientEntity.class);
		peticio = mock(ExpedientPeticioEntity.class);
		registre = mock(RegistreEntity.class);
		EntitatEntity entitat = mock(EntitatEntity.class);
		lenient().when(expedient.getId()).thenReturn(EXPEDIENT_ID);
		lenient().when(expedient.getEntitat()).thenReturn(entitat);
		lenient().when(expedient.getArxiuUuid()).thenReturn("uuid-expedient");
		lenient().when(entitat.getId()).thenReturn(1L);
		lenient().when(peticio.getRegistre()).thenReturn(registre);
		lenient().when(peticio.getIdentificador()).thenReturn(IDENTIFICADOR);
		lenient().when(registre.getIdentificador()).thenReturn(IDENTIFICADOR);
		lenient().when(registre.getAnnexos()).thenReturn(annexos);
	}

	// ---------------------------------------------------------------- findCarpetaRegistre

	@Test
	void carpetaAmbElNomDelRegistre() {
		CarpetaEntity carpeta = carpeta(10L, 0);
		carpetesAmbNom(carpeta);

		assertThat(helper.findCarpetaRegistre(expedient, peticio)).isSameAs(carpeta);
	}

	@Test
	void ambNomRepetitEsQuedaLaQueTeElsAnnexos() {
		CarpetaEntity primera = carpeta(10L, 0);
		CarpetaEntity ambAnnexos = carpeta(11L, 0);
		carpetesAmbNom(primera, ambAnnexos);
		annex(ambAnnexos, expedient);

		assertThat(helper.findCarpetaRegistre(expedient, peticio)).isSameAs(ambAnnexos);
	}

	@Test
	void ambNomRepetitISenseAnnexosEsQuedaLaDIdMenor() {
		CarpetaEntity primera = carpeta(10L, 0);
		carpetesAmbNom(carpeta(11L, 0), primera);

		assertThat(helper.findCarpetaRegistre(expedient, peticio)).isSameAs(primera);
	}

	@Test
	void senseNomEsQuedaLaCarpetaAmbMesAnnexos() {
		CarpetaEntity ambDos = carpeta(20L, 0);
		CarpetaEntity ambUn = carpeta(21L, 0);
		carpetesAmbNom();
		annex(ambDos, expedient);
		annex(ambDos, expedient);
		annex(ambUn, expedient);
		when(carpetaRepository.findAllById(any())).thenReturn(Arrays.asList(ambUn, ambDos));

		assertThat(helper.findCarpetaRegistre(expedient, peticio)).isSameAs(ambDos);
	}

	@Test
	void laCarpetaDelsAnnexosPotEstarEsborrada() {
		CarpetaEntity esborrada = carpeta(20L, 1);
		carpetesAmbNom();
		annex(esborrada, expedient);
		when(carpetaRepository.findAllById(any())).thenReturn(Collections.singletonList(esborrada));

		assertThat(helper.findCarpetaRegistre(expedient, peticio)).isSameAs(esborrada);
	}

	@Test
	void elsAnnexosDeLArrelIDAltresExpedientsNoCompten() {
		carpetesAmbNom();
		annex(expedient, expedient);
		ExpedientEntity altreExpedient = mock(ExpedientEntity.class);
		lenient().when(altreExpedient.getId()).thenReturn(200L);
		annex(carpeta(30L, 0), altreExpedient);

		assertThat(helper.findCarpetaRegistre(expedient, peticio)).isNull();
		verify(carpetaRepository, never()).findAllById(any());
	}

	@Test
	void senseAnnexosEsQuedaLaCarpetaEsborradaAmbElNom() {
		CarpetaEntity esborrada = carpeta(10L, 1);
		carpetesAmbNom(esborrada);

		assertThat(helper.findCarpetaRegistre(expedient, peticio)).isSameAs(esborrada);
	}

	// ---------------------------------------------------------------- recolocarJustificantRegistre

	@Test
	void justificantJaALaCarpetaNoEsMou() {
		CarpetaEntity carpeta = carpeta(10L, 0);
		DocumentEntity justificant = justificant(carpeta, true);

		String resultat = helper.recolocarJustificantRegistre(justificant, carpeta, peticio);

		assertThat(resultat).contains("ja és a la carpeta");
		verify(contingutHelper, never()).moureBaseDades(any(), any());
	}

	@Test
	void justificantAmbErrorEnMoureNomesEsMouABaseDades() {
		CarpetaEntity carpeta = carpeta(10L, 0);
		DocumentEntity justificant = justificant(expedient, true);

		String resultat = helper.recolocarJustificantRegistre(justificant, carpeta, peticio);

		assertThat(resultat).contains("l'arrel de l'expedient").contains("només a RIPEA");
		verify(contingutHelper).moureBaseDades(justificant, carpeta);
		verify(contingutHelper, never()).arxiuDocumentPropagarMoviment(any(), any(), any());
	}

	@Test
	void justificantSenseErrorEsMouTambeALArxiu() {
		CarpetaEntity carpeta = carpeta(10L, 0);
		when(carpeta.getArxiuUuid()).thenReturn("uuid-carpeta");
		when(pluginHelper.arxiuCarpetaConsultar(carpeta)).thenReturn(new Carpeta());
		DocumentEntity justificant = justificant(expedient, false);
		when(contingutHelper.arxiuDocumentPropagarMoviment("uuid-justificant", carpeta, "uuid-expedient")).thenReturn("uuid-copia");

		String resultat = helper.recolocarJustificantRegistre(justificant, carpeta, peticio);

		assertThat(resultat).contains("també a l'Arxiu");
		verify(justificant).updateArxiu("uuid-copia");
		verify(contingutHelper).moureBaseDades(justificant, carpeta);
	}

	@Test
	void siFallaLArxiuNoEsMouABaseDades() {
		CarpetaEntity carpeta = carpeta(10L, 0);
		when(carpeta.getArxiuUuid()).thenReturn("uuid-carpeta");
		when(pluginHelper.arxiuCarpetaConsultar(carpeta)).thenReturn(new Carpeta());
		DocumentEntity justificant = justificant(expedient, false);
		when(contingutHelper.arxiuDocumentPropagarMoviment("uuid-justificant", carpeta, "uuid-expedient"))
				.thenThrow(new SistemaExternException("ARXIU", "permission of Write"));

		assertThatThrownBy(() -> helper.recolocarJustificantRegistre(justificant, carpeta, peticio))
				.isInstanceOf(SistemaExternException.class);
		verify(contingutHelper, never()).moureBaseDades(any(), any());
	}

	@Test
	void ambUnAltreContingutAmbElMateixNomNoEsMou() {
		CarpetaEntity carpeta = carpeta(10L, 0);
		DocumentEntity justificant = justificant(expedient, true);
		when(contingutRepository.findByPareAndNomAndEsborrat(carpeta, "Justificant del registre " + IDENTIFICADOR, 0))
				.thenReturn(mock(DocumentEntity.class));

		assertThatThrownBy(() -> helper.recolocarJustificantRegistre(justificant, carpeta, peticio))
				.isInstanceOf(ValidationException.class);
		verify(contingutHelper, never()).moureBaseDades(any(), any());
	}

	// ---------------------------------------------------------------- utilitats

	private void carpetesAmbNom(CarpetaEntity... carpetes) {
		List<ContingutEntity> llista = new ArrayList<>(Arrays.asList(carpetes));
		when(contingutRepository.findByPareAndNomOrderByEsborratAsc(expedient, NOM_CARPETA)).thenReturn(llista);
	}

	/** Annex de l'anotació importat com a document dins el pare indicat. */
	private void annex(ContingutEntity pare, ExpedientEntity expedientDocument) {
		DocumentEntity document = mock(DocumentEntity.class);
		lenient().when(document.getPare()).thenReturn(pare);
		lenient().when(document.getExpedient()).thenReturn(expedientDocument);
		RegistreAnnexEntity annex = mock(RegistreAnnexEntity.class);
		lenient().when(annex.getDocument()).thenReturn(document);
		annexos.add(annex);
	}

	private CarpetaEntity carpeta(Long id, int esborrat) {
		CarpetaEntity carpeta = mock(CarpetaEntity.class);
		lenient().when(carpeta.getId()).thenReturn(id);
		lenient().when(carpeta.getEsborrat()).thenReturn(esborrat);
		lenient().when(carpeta.getTipus()).thenReturn(ContingutTipusEnumDto.CARPETA);
		lenient().when(carpeta.getNom()).thenReturn(NOM_CARPETA);
		lenient().when(carpeta.getNumeroRegistre()).thenReturn(IDENTIFICADOR);
		return carpeta;
	}

	private DocumentEntity justificant(ContingutEntity pare, boolean justificantMoureError) {
		DocumentEntity justificant = mock(DocumentEntity.class);
		lenient().when(justificant.getId()).thenReturn(1L);
		lenient().when(justificant.getNom()).thenReturn("Justificant del registre " + IDENTIFICADOR);
		lenient().when(justificant.getExpedient()).thenReturn(expedient);
		lenient().when(justificant.getPare()).thenReturn(pare);
		lenient().when(justificant.getArxiuUuid()).thenReturn("uuid-justificant");
		lenient().when(justificant.isJustificantMoureError()).thenReturn(justificantMoureError);
		return justificant;
	}

}
