/**
 * 
 */
package es.caib.ripea.core.api.dto;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

import lombok.Getter;
import lombok.Setter;

/**
 * Informació d'un permís.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class PermisDto implements Serializable {

    private Long id;
    private String principalNom;
    private PrincipalTipusEnumDto principalTipus;
    private boolean read;
    private boolean write;
    private boolean create;
    private boolean delete;
    private boolean administration;

    @Override
    public String toString() {
	return ToStringBuilder.reflectionToString(this);
    }

    private static final long serialVersionUID = -139254994389509932L;

}
