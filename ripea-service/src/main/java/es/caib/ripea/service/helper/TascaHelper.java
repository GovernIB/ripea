package es.caib.ripea.service.helper;

import es.caib.ripea.persistence.entity.*;
import es.caib.ripea.persistence.repository.*;
import es.caib.ripea.service.intf.config.PropertyConfig;
import es.caib.ripea.service.intf.dto.DocumentNotificacioEstatEnumDto;
import es.caib.ripea.service.intf.dto.ItemValidacioTascaEnum;
import es.caib.ripea.service.intf.dto.MetaExpedientTascaValidacioDto;
import es.caib.ripea.service.intf.exception.NotFoundException;
import es.caib.ripea.service.intf.utils.Utils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class TascaHelper {
	
	@Autowired private ExpedientTascaRepository expedientTascaRepository;
	@Autowired private MetaDadaRepository metaDadaRepository;
	@Autowired private DadaRepository dadaRepository;
	@Autowired private MetaDocumentRepository metaDocumentRepository;
	@Autowired private DocumentRepository documentRepository;
	@Autowired private DocumentNotificacioRepository documentNotificacioRepository;
	@Autowired private ConfigHelper configHelper;
	@Autowired private ConversioTipusHelper conversioTipusHelper;

	public List<MetaExpedientTascaValidacioDto> getValidacionsPendentsTasca(Long expedientTascaId) {
		List<MetaExpedientTascaValidacioDto> resultat = new ArrayList<MetaExpedientTascaValidacioDto>();
		
		ExpedientTascaEntity expedientTascaEntity = expedientTascaRepository.getOne(expedientTascaId);
		List<MetaExpedientTascaValidacioEntity> validacionsTasca = expedientTascaEntity.getMetaTasca().getValidacions();
		
		if (validacionsTasca!=null && validacionsTasca.size()>0) {
			
			List<DadaEntity> dadesExpedient = dadaRepository.findByNode(expedientTascaEntity.getExpedient());
			List<DocumentEntity> documentsExpedient = documentRepository.findByExpedientAndEsborrat(expedientTascaEntity.getExpedient(), 0);
			
			for (MetaExpedientTascaValidacioEntity validacioTasca: validacionsTasca) {
				
				if (validacioTasca.isActiva()) {
			
					boolean validacioOk = false;
					
					if (ItemValidacioTascaEnum.DADA.equals(validacioTasca.getItemValidacio())) {
						
						//La mateixa funció s'utilitza per guardar els valors de la pipella de dades del expedient.					
						MetaDadaEntity metaDadaProcediment = metaDadaRepository.findById(validacioTasca.getItemId()).orElse(null);
						
						if (metaDadaProcediment == null || !metaDadaProcediment.isActiva()) {
							validacioOk = true; //Si la meta-dada no esta activa actualment al procediment, no es valida perque no es podrá aportar...
						} else {
							for (DadaEntity dadaExp: dadesExpedient) {
								if (dadaExp.getMetaDada().getId().equals(validacioTasca.getItemId())) {
									switch (validacioTasca.getTipusValidacio()) {
									case AP:
										if (Utils.hasValue(dadaExp.getValorComString())) {
											validacioOk = true;
										}
										break;
									default:
										break;
									}
								}
							}
						}
						
					} else if (ItemValidacioTascaEnum.DOCUMENT.equals(validacioTasca.getItemValidacio())) {
						
						//Anam a cercar la dada del expedient, del tipus (metaDocumentId) igual al itemId de la validació
						MetaDocumentEntity metaDocProcediment = metaDocumentRepository.findById(validacioTasca.getItemId()).orElse(null);
						
						if (metaDocProcediment==null || !metaDocProcediment.isActiu()) {
							validacioOk = true; //Si el tipus de document no esta actiu acualment al procediment, no es valida perque no es podrá aportar...
						} else {
							for (DocumentEntity docExp: documentsExpedient) {
								if (docExp.getMetaDocument().getId().equals(validacioTasca.getItemId())) {
									switch (validacioTasca.getTipusValidacio()) {
									case AP:
										//S'ha trobat un document del tipus definit a la validació, no fa falta validar res més
										validacioOk = true;
										break;
									case AP_FI:
										if (docExp.isFirmat()) { validacioOk = true; }
										break;
									case AP_FI_NI:
										DocumentNotificacioEstatEnumDto darreraNot_I = documentNotificacioRepository.findLastEstatNotificacioByDocument(docExp);
										if (darreraNot_I!=null) { validacioOk = true; }
										break;
									case AP_FI_NF:
										DocumentNotificacioEstatEnumDto darreraNot_F = documentNotificacioRepository.findLastEstatNotificacioByDocument(docExp);
										if (DocumentNotificacioEstatEnumDto.FINALITZADA.equals(darreraNot_F) || 
											DocumentNotificacioEstatEnumDto.FINALITZADA_AMB_ERRORS.equals(darreraNot_F)) { 
												validacioOk = true;
										}
										break;
									default:
										break;
									}
								}
							}
						}
					}
					
					if (!validacioOk) {
						resultat.add(conversioTipusHelper.convertir(validacioTasca, MetaExpedientTascaValidacioDto.class));
					}
				}
			}
		}		
		
		return resultat;
	}
	
	public boolean shouldNotifyAboutDeadline(ExpedientTascaEntity expedientTascaEntity) {

		try {

			boolean shouldNotifyAboutDeadline = false;
			int preavisDataLimitEnDies = configHelper.getAsInt(PropertyConfig.TASCA_PREAVIS_DATA_LIMIT, 3);

			if (expedientTascaEntity.getDataLimit() != null) {
				if ((new Date()).after(new DateTime(expedientTascaEntity.getDataLimit()).minusDays(preavisDataLimitEnDies).toDate())) {
					shouldNotifyAboutDeadline = true;
				}
			}

			return shouldNotifyAboutDeadline;

		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	public ExpedientTascaEntity comprovarTasca(Long expedientTascaId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		ExpedientTascaEntity tasca = expedientTascaRepository.findById(expedientTascaId).orElse(null);
		
		if (tasca == null)
			throw new NotFoundException(expedientTascaId, ExpedientTascaEntity.class);
		
		if (tasca.getResponsables() != null) {
			boolean pemitted = false;
			for (UsuariEntity responsable : tasca.getResponsables()) {
				if (responsable.getCodi().equals(auth.getName())) {
					pemitted = true;
				}
			}
			UsuariEntity delegat = tasca.getDelegat();
			if (delegat != null && delegat.getCodi().equals(auth.getName())) {
				pemitted = true;
			}
			if (!pemitted) {
				throw new SecurityException("Sense permisos per accedir la tasca ("
						+ "tascaId=" + tasca.getId() + ", "
						+ "usuari=" + auth.getName() + ")");
			}
		}
		
		return tasca;
	}

}
