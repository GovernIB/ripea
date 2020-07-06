/**
 * 
 */
package es.caib.ripea.core.helper;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
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
import es.caib.ripea.core.repository.EmailPendentEnviarRepository;
import es.caib.ripea.core.repository.EntitatRepository;
import es.caib.ripea.core.repository.ExpedientPeticioRepository;
	
@Component
public class TestHelper {
	@Autowired
	private ExpedientPeticioRepository expedientPeticioRepository;
	@Autowired
	private ExpedientPeticioHelper expedientPeticioHelper;
	@Resource
	private EntitatRepository entitatRepository;
	@Resource
	private CacheHelper cacheHelper;
	@Autowired
	private JavaMailSender mailSender;
	@Autowired
	private EmailHelper emailHelper;
	
	@Autowired
	private EmailPendentEnviarRepository emailPendentEnviarRepository;
	
	
	private static final String PREFIX_RIPEA = "[RIPEA]";
	
	
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
				"ntiClasificacionSia").build(); 
		
		
		MetaDocumentEntity metaDocumentEntity = MetaDocumentEntity.getBuilder(new EntitatEntity(), "codi", "nom", MultiplicitatEnumDto.M_0_1, new MetaExpedientEntity(), NtiOrigenEnumDto.O0, DocumentNtiEstadoElaboracionEnumDto.EE01, "ntiTipoDocumental").build();
		
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
				expedientEntity).
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
				documentCrear).build();

		
		
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
				"ntiClasificacionSia").build(); 
		
		
		MetaDocumentEntity metaDocumentEntity = MetaDocumentEntity.getBuilder(new EntitatEntity(), "codi", "nom", MultiplicitatEnumDto.M_0_1, new MetaExpedientEntity(), NtiOrigenEnumDto.O0, DocumentNtiEstadoElaboracionEnumDto.EE01, "ntiTipoDocumental").build();
		
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
				expedientEntity).
				build();
		
		
		
		DocumentNotificacioEntity documentNotificacioEntity = DocumentNotificacioEntity.getBuilder(
				DocumentNotificacioEstatEnumDto.FINALITZADA, "assumpte", DocumentNotificacioTipusEnumDto.COMUNICACIO, new Date(), 23, new Date(), expedientEntity, documentCrear, ServeiTipusEnumDto.NORMAL, false).build();
		
		emailHelper.canviEstatNotificacio(
				documentNotificacioEntity,
				DocumentNotificacioEstatEnumDto.ENVIADA);
		
	}

}
