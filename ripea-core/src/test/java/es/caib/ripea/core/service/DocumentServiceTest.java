/**
 * 
 */
package es.caib.ripea.core.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import es.caib.ripea.core.api.dto.DocumentDto;
import es.caib.ripea.core.api.dto.DocumentNtiEstadoElaboracionEnumDto;
import es.caib.ripea.core.api.dto.DocumentTipusEnumDto;
import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.ExpedientDto;
import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.dto.MetaDocumentDto;
import es.caib.ripea.core.api.dto.NtiOrigenEnumDto;
import es.caib.ripea.core.api.service.DocumentService;

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
					}
				},
				"Eliminació d'un document de dins un expedient");
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
		FitxerDto fitxer = getFitxerPdfDeTest();
		dto.setFitxerNom(fitxer.getNom());
		dto.setFitxerContentType(fitxer.getContentType());
		dto.setFitxerContingut(fitxer.getContingut());
	}

}
