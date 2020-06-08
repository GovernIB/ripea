/**
 * 
 */
package es.caib.ripea.core.service;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import es.caib.plugins.arxiu.api.Document;
import es.caib.plugins.arxiu.api.DocumentEstat;
import es.caib.ripea.core.api.dto.AlertaDto;
import es.caib.ripea.core.api.dto.DocumentDto;
import es.caib.ripea.core.api.dto.DocumentEstatEnumDto;
import es.caib.ripea.core.api.dto.DocumentNtiEstadoElaboracionEnumDto;
import es.caib.ripea.core.api.dto.DocumentPortafirmesDto;
import es.caib.ripea.core.api.dto.DocumentTipusEnumDto;
import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.ExpedientDto;
import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.dto.MetaDocumentDto;
import es.caib.ripea.core.api.dto.MetaDocumentFirmaSequenciaTipusEnumDto;
import es.caib.ripea.core.api.dto.NtiOrigenEnumDto;
import es.caib.ripea.core.api.dto.PortafirmesCallbackEstatEnumDto;
import es.caib.ripea.core.api.dto.PortafirmesPrioritatEnumDto;
import es.caib.ripea.core.api.service.DocumentService;
import es.caib.ripea.plugin.SistemaExternException;
import es.caib.ripea.plugin.portafirmes.PortafirmesDocument;
import es.caib.ripea.plugin.portafirmes.PortafirmesPrioritatEnum;

/**
 * Tests per al servei de gestió de documents dels expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/es/caib/ripea/core/application-context-test.xml" })
public class DocumentServiceTest extends BaseExpedientServiceTest {

	@Autowired
	private DocumentService documentService;

	@Test
	public void create() {
		testAmbElementsIExpedient(
				new TestAmbElementsCreats() {
					@Override
					public void executar(
							List<Object> elementsCreats) throws IOException {
						EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
						MetaDocumentDto metaDocumentCreat = (MetaDocumentDto)elementsCreats.get(2);
						ExpedientDto expedientCreat = (ExpedientDto)elementsCreats.get(4);
						DocumentDto dto = new DocumentDto();
						dto.setNom("Test");
						dto.setData(new Date());
						dto.setDocumentTipus(DocumentTipusEnumDto.DIGITAL);
						dto.setNtiEstadoElaboracion(DocumentNtiEstadoElaboracionEnumDto.EE01);
						dto.setNtiOrigen(NtiOrigenEnumDto.O0);
						emplenarDocumentArxiu(dto);
						dto.setFirmaSeparada(false);
						MetaDocumentDto metaDocument = new MetaDocumentDto();
						metaDocument.setId(metaDocumentCreat.getId());
						dto.setMetaNode(metaDocument);
						DocumentDto documentCreat = documentService.create(
								entitatCreada.getId(),
								expedientCreat.getId(),
								dto,
								true);
						assertNotNull(documentCreat);
						assertNotNull(documentCreat.getId());
						assertNotNull(documentCreat.getEstat());
						assertNotNull(documentCreat.getArxiuUuid());
						assertNotNull(documentCreat.getNtiIdentificador());
						assertNotNull(documentCreat.getNtiVersion());
						assertNotNull(documentCreat.getDataCaptura());
						assertNotNull(documentCreat.getNtiOrgano());
						assertNotNull(documentCreat.getNtiTipoDocumental());
						comprovarDocument(
								dto,
								documentCreat);
					}
				},
				"Creació d'un document a dins un expedient");
	}

	@Test
	public void update() {
		testAmbElementsIExpedient(
				new TestAmbElementsCreats() {
					@Override
					public void executar(
							List<Object> elementsCreats) throws IOException {
						EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
						MetaDocumentDto metaDocumentCreat = (MetaDocumentDto)elementsCreats.get(2);
						ExpedientDto expedientCreat = (ExpedientDto)elementsCreats.get(4);
						DocumentDto dto = new DocumentDto();
						dto.setNom("Test");
						dto.setData(new Date());
						dto.setDocumentTipus(DocumentTipusEnumDto.DIGITAL);
						dto.setNtiEstadoElaboracion(DocumentNtiEstadoElaboracionEnumDto.EE01);
						dto.setNtiOrigen(NtiOrigenEnumDto.O0);
						emplenarDocumentArxiu(dto);
						dto.setFirmaSeparada(false);
						MetaDocumentDto metaDocument = new MetaDocumentDto();
						metaDocument.setId(metaDocumentCreat.getId());
						dto.setMetaNode(metaDocument);
						DocumentDto documentCreat = documentService.create(
								entitatCreada.getId(),
								expedientCreat.getId(),
								dto,
								true);
						assertNotNull(documentCreat);
						assertNotNull(documentCreat.getId());
						dto.setId(documentCreat.getId());
						dto.setNom("Test 2");
						dto.setData(new Date());
						dto.setNtiEstadoElaboracion(DocumentNtiEstadoElaboracionEnumDto.EE99);
						DocumentDto documentModificat = documentService.update(
								entitatCreada.getId(),
								dto,
								true);
						comprovarDocument(
								dto,
								documentModificat);
					}
				},
				"Modificació d'un document a dins un expedient");
	}

	@Test
	public void delete() {
		testAmbElementsIExpedient(
				new TestAmbElementsCreats() {
					@Override
					public void executar(
							List<Object> elementsCreats) throws IOException {
						EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
						MetaDocumentDto metaDocumentCreat = (MetaDocumentDto)elementsCreats.get(2);
						ExpedientDto expedientCreat = (ExpedientDto)elementsCreats.get(4);
						DocumentDto dto = new DocumentDto();
						dto.setNom("Test");
						dto.setData(new Date());
						dto.setDocumentTipus(DocumentTipusEnumDto.DIGITAL);
						dto.setNtiEstadoElaboracion(DocumentNtiEstadoElaboracionEnumDto.EE01);
						dto.setNtiOrigen(NtiOrigenEnumDto.O0);
						emplenarDocumentArxiu(dto);
						dto.setFirmaSeparada(false);
						MetaDocumentDto metaDocument = new MetaDocumentDto();
						metaDocument.setId(metaDocumentCreat.getId());
						dto.setMetaNode(metaDocument);
						DocumentDto documentCreat = documentService.create(
								entitatCreada.getId(),
								expedientCreat.getId(),
								dto,
								true);
						assertNotNull(documentCreat);
						assertNotNull(documentCreat.getId());
						DocumentDto documentObtingut1 = documentService.findById(
								entitatCreada.getId(),
								documentCreat.getId());
						assertNotNull(documentObtingut1);
						assertNotNull(documentObtingut1.getId());
						assertNull(documentObtingut1.getEsborratData());
						contingutService.deleteReversible(
								entitatCreada.getId(),
								documentCreat.getId());
						DocumentDto documentObtingut2 = documentService.findById(
								entitatCreada.getId(),
								documentCreat.getId());
						assertNotNull(documentObtingut2);
						assertNotNull(documentObtingut2.getEsborratData());
						Mockito.verify(arxiuPluginMock, Mockito.times(1)).documentEsborrar(Mockito.anyString());
					}
				},
				"Eliminació d'un document de dins un expedient");
	}

	@Test
	public void download() {
		testAmbElementsIExpedient(
				new TestAmbElementsCreats() {
					@Override
					public void executar(
							List<Object> elementsCreats) throws IOException {
						EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
						MetaDocumentDto metaDocumentCreat = (MetaDocumentDto)elementsCreats.get(2);
						ExpedientDto expedientCreat = (ExpedientDto)elementsCreats.get(4);
						DocumentDto dto = new DocumentDto();
						dto.setNom("Test");
						dto.setData(new Date());
						dto.setDocumentTipus(DocumentTipusEnumDto.DIGITAL);
						dto.setNtiEstadoElaboracion(DocumentNtiEstadoElaboracionEnumDto.EE01);
						dto.setNtiOrigen(NtiOrigenEnumDto.O0);
						emplenarDocumentArxiu(dto);
						dto.setFirmaSeparada(false);
						MetaDocumentDto metaDocument = new MetaDocumentDto();
						metaDocument.setId(metaDocumentCreat.getId());
						dto.setMetaNode(metaDocument);
						DocumentDto documentCreat = documentService.create(
								entitatCreada.getId(),
								expedientCreat.getId(),
								dto,
								true);
						assertNotNull(documentCreat);
						assertNotNull(documentCreat.getId());
						FitxerDto fitxer = documentService.descarregar(
								entitatCreada.getId(),
								documentCreat.getId(),
								null);
						assertNotNull(fitxer);
						assertEquals(dto.getFitxerNom(), fitxer.getNom());
						assertEquals(dto.getFitxerContentType(), fitxer.getContentType());
						assertEquals(dto.getFitxerExtension(), fitxer.getExtensio());
						assertArrayEquals(dto.getFitxerContingut(), fitxer.getContingut());
					}
				},
				"Descàrrega d'un document de dins un expedient");
	}

	@Test
	public void firmaClient() {
		testAmbElementsIExpedient(
				new TestAmbElementsCreats() {
					@Override
					public void executar(
							List<Object> elementsCreats) throws IOException {
						EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
						MetaDocumentDto metaDocumentCreat = (MetaDocumentDto)elementsCreats.get(2);
						ExpedientDto expedientCreat = (ExpedientDto)elementsCreats.get(4);
						DocumentDto dto = new DocumentDto();
						dto.setNom("Test");
						dto.setData(new Date());
						dto.setDocumentTipus(DocumentTipusEnumDto.DIGITAL);
						dto.setNtiEstadoElaboracion(DocumentNtiEstadoElaboracionEnumDto.EE01);
						dto.setNtiOrigen(NtiOrigenEnumDto.O0);
						emplenarDocumentArxiu(dto);
						dto.setFirmaSeparada(false);
						MetaDocumentDto metaDocument = new MetaDocumentDto();
						metaDocument.setId(metaDocumentCreat.getId());
						dto.setMetaNode(metaDocument);
						DocumentDto documentCreat = documentService.create(
								entitatCreada.getId(),
								expedientCreat.getId(),
								dto,
								true);
						assertNotNull(documentCreat);
						assertNotNull(documentCreat.getId());
						String identificador = documentService.generarIdentificadorFirmaClient(
								entitatCreada.getId(),
								documentCreat.getId());
						assertNotNull(identificador);
						documentService.processarFirmaClient(
								identificador,
								"firma.pdf",
								dto.getFitxerContingut());
						DocumentDto documentFirmat = documentService.findById(
								entitatCreada.getId(),
								documentCreat.getId());
						assertNotNull(documentFirmat);
						assertNotNull(documentFirmat.getId());
						assertTrue(documentFirmat.isCustodiat());
						Mockito.verify(arxiuPluginMock, Mockito.times(1)).documentModificar(Mockito.any(Document.class));
						ArgumentCaptor<Document> argument = ArgumentCaptor.forClass(Document.class);
						Mockito.verify(arxiuPluginMock).documentModificar(argument.capture());
						assertEquals(DocumentEstat.DEFINITIU, argument.getValue().getEstat());
					}
				},
				"Firma client d'un document a dins un expedient");
	}

	@Test
	public void firmaPortafirmes() {
		testAmbElementsIExpedient(
				new TestAmbElementsCreats() {
					@SuppressWarnings("unchecked")
					@Override
					public void executar(
							List<Object> elementsCreats) throws IOException {
						EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
						MetaDocumentDto metaDocumentCreat = (MetaDocumentDto)elementsCreats.get(2);
						ExpedientDto expedientCreat = (ExpedientDto)elementsCreats.get(4);
						DocumentDto dto = new DocumentDto();
						dto.setNom("Test");
						dto.setData(new Date());
						dto.setDocumentTipus(DocumentTipusEnumDto.DIGITAL);
						dto.setNtiEstadoElaboracion(DocumentNtiEstadoElaboracionEnumDto.EE01);
						dto.setNtiOrigen(NtiOrigenEnumDto.O0);
						emplenarDocumentArxiu(dto);
						dto.setFirmaSeparada(false);
						MetaDocumentDto metaDocument = new MetaDocumentDto();
						metaDocument.setId(metaDocumentCreat.getId());
						dto.setMetaNode(metaDocument);
						DocumentDto documentCreat = documentService.create(
								entitatCreada.getId(),
								expedientCreat.getId(),
								dto,
								true);
						assertNotNull(documentCreat);
						assertNotNull(documentCreat.getId());
						Calendar dataCaducitat = Calendar.getInstance();
						dataCaducitat.add(Calendar.DAY_OF_MONTH, 7);
						documentService.portafirmesEnviar(
								entitatCreada.getId(),
								documentCreat.getId(),
								"Motiu de proves",
								PortafirmesPrioritatEnumDto.NORMAL,
								dataCaducitat.getTime(),
								new String[] {"12345678Z"},
								MetaDocumentFirmaSequenciaTipusEnumDto.SERIE,
								null,
								null);
						DocumentDto documentEnviat = documentService.findById(
								entitatCreada.getId(),
								documentCreat.getId());
						assertNotNull(documentEnviat);
						assertNotNull(documentEnviat.getId());
						assertEquals(DocumentEstatEnumDto.FIRMA_PENDENT, documentEnviat.getEstat());
						try {
							Mockito.verify(portafirmesPluginMock, Mockito.times(1)).upload(
									Mockito.any(PortafirmesDocument.class),
									Mockito.anyString(),
									Mockito.anyString(),
									Mockito.anyString(),
									Mockito.any(PortafirmesPrioritatEnum.class),
									Mockito.any(Date.class),
									Mockito.nullable(List.class),
									Mockito.nullable(String.class),
									Mockito.nullable(List.class),
									Mockito.anyBoolean(),
									Mockito.nullable(String.class));
						} catch (SistemaExternException ex) {
							fail("La cridada al portafirmes ha fallat: " + ex.getMessage());
						}
						DocumentPortafirmesDto documentPortafirmes = documentService.portafirmesInfo(
								entitatCreada.getId(),
								documentCreat.getId());
						assertNotNull(documentPortafirmes);
						assertNotNull(documentPortafirmes.getPortafirmesId());
						Exception callbackException = documentService.portafirmesCallback(
								new Long(documentPortafirmes.getPortafirmesId()),
								PortafirmesCallbackEstatEnumDto.FIRMAT);
						assertNull(callbackException);
						DocumentDto documentFirmat = documentService.findById(
								entitatCreada.getId(),
								documentCreat.getId());
						assertNotNull(documentFirmat);
						assertNotNull(documentFirmat.getId());
						assertEquals(DocumentEstatEnumDto.CUSTODIAT, documentFirmat.getEstat());
						ExpedientDto expedientAlerta = expedientService.findById(
								entitatCreada.getId(),
								expedientCreat.getId());
						assertTrue(expedientAlerta.isAlerta());
						List<AlertaDto> alertes = contingutService.findAlertes(
								entitatCreada.getId(),
								expedientCreat.getId());
						assertNotNull(alertes);
						assertTrue(alertes.size() == 1);
					}
				},
				"Firma amb portafirmes d'un document a dins un expedient");
	}

	private void comprovarDocument(
			DocumentDto original,
			DocumentDto perComprovar) {
		assertEquals(original.getNom(), perComprovar.getNom());
		assertEquals(original.getData(), perComprovar.getData());
		assertEquals(original.getTipus(), perComprovar.getTipus());
		assertEquals(original.getDocumentTipus(), perComprovar.getDocumentTipus());
		assertEquals(original.getNtiOrigen(), perComprovar.getNtiOrigen());
		assertEquals(original.getNtiEstadoElaboracion(), perComprovar.getNtiEstadoElaboracion());
	}

	private void emplenarDocumentArxiu(DocumentDto dto) throws IOException {
		FitxerDto fitxerPdf = getFitxerPdfDeTest();
		dto.setFitxerNom(fitxerPdf.getNom());
		dto.setFitxerContentType(fitxerPdf.getContentType());
		dto.setFitxerContingut(fitxerPdf.getContingut());
	}

}