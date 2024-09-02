package es.caib.ripea.plugin.caib.summarize;

import es.caib.ripea.core.api.dto.Resum;
import es.caib.ripea.core.api.dto.config.ConfigDto;
import es.caib.ripea.plugin.SistemaExternException;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Properties;

import static org.junit.Assert.*;

@Ignore
public class SummarizePluginBertTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void testGetSummarizeSuccess() throws Exception {
        Properties properties = getProperties();
        SummarizePluginBert summarizePluginBert = new SummarizePluginBert(ConfigDto.prefix + ".", properties);
        String sampleText = "Són dies difícils per al PSOE, entre el cas de Begoña Gómez i l'acord per a un nou model de finançament català, els socialistes tenen molt fronts oberts dins i fora del partit. Tot plegat els està passant factura a les enquestes. Així ho mostra un sondeig de Sigma Dos per a El Mundo, dut a terme entre el 5 i el 8 d'agost. En comparació amb una enquesta feta per la mateixa companyia 20 dies abans, els de Pedro Sánchez reculen en les seves perspectives electorals. Fa aproximadament un mes, els socialistes aconseguien arribar als 128 escons, però ara han caigut fins als 124. Tanmateix, continua sent una millora respecte als 121 diputats que tenen actualment. ";

        Resum resultResum = summarizePluginBert.getSummarize(sampleText);
        assertNotNull(resultResum);
    }

    @Test
    public void testGetSummarizeFailure() throws Exception {
        Properties properties = getProperties();
        SummarizePluginBert summarizePluginBert = new SummarizePluginBert(ConfigDto.prefix + ".", properties);
        String sampleText = null;

        exceptionRule.expect(SistemaExternException.class);
        Resum resultResum = summarizePluginBert.getSummarize(sampleText);
    }

    @Test
    public void testIsActiveWhenUrlIsNull() throws Exception {
        Properties properties = new Properties();
        properties.put("es.caib.ripea.plugin.summarize.bert.debug", "true");
        SummarizePluginBert summarizePluginBert = new SummarizePluginBert(ConfigDto.prefix + ".", properties);

        boolean isActive = summarizePluginBert.isActive();

        assertFalse(isActive);
    }

    @Test
    public void testIsActiveWhenUrlIsInvalid() throws Exception {
        Properties properties = getProperties();
        properties.put("es.caib.ripea.plugin.summarize.bert.url", "http://172.17.0.3:8081/");
        SummarizePluginBert summarizePluginBert = new SummarizePluginBert(ConfigDto.prefix + ".", properties);

        boolean isActive = summarizePluginBert.isActive();

        assertFalse(isActive);
}

    @Test
    public void testIsActiveWhenUrlIsValid() throws Exception {
        Properties properties = getProperties();
        SummarizePluginBert summarizePluginBert = new SummarizePluginBert(ConfigDto.prefix + ".", properties);

        boolean isActive = summarizePluginBert.isActive();

        assertTrue(isActive);
    }

    private Properties getProperties() {
        Properties properties = new Properties();
        properties.put("es.caib.ripea.plugin.summarize.bert.url", "http://172.17.0.3:8080/");
        properties.put("es.caib.ripea.plugin.summarize.bert.debug", "true");
        return properties;
    }
}
