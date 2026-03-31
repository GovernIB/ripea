package es.caib.ripea.service.resourceservice;

import es.caib.ripea.persistence.entity.resourceentity.AlertaResourceEntity;
import es.caib.ripea.persistence.entity.resourcerepository.AlertaResourceRepository;
import es.caib.ripea.service.intf.base.exception.ActionExecutionException;
import es.caib.ripea.service.intf.model.ExpedientResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertaResourceServiceImplTest {

    @InjectMocks
    private AlertaResourceServiceImpl alertaResourceService;

    @Mock
    private AlertaResourceRepository alertaResourceRepository;

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("MASSIVE_LLEGIT - marca alertas como leídas")
    void testLlegitActionExecutor_exec() throws ActionExecutionException {
        // Given
        AlertaResourceEntity entity1 = new AlertaResourceEntity();
        entity1.setId(1L);
        entity1.setLlegida(false);
        AlertaResourceEntity entity2 = new AlertaResourceEntity();
        entity2.setId(2L);
        entity2.setLlegida(false);
        AlertaResourceEntity entity3 = new AlertaResourceEntity();
        entity3.setId(3L);
        entity3.setLlegida(false);

        ExpedientResource.MassiveAction params = new ExpedientResource.MassiveAction();
        // When
        params.setIds(Arrays.asList(1L, 2L));

        when(alertaResourceRepository.findById(1L)).thenReturn(Optional.of(entity1));
        when(alertaResourceRepository.findById(2L)).thenReturn(Optional.of(entity2));

        AlertaResourceServiceImpl.LlegitActionExecutor executor = alertaResourceService.new LlegitActionExecutor();
        executor.exec(null, null, params);
        // Then
        assertTrue(entity1.getLlegida());
        assertTrue(entity2.getLlegida());
        assertFalse(entity3.getLlegida());
    }

    @Test
    @DisplayName("MASSIVE_LLEGIT - params null")
    void testLlegitActionExecutor_withNullParams() throws ActionExecutionException {
        // Given
        ExpedientResource.MassiveAction params = null;

        // When

        AlertaResourceServiceImpl.LlegitActionExecutor executor = alertaResourceService.new LlegitActionExecutor();
        executor.exec(null, null, params);
        // Then
        verify(alertaResourceRepository, never()).findById(anyLong());
    }
}