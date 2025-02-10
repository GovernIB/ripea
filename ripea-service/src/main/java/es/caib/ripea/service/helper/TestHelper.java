/**
 * 
 */
package es.caib.ripea.service.helper;

import es.caib.ripea.core.persistence.entity.*;
import es.caib.ripea.core.persistence.repository.EntitatRepository;
import es.caib.ripea.service.intf.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
	
@Component
public class TestHelper {

	@Resource
	private EntitatRepository entitatRepository;
	@Resource
	private CacheHelper cacheHelper;
	@Autowired
	private EmailHelper emailHelper;
	
	public void testCanviEstatDocumentPortafirmes() {
		
		MetaExpedientEntity metaExpedientEntity = new MetaExpedientEntity();
		
		ExpedientEntity expedientEntity= ExpedientEntity.getBuilder(
				"nom",
				metaExpedientEntity,
				new ExpedientEntity(),
				new EntitatEntity(),
				"ntiVersion",
				"ntiOrgano",
				new Date(),
				"ntiClasificacionSia",
				new OrganGestorEntity(),
				PrioritatEnumDto.B_NORMAL,
				null).build();
		
		
		MetaDocumentEntity metaDocumentEntity = MetaDocumentEntity.getBuilder(
				new EntitatEntity(),
				"codi",
				"nom",
				MultiplicitatEnumDto.M_0_1,
				new MetaExpedientEntity(),
				NtiOrigenEnumDto.O0,
				DocumentNtiEstadoElaboracionEnumDto.EE01,
				"ntiTipoDocumental",
				false,
				"Finalitat test",
				0).build();
		
		DocumentEntity documentCrear = DocumentEntity.getBuilder(
				DocumentTipusEnumDto.DIGITAL,
				DocumentEstatEnumDto.REDACCIO,
				"nom",
				"descripcio",
				new Date(),
				new Date(),
				"ntiIdDocumentoOrigen",
				"1.0",
				"ntiOrgano",
				NtiOrigenEnumDto.O0,
				DocumentNtiEstadoElaboracionEnumDto.EE02,
				"ntiTipoDocumental",
				metaDocumentEntity,
				expedientEntity,
				new EntitatEntity(),
				expedientEntity, null, null).
				build();
		
		
		DocumentPortafirmesEntity documentPortafirmes = DocumentPortafirmesEntity.getBuilder(
				DocumentEnviamentEstatEnumDto.PENDENT,
				"assumpte",
				PortafirmesPrioritatEnumDto.ALTA,
				new Date(),
				"documentTipus",
				new String[] {"reposnsable" },
				MetaDocumentFirmaSequenciaTipusEnumDto.PARALEL,
				MetaDocumentFirmaFluxTipusEnumDto.PORTAFIB,
				"fluxId",
				expedientEntity,
				documentCrear,
				false,
				false).build();

		
		
		emailHelper.canviEstatDocumentPortafirmes(documentPortafirmes);
	}
	
	
	public void testCanviEstatNotificacio(){
		
		
		
		MetaExpedientEntity metaExpedientEntity = new MetaExpedientEntity();
//		metaExpedientEntity.setId(7083l);
		
		ExpedientEntity expedientEntity= ExpedientEntity.getBuilder(
				"nom",
				metaExpedientEntity,
				new ExpedientEntity(),
				new EntitatEntity(),
				"ntiVersion",
				"ntiOrgano",
				new Date(),
				"ntiClasificacionSia",
				new OrganGestorEntity(),
				PrioritatEnumDto.B_NORMAL,
				null).build();
		
		
		MetaDocumentEntity metaDocumentEntity = MetaDocumentEntity.getBuilder(
				new EntitatEntity(),
				"codi",
				"nom",
				MultiplicitatEnumDto.M_0_1,
				new MetaExpedientEntity(),
				NtiOrigenEnumDto.O0,
				DocumentNtiEstadoElaboracionEnumDto.EE01,
				"ntiTipoDocumental",
				false,
				"Finalitat test",
				0).build();
		
		DocumentEntity documentCrear = DocumentEntity.getBuilder(
				DocumentTipusEnumDto.DIGITAL,
				DocumentEstatEnumDto.REDACCIO,
				"nom",
				"descripcio",
				new Date(),
				new Date(),
				"ntiIdDocumentoOrigen",
				"1.0",
				"ntiOrgano",
				NtiOrigenEnumDto.O0,
				DocumentNtiEstadoElaboracionEnumDto.EE02,
				"ntiTipoDocumental",
				metaDocumentEntity,
				expedientEntity,
				new EntitatEntity(),
				expedientEntity, null, null).
				build();
		
		
		
		DocumentNotificacioEntity documentNotificacioEntity = DocumentNotificacioEntity.getBuilder(
				DocumentNotificacioEstatEnumDto.FINALITZADA, "assumpte", DocumentNotificacioTipusEnumDto.COMUNICACIO, new Date(), 23, new Date(), expedientEntity, documentCrear, ServeiTipusEnumDto.NORMAL, false).build();
		
		emailHelper.canviEstatNotificacio(
				documentNotificacioEntity,
				DocumentNotificacioEstatEnumDto.ENVIADA);
		
	}

}
