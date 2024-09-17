package es.caib.ripea.core.api.service;

import java.util.List;

import es.caib.ripea.core.api.dto.MonitorTascaInfo;



public interface MonitorTasquesService {
	

	public MonitorTascaInfo addTasca(String codiTasca);

	public void updateProperaExecucio(String codi, Long plusValue);

	public List<MonitorTascaInfo> findAll();

	public MonitorTascaInfo findByCodi(String codi);

	public void inici(String codiTasca);

	public void fi(String codiTasca);

	public void error(String codiTasca, String error);

	/** Mètode per posar totes les tasques en espera abans de reiniciar les tasques des de la 
	 * configuració dels paràmetres i la configuracío.
	 * 
	 */
	public void reiniciarTasquesEnSegonPla(String codiTasca);

}
