package es.caib.ripea.core.helper;

import es.caib.ripea.core.api.exception.NotDefinedConfigException;
import es.caib.ripea.core.entity.config.ConfigEntity;
import es.caib.ripea.core.entity.config.ConfigGroupEntity;
import es.caib.ripea.core.repository.config.ConfigGroupRepository;
import es.caib.ripea.core.repository.config.ConfigRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

@RunWith(MockitoJUnitRunner.class)
public class ConfigHelperTest {
    @Mock
    private ConfigRepository configRepository;
    @Mock
    private ConfigGroupRepository configGroupRepository;

    @InjectMocks
    private ConfigHelper configHelper;

    private final String MAX_INTENTS_CALLBACK = "10";

    @Before
    public void setUp() throws Exception {
        Mockito.when(configRepository.findOne(
                Mockito.eq("PROPERTY_KEY"))
        ).thenReturn(
                new ConfigEntity("PROPERTY_KEY", "PROPERTY_VALUE")
        );
    }

    @Test
    public void whenGetConfig_ThenReturnConfigValue() throws Exception {
        // When
        String propertyValue = configHelper.getConfig("PROPERTY_KEY");

        // Then
        Assert.assertEquals("PROPERTY_VALUE", propertyValue);
    }

    @Test(expected = NotDefinedConfigException.class)
    public void GivenNonExistingKey_whenGetConfig_ThenException() throws Exception {
        // When
        String propertyValue = configHelper.getConfig("PROPERTY_INEXISTENT");
    }

    @Test
    public void whenGetGroupProperties_ThenGetAllGroupProperties() throws Exception {
        // Given
        Set<ConfigEntity> configEntities_g1 = new HashSet<>();
        configEntities_g1.add(new ConfigEntity("CONFIG_1_1_KEY", "CONFIG_1_1"));
        configEntities_g1.add(new ConfigEntity("CONFIG_1_2_KEY", "CONFIG_1_2"));

        Set<ConfigEntity> configEntities_g2 = new HashSet<>();
        configEntities_g2.add(new ConfigEntity("CONFIG_2_1_KEY", "CONFIG_2_1"));
        configEntities_g2.add(new ConfigEntity("CONFIG_2_2_KEY", "CONFIG_2_2"));

        Set<ConfigGroupEntity> innerConfigs = new HashSet<>();
        innerConfigs.add(ConfigGroupEntity.builder()
                .key("GROUP_2")
                .configs(configEntities_g2)
                .innerConfigs(new HashSet<ConfigGroupEntity>())
                .build());
        ConfigGroupEntity configGroup_1 = ConfigGroupEntity.builder()
                .key("GROUP_1")
                .configs(configEntities_g1)
                .innerConfigs(innerConfigs)
                .build();

        Mockito.when(configGroupRepository.findOne(
                Mockito.eq("GROUP_1"))
        ).thenReturn(
                configGroup_1
        );

        // When
        Properties groupProperties = configHelper.getGroupProperties("GROUP_1");

        // Then
        Assert.assertNotNull(groupProperties.get("CONFIG_1_1_KEY"));
        Assert.assertNotNull(groupProperties.get("CONFIG_1_2_KEY"));
        Assert.assertNotNull(groupProperties.get("CONFIG_2_1_KEY"));
        Assert.assertNotNull(groupProperties.get("CONFIG_2_2_KEY"));

        Assert.assertEquals("CONFIG_1_1", groupProperties.get("CONFIG_1_1_KEY"));
        Assert.assertEquals("CONFIG_1_2", groupProperties.get("CONFIG_1_2_KEY"));
        Assert.assertEquals("CONFIG_2_1", groupProperties.get("CONFIG_2_1_KEY"));
        Assert.assertEquals("CONFIG_2_2", groupProperties.get("CONFIG_2_2_KEY"));
    }

}