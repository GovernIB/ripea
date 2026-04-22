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
import es.caib.ripea.service.intf.dto.DocumentNtiEstadoElaboracionEnumDto;
import es.caib.ripea.service.intf.dto.MetaDocumentFirmaFluxTipusEnumDto;
import es.caib.ripea.service.intf.dto.MetaDocumentFirmaSequenciaTipusEnumDto;
import es.caib.ripea.service.intf.dto.MultiplicitatEnumDto;
import es.caib.ripea.service.intf.dto.NtiOrigenEnumDto;
import es.caib.ripea.service.intf.model.MetaDocumentResource;
import es.caib.ripea.service.intf.resourceservice.MetaDocumentResourceService;
import es.caib.ripea.service.test.config.BaseServiceIT;

/**
 * Tests d'integració per a MetaDocumentResourceService.
 *
 * Cobreix: findPage (ordenació i filtre ràpid), create, update, delete i getOne.
 *
 * Conjunt de dades base (TestDataFactory):
 *   - 9 meta-documents: METADOC_1_1..METADOC_3_3 (3 per cada un dels 3 procediments base)
 *   - Tots pertanyen a ENT_TEST, tots actius, multiplicitat M_0_N
 */
@WithMockUser(username = "usuari1", roles = {"ADMIN"})
public class MetaDocumentResourceServiceIT extends BaseServiceIT {

    @Autowired private MetaDocumentResourceService metaDocumentResourceService;

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
    void quanConsultamMetaDocuments_retornaElsDeLEntitatActual() {
        Page<MetaDocumentResource> pagina = metaDocumentResourceService.findPage(
                null, null, null, null,
                PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "codi"))
        );

        assertThat(pagina.getTotalElements()).isEqualTo(9);
    }

    @Test
    void quanConsultamMetaDocuments_totsTenenLaMateixaEntitat() {
        Page<MetaDocumentResource> pagina = metaDocumentResourceService.findPage(
                null, null, null, null,
                PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "codi"))
        );

        Long entitatId = testData.entitat.getId();
        assertThat(pagina.getContent())
                .extracting(r -> r.getEntitat().getId())
                .containsOnly(entitatId);
    }

    // =========================================================================
    // Ordenació
    // =========================================================================

    @Test
    void quanOrdenamPerCodiAsc_elsResultatsSonEnOrdreCrescent() {
        Page<MetaDocumentResource> pagina = metaDocumentResourceService.findPage(
                null, null, null, null,
                PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "codi"))
        );

        assertThat(pagina.getContent())
                .extracting(MetaDocumentResource::getCodi)
                .containsExactly(
                        "METADOC_1_1", "METADOC_1_2", "METADOC_1_3",
                        "METADOC_2_1", "METADOC_2_2", "METADOC_2_3",
                        "METADOC_3_1", "METADOC_3_2", "METADOC_3_3"
                );
    }

    @Test
    void quanOrdenamPerCodiDesc_elsResultatsSonEnOrdreDecrescent() {
        Page<MetaDocumentResource> pagina = metaDocumentResourceService.findPage(
                null, null, null, null,
                PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "codi"))
        );

        assertThat(pagina.getContent())
                .extracting(MetaDocumentResource::getCodi)
                .containsExactly(
                        "METADOC_3_3", "METADOC_3_2", "METADOC_3_1",
                        "METADOC_2_3", "METADOC_2_2", "METADOC_2_1",
                        "METADOC_1_3", "METADOC_1_2", "METADOC_1_1"
                );
    }

    @Test
    void quanOrdenamPerNomAsc_elsResultatsSonEnOrdreCrescent() {
        Page<MetaDocumentResource> pagina = metaDocumentResourceService.findPage(
                null, null, null, null,
                PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "nom"))
        );

        assertThat(pagina.getContent())
                .extracting(MetaDocumentResource::getNom)
                .containsExactly(
                        "Meta-document 1.1", "Meta-document 1.2", "Meta-document 1.3",
                        "Meta-document 2.1", "Meta-document 2.2", "Meta-document 2.3",
                        "Meta-document 3.1", "Meta-document 3.2", "Meta-document 3.3"
                );
    }

    @Test
    void quanOrdenamPerMultiplicitatAsc_noLlencaExcepcioIRetornaTots() {
        Page<MetaDocumentResource> pagina = metaDocumentResourceService.findPage(
                null, null, null, null,
                PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "multiplicitat"))
        );

        // Tots amb M_0_N: l'ordre intern pot variar, però han de sortir els 9
        assertThat(pagina.getTotalElements()).isEqualTo(9);
        assertThat(pagina.getContent())
                .extracting(MetaDocumentResource::getMultiplicitat)
                .containsOnly(MultiplicitatEnumDto.M_0_N);
    }

    @Test
    void quanOrdenamPerActiuAsc_noLlencaExcepcioIRetornaTots() {
        Page<MetaDocumentResource> pagina = metaDocumentResourceService.findPage(
                null, null, null, null,
                PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "actiu"))
        );

        // Tots actius: l'ordre intern pot variar, però han de sortir els 9
        assertThat(pagina.getTotalElements()).isEqualTo(9);
        assertThat(pagina.getContent())
                .extracting(MetaDocumentResource::isActiu)
                .containsOnly(true);
    }

    // =========================================================================
    // Filtre ràpid (quickFilter)
    // =========================================================================

    @Test
    void quanFiltramPerQuickFilterCodi_retornaResultatCoincident() {
        Page<MetaDocumentResource> pagina = metaDocumentResourceService.findPage(
                "METADOC_1_1", null, null, null,
                PageRequest.of(0, 20)
        );

        assertThat(pagina.getTotalElements()).isEqualTo(1);
        assertThat(pagina.getContent().get(0).getCodi()).isEqualTo("METADOC_1_1");
    }

    @Test
    void quanFiltramPerQuickFilterNom_retornaResultatCoincident() {
        Page<MetaDocumentResource> pagina = metaDocumentResourceService.findPage(
                "Meta-document 2.3", null, null, null,
                PageRequest.of(0, 20)
        );

        assertThat(pagina.getTotalElements()).isEqualTo(1);
        assertThat(pagina.getContent().get(0).getNom()).isEqualTo("Meta-document 2.3");
    }

    @Test
    void quanFiltramPerQuickFilterParcial_retornaVarisResultats() {
        // "METADOC_1" coincideix amb METADOC_1_1, METADOC_1_2, METADOC_1_3
        Page<MetaDocumentResource> pagina = metaDocumentResourceService.findPage(
                "METADOC_1", null, null, null,
                PageRequest.of(0, 20)
        );

        assertThat(pagina.getTotalElements()).isEqualTo(3);
    }

    // =========================================================================
    // Filtre per camp (springFilter)
    // =========================================================================

    @Test
    void quanFiltramPerCodi_retornaUnicamentElMetaDocumentAmbAquellCodi() {
        Page<MetaDocumentResource> pagina = metaDocumentResourceService.findPage(
                null, "codi:'METADOC_2_2'", null, null,
                PageRequest.of(0, 20)
        );

        assertThat(pagina.getTotalElements()).isEqualTo(1);
        assertThat(pagina.getContent().get(0).getCodi()).isEqualTo("METADOC_2_2");
    }

    @Test
    void quanFiltramPerNom_retornaUnicamentElMetaDocumentAmbAquellNom() {
        Page<MetaDocumentResource> pagina = metaDocumentResourceService.findPage(
                null, "nom:'Meta-document 3.1'", null, null,
                PageRequest.of(0, 20)
        );

        assertThat(pagina.getTotalElements()).isEqualTo(1);
        assertThat(pagina.getContent().get(0).getNom()).isEqualTo("Meta-document 3.1");
    }

    @Test
    void quanFiltramPerMultiplicitat_retornaElsMetaDocumentsAmbAquellaMultiplicitat() {
        Page<MetaDocumentResource> pagina = metaDocumentResourceService.findPage(
                null, "multiplicitat:'M_0_N'", null, null,
                PageRequest.of(0, 20)
        );

        assertThat(pagina.getTotalElements()).isEqualTo(9);
    }

    @Test
    void quanFiltramPerActiuTrue_retornaElsMetaDocumentsActius() {
        Page<MetaDocumentResource> pagina = metaDocumentResourceService.findPage(
                null, "actiu:true", null, null,
                PageRequest.of(0, 20)
        );

        assertThat(pagina.getTotalElements()).isEqualTo(9);
        assertThat(pagina.getContent())
                .extracting(MetaDocumentResource::isActiu)
                .containsOnly(true);
    }

    @Test
    void quanFiltramPerMetaExpedient_retornaUnicamentElsMetaDocumentsDeAquellProcediment() {
        Long metaExpedientId = testData.metaExpedients.get(0).getId(); // PROC_01

        Page<MetaDocumentResource> pagina = metaDocumentResourceService.findPage(
                null, "metaExpedient.id:" + metaExpedientId, null, null,
                PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "codi"))
        );

        assertThat(pagina.getTotalElements()).isEqualTo(3);
        assertThat(pagina.getContent())
                .extracting(MetaDocumentResource::getCodi)
                .containsExactly("METADOC_1_1", "METADOC_1_2", "METADOC_1_3");
    }

    // =========================================================================
    // CRUD: getOne, create, update, delete
    // =========================================================================

    @Test
    void quanDemanамGetOnePerUnId_retornaElMetaDocumentCorrecte() {
        Long id = testData.metaDocuments.get(0).getId(); // METADOC_1_1

        MetaDocumentResource resultat = metaDocumentResourceService.getOne(id, null);

        assertThat(resultat).isNotNull();
        assertThat(resultat.getCodi()).isEqualTo("METADOC_1_1");
        assertThat(resultat.getNom()).isEqualTo("Meta-document 1.1");
    }

    @Test
    void quanCreemUnNouMetaDocument_laConsultaRetornaUnElementMes() {
        long totalInicial = metaDocumentResourceService.findPage(
                null, null, null, null, PageRequest.of(0, 1)
        ).getTotalElements();

        metaDocumentResourceService.create(buildResourceMinim(), null);
        entityManager.flush();
        entityManager.clear();

        long totalFinal = metaDocumentResourceService.findPage(
                null, null, null, null, PageRequest.of(0, 1)
        ).getTotalElements();

        assertThat(totalFinal).isEqualTo(totalInicial + 1);
    }

    @Test
    void quanActualitzamElNomDUnMetaDocument_getOneRetornaElNomActualitzat() {
        Long id = testData.metaDocuments.get(0).getId();
        MetaDocumentResource existent = metaDocumentResourceService.getOne(id, null);
        existent.setNom("Nom Actualitzat MetaDoc");

        metaDocumentResourceService.update(id, existent, null);
        entityManager.flush();
        entityManager.clear();

        assertThat(metaDocumentResourceService.getOne(id, null).getNom())
                .isEqualTo("Nom Actualitzat MetaDoc");
    }

    @Test
    void quanDesactivamUnMetaDocumentActiu_getOneRetornaActiuFals() {
        Long id = testData.metaDocuments.get(0).getId();
        MetaDocumentResource existent = metaDocumentResourceService.getOne(id, null);
        assertThat(existent.isActiu()).isTrue();

        existent.setActiu(false);
        metaDocumentResourceService.update(id, existent, null);
        entityManager.flush();
        entityManager.clear();

        assertThat(metaDocumentResourceService.getOne(id, null).isActiu()).isFalse();
    }

    @Test
    void quanEsborramUnMetaDocument_laConsultaRetornaMenysUnElement() {
        long totalInicial = metaDocumentResourceService.findPage(
                null, null, null, null, PageRequest.of(0, 1)
        ).getTotalElements();

        Long id = testData.metaDocuments.get(0).getId();
        metaDocumentResourceService.delete(id, null);
        entityManager.flush();
        entityManager.clear();

        long totalFinal = metaDocumentResourceService.findPage(
                null, null, null, null, PageRequest.of(0, 1)
        ).getTotalElements();

        assertThat(totalFinal).isEqualTo(totalInicial - 1);
    }

    // =========================================================================
    // Acció MARCAR_DEFECTE / DESMARCAR_DEFECTE (artifactActionExec)
    // =========================================================================

    /**
     * Cap metadocument de test té perDefecte=true inicialment.
     * MARCAR_DEFECTE (remove=false): posa perDefecte=true al target i false als altres.
     * DESMARCAR_DEFECTE (remove=true): posa perDefecte=false a tots, inclòs el target.
     */
    @Test
    void quanMarquemUnMetaDocumentComAPerDefecte_getOneRetornaPerDefecteVer() {
        Long id = testData.metaDocuments.get(0).getId(); // METADOC_1_1
        assertThat(metaDocumentResourceService.getOne(id, null).isPerDefecte()).isFalse();

        metaDocumentResourceService.artifactActionExec(id, MetaDocumentResource.ACTION_MARCAR_DEFECTE_CODE, null);
        entityManager.flush();
        entityManager.clear();

        assertThat(metaDocumentResourceService.getOne(id, null).isPerDefecte()).isTrue();
    }

    @Test
    void quanMarquemUnMetaDocument_elsAltresDelMateixProcedimentQuedenDesactivats() {
        Long idTarget  = testData.metaDocuments.get(0).getId(); // METADOC_1_1
        Long idAltre   = testData.metaDocuments.get(1).getId(); // METADOC_1_2

        // Primer marquem METADOC_1_2 per defecte
        metaDocumentResourceService.artifactActionExec(idAltre, MetaDocumentResource.ACTION_MARCAR_DEFECTE_CODE, null);
        entityManager.flush();
        entityManager.clear();
        assertThat(metaDocumentResourceService.getOne(idAltre, null).isPerDefecte()).isTrue();

        // Ara marquem METADOC_1_1: METADOC_1_2 ha de quedar false
        metaDocumentResourceService.artifactActionExec(idTarget, MetaDocumentResource.ACTION_MARCAR_DEFECTE_CODE, null);
        entityManager.flush();
        entityManager.clear();

        assertThat(metaDocumentResourceService.getOne(idTarget, null).isPerDefecte()).isTrue();
        assertThat(metaDocumentResourceService.getOne(idAltre, null).isPerDefecte()).isFalse();
    }

    @Test
    void quanDesmarquemUnMetaDocumentPerDefecte_getOneRetornaPerDefecteFals() {
        Long id = testData.metaDocuments.get(0).getId(); // METADOC_1_1

        // Primer el marquem
        metaDocumentResourceService.artifactActionExec(id, MetaDocumentResource.ACTION_MARCAR_DEFECTE_CODE, null);
        entityManager.flush();
        entityManager.clear();
        assertThat(metaDocumentResourceService.getOne(id, null).isPerDefecte()).isTrue();

        // Ara el desmarquem
        metaDocumentResourceService.artifactActionExec(id, MetaDocumentResource.ACTION_DESMARCAR_DEFECTE_CODE, null);
        entityManager.flush();
        entityManager.clear();

        assertThat(metaDocumentResourceService.getOne(id, null).isPerDefecte()).isFalse();
    }

    @Test
    void quanDesmarquemUnMetaDocument_capsDelProcedimentQuedaPerDefecte() {
        Long metaExpedientId = testData.metaExpedients.get(0).getId(); // PROC_01
        Long id = testData.metaDocuments.get(1).getId(); // METADOC_1_2

        // Marquem i desmarquem
        metaDocumentResourceService.artifactActionExec(id, MetaDocumentResource.ACTION_MARCAR_DEFECTE_CODE, null);
        entityManager.flush();
        entityManager.clear();
        metaDocumentResourceService.artifactActionExec(id, MetaDocumentResource.ACTION_DESMARCAR_DEFECTE_CODE, null);
        entityManager.flush();
        entityManager.clear();

        // Cap metadocument de PROC_01 ha de tenir perDefecte=true
        Page<MetaDocumentResource> pagina = metaDocumentResourceService.findPage(
                null, "metaExpedient.id:" + metaExpedientId + " and perDefecte:true", null, null,
                PageRequest.of(0, 20)
        );
        assertThat(pagina.getTotalElements()).isEqualTo(0);
    }

    // =========================================================================
    // Acció REORDENAR (artifactActionExec)
    // =========================================================================

    /**
     * Situació inicial per a PROC_01 (índexs 0..2 de testData.metaDocuments):
     *   ordre=1 → METADOC_1_1
     *   ordre=2 → METADOC_1_2
     *   ordre=3 → METADOC_1_3
     *
     * moveTo fa:
     *   1. Obté la llista ordenada per ordre ASC.
     *   2. Elimina l'element en la posició actual i l'insereix a la nova posició.
     *   3. Reassigna ordre=0,1,2,... a tots els elements.
     */
    @Test
    void quanReordenamElDarrerMetaDocumentAlPrimer_laConsultaPerOrdreReflecteixElCanvi() {
        Long metaExpedientId = testData.metaExpedients.get(0).getId(); // PROC_01
        Long idDarrer = testData.metaDocuments.get(2).getId(); // METADOC_1_3 (ordre=3)

        metaDocumentResourceService.artifactActionExec(idDarrer, MetaDocumentResource.ACTION_REORDENAR_CODE, 0);
        entityManager.flush();
        entityManager.clear();

        // Nou ordre: METADOC_1_3(0), METADOC_1_1(1), METADOC_1_2(2)
        Page<MetaDocumentResource> pagina = metaDocumentResourceService.findPage(
                null, "metaExpedient.id:" + metaExpedientId, null, null,
                PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "ordre"))
        );

        assertThat(pagina.getContent())
                .extracting(MetaDocumentResource::getCodi)
                .containsExactly("METADOC_1_3", "METADOC_1_1", "METADOC_1_2");
    }

    @Test
    void quanReordenamElPrimerMetaDocumentAlDarrer_laConsultaPerOrdreReflecteixElCanvi() {
        Long metaExpedientId = testData.metaExpedients.get(0).getId(); // PROC_01
        Long idPrimer = testData.metaDocuments.get(0).getId(); // METADOC_1_1 (ordre=1)

        metaDocumentResourceService.artifactActionExec(idPrimer, MetaDocumentResource.ACTION_REORDENAR_CODE, 2);
        entityManager.flush();
        entityManager.clear();

        // Nou ordre: METADOC_1_2(0), METADOC_1_3(1), METADOC_1_1(2)
        Page<MetaDocumentResource> pagina = metaDocumentResourceService.findPage(
                null, "metaExpedient.id:" + metaExpedientId, null, null,
                PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "ordre"))
        );

        assertThat(pagina.getContent())
                .extracting(MetaDocumentResource::getCodi)
                .containsExactly("METADOC_1_2", "METADOC_1_3", "METADOC_1_1");
    }

    @Test
    void quanReordenamUnMetaDocumentALaMateixaPosicio_lordreNoCanvia() {
        Long metaExpedientId = testData.metaExpedients.get(0).getId(); // PROC_01
        Long idMitja = testData.metaDocuments.get(1).getId(); // METADOC_1_2 (índex 1 a la llista)

        metaDocumentResourceService.artifactActionExec(idMitja, MetaDocumentResource.ACTION_REORDENAR_CODE, 1);
        entityManager.flush();
        entityManager.clear();

        Page<MetaDocumentResource> pagina = metaDocumentResourceService.findPage(
                null, "metaExpedient.id:" + metaExpedientId, null, null,
                PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "ordre"))
        );

        assertThat(pagina.getContent())
                .extracting(MetaDocumentResource::getCodi)
                .containsExactly("METADOC_1_1", "METADOC_1_2", "METADOC_1_3");
    }

    @Test
    void quanReordenamMetaDocument_elsAltresProcedimentsNoQueden_Afectats() {
        Long metaExpedient2Id = testData.metaExpedients.get(1).getId(); // PROC_02
        Long idDarrerPROC01  = testData.metaDocuments.get(2).getId();   // METADOC_1_3

        metaDocumentResourceService.artifactActionExec(idDarrerPROC01, MetaDocumentResource.ACTION_REORDENAR_CODE, 0);
        entityManager.flush();
        entityManager.clear();

        // PROC_02 ha de mantenir l'ordre original
        Page<MetaDocumentResource> pagina = metaDocumentResourceService.findPage(
                null, "metaExpedient.id:" + metaExpedient2Id, null, null,
                PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "ordre"))
        );

        assertThat(pagina.getContent())
                .extracting(MetaDocumentResource::getCodi)
                .containsExactly("METADOC_2_1", "METADOC_2_2", "METADOC_2_3");
    }

    // =========================================================================
    // Crear amb tots els camps i verificar detalls
    // =========================================================================

    @Test
    void quanCreemUnMetaDocumentAmbTotsElsCamps_getOneRetornaTotsElsCampsCorrectes() {
        metaDocumentResourceService.create(buildResourceAmbTotsElsCamps(), null);
        entityManager.flush();
        entityManager.clear();

        MetaDocumentResource recuperat = metaDocumentResourceService.findPage(
                null, "codi:'METADOC_COMPLET'", null, null, PageRequest.of(0, 1)
        ).getContent().get(0);
        Long id = recuperat.getId();
        recuperat = metaDocumentResourceService.getOne(id, null);

        assertThat(recuperat.getCodi()).isEqualTo("METADOC_COMPLET");
        assertThat(recuperat.getNom()).isEqualTo("Meta-document Complet");
        assertThat(recuperat.getDescripcio()).isEqualTo("Descripció completa del meta-document");
        assertThat(recuperat.getMultiplicitat()).isEqualTo(MultiplicitatEnumDto.M_1_N);
        assertThat(recuperat.isActiu()).isTrue();
        assertThat(recuperat.getNtiOrigen()).isEqualTo(NtiOrigenEnumDto.O1);
        assertThat(recuperat.getNtiEstadoElaboracion()).isEqualTo(DocumentNtiEstadoElaboracionEnumDto.EE02);
        assertThat(recuperat.getNtiTipoDocumental()).isEqualTo("TD02");
        assertThat(recuperat.isFirmaPortafirmesActiva()).isTrue();
        assertThat(recuperat.getPortafirmesDocumentTipus()).isEqualTo("TIPUS_PORTAFIRMES");
        assertThat(recuperat.getPortafirmesSequenciaTipus()).isEqualTo(MetaDocumentFirmaSequenciaTipusEnumDto.PARALEL);
        assertThat(recuperat.getPortafirmesCustodiaTipus()).isEqualTo("CUSTODIA_PORTAFIRMES");
        assertThat(recuperat.getPortafirmesFluxTipus()).isEqualTo(MetaDocumentFirmaFluxTipusEnumDto.PORTAFIB);
        assertThat(recuperat.isFirmaPassarelaActiva()).isTrue();
        assertThat(recuperat.getFirmaPassarelaCustodiaTipus()).isEqualTo("CUSTODIA_PASSARELA");
        assertThat(recuperat.isFirmaBiometricaActiva()).isTrue();
        assertThat(recuperat.isBiometricaLectura()).isTrue();
        assertThat(recuperat.isPinbalActiu()).isTrue();
        assertThat(recuperat.getPinbalFinalitat()).isEqualTo("FINALITAT_PROVA");
        assertThat(recuperat.getMetaExpedient().getId()).isEqualTo(testData.metaExpedients.get(0).getId());
        assertThat(recuperat.getPinbalServei().getId()).isEqualTo(testData.pinbalServei.getId());
    }

    // =========================================================================
    // Activar un tipus de document i verificar que apareix com a actiu
    // =========================================================================

    @Test
    void quanActivamUnMetaDocumentInactiu_apareixALaConsultaDAcitius() {
        Long id = testData.metaDocuments.get(0).getId(); // METADOC_1_1 (actiu=true)

        // Desactivam per tenir un document inactiu com a punt de partida
        MetaDocumentResource existent = metaDocumentResourceService.getOne(id, null);
        existent.setActiu(false);
        metaDocumentResourceService.update(id, existent, null);
        entityManager.flush();
        entityManager.clear();
        assertThat(metaDocumentResourceService.getOne(id, null).isActiu()).isFalse();

        // Activam el document
        existent = metaDocumentResourceService.getOne(id, null);
        existent.setActiu(true);
        metaDocumentResourceService.update(id, existent, null);
        entityManager.flush();
        entityManager.clear();

        // Verificam via getOne que l'estat és actiu
        assertThat(metaDocumentResourceService.getOne(id, null).isActiu()).isTrue();

        // Verificam via findPage (simulant "recarregar la pàgina") que apareix com a actiu
        Page<MetaDocumentResource> pagina = metaDocumentResourceService.findPage(
                null, "actiu:true", null, null,
                PageRequest.of(0, 20)
        );
        assertThat(pagina.getContent())
                .extracting(MetaDocumentResource::getId)
                .contains(id);
    }

    // =========================================================================
    // Modificar tots els camps i verificar les modificacions
    // =========================================================================

    @Test
    void quanModificamTotsElsCampsDeUnMetaDocument_getOneRetornaTotsElsValorsActualitzats() {
        Long id = testData.metaDocuments.get(0).getId(); // METADOC_1_1

        MetaDocumentResource existent = metaDocumentResourceService.getOne(id, null);

        // Modifiquem tots els camps editables (mantenint actiu=true perquè el
        // servei només crida updateActiu si canvia, i altrament aplicaria update general)
        existent.setNom("Nom Totalment Modificat");
        existent.setDescripcio("Descripció modificada completa");
        existent.setMultiplicitat(MultiplicitatEnumDto.M_1);
        existent.setNtiEstadoElaboracion(DocumentNtiEstadoElaboracionEnumDto.EE02);
        existent.setNtiTipoDocumental("TD02");
        existent.setFirmaPortafirmesActiva(true);
        existent.setPortafirmesFluxTipus(MetaDocumentFirmaFluxTipusEnumDto.PORTAFIB);
        existent.setPortafirmesDocumentTipus("PFIRMES_TIPUS_MOD");
        existent.setPortafirmesSequenciaTipus(MetaDocumentFirmaSequenciaTipusEnumDto.PARALEL);
        existent.setPortafirmesCustodiaTipus("PFIRMES_CUSTODIA_MOD");
        existent.setFirmaPassarelaActiva(true);
        existent.setFirmaPassarelaCustodiaTipus("PASS_CUSTODIA_MOD");
        existent.setFirmaBiometricaActiva(true);
        existent.setBiometricaLectura(true);
        existent.setPinbalActiu(true);
        existent.setPinbalFinalitat("PINBAL_FINALITAT_MOD");
        existent.setPinbalUtilitzarCifOrgan(true);

        metaDocumentResourceService.update(id, existent, null);
        entityManager.flush();
        entityManager.clear();

        MetaDocumentResource resultat = metaDocumentResourceService.getOne(id, null);

        assertThat(resultat.getNom()).isEqualTo("Nom Totalment Modificat");
        assertThat(resultat.getDescripcio()).isEqualTo("Descripció modificada completa");
        assertThat(resultat.getMultiplicitat()).isEqualTo(MultiplicitatEnumDto.M_1);
        assertThat(resultat.getNtiEstadoElaboracion()).isEqualTo(DocumentNtiEstadoElaboracionEnumDto.EE02);
        assertThat(resultat.getNtiTipoDocumental()).isEqualTo("TD02");
        assertThat(resultat.isFirmaPortafirmesActiva()).isTrue();
        assertThat(resultat.getPortafirmesFluxTipus()).isEqualTo(MetaDocumentFirmaFluxTipusEnumDto.PORTAFIB);
        assertThat(resultat.getPortafirmesDocumentTipus()).isEqualTo("PFIRMES_TIPUS_MOD");
        assertThat(resultat.getPortafirmesSequenciaTipus()).isEqualTo(MetaDocumentFirmaSequenciaTipusEnumDto.PARALEL);
        assertThat(resultat.getPortafirmesCustodiaTipus()).isEqualTo("PFIRMES_CUSTODIA_MOD");
        assertThat(resultat.isFirmaPassarelaActiva()).isTrue();
        assertThat(resultat.getFirmaPassarelaCustodiaTipus()).isEqualTo("PASS_CUSTODIA_MOD");
        assertThat(resultat.isFirmaBiometricaActiva()).isTrue();
        assertThat(resultat.isBiometricaLectura()).isTrue();
        assertThat(resultat.isPinbalActiu()).isTrue();
        assertThat(resultat.getPinbalFinalitat()).isEqualTo("PINBAL_FINALITAT_MOD");
        assertThat(resultat.isPinbalUtilitzarCifOrgan()).isTrue();
    }

    // =========================================================================
    // Mètode auxiliar
    // =========================================================================

    private MetaDocumentResource buildResourceMinim() {
        MetaDocumentResource r = new MetaDocumentResource();
        r.setCodi("METADOC_NOU");
        r.setNom("Meta-document Nou");
        r.setMultiplicitat(MultiplicitatEnumDto.M_1);
        r.setNtiOrigen(NtiOrigenEnumDto.O1);
        r.setNtiEstadoElaboracion(DocumentNtiEstadoElaboracionEnumDto.EE01);
        r.setNtiTipoDocumental("TD01");
        r.setMetaExpedient(ResourceReference.toResourceReference(
                testData.metaExpedients.get(0).getId(), "PROC_01"));
        r.setPinbalServei(ResourceReference.toResourceReference(
                testData.pinbalServei.getId(), testData.pinbalServei.getCodi()));
        return r;
    }

    private MetaDocumentResource buildResourceAmbTotsElsCamps() {
        MetaDocumentResource r = new MetaDocumentResource();
        r.setCodi("METADOC_COMPLET");
        r.setNom("Meta-document Complet");
        r.setDescripcio("Descripció completa del meta-document");
        r.setMultiplicitat(MultiplicitatEnumDto.M_1_N);
        r.setActiu(true);
        r.setNtiOrigen(NtiOrigenEnumDto.O1);
        r.setNtiEstadoElaboracion(DocumentNtiEstadoElaboracionEnumDto.EE02);
        r.setNtiTipoDocumental("TD02");
        r.setFirmaPortafirmesActiva(true);
        r.setPortafirmesFluxTipus(MetaDocumentFirmaFluxTipusEnumDto.PORTAFIB);
        r.setPortafirmesDocumentTipus("TIPUS_PORTAFIRMES");
        r.setPortafirmesSequenciaTipus(MetaDocumentFirmaSequenciaTipusEnumDto.PARALEL);
        r.setPortafirmesCustodiaTipus("CUSTODIA_PORTAFIRMES");
        r.setFirmaPassarelaActiva(true);
        r.setFirmaPassarelaCustodiaTipus("CUSTODIA_PASSARELA");
        r.setFirmaBiometricaActiva(true);
        r.setBiometricaLectura(true);
        r.setPinbalActiu(true);
        r.setPinbalFinalitat("FINALITAT_PROVA");
        r.setMetaExpedient(ResourceReference.toResourceReference(
                testData.metaExpedients.get(0).getId(), "PROC_01"));
        r.setPinbalServei(ResourceReference.toResourceReference(
                testData.pinbalServei.getId(), testData.pinbalServei.getCodi()));
        return r;
    }
}
