package es.caib.ripea.service.resourceservice;

import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import es.caib.ripea.persistence.entity.ExpedientPeticioEntity;
import es.caib.ripea.persistence.entity.MetaDocumentEntity;
import es.caib.ripea.persistence.entity.MetaExpedientEntity;
import es.caib.ripea.persistence.entity.OrganGestorEntity;
import es.caib.ripea.persistence.entity.resourceentity.ExpedientPeticioResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.MetaExpedientResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.RegistreInteressatResourceEntity;
import es.caib.ripea.persistence.entity.resourcerepository.MetaExpedientResourceRepository;
import es.caib.ripea.persistence.entity.resourcerepository.MetaExpedientSequenciaResourceRepository;
import es.caib.ripea.persistence.entity.resourcerepository.RegistreAnnexResourceRepository;
import es.caib.ripea.persistence.repository.ExpedientPeticioRepository;
import es.caib.ripea.persistence.repository.MetaExpedientRepository;
import es.caib.ripea.persistence.repository.OrganGestorRepository;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.helper.ConfigHelper;
import es.caib.ripea.service.helper.EmailHelper;
import es.caib.ripea.service.helper.EntityComprovarHelper;
import es.caib.ripea.service.helper.EventHelper;
import es.caib.ripea.service.helper.ExcepcioLogHelper;
import es.caib.ripea.service.helper.ExpedientHelper;
import es.caib.ripea.service.helper.ExpedientPeticioHelper;
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
import es.caib.ripea.service.intf.dto.ExpedientPeticioAccioEnumDto;
import es.caib.ripea.service.intf.dto.ExpedientPeticioEstatViewEnumDto;
import es.caib.ripea.service.intf.dto.InteressatAssociacioAccioEnum;
import es.caib.ripea.service.intf.dto.NtiTipoDocumentoEnumDto;
import es.caib.ripea.service.intf.model.ExpedientPeticioResource;
import es.caib.ripea.service.intf.model.ExpedientPeticioResource.AcceptarAnotacioForm;
import es.caib.ripea.service.intf.model.ExpedientPeticioResource.RebutjarAnotacioForm;
import es.caib.ripea.service.intf.model.MetaExpedientResource;
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

	private final OrganGestorRepository organGestorRepository;
	private final MetaExpedientRepository metaExpedientRepository;
	private final ExpedientPeticioRepository expedientPeticioRepository;

	private final RegistreAnnexResourceRepository registreAnnexResourceRepository;

	private final MetaExpedientSequenciaResourceRepository metaExpedientSequenciaResourceRepository;
	private final MetaExpedientResourceRepository metaExpedientResourceRepository;

    @PostConstruct
    public void init() {
        register(ExpedientPeticioResource.PERSPECTIVE_REGISTRE_CODE, new RegistrePerspectiveApplicator());
        register(ExpedientPeticioResource.PERSPECTIVE_ESTAT_VIEW_CODE, new EstatViewPerspectiveApplicator());
        register(ExpedientPeticioResource.REPORT_DOWNLOAD_JUSTIFICANT, new DescarregarJustificantReportGenerator());
        register(ExpedientPeticioResource.ACTION_REBUTJAR_ANOTACIO, new RebutjarAnotacioActionExecutor());
        register(ExpedientPeticioResource.ACTION_ACCEPTAR_ANOTACIO, new AcceptarAnotacioActionExecutor());
        register(ExpedientPeticioResource.ACTION_ESTAT_DISTRIBUCIO, new CanviEstatDistribucioActionExecutor());        
        register(ExpedientPeticioResource.Fields.metaExpedient, new MetaExpedientOnchangeLogicProcessor());
    }

    @Override
    protected String additionalSpringFilter(String currentSpringFilter, String[] namedQueries) {
    	
        String entitatActualCodi = configHelper.getEntitatActualCodi();
        EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatActualCodi, false, false, false, true,false);
        
		Filter filtreBase = (currentSpringFilter != null && !currentSpringFilter.isEmpty())?Filter.parse(currentSpringFilter):null;
        Filter filtreEntitat = FilterBuilder.equal(
        		ExpedientPeticioResource.Fields.registre + "." + RegistreResource.Fields.entitatCodi, 
        		entitat!=null?entitat.getUnitatArrel():"................................................................................");
        
        Filter filtrePermesos = null;
        Map<String, String> mapaNamedQueries =  Utils.namedQueriesToMap(namedQueries);
    	if (mapaNamedQueries.size()>0) {
    		if (mapaNamedQueries.containsKey("LLISTAT_ANOTACIONS")) {
    			
    			String organActualCodi	 = configHelper.getOrganActualCodi();
    			String rolActual		 = configHelper.getRolActual();
    			
    			boolean isAdmin 		= "IPA_ADMIN".equals(rolActual);
    			boolean isAdminOrgan 	= "IPA_ORGAN_ADMIN".equals(rolActual);
//    			boolean isDissenyOrgan 	= "IPA_DISSENY".equals(rolActual);
//    			boolean isSuper 		= "IPA_SUPER".equals(rolActual);

    			//Admin no aplica filtres de permisos
    			if (!isAdmin) {
    			
	    			OrganGestorEntity ogEntity	= organGestorRepository.findByEntitatIdAndCodi(entitat.getId(), organActualCodi);
					PermisosPerAnotacions permisosPerAnotacions = expedientPeticioHelper.findPermisosPerAnotacions(
							entitat.getId(),
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
				        
				        filtrePermesos = filtreOrgansPermesos;
						
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
				        
				        filtrePermesos = FilterBuilder.and(filtreProcedimentsPermesos, filterGEstioGrupsActius);
					}
    			}
    		}
    	}
        
        return FilterBuilder.and(filtreBase, filtreEntitat, filtrePermesos).generate();
    }
    
    @Override
	public ExpedientPeticioResource update(Long id, ExpedientPeticioResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) throws ResourceNotFoundException {
    	try {
    		expedientPeticioHelper.canviarProcediment(resource.getId(), resource.getMetaExpedient().getId(), resource.getGrup().getId());
    	} catch (Exception ex) {
    		excepcioLogHelper.addExcepcio("/anotacio/"+resource.getId()+"/update", ex);
    	}
    	return null;
    }
    
    private class MetaExpedientOnchangeLogicProcessor implements OnChangeLogicProcessor<ExpedientPeticioResource> {
		@Override
		public void onChange(Serializable id, ExpedientPeticioResource previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, ExpedientPeticioResource target) {
			if (fieldValue != null) {
				
			} else {
				target.setGrup(null);
			}
		}
    }
    
    private class RegistrePerspectiveApplicator implements PerspectiveApplicator<ExpedientPeticioResourceEntity, ExpedientPeticioResource> {
        @Override
        public void applySingle(String code, ExpedientPeticioResourceEntity entity, ExpedientPeticioResource resource) throws PerspectiveApplicationException {
            resource.setRegistreInfo(objectMappingHelper.newInstanceMap(Hibernate.unproxy(entity.getRegistre()), RegistreResource.class));

            resource.getRegistreInfo().setInteressats(
                    entity.getRegistre().getInteressats().stream()
                            .map(interessat -> {
                                RegistreInteressatResource interessatResource = objectMappingHelper.newInstanceMap(interessat, RegistreInteressatResource.class);
                                return ResourceReference.<RegistreInteressatResource, Long>toResourceReference(interessatResource.getId(), interessatResource.getCodiNom());
                            })
                            .collect(Collectors.toList())
            );

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
                String entitatActualCodi = configHelper.getEntitatActualCodi();
                EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatActualCodi, false, false, false, true,false);
                MetaExpedientEntity metaExpedientEntity = metaExpedientRepository.findById(Long.parseLong(requestParameterMap.get("metaExpedientId")[0])).get();
            	List<MetaDocumentEntity>  metaDocsPermesos = metaDocumentHelper.findMetaDocumentsDisponiblesPerCreacio(
            			entitat,
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
                                if (metaExpedientResource.getOrganGestor() != null) {
                                    target.setOrganGestor(metaExpedientResource.getOrganGestor());
                                    if (previous.getAny() != null) {
                                        Optional<Long> sequencia = metaExpedientSequenciaResourceRepository
                                                .findValorByMetaExpedientAndAny(metaExpedientResourceEntity, previous.getAny());

                                        sequencia.ifPresentOrElse(
                                                (value) -> target.setSequencia(value + 1),
                                                () -> target.setSequencia(1L)
                                        );
                                    }
                                }
                            });
                        } else {
                            target.setOrganGestor(null);
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

                Map<Long, Long> anexosIdsMetaDocsIdsMap = new HashMap<Long, Long>();
                if (params.getAnnexos()!=null) {
                	for (Map.Entry<Long, String> entry : params.getAnnexos().entrySet()) {
                		anexosIdsMetaDocsIdsMap.put(entry.getKey(), Long.parseLong(entry.getValue()));
                	}
                }

                Map<String, InteressatAssociacioAccioEnum> interessatsAccionsMap = new HashMap<>();
                if (params.getInteressats()!=null && entity.getRegistre().getInteressats()!=null) {
                	for(Long interessatId: params.getInteressats()) {
                		for(RegistreInteressatResourceEntity registreInteressatResourceEntity: entity.getRegistre().getInteressats()) {
                			if (registreInteressatResourceEntity.getId().equals(interessatId)) {
                				interessatsAccionsMap.put(registreInteressatResourceEntity.getDocumentNumero(), InteressatAssociacioAccioEnum.ASSOCIAR);
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
							params.getPrioritatMotiu());
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
							params.isAgafarExpedient());
				}

				/**
				 * Accions comunes, tant per l'acció de crear com de importar.
				 */
				expCreatArxiuOk = expedientHelper.arxiuPropagarExpedientAmbInteressatsNewTransaction(expedientId);

				if (expCreatArxiuOk) {

					expedientHelper.inicialitzarExpedientsWithImportacio();

					for (Map.Entry<Long, String> entry : params.getAnnexos().entrySet()) {
						try {
							expedientHelper.crearDocFromAnnex(
									expedientId,
									entry.getKey(),
									expedientPeticioId,
									Long.parseLong(entry.getValue()),
									rolActual);

						} catch (Exception e) {
							expedientHelper.updateRegistreAnnexError(
									entry.getKey(),
									ExceptionUtils.getStackTrace(e));
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
	                if (params.getAnnexos()!=null) {
	                	for (Map.Entry<Long, String> entry : params.getAnnexos().entrySet()) {
	                		registreAnnexResourceRepository.findById(entry.getKey()).get().setError(
	                				"Annex no s'ha processat perque l'expedient no s'ha creat en arxiu");
	                	}
	                }
				}

				return objectMappingHelper.newInstanceMap(entity, ExpedientPeticioResource.class);

			} catch (Exception e) {
				excepcioLogHelper.addExcepcio("/anotacio/"+entity.getId()+"/AcceptarAnotacioActionExecutor", e);
				throw new ActionExecutionException(getResourceClass(), entity.getId(), code, "Error al acceptar la anotació: "+e.getMessage());
			}
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
				throw new ActionExecutionException(getResourceClass(), entity.getId(), code, "Error al rebutjar la anotació: "+e.getMessage());
			}
		}
    }
    
    private class CanviEstatDistribucioActionExecutor implements ActionExecutor<ExpedientPeticioResourceEntity, Serializable, Serializable> {

		@Override
		public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, Serializable target) {}

		@Override
		public Serializable exec(String code, ExpedientPeticioResourceEntity entity, Serializable params) throws ActionExecutionException {
			try {
				expedientPeticioHelper.reintentarCanviEstatDistribucio(entity.getId());
				return objectMappingHelper.newInstanceMap(entity, ExpedientPeticioResource.class);
			} catch (Exception e) {
				excepcioLogHelper.addExcepcio("/anotacio/"+entity.getId()+"/RebutjarAnotacioActionExecutor", e);
				throw new ActionExecutionException(getResourceClass(), entity.getId(), code, "Error al rebutjar la anotació: "+e.getMessage());
			}
		}
    }
    
    private class DescarregarJustificantReportGenerator implements ReportGenerator<ExpedientPeticioResourceEntity, Serializable, Serializable> {

    	@Override
		public DownloadableFile generateFile(String code, List<?> data, ReportFileType fileType, OutputStream out) {
    		
    		DownloadableFile resultat = null;
    		
    		try {		
	    		
            	Document documentDetalls = pluginHelper.arxiuDocumentConsultar(
    					null, 
    					data.get(0).toString(), 
    					null, 
    					true, 
    					false);
            	
        		String extensio = documentDetalls.getMetadades().getExtensio()!=null?documentDetalls.getMetadades().getExtensio().toString():".pdf";
            	
            	resultat = new DownloadableFile(
            			documentDetalls.getNom()+extensio,
            			documentDetalls.getContingut().getTipusMime(),
            			documentDetalls.getContingut().getContingut());

			} catch (Exception e) {
				excepcioLogHelper.addExcepcio("/expedientPeticio/"+data.get(1)+"/DescarregarJustificantReportGenerator", e);
				throw new ReportGenerationException(getResourceClass(), data.get(1).toString(), code, "S'ha produit un error al descarregar el jsutificant del registre.");
			}
            
            return resultat;
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