package es.caib.ripea.service.helper;

import es.caib.ripea.persistence.repository.ContingutRepository;
import es.caib.ripea.persistence.repository.ExecucioMassivaContingutRepository;
import es.caib.ripea.persistence.entity.*;
import es.caib.ripea.service.firma.DocumentFirmaPortafirmesHelper;
import es.caib.ripea.service.intf.dto.ElementTipusEnumDto;
import es.caib.ripea.service.intf.dto.ExecucioMassivaEstatDto;
import es.caib.ripea.service.intf.dto.ExecucioMassivaTipusDto;
import es.caib.ripea.service.intf.exception.ArxiuJaGuardatException;
import es.caib.ripea.service.intf.exception.NotFoundException;
import es.caib.ripea.service.intf.utils.Utils;
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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class ExecucioMassivaHelper{

	@Autowired private ExecucioMassivaContingutRepository execucioMassivaContingutRepository;
	@Autowired private AlertaHelper alertaHelper;
	@Autowired private MessageHelper messageHelper;
	@Autowired private PluginHelper pluginHelper;
	@Autowired private DocumentFirmaPortafirmesHelper firmaPortafirmesHelper;
	@Autowired private OrganGestorHelper organGestorHelper;
	@Autowired private ContingutRepository contingutRepository;
	@Autowired private ExpedientHelper expedientHelper;
	@Autowired private EntityComprovarHelper entityComprovarHelper;
	@Autowired private ExpedientInteressatHelper expedientInteressatHelper;
	@Autowired private DocumentHelper documentHelper;
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public String executarExecucioMassivaContingutNewTransaction(Long execucioMassivaContingutId) {
		
		Throwable exc = null;
		String resultat = null;
		ExecucioMassivaContingutEntity emc = execucioMassivaContingutRepository.getOne(execucioMassivaContingutId);
		
		if (emc == null)
			throw new NotFoundException(execucioMassivaContingutId, ExecucioMassivaContingutEntity.class);
		
		if (!ExecucioMassivaEstatDto.ESTAT_FINALITZAT.equals(emc.getEstat())) {
		
			ExecucioMassivaEntity exm = emc.getExecucioMassiva();
			ExecucioMassivaTipusDto tipus = exm.getTipus();
			
			try {
				
				Authentication orgAuthentication = SecurityContextHolder.getContext().getAuthentication();
				final String user = exm.getCreatedBy().get().getCodi();
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
		        
				if (ExecucioMassivaTipusDto.PORTASIGNATURES.equals(tipus)){
					exc = enviarPortafirmes(emc);
				} else if (ExecucioMassivaTipusDto.CUSTODIAR_ELEMENTS_PENDENTS.equals(tipus)){
					exc = custodiarElementsPendents(emc);
				}
				
				SecurityContextHolder.getContext().setAuthentication(orgAuthentication);
				
				if (exc == null) {
					alertaHelper.crearAlerta(
							messageHelper.getMessage(
									"alertes.segon.pla.execucio.massiva",
									new Object[] {execucioMassivaContingutId}),
							null,
							emc.getElementId());
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

	private Throwable enviarPortafirmes(ExecucioMassivaContingutEntity emc) throws Exception {
		ContingutEntity contingut = contingutRepository.getOne(emc.getElementId());
		organGestorHelper.actualitzarOrganCodi(organGestorHelper.getOrganCodiFromContingutId(contingut.getId()));
		Throwable exc = null;
		try {
			ExecucioMassivaEntity em = emc.getExecucioMassiva();
			firmaPortafirmesHelper.portafirmesEnviar(
					contingut.getEntitat().getId(),
					(DocumentEntity)contingut,
					em.getMotiu(),
					em.getPrioritat(),
					null,
					em.getPortafirmesFluxId(),
					em.getPortafirmesResponsables() != null ? em.getPortafirmesResponsables().split(",") : null,
					em.getPortafirmesSequenciaTipus(),
					((DocumentEntity) contingut).getMetaDocument().getPortafirmesFluxTipus(),
					null,
					em.getPortafirmesTransaccioId(),
					em.getPortafirmesAvisFirmaParcial(),
					em.getPortafirmesFirmaParcial());
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