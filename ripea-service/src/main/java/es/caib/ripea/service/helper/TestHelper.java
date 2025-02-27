package es.caib.ripea.service.helper;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.caib.ripea.persistence.entity.DocumentEntity;
import es.caib.ripea.persistence.entity.DocumentNotificacioEntity;
import es.caib.ripea.persistence.entity.DocumentPortafirmesEntity;
import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.ExpedientEntity;
import es.caib.ripea.persistence.entity.MetaDocumentEntity;
import es.caib.ripea.persistence.entity.MetaExpedientEntity;
import es.caib.ripea.persistence.entity.OrganGestorEntity;
import es.caib.ripea.service.intf.dto.DocumentEnviamentEstatEnumDto;
import es.caib.ripea.service.intf.dto.DocumentEstatEnumDto;
import es.caib.ripea.service.intf.dto.DocumentNotificacioEstatEnumDto;
import es.caib.ripea.service.intf.dto.DocumentNotificacioTipusEnumDto;
import es.caib.ripea.service.intf.dto.DocumentNtiEstadoElaboracionEnumDto;
import es.caib.ripea.service.intf.dto.DocumentTipusEnumDto;
import es.caib.ripea.service.intf.dto.MetaDocumentFirmaFluxTipusEnumDto;
import es.caib.ripea.service.intf.dto.MetaDocumentFirmaSequenciaTipusEnumDto;
import es.caib.ripea.service.intf.dto.MultiplicitatEnumDto;
import es.caib.ripea.service.intf.dto.NtiOrigenEnumDto;
import es.caib.ripea.service.intf.dto.PortafirmesPrioritatEnumDto;
import es.caib.ripea.service.intf.dto.PrioritatEnumDto;
import es.caib.ripea.service.intf.dto.ServeiTipusEnumDto;
	
@Component
public class TestHelper {

	@Autowired private EmailHelper emailHelper;
	
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
