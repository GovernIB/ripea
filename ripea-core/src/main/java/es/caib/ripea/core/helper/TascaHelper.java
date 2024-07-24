package es.caib.ripea.core.helper;

import java.util.Calendar;
import java.util.Date;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.entity.ExpedientTascaEntity;
import es.caib.ripea.core.entity.UsuariEntity;
import es.caib.ripea.core.repository.ExpedientTascaRepository;

@Component
public class TascaHelper {
	@Autowired
	private ConfigHelper configHelper;
	@Autowired
	private ExpedientTascaRepository expedientTascaRepository;

	public boolean shouldNotifyAboutDeadline(ExpedientTascaEntity expedientTascaEntity) {

		try {

			boolean shouldNotifyAboutDeadline = false;
			String duracioTasca = expedientTascaEntity.getDuracio();
			int preavisDataLimitEnDies = configHelper.getAsInt("es.caib.ripea.tasca.preavisDataLimitEnDies");

			if (duracioTasca!=null && expedientTascaEntity.getDataInici()!=null) {

				Integer duracioInt = Integer.parseInt(nomesNumeros(duracioTasca));
				Calendar dataLimit = Calendar.getInstance();
				dataLimit.setTime(expedientTascaEntity.getDataInici());

				if (!duracioTasca.endsWith("h")) {
					duracioInt = duracioInt*24; //Si no son hores, son dies el que s'ha indicat
				}

				//Afegim la durada a la data de inici de la tasca (siguin hores o dies passats a hores)
				dataLimit.add(Calendar.HOUR, duracioInt);

				if ((new Date()).after(new DateTime(dataLimit).minusDays(preavisDataLimitEnDies).toDate())) {
					shouldNotifyAboutDeadline = true;
				}

			} else if (expedientTascaEntity.getDataLimit() != null) {
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

	private String nomesNumeros(String duracioIn) {
		StringBuilder numeros = new StringBuilder();
		for (char c : duracioIn.toCharArray()) {
			if (Character.isDigit(c)) {
				numeros.append(c);
			}
		}
		return numeros.toString();
	}

	public ExpedientTascaEntity comprovarTasca(Long expedientTascaId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		ExpedientTascaEntity tasca = expedientTascaRepository.findOne(expedientTascaId);
		
		if (tasca == null)
			throw new NotFoundException(expedientTascaId, ExpedientTascaEntity.class);
		
		if (tasca.getResponsables() != null) {
			boolean pemitted = false;
			for (UsuariEntity responsable : tasca.getResponsables()) {
				if (responsable.getCodi().equals(auth.getName())) {
					pemitted = true;
				}
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
