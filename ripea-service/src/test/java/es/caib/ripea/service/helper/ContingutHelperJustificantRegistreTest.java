package es.caib.ripea.service.helper;

import static es.caib.ripea.service.intf.utils.RegistreJustificantUtils.isJustificantRegistre;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.LinkedHashSet;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.caib.ripea.persistence.entity.CarpetaEntity;
import es.caib.ripea.persistence.entity.ContingutEntity;
import es.caib.ripea.persistence.entity.DocumentEntity;
import es.caib.ripea.persistence.repository.ContingutRepository;
import es.caib.ripea.service.intf.dto.DocumentTipusEnumDto;
import es.caib.ripea.service.intf.dto.MetaDocumentPerDefecteEnumDto;

/**
 * Tests unitaris de com es tracta el justificant de registre pendent de moure, que es queda a la sèrie de Regweb de
 * l'Arxiu (decisió de l'equip d'Arxiu, 18/11/2026): com es reconeix un justificant
 * (RegistreJustificantUtils.isJustificantRegistre), quan un contingut en conté (ContingutHelper.conteJustificantsRegistre)
 * i que ni esborrar-lo ni moure una carpeta lògica toquen el seu node de l'Arxiu.
 */
@ExtendWith(MockitoExtension.class)
class ContingutHelperJustificantRegistreTest {

	private static final String TIPUS_JUSTIFICANT = MetaDocumentPerDefecteEnumDto.REGISTRE_JUSTIFICANT_ENTRADA.getCodi();

	@Mock private PluginHelper pluginHelper;
	@Mock private ContingutRepository contingutRepository;
	@InjectMocks private ContingutHelper helper;

	// ---------------------------------------------------------------- isJustificantRegistre

	@Test
	void elJustificantPendentDeMoureEsJustificant() {
		assertThat(isJustificantRegistre(true, null, null, false)).isTrue();
	}

	@Test
	void ambElTipusDeJustificantIElNumeroDeRegistreEsJustificant() {
		assertThat(isJustificantRegistre(false, TIPUS_JUSTIFICANT, "GOIBE1/2026", false)).isTrue();
	}

	@Test
	void senseNumeroDeRegistreOAmbUnAltreTipusNoEsJustificant() {
		assertThat(isJustificantRegistre(false, TIPUS_JUSTIFICANT, null, false)).isFalse();
		assertThat(isJustificantRegistre(false, "ALTRE_TIPUS", "GOIBE1/2026", false)).isFalse();
		assertThat(isJustificantRegistre(false, null, "GOIBE1/2026", false)).isFalse();
	}

	@Test
	void unAnnexDeLAnotacioNoEsJustificantEncaraQueEnTinguiElTipus() {
		assertThat(isJustificantRegistre(false, TIPUS_JUSTIFICANT, "GOIBE1/2026", true)).isFalse();
	}

	// ---------------------------------------------------------------- conteJustificantsRegistre

	@Test
	void elJustificantIUnaCarpetaQueElConteEnContenen() {
		DocumentEntity justificant = document(true);

		assertThat(helper.conteJustificantsRegistre(justificant)).isTrue();
		assertThat(helper.conteJustificantsRegistre(carpeta(document(false), carpeta(justificant)))).isTrue();
	}

	@Test
	void unaCarpetaSenseJustificantsNoEnConte() {
		assertThat(helper.conteJustificantsRegistre(carpeta(document(false)))).isFalse();
	}

	@Test
	void elsJustificantsEsborratsNoCompten() {
		DocumentEntity esborrat = document(true);
		when(esborrat.getEsborrat()).thenReturn(1);

		assertThat(helper.conteJustificantsRegistre(carpeta(esborrat))).isFalse();
	}

	// ---------------------------------------------------------------- Arxiu

	@Test
	void esborrarElJustificantPendentDeMoureNoTocaLArxiu() {
		DocumentEntity justificant = document(true);
		when(justificant.getArxiuUuid()).thenReturn("uuid-regweb");

		helper.arxiuPropagarEliminacio(justificant);

		verifyNoInteractions(pluginHelper);
	}

	@Test
	void esborrarUnAltreDocumentEsPropagaALArxiu() {
		DocumentEntity document = document(false);
		when(document.getArxiuUuid()).thenReturn("uuid-ripea");
		when(document.getDocumentTipus()).thenReturn(DocumentTipusEnumDto.DIGITAL);

		helper.arxiuPropagarEliminacio(document);

		verify(pluginHelper).arxiuDocumentEsborrar(document);
	}

	@Test
	void moureUnaCarpetaLogicaNoMouElJustificantALArxiu() {
		CarpetaEntity carpeta = mock(CarpetaEntity.class);
		DocumentEntity justificant = document(true);
		DocumentEntity altre = document(false);
		when(altre.getArxiuUuid()).thenReturn("uuid-altre");
		when(contingutRepository.findByPareAndEsborratAndOrdenat(carpeta, 0)).thenReturn(Arrays.asList(justificant, altre));

		helper.arxiuCarpetaLogicaPropagarMoviment(carpeta, "uuid-expedient");

		verify(pluginHelper).arxiuDocumentMoure("uuid-altre", null, "uuid-expedient");
		verifyNoMoreInteractions(pluginHelper);
	}

	private static DocumentEntity document(boolean justificantMoureError) {
		DocumentEntity document = mock(DocumentEntity.class);
		lenient().when(document.isJustificantMoureError()).thenReturn(justificantMoureError);
		return document;
	}

	private static CarpetaEntity carpeta(ContingutEntity... fills) {
		CarpetaEntity carpeta = mock(CarpetaEntity.class);
		lenient().when(carpeta.getFills()).thenReturn(new LinkedHashSet<>(Arrays.asList(fills)));
		return carpeta;
	}
}
