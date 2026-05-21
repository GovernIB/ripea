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
import es.caib.ripea.service.intf.dto.PrioritatEnumDto;
import es.caib.ripea.service.intf.model.MetaExpedientTascaResource;
import es.caib.ripea.service.intf.resourceservice.MetaExpedientTascaResourceService;
import es.caib.ripea.service.test.config.BaseServiceIT;

/**
 * Tests d'integració per a MetaExpedientTascaResourceService.
 *
 * Cobreix: findPage (ordenació i filtre ràpid), create, update, delete i getOne.
 *
 * Conjunt de dades base (TestDataFactory):
 *   - 9 tasques: TASCA_1_1..TASCA_3_3 (3 per cada un dels 3 procediments base)
 *   - Tots pertanyen a ENT_TEST, activa=true, prioritat=B_NORMAL, duracio=null
 */
@WithMockUser(username = "usuari1", roles = {"ADMIN"})
public class MetaExpedientTascaResourceServiceIT extends BaseServiceIT {

    @Autowired private MetaExpedientTascaResourceService metaExpedientTascaResourceService;

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
    void quanConsultamTasques_retornaLesDeLEntitatActual() {
        Page<MetaExpedientTascaResource> pagina = metaExpedientTascaResourceService.findPage(
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
        Page<MetaExpedientTascaResource> pagina = metaExpedientTascaResourceService.findPage(
                null, null, null, null,
                PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "codi"))
        );

        assertThat(pagina.getContent())
                .extracting(MetaExpedientTascaResource::getCodi)
                .containsExactly(
                        "TASCA_1_1", "TASCA_1_2", "TASCA_1_3",
                        "TASCA_2_1", "TASCA_2_2", "TASCA_2_3",
                        "TASCA_3_1", "TASCA_3_2", "TASCA_3_3"
                );
    }

    @Test
    void quanOrdenamPerCodiDesc_elsResultatsSonEnOrdreDecrescent() {
        Page<MetaExpedientTascaResource> pagina = metaExpedientTascaResourceService.findPage(
                null, null, null, null,
                PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "codi"))
        );

        assertThat(pagina.getContent())
                .extracting(MetaExpedientTascaResource::getCodi)
                .containsExactly(
                        "TASCA_3_3", "TASCA_3_2", "TASCA_3_1",
                        "TASCA_2_3", "TASCA_2_2", "TASCA_2_1",
                        "TASCA_1_3", "TASCA_1_2", "TASCA_1_1"
                );
    }

    @Test
    void quanOrdenamPerNomAsc_elsResultatsSonEnOrdreCrescent() {
        Page<MetaExpedientTascaResource> pagina = metaExpedientTascaResourceService.findPage(
                null, null, null, null,
                PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "nom"))
        );

        assertThat(pagina.getContent())
                .extracting(MetaExpedientTascaResource::getNom)
                .containsExactly(
                        "Tasca 1.1", "Tasca 1.2", "Tasca 1.3",
                        "Tasca 2.1", "Tasca 2.2", "Tasca 2.3",
                        "Tasca 3.1", "Tasca 3.2", "Tasca 3.3"
                );
    }

    @Test
    void quanOrdenamPerDuracioAsc_noLlencaExcepcioIRetornaTots() {
        // Totes les tasques de test tenen duracio=null: l'ordre és no determinista
        // però no ha de llençar cap excepció i ha de retornar les 9
        Page<MetaExpedientTascaResource> pagina = metaExpedientTascaResourceService.findPage(
                null, null, null, null,
                PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "duracio"))
        );

        assertThat(pagina.getTotalElements()).isEqualTo(9);
    }

    @Test
    void quanOrdenamPerPrioritatAsc_noLlencaExcepcioIRetornaTots() {
        // Totes amb B_NORMAL: l'ordre intern pot variar, però han de sortir les 9
        Page<MetaExpedientTascaResource> pagina = metaExpedientTascaResourceService.findPage(
                null, null, null, null,
                PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "prioritat"))
        );

        assertThat(pagina.getTotalElements()).isEqualTo(9);
        assertThat(pagina.getContent())
                .extracting(MetaExpedientTascaResource::getPrioritat)
                .containsOnly(PrioritatEnumDto.B_NORMAL);
    }

    // =========================================================================
    // Filtre ràpid (quickFilter)
    // =========================================================================

    @Test
    void quanFiltramPerQuickFilterCodi_retornaResultatCoincident() {
        Page<MetaExpedientTascaResource> pagina = metaExpedientTascaResourceService.findPage(
                "TASCA_1_1", null, null, null,
                PageRequest.of(0, 20)
        );

        assertThat(pagina.getTotalElements()).isEqualTo(1);
        assertThat(pagina.getContent().get(0).getCodi()).isEqualTo("TASCA_1_1");
    }

    @Test
    void quanFiltramPerQuickFilterNom_retornaResultatCoincident() {
        Page<MetaExpedientTascaResource> pagina = metaExpedientTascaResourceService.findPage(
                "Tasca 2.3", null, null, null,
                PageRequest.of(0, 20)
        );

        assertThat(pagina.getTotalElements()).isEqualTo(1);
        assertThat(pagina.getContent().get(0).getNom()).isEqualTo("Tasca 2.3");
    }

    @Test
    void quanFiltramPerQuickFilterParcial_retornaVarisResultats() {
        // "TASCA_2" coincideix amb TASCA_2_1, TASCA_2_2, TASCA_2_3
        Page<MetaExpedientTascaResource> pagina = metaExpedientTascaResourceService.findPage(
                "TASCA_2", null, null, null,
                PageRequest.of(0, 20)
        );

        assertThat(pagina.getTotalElements()).isEqualTo(3);
    }

    // =========================================================================
    // Filtre per camp (springFilter)
    // =========================================================================

    @Test
    void quanFiltramPerCodi_retornaUnicamentLaTascaAmbAquellCodi() {
        Page<MetaExpedientTascaResource> pagina = metaExpedientTascaResourceService.findPage(
                null, "codi:'TASCA_3_2'", null, null,
                PageRequest.of(0, 20)
        );

        assertThat(pagina.getTotalElements()).isEqualTo(1);
        assertThat(pagina.getContent().get(0).getCodi()).isEqualTo("TASCA_3_2");
    }

    @Test
    void quanFiltramPerNom_retornaUnicamentLaTascaAmbAquellNom() {
        Page<MetaExpedientTascaResource> pagina = metaExpedientTascaResourceService.findPage(
                null, "nom:'Tasca 1.3'", null, null,
                PageRequest.of(0, 20)
        );

        assertThat(pagina.getTotalElements()).isEqualTo(1);
        assertThat(pagina.getContent().get(0).getNom()).isEqualTo("Tasca 1.3");
    }

    @Test
    void quanFiltramPerActivaTrue_retornaLesTasquesActives() {
        Page<MetaExpedientTascaResource> pagina = metaExpedientTascaResourceService.findPage(
                null, "activa:true", null, null,
                PageRequest.of(0, 20)
        );

        assertThat(pagina.getTotalElements()).isEqualTo(9);
        assertThat(pagina.getContent())
                .extracting(MetaExpedientTascaResource::isActiva)
                .containsOnly(true);
    }

    @Test
    void quanFiltramPerActivaFalse_retornaLlistaVuida() {
        // Cap tasca de test té activa=false
        Page<MetaExpedientTascaResource> pagina = metaExpedientTascaResourceService.findPage(
                null, "activa:false", null, null,
                PageRequest.of(0, 20)
        );

        assertThat(pagina.getTotalElements()).isEqualTo(0);
    }

    @Test
    void quanFiltramPerPrioritat_retornaLesTasquesAmbAquellaPrioritat() {
        Page<MetaExpedientTascaResource> pagina = metaExpedientTascaResourceService.findPage(
                null, "prioritat:'B_NORMAL'", null, null,
                PageRequest.of(0, 20)
        );

        assertThat(pagina.getTotalElements()).isEqualTo(9);
    }

    @Test
    void quanFiltramPerMetaExpedient_retornaUnicamentLesTasquesDeAquellProcediment() {
        Long metaExpedientId = testData.metaExpedients.get(0).getId(); // PROC_01

        Page<MetaExpedientTascaResource> pagina = metaExpedientTascaResourceService.findPage(
                null, "metaExpedient.id:" + metaExpedientId, null, null,
                PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "codi"))
        );

        assertThat(pagina.getTotalElements()).isEqualTo(3);
        assertThat(pagina.getContent())
                .extracting(MetaExpedientTascaResource::getCodi)
                .containsExactly("TASCA_1_1", "TASCA_1_2", "TASCA_1_3");
    }

    // =========================================================================
    // CRUD: getOne, create, update, delete
    // =========================================================================

    @Test
    void quanDemanамGetOnePerUnId_retornaLaTascaCorrecte() {
        Long id = testData.tasques.get(0).getId(); // TASCA_1_1

        MetaExpedientTascaResource resultat = metaExpedientTascaResourceService.getOne(id, null);

        assertThat(resultat).isNotNull();
        assertThat(resultat.getCodi()).isEqualTo("TASCA_1_1");
        assertThat(resultat.getNom()).isEqualTo("Tasca 1.1");
    }

    @Test
    void quanCreemUnaNovaTasca_laConsultaRetornaUnElementMes() {
        long totalInicial = metaExpedientTascaResourceService.findPage(
                null, null, null, null, PageRequest.of(0, 1)
        ).getTotalElements();

        metaExpedientTascaResourceService.create(buildResourceMinim(), null);
        entityManager.flush();
        entityManager.clear();

        long totalFinal = metaExpedientTascaResourceService.findPage(
                null, null, null, null, PageRequest.of(0, 1)
        ).getTotalElements();

        assertThat(totalFinal).isEqualTo(totalInicial + 1);
    }

    @Test
    void quanActualitzamElNomDUnaTasca_getOneRetornaElNomActualitzat() {
        Long id = testData.tasques.get(0).getId();
        MetaExpedientTascaResource existent = metaExpedientTascaResourceService.getOne(id, null);
        existent.setNom("Nom Actualitzat Tasca");

        metaExpedientTascaResourceService.update(id, existent, null);
        entityManager.flush();
        entityManager.clear();

        assertThat(metaExpedientTascaResourceService.getOne(id, null).getNom())
                .isEqualTo("Nom Actualitzat Tasca");
    }

    @Test
    void quanActualitzamLaPrioritatDUnaTasca_getOneRetornaLaPrioritatActualitzada() {
        Long id = testData.tasques.get(0).getId();
        MetaExpedientTascaResource existent = metaExpedientTascaResourceService.getOne(id, null);
        existent.setPrioritat(PrioritatEnumDto.D_MOLT_ALTA);

        metaExpedientTascaResourceService.update(id, existent, null);
        entityManager.flush();
        entityManager.clear();

        assertThat(metaExpedientTascaResourceService.getOne(id, null).getPrioritat())
                .isEqualTo(PrioritatEnumDto.D_MOLT_ALTA);
    }

    @Test
    void quanActualitzamLaDuracioDAunaTasca_getOneRetornaLaDuracioActualitzada() {
        Long id = testData.tasques.get(0).getId();
        MetaExpedientTascaResource existent = metaExpedientTascaResourceService.getOne(id, null);
        existent.setDuracio(30);

        metaExpedientTascaResourceService.update(id, existent, null);
        entityManager.flush();
        entityManager.clear();

        assertThat(metaExpedientTascaResourceService.getOne(id, null).getDuracio())
                .isEqualTo(30);
    }

    @Test
    void quanEsborramUnaTasca_laConsultaRetornaMenysUnElement() {
        long totalInicial = metaExpedientTascaResourceService.findPage(
                null, null, null, null, PageRequest.of(0, 1)
        ).getTotalElements();

        Long id = testData.tasques.get(0).getId();
        metaExpedientTascaResourceService.delete(id, null);
        entityManager.flush();
        entityManager.clear();

        long totalFinal = metaExpedientTascaResourceService.findPage(
                null, null, null, null, PageRequest.of(0, 1)
        ).getTotalElements();

        assertThat(totalFinal).isEqualTo(totalInicial - 1);
    }

    // =========================================================================
    // Mètode auxiliar
    // =========================================================================

    private MetaExpedientTascaResource buildResourceMinim() {
        MetaExpedientTascaResource r = new MetaExpedientTascaResource();
        r.setCodi("TASCA_NOU");
        r.setNom("Tasca Nova");
        r.setDescripcio("Descripció de la tasca nova");
        r.setPrioritat(PrioritatEnumDto.B_NORMAL);
        r.setMetaExpedient(ResourceReference.toResourceReference(
                testData.metaExpedients.get(0).getId(), "PROC_01"));
        return r;
    }
}
