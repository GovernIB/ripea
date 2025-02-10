package es.caib.ripea.service.intf.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class ProgresActualitzacioDto {

	int fase = 0;
	Integer progres = 0;
	Integer numOperacions;
	Integer numElementsActualitzats = 0;
	List<ActualitzacioInfo> info = new ArrayList<>();
	boolean finished = false;
	
	boolean error = false;
	String errorMsg;

	public void addInfo(ActualitzacioInfo detall) {
		addInfo(detall, false);
	}
	public void addInfo(ActualitzacioInfo detall, boolean incrementar) {
		info.add(detall);
		if (incrementar)
			incrementElementsActualitzats();
	}
	
	public void incrementElementsActualitzats() {
		if (numOperacions == null) {
			return;
		}
		this.numElementsActualitzats++;
		double auxprogres = (this.numElementsActualitzats.doubleValue()  / this.numOperacions.doubleValue()) * 100;
		this.setProgres((int) auxprogres);
	}

}
