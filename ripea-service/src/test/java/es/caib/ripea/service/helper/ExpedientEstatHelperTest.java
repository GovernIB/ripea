package es.caib.ripea.service.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.ExpedientEntity;
import es.caib.ripea.persistence.entity.ExpedientEstatEntity;
import es.caib.ripea.persistence.entity.MetaExpedientEntity;
import es.caib.ripea.persistence.entity.UsuariEntity;
import es.caib.ripea.persistence.repository.ExpedientEstatRepository;
import es.caib.ripea.persistence.repository.ExpedientRepository;
import es.caib.ripea.service.intf.dto.ExpedientEstatDto;
import es.caib.ripea.service.intf.dto.ExpedientEstatEnumDto;
import es.caib.ripea.service.intf.dto.LogTipusEnumDto;

/**
 * Tests unitaris per a ExpedientEstatHelper.
 *
 * Cobreix tots els mètodes públics amb casos normals i límit.
 * No arrenca cap context Spring: totes les dependències es proporcionen com a mocks de Mockito.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ExpedientEstatHelperTest {

    // ── Dependències mockejades ──────────────────────────────────────────────

    @Mock private ExpedientEstatRepository expedientEstatRepository;
    @Mock private ConversioTipusHelper conversioTipusHelper;
    @Mock private EntityComprovarHelper entityComprovarHelper;
    @Mock private MetaExpedientHelper metaExpedientHelper;
    @Mock private MessageHelper messageHelper;
    @Mock private ContingutLogHelper contingutLogHelper;
    @Mock private UsuariHelper usuariHelper;
    @Mock private EmailHelper emailHelper;
    @Mock private ExpedientRepository expedientRepository;

    @InjectMocks
    private ExpedientEstatHelper helper;

    private static final Long ENTITAT_ID         = 1L;
    private static final Long META_EXPEDIENT_ID  = 10L;
    private static final Long EXPEDIENT_ID       = 20L;
    private static final Long EXPEDIENT_ESTAT_ID = 30L;
    private static final Long ORGAN_ID           = 40L;

    // =========================================================================
    // findByMetaExpedientAndCodi
    // =========================================================================

    @Test
    void findByMetaExpedientAndCodi_delegaAlRepositori() {
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        ExpedientEstatEntity estat = mock(ExpedientEstatEntity.class);
        when(expedientEstatRepository.findByMetaExpedientAndCodi(metaExpedient, "CODI")).thenReturn(estat);

        ExpedientEstatEntity resultat = helper.findByMetaExpedientAndCodi(metaExpedient, "CODI");

        assertThat(resultat).isSameAs(estat);
    }

    @Test
    void findByMetaExpedientAndCodi_quanNoExisteix_retornaNull() {
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        when(expedientEstatRepository.findByMetaExpedientAndCodi(metaExpedient, "INEXISTENT")).thenReturn(null);

        assertThat(helper.findByMetaExpedientAndCodi(metaExpedient, "INEXISTENT")).isNull();
    }

    // =========================================================================
    // moveTo
    // =========================================================================

    @Test
    void moveTo_desplacaElEstatALaPosicioIndicada() {
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);

        ExpedientEstatEntity estat0 = mock(ExpedientEstatEntity.class);
        ExpedientEstatEntity estat1 = mock(ExpedientEstatEntity.class);
        ExpedientEstatEntity estat2 = mock(ExpedientEstatEntity.class);
        when(estat0.getId()).thenReturn(100L);
        when(estat1.getId()).thenReturn(101L);
        when(estat2.getId()).thenReturn(EXPEDIENT_ESTAT_ID);
        when(estat2.getMetaExpedient()).thenReturn(metaExpedient);

        when(expedientEstatRepository.getOne(EXPEDIENT_ESTAT_ID)).thenReturn(estat2);
        List<ExpedientEstatEntity> llista = new ArrayList<>(Arrays.asList(estat0, estat1, estat2));
        when(expedientEstatRepository.findByMetaExpedientOrderByOrdreAsc(metaExpedient)).thenReturn(llista);

        helper.moveTo(ENTITAT_ID, META_EXPEDIENT_ID, EXPEDIENT_ESTAT_ID, 0, "IPA_ADMIN");

        verify(estat2).updateOrdre(0);
        verify(estat0).updateOrdre(anyInt());
        verify(estat1).updateOrdre(anyInt());
    }

    @Test
    void moveTo_ambUnSolElement_actualitzaOrdreA0() {
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        ExpedientEstatEntity estat = mock(ExpedientEstatEntity.class);
        when(estat.getId()).thenReturn(EXPEDIENT_ESTAT_ID);
        when(estat.getMetaExpedient()).thenReturn(metaExpedient);

        when(expedientEstatRepository.getOne(EXPEDIENT_ESTAT_ID)).thenReturn(estat);
        when(expedientEstatRepository.findByMetaExpedientOrderByOrdreAsc(metaExpedient))
                .thenReturn(new ArrayList<>(Collections.singletonList(estat)));

        helper.moveTo(ENTITAT_ID, META_EXPEDIENT_ID, EXPEDIENT_ESTAT_ID, 0, "IPA_ADMIN");

        verify(estat).updateOrdre(0);
    }

    @Test
    void moveTo_retornaElEstatMogut() {
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        ExpedientEstatEntity estat = mock(ExpedientEstatEntity.class);
        when(estat.getId()).thenReturn(EXPEDIENT_ESTAT_ID);
        when(estat.getMetaExpedient()).thenReturn(metaExpedient);

        when(expedientEstatRepository.getOne(EXPEDIENT_ESTAT_ID)).thenReturn(estat);
        when(expedientEstatRepository.findByMetaExpedientOrderByOrdreAsc(metaExpedient))
                .thenReturn(new ArrayList<>(Collections.singletonList(estat)));

        ExpedientEstatEntity resultat = helper.moveTo(ENTITAT_ID, META_EXPEDIENT_ID, EXPEDIENT_ESTAT_ID, 0, "IPA_ADMIN");

        assertThat(resultat).isSameAs(estat);
    }

    // =========================================================================
    // updateExpedientEstat
    // =========================================================================

    @Test
    void updateExpedientEstat_crideaUpdateALaEntitat() {
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        ExpedientEstatEntity expEstatEntity = mock(ExpedientEstatEntity.class);
        ExpedientEstatDto dto = buildEstatDto();

        helper.updateExpedientEstat(metaExpedient, expEstatEntity, ENTITAT_ID, dto, "IPA_ADMIN", ORGAN_ID);

        verify(expEstatEntity).update(dto.getCodi(), dto.getNom(), dto.getColor(), metaExpedient, dto.getResponsableCodi());
    }

    @Test
    void updateExpedientEstat_retornaLaEntitatActualitzada() {
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        ExpedientEstatEntity expEstatEntity = mock(ExpedientEstatEntity.class);
        ExpedientEstatDto dto = buildEstatDto();

        ExpedientEstatEntity resultat = helper.updateExpedientEstat(metaExpedient, expEstatEntity, ENTITAT_ID, dto, "IPA_ADMIN", ORGAN_ID);

        assertThat(resultat).isSameAs(expEstatEntity);
    }

    // =========================================================================
    // updateEstatAdditional
    // =========================================================================

    @Test
    void updateEstatAdditional_estatIdNull_estableixEstatAdditionalNull() {
        ExpedientEntity expedient = mock(ExpedientEntity.class);
        when(entityComprovarHelper.comprovarExpedient(EXPEDIENT_ID, false, false, true, false, false, null))
                .thenReturn(expedient);
        when(expedient.getEstatAdditional()).thenReturn(null);
        when(messageHelper.getMessage("expedient.estat.enum.OBERT")).thenReturn("OBERT");

        helper.updateEstatAdditional(ENTITAT_ID, EXPEDIENT_ID, null);

        verify(expedient).updateEstatAdditional(null);
    }

    @Test
    void updateEstatAdditional_estatIdNoNull_estableixEstatAdditional() {
        ExpedientEntity expedient = mock(ExpedientEntity.class);
        ExpedientEstatEntity estatNou = mock(ExpedientEstatEntity.class);
        when(entityComprovarHelper.comprovarExpedient(EXPEDIENT_ID, false, false, true, false, false, null))
                .thenReturn(expedient);
        when(expedientEstatRepository.getOne(EXPEDIENT_ESTAT_ID)).thenReturn(estatNou);
        when(expedient.getEstatAdditional()).thenReturn(null);
        when(estatNou.getCodi()).thenReturn("NOU");
        when(messageHelper.getMessage("expedient.estat.enum.OBERT")).thenReturn("OBERT");

        helper.updateEstatAdditional(ENTITAT_ID, EXPEDIENT_ID, EXPEDIENT_ESTAT_ID);

        verify(expedient).updateEstatAdditional(estatNou);
    }

    @Test
    void updateEstatAdditional_estatCanvia_logCanviEstat() {
        ExpedientEntity expedient = mock(ExpedientEntity.class);
        ExpedientEstatEntity estatAnterior = mock(ExpedientEstatEntity.class);
        when(estatAnterior.getCodi()).thenReturn("ANTERIOR");

        ExpedientEstatEntity estatNou = mock(ExpedientEstatEntity.class);
        when(estatNou.getCodi()).thenReturn("NOU");
        when(estatNou.getResponsableCodi()).thenReturn(null);

        when(entityComprovarHelper.comprovarExpedient(EXPEDIENT_ID, false, false, true, false, false, null))
                .thenReturn(expedient);
        when(expedientEstatRepository.getOne(EXPEDIENT_ESTAT_ID)).thenReturn(estatNou);
        when(expedient.getEstatAdditional())
                .thenReturn(estatAnterior)  // null-check before update
                .thenReturn(estatAnterior)  // getCodi() before update
                .thenReturn(estatNou)       // null-check after update
                .thenReturn(estatNou);      // getCodi() after update

        helper.updateEstatAdditional(ENTITAT_ID, EXPEDIENT_ID, EXPEDIENT_ESTAT_ID);

        verify(contingutLogHelper).log(
                eq(expedient),
                eq(LogTipusEnumDto.CANVI_ESTAT),
                eq("ANTERIOR"),
                eq("NOU"),
                eq(false),
                eq(false));
    }

    @Test
    void updateEstatAdditional_estatNoCanvia_noLogCanviEstat() {
        ExpedientEntity expedient = mock(ExpedientEntity.class);
        ExpedientEstatEntity estatActual = mock(ExpedientEstatEntity.class);
        when(estatActual.getCodi()).thenReturn("MATEIX");
        when(estatActual.getResponsableCodi()).thenReturn(null);

        when(entityComprovarHelper.comprovarExpedient(EXPEDIENT_ID, false, false, true, false, false, null))
                .thenReturn(expedient);
        when(expedientEstatRepository.getOne(EXPEDIENT_ESTAT_ID)).thenReturn(estatActual);
        when(expedient.getEstatAdditional()).thenReturn(estatActual);

        helper.updateEstatAdditional(ENTITAT_ID, EXPEDIENT_ID, EXPEDIENT_ESTAT_ID);

        verify(contingutLogHelper, never()).log(any(), eq(LogTipusEnumDto.CANVI_ESTAT), any(), any(), any(), any());
    }

    @Test
    void updateEstatAdditional_estatAmbResponsableCodi_agafarExpedientPerAquestUsuari() {
        ExpedientEntity expedient = mock(ExpedientEntity.class);
        ExpedientEstatEntity estatNou = mock(ExpedientEstatEntity.class);
        when(estatNou.getCodi()).thenReturn("NOU");
        when(estatNou.getResponsableCodi()).thenReturn("USR001");

        UsuariEntity usuariNou = mock(UsuariEntity.class);

        when(entityComprovarHelper.comprovarExpedient(EXPEDIENT_ID, false, false, true, false, false, null))
                .thenReturn(expedient);
        when(expedientEstatRepository.getOne(EXPEDIENT_ESTAT_ID)).thenReturn(estatNou);
        when(expedient.getEstatAdditional()).thenReturn(null);
        when(messageHelper.getMessage("expedient.estat.enum.OBERT")).thenReturn("OBERT");
        when(expedientRepository.getOne(EXPEDIENT_ID)).thenReturn(expedient);
        when(usuariHelper.getUsuariByCodi("USR001")).thenReturn(usuariNou);
        when(expedient.getAgafatPer()).thenReturn(null);

        helper.updateEstatAdditional(ENTITAT_ID, EXPEDIENT_ID, EXPEDIENT_ESTAT_ID);

        verify(expedient).updateAgafatPer(usuariNou);
    }

    @Test
    void updateEstatAdditional_estatAmbResponsableIAgafatPerAltreUsuari_avisaPerEmail() {
        ExpedientEntity expedient = mock(ExpedientEntity.class);
        ExpedientEstatEntity estatNou = mock(ExpedientEstatEntity.class);
        when(estatNou.getCodi()).thenReturn("NOU");
        when(estatNou.getResponsableCodi()).thenReturn("USR001");

        UsuariEntity usuariOriginal = mock(UsuariEntity.class);
        UsuariEntity usuariNou = mock(UsuariEntity.class);

        when(entityComprovarHelper.comprovarExpedient(EXPEDIENT_ID, false, false, true, false, false, null))
                .thenReturn(expedient);
        when(expedientEstatRepository.getOne(EXPEDIENT_ESTAT_ID)).thenReturn(estatNou);
        when(expedient.getEstatAdditional()).thenReturn(null);
        when(messageHelper.getMessage("expedient.estat.enum.OBERT")).thenReturn("OBERT");
        when(expedientRepository.getOne(EXPEDIENT_ID)).thenReturn(expedient);
        when(usuariHelper.getUsuariByCodi("USR001")).thenReturn(usuariNou);
        when(expedient.getAgafatPer()).thenReturn(usuariOriginal);

        helper.updateEstatAdditional(ENTITAT_ID, EXPEDIENT_ID, EXPEDIENT_ESTAT_ID);

        verify(emailHelper).contingutAgafatPerAltreUsusari(expedient, usuariOriginal, usuariNou);
    }

    @Test
    void updateEstatAdditional_retornaExpedient() {
        ExpedientEntity expedient = mock(ExpedientEntity.class);
        when(entityComprovarHelper.comprovarExpedient(EXPEDIENT_ID, false, false, true, false, false, null))
                .thenReturn(expedient);
        when(expedient.getEstatAdditional()).thenReturn(null);
        when(messageHelper.getMessage("expedient.estat.enum.OBERT")).thenReturn("OBERT");

        ExpedientEntity resultat = helper.updateEstatAdditional(ENTITAT_ID, EXPEDIENT_ID, null);

        assertThat(resultat).isSameAs(expedient);
    }

    // =========================================================================
    // createExpedientEstat
    // =========================================================================

    @Test
    void createExpedientEstat_desaElNouEstat() {
        ExpedientEstatEntity saved = prepararCreate(false);

        helper.createExpedientEstat(ENTITAT_ID, buildEstatDto(), "IPA_ADMIN", ORGAN_ID);

        verify(expedientEstatRepository).save(any(ExpedientEstatEntity.class));
    }

    @Test
    void createExpedientEstat_inicialTrue_posaPrimersAFalse() {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        ExpedientEstatEntity estatExistent = mock(ExpedientEstatEntity.class);
        ExpedientEstatEntity saved = mock(ExpedientEstatEntity.class);

        when(entityComprovarHelper.comprovarEntitatPerMetaExpedients(ENTITAT_ID)).thenReturn(entitat);
        when(entityComprovarHelper.comprovarMetaExpedient(entitat, META_EXPEDIENT_ID)).thenReturn(metaExpedient);
        when(expedientEstatRepository.countByMetaExpedient(metaExpedient)).thenReturn(1);
        when(expedientEstatRepository.findByMetaExpedientOrderByOrdreAsc(metaExpedient))
                .thenReturn(Collections.singletonList(estatExistent));
        when(expedientEstatRepository.save(any(ExpedientEstatEntity.class))).thenReturn(saved);
        when(conversioTipusHelper.convertir(saved, ExpedientEstatDto.class)).thenReturn(new ExpedientEstatDto());

        ExpedientEstatDto dto = buildEstatDto();
        dto.setInicial(true);

        helper.createExpedientEstat(ENTITAT_ID, dto, "IPA_ADMIN", ORGAN_ID);

        verify(estatExistent).updateInicial(false);
    }

    @Test
    void createExpedientEstat_inicialFalse_noModificaAltresEstats() {
        prepararCreate(false);

        helper.createExpedientEstat(ENTITAT_ID, buildEstatDto(), "IPA_ADMIN", ORGAN_ID);

        verify(expedientEstatRepository, never()).findByMetaExpedientOrderByOrdreAsc(any());
    }

    @Test
    void createExpedientEstat_rolOrganAdmin_crideaCanviarRevisioADisseny() {
        MetaExpedientEntity metaExpedient = prepararCreateAmbMetaExpedient(false);

        helper.createExpedientEstat(ENTITAT_ID, buildEstatDto(), "IPA_ORGAN_ADMIN", ORGAN_ID);

        verify(metaExpedientHelper).canviarRevisioADisseny(ENTITAT_ID, META_EXPEDIENT_ID, ORGAN_ID);
    }

    @Test
    void createExpedientEstat_rolNoOrganAdmin_nocrideaCanviarRevisio() {
        prepararCreate(false);

        helper.createExpedientEstat(ENTITAT_ID, buildEstatDto(), "IPA_ADMIN", ORGAN_ID);

        verify(metaExpedientHelper, never()).canviarRevisioADisseny(any(), any(), any());
    }

    @Test
    void createExpedientEstat_retornaElDtoConvertit() {
        ExpedientEstatEntity saved = prepararCreate(false);
        ExpedientEstatDto dtoEsperat = new ExpedientEstatDto("Nom", EXPEDIENT_ESTAT_ID);
        when(conversioTipusHelper.convertir(saved, ExpedientEstatDto.class)).thenReturn(dtoEsperat);

        ExpedientEstatDto resultat = helper.createExpedientEstat(ENTITAT_ID, buildEstatDto(), "IPA_ADMIN", ORGAN_ID);

        assertThat(resultat).isSameAs(dtoEsperat);
    }

    // ── Helpers privats ───────────────────────────────────────────────────────

    private ExpedientEstatDto buildEstatDto() {
        ExpedientEstatDto dto = new ExpedientEstatDto();
        dto.setCodi("ESTAT01");
        dto.setNom("Estat de prova");
        dto.setColor("#FF0000");
        dto.setMetaExpedientId(META_EXPEDIENT_ID);
        dto.setInicial(false);
        dto.setResponsableCodi(null);
        return dto;
    }

    private ExpedientEstatEntity prepararCreate(boolean inicial) {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        ExpedientEstatEntity saved = mock(ExpedientEstatEntity.class);

        when(entityComprovarHelper.comprovarEntitatPerMetaExpedients(ENTITAT_ID)).thenReturn(entitat);
        when(entityComprovarHelper.comprovarMetaExpedient(entitat, META_EXPEDIENT_ID)).thenReturn(metaExpedient);
        when(expedientEstatRepository.countByMetaExpedient(metaExpedient)).thenReturn(0);
        when(expedientEstatRepository.save(any(ExpedientEstatEntity.class))).thenReturn(saved);
        when(conversioTipusHelper.convertir(saved, ExpedientEstatDto.class)).thenReturn(new ExpedientEstatDto());
        return saved;
    }

    private MetaExpedientEntity prepararCreateAmbMetaExpedient(boolean inicial) {
        EntitatEntity entitat = mock(EntitatEntity.class);
        MetaExpedientEntity metaExpedient = mock(MetaExpedientEntity.class);
        ExpedientEstatEntity saved = mock(ExpedientEstatEntity.class);

        when(metaExpedient.getId()).thenReturn(META_EXPEDIENT_ID);
        when(entityComprovarHelper.comprovarEntitatPerMetaExpedients(ENTITAT_ID)).thenReturn(entitat);
        when(entityComprovarHelper.comprovarMetaExpedient(entitat, META_EXPEDIENT_ID)).thenReturn(metaExpedient);
        when(expedientEstatRepository.countByMetaExpedient(metaExpedient)).thenReturn(0);
        when(expedientEstatRepository.save(any(ExpedientEstatEntity.class))).thenReturn(saved);
        when(conversioTipusHelper.convertir(saved, ExpedientEstatDto.class)).thenReturn(new ExpedientEstatDto());
        return metaExpedient;
    }
}
