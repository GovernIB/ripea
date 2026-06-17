package es.caib.ripea.service.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithMockUser;

import es.caib.ripea.persistence.entity.ExpedientEntity;
import es.caib.ripea.persistence.entity.GrupEntity;
import es.caib.ripea.persistence.entity.MetaExpedientEntity;
import es.caib.ripea.persistence.entity.MetaExpedientOrganGestorEntity;
import es.caib.ripea.persistence.entity.MetaNodeEntity;
import es.caib.ripea.persistence.repository.ExpedientRepository;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.dto.PrioritatEnumDto;
import es.caib.ripea.service.intf.model.ExpedientResource;
import es.caib.ripea.service.intf.resourceservice.ExpedientResourceService;
import es.caib.ripea.service.test.config.BaseServiceIT;

/**
 * Tests d'integració del llistat d'expedients (ExpedientResourceServiceImpl) que
 * fixen dos comportaments de permisos enfront de regressions:
 *
 * 1) El tall per capPermis(): si l'usuari no té permís per cap via, additionalSpringFilter
 *    retorna "id : 0" i el llistat dona 0 resultats (no executa la consulta real).
 *
 * 2) El LEFT JOIN sobre la col·lecció metaexpedientOrganGestorPares: quan el filtre de
 *    permisos navega aquesta col·lecció dins d'un OR (vies d'òrgan), un expedient SENSE
 *    files a ipa_expedient_organpare però accessible per una altra branca (procediment)
 *    s'ha de seguir retornant. Amb un INNER JOIN quedava exclòs (el bug corregit a
 *    ExpressionGenerator: PluralAttributePath -> JoinType.LEFT).
 *
 * El rol es força a ROLE_USER (tothom) via configHelper perquè additionalSpringFilter
 * apliqui el camí d'usuari normal (no el bypass d'admin).
 */
@WithMockUser(username = "usuari1", roles = {"ADMIN"})
public class ExpedientResourceServiceIT extends BaseServiceIT {

    @Autowired private ExpedientResourceService expedientResourceService;
    @Autowired private ExpedientRepository expedientRepository;

    @BeforeEach
    @Override
    public void setUpTestData() {
        super.setUpTestData();
        Mockito.lenient().when(configHelper.getEntitatActualCodi()).thenReturn("ENT_TEST");
        Mockito.lenient().when(configHelper.getRolActual()).thenReturn(BaseConfig.ROLE_USER);
        Mockito.lenient().when(configHelper.getOrganActualCodi()).thenReturn(null);
    }

    // =========================================================================
    // 1) Tall per capPermis(): sense permís per cap via -> 0 resultats
    // =========================================================================

    @Test
    void donatUnUsuariSensePermisos_quanLlistaExpedients_noRetornaCap() {
        // Cap permís per cap via (totes les llistes ACL buides)
        stubPermisos(new ArrayList<>(), new ArrayList<>());

        // Existeix un expedient que, sense el tall, hauria de poder coincidir
        crearExpedientSenseOrganpare(testData.metaExpedients.get(0));
        entityManager.flush();
        entityManager.clear();

        Page<ExpedientResource> pagina = expedientResourceService.findPage(
                null, null, null, null, PageRequest.of(0, 10));

        assertThat(pagina.getTotalElements()).isZero();
    }

    // =========================================================================
    // 2) LEFT JOIN: expedient sense organpare accessible per procediment -> apareix
    // =========================================================================

    @Test
    void donatUnExpedientSenseOrganpareAccessiblePerProcediment_quanElFiltreNavegaLaColleccio_segueixApareixent() {
        MetaExpedientEntity proc01 = testData.metaExpedients.get(0); // permisDirecte = false

        // VIA 1: permís de lectura directe sobre el procediment de l'expedient.
        // VIA 3: parells procediment-òrgan permesos NO buits -> el filtre navega
        //        metaexpedientOrganGestorPares.id dins de l'OR i força el JOIN a la col·lecció.
        //        (l'id no cal que existeixi: només serveix per generar el JOIN)
        stubPermisos(Arrays.asList(proc01.getId()), Arrays.asList(999999L));

        // Expedient de proc01 SENSE cap fila a ipa_expedient_organpare i sense grup
        ExpedientEntity expedient = crearExpedientSenseOrganpare(proc01);
        entityManager.flush();
        entityManager.clear();

        Page<ExpedientResource> pagina = expedientResourceService.findPage(
                null, null, null, null, PageRequest.of(0, 10));

        // Amb LEFT JOIN apareix (coincideix per VIA 1). Amb INNER JOIN el bug el descartava (0).
        assertThat(pagina.getTotalElements()).isEqualTo(1);
        assertThat(pagina.getContent())
                .extracting(ExpedientResource::getId)
                .containsExactly(expedient.getId());
    }

    // =========================================================================
    // Auxiliars
    // =========================================================================

    /**
     * Stubeja les 4 consultes ACL que fa ExpedientHelper.findPermisosPerExpedients
     * (branca d'usuari normal). Cal stubejar-les TOTES: el mock retornaria null per
     * defecte i toListLong(null) provocaria NPE.
     *
     * @param procedimentsRead          ids de procediments amb READ (VIA 1)
     * @param parellsProcedimentOrgan   ids de parells procediment-òrgan amb READ (VIA 3)
     */
    private void stubPermisos(List<Long> procedimentsRead, List<Long> parellsProcedimentOrgan) {
        Mockito.when(permisosHelper.getObjectsIdsWithPermission(eq(MetaNodeEntity.class), any()))
                .thenReturn(procedimentsRead);
        Mockito.when(permisosHelper.getObjectsIdsWithPermission(eq(MetaExpedientOrganGestorEntity.class), any()))
                .thenReturn(parellsProcedimentOrgan);
        Mockito.when(permisosHelper.getObjectsIdsWithPermission(eq(GrupEntity.class), any()))
                .thenReturn(new ArrayList<>());
        Mockito.when(permisosHelper.getObjectsIdsWithTwoPermissions(any(), any(), any()))
                .thenReturn(new ArrayList<>());
    }

    private ExpedientEntity crearExpedientSenseOrganpare(MetaExpedientEntity metaExpedient) {
        ExpedientEntity expedient = ExpedientEntity.getBuilder(
                "Expedient " + metaExpedient.getCodi(),
                metaExpedient,
                null,
                testData.entitat,
                "1.0",
                "A00000000",
                new Date(),
                "SIA001",
                testData.organs.get(0),
                PrioritatEnumDto.B_NORMAL,
                null
        ).build();
        expedient.updateAnySequenciaCodi(2024, 1, metaExpedient.getCodi() + "/2024/1");
        expedient.updateNumero(metaExpedient.getCodi() + "/2024/000001");
        return expedientRepository.save(expedient);
    }
}
