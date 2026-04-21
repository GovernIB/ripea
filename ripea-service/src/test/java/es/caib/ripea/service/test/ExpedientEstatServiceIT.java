package es.caib.ripea.service.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

import es.caib.ripea.persistence.entity.ExpedientEstatEntity;
import es.caib.ripea.service.intf.dto.ExpedientEstatDto;
import es.caib.ripea.service.intf.service.ExpedientEstatService;
import es.caib.ripea.service.test.config.BaseServiceIT;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests d'integració per a ExpedientEstatService.
 *
 * Verifica que els mètodes de servei funcionen correctament contra
 * una BBDD H2 in-memory amb el conjunt de dades bàsic.
 */
@WithMockUser(username = "usuari1", roles = {"ADMIN"})
public class ExpedientEstatServiceIT extends BaseServiceIT {

    @Autowired
    private ExpedientEstatService expedientEstatService;

    @Test
    void donatUnEstatExistent_quanElCercamPerId_elRetornaCorrèctament() {
        ExpedientEstatEntity estat = testData.estats.get(0);

        ExpedientEstatDto dto = expedientEstatService.findExpedientEstatById(
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

        ExpedientEstatDto dto = expedientEstatService.findExpedientEstatById(
                testData.entitat.getId(),
                estat.getId()
        );

        assertThat(dto.getColor()).isEqualTo("#3366FF");
    }

    @Test
    void donatUnEstatDelTercerProcediment_quanElCercamPerId_elMetaExpedientAssociatEsCorrect() {
        // L'estat número 7 (índex 6) pertany al tercer procediment (estats 7, 8, 9)
        ExpedientEstatEntity estat = testData.estats.get(6);
//        ExpedientEstatEntity estatDelTercerProc = testData.estats.get(6);

        ExpedientEstatDto dto = expedientEstatService.findExpedientEstatById(
                testData.entitat.getId(),
                estat.getId()
        );

        assertThat(dto.getMetaExpedientId()).isEqualTo(testData.metaExpedients.get(2).getId());
    }
}
