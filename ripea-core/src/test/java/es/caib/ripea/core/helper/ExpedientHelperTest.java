package es.caib.ripea.core.helper;

import es.caib.distribucio.rest.client.integracio.domini.DocumentTipus;
import es.caib.distribucio.rest.client.integracio.domini.InteressatTipus;
import es.caib.ripea.core.api.dto.InteressatDocumentTipusEnumDto;
import es.caib.ripea.core.api.dto.InteressatDto;
import es.caib.ripea.core.api.dto.InteressatIdiomaEnumDto;
import es.caib.ripea.core.api.dto.InteressatPersonaFisicaDto;
import es.caib.ripea.core.api.dto.InteressatPersonaJuridicaDto;
import es.caib.ripea.core.api.dto.InteressatTipusEnumDto;
import es.caib.ripea.core.api.dto.PermissionEnumDto;
import es.caib.ripea.core.persistence.ExpedientEntity;
import es.caib.ripea.core.persistence.ExpedientPeticioEntity;
import es.caib.ripea.core.persistence.InteressatEntity;
import es.caib.ripea.core.persistence.InteressatPersonaFisicaEntity;
import es.caib.ripea.core.persistence.RegistreEntity;
import es.caib.ripea.core.persistence.RegistreInteressatEntity;
import es.caib.ripea.core.repository.ExpedientPeticioRepository;
import es.caib.ripea.core.repository.ExpedientRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ExpedientHelperTest {

    @Mock
    private ExpedientEntity expedientEntity;
    @Mock
    private ExpedientPeticioEntity expedientPeticioEntity;
    @Mock
    private RegistreEntity registreEntity;
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
    public void testTtoInteressatMergedDto() {

        RegistreInteressatEntity incomingInteressat = getIncomingInteressat();
        InteressatDto existingInteressat = getExistingInteressat();
        InteressatDto expectedInteressat = getExpectedInteressat();

        InteressatDto interessatMerged = expedientHelper.toInteressatMergedDto(incomingInteressat, existingInteressat);
        InteressatDto representantMerged = expedientHelper.toInteressatMergedDto(incomingInteressat.getRepresentant(), existingInteressat.getRepresentant());

        assertEquals(expectedInteressat.getTipus(), interessatMerged.getTipus());
        assertEquals(expectedInteressat.getDocumentTipus(), interessatMerged.getDocumentTipus());
        assertEquals(expectedInteressat.getDocumentNum(), interessatMerged.getDocumentNum());
        assertEquals(expectedInteressat.getPais(), interessatMerged.getPais());
        assertEquals(expectedInteressat.getProvincia(), interessatMerged.getProvincia());
        assertEquals(expectedInteressat.getMunicipi(), interessatMerged.getMunicipi());
        assertEquals(expectedInteressat.getAdresa(), interessatMerged.getAdresa());
        assertEquals(expectedInteressat.getCodiPostal(), interessatMerged.getCodiPostal());
        assertEquals(expectedInteressat.getEmail(), interessatMerged.getEmail());
        assertEquals(expectedInteressat.getTelefon(), interessatMerged.getTelefon());
        assertEquals(expectedInteressat.getObservacions(), interessatMerged.getObservacions());
        assertEquals(expectedInteressat.getPreferenciaIdioma(), interessatMerged.getPreferenciaIdioma());
        assertEquals(expectedInteressat.getPaisNom(), interessatMerged.getPaisNom());
        assertEquals(expectedInteressat.getProvinciaNom(), interessatMerged.getProvinciaNom());
        assertEquals(expectedInteressat.getMunicipiNom(), interessatMerged.getMunicipiNom());

        assertEquals(expectedInteressat.getRepresentant().getTipus(), representantMerged.getTipus());
        assertEquals(expectedInteressat.getRepresentant().getDocumentTipus(), representantMerged.getDocumentTipus());
        assertEquals(expectedInteressat.getRepresentant().getDocumentNum(), representantMerged.getDocumentNum());
        assertEquals(expectedInteressat.getRepresentant().getPais(), representantMerged.getPais());
        assertEquals(expectedInteressat.getRepresentant().getProvincia(), representantMerged.getProvincia());
        assertEquals(expectedInteressat.getRepresentant().getMunicipi(), representantMerged.getMunicipi());
        assertEquals(expectedInteressat.getRepresentant().getAdresa(), representantMerged.getAdresa());
        assertEquals(expectedInteressat.getRepresentant().getCodiPostal(), representantMerged.getCodiPostal());
        assertEquals(expectedInteressat.getRepresentant().getEmail(), representantMerged.getEmail());
        assertEquals(expectedInteressat.getRepresentant().getTelefon(), representantMerged.getTelefon());
        assertEquals(expectedInteressat.getRepresentant().getObservacions(), representantMerged.getObservacions());
        assertEquals(expectedInteressat.getRepresentant().getPreferenciaIdioma(), representantMerged.getPreferenciaIdioma());
        assertEquals(expectedInteressat.getRepresentant().getPaisNom(), representantMerged.getPaisNom());
        assertEquals(expectedInteressat.getRepresentant().getProvinciaNom(), representantMerged.getProvinciaNom());
        assertEquals(expectedInteressat.getRepresentant().getMunicipiNom(), representantMerged.getMunicipiNom());
//        assertEquals(expectedInteressat.getRepresentantId(), interessatMerged.getRepresentantId());
//        assertEquals(expectedInteressat.getRepresentantIdentificador(), interessatMerged.getRepresentantIdentificador());
//        assertEquals(expectedInteressat.getIdentificador(), interessatMerged.getIdentificador());
//        assertEquals(expectedInteressat.getEntregaDeh(), interessatMerged.getEntregaDeh());
//        assertEquals(expectedInteressat.getEntregaDehObligat(), interessatMerged.getEntregaDehObligat());
//        assertEquals(expectedInteressat.getRepresentant(), interessatMerged.getRepresentant());
//        interessatsEquals &= (expectedInteressat.isEsRepresentant() == interessatMerged.isEsRepresentant());
//        interessatsEquals &= (expectedInteressat.isArxiuPropagat() == interessatMerged.isArxiuPropagat());
//        interessatsEquals &= (expectedInteressat.isRepresentantArxiuPropagat() == interessatMerged.isRepresentantArxiuPropagat());
//        interessatsEquals &= (expectedInteressat.isExpedientArxiuPropagat() == interessatMerged.isExpedientArxiuPropagat());
//        interessatsEquals &= Objects.equals(expectedInteressat.getIncapacitat(), interessatMerged.getIncapacitat());

    }

//    @Test
//    public void testAssociateInteressats() {
//        Long expedientId = 1L;
//        Long entitatId = 2L;
//        Long expedientPeticioId = 3L;
//        PermissionEnumDto permission = PermissionEnumDto.CREATE;
//        String rolActual = "Role1";
//
////        ExpedientPeticioEntity expedientPeticioEntity = new ExpedientPeticioEntity();
////        ExpedientEntity expedientEntity = new ExpedientEntity();
//
//        // Stubbing mock object's behavior
//        when(expedientPeticioRepository.findOne(expedientPeticioId)).thenReturn(expedientPeticioEntity);
//        when(expedientRepository.findOne(expedientId)).thenReturn(expedientEntity);
//        when(expedientPeticioEntity.getRegistre()).thenReturn(registreEntity);
//        when(expedientEntity.getInteressatsORepresentants()).thenReturn(getExistingInteressats());
//        when(registreEntity.getInteressats()).thenReturn(getIncomingInteressats());
//
//
//        // some logic to setup expected result DTO based on mocked entity.
//        InteressatDto expectedDto = getExpectedInteressat(); //expected Result;
//
//        // test method
//        List<InteressatDto> resultDto = expedientHelper.associateInteressats(expedientId, entitatId, expedientPeticioId, permission, rolActual);
//
//        // verify results
//        verify(expedientPeticioRepository, times(1)).findOne(expedientPeticioId);
//        verify(expedientRepository, times(1)).findOne(expedientId);
//        assertEquals(expectedDto, resultDto.get(0));
//
//        // Verify the methods were called on the mock object
//        verify(expedientEntity, times(1)).getTipus();
//    }


    private InteressatDto getExistingInteressat() {
        InteressatPersonaFisicaDto representant = new InteressatPersonaFisicaDto();
        representant.setDocumentTipus(InteressatDocumentTipusEnumDto.NIF);
        representant.setDocumentNum("00000000A");
        representant.setPais("AN");
        representant.setProvincia("01");
        representant.setMunicipi("001");
        representant.setAdresa("AdressaRep");
        representant.setCodiPostal("07001");
        representant.setEmail("emailRep@limit.es");
        representant.setTelefon("654321098");
        representant.setObservacions("Observacions Rep");
        representant.setPreferenciaIdioma(InteressatIdiomaEnumDto.ES);
//        representant.setTipus(InteressatTipusEnumDto.PERSONA_FISICA);
        representant.setNom("NomRep");
        representant.setLlinatge1("Llinatge1Rep");
        representant.setLlinatge2("Llinatge2Rep");

        InteressatPersonaJuridicaDto interessat = new InteressatPersonaJuridicaDto();
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
//        interessat.setTipus(InteressatTipusEnumDto.PERSONA_FISICA);
        interessat.setRaoSocial("RaoSocial");
        interessat.setPreferenciaIdioma(InteressatIdiomaEnumDto.CA);
        interessat.setRepresentant(representant);

        return interessat;
    }

    private RegistreInteressatEntity getIncomingInteressat() {
        RegistreInteressatEntity representant = RegistreInteressatEntity.getBuilder(InteressatTipus.PERSONA_FISICA)
                .nom("NouNomRep")
                .llinatge1("NouLlinatge1Rep")
                .llinatge2("NouLlinatge2Rep")
                .documentTipus(DocumentTipus.NIF)
                .documentNumero("00000000A")
//                .paisCodi("AN")
//                .pais("Andorra")
//                .provincia("01")
//                .municipiCodi("001")
//                .municipi("Palma")
//                .adresa("AdressaRep")
//                .cp("07001")
//                .email("emailRep@limit.es")
//                .telefon("654321098")
//                .observacions("Observacions Rep")
                .build();

        RegistreInteressatEntity interessat = RegistreInteressatEntity.getBuilder(InteressatTipus.PERSONA_JURIDICA)
                .raoSocial("NovaRaoSocial")
                .documentTipus(DocumentTipus.NIF)
                .documentNumero("99999999R")
//                .paisCodi("ES")
//                .pais("ES")
//                .provincia("07")
//                .municipiCodi("033")
//                .municipi("033")
//                .adresa("Adressa")
//                .cp("07500")
//                .email("email@limit.es")
//                .telefon("678901234")
//                .observacions("Observacions")
                .representant(representant)
                .build();
        return interessat;
    }

    private InteressatDto getExpectedInteressat() {
        InteressatPersonaFisicaDto representant = new InteressatPersonaFisicaDto();
        representant.setDocumentTipus(InteressatDocumentTipusEnumDto.NIF);
        representant.setDocumentNum("00000000A");
        representant.setPais("AN");
        representant.setProvincia("01");
        representant.setMunicipi("001");
        representant.setAdresa("AdressaRep");
        representant.setCodiPostal("07001");
        representant.setEmail("emailRep@limit.es");
        representant.setTelefon("654321098");
        representant.setObservacions("Observacions Rep");
        representant.setPreferenciaIdioma(InteressatIdiomaEnumDto.ES);
//        representant.setTipus(InteressatTipusEnumDto.PERSONA_FISICA);
        representant.setNom("NouNomRep");
        representant.setLlinatge1("NouLlinatge1Rep");
        representant.setLlinatge2("NouLlinatge2Rep");

        InteressatPersonaJuridicaDto interessat = new InteressatPersonaJuridicaDto();
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
        interessat.setPreferenciaIdioma(InteressatIdiomaEnumDto.CA);
//        interessat.setTipus(InteressatTipusEnumDto.PERSONA_FISICA);
        interessat.setRaoSocial("NovaRaoSocial");
        interessat.setRepresentant(representant);

        return interessat;
    }

    private boolean checkInteressatEquals(InteressatDto interessatEsperat, InteressatDto interessatMerged) {
        boolean interessatsEquals = true;



        return interessatsEquals;
    }

    //    private Set<InteressatEntity> getExistingInteressats() {
//        Set<InteressatEntity> interessats = new HashSet<>();
//        InteressatPersonaFisicaEntity representant = InteressatPersonaFisicaEntity.getBuilder(
//                "NomRep",
//                "Llinatge1Rep",
//                "Llinatge2Rep",
//                InteressatDocumentTipusEnumDto.NIF,
//                "00000000A",
//                "AN",
//                "01",
//                "001",
//                "AdressaRep",
//                "07001",
//                "emailRep@limit.es",
//                "654321098",
//                "Observacions Rep",
//                InteressatIdiomaEnumDto.ES,
//                null,
//                null,
//                false,
//                false,
//                false
//        ).build();
//        representant.updateEsRepresentant(true);
//
//        InteressatPersonaFisicaEntity interessat = InteressatPersonaFisicaEntity.getBuilder(
//                "Nom",
//                "Llinatge1",
//                "Llinatge2",
//                InteressatDocumentTipusEnumDto.NIF,
//                "99999999R",
//                "ES",
//                "07",
//                "033",
//                "Adressa",
//                "07500",
//                "email@limit.es",
//                "678901234",
//                "Observacions",
//                InteressatIdiomaEnumDto.CA,
//                null,
//                representant,
//                false,
//                false,
//                false
//        ).build();
//        interessats.add(interessat);
//        return interessats;
//    }

}