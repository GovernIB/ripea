package es.caib.ripea.service.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.ExecucioMassivaContingutEntity;
import es.caib.ripea.persistence.entity.ExecucioMassivaEntity;
import es.caib.ripea.persistence.entity.ExpedientEntity;
import es.caib.ripea.persistence.entity.UsuariEntity;
import es.caib.ripea.persistence.repository.ContingutRepository;
import es.caib.ripea.persistence.repository.ExecucioMassivaContingutRepository;
import es.caib.ripea.persistence.repository.ExecucioMassivaRepository;
import es.caib.ripea.persistence.repository.ExpedientRepository;
import es.caib.ripea.persistence.repository.UsuariRepository;
import es.caib.ripea.service.helper.AlertaHelper;
import es.caib.ripea.service.helper.ConfigHelper;
import es.caib.ripea.service.helper.ConversioTipusHelper;
import es.caib.ripea.service.helper.EmailHelper;
import es.caib.ripea.service.helper.EntityComprovarHelper;
import es.caib.ripea.service.helper.ExceptionHelper;
import es.caib.ripea.service.helper.ExecucioMassivaHelper;
import es.caib.ripea.service.helper.ExpedientHelper;
import es.caib.ripea.service.helper.MessageHelper;
import es.caib.ripea.service.intf.config.PropertyConfig;
import es.caib.ripea.service.intf.dto.DocumentDto;
import es.caib.ripea.service.intf.dto.ElementTipusEnumDto;
import es.caib.ripea.service.intf.dto.EntitatDto;
import es.caib.ripea.service.intf.dto.ExecucioMassivaContingutDto;
import es.caib.ripea.service.intf.dto.ExecucioMassivaDto;
import es.caib.ripea.service.intf.dto.ExecucioMassivaEstatDto;
import es.caib.ripea.service.intf.dto.ExecucioMassivaTipusDto;
import es.caib.ripea.service.intf.dto.FitxerDto;
import es.caib.ripea.service.intf.dto.UsuariDto;
import es.caib.ripea.service.intf.exception.NotFoundException;
import es.caib.ripea.service.intf.exception.ValidationException;
import es.caib.ripea.service.intf.service.ExecucioMassivaService;

@Service
public class ExecucioMassivaServiceImpl implements ExecucioMassivaService {

	@Autowired private ContingutRepository contingutRepository;
	@Autowired private UsuariRepository usuariRepository;
	@Autowired private ExecucioMassivaRepository execucioMassivaRepository;
	@Autowired private ExecucioMassivaContingutRepository execucioMassivaContingutRepository;
	@Autowired private ConversioTipusHelper conversioTipusHelper;
	@Autowired private EntityComprovarHelper entityComprovarHelper;
	@Autowired private ExecucioMassivaHelper execucioMassivaHelper;
	@Autowired private AlertaHelper alertaHelper;
	@Autowired private MessageHelper messageHelper;
	@Autowired private ExpedientHelper expedientHelper;
	@Autowired private EmailHelper emailHelper;
	@Autowired private ConfigHelper configHelper;
    @Autowired private ExpedientRepository expedientRepository;

	@Transactional
	@Override
	public void crearExecucioMassiva(Long entitatId, ExecucioMassivaDto dto) throws NotFoundException, ValidationException {
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
		
		if (dto.getTipus() == ExecucioMassivaTipusDto.PORTASIGNATURES) {
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
		}
		
		int ordre = 0;
		for (Long contingutId: dto.getContingutIds()) {
			ExecucioMassivaContingutEntity emc = ExecucioMassivaContingutEntity.getBuilder(
					execucioMassiva, 
					contingutId, 
					contingutRepository.getOne(contingutId).getNom(),
					ElementTipusEnumDto.DOCUMENT, 
					ordre++).build();
			
			execucioMassiva.addContingut(emc);
		}
		
		execucioMassivaRepository.save(execucioMassiva);
	}

	@Override
	@Transactional(readOnly = true)
	public FitxerDto descarregarDocumentExecMassiva(Long entitatId, Long execMassivaId) {
		return execucioMassivaHelper.descarregarDocumentExecMassiva(entitatId, execMassivaId);
	}

	@Transactional
	@Override
	public void saveExecucioMassiva(
			Long entitatId,
			ExecucioMassivaDto execMassDto,
			List<ExecucioMassivaContingutDto> execElements,
			ElementTipusEnumDto elementTipus) throws NotFoundException, ValidationException {
		EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(entitatId,false,false,false,true,false);
		execucioMassivaHelper.saveExecucioMassiva(entitatEntity, execMassDto, execElements, elementTipus);
	}

	@Override
	public List<ExecucioMassivaDto> findExecucionsMassivesPerUsuari(Long entitatId, UsuariDto usuari, int pagina) throws NotFoundException {
		
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				true, false, false);
		
		Pageable paginacio = PageRequest.of(pagina, 8, Direction.DESC, "dataInici");
		
		List<ExecucioMassivaEntity> exmEntities = new ArrayList<ExecucioMassivaEntity>();
		if (usuari == null) {
			exmEntities = execucioMassivaRepository.findByEntitatIdOrderByCreatedDateDesc(entitat.getId(), paginacio);
		} else {
			UsuariEntity usuariEntity = usuariRepository.findByCodi(usuari.getCodi());
			exmEntities = execucioMassivaRepository.findByCreatedByAndEntitatIdOrderByCreatedDateDesc(usuariEntity.getCodi(), entitat.getId(), paginacio);
		}
		
		return recompteErrors(exmEntities);
	}

	@Override
	public List<ExecucioMassivaDto> findExecucionsMassivesGlobals() throws NotFoundException {
		List<ExecucioMassivaEntity> entities = execucioMassivaRepository.findAll();
		return recompteErrors(entities);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<ExecucioMassivaContingutDto> findContingutPerExecucioMassiva(Long exm_id) throws NotFoundException {
		ExecucioMassivaEntity execucioMassiva = execucioMassivaRepository.findById(exm_id).orElse(null);
		if (execucioMassiva == null)
			throw new NotFoundException(exm_id, ExecucioMassivaEntity.class);
		
		List<ExecucioMassivaContingutEntity> continguts = execucioMassivaContingutRepository.findByExecucioMassivaOrderByOrdreAsc(execucioMassiva);
		List<ExecucioMassivaContingutDto> dtos = conversioTipusHelper.convertirList(continguts, ExecucioMassivaContingutDto.class);
		
		return dtos;
	}

	@Transactional
	@Override
	public void executeNextMassiveScheduledTask() {
		logger.trace("Execució tasca periòdica: Execucions massives");

		try {
			List<ExecucioMassivaEntity> massives = execucioMassivaRepository.getMassivesPerProcessar(new Date());

			if (massives != null && massives.size()>0) {
				
				ExecucioMassivaEntity execucioMassiva = massives.get(0);
				EntitatDto entitat = conversioTipusHelper.convertir(execucioMassiva.getEntitat(), EntitatDto.class);
				ConfigHelper.setEntitat(entitat);
				
				if (execucioMassiva.getContinguts() != null) {

					if (ExecucioMassivaTipusDto.EXPORTAR_ZIP.equals(execucioMassiva.getTipus())) {
						//Cas de exportació a ZIP, no es individual per cada expedient, s'ha de executar per tots els expedients
						generaZipDocumentsExpedients(execucioMassiva);
					} else if (ExecucioMassivaTipusDto.EXPORTAR_INDEX_ZIP.equals(execucioMassiva.getTipus()) ||
							ExecucioMassivaTipusDto.EXPORTAR_INDEX_PDF.equals(execucioMassiva.getTipus())||
							ExecucioMassivaTipusDto.EXPORTAR_INDEX_EXCEL.equals(execucioMassiva.getTipus()) ||
							ExecucioMassivaTipusDto.EXPORTAR_ENI.equals(execucioMassiva.getTipus()) || 
							ExecucioMassivaTipusDto.EXPORTAR_INSIDE.equals(execucioMassiva.getTipus()) ||
							ExecucioMassivaTipusDto.EXPORTAR_EXCEL.equals(execucioMassiva.getTipus()) || 
							ExecucioMassivaTipusDto.EXPORTAR_CSV.equals(execucioMassiva.getTipus())) {
						exportarExpedients(execucioMassiva);
					} else {
						
						for (ExecucioMassivaContingutEntity execucioMassivaItemEntity : execucioMassiva.getContinguts()) {
							
							String throwable = execucioMassivaHelper.executarExecucioMassivaContingutNewTransaction(
									execucioMassivaItemEntity.getId());
							
							if (throwable != null) {
								alertaHelper.crearAlerta(
										messageHelper.getMessage(
												"alertes.segon.pla.executar.execucio.massiva.error",
												new Object[] {execucioMassivaItemEntity.getId()}),
										throwable,
										false,
										execucioMassivaItemEntity.getElementId());
							}
						}
					}
				}
				execucioMassiva.updateDataFi(new Date());
				execucioMassivaRepository.save(execucioMassiva);
				
				if (execucioMassiva.getEnviarCorreu()!=null && execucioMassiva.getEnviarCorreu().booleanValue()) {
					try {
						emailHelper.execucioMassivaFinalitzada(execucioMassiva);
					} catch (Exception e) {
						logger.error("No s'ha pogut enviar el correu de finalització d'accio massiva", e);
					}
				}
			}
		} catch (Exception e) {
			logger.error("Error al fer execucio massiva", e);
		}
	}

	private Set<Long> contingutsToLongList(List<ExecucioMassivaContingutEntity> continguts) {
		Set<Long> resultat = new HashSet<Long>();
		if (continguts!=null) {
			for (ExecucioMassivaContingutEntity execucioMassivaContingutEntity : continguts) {
				resultat.add(execucioMassivaContingutEntity.getElementId());
			}
		}
		return resultat;
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	private void exportarExpedients(ExecucioMassivaEntity execucioMassiva) {
		
		try {
			
			Set<Long> ids = contingutsToLongList(execucioMassiva.getContinguts());
			FitxerDto resultat = new FitxerDto();
			if (ExecucioMassivaTipusDto.EXPORTAR_INDEX_ZIP.equals(execucioMassiva.getTipus())) {
				resultat = expedientHelper.generarIndexExpedients(execucioMassiva.getEntitat().getId(), ids, false, "ZIP");
			} else if (ExecucioMassivaTipusDto.EXPORTAR_INDEX_PDF.equals(execucioMassiva.getTipus())) {
				resultat = expedientHelper.generarIndexExpedients(execucioMassiva.getEntitat().getId(), ids, false, "PDF");
			} else if (ExecucioMassivaTipusDto.EXPORTAR_INDEX_EXCEL.equals(execucioMassiva.getTipus())) {
				resultat = expedientHelper.generarIndexExpedients(execucioMassiva.getEntitat().getId(), ids, false, "XLSX");
			} else if (ExecucioMassivaTipusDto.EXPORTAR_ENI.equals(execucioMassiva.getTipus())) {
				resultat = expedientHelper.exportarExpedient(ids, false);
			} else if (ExecucioMassivaTipusDto.EXPORTAR_INSIDE.equals(execucioMassiva.getTipus())) {
				resultat = expedientHelper.exportarExpedient(ids, true);
			} else if (ExecucioMassivaTipusDto.EXPORTAR_EXCEL.equals(execucioMassiva.getTipus())) {
				resultat = expedientHelper.exportacio(execucioMassiva.getEntitat().getId(), ids, "ODS");
			} else if (ExecucioMassivaTipusDto.EXPORTAR_CSV.equals(execucioMassiva.getTipus())) {
				resultat = expedientHelper.exportacio(execucioMassiva.getEntitat().getId(), ids, "CSV");
			}
			
			String directoriDesti = configHelper.getConfig(PropertyConfig.APP_DATA_DIR);
			String documentNom = "/exportZip/documentsExpedients_" + Calendar.getInstance().getTimeInMillis() + ".zip";
			File fContent = new File(directoriDesti + documentNom);
			fContent.getParentFile().mkdirs();
			FileOutputStream outContent = new FileOutputStream(fContent);
			outContent.write(resultat.getContingut());
			outContent.close();
			execucioMassiva.setDocumentNom(documentNom);
			
			//Marcam tots els elements de la exec massiva com a finalitzats
			if(execucioMassiva.getContinguts()!=null) {
				for (ExecucioMassivaContingutEntity emc: execucioMassiva.getContinguts()) {
					emc.updateFinalitzat(new Date());
				}
			}
			
		} catch (Exception exc) {
			//En aquets cas, no es pot saber quin expedient ha fallat, els marcam a tots com a error
			for (ExecucioMassivaContingutEntity execucioMassivaContingutEntity : execucioMassiva.getContinguts()) {
				execucioMassivaContingutEntity.updateError(new Date(), "Error en la generació del index dels expedients."+execucioMassiva.getTipus().toString());
			}
		}
	}	
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	private void generaZipDocumentsExpedients(ExecucioMassivaEntity execucioMassiva) {

		ExecucioMassivaContingutEntity execMassivaItem = null;

		try {

			long t1 = Calendar.getInstance().getTimeInMillis();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ZipOutputStream out = new ZipOutputStream(baos);
			ZipEntry ze;
			int numFiles = 0;
			List<String> nomsArxius = new ArrayList<String>();
			List<DocumentDto> docsExp = new ArrayList<DocumentDto>();
			double maxMbFitxer	= Integer.parseInt(configHelper.getConfig(PropertyConfig.ARXIU_MAX_MB, "100"));
			double actualMbFitxer = 0;
			Integer maxMinExec = Integer.parseInt(configHelper.getConfig(PropertyConfig.SEGON_PLA_TIMEOUT, "10"));
			long maxTempsProces	= t1+(maxMinExec*60*1000);
			boolean error = false;

			for (ExecucioMassivaContingutEntity execucioMassivaContingutEntity : execucioMassiva.getContinguts()) {
	
				ExpedientEntity expedient = expedientRepository.getOne(execucioMassivaContingutEntity.getElementId());
				execMassivaItem = execucioMassivaContingutEntity;
				docsExp.addAll(
						execucioMassivaHelper.getDocumentsForExportacioZip(
								expedient,
								execucioMassiva.getNomFitxer(),
								execucioMassiva.getVersioImprimible().booleanValue(),
								execucioMassiva.getCarpetes().booleanValue(),
								actualMbFitxer,
								null));
				
				if (Calendar.getInstance().getTimeInMillis()>maxTempsProces) {
					execucioMassivaContingutEntity.updateError(new Date(), "El procés ha superat el limit de temps definit per la seva execució: "+maxMinExec+" min. Intenteu seleccionar menys expedients.");
					error = true;
					break;
				} else if (actualMbFitxer>maxMbFitxer) {
					execucioMassivaContingutEntity.updateError(new Date(), "El fitxer a generar supera el maxim de Mb permesos: "+maxMbFitxer+" Mb. Intenteu seleccionar menys expedients.");
					error = true;
					break;
				} else {
					execucioMassivaContingutEntity.updateFinalitzat(new Date());
				}
			}
	
			if (!error) {
	
				if (docsExp != null && docsExp.size() > 0) {
					for (DocumentDto documentDto : docsExp) {
						String recursNom = this.getZipRecursNom(documentDto.getFitxerNom(), nomsArxius);
						ze = new ZipEntry(recursNom);
						out.putNextEntry(ze);
						if (documentDto.getFitxerContingut()!=null) {
							out.write(documentDto.getFitxerContingut());
						}
						out.closeEntry();
						numFiles++;
					}
				}
	
				out.close();
	
				if (numFiles > 0) {
					String directoriDesti = configHelper.getConfig(PropertyConfig.APP_DATA_DIR);
					String documentNom = "/exportZip/documentsExpedients_" + Calendar.getInstance().getTimeInMillis() + ".zip";
					File fContent = new File(directoriDesti + documentNom);
					fContent.getParentFile().mkdirs();
					FileOutputStream outContent = new FileOutputStream(fContent);
					outContent.write(baos.toByteArray());
					outContent.close();
					execucioMassiva.setDocumentNom(documentNom);
				}

			} else {
				out.close();
				baos.close();
			}

		} catch (Exception exc) {
			if (execMassivaItem!=null) {
				Throwable excepcioRetorn = ExceptionHelper.getRootCauseOrItself(exc);
				String errorTractat = ExecucioMassivaHelper.getExceptionString(excepcioRetorn, 2046);
				execMassivaItem.updateError(new Date(), errorTractat);
			}
			//Si ha donat un error algun dels elements, marcam els que no s'han processat com a cancelats.
			for (ExecucioMassivaContingutEntity execucioMassivaContingutEntity : execucioMassiva.getContinguts()) {
				if (execucioMassivaContingutEntity.getEstat()==null || 
					ExecucioMassivaEstatDto.ESTAT_PENDENT.equals(execucioMassivaContingutEntity.getEstat())) {
					execucioMassivaContingutEntity.updateCancelat(new Date());
				}
			}
		}
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

	private List<ExecucioMassivaDto> recompteErrors(List<ExecucioMassivaEntity> exmEntities) {
		List<ExecucioMassivaDto> dtos = new ArrayList<ExecucioMassivaDto>();
		for (ExecucioMassivaEntity exm: exmEntities) {
			ExecucioMassivaDto dto = conversioTipusHelper.convertir(exm, ExecucioMassivaDto.class);
			int errors = 0;
			Long pendents = 0L;
			int cancelats = 0;
			for (ExecucioMassivaContingutEntity emc: exm.getContinguts()) {
				if (emc.getEstat() == ExecucioMassivaEstatDto.ESTAT_ERROR)
					errors ++;
				if (emc.getEstat() == ExecucioMassivaEstatDto.ESTAT_CANCELAT)
					cancelats ++;
				if (emc.getDataFi() == null)
					pendents++;
				dto.getContingutIds().add(emc.getId());
			}
			dto.setErrors(errors);
			dto.setCancelats(cancelats);
			Long total = Long.valueOf(dto.getContingutIds().size());
			dto.setExecutades(getPercent((total - pendents), total));
			dto.setDocumentNom(exm.getDocumentNom());
			dtos.add(dto);
		}
		return dtos;
	}

	private double getPercent(Long value, Long total) {
		if (total == 0)
			return 100L;
		else if (value == 0L)
			return 0L;
	    return Math.round(value * 100 / total);
	}

	private static final Logger logger = LoggerFactory.getLogger(EntitatServiceImpl.class);
}
