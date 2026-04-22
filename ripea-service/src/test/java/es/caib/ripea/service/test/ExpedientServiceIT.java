package es.caib.ripea.service.test;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

import es.caib.ripea.persistence.entity.ExpedientEntity;
import es.caib.ripea.persistence.entity.MetaExpedientEntity;
import es.caib.ripea.persistence.repository.ExpedientRepository;
import es.caib.ripea.service.intf.dto.ExpedientDto;
import es.caib.ripea.service.intf.dto.PaginaDto;
import es.caib.ripea.service.intf.dto.PaginacioParamsDto;
import es.caib.ripea.service.intf.dto.PrioritatEnumDto;
import es.caib.ripea.service.intf.service.ExpedientService;
import es.caib.ripea.service.test.config.BaseServiceIT;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests d'integració per a ExpedientService.
 *
 * Verifica que els mètodes de servei funcionen correctament contra
 * una BBDD H2 in-memory amb el conjunt de dades bàsic.
 */
@WithMockUser(username = "usuari1", roles = {"ADMIN"})
public class ExpedientServiceIT extends BaseServiceIT {

    @Autowired
    private ExpedientService expedientService;

    @Autowired
    private ExpedientRepository expedientRepository;

    @Test
    void donatsTresExpedientsAssociatsAUnProcediment_quanElsCercamPaginats_esRetornenElsTres() {
        MetaExpedientEntity proc01 = testData.metaExpedients.get(0);
        MetaExpedientEntity proc02 = testData.metaExpedients.get(1);

        crearIDesarExpedient(proc01, 1);
        crearIDesarExpedient(proc01, 2);
        crearIDesarExpedient(proc01, 3);
        crearIDesarExpedient(proc02, 1);

        entityManager.flush();
        entityManager.clear();

        PaginacioParamsDto paginacio = new PaginacioParamsDto();
        paginacio.setPaginaNum(0);
        paginacio.setPaginaTamany(10);

        PaginaDto<ExpedientDto> resultat = expedientService.findExpedientMetaExpedientPaginat(
                testData.entitat.getId(),
                proc01.getId(),
                paginacio
        );

        assertThat(resultat).isNotNull();
        assertThat(resultat.getElementsTotal()).isEqualTo(3);
        assertThat(resultat.getContingut()).hasSize(3);
    }

    private void crearIDesarExpedient(MetaExpedientEntity metaExpedient, long sequencia) {
        ExpedientEntity expedient = ExpedientEntity.getBuilder(
                "Expedient " + metaExpedient.getCodi() + "/" + sequencia,
                metaExpedient,
                null,
                testData.entitat,
                "1.0",
                "A00000000",
                new Date(),
                "SIA001",
                null,
                PrioritatEnumDto.B_NORMAL,
                null
        ).build();
        expedient.updateAnySequenciaCodi(2024, sequencia, metaExpedient.getCodi() + "/2024/" + sequencia);
        expedient.updateNumero(metaExpedient.getCodi() + "/2024/" + String.format("%06d", sequencia));
        expedientRepository.save(expedient);
    }
}
