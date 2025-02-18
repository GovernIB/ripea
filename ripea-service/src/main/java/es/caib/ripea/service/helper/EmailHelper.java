package es.caib.ripea.service.helper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import es.caib.ripea.service.intf.config.PropertyConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import es.caib.ripea.persistence.entity.CarpetaEntity;
import es.caib.ripea.persistence.entity.ContingutEntity;
import es.caib.ripea.persistence.entity.DocumentEntity;
import es.caib.ripea.persistence.entity.DocumentNotificacioEntity;
import es.caib.ripea.persistence.entity.DocumentPortafirmesEntity;
import es.caib.ripea.persistence.entity.DocumentViaFirmaEntity;
import es.caib.ripea.persistence.entity.EmailPendentEnviarEntity;
import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.ExecucioMassivaEntity;
import es.caib.ripea.persistence.entity.ExpedientEntity;
import es.caib.ripea.persistence.entity.ExpedientPeticioEntity;
import es.caib.ripea.persistence.entity.ExpedientTascaEntity;
import es.caib.ripea.persistence.entity.MetaExpedientComentariEntity;
import es.caib.ripea.persistence.entity.MetaExpedientEntity;
import es.caib.ripea.persistence.entity.MetaExpedientOrganGestorEntity;
import es.caib.ripea.persistence.entity.OrganGestorEntity;
import es.caib.ripea.persistence.entity.RegistreEntity;
import es.caib.ripea.persistence.entity.UsuariEntity;
import es.caib.ripea.persistence.repository.DocumentRepository;
import es.caib.ripea.persistence.repository.EmailPendentEnviarRepository;
import es.caib.ripea.persistence.repository.ExpedientPeticioRepository;
import es.caib.ripea.persistence.repository.MetaExpedientOrganGestorRepository;
import es.caib.ripea.persistence.repository.OrganGestorRepository;
import es.caib.ripea.plugin.usuari.DadesUsuari;
import es.caib.ripea.service.intf.dto.DocumentEnviamentEstatEnumDto;
import es.caib.ripea.service.intf.dto.DocumentNotificacioEstatEnumDto;
import es.caib.ripea.service.intf.dto.EventTipusEnumDto;
import es.caib.ripea.service.intf.dto.FitxerDto;
import es.caib.ripea.service.intf.dto.PermisDto;
import es.caib.ripea.service.intf.dto.TascaEstatEnumDto;
import es.caib.ripea.service.intf.utils.Utils;
import es.caib.ripea.service.permission.ExtendedPermission;

@Component
public class EmailHelper {

	@Autowired private CacheHelper cacheHelper;
	@Autowired private PluginHelper pluginHelper;
	@Autowired private JavaMailSender mailSender;
	@Autowired private UsuariHelper usuariHelper;
	@Autowired private EmailPendentEnviarRepository emailPendentEnviarRepository;
	@Autowired private PermisosHelper permisosHelper;
	@Autowired private ConfigHelper configHelper;
	@Autowired private OrganGestorRepository organGestorRepository;
	@Autowired private ExpedientPeticioRepository expedientPeticioRepository;
    @Autowired private OrganGestorHelper organGestorHelper;
    @Autowired private MetaExpedientOrganGestorRepository metaExpedientOrganGestorRepository;
    @Autowired private DocumentRepository documentRepository;
    @Autowired private DocumentHelper documentHelper;

	public void contingutAgafatPerAltreUsusari(
			ContingutEntity contingut,
			UsuariEntity usuariOriginal,
			UsuariEntity usuariNou) {

		String tipus = "desconegut";
		if (contingut instanceof ExpedientEntity) {
			tipus = "expedient";
		} else if (contingut instanceof DocumentEntity) {
			tipus = "document";
		} else if (contingut instanceof CarpetaEntity) {
			tipus = "carpeta";
		}
		String subject = getPrefixRipea() + " Element de l'escriptori agafat per un altre usuari: (" + tipus + ") " + contingut.getNom();
		String text =
				"Informació de l'element de l'escriptori:\n" +
				"\tTipus: " + tipus + "\n" +
				"\tNom: " + contingut.getNom() + "\n\n" +
				"\tPersona que ho ha agafat: " + usuariNou.getNom() + "(" + usuariNou.getCodi() + ").";
		sendOrSaveEmail(
				usuariOriginal.getCodi(),
				subject,
				text,
				EventTipusEnumDto.AGAFAT_ALTRE_USUARI);
	}

	public void contingutAlliberat(ExpedientEntity expedient, UsuariEntity usuariCreador, UsuariEntity usuariActual) {
		String tipus = "expedient";
		String subject = getPrefixRipea() + " Element de l'escriptori s'ha retornat per un usuari: (" + tipus + ") " + expedient.getNom();
		String text =
				"Informació de l'element de l'escriptori:\n" +
				"\tTipus: " + tipus + "\n" +
				"\tNom: " + expedient.getNom() + "\n\n" +
				"\tPersona que vos ho ha retornat: " + usuariActual.getNom() + "(" + usuariActual.getCodi() + ").";
		sendOrSaveEmail(
				usuariCreador.getCodi(),
				subject,
				text,
				EventTipusEnumDto.ALLIBERAT);
	}

	public void execucioMassivaFinalitzada(
			ExecucioMassivaEntity em) {

		logger.debug("Enviant correu electrònic per a execució massiva finalitzada (" +
			"execucioMassivaId=" + em.getId() + ")");

		String from = getRemitent();
		String subject = getPrefixRipea() + " Execucio massiva finalitzada: " + em.getTipus();
		String text =
				"Execució massiva finalitzada:\n" +
				"\tId: " + em.getId() + "\n" +
				"\tTipus: " + em.getTipus() + "\n" +
				"\tData Inici: " + em.getDataInici() + "\n" +
				"\tData Fi: " + em.getDataFi() + "\n" +
				"\tContinguts: " + em.getContinguts().size() + "\n";

		UsuariEntity usuari = usuariHelper.getUsuariByCodiDades(
				em.getCreatedBy().get().getCodi(),
				false,
				false);

		if (usuari != null) {
			String to = getEmail(usuari);
			if (Utils.isNotEmpty(to)) {
				SimpleMailMessage missatge = new SimpleMailMessage();
				missatge.setFrom(from);
				missatge.setTo(to);
				missatge.setSubject(subject);
				missatge.setText(text);
				mailSender.send(missatge);
			}
		}
	}



	public void canviEstatRevisioMetaExpedient(
			MetaExpedientEntity metaExpedientEntity,
			Long entitatId) {
		logger.debug("Enviant correu electrònic per a canvi d'estat de revisio");


		List<String> emailsNoAgrupats = new ArrayList<>();
		List<String> emailsAgrupats = new ArrayList<>();
		List<DadesUsuari> dadesUsuarisRevisio = pluginHelper.dadesUsuariFindAmbGrup("IPA_REVISIO");
		for (DadesUsuari dadesUsuari : dadesUsuarisRevisio) {
            addDestinatari(
                dadesUsuari.getCodi(),
                emailsNoAgrupats,
                emailsAgrupats,
                EventTipusEnumDto.CANVI_ESTAT_REVISIO,
                "Email canviEstatRevisioMetaExpedient. Permission: Rol IPA_REVISIO: " + ", user: " + dadesUsuari.getCodi());
		}

		List<DadesUsuari> dadesUsuarisAdmin = pluginHelper.dadesUsuariFindAmbGrup("IPA_ADMIN");
		for (DadesUsuari dadesUsuari : dadesUsuarisAdmin) {
            addDestinatari(
                dadesUsuari.getCodi(),
                emailsNoAgrupats,
                emailsAgrupats,
                EventTipusEnumDto.CANVI_ESTAT_REVISIO,
                "Email canviEstatRevisioMetaExpedient. Permission: Administració de entitat: " + entitatId + ", user: " + dadesUsuari.getCodi());
		}

		String subject = getPrefixRipea() + " Canvi d'estat de revisió de procediment";
		String comentari = "";
		if (metaExpedientEntity.getRevisioComentari() != null && !metaExpedientEntity.getRevisioComentari().isEmpty()) {
			comentari = "\tComentari: " + metaExpedientEntity.getRevisioComentari() + "\n";
		}
		String text =
				"Informació del procediment:\n" +
						"\tNom: " + metaExpedientEntity.getCodiSiaINom() + "\n" +
						"Estat de revisio: " + metaExpedientEntity.getRevisioEstat() + "\n" +
						"Data de revisió: " + Utils.convertDateToString(new Date(), "dd-MM-yyyy HH:mm:ss") + "\n" +
						comentari ;

		sendOrSaveEmail(
				emailsNoAgrupats,
				emailsAgrupats,
				subject,
				text,
				EventTipusEnumDto.CANVI_ESTAT_REVISIO);
	}



	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void comentariMetaExpedient(
			MetaExpedientComentariEntity metaExpComnt) {

		String comentari = metaExpComnt.getText();
		MetaExpedientEntity metaExpedientEntity = metaExpComnt.getMetaExpedient();
		Long entitatId = metaExpedientEntity.getEntitat().getId();
		List<String> emailsNoAgrupats = new ArrayList<>();
		List<String> emailsAgrupats = new ArrayList<>();
		List<DadesUsuari> dadesUsuarisRevisio = pluginHelper.dadesUsuariFindAmbGrup("IPA_REVISIO");
		for (DadesUsuari dadesUsuari : dadesUsuarisRevisio) {
			addDestinatari(
					dadesUsuari.getCodi(),
					emailsNoAgrupats,
					emailsAgrupats,
					null,
					"Email comentariMetaExpedient. Permission: IPA_REVISIO, user: " + dadesUsuari.getCodi());
		}

		List<DadesUsuari> dadesUsuarisAdminEntitat = pluginHelper.dadesUsuariFindAmbGrup("IPA_ADMIN");
		for (DadesUsuari dadesUsuari : dadesUsuarisAdminEntitat) {
			boolean granted = permisosHelper.isGrantedAll(
					entitatId,
					EntitatEntity.class,
					new Permission[] { ExtendedPermission.ADMINISTRATION },
					dadesUsuari.getCodi());
			if (granted) {
				addDestinatari(
						dadesUsuari.getCodi(),
						emailsNoAgrupats,
						emailsAgrupats,
						null,
						"Email comentariMetaExpedient. Permission: Administració de entitat: " + entitatId + ", user: " + dadesUsuari.getCodi());
			}
		}

		OrganGestorEntity organGestor = metaExpedientEntity.getOrganGestor();
		if (organGestor == null) {
			List<OrganGestorEntity> organs = organGestorRepository.findByEntitat(metaExpedientEntity.getEntitat());
			List<DadesUsuari> dadesUsuarisAdminOrgan = pluginHelper.dadesUsuariFindAmbGrup("IPA_ORGAN_ADMIN");
			for (DadesUsuari dadesUsuari : dadesUsuarisAdminOrgan) {
				for (OrganGestorEntity organ : organs) {
					boolean granted = permisosHelper.isGrantedAll(
							organ.getId(),
							OrganGestorEntity.class,
							new Permission[] { ExtendedPermission.ADMINISTRATION, ExtendedPermission.ADM_COMU},
							dadesUsuari.getCodi());
					if (granted) {
						addDestinatari(
								dadesUsuari.getCodi(),
								emailsNoAgrupats,
								emailsAgrupats,
								null,
								"Email comentariMetaExpedient. Permission: Administració órgan comuns: " + organ.getId() + ", user: " + dadesUsuari.getCodi());
					}
				}
			}
		} else {
			List<DadesUsuari> dadesUsuarisAdminOrgan = pluginHelper.dadesUsuariFindAmbGrup("IPA_ORGAN_ADMIN");
			for (DadesUsuari dadesUsuari : dadesUsuarisAdminOrgan) {
				boolean granted = permisosHelper.isGrantedAll(
						organGestor.getId(),
						OrganGestorEntity.class,
						new Permission[] { ExtendedPermission.ADMINISTRATION },
						dadesUsuari.getCodi());
				if (granted) {
					addDestinatari(
							dadesUsuari.getCodi(),
							emailsNoAgrupats,
							emailsAgrupats,
							null,
							"Email comentariMetaExpedient. Permission: Administració órgan : " + organGestor.getId() + ", user: " + dadesUsuari.getCodi());
				}
			}
		}


		String subject = getPrefixRipea() + " Nou comentari en el procediment";
		String text =
				"Informació del procediment:\n" +
						"\tNom: " + metaExpedientEntity.getCodiSiaINom() + "\n" +
						"Comentari: \n\t" + comentari.replace("\n", "\n\t") + "\n" +
						"Usuari: " + metaExpComnt.getCreatedBy().get().getNom() + "\n" +
						"Data: " + Utils.convertDateToString(metaExpComnt.getCreatedDate().get(), "dd-MM-yyyy HH:mm:ss");

		sendOrSaveEmail(
				emailsNoAgrupats,
				emailsAgrupats,
				subject,
				text,
				EventTipusEnumDto.PROCEDIMENT_COMENTARI);

		metaExpComnt.updateEmailEnviat(true);
	}


	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void novaAnotacioPendent(Long expedientPeticioId) {

		long t1 = System.currentTimeMillis();
		if (cacheHelper.mostrarLogsRendimentDescarregarAnotacio())
			logger.info("novaAnotacioPendent start (id=" + expedientPeticioId + ")");

		ExpedientPeticioEntity expedientPeticio = expedientPeticioRepository.findById(expedientPeticioId).orElse(null);
		RegistreEntity registre = expedientPeticio.getRegistre();
		MetaExpedientEntity metaExpedient = expedientPeticio.getMetaExpedient();
		EntitatEntity entitat = registre.getEntitat();
		OrganGestorEntity organ = organGestorRepository.findByCodi(registre.getDestiCodi());

		List<String> emailsNoAgrupats = new ArrayList<>();
		List<String> emailsAgrupats = new ArrayList<>();

		long t2 = System.currentTimeMillis();
		// Administradors d'entitats
		List<DadesUsuari> dadesUsuarisAdminEntitat = pluginHelper.dadesUsuariFindAmbGrup("IPA_ADMIN");

		if (cacheHelper.mostrarLogsRendimentDescarregarAnotacio())
			logger.info("dadesUsuariFindAmbGrup(IPA_ADMIN) (size=" + Utils.getSize(dadesUsuarisAdminEntitat) + ")");

		for (DadesUsuari dadesUsuari : dadesUsuarisAdminEntitat) {
			boolean granted = permisosHelper.isGrantedAll(
					entitat.getId(),
					EntitatEntity.class,
					new Permission[] { ExtendedPermission.ADMINISTRATION },
					dadesUsuari.getCodi());
			if (granted) {
				addDestinatari(
						dadesUsuari.getCodi(),
						emailsNoAgrupats,
						emailsAgrupats,
						EventTipusEnumDto.NOVA_ANOTACIO,
						"Email nova anotació. Permission: Administració de entitat: " + entitat.getId() + ", user: " + dadesUsuari.getCodi());
			}
		}
		if (cacheHelper.mostrarLogsRendimentDescarregarAnotacio())
			logger.info("dadesUsuariFindAmbGrup(IPA_ADMIN) time:  " + (System.currentTimeMillis() - t2) + " ms");

		if (metaExpedient != null) {
			boolean isProcedimentNoComu = metaExpedient.getOrganGestor() != null;

			long t3 = System.currentTimeMillis();
			// Administradors d'òrgans
			List<DadesUsuari> dadesUsuarisAdminOrgan = pluginHelper.dadesUsuariFindAmbGrup("IPA_ORGAN_ADMIN");

			if (cacheHelper.mostrarLogsRendimentDescarregarAnotacio())
				logger.info("dadesUsuariFindAmbGrup(IPA_ORGAN_ADMIN) (size=" + Utils.getSize(dadesUsuarisAdminOrgan) + ") isProcedimentNoComu=" + isProcedimentNoComu + " " + organ);

			for (DadesUsuari dadesUsuari : dadesUsuarisAdminOrgan) {
				if (isProcedimentNoComu) {

					List<Long> organPathIds = organGestorHelper.findParesIds(metaExpedient.getOrganGestor().getId(), true);
					for (Long orgId : organPathIds) {
						boolean granted = permisosHelper.isGrantedAll(
								orgId,
								OrganGestorEntity.class,
								new Permission[] { ExtendedPermission.ADMINISTRATION },
								dadesUsuari.getCodi());
						if (granted) {
							addDestinatari(
									dadesUsuari.getCodi(),
									emailsNoAgrupats,
									emailsAgrupats,
									EventTipusEnumDto.NOVA_ANOTACIO,
									"Email nova anotació. Permission: Administració de òrgan (no comuns): " + orgId + ", user: " + dadesUsuari.getCodi());
						}
					}

				} else {
					if (organ != null) {
						List<Long> organPathIds = organGestorHelper.findParesIds(organ.getId(),
								true);
						for (Long orgId : organPathIds) {
							boolean granted = permisosHelper.isGrantedAll(
									orgId,
									OrganGestorEntity.class,
									new Permission[] { ExtendedPermission.ADMINISTRATION, ExtendedPermission.ADM_COMU },
									dadesUsuari.getCodi());
							if (granted) {
								addDestinatari(
										dadesUsuari.getCodi(),
										emailsNoAgrupats,
										emailsAgrupats,
										EventTipusEnumDto.NOVA_ANOTACIO,
										"Email nova anotació. Permission: Administració de òrgan (comuns): " + orgId + ", user: " + dadesUsuari.getCodi());
							}
						}
					}
				}
			}
			if (cacheHelper.mostrarLogsRendimentDescarregarAnotacio())
				logger.info("dadesUsuariFindAmbGrup(IPA_ORGAN_ADMIN) time:  " + (System.currentTimeMillis() - t3) + " ms");

			long t4 = System.currentTimeMillis();

			if (cacheHelper.mostrarLogsRendimentDescarregarAnotacio())
				logger.info("dadesUsuariFindAmbGrup(tothom) isProcedimentNoComu=" + isProcedimentNoComu + " " + organ);

			List<Long> objectesAclIds = new ArrayList<Long>();
			Set<PermisDto> usuarisUnaCondicio = new HashSet<PermisDto>();
			Set<PermisDto> usuarisDobleCondicio = new HashSet<PermisDto>();
			Set<PermisDto> usuarisAmbPermis = new HashSet<PermisDto>();

			// 1. Permission on procediment of anotacion (procediments no comuns)
			objectesAclIds.add(metaExpedient.getId());

			if (cacheHelper.mostrarLogsRendimentDescarregarAnotacio()) {
				logger.info("1. Permission on procediment of anotacion (procediments no comuns): " + objectesAclIds);
			}

			if (isProcedimentNoComu) {
				// 2. Permission on organ of procediment of anotacio (procediments no comuns)
				List<Long> organPathIds = organGestorHelper.findParesIds(metaExpedient.getOrganGestor().getId(), true);

				objectesAclIds.addAll(organPathIds);

				if (cacheHelper.mostrarLogsRendimentDescarregarAnotacio()) {
					logger.info("2. Permission on organ of procediment of anotacio (procediments no comuns): " + organPathIds);
				}
			} else {
				// 3. Permission on pair organ-procediment of anotacio (procediments comuns)
				List<Long> organPathIds = organGestorHelper.findParesIds(organ.getId(), true);

				for (Long orgId : organPathIds) {
					MetaExpedientOrganGestorEntity metaExpedientOrganGestor = metaExpedientOrganGestorRepository.findByMetaExpedientIdAndOrganGestorId(metaExpedient.getId(), orgId);
					if (metaExpedientOrganGestor != null)
						objectesAclIds.add(metaExpedientOrganGestor.getId());
				}

				if (cacheHelper.mostrarLogsRendimentDescarregarAnotacio()) {
					logger.info("3. Permission on pair organ-procediment of anotacio (procediments comuns): " + organPathIds);
				}

				// 4. Permission on organ per procediments comuns (procediments comuns)
				usuarisDobleCondicio = permisosHelper.findPermisosObjectes(
						organPathIds,
						new Permission[] { ExtendedPermission.CREATE, ExtendedPermission.WRITE },
						new Permission[] { ExtendedPermission.COMU});

				if (cacheHelper.mostrarLogsRendimentDescarregarAnotacio() && usuarisDobleCondicio != null && ! usuarisDobleCondicio.isEmpty()) {
					logger.info("4. Permission on organ per procediments comuns (procediments comuns): " + usuarisDobleCondicio.size());
				}

				usuarisAmbPermis.addAll(usuarisDobleCondicio);
			}

			// Usuaris amb permís sobre procediment o òrgan del procediment
			usuarisUnaCondicio = permisosHelper.findPermisosObjectes(
					objectesAclIds,
					new Permission[] { ExtendedPermission.CREATE, ExtendedPermission.WRITE },
					null);

			if (cacheHelper.mostrarLogsRendimentDescarregarAnotacio())
				logger.info("dadesUsuariFindAmbGrup(tothom) time:  " + (System.currentTimeMillis() - t4) + " ms");

			if (cacheHelper.mostrarLogsRendimentDescarregarAnotacio() && usuarisUnaCondicio != null && ! usuarisUnaCondicio.isEmpty()) {
				logger.info("Usuaris amb permís sobre procediment o òrgan del procediment (objectesAclIds): " + objectesAclIds);
				logger.info("Usuaris amb permís sobre procediment o òrgan del procediment (usuarisUnaCondicio): " + usuarisUnaCondicio.size());
			}

			usuarisAmbPermis.addAll(usuarisUnaCondicio);

			if (cacheHelper.mostrarLogsRendimentDescarregarAnotacio() && usuarisAmbPermis != null && ! usuarisAmbPermis.isEmpty()) {
				logger.info("Usuaris amb permís sobre procediment o òrgan del procediment (usuarisAmbPermis): " + usuarisAmbPermis.size());
			}

			for (PermisDto permisDto : usuarisAmbPermis) {
				addDestinatari(
						permisDto.getPrincipalNom(),
						emailsNoAgrupats,
						emailsAgrupats,
						EventTipusEnumDto.NOVA_ANOTACIO,
						"Email nova anotació. Permission on objects ACL: " + metaExpedient.getId() + ", user: " + permisDto.getPrincipalNom());
			}
		}

		String subject = getPrefixRipea() + " Nova anotació pendent";
		String text =
				"Informació d'anotació:\n" +
						"\tNúmero: " + registre.getIdentificador() + "\n" +
						"\tExtracte: " + registre.getExtracte() + "\n";

		if (organ != null) {
			text += "\tDestinació: " + organ.getCodiINom() + "\n";
		}
		if (metaExpedient != null) {
			text += "\tProcediment: " + metaExpedient.getCodiSiaINom() + "\n";
		}

		sendOrSaveEmail(
				emailsNoAgrupats,
				emailsAgrupats,
				subject,
				text,
				EventTipusEnumDto.NOVA_ANOTACIO);


		if (cacheHelper.mostrarLogsRendimentDescarregarAnotacio())
			logger.info("novaAnotacioPendent end (id=" + expedientPeticioId + "):  " + (System.currentTimeMillis() - t1) + " ms");
	}

	public void canviEstatRevisioMetaExpedientEnviarAAdminOrganCreador(
			MetaExpedientEntity metaExpedientEntity,
			Long entitatId) {
		UsuariEntity organAdminCreador = metaExpedientEntity.getCreatedBy().get();

        List<String> emailsNoAgrupats = new ArrayList<>();
        List<String> emailsAgrupats = new ArrayList<>();

        addDestinatari(
                organAdminCreador.getCodi(),
                emailsNoAgrupats,
                emailsAgrupats,
                EventTipusEnumDto.CANVI_ESTAT_REVISIO,
                "Email canviEstatRevisioMetaExpedientEnviarAAdminOrganCreador. Permission: creador del metaexpedient," + metaExpedientEntity.getCodi() + ", user: " + organAdminCreador.getCodi());

        String subject = getPrefixRipea() + " Canvi d'estat de revisió de procediment";
        String comentari = "";
        if (metaExpedientEntity.getRevisioComentari() != null && !metaExpedientEntity.getRevisioComentari().isEmpty()) {
            comentari = "\tComentari: " + metaExpedientEntity.getRevisioComentari() + "\n";
        }
        String text =
                "Informació del procediment:\n" +
                        "\tProcediment: " + metaExpedientEntity.getCodiSiaINom() + "\n" +
                        "Estat de revisió: " + metaExpedientEntity.getRevisioEstat() + "\n" +
                        "Data de revisió: " + Utils.convertDateToString(new Date(), "dd-MM-yyyy HH:mm:ss") + "\n" +
                        comentari;

        sendOrSaveEmail(
                emailsNoAgrupats,
                emailsAgrupats,
                subject,
                text,
                EventTipusEnumDto.CANVI_ESTAT_REVISIO);
	}

	public void canviEstatDocumentPortafirmes(
			DocumentPortafirmesEntity documentPortafirmes) {


		DocumentEntity document = documentPortafirmes.getDocument();
		String enviamentCreatedByCodi = documentPortafirmes.getCreatedBy().get().getCodi();
		ExpedientEntity expedient = document.getExpedient();

		String subject = getPrefixRipea() + " Canvi d'estat de document enviat a portafirmes";
		String estat = (documentPortafirmes.getEstat() == DocumentEnviamentEstatEnumDto.PROCESSAT) ? "FIRMAT" : documentPortafirmes.getEstat().toString();
		String rebutjMotiu = "";
		String responsableRebuig = "";
		boolean isFirmaParcial = documentPortafirmes.getFirmaParcial() != null && documentPortafirmes.getFirmaParcial();
		if (documentPortafirmes.getEstat() == DocumentEnviamentEstatEnumDto.REBUTJAT) {
			rebutjMotiu = "\tMotiu: " + documentPortafirmes.getMotiuRebuig() + "\n";
			if (documentPortafirmes.getName() != null)
				responsableRebuig = "\tResponsable del rebuig: " + documentPortafirmes.getName() + " (" + documentPortafirmes.getAdministrationId() + ")" + "\n\n";
			else
				responsableRebuig = "\tResponsable del rebuig: " + documentPortafirmes.getAdministrationId() + "\n\n";
		}
		String text =
				"Informació del document:\n" +
						"\tExpedient nom: " + expedient.getNom() + "\n" +
						"\tExpedient núm.: " + expedient.getNumero() + "\n" +
						"\tDocument nom: " + document.getNom() + "\n" +
						"\tDocument tipus.: " + document.getMetaDocument().getNom() + "\n" +
						"\tDocument fitxer: " + document.getFitxerNom() + "\n\n" +
						(isFirmaParcial ? "\tFirma parcial: Sí" : "" ) + "\n" +
						"Estat del document:" + estat + "\n" +
						rebutjMotiu +
						responsableRebuig +
						getEnllacExpedient(expedient.getId());


		Set<DadesUsuari> responsables = getGestors(
				false,
				false,
				expedient,
				enviamentCreatedByCodi,
				null,
				null);

		sendOrSaveEmail(
				responsables,
				subject,
				text,
				EventTipusEnumDto.CANVI_ESTAT_PORTAFIRMES);

	}

	public void firmaParcialDocumentPortafirmes(
			DocumentPortafirmesEntity documentPortafirmes) {
		logger.debug("Enviant correu electrònic avís firma parcial de document al portafirmes (" +
			"documentPortafirmesId=" + documentPortafirmes.getId() + ")");

		DocumentEntity document = documentPortafirmes.getDocument();
		String enviamentCreatedByCodi = documentPortafirmes.getCreatedBy().get().getCodi();
		ExpedientEntity expedient = document.getExpedient();

		String subject = getPrefixRipea() + " Firma parcial de document enviat a portafirmes";
		String estat = (documentPortafirmes.getEstat() == DocumentEnviamentEstatEnumDto.PROCESSAT) ? "FIRMAT" : documentPortafirmes.getEstat().toString();
		String text =
				"Informació del document:\n" +
						"\tExpedient nom: " + expedient.getNom() + "\n" +
						"\tExpedient núm.: " + expedient.getNumero() + "\n" +
						"\tDocument nom: " + document.getNom() + "\n" +
						"\tDocument tipus.: " + document.getMetaDocument().getNom() + "\n" +
						"\tDocument fitxer: " + document.getFitxerNom() + "\n\n" +
						"Estat del document:" + estat + "\n" +
						getEnllacExpedient(expedient.getId());


		Set<DadesUsuari> responsables = getGestors(
				false,
				false,
				expedient,
				enviamentCreatedByCodi,
				null,
				null);


		for (DadesUsuari dadesUsuari : responsables) {
			if (dadesUsuari != null) {
				String to = dadesUsuari.getEmail();
				if (Utils.isNotEmpty(to)) {
					SimpleMailMessage missatge = new SimpleMailMessage();
					missatge.setFrom(getRemitent());
					missatge.setTo(to);
					missatge.setSubject(subject);
					missatge.setText(text);
					mailSender.send(missatge);
				}
			}
		}

	}

	public void canviEstatDocumentViaFirma(
			DocumentViaFirmaEntity documentViaFirma) {
		logger.debug("Enviant correu electrònic per a canvi d'estat de document a ViaFirma (" +
			"documentViaFirma=" + documentViaFirma.getId() + ")");

		DocumentEntity document = documentViaFirma.getDocument();
		String enviamentCreatedByCodi = documentViaFirma.getCreatedBy().get().getCodi();
		ExpedientEntity expedient = document.getExpedient();

		String subject = getPrefixRipea() + " Canvi d'estat de document enviat a ViaFirma";
		String estat = (documentViaFirma.getEstat() == DocumentEnviamentEstatEnumDto.PROCESSAT) ? "FIRMAT" : documentViaFirma.getEstat().toString();

		String text =
				"Informació del document:\n" +
						"\tExpedient nom: " + expedient.getNom() + "\n" +
						"\tExpedient núm.: " + expedient.getNumero() + "\n" +
						"\tDocument nom: " + document.getNom() + "\n" +
						"\tDocument tipus.: " + document.getMetaDocument().getNom() + "\n" +
						"\tDocument fitxer: " + document.getFitxerNom() + "\n\n" +
						"Estat del document:" + estat + "\n" +
						getEnllacExpedient(expedient.getId());


		Set<DadesUsuari> responsables = getGestors(
				false,
				false,
				expedient,
				enviamentCreatedByCodi,
				null,
				null);

		sendOrSaveEmail(
				responsables,
				subject,
				text,
				EventTipusEnumDto.CANVI_ESTAT_VIAFIRMA);

	}


	public void canviEstatNotificacio(
			DocumentNotificacioEntity documentNotificacio,
			DocumentEnviamentEstatEnumDto estatAnterior) {
		logger.debug("Enviant correu electrònic per a canvi d'estat de notificació (" +
			"documentNotificacioId=" + documentNotificacio.getId() + ")");

		DocumentEntity document = documentNotificacio.getDocument();
		String notificacioCreatedByCodi = documentNotificacio.getCreatedBy().get().getCodi();
		ExpedientEntity expedient = document.getExpedient();

		String subject = getPrefixRipea() + " Canvi d'estat de notificació";
		String estat = (documentNotificacio.getEstat() == DocumentEnviamentEstatEnumDto.PROCESSAT) ? "ENTREGAT" : documentNotificacio.getEstat().toString();
		String text =
				"Informació del document:\n" +
				"\tExpedient nom: " + expedient.getNom() + "\n" +
				"\tExpedient núm.: " + expedient.getNumero() + "\n" +
				"\tDocument nom: " + document.getNom() + "\n" +
				(document.getMetaDocument() != null ? "\tDocument tipus.: " + document.getMetaDocument().getNom() : "" ) + "\n" +
				"\tDocument fitxer: " + document.getFitxerNom() + "\n\n" +
				"Estat anterior:" + estatAnterior + "\n" +
				"Estat actual:" + estat + "\n" +
				getEnllacExpedient(expedient.getId());


		Set<DadesUsuari> responsables = getGestors(
				false,
				false,
				expedient,
				notificacioCreatedByCodi,
				null,
				null);

		sendOrSaveEmail(
				responsables,
				subject,
				text,
				EventTipusEnumDto.CANVI_ESTAT_NOTIFICACIO);


	}





	public void canviEstatNotificacio(
			DocumentNotificacioEntity documentNotificacio,
			DocumentNotificacioEstatEnumDto estatAnterior) {
		logger.debug("Enviant correu electrònic per a canvi d'estat de notificació (" +
			"documentNotificacioId=" + documentNotificacio.getId() + ")");

		DocumentEntity document = documentNotificacio.getDocument();
		String notificacioCreatedByCodi = documentNotificacio.getCreatedBy().get().getCodi();
		ExpedientEntity expedient = document.getExpedient();


		String subject = getPrefixRipea() + " Canvi d'estat de notificació";
		String estat = documentNotificacio.getNotificacioEstat() != null ? documentNotificacio.getNotificacioEstat().toString() : "";
		String text =
				"Informació del document:\n" +
				"\tExpedient nom: " + expedient.getNom() + "\n" +
				"\tExpedient núm.: " + expedient.getNumero() + "\n" +
				"\tDocument nom: " + document.getNom() + "\n" +
				(document.getMetaDocument() != null ? "\tDocument tipus.: " + document.getMetaDocument().getNom() : "" ) + "\n" +
				"\tDocument fitxer: " + document.getFitxerNom() + "\n\n" +
				"Estat anterior:" + estatAnterior.toString() + "\n" +
				"Estat actual:" + estat + "\n" +
				getEnllacExpedient(expedient.getId());


		Set<DadesUsuari> responsables = getGestors(
				false,
				false,
				expedient,
				notificacioCreatedByCodi,
				null,
				null);


		sendOrSaveEmail(
				responsables,
				subject,
				text,
				EventTipusEnumDto.CANVI_ESTAT_NOTIFICACIO);
	}


	public void enviarEmailCanviarEstatTasca(
			ExpedientTascaEntity expedientTascaEntity,
			TascaEstatEnumDto estatAnterior) {
		logger.debug("Enviant correu electrònic per a canvis de tasca (" +
			"tascaId=" + expedientTascaEntity.getId() + ")");

		enviarEmailCanviarEstatTasca(
				expedientTascaEntity,
				estatAnterior,
				getGestors(
						true,
						estatAnterior == null,
						expedientTascaEntity.getExpedient(),
						expedientTascaEntity.getCreatedBy().get().getCodi(),
						expedientTascaEntity.getResponsables(),
						null));

	}

	public void enviarEmailReasignarResponsableTasca(
			ExpedientTascaEntity expedientTascaEntity) {
		logger.debug("Enviant correu electrònic per a reassignar responsable de tasca (" +
				"tascaId=" + expedientTascaEntity.getId() + ")");

		enviarEmailReasignarResponsableTasca(
				expedientTascaEntity,
				getGestors(
						true,
						false,
						expedientTascaEntity.getExpedient(),
						expedientTascaEntity.getCreatedBy().get().getCodi(),
						expedientTascaEntity.getResponsables(),
						null));

	}

	public void enviarEmailDelegarTasca(
			ExpedientTascaEntity expedientTascaEntity) {
		logger.debug("Enviant correu electrònic per a delegar de tasca (" +
				"tascaId=" + expedientTascaEntity.getId() + "," +
				"delegatCodi=" + expedientTascaEntity.getDelegat().getCodi() + ")");

		enviarEmailDelegarTasca(
				expedientTascaEntity,
				getGestors(
						true,
						false,
						expedientTascaEntity.getExpedient(),
						expedientTascaEntity.getCreatedBy().get().getCodi(),
						expedientTascaEntity.getResponsables(),
						expedientTascaEntity.getDelegat()));

	}

	public void enviarEmailCancelarDelegacioTasca(
			ExpedientTascaEntity expedientTascaEntity,
			UsuariEntity delegat,
			String comentari) {
		logger.debug("Enviant correu electrònic cancel·lació delegació tasca (" +
				"tascaId=" + expedientTascaEntity.getId() + "," +
				(comentari != null ? "comentari=" + comentari + "," : "") +
				"delegatCodi=" + delegat.getCodi() + ")");

		enviarEmailCancelarDelegacioTasca(
				expedientTascaEntity,
				getGestors(
						true,
						false,
						expedientTascaEntity.getExpedient(),
						expedientTascaEntity.getCreatedBy().get().getCodi(),
						expedientTascaEntity.getResponsables(),
						delegat),
				comentari);

	}

	public void enviarEmailModificacioDataLimitTasca(
			ExpedientTascaEntity expedientTascaEntity) {
		logger.debug("Enviant correu electrònic per a reassignar responsable de tasca (" +
				"tascaId=" + expedientTascaEntity.getId() + ")");

		Set<DadesUsuari> responsables = getGestors(
				true,
				false,
				expedientTascaEntity.getExpedient(),
				expedientTascaEntity.getCreatedBy().get().getCodi(),
				expedientTascaEntity.getResponsables(),
				null);

		enviarEmailModificacioDataLimitTasca(
				expedientTascaEntity,
				responsables);
	}

	public void sendEmailAvisMencionatComentari(
			String emailDestinatari,
			UsuariEntity usuariActual,
			ExpedientEntity expedient,
			String comentari) {
		logger.debug("Enviament email comentari a destinatari");
		SimpleMailMessage missatge = new SimpleMailMessage();
		missatge.setTo(emailDestinatari);
		missatge.setFrom(getRemitent());
		missatge.setSubject(getPrefixRipea() + " Mencionat al comentari d'un expedient [" + expedient.getNom() + "]");
		missatge.setText(
				"L'usuari " + usuariActual.getNom() + "(" + usuariActual.getCodi() + ") t'ha mencionat al comentari d'un expedient [" + expedient.getNom() + "]: \n" +
				"\tNom expedient: " + (expedient != null ? expedient.getNom() : "") + "\n" +
				"\tComentari: " + comentari + "\n");
		mailSender.send(missatge);
	}

	private String getEnllacExpedient(Long expedientId) {
		String baseUrl = configHelper.getConfig("es.caib.ripea.base.url");
		String enllacExpedient = "Pot accedir a l'expedient utilizant el següent enllaç: " + baseUrl + "/contingut/" + expedientId + "\n";
		return baseUrl != null ? enllacExpedient : "";
	}

	private Set<DadesUsuari> getGestors(
			boolean isTasca,
			boolean isTascaNova,
			ExpedientEntity expedient,
			String createdByCodi,
			List<UsuariEntity> responsablesTasca,
			UsuariEntity delegat) {
		Set<DadesUsuari> responsables = new HashSet<DadesUsuari>();
		UsuariEntity agafatPer = expedient.getAgafatPer();

		if (createdByCodi != null) {
			//Persona que ha llançat l'enviament / tasca (createdBy)
			DadesUsuari createdBy = pluginHelper.dadesUsuariFindAmbCodi(createdByCodi);
			if ((!isTasca || (isTasca && isTascaNova)) && createdBy.getEmail() != null && !createdBy.getEmail().isEmpty())
				responsables.add(createdBy);

			//Persones responsables tasca
			if (responsablesTasca != null && !responsablesTasca.isEmpty()) {
				for (UsuariEntity resp: responsablesTasca) {
					DadesUsuari responsable = pluginHelper.dadesUsuariFindAmbCodi(resp.getCodi());
					if (responsable.getEmail() != null && !responsable.getEmail().isEmpty())
						responsables.add(responsable);
				}
			}

			//Persona que té agafat l'expedient sempre en cas de ser un canvi d'estat d'una tasca
			if (isTasca && ! isTascaNova && agafatPer != null) {
				DadesUsuari propietariExpedient = pluginHelper.dadesUsuariFindAmbCodi(expedient.getAgafatPer().getCodi());
				if (propietariExpedient.getEmail() != null && !propietariExpedient.getEmail().isEmpty())
					responsables.add(propietariExpedient);
			}

			//Persona que té agafat l'expedient si no és la mateixa que ha creat l'enviament, notificació o tasca
			if (agafatPer != null && (!agafatPer.getCodi().equals(createdByCodi))) {
				DadesUsuari propietariExpedient = pluginHelper.dadesUsuariFindAmbCodi(expedient.getAgafatPer().getCodi());
				if (propietariExpedient.getEmail() != null && !propietariExpedient.getEmail().isEmpty())
					responsables.add(propietariExpedient);
			}

			//Seguidors
			List<UsuariEntity> seguidors = expedient.getSeguidors();
			for (UsuariEntity seguidorEntity : seguidors) {
				DadesUsuari seguidor = pluginHelper.dadesUsuariFindAmbCodi(seguidorEntity.getCodi());

				if ((agafatPer != null
						&& (!agafatPer.getCodi().equals(seguidor.getCodi()))) //En cas de no ser la mateixa persona que ha llançat l'enviament o la que te agafat l'expedient
						&& !createdByCodi.equals(seguidor.getCodi())
						&& (seguidor.getEmail() != null && !seguidor.getEmail().isEmpty())) {
					responsables.add(seguidor);
				}
			}
		}
//			List<PermisDto> permisos = permisosHelper.findPermisos(
//					expedient.getMetaNode().getId(),
//					MetaNodeEntity.class);
//			for (PermisDto permis: permisos) {
//				if (permis.isWrite()) {
//					try {
//						if (PrincipalTipusEnumDto.USUARI == permis.getPrincipalTipus()) {
//							responsables.add(
//									pluginHelper.dadesUsuariFindAmbCodi(permis.getPrincipalNom()));
//						}
//						if (PrincipalTipusEnumDto.ROL == permis.getPrincipalTipus()) {
//							responsables.addAll(
//									pluginHelper.dadesUsuariFindAmbGrup(permis.getPrincipalNom()));
//						}
//					} catch (Exception ex) {
//						logger.error(
//								"No s'ha pogut obtenir el gestor de l'expedient(" +
//								"id=" + expedient.getId() + ", " +
//								"nom=" + expedient.getNom() + ", " +
//								"any=" + expedient.getAny() + ", " +
//								"sequencia=" + expedient.getSequencia() + ")",
//								ex);
//					}
//				}
//			}
		return responsables;
	}

	private void enviarEmailCanviarEstatTasca(
			ExpedientTascaEntity expedientTascaEntity,
			TascaEstatEnumDto estatAnterior,
			Set<DadesUsuari> responsables) {
		logger.debug("Enviant correu electrònic per a canvis de tasca (" +
			"tascaId=" + expedientTascaEntity.getId() + ")");

		String subject;
		String text;
		String comentari = expedientTascaEntity.getTextLastComentari();
		TascaEstatEnumDto estat = expedientTascaEntity.getEstat();
		String rebutjMotiu = "";
		if (estat == TascaEstatEnumDto.REBUTJADA) {
			rebutjMotiu = "\tMotiu: " + expedientTascaEntity.getMotiuRebuig() + "\n";
		}
		String enllacTramitar = "Pot accedir a la tasca utilizant el següent enllaç: " + configHelper.getConfig("es.caib.ripea.base.url") + "/contingut/" + expedientTascaEntity.getExpedient().getId() + "?tascaId=" + expedientTascaEntity.getId() + "\n";
		String titol = expedientTascaEntity.getTitol();
		String observacions = expedientTascaEntity.getObservacions();
		if (estatAnterior == null) {
			subject = getPrefixRipea() + " Nova tasca: " + expedientTascaEntity.getMetaTasca().getNom();
			text =
					"S'ha creat una nova tasca a RIPEA:\n" +
					"\tNom: " + expedientTascaEntity.getMetaTasca().getNom() + "\n" +
					"\tDescripció: " + expedientTascaEntity.getMetaTasca().getDescripcio() + "\n" +
					((titol != null && ! titol.isEmpty()) ? "\tTítol: " + titol + "\n" : "") +
					((observacions != null && ! observacions.isEmpty()) ? "\tObservacions: " + observacions + "\n" : "") +
					"\tEstat: " + estat + "\n" +
					((comentari != null && !comentari.isEmpty()) ? "\tComentari: " + comentari + "\n" : "") +
					"\tExpedient: " + expedientTascaEntity.getExpedient().getNomINumero() + "\n" +
					enllacTramitar;
		} else {
			subject = getPrefixRipea() + " Canvi d'estat de la tasca: " + expedientTascaEntity.getMetaTasca().getNom();
			text =
					"S'ha modificat l'estat de la tasca a RIPEA:\n" +
							"\tNom: " + expedientTascaEntity.getMetaTasca().getNom() + "\n" +
							"\tDescripció: " + expedientTascaEntity.getMetaTasca().getDescripcio() + "\n" +
							"\tEstat anterior:" + estatAnterior + "\n" +
							"\tEstat actual:" + estat + "\n" +
							((comentari != null && !comentari.isEmpty()) ? "\tComentari: " + comentari + "\n" : "") +
							rebutjMotiu +
							"\tExpedient: " + expedientTascaEntity.getExpedient().getNomINumero() + "\n" +
							enllacTramitar;
		}


		sendOrSaveEmail(
				responsables,
				subject,
				text,
				EventTipusEnumDto.CANVI_ESTAT_TASCA);

	}

	private void enviarEmailReasignarResponsableTasca(
			ExpedientTascaEntity expedientTascaEntity,
			Set<DadesUsuari> responsables) {
		logger.debug("Enviant correu electrònic per a reassignar responsable de tasca (" +
				"tascaId=" + expedientTascaEntity.getId() + ")");

		String subject = getPrefixRipea() + " Canvi de responsable de la tasca: " + expedientTascaEntity.getMetaTasca().getNom();
		String text =
					"S'ha modificat el responsable de la tasca a RIPEA:\n" +
							"\tNom: " + expedientTascaEntity.getMetaTasca().getNom() + "\n" +
							"\tDescripció: " + expedientTascaEntity.getMetaTasca().getDescripcio() + "\n" +
							"\tResponsable:" + expedientTascaEntity.getResponsables().get(0).getNom() + " (" +
							expedientTascaEntity.getResponsables().get(0).getCodi() + ")";

		sendOrSaveEmail(
				responsables,
				subject,
				text,
				EventTipusEnumDto.CANVI_RESPONSABLES_TASCA);

	}

	private void enviarEmailDelegarTasca(
			ExpedientTascaEntity expedientTascaEntity,
			Set<DadesUsuari> responsables) {
		logger.debug("Enviant correu electrònic delegar tasca (" +
				"tascaId=" + expedientTascaEntity.getId() + "," +
				"delegatCodi" + expedientTascaEntity.getDelegat().getCodi() + ")");

		String subject = getPrefixRipea() + " Delegat tasca: " + expedientTascaEntity.getMetaTasca().getNom();
		String text =
					"S'ha delegat la tasca a RIPEA:\n" +
							"\tNom: " + expedientTascaEntity.getMetaTasca().getNom() + "\n" +
							"\tDescripció: " + expedientTascaEntity.getMetaTasca().getDescripcio() + "\n" +
							"\tDelegat:" + expedientTascaEntity.getDelegat().getNom() + " (" +
							expedientTascaEntity.getDelegat().getCodi() + ")";

		sendOrSaveEmail(
				responsables,
				subject,
				text,
				EventTipusEnumDto.DELEGAT_TASCA);

	}

	private void enviarEmailCancelarDelegacioTasca(
			ExpedientTascaEntity expedientTascaEntity,
			Set<DadesUsuari> responsables,
			String comentari) {
		logger.debug("Enviant correu electrònic cancel·lació delegació tasca (" +
				"tascaId=" + expedientTascaEntity.getId() + ")");

		String subject = getPrefixRipea() + " Cancel·lació delegació tasca: " + expedientTascaEntity.getMetaTasca().getNom();
		String text =
					"S'ha cancel·lat la delegació de la tasca a RIPEA:\n" +
							"\tNom: " + expedientTascaEntity.getMetaTasca().getNom() + "\n" +
							"\tDescripció: " + expedientTascaEntity.getMetaTasca().getDescripcio() + "\n" +
							((comentari != null && !comentari.isEmpty()) ? "\tComentari: " + comentari : "");

		sendOrSaveEmail(
				responsables,
				subject,
				text,
				EventTipusEnumDto.CANCELAR_DELEGACIO_TASCA);

	}

	private void enviarEmailModificacioDataLimitTasca(
			ExpedientTascaEntity expedientTascaEntity,
			Set<DadesUsuari> responsables) {
		logger.debug("Enviant correu electrònic modificació data límit tasca (" +
				"tascaId=" + expedientTascaEntity.getId() + ")");

		String subject = getPrefixRipea() + " Modificació data límit de la tasca: " + expedientTascaEntity.getMetaTasca().getNom();
		String text =
					"S'ha modificat la data límit de la tasca a RIPEA:\n" +
							"\tNom: " + expedientTascaEntity.getMetaTasca().getNom() + "\n" +
							"\tDescripció: " + expedientTascaEntity.getMetaTasca().getDescripcio() + "\n" +
							"\tResponsable:" + expedientTascaEntity.getResponsables().get(0).getNom() + " (" +
							expedientTascaEntity.getResponsables().get(0).getCodi() + ")" + "\n" +
							"\tData límit: " + expedientTascaEntity.getDataLimit();

		sendOrSaveEmail(
				responsables,
				subject,
				text,
				EventTipusEnumDto.MODIFICACIO_DATALIMIT_TASCA);

	}

	private void sendOrSaveEmail(
			String codi,
			String subject,
			String text,
			EventTipusEnumDto eventTipus) {

		List<String> destinatarisAgrupats = new ArrayList<String>();
		List<String> destinatarisNoAgrupats = new ArrayList<String>();

		addDestinatari(codi, destinatarisNoAgrupats, destinatarisAgrupats, null, null);

		sendOrSaveEmail(
				destinatarisNoAgrupats,
				destinatarisAgrupats,
				subject,
				text,
				eventTipus);

	}

	public String getPrefixRipea() {
		String entorn = configHelper.getConfig(PropertyConfig.ENTORN);
		String prefix;
		if (entorn == null || entorn.equals("PRO")) {
			prefix = "[RIPEA]";
		} else {
			prefix = "[RIPEA-" + entorn + "]";
		}
		return prefix;
	}

	private void sendOrSaveEmail(
			Set<DadesUsuari> responsables,
			String subject,
			String text,
			EventTipusEnumDto eventTipus) {

		List<String> destinatarisAgrupats = new ArrayList<String>();
		List<String> destinatarisNoAgrupats = new ArrayList<String>();

		for (DadesUsuari responsable : responsables) {
			addDestinatari(responsable.getCodi(), destinatarisNoAgrupats, destinatarisAgrupats, null, null);
		}

		sendOrSaveEmail(
				destinatarisNoAgrupats,
				destinatarisAgrupats,
				subject,
				text,
				eventTipus);

	}

    public void enviarDocument(Long adjuntId, List<String> emails, List<String> desinataris) {
    	List<String> destinatarisNoAgrupats = new ArrayList<>(emails);
        List<String> destinatarisAgrupats = new ArrayList<>();

        for (String desinatariCodi:desinataris) {
            addDestinatari(
                    desinatariCodi,
                    destinatarisNoAgrupats,
                    destinatarisAgrupats,
                    EventTipusEnumDto.ENVIAR_FICHERO,
                    "Email enviarDocument");
        }

        UsuariEntity usuariEntity = usuariHelper.getUsuariAutenticat();
        FitxerDto fitxer = documentHelper.getFitxerAssociat(adjuntId, null);
        DocumentEntity document= documentRepository.findById(adjuntId).orElse(null);

        String subject = getPrefixRipea() + " Enviar document";
        String text ="Ha rebut el document adjunt '"+fitxer.getNom()+"' a partir de la funció 'Enviar document via email' de RIPEA. \n" +
                "Enviat per: " + usuariEntity.getNom() + "\n" +
                "Expedient: " + document.getExpedient().getNom();

        sendOrSaveEmail(
                destinatarisNoAgrupats,
                destinatarisAgrupats,
                subject,
                text,
                EventTipusEnumDto.ENVIAR_FICHERO,
                adjuntId);
    }

    private void sendOrSaveEmail(
            List<String> destinatarisNoAgrupats,
            List<String> destinatarisAgrupats,
            String subject,
            String text,
            EventTipusEnumDto eventTipus) {
        sendOrSaveEmail(
                destinatarisNoAgrupats,
                destinatarisAgrupats,
                subject,
                text,
                eventTipus,
                null);
    }

	private void sendOrSaveEmail(
			List<String> destinatarisNoAgrupats,
			List<String> destinatarisAgrupats,
			String subject,
			String text,
			EventTipusEnumDto eventTipus,
            Long adjuntId) {

		// remove duplicats
		destinatarisNoAgrupats = new ArrayList<>(new HashSet<>(destinatarisNoAgrupats));
		destinatarisAgrupats = new ArrayList<>(new HashSet<>(destinatarisAgrupats));

		String from = getRemitent();

		if (Utils.isNotEmpty(destinatarisNoAgrupats)) {
			String[] to = destinatarisNoAgrupats.toArray(new String[destinatarisNoAgrupats.size()]);
            MimeMessage message = mailSender.createMimeMessage();

            try {
                MimeMessageHelper helper = new MimeMessageHelper(message, true);
                helper.setFrom(from);

                switch (eventTipus) {
                    case CANVI_ESTAT_REVISIO:
                    case PROCEDIMENT_COMENTARI:
                    case NOVA_ANOTACIO:
                        helper.setBcc(to);
                        break;
                    default:
                        helper.setTo(to);
                        break;
                }

                helper.setSubject(subject);
                helper.setText(text);

                if (adjuntId != null) {
                    FitxerDto fitxer = documentHelper.getFitxerAssociat(adjuntId, null);
                    if (fitxer != null) {
                        helper.addAttachment(fitxer.getNom(), new ByteArrayResource(fitxer.getContingut()));
                    }
                }
                mailSender.send(message);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
		}

		if (Utils.isNotEmpty(destinatarisAgrupats)) {
			for (String dest : destinatarisAgrupats) {
				EmailPendentEnviarEntity enitity = EmailPendentEnviarEntity.getBuilder(
						from,
						dest,
						subject,
						text,
						eventTipus,
                        adjuntId)
						.build();
				emailPendentEnviarRepository.save(enitity);
			}
		}
	}

	private void addDestinatari(
			String codi,
			List<String> emailsNoAgrupats,
			List<String> emailsAgrupats,
			EventTipusEnumDto event,
			String logMsg) {
		boolean addDestinatari = false;
		String email = null;
		UsuariEntity usuari = usuariHelper.getUsuariByCodiDades(codi, false, false);
		if (usuari != null) {
			email = getEmail(usuari);
			if (Utils.isNotEmpty(email)) {
                if (event == null || EventTipusEnumDto.ENVIAR_FICHERO.equals(event)) {
                    addDestinatari = true;
                } else if (EventTipusEnumDto.NOVA_ANOTACIO.equals(event)) {
                    addDestinatari = usuari.isRebreAvisosNovesAnotacions();
                } else if (EventTipusEnumDto.CANVI_ESTAT_REVISIO.equals(event)){
                    addDestinatari = usuari.isRebreEmailsCanviEstatRevisio();
                }
			}
		}

		if (addDestinatari) {
			if (usuari.isRebreEmailsAgrupats()) {
				emailsAgrupats.add(email);
			} else {
				emailsNoAgrupats.add(email);
			}
			if (cacheHelper.mostrarLogsEmail() && Utils.isNotEmpty(logMsg)) {
				logger.info(logMsg);
			}
		}
	}


	private String getEmail(UsuariEntity usuari) {
		return Utils.isNotEmpty(usuari.getEmailAlternatiu()) ? usuari.getEmailAlternatiu() : usuari.getEmail();
	}

	private String getRemitent() {
		return configHelper.getConfig("es.caib.ripea.email.remitent");
	}

	private static final Logger logger = LoggerFactory.getLogger(EmailHelper.class);

}
