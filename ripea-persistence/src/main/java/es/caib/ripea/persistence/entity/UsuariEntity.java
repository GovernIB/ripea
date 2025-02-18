/**
 * 
 */
package es.caib.ripea.persistence.entity;

import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.dto.ContingutVistaEnumDto;
import es.caib.ripea.service.intf.dto.MoureDestiVistaEnumDto;
import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Classe de model de dades que conté la informació d'un usuari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Entity
@Table(name = BaseConfig.DB_PREFIX + "usuari")
public class UsuariEntity implements Serializable {

	@Id
	@Column(name = "codi", length = 64, nullable = false)
	private String codi;
	@Column(name = "nom", length = 200)
	private String nom;
	@Column(name = "nif", length = 9, nullable = false)
	private String nif;
	@Column(name = "email", length = 200)
	private String email;
	@Column(name = "email_alternatiu", length = 200)
	private String emailAlternatiu;
	@Column(name="idioma", length = 2)
	private String idioma;
	@Column(name = "inicialitzat")
	private boolean inicialitzat = false;
	@ManyToMany(
			cascade = CascadeType.ALL,
			fetch = FetchType.EAGER)
	@JoinTable(
			name = BaseConfig.DB_PREFIX + "usuari_viafirma_ripea",
			joinColumns = {@JoinColumn(name = "ripea_user_codi")},
			inverseJoinColumns = {@JoinColumn(name = "viafirma_user_codi")})
	private Set<ViaFirmaUsuariEntity> viaFirmaUsuaris = new HashSet<ViaFirmaUsuariEntity>();
	
	@Column(name="rol_actual", length = 64)
	private String rolActual;
	
	@Column(name="vista_actual", length = 64)
	@Enumerated(EnumType.STRING)
	private ContingutVistaEnumDto vistaActual = ContingutVistaEnumDto.TREETABLE_PER_CARPETA;
	
	@Column(name="num_elements_pagina")
	private Long numElementsPagina;
	
	@Version
	private long version = 0;
	
	
	@Column(name = "emails_agrupats")
	private boolean rebreEmailsAgrupats = true;
	
	@Column(name = "avisos_noves_anotacions")
	private boolean rebreAvisosNovesAnotacions;

	@Column(name = "emails_canvi_estat_revisio")
	private boolean rebreEmailsCanviEstatRevisio = true;
	
	@Column(name = "exp_list_data_darrer_env")
	private boolean expedientListDataDarrerEnviament = false;
	@Column(name = "exp_list_agafat_per")
	private boolean expedientListAgafatPer = true;
	@Column(name = "exp_list_interessats")
	private boolean expedientListInteressats = true;
	@Column(name = "exp_list_comentaris")
	private boolean expedientListComentaris = true;
	@Column(name = "exp_list_grup")
	private boolean expedientListGrup = false;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "metaexpedient_id")
	@ForeignKey(name = BaseConfig.DB_PREFIX + "metaexp_usuari_fk")
	private MetaExpedientEntity procediment;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "entitat_defecte_id")
	@ForeignKey(name = BaseConfig.DB_PREFIX + "entitat_usuari_fk")
	private EntitatEntity entitatPerDefecte;
	
	@Column(name = "expedient_expandit")
	private boolean expedientExpandit = true;

	@Column(name="vista_moure_actual", length = 16)
	@Enumerated(EnumType.STRING)
	private MoureDestiVistaEnumDto vistaMoureActual = MoureDestiVistaEnumDto.LLISTA;
	
	public ContingutVistaEnumDto getVistaActual() {
		return vistaActual;
	}
	
	public String getCodiAndNom() {
		return nom + " (" + codi + ")";
	}
	
	public void updateVistaActual(ContingutVistaEnumDto vistaActual) {
		this.vistaActual = vistaActual;
	}

	
	public void update(
			String emailAlternatiu,
			String idioma,
			boolean rebreEmailsAgrupats,
			boolean rebreAvisosNovesAnotacions, 
			boolean rebreEmailsCanviEstatRevisio,
			Long numElementsPagina,
			boolean expedientListDataDarrerEnviament,
			boolean expedientListAgafatPer,
			boolean expedientListInteressats,
			boolean expedientListComentaris,
			boolean expedientListGrup,
			MetaExpedientEntity procediment,
			ContingutVistaEnumDto vistaActual, 
			boolean expedientExpandit,
			EntitatEntity entitatPerDefecte,
			MoureDestiVistaEnumDto vistaMoureActual) {
		this.emailAlternatiu = emailAlternatiu;
		this.idioma = idioma;
		this.rebreEmailsAgrupats = rebreEmailsAgrupats;
		this.rebreAvisosNovesAnotacions = rebreAvisosNovesAnotacions;
		this.rebreEmailsCanviEstatRevisio = rebreEmailsCanviEstatRevisio;
		this.expedientListDataDarrerEnviament = expedientListDataDarrerEnviament;
		this.expedientListAgafatPer = expedientListAgafatPer;
		this.expedientListInteressats = expedientListInteressats;
		this.expedientListComentaris = expedientListComentaris;
		this.expedientListGrup = expedientListGrup;
		this.procediment = procediment;
		this.vistaActual = vistaActual;
		this.expedientExpandit = expedientExpandit;
		this.entitatPerDefecte = entitatPerDefecte;
		this.vistaMoureActual = vistaMoureActual;
	}

	public void update(
			String nom,
			String nif,
			String email) {
		
		this.nom = trimAndShorten(nom, 200);
		this.email = trimAndShorten(email, 200);
		this.nif = trimAndShortenNif(nif);
		this.inicialitzat = true;
	}

	public void updateProcediment(MetaExpedientEntity procediment) {
		this.procediment = procediment;
	}

	public void removeEntitatPerDefecte() {
		this.entitatPerDefecte = null;
	}
	
	private static String trimAndShortenNif(String value) {
		String valueProcessed = null;
		if (value != null) {
			valueProcessed = value.replaceAll("[-_ ]", "");
			valueProcessed = trimAndShorten(valueProcessed, 9);
		}

		return valueProcessed;
	}
	
	private static String trimAndShorten (String value, int endIndex){
		String valueProcessed = null;
		if (value != null) {
			valueProcessed = value.trim();
			valueProcessed = valueProcessed.substring(0, Math.min(endIndex, valueProcessed.length()));
		}
		
		return valueProcessed;
	}
	

	public void updateRolActual(String rolActual) {
		this.rolActual = rolActual;
	}
	/**
	 * Obté el Builder per a crear objectes de tipus Usuari.
	 * 
	 * @param codi
	 *            El codi de l'usuari.
	 * @param nom
	 *            El nom de l'usuari.
	 * @param nif
	 *            El nif de l'usuari.
	 * @param email
	 *            L'areça de correu electrònic de l'usuari.
	 * @return Una nova instància del Builder.
	 */
	public static Builder getBuilder(
			String codi,
			String nom,
			String nif,
			String email,
			String idioma) {
		return new Builder(
				codi,
				nom,
				nif,
				email,
				idioma);
	}

	/**
	 * Builder per a crear noves instàncies d'aquesta entitat.
	 * 
	 * @author Limit Tecnologies <limit@limit.es>
	 */
	public static class Builder {
		UsuariEntity built;
		Builder(String codi,
				String nom,
				String nif,
				String email,
				String idioma) {
			built = new UsuariEntity();
			built.codi = codi;
			built.nom = trimAndShorten(nom, 200);
			built.nif = trimAndShortenNif(nif);
			built.email = trimAndShorten(email, 200);
			built.idioma = idioma;
			built.inicialitzat = true;
		}
		public UsuariEntity build() {
			return built;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codi == null) ? 0 : codi.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UsuariEntity other = (UsuariEntity) obj;
		if (codi == null) {
			if (other.codi != null)
				return false;
		} else if (!codi.equals(other.codi))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}


	private static final long serialVersionUID = -6657066865382086237L;

}
