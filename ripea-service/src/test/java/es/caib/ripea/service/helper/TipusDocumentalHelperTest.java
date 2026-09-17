package es.caib.ripea.service.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Locale;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.context.i18n.LocaleContextHolder;

import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.TipusDocumentalEntity;
import es.caib.ripea.persistence.repository.DocumentRepository;
import es.caib.ripea.persistence.repository.EntitatRepository;
import es.caib.ripea.persistence.repository.MetaDocumentRepository;
import es.caib.ripea.persistence.repository.TipusDocumentalRepository;
import es.caib.ripea.service.intf.dto.TipusDocumentalDto;
import es.caib.ripea.service.intf.exception.ValidationException;

/**
 * Tests unitaris per a TipusDocumentalHelper.
 *
 * Cobreix la resolució tolerant del nom (el codi es guarda sense FK i pot no existir a la taula)
 * i les comprovacions d'esborrat, canvi de codi i assignació de tipus documentals desactivats.
 * No arrenca cap context Spring: totes les dependències es proporcionen com a mocks de Mockito.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TipusDocumentalHelperTest {

    @Mock private EntitatRepository entitatRepository;
    @Mock private TipusDocumentalRepository tipusDocumentalRepository;
    @Mock private MetaDocumentRepository metaDocumentRepository;
    @Mock private DocumentRepository documentRepository;
    @Mock private MessageHelper messageHelper;
    @Mock private PluginHelper pluginHelper;

    @InjectMocks
    private TipusDocumentalHelper helper;

    private static final Long ENTITAT_ID = 1L;
    private static final String CODI = "TD99";
    private static final String CODI_NOU = "TD98";
    private static final String NO_DISPONIBLE = "Tipus documental no disponible";

    @AfterEach
    void resetLocale() {
        LocaleContextHolder.resetLocaleContext();
    }

    // =========================================================================
    // getNomTipusDocumental
    // =========================================================================

    @Test
    void getNomTipusDocumental_codiBuit_retornaNull() {
        assertThat(helper.getNomTipusDocumental(null, ENTITAT_ID, true)).isNull();
        assertThat(helper.getNomTipusDocumental("", ENTITAT_ID, true)).isNull();
    }

    @Test
    void getNomTipusDocumental_existeix_retornaNomSegonsIdioma() {
        TipusDocumentalEntity tipus = tipusDocumental(true);
        when(tipusDocumentalRepository.findByCodiAndEntitatId(CODI, ENTITAT_ID)).thenReturn(tipus);

        LocaleContextHolder.setLocale(new Locale("ca"));
        assertThat(helper.getNomTipusDocumental(CODI, ENTITAT_ID, true)).isEqualTo("Nom català");

        LocaleContextHolder.setLocale(new Locale("es"));
        assertThat(helper.getNomTipusDocumental(CODI, ENTITAT_ID, true)).isEqualTo("Nombre español");
        verify(pluginHelper, never()).documentTipusAddicionals();
    }

    @Test
    void getNomTipusDocumental_esborratAmbCodiNti_retornaNomNtiSenseConsultarPlugin() {
        when(messageHelper.getMessage("document.nti.tipdoc.enum." + CODI)).thenReturn("Altres");

        assertThat(helper.getNomTipusDocumental(CODI, ENTITAT_ID, true)).isEqualTo("Altres");
        verify(pluginHelper, never()).documentTipusAddicionals();
    }

    @Test
    void getNomTipusDocumental_codiAddicional_retornaNomDelPlugin() {
        when(messageHelper.getMessage("document.nti.tipdoc.enum.ADD1")).thenReturn("???document.nti.tipdoc.enum.ADD1???");
        TipusDocumentalDto addicional = new TipusDocumentalDto();
        addicional.setCodi("ADD1");
        addicional.setNom("Addicional");
        when(pluginHelper.documentTipusAddicionals()).thenReturn(Collections.singletonList(addicional));

        assertThat(helper.getNomTipusDocumental("ADD1", ENTITAT_ID, true)).isEqualTo("Addicional");
    }

    @Test
    void getNomTipusDocumental_codiDesconegutSenseAddicionals_retornaNoDisponibleSenseConsultarPlugin() {
        when(messageHelper.getMessage("document.nti.tipdoc.enum.XX01")).thenReturn("???document.nti.tipdoc.enum.XX01???");
        when(messageHelper.getMessage("tipusdocumental.nom.no.disponible")).thenReturn(NO_DISPONIBLE);

        assertThat(helper.getNomTipusDocumental("XX01", ENTITAT_ID, false)).isEqualTo(NO_DISPONIBLE);
        verify(pluginHelper, never()).documentTipusAddicionals();
    }

    @Test
    void getNomTipusDocumental_errorDelPlugin_noPropagaIRetornaNoDisponible() {
        when(messageHelper.getMessage("document.nti.tipdoc.enum.XX01")).thenReturn("???document.nti.tipdoc.enum.XX01???");
        when(messageHelper.getMessage("tipusdocumental.nom.no.disponible")).thenReturn(NO_DISPONIBLE);
        when(pluginHelper.documentTipusAddicionals()).thenThrow(new RuntimeException("Arxiu no disponible"));

        assertThat(helper.getNomTipusDocumental("XX01", ENTITAT_ID, true)).isEqualTo(NO_DISPONIBLE);
    }

    @Test
    void getNomTipusDocumental_senseEntitat_usaElPrimerTipusAmbElCodi() {
        TipusDocumentalEntity tipus = tipusDocumental(true);
        when(tipusDocumentalRepository.findByCodi(CODI)).thenReturn(Collections.singletonList(tipus));
        LocaleContextHolder.setLocale(new Locale("es"));

        assertThat(helper.getNomTipusDocumental(CODI, null, false)).isEqualTo("Nombre español");
    }

    // =========================================================================
    // comprovarEsborrable
    // =========================================================================

    @Test
    void comprovarEsborrable_senseUs_noLlança() {
        assertThatCode(() -> helper.comprovarEsborrable(ENTITAT_ID, CODI)).doesNotThrowAnyException();
    }

    @Test
    void comprovarEsborrable_assignatATipusDeDocument_llança() {
        when(metaDocumentRepository.existsByEntitatIdAndNtiTipoDocumental(ENTITAT_ID, CODI)).thenReturn(true);
        when(messageHelper.getMessage(eq("tipusdocumental.esborrar.error.utilitzat"), any(Object[].class))).thenReturn("En ús");

        assertThatThrownBy(() -> helper.comprovarEsborrable(ENTITAT_ID, CODI))
                .isInstanceOf(ValidationException.class)
                .hasMessage("En ús");
    }

    @Test
    void comprovarEsborrable_assignatADocuments_llança() {
        when(documentRepository.existsByEntitatIdAndNtiTipoDocumental(ENTITAT_ID, CODI)).thenReturn(true);

        assertThatThrownBy(() -> helper.comprovarEsborrable(ENTITAT_ID, CODI))
                .isInstanceOf(ValidationException.class);
    }

    // =========================================================================
    // comprovarCanviCodi
    // =========================================================================

    @Test
    void comprovarCanviCodi_mateixCodi_noConsultaUs() {
        assertThatCode(() -> helper.comprovarCanviCodi(ENTITAT_ID, CODI, CODI)).doesNotThrowAnyException();
        verify(metaDocumentRepository, never()).existsByEntitatIdAndNtiTipoDocumental(anyLong(), anyString());
        verify(documentRepository, never()).existsByEntitatIdAndNtiTipoDocumental(anyLong(), anyString());
    }

    @Test
    void comprovarCanviCodi_codiAnteriorEnUs_llança() {
        when(documentRepository.existsByEntitatIdAndNtiTipoDocumental(ENTITAT_ID, CODI)).thenReturn(true);

        assertThatThrownBy(() -> helper.comprovarCanviCodi(ENTITAT_ID, CODI, CODI_NOU))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void comprovarCanviCodi_codiAnteriorSenseUs_noLlança() {
        assertThatCode(() -> helper.comprovarCanviCodi(ENTITAT_ID, CODI, CODI_NOU)).doesNotThrowAnyException();
    }

    // =========================================================================
    // comprovarAssignable
    // =========================================================================

    @Test
    void comprovarAssignable_tipusDesactivatNou_llança() {
        when(tipusDocumentalRepository.findByCodiAndEntitatId(CODI_NOU, ENTITAT_ID)).thenReturn(tipusDocumental(false));

        assertThatThrownBy(() -> helper.comprovarAssignable(ENTITAT_ID, CODI, CODI_NOU))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void comprovarAssignable_esConservaElTipusDesactivatQueJaTenia_noLlança() {
        when(tipusDocumentalRepository.findByCodiAndEntitatId(CODI, ENTITAT_ID)).thenReturn(tipusDocumental(false));

        assertThatCode(() -> helper.comprovarAssignable(ENTITAT_ID, CODI, CODI)).doesNotThrowAnyException();
    }

    @Test
    void comprovarAssignable_tipusActiuOCodiQueNoEsALaTaula_noLlança() {
        when(tipusDocumentalRepository.findByCodiAndEntitatId(CODI_NOU, ENTITAT_ID)).thenReturn(tipusDocumental(true));

        assertThatCode(() -> helper.comprovarAssignable(ENTITAT_ID, null, CODI_NOU)).doesNotThrowAnyException();
        assertThatCode(() -> helper.comprovarAssignable(ENTITAT_ID, null, "ADD1")).doesNotThrowAnyException();
    }

    private static TipusDocumentalEntity tipusDocumental(boolean actiu) {
        TipusDocumentalEntity tipus = TipusDocumentalEntity.getBuilder(
                CODI,
                "Nombre español",
                mock(EntitatEntity.class),
                "Nom català").build();
        tipus.updateActiu(actiu);
        return tipus;
    }
}
