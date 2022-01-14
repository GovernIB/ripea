/**
 * 
 */
package es.caib.ripea.core.service;

import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import es.caib.ripea.core.api.dto.DadaDto;
import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.ExpedientDto;
import es.caib.ripea.core.api.dto.MetaDadaDto;
import es.caib.ripea.core.api.dto.MetaDadaTipusEnumDto;
import es.caib.ripea.core.api.dto.MetaExpedientDto;
import es.caib.ripea.core.api.dto.MultiplicitatEnumDto;

/**
 * Tests per al servei de gestió d'expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/es/caib/ripea/core/application-context-test.xml"})
public class ContingutServiceTest extends BaseExpedientServiceTest {

	private MetaDadaDto metaDadaText;
	private MetaDadaDto metaDadaData;
	private MetaDadaDto metaDadaImport;
	private MetaDadaDto metaDadaSencer;
	private MetaDadaDto metaDadaFlotant;
	private MetaDadaDto metaDadaBoolea;
	
	private MetaDadaDto metaDadaTextMultiple;

	@Before
	public void setUp() {
		super.setUp();
		setDefaultConfigs();
		metaDadaText = new MetaDadaDto();
		metaDadaText.setCodi("metaDadaText");
		metaDadaText.setNom("metaDadaText");
		metaDadaText.setDescripcio("metaDadaText");
		metaDadaText.setTipus(MetaDadaTipusEnumDto.TEXT);
		metaDadaText.setMultiplicitat(MultiplicitatEnumDto.M_1);
		
		metaDadaData = new MetaDadaDto();
		metaDadaData.setCodi("metaDadaData");
		metaDadaData.setNom("metaDadaData");
		metaDadaData.setDescripcio("metaDadaData");
		metaDadaData.setTipus(MetaDadaTipusEnumDto.DATA);
		metaDadaData.setMultiplicitat(MultiplicitatEnumDto.M_1);
		
		metaDadaImport = new MetaDadaDto();
		metaDadaImport.setCodi("metaDadaImport");
		metaDadaImport.setNom("metaDadaImport");
		metaDadaImport.setDescripcio("metaDadaImport");
		metaDadaImport.setTipus(MetaDadaTipusEnumDto.IMPORT);
		metaDadaImport.setMultiplicitat(MultiplicitatEnumDto.M_1);
		
		metaDadaSencer = new MetaDadaDto();
		metaDadaSencer.setCodi("metaDadaSencer");
		metaDadaSencer.setNom("metaDadaSencer");
		metaDadaSencer.setDescripcio("metaDadaSencer");
		metaDadaSencer.setTipus(MetaDadaTipusEnumDto.SENCER);
		metaDadaSencer.setMultiplicitat(MultiplicitatEnumDto.M_1);
		
		metaDadaFlotant = new MetaDadaDto();
		metaDadaFlotant.setCodi("metaDadaFlotant");
		metaDadaFlotant.setNom("metaDadaFlotant");
		metaDadaFlotant.setDescripcio("metaDadaFlotant");
		metaDadaFlotant.setTipus(MetaDadaTipusEnumDto.FLOTANT);
		metaDadaFlotant.setMultiplicitat(MultiplicitatEnumDto.M_1);
		
		metaDadaBoolea = new MetaDadaDto();
		metaDadaBoolea.setCodi("metaDadaBoolea");
		metaDadaBoolea.setNom("metaDadaBoolea");
		metaDadaBoolea.setDescripcio("metaDadaBoolea");
		metaDadaBoolea.setTipus(MetaDadaTipusEnumDto.BOOLEA);
		metaDadaBoolea.setMultiplicitat(MultiplicitatEnumDto.M_1);
		
		
		metaDadaTextMultiple = new MetaDadaDto();
		metaDadaTextMultiple.setCodi("metaDadaTextMultiple");
		metaDadaTextMultiple.setNom("metaDadaTextMultiple");
		metaDadaTextMultiple.setDescripcio("metaDadaTextMultiple");
		metaDadaTextMultiple.setTipus(MetaDadaTipusEnumDto.TEXT);
		metaDadaTextMultiple.setMultiplicitat(MultiplicitatEnumDto.M_0_N);
	}

	@Test
    public void modificarConsultarDades() {
		testAmbElementsIExpedient(
				new TestAmbElementsCreats() {
					@Override
					public void executar(List<Object> elementsCreats) {
						EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
						MetaExpedientDto metaExpedient = (MetaExpedientDto)elementsCreats.get(2);
						ExpedientDto expedientCreat = (ExpedientDto)elementsCreats.get(5);

						autenticarUsuari("admin");
						
						MetaDadaDto metaDadaTextCreada = metaDadaService.create(
								entitatCreada.getId(),
								metaExpedient.getId(),
								metaDadaText, "tothom", null);
						
						MetaDadaDto metaDadaDataCreada = metaDadaService.create(
								entitatCreada.getId(),
								metaExpedient.getId(),
								metaDadaData, "tothom", null);
						
						MetaDadaDto metaDadaImportCreada = metaDadaService.create(
								entitatCreada.getId(),
								metaExpedient.getId(),
								metaDadaImport, "tothom", null);
						
						MetaDadaDto metaDadaSencerCreada = metaDadaService.create(
								entitatCreada.getId(),
								metaExpedient.getId(),
								metaDadaSencer, "tothom", null);
						
						MetaDadaDto metaDadaFlotantCreada = metaDadaService.create(
								entitatCreada.getId(),
								metaExpedient.getId(),
								metaDadaFlotant, "tothom", null);
						
						MetaDadaDto metaDadaBooleaCreada = metaDadaService.create(
								entitatCreada.getId(),
								metaExpedient.getId(),
								metaDadaBoolea, "tothom", null);
						
						MetaDadaDto metaDadaTextMultipleCreada = metaDadaService.create(
								entitatCreada.getId(),
								metaExpedient.getId(),
								metaDadaTextMultiple, "tothom", null);
						
						
						autenticarUsuari("user");
						
				        Calendar cal = Calendar.getInstance();
				        cal.set(Calendar.YEAR, 2020);
				        cal.set(Calendar.MONTH, 05);
				        cal.set(Calendar.DAY_OF_MONTH, 05);
				        cal.set(Calendar.HOUR_OF_DAY, 0);
				        cal.set(Calendar.MINUTE, 0);
				        cal.set(Calendar.SECOND, 0);
				        cal.set(Calendar.MILLISECOND, 0);
						
						Map<String, Object> valors = new HashMap<String, Object>();
						valors.put(metaDadaTextCreada.getCodi(),"text");
						valors.put(metaDadaDataCreada.getCodi(),cal.getTime());
						valors.put(metaDadaImportCreada.getCodi(),new BigDecimal(23.00));
						valors.put(metaDadaSencerCreada.getCodi(),new Long(23));
						valors.put(metaDadaFlotantCreada.getCodi(),new Double(13.00));
						valors.put(metaDadaBooleaCreada.getCodi(), true);
						valors.put(metaDadaTextMultipleCreada.getCodi(),
								new String[] { "one", "two" });

						contingutService.dadaSave(
								entitatCreada.getId(),
								expedientCreat.getId(),
								valors);
						
						ExpedientDto contingut = (ExpedientDto) contingutService.findAmbIdUser(
								entitatCreada.getId(),
								expedientCreat.getId(),
								true,
								true, null);
						
						List<MetaDadaDto> metaDades = metaDadaService.findByNode(entitatCreada.getId(),
								expedientCreat.getId());
						int mutlipleCount = 0;
						for (MetaDadaDto metaDadaDto : metaDades) {
							for (DadaDto dadaDto : contingut.getDades()) {
								if (dadaDto.getMetaDada().getCodi().equals(metaDadaDto.getCodi())) {

									if (dadaDto.getMetaDada().getTipus() == MetaDadaTipusEnumDto.TEXT) {
										if (dadaDto.getMetaDada().getMultiplicitat() == MultiplicitatEnumDto.M_0_N) {
											mutlipleCount++;
										} else {
											if (!dadaDto.getValor().equals("text")) {
												fail("Values don't match");
											}
										}
									} else if (dadaDto.getMetaDada().getTipus() == MetaDadaTipusEnumDto.DATA) {
										if (!dadaDto.getValor().equals(cal.getTime())) {
											fail("Values don't match");
										}
									} else if (dadaDto.getMetaDada().getTipus() == MetaDadaTipusEnumDto.IMPORT) {
										if (!dadaDto.getValor().equals(new BigDecimal(23.00))) {
											fail("Values don't match");
										}
									} else if (dadaDto.getMetaDada().getTipus() == MetaDadaTipusEnumDto.SENCER) {
										if (!dadaDto.getValor().equals(new Long(23))) {
											fail("Values don't match");
										}
									} else if (dadaDto.getMetaDada().getTipus() == MetaDadaTipusEnumDto.FLOTANT) {
										if (!dadaDto.getValor().equals(new Double(13.00))) {
											fail("Values don't match");
										}
									} else if (dadaDto.getMetaDada().getTipus() == MetaDadaTipusEnumDto.BOOLEA) {
										if (!dadaDto.getValor().equals(true)) {
											fail("Values don't match");
										}
									} 

								}
							}
						}
						
						if (mutlipleCount != 2) {
							fail("Incorrect creation of multiple dada");
						}
					}
				});
	}

}
