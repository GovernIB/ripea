package es.caib.ripea.core.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class ProgresActualitzacioDto {

	Integer progres = 0;
	Integer numProcediments;
	Integer numProcedimentsActualitzats = 0;
	List<ActualitzacioInfo> info = new ArrayList<>();
	boolean finished = false;
	
	boolean error = false;
	String errorMsg;
	
	public void addInfo(ActualitzacioInfo detall) {
		info.add(detall);
		incrementProcedimentsActualitzats();
	}
	
	public void incrementProcedimentsActualitzats() {
		if (numProcediments == null) {
			return;
		}
		this.numProcedimentsActualitzats++;
		double auxprogres = (this.numProcedimentsActualitzats.doubleValue()  / this.numProcediments.doubleValue()) * 100;
		this.progres = (int) auxprogres;
	}

}
