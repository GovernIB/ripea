/**
 * 
 */
package es.caib.ripea.core.helper;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.entity.ContingutEntity;
import es.caib.ripea.core.entity.DocumentEntity;
import es.caib.ripea.core.entity.ExecucioMassivaContingutEntity;
import es.caib.ripea.core.entity.ExecucioMassivaEntity;
import es.caib.ripea.core.entity.ExecucioMassivaEntity.ExecucioMassivaTipus;
import es.caib.ripea.core.firma.DocumentFirmaPortafirmesHelper;
import es.caib.ripea.core.repository.ExecucioMassivaContingutRepository;


@Component
public class ExecucioMassivaHelper{


	@Autowired
	private ExecucioMassivaContingutRepository execucioMassivaContingutRepository;
	@Autowired
	private AlertaHelper alertaHelper;
	@Autowired
	private MessageHelper messageHelper;
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private DocumentFirmaPortafirmesHelper firmaPortafirmesHelper;
	@Autowired
	private OrganGestorHelper organGestorHelper;
	
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Throwable executarExecucioMassivaContingutNewTransaction(Long execucioMassivaContingutId) {
		Throwable exc = null;
		
		ExecucioMassivaContingutEntity emc = execucioMassivaContingutRepository.findOne(execucioMassivaContingutId);
		if (emc == null)
			throw new NotFoundException(execucioMassivaContingutId, ExecucioMassivaContingutEntity.class);
		ExecucioMassivaEntity exm = emc.getExecucioMassiva();
		ExecucioMassivaTipus tipus = exm.getTipus();
		try {
			Authentication orgAuthentication = SecurityContextHolder.getContext().getAuthentication();
			final String user = exm.getCreatedBy().getCodi();
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
			if (tipus == ExecucioMassivaTipus.PORTASIGNATURES){
				exc = enviarPortafirmes(emc);
			}
			SecurityContextHolder.getContext().setAuthentication(orgAuthentication);
			
			if (exc == null) {
				alertaHelper.crearAlerta(
						messageHelper.getMessage(
								"alertes.segon.pla.execucio.massiva",
								new Object[] {execucioMassivaContingutId}),
						null,
						emc.getContingut().getId());
			}
			
		} catch (Throwable e) {
			exc = e;
		}
		return exc;
	}
	
	
	public Throwable enviarPortafirmes(ExecucioMassivaContingutEntity emc) throws Exception {
		

		ContingutEntity contingut = emc.getContingut();
		organGestorHelper.actualitzarOrganCodi(organGestorHelper.getOrganCodiFromContingutId(contingut.getId()));
		Throwable exc = null;
		try {
			emc.updateDataInici(new Date());

			ExecucioMassivaEntity em = emc.getExecucioMassiva();
			firmaPortafirmesHelper.portafirmesEnviar(
					contingut.getEntitat().getId(),
					(DocumentEntity)contingut,
					em.getMotiu(),
					em.getPrioritat(),
					null,
					em.getPortafirmesFluxId(),
					em.getPortafirmesResponsables() != null ? em.getPortafirmesResponsables().split(",") : null,
//					((DocumentEntity) contingut).getMetaDocument().getPortafirmesResponsables(),
					em.getPortafirmesSequenciaTipus(),
//					((DocumentEntity) contingut).getMetaDocument().getPortafirmesSequenciaTipus(),
					((DocumentEntity) contingut).getMetaDocument().getPortafirmesFluxTipus(),
					null,
					em.getPortafirmesTransaccioId(),
					em.getPortafirmesAvisFirmaParcial());
			
				
			emc.updateFinalitzat(new Date());
			execucioMassivaContingutRepository.save(emc);
		} catch (Exception ex) {
			logger.error("CONTINGUT MASSIU:" + emc.getId() + ". No s'ha pogut enviar el document al portasignatures", ex);
			exc = ex;
		}
		return exc;
	}
	
	

	
	

	
	private static final Logger logger = LoggerFactory.getLogger(ExecucioMassivaHelper.class);

}
