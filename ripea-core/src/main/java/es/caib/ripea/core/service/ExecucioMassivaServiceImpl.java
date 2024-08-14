/**
 * 
 */
package es.caib.ripea.core.service;

import es.caib.ripea.core.api.dto.*;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.exception.ValidationException;
import es.caib.ripea.core.api.service.ExecucioMassivaService;
import es.caib.ripea.core.entity.*;
import es.caib.ripea.core.helper.*;
import es.caib.ripea.core.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Implementació dels mètodes per a gestionar documents.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class ExecucioMassivaServiceImpl implements ExecucioMassivaService {

	@Autowired
	private ContingutRepository contingutRepository;
	@Autowired
	private UsuariRepository usuariRepository;
	@Autowired
	private ExecucioMassivaRepository execucioMassivaRepository;
	@Autowired
	private ExecucioMassivaContingutRepository execucioMassivaContingutRepository;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private EntityComprovarHelper entityComprovarHelper;
	@Autowired
	private ExecucioMassivaHelper execucioMassivaHelper;
	@Autowired
	private AlertaHelper alertaHelper;
	@Autowired
	private MessageHelper messageHelper;
	@Autowired
	private EmailHelper emailHelper;
	@Autowired
	private DocumentHelper documentHelper;
	@Autowired
	private ConfigHelper configHelper;
	@Autowired
	private InteressatRepository interessatRepository;
	@Autowired
	private RegistreAnnexRepository registreAnnexRepository;
	@Autowired
	private ExpedientPeticioRepository expedientPeticioRepository;
    @Autowired
    private ExpedientRepository expedientRepository;

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
					dto.getPortafirmesAvisFirmaParcial()).build();
		}
		
		int ordre = 0;
		for (Long contingutId: dto.getContingutIds()) {
			ExecucioMassivaContingutEntity emc = ExecucioMassivaContingutEntity.getBuilder(
					execucioMassiva, 
					contingutId, 
					contingutRepository.findOne(contingutId).getNom(),
					ElementTipusEnumDto.DOCUMENT, 
					ordre++).build();
			
			execucioMassiva.addContingut(emc);
		}
		
		execucioMassivaRepository.save(execucioMassiva);
	}

	@Override
	@Transactional(readOnly = true)
	public FitxerDto descarregarDocumentExecMassiva(Long entitatId, Long execMassivaId) {
		ExecucioMassivaEntity execucioMassiva = execucioMassivaRepository.findOne(execMassivaId);
		if (execucioMassiva!=null && execucioMassiva.getDocumentNom()!=null) {
			FitxerDto resultat = new FitxerDto();
			String directoriDesti = configHelper.getConfig("es.caib.ripea.app.data.dir") + execucioMassiva.getDocumentNom();
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

	@Transactional
	@Override
	public void saveExecucioMassiva(
			Long entitatId,
			ExecucioMassivaDto exec,
			List<ExecucioMassivaContingutDto> execElements,
			ElementTipusEnumDto elementTipus) throws NotFoundException, ValidationException {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				false, 
				true, 
				false);
	
		ExecucioMassivaEntity execucioMassiva = ExecucioMassivaEntity.getBuilder(
				exec.getTipus(),
				exec.getDataInici(),
				exec.getDataFi(),
				entitat,
				exec.getRolActual()).build();
	
		int ordre = 0;
		for (ExecucioMassivaContingutDto execElement: execElements) {
			
			String elementName = null;
			if (elementTipus == ElementTipusEnumDto.EXPEDIENT || elementTipus == ElementTipusEnumDto.DOCUMENT) {
				elementName = contingutRepository.findOne(execElement.getElementId()).getNom();
			} else if (elementTipus == ElementTipusEnumDto.INTERESSAT) {
				elementName = interessatRepository.findOne(execElement.getElementId()).getNom();
			} else if (elementTipus == ElementTipusEnumDto.ANOTACIO) {
				elementName = expedientPeticioRepository.findOne(execElement.getElementId()).getIdentificador();
			} else if (elementTipus == ElementTipusEnumDto.ANNEX) {
				elementName = registreAnnexRepository.findOne(execElement.getElementId()).getNom();
			}

			ExecucioMassivaContingutEntity emc = ExecucioMassivaContingutEntity.getBuilder(
					execucioMassiva, 
					execElement.getElementId(), 
					elementName,
					elementTipus, 
					ordre++).build();
			
			execucioMassiva.addContingut(emc);
			emc.updateEstatDataFi(
					execElement.getEstat(),
					execElement.getDataFi());
			
			Throwable excepcioRetorn = ExceptionHelper.getRootCauseOrItself(execElement.getThrowable());
			if (excepcioRetorn != null) {
				String error = ExecucioMassivaHelper.getExceptionString(
						emc,
						excepcioRetorn);
				
				emc.updateError(
						new Date(), 
						error);
			}
			execucioMassivaContingutRepository.save(emc);
		}
		
		execucioMassivaRepository.save(execucioMassiva);
	}

	@Override
	public void executarExecucioMassiva(Long execucioMassivaContingutId) {
//		execucioMassivaHelper.executarExecucioMassiva(execucioMassivaContingutId);
	}

	@Override
	public List<ExecucioMassivaDto> findExecucionsMassivesPerUsuari(Long entitatId, UsuariDto usuari, int pagina) throws NotFoundException {
		
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				true, false, false);
		
		Pageable paginacio = new PageRequest(pagina, 8, Direction.DESC, "dataInici");		
		
		List<ExecucioMassivaEntity> exmEntities = new ArrayList<ExecucioMassivaEntity>();
		if (usuari == null) {
			exmEntities = execucioMassivaRepository.findByEntitatIdOrderByCreatedDateDesc(entitat.getId(), paginacio);
		} else {
			UsuariEntity usuariEntity = usuariRepository.findByCodi(usuari.getCodi());
			exmEntities = execucioMassivaRepository.findByCreatedByAndEntitatIdOrderByCreatedDateDesc(usuariEntity, entitat.getId(), paginacio);
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
		ExecucioMassivaEntity execucioMassiva = execucioMassivaRepository.findOne(exm_id);
		if (execucioMassiva == null)
			throw new NotFoundException(exm_id, ExecucioMassivaEntity.class);
		
		List<ExecucioMassivaContingutEntity> continguts = execucioMassivaContingutRepository.findByExecucioMassivaOrderByOrdreAsc(execucioMassiva);
		List<ExecucioMassivaContingutDto> dtos = conversioTipusHelper.convertirList(continguts, ExecucioMassivaContingutDto.class);
		
		return dtos;
	}

	@Transactional
	@Override
	public void comprovarExecucionsMassives() {
		logger.trace("Execució tasca periòdica: Execucions massives");

		try {
			List<ExecucioMassivaEntity> massives = execucioMassivaRepository.getMassivesPerProcessar(new Date());

			if (massives != null) {
				for (ExecucioMassivaEntity execucioMassiva : massives) {

					EntitatDto entitat = conversioTipusHelper.convertir(execucioMassiva.getEntitat(), EntitatDto.class);
					ConfigHelper.setEntitat(entitat);
					
					if (execucioMassiva.getContinguts() != null) {

						if (ExecucioMassivaTipusDto.EXPORTAR_ZIP.equals(execucioMassiva.getTipus())) {
							//Cas de exportació a ZIP, no es individual per cada expedient, s'ha de executar per tots els expedients
							generaZipDocumentsExpedients(execucioMassiva);
						} else {
							for (ExecucioMassivaContingutEntity execucioMassivaEntity : execucioMassiva.getContinguts()) {
								executarExecucioMassivaContingut(execucioMassivaEntity.getId());
							}
						}
					}
					execucioMassiva.updateDataFi(new Date());
					execucioMassivaRepository.save(execucioMassiva);
					
					if (execucioMassiva.getEnviarCorreu()) {
						try {
							emailHelper.execucioMassivaFinalitzada(execucioMassiva);
						} catch (Exception e) {
							logger.error("No s'ha pogut enviar el correu de finalització d'accio massiva", e);
						}
					}
				}
			}

		} catch (Exception e) {
			logger.error("Error al fer execucio massiva", e);
		}
	}

	private void generaZipDocumentsExpedients(ExecucioMassivaEntity execucioMassiva) throws IOException {

		long t1 = Calendar.getInstance().getTimeInMillis();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ZipOutputStream out = new ZipOutputStream(baos);
		ZipEntry ze;
		int numFiles = 0;
		Set<String> nomsArxius = new HashSet<String>();
		List<DocumentDto> docsExp = new ArrayList<DocumentDto>();

		double maxMbFitxer	= Integer.parseInt(configHelper.getConfig("es.caib.ripea.segonpla.arxiu.maxMb", "100"));
		double actualMbFitxer = 0;
		Integer maxMinExec = Integer.parseInt(configHelper.getConfig("es.caib.ripea.segonpla.arxiu.maxTempsExec", "10"));
		long maxTempsProces	= t1+(maxMinExec*60*1000);
		boolean error = false;

		for (ExecucioMassivaContingutEntity execucioMassivaContingutEntity : execucioMassiva.getContinguts()) {

			ExpedientEntity expedient = expedientRepository.findOne(execucioMassivaContingutEntity.getElementId());
			List<ContingutEntity> contingutsExp = contingutRepository.findByPareAndEsborratAndOrdenat(expedient, 0);

			if (contingutsExp != null && contingutsExp.size()>0) {
				for (ContingutEntity contingutExpedient : contingutsExp) {
					if (ContingutTipusEnumDto.DOCUMENT.equals(contingutExpedient.getTipus())) {
						DocumentEntity documentEntity = (DocumentEntity)contingutExpedient;

						DocumentDto doc = new DocumentDto();
						doc.setId(documentEntity.getId());

						FitxerDto fitxerDto = documentHelper.getFitxerAssociat(documentEntity, null);
						actualMbFitxer = actualMbFitxer + (fitxerDto.getContingut().length / (1024.0 * 1024.0));

						doc.setFitxerContingut(fitxerDto.getContingut());

						String nomDoc = documentEntity.getFitxerNom().replaceAll("/", "_");
						if (documentEntity.getPare()!=null) {
							ContingutEntity pare = documentEntity.getPare();
							while (pare!=null) {
								nomDoc = pare.getNom().replaceAll("/", "_") + "/" + nomDoc;
								pare = pare.getPare(); //Pujam fins a l'arrel
							}
						} else {
							nomDoc = expedient.getCodi().replaceAll("/", "_") + "/" + nomDoc;
						}
						doc.setFitxerNom(nomDoc);
						docsExp.add(doc);
					}
				}
			}

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
					out.write(documentDto.getFitxerContingut());
					out.closeEntry();
					numFiles++;
				}
			}

			out.close();

			if (numFiles > 0) {
				String directoriDesti = configHelper.getConfig("es.caib.ripea.app.data.dir");
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
	}

	private String getZipRecursNom(String nomEntrada, Set<String> nomsArxius) {
		int contador = 0;
		for (String nom : nomsArxius) {
			if (nom!=null && nom.equals(nomEntrada)) {
				contador++;
			}
		}
		if (contador > 0) {
			nomEntrada = nomEntrada.substring(0, nomEntrada.lastIndexOf(".")) +
					" (" + contador + ")" +
					nomEntrada.substring(nomEntrada.lastIndexOf(".")+1);
		}
		nomsArxius.add(nomEntrada);
		return nomEntrada;
	}

	public Throwable executarExecucioMassivaContingut(Long execucioMassivaContingutId) {

		Throwable throwable = execucioMassivaHelper.executarExecucioMassivaContingutNewTransaction(execucioMassivaContingutId);
		
		if (throwable != null) {
			ExecucioMassivaContingutEntity emc = execucioMassivaContingutRepository.findOne(execucioMassivaContingutId);
			
			Throwable excepcioRetorn = ExceptionHelper.getRootCauseOrItself(throwable);
			
			String error = ExecucioMassivaHelper.getExceptionString(
					emc,
					excepcioRetorn);
			
			emc.updateError(
					new Date(), 
					error);
			
			execucioMassivaContingutRepository.save(emc);
			
			alertaHelper.crearAlerta(
					messageHelper.getMessage(
							"alertes.segon.pla.executar.execucio.massiva.error",
							new Object[] {execucioMassivaContingutId}),
					error,
					false,
					emc.getElementId());
			}

		return throwable;
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
			Long total = new Long(dto.getContingutIds().size());
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
