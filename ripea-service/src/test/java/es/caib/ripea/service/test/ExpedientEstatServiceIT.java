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

import es.caib.ripea.persistence.entity.ExpedientEstatEntity;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.dto.ExpedientEstatDto;
import es.caib.ripea.service.intf.model.MetaExpedientEstatResource;
import es.caib.ripea.service.intf.resourceservice.MetaExpedientEstatResourceService;
import es.caib.ripea.service.intf.service.ExpedientEstatService;
import es.caib.ripea.service.test.config.BaseServiceIT;

/**
 * Tests d'integració per a ExpedientEstatService i MetaExpedientEstatResourceService.
 *
 * Conjunt de dades base (TestDataFactory):
 *   - 9 estats: EST_1_1..EST_1_3 (PROC_01), EST_2_1..EST_2_3 (PROC_02), EST_3_1..EST_3_3 (PROC_03)
 *   - Tots amb color #3366FF, ordre 1..3 per procediment, responsable null
 */
@WithMockUser(username = "usuari1", roles = {"ADMIN"})
public class ExpedientEstatServiceIT extends BaseServiceIT {

    @Autowired private ExpedientEstatService metaExpedientEstatService;
    @Autowired private MetaExpedientEstatResourceService metaExpedientEstatResourceService;

    @BeforeEach
    @Override
    public void setUpTestData() {
        super.setUpTestData();

        Mockito.lenient().when(configHelper.getEntitatActualCodi()).thenReturn("ENT_TEST");
        Mockito.lenient().when(configHelper.getRolActual()).thenReturn(BaseConfig.ROLE_ADMIN);
        Mockito.lenient().when(configHelper.getOrganActualCodi()).thenReturn(null);
    }

    // =========================================================================
    // Tests ExpedientEstatService (servei llegat)
    // =========================================================================

    @Test
    void donatUnEstatExistent_quanElCercamPerId_elRetornaCorrèctament() {
        ExpedientEstatEntity estat = testData.estats.get(0);

        ExpedientEstatDto dto = metaExpedientEstatService.findExpedientEstatById(
                testData.entitat.getId(),
                estat.getId()
        );

        assertThat(dto).isNotNull();
        assertThat(dto.getCodi()).isEqualTo(estat.getCodi());
        assertThat(dto.getNom()).isEqualTo(estat.getNom());
        assertThat(dto.getMetaExpedientId()).isEqualTo(estat.getMetaExpedient().getId());
    }

    @Test
    void donatUnEstatExistent_quanElCercamPerId_elColorEsCorrect() {
        ExpedientEstatEntity estat = testData.estats.get(0);

        ExpedientEstatDto dto = metaExpedientEstatService.findExpedientEstatById(
                testData.entitat.getId(),
                estat.getId()
        );

        assertThat(dto.getColor()).isEqualTo("#3366FF");
    }

    @Test
    void donatUnEstatDelTercerProcediment_quanElCercamPerId_elMetaExpedientAssociatEsCorrect() {
        ExpedientEstatEntity estat = testData.estats.get(6);

        ExpedientEstatDto dto = metaExpedientEstatService.findExpedientEstatById(
                testData.entitat.getId(),
                estat.getId()
        );

        assertThat(dto.getMetaExpedientId()).isEqualTo(testData.metaExpedients.get(2).getId());
    }

    // =========================================================================
    // Llistat i filtre d'entitat
    // =========================================================================

    @Test
    void quanConsultamEstats_retornaElsNouDeLEntitatActual() {
        Page<MetaExpedientEstatResource> pagina = metaExpedientEstatResourceService.findPage(
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
        Page<MetaExpedientEstatResource> pagina = metaExpedientEstatResourceService.findPage(
                null, null, null, null,
                PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "codi"))
        );

        assertThat(pagina.getContent())
                .extracting(MetaExpedientEstatResource::getCodi)
                .containsExactly(
                        "EST_1_1", "EST_1_2", "EST_1_3",
                        "EST_2_1", "EST_2_2", "EST_2_3",
                        "EST_3_1", "EST_3_2", "EST_3_3"
                );
    }

    @Test
    void quanOrdenamPerCodiDesc_elsResultatsSonEnOrdreDecrescent() {
        Page<MetaExpedientEstatResource> pagina = metaExpedientEstatResourceService.findPage(
                null, null, null, null,
                PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "codi"))
        );

        assertThat(pagina.getContent())
                .extracting(MetaExpedientEstatResource::getCodi)
                .containsExactly(
                        "EST_3_3", "EST_3_2", "EST_3_1",
                        "EST_2_3", "EST_2_2", "EST_2_1",
                        "EST_1_3", "EST_1_2", "EST_1_1"
                );
    }

    @Test
    void quanOrdenamPerNomAsc_elsResultatsSonEnOrdreCrescent() {
        Page<MetaExpedientEstatResource> pagina = metaExpedientEstatResourceService.findPage(
                null, null, null, null,
                PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "nom"))
        );

        assertThat(pagina.getContent())
                .extracting(MetaExpedientEstatResource::getNom)
                .containsExactly(
                        "Estat 1.1", "Estat 1.2", "Estat 1.3",
                        "Estat 2.1", "Estat 2.2", "Estat 2.3",
                        "Estat 3.1", "Estat 3.2", "Estat 3.3"
                );
    }

    // =========================================================================
    // Filtre ràpid (quickFilter)
    // =========================================================================

    @Test
    void quanFiltramPerQuickFilterCodi_retornaResultatCoincident() {
        Page<MetaExpedientEstatResource> pagina = metaExpedientEstatResourceService.findPage(
                "EST_1_1", null, null, null,
                PageRequest.of(0, 20)
        );

        assertThat(pagina.getTotalElements()).isEqualTo(1);
        assertThat(pagina.getContent().get(0).getCodi()).isEqualTo("EST_1_1");
    }

    @Test
    void quanFiltramPerQuickFilterNom_retornaResultatCoincident() {
        Page<MetaExpedientEstatResource> pagina = metaExpedientEstatResourceService.findPage(
                "Estat 2.3", null, null, null,
                PageRequest.of(0, 20)
        );

        assertThat(pagina.getTotalElements()).isEqualTo(1);
        assertThat(pagina.getContent().get(0).getNom()).isEqualTo("Estat 2.3");
    }

    @Test
    void quanFiltramPerQuickFilterParcial_retornaVarisResultats() {
        Page<MetaExpedientEstatResource> pagina = metaExpedientEstatResourceService.findPage(
                "EST_1", null, null, null,
                PageRequest.of(0, 20)
        );

        assertThat(pagina.getTotalElements()).isEqualTo(3);
    }

    // =========================================================================
    // Filtre per camp (springFilter): codi, nom, responsable, metaExpedient
    // =========================================================================

    @Test
    void quanFiltramPerCodi_retornaUnicamentLEstatAmbAquellCodi() {
        Page<MetaExpedientEstatResource> pagina = metaExpedientEstatResourceService.findPage(
                null, "codi:'EST_2_2'", null, null,
                PageRequest.of(0, 20)
        );

        assertThat(pagina.getTotalElements()).isEqualTo(1);
        assertThat(pagina.getContent().get(0).getCodi()).isEqualTo("EST_2_2");
    }

    @Test
    void quanFiltramPerNom_retornaUnicamentLEstatAmbAquellNom() {
        Page<MetaExpedientEstatResource> pagina = metaExpedientEstatResourceService.findPage(
                null, "nom:'Estat 3.1'", null, null,
                PageRequest.of(0, 20)
        );

        assertThat(pagina.getTotalElements()).isEqualTo(1);
        assertThat(pagina.getContent().get(0).getNom()).isEqualTo("Estat 3.1");
    }

    @Test
    void quanFiltramPerResponsable_retornaUnicamentElsEstatsAmbAquellResponsable() {
        MetaExpedientEstatResource nouEstat = buildResourceMinim();
        nouEstat.setResponsable(ResourceReference.toResourceReference(
                testData.usuari.getCodi(), testData.usuari.getNom()));
        metaExpedientEstatResourceService.create(nouEstat, null);
        entityManager.flush();
        entityManager.clear();

        Page<MetaExpedientEstatResource> pagina = metaExpedientEstatResourceService.findPage(
                null, "responsable.id:'" + testData.usuari.getCodi() + "'", null, null,
                PageRequest.of(0, 20)
        );

        assertThat(pagina.getTotalElements()).isEqualTo(1);
        assertThat(pagina.getContent().get(0).getResponsable().getId())
                .isEqualTo(testData.usuari.getCodi());
    }

    @Test
    void quanFiltramPerMetaExpedient_retornaUnicamentElsEstatsDAquellProcediment() {
        Long metaExpedientId = testData.metaExpedients.get(0).getId();

        Page<MetaExpedientEstatResource> pagina = metaExpedientEstatResourceService.findPage(
                null, "metaExpedient.id:" + metaExpedientId, null, null,
                PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "codi"))
        );

        assertThat(pagina.getTotalElements()).isEqualTo(3);
        assertThat(pagina.getContent())
                .extracting(MetaExpedientEstatResource::getCodi)
                .containsExactly("EST_1_1", "EST_1_2", "EST_1_3");
    }

    // =========================================================================
    // CRUD: getOne, create, update, delete
    // =========================================================================

    @Test
    void quanDemanAmGetOnePerUnId_retornaLEstatCorrecte() {
        Long id = testData.estats.get(0).getId();

        MetaExpedientEstatResource resultat = metaExpedientEstatResourceService.getOne(id, null);

        assertThat(resultat).isNotNull();
        assertThat(resultat.getCodi()).isEqualTo("EST_1_1");
        assertThat(resultat.getNom()).isEqualTo("Estat 1.1");
        assertThat(resultat.getColor()).isEqualTo("#3366FF");
        assertThat(resultat.getMetaExpedient().getId())
                .isEqualTo(testData.metaExpedients.get(0).getId());
    }

    @Test
    void quanCreemUnNouEstat_laConsultaRetornaUnElementMes() {
        long totalInicial = metaExpedientEstatResourceService.findPage(
                null, null, null, null, PageRequest.of(0, 1)
        ).getTotalElements();

        metaExpedientEstatResourceService.create(buildResourceMinim(), null);
        entityManager.flush();
        entityManager.clear();

        long totalFinal = metaExpedientEstatResourceService.findPage(
                null, null, null, null, PageRequest.of(0, 1)
        ).getTotalElements();

        assertThat(totalFinal).isEqualTo(totalInicial + 1);
    }

    @Test
    void quanActualitzamElNomDUnEstat_getOneRetornaElNomActualitzat() {
        Long id = testData.estats.get(0).getId();
        MetaExpedientEstatResource existent = metaExpedientEstatResourceService.getOne(id, null);
        existent.setNom("Nom Actualitzat Estat");

        metaExpedientEstatResourceService.update(id, existent, null);
        entityManager.flush();
        entityManager.clear();

        assertThat(metaExpedientEstatResourceService.getOne(id, null).getNom())
                .isEqualTo("Nom Actualitzat Estat");
    }

    @Test
    void quanActualitzamElColorDUnEstat_getOneRetornaElColorActualitzat() {
        Long id = testData.estats.get(0).getId();
        MetaExpedientEstatResource existent = metaExpedientEstatResourceService.getOne(id, null);
        existent.setColor("#FF0000");

        metaExpedientEstatResourceService.update(id, existent, null);
        entityManager.flush();
        entityManager.clear();

        assertThat(metaExpedientEstatResourceService.getOne(id, null).getColor())
                .isEqualTo("#FF0000");
    }

    @Test
    void quanEsborramUnEstat_laConsultaRetornaMenysUnElement() {
        long totalInicial = metaExpedientEstatResourceService.findPage(
                null, null, null, null, PageRequest.of(0, 1)
        ).getTotalElements();

        Long id = testData.estats.get(0).getId();
        metaExpedientEstatResourceService.delete(id, null);
        entityManager.flush();
        entityManager.clear();

        long totalFinal = metaExpedientEstatResourceService.findPage(
                null, null, null, null, PageRequest.of(0, 1)
        ).getTotalElements();

        assertThat(totalFinal).isEqualTo(totalInicial - 1);
    }

    // =========================================================================
    // Mètode auxiliar
    // =========================================================================

    private MetaExpedientEstatResource buildResourceMinim() {
        MetaExpedientEstatResource r = new MetaExpedientEstatResource();
        r.setCodi("EST_NOU");
        r.setNom("Estat Nou");
        r.setOrdre(10);
        r.setColor("#00FF00");
        r.setMetaExpedient(ResourceReference.toResourceReference(
                testData.metaExpedients.get(0).getId(), "PROC_01"));
        return r;
    }
}
