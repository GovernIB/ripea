package es.caib.ripea.service.intf.model;

import java.io.Serializable;
import java.util.List;

import es.caib.comanda.model.server.monitoring.InformacioSistema;
import es.caib.comanda.ms.salut.helper.MonitorHelper.CpuUsage;
import es.caib.comanda.ms.salut.helper.MonitorHelper.DiskUsage;
import es.caib.comanda.ms.salut.helper.MonitorHelper.JvmInfo;
import es.caib.comanda.ms.salut.helper.MonitorHelper.MemoryUsage;
import es.caib.comanda.ms.salut.helper.MonitorHelper.SystemInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SystemInfoResource implements Serializable {
		
	private SystemInfo systemInfo;
	private InformacioSistema informacioSistema;
	private JvmInfo jvmInfo;
	private MemoryUsage jvmMemory;
	private CpuUsage cpuUsage;
	private DiskUsage rootDiskUsage;
	private List<DiskUsage> disksUsage;
	private MemoryUsage phisicalMemory;
	private String applicationServerInfo;
	private String jbossVersion;
	
	private static final long serialVersionUID = 4916970368278159299L;
}