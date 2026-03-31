package es.caib.ripea.plugin.caib.procediment;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Rolsac2ProcedimentFilterRequest {
		private String codigoSia;
		private String codigoUADir3;
		/*
		 * Possibles valors:
		 * <ul>
		 * <li>'A' = Alta</li>
		 * <li>'B' = Baja</li>
		 * <li>'N' = No integrado</li>
		 * </ul>
		 */
		private String estadoSia;
		/*
		 * Possibles valors: 0/1
		 */
		private Integer buscarEnDescendientesUA;
		/*
		 * Possibles valors: 0/1
		 * corresponde a visible en SEDE
		 */
		private Integer activo;
		Rolsac2FiltrePaginacio filtroPaginacion;
}