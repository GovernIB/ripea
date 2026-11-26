package es.caib.ripea.service.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.caib.ripea.persistence.entity.resourceentity.ExcepcioLogResourceEntity;
import es.caib.ripea.persistence.entity.resourcerepository.ExcepcioLogResourceRepository;

/**
 * Tests unitaris per a ExcepcioLogHelper: les excepcions de les accions executades sense monitors no es desen al monitor
 * d'excepcions, i l'esborrat de les antigues respecta els dies de conservació.
 */
@ExtendWith(MockitoExtension.class)
class ExcepcioLogHelperTest {

	@Mock private ExcepcioLogResourceRepository excepcioLogResourceRepository;
	@InjectMocks private ExcepcioLogHelper helper;

	@AfterEach
	void netejar() {
		SenseMonitorsUtils.netejar();
	}

	@Test
	void excepcioEsRegistra() {
		helper.addExcepcio("/expedient/1", new IllegalStateException("error"));
		verify(excepcioLogResourceRepository).save(any(ExcepcioLogResourceEntity.class));
	}

	@Test
	void excepcioSenseMonitorsNoEsRegistra() throws Exception {
		SenseMonitorsUtils.executar(() -> {
			helper.addExcepcio("/expedient/1", new IllegalStateException("error"));
			helper.addExcepcio("/expedient/1", new IllegalStateException("error"), "param1", "param2");
			return null;
		});
		verifyNoInteractions(excepcioLogResourceRepository);
	}

	// ---------------------------------------------------------------- esborrat de les excepcions antigues

	@Test
	void esborrarExcepcionsAntiguesEsborraLesAnteriorsALaDataLimit() {
		when(excepcioLogResourceRepository.deleteByDataBefore(any(Date.class))).thenReturn(5);

		Date abans = DateHelper.dataLimitConservacio(30);
		int esborrades = helper.esborrarExcepcionsAntigues(30);
		Date despres = DateHelper.dataLimitConservacio(30);

		assertThat(esborrades).isEqualTo(5);
		ArgumentCaptor<Date> dataLimit = ArgumentCaptor.forClass(Date.class);
		verify(excepcioLogResourceRepository).deleteByDataBefore(dataLimit.capture());
		assertThat(dataLimit.getValue()).isBetween(abans, despres, true, true);
	}

	@Test
	void esborrarExcepcionsAntiguesSenseDiesNoEsborraRes() {
		assertThatThrownBy(() -> helper.esborrarExcepcionsAntigues(0)).isInstanceOf(IllegalArgumentException.class);
		verifyNoInteractions(excepcioLogResourceRepository);
	}

}
