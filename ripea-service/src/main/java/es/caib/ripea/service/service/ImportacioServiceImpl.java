package es.caib.ripea.service.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.ripea.service.helper.ContingutHelper;
import es.caib.ripea.service.helper.OrganGestorHelper;
import es.caib.ripea.service.intf.dto.DocumentDto;
import es.caib.ripea.service.intf.dto.ImportacioDto;
import es.caib.ripea.service.intf.service.ImportacioService;

@Service
public class ImportacioServiceImpl implements ImportacioService {

	@Autowired private ContingutHelper contingutHelper;
	@Autowired private OrganGestorHelper organGestorHelper;
	
	public static List<DocumentDto> expedientsWithImportacio = new ArrayList<DocumentDto>();
	public Map<String, String> documentAlreadyHasExpedient = new HashMap<String, String>();
	
	@Transactional
	@Override
	public int importarDocuments(
			Long entitatId,
			Long contingutId,
			ImportacioDto params) {
		organGestorHelper.actualitzarOrganCodi(organGestorHelper.getOrganCodiFromContingutId(contingutId));
		logger.debug("Important documents de l'arxiu digital (numeroRegistre=" + params.getNumeroRegistre() + ")");
		return contingutHelper.importarDocuments(entitatId, contingutId, params, documentAlreadyHasExpedient, expedientsWithImportacio);
	}

	@Override
	public List<DocumentDto> consultaExpedientsAmbImportacio() {
		return expedientsWithImportacio;
	}
	
	@Override
	public Map<String, String> consultaDocumentsWithExpedient() {
		return documentAlreadyHasExpedient;
	}
	
	private static final Logger logger = LoggerFactory.getLogger(ImportacioServiceImpl.class);
}