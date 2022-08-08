/**
 * 
 */
package es.caib.ripea.core.service;

import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.MetaExpedientDto;
import es.caib.ripea.core.api.dto.OrganGestorDto;
import es.caib.ripea.core.api.dto.PermisDto;
import es.caib.ripea.core.api.dto.PrincipalTipusEnumDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.service.MetaExpedientService;
import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests per al servei d'entitats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/es/caib/ripea/core/application-context-test.xml"})
@Transactional
public class MetaExpedientServiceTest extends BaseServiceTest {

	@Autowired
	private MetaExpedientService metaExpedientService;
	@Autowired
	private EntityManager entityManager;

	private EntitatDto entitat;
	protected OrganGestorDto organGestorDto;
	private MetaExpedientDto metaExpedientCreate;
	private MetaExpedientDto metaExpedientUpdate;
	private PermisDto permisUserRead;

	@Before
	public void setUp() {
		setDefaultConfigs();
		entitat = new EntitatDto();
		entitat.setCodi("LIMIT");
		entitat.setNom("Limit Tecnologies");
		entitat.setCif("00000000T");
		entitat.setUnitatArrel(CODI_UNITAT_ARREL);
		List<PermisDto> permisos = new ArrayList<PermisDto>();
		PermisDto permisAdminAdmin = new PermisDto();
		permisAdminAdmin.setAdministration(true);
		permisAdminAdmin.setPrincipalTipus(PrincipalTipusEnumDto.USUARI);
		permisAdminAdmin.setPrincipalNom("admin");
		permisos.add(permisAdminAdmin);
		entitat.setPermisos(permisos);
		metaExpedientCreate = new MetaExpedientDto();
		metaExpedientCreate.setCodi("TEST1");
		metaExpedientCreate.setNom("Metaexpedient de test");
		metaExpedientCreate.setDescripcio("Descripció de test");
		metaExpedientCreate.setSerieDocumental("1234");
		metaExpedientCreate.setClassificacioSia("1234");
		metaExpedientCreate.setNotificacioActiva(false);
//		metaExpedientCreate.setNotificacioSeuProcedimentCodi("1234");
//		metaExpedientCreate.setNotificacioSeuRegistreLlibre("1234");
//		metaExpedientCreate.setNotificacioSeuRegistreOficina("1234");
//		metaExpedientCreate.setNotificacioSeuRegistreOrgan("1234");
//		metaExpedientCreate.setNotificacioSeuExpedientUnitatOrganitzativa("1234");
//		metaExpedientCreate.setNotificacioAvisTitol("1234");
//		metaExpedientCreate.setNotificacioAvisText("1234");
//		metaExpedientCreate.setNotificacioAvisTextMobil("1234");
//		metaExpedientCreate.setNotificacioOficiTitol("1234");
//		metaExpedientCreate.setNotificacioOficiText("1234");
		metaExpedientCreate.setPareId(null);
		metaExpedientUpdate = new MetaExpedientDto();
		metaExpedientUpdate.setCodi("TEST2");
		metaExpedientUpdate.setNom("Metaexpedient de test2");
		metaExpedientUpdate.setDescripcio("Descripció de test2");
		metaExpedientUpdate.setSerieDocumental("12341");
		metaExpedientUpdate.setClassificacioSia("12342");
		metaExpedientUpdate.setNotificacioActiva(true);
//		metaExpedientUpdate.setNotificacioSeuProcedimentCodi("1234");
//		metaExpedientUpdate.setNotificacioSeuRegistreLlibre("1234");
//		metaExpedientUpdate.setNotificacioSeuRegistreOficina("1234");
//		metaExpedientUpdate.setNotificacioSeuRegistreOrgan("1234");
//		metaExpedientUpdate.setNotificacioSeuExpedientUnitatOrganitzativa("1234");
//		metaExpedientUpdate.setNotificacioAvisTitol("12346");
//		metaExpedientUpdate.setNotificacioAvisText("12347");
//		metaExpedientUpdate.setNotificacioAvisTextMobil("12348");
//		metaExpedientUpdate.setNotificacioOficiTitol("12349");
//		metaExpedientUpdate.setNotificacioOficiText("12340");
		metaExpedientUpdate.setPareId(null);
		permisUserRead = new PermisDto();
		permisUserRead.setRead(true);
		permisUserRead.setPrincipalTipus(PrincipalTipusEnumDto.USUARI);
		permisUserRead.setPrincipalNom("user");

		organGestorDto = new OrganGestorDto();
		organGestorDto.setCodi("A000000000");
		organGestorDto.setNom("Òrgan 0");
	}

	@Test
    public void create() {
		testCreantElements(
				new TestAmbElementsCreats() {
					@Override
					public void executar(List<Object> elementsCreats) {
						MetaExpedientDto metaExpedientCreat = (MetaExpedientDto)elementsCreats.get(2);
						assertNotNull(metaExpedientCreat);
						assertNotNull(metaExpedientCreat.getId());
						comprovarMetaExpedientCoincideix(
								metaExpedientCreate,
								metaExpedientCreat);
						assertEquals(true, metaExpedientCreat.isActiu());
					}
				},
				"Creació d'un meta-expedient",
				entitat,
				organGestorDto,
				metaExpedientCreate);
	}

	@Test
	public void findById() {
		testCreantElements(
				new TestAmbElementsCreats() {
					@Override
					public void executar(List<Object> elementsCreats) {
						autenticarUsuari("admin");
						EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
						MetaExpedientDto metaExpedientCreat = (MetaExpedientDto)elementsCreats.get(2);
						MetaExpedientDto trobat = metaExpedientService.findById(
								entitatCreada.getId(),
								metaExpedientCreat.getId());
						assertNotNull(trobat);
						assertNotNull(trobat.getId());
						comprovarMetaExpedientCoincideix(
								metaExpedientCreat,
								trobat);
					}
				},
				"Consulta d'un meta-expedient",
				entitat,
				organGestorDto,
				metaExpedientCreate);
    }

	@Test
    public void update() {
		testCreantElements(
				new TestAmbElementsCreats() {
					@Override
					public void executar(List<Object> elementsCreats) {
						autenticarUsuari("admin");
						EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
						MetaExpedientDto metaExpedientCreat = (MetaExpedientDto)elementsCreats.get(2);
						metaExpedientUpdate.setId(metaExpedientCreat.getId());
						MetaExpedientDto modificat = metaExpedientService.update(
								entitatCreada.getId(),
								metaExpedientUpdate, null, false, null);
						assertNotNull(modificat);
						assertNotNull(modificat.getId());
						assertEquals(
								metaExpedientCreat.getId(),
								modificat.getId());
						comprovarMetaExpedientCoincideix(
								metaExpedientUpdate,
								modificat);
						assertEquals(true, modificat.isActiu());
					}
				},
				"Modificació d'un meta-expedient",
				entitat,
				organGestorDto,
				metaExpedientCreate);
	}

	@Test
	public void delete() {
		testCreantElements(
				new TestAmbElementsCreats() {
					@Override
					public void executar(List<Object> elementsCreats) {
						autenticarUsuari("admin");
						EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
						MetaExpedientDto metaExpedientCreat = (MetaExpedientDto)elementsCreats.get(2);
						MetaExpedientDto esborrat = metaExpedientService.delete(
								entitatCreada.getId(),
								metaExpedientCreat.getId(), null);
						comprovarMetaExpedientCoincideix(
								metaExpedientCreate,
								esborrat);
						try {
							metaExpedientService.findById(
									entitatCreada.getId(),
									metaExpedientCreat.getId());
							fail("El meta-expedient esborrat no s'hauria d'haver trobat");
						} catch (NotFoundException expected) {
							entityManager.unwrap(Session.class).clear();
						}
						elementsCreats.remove(metaExpedientCreat);
					}
				},
				"Eliminació d'un meta-expedient",
				entitat,
				organGestorDto,
				metaExpedientCreate);
	}

	@Test
	public void updateActiu() {
		testCreantElements(
				new TestAmbElementsCreats() {
					@Override
					public void executar(List<Object> elementsCreats) {
						autenticarUsuari("admin");
						EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
						MetaExpedientDto metaExpedientCreat = (MetaExpedientDto)elementsCreats.get(2);
						MetaExpedientDto desactivat = metaExpedientService.updateActiu(
								entitatCreada.getId(),
								metaExpedientCreat.getId(),
								false, "tothom", null);
						assertEquals(
								false,
								desactivat.isActiu());
						MetaExpedientDto activat = metaExpedientService.updateActiu(
								entitatCreada.getId(),
								metaExpedientCreat.getId(),
								true, "tothom", null);
						assertEquals(
								true,
								activat.isActiu());
					}
				},
				"Activació/desactivació d'un meta-expedient",
				entitat,
				organGestorDto,
				metaExpedientCreate);
	}

	private void comprovarMetaExpedientCoincideix(
			MetaExpedientDto original,
			MetaExpedientDto perComprovar) {
		assertEquals(
				original.getCodi(),
				perComprovar.getCodi());
		assertEquals(
				original.getNom(),
				perComprovar.getNom());
		assertEquals(
				original.getDescripcio(),
				perComprovar.getDescripcio());
		assertEquals(
				original.getSerieDocumental(),
				perComprovar.getSerieDocumental());
		assertEquals(
				original.getClassificacioSia(),
				perComprovar.getClassificacioSia());
		assertEquals(
				original.isNotificacioActiva(),
				perComprovar.isNotificacioActiva());
//		assertEquals(
//				original.getNotificacioSeuProcedimentCodi(),
//				perComprovar.getNotificacioSeuProcedimentCodi());
//		assertEquals(
//				original.getNotificacioSeuRegistreLlibre(),
//				perComprovar.getNotificacioSeuRegistreLlibre());
//		assertEquals(
//				original.getNotificacioSeuRegistreOficina(),
//				perComprovar.getNotificacioSeuRegistreOficina());
//		assertEquals(
//				original.getNotificacioSeuRegistreOrgan(),
//				perComprovar.getNotificacioSeuRegistreOrgan());
//		assertEquals(
//				original.getNotificacioSeuExpedientUnitatOrganitzativa(),
//				perComprovar.getNotificacioSeuExpedientUnitatOrganitzativa());
//		assertEquals(
//				original.getNotificacioAvisTitol(),
//				perComprovar.getNotificacioAvisTitol());
//		assertEquals(
//				original.getNotificacioAvisText(),
//				perComprovar.getNotificacioAvisText());
//		assertEquals(
//				original.getNotificacioAvisTextMobil(),
//				perComprovar.getNotificacioAvisTextMobil());
//		assertEquals(
//				original.getNotificacioOficiTitol(),
//				perComprovar.getNotificacioOficiTitol());
//		assertEquals(
//				original.getNotificacioOficiText(),
//				perComprovar.getNotificacioOficiText());
		assertEquals(
				original.getPareId(),
				perComprovar.getPareId());
	}

}
