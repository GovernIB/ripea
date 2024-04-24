package es.caib.ripea.core.helper;

import es.caib.ripea.core.api.dto.InteressatDocumentTipusEnumDto;
import es.caib.ripea.core.api.dto.InteressatDto;
import es.caib.ripea.core.api.dto.InteressatIdiomaEnumDto;
import es.caib.ripea.core.api.dto.InteressatPersonaFisicaDto;
import es.caib.ripea.core.api.dto.InteressatTipusEnumDto;
import es.caib.ripea.core.api.dto.PermissionEnumDto;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.entity.ExpedientPeticioEntity;
import es.caib.ripea.core.entity.InteressatEntity;
import es.caib.ripea.core.entity.InteressatPersonaFisicaEntity;
import es.caib.ripea.core.repository.ExpedientPeticioRepository;
import es.caib.ripea.core.repository.ExpedientRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.mockito.Mockito.when;

public class ExpedientHelperTest {

    @Mock
    private ExpedientEntity mockEntity;
//    @Mock
//    private InteressatDto mockDto;
    @Mock
    private ExpedientPeticioRepository expedientPeticioRepository;
    @Mock
    private ExpedientRepository expedientRepository;
    @InjectMocks
    private ExpedientHelper expedientHelper;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testAssociateInteressats() {
        Long expedientId = 1L;
        Long entitatId = 2L;
        Long expedientPeticioId = 3L;
        PermissionEnumDto permission = PermissionEnumDto.CREATE;
        String rolActual = "Role1";

        ExpedientPeticioEntity expedientPeticioEntity = new ExpedientPeticioEntity();
//        ExpedientEntity expedientEntity = new ExpedientEntity();

        // Stubbing mock object's behavior
        when(expedientPeticioRepository.findOne(expedientPeticioId)).thenReturn(expedientPeticioEntity);
        when(expedientRepository.findOne(expedientId)).thenReturn(mockEntity);

        // some logic to setup expected result DTO based on mocked entity.
        InteressatDto expectedDto = getExpectedInteressat(); //expected Result;

        // test method
        List<InteressatDto> resultDto = expedientHelper.associateInteressats(expedientId, entitatId, expedientPeticioId, permission, rolActual);

        // verify results
//        verify(expedientPeticioRepository, times(1)).findOne(expedientPeticioId);
//        verify(expedientRepository, times(1)).findOne(expedientId);
//        assertEquals(expectedDto, resultDto.get(0));
//
//        // Verify the methods were called on the mock object
//        verify(mockEntity, times(1)).getTipus();
    }

    private InteressatEntity getExistingInteressat() {
        InteressatPersonaFisicaEntity representant = InteressatPersonaFisicaEntity.getBuilder(
                "NomRep",
                "Llinatge1Rep",
                "Llinatge2Rep",
                InteressatDocumentTipusEnumDto.NIF,
                "00000000A",
                "AN",
                "01",
                "001",
                "AdressaRep",
                "07001",
                "emailRep@limit.es",
                "654321098",
                "Observacions Rep",
                InteressatIdiomaEnumDto.ES,
                null,
                null,
                false,
                false,
                false
        ).build();

        InteressatPersonaFisicaEntity interessat = InteressatPersonaFisicaEntity.getBuilder(
                "Nom",
                "Llinatge1",
                "Llinatge2",
                InteressatDocumentTipusEnumDto.NIF,
                "99999999R",
                "ES",
                "07",
                "033",
                "Adressa",
                "07500",
                "email@limit.es",
                "678901234",
                "Observacions",
                InteressatIdiomaEnumDto.CA,
                null,
                representant,
                false,
                false,
                false
        ).build();
        return interessat;
    }

    private InteressatDto getExpectedInteressat() {
        InteressatPersonaFisicaDto representant = new InteressatPersonaFisicaDto();
        representant.setDocumentTipus(InteressatDocumentTipusEnumDto.NIF);
        representant.setDocumentNum("99999999R");
        representant.setPais("ES");
        representant.setProvincia("07");
        representant.setMunicipi("033");
        representant.setAdresa("Adressa");
        representant.setCodiPostal("07500");
        representant.setEmail("email@limit.es");
        representant.setTelefon("678901234");
        representant.setObservacions("Observacions");
        representant.setTipus(InteressatTipusEnumDto.PERSONA_FISICA);
        representant.setNom("Nom");
        representant.setLlinatge1("Llinatge1");
        representant.setLlinatge2("Llinatge2");
        representant.setId(null);
        representant.setRepresentant(representant);

        InteressatPersonaFisicaDto interessat = new InteressatPersonaFisicaDto();
        interessat.setDocumentTipus(InteressatDocumentTipusEnumDto.NIF);
        interessat.setDocumentNum("99999999R");
        interessat.setPais("ES");
        interessat.setProvincia("07");
        interessat.setMunicipi("033");
        interessat.setAdresa("Adressa");
        interessat.setCodiPostal("07500");
        interessat.setEmail("email@limit.es");
        interessat.setTelefon("678901234");
        interessat.setObservacions("Observacions");
        interessat.setTipus(InteressatTipusEnumDto.PERSONA_FISICA);
        interessat.setNom("Nom");
        interessat.setLlinatge1("Llinatge1");
        interessat.setLlinatge2("Llinatge2");
        interessat.setId(null);
        interessat.setRepresentant(representant);

        return interessat;
    }
}