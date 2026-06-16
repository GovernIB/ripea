package es.caib.ripea.service.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.MetaDadaEntity;
import es.caib.ripea.persistence.entity.MetaDocumentEntity;
import es.caib.ripea.persistence.entity.MetaExpedientEntity;
import es.caib.ripea.persistence.entity.MetaExpedientTascaValidacioEntity;
import es.caib.ripea.persistence.entity.MetaNodeEntity;
import es.caib.ripea.persistence.repository.MetaDadaRepository;
import es.caib.ripea.persistence.repository.MetaExpedientTascaValidacioRepository;
import es.caib.ripea.service.intf.dto.ExpedientEstatEnumDto;
import es.caib.ripea.service.intf.dto.ItemValidacioTascaEnum;
import es.caib.ripea.service.intf.dto.LogObjecteTipusEnumDto;
import es.caib.ripea.service.intf.dto.LogTipusEnumDto;
import es.caib.ripea.service.intf.dto.MetaDadaDto;
import es.caib.ripea.service.intf.dto.MetaDadaTipusEnumDto;
import es.caib.ripea.service.intf.dto.MultiplicitatEnumDto;

/**
 * Tests unitaris per a MetaDadaHelper.
 *
 * Cobreix tots els mètodes públics amb casos normals i límit.
 * No arrenca cap context Spring: totes les dependències es proporcionen com a mocks de Mockito.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MetaDadaHelperTest {

    // ── Dependències mockejades ──────────────────────────────────────────────

    @Mock private MetaDadaRepository metaDadaRepository;
    @Mock private MetaExpedientTascaValidacioRepository metaExpedientTascaValidacioRepository;
    @Mock private EntityComprovarHelper entityComprovarHelper;
    @Mock private MetaExpedientHelper metaExpedientHelper;
    @Mock private ApplicationHelper applicationHelper;
    @Mock private ValidacioCacheEvictHelper validacioCacheEvictHelper;
    @Mock private ContingutLogHelper contingutLogHelper;

    @InjectMocks
    private MetaDadaHelper helper;

    private static final Long ENTITAT_ID   = 1L;
    private static final Long META_NODE_ID = 10L;
    private static final Long META_DADA_ID = 20L;
    private static final Long ORGAN_ID     = 30L;

    @BeforeEach
    void configurarMeterRegistry() {
        io.micrometer.core.instrument.MeterRegistry meterRegistry =
                new io.micrometer.core.instrument.simple.SimpleMeterRegistry();
        when(applicationHelper.getMeterRegistry()).thenReturn(meterRegistry);
    }

    // =========================================================================
    // findByMetaNodeAndCodi
    // =========================================================================

    @Test
    void findByMetaNodeAndCodi_delegaAlRepositori() {
        MetaNodeEntity metaNode = mock(MetaNodeEntity.class);
        MetaDadaEntity entity = mock(MetaDadaEntity.class);
        when(metaDadaRepository.findByMetaNodeAndCodi(metaNode, "CODI")).thenReturn(entity);

        MetaDadaEntity resultat = helper.findByMetaNodeAndCodi(metaNode, "CODI");

        assertThat(resultat).isSameAs(entity);
    }

    @Test
    void findByMetaNodeAndCodi_quanNoExisteix_retornaNull() {
        MetaNodeEntity metaNode = mock(MetaNodeEntity.class);
        when(metaDadaRepository.findByMetaNodeAndCodi(metaNode, "INEXISTENT")).thenReturn(null);

        assertThat(helper.findByMetaNodeAndCodi(metaNode, "INEXISTENT")).isNull();
    }

    // =========================================================================
    // create
    // =========================================================================

    private MetaDadaDto buildDto(MetaDadaTipusEnumDto tipus) {
        MetaDadaDto dto = new MetaDadaDto();
        dto.setCodi("CODI");
        dto.setNom("Nom");
        dto.setTipus(tipus);
        dto.setMultiplicitat(MultiplicitatEnumDto.M_1);
        dto.setReadOnly(false);
        dto.setNoAplica(false);
        dto.setEnviable(false);
        dto.setMetadadaArxiu(null);
        return dto;
    }

    private void prepararCreate(EntitatEntity entitat, MetaNodeEntity metaNode) {
        when(entityComprovarHelper.comprovarEntitatPerMetaExpedients(ENTITAT_ID)).thenReturn(entitat);
        when(entityComprovarHelper.comprovarMetaNode(entitat, META_NODE_ID)).thenReturn(metaNode);
        when(metaDadaRepository.countByMetaNode(metaNode)).thenReturn(0);
        when(metaDadaRepository.save(any(MetaDadaEntity.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void create_tipusBoolea_desaLaMetaDada() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity metaNode = mock(MetaExpedientEntity.class);
        prepararCreate(entitat, metaNode);
        MetaDadaDto dto = buildDto(MetaDadaTipusEnumDto.BOOLEA);
        dto.setValorBoolea(Boolean.TRUE);

        MetaDadaEntity resultat = helper.create(ENTITAT_ID, META_NODE_ID, dto, "IPA_ADMIN", ORGAN_ID);

        assertThat(resultat).isNotNull();
        verify(metaDadaRepository).save(any(MetaDadaEntity.class));
    }

    @Test
    void create_tipusData_desaLaMetaDada() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity metaNode = mock(MetaExpedientEntity.class);
        prepararCreate(entitat, metaNode);
        MetaDadaDto dto = buildDto(MetaDadaTipusEnumDto.DATA);
        dto.setValorData(new java.util.Date());

        MetaDadaEntity resultat = helper.create(ENTITAT_ID, META_NODE_ID, dto, "IPA_ADMIN", ORGAN_ID);

        assertThat(resultat).isNotNull();
        verify(metaDadaRepository).save(any(MetaDadaEntity.class));
    }

    @Test
    void create_tipusFlotant_desaLaMetaDada() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity metaNode = mock(MetaExpedientEntity.class);
        prepararCreate(entitat, metaNode);
        MetaDadaDto dto = buildDto(MetaDadaTipusEnumDto.FLOTANT);
        dto.setValorFlotant(3.14);

        MetaDadaEntity resultat = helper.create(ENTITAT_ID, META_NODE_ID, dto, "IPA_ADMIN", ORGAN_ID);

        assertThat(resultat).isNotNull();
        verify(metaDadaRepository).save(any(MetaDadaEntity.class));
    }

    @Test
    void create_tipusImport_desaLaMetaDada() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity metaNode = mock(MetaExpedientEntity.class);
        prepararCreate(entitat, metaNode);
        MetaDadaDto dto = buildDto(MetaDadaTipusEnumDto.IMPORT);
        dto.setValorImport(java.math.BigDecimal.TEN);

        MetaDadaEntity resultat = helper.create(ENTITAT_ID, META_NODE_ID, dto, "IPA_ADMIN", ORGAN_ID);

        assertThat(resultat).isNotNull();
        verify(metaDadaRepository).save(any(MetaDadaEntity.class));
    }

    @Test
    void create_tipusSencer_desaLaMetaDada() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity metaNode = mock(MetaExpedientEntity.class);
        prepararCreate(entitat, metaNode);
        MetaDadaDto dto = buildDto(MetaDadaTipusEnumDto.SENCER);
        dto.setValorSencer(42L);

        MetaDadaEntity resultat = helper.create(ENTITAT_ID, META_NODE_ID, dto, "IPA_ADMIN", ORGAN_ID);

        assertThat(resultat).isNotNull();
        verify(metaDadaRepository).save(any(MetaDadaEntity.class));
    }

    @Test
    void create_tipusText_desaLaMetaDada() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity metaNode = mock(MetaExpedientEntity.class);
        prepararCreate(entitat, metaNode);
        MetaDadaDto dto = buildDto(MetaDadaTipusEnumDto.TEXT);
        dto.setValorString("valor text");

        MetaDadaEntity resultat = helper.create(ENTITAT_ID, META_NODE_ID, dto, "IPA_ADMIN", ORGAN_ID);

        assertThat(resultat).isNotNull();
        verify(metaDadaRepository).save(any(MetaDadaEntity.class));
    }

    @Test
    void create_tipusDomini_desaLaMetaDada() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity metaNode = mock(MetaExpedientEntity.class);
        prepararCreate(entitat, metaNode);
        MetaDadaDto dto = buildDto(MetaDadaTipusEnumDto.DOMINI);
        dto.setValorString("valor domini");

        MetaDadaEntity resultat = helper.create(ENTITAT_ID, META_NODE_ID, dto, "IPA_ADMIN", ORGAN_ID);

        assertThat(resultat).isNotNull();
        verify(metaDadaRepository).save(any(MetaDadaEntity.class));
    }

    @Test
    void create_rolOrganAdmin_ambMetaExpedient_crideaCanviarRevisio() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity metaNode = mock(MetaExpedientEntity.class);
        when(metaNode.getId()).thenReturn(META_NODE_ID);
        prepararCreate(entitat, metaNode);
        MetaDadaDto dto = buildDto(MetaDadaTipusEnumDto.TEXT);

        helper.create(ENTITAT_ID, META_NODE_ID, dto, "IPA_ORGAN_ADMIN", ORGAN_ID);

        verify(metaExpedientHelper).canviarRevisioADisseny(ENTITAT_ID, META_NODE_ID, ORGAN_ID);
    }

    @Test
    void create_rolOrganAdmin_ambMetaDocument_crideaCanviarRevisio() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaDocumentEntity metaNode = mock(MetaDocumentEntity.class);
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        when(metaExpedient.getId()).thenReturn(META_NODE_ID);
        when(metaNode.getMetaExpedient()).thenReturn(metaExpedient);
        prepararCreate(entitat, metaNode);
        MetaDadaDto dto = buildDto(MetaDadaTipusEnumDto.TEXT);

        helper.create(ENTITAT_ID, META_NODE_ID, dto, "IPA_ORGAN_ADMIN", ORGAN_ID);

        verify(metaExpedientHelper).canviarRevisioADisseny(ENTITAT_ID, META_NODE_ID, ORGAN_ID);
    }

    @Test
    void create_rolNoOrganAdmin_nocrideaCanviarRevisio() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity metaNode = mock(MetaExpedientEntity.class);
        prepararCreate(entitat, metaNode);
        MetaDadaDto dto = buildDto(MetaDadaTipusEnumDto.TEXT);

        helper.create(ENTITAT_ID, META_NODE_ID, dto, "IPA_ADMIN", ORGAN_ID);

        verify(metaExpedientHelper, never()).canviarRevisioADisseny(any(), any(), any());
    }

    @Test
    void create_metaNodeEsMetaExpedient_crideaLogProcedimentObjecte() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity metaNode = mock(MetaExpedientEntity.class);
        when(metaNode.getId()).thenReturn(META_NODE_ID);
        prepararCreate(entitat, metaNode);
        MetaDadaDto dto = buildDto(MetaDadaTipusEnumDto.TEXT);

        helper.create(ENTITAT_ID, META_NODE_ID, dto, "IPA_ADMIN", ORGAN_ID);

        verify(contingutLogHelper).logProcedimentObjecte(
                any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void create_metaNodeEsMetaDocument_nocrideaLogProcedimentObjecte() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaDocumentEntity metaNode = mock(MetaDocumentEntity.class);
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        when(metaNode.getMetaExpedient()).thenReturn(metaExpedient);
        prepararCreate(entitat, metaNode);
        MetaDadaDto dto = buildDto(MetaDadaTipusEnumDto.TEXT);

        helper.create(ENTITAT_ID, META_NODE_ID, dto, "IPA_ADMIN", ORGAN_ID);

        verify(contingutLogHelper, never()).logProcedimentObjecte(
                any(), any(), any(), any(), any(), any(), any());
    }

    // =========================================================================
    // evictValidacionsExpedients(EntitatEntity, MetaNodeEntity, boolean)
    // =========================================================================

    @Test
    void evictValidacionsExpedients_metaNodeNull_noFaRes() {
        EntitatEntity entitat = mock(EntitatEntity.class);

        helper.evictValidacionsExpedients(entitat, null, true);

        verify(validacioCacheEvictHelper, never()).evictValidacioExpedientsPerMetaExpedientEnBackground(any(), any());
        verify(validacioCacheEvictHelper, never()).evictValidacioDocumentsPerMetaDocumentEnBackground(any());
    }

    @Test
    void evictValidacionsExpedients_metaNodeEsMetaDocument_evictaDocumentsEnBackground() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaDocumentEntity metaNode = mock(MetaDocumentEntity.class);
        when(metaNode.getId()).thenReturn(META_NODE_ID);

        helper.evictValidacionsExpedients(entitat, metaNode, true);

        //Pels meta-documents el evict és pels documents, en segon pla, no pels expedients
        verify(validacioCacheEvictHelper).evictValidacioDocumentsPerMetaDocumentEnBackground(META_NODE_ID);
        verify(validacioCacheEvictHelper, never()).evictValidacioExpedientsPerMetaExpedientEnBackground(any(), any());
    }

    @Test
    void evictValidacionsExpedients_metaNodeEsMetaExpedient_evictaExpedientsEnBackground() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity metaNode = mock(MetaExpedientEntity.class);
        when(metaNode.getId()).thenReturn(META_NODE_ID);

        helper.evictValidacionsExpedients(entitat, metaNode, true);

        //Pel meta-expedient el evict és pels expedients oberts, en segon pla
        verify(validacioCacheEvictHelper).evictValidacioExpedientsPerMetaExpedientEnBackground(
                META_NODE_ID, ExpedientEstatEnumDto.OBERT);
        verify(validacioCacheEvictHelper, never()).evictValidacioDocumentsPerMetaDocumentEnBackground(any());
    }

    // =========================================================================
    // update
    // =========================================================================

    private MetaDadaDto buildDtoAmbId(MultiplicitatEnumDto multiplicitat) {
        MetaDadaDto dto = buildDto(MetaDadaTipusEnumDto.TEXT);
        dto.setId(META_DADA_ID);
        dto.setMultiplicitat(multiplicitat);
        return dto;
    }

    @Test
    void update_multiplicitatNoCanvia_noEvictaValidacions() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity metaNode = mock(MetaExpedientEntity.class);
        MetaDadaEntity metaDadaEntity = mock(MetaDadaEntity.class);
        when(metaDadaEntity.getMultiplicitat()).thenReturn(MultiplicitatEnumDto.M_1);
        when(entityComprovarHelper.comprovarEntitatPerMetaExpedients(ENTITAT_ID)).thenReturn(entitat);
        when(entityComprovarHelper.comprovarMetaNode(entitat, META_NODE_ID)).thenReturn(metaNode);
        when(entityComprovarHelper.comprovarMetaDada(entitat, metaNode, META_DADA_ID)).thenReturn(metaDadaEntity);
        MetaDadaDto dto = buildDtoAmbId(MultiplicitatEnumDto.M_1);

        helper.update(ENTITAT_ID, META_NODE_ID, dto, "IPA_ADMIN", ORGAN_ID);

        verify(validacioCacheEvictHelper, never()).evictValidacioExpedientsPerMetaExpedientEnBackground(any(), any());
        verify(validacioCacheEvictHelper, never()).evictValidacioDocumentsPerMetaDocumentEnBackground(any());
    }

    @Test
    void update_multiplicitatCanvia_evictaValidacions() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity metaNode = mock(MetaExpedientEntity.class);
        when(metaNode.getId()).thenReturn(META_NODE_ID);
        MetaDadaEntity metaDadaEntity = mock(MetaDadaEntity.class);
        when(metaDadaEntity.getMultiplicitat()).thenReturn(MultiplicitatEnumDto.M_1);
        when(entityComprovarHelper.comprovarEntitatPerMetaExpedients(ENTITAT_ID)).thenReturn(entitat);
        when(entityComprovarHelper.comprovarMetaNode(entitat, META_NODE_ID)).thenReturn(metaNode);
        when(entityComprovarHelper.comprovarMetaDada(entitat, metaNode, META_DADA_ID)).thenReturn(metaDadaEntity);
        MetaDadaDto dto = buildDtoAmbId(MultiplicitatEnumDto.M_0_N);

        helper.update(ENTITAT_ID, META_NODE_ID, dto, "IPA_ADMIN", ORGAN_ID);

        verify(validacioCacheEvictHelper).evictValidacioExpedientsPerMetaExpedientEnBackground(
                META_NODE_ID, ExpedientEstatEnumDto.OBERT);
    }

    @Test
    void update_rolOrganAdmin_ambMetaExpedient_crideaCanviarRevisio() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity metaNode = mock(MetaExpedientEntity.class);
        when(metaNode.getId()).thenReturn(META_NODE_ID);
        MetaDadaEntity metaDadaEntity = mock(MetaDadaEntity.class);
        when(metaDadaEntity.getMultiplicitat()).thenReturn(MultiplicitatEnumDto.M_1);
        when(entityComprovarHelper.comprovarEntitatPerMetaExpedients(ENTITAT_ID)).thenReturn(entitat);
        when(entityComprovarHelper.comprovarMetaNode(entitat, META_NODE_ID)).thenReturn(metaNode);
        when(entityComprovarHelper.comprovarMetaDada(entitat, metaNode, META_DADA_ID)).thenReturn(metaDadaEntity);
        MetaDadaDto dto = buildDtoAmbId(MultiplicitatEnumDto.M_1);

        helper.update(ENTITAT_ID, META_NODE_ID, dto, "IPA_ORGAN_ADMIN", ORGAN_ID);

        verify(metaExpedientHelper).canviarRevisioADisseny(ENTITAT_ID, META_NODE_ID, ORGAN_ID);
    }

    @Test
    void update_rolOrganAdmin_ambMetaDocument_crideaCanviarRevisio() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaDocumentEntity metaNode = mock(MetaDocumentEntity.class);
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        when(metaExpedient.getId()).thenReturn(META_NODE_ID);
        when(metaNode.getMetaExpedient()).thenReturn(metaExpedient);
        MetaDadaEntity metaDadaEntity = mock(MetaDadaEntity.class);
        when(metaDadaEntity.getMultiplicitat()).thenReturn(MultiplicitatEnumDto.M_1);
        when(entityComprovarHelper.comprovarEntitatPerMetaExpedients(ENTITAT_ID)).thenReturn(entitat);
        when(entityComprovarHelper.comprovarMetaNode(entitat, META_NODE_ID)).thenReturn(metaNode);
        when(entityComprovarHelper.comprovarMetaDada(entitat, metaNode, META_DADA_ID)).thenReturn(metaDadaEntity);
        MetaDadaDto dto = buildDtoAmbId(MultiplicitatEnumDto.M_1);

        helper.update(ENTITAT_ID, META_NODE_ID, dto, "IPA_ORGAN_ADMIN", ORGAN_ID);

        verify(metaExpedientHelper).canviarRevisioADisseny(ENTITAT_ID, META_NODE_ID, ORGAN_ID);
    }

    @Test
    void update_metaNodeEsMetaExpedient_crideaLogProcedimentObjecte() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity metaNode = mock(MetaExpedientEntity.class);
        when(metaNode.getId()).thenReturn(META_NODE_ID);
        MetaDadaEntity metaDadaEntity = mock(MetaDadaEntity.class);
        when(metaDadaEntity.getMultiplicitat()).thenReturn(MultiplicitatEnumDto.M_1);
        when(entityComprovarHelper.comprovarEntitatPerMetaExpedients(ENTITAT_ID)).thenReturn(entitat);
        when(entityComprovarHelper.comprovarMetaNode(entitat, META_NODE_ID)).thenReturn(metaNode);
        when(entityComprovarHelper.comprovarMetaDada(entitat, metaNode, META_DADA_ID)).thenReturn(metaDadaEntity);
        MetaDadaDto dto = buildDtoAmbId(MultiplicitatEnumDto.M_1);

        helper.update(ENTITAT_ID, META_NODE_ID, dto, "IPA_ADMIN", ORGAN_ID);

        verify(contingutLogHelper).logProcedimentObjecte(
                any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void update_retornaLaEntitat() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity metaNode = mock(MetaExpedientEntity.class);
        MetaDadaEntity metaDadaEntity = mock(MetaDadaEntity.class);
        when(metaDadaEntity.getMultiplicitat()).thenReturn(MultiplicitatEnumDto.M_1);
        when(entityComprovarHelper.comprovarEntitatPerMetaExpedients(ENTITAT_ID)).thenReturn(entitat);
        when(entityComprovarHelper.comprovarMetaNode(entitat, META_NODE_ID)).thenReturn(metaNode);
        when(entityComprovarHelper.comprovarMetaDada(entitat, metaNode, META_DADA_ID)).thenReturn(metaDadaEntity);
        MetaDadaDto dto = buildDtoAmbId(MultiplicitatEnumDto.M_1);

        MetaDadaEntity resultat = helper.update(ENTITAT_ID, META_NODE_ID, dto, "IPA_ADMIN", ORGAN_ID);

        assertThat(resultat).isSameAs(metaDadaEntity);
    }

    // =========================================================================
    // delete
    // =========================================================================

    @Test
    void delete_ambValidacions_eliminaValidacionsIEntitat() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity metaNode = mock(MetaExpedientEntity.class);
        MetaDadaEntity metaDadaEntity = mock(MetaDadaEntity.class);
        MetaExpedientTascaValidacioEntity validacio = mock(MetaExpedientTascaValidacioEntity.class);
        List<MetaExpedientTascaValidacioEntity> validacions = Collections.singletonList(validacio);
        when(entityComprovarHelper.comprovarEntitatPerMetaExpedients(ENTITAT_ID)).thenReturn(entitat);
        when(entityComprovarHelper.comprovarMetaNode(entitat, META_NODE_ID)).thenReturn(metaNode);
        when(entityComprovarHelper.comprovarMetaDada(entitat, metaNode, META_DADA_ID)).thenReturn(metaDadaEntity);
        when(metaExpedientTascaValidacioRepository.findByItemValidacioAndItemId(
                ItemValidacioTascaEnum.DADA, META_DADA_ID)).thenReturn(validacions);

        helper.delete(ENTITAT_ID, META_NODE_ID, META_DADA_ID, "IPA_ADMIN", ORGAN_ID);

        verify(metaExpedientTascaValidacioRepository).deleteAll(validacions);
        verify(metaDadaRepository).delete(metaDadaEntity);
    }

    @Test
    void delete_senseValidacions_nocrideaDeleteAll() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity metaNode = mock(MetaExpedientEntity.class);
        MetaDadaEntity metaDadaEntity = mock(MetaDadaEntity.class);
        when(entityComprovarHelper.comprovarEntitatPerMetaExpedients(ENTITAT_ID)).thenReturn(entitat);
        when(entityComprovarHelper.comprovarMetaNode(entitat, META_NODE_ID)).thenReturn(metaNode);
        when(entityComprovarHelper.comprovarMetaDada(entitat, metaNode, META_DADA_ID)).thenReturn(metaDadaEntity);
        when(metaExpedientTascaValidacioRepository.findByItemValidacioAndItemId(
                ItemValidacioTascaEnum.DADA, META_DADA_ID)).thenReturn(Collections.emptyList());

        helper.delete(ENTITAT_ID, META_NODE_ID, META_DADA_ID, "IPA_ADMIN", ORGAN_ID);

        verify(metaExpedientTascaValidacioRepository, never()).deleteAll(any(List.class));
        verify(metaDadaRepository).delete(metaDadaEntity);
    }

    @Test
    void delete_rolOrganAdmin_crideaCanviarRevisio() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity metaNode = mock(MetaExpedientEntity.class);
        when(metaNode.getId()).thenReturn(META_NODE_ID);
        MetaDadaEntity metaDadaEntity = mock(MetaDadaEntity.class);
        when(entityComprovarHelper.comprovarEntitatPerMetaExpedients(ENTITAT_ID)).thenReturn(entitat);
        when(entityComprovarHelper.comprovarMetaNode(entitat, META_NODE_ID)).thenReturn(metaNode);
        when(entityComprovarHelper.comprovarMetaDada(entitat, metaNode, META_DADA_ID)).thenReturn(metaDadaEntity);
        when(metaExpedientTascaValidacioRepository.findByItemValidacioAndItemId(any(), any()))
                .thenReturn(Collections.emptyList());

        helper.delete(ENTITAT_ID, META_NODE_ID, META_DADA_ID, "IPA_ORGAN_ADMIN", ORGAN_ID);

        verify(metaExpedientHelper).canviarRevisioADisseny(ENTITAT_ID, META_NODE_ID, ORGAN_ID);
    }

    @Test
    void delete_metaNodeEsMetaExpedient_crideaLogEliminacio() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity metaNode = mock(MetaExpedientEntity.class);
        when(metaNode.getId()).thenReturn(META_NODE_ID);
        MetaDadaEntity metaDadaEntity = mock(MetaDadaEntity.class);
        when(entityComprovarHelper.comprovarEntitatPerMetaExpedients(ENTITAT_ID)).thenReturn(entitat);
        when(entityComprovarHelper.comprovarMetaNode(entitat, META_NODE_ID)).thenReturn(metaNode);
        when(entityComprovarHelper.comprovarMetaDada(entitat, metaNode, META_DADA_ID)).thenReturn(metaDadaEntity);
        when(metaExpedientTascaValidacioRepository.findByItemValidacioAndItemId(any(), any()))
                .thenReturn(Collections.emptyList());

        helper.delete(ENTITAT_ID, META_NODE_ID, META_DADA_ID, "IPA_ADMIN", ORGAN_ID);

        verify(contingutLogHelper).logProcedimentObjecte(
                any(),
                org.mockito.ArgumentMatchers.eq(LogTipusEnumDto.MODIFICACIO),
                org.mockito.ArgumentMatchers.isNull(),
                org.mockito.ArgumentMatchers.eq(LogObjecteTipusEnumDto.METADADA),
                org.mockito.ArgumentMatchers.eq(LogTipusEnumDto.ELIMINACIO),
                any(), any());
    }

    @Test
    void delete_retornaLaEntitat() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity metaNode = mock(MetaExpedientEntity.class);
        MetaDadaEntity metaDadaEntity = mock(MetaDadaEntity.class);
        when(entityComprovarHelper.comprovarEntitatPerMetaExpedients(ENTITAT_ID)).thenReturn(entitat);
        when(entityComprovarHelper.comprovarMetaNode(entitat, META_NODE_ID)).thenReturn(metaNode);
        when(entityComprovarHelper.comprovarMetaDada(entitat, metaNode, META_DADA_ID)).thenReturn(metaDadaEntity);
        when(metaExpedientTascaValidacioRepository.findByItemValidacioAndItemId(any(), any()))
                .thenReturn(Collections.emptyList());

        MetaDadaEntity resultat = helper.delete(ENTITAT_ID, META_NODE_ID, META_DADA_ID, "IPA_ADMIN", ORGAN_ID);

        assertThat(resultat).isSameAs(metaDadaEntity);
    }

    // =========================================================================
    // updateActiva
    // =========================================================================

    @Test
    void updateActiva_crideaUpdateActivaALaEntitat() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity metaNode = mock(MetaExpedientEntity.class);
        MetaDadaEntity metaDadaEntity = mock(MetaDadaEntity.class);
        when(entityComprovarHelper.comprovarEntitatPerMetaExpedients(ENTITAT_ID)).thenReturn(entitat);
        when(entityComprovarHelper.comprovarMetaNode(entitat, META_NODE_ID)).thenReturn(metaNode);
        when(entityComprovarHelper.comprovarMetaDada(entitat, metaNode, META_DADA_ID)).thenReturn(metaDadaEntity);

        helper.updateActiva(ENTITAT_ID, META_NODE_ID, META_DADA_ID, true, "IPA_ADMIN", ORGAN_ID);

        verify(metaDadaEntity).updateActiva(true);
    }

    @Test
    void updateActiva_rolOrganAdmin_crideaCanviarRevisio() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity metaNode = mock(MetaExpedientEntity.class);
        when(metaNode.getId()).thenReturn(META_NODE_ID);
        MetaDadaEntity metaDadaEntity = mock(MetaDadaEntity.class);
        when(entityComprovarHelper.comprovarEntitatPerMetaExpedients(ENTITAT_ID)).thenReturn(entitat);
        when(entityComprovarHelper.comprovarMetaNode(entitat, META_NODE_ID)).thenReturn(metaNode);
        when(entityComprovarHelper.comprovarMetaDada(entitat, metaNode, META_DADA_ID)).thenReturn(metaDadaEntity);

        helper.updateActiva(ENTITAT_ID, META_NODE_ID, META_DADA_ID, true, "IPA_ORGAN_ADMIN", ORGAN_ID);

        verify(metaExpedientHelper).canviarRevisioADisseny(ENTITAT_ID, META_NODE_ID, ORGAN_ID);
    }

    @Test
    void updateActiva_metaNodeEsMetaExpedient_activaTrue_logActivacio() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity metaNode = mock(MetaExpedientEntity.class);
        when(metaNode.getId()).thenReturn(META_NODE_ID);
        MetaDadaEntity metaDadaEntity = mock(MetaDadaEntity.class);
        when(entityComprovarHelper.comprovarEntitatPerMetaExpedients(ENTITAT_ID)).thenReturn(entitat);
        when(entityComprovarHelper.comprovarMetaNode(entitat, META_NODE_ID)).thenReturn(metaNode);
        when(entityComprovarHelper.comprovarMetaDada(entitat, metaNode, META_DADA_ID)).thenReturn(metaDadaEntity);

        helper.updateActiva(ENTITAT_ID, META_NODE_ID, META_DADA_ID, true, "IPA_ADMIN", ORGAN_ID);

        verify(contingutLogHelper).logProcedimentObjecte(
                any(), any(), any(), any(),
                org.mockito.ArgumentMatchers.eq(LogTipusEnumDto.ACTIVACIO),
                any(), any());
    }

    @Test
    void updateActiva_metaNodeEsMetaExpedient_activaFalse_logDesactivacio() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity metaNode = mock(MetaExpedientEntity.class);
        when(metaNode.getId()).thenReturn(META_NODE_ID);
        MetaDadaEntity metaDadaEntity = mock(MetaDadaEntity.class);
        when(entityComprovarHelper.comprovarEntitatPerMetaExpedients(ENTITAT_ID)).thenReturn(entitat);
        when(entityComprovarHelper.comprovarMetaNode(entitat, META_NODE_ID)).thenReturn(metaNode);
        when(entityComprovarHelper.comprovarMetaDada(entitat, metaNode, META_DADA_ID)).thenReturn(metaDadaEntity);

        helper.updateActiva(ENTITAT_ID, META_NODE_ID, META_DADA_ID, false, "IPA_ADMIN", ORGAN_ID);

        verify(contingutLogHelper).logProcedimentObjecte(
                any(), any(), any(), any(),
                org.mockito.ArgumentMatchers.eq(LogTipusEnumDto.DESACTIVACIO),
                any(), any());
    }

    @Test
    void updateActiva_metadadaObligatoria_evictaValidacionsExpedients() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity metaNode = mock(MetaExpedientEntity.class);
        when(metaNode.getId()).thenReturn(META_NODE_ID);
        MetaDadaEntity metaDadaEntity = mock(MetaDadaEntity.class);
        when(metaDadaEntity.getMultiplicitat()).thenReturn(MultiplicitatEnumDto.M_1);
        when(entityComprovarHelper.comprovarEntitatPerMetaExpedients(ENTITAT_ID)).thenReturn(entitat);
        when(entityComprovarHelper.comprovarMetaNode(entitat, META_NODE_ID)).thenReturn(metaNode);
        when(entityComprovarHelper.comprovarMetaDada(entitat, metaNode, META_DADA_ID)).thenReturn(metaDadaEntity);

        helper.updateActiva(ENTITAT_ID, META_NODE_ID, META_DADA_ID, false, "IPA_ADMIN", ORGAN_ID);

        verify(validacioCacheEvictHelper).evictValidacioExpedientsPerMetaExpedientEnBackground(
                META_NODE_ID, ExpedientEstatEnumDto.OBERT);
    }

    @Test
    void updateActiva_metadadaNoObligatoria_noEvictaValidacions() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity metaNode = mock(MetaExpedientEntity.class);
        MetaDadaEntity metaDadaEntity = mock(MetaDadaEntity.class);
        when(metaDadaEntity.getMultiplicitat()).thenReturn(MultiplicitatEnumDto.M_0_N);
        when(entityComprovarHelper.comprovarEntitatPerMetaExpedients(ENTITAT_ID)).thenReturn(entitat);
        when(entityComprovarHelper.comprovarMetaNode(entitat, META_NODE_ID)).thenReturn(metaNode);
        when(entityComprovarHelper.comprovarMetaDada(entitat, metaNode, META_DADA_ID)).thenReturn(metaDadaEntity);

        helper.updateActiva(ENTITAT_ID, META_NODE_ID, META_DADA_ID, false, "IPA_ADMIN", ORGAN_ID);

        verify(validacioCacheEvictHelper, never()).evictValidacioExpedientsPerMetaExpedientEnBackground(any(), any());
        verify(validacioCacheEvictHelper, never()).evictValidacioDocumentsPerMetaDocumentEnBackground(any());
    }

    @Test
    void updateActiva_retornaLaEntitat() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity metaNode = mock(MetaExpedientEntity.class);
        MetaDadaEntity metaDadaEntity = mock(MetaDadaEntity.class);
        when(entityComprovarHelper.comprovarEntitatPerMetaExpedients(ENTITAT_ID)).thenReturn(entitat);
        when(entityComprovarHelper.comprovarMetaNode(entitat, META_NODE_ID)).thenReturn(metaNode);
        when(entityComprovarHelper.comprovarMetaDada(entitat, metaNode, META_DADA_ID)).thenReturn(metaDadaEntity);

        MetaDadaEntity resultat = helper.updateActiva(ENTITAT_ID, META_NODE_ID, META_DADA_ID, true, "IPA_ADMIN", ORGAN_ID);

        assertThat(resultat).isSameAs(metaDadaEntity);
    }
}
