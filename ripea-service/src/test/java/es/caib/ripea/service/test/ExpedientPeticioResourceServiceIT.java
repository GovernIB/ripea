package es.caib.ripea.service.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithMockUser;

import es.caib.ripea.persistence.entity.ExpedientPeticioEntity;
import es.caib.ripea.persistence.entity.GrupEntity;
import es.caib.ripea.persistence.entity.MetaExpedientEntity;
import es.caib.ripea.persistence.entity.MetaExpedientOrganGestorEntity;
import es.caib.ripea.persistence.entity.MetaNodeEntity;
import es.caib.ripea.persistence.entity.OrganGestorEntity;
import es.caib.ripea.persistence.entity.RegistreEntity;
import es.caib.ripea.persistence.repository.ExpedientPeticioRepository;
import es.caib.ripea.persistence.repository.RegistreRepository;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.dto.ExpedientPeticioEstatEnumDto;
import es.caib.ripea.service.intf.dto.ExpedientPeticioFiltreDto;
import es.caib.ripea.service.intf.dto.ExpedientPeticioListDto;
import es.caib.ripea.service.intf.dto.PaginaDto;
import es.caib.ripea.service.intf.model.ExpedientPeticioResource;
import es.caib.ripea.service.intf.resourceservice.ExpedientPeticioResourceService;
import es.caib.ripea.service.intf.service.ExpedientPeticioService;
import es.caib.ripea.service.intf.utils.Utils;
import es.caib.ripea.service.test.config.BaseServiceIT;

/**
 * Tests d'integració que contrasten el llistat d'anotacions de la interfície REACT
 * (ExpedientPeticioResourceServiceImpl.additionalSpringFilter, named query
 * "LLISTAT_ANOTACIONS") amb el de la interfície JSP (ExpedientPeticioServiceImpl.findAmbFiltre
 * -> ExpedientPeticioRepository.findByEntitatAndFiltre).
 *
 * Tots dos camins comparteixen la mateixa font de permisos
 * (ExpedientPeticioHelper.findPermisosPerAnotacions), de manera que aquests tests fixen que
 * la TRADUCCIÓ a SQL (JPQL antic vs spring-filter nou) retorni EL MATEIX conjunt d'anotacions
 * a nivell de permisos, per als rols rellevants i per al cas sense permisos.
 *
 * Per evitar la divergència coneguda del filtre d'entitat (l'antic filtra per la FK
 * registre.entitat i el nou per registre.entitatCodi = entitat.unitatArrel), totes les
 * anotacions es creen amb entitatCodi = unitatArrel, que és l'escenari real de producció.
 */
@WithMockUser(username = "usuari1", roles = {"ADMIN"})
public class ExpedientPeticioResourceServiceIT extends BaseServiceIT {

    @Autowired private ExpedientPeticioResourceService expedientPeticioResourceService; // REACT
    @Autowired private ExpedientPeticioService expedientPeticioService;                 // JSP
    @Autowired private ExpedientPeticioRepository expedientPeticioRepository;
    @Autowired private RegistreRepository registreRepository;

    @BeforeEach
    @Override
    public void setUpTestData() {
        super.setUpTestData();
        Mockito.lenient().when(configHelper.getEntitatActualCodi()).thenReturn("ENT_TEST");
    }

    // =========================================================================
    // 1) tothom: REACT i JSP retornen el mateix conjunt (filtre per procediment)
    // =========================================================================

    @Test
    void donatRolTothom_elLlistatReactIElJspRetornenElMateixConjunt() {
        forcarRol(BaseConfig.ROLE_USER, null);

        MetaExpedientEntity procPermes = testData.metaExpedients.get(0);    // PROC_01, gestioAmbGrups=false
        MetaExpedientEntity procNoPermes = testData.metaExpedients.get(1);  // PROC_02
        String destiCodi = testData.organs.get(0).getCodi();

        // tothom només té permís CREATE/WRITE sobre PROC_01 i cap permís de grup
        stubPermisosTothom(Arrays.asList(procPermes.getId()), new ArrayList<>());

        Long aPermes = crearAnotacio("PERMES", procPermes, null, destiCodi);
        crearAnotacio("NO_PERMES", procNoPermes, null, destiCodi);  // procediment no permès -> exclòs
        crearAnotacio("SENSE_PROC", null, null, destiCodi);         // sense procediment -> exclòs (metaExpedient IN ...)
        flushAndClear();

        Set<Long> jspIds = jspIds(BaseConfig.ROLE_USER, null);
        Set<Long> reactIds = reactIds();

        assertThat(reactIds).isEqualTo(jspIds);
        assertThat(reactIds).containsExactlyInAnyOrder(aPermes);
    }

    // =========================================================================
    // 2) IPA_ORGAN_ADMIN: REACT i JSP retornen el mateix conjunt (filtre per destiCodi)
    // =========================================================================

    @Test
    void donatRolOrganAdmin_elLlistatReactIElJspRetornenElMateixConjunt() {
        OrganGestorEntity organActual = testData.organs.get(0);   // ORG_TEST_1
        OrganGestorEntity altreOrgan = testData.organs.get(1);    // ORG_TEST_2
        forcarRol(BaseConfig.ROLE_ORGAN_ADMIN, organActual.getCodi());

        MetaExpedientEntity proc = testData.metaExpedients.get(0);

        // L'organigrama de test és buit (no hi ha òrgan amb codi = unitatArrel), de manera que
        // getCodisOrgansFills retorna nomes [ORG_TEST_1]. El filtre és registre.destiCodi IN ('ORG_TEST_1').
        Long aDestiOk = crearAnotacio("DESTI_OK", proc, null, organActual.getCodi());
        Long aSenseProcDestiOk = crearAnotacio("SENSE_PROC_DESTI_OK", null, null, organActual.getCodi());
        crearAnotacio("DESTI_ALTRE", proc, null, altreOrgan.getCodi());  // destí d'un altre òrgan -> exclòs
        flushAndClear();

        Set<Long> jspIds = jspIds(BaseConfig.ROLE_ORGAN_ADMIN, organActual.getId());
        Set<Long> reactIds = reactIds();

        assertThat(reactIds).isEqualTo(jspIds);
        assertThat(reactIds).containsExactlyInAnyOrder(aDestiOk, aSenseProcDestiOk);
    }

    // =========================================================================
    // 3) Sense permisos (tothom): tots dos llistats retornen 0 resultats
    // =========================================================================

    @Test
    void donatRolTothomSensePermisos_elsDosLlistatsRetornenZero() {
        forcarRol(BaseConfig.ROLE_USER, null);

        // Cap permís per cap via -> procedimentsPermesos = null
        stubPermisosTothom(new ArrayList<>(), new ArrayList<>());

        // Existeix una anotació que, amb permisos, hauria de coincidir
        crearAnotacio("PROC01", testData.metaExpedients.get(0), null, testData.organs.get(0).getCodi());
        flushAndClear();

        Set<Long> jspIds = jspIds(BaseConfig.ROLE_USER, null);
        Set<Long> reactIds = reactIds();

        assertThat(jspIds).isEmpty();
        assertThat(reactIds).isEmpty();
    }

    // =========================================================================
    // Auxiliars
    // =========================================================================

    private void forcarRol(String rol, String organCodi) {
        Mockito.lenient().when(configHelper.getRolActual()).thenReturn(rol);
        Mockito.lenient().when(configHelper.getOrganActualCodi()).thenReturn(organCodi);
    }

    /**
     * Stubeja les crides ACL que fa la branca tothom de findPermisosPerAnotacions
     * (via getCreateWritePermesos -> findAmbPermis) i la consulta de grups amb READ.
     * Cal stubejar-les totes: per defecte el mock retornaria null i provocaria NPE.
     *
     * @param procedimentsCreateWrite ids de procediments amb permís directe CREATE/WRITE
     * @param grupsRead               ids de grups amb permís READ (idsGrupsPermesos)
     */
    private void stubPermisosTothom(List<Long> procedimentsCreateWrite, List<Long> grupsRead) {
        // findAmbPermis: procediments amb permís directe (VIA 1)
        Mockito.when(permisosHelper.getObjectsIdsWithPermission(eq(MetaNodeEntity.class), any()))
                .thenReturn(procedimentsCreateWrite);
        // findAmbPermis: òrgans, parells procediment-òrgan i grups (no usats en aquest escenari)
        Mockito.when(permisosHelper.getObjectsIdsWithPermission(eq(OrganGestorEntity.class), any()))
                .thenReturn(new ArrayList<>());
        Mockito.when(permisosHelper.getObjectsIdsWithPermission(eq(MetaExpedientOrganGestorEntity.class), any()))
                .thenReturn(new ArrayList<>());
        Mockito.when(permisosHelper.getObjectsIdsWithPermission(eq(GrupEntity.class), any()))
                .thenReturn(new ArrayList<>());
        Mockito.when(permisosHelper.getObjectsIdsWithTwoPermissions(any(), any(), any()))
                .thenReturn(new ArrayList<>());
        // findPermisosPerAnotacions (tothom): grups amb READ -> idsGrupsPermesos (sobrecàrrega de 4 args)
        Mockito.when(permisosHelper.getObjectsIdsWithPermission(eq(GrupEntity.class), any(), any(), any()))
                .thenReturn(grupsRead);
    }

    private Set<Long> jspIds(String rol, Long organId) {
        PaginaDto<ExpedientPeticioListDto> pagina = expedientPeticioService.findAmbFiltre(
                testData.entitat.getId(),
                new ExpedientPeticioFiltreDto(),
                Utils.sensePaginacio(),
                rol,
                organId);
        return pagina.getContingut().stream()
                .map(ExpedientPeticioListDto::getId)
                .collect(Collectors.toSet());
    }

    private Set<Long> reactIds() {
        Page<ExpedientPeticioResource> pagina = expedientPeticioResourceService.findPage(
                null,
                null,
                new String[] {"LLISTAT_ANOTACIONS"},
                null,
                PageRequest.of(0, 100));
        return pagina.getContent().stream()
                .map(ExpedientPeticioResource::getId)
                .collect(Collectors.toSet());
    }

    private Long crearAnotacio(
            String suffix,
            MetaExpedientEntity metaExpedient,
            GrupEntity grup,
            String destiCodi) {
        RegistreEntity registre = RegistreEntity.getBuilder(
                "ASS",                                   // assumpteTipusCodi
                new Date(),                              // data
                testData.entitat.getUnitatArrel(),       // entitatCodi = unitatArrel (escenari producció)
                "REG_" + suffix,                         // identificador
                "ca",                                    // idiomaCodi
                "LL",                                    // llibreCodi
                "OF",                                    // oficinaCodi
                destiCodi,                               // destiCodi
                testData.entitat
        ).extracte("Extracte " + suffix).build();
        registre = registreRepository.save(registre);

        ExpedientPeticioEntity peticio = ExpedientPeticioEntity.getBuilder(
                "ANOT_" + suffix,
                "CLAU_" + suffix,
                new Date(),
                ExpedientPeticioEstatEnumDto.PENDENT
        ).build();
        peticio.updateRegistre(registre);
        peticio.updateMetaExpedient(metaExpedient);
        peticio.setGrup(grup);
        return expedientPeticioRepository.save(peticio).getId();
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
