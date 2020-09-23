/**
 * 
 */
package es.caib.ripea.core.api.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

import lombok.Getter;
import lombok.Setter;

/**
 * Informació d'un contingut.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public abstract class ContingutDto extends AuditoriaDto {

	protected Long id;
	protected String nom;
	protected List<ContingutDto> fills;
	protected List<ContingutDto> path;
	protected ExpedientDto expedientPare;
	protected EntitatDto entitat;
	protected int esborrat;
	protected String arxiuUuid;
	protected Date arxiuDataActualitzacio;
	protected Date darrerMovimentData;
	protected UsuariDto darrerMovimentUsuari;
	protected String darrerMovimentComentari;
	private boolean alerta;
	private boolean hasFills;
	protected Date esborratData;

	public boolean isEsborrat() {
		return esborrat > 0;
	}

	public ContingutDto getPare() {
		if (getPath() != null && !getPath().isEmpty())
			return getPath().get(getPath().size() - 1);
		else
			return null;
	}

	public String getPathAsStringWebdav() {
		if (getPath() == null)
			return null;
		StringBuilder pathString = new StringBuilder();
		for (ContingutDto pathElement : getPath()) {
			pathString.append("/");
			if (pathElement instanceof EscriptoriDto) {
				if (entitat != null)
					pathString.append(entitat.getNom());
				else
					pathString.append(pathElement.getNom());
			} else {
				pathString.append(pathElement.getNom());
			}
		}
		return pathString.toString();
	}

	public String getPathAsStringWebdavAmbNom() {
		return getPathAsStringWebdav() + "/" + nom;
	}

	public String getPathAsStringExplorador() {
		if (getPath() == null)
			return null;
		StringBuilder pathString = new StringBuilder();
		for (ContingutDto pathElement : getPath()) {
			if (pathString.length() > 0)
				pathString.append(" / ");
			if (pathElement.isEscriptori()) {
				pathString.append("#E# ");
			} else {
				if (pathElement.isExpedient()) {
					pathString.append("#X# ");
				} else if (pathElement.isCarpeta()) {
					pathString.append("#C# ");
				} else if (pathElement.isDocument()) {
					pathString.append("#D# ");
				}
				pathString.append(pathElement.getNom());
			}
		}
		return pathString.toString();
	}

	public String getPathAsStringExploradorAmbNom() {
		if (isExpedient()) {
			if (getPathAsStringExplorador() != null)
				return getPathAsStringExplorador() + " / #X# " + nom;
			else if (expedientPare != null)
				return "#X# " + expedientPare.getNom() + " / #X# " + nom;
		} else if (isCarpeta()) {
			if (getPathAsStringExplorador() != null)
				return getPathAsStringExplorador() + " / #C# " + nom;
			else if (expedientPare != null)
				return "#X# " + expedientPare.getNom() + " / #C# " + nom;
		} else if (isDocument()) {
			if (getPathAsStringExplorador() != null)
				return getPathAsStringExplorador() + " / #D# " + nom;
			else if (expedientPare != null)
				return "#X# " + expedientPare.getNom() + "/ #D# " + nom;
		} else {
			if (getPathAsStringExplorador() != null)
				return getPathAsStringExplorador() + " / " + nom;
			else if (expedientPare != null)
				return "#X# " + expedientPare.getNom() + "/ " + nom;
		}
		return "/ " + nom;
	}

	public List<ExpedientDto> getFillsExpedients() {
		List<ExpedientDto> expedients = new ArrayList<ExpedientDto>();
		if (fills != null) {
			for (ContingutDto contenidor : fills) {
				if (contenidor instanceof ExpedientDto)
					expedients.add((ExpedientDto)contenidor);
			}
		}
		return expedients;
	}

	public List<ContingutDto> getFillsNoExpedients() {
		List<ContingutDto> noExpedients = new ArrayList<ContingutDto>();
		if (fills != null) {
			for (ContingutDto contenidor : fills) {
				if (!(contenidor instanceof ExpedientDto))
					noExpedients.add(contenidor);
			}
		}
		return noExpedients;
	}

	public List<RegistreAnotacioDto> getFillsRegistres() {
		List<RegistreAnotacioDto> registres = new ArrayList<RegistreAnotacioDto>();
		if (fills != null) {
			for (ContingutDto contenidor : fills) {
				if (contenidor instanceof RegistreAnotacioDto)
					registres.add((RegistreAnotacioDto)contenidor);
			}
		}
		return registres;
	}

	public List<ContingutDto> getFillsNoRegistres() {
		List<ContingutDto> noRegistres = new ArrayList<ContingutDto>();
		if (fills != null) {
			for (ContingutDto contenidor : fills) {
				if (!(contenidor instanceof RegistreAnotacioDto))
					noRegistres.add(contenidor);
			}
		}
		return noRegistres;
	}

	public int getFillsCount() {
		return (fills == null) ? 0 : fills.size();
	}

	public int getFillsExpedientsCount() {
		if (fills == null) {
			return 0;
		} else {
			int count = 0;
			for (ContingutDto contenidor : fills) {
				if (contenidor instanceof ExpedientDto)
					count++;
			}
			return count;
		}
	}

	public int getFillsNoExpedientsCount() {
		if (fills == null) {
			return 0;
		} else {
			int count = 0;
			for (ContingutDto contenidor : fills) {
				if (!(contenidor instanceof ExpedientDto))
					count++;
			}
			return count;
		}
	}

	public int getFillsRegistresCount() {
		if (fills == null) {
			return 0;
		} else {
			int count = 0;
			for (ContingutDto contenidor : fills) {
				if (contenidor instanceof RegistreAnotacioDto)
					count++;
			}
			return count;
		}
	}

	public int getFillsNoRegistresCount() {
		if (fills == null) {
			return 0;
		} else {
			int count = 0;
			for (ContingutDto contenidor : fills) {
				if (!(contenidor instanceof RegistreAnotacioDto))
					count++;
				// No contar resultat concatenació i zip
				if (contenidor.isDocument() &&
						((DocumentDto)contenidor).getDocumentTipus().equals(DocumentTipusEnumDto.VIRTUAL)) {
					count--;
				}
			}
			return count;
		}
	}

	public void setContenidorArrelIdPerPath(Long contenidorArrelId) {
		if (path != null) {
			if (!id.equals(contenidorArrelId)) {
				Iterator<ContingutDto> it = path.iterator();
				boolean trobat = false;
				while (it.hasNext()) {
					ContingutDto pathElement = it.next();
					if (pathElement.getId().equals(contenidorArrelId))
						trobat = true;
					if (!trobat) {
						it.remove();
					}
				}
			} else {
				path = null;
			}
		}
	}

	public boolean isCrearExpedients() {
		return isEscriptori();
	}

	public boolean isReplicatDinsArxiu() {
		return arxiuUuid != null;
	}

	public boolean isExpedient() {
		return this instanceof ExpedientDto;
	}

	public boolean isDocument() {
		return this instanceof DocumentDto;
	}

	public boolean isNode() {
		return this instanceof NodeDto;
	}

	public boolean isCarpeta() {
		return this instanceof CarpetaDto;
	}

	public boolean isEscriptori() {
		return this instanceof EscriptoriDto;
	}

	public boolean isRegistre() {
		return this instanceof RegistreAnotacioDto;
	}

	public ContingutTipusEnumDto getTipus() {
		if (isExpedient()) {
			return ContingutTipusEnumDto.EXPEDIENT;
		} else if (isDocument()) {
			return ContingutTipusEnumDto.DOCUMENT;
		} else if (isCarpeta()) {
			return ContingutTipusEnumDto.CARPETA;
		} else if (isRegistre()) {
			return ContingutTipusEnumDto.REGISTRE;
		} else {
			return null;
		}
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	protected abstract ContingutDto copiarContenidor(ContingutDto original);

}
