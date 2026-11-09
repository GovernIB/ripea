package es.caib.ripea.service.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.caib.ripea.persistence.entity.CarpetaEntity;
import es.caib.ripea.persistence.entity.DocumentEntity;
import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.ExpedientEntity;
import es.caib.ripea.persistence.entity.ExpedientPeticioEntity;
import es.caib.ripea.persistence.entity.MetaDocumentEntity;
import es.caib.ripea.persistence.entity.MetaExpedientEntity;
import es.caib.ripea.persistence.entity.RegistreEntity;
import es.caib.ripea.persistence.repository.DocumentRepository;
import es.caib.ripea.service.intf.dto.MetaDocumentPerDefecteEnumDto;

/**
 * Tests unitaris per a RegistreJustificantHelper.processarJustificantSegonPla: quan es recol·loca, s'esborra, no es
 * fa res o es descarrega el justificant de registre d'una anotació.
 */
@ExtendWith(MockitoExtension.class)
class RegistreJustificantHelperTest {

	private static final Long ANOTACIO_ID = 7L;
	private static final Long EXPEDIENT_ID = 100L;
	private static final Long ENTITAT_ID = 1L;
	private static final String IDENTIFICADOR = "GOIBE1/2026";
	private static final String CODI_JUSTIFICANT = MetaDocumentPerDefecteEnumDto.REGISTRE_JUSTIFICANT_ENTRADA.getCodi();

	@Mock private DocumentRepository documentRepository;
	@Mock private MetaDocumentHelper metaDocumentHelper;
	@Mock private ExpedientHelper expedientHelper;
	@Mock private ContingutHelper contingutHelper;
	@Mock private CacheHelper cacheHelper;
	@InjectMocks private RegistreJustificantHelper helper;

	private ExpedientPeticioEntity peticio;
	private RegistreEntity registre;
	private ExpedientEntity expedient;

	@BeforeEach
	void setUp() {
		peticio = mock(ExpedientPeticioEntity.class);
		registre = mock(RegistreEntity.class);
		expedient = mock(ExpedientEntity.class);
		EntitatEntity entitat = mock(EntitatEntity.class);
		lenient().when(peticio.getId()).thenReturn(ANOTACIO_ID);
		lenient().when(peticio.getRegistre()).thenReturn(registre);
		lenient().when(peticio.getExpedient()).thenReturn(expedient);
		lenient().when(peticio.getIdentificador()).thenReturn(IDENTIFICADOR);
		lenient().when(registre.getIdentificador()).thenReturn(IDENTIFICADOR);
		lenient().when(registre.getJustificantArxiuUuid()).thenReturn("uuid-justificant");
		lenient().when(expedient.getId()).thenReturn(EXPEDIENT_ID);
		lenient().when(expedient.getEntitat()).thenReturn(entitat);
		lenient().when(entitat.getId()).thenReturn(ENTITAT_ID);
	}

	@Test
	void justificantNomesEsborratNoEsDescarregaNiEsMou() throws Exception {
		justificants(document(1L, 1));

		String resultat = helper.processarJustificantSegonPla(ANOTACIO_ID, peticio);

		assertThat(resultat).contains("està esborrat");
		verify(expedientHelper, never()).findCarpetaRegistre(any(), any());
		verify(expedientHelper, never()).recolocarJustificantRegistre(any(), any(), any());
		verifyNoDescarrega();
	}

	@Test
	void diversosJustificantsNoEsMouCap() {
		justificants(document(1L, 0), document(2L, 0));

		assertThatThrownBy(() -> helper.processarJustificantSegonPla(ANOTACIO_ID, peticio))
				.hasMessageContaining("2 justificants")
				.hasMessageContaining("1, 2");
		verify(expedientHelper, never()).recolocarJustificantRegistre(any(), any(), any());
	}

	@Test
	void carpetaEsborradaEsborraElJustificant() throws Exception {
		DocumentEntity justificant = document(1L, 0);
		justificants(justificant);
		carpetaRegistre(carpeta(50L, 1));

		String resultat = helper.processarJustificantSegonPla(ANOTACIO_ID, peticio);

		assertThat(resultat).contains("s'ha esborrat també el justificant");
		verify(contingutHelper).marcarEsborrat(ENTITAT_ID, justificant, null);
		verify(expedientHelper, never()).recolocarJustificantRegistre(any(), any(), any());
		verifyNoDescarrega();
	}

	@Test
	void carpetaEsborradaSenseJustificantNoEsDescarrega() throws Exception {
		justificants();
		carpetaRegistre(carpeta(50L, 1));

		String resultat = helper.processarJustificantSegonPla(ANOTACIO_ID, peticio);

		assertThat(resultat).contains("no s'hi incorpora el justificant");
		verify(contingutHelper, never()).marcarEsborrat(anyLong(), any(), any());
		verifyNoDescarrega();
	}

	@Test
	void justificantExistentEsRecollocaIEsBuidaLError() throws Exception {
		DocumentEntity justificant = document(1L, 0);
		CarpetaEntity carpeta = carpeta(50L, 0);
		justificants(justificant);
		carpetaRegistre(carpeta);
		when(registre.getJustificantError()).thenReturn("error anterior");
		when(expedientHelper.recolocarJustificantRegistre(justificant, carpeta, peticio)).thenReturn("mogut");

		String resultat = helper.processarJustificantSegonPla(ANOTACIO_ID, peticio);

		assertThat(resultat).contains("ja està incorporat").contains("mogut");
		verify(registre).updateJustificantError(null);
		verifyNoDescarrega();
	}

	@Test
	void justificantExistentSenseCarpetaEsRecollocaPerCrearLa() throws Exception {
		DocumentEntity justificant = document(1L, 0);
		justificants(justificant);
		carpetaRegistre(null);
		when(expedientHelper.recolocarJustificantRegistre(justificant, null, peticio)).thenReturn("mogut a la nova carpeta");

		helper.processarJustificantSegonPla(ANOTACIO_ID, peticio);

		verify(expedientHelper).recolocarJustificantRegistre(justificant, null, peticio);
		verifyNoDescarrega();
	}

	@Test
	void senseJustificantEsDescarrega() throws Exception {
		justificants();
		carpetaRegistre(null);
		MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
		MetaDocumentEntity metaDocument = mock(MetaDocumentEntity.class);
		when(expedient.getArxiuUuid()).thenReturn("uuid-expedient");
		when(expedient.getMetaExpedient()).thenReturn(metaExpedient);
		when(metaDocument.getId()).thenReturn(30L);
		when(metaDocumentHelper.getOrCreateMetaDocumentPerDefecte(metaExpedient, MetaDocumentPerDefecteEnumDto.REGISTRE_JUSTIFICANT_ENTRADA))
				.thenReturn(metaDocument);

		String resultat = helper.processarJustificantSegonPla(ANOTACIO_ID, peticio);

		assertThat(resultat).contains("Incorporat el justificant");
		verify(expedientHelper).crearDocFromJustificantRegistreUuid(
				eq(EXPEDIENT_ID), eq("uuid-justificant"), eq(ANOTACIO_ID), eq(30L), anyString(), anyString());
		verify(expedientHelper, never()).recolocarJustificantRegistre(any(), any(), any());
	}

	private void justificants(DocumentEntity... documents) {
		List<DocumentEntity> llista = documents.length == 0 ? Collections.emptyList() : Arrays.asList(documents);
		when(documentRepository.findByExpedientAndMetaDocumentCodiAndNumeroRegistre(expedient, CODI_JUSTIFICANT, IDENTIFICADOR))
				.thenReturn(llista);
	}

	private void carpetaRegistre(CarpetaEntity carpeta) {
		when(expedientHelper.findCarpetaRegistre(expedient, peticio)).thenReturn(carpeta);
	}

	private void verifyNoDescarrega() {
		verify(expedientHelper, never()).crearDocFromJustificantRegistreUuid(any(), any(), any(), any(), any(), any());
	}

	private static DocumentEntity document(Long id, int esborrat) {
		DocumentEntity document = mock(DocumentEntity.class);
		lenient().when(document.getId()).thenReturn(id);
		lenient().when(document.getEsborrat()).thenReturn(esborrat);
		return document;
	}

	private static CarpetaEntity carpeta(Long id, int esborrat) {
		CarpetaEntity carpeta = mock(CarpetaEntity.class);
		lenient().when(carpeta.getId()).thenReturn(id);
		lenient().when(carpeta.getEsborrat()).thenReturn(esborrat);
		lenient().when(carpeta.getNom()).thenReturn("Registre entrada: " + IDENTIFICADOR);
		return carpeta;
	}

}
