/**
 * 
 */
package es.caib.ripea.core.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import es.caib.plugins.arxiu.api.Expedient;
import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.ExpedientDto;
import es.caib.ripea.core.api.dto.InteressatAdministracioDto;
import es.caib.ripea.core.api.dto.InteressatDocumentTipusEnumDto;
import es.caib.ripea.core.api.dto.InteressatDto;
import es.caib.ripea.core.api.dto.InteressatIdiomaEnumDto;
import es.caib.ripea.core.api.dto.InteressatPersonaFisicaDto;
import es.caib.ripea.core.api.dto.InteressatPersonaJuridicaDto;
import es.caib.ripea.core.api.dto.InteressatTipusEnumDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.service.ExpedientInteressatService;
import es.caib.ripea.plugin.SistemaExternException;
import es.caib.ripea.plugin.unitat.UnitatOrganitzativa;
import es.caib.ripea.plugin.unitat.UnitatsOrganitzativesPlugin;

/**
 * Tests per al servei d'entitats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/es/caib/ripea/core/application-context-test.xml"})
public class ExpedientInteressatServiceTest extends BaseExpedientServiceTest {

	@Autowired
	private ExpedientInteressatService expedientInteressatService;

	InteressatPersonaFisicaDto interessatPersonaFisicaDto;
	InteressatPersonaFisicaDto representantPersonaFisicaDto;
	InteressatPersonaJuridicaDto interessatPersonaJuridicaDto;
	InteressatPersonaJuridicaDto representantPersonaJuridicaDto;
	InteressatAdministracioDto interessatAdministracioDto;

	UnitatsOrganitzativesPlugin mockUnitatsOrganitzatives;

	@Before
	public void setUp() {
		super.setUp();
		configureMockUnitatsOrganitzativesPlugin();
		// =============================== PERSONA FISICA =====================================
		interessatPersonaFisicaDto = new InteressatPersonaFisicaDto();
		interessatPersonaFisicaDto.setAdresa("Test adresa 1");
		interessatPersonaFisicaDto.setCodiPostal("07500");
		interessatPersonaFisicaDto.setDocumentNum("07450666T");
		interessatPersonaFisicaDto.setDocumentTipus(InteressatDocumentTipusEnumDto.NIF);
		interessatPersonaFisicaDto.setEmail("test@mail.com");
		interessatPersonaFisicaDto.setEntregaDeh(true);
		interessatPersonaFisicaDto.setEntregaDehObligat(true);
		interessatPersonaFisicaDto.setEsRepresentant(false);
		interessatPersonaFisicaDto.setIncapacitat(true);
		interessatPersonaFisicaDto.setLlinatge1("Test Llinatge1");
		interessatPersonaFisicaDto.setLlinatge2("Test Llinatge2");
		interessatPersonaFisicaDto.setMunicipi("163");
		interessatPersonaFisicaDto.setNom("Test nom");
		interessatPersonaFisicaDto.setNotificacioAutoritzat(true);
		interessatPersonaFisicaDto.setObservacions("Test observacions");
		interessatPersonaFisicaDto.setPais("724");
		interessatPersonaFisicaDto.setPreferenciaIdioma(InteressatIdiomaEnumDto.CA);
		interessatPersonaFisicaDto.setProvincia("01");
		interessatPersonaFisicaDto.setTelefon("666111222");
		interessatPersonaFisicaDto.setTipus(InteressatTipusEnumDto.PERSONA_FISICA);
		representantPersonaFisicaDto = new InteressatPersonaFisicaDto();
		representantPersonaFisicaDto.setAdresa("Test adresa 1 representant");
		representantPersonaFisicaDto.setCodiPostal("07500");
		representantPersonaFisicaDto.setDocumentNum("07450666T");
		representantPersonaFisicaDto.setDocumentTipus(InteressatDocumentTipusEnumDto.NIF);
		representantPersonaFisicaDto.setEmail("testRepresentant@mail.com");
		representantPersonaFisicaDto.setEsRepresentant(true);
		representantPersonaFisicaDto.setLlinatge1("Test Llinatge1 representant");
		representantPersonaFisicaDto.setLlinatge2("Test Llinatge2 representant");
		representantPersonaFisicaDto.setMunicipi("163");
		representantPersonaFisicaDto.setNom("Test nom representant");
		representantPersonaFisicaDto.setNotificacioAutoritzat(true);
		representantPersonaFisicaDto.setObservacions("Test observacions representant");
		representantPersonaFisicaDto.setPais("724");
		representantPersonaFisicaDto.setPreferenciaIdioma(InteressatIdiomaEnumDto.CA);
		representantPersonaFisicaDto.setProvincia("01");
		representantPersonaFisicaDto.setTelefon("666111222");
		representantPersonaFisicaDto.setTipus(InteressatTipusEnumDto.PERSONA_FISICA);
		// =============================== PERSONA JURIDICA =====================================
		interessatPersonaJuridicaDto = new InteressatPersonaJuridicaDto();
		interessatPersonaJuridicaDto.setAdresa("Test adresa 1");
		interessatPersonaJuridicaDto.setCodiPostal("07500");
		interessatPersonaJuridicaDto.setDocumentNum("07450666T");
		interessatPersonaJuridicaDto.setDocumentTipus(InteressatDocumentTipusEnumDto.NIF);
		interessatPersonaJuridicaDto.setEmail("test@mail.com");
		interessatPersonaJuridicaDto.setEntregaDeh(true);
		interessatPersonaJuridicaDto.setEntregaDehObligat(true);
		interessatPersonaJuridicaDto.setEsRepresentant(false);
		interessatPersonaJuridicaDto.setIncapacitat(true);
		interessatPersonaJuridicaDto.setRaoSocial("Rao social");
		interessatPersonaJuridicaDto.setMunicipi("163");
		interessatPersonaJuridicaDto.setNotificacioAutoritzat(true);
		interessatPersonaJuridicaDto.setObservacions("Test observacions");
		interessatPersonaJuridicaDto.setPais("724");
		interessatPersonaJuridicaDto.setPreferenciaIdioma(InteressatIdiomaEnumDto.CA);
		interessatPersonaJuridicaDto.setProvincia("01");
		interessatPersonaJuridicaDto.setTelefon("666111222");
		interessatPersonaJuridicaDto.setTipus(InteressatTipusEnumDto.PERSONA_JURIDICA);
		representantPersonaJuridicaDto = new InteressatPersonaJuridicaDto();
		representantPersonaJuridicaDto.setAdresa("Test adresa 1 representant");
		representantPersonaJuridicaDto.setCodiPostal("07500");
		representantPersonaJuridicaDto.setDocumentNum("07450666T");
		representantPersonaJuridicaDto.setDocumentTipus(InteressatDocumentTipusEnumDto.NIF);
		representantPersonaJuridicaDto.setEmail("testRepresentant@mail.com");
		representantPersonaJuridicaDto.setEsRepresentant(true);
		representantPersonaJuridicaDto.setRaoSocial("Rao social");
		representantPersonaJuridicaDto.setMunicipi("163");
		representantPersonaJuridicaDto.setNotificacioAutoritzat(true);
		representantPersonaJuridicaDto.setObservacions("Test observacions representant");
		representantPersonaJuridicaDto.setPais("724");
		representantPersonaJuridicaDto.setPreferenciaIdioma(InteressatIdiomaEnumDto.CA);
		representantPersonaJuridicaDto.setProvincia("01");
		representantPersonaJuridicaDto.setTelefon("666111222");
		representantPersonaJuridicaDto.setTipus(InteressatTipusEnumDto.PERSONA_JURIDICA);
		// =============================== ADMINISTRACIO =====================================
		interessatAdministracioDto = new InteressatAdministracioDto();
		interessatAdministracioDto.setAdresa("Test adresa 1");
		interessatAdministracioDto.setCodiPostal("07500");
		interessatAdministracioDto.setDocumentTipus(InteressatDocumentTipusEnumDto.NIF);
		interessatAdministracioDto.setEmail("test@mail.com");
		interessatAdministracioDto.setEntregaDeh(true);
		interessatAdministracioDto.setEntregaDehObligat(true);
		interessatAdministracioDto.setEsRepresentant(false);
		interessatAdministracioDto.setIncapacitat(true);
		interessatAdministracioDto.setOrganCodi("A04032369");
		interessatAdministracioDto.setDocumentNum("A04032369");
		interessatAdministracioDto.setMunicipi("163");
		interessatAdministracioDto.setNotificacioAutoritzat(true);
		interessatAdministracioDto.setObservacions("Test observacions");
		interessatAdministracioDto.setPais("724");
		interessatAdministracioDto.setPreferenciaIdioma(InteressatIdiomaEnumDto.CA);
		interessatAdministracioDto.setProvincia("01");
		interessatAdministracioDto.setTelefon("666111222");
		interessatAdministracioDto.setTipus(InteressatTipusEnumDto.PERSONA_JURIDICA);
	}

	@Test
	public void createPersonaFisica() {
		testAmbElementsIExpedient(
				new TestAmbElementsCreats() {
					@Override
					public void executar(List<Object> elementsCreats) {
						EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
						ExpedientDto expedientCreat = (ExpedientDto)elementsCreats.get(5);
						InteressatDto interessatCreat = expedientInteressatService.create(
								entitatCreada.getId(),
								expedientCreat.getId(),
								interessatPersonaFisicaDto);	
						assertNotNull(interessatCreat);
						assertNotNull(interessatCreat.getId());
						comprovarInteressatPersonaFisicaCoincideix(
								(InteressatPersonaFisicaDto) interessatPersonaFisicaDto,
								(InteressatPersonaFisicaDto) interessatCreat);
						Mockito.verify(arxiuPluginMock, Mockito.times(1)).expedientModificar((Mockito.any(Expedient.class)));
						ArgumentCaptor<Expedient> argument = ArgumentCaptor.forClass(Expedient.class);
						Mockito.verify(arxiuPluginMock).expedientModificar(argument.capture());
						assertEquals(interessatPersonaFisicaDto.getDocumentNum(), argument.getValue().getMetadades().getInteressats().get(0));
					}
				},
				"Creació d'un interessat de tipus persona física");
	}

	@Test
	public void updatePersonaFisica() {
		testAmbElementsIExpedient(
				new TestAmbElementsCreats() {
					@Override
					public void executar(List<Object> elementsCreats) {
						EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
						ExpedientDto expedientCreat = (ExpedientDto)elementsCreats.get(5);
						InteressatDto interessatCreat = expedientInteressatService.create(
								entitatCreada.getId(),
								expedientCreat.getId(),
								interessatPersonaFisicaDto);	
						interessatPersonaFisicaDto.setId(interessatCreat.getId());
						interessatPersonaFisicaDto.setLlinatge1("Llinatge1 test modificar");
						interessatPersonaFisicaDto.setDocumentNum("07933975X");
						InteressatDto interessatModificat = expedientInteressatService.update(
								entitatCreada.getId(),
								expedientCreat.getId(),
								interessatPersonaFisicaDto);
						assertNotNull(interessatModificat);
						assertNotNull(interessatModificat.getId());
						comprovarInteressatPersonaFisicaCoincideix(
								(InteressatPersonaFisicaDto) interessatPersonaFisicaDto,
								(InteressatPersonaFisicaDto) interessatModificat);
					}
				},
				"Modificació d'un interessat de tipus persona física");
	}

	@Test
	public void deletePersonaFisica() {
		testAmbElementsIExpedient(
				new TestAmbElementsCreats() {
					@Override
					public void executar(List<Object> elementsCreats) {
						EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
						ExpedientDto expedientCreat = (ExpedientDto)elementsCreats.get(5);
						InteressatDto interessatCreat = expedientInteressatService.create(
								entitatCreada.getId(),
								expedientCreat.getId(),
								interessatPersonaFisicaDto);	
						expedientInteressatService.delete(
								entitatCreada.getId(),
								expedientCreat.getId(),
								interessatCreat.getId());
						try {
							autenticarUsuari("user");
							expedientInteressatService.findById(
									interessatCreat.getId());
							fail("L'expe`dient esborrat no s'hauria d'haver trobat");
						} catch (NotFoundException expected) {
						}
					}
				},
				"Eliminació d'un interessat de tipus persona física");
	}

	@Test
	public void afegirRepresentantPersonaFisica() {
		testAmbElementsIExpedient(
				new TestAmbElementsCreats() {
					@Override
					public void executar(List<Object> elementsCreats) {
						EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
						ExpedientDto expedientCreat = (ExpedientDto)elementsCreats.get(5);
						InteressatDto interessatCreat = expedientInteressatService.create(
								entitatCreada.getId(),
								expedientCreat.getId(),
								interessatPersonaFisicaDto);	
						InteressatDto representantCreat = expedientInteressatService.create(
								entitatCreada.getId(),
								expedientCreat.getId(),
								interessatCreat.getId(),
								representantPersonaFisicaDto,
								true);
						assertNotNull(representantCreat);
						assertNotNull(representantCreat.getId());
						comprovarInteressatPersonaFisicaCoincideix(
								(InteressatPersonaFisicaDto) representantPersonaFisicaDto,
								(InteressatPersonaFisicaDto) representantCreat);
					}
				},
				"Afegir un representant a un interessat de tipus persona física");
	}

	@Test
	public void modificarRepresentantPersonaFisica() {
		testAmbElementsIExpedient(
				new TestAmbElementsCreats() {
					@Override
					public void executar(List<Object> elementsCreats) {
						EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
						ExpedientDto expedientCreat = (ExpedientDto)elementsCreats.get(5);
						InteressatDto interessatCreat = expedientInteressatService.create(
								entitatCreada.getId(),
								expedientCreat.getId(),
								interessatPersonaFisicaDto);	
						InteressatDto representantCreat = expedientInteressatService.create(
								entitatCreada.getId(),
								expedientCreat.getId(),
								interessatCreat.getId(),
								representantPersonaFisicaDto,
								true);
						assertNotNull(representantCreat);
						assertNotNull(representantCreat.getId());
						comprovarInteressatPersonaFisicaCoincideix(
								(InteressatPersonaFisicaDto) representantPersonaFisicaDto,
								(InteressatPersonaFisicaDto) representantCreat);
					}
				},
				"Modificar un representant a un interessat de tipus persona física");
	}

	@Test
	public void createPersonaJuridica() {
		testAmbElementsIExpedient(
				new TestAmbElementsCreats() {
					@Override
					public void executar(List<Object> elementsCreats) {
						EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
						ExpedientDto expedientCreat = (ExpedientDto)elementsCreats.get(5);

						InteressatDto interessatCreat = expedientInteressatService.create(
								entitatCreada.getId(),
								expedientCreat.getId(),
								interessatPersonaJuridicaDto);	

						assertNotNull(interessatCreat);
						assertNotNull(interessatCreat.getId());

						comprovarInteressatPersonaJuridicaCoincideix(
								(InteressatPersonaJuridicaDto) interessatPersonaJuridicaDto,
								(InteressatPersonaJuridicaDto) interessatCreat);
						
						
						Mockito.verify(arxiuPluginMock, Mockito.times(1)).expedientModificar((Mockito.any(Expedient.class)));
						ArgumentCaptor<Expedient> argument = ArgumentCaptor.forClass(Expedient.class);
						Mockito.verify(arxiuPluginMock).expedientModificar(argument.capture());
						assertEquals(interessatPersonaJuridicaDto.getDocumentNum(), argument.getValue().getMetadades().getInteressats().get(0));

						
						
					}
				},
				"Creació d'un interessat de tipus persona jurídica");
	}

	@Test
	public void updatePersonaJuridica() {
		testAmbElementsIExpedient(
				new TestAmbElementsCreats() {
					@Override
					public void executar(List<Object> elementsCreats) {
						EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
						ExpedientDto expedientCreat = (ExpedientDto)elementsCreats.get(5);
						InteressatDto interessatCreat = expedientInteressatService.create(
								entitatCreada.getId(),
								expedientCreat.getId(),
								interessatPersonaJuridicaDto);	
						interessatPersonaJuridicaDto.setId(interessatCreat.getId());
						interessatPersonaJuridicaDto.setDocumentNum("07933975X");
						InteressatDto interessatModificat = expedientInteressatService.update(
								entitatCreada.getId(),
								expedientCreat.getId(),
								interessatPersonaJuridicaDto);
						assertNotNull(interessatModificat);
						assertNotNull(interessatModificat.getId());
						comprovarInteressatPersonaJuridicaCoincideix(
								(InteressatPersonaJuridicaDto) interessatPersonaJuridicaDto,
								(InteressatPersonaJuridicaDto) interessatModificat);
					}
				},
				"Modificació d'un interessat de tipus persona jurídica");
	}

	@Test
	public void deletePersonaJuridica() {
		testAmbElementsIExpedient(
				new TestAmbElementsCreats() {
					@Override
					public void executar(List<Object> elementsCreats) {
						EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
						ExpedientDto expedientCreat = (ExpedientDto)elementsCreats.get(5);
						InteressatDto interessatCreat = expedientInteressatService.create(
								entitatCreada.getId(),
								expedientCreat.getId(),
								interessatPersonaJuridicaDto);	
						expedientInteressatService.delete(
								entitatCreada.getId(),
								expedientCreat.getId(),
								interessatCreat.getId());
						try {
							autenticarUsuari("user");
							expedientInteressatService.findById(
									interessatCreat.getId());
							fail("L'expe`dient esborrat no s'hauria d'haver trobat");
						} catch (NotFoundException expected) {
						}
					}
				},
				"Eliminació d'un interessat de tipus persona jurídica");
	}

	@Test
	public void createAdministracio() {
		testAmbElementsIExpedient(
				new TestAmbElementsCreats() {
					@Override
					public void executar(List<Object> elementsCreats) {
						EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
						ExpedientDto expedientCreat = (ExpedientDto)elementsCreats.get(5);
						InteressatDto interessatCreat = expedientInteressatService.create(
								entitatCreada.getId(),
								expedientCreat.getId(),
								interessatAdministracioDto);	
						assertNotNull(interessatCreat);
						assertNotNull(interessatCreat.getId());
						comprovarInteressatAdministracioCoincideix(
								(InteressatAdministracioDto) interessatAdministracioDto,
								(InteressatAdministracioDto) interessatCreat);
						try {
							Mockito.verify(mockUnitatsOrganitzatives, Mockito.times(1)).findAmbCodi(Mockito.anyString());
							ArgumentCaptor<String> argument1 = ArgumentCaptor.forClass(String.class);
							Mockito.verify(mockUnitatsOrganitzatives).findAmbCodi(argument1.capture());
							assertEquals(interessatAdministracioDto.getOrganCodi(), argument1.getValue());
						} catch (SistemaExternException e) {
							e.printStackTrace();
							fail();
						}
					}
				},
				"Creació d'un interessat de tipus administració");
	}

	@Test
	public void updateAdministracio() {
		testAmbElementsIExpedient(
				new TestAmbElementsCreats() {
					@Override
					public void executar(List<Object> elementsCreats) {
						EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
						ExpedientDto expedientCreat = (ExpedientDto)elementsCreats.get(5);
						InteressatDto interessatCreat = expedientInteressatService.create(
								entitatCreada.getId(),
								expedientCreat.getId(),
								interessatAdministracioDto);	
						interessatAdministracioDto.setId(interessatCreat.getId());
						interessatAdministracioDto.setDocumentNum("07933975X");
						InteressatDto interessatModificat = expedientInteressatService.update(
								entitatCreada.getId(),
								expedientCreat.getId(),
								interessatAdministracioDto);
						assertNotNull(interessatModificat);
						assertNotNull(interessatModificat.getId());
						comprovarInteressatAdministracioCoincideix(
								(InteressatAdministracioDto) interessatAdministracioDto,
								(InteressatAdministracioDto) interessatModificat);
						
					}
				},
				"Modificació d'un interessat de tipus administració");
	}

	@Test
	public void deleteAdministracio() {
		testAmbElementsIExpedient(
				new TestAmbElementsCreats() {
					@Override
					public void executar(List<Object> elementsCreats) {
						EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
						ExpedientDto expedientCreat = (ExpedientDto)elementsCreats.get(5);
						InteressatDto interessatCreat = expedientInteressatService.create(
								entitatCreada.getId(),
								expedientCreat.getId(),
								interessatAdministracioDto);	
						expedientInteressatService.delete(
								entitatCreada.getId(),
								expedientCreat.getId(),
								interessatCreat.getId());
						try {
							autenticarUsuari("user");
							expedientInteressatService.findById(
									interessatCreat.getId());
							fail("L'expe`dient esborrat no s'hauria d'haver trobat");
						} catch (NotFoundException expected) {
						}
					}
				},
				"Eliminació d'un interessat de tipus administració");
	}

	private void comprovarInteressatPersonaFisicaCoincideix(
			InteressatPersonaFisicaDto original,
			InteressatPersonaFisicaDto perComprovar) {
		assertEquals(original.getAdresa(), perComprovar.getAdresa());
		assertEquals(original.getCodiPostal(), perComprovar.getCodiPostal());
		assertEquals(original.getDocumentNum(), perComprovar.getDocumentNum());
		assertEquals(original.getDocumentTipus(), perComprovar.getDocumentTipus());
		assertEquals(original.getEmail(), perComprovar.getEmail());
		assertEquals(original.getEntregaDeh(), perComprovar.getEntregaDeh());
		assertEquals(original.getEntregaDehObligat(), perComprovar.getEntregaDehObligat());
		assertEquals(original.isEsRepresentant(), perComprovar.isEsRepresentant());
		assertEquals(original.getLlinatge1(), perComprovar.getLlinatge1());
		assertEquals(original.getLlinatge2(), perComprovar.getLlinatge2());
		assertEquals(original.getMunicipi(), perComprovar.getMunicipi());
		assertEquals(original.getNom(), perComprovar.getNom());
		assertEquals(original.getNotificacioAutoritzat(), perComprovar.getNotificacioAutoritzat());
		assertEquals(original.getObservacions(), perComprovar.getObservacions());
		assertEquals(original.getPais(), perComprovar.getPais());
		assertEquals(original.getPreferenciaIdioma(), perComprovar.getPreferenciaIdioma());
		assertEquals(original.getProvincia(), perComprovar.getProvincia());
		assertEquals(original.getTelefon(), perComprovar.getTelefon());
		assertEquals(original.getTipus(), perComprovar.getTipus());
	}

	private void comprovarInteressatPersonaJuridicaCoincideix(
			InteressatPersonaJuridicaDto original,
			InteressatPersonaJuridicaDto perComprovar) {
		assertEquals(original.getAdresa(), perComprovar.getAdresa());
		assertEquals(original.getCodiPostal(), perComprovar.getCodiPostal());
		assertEquals(original.getDocumentNum(), perComprovar.getDocumentNum());
		assertEquals(original.getDocumentTipus(), perComprovar.getDocumentTipus());
		assertEquals(original.getEmail(), perComprovar.getEmail());
		assertEquals(original.getEntregaDeh(), perComprovar.getEntregaDeh());
		assertEquals(original.getEntregaDehObligat(), perComprovar.getEntregaDehObligat());
		assertEquals(original.isEsRepresentant(), perComprovar.isEsRepresentant());
		assertEquals(original.getIncapacitat(), perComprovar.getIncapacitat());
		assertEquals(original.getRaoSocial(), perComprovar.getRaoSocial());
		assertEquals(original.getMunicipi(), perComprovar.getMunicipi());
		assertEquals(original.getNotificacioAutoritzat(), perComprovar.getNotificacioAutoritzat());
		assertEquals(original.getObservacions(), perComprovar.getObservacions());
		assertEquals(original.getPais(), perComprovar.getPais());
		assertEquals(original.getPreferenciaIdioma(), perComprovar.getPreferenciaIdioma());
		assertEquals(original.getProvincia(), perComprovar.getProvincia());
		assertEquals(original.getTelefon(), perComprovar.getTelefon());
		assertEquals(original.getTipus(), perComprovar.getTipus());
	}

	private void comprovarInteressatAdministracioCoincideix(
			InteressatAdministracioDto original,
			InteressatAdministracioDto perComprovar) {
		assertEquals(original.getAdresa(), perComprovar.getAdresa());
		assertEquals(original.getCodiPostal(), perComprovar.getCodiPostal());
		assertEquals(original.getDocumentNum(), perComprovar.getDocumentNum());
		assertEquals(original.getDocumentTipus(), perComprovar.getDocumentTipus());
		assertEquals(original.getEmail(), perComprovar.getEmail());
		assertEquals(original.getEntregaDeh(), perComprovar.getEntregaDeh());
		assertEquals(original.getEntregaDehObligat(), perComprovar.getEntregaDehObligat());
		assertEquals(original.isEsRepresentant(), perComprovar.isEsRepresentant());
		assertEquals(original.getIncapacitat(), perComprovar.getIncapacitat());
		assertEquals(original.getOrganCodi(), perComprovar.getOrganCodi());
		assertEquals(original.getMunicipi(), perComprovar.getMunicipi());
		assertEquals(original.getNotificacioAutoritzat(), perComprovar.getNotificacioAutoritzat());
		assertEquals(original.getObservacions(), perComprovar.getObservacions());
		assertEquals(original.getPais(), perComprovar.getPais());
		assertEquals(original.getPreferenciaIdioma(), perComprovar.getPreferenciaIdioma());
		assertEquals(original.getProvincia(), perComprovar.getProvincia());
		assertEquals(original.getTelefon(), perComprovar.getTelefon());
		assertEquals(original.getTipus(), perComprovar.getTipus());
	}

	private void configureMockUnitatsOrganitzativesPlugin() {
		mockUnitatsOrganitzatives = Mockito.mock(UnitatsOrganitzativesPlugin.class);
		UnitatOrganitzativa unitatOrganitzativa = new UnitatOrganitzativa();
		unitatOrganitzativa.setCodi("A04032369");
		unitatOrganitzativa.setDenominacio("Consejería de Presidencia, Cultura e Igualdad");
		try {
			Mockito.when(mockUnitatsOrganitzatives.findAmbCodi(Mockito.anyString())).thenReturn(unitatOrganitzativa);
		} catch (SistemaExternException e) {
			e.printStackTrace();
			fail();
		}
		pluginHelper.setUnitatsOrganitzativesPlugin(mockUnitatsOrganitzatives);
	}

}
