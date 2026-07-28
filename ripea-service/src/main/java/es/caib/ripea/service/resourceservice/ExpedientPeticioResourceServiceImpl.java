package es.caib.ripea.service.resourceservice;

import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import com.turkraft.springfilter.FilterBuilder;
import com.turkraft.springfilter.parser.Filter;

import es.caib.plugins.arxiu.api.ContingutOrigen;
import es.caib.plugins.arxiu.api.Document;
import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.ExpedientEntity;
import es.caib.ripea.persistence.entity.ExpedientPeticioEntity;
import es.caib.ripea.persistence.entity.GrupEntity;
import es.caib.ripea.persistence.entity.MetaDocumentEntity;
import es.caib.ripea.persistence.entity.MetaExpedientEntity;
import es.caib.ripea.persistence.entity.OrganGestorEntity;
import es.caib.ripea.persistence.entity.resourceentity.ExpedientPeticioResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.MetaExpedientResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.RegistreAnnexResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.RegistreInteressatResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.RegistreResourceEntity;
import es.caib.ripea.persistence.entity.resourcerepository.MetaExpedientResourceRepository;
import es.caib.ripea.persistence.entity.resourcerepository.MetaExpedientSequenciaResourceRepository;
import es.caib.ripea.persistence.entity.resourcerepository.RegistreAnnexResourceRepository;
import es.caib.ripea.persistence.entity.resourcerepository.RegistreResourceRepository;
import es.caib.ripea.persistence.repository.ExecucioMassivaContingutRepository;
import es.caib.ripea.persistence.repository.ExpedientPeticioRepository;
import es.caib.ripea.persistence.repository.ExpedientRepository;
import es.caib.ripea.persistence.repository.MetaExpedientRepository;
import es.caib.ripea.persistence.repository.OrganGestorRepository;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.helper.AnotacioDistribucioHelper;
import es.caib.ripea.service.helper.ConfigHelper;
import es.caib.ripea.service.helper.EmailHelper;
import es.caib.ripea.service.helper.EntityComprovarHelper;
import es.caib.ripea.service.helper.EventHelper;
import es.caib.ripea.service.helper.ExcepcioLogHelper;
import es.caib.ripea.service.helper.ExecucioMassivaHelper;
import es.caib.ripea.service.helper.ExpedientHelper;
import es.caib.ripea.service.helper.ExpedientPeticioHelper;
import es.caib.ripea.service.helper.GrupHelper;
import es.caib.ripea.service.helper.MessageHelper;
import es.caib.ripea.service.helper.MetaDocumentHelper;
import es.caib.ripea.service.helper.PermisosPerAnotacions;
import es.caib.ripea.service.helper.PluginHelper;
import es.caib.ripea.service.intf.base.exception.ActionExecutionException;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException.AnswerValue;
import es.caib.ripea.service.intf.base.exception.PerspectiveApplicationException;
import es.caib.ripea.service.intf.base.exception.ReportGenerationException;
import es.caib.ripea.service.intf.base.exception.ResourceNotFoundException;
import es.caib.ripea.service.intf.base.model.DownloadableFile;
import es.caib.ripea.service.intf.base.model.FieldOption;
import es.caib.ripea.service.intf.base.model.ReportFileType;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.config.PropertyConfig;
import es.caib.ripea.service.intf.dto.ArxiuEstatEnumDto;
import es.caib.ripea.service.intf.dto.ElementTipusEnumDto;
import es.caib.ripea.service.intf.dto.ExecucioMassivaDto;
import es.caib.ripea.service.intf.dto.ExecucioMassivaTipusDto;
import es.caib.ripea.service.intf.dto.ExpedientPeticioAccioEnumDto;
import es.caib.ripea.service.intf.dto.ExpedientPeticioEstatEnumDto;
import es.caib.ripea.service.intf.dto.ExpedientPeticioEstatViewEnumDto;
import es.caib.ripea.service.intf.dto.InteressatAssociacioAccioEnum;
import es.caib.ripea.service.intf.dto.NtiTipoDocumentoEnumDto;
import es.caib.ripea.service.intf.dto.SiNoEnumDto;
import es.caib.ripea.service.intf.model.ExpedientPeticioResource;
import es.caib.ripea.service.intf.model.ExpedientPeticioResource.AcceptarAnotacioForm;
import es.caib.ripea.service.intf.model.ExpedientPeticioResource.RebutjarAnotacioForm;
import es.caib.ripea.service.intf.model.ExpedientPeticioResource.SubsanarAnnexosForm;
import es.caib.ripea.service.intf.model.MetaExpedientResource;
import es.caib.ripea.service.intf.model.NodeResource.MassiveAction;
import es.caib.ripea.service.intf.model.RegistreAnnexResource;
import es.caib.ripea.service.intf.model.RegistreInteressatResource;
import es.caib.ripea.service.intf.model.RegistreResource;
import es.caib.ripea.service.intf.registre.RegistreAnnexFirmaTipusEnum;
import es.caib.ripea.service.intf.registre.RegistreAnnexNtiEstadoElaboracionEnum;
import es.caib.ripea.service.intf.registre.RegistreAnnexNtiOrigenEnum;
import es.caib.ripea.service.intf.resourceservice.ExpedientPeticioResourceService;
import es.caib.ripea.service.intf.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExpedientPeticioResourceServiceImpl extends BaseMutableResourceService<ExpedientPeticioResource, Long, ExpedientPeticioResourceEntity> implements ExpedientPeticioResourceService {

	private final ConfigHelper configHelper;
	private final PluginHelper pluginHelper;
	private final EventHelper eventHelper;
	private final EmailHelper emailHelper;
	private final ExcepcioLogHelper excepcioLogHelper;
	private final ExpedientPeticioHelper expedientPeticioHelper;
	private final EntityComprovarHelper entityComprovarHelper;
	private final MetaDocumentHelper metaDocumentHelper;
	private final ExpedientHelper expedientHelper;
	private final MessageHelper messageHelper;
	private final AnotacioDistribucioHelper anotacioDistribucioHelper;
	private final ExecucioMassivaHelper execucioMassivaHelper;
	private final ExecucioMassivaContingutRepository execucioMassivaContingutRepository;

	private final OrganGestorRepository organGestorRepository;
	private final MetaExpedientRepository metaExpedientRepository;
	private final ExpedientPeticioRepository expedientPeticioRepository;
	private final ExpedientRepository expedientRepository;
	private final RegistreResourceRepository registreResourceRepository;
	private final GrupHelper grupHelper;

	private final RegistreAnnexResourceRepository registreAnnexResourceRepository;

	private final MetaExpedientSequenciaResourceRepository metaExpedientSequenciaResourceRepository;
	private final MetaExpedientResourceRepository metaExpedientResourceRepository;

    @PostConstruct
    public void init() {
        register(ExpedientPeticioResource.PERSPECTIVE_REGISTRE_CODE, new RegistrePerspectiveApplicator());
        register(ExpedientPeticioResource.PERSPECTIVE_ESTAT_VIEW_CODE, new EstatViewPerspectiveApplicator());
        register(ExpedientPeticioResource.PERSPECTIVE_EN_PROCES_ACTUALITZAR_ESTAT_CODE, new EnProcesActualitzarEstatPerspectiveApplicator());
        register(ExpedientPeticioResource.REPORT_DOWNLOAD_JUSTIFICANT, new DescarregarJustificantReportGenerator());
        register(ExpedientPeticioResource.ACTION_REBUTJAR_ANOTACIO, new RebutjarAnotacioActionExecutor());
        register(ExpedientPeticioResource.ACTION_ACCEPTAR_ANOTACIO, new AcceptarAnotacioActionExecutor());
        register(ExpedientPeticioResource.ACTION_ESTAT_DISTRIBUCIO, new CanviEstatDistribucioActionExecutor());
        register(ExpedientPeticioResource.ACTION_CONSULTAR_I_GUARDAR, new ConsultarGuardarAnotacioPendentActionExecutor());
        register(ExpedientPeticioResource.PERSPECTIVE_ANNEXOS_ERROR_CODE, new AnnexosErrorPerspectiveApplicator());
        register(ExpedientPeticioResource.ACTION_SUBSANAR_ANNEXOS, new SubsanarAnnexosActionExecutor());

        register(ExpedientPeticioResource.Fields.metaExpedient, new MetaExpedientOnchangeLogicProcessor());
        register(null, new InitialOnChangeDocumentResourceLogicProcessor());
    }

    public class InitialOnChangeDocumentResourceLogicProcessor implements OnChangeLogicProcessor<ExpedientPeticioResource> {
		@Override
		public void onChange(Serializable id, ExpedientPeticioResource previous, String fieldName, Object fieldValue,
				Map<String, AnswerValue> answers, String[] previousFieldNames, ExpedientPeticioResource target) {
			setGrupsFromProcediment(previous.getMetaExpedient(), target);

			RegistreResourceEntity rre = registreResourceRepository.findById(previous.getRegistre().getId()).get();
			target.setRegistreExtracte(rre.getExtracte());
		}
    }

    private class MetaExpedientOnchangeLogicProcessor implements OnChangeLogicProcessor<ExpedientPeticioResource> {
		@Override
		public void onChange(Serializable id, ExpedientPeticioResource previous, String fieldName, Object fieldValue,
				Map<String, AnswerValue> answers, String[] previousFieldNames, ExpedientPeticioResource target) {
			setGrupsFromProcediment(fieldValue, target);
		}
    }

    private void setGrupsFromProcediment(Object fieldValue, ExpedientPeticioResource target) {

		if (fieldValue!=null) {

			ResourceReference<MetaExpedientResource, Long> metaExpRR = (ResourceReference<MetaExpedientResource, Long>)fieldValue;

			if (metaExpRR!=null && metaExpRR.getId()!=null) {

				String entitatActualCodi = configHelper.getEntitatActualCodi();
		        EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatActualCodi, false, false, false, true,false);

		        Long organGestorId = null;

		        if (configHelper.isRolActualTreballaAmbOrgan()) {
		        	OrganGestorEntity ogEntity	= organGestorRepository.findByEntitatIdAndCodi(
		        			entitat.getId(),
		        			configHelper.getOrganActualCodi());
		        	if (ogEntity!=null) {
		        		organGestorId = ogEntity.getId();
		        	}
		        }

		        List<GrupEntity> grupsProcediment = grupHelper.findGrups(entitat.getId(), organGestorId, metaExpRR.getId());
		        target.setMostrarGrups(grupsProcediment.size()>0);

			} else {
				target.setMostrarGrups(false);
			}
		} else {
			target.setMostrarGrups(false);
		}
    }

    @Override
    protected String additionalSpringFilter(String currentSpringFilter, String[] namedQueries) {
        List<Filter> filters = new ArrayList<>();
        filters.add((currentSpringFilter != null && !currentSpringFilter.isEmpty())?Filter.parse(currentSpringFilter):null);

        String entitatActualCodi = configHelper.getEntitatActualCodi();
        EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatActualCodi, false, false, false, true,false);

        Map<String, String> mapaNamedQueries =  Utils.namedQueriesToMap(namedQueries);

        //El filtre per entitat s'aplica sempre (consulta genèrica i named queries),
        //excepte el llistat del menú "Consulta > Anotacions comunicades", que no filtra per entitat.
        if (!mapaNamedQueries.containsKey("CONSULTA_COMUNICADES")) {
	        filters.add(FilterBuilder.equal(
	        		ExpedientPeticioResource.Fields.registre + "." + RegistreResource.Fields.entitatCodi,
	        		entitat!=null?entitat.getUnitatArrel():"................................................................................"));
        }

    	if (!mapaNamedQueries.isEmpty()) {
    		
    		if (mapaNamedQueries.containsKey("ESTAT_PENDENT")) {
    			filters.add(FilterBuilder.equal(ExpedientPeticioResource.Fields.estat, ExpedientPeticioEstatEnumDto.PENDENT));
    		}
    		
    		if (mapaNamedQueries.containsKey("LLISTAT_ANOTACIONS")) {

    			String organActualCodi	 = configHelper.getOrganActualCodi();
    			String rolActual		 = configHelper.getRolActual();

    			boolean isAdmin 		= "IPA_ADMIN".equals(rolActual);
    			boolean isAdminOrgan 	= "IPA_ORGAN_ADMIN".equals(rolActual);

    			//Admin no aplica filtres de permisos
    			if (!isAdmin) {

	    			OrganGestorEntity ogEntity	= organGestorRepository.findByEntitatIdAndCodi(entitat.getId(), organActualCodi);
					PermisosPerAnotacions permisosPerAnotacions = expedientPeticioHelper.findPermisosPerAnotacions(
							entitat.getId(),
							null, //UsuariActual, agafa el autenticat
							rolActual,
							ogEntity!=null?ogEntity.getId():null);

					//Aplica filtres de permisos per organ
					if (isAdminOrgan) {

				        String ogId = ExpedientPeticioResource.Fields.registre + "." + RegistreResource.Fields.destiCodi;
				        Filter filtreOrgansPermesos = null;
				        List<String> grupsOrgansPermesosClausulesIn = permisosPerAnotacions.getIdsOrganGestorsGruposMil();
				        if (grupsOrgansPermesosClausulesIn!=null) {
					        for (String aux: grupsOrgansPermesosClausulesIn) {
						        if (aux != null && !aux.isEmpty()) {
					        		filtreOrgansPermesos = FilterBuilder.or(filtreOrgansPermesos, Filter.parse(ogId + " IN (" + aux + ")"));
						        }
					        }
				        }

				        //Sense òrgans permesos no es retornen resultats (igual que la consulta antiga),
				        //evitant que un filtre nul es perdi a l'AND final.
                        filters.add(filtreOrgansPermesos!=null ? filtreOrgansPermesos : FilterBuilder.equal("id", 0));

					} else { //Aplica filtres de permisos per procediment

				        String prId = ExpedientPeticioResource.Fields.metaExpedient + ".id";
				        Filter filtreProcedimentsPermesos = null;
				        List<String> grupsProcsPermesosClausulesIn = permisosPerAnotacions.getIdsProcedimentsGruposMil();
				        if (grupsProcsPermesosClausulesIn!=null) {
					        for (String aux: grupsProcsPermesosClausulesIn) {
						        if (aux != null && !aux.isEmpty()) {
					        		filtreProcedimentsPermesos = FilterBuilder.or(filtreProcedimentsPermesos, Filter.parse(prId + " IN (" + aux + ")"));
						        }
					        }
				        }

				        String grId = ExpedientPeticioResource.Fields.grup + ".id";
				        Filter filtregrupsPermesos = null;
				        List<String> grupsgrupsPermesosClausulesIn = permisosPerAnotacions.getIdsGrupsGruposMil();
				        if (grupsgrupsPermesosClausulesIn!=null) {
					        for (String aux: grupsgrupsPermesosClausulesIn) {
						        if (aux != null && !aux.isEmpty()) {
						        	filtregrupsPermesos = FilterBuilder.or(filtregrupsPermesos, Filter.parse(grId + " IN (" + aux + ")"));
						        }
					        }
				        }

				        String grAct = ExpedientPeticioResource.Fields.metaExpedient +"."+ MetaExpedientResource.Fields.gestioAmbGrupsActiva;
				        Filter notGestioGrupsActiva = FilterBuilder.equal(grAct, false);
				        Filter filterGEstioGrupsActius = FilterBuilder.or(notGestioGrupsActiva, filtregrupsPermesos);

				        //Sense procediments permesos no es retornen resultats (igual que la consulta antiga).
				        //Així evitam que FilterBuilder.and(null, ...) elimini la restricció per procediment.
				        if (filtreProcedimentsPermesos!=null) {
				        	filters.add(FilterBuilder.and(filtreProcedimentsPermesos, filterGEstioGrupsActius));
				        } else {
				        	filters.add(FilterBuilder.equal("id", 0));
				        }
					}
    			}
    		} else if (mapaNamedQueries.containsKey("MASSIU_ANOTACIONS_ESTAT")) {
                filters.add(FilterBuilder.notEqual(ExpedientPeticioResource.Fields.estat, ExpedientPeticioEstatEnumDto.CREAT));
    		}
    	}

        List<Filter> result = filters.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return result.isEmpty() ? null : FilterBuilder.and(result).generate();
    }

    @Override
	public ExpedientPeticioResource update(Long id, ExpedientPeticioResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) throws ResourceNotFoundException {
    	try {
    		expedientPeticioHelper.canviarProcediment(
    				resource.getId(),
    				resource.getMetaExpedient().getId(),
    				resource.getGrup()!=null?resource.getGrup().getId():null);
    		return resource;
    	} catch (Exception ex) {
    		excepcioLogHelper.addExcepcio("/anotacio/"+resource.getId()+"/update", ex);
    	}
    	return null;
    }

    private class RegistrePerspectiveApplicator implements PerspectiveApplicator<ExpedientPeticioResourceEntity, ExpedientPeticioResource> {
        @Override
        public void applySingle(String code, ExpedientPeticioResourceEntity entity, ExpedientPeticioResource resource) throws PerspectiveApplicationException {

        	RegistreResource rr = objectMappingHelper.newInstanceMap(Hibernate.unproxy(entity.getRegistre()), RegistreResource.class);
        	resource.setRegistreInfo(rr);

        	if (entity.getRegistre().getInteressats()!=null) {
	            resource.getRegistreInfo().setInteressats(
	                    entity.getRegistre().getInteressats().stream()
	                            .map(interessat -> {
	                                RegistreInteressatResource interessatResource = objectMappingHelper.newInstanceMap(interessat, RegistreInteressatResource.class);
	                                return ResourceReference.<RegistreInteressatResource, Long>toResourceReference(interessatResource.getId(), interessatResource.getCodiNom());
	                            })
	                            .collect(Collectors.toList())
	            );
        	}

            if (resource.getRegistreInfo().getJustificantArxiuUuid()!=null && Boolean.parseBoolean(configHelper.getConfig(PropertyConfig.INCORPORAR_JUSTIFICANT))) {
            	RegistreAnnexResource justificant = new RegistreAnnexResource();
            	Document documentDetalls = pluginHelper.arxiuDocumentConsultar(
    					null,
    					resource.getRegistreInfo().getJustificantArxiuUuid(),
    					null,
    					true,
    					false);
            	justificant.setTitol(documentDetalls.getNom());
            	if (documentDetalls.getContingut()!=null) {
            		justificant.setTamany(documentDetalls.getContingut().getTamany());
            		justificant.setTipusMime(documentDetalls.getContingut().getTipusMime());
            	}
            	justificant.setObservacions(documentDetalls.getDescripcio());
            	//Millora: es fan conversions d'enumerats que no farien falta si la classe destí tengues com a tipus d'atribut la clase enum del origen
            	try {
            		ArxiuEstatEnumDto estatArxiu = ArxiuEstatEnumDto.valueOf(documentDetalls.getEstat().toString());
            		justificant.setAnnexArxiuEstat(estatArxiu);
            	} catch (Exception ex) {}
            	if (documentDetalls.getMetadades()!=null) {
            		String extensio = documentDetalls.getMetadades().getExtensio()!=null?documentDetalls.getMetadades().getExtensio().toString():".pdf";
            		justificant.setNom(documentDetalls.getNom()+extensio);
            		justificant.setNtiFechaCaptura(documentDetalls.getMetadades().getDataCaptura());
            		if (ContingutOrigen.ADMINISTRACIO.equals(documentDetalls.getMetadades().getOrigen())) {
            			justificant.setNtiOrigen(RegistreAnnexNtiOrigenEnum.ADMINISTRACIO);
            		} else {
            			justificant.setNtiOrigen(RegistreAnnexNtiOrigenEnum.CIUTADA);
            		}
            		try {
            			NtiTipoDocumentoEnumDto enumTD = NtiTipoDocumentoEnumDto.valueOf(documentDetalls.getMetadades().getTipusDocumental().name());
            			justificant.setNtiTipoDocumental(enumTD);
            		} catch (Exception ex) {}
            		justificant.setUuid(documentDetalls.getIdentificador());
            		if (documentDetalls.getFirmes() != null && !documentDetalls.getFirmes().isEmpty()) {
            			justificant.setFirmaPerfil(documentDetalls.getFirmes().get(0).getPerfil().name());
            			try {
            				RegistreAnnexFirmaTipusEnum enumTF = RegistreAnnexFirmaTipusEnum.valueOf(documentDetalls.getFirmes().get(0).getTipus().name());
            				justificant.setFirmaTipus(enumTF);
            			} catch (Exception ex) {}
            		}
            		try {
            			RegistreAnnexNtiEstadoElaboracionEnum enumEE = RegistreAnnexNtiEstadoElaboracionEnum.valueOf(documentDetalls.getMetadades().getEstatElaboracio().name());
            			justificant.setNtiEstadoElaboracion(enumEE);
            		} catch (Exception ex) {}
            	}
            	resource.getRegistreInfo().setJustificant(justificant);
            }
        }
    }

    private class EstatViewPerspectiveApplicator implements PerspectiveApplicator<ExpedientPeticioResourceEntity, ExpedientPeticioResource> {
        @Override
        public void applySingle(String code, ExpedientPeticioResourceEntity entity, ExpedientPeticioResource resource) throws PerspectiveApplicationException {
            if (resource.getEstat() != null) {
                switch (resource.getEstat()) {
                    case PENDENT:
                        resource.setEstatView(ExpedientPeticioEstatViewEnumDto.PENDENT);
                        break;
                    case PROCESSAT_PENDENT:
                    case PROCESSAT_NOTIFICAT:
                        resource.setEstatView(ExpedientPeticioEstatViewEnumDto.ACCEPTAT);
                        break;
                    case REBUTJAT:
                        resource.setEstatView(ExpedientPeticioEstatViewEnumDto.REBUTJAT);
                        break;
                }
            }
        }
    }

    private class EnProcesActualitzarEstatPerspectiveApplicator implements PerspectiveApplicator<ExpedientPeticioResourceEntity, ExpedientPeticioResource> {
        @Override
        public void applySingle(String code, ExpedientPeticioResourceEntity entity, ExpedientPeticioResource resource) throws PerspectiveApplicationException {
            execucioMassivaContingutRepository
                    .findFirstByElementIdAndElementTipusAndExecucioMassivaTipusAndExecucioMassivaDataFiNull(
                            entity.getId(), ElementTipusEnumDto.ANOTACIO, ExecucioMassivaTipusDto.ACTUALITZAR_ESTAT_ANOTACIONS)
                    .ifPresent(contingut -> resource.setExecucioMassivaActualitzarEstatId(contingut.getExecucioMassiva().getId()));
        }
    }

    private class AnnexosErrorPerspectiveApplicator implements PerspectiveApplicator<ExpedientPeticioResourceEntity, ExpedientPeticioResource> {
        @Override
        public void applySingle(String code, ExpedientPeticioResourceEntity entity, ExpedientPeticioResource resource) throws PerspectiveApplicationException {
            //Només les anotacions acceptades (amb expedient associat) poden tenir annexos en error pendents de subsanar.
            if (entity.getExpedient() != null && entity.getRegistre() != null) {
                long annexosAmbError = registreAnnexResourceRepository.countAnnexosAmbErrorByRegistreId(entity.getRegistre().getId());
                resource.setTeAnnexosAmbError(annexosAmbError > 0);
            }
        }
    }

    private class AcceptarAnotacioActionExecutor implements ActionExecutor<ExpedientPeticioResourceEntity, AcceptarAnotacioForm, Serializable> {

        private Map<String, String> parseToMap(String input){
            String[] tokens = input.split(",", -1); // split preservando vacíos

            Map<String, String> map = new HashMap<>();
            for (int i = 0; i < tokens.length - 1; i += 2) {
                String key = tokens[i].trim();
                String value = tokens[i + 1].trim();
                map.put(key, value);
            }
            return map;
        }

        @Override
        public List<FieldOption> getOptions(String fieldName, Map<String, String[]> requestParameterMap) {
            List<FieldOption> resultat = new ArrayList<>();
            if (ExpedientPeticioResource.AcceptarAnotacioForm.Fields.tipusDocument.equals(fieldName)){
                MetaExpedientEntity metaExpedientEntity = metaExpedientRepository.findById(Long.parseLong(requestParameterMap.get("metaExpedientId")[0])).get();
            	List<MetaDocumentEntity>  metaDocsPermesos = metaDocumentHelper.findMetaDocumentsDisponiblesPerCreacio(
            			metaExpedientEntity.getEntitat(),
                        null,
                        metaExpedientEntity,
                        false);
            	if (metaDocsPermesos!=null) {
            		//Rebem per parametre els ids dels metadocuments ja utilitzats per algun dels annexes
                    Map<String, String> additionalOption = parseToMap(requestParameterMap.get("annexos")[0]);
                    String annex = requestParameterMap.get("annex")[0];

            		for (MetaDocumentEntity metaDoc: metaDocsPermesos) {
                        if ( metaDoc.isMultiple() ||
                                (
                                    !additionalOption.containsValue(String.valueOf(metaDoc.getId())) ||
                                    String.valueOf(metaDoc.getId()).equals(additionalOption.get(annex))
                                )
                        ) {
                            resultat.add(new FieldOption(metaDoc.getId().toString(), metaDoc.getNom()));
                        }
            		}
                    resultat.sort(Comparator.comparing(FieldOption::getDescription));
            	}
            }
            return resultat;
        }

        @Override
		public void onChange(Serializable id, AcceptarAnotacioForm previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, AcceptarAnotacioForm target) {
            if (fieldName!=null){
                switch (fieldName){
                    case AcceptarAnotacioForm.Fields.metaExpedient:
                        if (fieldValue != null) {
                            ResourceReference<MetaExpedientResource, Long> reference =
                                    (ResourceReference<MetaExpedientResource, Long>) fieldValue;
                            Optional<MetaExpedientResourceEntity> metaExpedientResourceOptional =
                                    metaExpedientResourceRepository.findById(reference.getId());

                            metaExpedientResourceOptional.ifPresent((metaExpedientResourceEntity) -> {
                                MetaExpedientResource metaExpedientResource =
                                        objectMappingHelper.newInstanceMap(metaExpedientResourceEntity, MetaExpedientResource.class);

                                if (metaExpedientResource.isGestioAmbGrupsActiva()) {
                                    target.setGestioAmbGrupsActiva(true);
                                } else {
                                    target.setGestioAmbGrupsActiva(false);
                                    target.setGrup(null);
                                }

                                if (previous.getAny() != null) {
                                    Optional<Long> sequencia = metaExpedientSequenciaResourceRepository
                                            .findValorByMetaExpedientAndAny(metaExpedientResourceEntity, previous.getAny());

                                    sequencia.ifPresentOrElse(
                                            (value) -> target.setSequencia(value + 1),
                                            () -> target.setSequencia(1L)
                                    );
                                }

                                if (metaExpedientResource.getOrganGestor() != null) {
                                    target.setOrganGestor(metaExpedientResource.getOrganGestor());
                                    target.setDisableOrganGestor(true);
                                } else {
                                    //Procediment comú
                                    target.setDisableOrganGestor(false);
                                }
                            });
                        } else {
                            target.setGestioAmbGrupsActiva(false);
                            target.setOrganGestor(null);
                            target.setDisableOrganGestor(true);
                            target.setSequencia(null);
                        }
                        break;
                    case AcceptarAnotacioForm.Fields.any:
                        if (fieldValue != null && previous.getMetaExpedient() != null) {
                            Optional<MetaExpedientResourceEntity> metaExpedientResourceOptional =
                                    metaExpedientResourceRepository.findById(previous.getMetaExpedient().getId());

                            metaExpedientResourceOptional.ifPresent((metaExpedientResourceEntity) -> {
                                MetaExpedientResource metaExpedientResource =
                                        objectMappingHelper.newInstanceMap(metaExpedientResourceEntity, MetaExpedientResource.class);
                                if (metaExpedientResource.getOrganGestor() != null) {
                                    Optional<Long> sequencia = metaExpedientSequenciaResourceRepository
                                            .findValorByMetaExpedientAndAny(metaExpedientResourceEntity, (Integer) fieldValue);

                                    sequencia.ifPresentOrElse(
                                            (value) -> target.setSequencia(value + 1),
                                            () -> target.setSequencia(1L)
                                    );
                                }
                            });
                        } else {
                            target.setSequencia(null);
                        }
                        break;
                }
            } else {
                if (previous.getAny() != null) {
                    onChange(id, previous, AcceptarAnotacioForm.Fields.any, previous.getAny(), answers, previousFieldNames, target);
                }
                if (previous.getMetaExpedient() != null) {
                    onChange(id, previous, AcceptarAnotacioForm.Fields.metaExpedient, previous.getMetaExpedient(), answers, previousFieldNames, target);
                }
                comprovarExpedientReferenciat(id, target);
            }
        }

        /**
         * Si l'anotació porta informat un número d'expedient de referència, cerca l'expedient
         * amb aquest número dins el procediment de l'anotació. Si no el troba, deixa un missatge
         * d'avís al formulari perquè el front el mostri (equival al warning del JSP a
         * {@code ExpedientPeticioController.omplirModel}).
         */
        private void comprovarExpedientReferenciat(Serializable id, AcceptarAnotacioForm target) {
            if (id == null) {
                return;
            }
            ExpedientPeticioEntity expedientPeticio = expedientPeticioRepository.findById(Long.valueOf(id.toString())).orElse(null);
            if (expedientPeticio == null || expedientPeticio.getMetaExpedient() == null || expedientPeticio.getRegistre() == null) {
                return;
            }
            String expedientNumero = expedientPeticio.getRegistre().getExpedientNumero();
            if (expedientNumero == null || expedientNumero.isEmpty()) {
                return;
            }
            EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(configHelper.getEntitatActualCodi(), false, false, false, true, false);
            ExpedientEntity expedient = expedientRepository.findByEntitatAndMetaNodeAndNumero(
                    entitat,
                    expedientPeticio.getMetaExpedient(),
                    expedientNumero);
            if (expedient == null) {
                target.setExpedientNoTrobatMissatge(
                        messageHelper.getMessage("expedient.peticio.form.acceptar.expedient.noTorbat") + ": " + expedientNumero);
            }
        }

		@Override
		public Serializable exec(String code, ExpedientPeticioResourceEntity entity, AcceptarAnotacioForm params) throws ActionExecutionException {
			try {

				Long expedientPeticioId = entity.getId();
				String rolActual = configHelper.getRolActual();
                String entitatActualCodi = configHelper.getEntitatActualCodi();
                EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(entitatActualCodi, false, false, false, true,false);
                boolean expCreatArxiuOk = true;
                Long expedientId = null;

                Map<String, InteressatAssociacioAccioEnum> interessatsAccionsMap = new HashMap<>();
                if (params.getInteressats()!=null && entity.getRegistre().getInteressats()!=null) {
                	for(Long interessatId: params.getInteressats()) {
                		for(RegistreInteressatResourceEntity registreInteressatResourceEntity: entity.getRegistre().getInteressats()) {
                			if (registreInteressatResourceEntity.getId().equals(interessatId)) {
                				interessatsAccionsMap.put(registreInteressatResourceEntity.getDocumentNumero(), InteressatAssociacioAccioEnum.ASSOCIAR);
                				break;
                			}
                		}
                	}
                }

				if (ExpedientPeticioAccioEnumDto.CREAR.equals(params.getAccio())) {
	                /**
	                 * ExpedientServiceImpl.create
	                 */
					expedientId = expedientHelper.create(
							entitatEntity.getId(),
							params.getMetaExpedient().getId(),
							null,
							params.getOrganGestor().getId(),
                            params.getAny(),
							params.getNewExpedientTitol(),
							expedientPeticioId,
							params.isAssociarInteressats(),
							interessatsAccionsMap,
							entity.getGrup()!=null?entity.getGrup().getId():null,
							rolActual,
							params.getPrioritat(),
							params.getPrioritatMotiu(),
							params.isSeguidor()?SiNoEnumDto.SI:SiNoEnumDto.NO);
				} else {
	                /**
	                 * ExpedientServiceImpl.incorporar
	                 */
					expedientId = params.getExpedient().getId();
					expedientHelper.relateExpedientWithPeticioAndSetAnnexosPendentNewTransaction(
							expedientPeticioId,
							params.getExpedient().getId(),
							rolActual,
							entitatEntity.getId(),
							params.isAssociarInteressats(),
							interessatsAccionsMap,
							params.isAgafarExpedient(),
							false);
				}

				//Crea l'expedient a arxiu amb les metadades dels interessats.
				expCreatArxiuOk = expedientHelper.arxiuPropagarExpedientAmbInteressatsNewTransaction(expedientId);

				// Carpetes per defecte del procediment: només en l'acció de crear (no en incorporar a un
				// expedient existent) i només si l'expedient s'ha propagat correctament a l'Arxiu (ja té
				// UUID). Un error creant-les no ha de fer fallar el processament de l'anotació.
				if (expCreatArxiuOk && ExpedientPeticioAccioEnumDto.CREAR.equals(params.getAccio())) {
					try {
						expedientHelper.crearCarpetesMetaExpedientNewTransaction(entitatEntity.getId(), expedientId);
					} catch (Exception e) {
						excepcioLogHelper.addExcepcio("/expedient/" + expedientId + "/crearCarpetesMetaExpedient", e);
						log.error("No s'han pogut crear les carpetes per defecte de l'expedient " + expedientId, e);
					}
				}

				if (expCreatArxiuOk) {

					expedientHelper.inicialitzarExpedientsWithImportacio();

					for (Map.Entry<Long, String> entry : params.getAnnexos().entrySet()) {
						try {

							if (entry.getKey()>0) {

								//És un annex
								Exception errorMoguentAnnex = expedientHelper.crearDocFromAnnex(
										expedientId,
										entry.getKey(),
										expedientPeticioId,
										Long.parseLong(entry.getValue()),
										rolActual);

								if (errorMoguentAnnex!=null) {
									expedientHelper.updateRegistreAnnexError(entry.getKey(), ExceptionUtils.getStackTrace(errorMoguentAnnex));
								}

							} else {

								//És un justificant
								String arxiuUuid = entity.getRegistre().getJustificantArxiuUuid();
								if (arxiuUuid != null && configHelper.getAsBoolean(PropertyConfig.INCORPORAR_JUSTIFICANT)) {
									expedientHelper.crearDocFromUuid(
											expedientId,
											arxiuUuid,
											expedientPeticioId,
											Long.parseLong(entry.getValue()));
								}
							}

						} catch (Exception e) {
							expedientHelper.updateRegistreAnnexError(entry.getKey(), ExceptionUtils.getStackTrace(e));
						}
					}

					ExpedientPeticioEntity expedientPeticioEntity = expedientPeticioRepository.getOne(expedientPeticioId);
					try {
						expedientHelper.notificarICanviEstatToProcessatNotificat(expedientPeticioEntity);
					} catch (Exception e) {
						expedientPeticioEntity.setEstatCanviatDistribucio(false);
						expedientHelper.updateNotificarError(expedientPeticioEntity.getId(), ExceptionUtils.getStackTrace(e)); // this will be replaced by expedientPeticioEntity.setPendentCanviarEstatDistribucio(true, false);
					}

					expedientHelper.updateRegistresImportats(expedientId, expedientPeticioEntity.getIdentificador());

					try {
						eventHelper.notifyAnotacionsPendents(emailHelper.dadesUsuarisAfectatsAnotacio(expedientPeticioId));
					} catch (Exception ex) {}

				} else {
					//Si ha donat error arxiu, marcam els annexos com a pendents
	                if (params.getAnnexos()!=null) {
	                	for (Map.Entry<Long, String> entry : params.getAnnexos().entrySet()) {
	                		registreAnnexResourceRepository.findById(entry.getKey()).get().setError(
	                				"Annex no processat perque l'expedient no s'ha creat a l'Arxiu");
	                	}
	                }
				}

				return objectMappingHelper.newInstanceMap(entity, ExpedientPeticioResource.class);

			} catch (Exception e) {
				excepcioLogHelper.addExcepcio("/anotacio/"+entity.getId()+"/AcceptarAnotacioActionExecutor", e);
				throw new ActionExecutionException(getResourceClass(), entity.getId(), code, messageHelper.getMessage("expedientPeticio.acceptarAnotacio.reject", new Object[]{e.getMessage()}));
			}
		}
    }

    private class SubsanarAnnexosActionExecutor implements ActionExecutor<ExpedientPeticioResourceEntity, SubsanarAnnexosForm, Serializable> {

        private Map<String, String> parseToMap(String input) {
            String[] tokens = input.split(",", -1); // split preservant buits
            Map<String, String> map = new HashMap<>();
            for (int i = 0; i < tokens.length - 1; i += 2) {
                map.put(tokens[i].trim(), tokens[i + 1].trim());
            }
            return map;
        }

        @Override
        public List<FieldOption> getOptions(String fieldName, Map<String, String[]> requestParameterMap) {
            List<FieldOption> resultat = new ArrayList<>();
            if (SubsanarAnnexosForm.Fields.tipusDocument.equals(fieldName)) {

                String entitatActualCodi = configHelper.getEntitatActualCodi();
                EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatActualCodi, false, false, false, true, false);

                String annex = requestParameterMap.get("annex")[0];
                RegistreAnnexResourceEntity annexEntity = registreAnnexResourceRepository.findById(Long.parseLong(annex)).orElse(null);

                //L'expedient ja existeix (anotació acceptada). Si l'annex ja té document creat (només va fallar el
                //moviment a l'Arxiu) s'usen els meta-documents per a modificació, que inclouen el tipus actual del
                //document encara que la multiplicitat l'excluiria. Si no, els disponibles per creació dins l'expedient.
                List<MetaDocumentEntity> metaDocsPermesos;
                if (annexEntity != null && annexEntity.getDocument() != null) {
                    metaDocsPermesos = metaDocumentHelper.findActiusPerModificacio(entitat, annexEntity.getDocument().getId());
                } else {
                    Long expedientId = Long.parseLong(requestParameterMap.get("expedientId")[0]);
                    metaDocsPermesos = metaDocumentHelper.findActiusPerCreacio(entitat, expedientId, null, false);
                }

                if (metaDocsPermesos != null) {
                    //Rebem per paràmetre els ids dels meta-documents ja seleccionats per algun dels annexos
                    Map<String, String> additionalOption = parseToMap(requestParameterMap.get("annexos")[0]);

                    for (MetaDocumentEntity metaDoc : metaDocsPermesos) {
                        if (metaDoc.isMultiple() ||
                                (
                                    !additionalOption.containsValue(String.valueOf(metaDoc.getId())) ||
                                    String.valueOf(metaDoc.getId()).equals(additionalOption.get(annex))
                                )
                        ) {
                            resultat.add(new FieldOption(metaDoc.getId().toString(), metaDoc.getNom()));
                        }
                    }
                    resultat.sort(Comparator.comparing(FieldOption::getDescription));
                }
            }
            return resultat;
        }

        @Override
        public void onChange(Serializable id, SubsanarAnnexosForm previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, SubsanarAnnexosForm target) {}

        @Override
        public Serializable exec(String code, ExpedientPeticioResourceEntity entity, SubsanarAnnexosForm params) throws ActionExecutionException {
            String rolActual = configHelper.getRolActual();

            //Si el expedient no s'ha creat al arxiu, ha de crear-se abans de res.
            if (entity.getExpedient().getArxiuUuid()==null) {
            	expedientHelper.guardarExpedientArxiu(entity.getExpedient().getId());
            }

            Exception darrerError = null;
            for (Map.Entry<Long, String> entry : params.getAnnexos().entrySet()) {
                Long registreAnnexId = entry.getKey();
                Long metaDocumentId = (entry.getValue() != null && !entry.getValue().isEmpty()) ? Long.parseLong(entry.getValue()) : null;
                try {
                    //Per als annexos que ja tenen document es modifica el tipus abans de tornar a intentar el moviment.
                    Exception ex = expedientHelper.retryCreateDocFromAnnex(registreAnnexId, metaDocumentId, rolActual, true);
                    if (ex != null) {
                        darrerError = ex;
                    }
                } catch (Exception e) {
                    //Cada annex es processa de manera aïllada: un error no atura la resta.
                    darrerError = e;
                }
            }
            if (darrerError != null) {
                excepcioLogHelper.addExcepcio("/anotacio/" + entity.getId() + "/SubsanarAnnexosActionExecutor", darrerError);
                String message = messageHelper.getMessage("message.common.action.error") + ": " + ExceptionUtils.getRootCauseMessage(darrerError);
                throw new ActionExecutionException(getResourceClass(), entity.getId(), code, message);
            }
            return objectMappingHelper.newInstanceMap(entity, ExpedientPeticioResource.class);
        }
    }

    private class RebutjarAnotacioActionExecutor implements ActionExecutor<ExpedientPeticioResourceEntity, ExpedientPeticioResource.RebutjarAnotacioForm, Serializable> {

		@Override
		public void onChange(Serializable id, RebutjarAnotacioForm previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, RebutjarAnotacioForm target) {}

		@Override
		public Serializable exec(String code, ExpedientPeticioResourceEntity entity, RebutjarAnotacioForm params) throws ActionExecutionException {
			try {
				expedientPeticioHelper.rebutjar(entity.getId(), params.getMotiu());
				try {
					eventHelper.notifyAnotacionsPendents(entity.getId());
				} catch (Exception ex) {}
				return objectMappingHelper.newInstanceMap(entity, ExpedientPeticioResource.class);
			} catch (Exception e) {
				excepcioLogHelper.addExcepcio("/anotacio/"+entity.getId()+"/RebutjarAnotacioActionExecutor", e);
				throw new ActionExecutionException(getResourceClass(), entity.getId(), code, messageHelper.getMessage("expedientPeticio.rebutjarAnotacio.reject", new Object[]{e.getMessage()}));
			}
		}
    }

    private class ConsultarGuardarAnotacioPendentActionExecutor implements ActionExecutor<ExpedientPeticioResourceEntity, MassiveAction, Serializable> {

		@Override
		public void onChange(Serializable id, MassiveAction previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, MassiveAction target) {}

		@Override
		public Serializable exec(String code, ExpedientPeticioResourceEntity entity, MassiveAction params) throws ActionExecutionException {
			try {
				for (Long petId: params.getIds()) {
					anotacioDistribucioHelper.consultarIGuardarAnotacioPeticioPendent(petId, true);
				}
				int numElem = params!=null && params.getIds()!=null?params.getIds().size():0;
				return "{\"num\": \""+numElem+"\"}";
			} catch (Exception e) {
				String ids = Utils.getIdsSeparatsComa(params.getIds());
				excepcioLogHelper.addExcepcio("/anotacio/ConsultarGuardarAnotacioPendentActionExecutor", e, ids, "massiu="+params.isMassivo());
				String message = messageHelper.getMessage("message.common.action.error")+": "+e.getMessage();
				throw new ActionExecutionException(getResourceClass(), ids, code, message);
			} catch (Throwable e) {
				String ids = Utils.getIdsSeparatsComa(params.getIds());
				excepcioLogHelper.addExcepcio("/anotacio/ConsultarGuardarAnotacioPendentActionExecutor", e, ids, "massiu="+params.isMassivo());
				String message = messageHelper.getMessage("message.common.action.error")+": "+e.getMessage();
				throw new ActionExecutionException(getResourceClass(), ids, code, message);
			}
		}
    }

    private class CanviEstatDistribucioActionExecutor implements ActionExecutor<ExpedientPeticioResourceEntity, MassiveAction, Serializable> {

		@Override
		public void onChange(Serializable id, MassiveAction previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, MassiveAction target) {}

		@Override
		public Serializable exec(String code, ExpedientPeticioResourceEntity entity, MassiveAction params) throws ActionExecutionException {
			try {
				if (!params.isMassivo()) {
					//En teoria només hauria de arribar un element
					Exception exCanviEstat = expedientPeticioHelper.reintentarCanviEstatDistribucio(params.getIds().get(0));

					if (exCanviEstat!=null) {
						String ids = Utils.getIdsSeparatsComa(params.getIds());
						excepcioLogHelper.addExcepcio("/anotacio/CanviEstatDistribucioActionExecutor", exCanviEstat, ids, "massiu="+params.isMassivo());
						String message = messageHelper.getMessage("message.common.action.error")+": "+exCanviEstat.getMessage();
						throw new ActionExecutionException(getResourceClass(), ids, code, message);
					}

				} else {
					ExecucioMassivaDto dto = new ExecucioMassivaDto();
					dto.setTipus(ExecucioMassivaTipusDto.ACTUALITZAR_ESTAT_ANOTACIONS);
					dto.setContingutIds(params.getIds());
					dto.setRolActual(configHelper.getRolActual());

			        String entitatActualCodi = configHelper.getEntitatActualCodi();
			        EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatActualCodi, false, false, false, true,false);

			        execucioMassivaHelper.crearExecucioMassiva(entitat.getId(), dto, ElementTipusEnumDto.ANOTACIO);
				}

				int numElem = params!=null && params.getIds()!=null?params.getIds().size():0;
				return "{\"num\": \""+numElem+"\"}";

			} catch (Exception e) {
				String ids = Utils.getIdsSeparatsComa(params.getIds());
				excepcioLogHelper.addExcepcio("/anotacio/CanviEstatDistribucioActionExecutor", e, ids, "massiu="+params.isMassivo());
				String message = messageHelper.getMessage("message.common.action.error")+": "+e.getMessage();
				throw new ActionExecutionException(getResourceClass(), ids, code, message);
			}
		}
    }

    private class DescarregarJustificantReportGenerator implements ReportGenerator<ExpedientPeticioResourceEntity, Serializable, Serializable> {

    	@Override
		public DownloadableFile generateFile(String code, List<?> data, ReportFileType fileType, OutputStream out) {

    		try {

            	Document documentDetalls = pluginHelper.arxiuDocumentConsultar(
    					null,
    					data.get(0).toString(),
    					null,
    					true,
    					false);

        		String extensio = documentDetalls.getMetadades().getExtensio()!=null?documentDetalls.getMetadades().getExtensio().toString():".pdf";

                return new DownloadableFile(
            			documentDetalls.getNom()+extensio,
            			documentDetalls.getContingut().getTipusMime(),
            			documentDetalls.getContingut().getContingut());

			} catch (Exception e) {
				excepcioLogHelper.addExcepcio("/expedientPeticio/"+data.get(1)+"/DescarregarJustificantReportGenerator", e);
				throw new ReportGenerationException(getResourceClass(), data.get(1).toString(), code, "expedientPeticio.justificant.reject");
			}
		}

		@Override
		public List<Serializable> generateData(String code, ExpedientPeticioResourceEntity entity, Serializable params) throws ReportGenerationException {
			List<Serializable> parametres = new ArrayList<Serializable>();
			parametres.add(entity.getRegistre().getJustificantArxiuUuid());
			parametres.add(entity.getId());
			return parametres;
		}

		@Override
		public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, Serializable target) {}
    }
}
