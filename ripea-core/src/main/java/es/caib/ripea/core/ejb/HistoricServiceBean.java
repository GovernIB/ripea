package es.caib.ripea.core.ejb;

import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.dto.OrganGestorDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.dto.historic.HistoricExpedientDto;
import es.caib.ripea.core.api.dto.historic.HistoricFiltreDto;
import es.caib.ripea.core.api.dto.historic.HistoricInteressatDto;
import es.caib.ripea.core.api.dto.historic.HistoricTipusEnumDto;
import es.caib.ripea.core.api.dto.historic.HistoricUsuariDto;
import es.caib.ripea.core.api.dto.historic.serializer.HistoricInteressatSerializer.RegistresInteressatDiari;
import es.caib.ripea.core.api.dto.historic.serializer.HistoricOrganGestorSerializer;
import es.caib.ripea.core.api.dto.historic.serializer.HistoricOrganGestorSerializer.RegistreOrganGestor;
import es.caib.ripea.core.api.dto.historic.serializer.HistoricUsuariSerializer.RegistresUsuariDiari;
import es.caib.ripea.core.api.service.HistoricService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class HistoricServiceBean implements HistoricService {

	@Autowired
	private HistoricService historicService;

	@Override
	@RolesAllowed({"IPA_API_HIST"})
	public void generateOldHistorics() {
		historicService.generateOldHistorics();
	}
	
	@Override
	@RolesAllowed({"tothom", "IPA_ADMIN"})
	public PaginaDto<HistoricExpedientDto> getPageDadesEntitat(
			Long entitatId,
			HistoricFiltreDto filtre,
			String rolActual,
			PaginacioParamsDto paginacioParams) {
		return historicService.getPageDadesEntitat(entitatId, filtre, rolActual, paginacioParams);
	}

	@Override
	@RolesAllowed({"tothom", "IPA_ADMIN", "IPA_API_HIST"})
	public List<HistoricExpedientDto> getDadesEntitat(Long entitatId, String rolActual, HistoricFiltreDto filtre) {
		return historicService.getDadesEntitat(entitatId, rolActual, filtre);
	}

	@Override
	@RolesAllowed({"tothom", "IPA_ADMIN", "IPA_API_HIST"})
	public Map<Date, Map<OrganGestorDto, HistoricExpedientDto>> getDadesOrgansGestors(
			Long entitatId, 
			String rolActual,
			HistoricFiltreDto filtre) {
		return historicService.getDadesOrgansGestors(entitatId, rolActual, filtre);
	}

	@Override
	@RolesAllowed({"tothom", "IPA_ADMIN", "IPA_API_HIST"})
	public List<HistoricOrganGestorSerializer.RegistresOrganGestor> getRegistresDadesOrgansGestors(Long entitatId, String rolActual, HistoricFiltreDto filtre, HistoricTipusEnumDto tipusAgrupament) {
		return historicService.getRegistresDadesOrgansGestors(entitatId, rolActual, filtre, tipusAgrupament);
	}

	@Override
	@RolesAllowed({"tothom", "IPA_ADMIN"})
	public Map<OrganGestorDto, List<HistoricExpedientDto>> getHistoricsByOrganGestor(
			Long entitatId, 
			String rolActual, 
			HistoricFiltreDto filtre) {
		return historicService.getHistoricsByOrganGestor(entitatId, rolActual, filtre);
	}

	
	@Override
	@RolesAllowed({"tothom", "IPA_ADMIN", "IPA_API_HIST"})
	public List<HistoricUsuariDto> getDadesUsuari(String usuariCodi, Long entitatId, String rolActual, HistoricFiltreDto filtre) {
		return historicService.getDadesUsuari(usuariCodi, entitatId, rolActual, filtre);
	}

	@Override
	@RolesAllowed({"tothom", "IPA_ADMIN", "IPA_API_HIST"})
	public List<RegistresUsuariDiari> getRegistresDadesUsuaris(List<String> usuarisCodi, Long entitatId, String rolActual, HistoricFiltreDto filtre, HistoricTipusEnumDto tipusAgrupament) {
		return historicService.getRegistresDadesUsuaris(usuarisCodi, entitatId, rolActual, filtre, tipusAgrupament);
	}

	@Override
	@RolesAllowed({"tothom", "IPA_ADMIN", "IPA_API_HIST"})
	public List<HistoricInteressatDto> getDadesInteressat(String interessatDocNum, Long entitatId, String rolActual, HistoricFiltreDto filtre) {
		return historicService.getDadesInteressat(interessatDocNum, entitatId, rolActual, filtre);
	}

	@Override
	@RolesAllowed({"tothom", "IPA_ADMIN", "IPA_API_HIST"})
	public List<RegistresInteressatDiari> getRegistresDadesInteressat(List<String> interessatsDocNum, Long entitatId, String rolActual, HistoricFiltreDto filtre, HistoricTipusEnumDto tipusAgrupament) {
		return historicService.getRegistresDadesInteressat(interessatsDocNum, entitatId, rolActual, filtre, tipusAgrupament);
	}

	@Override
	@RolesAllowed({"tothom", "IPA_ADMIN", "IPA_API_HIST"})
	public List<HistoricExpedientDto> getDadesActualsEntitat(Long entitatId, String rolActual, HistoricFiltreDto filtre) {
		return historicService.getDadesActualsEntitat(entitatId, rolActual, filtre);
	}

	@Override
	@RolesAllowed({"tothom", "IPA_ADMIN", "IPA_API_HIST"})
	public Map<OrganGestorDto, HistoricExpedientDto> getDadesActualsOrgansGestors(Long entitatId, String rolActual, HistoricFiltreDto filtre) {
		return historicService.getDadesActualsOrgansGestors(entitatId, rolActual, filtre);
	}

    @Override
	@RolesAllowed({"tothom", "IPA_ADMIN", "IPA_API_HIST"})
    public List<RegistreOrganGestor> getRegistresDadesActualsOrgansGestors(Long entitatId, String rolActual, HistoricFiltreDto filtre) {
        return historicService.getRegistresDadesActualsOrgansGestors(entitatId, rolActual, filtre);
    }

    @Override
	@RolesAllowed({"tothom", "IPA_ADMIN", "IPA_API_HIST"})
	public List<HistoricUsuariDto> getDadesActualsUsuari(String codiUsuari, Long entitatId, String rolActual, HistoricFiltreDto filtre) {
		return historicService.getDadesActualsUsuari(codiUsuari, entitatId, rolActual, filtre);
	}

	@Override
	@RolesAllowed({"tothom", "IPA_ADMIN", "IPA_API_HIST"})
	public List<HistoricInteressatDto> getDadesActualsInteressat(String codiUsuari, Long entitatId, String rolActual, HistoricFiltreDto filtre) {
		return historicService.getDadesActualsInteressat(codiUsuari, entitatId, rolActual, filtre);
	}

	@Override
	@RolesAllowed({"tothom", "IPA_ADMIN"})
	public List<Long> comprovarAccesEstadistiques(Long entitatId, String rolActual) {
		return historicService.comprovarAccesEstadistiques(entitatId, rolActual);
	}

	@Override
	@RolesAllowed({"tothom", "IPA_ADMIN", "IPA_API_HIST"})
	public FitxerDto exportarHistoricEntitat(EntitatDto entitat, String rolActual, HistoricFiltreDto filtre, String format) throws Exception {
		return historicService.exportarHistoricEntitat(entitat, rolActual, filtre, format);
	}

	@Override
	@RolesAllowed({"tothom", "IPA_ADMIN", "IPA_API_HIST"})
	public FitxerDto exportarHistoricOrgansGestors(Long entitatId, String rolActual, HistoricFiltreDto filtre, String format) throws Exception {
		return historicService.exportarHistoricOrgansGestors(entitatId, rolActual, filtre, format);
	}

	@Override
	@RolesAllowed({"tothom", "IPA_ADMIN", "IPA_API_HIST"})
	public FitxerDto exportarHistoricUsuaris(String[] usuarisCodi, Long entitatId, String rolActual, HistoricFiltreDto filtre, String format) throws Exception {
		return historicService.exportarHistoricUsuaris(usuarisCodi, entitatId, rolActual, filtre, format);
	}

	@Override
	@RolesAllowed({"tothom", "IPA_ADMIN", "IPA_API_HIST"})
	public FitxerDto exportarHistoricInteressats(String[] interessatsDocNum, Long entitatId, String rolActual, HistoricFiltreDto filtre, String format) throws Exception {
		return historicService.exportarHistoricInteressats(interessatsDocNum, entitatId, rolActual, filtre, format);
	}

}
