/**
 * 
 */
package es.caib.ripea.core.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import es.caib.ripea.core.api.dto.DocumentEnviamentEstatEnumDto;
import es.caib.ripea.core.api.dto.DocumentNotificacioEstatEnumDto;
import es.caib.ripea.core.api.dto.EventTipusEnumDto;
import es.caib.ripea.core.api.dto.PermisDto;
import es.caib.ripea.core.api.dto.PrincipalTipusEnumDto;
import es.caib.ripea.core.api.dto.TascaEstatEnumDto;
import es.caib.ripea.core.entity.CarpetaEntity;
import es.caib.ripea.core.entity.ContingutEntity;
import es.caib.ripea.core.entity.DocumentEntity;
import es.caib.ripea.core.entity.DocumentNotificacioEntity;
import es.caib.ripea.core.entity.DocumentPortafirmesEntity;
import es.caib.ripea.core.entity.EmailPendentEnviarEntity;
import es.caib.ripea.core.entity.ExecucioMassivaEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.entity.ExpedientTascaEntity;
import es.caib.ripea.core.entity.MetaNodeEntity;
import es.caib.ripea.core.entity.UsuariEntity;
import es.caib.ripea.core.repository.EmailPendentEnviarRepository;
import es.caib.ripea.plugin.usuari.DadesUsuari;

/**
 * Mètodes per a l'enviament de correus.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class EmailHelper {

	private static final String PREFIX_RIPEA = "[RIPEA]";

	@Autowired
	private CacheHelper cacheHelper;
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private PermisosHelper permisosHelper;
	@Autowired
	private ExpedientHelper expedientHelper;
	@Autowired
	private JavaMailSender mailSender;
	@Autowired
	private UsuariHelper usuariHelper;
	@Autowired
	private EmailPendentEnviarRepository emailPendentEnviarRepository;

	public void contingutAgafatPerAltreUsusari(
			ContingutEntity contingut,
			UsuariEntity usuariOriginal,
			UsuariEntity usuariNou) {
		
		if (usuariOriginal.getEmail() != null) {
			
			String from = getRemitent();
			String to = usuariOriginal.getEmail();
			String tipus = "desconegut";
			if (contingut instanceof ExpedientEntity) {
				tipus = "expedient";
			} else if (contingut instanceof DocumentEntity) {
				tipus = "document";
			} else if (contingut instanceof CarpetaEntity) {
				tipus = "carpeta";
			}
			String subject = PREFIX_RIPEA + " Element de l'escriptori agafat per un altre usuari: (" + tipus + ") " + contingut.getNom();
			String text = 
					"Informació de l'element de l'escriptori:\n" +
					"\tEntitat: " + contingut.getEntitat().getNom() + "\n" +
					"\tTipus: " + tipus + "\n" +
					"\tNom: " + contingut.getNom() + "\n\n" + 
					"\tPersona que ho ha agafat: " + usuariNou.getNom() + "(" + usuariNou.getCodi() + ").";
			

			
			if (usuariOriginal.isRebreEmailsAgrupats()) {
				EmailPendentEnviarEntity enitity = EmailPendentEnviarEntity.getBuilder(
						from,
						to,
						subject,
						text,
						EventTipusEnumDto.AGAFAT_ALTRE_USUARI)
						.build();
				emailPendentEnviarRepository.save(enitity);
				
			} else {
				
				logger.debug("Enviant correu electrònic per a contingut agafat per altre usuari (" +
						"contingutId=" + contingut.getId() + ")");
				SimpleMailMessage missatge = new SimpleMailMessage();
				missatge.setFrom(from);
				missatge.setTo(to);
				missatge.setSubject(subject);
				missatge.setText(text);
				mailSender.send(missatge);
			}
		}
		
	}

	public void execucioMassivaFinalitzada(
			ExecucioMassivaEntity em) {
		logger.debug("Enviant correu electrònic per a execució massiva finalitzada (" + 
			"execucioMassivaId=" + em.getId() + ")");
		SimpleMailMessage missatge = new SimpleMailMessage();
		if (emplenarDestinatariAmbUsuari(
				missatge,
				em.getCreatedBy().getCodi())) {
			missatge.setFrom(getRemitent());
			missatge.setSubject(PREFIX_RIPEA + " Execucio massiva finalitzada: " + em.getTipus());
			missatge.setText(
					"Execució massiva finalitzada:\n" +
					"\tId: " + em.getId() + "\n" +
					"\tTipus: " + em.getTipus() + "\n" +
					"\tData Inici: " + em.getDataInici() + "\n" +
					"\tData Fi: " + em.getDataFi() + "\n" +
					"\tContinguts: " + em.getContinguts().size() + "\n");
			mailSender.send(missatge);
		}
	}

	public void canviEstatDocumentPortafirmes(
			DocumentPortafirmesEntity documentPortafirmes) {
		logger.debug("Enviant correu electrònic per a canvi d'estat de document al portafirmes (" +
			"documentPortafirmesId=" + documentPortafirmes.getId() + ")");
		
		DocumentEntity document = documentPortafirmes.getDocument();
		ExpedientEntity expedient = document.getExpedient();
		Set<DadesUsuari> responsables = getGestors(expedient);
		
		String from = getRemitent();
		String subject = PREFIX_RIPEA + " Canvi d'estat de document enviat a portafirmes";
		String estat = (documentPortafirmes.getEstat() == DocumentEnviamentEstatEnumDto.PROCESSAT) ? "FIRMAT" : documentPortafirmes.getEstat().toString();
		String text = 
				"Informació del document:\n" +
						"\tEntitat: " + expedient.getEntitat().getNom() + "\n" +
						"\tExpedient nom: " + expedient.getNom() + "\n" +
						"\tExpedient núm.: " + expedientHelper.calcularNumero(expedient) + "\n" +
						"\tDocument nom: " + document.getNom() + "\n" +
						"\tDocument tipus.: " + document.getMetaDocument().getNom() + "\n" +
						"\tDocument fitxer: " + document.getFitxerNom() + "\n\n" +
						"Estat del document:" + estat + "\n";
		
		List<String> destinatarisAgrupats = new ArrayList<String>();
		List<String> destinatarisNoAgrupats = new ArrayList<String>();
		
		for (DadesUsuari responsable : responsables) {
			if (responsable != null && (responsable.getEmail() != null && !responsable.getEmail().isEmpty())) {
				UsuariEntity usuari = usuariHelper.getUsuariByCodi(responsable.getCodi());
				if (usuari != null && usuari.isRebreEmailsAgrupats()) {
					destinatarisAgrupats.add(responsable.getEmail());
				} else {
					destinatarisNoAgrupats.add(responsable.getEmail());
				}
			}
		}
		
		if (destinatarisNoAgrupats != null && !destinatarisNoAgrupats.isEmpty()) {
			String[] to = destinatarisNoAgrupats.toArray(new String[destinatarisNoAgrupats.size()]);
			SimpleMailMessage missatge = new SimpleMailMessage();
			missatge.setFrom(from);
			missatge.setTo(to);
			missatge.setSubject(subject);
			missatge.setText(text);
			logger.debug(missatge.toString());
			mailSender.send(missatge);
		}
		
		if (destinatarisAgrupats != null && !destinatarisAgrupats.isEmpty()) {
			for (String dest : destinatarisAgrupats) {
				EmailPendentEnviarEntity enitity = EmailPendentEnviarEntity.getBuilder(
						from,
						dest,
						subject,
						text,
						EventTipusEnumDto.CANVI_ESTAT_PORTAFIRMES)
						.build();
				emailPendentEnviarRepository.save(enitity);
			}
		}
	}
	

	public void canviEstatNotificacio(
			DocumentNotificacioEntity documentNotificacio,
			DocumentEnviamentEstatEnumDto estatAnterior) {
		logger.debug("Enviant correu electrònic per a canvi d'estat de notificació (" +
			"documentNotificacioId=" + documentNotificacio.getId() + ")");
		DocumentEntity document = documentNotificacio.getDocument();
		ExpedientEntity expedient = document.getExpedient();
		Set<DadesUsuari> responsables = getGestors(expedient);
		
		String from = getRemitent();
		String subject = PREFIX_RIPEA + " Canvi d'estat de notificació";
		String estat = (documentNotificacio.getEstat() == DocumentEnviamentEstatEnumDto.PROCESSAT) ? "ENTREGAT" : documentNotificacio.getEstat().toString();
		String text = 
				"Informació del document:\n" +
				"\tEntitat: " + expedient.getEntitat().getNom() + "\n" +
				"\tExpedient nom: " + expedient.getNom() + "\n" +
				"\tExpedient núm.: " + expedientHelper.calcularNumero(expedient) + "\n" +
				"\tDocument nom: " + document.getNom() + "\n" +
				"\tDocument tipus.: " + document.getMetaDocument().getNom() + "\n" +
				"\tDocument fitxer: " + document.getFitxerNom() + "\n\n" +
				"Estat anterior:" + estatAnterior + "\n" +
				"Estat actual:" + estat + "\n";
		
		List<String> destinatarisAgrupats = new ArrayList<String>();
		List<String> destinatarisNoAgrupats = new ArrayList<String>();
		
		for (DadesUsuari responsable : responsables) {
			if (responsable != null && (responsable.getEmail() != null && !responsable.getEmail().isEmpty())) {
				UsuariEntity usuari = usuariHelper.getUsuariByCodi(responsable.getCodi());
				if (usuari != null && usuari.isRebreEmailsAgrupats()) {
					destinatarisAgrupats.add(responsable.getEmail());
				} else {
					destinatarisNoAgrupats.add(responsable.getEmail());
				}
			}
		}
		
		if (destinatarisNoAgrupats != null && !destinatarisNoAgrupats.isEmpty()) {
			String[] to = destinatarisNoAgrupats.toArray(new String[destinatarisNoAgrupats.size()]);
			SimpleMailMessage missatge = new SimpleMailMessage();
			missatge.setFrom(from);
			missatge.setTo(to);
			missatge.setSubject(subject);
			missatge.setText(text);
			logger.debug(missatge.toString());
			mailSender.send(missatge);
		}
		
		if (destinatarisAgrupats != null && !destinatarisAgrupats.isEmpty()) {
			for (String dest : destinatarisAgrupats) {
				EmailPendentEnviarEntity enitity = EmailPendentEnviarEntity.getBuilder(
						from,
						dest,
						subject,
						text,
						EventTipusEnumDto.CANVI_ESTAT_NOTIFICACIO)
						.build();
				emailPendentEnviarRepository.save(enitity);
			}
		}
	}
	
	public void canviEstatNotificacio(
			DocumentNotificacioEntity documentNotificacio,
			DocumentNotificacioEstatEnumDto estatAnterior) {
		logger.debug("Enviant correu electrònic per a canvi d'estat de notificació (" +
			"documentNotificacioId=" + documentNotificacio.getId() + ")");
		
		DocumentEntity document = documentNotificacio.getDocument();
		ExpedientEntity expedient = document.getExpedient();
		Set<DadesUsuari> responsables = getGestors(expedient);
		
		String from = getRemitent();
		String subject = PREFIX_RIPEA + " Canvi d'estat de notificació";
		String estat = documentNotificacio.getNotificacioEstat().toString();
		String text = 
				"Informació del document:\n" +
				"\tEntitat: " + expedient.getEntitat().getNom() + "\n" +
				"\tExpedient nom: " + expedient.getNom() + "\n" +
				"\tExpedient núm.: " + expedientHelper.calcularNumero(expedient) + "\n" +
				"\tDocument nom: " + document.getNom() + "\n" +
				"\tDocument tipus.: " + document.getMetaDocument().getNom() + "\n" +
				"\tDocument fitxer: " + document.getFitxerNom() + "\n\n" +
				"Estat anterior:" + estatAnterior.toString() + "\n" +
				"Estat actual:" + estat + "\n";
		
		List<String> destinatarisAgrupats = new ArrayList<String>();
		List<String> destinatarisNoAgrupats = new ArrayList<String>();
		
		for (DadesUsuari responsable : responsables) {
			if (responsable != null && (responsable.getEmail() != null && !responsable.getEmail().isEmpty())) {
				UsuariEntity usuari = usuariHelper.getUsuariByCodi(responsable.getCodi());
				if (usuari != null && usuari.isRebreEmailsAgrupats()) {
					destinatarisAgrupats.add(responsable.getEmail());
				} else {
					destinatarisNoAgrupats.add(responsable.getEmail());
				}
			}
		}
		
		if (destinatarisNoAgrupats != null && !destinatarisNoAgrupats.isEmpty()) {
			String[] to = destinatarisNoAgrupats.toArray(new String[destinatarisNoAgrupats.size()]);
			SimpleMailMessage missatge = new SimpleMailMessage();
			missatge.setFrom(from);
			missatge.setTo(to);
			missatge.setSubject(subject);
			missatge.setText(text);
			logger.debug(missatge.toString());
			mailSender.send(missatge);
		}
		
		if (destinatarisAgrupats != null && !destinatarisAgrupats.isEmpty()) {
			for (String dest : destinatarisAgrupats) {
				EmailPendentEnviarEntity enitity = EmailPendentEnviarEntity.getBuilder(
						from,
						dest,
						subject,
						text,
						EventTipusEnumDto.CANVI_ESTAT_NOTIFICACIO)
						.build();
				emailPendentEnviarRepository.save(enitity);
			}
		}
	}
	
	
	public void enviarEmailCanviarEstatTasca(
			ExpedientTascaEntity expedientTascaEntity,
			TascaEstatEnumDto estatAnterior) {
		logger.debug("Enviant correu electrònic per a canvis de tasca (" +
			"tascaId=" + expedientTascaEntity.getId() + ")");


		enviarEmailCanviarEstatTasca(expedientTascaEntity, estatAnterior, getGestors(expedientTascaEntity.getExpedient()), false);
		
		if (expedientTascaEntity.getResponsable() != null && expedientTascaEntity.getResponsable().getEmail() != null && !expedientTascaEntity.getResponsable().getEmail().isEmpty()) {
			
			enviarEmailCanviarEstatTasca(expedientTascaEntity, estatAnterior,  new HashSet<DadesUsuari>(Arrays.asList(pluginHelper.dadesUsuariFindAmbCodi((expedientTascaEntity.getResponsable().getCodi())))), true);
		}

	}	
	
	
	
	private Set<DadesUsuari> getGestors(ExpedientEntity expedient) {
		Set<DadesUsuari> responsables = new HashSet<DadesUsuari>();
		List<PermisDto> permisos = permisosHelper.findPermisos(
				expedient.getMetaNode().getId(),
				MetaNodeEntity.class);
		for (PermisDto permis: permisos) {
			if (permis.isWrite()) {
				try {
					if (PrincipalTipusEnumDto.USUARI == permis.getPrincipalTipus()) {
						responsables.add(
								pluginHelper.dadesUsuariFindAmbCodi(permis.getPrincipalNom()));
					}
					if (PrincipalTipusEnumDto.ROL == permis.getPrincipalTipus()) {
						responsables.addAll(
								pluginHelper.dadesUsuariFindAmbGrup(permis.getPrincipalNom()));
					}
				} catch (Exception ex) {
					logger.error(
							"No s'ha pogut obtenir el gestor de l'expedient(" +
							"id=" + expedient.getId() + ", " +
							"nom=" + expedient.getNom() + ", " +
							"any=" + expedient.getAny() + ", " +
							"sequencia=" + expedient.getSequencia() + ")",
							ex);
				}
			}
		}
		return responsables;
	}
	


	
	private void enviarEmailCanviarEstatTasca(
			ExpedientTascaEntity expedientTascaEntity,
			TascaEstatEnumDto estatAnterior,
			Set<DadesUsuari> responsables,
			boolean destinitariHasPermisTasca) {
		logger.debug("Enviant correu electrònic per a canvis de tasca (" +
			"tascaId=" + expedientTascaEntity.getId() + ")");
		

		String from = getRemitent();
		String subject;
		String text;
		TascaEstatEnumDto estat = expedientTascaEntity.getEstat();
		String rebutjMotiu = "";
		if (estat == TascaEstatEnumDto.REBUTJADA) {
			rebutjMotiu = "\tMotiu: " + expedientTascaEntity.getMotiuRebuig() + "\n";
		}
		String enllacTramitar = "";
		if (destinitariHasPermisTasca && (estatAnterior == null || expedientTascaEntity.getEstat() == TascaEstatEnumDto.INICIADA || expedientTascaEntity.getEstat() == TascaEstatEnumDto.PENDENT)) {
			enllacTramitar = "Pot accedir a la tasca utilizant el següent enllaç: " + PropertiesHelper.getProperties().getProperty("es.caib.ripea.base.url") + "/usuariTasca/" + expedientTascaEntity.getId() + "/tramitar" + "\n";
		}
		if (estatAnterior == null) {
			subject = PREFIX_RIPEA + " Nova tasca: " + expedientTascaEntity.getMetaExpedientTasca().getNom();
			text = 					
					"S'ha creat una nova tasca a RIPEA:\n" +
					"\tNom: " + expedientTascaEntity.getMetaExpedientTasca().getNom() + "\n" +
					"\tDescripció: " + expedientTascaEntity.getMetaExpedientTasca().getDescripcio() + "\n" +
					"\tEstat: " + estat + "\n" +
					enllacTramitar;
		} else {
			subject = PREFIX_RIPEA + " Canvi d'estat de la tasca: " + expedientTascaEntity.getMetaExpedientTasca().getNom();
			text = 			
					"S'ha modificat l'estat de la tasca a RIPEA:\n" +
							"\tNom: " + expedientTascaEntity.getMetaExpedientTasca().getNom() + "\n" +
							"\tDescripció: " + expedientTascaEntity.getMetaExpedientTasca().getDescripcio() + "\n" +
							"\tEstat anterior:" + estatAnterior + "\n" +
							"\tEstat actual:" + estat + "\n" + 
							rebutjMotiu +
							enllacTramitar;
		}
		
		
		List<String> destinatarisAgrupats = new ArrayList<String>();
		List<String> destinatarisNoAgrupats = new ArrayList<String>();
		
		for (DadesUsuari responsable : responsables) {
			if (responsable != null && (responsable.getEmail() != null && !responsable.getEmail().isEmpty())) {
				UsuariEntity usuari = usuariHelper.getUsuariByCodi(responsable.getCodi());
				if (usuari != null && usuari.isRebreEmailsAgrupats()) {
					destinatarisAgrupats.add(responsable.getEmail());
				} else {
					destinatarisNoAgrupats.add(responsable.getEmail());
				}
			}
		}
		
		if (destinatarisNoAgrupats != null && !destinatarisNoAgrupats.isEmpty()) {
			String[] to = destinatarisNoAgrupats.toArray(new String[destinatarisNoAgrupats.size()]);
			SimpleMailMessage missatge = new SimpleMailMessage();
			missatge.setFrom(from);
			missatge.setTo(to);
			missatge.setSubject(subject);
			missatge.setText(text);
			logger.debug(missatge.toString());
			mailSender.send(missatge);
		}
		
		if (destinatarisAgrupats != null && !destinatarisAgrupats.isEmpty()) {
			for (String dest : destinatarisAgrupats) {
				EmailPendentEnviarEntity enitity = EmailPendentEnviarEntity.getBuilder(
						from,
						dest,
						subject,
						text,
						EventTipusEnumDto.CANVI_ESTAT_TASCA)
						.build();
				emailPendentEnviarRepository.save(enitity);
			}
		}

	}
	

	private boolean emplenarDestinatariAmbUsuari(
			MailMessage mailMessage,
			String usuariCodi) {
		DadesUsuari dadesUsuari = cacheHelper.findUsuariAmbCodi(usuariCodi);
		if (dadesUsuari != null && dadesUsuari.getEmail() != null) {
			mailMessage.setTo(dadesUsuari.getEmail());
			return true;
		} else {
			return false;
		}
	}

	private String getRemitent() {
		return PropertiesHelper.getProperties().getProperty("es.caib.ripea.email.remitent");
	}

	private static final Logger logger = LoggerFactory.getLogger(EmailHelper.class);

}
