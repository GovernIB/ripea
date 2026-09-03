package es.caib.ripea.service.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import es.caib.ripea.persistence.entity.ContingutEntity;
import es.caib.ripea.persistence.entity.DocumentEntity;
import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.ExpedientEntity;
import es.caib.ripea.persistence.entity.FluxFirmaUsuariEntity;
import es.caib.ripea.persistence.entity.MetaDocumentEntity;
import es.caib.ripea.persistence.entity.MetaDocumentFluxPortafibEntity;
import es.caib.ripea.persistence.entity.MetaExpedientEntity;
import es.caib.ripea.persistence.entity.MetaExpedientTascaValidacioEntity;
import es.caib.ripea.persistence.entity.PinbalServeiEntity;
import es.caib.ripea.persistence.entity.UsuariEntity;
import es.caib.ripea.persistence.repository.DocumentRepository;
import es.caib.ripea.persistence.repository.ExpedientRepository;
import es.caib.ripea.persistence.repository.FluxFirmaUsuariRepository;
import es.caib.ripea.persistence.repository.MetaDocumentFluxPortafibRepository;
import es.caib.ripea.persistence.repository.MetaDocumentRepository;
import es.caib.ripea.persistence.repository.MetaExpedientRepository;
import es.caib.ripea.persistence.repository.MetaExpedientTascaValidacioRepository;
import es.caib.ripea.persistence.repository.PinbalServeiRepository;
import es.caib.ripea.persistence.repository.UsuariRepository;
import es.caib.ripea.service.intf.dto.DocumentNtiEstadoElaboracionEnumDto;
import es.caib.ripea.service.intf.dto.ExpedientEstatEnumDto;
import es.caib.ripea.service.intf.dto.ItemValidacioTascaEnum;
import es.caib.ripea.service.intf.dto.LogObjecteTipusEnumDto;
import es.caib.ripea.service.intf.dto.LogTipusEnumDto;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.dto.MetaDocumentDto;
import es.caib.ripea.service.intf.dto.MetaDocumentPerDefecteEnumDto;
import es.caib.ripea.service.intf.dto.MultiplicitatEnumDto;
import es.caib.ripea.service.intf.dto.NtiOrigenEnumDto;
import es.caib.ripea.service.intf.dto.PortafirmesFluxInfoDto;
import es.caib.ripea.service.intf.exception.ExisteixenDocumentsException;
import es.caib.ripea.service.intf.exception.PermissionDeniedException;

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
    @Mock private ValidacioCacheEvictHelper validacioCacheEvictHelper;
    @Mock private ConfigHelper configHelper;

    @InjectMocks
    private MetaDocumentHelper helper;

    private static final Long ENTITAT_ID        = 1L;
    private static final Long META_EXPEDIENT_ID = 10L;
    private static final Long META_DOCUMENT_ID  = 20L;
    private static final Long ORGAN_ID          = 30L;

    private static final String TEST_USER = "usuari1";

    @BeforeEach
    void configurar() {
        io.micrometer.core.instrument.MeterRegistry meterRegistry =
                new io.micrometer.core.instrument.simple.SimpleMeterRegistry();
        when(applicationHelper.getMeterRegistry()).thenReturn(meterRegistry);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(TEST_USER, "pass", Collections.emptyList()));
    }

    @AfterEach
    void netejarSecurityContext() {
        SecurityContextHolder.clearContext();
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

        verify(validacioCacheEvictHelper, never()).evictValidacioExpedientsPerMetaExpedientEnBackground(any(), any());
    }

    @Test
    void evictErrorsValidacioAndNotify_ambMetaExpedientId_evictaExpedientsEnBackground() {
        helper.evictErrorsValidacioAndNotify(ENTITAT_ID, META_EXPEDIENT_ID, true);

        verify(validacioCacheEvictHelper).evictValidacioExpedientsPerMetaExpedientEnBackground(
                META_EXPEDIENT_ID, ExpedientEstatEnumDto.OBERT);
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

        verify(validacioCacheEvictHelper, never()).evictValidacioExpedientsPerMetaExpedientEnBackground(any(), any());
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

        helper.update(META_EXPEDIENT_ID, buildMetaDocumentDto(MultiplicitatEnumDto.M_0_N), null, null, null);

        verify(validacioCacheEvictHelper).evictValidacioExpedientsPerMetaExpedientEnBackground(
                META_EXPEDIENT_ID, ExpedientEstatEnumDto.OBERT);
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
    // crearMetaDocumentsPerDefecte
    // =========================================================================

    @Test
    void crearMetaDocumentsPerDefecte_procedimentSenseTipusDocument_elsCreaTots() {
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        when(metaExpedient.getId()).thenReturn(META_EXPEDIENT_ID);
        when(metaDocumentRepository.findByMetaExpedientAndCodi(any(), any())).thenReturn(null);
        when(metaDocumentRepository.countByMetaExpedient(metaExpedient)).thenReturn(0, 1, 2, 3);
        when(metaDocumentRepository.save(any(MetaDocumentEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        List<MetaDocumentEntity> creats = helper.crearMetaDocumentsPerDefecte(metaExpedient);

        assertThat(creats).hasSize(4);

        MetaDocumentEntity justificantRecepcio = creats.get(0);
        assertThat(justificantRecepcio.getCodi()).isEqualTo(MetaDocumentPerDefecteEnumDto.NOTIB_JUSTIFICANT_RECEPCIO.getCodi());
        assertThat(justificantRecepcio.getNom()).isEqualTo("Justificant de recepció de la notificació");
        assertThat(justificantRecepcio.getDescripcio()).isNull();
        assertThat(justificantRecepcio.getNtiTipoDocumental()).isEqualTo("TD09");
        assertThat(justificantRecepcio.getNtiEstadoElaboracion()).isEqualTo(DocumentNtiEstadoElaboracionEnumDto.EE01);
        assertThat(justificantRecepcio.getOrdre()).isZero();

        MetaDocumentEntity justificantEntrada = creats.get(1);
        assertThat(justificantEntrada.getCodi()).isEqualTo(MetaDocumentPerDefecteEnumDto.REGISTRE_JUSTIFICANT_ENTRADA.getCodi());
        assertThat(justificantEntrada.getNom()).isEqualTo("Justificant de registre");
        assertThat(justificantEntrada.getDescripcio()).isNull();
        assertThat(justificantEntrada.getNtiTipoDocumental()).isEqualTo("TD11");
        assertThat(justificantEntrada.getNtiEstadoElaboracion()).isEqualTo(DocumentNtiEstadoElaboracionEnumDto.EE01);
        assertThat(justificantEntrada.getOrdre()).isEqualTo(1);

        MetaDocumentEntity notificacioMultiple = creats.get(2);
        assertThat(notificacioMultiple.getCodi()).isEqualTo(MetaDocumentPerDefecteEnumDto.NOTIFICACIO_MULTIPLE.getCodi());
        assertThat(notificacioMultiple.getNom()).isEqualTo("Notificació de múltiples documents");
        assertThat(notificacioMultiple.getDescripcio())
                .isEqualTo(MetaDocumentPerDefecteEnumDto.NOTIFICACIO_MULTIPLE.getDescripcio());
        assertThat(notificacioMultiple.getNtiTipoDocumental()).isEqualTo("TD07");
        assertThat(notificacioMultiple.getNtiEstadoElaboracion()).isEqualTo(DocumentNtiEstadoElaboracionEnumDto.EE99);
        assertThat(notificacioMultiple.getOrdre()).isEqualTo(2);

        MetaDocumentEntity otros = creats.get(3);
        assertThat(otros.getCodi()).isEqualTo(MetaDocumentPerDefecteEnumDto.OTROS.getCodi());
        assertThat(otros.getNom()).isEqualTo("Otros");
        assertThat(otros.getDescripcio()).isEqualTo("Altres documents del procediment");
        assertThat(otros.getNtiTipoDocumental()).isEqualTo("TD99");
        assertThat(otros.getNtiEstadoElaboracion()).isEqualTo(DocumentNtiEstadoElaboracionEnumDto.EE01);
        assertThat(otros.getOrdre()).isEqualTo(3);

        // Nomes OTROS queda marcat com a tipus de document per defecte del procediment.
        assertThat(otros.isPerDefecte()).isTrue();
        assertThat(justificantRecepcio.isPerDefecte()).isFalse();
        assertThat(justificantEntrada.isPerDefecte()).isFalse();
        assertThat(notificacioMultiple.isPerDefecte()).isFalse();

        assertThat(creats).allSatisfy(metaDocument -> {
            assertThat(metaDocument.getMultiplicitat()).isEqualTo(MultiplicitatEnumDto.M_0_N);
            assertThat(metaDocument.getNtiOrigen()).isEqualTo(NtiOrigenEnumDto.O1);
            assertThat(metaDocument.getMetaExpedient()).isSameAs(metaExpedient);
        });
    }

    @Test
    void crearMetaDocumentsPerDefecte_quanJaHiHaTipusPerDefecte_noElSubstitueix() {
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        when(metaExpedient.getId()).thenReturn(META_EXPEDIENT_ID);
        when(metaDocumentRepository.findByMetaExpedientAndCodi(any(), any())).thenReturn(null);
        when(metaDocumentRepository.findByMetaExpedientAndPerDefecteTrue(metaExpedient))
                .thenReturn(mock(MetaDocumentEntity.class));
        when(metaDocumentRepository.save(any(MetaDocumentEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        List<MetaDocumentEntity> creats = helper.crearMetaDocumentsPerDefecte(metaExpedient);

        assertThat(creats).allSatisfy(metaDocument -> assertThat(metaDocument.isPerDefecte()).isFalse());
    }

    @Test
    void crearMetaDocumentsPerDefecte_quanJaExisteixen_noEnCreaCapDeNou() {
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        when(metaExpedient.getId()).thenReturn(META_EXPEDIENT_ID);
        when(metaDocumentRepository.findByMetaExpedientAndCodi(any(), any()))
                .thenReturn(mock(MetaDocumentEntity.class));

        List<MetaDocumentEntity> creats = helper.crearMetaDocumentsPerDefecte(metaExpedient);

        assertThat(creats).isEmpty();
        verify(metaDocumentRepository, never()).save(any(MetaDocumentEntity.class));
    }

    @Test
    void crearMetaDocumentsPerDefecte_quanNomesNhiHaUn_creaNomesElQueFalta() {
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        when(metaExpedient.getId()).thenReturn(META_EXPEDIENT_ID);
        when(metaDocumentRepository.findByMetaExpedientAndCodi(any(), any()))
                .thenReturn(mock(MetaDocumentEntity.class));
        when(metaDocumentRepository.findByMetaExpedientAndCodi(
                metaExpedient, MetaDocumentPerDefecteEnumDto.REGISTRE_JUSTIFICANT_ENTRADA.getCodi()))
                .thenReturn(null);
        when(metaDocumentRepository.countByMetaExpedient(metaExpedient)).thenReturn(3);
        when(metaDocumentRepository.save(any(MetaDocumentEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        List<MetaDocumentEntity> creats = helper.crearMetaDocumentsPerDefecte(metaExpedient);

        assertThat(creats).hasSize(1);
        assertThat(creats.get(0).getCodi()).isEqualTo(MetaDocumentPerDefecteEnumDto.REGISTRE_JUSTIFICANT_ENTRADA.getCodi());
        assertThat(creats.get(0).getOrdre()).isEqualTo(3);
        verify(metaDocumentRepository).save(any(MetaDocumentEntity.class));
    }

    // =========================================================================
    // getOrCreateMetaDocumentPerDefecte
    // =========================================================================

    @Test
    void getOrCreateMetaDocumentPerDefecte_quanElProcedimentJaElTe_elRetornaSenseCrearlo() {
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        MetaDocumentEntity existent = mock(MetaDocumentEntity.class);
        when(metaDocumentRepository.findByMetaExpedientAndCodi(
                metaExpedient, MetaDocumentPerDefecteEnumDto.NOTIB_JUSTIFICANT_RECEPCIO.getCodi()))
                .thenReturn(existent);

        MetaDocumentEntity resultat = helper.getOrCreateMetaDocumentPerDefecte(
                metaExpedient, MetaDocumentPerDefecteEnumDto.NOTIB_JUSTIFICANT_RECEPCIO);

        assertThat(resultat).isSameAs(existent);
        verify(metaDocumentRepository, never()).save(any(MetaDocumentEntity.class));
    }

    @Test
    void getOrCreateMetaDocumentPerDefecte_quanNoHiEs_elCrea() {
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        when(metaExpedient.getId()).thenReturn(META_EXPEDIENT_ID);
        when(metaDocumentRepository.findByMetaExpedientAndCodi(any(), any())).thenReturn(null);
        when(metaDocumentRepository.countByMetaExpedient(metaExpedient)).thenReturn(5);
        when(metaDocumentRepository.save(any(MetaDocumentEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        MetaDocumentEntity resultat = helper.getOrCreateMetaDocumentPerDefecte(
                metaExpedient, MetaDocumentPerDefecteEnumDto.REGISTRE_JUSTIFICANT_ENTRADA);

        assertThat(resultat).isNotNull();
        assertThat(resultat.getCodi()).isEqualTo(MetaDocumentPerDefecteEnumDto.REGISTRE_JUSTIFICANT_ENTRADA.getCodi());
        assertThat(resultat.getNtiTipoDocumental()).isEqualTo("TD11");
        assertThat(resultat.getMetaExpedient()).isSameAs(metaExpedient);
        assertThat(resultat.getOrdre()).isEqualTo(5);
        verify(metaDocumentRepository).save(any(MetaDocumentEntity.class));
    }

    // =========================================================================
    // Restricció de modificació dels tipus de document per defecte
    // =========================================================================

    private MetaDocumentEntity mockMetaDocument(String codi) {
        MetaDocumentEntity metaDocument = mock(MetaDocumentEntity.class);
        when(metaDocument.getId()).thenReturn(META_DOCUMENT_ID);
        when(metaDocument.getCodi()).thenReturn(codi);
        when(metaDocumentRepository.findById(META_DOCUMENT_ID)).thenReturn(Optional.of(metaDocument));
        return metaDocument;
    }

    // Reordenar no és una modificació restringida: només canvia l'ordre de presentació.
    @Test
    void moveTo_metaDocumentPerDefecteISenseRolAdminEntitat_permetElCanvi() {
        MetaDocumentEntity metaDocument =
                mockMetaDocument(MetaDocumentPerDefecteEnumDto.NOTIB_JUSTIFICANT_RECEPCIO.getCodi());
        when(configHelper.getRolActual()).thenReturn(BaseConfig.ROLE_ORGAN_ADMIN);
        when(metaDocumentRepository.findByMetaExpedientOrderByOrdreAsc(any()))
                .thenReturn(new ArrayList<>(Arrays.asList(metaDocument)));

        helper.moveTo(META_DOCUMENT_ID, 0);

        verify(metaDocument).updateOrdre(0);
    }

    @Test
    void delete_metaDocumentPerDefecteISenseRolAdminEntitat_llancaPermissionDenied() {
        mockMetaDocument(MetaDocumentPerDefecteEnumDto.NOTIB_JUSTIFICANT_RECEPCIO.getCodi());
        when(entityComprovarHelper.comprovarEntitat(ENTITAT_ID, false, false, false, false, true))
                .thenReturn(mock(EntitatEntity.class));
        when(configHelper.getRolActual()).thenReturn(BaseConfig.ROLE_DISSENY);

        assertThatThrownBy(() -> helper.delete(
                ENTITAT_ID, null, META_DOCUMENT_ID, BaseConfig.ROLE_DISSENY, ORGAN_ID))
                .isInstanceOf(PermissionDeniedException.class);

        verify(metaDocumentRepository, never()).delete(any(MetaDocumentEntity.class));
    }

    @Test
    void updateActiu_metaDocumentPerDefecteISenseRolAdminEntitat_llancaPermissionDenied() {
        MetaDocumentEntity metaDocument =
                mockMetaDocument(MetaDocumentPerDefecteEnumDto.REGISTRE_JUSTIFICANT_ENTRADA.getCodi());
        when(entityComprovarHelper.comprovarEntitat(ENTITAT_ID, false, false, false, false, true))
                .thenReturn(mock(EntitatEntity.class));
        when(configHelper.getRolActual()).thenReturn(BaseConfig.ROLE_ORGAN_ADMIN);

        assertThatThrownBy(() -> helper.updateActiu(
                ENTITAT_ID, null, META_DOCUMENT_ID, false, BaseConfig.ROLE_ORGAN_ADMIN))
                .isInstanceOf(PermissionDeniedException.class);

        verify(metaDocument, never()).updateActiu(anyBoolean());
    }

    // =========================================================================
    // comprovarPermisModificacioMetaDades
    // =========================================================================

    @Test
    void comprovarPermisModificacioMetaDades_metaDocumentPerDefecteISenseRolAdminEntitat_llancaPermissionDenied() {
        MetaDocumentEntity metaDocument = mock(MetaDocumentEntity.class);
        when(metaDocument.getCodi()).thenReturn(MetaDocumentPerDefecteEnumDto.NOTIB_JUSTIFICANT_RECEPCIO.getCodi());
        when(configHelper.getRolActual()).thenReturn(BaseConfig.ROLE_DISSENY);

        assertThatThrownBy(() -> helper.comprovarPermisModificacioMetaDades(metaDocument))
                .isInstanceOf(PermissionDeniedException.class);
    }

    @Test
    void comprovarPermisModificacioMetaDades_metaDocumentPerDefecteIRolAdminEntitat_noLlancaRes() {
        MetaDocumentEntity metaDocument = mock(MetaDocumentEntity.class);
        when(metaDocument.getCodi()).thenReturn(MetaDocumentPerDefecteEnumDto.REGISTRE_JUSTIFICANT_ENTRADA.getCodi());
        when(configHelper.getRolActual()).thenReturn(BaseConfig.ROLE_ADMIN);

        assertThatCode(() -> helper.comprovarPermisModificacioMetaDades(metaDocument)).doesNotThrowAnyException();
    }

    @Test
    void comprovarPermisModificacioMetaDades_metaDocumentNormal_noLlancaRes() {
        MetaDocumentEntity metaDocument = mock(MetaDocumentEntity.class);
        when(metaDocument.getCodi()).thenReturn("DOC_QUALSEVOL");
        when(configHelper.getRolActual()).thenReturn(BaseConfig.ROLE_DISSENY);

        assertThatCode(() -> helper.comprovarPermisModificacioMetaDades(metaDocument)).doesNotThrowAnyException();
    }

    // Les metadades del procediment no tenen cap restricció addicional.
    @Test
    void comprovarPermisModificacioMetaDades_metaExpedient_noLlancaRes() {
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        when(configHelper.getRolActual()).thenReturn(BaseConfig.ROLE_DISSENY);

        assertThatCode(() -> helper.comprovarPermisModificacioMetaDades(metaExpedient)).doesNotThrowAnyException();
    }

    // =========================================================================
    // findMetaDocumentsDisponiblesPerCreacio
    // =========================================================================

    @Test
    void findMetaDocumentsDisponiblesPerCreacio_senseExpedient_retornaMetadocsDelProcediment() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        MetaDocumentEntity doc = mock(MetaDocumentEntity.class);
        when(metaDocumentRepository.findByMetaExpedientAndActiuTrue(metaExpedient))
                .thenReturn(Collections.singletonList(doc));

        List<MetaDocumentEntity> resultat = helper.findMetaDocumentsDisponiblesPerCreacio(
                entitat, null, metaExpedient, false);

        assertThat(resultat).containsExactly(doc);
    }

    @Test
    void findMetaDocumentsDisponiblesPerCreacio_ambExpedient_metaDocM1JaExisteix_noEsAfegit() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        MetaDocumentEntity metaDoc = mock(MetaDocumentEntity.class);
        DocumentEntity docExistent = mock(DocumentEntity.class);

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

    // =========================================================================
    // updateFluxos (via update) — flux no existent, entra al if
    // =========================================================================

    private MetaDocumentDto buildMetaDocumentDtoAmbFlux(MultiplicitatEnumDto multiplicitat, String... fluxIds) {
        MetaDocumentDto dto = new MetaDocumentDto();
        dto.setId(META_DOCUMENT_ID);
        dto.setCodi("COD");
        dto.setNom("Nom");
        dto.setMultiplicitat(multiplicitat);
        dto.setPortafirmesFluxosId(fluxIds);
        return dto;
    }

    @Test
    void update_ambFluxNou_desaElFluxIActualitzaDesc() throws Exception {
        MetaDocumentEntity metaDocument = mock(MetaDocumentEntity.class);
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        when(metaDocument.getMultiplicitat()).thenReturn(MultiplicitatEnumDto.M_1);
        when(metaDocument.getMetaExpedient()).thenReturn(metaExpedient);
        when(metaDocument.getEntitat()).thenReturn(mock(EntitatEntity.class));
        when(metaDocument.getFluxosFirma()).thenReturn(new ArrayList<>());
        when(metaDocument.fluxeExistById("FLUX_01")).thenReturn(false);
        when(metaDocumentRepository.findById(META_DOCUMENT_ID)).thenReturn(Optional.of(metaDocument));
        when(metaExpedient.getId()).thenReturn(META_EXPEDIENT_ID);

        UsuariEntity usuari = mock(UsuariEntity.class);
        when(usuari.getIdioma()).thenReturn("ca");
        when(usuariRepository.findById(TEST_USER)).thenReturn(Optional.of(usuari));

        when(metaDocumentFluxPortafibRepository.findByMetaDocumentIsNullAndPortafirmesFluxId("FLUX_01"))
                .thenReturn(null);

        PortafirmesFluxInfoDto fluxInfo = new PortafirmesFluxInfoDto();
        fluxInfo.setNom("Flux de prova");
        when(pluginHelper.portafirmesRecuperarInfoFluxDeFirma("FLUX_01", "ca", false))
                .thenReturn(fluxInfo);

        helper.update(META_EXPEDIENT_ID, buildMetaDocumentDtoAmbFlux(MultiplicitatEnumDto.M_1, "FLUX_01"), null, null, null);

        verify(metaDocumentFluxPortafibRepository).save(any(MetaDocumentFluxPortafibEntity.class));
    }

    @Test
    void update_ambFluxJaExistent_noTornaADesar() throws Exception {
        MetaDocumentEntity metaDocument = mock(MetaDocumentEntity.class);
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        when(metaDocument.getMultiplicitat()).thenReturn(MultiplicitatEnumDto.M_1);
        when(metaDocument.getMetaExpedient()).thenReturn(metaExpedient);
        when(metaDocument.getEntitat()).thenReturn(mock(EntitatEntity.class));
        when(metaDocument.getFluxosFirma()).thenReturn(new ArrayList<>());
        when(metaDocument.fluxeExistById("FLUX_01")).thenReturn(true);
        when(metaDocumentRepository.findById(META_DOCUMENT_ID)).thenReturn(Optional.of(metaDocument));
        when(metaExpedient.getId()).thenReturn(META_EXPEDIENT_ID);

        helper.update(META_EXPEDIENT_ID, buildMetaDocumentDtoAmbFlux(MultiplicitatEnumDto.M_1, "FLUX_01"), null, null, null);

        verify(metaDocumentFluxPortafibRepository, never()).save(any());
    }

    @Test
    void update_ambFluxNouIOrfeExistent_reutilitzaFluxOrfe() throws Exception {
        MetaDocumentEntity metaDocument = mock(MetaDocumentEntity.class);
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        when(metaDocument.getMultiplicitat()).thenReturn(MultiplicitatEnumDto.M_1);
        when(metaDocument.getMetaExpedient()).thenReturn(metaExpedient);
        when(metaDocument.getEntitat()).thenReturn(mock(EntitatEntity.class));
        when(metaDocument.getFluxosFirma()).thenReturn(new ArrayList<>());
        when(metaDocument.fluxeExistById("FLUX_ORFE")).thenReturn(false);
        when(metaDocumentRepository.findById(META_DOCUMENT_ID)).thenReturn(Optional.of(metaDocument));
        when(metaExpedient.getId()).thenReturn(META_EXPEDIENT_ID);

        MetaDocumentFluxPortafibEntity fluxOrfe = new MetaDocumentFluxPortafibEntity();
        fluxOrfe.setPortafirmesFluxId("FLUX_ORFE");
        when(metaDocumentFluxPortafibRepository.findByMetaDocumentIsNullAndPortafirmesFluxId("FLUX_ORFE"))
                .thenReturn(fluxOrfe);

        UsuariEntity usuari = mock(UsuariEntity.class);
        when(usuari.getIdioma()).thenReturn("ca");
        when(usuariRepository.findById(TEST_USER)).thenReturn(Optional.of(usuari));

        PortafirmesFluxInfoDto fluxInfo = new PortafirmesFluxInfoDto();
        fluxInfo.setNom("Flux Orfe");
        when(pluginHelper.portafirmesRecuperarInfoFluxDeFirma("FLUX_ORFE", "ca", false))
                .thenReturn(fluxInfo);

        helper.update(META_EXPEDIENT_ID, buildMetaDocumentDtoAmbFlux(MultiplicitatEnumDto.M_1, "FLUX_ORFE"), null, null, null);

        verify(metaDocumentFluxPortafibRepository).save(fluxOrfe);
        assertThat(fluxOrfe.getMetaDocument()).isSameAs(metaDocument);
    }

    // =========================================================================
    // initMetaDocumentFlux
    // =========================================================================

    @Test
    void initMetaDocumentFlux_senseFluxos_retornaHtmlNoHiHaFluxos() throws Exception {
        when(metaDocumentFluxPortafibRepository.findAll()).thenReturn(Collections.emptyList());
        when(fluxFirmaUsuariRepository.findAll()).thenReturn(Collections.emptyList());

        String html = helper.initMetaDocumentFlux();

        assertThat(html).contains("No hi ha fluxos a actualitzar");
        assertThat(html).contains("No hi ha fluxos d'usuari a actualitzar");
    }

    @Test
    void initMetaDocumentFlux_ambFluxMetaDocOk_retornaHtmlOk() throws Exception {
        MetaDocumentFluxPortafibEntity flux = mock(MetaDocumentFluxPortafibEntity.class);
        when(flux.getPortafirmesFluxId()).thenReturn("FLUX_01");
        when(flux.getPortafirmesFluxDesc()).thenReturn("Nom Antic");
        when(metaDocumentFluxPortafibRepository.findAll()).thenReturn(Collections.singletonList(flux));
        when(fluxFirmaUsuariRepository.findAll()).thenReturn(Collections.emptyList());

        PortafirmesFluxInfoDto fluxInfo = new PortafirmesFluxInfoDto();
        fluxInfo.setNom("Nom Nou");
        when(pluginHelper.portafirmesRecuperarInfoFluxDeFirma("FLUX_01", "ca", false))
                .thenReturn(fluxInfo);

        String html = helper.initMetaDocumentFlux();

        assertThat(html).contains("OK");
        assertThat(html).contains("FLUX_01");
        verify(flux).setPortafirmesFluxDesc("Nom Nou");
    }

    @Test
    void initMetaDocumentFlux_ambFluxMetaDocRetornaNul_retornaHtmlNoTrobat() throws Exception {
        MetaDocumentFluxPortafibEntity flux = mock(MetaDocumentFluxPortafibEntity.class);
        when(flux.getPortafirmesFluxId()).thenReturn("FLUX_02");
        when(flux.getPortafirmesFluxDesc()).thenReturn("Nom Antic 2");
        when(metaDocumentFluxPortafibRepository.findAll()).thenReturn(Collections.singletonList(flux));
        when(fluxFirmaUsuariRepository.findAll()).thenReturn(Collections.emptyList());

        when(pluginHelper.portafirmesRecuperarInfoFluxDeFirma("FLUX_02", "ca", false))
                .thenReturn(null);

        String html = helper.initMetaDocumentFlux();

        assertThat(html).contains("NO TROBAT");
        assertThat(html).contains("FLUX_02");
    }

    @Test
    void initMetaDocumentFlux_ambFluxMetaDocLlencaExcepcio_retornaHtmlError() throws Exception {
        MetaDocumentFluxPortafibEntity flux = mock(MetaDocumentFluxPortafibEntity.class);
        when(flux.getPortafirmesFluxId()).thenReturn("FLUX_03");
        when(flux.getPortafirmesFluxDesc()).thenReturn("Nom Antic 3");
        when(metaDocumentFluxPortafibRepository.findAll()).thenReturn(Collections.singletonList(flux));
        when(fluxFirmaUsuariRepository.findAll()).thenReturn(Collections.emptyList());

        when(pluginHelper.portafirmesRecuperarInfoFluxDeFirma("FLUX_03", "ca", false))
                .thenThrow(new RuntimeException("connexió fallida"));

        String html = helper.initMetaDocumentFlux();

        assertThat(html).contains("ERROR");
        assertThat(html).contains("FLUX_03");
    }

    @Test
    void initMetaDocumentFlux_ambFluxUsuariOk_retornaHtmlOkUsuari() throws Exception {
        when(metaDocumentFluxPortafibRepository.findAll()).thenReturn(Collections.emptyList());

        FluxFirmaUsuariEntity fluxUsuari = mock(FluxFirmaUsuariEntity.class);
        when(fluxUsuari.getPortafirmesFluxId()).thenReturn("FLUX_USR_01");
        when(fluxUsuari.getNom()).thenReturn("Flux Usuari Antic");
        when(fluxFirmaUsuariRepository.findAll()).thenReturn(Collections.singletonList(fluxUsuari));

        PortafirmesFluxInfoDto fluxInfo = new PortafirmesFluxInfoDto();
        fluxInfo.setNom("Flux Usuari Nou");
        fluxInfo.setDescripcio("Desc");
        when(pluginHelper.portafirmesRecuperarInfoFluxDeFirma("FLUX_USR_01", "ca", false))
                .thenReturn(fluxInfo);

        String html = helper.initMetaDocumentFlux();

        assertThat(html).contains("OK");
        assertThat(html).contains("FLUX_USR_01");
        verify(fluxUsuari).updateNomDescripcio("Flux Usuari Nou", "Desc");
    }

    // =========================================================================
    // findActiusPerCreacio
    // =========================================================================

    @Test
    void findActiusPerCreacio_ambContingutId_retornaMetaDocumentsDisponibles() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        ContingutEntity contingut = mock(ContingutEntity.class);
        ExpedientEntity expedient = mock(ExpedientEntity.class);
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        MetaDocumentEntity doc = mock(MetaDocumentEntity.class);

        when(entityComprovarHelper.comprovarContingut(99L)).thenReturn(contingut);
        when(contingutHelper.getExpedientSuperior(contingut, true, false, false, null)).thenReturn(expedient);
        when(expedient.getMetaExpedient()).thenReturn(metaExpedient);
        when(metaDocumentRepository.findByMetaExpedientAndActiuTrue(metaExpedient))
                .thenReturn(Collections.singletonList(doc));
        when(documentRepository.findByExpedientAndEsborrat(expedient, 0)).thenReturn(Collections.emptyList());

        List<MetaDocumentEntity> resultat = helper.findActiusPerCreacio(entitat, 99L, null, false);

        assertThat(resultat).containsExactly(doc);
        verify(entityComprovarHelper).comprovarContingut(99L);
        verify(contingutHelper).getExpedientSuperior(contingut, true, false, false, null);
    }

    @Test
    void findActiusPerCreacio_senseContingutId_retornaMetaDocumentsDelMetaExpedient() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        MetaDocumentEntity doc = mock(MetaDocumentEntity.class);

        when(metaExpedientRepository.findById(META_EXPEDIENT_ID)).thenReturn(Optional.of(metaExpedient));
        when(metaDocumentRepository.findByMetaExpedientAndActiuTrue(metaExpedient))
                .thenReturn(Collections.singletonList(doc));

        List<MetaDocumentEntity> resultat = helper.findActiusPerCreacio(entitat, null, META_EXPEDIENT_ID, false);

        assertThat(resultat).containsExactly(doc);
        verify(metaExpedientRepository).findById(META_EXPEDIENT_ID);
    }

    // =========================================================================
    // findActiusPerModificacio
    // =========================================================================

    @Test
    void findActiusPerModificacio_retornaMetaDocumentsDisponibles() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        DocumentEntity document = mock(DocumentEntity.class);
        ExpedientEntity expedient = mock(ExpedientEntity.class);
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        MetaDocumentEntity docDisponible = mock(MetaDocumentEntity.class);

        when(entityComprovarHelper.comprovarDocument(entitat, null, 77L, false, false, false, false))
                .thenReturn(document);
        when(contingutHelper.getExpedientSuperior(document, true, false, false, null)).thenReturn(expedient);
        when(expedient.getMetaExpedient()).thenReturn(metaExpedient);
        when(metaDocumentRepository.findByMetaExpedientAndActiuTrue(metaExpedient))
                .thenReturn(new ArrayList<>(Collections.singletonList(docDisponible)));
        when(documentRepository.findByExpedientAndEsborrat(expedient, 0)).thenReturn(Collections.emptyList());
        when(document.getMetaDocument()).thenReturn(docDisponible);
        when(docDisponible.getNom()).thenReturn("Doc A");

        List<MetaDocumentEntity> resultat = helper.findActiusPerModificacio(entitat, 77L);

        assertThat(resultat).contains(docDisponible);
    }

    @Test
    void findActiusPerModificacio_metaDocumentNoDisponible_safegeixAlFinal() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        DocumentEntity document = mock(DocumentEntity.class);
        ExpedientEntity expedient = mock(ExpedientEntity.class);
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        MetaDocumentEntity docDisponible = mock(MetaDocumentEntity.class);
        MetaDocumentEntity docDelDocument = mock(MetaDocumentEntity.class);

        when(entityComprovarHelper.comprovarDocument(entitat, null, 77L, false, false, false, false))
                .thenReturn(document);
        when(contingutHelper.getExpedientSuperior(document, true, false, false, null)).thenReturn(expedient);
        when(expedient.getMetaExpedient()).thenReturn(metaExpedient);
        when(metaDocumentRepository.findByMetaExpedientAndActiuTrue(metaExpedient))
                .thenReturn(new ArrayList<>(Collections.singletonList(docDisponible)));
        when(documentRepository.findByExpedientAndEsborrat(expedient, 0)).thenReturn(Collections.emptyList());
        when(document.getMetaDocument()).thenReturn(docDelDocument);
        when(docDisponible.getNom()).thenReturn("Doc A");
        when(docDelDocument.getNom()).thenReturn("Doc B");

        List<MetaDocumentEntity> resultat = helper.findActiusPerModificacio(entitat, 77L);

        assertThat(resultat).contains(docDisponible, docDelDocument);
    }
}
