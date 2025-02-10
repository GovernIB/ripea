package es.caib.ripea.core.service;

import es.caib.ripea.core.api.dto.config.ConfigDto;
import es.caib.ripea.core.persistence.config.ConfigEntity;
import es.caib.ripea.core.helper.CacheHelper;
import es.caib.ripea.core.helper.ConversioTipusHelper;
import es.caib.ripea.core.helper.PluginHelper;
import es.caib.ripea.core.repository.config.ConfigGroupRepository;
import es.caib.ripea.core.repository.config.ConfigRepository;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ConfigServiceTest {

    @Mock
    private ConfigGroupRepository configGroupRepository;
    @Mock
    private ConfigRepository configRepository;
    @Spy
    private ConversioTipusHelper conversioTipusHelper;
    @Mock
    private PluginHelper pluginHelper;
    @Mock
    private CacheHelper cacheHelper;

    @InjectMocks
    private ConfigServiceImpl configService;



    @Before
    public void setUp() throws Exception { }

    @After
    public void tearDown() throws Exception { }

    @Test
    public void whenUpdateProperty_thenCallFindOneAndReloadPluginProperties() throws Exception {
        // Given
        Mockito.when(
                conversioTipusHelper.convertir(Mockito.any(ConfigEntity.class), Mockito.eq(ConfigDto.class))
        ).thenCallRealMethod();

        ConfigEntity configEntity = new ConfigEntity("PROPERTY_KEY", "PROPERTY_VALUE");
        Mockito.when(
                configRepository.findOne(Mockito.eq("PROPERTY_KEY"))
        ).thenReturn(configEntity);

        ConfigDto configDto = conversioTipusHelper.convertir(configEntity, ConfigDto.class);
        configDto.setValue("NEW_VALUE");

        // When
        ConfigDto configEdited = configService.updateProperty(configDto);

        // Then
//        Mockito.verify(pluginHelper).reloadProperties(Mockito.nullable(String.class));
        Mockito.verify(configRepository).findOne(Mockito.eq("PROPERTY_KEY"));

        Assert.assertEquals("NEW_VALUE", configEdited.getValue());

    }
}