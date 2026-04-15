package es.caib.ripea.service.test;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.test.context.support.WithMockUser;

import es.caib.ripea.service.helper.ConfigHelper;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.dto.TipusClassificacioEnumDto;
import es.caib.ripea.service.intf.model.MetaExpedientResource;
import es.caib.ripea.service.intf.resourceservice.MetaExpedientResourceService;
import es.caib.ripea.service.test.config.BaseServiceTest;

/**
 * Tests d'integració per a MetaExpedientResourceService.
 *
 * Cobreix: findPage (ordenació i filtre), create, update i delete.
 *
 * ConfigHelper es mocka perquè retorni l'entitat "ENT_TEST" i el rol "IPA_ADMIN",
 * que és el que utilitza additionalSpringFilter per filtrar per entitat i determinar
 * quins procediments són visibles.
 *
 * Conjunt de dades base (TestDataFactory):
 *   - 7 procediments: PROC_01..07
 *   - PROC_01..03: actius, PROCEDIMENT, sense organGestor
 *   - PROC_04: actiu, SERVEI, sense organGestor
 *   - PROC_05: inactiu, PROCEDIMENT, sense organGestor
 *   - PROC_06: actiu, PROCEDIMENT, organGestor = organs[0]
 *   - PROC_07: actiu, PROCEDIMENT, organGestor = organs[1]
 */
@WithMockUser(username = "usuari1", roles = {"ADMIN"})
public class MetaExpedientResourceServiceTest extends BaseServiceTest {

    @Autowired
    private MetaExpedientResourceService metaExpedientResourceService;

    @MockBean
    private ConfigHelper configHelper;

    @BeforeEach
    @Override
    public void setUpTestData() {
        super.setUpTestData();

        Mockito.lenient().when(configHelper.getEntitatActualCodi()).thenReturn("ENT_TEST");
        Mockito.lenient().when(configHelper.getRolActual()).thenReturn(BaseConfig.ROLE_ADMIN);
        Mockito.lenient().when(configHelper.getOrganActualCodi()).thenReturn(null);
    }

    // =========================================================================
    // Tests d'ordenació per nom (existents, actualitzats per a 7 procediments)
    // =========================================================================

    // La página retorna 7 procedimientos en total
    @Test
    void quanDemanemLaPrimeraPaginaOrdenadaPerNom_retornaSetProcediments() {
        Page<MetaExpedientResource> pagina = metaExpedientResourceService.findPage(
                null, null, null, null,
                PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "nom"))
        );

        assertThat(pagina.getTotalElements()).isEqualTo(7);
        assertThat(pagina.getContent()).hasSize(7);
    }

    // Los procedimientos se ordenan ascendentemente por nombre
    @Test
    void quanDemanemLaPrimeraPaginaOrdenadaPerNom_elsResultatsSonEnOrdreCrescent() {
        Page<MetaExpedientResource> pagina = metaExpedientResourceService.findPage(
                null, null, null, null,
                PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "nom"))
        );

        assertThat(pagina.getContent())
                .extracting(MetaExpedientResource::getNom)
                .containsExactly(
                        "Procediment 1", "Procediment 2", "Procediment 3",
                        "Procediment 4", "Procediment 5", "Procediment 6", "Procediment 7"
                );
    }

    // Todos los procedimientos pertenecen a la misma entidad
    @Test
    void quanDemanemLaPrimeraPaginaOrdenadaPerNom_totsTenenLaMateixaEntitat() {
        Page<MetaExpedientResource> pagina = metaExpedientResourceService.findPage(
                null, null, null, null,
                PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "nom"))
        );

        Long entitatId = testData.entitat.getId();
        assertThat(pagina.getContent())
                .extracting(r -> r.getEntitat().getId())
                .containsOnly(entitatId);
    }

    // =========================================================================
    // Tests d'ordenació per altres camps
    // =========================================================================

    // Los procedimientos se ordenan ascendentemente por código
    @Test
    void quanOrdenamPerCodiAsc_elsResultatsSonEnOrdreCrescent() {
        Page<MetaExpedientResource> pagina = metaExpedientResourceService.findPage(
                null, null, null, null,
                PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "codi"))
        );

        assertThat(pagina.getTotalElements()).isEqualTo(7);
        assertThat(pagina.getContent())
                .extracting(MetaExpedientResource::getCodi)
                .containsExactly(
                        "PROC_01", "PROC_02", "PROC_03",
                        "PROC_04", "PROC_05", "PROC_06", "PROC_07"
                );
    }

    // Los procedimientos se ordenan ascendentemente por clasificación
    @Test
    void quanOrdenamPerClassificacioAsc_elsResultatsSonEnOrdreCrescent() {
        Page<MetaExpedientResource> pagina = metaExpedientResourceService.findPage(
                null, null, null, null,
                PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "classificacio"))
        );

        assertThat(pagina.getTotalElements()).isEqualTo(7);
        assertThat(pagina.getContent())
                .extracting(MetaExpedientResource::getClassificacio)
                .containsExactly(
                        "SIA001", "SIA002", "SIA003",
                        "SIA004", "SIA005", "SIA006", "SIA007"
                );
    }

    // Los procedimientos se ordenan ascendentemente por serie documental
    @Test
    void quanOrdenamPerSerieDocumentalAsc_elsResultatsSonEnOrdreCrescent() {
        Page<MetaExpedientResource> pagina = metaExpedientResourceService.findPage(
                null, null, null, null,
                PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "serieDocumental"))
        );

        assertThat(pagina.getTotalElements()).isEqualTo(7);
        assertThat(pagina.getContent())
                .extracting(MetaExpedientResource::getSerieDocumental)
                .containsExactly(
                        "SERIE_01", "SERIE_02", "SERIE_03",
                        "SERIE_04", "SERIE_05", "SERIE_06", "SERIE_07"
                );
    }

    // Los procedimientos con organGestor se ordenan por id de órgano gestor
    @Test
    void quanOrdenamPerOrganGestorAmbFiltreNoNull_elsOrganGestorsSortosEnOrdreCrescent() {
        // Filtra per organGestor is not null i ordena per organGestor (→ organGestor.id ASC)
        // ORG_TEST_1 s'insereix primer (id menor), ORG_TEST_2 després (id major)
        Page<MetaExpedientResource> pagina = metaExpedientResourceService.findPage(
                null,
                "organGestor is not null",
                null, null,
                PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "organGestor"))
        );

        assertThat(pagina.getTotalElements()).isEqualTo(2);
        assertThat(pagina.getContent())
                .extracting(r -> r.getOrganGestor().getId())
                .containsExactly(
                        testData.organs.get(0).getId(),
                        testData.organs.get(1).getId()
                );
    }

    // =========================================================================
    // Tests de filtre
    // =========================================================================

    // Filtro por tipo SERVEI retorna únicamente PROC_04
    @Test
    void quanFiltramPerTipusServei_retornaUnicamentElServei() {
        Page<MetaExpedientResource> pagina = metaExpedientResourceService.findPage(
                null,
                "tipusProcedimentServei:'SERVEI'",
                null, null,
                PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "nom"))
        );

        assertThat(pagina.getTotalElements()).isEqualTo(1);
        assertThat(pagina.getContent().get(0).getCodi()).isEqualTo("PROC_04");
    }

    // Filtro por código retorna únicamente el procedimiento con ese código
    @Test
    void quanFiltramPerCodi_retornaUnicamentElProcedimentAmbAquellCodi() {
        Page<MetaExpedientResource> pagina = metaExpedientResourceService.findPage(
                null,
                "codi:'PROC_01'",
                null, null,
                PageRequest.of(0, 10)
        );

        assertThat(pagina.getTotalElements()).isEqualTo(1);
        assertThat(pagina.getContent().get(0).getCodi()).isEqualTo("PROC_01");
    }

    // Filtro por nombre retorna únicamente el procedimiento con ese nombre
    @Test
    void quanFiltramPerNom_retornaUnicamentElProcedimentAmbAquellNom() {
        Page<MetaExpedientResource> pagina = metaExpedientResourceService.findPage(
                null,
                "nom:'Procediment 2'",
                null, null,
                PageRequest.of(0, 10)
        );

        assertThat(pagina.getTotalElements()).isEqualTo(1);
        assertThat(pagina.getContent().get(0).getNom()).isEqualTo("Procediment 2");
    }

    // Filtro por clasificación retorna únicamente el procedimiento con esa clasificación
    @Test
    void quanFiltramPerClassificacio_retornaUnicamentElProcedimentAmbAquellaClassificacio() {
        Page<MetaExpedientResource> pagina = metaExpedientResourceService.findPage(
                null,
                "classificacio:'SIA003'",
                null, null,
                PageRequest.of(0, 10)
        );

        assertThat(pagina.getTotalElements()).isEqualTo(1);
        assertThat(pagina.getContent().get(0).getClassificacio()).isEqualTo("SIA003");
    }

    // Filtro por actiu:false retorna únicamente PROC_05 (inactivo)
    @Test
    void quanFiltramPerActiuFals_retornaUnicamentElProcedimentInactiu() {
        Page<MetaExpedientResource> pagina = metaExpedientResourceService.findPage(
                null,
                "actiu:false",
                null, null,
                PageRequest.of(0, 10)
        );

        assertThat(pagina.getTotalElements()).isEqualTo(1);
        assertThat(pagina.getContent().get(0).getCodi()).isEqualTo("PROC_05");
    }

    // Filtro organGestor null retorna los 5 procedimientos comunes (PROC_01..05)
    @Test
    void quanFiltramPerAmbitComus_retornaElsProcedimentsSenseOrganGestor() {
        // Ambit COMUNS = procediments sense organGestor (PROC_01..05)
        Page<MetaExpedientResource> pagina = metaExpedientResourceService.findPage(
                null,
                "organGestor is null",
                null, null,
                PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "codi"))
        );

        assertThat(pagina.getTotalElements()).isEqualTo(5);
        assertThat(pagina.getContent())
                .extracting(MetaExpedientResource::getCodi)
                .containsExactly("PROC_01", "PROC_02", "PROC_03", "PROC_04", "PROC_05");
    }

    // Filtro organGestor not null retorna PROC_06 y PROC_07
    @Test
    void quanFiltramPerAmbitAssignatsAOrgan_retornaElsProcedimentsAmbOrganGestor() {
        // Ambit ASSIGNATS_A_ORGAN = procediments amb organGestor (PROC_06, PROC_07)
        Page<MetaExpedientResource> pagina = metaExpedientResourceService.findPage(
                null,
                "organGestor is not null",
                null, null,
                PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "codi"))
        );

        assertThat(pagina.getTotalElements()).isEqualTo(2);
        assertThat(pagina.getContent())
                .extracting(MetaExpedientResource::getCodi)
                .containsExactly("PROC_06", "PROC_07");
    }

    // Filtro por id de órgano retorna únicamente los procedimientos de ese órgano
    @Test
    void quanFiltramPerOrganGestor_retornaUnicamentElsProcedimentsDeAquellOrgan() {
        Long organ1Id = testData.organs.get(0).getId();

        Page<MetaExpedientResource> pagina = metaExpedientResourceService.findPage(
                null,
                "organGestor.id:" + organ1Id,
                null, null,
                PageRequest.of(0, 10)
        );

        assertThat(pagina.getTotalElements()).isEqualTo(1);
        assertThat(pagina.getContent().get(0).getCodi()).isEqualTo("PROC_06");
    }

    // =========================================================================
    // Tests CRUD: create, update, delete
    // =========================================================================

    // Crear un procedimiento incrementa el total en 1
    @Test
    void quanCreemUnNouProcedimentAmbElsCampsMinims_laConsultaRetornaUnElementMes() {
        long totalInicial = metaExpedientResourceService.findPage(
                null, null, null, null, PageRequest.of(0, 1)
        ).getTotalElements();

        MetaExpedientResource nou = buildResourceMinim();
        metaExpedientResourceService.create(nou, null);
        entityManager.flush();
        entityManager.clear();

        long totalFinal = metaExpedientResourceService.findPage(
                null, null, null, null, PageRequest.of(0, 1)
        ).getTotalElements();

        assertThat(totalFinal).isEqualTo(totalInicial + 1);
    }

    // Actualizar el nombre de un procedimiento es persistente
    @Test
    void quanCreemIActualitzamElNomDUnProcediment_getOneRetornaElNomActualitzat() {
        MetaExpedientResource nou = buildResourceMinim();
        MetaExpedientResource creat = metaExpedientResourceService.create(nou, null);
        entityManager.flush();
        entityManager.clear();

        MetaExpedientResource existent = metaExpedientResourceService.getOne(creat.getId(), null);
        existent.setNom("Nom Actualitzat");
        metaExpedientResourceService.update(creat.getId(), existent, null);
        entityManager.flush();
        entityManager.clear();

        MetaExpedientResource actualitzat = metaExpedientResourceService.getOne(creat.getId(), null);
        assertThat(actualitzat.getNom()).isEqualTo("Nom Actualitzat");
    }

    // Borrar un procedimiento devuelve el total al valor inicial
    @Test
    void quanCreemIEsborrамUnProcediment_laConsultaRetornaNomesElsOriginals() {
        long totalInicial = metaExpedientResourceService.findPage(
                null, null, null, null, PageRequest.of(0, 1)
        ).getTotalElements();

        MetaExpedientResource nou = buildResourceMinim();
        MetaExpedientResource creat = metaExpedientResourceService.create(nou, null);
        entityManager.flush();
        entityManager.clear();

        metaExpedientResourceService.delete(creat.getId(), null);
        entityManager.flush();
        entityManager.clear();

        long totalFinal = metaExpedientResourceService.findPage(
                null, null, null, null, PageRequest.of(0, 1)
        ).getTotalElements();

        assertThat(totalFinal).isEqualTo(totalInicial);
    }

    // =========================================================================
    // Mètode auxiliar
    // =========================================================================

    private MetaExpedientResource buildResourceMinim() {
        MetaExpedientResource r = new MetaExpedientResource();
        r.setCodi("PROC_NOU");
        r.setNom("Procediment Nou");
        r.setTipusClassificacio(TipusClassificacioEnumDto.SIA);
        r.setClassificacio("SIA_NOU");
        r.setSerieDocumental("SERIE_NOU");
        return r;
    }
}
