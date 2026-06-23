package es.caib.ripea.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import es.caib.ripea.service.intf.dto.PermisosPerExpedientsDto;

/**
 * Test unitari de {@link PermisosPerExpedientsDto#capPermis()}.
 *
 * capPermis() s'utilitza a ExpedientResourceServiceImpl.additionalSpringFilter per
 * tallar l'execució i retornar 0 resultats quan l'usuari no té permís per cap via.
 * Ha de coincidir EXACTAMENT amb les vies que concedeixen accés a getFiltrePermisos:
 *
 *   VIA 1 - idsMetaExpedientsPermesos               (metaExpedient.id IN ME)
 *   VIA 2 - idsOrgansPermesos                       (meogp.organGestor.id IN OG)
 *   VIA 3 - idsMetaExpedientOrganPairsPermesos      (meogp.id IN MEOP)
 *   VIA 4 - idsOrgansAmbProcedimentsComunsPermesos  AND idsProcedimentsComuns  (cal totes dues)
 *   VIA 5 - idsGrupsPermesos                        (grup.id IN GR)
 *
 * Invariant: capPermis()==true  <=>  cap via concedeix accés.
 */
class PermisosPerExpedientsCapPermisTest {

    private static final List<Long> ALGUNS = Arrays.asList(1L, 2L);
    private static final List<Long> BUIDA = Collections.emptyList();

    // -------------------------------------------------------------------------
    // Sense permís per cap via -> capPermis() == true (es talla, 0 resultats)
    // -------------------------------------------------------------------------

    @Test
    void donatUnDtoSenseCapLlista_capPermisEsTrue() {
        assertThat(new PermisosPerExpedientsDto().capPermis()).isTrue();
    }

    @Test
    void donatUnDtoAmbTotesLesLlistesBuides_capPermisEsTrue() {
        PermisosPerExpedientsDto dto = new PermisosPerExpedientsDto();
        dto.setIdsMetaExpedientsPermesos(BUIDA);
        dto.setIdsOrgansPermesos(BUIDA);
        dto.setIdsMetaExpedientOrganPairsPermesos(BUIDA);
        dto.setIdsOrgansAmbProcedimentsComunsPermesos(BUIDA);
        dto.setIdsProcedimentsComuns(BUIDA);
        dto.setIdsGrupsPermesos(BUIDA);
        assertThat(dto.capPermis()).isTrue();
    }

    // -------------------------------------------------------------------------
    // Cada via, en solitari, concedeix accés -> capPermis() == false (no es talla)
    // -------------------------------------------------------------------------

    @Test
    void via1_nomesProcedimentsPermesos_capPermisEsFalse() {
        PermisosPerExpedientsDto dto = new PermisosPerExpedientsDto();
        dto.setIdsMetaExpedientsPermesos(ALGUNS);
        assertThat(dto.capPermis()).isFalse();
    }

    @Test
    void via2_nomesOrgansPermesos_capPermisEsFalse() {
        PermisosPerExpedientsDto dto = new PermisosPerExpedientsDto();
        dto.setIdsOrgansPermesos(ALGUNS);
        assertThat(dto.capPermis()).isFalse();
    }

    @Test
    void via3_nomesParellsProcedimentOrgan_capPermisEsFalse() {
        PermisosPerExpedientsDto dto = new PermisosPerExpedientsDto();
        dto.setIdsMetaExpedientOrganPairsPermesos(ALGUNS);
        assertThat(dto.capPermis()).isFalse();
    }

    @Test
    void via5_nomesGrups_capPermisEsFalse() {
        PermisosPerExpedientsDto dto = new PermisosPerExpedientsDto();
        dto.setIdsGrupsPermesos(ALGUNS);
        assertThat(dto.capPermis()).isFalse();
    }

    // -------------------------------------------------------------------------
    // VIA 4 (procediments comuns): cal OGC i PC alhora.
    // Una sola de les dues NO concedeix accés -> capPermis() segueix sent true.
    // -------------------------------------------------------------------------

    @Test
    void via4_nomesOrgansComunsSenseProcedimentsComuns_capPermisEsTrue() {
        PermisosPerExpedientsDto dto = new PermisosPerExpedientsDto();
        dto.setIdsOrgansAmbProcedimentsComunsPermesos(ALGUNS);
        // idsProcedimentsComuns buida
        assertThat(dto.capPermis()).isTrue();
    }

    @Test
    void via4_nomesProcedimentsComunsSenseOrgansComuns_capPermisEsTrue() {
        PermisosPerExpedientsDto dto = new PermisosPerExpedientsDto();
        dto.setIdsProcedimentsComuns(ALGUNS);
        // idsOrgansAmbProcedimentsComunsPermesos buida
        assertThat(dto.capPermis()).isTrue();
    }

    @Test
    void via4_organsComunsIProcedimentsComunsAlhora_capPermisEsFalse() {
        PermisosPerExpedientsDto dto = new PermisosPerExpedientsDto();
        dto.setIdsOrgansAmbProcedimentsComunsPermesos(ALGUNS);
        dto.setIdsProcedimentsComuns(ALGUNS);
        assertThat(dto.capPermis()).isFalse();
    }
}
