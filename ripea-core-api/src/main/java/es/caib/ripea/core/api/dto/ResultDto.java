/**
 * 
 */
package es.caib.ripea.core.api.dto;

import java.util.List;

public class ResultDto<T> {

	private PaginaDto<T> pagina;
	private List<Long> ids;
	
	public PaginaDto<T> getPagina() {
		return pagina;
	}
	public void setPagina(PaginaDto<T> pagina) {
		this.pagina = pagina;
	}
	public List<Long> getIds() {
		return ids;
	}
	public void setIds(List<Long> ids) {
		this.ids = ids;
	}

}
