/**
 * 
 */
package es.caib.ripea.core.service;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.transaction.annotation.Transactional;

import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.MetaDadaDto;
import es.caib.ripea.core.api.dto.MetaDocumentDto;
import es.caib.ripea.core.api.dto.MetaExpedientDto;
import es.caib.ripea.core.api.dto.PermisDto;
import es.caib.ripea.core.api.service.EntitatService;
import es.caib.ripea.core.api.service.MetaDadaService;
import es.caib.ripea.core.api.service.MetaDocumentService;
import es.caib.ripea.core.api.service.MetaExpedientService;
import es.caib.ripea.core.entity.UsuariEntity;
import es.caib.ripea.core.helper.PropertiesHelper;
import es.caib.ripea.core.repository.UsuariRepository;

/**
 * Tests per al servei d'entitats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class BaseServiceTest {

	protected static final String CODI_UNITAT_ARREL = "00000000T";
	protected static final String CODI_UNITAT_FILLA = "12345678Z";

	protected static final String PLANTILLA_NOM = "image.png";
	protected static final String PLANTILLA_CONTTYPE = "image/png";
	protected static final byte[] PLANTILLA_CONTINGUT = new byte[10];
	
	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private EntitatService entitatService;
	@Autowired
	private MetaDadaService metaDadaService;
	@Autowired
	private MetaDocumentService metaDocumentService;
	@Autowired
	private MetaExpedientService metaExpedientService;

	@Autowired
	private  UsuariRepository usuariRepository;

	@BeforeClass
	public static void beforeClass() {
		PropertiesHelper.getProperties("classpath:es/caib/ripea/core/test.properties");
	}

	@AfterClass
	public static void afterClass() {
	}

	@Transactional
	protected void autenticarUsuari(String usuariCodi) {
		logger.debug("Autenticant usuari " + usuariCodi + "...");
		UserDetails userDetails = userDetailsService.loadUserByUsername(usuariCodi);
		Authentication authToken = new UsernamePasswordAuthenticationToken(
				userDetails.getUsername(),
				userDetails.getPassword(),
				userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
        UsuariEntity usuariEntity = usuariRepository.findOne(usuariCodi);
		if (usuariEntity == null) {
			usuariRepository.save(
					UsuariEntity.getBuilder(
							usuariCodi,
							usuariCodi,
							"00000000T",
							usuariCodi + "@mail.com",
							"CA").build());
		}
		logger.debug("... usuari " + usuariCodi + " autenticat correctament");
	}

	protected void testCreantElements(
			TestAmbElementsCreats test,
			String descripcioTest,
			Object... elements) {
		String descripcio = (descripcioTest != null && !descripcioTest.isEmpty()) ? descripcioTest : "";
		logger.info("-------------------------------------------------------------------");
		logger.info("-- Executant test \"" + descripcio + "\" amb els elements creats...");
		logger.info("-------------------------------------------------------------------");
		List<Object> elementsCreats = new ArrayList<Object>();
		Long entitatId = null;
		try {
			for (Object element: elements) {
				Long id = null;
				logger.debug("Creant objecte de tipus " + element.getClass().getSimpleName() + "...");
				if (element instanceof EntitatDto) {
					autenticarUsuari("super");
					EntitatDto entitatCreada = entitatService.create((EntitatDto)element);
					entitatId = entitatCreada.getId();
					elementsCreats.add(entitatCreada);
					if (((EntitatDto)element).getPermisos() != null) {
						for (PermisDto permis: ((EntitatDto)element).getPermisos()) {
							entitatService.updatePermisSuper(
									entitatCreada.getId(),
									permis);
						}
					}
					id = entitatCreada.getId();
				} else {
					autenticarUsuari("admin");
					// TODO
					if (entitatId != null) {
						if (element instanceof MetaExpedientDto) {
							MetaExpedientDto metaExpedientCreat = metaExpedientService.create(
									entitatId,
									(MetaExpedientDto) element);
							elementsCreats.add(metaExpedientCreat);
							if (((MetaExpedientDto)element).getPermisos() != null) {
								for (PermisDto permis: ((MetaExpedientDto)element).getPermisos()) {
									metaExpedientService.permisUpdate(entitatId,
											metaExpedientCreat.getId(),
											permis);
								}
							}
							id = metaExpedientCreat.getId();
						} else if (element instanceof MetaDocumentDto) {
							MetaDocumentDto metaDocumentCreat = metaDocumentService.create(
									entitatId,
									((MetaExpedientDto) elementsCreats.get(1)).getId(),
									(MetaDocumentDto)element,
									PLANTILLA_NOM,
									PLANTILLA_CONTTYPE,
									PLANTILLA_CONTINGUT);
							elementsCreats.add(metaDocumentCreat);
							if (((MetaDocumentDto)element).getPermisos() != null) {
								for (PermisDto permis: ((MetaDocumentDto)element).getPermisos()) {
									metaExpedientService.permisUpdate(entitatId,
											metaDocumentCreat.getId(),
											permis);
								}
							}
							id = metaDocumentCreat.getId();
						} else if (element instanceof MetaDadaDto) {
							MetaDadaDto metaDadaCreada = metaDadaService.create(
									entitatId,
									((MetaExpedientDto)elementsCreats.get(1)).getId(),
									(MetaDadaDto) element);
							elementsCreats.add(metaDadaCreada);
							id = metaDadaCreada.getId();
						} else {
							fail("Tipus d'objecte desconegut: " + element.getClass().getSimpleName());
						}
					} else {
						fail("No s'ha trobat cap entitat per associar l'objecte de tipus " + element.getClass().getSimpleName());
					}
				}
				logger.debug("...objecte de tipus " + element.getClass().getSimpleName() + "creat (id=" + id + ").");
			}
			logger.debug("Executant accions del test...");
			test.executar(elementsCreats);
			logger.debug("...accions del test executades.");
		} catch (Exception ex) {
			logger.error("L'execució del test ha produït una excepció", ex);
			fail("L'execució del test ha produït una excepció");
		} finally {
			Long metaExpedientId = null;
			if (elementsCreats.size() > 1) {
				metaExpedientId = ((MetaExpedientDto)elementsCreats.get(1)).getId();
			}
			// TODO
			Collections.reverse(elementsCreats);
			for (Object element: elementsCreats) {
				autenticarUsuari("admin");
				logger.debug("Esborrant objecte de tipus " + element.getClass().getSimpleName() + "...");
				if (element instanceof EntitatDto) {
					autenticarUsuari("super");
					entitatService.delete(
							((EntitatDto)element).getId());
					entitatId = null;
				} else if (element instanceof MetaDadaDto) {
					metaDadaService.delete(
							entitatId,
							metaExpedientId,
							((MetaDadaDto)element).getId());
				} else if (element instanceof MetaDocumentDto) {
					metaDocumentService.delete(
							entitatId,
							metaExpedientId,
							((MetaDocumentDto)element).getId());
				} else if (element instanceof MetaExpedientDto) {
					metaExpedientService.delete(
							entitatId,
							((MetaExpedientDto)element).getId(), false);
				}
				logger.debug("...objecte de tipus " + element.getClass().getSimpleName() + " esborrat correctament.");
			}
			logger.info("-------------------------------------------------------------------");
			logger.info("-- ...test \"" + descripcio + "\" executat.");
			logger.info("-------------------------------------------------------------------");
		}
	}

	protected void testCreantElements(
			final TestAmbElementsCreats test,
			Object... elements) {
		testCreantElements(test, null, elements);
	}

	abstract class TestAmbElementsCreats {
		public abstract void executar(
				List<Object> elementsCreats) throws Exception;
	}

	private static final Logger logger = LoggerFactory.getLogger(BaseServiceTest.class);

}
