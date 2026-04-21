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
import es.caib.ripea.service.intf.config.PropertyConfig;
import es.caib.ripea.service.intf.dto.MetaExpedientExportDto;
import es.caib.ripea.service.intf.dto.MetaExpedientRevisioEstatEnumDto;
import es.caib.ripea.service.intf.dto.TipusClassificacioEnumDto;
import es.caib.ripea.service.intf.model.MetaExpedientResource;
import es.caib.ripea.service.intf.resourceservice.MetaExpedientResourceService;
import es.caib.ripea.service.intf.service.MetaExpedientService;
import es.caib.ripea.service.intf.utils.Utils;
import es.caib.ripea.service.test.config.BaseServiceIT;

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
 *   - 8 procediments totals: PROC_01..08
 *   - PROC_01..03: actius, PROCEDIMENT, sense organGestor (ENT_TEST)
 *   - PROC_04: actiu, SERVEI, sense organGestor (ENT_TEST)
 *   - PROC_05: inactiu, PROCEDIMENT, sense organGestor (ENT_TEST)
 *   - PROC_06: actiu, PROCEDIMENT, organGestor = organs[0] (ENT_TEST)
 *   - PROC_07: actiu, PROCEDIMENT, organGestor = organs[1] (ENT_TEST)
 *   - PROC_08: actiu, PROCEDIMENT, sense organGestor (ENT_TEST2 — no apareix en les consultes)
 */
@WithMockUser(username = "usuari1", roles = {"ADMIN"})
public class MetaExpedientResourceServiceIT extends BaseServiceIT {

    @Autowired private MetaExpedientResourceService metaExpedientResourceService;
    @Autowired private MetaExpedientService metaExpedientService;

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

    // La consulta retorna només els 7 procediments de ENT_TEST, no el PROC_08 de ENT_TEST2
    @Test
    void quanConsultamProcediments_nomoesRetornaElsDeLEntitatActual() {
        Page<MetaExpedientResource> pagina = metaExpedientResourceService.findPage(
                null, null, null, null,
                PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "nom"))
        );

        assertThat(pagina.getTotalElements()).isEqualTo(7);
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

    // Desactivar un procedimiento activo hace que isActiu() devuelva false
    @Test
    void quanDesactivamUnProcedimentActiu_getOneRetornaActiuFals() {
        Long id = testData.metaExpedients.get(0).getId(); // PROC_01, actiu=true
        MetaExpedientResource resource = metaExpedientResourceService.getOne(id, null);
        assertThat(resource.isActiu()).isTrue();

        resource.setActiu(false);
        metaExpedientResourceService.update(id, resource, null);
        entityManager.flush();
        entityManager.clear();

        assertThat(metaExpedientResourceService.getOne(id, null).isActiu()).isFalse();
    }

    // Activar un procedimiento inactivo hace que isActiu() devuelva true
    @Test
    void quanActivamUnProcedimentInactiu_getOneRetornaActiuVer() {
        Long id = testData.metaExpedients.get(4).getId(); // PROC_05, actiu=false
        MetaExpedientResource resource = metaExpedientResourceService.getOne(id, null);
        assertThat(resource.isActiu()).isFalse();

        resource.setActiu(true);
        metaExpedientResourceService.update(id, resource, null);
        entityManager.flush();
        entityManager.clear();

        assertThat(metaExpedientResourceService.getOne(id, null).isActiu()).isTrue();
    }

    // =========================================================================
    // Tests de exportació i importació
    // =========================================================================
    @Test
    void quanExportamImportamUnProcediment_retornaJsonAndFindImportatByCodi() {
    	String resultat = metaExpedientService.export(testData.entitat.getId(), testData.metaExpedients.get(0).getId(), null);
    	MetaExpedientExportDto metaExpedientExport = null;
    	try {
    		metaExpedientExport = Utils.convertirJsonToMetaExpedientDto(resultat.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	assertThat(metaExpedientExport).isNotNull();
    	
    	//Emplenam alguns atributs que s'emplenaríen desde el formulari de importació:
    	metaExpedientExport.setCodi("IMPORTAT_JUNIT");
    	metaExpedientExport.setClassificacio("IMPORTAT_JUNIT");
    	
    	metaExpedientService.createFromImport(testData.entitat.getId(), metaExpedientExport, BaseConfig.ROLE_ADMIN, null);
    	
        Page<MetaExpedientResource> pagina = metaExpedientResourceService.findPage(
        		"IMPORTAT_JUNIT", null, null, null,
                PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "nom"))
        );
        
        assertThat(pagina.getTotalElements()).isEqualTo(1);

        // flush+clear needed: Hibernate initializes the `estats` lazy collection as empty for the newly
        // created entity in this same transaction, so cascade REMOVE would skip it without a DB reload.
        entityManager.flush();
        entityManager.clear();

        metaExpedientService.delete(testData.entitat.getId(), pagina.getContent().get(0).getId(), null);
        entityManager.flush();
        entityManager.clear();
        
        pagina = metaExpedientResourceService.findPage(
        		"IMPORTAT_JUNIT", null, null, null,
                PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "nom"))
        );
        
        assertThat(pagina.getTotalElements()).isEqualTo(0);
    }
    
    // =========================================================================
    // Accions: VINCULAR_GRUP, DESVINCULAR_GRUP, TOGGLE_GRUP_DEF
    // =========================================================================

    @Test
    void quanVinculamUnGrupAUnProcediment_elGrupPerDefecteEsElGrupVinculat() {
        Long id = testData.metaExpedients.get(0).getId(); // PROC_01

        MetaExpedientResource.VincularGrupFormAction params = new MetaExpedientResource.VincularGrupFormAction();
        params.setGrup(ResourceReference.toResourceReference(testData.grup.getId(), testData.grup.getCodi()));
        params.setPerDefecte(true);

        metaExpedientResourceService.artifactActionExec(id, MetaExpedientResource.ACTION_VINCULAR_GRUP_CODE, params);
        entityManager.flush();
        entityManager.clear();

        MetaExpedientResource resultat = metaExpedientResourceService.getOne(id, null);
        assertThat(resultat.getGrupPerDefecte()).isNotNull();
        assertThat(resultat.getGrupPerDefecte().getId()).isEqualTo(testData.grup.getId());
    }

    @Test
    void quanDesvincularElGrupPerDefecte_elGrupPerDefecteEsNull() {
        Long id = testData.metaExpedients.get(0).getId(); // PROC_01, grupPerDefecte=GRP_TEST

        // Primer vincular per assegurar que GRP_TEST és a la col·lecció grups
        MetaExpedientResource.VincularGrupFormAction vincularParams = new MetaExpedientResource.VincularGrupFormAction();
        vincularParams.setGrup(ResourceReference.toResourceReference(testData.grup.getId(), testData.grup.getCodi()));
        vincularParams.setPerDefecte(true);
        metaExpedientResourceService.artifactActionExec(id, MetaExpedientResource.ACTION_VINCULAR_GRUP_CODE, vincularParams);

        MetaExpedientResource.DesVincularGrupFormAction desvincularParams = new MetaExpedientResource.DesVincularGrupFormAction();
        desvincularParams.setGrup(ResourceReference.toResourceReference(testData.grup.getId(), testData.grup.getCodi()));
        metaExpedientResourceService.artifactActionExec(id, MetaExpedientResource.ACTION_DESVINCULAR_GRUP_CODE, desvincularParams);
        entityManager.flush();
        entityManager.clear();

        MetaExpedientResource resultat = metaExpedientResourceService.getOne(id, null);
        assertThat(resultat.getGrupPerDefecte()).isNull();
    }

    @Test
    void quanToggleGrupDefecteANull_elGrupPerDefecteEsNull() {
        Long id = testData.metaExpedients.get(0).getId(); // PROC_01, grupPerDefecte=GRP_TEST

        MetaExpedientResource.ToggleGrupDefecteFormAction params = new MetaExpedientResource.ToggleGrupDefecteFormAction();
        params.setGrupId(null);
        metaExpedientResourceService.artifactActionExec(id, MetaExpedientResource.ACTION_TOGGLE_GRUP_DEF_CODE, params);
        entityManager.flush();
        entityManager.clear();

        MetaExpedientResource resultat = metaExpedientResourceService.getOne(id, null);
        assertThat(resultat.getGrupPerDefecte()).isNull();
    }

    @Test
    void quanToggleGrupDefecteAmbGrupId_elGrupPerDefecteCanviaAlGrupIndicat() {
        Long id = testData.metaExpedients.get(0).getId(); // PROC_01

        MetaExpedientResource.ToggleGrupDefecteFormAction params = new MetaExpedientResource.ToggleGrupDefecteFormAction();
        params.setGrupId(testData.grup.getId());
        metaExpedientResourceService.artifactActionExec(id, MetaExpedientResource.ACTION_TOGGLE_GRUP_DEF_CODE, params);
        entityManager.flush();
        entityManager.clear();

        MetaExpedientResource resultat = metaExpedientResourceService.getOne(id, null);
        assertThat(resultat.getGrupPerDefecte()).isNotNull();
        assertThat(resultat.getGrupPerDefecte().getId()).isEqualTo(testData.grup.getId());
    }

    // =========================================================================
    // Accions: CANVIAR_DISSENY, CANVIAR_PENDENT
    // =========================================================================

    @Test
    void quanCanviarEstatDisseny_elRevisioEstatEsDisseny() {
        Long id = testData.metaExpedients.get(0).getId(); // PROC_01, revisioEstat=null

        metaExpedientResourceService.artifactActionExec(id, MetaExpedientResource.ACTION_CANVIAR_DISSENY_CODE, null);
        entityManager.flush();
        entityManager.clear();

        MetaExpedientResource resultat = metaExpedientResourceService.getOne(id, null);
        assertThat(resultat.getRevisioEstat()).isEqualTo(MetaExpedientRevisioEstatEnumDto.DISSENY);
    }

    @Test
    void quanCanviarEstatPendentAmbRevisioActiva_elRevisioEstatEsPendent() {
        Mockito.lenient().when(configHelper.getAsBoolean(PropertyConfig.METAEXPEDIENT_REVISIO_ACTIVA)).thenReturn(true);
        Long id = testData.metaExpedients.get(0).getId(); // PROC_01, revisioEstat=null

        metaExpedientResourceService.artifactActionExec(id, MetaExpedientResource.ACTION_CANVIAR_PENDENT_CODE, null);
        entityManager.flush();
        entityManager.clear();

        MetaExpedientResource resultat = metaExpedientResourceService.getOne(id, null);
        assertThat(resultat.getRevisioEstat()).isEqualTo(MetaExpedientRevisioEstatEnumDto.PENDENT);
    }

    // =========================================================================
    // Accions: CLONAR
    // =========================================================================

    @Test
    void quanClonamUnProcediment_elClonApareixALaConsulta() {
        Long id = testData.metaExpedients.get(0).getId(); // PROC_01

        MetaExpedientResource.ClonarFormAction params = new MetaExpedientResource.ClonarFormAction();
        params.setCodi("PROC_CLON");
        params.setClassificacio("SIA_CLON");
        metaExpedientResourceService.artifactActionExec(id, MetaExpedientResource.ACTION_CLONAR_CODE, params);
        entityManager.flush();
        entityManager.clear();

        Page<MetaExpedientResource> pagina = metaExpedientResourceService.findPage(
                "PROC_CLON", null, null, null, PageRequest.of(0, 10));
        assertThat(pagina.getTotalElements()).isEqualTo(1);
        assertThat(pagina.getContent().get(0).getCodi()).isEqualTo("PROC_CLON");
    }

    // =========================================================================
    // Accions: CHANGE_REVISIO
    // =========================================================================

    @Test
    void quanChangeRevisioARevisat_elRevisioEstatEsRevisat() {
        Long id = testData.metaExpedients.get(0).getId(); // PROC_01

        MetaExpedientResource.RevisioChangeFormAction params = new MetaExpedientResource.RevisioChangeFormAction();
        params.setRevisioEstat(MetaExpedientRevisioEstatEnumDto.REVISAT);
        metaExpedientResourceService.artifactActionExec(id, MetaExpedientResource.ACTION_CHANGE_REVISIO_CODE, params);
        entityManager.flush();
        entityManager.clear();

        MetaExpedientResource resultat = metaExpedientResourceService.getOne(id, null);
        assertThat(resultat.getRevisioEstat()).isEqualTo(MetaExpedientRevisioEstatEnumDto.REVISAT);
    }

    @Test
    void quanChangeRevisioAPendent_elRevisioEstatEsPendent() {
        Long id = testData.metaExpedients.get(0).getId(); // PROC_01

        MetaExpedientResource.RevisioChangeFormAction params = new MetaExpedientResource.RevisioChangeFormAction();
        params.setRevisioEstat(MetaExpedientRevisioEstatEnumDto.PENDENT);
        metaExpedientResourceService.artifactActionExec(id, MetaExpedientResource.ACTION_CHANGE_REVISIO_CODE, params);
        entityManager.flush();
        entityManager.clear();

        MetaExpedientResource resultat = metaExpedientResourceService.getOne(id, null);
        assertThat(resultat.getRevisioEstat()).isEqualTo(MetaExpedientRevisioEstatEnumDto.PENDENT);
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
