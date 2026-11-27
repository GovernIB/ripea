package es.caib.ripea.service.intf.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import es.caib.ripea.service.intf.base.annotation.ResourceArtifact;
import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.model.BaseResource;
import es.caib.ripea.service.intf.base.model.ResourceArtifactType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Pantalla d'inici de la interfície REACT (issue #1510): targetes d'indicadors i llistes resum
 * (mini-graelles) del rol actiu.
 *
 * Recurs sense taula: no té llistat ni detall propis, només informes de col·lecció que el front demana
 * en carregar la pantalla, i que torna a demanar quan canvia el rol, l'entitat o l'òrgan gestor actius:
 * <ul>
 *   <li>{@link #REPORT_INDICADORS}: les targetes del rol actiu, amb el valor de cada una.</li>
 *   <li>{@link #REPORT_LLISTES}: quines llistes resum té el rol actiu (només els codis, sense consultes).</li>
 *   <li>{@link #REPORT_LLISTA}: una llista resum amb les seves primeres files. El front en demana cada una per
 *   separat i en paral·lel, perquè una llista lenta no endarrereixi les altres.</li>
 * </ul>
 * El backend decideix quins indicadors i quines llistes veu cada rol (catàleg tancat per rol, sense
 * configuració per usuari) i en quin ordre. El front els presenta a partir del codi: títol,
 * descripció, icona, color i pantalla de destinació.
 */
@SuppressWarnings("serial")
@Getter
@Setter
@NoArgsConstructor
@ResourceConfig(
		artifacts = {
				@ResourceArtifact(
						type = ResourceArtifactType.REPORT,
						code = IniciResource.REPORT_INDICADORS),
				@ResourceArtifact(
						type = ResourceArtifactType.REPORT,
						code = IniciResource.REPORT_LLISTES),
				@ResourceArtifact(
						type = ResourceArtifactType.REPORT,
						code = IniciResource.REPORT_LLISTA,
						formClass = IniciResource.LlistaForm.class),
		})
public class IniciResource extends BaseResource<Long> {

	public static final String REPORT_INDICADORS = "INDICADORS";
	public static final String REPORT_LLISTES = "LLISTES";
	public static final String REPORT_LLISTA = "LLISTA";

	/** Paràmetres de l'informe {@link #REPORT_LLISTA}. */
	@Getter
	@Setter
	@NoArgsConstructor
	public static class LlistaForm implements Serializable {
		/** Codi de la llista: ha de ser una de les del rol actiu (informe {@link #REPORT_LLISTES}). */
		private String codi;
	}

	/** Targeta d'indicador de la pantalla d'inici. */
	@Getter
	@Setter
	@NoArgsConstructor
	public static class Indicador implements Serializable {
		/** Codi de l'indicador: el front en deriva la presentació. */
		private String codi;
		/** Valor de la targeta. Null si no s'ha pogut calcular. */
		private Long valor;
		/** Valor de referència opcional, per exemple el total de memòria o de disc (en bytes). */
		private Long total;
		/** Dies que fa servir el càlcul (configurables), perquè el text de la targeta els pugui mostrar. */
		private Integer dies;
		/** Cert si el càlcul ha fallat: la targeta es mostra sense valor i la resta de la pantalla, normalment. */
		private boolean error;
		/** Cert si el càlcul ha fallat perquè una consulta ha superat el límit de temps (també té error). */
		private boolean tempsEsgotat;
	}

	/** Llista resum (mini-graella) de la pantalla d'inici. */
	@Getter
	@Setter
	@NoArgsConstructor
	public static class Llista implements Serializable {
		/** Codi de la llista: el front en deriva el títol, la descripció, les columnes i els enllaços. */
		private String codi;
		/**
		 * Primeres files de la llista (com a màxim 10), en l'ordre fix de la llista. Sense valor a l'informe
		 * {@link #REPORT_LLISTES}, que només diu quines llistes hi ha. Les claus de cada
		 * fila són les columnes de la llista i els identificadors que necessiten els seus enllaços.
		 */
		private List<Map<String, Object>> files;
		/** Dies que fa servir el criteri de la llista (configurables), perquè la descripció els pugui mostrar. */
		private Integer dies;
		/**
		 * Minuts configurables que fa servir la llista, perquè la descripció els pugui mostrar (excepcions: finestra en què
		 * les repeticions d'una excepció s'agrupen en una fila; null si no s'agrupen).
		 */
		private Integer minuts;
		/** Cert si la consulta ha fallat: la llista es mostra amb un missatge d'error i la resta, normalment. */
		private boolean error;
		/** Cert si la consulta ha fallat perquè ha superat el límit de temps (també té error). */
		private boolean tempsEsgotat;
	}

}
