package es.caib.ripea.api.interna.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.caib.comanda.model.server.monitoring.DimensioDesc;
import es.caib.comanda.model.server.monitoring.EstadistiquesInfo;
import es.caib.comanda.model.server.monitoring.IndicadorDesc;
import es.caib.comanda.model.server.monitoring.RegistresEstadistics;
import es.caib.ripea.api.interna.config.BaseApiInternaSecurityConfig;
import es.caib.ripea.service.intf.config.PropertyConfig;
import es.caib.ripea.service.intf.service.AplicacioService;
import es.caib.ripea.service.intf.service.SegonPlaService;
import es.caib.ripea.service.intf.utils.DateUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping(BaseApiInternaSecurityConfig.VERSIO_API_COMANDA+"/estadistiques")
@Tag(name = "Integració comanda - RIPEA", description = "Publicació de dades estadístiques de l'aplicació")
public class EstadistiquesController extends BaseApiInternaController {

	private final SegonPlaService segonPlaService;
	private final AplicacioService aplicacioService;
	
    @GetMapping("/info")
	@Operation(
			summary = "Consulta de paràmetres estadístics de l'aplicació (indicadors i dimensions disponibles).",
			security = { @SecurityRequirement(name = "basicAuth") })
    public EstadistiquesInfo statsInfo() throws IOException {
        List<DimensioDesc> dimensions  = segonPlaService.getDimensionsInfo();
        List<IndicadorDesc> indicadors = segonPlaService.getIndicadorsInfo();
        return new EstadistiquesInfo()
        		.codi(aplicacioService.propertyFindByNom(PropertyConfig.COMANDA_APP_CODI))
        		.data(DateUtil.toOffsetDateTime(Calendar.getInstance().getTime()))
        		.versio(getManifestInfo().getVersion())
        		.dimensions(dimensions)
        		.indicadors(indicadors);
    }
	
    @GetMapping
	@Operation(
			summary = "Consulta d'estadístiques del dia anterior.",
			security = { @SecurityRequirement(name = "basicAuth") })
    public RegistresEstadistics estadistiques(HttpServletRequest request) throws Exception {
        LocalDate ayer = LocalDate.now().minusDays(1);
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    	return estadistiques(request, ayer.format(formato));
    }
    
    @GetMapping("/of/{data}")
	@Operation(
			summary = "Consulta d'estadístiques d'una data determinada.",
			security = { @SecurityRequirement(name = "basicAuth") })
    public RegistresEstadistics estadistiques(HttpServletRequest request, @PathVariable String data) throws Exception {
        LocalDate date = LocalDate.parse(data, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        if (!segonPlaService.existeixenEstadistiques(date)) {
    		Date dateJava = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
    		segonPlaService.generarEstadistiquesDiaries(dateJava);
        }
        return segonPlaService.consultaEstadistiques(date);
    }

    @GetMapping("/from/{dataInici}/to/{dataFi}")
	@Operation(
			summary = "Consulta d'estadístiques entre dues dates determinades.",
			security = { @SecurityRequirement(name = "basicAuth") })
    public List<RegistresEstadistics> estadistiques(HttpServletRequest request, @PathVariable String dataInici, @PathVariable String dataFi) throws Exception {
        List<RegistresEstadistics> result = new ArrayList<>();
        LocalDate dataFrom = LocalDate.parse(dataInici, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        LocalDate dataTo = LocalDate.parse(dataFi, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        LocalDate startDate = dataFrom.isBefore(dataTo) ? dataFrom : dataTo;
        LocalDate endDate = dataFrom.isBefore(dataTo) ? dataTo : dataFrom;
        LocalDate ahir = LocalDate.now().minusDays(1);
        if (endDate.isAfter(ahir)) {
            endDate = ahir;
        }
	    LocalDate fechaLoop = startDate;
	    while (!fechaLoop.isAfter(endDate)) {
	    	if (!segonPlaService.existeixenEstadistiques(fechaLoop)) {
	    		Date date = Date.from(fechaLoop.atStartOfDay(ZoneId.systemDefault()).toInstant());
	    		segonPlaService.generarEstadistiquesDiaries(date);
	    	}
	    	result.add(segonPlaService.consultaEstadistiques(fechaLoop));
	        fechaLoop = fechaLoop.plusDays(1);
	    }
		return result;
    }
    
	private ManifestInfo manifestInfo;
	protected ManifestInfo getManifestInfo() throws IOException {
        if (manifestInfo == null) {
            manifestInfo = buildManifestInfo();
        }
        return manifestInfo;
    }
}