package es.caib.ripea.plugin.registre;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RegistreInteressat {

	private Long tipusInteressat;
	private RegistreInteressatDocumentTipusEnum tipusDocumentIdentificacio;
	private String documentNum;
	private String nom;
	private String llinatge1;
	private String llinatge2;
	private String raoSocial;
	private String pais;
	private String provincia;
	private String municipi;
	private String adresa;
	private String codiPostal;
	private String email;
	private String telefon;
	private String emailHabilitat;
	private String canalPreferent;
	private String observacions;
	
}
