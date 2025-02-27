package es.caib.ripea.service.service;

import es.caib.ripea.service.intf.dto.MonitorTascaEstatEnum;
import es.caib.ripea.service.intf.dto.MonitorTascaInfo;
import es.caib.ripea.service.intf.service.MonitorTasquesService;
import org.springframework.stereotype.Service;

import java.util.*;



@Service
public class MonitorTasquesServiceImpl implements MonitorTasquesService {
	
	private static Map<String, MonitorTascaInfo> tasques = new HashMap<>();

	@Override
	public MonitorTascaInfo addTasca(String codiTasca) {
		
		MonitorTascaInfo monitorTascaInfo = new MonitorTascaInfo();
		monitorTascaInfo.setCodi(codiTasca);
		monitorTascaInfo.setEstat(MonitorTascaEstatEnum.EN_ESPERA);
		
		MonitorTasquesServiceImpl.tasques.put(codiTasca, monitorTascaInfo);
		
		return monitorTascaInfo;
	}


	@Override
	public void updateProperaExecucio(String codi, Long plusValue) {
		
		MonitorTascaInfo monitorTascaInfo = MonitorTasquesServiceImpl.tasques.get(codi);
		Date dataProperaExecucio = plusValue != null ? new Date(System.currentTimeMillis() + plusValue) : null;
		monitorTascaInfo.setProperaExecucio(dataProperaExecucio);
	}
	
	@Override
	public List<MonitorTascaInfo> findAll() {
		
		List<MonitorTascaInfo> monitorTasques = new ArrayList<>();
		for(Map.Entry<String, MonitorTascaInfo> tasca : MonitorTasquesServiceImpl.tasques.entrySet()) {
			monitorTasques.add(tasca.getValue());
		}
		return monitorTasques;
	}

	@Override
	public MonitorTascaInfo findByCodi(String codi) {
		return MonitorTasquesServiceImpl.tasques.get(codi);
	}

	@Override
	public void inici(String codiTasca) {
		
		MonitorTascaInfo monitorTascaInfo = MonitorTasquesServiceImpl.tasques.get(codiTasca);
		monitorTascaInfo.setEstat(MonitorTascaEstatEnum.EN_EXECUCIO);
		monitorTascaInfo.setDataInici(new Date());
		monitorTascaInfo.setDataFi(null);
		monitorTascaInfo.setObservacions(null);
		monitorTascaInfo.setProperaExecucio(null);
	}

	@Override
	public void fi(String codiTasca) {
		
		MonitorTascaInfo monitorTascaInfo = MonitorTasquesServiceImpl.tasques.get(codiTasca);
		monitorTascaInfo.setEstat(MonitorTascaEstatEnum.EN_ESPERA);
		monitorTascaInfo.setDataFi(new Date());
	}

	@Override
	public void error(String codiTasca, String error) {
		
		MonitorTascaInfo monitorTascaInfo = MonitorTasquesServiceImpl.tasques.get(codiTasca);
		monitorTascaInfo.setEstat(MonitorTascaEstatEnum.ERROR);
		monitorTascaInfo.setDataFi(new Date());
		monitorTascaInfo.setObservacions(error);
	}

	@Override
	public void reiniciarTasquesEnSegonPla(String codiTasca) {
		
		List<MonitorTascaInfo> tasques = this.findAll();
		for (MonitorTascaInfo tasca : tasques) {
			if (tasca.getCodi().equals(codiTasca) || "totes".equals(codiTasca)) {
				tasca.setEstat(MonitorTascaEstatEnum.EN_ESPERA);
			}
		}
	}

}
