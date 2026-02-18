package es.caib.ripea.service.intf.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
public class ProgresProcessamentZipDto implements Serializable {

	private static final long serialVersionUID = 1333490873786597486L;
	
	private Integer progres = 0;
	private Integer numOperacions = 0;
	private Integer numOperacionsRealitzades = 0;
	private boolean error = false;
	private String errorMsg;

	// Informe
    private int documentsCorrectes = 0;
    private int documentsError = 0;
    private int documentsFirmaError = 0;
    private int carpetesCreades = 0;
    private long tamanyTotal = 0L; // bytes
    private Set<String> carpetesCreadesSet = new HashSet<String>();
    
	private List<String> errorsDetall = new ArrayList<>();
	List<ProgresProcessamentZipInfo> info = new ArrayList<ProgresProcessamentZipInfo>();
	
	public void addInfo(String text) {
		log.info("[Progres Actualitzacio] " + text);
		this.info.add(new ProgresProcessamentZipInfo(text));
	}
	
    public void addErrorFirma(String errorText) {
        log.error(errorText);
        this.errorsDetall.add(errorText);
        this.documentsFirmaError++;
        this.error = true;
        this.errorMsg = errorText;
    }
    
    public void addError(String errorText) {
        log.error(errorText);
        this.errorsDetall.add(errorText);
        this.documentsError++;
        this.error = true;
        this.errorMsg = errorText;
    }
    
    public void addDocumentCorrecte(long tamany) {
    	this.documentsCorrectes++;
    	this.tamanyTotal += tamany;
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
        return this.progres >= 100;
    }
	
	@Getter @Setter @AllArgsConstructor @NoArgsConstructor
	public class ProgresProcessamentZipInfo {	
		String text;
	}
}
