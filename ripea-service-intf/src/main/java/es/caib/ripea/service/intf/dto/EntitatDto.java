package es.caib.ripea.service.intf.dto;

import java.util.List;
import org.apache.commons.lang3.builder.ToStringBuilder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class EntitatDto extends AuditoriaDto {

	private Long id;
	@EqualsAndHashCode.Include
	private String codi;
	private String nom;
	private String descripcio;
	private String cif;
	private String unitatArrel;
	private boolean activa;

	private List<PermisDto> permisos;
	private List<OrganGestorDto> organsGestors;
	
	private boolean usuariActualRead;
	private boolean usuariActualAdministration;
	private boolean usuariActualAdministrationRead;
	
	//Dades de configuracio modo light
	private byte[] logoImgBytes;
	private boolean logo;
	private byte[] faviconImgBytes;
	private boolean logoFavicon;
	private byte[] menuImgBytes;
	private boolean logoMenu;
	private String capsaleraColorFons;
	private String capsaleraColorLletra;
	
	//Dades de configuracio modo dark
	private byte[] blackLogoImgBytes;
	private boolean blackLogo;
	private byte[] blackFaviconImgBytes;
	private boolean blackFavicon;
	private byte[] blackMenuImgBytes;
	private boolean blackMenu;
	private String blackCapsaleraColorFons;
	private String blackCapsaleraColorLletra;	
	
    private boolean permetreEnviamentPostal;
	
	public boolean isUsuariActualTeOrgans() {
		return organsGestors != null && !organsGestors.isEmpty();
	}
	
	public int getPermisosCount() {
		if  (permisos == null)
			return 0;
		else
			return permisos.size();
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
