package es.caib.ripea.service.intf.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class FirmaResultatDto {
	
	private StatusEnumDto status;
	private String msg;
	private List<FirmaSignatureStatus> signatures = new ArrayList<>();
	private boolean processada = false; //Processada per el client SSE
	private String usuari; //Usuari que ha iniciat el proces de firma
	
	public FirmaResultatDto(StatusEnumDto status, String msg) {
		this.status = status;
		this.msg = msg;
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