/**
 * 
 */
package es.caib.ripea.persistence.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.dto.ContingutTipusEnumDto;
import lombok.Getter;

/**
 * Classe del model de dades que representa una carpeta.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = BaseConfig.DB_PREFIX + "carpeta")
@EntityListeners(AuditingEntityListener.class)
@Getter
public class CarpetaEntity extends ContingutEntity {
 
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "expedient_relacionat")
	@ForeignKey(name = BaseConfig.DB_PREFIX + "carpeta_exprel_fk")
	private ExpedientEntity expedientRelacionat;
	
	@Column(name = "restringida", nullable = false)
	private Boolean restringida = false;
	
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsable_restriccio_codi")
    private UsuariEntity responsableRestriccio;    
	
	@Column(name = "motiu_restriccio")
	private String motiuRestriccio;
	
	@OneToMany(mappedBy = "carpeta", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CarpetaRestriccioEntity> restriccions = new ArrayList<>();
	
	public void updateNom(
			String nom) {
		this.nom = nom;
	}
	
	public void updateExpedientRelacionat(ExpedientEntity expedientRelacionat) {
		this.expedientRelacionat = expedientRelacionat;
	}

	public void updateRestriccio(boolean restringida, String motiuRestriccio, List<UsuariEntity> usuarisRestriccio, UsuariEntity responsableRestriccio) {
		this.restringida = restringida;
		this.motiuRestriccio = motiuRestriccio;
		this.responsableRestriccio = responsableRestriccio;
		
	    Map<String, CarpetaRestriccioEntity> actuales = this.restriccions.stream()
	            .collect(Collectors.toMap(r -> r.getUsuari().getCodi(), r -> r));
	    
	    for (UsuariEntity usuari : usuarisRestriccio) {
	        if (!actuales.containsKey(usuari.getCodi())) {
	        	addUsuariRestriccio(usuari);
	        }
	    }
	    
	    this.restriccions.removeIf(r -> usuarisRestriccio.stream()
	            .noneMatch(u -> u.getCodi().equals(r.getUsuari().getCodi())));
	    
	    if (! restringida) {
	    	this.motiuRestriccio= null; 
	    	this.restriccions.clear();
	    }
	}

	private void addUsuariRestriccio(UsuariEntity usuari) {
	    CarpetaRestriccioEntity rel = new CarpetaRestriccioEntity();
	    rel.setId(new CarpetaRestriccioUsuariId(this.getId(), usuari.getCodi()));
	    rel.setCarpeta(this);
	    rel.setUsuari(usuari);
	    
	    restriccions.add(rel);
	}


	public static Builder getBuilder(
			String nom,
			ContingutEntity pare,
			EntitatEntity entitat,
			ExpedientEntity expedient) {
		return new Builder(
				nom,
				pare,
				entitat,
				expedient);
	}
	public static class Builder {
		CarpetaEntity built;
		Builder(
				String nom,
				ContingutEntity pare,
				EntitatEntity entitat,
				ExpedientEntity expedient) {
			built = new CarpetaEntity();
			built.nom = nom;
			built.pare = pare;
			built.entitat = entitat;
			built.expedient = expedient;
			built.tipus = ContingutTipusEnumDto.CARPETA;
		}
		public CarpetaEntity build() {
			return built;
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		CarpetaEntity other = (CarpetaEntity) obj;
		if (nom == null) {
			if (other.nom != null)
				return false;
		} else if (!nom.equals(other.nom))
			return false;
		return true;
	}
	private static final long serialVersionUID = -2299453443943600172L;

}
