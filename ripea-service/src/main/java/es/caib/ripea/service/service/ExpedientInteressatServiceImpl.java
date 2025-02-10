package es.caib.ripea.service.service;

import es.caib.ripea.core.persistence.entity.*;
import es.caib.ripea.core.persistence.repository.ExpedientRepository;
import es.caib.ripea.core.persistence.repository.InteressatRepository;
import es.caib.ripea.service.helper.*;
import es.caib.ripea.service.intf.dto.*;
import es.caib.ripea.service.intf.exception.NotFoundException;
import es.caib.ripea.service.intf.exception.ValidationException;
import es.caib.ripea.service.intf.service.DadesExternesService;
import es.caib.ripea.service.intf.service.ExpedientInteressatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementació dels mètodes per a gestionar interessats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class ExpedientInteressatServiceImpl implements ExpedientInteressatService {

	@Autowired private InteressatRepository interessatRepository;
	@Autowired private ExpedientRepository expedientRepository;
	@Autowired private ConversioTipusHelper conversioTipusHelper;
	@Autowired private ContingutHelper contingutHelper;
	@Autowired private EntityComprovarHelper entityComprovarHelper;
	@Autowired private ExpedientInteressatHelper expedientInteressatHelper;
	@Autowired private DadesExternesService dadesExternesService;

	@Override
	public InteressatDto create(
			Long entitatId,
			Long expedientId,
			InteressatDto interessat, 
			String rolActual) {
		return create(entitatId, expedientId, null, interessat, true, rolActual);
	}

	@Override
	public InteressatDto create(
			Long entitatId,
			Long expedientId,
			Long interessatId,
			InteressatDto interessat,
			boolean propagarArxiu, 
			String rolActual) {
		return expedientInteressatHelper.create(
				expedientId,
				interessat,
				propagarArxiu,
				PermissionEnumDto.WRITE, 
				rolActual, 
				true);
	}
	
	@Override
	public InteressatDto createRepresentant(
			Long entitatId,
			Long expedientId,
			Long interessatId,
			InteressatDto representant,
			boolean propagarArxiu, 
			String rolActual) {
		return expedientInteressatHelper.createRepresentant(
				expedientId,
				interessatId,
				representant,
				propagarArxiu, 
				PermissionEnumDto.WRITE, 
				rolActual, 
				true);
	}
	@Override
	@Transactional
	public String importarInteressats(
			Long entitatId,
			Long expedientId,
			String rolActual,
			List<InteressatDto> interessats,
			List<Long> seleccionats) {
		
		if (seleccionats!=null && seleccionats.size()>0) {

			int numInteressatsUpd = 0;
			int numInteressatsIns = 0;
			Map<String, String> errorsInteressats = new HashMap<String, String>();
			
			if (interessats!=null && interessats.size()>0) {
				
				//Recuperam tots els InteressatDto del expedient, siguin interessats arrel o representants.
				List<InteressatEntity> interessatsActualsExp = interessatRepository.findByExpedientId(expedientId);
				
				//Recorrem els interessats del JSON que s'ha importat
				for (InteressatDto interessat : interessats) {
					logger.debug(" - Importació del interessat "+interessat.getDocumentNum()+" a l'expedient "+expedientId);
					//Si l'usuari ha marcat que el interessat s'ha de importar al expedient actual...
					if (seleccionats.contains(interessat.getId())) {
						
						InteressatEntity interessatProcessar = getInteressatActualExpedientByDocNum(interessatsActualsExp, interessat.getDocumentNum());
						if (interessatProcessar==null) {
							//El create, crea el interessat associat al expedient, sense FK cap a representant, i amb es_representant=false
							//És a dir, un interessat arrel del expedient.

							InteressatDto interessatCreatDto = create(entitatId, expedientId, interessat, rolActual);
							interessatProcessar = interessatRepository.getOne(interessatCreatDto.getId());
							interessatsActualsExp.add(interessatProcessar);
							numInteressatsIns++;
							logger.debug("   > Interessat creat perque no existia al expedient.");

						} else {
							//El merge no toca ni la FK cap a representant, ni l'atribut es_representant
							//per tant si era interessat haurà actualitzat el interessat, i si era representant, el representant.
							interessatProcessar = expedientInteressatHelper.mergeInteressat(interessatProcessar.getId(), interessat);
							numInteressatsUpd++;
							logger.debug("   > Interessat mergeat perque ja existia al expedient.");
						}
					}
				}
				
				for (InteressatDto interessat : interessats) {
					if (seleccionats.contains(interessat.getId())) {
						//Un cop actualizades les dades generiques dels interessats, actualitzam les relacions interessat-representant entre ells
						if (interessat.getRepresentant()!=null) {
							InteressatEntity interessatProcessar = getInteressatActualExpedientByDocNum(interessatsActualsExp, interessat.getDocumentNum());
							logger.debug(" - Importació del representant "+interessat.getRepresentant().getDocumentNum()+" del interessat "+interessat.getDocumentNum()+" a l'expedient "+expedientId);
							//Si el representant amb numDoc no existeix al expedient (sigui com a representant o com a interessat), es crea com a nou interessat
							InteressatEntity representantProcessar = getInteressatActualExpedientByDocNum(interessatsActualsExp, interessat.getRepresentant().getDocumentNum());
							if (representantProcessar==null) {

								InteressatDto representantCreatDto = create(entitatId, expedientId, interessat.getRepresentant(), rolActual);
								representantProcessar = interessatRepository.getOne(representantCreatDto.getId());
								representantProcessar.updateEsRepresentant(true);
								logger.debug("   > S'ha creat el representant perque no existia al expedient.");

							} else {
								
								representantProcessar = expedientInteressatHelper.mergeInteressat(representantProcessar.getId(), interessat.getRepresentant());
								logger.debug("   > S'ha mergeat el representant perque ja existia al expedient.");
							}
							
							//Ara tenim el representant actualitzat o creat, pero encara no apunta al interessat que estam important
							interessatProcessar.setRepresentant(representantProcessar);
							logger.debug("   > El representant "+interessat.getRepresentant().getDocumentNum()+" s'ha associat al interessat "+interessatProcessar.getDocumentNum()+".");
						}
					}
				}
			}
			
			String resultatStr = "S'han importat <b>"+numInteressatsIns+"</b> nous interessats, i <b>"+numInteressatsUpd+"</b> s'han actualitzat.";
			if (errorsInteressats.size()>0) {
				resultatStr+="<br/>Els seguents interessats no s'han pogut importar:";
				for (Map.Entry<String, String> entry : errorsInteressats.entrySet()) {
					resultatStr+="<br/> - "+entry.getKey()+": "+entry.getValue();
				}
			}
			return resultatStr;
			
		} else {
			return "No s'ha seleccionat interessats per importar.";
		}
	}

	private InteressatEntity getInteressatActualExpedientByDocNum(List<InteressatEntity> interessatsActualsExp, String docNum) {
		if (interessatsActualsExp!=null) {
			for (InteressatEntity interessatExistent : interessatsActualsExp) {
				if (interessatExistent.getDocumentNum().equalsIgnoreCase(docNum)) {
					return interessatExistent;
				}
			}
		}
		return null;
	}
	
	@Transactional
	@Override
	public InteressatDto update(
			Long entitatId,
			Long expedientId,
			InteressatDto interessat, 
			String rolActual) {
		return expedientInteressatHelper.update(
//				entitatId,
				expedientId,
				null,
				interessat,
				rolActual, 
				true, 
				true);
	}

	@Transactional
	@Override
	public InteressatDto update(
			Long entitatId,
			Long expedientId,
			Long representatId,
			InteressatDto interessat, 
			String rolActual) {
		return expedientInteressatHelper.update(
//				entitatId,
				expedientId,
				representatId,
				interessat,
				rolActual, 
				true, 
				true);
	}

	@Transactional
	@Override
	public void delete(
			Long entitatId,
			Long expedientId,
			Long interessatId, 
			String rolActual) {
		expedientInteressatHelper.delete(
				entitatId,
				expedientId,
				interessatId,
				rolActual);
	}

	@Transactional
	@Override
	public void deleteRepresentant(
			Long entitatId,
			Long expedientId,
			Long interessatId,
			Long representantId, 
			String rolActual) {

		expedientInteressatHelper.deleteRepresentant(
				entitatId,
				expedientId,
				interessatId,
				representantId,
				rolActual);
	}
	
	@Override
	public Exception guardarInteressatsArxiu(
			Long expId) {
		return expedientInteressatHelper.guardarInteressatsArxiu(expId);
	}
	

	@Transactional(readOnly = true)
	@Override
	public InteressatDto findById(Long id, boolean consultarDadesExternes) {
		logger.debug("Consulta de l'interessat ("
				+ "id=" + id + ")");
		InteressatEntity interessat = entityComprovarHelper.comprovarInteressat(
				null,
				id);
//		##### Hi ha un mapeig dins conversioTipusHelper ###
//		if (interessat instanceof InteressatPersonaFisicaEntity)
//			return conversioTipusHelper.convertir(
//					(InteressatPersonaFisicaEntity)interessat,
//					InteressatPersonaFisicaDto.class);
//		else if (interessat instanceof InteressatPersonaJuridicaEntity)
//			return conversioTipusHelper.convertir(
//					interessat,
//					InteressatPersonaJuridicaDto.class);
//		else if (interessat instanceof InteressatAdministracioEntity)
//			return conversioTipusHelper.convertir(
//					interessat,
//					InteressatAdministracioDto.class);
		
		InteressatDto interessatDto = conversioTipusHelper.convertir(
				interessat,
				InteressatDto.class);
		if (consultarDadesExternes) {
			String provinciaCodi = interessat.getProvincia();
			for (PaisDto paisDto : dadesExternesService.findPaisos()) {
				if (paisDto.getCodi().equals(interessat.getPais())) {
					interessatDto.setPaisNom(paisDto.getNom());
				}
			}
			for (ProvinciaDto provinciaDto : dadesExternesService.findProvincies()) {
				if (provinciaDto.getCodi().equals(interessat.getProvincia())) {
					interessatDto.setProvinciaNom(provinciaDto.getNom());
				}
			}
			for (MunicipiDto municipiDto : dadesExternesService.findMunicipisPerProvincia(provinciaCodi)) {
				if (municipiDto.getCodi().equals(interessat.getMunicipi())) {
					interessatDto.setMunicipiNom(municipiDto.getNom());
				}
			}
			InteressatEntity representant = interessat.getRepresentant();
			if (representant != null) {
				for (PaisDto paisDto : dadesExternesService.findPaisos()) {
					if (paisDto.getCodi().equals(representant.getPais())) {
						interessatDto.getRepresentant().setPaisNom(paisDto.getNom());
					}
				}
				for (ProvinciaDto provinciaDto : dadesExternesService.findProvincies()) {
					if (provinciaDto.getCodi().equals(representant.getProvincia())) {
						interessatDto.getRepresentant().setProvinciaNom(provinciaDto.getNom());
					}
				}
				for (MunicipiDto municipiDto : dadesExternesService.findMunicipisPerProvincia(provinciaCodi)) {
					if (municipiDto.getCodi().equals(representant.getMunicipi())) {
						interessatDto.getRepresentant().setMunicipiNom(municipiDto.getNom());
					}
				}
			}
		}
		return interessatDto;
	}

	@Transactional(readOnly = true)
	@Override
	public InteressatDto findRepresentantById(
			Long interessatId,
			Long id) {
		logger.debug("Consulta de l'interessat ("
				+ "id=" + id + ")");
		InteressatEntity interessat = entityComprovarHelper.comprovarInteressat(
				null,
				interessatId);
		if (interessat.getRepresentantId() == null || !interessat.getRepresentantId().equals(id)) {
			throw new ValidationException(
					id,
					InteressatEntity.class,
					"El representant especificat (id=" + id + ") no pertany a l'interessat (id=" + interessatId + ")");
		}
		InteressatEntity representant = HibernateHelper.deproxy(interessat.getRepresentant());
		return conversioTipusHelper.convertir(
				representant,
				InteressatDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public List<InteressatDto> findByExpedient(
			Long entitatId,
			Long expedientId,
			boolean nomesAmbNotificacioActiva) {
		logger.debug("Consulta interessats de l'expedient ("
				+ "entitatId=" + entitatId + ", "
				+ "expedientId=" + expedientId + ")");
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				expedientId,
				false,
				true,
				false,
				false,
				false,
				null);
		
		List<InteressatEntity> interessats = new ArrayList<>();
		if (nomesAmbNotificacioActiva) {
			interessats = expedientInteressatHelper.findByExpedientAndNotRepresentantAndAmbDadesPerNotificacio(
					expedient);
		} else {
			interessats = interessatRepository.findByExpedientAndNotRepresentant(
					expedient);
		}

		List<InteressatDto> resposta = new ArrayList<InteressatDto>();
		for (InteressatEntity interessat: interessats) {
			resposta.add(conversioTipusHelper.convertir(
					interessat,
					InteressatDto.class));
		}
		return resposta;
	}

	@Transactional(readOnly = true)
	@Override
	public long countByExpedient(
			Long entitatId,
			Long expedientId) {
		logger.debug("Consulta interessats de l'expedient ("
				+ "entitatId=" + entitatId + ", "
				+ "expedientId=" + expedientId + ")");
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				expedientId,
				false,
				true,
				false,
				false,
				false,
				null);
		return interessatRepository.countByExpedient(
				expedient);
	}

	@Transactional(readOnly = true)
	@Override
	public List<InteressatDto> findAmbDocumentPerNotificacio(
			Long entitatId,
			Long documentId) {
		logger.debug("Consulta interessats del document per notificacions ("
				+ "entitatId=" + entitatId + ", "
				+ "documentId=" + documentId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false, false, false);
		DocumentEntity document = entityComprovarHelper.comprovarDocument(
				entitat,
				null,
				documentId,
				false,
				false,
				false,
				false);
		ExpedientEntity expedient = contingutHelper.getExpedientSuperior(
				document,
				false,
				false,
				true, null);
		if (expedient == null) {
			throw new ValidationException(
					documentId,
					DocumentEntity.class,
					"El document no pertany a cap expedient");
		}
		List<InteressatEntity> interessats = interessatRepository.findByExpedientPerNotificacions(
				expedient);
		List<InteressatDto> resposta = new ArrayList<InteressatDto>();
		for (InteressatEntity interessat: interessats) {
			if (interessat instanceof InteressatPersonaFisicaEntity)
				resposta.add(conversioTipusHelper.convertir(
						interessat,
						InteressatPersonaFisicaDto.class));
			else if (interessat instanceof InteressatPersonaJuridicaEntity)
				resposta.add(conversioTipusHelper.convertir(
						interessat,
						InteressatAdministracioDto.class));
		}
		return resposta;
	}

	@Transactional(readOnly = true)
	@Override
	public List<InteressatPersonaFisicaDto> findByFiltrePersonaFisica(
			String documentNum,
			String nom,
			String llinatge1,
			String llinatge2,
			Long expedientId) {
		logger.debug("Consulta interessats de tipus ciutadà ("
				+ "nom=" + nom + ", "
				+ "documentNum=" + documentNum + ", "
				+ "llinatge1=" + llinatge1 + ", "
				+ "llinatge2=" + llinatge2 + ")");
		
		ExpedientEntity expedient = expedientRepository.getOne(expedientId);
		return conversioTipusHelper.convertirList(
				interessatRepository.findByFiltrePersonaFisica(
						nom == null || nom.isEmpty(),
						nom,
						documentNum == null || documentNum.isEmpty(),
						documentNum,
						llinatge1 == null || llinatge1.isEmpty(),
						llinatge1,
						llinatge2 == null || llinatge2.isEmpty(),
						llinatge2,
						expedient),
				InteressatPersonaFisicaDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public List<InteressatPersonaJuridicaDto> findByFiltrePersonaJuridica(
			String documentNum,
			String raoSocial,
			Long expedientId) {
		logger.debug("Consulta interessats de tipus ciutadà ("
				+ "raoSocial=" + raoSocial + ", "
				+ "documentNum=" + documentNum + ")");
		
		ExpedientEntity expedient = expedientRepository.getOne(expedientId);
		return conversioTipusHelper.convertirList(
				interessatRepository.findByFiltrePersonaJuridica(
						documentNum == null || documentNum.isEmpty(),
						documentNum,
						raoSocial == null || raoSocial.isEmpty(),
						raoSocial,
						expedient),
				InteressatPersonaJuridicaDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public List<InteressatAdministracioDto> findByFiltreAdministracio(
			String organCodi,
			Long expedientId) {
		logger.debug("Consulta interessats de tipus administració ("
				+ "organCodi=" + organCodi + ")");
		
		ExpedientEntity expedient = expedientRepository.getOne(expedientId);
		List<InteressatAdministracioEntity> administracions = interessatRepository.findByFiltreAdministracio(
				organCodi == null || organCodi.isEmpty(),
				organCodi,
				expedient);
		return conversioTipusHelper.convertirList(
				administracions,
				InteressatAdministracioDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public InteressatDto findByExpedientAndDocumentNum(
			String documentNum,
			Long expedientId) throws NotFoundException {
		ExpedientEntity expedient = expedientRepository.getOne(expedientId);
		return conversioTipusHelper.convertir(
				interessatRepository.findByExpedientAndDocumentNum(expedient, documentNum),
				InteressatDto.class);
	}
	
	private static final Logger logger = LoggerFactory.getLogger(ExpedientInteressatServiceImpl.class);

	@Transactional(readOnly = true)
	@Override
	public List<InteressatDto> findByText(String text) {
		return conversioTipusHelper.convertirList(
				interessatRepository.findByText(text),
				InteressatDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public InteressatDto findByDocumentNum(String documentNum) throws NotFoundException {
		return conversioTipusHelper.convertir(
				interessatRepository.findByDocumentNum(documentNum).get(0),
				InteressatDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public Long findExpedientIdByInteressat(Long interessatId) {
		logger.debug("Consulta de expedient Id per interessat (interessatId=" + interessatId + ")");
		InteressatEntity interessat = entityComprovarHelper.comprovarInteressat(
				null,
				interessatId);

		if (interessat != null && interessat.getExpedient() != null)
			return interessat.getExpedient().getId();
		return null;
	}
}
