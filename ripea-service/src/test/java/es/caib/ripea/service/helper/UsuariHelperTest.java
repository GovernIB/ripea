package es.caib.ripea.service.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.caib.ripea.persistence.entity.UsuariEntity;
import es.caib.ripea.persistence.repository.UsuariRepository;
import es.caib.ripea.service.intf.config.PropertyConfig;

/**
 * Tests unitaris per a UsuariHelper: creació de l'usuari de sistema dels processos en segon pla.
 */
@ExtendWith(MockitoExtension.class)
class UsuariHelperTest {

	@Mock private UsuariRepository usuariRepository;
	@Mock private ConfigHelper configHelper;
	@InjectMocks private UsuariHelper usuariHelper;

	@Test
	void usuariSistemaExistentNoEsTornaACrear() {
		when(usuariRepository.existsById(UsuariHelper.CODI_USUARI_SISTEMA)).thenReturn(true);

		assertThat(usuariHelper.crearUsuariSistemaSiNoExisteix()).isFalse();
		verify(usuariRepository, never()).saveAndFlush(any());
	}

	@Test
	void usuariSistemaInexistentEsCreaSenseConsultarElPluginDUsuaris() {
		when(usuariRepository.existsById(UsuariHelper.CODI_USUARI_SISTEMA)).thenReturn(false);
		when(configHelper.getConfig(PropertyConfig.IDIOMA_DEFECTE)).thenReturn(null);

		assertThat(usuariHelper.crearUsuariSistemaSiNoExisteix()).isTrue();

		ArgumentCaptor<UsuariEntity> captor = ArgumentCaptor.forClass(UsuariEntity.class);
		verify(usuariRepository).saveAndFlush(captor.capture());
		UsuariEntity usuari = captor.getValue();
		assertThat(usuari.getCodi()).isEqualTo(UsuariHelper.CODI_USUARI_SISTEMA);
		assertThat(usuari.getNif()).isNull();
		assertThat(usuari.getIdioma()).isEqualTo("ca");
		assertThat(usuari.isInicialitzat()).isTrue();
	}

}
