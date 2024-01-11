package es.caib.ripea.core.api.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
public class FirmaResultatDto {
	
	private StatusEnumDto status;
	private String msg;
	private List<FirmaSignatureStatus> signatures = new ArrayList<>();
	
	public FirmaResultatDto(
			StatusEnumDto status,
			String msg) {
		this.status = status;
		this.msg = msg;
	}
	
	
	public StatusEnumDto getStatus() {
		return status;
	}
	public String getMsg() {
		return msg;
	}
	
	public void addSignature(FirmaSignatureStatus firmaSignatureStatus){
		signatures.add(firmaSignatureStatus);
	}

	
	@Getter
	@Setter
	public static class FirmaSignatureStatus {
		
		
		public FirmaSignatureStatus(
				String signID,
				StatusEnumDto status,
				String msg) {
			this.signID = signID;
			this.status = status;
			this.msg = msg;
		}
		
		
		public FirmaSignatureStatus(
				String signID,
				StatusEnumDto status,
				String fitxerFirmatNom,
				byte[] fitxerFirmatContingut) {
			this.signID = signID;
			this.status = status;
			this.fitxerFirmatNom = fitxerFirmatNom;
			this.fitxerFirmatContingut = fitxerFirmatContingut;
		}


		private String signID;
		private StatusEnumDto status;
		private String msg;
		private String fitxerFirmatNom; 
		private byte[] fitxerFirmatContingut;
		
		
	}

}
