/**
 * 
 */
package es.caib.ripea.core.helper;

import java.util.Date;

import javax.annotation.Resource;

import es.caib.ripea.core.api.dto.PrioritatEnumDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.caib.ripea.core.api.dto.DocumentEnviamentEstatEnumDto;
import es.caib.ripea.core.api.dto.DocumentEstatEnumDto;
import es.caib.ripea.core.api.dto.DocumentNotificacioEstatEnumDto;
import es.caib.ripea.core.api.dto.DocumentNotificacioTipusEnumDto;
import es.caib.ripea.core.api.dto.DocumentNtiEstadoElaboracionEnumDto;
import es.caib.ripea.core.api.dto.DocumentTipusEnumDto;
import es.caib.ripea.core.api.dto.MetaDocumentFirmaFluxTipusEnumDto;
import es.caib.ripea.core.api.dto.MetaDocumentFirmaSequenciaTipusEnumDto;
import es.caib.ripea.core.api.dto.MultiplicitatEnumDto;
import es.caib.ripea.core.api.dto.NtiOrigenEnumDto;
import es.caib.ripea.core.api.dto.PortafirmesPrioritatEnumDto;
import es.caib.ripea.core.api.dto.ServeiTipusEnumDto;
import es.caib.ripea.core.entity.DocumentEntity;
import es.caib.ripea.core.entity.DocumentNotificacioEntity;
import es.caib.ripea.core.entity.DocumentPortafirmesEntity;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.entity.MetaDocumentEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;
import es.caib.ripea.core.entity.OrganGestorEntity;
import es.caib.ripea.core.repository.EntitatRepository;
	
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
				PrioritatEnumDto.NORMAL).build();
		
		
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
				PrioritatEnumDto.NORMAL).build();
		
		
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
