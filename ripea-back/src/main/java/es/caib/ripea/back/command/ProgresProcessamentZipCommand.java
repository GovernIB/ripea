package es.caib.ripea.back.command;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Command que representa el progrés d'importació de documents.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Getter @Setter
public class ProgresProcessamentZipCommand {

	private Integer progres = 0;
	private Integer numOperacions = 0;
	private Integer numOperacionsRealitzades = 0;
	private boolean error = false;
	private String errorMsg;
	
	List<ProgresProcessamentZipInfo> info = new ArrayList<ProgresProcessamentZipInfo>();
	
	public void addInfo(String text) {

		log.info("[Progres Actualitzacio] " + text);
		info.add(new ProgresProcessamentZipInfo(text));
	}

	public void incrementOperacionsRealitzades() {
		incrementOperacionsRealitzades(1);
	}
	
	public void incrementOperacionsRealitzades(int numOperacions) {
		if (this.numOperacions == null) {
			return;
		}
		this.numOperacionsRealitzades += numOperacions;
		double auxprogres = (this.numOperacionsRealitzades.doubleValue()  / this.numOperacions.doubleValue()) * 100;
		this.progres = (int) auxprogres;
	}
	
	public boolean isFinished() {
		return numOperacions.equals(numOperacionsRealitzades);
	}
	
	@Getter @Setter @AllArgsConstructor @NoArgsConstructor
	public class ProgresProcessamentZipInfo {	
		String text;
	}
}
