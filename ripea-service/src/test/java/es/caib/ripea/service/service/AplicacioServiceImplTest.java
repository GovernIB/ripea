package es.caib.ripea.service.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import es.caib.ripea.persistence.repository.EntitatRepository;
import es.caib.ripea.service.helper.CacheHelper;

/**
 * Tests unitaris per a AplicacioServiceImpl.
 *
 * Cobreix evictCachesUsuariActual: invalidació, en iniciar la sessió, de les caches de l'usuari actual que
 * depenen dels seus rols. No arrenca cap context Spring: les dependències es proporcionen com a mocks.
 */
@ExtendWith(MockitoExtension.class)
class AplicacioServiceImplTest {

    @Mock private CacheHelper cacheHelper;
    @Mock private EntitatRepository entitatRepository;

    @InjectMocks
    private AplicacioServiceImpl aplicacioService;

    @BeforeEach
    void autenticaUsuari() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("usuari1", null, Collections.emptyList()));
    }

    @AfterEach
    void netejaSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void evictCachesUsuariActual_evictaLesCachesDeLUsuariActual() {
        when(entitatRepository.findAllIds()).thenReturn(Collections.emptyList());

        aplicacioService.evictCachesUsuariActual();

        verify(cacheHelper).evictEntitatsAccessiblesUsuari("usuari1");
        verify(cacheHelper).evictFindRolsAmbCodi("usuari1");
        verify(cacheHelper).evictCountAnotacionsPendents("usuari1");
        verify(cacheHelper, never()).evictEntitatsAccessiblesAllUsuaris();
    }

    @Test
    void evictCachesUsuariActual_evictaElsOrgansDeLUsuariPerCadaEntitat() {
        when(entitatRepository.findAllIds()).thenReturn(Arrays.asList(1L, 3721L));

        aplicacioService.evictCachesUsuariActual();

        verify(cacheHelper).evictOrganismesEntitatAmbPermis(1L, "usuari1");
        verify(cacheHelper).evictOrganismesEntitatAmbPermis(3721L, "usuari1");
        verify(cacheHelper).evictOrganismesEntitatAmbPermisDisseny(1L, "usuari1");
        verify(cacheHelper).evictOrganismesEntitatAmbPermisDisseny(3721L, "usuari1");
        verify(cacheHelper, never()).evictAllOrganismesEntitatAmbPermis();
    }

    @Test
    void evictCachesUsuariActual_senseEntitats_noEvictaCapOrgan() {
        when(entitatRepository.findAllIds()).thenReturn(Collections.emptyList());

        aplicacioService.evictCachesUsuariActual();

        verify(cacheHelper, never()).evictOrganismesEntitatAmbPermis(anyLong(), any());
        verify(cacheHelper, never()).evictOrganismesEntitatAmbPermisDisseny(anyLong(), any());
    }
}
