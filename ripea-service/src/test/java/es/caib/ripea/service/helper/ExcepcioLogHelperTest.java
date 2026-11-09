package es.caib.ripea.service.helper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.caib.ripea.persistence.entity.resourceentity.ExcepcioLogResourceEntity;
import es.caib.ripea.persistence.entity.resourcerepository.ExcepcioLogResourceRepository;

/**
 * Tests unitaris per a ExcepcioLogHelper.addExcepcio: les excepcions de les accions executades sense monitors no es
 * desen al monitor d'excepcions.
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

}
