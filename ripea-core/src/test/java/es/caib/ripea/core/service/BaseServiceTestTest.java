/**
 * 
 */
package es.caib.ripea.core.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.List;

import es.caib.ripea.plugin.PropertiesHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.service.EntitatService;

/**
 * Tests per als mètodes del BaseServiceTest.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/es/caib/ripea/core/application-context-test.xml"})
@Transactional
public class BaseServiceTestTest extends BaseServiceTest {

	@Autowired
	private EntitatService entitatService;

	private EntitatDto entitat;
	Long entitatId1 = null;

	@Before
	public void setUp() {
		PropertiesHelper.getProperties("classpath:es/caib/ripea/core/test.properties");
		entitat = new EntitatDto();
		entitat.setCodi("LIMIT");
		entitat.setNom("Limit Tecnologies");
		entitat.setCif("12345678Z");
		entitat.setUnitatArrel("LIM000001");
		
	}

	@Test
    public void nomesEntitat() {
		testCreantElements(
				new TestAmbElementsCreats() {
					@Override
					public void executar(List<Object> elementsCreats) {
						EntitatDto entitat = (EntitatDto)elementsCreats.get(0);
						entitatId1 = entitat.getId();
					}
				},
				"Verificant que l'entitat s'esborra després del test",
				entitat);
		autenticarUsuari("super");
		assertNotNull(entitatId1);
		try {
			entitatService.findById(entitatId1);
			fail("La entitat esborrada no s'hauria d'haver trobat");
		} catch (NotFoundException expected) {
		}
	}

}
