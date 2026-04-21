package es.caib.ripea.service.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import es.caib.ripea.persistence.entity.DocumentEntity;
import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.ExpedientEntity;
import es.caib.ripea.persistence.entity.MetaDocumentEntity;
import es.caib.ripea.persistence.entity.MetaExpedientEntity;
import es.caib.ripea.persistence.entity.MetaExpedientTascaValidacioEntity;
import es.caib.ripea.persistence.entity.PinbalServeiEntity;
import es.caib.ripea.persistence.repository.DocumentRepository;
import es.caib.ripea.persistence.repository.ExpedientRepository;
import es.caib.ripea.persistence.repository.FluxFirmaUsuariRepository;
import es.caib.ripea.persistence.repository.MetaDocumentFluxPortafibRepository;
import es.caib.ripea.persistence.repository.MetaDocumentRepository;
import es.caib.ripea.persistence.repository.MetaExpedientRepository;
import es.caib.ripea.persistence.repository.MetaExpedientTascaValidacioRepository;
import es.caib.ripea.persistence.repository.PinbalServeiRepository;
import es.caib.ripea.persistence.repository.UsuariRepository;
import es.caib.ripea.service.intf.dto.ExpedientEstatEnumDto;
import es.caib.ripea.service.intf.dto.ItemValidacioTascaEnum;
import es.caib.ripea.service.intf.dto.LogObjecteTipusEnumDto;
import es.caib.ripea.service.intf.dto.LogTipusEnumDto;
import es.caib.ripea.service.intf.dto.MetaDocumentDto;
import es.caib.ripea.service.intf.dto.MultiplicitatEnumDto;
import es.caib.ripea.service.intf.exception.ExisteixenDocumentsException;

/**
 * Tests unitaris per a MetaDocumentHelper.
 *
 * Cobreix els mètodes públics amb casos normals i límit.
 * No arrenca cap context Spring: totes les dependències es proporcionen com a mocks de Mockito.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MetaDocumentHelperTest {

    // ── Dependències mockejades ──────────────────────────────────────────────

    @Mock private EntityComprovarHelper entityComprovarHelper;
    @Mock private MetaExpedientHelper metaExpedientHelper;
    @Mock private ContingutHelper contingutHelper;
    @Mock private PluginHelper pluginHelper;
    @Mock private CacheHelper cacheHelper;
    @Mock private ApplicationHelper applicationHelper;
    @Mock private MetaExpedientTascaValidacioRepository metaExpedientTascaValidacioRepository;
    @Mock private ExpedientRepository expedientRepository;
    @Mock private PinbalServeiRepository pinbalServeiRepository;
    @Mock private MetaExpedientRepository metaExpedientRepository;
    @Mock private MetaDocumentRepository metaDocumentRepository;
    @Mock private MetaDocumentFluxPortafibRepository metaDocumentFluxPortafibRepository;
    @Mock private FluxFirmaUsuariRepository fluxFirmaUsuariRepository;
    @Mock private DocumentRepository documentRepository;
    @Mock private UsuariRepository usuariRepository;
    @Mock private ContingutLogHelper contingutLogHelper;

    @InjectMocks
    private MetaDocumentHelper helper;

    private static final Long ENTITAT_ID        = 1L;
    private static final Long META_EXPEDIENT_ID = 10L;
    private static final Long META_DOCUMENT_ID  = 20L;
    private static final Long ORGAN_ID          = 30L;

    @BeforeEach
    void configurarMeterRegistry() {
        io.micrometer.core.instrument.MeterRegistry meterRegistry =
                new io.micrometer.core.instrument.simple.SimpleMeterRegistry();
        when(applicationHelper.getMeterRegistry()).thenReturn(meterRegistry);
    }

    // =========================================================================
    // moveTo
    // =========================================================================

    @Test
    void moveTo_desplacaElMetaDocumentALaPosicioIndicada() {
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);

        MetaDocumentEntity doc0 = mock(MetaDocumentEntity.class);
        MetaDocumentEntity doc1 = mock(MetaDocumentEntity.class);
        MetaDocumentEntity doc2 = mock(MetaDocumentEntity.class);
        when(doc0.getId()).thenReturn(100L);
        when(doc1.getId()).thenReturn(101L);
        when(doc2.getId()).thenReturn(META_DOCUMENT_ID);
        when(doc2.getMetaExpedient()).thenReturn(metaExpedient);

        when(metaDocumentRepository.findById(META_DOCUMENT_ID)).thenReturn(Optional.of(doc2));
        List<MetaDocumentEntity> llista = new ArrayList<>(Arrays.asList(doc0, doc1, doc2));
        when(metaDocumentRepository.findByMetaExpedientOrderByOrdreAsc(metaExpedient)).thenReturn(llista);

        helper.moveTo(META_DOCUMENT_ID, 0);

        // doc2 desplaçat a posició 0: updateOrdre(0) per a doc2
        verify(doc2).updateOrdre(0);
        // els altres queden en posicions 1 i 2
        verify(doc0).updateOrdre(anyInt());
        verify(doc1).updateOrdre(anyInt());
    }

    @Test
    void moveTo_ambUnSolElement_actualitzaOrdreA0() {
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        MetaDocumentEntity doc = mock(MetaDocumentEntity.class);
        when(doc.getId()).thenReturn(META_DOCUMENT_ID);
        when(doc.getMetaExpedient()).thenReturn(metaExpedient);
        when(metaDocumentRepository.findById(META_DOCUMENT_ID)).thenReturn(Optional.of(doc));
        when(metaDocumentRepository.findByMetaExpedientOrderByOrdreAsc(metaExpedient)).thenReturn(new ArrayList<>(Collections.singletonList(doc)));

        helper.moveTo(META_DOCUMENT_ID, 0);

        verify(doc).updateOrdre(0);
    }

    // =========================================================================
    // marcarPerDefecte
    // =========================================================================

    private EntitatEntity preparaEntitatIMetaExpedient(MetaExpedientEntity metaExpedient) {
        EntitatEntity entitat = mock(EntitatEntity.class);
        when(entityComprovarHelper.comprovarEntitat(ENTITAT_ID, false, false, false, false, true))
                .thenReturn(entitat);
        when(entityComprovarHelper.comprovarMetaExpedient(entitat, META_EXPEDIENT_ID))
                .thenReturn(metaExpedient);
        return entitat;
    }

    @Test
    void marcarPerDefecte_removeTrue_desactiva_perDefecteActual_iNoActiva_elNou() {
        MetaDocumentEntity existing = mock(MetaDocumentEntity.class);
        when(existing.isPerDefecte()).thenReturn(true);
        MetaDocumentEntity target = mock(MetaDocumentEntity.class);
        when(target.isPerDefecte()).thenReturn(false);

        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        when(metaExpedient.getId()).thenReturn(META_EXPEDIENT_ID);
        java.util.Set<MetaDocumentEntity> docs = new java.util.HashSet<>(Arrays.asList(existing, target));
        when(metaExpedient.getMetaDocuments()).thenReturn(docs);

        preparaEntitatIMetaExpedient(metaExpedient);
        when(entityComprovarHelper.comprovarMetaDocument(META_DOCUMENT_ID)).thenReturn(target);

        helper.marcarPerDefecte(ENTITAT_ID, META_EXPEDIENT_ID, META_DOCUMENT_ID, true);

        verify(existing).updatePerDefecte(false);
        verify(target, never()).updatePerDefecte(true);
    }

    @Test
    void marcarPerDefecte_removeFalse_desactiva_anterior_iActiva_elNou() {
        MetaDocumentEntity existing = mock(MetaDocumentEntity.class);
        when(existing.isPerDefecte()).thenReturn(true);
        MetaDocumentEntity target = mock(MetaDocumentEntity.class);
        when(target.isPerDefecte()).thenReturn(false);

        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        when(metaExpedient.getId()).thenReturn(META_EXPEDIENT_ID);
        java.util.Set<MetaDocumentEntity> docs = new java.util.HashSet<>(Arrays.asList(existing, target));
        when(metaExpedient.getMetaDocuments()).thenReturn(docs);

        preparaEntitatIMetaExpedient(metaExpedient);
        when(entityComprovarHelper.comprovarMetaDocument(META_DOCUMENT_ID)).thenReturn(target);

        helper.marcarPerDefecte(ENTITAT_ID, META_EXPEDIENT_ID, META_DOCUMENT_ID, false);

        verify(existing).updatePerDefecte(false);
        verify(target).updatePerDefecte(true);
    }

    @Test
    void marcarPerDefecte_cridaLogProcedimentObjecte() {
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        when(metaExpedient.getId()).thenReturn(META_EXPEDIENT_ID);
        when(metaExpedient.getMetaDocuments()).thenReturn(Collections.emptySet());

        MetaDocumentEntity target = mock(MetaDocumentEntity.class);
        when(target.isPerDefecte()).thenReturn(false);

        preparaEntitatIMetaExpedient(metaExpedient);
        when(entityComprovarHelper.comprovarMetaDocument(META_DOCUMENT_ID)).thenReturn(target);

        helper.marcarPerDefecte(ENTITAT_ID, META_EXPEDIENT_ID, META_DOCUMENT_ID, false);

        verify(contingutLogHelper).logProcedimentObjecte(
                any(), any(), any(), any(), any(), any(), any());
    }

    // =========================================================================
    // evictErrorsValidacioAndNotify
    // =========================================================================

    @Test
    void evictErrorsValidacioAndNotify_metaExpedientIdNull_noFaRes() {
        helper.evictErrorsValidacioAndNotify(ENTITAT_ID, null, true);

        verify(cacheHelper, never()).evictErrorsValidacioAndNotify(any());
        verify(cacheHelper, never()).evictErrorsValidacioPerNode(any());
    }

    @Test
    void evictErrorsValidacioAndNotify_ambExpedients_notificaSseTrue_crideaAndNotify() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        when(entityComprovarHelper.comprovarEntitat(ENTITAT_ID, false, false, false, false, true))
                .thenReturn(entitat);
        when(entityComprovarHelper.comprovarMetaExpedient(entitat, META_EXPEDIENT_ID))
                .thenReturn(metaExpedient);
        ExpedientEntity expedient = mock(ExpedientEntity.class);
        when(expedient.getId()).thenReturn(99L);
        when(expedientRepository.findByEntitatAndMetaExpedientAndEstatAndEsborrat(
                entitat, metaExpedient, ExpedientEstatEnumDto.OBERT, 0))
                .thenReturn(Collections.singletonList(expedient));

        helper.evictErrorsValidacioAndNotify(ENTITAT_ID, META_EXPEDIENT_ID, true);

        verify(cacheHelper).evictErrorsValidacioAndNotify(99L);
        verify(cacheHelper, never()).evictErrorsValidacioPerNode(any());
    }

    @Test
    void evictErrorsValidacioAndNotify_ambExpedients_notificaSseFalse_crideaPerNode() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        when(entityComprovarHelper.comprovarEntitat(ENTITAT_ID, false, false, false, false, true))
                .thenReturn(entitat);
        when(entityComprovarHelper.comprovarMetaExpedient(entitat, META_EXPEDIENT_ID))
                .thenReturn(metaExpedient);
        ExpedientEntity expedient = mock(ExpedientEntity.class);
        when(expedient.getId()).thenReturn(99L);
        when(expedientRepository.findByEntitatAndMetaExpedientAndEstatAndEsborrat(
                entitat, metaExpedient, ExpedientEstatEnumDto.OBERT, 0))
                .thenReturn(Collections.singletonList(expedient));

        helper.evictErrorsValidacioAndNotify(ENTITAT_ID, META_EXPEDIENT_ID, false);

        verify(cacheHelper).evictErrorsValidacioPerNode(99L);
        verify(cacheHelper, never()).evictErrorsValidacioAndNotify(any());
    }

    @Test
    void evictErrorsValidacioAndNotify_senseExpedients_nocrideaCache() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        when(entityComprovarHelper.comprovarEntitat(ENTITAT_ID, false, false, false, false, true))
                .thenReturn(entitat);
        when(entityComprovarHelper.comprovarMetaExpedient(entitat, META_EXPEDIENT_ID))
                .thenReturn(metaExpedient);
        when(expedientRepository.findByEntitatAndMetaExpedientAndEstatAndEsborrat(
                entitat, metaExpedient, ExpedientEstatEnumDto.OBERT, 0))
                .thenReturn(Collections.emptyList());

        helper.evictErrorsValidacioAndNotify(ENTITAT_ID, META_EXPEDIENT_ID, true);

        verify(cacheHelper, never()).evictErrorsValidacioAndNotify(any());
        verify(cacheHelper, never()).evictErrorsValidacioPerNode(any());
    }

    // =========================================================================
    // delete
    // =========================================================================

    private void preparaDelete(EntitatEntity entitat, MetaExpedientEntity metaExpedient,
            MetaDocumentEntity metaDocument) {
        when(entityComprovarHelper.comprovarEntitat(ENTITAT_ID, false, false, false, false, true))
                .thenReturn(entitat);
        when(entityComprovarHelper.comprovarMetaExpedient(entitat, META_EXPEDIENT_ID))
                .thenReturn(metaExpedient);
        when(entityComprovarHelper.comprovarMetaDocument(entitat, metaExpedient, META_DOCUMENT_ID))
                .thenReturn(metaDocument);
        when(documentRepository.findByMetaNode(metaDocument)).thenReturn(Collections.emptyList());
        when(metaExpedientTascaValidacioRepository.findByItemValidacioAndItemId(
                ItemValidacioTascaEnum.DOCUMENT, META_DOCUMENT_ID))
                .thenReturn(Collections.emptyList());
        // evict intern
        when(expedientRepository.findByEntitatAndMetaExpedientAndEstatAndEsborrat(
                entitat, metaExpedient, ExpedientEstatEnumDto.OBERT, 0))
                .thenReturn(Collections.emptyList());
        when(metaExpedient.getId()).thenReturn(META_EXPEDIENT_ID);
    }

    @Test
    void delete_ambMetaExpedient_senseDocuments_eliminaMetaDocument() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        MetaDocumentEntity metaDocument = mock(MetaDocumentEntity.class);
        preparaDelete(entitat, metaExpedient, metaDocument);

        helper.delete(ENTITAT_ID, META_EXPEDIENT_ID, META_DOCUMENT_ID, "IPA_ADMIN", ORGAN_ID);

        verify(metaDocumentRepository).delete(metaDocument);
    }

    @Test
    void delete_ambDocumentsExistents_llencaExisteixenDocumentsException() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        MetaDocumentEntity metaDocument = mock(MetaDocumentEntity.class);
        when(entityComprovarHelper.comprovarEntitat(ENTITAT_ID, false, false, false, false, true))
                .thenReturn(entitat);
        when(entityComprovarHelper.comprovarMetaExpedient(entitat, META_EXPEDIENT_ID))
                .thenReturn(metaExpedient);
        when(entityComprovarHelper.comprovarMetaDocument(entitat, metaExpedient, META_DOCUMENT_ID))
                .thenReturn(metaDocument);
        when(documentRepository.findByMetaNode(metaDocument))
                .thenReturn(Collections.singletonList(mock(DocumentEntity.class)));
        when(expedientRepository.findByEntitatAndMetaExpedientAndEstatAndEsborrat(
                entitat, metaExpedient, ExpedientEstatEnumDto.OBERT, 0))
                .thenReturn(Collections.emptyList());

        assertThatThrownBy(() ->
                helper.delete(ENTITAT_ID, META_EXPEDIENT_ID, META_DOCUMENT_ID, "IPA_ADMIN", ORGAN_ID))
                .isInstanceOf(ExisteixenDocumentsException.class);

        verify(metaDocumentRepository, never()).delete(any());
    }

    @Test
    void delete_ambValidacions_eliminaValidacionsAbansDelMetaDocument() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        MetaDocumentEntity metaDocument = mock(MetaDocumentEntity.class);
        MetaExpedientTascaValidacioEntity validacio = mock(MetaExpedientTascaValidacioEntity.class);
        List<MetaExpedientTascaValidacioEntity> validacions = Collections.singletonList(validacio);
        when(entityComprovarHelper.comprovarEntitat(ENTITAT_ID, false, false, false, false, true))
                .thenReturn(entitat);
        when(entityComprovarHelper.comprovarMetaExpedient(entitat, META_EXPEDIENT_ID))
                .thenReturn(metaExpedient);
        when(entityComprovarHelper.comprovarMetaDocument(entitat, metaExpedient, META_DOCUMENT_ID))
                .thenReturn(metaDocument);
        when(documentRepository.findByMetaNode(metaDocument)).thenReturn(Collections.emptyList());
        when(metaExpedientTascaValidacioRepository.findByItemValidacioAndItemId(
                ItemValidacioTascaEnum.DOCUMENT, META_DOCUMENT_ID)).thenReturn(validacions);
        when(expedientRepository.findByEntitatAndMetaExpedientAndEstatAndEsborrat(
                entitat, metaExpedient, ExpedientEstatEnumDto.OBERT, 0))
                .thenReturn(Collections.emptyList());
        when(metaExpedient.getId()).thenReturn(META_EXPEDIENT_ID);

        helper.delete(ENTITAT_ID, META_EXPEDIENT_ID, META_DOCUMENT_ID, "IPA_ADMIN", ORGAN_ID);

        verify(metaExpedientTascaValidacioRepository).deleteAll(validacions);
        verify(metaDocumentRepository).delete(metaDocument);
    }

    @Test
    void delete_rolOrganAdmin_crideaCanviarRevisioADisseny() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        MetaDocumentEntity metaDocument = mock(MetaDocumentEntity.class);
        preparaDelete(entitat, metaExpedient, metaDocument);

        helper.delete(ENTITAT_ID, META_EXPEDIENT_ID, META_DOCUMENT_ID, "IPA_ORGAN_ADMIN", ORGAN_ID);

        verify(metaExpedientHelper).canviarRevisioADisseny(ENTITAT_ID, META_EXPEDIENT_ID, ORGAN_ID);
    }

    @Test
    void delete_rolNoOrganAdmin_nocrideaCanviarRevisio() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        MetaDocumentEntity metaDocument = mock(MetaDocumentEntity.class);
        preparaDelete(entitat, metaExpedient, metaDocument);

        helper.delete(ENTITAT_ID, META_EXPEDIENT_ID, META_DOCUMENT_ID, "IPA_ADMIN", ORGAN_ID);

        verify(metaExpedientHelper, never()).canviarRevisioADisseny(any(), any(), any());
    }

    @Test
    void delete_ambMetaExpedient_cridaLogEliminacio() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        MetaDocumentEntity metaDocument = mock(MetaDocumentEntity.class);
        preparaDelete(entitat, metaExpedient, metaDocument);

        helper.delete(ENTITAT_ID, META_EXPEDIENT_ID, META_DOCUMENT_ID, "IPA_ADMIN", ORGAN_ID);

        verify(contingutLogHelper).logProcedimentObjecte(
                any(),
                org.mockito.ArgumentMatchers.eq(LogTipusEnumDto.MODIFICACIO),
                org.mockito.ArgumentMatchers.isNull(),
                org.mockito.ArgumentMatchers.eq(LogObjecteTipusEnumDto.METADOCUMENT),
                org.mockito.ArgumentMatchers.eq(LogTipusEnumDto.ELIMINACIO),
                any(), any());
    }

    @Test
    void delete_retornaElMetaDocumentEliminat() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        MetaDocumentEntity metaDocument = mock(MetaDocumentEntity.class);
        preparaDelete(entitat, metaExpedient, metaDocument);

        MetaDocumentEntity resultat = helper.delete(ENTITAT_ID, META_EXPEDIENT_ID, META_DOCUMENT_ID, "IPA_ADMIN", ORGAN_ID);

        assertThat(resultat).isSameAs(metaDocument);
    }

    // =========================================================================
    // updateActiu
    // =========================================================================

    private void preparaUpdateActiu(EntitatEntity entitat, MetaExpedientEntity metaExpedient,
            MetaDocumentEntity metaDocument) {
        when(entityComprovarHelper.comprovarEntitat(ENTITAT_ID, false, false, false, false, true))
                .thenReturn(entitat);
        when(entityComprovarHelper.comprovarMetaExpedient(entitat, META_EXPEDIENT_ID))
                .thenReturn(metaExpedient);
        when(entityComprovarHelper.comprovarMetaDocument(entitat, metaExpedient, META_DOCUMENT_ID))
                .thenReturn(metaDocument);
        when(expedientRepository.findByEntitatAndMetaExpedientAndEstatAndEsborrat(
                entitat, metaExpedient, ExpedientEstatEnumDto.OBERT, 0))
                .thenReturn(Collections.emptyList());
        when(metaExpedient.getId()).thenReturn(META_EXPEDIENT_ID);
    }

    @Test
    void updateActiu_crideaUpdateActiuALaEntitat() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        MetaDocumentEntity metaDocument = mock(MetaDocumentEntity.class);
        preparaUpdateActiu(entitat, metaExpedient, metaDocument);

        helper.updateActiu(ENTITAT_ID, META_EXPEDIENT_ID, META_DOCUMENT_ID, true, "IPA_ADMIN");

        verify(metaDocument).updateActiu(true);
    }

    @Test
    void updateActiu_rolOrganAdmin_crideaCanviarRevisioADisseny() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        MetaDocumentEntity metaDocument = mock(MetaDocumentEntity.class);
        preparaUpdateActiu(entitat, metaExpedient, metaDocument);

        helper.updateActiu(ENTITAT_ID, META_EXPEDIENT_ID, META_DOCUMENT_ID, true, "IPA_ORGAN_ADMIN");

        verify(metaExpedientHelper).canviarRevisioADisseny(ENTITAT_ID, META_EXPEDIENT_ID, null);
    }

    @Test
    void updateActiu_rolNoOrganAdmin_nocrideaCanviarRevisio() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        MetaDocumentEntity metaDocument = mock(MetaDocumentEntity.class);
        preparaUpdateActiu(entitat, metaExpedient, metaDocument);

        helper.updateActiu(ENTITAT_ID, META_EXPEDIENT_ID, META_DOCUMENT_ID, true, "IPA_ADMIN");

        verify(metaExpedientHelper, never()).canviarRevisioADisseny(any(), any(), any());
    }

    @Test
    void updateActiu_activaTrue_ambMetaExpedient_logActivacio() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        MetaDocumentEntity metaDocument = mock(MetaDocumentEntity.class);
        preparaUpdateActiu(entitat, metaExpedient, metaDocument);

        helper.updateActiu(ENTITAT_ID, META_EXPEDIENT_ID, META_DOCUMENT_ID, true, "IPA_ADMIN");

        verify(contingutLogHelper).logProcedimentObjecte(
                any(), any(), any(), any(),
                org.mockito.ArgumentMatchers.eq(LogTipusEnumDto.ACTIVACIO),
                any(), any());
    }

    @Test
    void updateActiu_activaFalse_ambMetaExpedient_logDesactivacio() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        MetaDocumentEntity metaDocument = mock(MetaDocumentEntity.class);
        preparaUpdateActiu(entitat, metaExpedient, metaDocument);

        helper.updateActiu(ENTITAT_ID, META_EXPEDIENT_ID, META_DOCUMENT_ID, false, "IPA_ADMIN");

        verify(contingutLogHelper).logProcedimentObjecte(
                any(), any(), any(), any(),
                org.mockito.ArgumentMatchers.eq(LogTipusEnumDto.DESACTIVACIO),
                any(), any());
    }

    @Test
    void updateActiu_retornaElMetaDocument() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        MetaDocumentEntity metaDocument = mock(MetaDocumentEntity.class);
        preparaUpdateActiu(entitat, metaExpedient, metaDocument);

        MetaDocumentEntity resultat = helper.updateActiu(ENTITAT_ID, META_EXPEDIENT_ID, META_DOCUMENT_ID, true, "IPA_ADMIN");

        assertThat(resultat).isSameAs(metaDocument);
    }

    // =========================================================================
    // update
    // =========================================================================

    private MetaDocumentDto buildMetaDocumentDto(MultiplicitatEnumDto multiplicitat) {
        MetaDocumentDto dto = new MetaDocumentDto();
        dto.setId(META_DOCUMENT_ID);
        dto.setCodi("COD");
        dto.setNom("Nom");
        dto.setMultiplicitat(multiplicitat);
        dto.setPortafirmesFluxosId(new String[0]);
        return dto;
    }

    @Test
    void update_multiplicitatNoCanvia_noEvictaValidacions() {
        MetaDocumentEntity metaDocument = mock(MetaDocumentEntity.class);
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        when(metaDocument.getMultiplicitat()).thenReturn(MultiplicitatEnumDto.M_1);
        when(metaDocument.getMetaExpedient()).thenReturn(metaExpedient);
        when(metaDocument.getEntitat()).thenReturn(mock(EntitatEntity.class));
        when(metaDocument.getFluxosFirma()).thenReturn(new ArrayList<>());
        when(metaDocumentRepository.findById(META_DOCUMENT_ID)).thenReturn(Optional.of(metaDocument));
        when(metaExpedient.getId()).thenReturn(META_EXPEDIENT_ID);

        helper.update(META_EXPEDIENT_ID, buildMetaDocumentDto(MultiplicitatEnumDto.M_1), null, null, null);

        verify(cacheHelper, never()).evictErrorsValidacioAndNotify(any());
        verify(cacheHelper, never()).evictErrorsValidacioPerNode(any());
    }

    @Test
    void update_multiplicitatCanvia_evictaValidacionsExpedients() {
        MetaDocumentEntity metaDocument = mock(MetaDocumentEntity.class);
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        EntitatEntity entitat = mock(EntitatEntity.class);
        when(metaDocument.getMultiplicitat()).thenReturn(MultiplicitatEnumDto.M_1);
        when(metaDocument.getMetaExpedient()).thenReturn(metaExpedient);
        when(metaDocument.getEntitat()).thenReturn(entitat);
        when(metaDocument.getFluxosFirma()).thenReturn(new ArrayList<>());
        when(metaDocumentRepository.findById(META_DOCUMENT_ID)).thenReturn(Optional.of(metaDocument));
        when(entitat.getId()).thenReturn(ENTITAT_ID);
        when(metaExpedient.getId()).thenReturn(META_EXPEDIENT_ID);

        when(entityComprovarHelper.comprovarEntitat(ENTITAT_ID, false, false, false, false, true))
                .thenReturn(entitat);
        when(entityComprovarHelper.comprovarMetaExpedient(entitat, META_EXPEDIENT_ID))
                .thenReturn(metaExpedient);
        ExpedientEntity expedient = mock(ExpedientEntity.class);
        when(expedient.getId()).thenReturn(55L);
        when(expedientRepository.findByEntitatAndMetaExpedientAndEstatAndEsborrat(
                entitat, metaExpedient, ExpedientEstatEnumDto.OBERT, 0))
                .thenReturn(Collections.singletonList(expedient));

        helper.update(META_EXPEDIENT_ID, buildMetaDocumentDto(MultiplicitatEnumDto.M_0_N), null, null, null);

        verify(cacheHelper).evictErrorsValidacioPerNode(55L);
    }

    @Test
    void update_ambMetaExpedientId_cridaLog() {
        MetaDocumentEntity metaDocument = mock(MetaDocumentEntity.class);
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        when(metaDocument.getMultiplicitat()).thenReturn(MultiplicitatEnumDto.M_1);
        when(metaDocument.getMetaExpedient()).thenReturn(metaExpedient);
        when(metaDocument.getEntitat()).thenReturn(mock(EntitatEntity.class));
        when(metaDocument.getFluxosFirma()).thenReturn(new ArrayList<>());
        when(metaDocumentRepository.findById(META_DOCUMENT_ID)).thenReturn(Optional.of(metaDocument));
        when(metaExpedient.getId()).thenReturn(META_EXPEDIENT_ID);

        helper.update(META_EXPEDIENT_ID, buildMetaDocumentDto(MultiplicitatEnumDto.M_1), null, null, null);

        verify(contingutLogHelper).logProcedimentObjecte(
                any(), any(), any(),
                org.mockito.ArgumentMatchers.eq(LogObjecteTipusEnumDto.METADOCUMENT),
                org.mockito.ArgumentMatchers.eq(LogTipusEnumDto.MODIFICACIO),
                any(), any());
    }

    @Test
    void update_senseMetaExpedientId_nocrideaLog() {
        MetaDocumentEntity metaDocument = mock(MetaDocumentEntity.class);
        when(metaDocument.getMultiplicitat()).thenReturn(MultiplicitatEnumDto.M_1);
        when(metaDocument.getMetaExpedient()).thenReturn(null);
        when(metaDocument.getEntitat()).thenReturn(mock(EntitatEntity.class));
        when(metaDocument.getFluxosFirma()).thenReturn(new ArrayList<>());
        when(metaDocumentRepository.findById(META_DOCUMENT_ID)).thenReturn(Optional.of(metaDocument));

        helper.update(null, buildMetaDocumentDto(MultiplicitatEnumDto.M_1), null, null, null);

        verify(contingutLogHelper, never()).logProcedimentObjecte(any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void update_retornaElMetaDocument() {
        MetaDocumentEntity metaDocument = mock(MetaDocumentEntity.class);
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        when(metaDocument.getMultiplicitat()).thenReturn(MultiplicitatEnumDto.M_1);
        when(metaDocument.getMetaExpedient()).thenReturn(metaExpedient);
        when(metaDocument.getEntitat()).thenReturn(mock(EntitatEntity.class));
        when(metaDocument.getFluxosFirma()).thenReturn(new ArrayList<>());
        when(metaDocumentRepository.findById(META_DOCUMENT_ID)).thenReturn(Optional.of(metaDocument));
        when(metaExpedient.getId()).thenReturn(META_EXPEDIENT_ID);

        MetaDocumentEntity resultat = helper.update(META_EXPEDIENT_ID, buildMetaDocumentDto(MultiplicitatEnumDto.M_1), null, null, null);

        assertThat(resultat).isSameAs(metaDocument);
    }

    // =========================================================================
    // create
    // =========================================================================

    private MetaDocumentDto buildCreateDto() {
        MetaDocumentDto dto = new MetaDocumentDto();
        dto.setCodi("COD_NOU");
        dto.setNom("Nom Nou");
        dto.setMultiplicitat(MultiplicitatEnumDto.M_1);
        dto.setPortafirmesFluxosId(new String[0]);
        return dto;
    }

    @Test
    void create_ambMetaExpedient_desaElMetaDocument() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        MetaDocumentEntity saved = mock(MetaDocumentEntity.class);
        when(entityComprovarHelper.comprovarEntitat(ENTITAT_ID, false, false, false, true, false))
                .thenReturn(entitat);
        when(entityComprovarHelper.comprovarMetaExpedient(entitat, META_EXPEDIENT_ID))
                .thenReturn(metaExpedient);
        when(metaDocumentRepository.countByMetaExpedient(metaExpedient)).thenReturn(0);
        when(metaDocumentRepository.save(any(MetaDocumentEntity.class))).thenReturn(saved);
        when(saved.getFluxosFirma()).thenReturn(new ArrayList<>());
        when(expedientRepository.findByEntitatAndMetaExpedientAndEstatAndEsborrat(
                entitat, metaExpedient, ExpedientEstatEnumDto.OBERT, 0))
                .thenReturn(Collections.emptyList());
        when(entitat.getId()).thenReturn(ENTITAT_ID);
        when(metaExpedient.getId()).thenReturn(META_EXPEDIENT_ID);

        MetaDocumentEntity resultat = helper.create(ENTITAT_ID, META_EXPEDIENT_ID, buildCreateDto(),
                null, null, null, "IPA_ADMIN", ORGAN_ID);

        verify(metaDocumentRepository).save(any(MetaDocumentEntity.class));
        assertThat(resultat).isSameAs(saved);
    }

    @Test
    void create_rolOrganAdmin_crideaCanviarRevisioADisseny() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        MetaDocumentEntity saved = mock(MetaDocumentEntity.class);
        when(entityComprovarHelper.comprovarEntitat(ENTITAT_ID, false, false, false, true, false))
                .thenReturn(entitat);
        when(entityComprovarHelper.comprovarMetaExpedient(entitat, META_EXPEDIENT_ID))
                .thenReturn(metaExpedient);
        when(metaDocumentRepository.countByMetaExpedient(metaExpedient)).thenReturn(0);
        when(metaDocumentRepository.save(any(MetaDocumentEntity.class))).thenReturn(saved);
        when(saved.getFluxosFirma()).thenReturn(new ArrayList<>());
        when(expedientRepository.findByEntitatAndMetaExpedientAndEstatAndEsborrat(
                entitat, metaExpedient, ExpedientEstatEnumDto.OBERT, 0))
                .thenReturn(Collections.emptyList());
        when(entitat.getId()).thenReturn(ENTITAT_ID);
        when(metaExpedient.getId()).thenReturn(META_EXPEDIENT_ID);

        helper.create(ENTITAT_ID, META_EXPEDIENT_ID, buildCreateDto(),
                null, null, null, "IPA_ORGAN_ADMIN", ORGAN_ID);

        verify(metaExpedientHelper).canviarRevisioADisseny(ENTITAT_ID, META_EXPEDIENT_ID, ORGAN_ID);
    }

    @Test
    void create_rolNoOrganAdmin_nocrideaCanviarRevisio() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        MetaDocumentEntity saved = mock(MetaDocumentEntity.class);
        when(entityComprovarHelper.comprovarEntitat(ENTITAT_ID, false, false, false, true, false))
                .thenReturn(entitat);
        when(entityComprovarHelper.comprovarMetaExpedient(entitat, META_EXPEDIENT_ID))
                .thenReturn(metaExpedient);
        when(metaDocumentRepository.countByMetaExpedient(metaExpedient)).thenReturn(0);
        when(metaDocumentRepository.save(any(MetaDocumentEntity.class))).thenReturn(saved);
        when(saved.getFluxosFirma()).thenReturn(new ArrayList<>());
        when(expedientRepository.findByEntitatAndMetaExpedientAndEstatAndEsborrat(
                entitat, metaExpedient, ExpedientEstatEnumDto.OBERT, 0))
                .thenReturn(Collections.emptyList());
        when(entitat.getId()).thenReturn(ENTITAT_ID);
        when(metaExpedient.getId()).thenReturn(META_EXPEDIENT_ID);

        helper.create(ENTITAT_ID, META_EXPEDIENT_ID, buildCreateDto(),
                null, null, null, "IPA_ADMIN", ORGAN_ID);

        verify(metaExpedientHelper, never()).canviarRevisioADisseny(any(), any(), any());
    }

    @Test
    void create_ambMetaExpedient_cridaLogCreacio() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        MetaDocumentEntity saved = mock(MetaDocumentEntity.class);
        when(entityComprovarHelper.comprovarEntitat(ENTITAT_ID, false, false, false, true, false))
                .thenReturn(entitat);
        when(entityComprovarHelper.comprovarMetaExpedient(entitat, META_EXPEDIENT_ID))
                .thenReturn(metaExpedient);
        when(metaDocumentRepository.countByMetaExpedient(metaExpedient)).thenReturn(0);
        when(metaDocumentRepository.save(any(MetaDocumentEntity.class))).thenReturn(saved);
        when(saved.getFluxosFirma()).thenReturn(new ArrayList<>());
        when(expedientRepository.findByEntitatAndMetaExpedientAndEstatAndEsborrat(
                entitat, metaExpedient, ExpedientEstatEnumDto.OBERT, 0))
                .thenReturn(Collections.emptyList());
        when(entitat.getId()).thenReturn(ENTITAT_ID);
        when(metaExpedient.getId()).thenReturn(META_EXPEDIENT_ID);

        helper.create(ENTITAT_ID, META_EXPEDIENT_ID, buildCreateDto(),
                null, null, null, "IPA_ADMIN", ORGAN_ID);

        verify(contingutLogHelper).logProcedimentObjecte(
                any(), any(), any(),
                org.mockito.ArgumentMatchers.eq(LogObjecteTipusEnumDto.METADOCUMENT),
                org.mockito.ArgumentMatchers.eq(LogTipusEnumDto.CREACIO),
                any(), any());
    }

    // =========================================================================
    // findByCodiAndProcediment
    // =========================================================================

    @Test
    void findByCodiAndProcediment_delegaAlRepositori() {
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        MetaDocumentEntity metaDocument = mock(MetaDocumentEntity.class);
        when(metaDocumentRepository.findByMetaExpedientAndCodi(metaExpedient, "COD")).thenReturn(metaDocument);

        MetaDocumentEntity resultat = helper.findByCodiAndProcediment(metaExpedient, "COD");

        assertThat(resultat).isSameAs(metaDocument);
    }

    @Test
    void findByCodiAndProcediment_quanNoExisteix_retornaNull() {
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        when(metaDocumentRepository.findByMetaExpedientAndCodi(metaExpedient, "INEXISTENT")).thenReturn(null);

        assertThat(helper.findByCodiAndProcediment(metaExpedient, "INEXISTENT")).isNull();
    }

    // =========================================================================
    // findMetaDocumentsDisponiblesPerCreacio
    // =========================================================================

    @Test
    void findMetaDocumentsDisponiblesPerCreacio_senseExpedient_senseGenerics_retornaMetadocsDelProcediment() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        MetaDocumentEntity doc = mock(MetaDocumentEntity.class);
        when(metaExpedient.isPermetMetadocsGenerals()).thenReturn(false);
        when(metaDocumentRepository.findByMetaExpedientAndActiuTrue(metaExpedient))
                .thenReturn(Collections.singletonList(doc));

        List<MetaDocumentEntity> resultat = helper.findMetaDocumentsDisponiblesPerCreacio(
                entitat, null, metaExpedient, false);

        assertThat(resultat).containsExactly(doc);
        verify(metaDocumentRepository, never()).findWithoutMetaExpedient();
    }

    @Test
    void findMetaDocumentsDisponiblesPerCreacio_senseExpedient_ambGenerics_retornaTambeGenerics() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        MetaDocumentEntity docProcediment = mock(MetaDocumentEntity.class);
        MetaDocumentEntity docGeneric = mock(MetaDocumentEntity.class);
        when(metaExpedient.isPermetMetadocsGenerals()).thenReturn(true);
        when(metaDocumentRepository.findByMetaExpedientAndActiuTrue(metaExpedient))
                .thenReturn(new ArrayList<>(Collections.singletonList(docProcediment)));
        when(metaDocumentRepository.findWithoutMetaExpedient())
                .thenReturn(Collections.singletonList(docGeneric));

        List<MetaDocumentEntity> resultat = helper.findMetaDocumentsDisponiblesPerCreacio(
                entitat, null, metaExpedient, false);

        assertThat(resultat).contains(docProcediment, docGeneric);
    }

    @Test
    void findMetaDocumentsDisponiblesPerCreacio_ambExpedient_metaDocM1JaExisteix_noEsAfegit() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        MetaDocumentEntity metaDoc = mock(MetaDocumentEntity.class);
        DocumentEntity docExistent = mock(DocumentEntity.class);

        when(metaExpedient.isPermetMetadocsGenerals()).thenReturn(false);
        when(metaDoc.getMultiplicitat()).thenReturn(MultiplicitatEnumDto.M_1);
        when(docExistent.getMetaNode()).thenReturn(metaDoc);

        ExpedientEntity expedient = mock(ExpedientEntity.class);
        when(expedient.getMetaExpedient()).thenReturn(metaExpedient);
        when(metaDocumentRepository.findByMetaExpedientAndActiuTrue(metaExpedient))
                .thenReturn(Collections.singletonList(metaDoc));
        when(documentRepository.findByExpedientAndEsborrat(expedient, 0))
                .thenReturn(Collections.singletonList(docExistent));

        List<MetaDocumentEntity> resultat = helper.findMetaDocumentsDisponiblesPerCreacio(
                entitat, expedient, null, false);

        assertThat(resultat).doesNotContain(metaDoc);
    }

    @Test
    void findMetaDocumentsDisponiblesPerCreacio_ambExpedient_metaDocM0NJaExisteix_encararaEsAfegit() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        MetaDocumentEntity metaDoc = mock(MetaDocumentEntity.class);
        DocumentEntity docExistent = mock(DocumentEntity.class);

        when(metaExpedient.isPermetMetadocsGenerals()).thenReturn(false);
        when(metaDoc.getMultiplicitat()).thenReturn(MultiplicitatEnumDto.M_0_N);
        when(docExistent.getMetaNode()).thenReturn(metaDoc);

        ExpedientEntity expedient = mock(ExpedientEntity.class);
        when(expedient.getMetaExpedient()).thenReturn(metaExpedient);
        when(metaDocumentRepository.findByMetaExpedientAndActiuTrue(metaExpedient))
                .thenReturn(Collections.singletonList(metaDoc));
        when(documentRepository.findByExpedientAndEsborrat(expedient, 0))
                .thenReturn(Collections.singletonList(docExistent));

        List<MetaDocumentEntity> resultat = helper.findMetaDocumentsDisponiblesPerCreacio(
                entitat, expedient, null, false);

        assertThat(resultat).contains(metaDoc);
    }

    @Test
    void findMetaDocumentsDisponiblesPerCreacio_ambExpedient_noExisteixDocument_esAfegit() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        MetaDocumentEntity metaDoc = mock(MetaDocumentEntity.class);

        when(metaExpedient.isPermetMetadocsGenerals()).thenReturn(false);

        ExpedientEntity expedient = mock(ExpedientEntity.class);
        when(expedient.getMetaExpedient()).thenReturn(metaExpedient);
        when(metaDocumentRepository.findByMetaExpedientAndActiuTrue(metaExpedient))
                .thenReturn(Collections.singletonList(metaDoc));
        when(documentRepository.findByExpedientAndEsborrat(expedient, 0))
                .thenReturn(Collections.emptyList());

        List<MetaDocumentEntity> resultat = helper.findMetaDocumentsDisponiblesPerCreacio(
                entitat, expedient, null, false);

        assertThat(resultat).contains(metaDoc);
    }

    // =========================================================================
    // findMetaDocumentsPinbalDisponiblesPerCreacio
    // =========================================================================

    @Test
    void findMetaDocumentsPinbalDisponiblesPerCreacio_retornaSolsElsAmbPinbalActiu() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        MetaDocumentEntity docPinbal = mock(MetaDocumentEntity.class);
        MetaDocumentEntity docNoPinbal = mock(MetaDocumentEntity.class);

        when(metaExpedient.isPermetMetadocsGenerals()).thenReturn(false);
        when(docPinbal.isPinbalActiu()).thenReturn(true);
        when(docPinbal.getNom()).thenReturn("DocPinbal");
        when(docNoPinbal.isPinbalActiu()).thenReturn(false);
        when(docNoPinbal.getNom()).thenReturn("DocNoPinbal");

        ExpedientEntity expedient = mock(ExpedientEntity.class);
        when(expedient.getMetaExpedient()).thenReturn(metaExpedient);
        when(metaDocumentRepository.findByMetaExpedientAndActiuTrue(metaExpedient))
                .thenReturn(Arrays.asList(docPinbal, docNoPinbal));
        when(documentRepository.findByExpedientAndEsborrat(expedient, 0))
                .thenReturn(Collections.emptyList());

        List<MetaDocumentEntity> resultat = helper.findMetaDocumentsPinbalDisponiblesPerCreacio(expedient);

        assertThat(resultat).containsExactly(docPinbal);
    }

    @Test
    void findMetaDocumentsPinbalDisponiblesPerCreacio_sensePinbal_retornaLlistaVuida() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        MetaDocumentEntity docNoPinbal = mock(MetaDocumentEntity.class);

        when(metaExpedient.isPermetMetadocsGenerals()).thenReturn(false);
        when(docNoPinbal.isPinbalActiu()).thenReturn(false);

        ExpedientEntity expedient = mock(ExpedientEntity.class);
        when(expedient.getMetaExpedient()).thenReturn(metaExpedient);
        when(metaDocumentRepository.findByMetaExpedientAndActiuTrue(metaExpedient))
                .thenReturn(Collections.singletonList(docNoPinbal));
        when(documentRepository.findByExpedientAndEsborrat(expedient, 0))
                .thenReturn(Collections.emptyList());

        List<MetaDocumentEntity> resultat = helper.findMetaDocumentsPinbalDisponiblesPerCreacio(expedient);

        assertThat(resultat).isEmpty();
    }
}
