/**
 * 
 */
package es.caib.ripea.core.helper;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import es.caib.ripea.core.api.dto.MetaDocumentDto;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.MetaDocumentEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;
import es.caib.ripea.core.repository.DocumentRepository;
import es.caib.ripea.core.repository.EntitatRepository;
import es.caib.ripea.core.repository.MetaDadaRepository;
import es.caib.ripea.core.repository.MetaDocumentRepository;


@Component
public class MetaDocumentHelper {
	@Resource
	private MetaDocumentRepository metaDocumentRepository;
	@Resource
	private EntitatRepository entitatRepository;
	@Resource
	private MetaDadaRepository metaDadaRepository;
	@Resource
	private DocumentRepository documentRepository;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private MetaNodeHelper metaNodeHelper;
	@Resource
	private ContingutHelper contenidorHelper;
	@Resource
	private PaginacioHelper paginacioHelper;
	@Resource
	private PermisosHelper permisosHelper;
	@Resource
	private PluginHelper pluginHelper;
	@Resource
	private EntityComprovarHelper entityComprovarHelper;
	@Resource
	private MetaExpedientHelper metaExpedientHelper;
    
	
	
	public MetaDocumentDto create(
			Long entitatId,
			Long metaExpedientId,
			MetaDocumentDto metaDocument,
			String plantillaNom,
			String plantillaContentType,
			byte[] plantillaContingut, String rolActual, Long organId) {
		
		logger.debug("Creant un nou meta-document (" +
				"entitatId=" + entitatId + ", " +
				"metaExpedientId=" + metaExpedientId + ", " +
				"metaDocument=" + metaDocument + ")");

		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				false, 
				true, false);
		MetaExpedientEntity metaExpedient = entityComprovarHelper.comprovarMetaExpedient(
				entitat,
				metaExpedientId);
		MetaDocumentEntity entity = MetaDocumentEntity.getBuilder(
				entitat,
				metaDocument.getCodi(),
				metaDocument.getNom(),
				metaDocument.getMultiplicitat(),
				metaExpedient,
				metaDocument.getNtiOrigen(),
				metaDocument.getNtiEstadoElaboracion(),
				metaDocument.getNtiTipoDocumental(),
				metaDocument.isPinbalActiu(),
				metaDocument.getPinbalFinalitat(),
				metaDocument.getPinbalServeiDocsPermesos()).
				biometricaLectura(metaDocument.isBiometricaLectura()).
				firmaBiometricaActiva(metaDocument.isFirmaBiometricaActiva()).
				firmaPortafirmesActiva(metaDocument.isFirmaPortafirmesActiva()).
				descripcio(metaDocument.getDescripcio()).
				portafirmesDocumentTipus(metaDocument.getPortafirmesDocumentTipus()).
				portafirmesFluxId(metaDocument.getPortafirmesFluxId()).
				portafirmesResponsables(metaDocument.getPortafirmesResponsables()).
				portafirmesSequenciaTipus(metaDocument.getPortafirmesSequenciaTipus()).
				portafirmesCustodiaTipus(metaDocument.getPortafirmesCustodiaTipus()).
				firmaPassarelaActiva(metaDocument.isFirmaPassarelaActiva()).
				firmaPassarelaCustodiaTipus(metaDocument.getFirmaPassarelaCustodiaTipus()).
				portafirmesFluxTipus(metaDocument.getPortafirmesFluxTipus()).
				pinbalServei(metaDocument.getPinbalServei()).
				build();
		if (plantillaContingut != null) {
			entity.updatePlantilla(
					plantillaNom,
					plantillaContentType,
					plantillaContingut);
		}
		
		if ("IPA_ORGAN_ADMIN".equals(rolActual)) {
			metaExpedientHelper.canviarRevisioADisseny(entitatId, metaExpedientId, organId);
		}

		return conversioTipusHelper.convertir(
				metaDocumentRepository.save(entity),
				MetaDocumentDto.class);
	}
	
	
	private static final Logger logger = LoggerFactory.getLogger(MetaDocumentHelper.class);
	

}
