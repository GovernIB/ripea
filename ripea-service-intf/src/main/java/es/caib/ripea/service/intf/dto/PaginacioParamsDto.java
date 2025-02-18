/**
 * 
 */
package es.caib.ripea.service.intf.dto;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Dto amb els paràmetres per a paginar i ordenar els
 * resultats d'una consulta.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class PaginacioParamsDto implements Serializable {

	private int paginaNum;
	private int paginaTamany;
	private String filtre;
	private List<FiltreDto> filtres = new ArrayList<FiltreDto>();
	private List<OrdreDto> ordres = new ArrayList<OrdreDto>();

	public void canviaCampOrdenacio(String campInicial, String campFinal) {
		if (ordres!=null) {
			for (OrdreDto ordre: this.ordres) {
				if (campInicial.equals(ordre.camp)) {
					ordre.setCamp(campFinal);
				}
			}
		}
	}
	
	public void eliminaCampOrdenacio(String campEliminar) {
		if (ordres!=null) {
			for (int o=this.ordres.size()-1; o>=0; o--) {
				if (campEliminar.equals(this.ordres.get(o).camp)) {
					this.ordres.remove(o);
				}
			}
		}
	}

	public void afegirFiltre(String camp, String valor) {
		getFiltres().add(new FiltreDto(camp, valor));
	}
	public void afegirOrdre(String camp, OrdreDireccioDto direccio) {
		getOrdres().add(new OrdreDto(camp, direccio));
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public static class FiltreDto implements Serializable {
		private String camp;
		private String valor;
		public FiltreDto(String camp, String valor) {
			this.camp = camp;
			this.valor = valor;
		}
		public String getCamp() {
			return camp;
		}
		public void setCamp(String camp) {
			this.camp = camp;
		}
		public String getValor() {
			return valor;
		}
		public void setValor(String valor) {
			this.valor = valor;
		}
		private static final long serialVersionUID = -139254994389509932L;
	}

	public enum OrdreDireccioDto {
		ASCENDENT,
		DESCENDENT
	}
	public static class OrdreDto implements Serializable {
		private String camp;
		private OrdreDireccioDto direccio;
		public OrdreDto(String camp, OrdreDireccioDto direccio) {
			this.camp = camp;
			this.direccio = direccio;
		}
		public String getCamp() {
			return camp;
		}
		public void setCamp(String camp) {
			this.camp = camp;
		}
		public OrdreDireccioDto getDireccio() {
			return direccio;
		}
		public void setDireccio(OrdreDireccioDto direccio) {
			this.direccio = direccio;
		}
		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this);
		}
		private static final long serialVersionUID = -139254994389509932L;
	}

	private static final long serialVersionUID = -139254994389509932L;

}
