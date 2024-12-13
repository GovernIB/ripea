package es.caib.ripea.core.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.ripea.core.api.dto.PinbalServeiDocPermesEnumDto;
import es.caib.ripea.core.audit.RipeaAuditable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ipa_pinbal_servei")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PinbalServeiEntity extends RipeaAuditable<Long> {

	@Column(name = "codi", length = 64, nullable = false)
	private String codi;
	@Column(name = "nom", length = 256)
	private String nom;
	@Column(name = "doc_permes_dni", nullable = false)
	private boolean pinbalServeiDocPermesDni;
	@Column(name = "doc_permes_nif", nullable = false)
	private boolean pinbalServeiDocPermesNif;
	@Column(name = "doc_permes_cif", nullable = false)
	private boolean pinbalServeiDocPermesCif;
	@Column(name = "doc_permes_nie", nullable = false)
	private boolean pinbalServeiDocPermesNie;
	@Column(name = "doc_permes_pas", nullable = false)
	private boolean pinbalServeiDocPermesPas;
	@Column(name = "actiu", nullable = false)
	private boolean actiu;

	public List<PinbalServeiDocPermesEnumDto> getPinbalServeiDocsPermesos() {
		List<PinbalServeiDocPermesEnumDto> pinbalServeiDocsPermesosEnumDto = new ArrayList<>();
		
		if (this.pinbalServeiDocPermesDni) {
			pinbalServeiDocsPermesosEnumDto.add(PinbalServeiDocPermesEnumDto.DNI);
		}
		if (this.pinbalServeiDocPermesNif) {
			pinbalServeiDocsPermesosEnumDto.add(PinbalServeiDocPermesEnumDto.NIF);
		}
		if (this.pinbalServeiDocPermesCif) {
			pinbalServeiDocsPermesosEnumDto.add(PinbalServeiDocPermesEnumDto.CIF);
		}
		if (this.pinbalServeiDocPermesNie) {
			pinbalServeiDocsPermesosEnumDto.add(PinbalServeiDocPermesEnumDto.NIE);
		}
		if (this.pinbalServeiDocPermesPas) {
			pinbalServeiDocsPermesosEnumDto.add(PinbalServeiDocPermesEnumDto.PASSAPORT);
		}
		
		return pinbalServeiDocsPermesosEnumDto;
	}
	
	public void update(List<PinbalServeiDocPermesEnumDto> pinbalServeiDocsPermesos) {

		if (pinbalServeiDocsPermesos != null) {
			this.pinbalServeiDocPermesDni = pinbalServeiDocsPermesos.contains(PinbalServeiDocPermesEnumDto.DNI);
			this.pinbalServeiDocPermesNif = pinbalServeiDocsPermesos.contains(PinbalServeiDocPermesEnumDto.NIF);
			this.pinbalServeiDocPermesCif = pinbalServeiDocsPermesos.contains(PinbalServeiDocPermesEnumDto.CIF);
			this.pinbalServeiDocPermesNie = pinbalServeiDocsPermesos.contains(PinbalServeiDocPermesEnumDto.NIE);
			this.pinbalServeiDocPermesPas = pinbalServeiDocsPermesos.contains(PinbalServeiDocPermesEnumDto.PASSAPORT);
		}	else {
			this.pinbalServeiDocPermesDni = false;
			this.pinbalServeiDocPermesNif = false;
			this.pinbalServeiDocPermesCif = false;
			this.pinbalServeiDocPermesNie = false;
			this.pinbalServeiDocPermesPas = false;
		}
		
	}
	
	private static final long serialVersionUID = 7356212275586432419L;
}