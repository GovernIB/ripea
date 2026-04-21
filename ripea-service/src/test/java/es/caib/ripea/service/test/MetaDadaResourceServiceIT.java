package es.caib.ripea.service.test;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.test.context.support.WithMockUser;

import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.dto.MetaDadaTipusEnumDto;
import es.caib.ripea.service.intf.dto.MultiplicitatEnumDto;
import es.caib.ripea.service.intf.model.MetaDadaResource;
import es.caib.ripea.service.intf.resourceservice.MetaDadaResourceService;
import es.caib.ripea.service.test.config.BaseServiceIT;

/**
 * Tests d'integració per a MetaDadaResourceService.
 *
 * Cobreix: findPage (ordenació i filtre ràpid), create, update, delete i getOne.
 *
 * Conjunt de dades base (TestDataFactory):
 *   - 9 meta-dades: METADADA_1_1..METADADA_3_3 (3 per cada un dels 3 procediments base)
 *   - Tots pertanyen a ENT_TEST, activa=false, multiplicitat=M_0_1, tipus=TEXT
 */
@WithMockUser(username = "usuari1", roles = {"ADMIN"})
public class MetaDadaResourceServiceIT extends BaseServiceIT {

    @Autowired private MetaDadaResourceService metaDadaResourceService;

    @BeforeEach
    @Override
    public void setUpTestData() {
        super.setUpTestData();

        Mockito.lenient().when(configHelper.getEntitatActualCodi()).thenReturn("ENT_TEST");
        Mockito.lenient().when(configHelper.getRolActual()).thenReturn(BaseConfig.ROLE_ADMIN);
        Mockito.lenient().when(configHelper.getOrganActualCodi()).thenReturn(null);
    }

    // =========================================================================
    // Llistat i filtre d'entitat
    // =========================================================================

    @Test
    void quanConsultamMetaDades_retornaLesDeLEntitatActual() {
        Page<MetaDadaResource> pagina = metaDadaResourceService.findPage(
                null, null, null, null,
                PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "codi"))
        );

        assertThat(pagina.getTotalElements()).isEqualTo(9);
    }

    // =========================================================================
    // Ordenació
    // =========================================================================

    @Test
    void quanOrdenamPerCodiAsc_elsResultatsSonEnOrdreCrescent() {
        Page<MetaDadaResource> pagina = metaDadaResourceService.findPage(
                null, null, null, null,
                PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "codi"))
        );

        assertThat(pagina.getContent())
                .extracting(MetaDadaResource::getCodi)
                .containsExactly(
                        "METADADA_1_1", "METADADA_1_2", "METADADA_1_3",
                        "METADADA_2_1", "METADADA_2_2", "METADADA_2_3",
                        "METADADA_3_1", "METADADA_3_2", "METADADA_3_3"
                );
    }

    @Test
    void quanOrdenamPerCodiDesc_elsResultatsSonEnOrdreDecrescent() {
        Page<MetaDadaResource> pagina = metaDadaResourceService.findPage(
                null, null, null, null,
                PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "codi"))
        );

        assertThat(pagina.getContent())
                .extracting(MetaDadaResource::getCodi)
                .containsExactly(
                        "METADADA_3_3", "METADADA_3_2", "METADADA_3_1",
                        "METADADA_2_3", "METADADA_2_2", "METADADA_2_1",
                        "METADADA_1_3", "METADADA_1_2", "METADADA_1_1"
                );
    }

    @Test
    void quanOrdenamPerNomAsc_elsResultatsSonEnOrdreCrescent() {
        Page<MetaDadaResource> pagina = metaDadaResourceService.findPage(
                null, null, null, null,
                PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "nom"))
        );

        assertThat(pagina.getContent())
                .extracting(MetaDadaResource::getNom)
                .containsExactly(
                        "Meta-dada 1.1", "Meta-dada 1.2", "Meta-dada 1.3",
                        "Meta-dada 2.1", "Meta-dada 2.2", "Meta-dada 2.3",
                        "Meta-dada 3.1", "Meta-dada 3.2", "Meta-dada 3.3"
                );
    }

    @Test
    void quanOrdenamPerMultiplicitatAsc_noLlencaExcepcioIRetornaTots() {
        // Totes amb M_0_1: l'ordre intern pot variar, però han de sortir les 9
        Page<MetaDadaResource> pagina = metaDadaResourceService.findPage(
                null, null, null, null,
                PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "multiplicitat"))
        );

        assertThat(pagina.getTotalElements()).isEqualTo(9);
        assertThat(pagina.getContent())
                .extracting(MetaDadaResource::getMultiplicitat)
                .containsOnly(MultiplicitatEnumDto.M_0_1);
    }

    @Test
    void quanOrdenamPerActivaAsc_noLlencaExcepcioIRetornaTots() {
        // Totes amb activa=false: l'ordre intern pot variar, però han de sortir les 9
        Page<MetaDadaResource> pagina = metaDadaResourceService.findPage(
                null, null, null, null,
                PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "activa"))
        );

        assertThat(pagina.getTotalElements()).isEqualTo(9);
        assertThat(pagina.getContent())
                .extracting(MetaDadaResource::isActiva)
                .containsOnly(false);
    }

    // =========================================================================
    // Filtre ràpid (quickFilter)
    // =========================================================================

    @Test
    void quanFiltramPerQuickFilterCodi_retornaResultatCoincident() {
        Page<MetaDadaResource> pagina = metaDadaResourceService.findPage(
                "METADADA_1_1", null, null, null,
                PageRequest.of(0, 20)
        );

        assertThat(pagina.getTotalElements()).isEqualTo(1);
        assertThat(pagina.getContent().get(0).getCodi()).isEqualTo("METADADA_1_1");
    }

    @Test
    void quanFiltramPerQuickFilterNom_retornaResultatCoincident() {
        Page<MetaDadaResource> pagina = metaDadaResourceService.findPage(
                "Meta-dada 2.2", null, null, null,
                PageRequest.of(0, 20)
        );

        assertThat(pagina.getTotalElements()).isEqualTo(1);
        assertThat(pagina.getContent().get(0).getNom()).isEqualTo("Meta-dada 2.2");
    }

    @Test
    void quanFiltramPerQuickFilterTipus_retornaResultatsCoincidents() {
        // quickFilterFields inclou "tipus", tots TEXT → retorna els 9
        Page<MetaDadaResource> pagina = metaDadaResourceService.findPage(
                "TEXT", null, null, null,
                PageRequest.of(0, 20)
        );

        assertThat(pagina.getTotalElements()).isEqualTo(9);
    }

    @Test
    void quanFiltramPerQuickFilterParcial_retornaVarisResultats() {
        // "METADADA_3" coincideix amb METADADA_3_1, METADADA_3_2, METADADA_3_3
        Page<MetaDadaResource> pagina = metaDadaResourceService.findPage(
                "METADADA_3", null, null, null,
                PageRequest.of(0, 20)
        );

        assertThat(pagina.getTotalElements()).isEqualTo(3);
    }

    // =========================================================================
    // Filtre per camp (springFilter)
    // =========================================================================

    @Test
    void quanFiltramPerCodi_retornaUnicamentLaMetaDadaAmbAquellCodi() {
        Page<MetaDadaResource> pagina = metaDadaResourceService.findPage(
                null, "codi:'METADADA_2_3'", null, null,
                PageRequest.of(0, 20)
        );

        assertThat(pagina.getTotalElements()).isEqualTo(1);
        assertThat(pagina.getContent().get(0).getCodi()).isEqualTo("METADADA_2_3");
    }

    @Test
    void quanFiltramPerNom_retornaUnicamentLaMetaDadaAmbAquellNom() {
        Page<MetaDadaResource> pagina = metaDadaResourceService.findPage(
                null, "nom:'Meta-dada 3.2'", null, null,
                PageRequest.of(0, 20)
        );

        assertThat(pagina.getTotalElements()).isEqualTo(1);
        assertThat(pagina.getContent().get(0).getNom()).isEqualTo("Meta-dada 3.2");
    }

    @Test
    void quanFiltramPerMultiplicitat_retornaLesDadesAmbAquellaMultiplicitat() {
        Page<MetaDadaResource> pagina = metaDadaResourceService.findPage(
                null, "multiplicitat:'M_0_1'", null, null,
                PageRequest.of(0, 20)
        );

        assertThat(pagina.getTotalElements()).isEqualTo(9);
    }

    @Test
    void quanFiltramPerActivaFalse_retornaLesDadesInactives() {
        // Totes les metadades de test estan inactives (activa=false per defecte)
        Page<MetaDadaResource> pagina = metaDadaResourceService.findPage(
                null, "activa:false", null, null,
                PageRequest.of(0, 20)
        );

        assertThat(pagina.getTotalElements()).isEqualTo(9);
        assertThat(pagina.getContent())
                .extracting(MetaDadaResource::isActiva)
                .containsOnly(false);
    }

    @Test
    void quanFiltramPerActivaTrue_retornaLlistaVuida() {
        // Cap metadada de test té activa=true
        Page<MetaDadaResource> pagina = metaDadaResourceService.findPage(
                null, "activa:true", null, null,
                PageRequest.of(0, 20)
        );

        assertThat(pagina.getTotalElements()).isEqualTo(0);
    }

    @Test
    void quanFiltramPerMetaNode_retornaUnicamentLesDadesDeAquellProcediment() {
        Long metaNodeId = testData.metaExpedients.get(0).getId(); // PROC_01

        Page<MetaDadaResource> pagina = metaDadaResourceService.findPage(
                null, "metaNode.id:" + metaNodeId, null, null,
                PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "codi"))
        );

        assertThat(pagina.getTotalElements()).isEqualTo(3);
        assertThat(pagina.getContent())
                .extracting(MetaDadaResource::getCodi)
                .containsExactly("METADADA_1_1", "METADADA_1_2", "METADADA_1_3");
    }

    // =========================================================================
    // CRUD: getOne, create, update, delete
    // =========================================================================

    @Test
    void quanDemanамGetOnePerUnId_retornaLaMetaDadaCorrecte() {
        Long id = testData.metaDades.get(0).getId(); // METADADA_1_1

        MetaDadaResource resultat = metaDadaResourceService.getOne(id, null);

        assertThat(resultat).isNotNull();
        assertThat(resultat.getCodi()).isEqualTo("METADADA_1_1");
        assertThat(resultat.getNom()).isEqualTo("Meta-dada 1.1");
    }

    @Test
    void quanCreemUnaNouaMetaDada_laConsultaRetornaUnElementMes() {
        long totalInicial = metaDadaResourceService.findPage(
                null, null, null, null, PageRequest.of(0, 1)
        ).getTotalElements();

        metaDadaResourceService.create(buildResourceMinim(), null);
        entityManager.flush();
        entityManager.clear();

        long totalFinal = metaDadaResourceService.findPage(
                null, null, null, null, PageRequest.of(0, 1)
        ).getTotalElements();

        assertThat(totalFinal).isEqualTo(totalInicial + 1);
    }

    @Test
    void quanActualitzamElNomDUnaMetaDada_getOneRetornaElNomActualitzat() {
        Long id = testData.metaDades.get(0).getId();
        MetaDadaResource existent = metaDadaResourceService.getOne(id, null);
        existent.setNom("Nom Actualitzat MetaDada");

        metaDadaResourceService.update(id, existent, null);
        entityManager.flush();
        entityManager.clear();

        assertThat(metaDadaResourceService.getOne(id, null).getNom())
                .isEqualTo("Nom Actualitzat MetaDada");
    }

    @Test
    void quanActivamUnaMetaDadaInactiva_getOneRetornaActivaVer() {
        Long id = testData.metaDades.get(0).getId();
        MetaDadaResource existent = metaDadaResourceService.getOne(id, null);
        assertThat(existent.isActiva()).isFalse();

        existent.setActiva(true);
        metaDadaResourceService.update(id, existent, null);
        entityManager.flush();
        entityManager.clear();

        assertThat(metaDadaResourceService.getOne(id, null).isActiva()).isTrue();
    }

    @Test
    void quanActualitzamLaMultiplicitatDUnaMetaDada_getOneRetornaLaMultiplicitatActualitzada() {
        Long id = testData.metaDades.get(0).getId();
        MetaDadaResource existent = metaDadaResourceService.getOne(id, null);
        existent.setMultiplicitat(MultiplicitatEnumDto.M_1);

        metaDadaResourceService.update(id, existent, null);
        entityManager.flush();
        entityManager.clear();

        assertThat(metaDadaResourceService.getOne(id, null).getMultiplicitat())
                .isEqualTo(MultiplicitatEnumDto.M_1);
    }

    @Test
    void quanEsborramUnaMetaDada_laConsultaRetornaMenysUnElement() {
        long totalInicial = metaDadaResourceService.findPage(
                null, null, null, null, PageRequest.of(0, 1)
        ).getTotalElements();

        Long id = testData.metaDades.get(0).getId();
        metaDadaResourceService.delete(id, null);
        entityManager.flush();
        entityManager.clear();

        long totalFinal = metaDadaResourceService.findPage(
                null, null, null, null, PageRequest.of(0, 1)
        ).getTotalElements();

        assertThat(totalFinal).isEqualTo(totalInicial - 1);
    }

    // =========================================================================
    // Acció REORDENAR (artifactActionExec)
    // =========================================================================

    /**
     * Situació inicial per a PROC_01 (índexs 0..2 de testData.metaDades):
     *   ordre=1 → METADADA_1_1
     *   ordre=2 → METADADA_1_2
     *   ordre=3 → METADADA_1_3
     *
     * moureMetaNodeMetaDada fa:
     *   1. Obté la llista ordenada per ordre ASC.
     *   2. Elimina l'element en la posició actual i l'insereix a la nova posició.
     *   3. Reassigna ordre=0,1,2,... a tots els elements.
     */
    @Test
    void quanReordenamLaDarreraMetaDadaAlPrimer_laConsultaPerOrdreReflecteixElCanvi() {
        Long metaNodeId = testData.metaExpedients.get(0).getId(); // PROC_01
        // METADADA_1_3 és la darrera (ordre=3); la movem a posició 0
        Long idDarrera = testData.metaDades.get(2).getId(); // METADADA_1_3

        metaDadaResourceService.artifactActionExec(idDarrera, MetaDadaResource.ACTION_REORDENAR_CODE, 0);
        entityManager.flush();
        entityManager.clear();

        // Ara l'ordre ha de ser: METADADA_1_3 (ordre=0), METADADA_1_1 (ordre=1), METADADA_1_2 (ordre=2)
        Page<MetaDadaResource> pagina = metaDadaResourceService.findPage(
                null, "metaNode.id:" + metaNodeId, null, null,
                PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "ordre"))
        );

        assertThat(pagina.getContent())
                .extracting(MetaDadaResource::getCodi)
                .containsExactly("METADADA_1_3", "METADADA_1_1", "METADADA_1_2");
    }

    @Test
    void quanReordenamLaPrimeraMetaDadaAlDarrer_laConsultaPerOrdreReflecteixElCanvi() {
        Long metaNodeId = testData.metaExpedients.get(0).getId(); // PROC_01
        // METADADA_1_1 és la primera (ordre=1); la movem a posició 2 (última)
        Long idPrimera = testData.metaDades.get(0).getId(); // METADADA_1_1

        metaDadaResourceService.artifactActionExec(idPrimera, MetaDadaResource.ACTION_REORDENAR_CODE, 2);
        entityManager.flush();
        entityManager.clear();

        // Ara l'ordre ha de ser: METADADA_1_2 (ordre=0), METADADA_1_3 (ordre=1), METADADA_1_1 (ordre=2)
        Page<MetaDadaResource> pagina = metaDadaResourceService.findPage(
                null, "metaNode.id:" + metaNodeId, null, null,
                PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "ordre"))
        );

        assertThat(pagina.getContent())
                .extracting(MetaDadaResource::getCodi)
                .containsExactly("METADADA_1_2", "METADADA_1_3", "METADADA_1_1");
    }

    @Test
    void quanReordenamUnElementALaMateixaPosicio_lordreNoCanvia() {
        Long metaNodeId = testData.metaExpedients.get(0).getId(); // PROC_01
        Long idMitjana = testData.metaDades.get(1).getId(); // METADADA_1_2 (ordre=2, índex 1 a la llista)

        // La llista ordenada és [METADADA_1_1(0), METADADA_1_2(1), METADADA_1_3(2)] (0-based after first call)
        // METADADA_1_2 és a la posició 1; la movem a posició 1 → sense canvi
        metaDadaResourceService.artifactActionExec(idMitjana, MetaDadaResource.ACTION_REORDENAR_CODE, 1);
        entityManager.flush();
        entityManager.clear();

        Page<MetaDadaResource> pagina = metaDadaResourceService.findPage(
                null, "metaNode.id:" + metaNodeId, null, null,
                PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "ordre"))
        );

        assertThat(pagina.getContent())
                .extracting(MetaDadaResource::getCodi)
                .containsExactly("METADADA_1_1", "METADADA_1_2", "METADADA_1_3");
    }

    @Test
    void quanReordenamMetaDada_elsDaltresMetaNodeNoEsVeuen_Afectats() {
        // Reordenar dins PROC_01 no ha d'afectar les metadades de PROC_02
        Long metaNode2Id = testData.metaExpedients.get(1).getId(); // PROC_02
        Long idDarreraPROC01 = testData.metaDades.get(2).getId();   // METADADA_1_3

        metaDadaResourceService.artifactActionExec(idDarreraPROC01, MetaDadaResource.ACTION_REORDENAR_CODE, 0);
        entityManager.flush();
        entityManager.clear();

        // PROC_02 ha de mantenir l'ordre original
        Page<MetaDadaResource> pagina = metaDadaResourceService.findPage(
                null, "metaNode.id:" + metaNode2Id, null, null,
                PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "ordre"))
        );

        assertThat(pagina.getContent())
                .extracting(MetaDadaResource::getCodi)
                .containsExactly("METADADA_2_1", "METADADA_2_2", "METADADA_2_3");
    }

    // =========================================================================
    // Mètode auxiliar
    // =========================================================================

    private MetaDadaResource buildResourceMinim() {
        MetaDadaResource r = new MetaDadaResource();
        r.setCodi("METADADA_NOU");
        r.setNom("Meta-dada Nova");
        r.setTipus(MetaDadaTipusEnumDto.TEXT);
        r.setMultiplicitat(MultiplicitatEnumDto.M_1);
        r.setMetaNode(ResourceReference.toResourceReference(
                testData.metaExpedients.get(0).getId(), "PROC_01"));
        return r;
    }
}
