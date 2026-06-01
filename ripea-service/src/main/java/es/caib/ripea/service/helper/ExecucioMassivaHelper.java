package es.caib.ripea.service.helper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import es.caib.ripea.persistence.entity.ContingutEntity;
import es.caib.ripea.persistence.entity.DocumentEntity;
import es.caib.ripea.persistence.entity.DocumentNotificacioEntity;
import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.ExecucioMassivaContingutEntity;
import es.caib.ripea.persistence.entity.ExecucioMassivaEntity;
import es.caib.ripea.persistence.entity.ExpedientEntity;
import es.caib.ripea.persistence.entity.InteressatEntity;
import es.caib.ripea.persistence.entity.MetaDocumentEntity;
import es.caib.ripea.persistence.entity.RegistreAnnexEntity;
import es.caib.ripea.persistence.repository.ContingutRepository;
import es.caib.ripea.persistence.repository.DocumentNotificacioRepository;
import es.caib.ripea.persistence.repository.ExecucioMassivaContingutRepository;
import es.caib.ripea.persistence.repository.ExecucioMassivaRepository;
import es.caib.ripea.persistence.repository.ExpedientPeticioRepository;
import es.caib.ripea.persistence.repository.InteressatRepository;
import es.caib.ripea.persistence.repository.MetaDocumentRepository;
import es.caib.ripea.persistence.repository.RegistreAnnexRepository;
import es.caib.ripea.service.firma.DocumentFirmaPortafirmesHelper;
import es.caib.ripea.service.intf.base.model.DownloadableFile;
import es.caib.ripea.service.intf.config.PropertyConfig;
import es.caib.ripea.service.intf.dto.ContingutTipusEnumDto;
import es.caib.ripea.service.intf.dto.DocumentAmbTipusDto;
import es.caib.ripea.service.intf.dto.DocumentDto;
import es.caib.ripea.service.intf.dto.DocumentFirmaTipusEnumDto;
import es.caib.ripea.service.intf.dto.DocumentTipusEnumDto;
import es.caib.ripea.service.intf.dto.ElementTipusEnumDto;
import es.caib.ripea.service.intf.dto.ExecucioMassivaContingutDto;
import es.caib.ripea.service.intf.dto.ExecucioMassivaDto;
import es.caib.ripea.service.intf.dto.ExecucioMassivaEstatDto;
import es.caib.ripea.service.intf.dto.ExecucioMassivaTipusDto;
import es.caib.ripea.service.intf.dto.FileNameOption;
import es.caib.ripea.service.intf.dto.FitxerDto;
import es.caib.ripea.service.intf.dto.MetaDocumentDto;
import es.caib.ripea.service.intf.dto.SignatureInfoDto;
import es.caib.ripea.service.intf.exception.ArxiuJaGuardatException;
import es.caib.ripea.service.intf.exception.NotFoundException;
import es.caib.ripea.service.intf.exception.ValidationException;
import es.caib.ripea.service.intf.model.ExpedientResource.ExportGenericForm;
import es.caib.ripea.service.intf.utils.Utils;

@Component
public class ExecucioMassivaHelper {

	@Autowired private ExecucioMassivaRepository execucioMassivaRepository;
	@Autowired private ExecucioMassivaContingutRepository execucioMassivaContingutRepository;
	@Autowired private InteressatRepository interessatRepository;
	@Autowired private RegistreAnnexRepository registreAnnexRepository;
	@Autowired private ExpedientPeticioRepository expedientPeticioRepository;
	@Autowired private ContingutRepository contingutRepository;
	@Autowired private MetaDocumentRepository metaDocumentRepository;
	@Autowired private DocumentNotificacioRepository documentNotificacioRepository;

	@Autowired private AlertaHelper alertaHelper;
	@Autowired private MessageHelper messageHelper;
	@Autowired private PluginHelper pluginHelper;
	@Autowired private ConfigHelper configHelper;
	@Autowired private DocumentFirmaPortafirmesHelper firmaPortafirmesHelper;
	@Autowired private OrganGestorHelper organGestorHelper;
	@Autowired private ExpedientHelper expedientHelper;
	@Autowired private EntityComprovarHelper entityComprovarHelper;
	@Autowired private ExpedientInteressatHelper expedientInteressatHelper;
	@Autowired private DocumentHelper documentHelper;
	@Autowired private ContingutHelper contingutHelper;
	@Autowired private ExpedientPeticioHelper expedientPeticioHelper;
	@Autowired private DocumentNotificacioHelper documentNotificacioHelper;

	public FitxerDto descarregarDocumentExecMassiva(Long entitatId, Long execMassivaId) {
		ExecucioMassivaEntity execucioMassiva = execucioMassivaRepository.findById(execMassivaId).orElse(null);
		if (execucioMassiva!=null && execucioMassiva.getDocumentNom()!=null) {
			FitxerDto resultat = new FitxerDto();
			String directoriDesti = configHelper.getConfig(PropertyConfig.APP_DATA_DIR) + execucioMassiva.getDocumentNom();
            try {
                byte[] bytes = Files.readAllBytes(Paths.get(directoriDesti));
				resultat.setContingut(bytes);
				if (execucioMassiva.getDocumentNom().lastIndexOf("/")>0) {
					resultat.setNom(execucioMassiva.getDocumentNom().substring(execucioMassiva.getDocumentNom().lastIndexOf("/")));
				} else {
					resultat.setNom(execucioMassiva.getDocumentNom());
				}
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
			return resultat;
		}
		return null;
	}
	
	public DownloadableFile getDownloadableFileFromExport(Long entitatId, ExportGenericForm params) throws IOException {
    	
		List<FitxerDto> fitxersGenerats = new ArrayList<FitxerDto>();
		DownloadableFile resultat = null;
				
		if (params.isExportarExcel()) {
			fitxersGenerats.add(expedientHelper.exportacio(entitatId, new HashSet<>(params.getIds()), "ODS"));
		}
		
		if (params.isExportarCsv()) {
			fitxersGenerats.add(expedientHelper.exportacio(entitatId, new HashSet<>(params.getIds()), "CSV"));
		}
		
    	if (params.isExportarIndexXls()) {
    		fitxersGenerats.add(expedientHelper.generarIndexExpedients(
    				entitatId,
    				new HashSet<>(params.getIds()),
    				false,
    				"XLSX"));
    	}
    	
    	//Seria redundant exportar index PDF haguent marcat la opció de ExportarIndexPdfEni
    	if (params.isExportarIndexPdf()) {
    		fitxersGenerats.add(expedientHelper.generarIndexExpedients(
    				entitatId,
    				new HashSet<>(params.getIds()),
    				false,
    				"PDF"));
    	}
    	
    	if (params.isInlourerEstructEni()) {
    		fitxersGenerats.add(expedientHelper.generarIndexExpedients(
    				entitatId,
    				new HashSet<>(params.getIds()),
    				true,
    				"PDF"));
    	}

    	if (params.isExportarIndexZip()) {
    		fitxersGenerats.add(expedientHelper.generarIndexExpedients(
    				entitatId,
    				Utils.listToSet(params.getIds()),
    				false,
    				"ZIP"));
    	}

    	//Seria redundant exportar ENI haguent marcat la opció de ExportarIndexPdfEni
    	if (params.isExportarEni() && !params.isInlourerEstructEni()) {
    		fitxersGenerats.add(expedientHelper.exportarExpedient(new HashSet<>(params.getIds()), false));
    	}

    	if (params.isExportarInside()) {
    		fitxersGenerats.add(expedientHelper.exportarExpedient(new HashSet<>(params.getIds()), true));
    	}
    	
    	if (fitxersGenerats.size()==1) {
        	resultat = new DownloadableFile(
        			fitxersGenerats.get(0).getNom(),
        			fitxersGenerats.get(0).getContentType(),
        			fitxersGenerats.get(0).getContingut());
    	} else {
    		
    	    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    	    try (ZipOutputStream zipOut = new ZipOutputStream(baos)) {

    	        for (FitxerDto fitxer : fitxersGenerats) {

    	            if (fitxer.getContingut() == null || fitxer.getNom() == null) {
    	                continue; // Saltar fitxers invàlids
    	            }

    	            ZipEntry zipEntry = new ZipEntry(fitxer.getNom());
    	            zipEntry.setSize(fitxer.getContingut().length);
    	            zipOut.putNextEntry(zipEntry);
    	            zipOut.write(fitxer.getContingut());
    	            zipOut.closeEntry();
    	        }

    	    } // El try-with-resources tanca i finalitza el ZipOutputStream aquí
    	    
        	resultat = new DownloadableFile(
        			"Exportacio_expedient_"+params.getIds().get(0)+".zip",
        			"application/zip",
        			baos.toByteArray());
    	}
    	
    	return resultat;
	}
	
	public ByteArrayOutputStream getZipFromDocuments(List<DocumentDto> docsExp) throws IOException {
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ZipOutputStream out = new ZipOutputStream(baos);
		List<String> nomsArxius = new ArrayList<String>();
		ZipEntry ze;
		
		if (docsExp != null && docsExp.size() > 0) {
			for (DocumentDto documentDto : docsExp) {
				String recursNom = getZipRecursNom(documentDto.getFitxerNom(), nomsArxius);
				ze = new ZipEntry(recursNom);
				out.putNextEntry(ze);
				if (documentDto.getFitxerContingut()!=null) {
					out.write(documentDto.getFitxerContingut());
				}
				out.closeEntry();
			}
		}

		out.close();
		return baos;
	}
	
	private String getZipRecursNom(String nomEntrada, List<String> nomsArxius) {
		int contador = 0;
		for (String nom : nomsArxius) {
			if (nom!=null && nom.equals(nomEntrada)) {
				contador++;
			}
		}
		//Guardam al llistat de noms de documents abans de modificar-lo amb un contador.
		nomsArxius.add(nomEntrada+""); //Ens asseguram que es guarda un nou string, no un apuntador.
		if (contador > 0) {
			nomEntrada = nomEntrada.substring(0, nomEntrada.lastIndexOf(".")) +
					" (" + contador + ")" +
					nomEntrada.substring(nomEntrada.lastIndexOf("."));
		}
		return nomEntrada;
	}
	
	public List<DocumentDto> getDocumentsForExportacioZip(
			ExpedientEntity expedient,
			FileNameOption nomFitxer,
			boolean isVersioImprimible,
			boolean carpetes,
			double actualMbFitxer,
			List<Long> documentsInclosos) {
		
		List<ContingutEntity> contingutsExp = new ArrayList<>();
		List<DocumentDto> docsExp = new ArrayList<DocumentDto>();
		contingutHelper.findDescendants(expedient, contingutsExp, true, false);

		if (contingutsExp != null && contingutsExp.size()>0) {

			for (ContingutEntity contingutExpedient : contingutsExp) {
				
				if (ContingutTipusEnumDto.DOCUMENT.equals(contingutExpedient.getTipus())) {
					
					if (documentsInclosos==null || documentsInclosos.contains(contingutExpedient.getId())) {
						
						DocumentEntity documentEntity = (DocumentEntity)contingutExpedient;
	
						DocumentDto doc = new DocumentDto();
						doc.setId(documentEntity.getId());
	
						FitxerDto fitxerDto = null;
						
						//Configuració de l'execució massiva: Versió imprimible o original del fitxer
						if (isVersioImprimible) {
							try {
								//La versió imprimible pot no estar disponible si el document no esta definitiu (signat)
								fitxerDto = pluginHelper.arxiuDocumentVersioImprimible(documentEntity);
							} catch (Exception notImpr) {
								//Si no té uuid l'intentará obtenir el gestorDocumental amb el identificador "getGesDocAdjuntId"
								fitxerDto = documentHelper.getFitxerAssociat(documentEntity, null);
							}
						} else {
							//Si no té uuid l'intentará obtenir el gestorDocumental amb el identificador "getGesDocAdjuntId"
							fitxerDto = documentHelper.getFitxerAssociat(documentEntity, null);
						}
	
						actualMbFitxer = actualMbFitxer + (fitxerDto.getContingut().length / (1024.0 * 1024.0));
	
						doc.setFitxerContingut(fitxerDto.getContingut());
	
						String nomDoc = null;
						//Configuració de l'execució massiva: Nom del fitxer dins el ZIP
						if (nomFitxer!=null) {
							switch (nomFitxer) {
							case TITLE:
								nomDoc = documentEntity.getNom().replaceAll("/", "_");
								nomDoc = nomDoc + documentEntity.getExtensio();
								break;
							case TYPE_TITLE:
								nomDoc = documentEntity.getMetaDocument().getNom().replaceAll("/", "_");
								nomDoc = nomDoc + " - " + documentEntity.getNom().replaceAll("/", "_");
								nomDoc = nomDoc + documentEntity.getExtensio();
								break;
							case TYPE_ORIGINAL:
								nomDoc = documentEntity.getMetaDocument().getNom().replaceAll("/", "_");
								nomDoc = nomDoc + " - " + documentEntity.getFitxerNom().replaceAll("/", "_");
								break;								
							default:
								nomDoc = documentEntity.getFitxerNom().replaceAll("/", "_");
								break;
							}
						}
						//Valor per defecte del nom del fitxer
						if (nomDoc==null) {
							nomDoc = documentEntity.getFitxerNom().replaceAll("/", "_");
						}
	
						//Configuració de l'execució massiva: Estructura de carpetes = al expedient
						if (carpetes) {
							if (documentEntity.getPare()!=null) {
								ContingutEntity pare = documentEntity.getPare();
								while (pare!=null) {
									//Ens aturam abans de arribar a nivell de expedient
									if (pare.getPare()!=null) {
										nomDoc = pare.getNom().replaceAll("/", "_") + "/" + nomDoc;
									}
									pare = pare.getPare(); //Pujam fins a l'arrel
								}
							}
						}
						
						nomDoc = ("[" + expedient.getNumero() + "] " + expedient.getNom()).replaceAll("/", "_") + "/" + nomDoc;
						
						doc.setFitxerNom(nomDoc);
						docsExp.add(doc);
					}
				}
			}
		} else {
			//Expedient sense documents, cream la carpeta buida al zip
			DocumentDto doc = new DocumentDto();
			doc.setFitxerNom(("[" + expedient.getNumero() + "] " + expedient.getNom()).replaceAll("/", "_") + "/");
			docsExp.add(doc);
		}
		
		return docsExp;
	}
	
	public void crearExecucioMassiva(Long entitatId, ExecucioMassivaDto dto, ElementTipusEnumDto elementTipus) throws NotFoundException, ValidationException {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				false, 
				true, false);
		
		ExecucioMassivaEntity execucioMassiva = null;
		
		Date dataInici;
		if (dto.getDataInici() == null) {
			dataInici = new Date();
		} else {
			dataInici = dto.getDataInici();
		}
		
		if (ExecucioMassivaTipusDto.PORTASIGNATURES.equals(dto.getTipus())) {
			execucioMassiva = ExecucioMassivaEntity.getBuilder(
					ExecucioMassivaTipusDto.PORTASIGNATURES,
					dataInici,
					dto.getMotiu(), 
					dto.getPrioritat(),
					dto.getPortafirmesResponsablesString(),
					dto.getPortafirmesSequenciaTipus(),
					dto.getPortafirmesFluxId(),
					dto.getPortafirmesTransaccioId(),
					dto.getDataCaducitat(), 
					dto.getEnviarCorreu(),
					entitat,
					dto.getRolActual(),
					dto.getPortafirmesAvisFirmaParcial(),
					dto.getPortafirmesFirmaParcial()).build();
		} else {
			execucioMassiva = ExecucioMassivaEntity.getBuilder(dto.getTipus(), new Date(), null, entitat, dto.getRolActual()).build();
			execucioMassiva.setExpedientOrigenId(dto.getExpedientOrigenId());
			execucioMassiva.setExpedientDestiId(dto.getExpedientDestiId());
			execucioMassiva.setIdentificadorAuxiliar(dto.getIdentificadorAuxiliar());
		}
		
		int ordre = 0;
		for (Long contingutId: dto.getContingutIds()) {
			
			String elementName = null;
			
			if (elementTipus == ElementTipusEnumDto.EXPEDIENT || elementTipus == ElementTipusEnumDto.DOCUMENT) {
				elementName = contingutRepository.getOne(contingutId).getNom();
			} else if (elementTipus == ElementTipusEnumDto.INTERESSAT) {
				elementName = interessatRepository.getOne(contingutId).getNom();
			} else if (elementTipus == ElementTipusEnumDto.ANOTACIO) {
				elementName = expedientPeticioRepository.getOne(contingutId).getIdentificador();
			} else if (elementTipus == ElementTipusEnumDto.ANNEX) {
				elementName = registreAnnexRepository.getOne(contingutId).getNom();
			} else if (elementTipus == ElementTipusEnumDto.NOTIFICACIO) {
				elementName = documentNotificacioRepository.getOne(contingutId).getAssumpte();
			} else if (elementTipus == ElementTipusEnumDto.ACCIO) {
				elementName = "Element #"+contingutId;
			}

			ExecucioMassivaContingutEntity emc = ExecucioMassivaContingutEntity.getBuilder(
					execucioMassiva,
					contingutId,
					elementName,
					elementTipus, 
					ordre++).build();
			
			execucioMassiva.addContingut(emc);
		}

		execucioMassivaRepository.save(execucioMassiva);
	}
	
	public void saveExecucioMassiva(
			EntitatEntity entitat,
			ExecucioMassivaDto execMassDto,
			List<ExecucioMassivaContingutDto> execElements,
			ElementTipusEnumDto elementTipus) throws NotFoundException, ValidationException {
		
		ExecucioMassivaEntity execucioMassiva = ExecucioMassivaEntity.getBuilder(
				execMassDto.getTipus(),
				execMassDto.getDataInici(),
				execMassDto.getDataFi(),
				entitat,
				execMassDto.getRolActual()).build();
	
		execucioMassiva.setCarpetes(execMassDto.getCarpetes());
		execucioMassiva.setVersioImprimible(execMassDto.getVersioImprimible());
		execucioMassiva.setNomFitxer(execMassDto.getNomFitxer());
		execucioMassiva.setDocumentNom(execMassDto.getDocumentNom());
		execucioMassiva.setEnviarCorreu(execMassDto.getEnviarCorreu());
		execucioMassiva.setMotiu(execMassDto.getMotiu());
		execucioMassiva.setPrioritat(execMassDto.getPrioritat());
		execucioMassiva.setPortafirmesResponsables(execMassDto.getPortafirmesResponsables()!=null?String.join(",", execMassDto.getPortafirmesResponsables()):null);
		execucioMassiva.setPortafirmesSequenciaTipus(execMassDto.getPortafirmesSequenciaTipus());
		execucioMassiva.setPortafirmesFluxId(execMassDto.getPortafirmesFluxId());
		execucioMassiva.setPortafirmesTransaccioId(execMassDto.getPortafirmesTransaccioId());
		execucioMassiva.setPortafirmesAvisFirmaParcial(execMassDto.getPortafirmesAvisFirmaParcial());
		execucioMassiva.setPortafirmesFirmaParcial(execMassDto.getPortafirmesFirmaParcial());
		execucioMassiva.setExpedientOrigenId(execMassDto.getExpedientOrigenId());
		execucioMassiva.setExpedientDestiId(execMassDto.getExpedientDestiId());
		
		execucioMassiva = execucioMassivaRepository.save(execucioMassiva);
		
		int ordre = 0;
		for (ExecucioMassivaContingutDto execElement: execElements) {
			
			String elementName = null;
			ElementTipusEnumDto elementTipusContingut = elementTipus;
			
			if (elementTipus == ElementTipusEnumDto.EXPEDIENT || elementTipus == ElementTipusEnumDto.DOCUMENT) {
				elementName = contingutRepository.getOne(execElement.getElementId()).getNom();
			} else if (elementTipus == ElementTipusEnumDto.INTERESSAT) {
				elementName = interessatRepository.getOne(execElement.getElementId()).getNom();
			} else if (elementTipus == ElementTipusEnumDto.ANOTACIO) {
				elementName = expedientPeticioRepository.getOne(execElement.getElementId()).getIdentificador();
			} else if (elementTipus == ElementTipusEnumDto.ANNEX) {
				elementName = registreAnnexRepository.getOne(execElement.getElementId()).getNom();
			} else if (elementTipus == ElementTipusEnumDto.ACCIO) {
				elementName = execElement.getElementNom();
				elementTipusContingut = execElement.getElementTipus(); // Acció especifica
			}

			ExecucioMassivaContingutEntity emc = ExecucioMassivaContingutEntity.getBuilder(
					execucioMassiva, 
					execElement.getElementId(), 
					elementName,
					elementTipusContingut, 
					ordre++).build();
			
			emc.updateEstatDataFi(
					execElement.getEstat(),
					execElement.getDataFi());
			
			emc.setError(execElement.getError());
			
			Throwable excepcioRetorn = ExceptionHelper.getRootCauseOrItself(execElement.getThrowable());
			if (excepcioRetorn != null) {
				String error = ExecucioMassivaHelper.getExceptionString(
						emc,
						excepcioRetorn);
				
				emc.updateError(
						new Date(), 
						error);
			}
			execucioMassivaContingutRepository.saveAndFlush(emc);
		}
	}
	
	public List<ExecucioMassivaContingutDto> getMassivaContingutFromIds(List<Long> seleccio) {
		List<ExecucioMassivaContingutDto> execucioMassivaElements = new ArrayList<ExecucioMassivaContingutDto>();
		if (seleccio!=null && seleccio.size()>0) {
			for (Long expedientId : seleccio) {
				ExecucioMassivaContingutDto execMass = new ExecucioMassivaContingutDto(
						new Date(),
						null,
						expedientId,
						ExecucioMassivaEstatDto.ESTAT_PENDENT);
				execucioMassivaElements.add(execMass);
			}
		}
		return execucioMassivaElements;
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
 	public String executarExecucioMassivaContingutNewTransaction(Long execucioMassivaContingutId, List<DocumentAmbTipusDto> documents) {
		
		Throwable exc = null;
		String resultat = null;
		ExecucioMassivaContingutEntity emc = execucioMassivaContingutRepository.findById(execucioMassivaContingutId).orElse(null);
		
		if (emc == null)
			throw new NotFoundException(execucioMassivaContingutId, ExecucioMassivaContingutEntity.class);
		
		if (!ExecucioMassivaEstatDto.ESTAT_FINALITZAT.equals(emc.getEstat())) {
		
			ExecucioMassivaEntity exm = emc.getExecucioMassiva();
			ExecucioMassivaTipusDto tipus = exm.getTipus();
			
			try {
				
				Authentication orgAuthentication = SecurityContextHolder.getContext().getAuthentication();
				final String user = exm.getCreatedBy().get();
		        Principal principal = new Principal() {
					public String getName() {
						return user;
					}
				};
				
				List<String> rolsUsuariActual = pluginHelper.rolsUsuariFindAmbCodi(user);
				if (rolsUsuariActual.isEmpty())
					rolsUsuariActual.add("tothom");
		
				List<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
				for (String rol : rolsUsuariActual) {
					SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(rol);
					authorities.add(simpleGrantedAuthority);
				}
				
				Authentication authentication =  new UsernamePasswordAuthenticationToken(
						principal, 
						"N/A",
						authorities);
		        SecurityContextHolder.getContext().setAuthentication(authentication);
		        
		        /**
		         * Actualitzam la data de INICI de l'item actual de l'execució massiva 
		         */
		        emc.updateDataInici(new Date());
		        
		        if (ExecucioMassivaTipusDto.IMPORTAR_DOCS.equals(tipus)){
		        	if (documents!=null) {
		        		for (DocumentAmbTipusDto docExp: documents) {
		        			
		        			//Content type ampliat
		        			String contentTypeDoc = Utils.getFitxerContentType(docExp.getFitxer().getName(), docExp.getFitxer().getContentType());
		        			
		        			DocumentDto documentDto = new DocumentDto();
		        			documentDto.setDocumentFirmaTipus(DocumentFirmaTipusEnumDto.SENSE_FIRMA);
		        	        MetaDocumentDto metaNode = new MetaDocumentDto();
		        	        metaNode.setId(docExp.getTipusDocument());
		        	        documentDto.setMetaNode(metaNode);
		        	        documentDto.setPareId(emc.getElementId());
		        	        if (docExp.getTipusDocument()==null) {
		        	        	throw new NotFoundException(docExp.getTipusDocument(), Long.class);
		        	        }
		        	        MetaDocumentEntity metaDocumentEntity = metaDocumentRepository.findById(docExp.getTipusDocument()).orElse(null);
		        	        if (metaDocumentEntity==null) {
		        	        	throw new NotFoundException(metaDocumentEntity, MetaDocumentEntity.class);
		        	        }
		        	        documentDto.setDocumentTipus(DocumentTipusEnumDto.DIGITAL);
		        	        //El nom no es pot repetir dins l'expedient
		        	        String nomDocument = metaDocumentEntity.getNom()+" "+emc.getElementId()+ " "+emc.getExecucioMassiva().getId(); 
		        	        documentDto.setNom(Utils.abbreviate(nomDocument, 1024));
		        	        documentDto.setDescripcio("Document importat massivament desde el llistat de expedients.");
		        	        documentDto.setData(Calendar.getInstance().getTime());
		        	        documentDto.setNtiOrigen(metaDocumentEntity.getNtiOrigen());
		        	        documentDto.setNtiEstadoElaboracion(metaDocumentEntity.getNtiEstadoElaboracion());
		        	        documentDto.setNtiIdDocumentoOrigen(null);
		        	        documentDto.setFitxerNom(docExp.getFitxer().getName());
		        	        documentDto.setFitxerContingut(docExp.getFitxer().getContent());
		        	        documentDto.setFitxerContentType(contentTypeDoc);
		        			
		        			//1.- comprovar la firma del document com es faria desde el formulari de contingut
		        			if (Boolean.parseBoolean(configHelper.getConfig(PropertyConfig.DETECCIO_FIRMA_AUTOMATICA))) {
		                    	SignatureInfoDto signatureInfoDto = pluginHelper.detectaFirmaDocument(
		                    			docExp.getFitxer().getContent(),
		                    			contentTypeDoc);
		                    	
		                    	if (signatureInfoDto.isSigned()) {
		                    		documentDto.setAmbFirma(true);
		                    		documentDto.setDocumentFirmaTipus(DocumentFirmaTipusEnumDto.FIRMA_ADJUNTA);
		                    	}
		        			}

		        			//2.- Crear el document per l'expedient (multiplicitat)
		        			documentHelper.crearDocument(
		        					emc.getExecucioMassiva().getEntitat().getId(),
		        					documentDto,
		        					contingutRepository.findById(emc.getElementId()).get(),
		        					false,
		        					false,
		        					true);
		        		}
		        	}
		        } else if (ExecucioMassivaTipusDto.PORTASIGNATURES.equals(tipus)){
					exc = enviarPortafirmes(emc);
				} else if (ExecucioMassivaTipusDto.CUSTODIAR_ELEMENTS_PENDENTS.equals(tipus)){
					exc = custodiarElementsPendents(emc);
				} else if (ExecucioMassivaTipusDto.CANVI_ESTAT.equals(tipus)){
					//TODO: Es necessita saber l'estat al qual es vol canviar l'expedient.
				} else if (ExecucioMassivaTipusDto.TANCAMENT.equals(tipus)){
					exc = tancarExpedients(emc);
				} else if (ExecucioMassivaTipusDto.ADJUNTAR_ANNEXOS_PENDENTS.equals(tipus)){
					exc = retryCreateDocFromAnnex(emc);
				} else if (ExecucioMassivaTipusDto.ACTUALITZAR_ESTAT_ANOTACIONS.equals(tipus)){
					exc = reintentarCanviEstatDistribucio(emc);
				} else if (ExecucioMassivaTipusDto.ACTUALITZAR_ESTAT_NOTIFICACIONS.equals(tipus)){
					exc = actualitzarEstatNotificacio(emc);
				} else if (ExecucioMassivaTipusDto.FIRMASIMPLEWEB.equals(tipus)){
					//TODO: Veurer com es fa a DocumentMassiuFirmaWebController. Es necessita info addicional.
				} else if (ExecucioMassivaTipusDto.AGAFAR_EXPEDIENT.equals(tipus) ||
						ExecucioMassivaTipusDto.ALLIBERAR_EXPEDIENT.equals(tipus) ||
						ExecucioMassivaTipusDto.RETORNAR_EXPEDIENT.equals(tipus) ||
						ExecucioMassivaTipusDto.SEGUIR_EXPEDIENT.equals(tipus) ||
						ExecucioMassivaTipusDto.UNFOLLOW_EXPEDIENT.equals(tipus) ||
						ExecucioMassivaTipusDto.ESBORRAR_EXPEDIENT.equals(tipus)){
					exc = executarAccioSimpleExpedient(emc, tipus);
				}

				SecurityContextHolder.getContext().setAuthentication(orgAuthentication);
				
				if (exc == null) {
					Long contingutId = emc.getElementId();
					if (ElementTipusEnumDto.INTERESSAT.equals(emc.getElementTipus())) {
						InteressatEntity ie = interessatRepository.findById(emc.getElementId()).orElseGet(null);
						if (ie!=null && ie.getExpedient()!=null) {
							contingutId = ie.getExpedient().getId();
						}
					} else if (ElementTipusEnumDto.ANOTACIO.equals(emc.getElementTipus())) {
						contingutId = null; //TODO com treurer el contingut a partir de una anotació
					} else if (ElementTipusEnumDto.ANNEX.equals(emc.getElementTipus())) {
						RegistreAnnexEntity ra = registreAnnexRepository.findById(emc.getElementId()).orElseGet(null);
						if (ra!=null && ra.getDocument()!=null) {
							contingutId = ra.getDocument().getId();
						}
					} else if (ElementTipusEnumDto.NOTIFICACIO.equals(emc.getElementTipus())) {
						DocumentNotificacioEntity dn = documentNotificacioRepository.findById(emc.getElementId()).orElseGet(null);
						if (dn!=null && dn.getDocument()!=null) {
							contingutId = dn.getDocument().getId();
						}
					}

					if (contingutId!=null) {
						alertaHelper.crearAlerta(
								messageHelper.getMessage(
										"alertes.segon.pla.execucio.massiva",
										new Object[] {execucioMassivaContingutId}),
								null,
								contingutId);
					}
				}
				
			} catch (Throwable e) {
				exc = e;
			}
		}
		
		if (exc == null) {
			emc.updateFinalitzat(new Date());
		} else {
			Throwable excepcioRetorn = ExceptionHelper.getRootCauseOrItself(exc);
			String error = ExecucioMassivaHelper.getExceptionString(emc, excepcioRetorn);
			emc.updateError(new Date(), error);									
		}
		
		execucioMassivaContingutRepository.save(emc);
		
		return resultat;
	}

	private Throwable executarAccioSimpleExpedient(ExecucioMassivaContingutEntity emc, ExecucioMassivaTipusDto accio) {
		
		Throwable exc = null;
		try {
			if (ExecucioMassivaTipusDto.AGAFAR_EXPEDIENT.equals(accio)){
				expedientHelper.agafar(emc.getElementId(), emc.getCreatedBy().get(), "Exec. massiva "+emc.getId());
			} else if (ExecucioMassivaTipusDto.ALLIBERAR_EXPEDIENT.equals(accio)){
				expedientHelper.alliberar(emc.getElementId());
			} else if (ExecucioMassivaTipusDto.RETORNAR_EXPEDIENT.equals(accio)){
				expedientHelper.retornar(emc.getElementId());
			} else if (ExecucioMassivaTipusDto.SEGUIR_EXPEDIENT.equals(accio)){
				expedientHelper.follow(emc.getElementId(), emc.getCreatedBy().get());
			} else if (ExecucioMassivaTipusDto.UNFOLLOW_EXPEDIENT.equals(accio)){
				expedientHelper.unfollow(emc.getElementId(), emc.getCreatedBy().get());
			} else if (ExecucioMassivaTipusDto.ESBORRAR_EXPEDIENT.equals(accio)){
				contingutHelper.deleteReversible(
						emc.getExecucioMassiva().getEntitat().getId(),
						emc.getElementId(),
						null, //tascaID
						null);//rolActual
			}
		} catch (Exception ex) {
			logger.error("CONTINGUT MASSIU:" + emc.getId() + ". No s'ha pogut realitzar l'accio "+accio.toString()+" sobre l'element "+emc.getElementId(), ex);
			exc = ex;
		}
		return exc;		
	}
	
	private Throwable retryCreateDocFromAnnex(ExecucioMassivaContingutEntity emc) {
		Throwable exc = null;
		try {
			exc = expedientHelper.retryCreateDocFromAnnex(
					emc.getElementId(),
					emc.getExecucioMassiva().getIdentificadorAuxiliar(),
					"IPA_ADMIN");
		} catch (Exception ex) {
			logger.error("CONTINGUT MASSIU:" + emc.getId() + ". No s'ha pogut crear el document a partir del annex", ex);
			exc = ex;
		}
		return exc;
	}
	
	private Throwable reintentarCanviEstatDistribucio(ExecucioMassivaContingutEntity emc) {
		Throwable exc = null;
		try {
			exc = expedientPeticioHelper.reintentarCanviEstatDistribucio(emc.getElementId());
		} catch (Exception ex) {
			logger.error("CONTINGUT MASSIU:" + emc.getId() + ". No s'ha pogut actualitzat l'estat a distribució de l'element", ex);
			exc = ex;
		}
		return exc;
	}

	private Throwable actualitzarEstatNotificacio(ExecucioMassivaContingutEntity emc) {
		Throwable exc = null;
		try {
			documentNotificacioHelper.actualitzarEstat(emc.getElementId());
		} catch (Exception ex) {
			logger.error("CONTINGUT MASSIU:" + emc.getId() + ". No s'ha pogut actualitzar l'estat de la notificació", ex);
			exc = ex;
		}
		return exc;
	}
	
	private Throwable custodiarElementsPendents(ExecucioMassivaContingutEntity emc) throws Exception {
		
		Throwable exc = null;
		
		try {

			//En el cas de custodiar elements pendents, el ID del element a tractar pot ser:
			// - expedientId:	ExpedientEntity extends NodeEntity extends ContingutEntity
			// - documentId:	DocumentEntity  extends NodeEntity extends ContingutEntity
			// - interessatId:	InteressatEntity (no exten de NodeEntity)
			
			if (ElementTipusEnumDto.EXPEDIENT.equals(emc.getElementTipus())) {
				
				exc = expedientHelper.guardarExpedientArxiu(emc.getElementId());
				
			} else if (ElementTipusEnumDto.INTERESSAT.equals(emc.getElementTipus())) {
				
				InteressatEntity interessat = entityComprovarHelper.comprovarInteressat(null, emc.getElementId());
				if (interessat != null && interessat.getExpedient() != null) {
					exc = expedientInteressatHelper.guardarInteressatsArxiu(interessat.getExpedient().getId());
				}

			} else if (ElementTipusEnumDto.DOCUMENT.equals(emc.getElementTipus())) {
				
				DocumentEntity document = (DocumentEntity) entityComprovarHelper.comprovarContingut(emc.getElementId());
				
				if (document.getArxiuUuid() == null) { //documents uploaded manually in ripea that were not saved in arxiu
					
					exc = documentHelper.guardarDocumentArxiu(document.getId());
					
				} else if (document.isPendentMoverArxiu()) { //documents from distribucio that were not moved in arxiu to ripea expedient
					
					exc = expedientHelper.moveDocumentArxiuNewTransaction(document.getAnnexAnotacioId());
					
				} else if (document.getGesDocFirmatId() != null) { // documents signed in portafirmes that arrived in callback and were not saved in arxiu
					
					exc = firmaPortafirmesHelper.portafirmesReintentar(
							emc.getExecucioMassiva().getEntitat().getId(),
							document);
				}
			}
			
		} catch (Exception ex) {
			logger.error("CONTINGUT MASSIU:" + emc.getId() + ". No s'ha pogut custodiar l'element", ex);
			exc = ex;
		}
		
		//Si en algun moment guardant Expedient, documents o interessats, dona error perque ja es troba dins Arxiu, no conta com a error
		if (exc instanceof ArxiuJaGuardatException) { exc = null; }
		
		return exc;
	}	

	private Throwable tancarExpedients (ExecucioMassivaContingutEntity emc) throws Exception {
		Throwable exc = null;
		try {
			ExpedientEntity contingut = (ExpedientEntity)contingutRepository.findById(emc.getElementId()).get();
			expedientHelper.tancar(
					contingut.getEntitat().getId(),
					emc.getElementId(),
					emc.getExecucioMassiva().getMotiu(),
					Utils.csvToLongArray(emc.getError()),
					false);
			emc.setError(null); //Netejam error, que haviem emprat momentaniament per guardar els IDs dels documents.
		} catch (Exception ex) {
			logger.error("CONTINGUT MASSIU:" + emc.getId() + ". No s'ha pogut tancar l'expedient", ex);
			exc = ex;
		}
		return exc;
	}
	
	private Throwable enviarPortafirmes(ExecucioMassivaContingutEntity emc) throws Exception {
		Throwable exc = null;
		try {
			DocumentEntity contingut = (DocumentEntity)contingutRepository.findById(emc.getElementId()).get();
			organGestorHelper.actualitzarOrganCodi(organGestorHelper.getOrganCodiFromContingutId(contingut.getId()));
			ExecucioMassivaEntity em = emc.getExecucioMassiva();
			firmaPortafirmesHelper.portafirmesEnviar(
					contingut.getEntitat().getId(),
					contingut,
					em.getMotiu(),
					em.getPrioritat(),
					null,
					em.getPortafirmesFluxId(),
					em.getPortafirmesResponsables() != null ? em.getPortafirmesResponsables().split(",") : null,
					em.getPortafirmesSequenciaTipus(),
					contingut.getMetaDocument().getPortafirmesFluxTipus(),
					null,
					em.getPortafirmesTransaccioId(),
					em.getPortafirmesAvisFirmaParcial()!=null?em.getPortafirmesAvisFirmaParcial().booleanValue():false,
					em.getPortafirmesFirmaParcial()!=null?em.getPortafirmesFirmaParcial().booleanValue():false);
		} catch (Exception ex) {
			logger.error("CONTINGUT MASSIU:" + emc.getId() + ". No s'ha pogut enviar el document al portasignatures", ex);
			exc = ex;
		}
		return exc;
	}
	
	public static String getExceptionString(Throwable cause, int retalla) {
		StringWriter out = new StringWriter();
		cause.printStackTrace(new PrintWriter(out));
		return Utils.abbreviate(out.toString(), retalla);
	}
	
	public static String getExceptionString(
			ExecucioMassivaContingutEntity emc,
			Throwable cause) {

		String finalMessage = "Error al executar la acció massiva (";
		if (emc.getElementId() != null)
			finalMessage += "elementId: " + emc.getElementId() + ", ";
		if (emc.getElementNom() != null)
			finalMessage += "elementNom: " + emc.getElementNom() + ", ";
		if (emc.getElementTipus() != null)
			finalMessage += "elementTipus: " + emc.getElementTipus() + ", ";
		if (emc.getExecucioMassiva().getId() != null)
			finalMessage += "execucioMassivaId: " + emc.getExecucioMassiva().getId() + ", ";
		if (emc.getId() != null)
			finalMessage += "execucioMassivaContingutId: " + emc.getId();
		finalMessage += ") ===> \r\n";
		StringWriter out = new StringWriter();
		cause.printStackTrace(new PrintWriter(out));
		finalMessage += out.toString();
		
		return Utils.abbreviate(finalMessage, 2045);
	}
	
	private static final Logger logger = LoggerFactory.getLogger(ExecucioMassivaHelper.class);
}
