package es.caib.ripea.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import es.caib.ripea.service.intf.dto.ExecucioMassivaTipusDto;

/**
 * Quins tipus d'execució massiva es poden cancel·lar: totes excepte les exportacions i moure entre expedients.
 */
public class ExecucioMassivaTipusDtoTest {

    @Test
    void lesExportacionsIMoureEntreExpedients_noEsPodenCancelar() {
        Arrays.stream(ExecucioMassivaTipusDto.values())
                .filter(tipus -> tipus.name().startsWith("EXPORTAR_"))
                .forEach(tipus -> assertThat(tipus.isCancelable()).as(tipus.name()).isFalse());
        assertThat(ExecucioMassivaTipusDto.MOURE_EXPEDIENT.isCancelable()).isFalse();
    }

    @Test
    void laRestaDeTipus_esPodenCancelar() {
        assertThat(ExecucioMassivaTipusDto.PORTASIGNATURES.isCancelable()).isTrue();
        assertThat(ExecucioMassivaTipusDto.TANCAMENT.isCancelable()).isTrue();
        assertThat(ExecucioMassivaTipusDto.IMPORTAR_DOCS.isCancelable()).isTrue();
        assertThat(ExecucioMassivaTipusDto.CUSTODIAR_ELEMENTS_PENDENTS.isCancelable()).isTrue();
        assertThat(ExecucioMassivaTipusDto.ESBORRAR_EXPEDIENT.isCancelable()).isTrue();
    }
}
