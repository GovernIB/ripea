/**
 * 
 */
package es.caib.ripea.service.intf.dto;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.voodoodyne.jackson.jsog.JSOGGenerator;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.*;

/**
 * Informació d'un contingut.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@JsonIdentityInfo(generator = JSOGGenerator.class)
public abstract class ContingutDto extends AuditoriaDto {

	@ToString.Include
	protected Long id;
	@ToString.Include
	protected String nom;
	protected List<ContingutDto> fills;

	protected Map<MetaDocumentDto, List<ContingutDto>> mapPerTipusDocument;
	protected Map<ExpedientEstatDto, List<ContingutDto>> mapPerEstat;

	protected List<ContingutDto> path;
	protected ExpedientDto expedientPare;
	protected EntitatDto entitat;
	@ToString.Include
	protected int esborrat;
	@ToString.Include
	protected String arxiuUuid;
	protected Date arxiuDataActualitzacio;
	protected Date darrerMovimentData;
	protected UsuariDto darrerMovimentUsuari;
	protected String darrerMovimentComentari;
	private boolean alerta;
	private boolean hasFills;
	protected Date esborratData;
	private boolean admin;

	private boolean conteDocumentsDefinitius;
	private boolean conteDocumentsPendentsReintentsArxiu;

	protected int ordre;

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


	public List<ContingutDto> getFillsHierarchical() {
		return fills;
	}

	public List<ContingutDto> getFillsFlat() {
		List<ContingutDto> fillsFlat = new ArrayList<ContingutDto>();
		if (fills != null) {
			for (ContingutDto fill : fills) {
				getFillsFlat(fill, fillsFlat);
			}
		}
		return fillsFlat;
	}


	public void getFillsFlat(ContingutDto contenidor, List<ContingutDto> fillsFlat) {
		if (contenidor instanceof CarpetaDto) {
			if (contenidor.getFills() != null) {
				for (ContingutDto fill : contenidor.getFills()) {
					getFillsFlat(fill, fillsFlat);
				}
			}
		} else {
			fillsFlat.add(contenidor);
		}
	}


	public int getFillsFlatCount() {
		return getFillsFlat().size();
	}


	public int getFillsHierarchicalCount() {
		if (fills == null) {
			return 0;
		} else {
			int count = 0;
			for (ContingutDto contenidor : fills) {
				count++;
				// No contar resultat concatenació i zip
				if (contenidor.isDocument() &&
						((DocumentDto) contenidor).getDocumentTipus().equals(DocumentTipusEnumDto.VIRTUAL)) {
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

	public Long getExpedientId() {
		ExpedientDto expedient = getExpedientObject();
		if (expedient != null) {
			return expedient.getId();
		} else {
			return null;
		}
	}

	public ExpedientDto getExpedientObject() {
		if (isExpedient()) {
			return ((ExpedientDto) this);
		} else {
			if (expedientPare != null) {
				return expedientPare;
			} else {
				return null;
			}
		}
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

	protected abstract ContingutDto copiarContenidor(ContingutDto original);

}
