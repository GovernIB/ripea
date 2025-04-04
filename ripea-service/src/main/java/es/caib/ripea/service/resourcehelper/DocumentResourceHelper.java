package es.caib.ripea.service.resourcehelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;

import es.caib.ripea.persistence.entity.DocumentEntity;
import es.caib.ripea.persistence.entity.DocumentPortafirmesEntity;
import es.caib.ripea.persistence.entity.resourceentity.DocumentEnviamentAnnexResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.DocumentPortafirmesResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.DocumentResourceEntity;
import es.caib.ripea.persistence.entity.resourcerepository.DocumentEnviamentAnnexResourceRepository;
import es.caib.ripea.persistence.entity.resourcerepository.DocumentPortafirmesResourceRepository;
import es.caib.ripea.persistence.entity.resourcerepository.DocumentResourceRepository;
import es.caib.ripea.plugin.portafirmes.PortafirmesPrioritatEnum;
import es.caib.ripea.service.helper.ConfigHelper;
import es.caib.ripea.service.intf.config.PropertyConfig;
import es.caib.ripea.service.intf.dto.DocumentEnviamentEstatEnumDto;
import es.caib.ripea.service.intf.dto.DocumentEstatEnumDto;
import es.caib.ripea.service.intf.dto.DocumentTipusEnumDto;
import es.caib.ripea.service.intf.dto.LogTipusEnumDto;
import es.caib.ripea.service.intf.dto.MetaDocumentFirmaFluxTipusEnumDto;
import es.caib.ripea.service.intf.dto.MetaDocumentFirmaSequenciaTipusEnumDto;
import es.caib.ripea.service.intf.dto.PortafirmesPrioritatEnumDto;
import es.caib.ripea.service.intf.exception.SistemaExternException;
import es.caib.ripea.service.intf.exception.ValidationException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DocumentResourceHelper {

    private final DocumentResourceRepository documentResourceRepository;
    private final DocumentPortafirmesResourceRepository documentPortafirmesResourceRepository;
    private final DocumentEnviamentAnnexResourceRepository documentEnviamentAnnexResourceRepository;
    private final CacheResourceHelper cacheResourceHelper;
    private final ConfigHelper configHelper;

    public String getUniqueNameInPare(DocumentResourceEntity entity) {
        List<DocumentResourceEntity> documentResourceEntityList = documentResourceRepository.findAllByPareId(entity.getPare().getId());
        List<String> fitxerNomList = documentResourceEntityList.stream()
                .filter((document)-> Objects.isNull(entity.getId()) || !Objects.equals(entity.getId(), document.getId()))
                .map(DocumentResourceEntity::getFitxerNom)
                .collect(Collectors.toList());

        return getUniqueNameInPare(entity.getFitxerNom(), fitxerNomList);
    }

    public String getUniqueNameInPare(String nomPerComprovar, List<String> noms) {
        int ocurrences = 0;
        noms = noms.stream()
                .map((nom)->nom.substring(0, nom.lastIndexOf('.')))
                .collect(Collectors.toList());
        String newName = nomPerComprovar.substring(0, nomPerComprovar.lastIndexOf('.'));
        while(noms.contains(newName)) {
            ocurrences ++;
            newName = nomPerComprovar + " (" + ocurrences + ")";
        }
        return newName + nomPerComprovar.substring(nomPerComprovar.lastIndexOf('.'));
    }
    
	public String portafirmesEnviar(
			Long entitatId,
			DocumentResourceEntity document,
			String assumpte,
			PortafirmesPrioritatEnumDto prioritat,
			Date dataCaducitat,
			String portafirmesFluxId,
			String[] portafirmesResponsables,
			MetaDocumentFirmaSequenciaTipusEnumDto portafirmesSeqTipus,
			MetaDocumentFirmaFluxTipusEnumDto portafirmesFluxTipus,
			Long[] annexosIds,
			String transaccioId,
			boolean avisFirmaParcial,
			boolean firmaParcial) {
		
		if (!DocumentTipusEnumDto.DIGITAL.equals(document.getDocumentTipus())) {
			throw new ValidationException(document.getId(), DocumentEntity.class, "El document a enviar al portafirmes no és del tipus " + DocumentTipusEnumDto.DIGITAL);
		}
		if (!cacheResourceHelper.findErrorsValidacioPerNode(document).isEmpty()) {
			throw new ValidationException(document.getId(), DocumentEntity.class, "El document a enviar al portafirmes te alertes de validació");
		}		
		if (DocumentEstatEnumDto.FIRMAT.equals(document.getEstat()) || DocumentEstatEnumDto.CUSTODIAT.equals(document.getEstat())) {
			throw new ValidationException(document.getId(), DocumentEntity.class, "No es poden enviar al portafirmes documents firmates o custodiats");
		}
		
		List<DocumentPortafirmesResourceEntity> enviamentsPendents = documentPortafirmesResourceRepository.findByDocumentAndEstatInOrderByCreatedDateDesc(
				document,
				new DocumentEnviamentEstatEnumDto[] {
						DocumentEnviamentEstatEnumDto.PENDENT,
						DocumentEnviamentEstatEnumDto.ENVIAT
				});
		
		if (enviamentsPendents.size() > 0) {
			throw new ValidationException(document.getId(), DocumentEntity.class, "Aquest document te enviaments al portafirmes pendents");
		}
		if (!document.getMetaDocument().isFirmaPortafirmesActiva()) {
			throw new ValidationException(document.getId(), DocumentEntity.class, "El document no te activada la firma amb portafirmes");
		}
		
		// Activar l'ús del tipus de document de portafirmes
		boolean tipusDocumentPortafirmes = configHelper.getAsBoolean(PropertyConfig.TIPUS_DOC_PORTAFIRMES_ACTIU);
		
		DocumentPortafirmesResourceEntity documentPortafirmes = DocumentPortafirmesResourceEntity.getBuilder(
				DocumentEnviamentEstatEnumDto.PENDENT,
				assumpte,
				prioritat,
				dataCaducitat,
				tipusDocumentPortafirmes ? document.getMetaDocument().getPortafirmesDocumentTipus() : StringUtils.stripStart(document.getMetaDocument().getNtiTipoDocumental(), "TD0"),
				portafirmesResponsables,
				portafirmesSeqTipus,
				portafirmesFluxTipus,
				(portafirmesFluxId != null && !portafirmesFluxId.isEmpty()) ? portafirmesFluxId : document.getMetaDocument().getPortafirmesFluxId(),
				document.getExpedient(),
				document,
				avisFirmaParcial,
				firmaParcial).build();

		List<DocumentResourceEntity> annexos = new ArrayList<DocumentResourceEntity>();
		if (annexosIds != null) {
			for (Long annexId : annexosIds) {
				DocumentResourceEntity annex = documentResourceRepository.getOne(annexId);
				annexos.add(annex);
			}
		}
		
		String portafirmesId = null;
		
		try {
			/*
			 portafirmesId = pluginHelper.portafirmesUpload(
					document,
					documentPortafirmes.getAssumpte(),
					PortafirmesPrioritatEnum.valueOf(documentPortafirmes.getPrioritat().name()),
					documentPortafirmes.getCaducitatData(),
					documentPortafirmes.getDocumentTipus(),
					documentPortafirmes.getResponsables(),
					documentPortafirmes.getSequenciaTipus(),
					documentPortafirmes.getFluxId(),
					annexos,
					transaccioId);
			documentPortafirmes.updateEnviat(new Date(), portafirmesId);*/
		} catch (SistemaExternException ex) {
			Throwable rootCause = ExceptionUtils.getRootCause(ex);
			if (rootCause == null) rootCause = ex;
			documentPortafirmes.updateEnviatError(ExceptionUtils.getStackTrace(rootCause), null);
			throw ex;
		} finally {
			cacheResourceHelper.evictEnviamentsPortafirmesPendentsPerExpedient(document.getExpedient());
		}

		documentPortafirmes = documentPortafirmesResourceRepository.save(documentPortafirmes);
		
		document.updateEstat(DocumentEstatEnumDto.FIRMA_PENDENT);
		
		if (annexosIds != null) {
			for (DocumentResourceEntity annex : annexos) {
				DocumentEnviamentAnnexResourceEntity dere = new DocumentEnviamentAnnexResourceEntity();
				dere.setDocumentEnviament(documentPortafirmes);
				dere.setDocument(annex);
				documentEnviamentAnnexResourceRepository.save(dere);;
			}
		}
		
//		logAll(document, documentPortafirmes, LogTipusEnumDto.PFIRMA_ENVIAMENT);
		
		return portafirmesId;
	}
}
