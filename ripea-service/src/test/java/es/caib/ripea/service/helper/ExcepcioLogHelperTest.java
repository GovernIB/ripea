package es.caib.ripea.service.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import es.caib.ripea.persistence.entity.resourceentity.ExcepcioLogResourceEntity;
import es.caib.ripea.persistence.entity.resourcerepository.ExcepcioLogResourceRepository;
import es.caib.ripea.service.intf.config.PropertyConfig;

/**
 * Tests unitaris de la deduplicació d'excepcions i de la retenció a ExcepcioLogHelper.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ExcepcioLogHelperTest {

	@Mock private ExcepcioLogResourceRepository repository;
	@Mock private ConfigHelper configHelper;
	@InjectMocks private ExcepcioLogHelper helper;

	@Test
	void repetidaDinsFinestra_incrementaComptadorISenseInserir() {
		when(configHelper.getAsInt(eq(PropertyConfig.EXCEPCIONS_DEDUP_MINUTS), org.mockito.ArgumentMatchers.anyInt())).thenReturn(10);
		when(repository.findIdRecentBySignatura(anyString(), any())).thenReturn(5L);
		when(repository.incrementarOcurrencies(eq(5L), any())).thenReturn(1);

		helper.addExcepcio("/x", new IllegalStateException("error 1"));

		verify(repository).incrementarOcurrencies(eq(5L), any());
		verify(repository, never()).save(any());
	}

	@Test
	void noRepetida_insereixAmbSignaturaIComptadorA1() {
		when(configHelper.getAsInt(eq(PropertyConfig.EXCEPCIONS_DEDUP_MINUTS), org.mockito.ArgumentMatchers.anyInt())).thenReturn(10);
		when(repository.findIdRecentBySignatura(anyString(), any())).thenReturn(null);

		helper.addExcepcio("/x", new IllegalStateException("error"));

		ArgumentCaptor<ExcepcioLogResourceEntity> captor = ArgumentCaptor.forClass(ExcepcioLogResourceEntity.class);
		verify(repository).save(captor.capture());
		assertThat(captor.getValue().getSignatura()).hasSize(64);
		assertThat(captor.getValue().getNumOcurrencies()).isEqualTo(1);
		assertThat(captor.getValue().getDataDarrera()).isEqualTo(captor.getValue().getData());
	}

	@Test
	void dedupDesactivada_insereixSempre() {
		when(configHelper.getAsInt(eq(PropertyConfig.EXCEPCIONS_DEDUP_MINUTS), org.mockito.ArgumentMatchers.anyInt())).thenReturn(0);

		helper.addExcepcio("/x", new IllegalStateException("error"));

		verify(repository, never()).findIdRecentBySignatura(anyString(), any());
		verify(repository).save(any());
	}

	@Test
	void errorEnLaComprovacio_insereixIgualment() {
		when(configHelper.getAsInt(eq(PropertyConfig.EXCEPCIONS_DEDUP_MINUTS), org.mockito.ArgumentMatchers.anyInt())).thenReturn(10);
		when(repository.findIdRecentBySignatura(anyString(), any())).thenThrow(new RuntimeException("BD"));

		helper.addExcepcio("/x", new IllegalStateException("error"));

		verify(repository).save(any());
	}

	@Test
	void signatura_ignoraDigitsIDistingeixClasses() {
		String a = ExcepcioLogHelper.calcularSignatura(new IllegalStateException("Expedient 123 no trobat"));
		String b = ExcepcioLogHelper.calcularSignatura(new IllegalStateException("Expedient 456 no trobat"));
		String c = ExcepcioLogHelper.calcularSignatura(new IllegalArgumentException("Expedient 123 no trobat"));
		assertThat(a).isEqualTo(b).isNotEqualTo(c);
	}

	@Test
	void retencioDies_valorPerDefecteSiNoValid() {
		when(configHelper.getAsInt(eq(PropertyConfig.EXCEPCIONS_RETENCIO_DIES), org.mockito.ArgumentMatchers.anyInt())).thenReturn(-5);
		assertThat(helper.getRetencioDies()).isEqualTo(ExcepcioLogHelper.RETENCIO_DIES_DEFECTE);
		when(configHelper.getAsInt(eq(PropertyConfig.EXCEPCIONS_RETENCIO_DIES), org.mockito.ArgumentMatchers.anyInt())).thenReturn(30);
		assertThat(helper.getRetencioDies()).isEqualTo(30);
	}
}
