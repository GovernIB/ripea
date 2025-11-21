package es.caib.ripea.api.interna.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import es.caib.comanda.ms.estadistica.model.DimensioDesc;
import es.caib.comanda.ms.estadistica.model.EstadistiquesInfo;
import es.caib.comanda.ms.estadistica.model.IndicadorDesc;
import es.caib.comanda.ms.estadistica.model.RegistresEstadistics;
import es.caib.ripea.service.intf.service.SegonPlaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@Tag(name = "Integració comanda - RIPEA", description = "Publicació de dades estadístiques de l'aplicació")
public class EstadistiquesController extends BaseApiInternaController {

	private final SegonPlaService segonPlaService;
	private ManifestInfo manifestInfo;
	
	protected ManifestInfo getManifestInfo() throws IOException {
        if (manifestInfo == null) {
            manifestInfo = buildManifestInfo();
        }
        return manifestInfo;
    }
	
    @GetMapping("/estadistiquesInfo")
    public EstadistiquesInfo statsInfo() throws IOException {
    	autenticaAmbRolTothom();
        List<DimensioDesc> dimensions  = segonPlaService.getDimensionsInfo();
        List<IndicadorDesc> indicadors = segonPlaService.getIndicadorsInfo();
        return EstadistiquesInfo.builder()
        		.codi("RIP")
        		.data(Calendar.getInstance().getTime())
        		.versio(getManifestInfo().getVersion())
        		.dimensions(dimensions)
        		.indicadors(indicadors).build();
    }
	
    @GetMapping("/estadistiques")
    public RegistresEstadistics estadistiques(HttpServletRequest request) throws Exception {
        LocalDate ayer = LocalDate.now().minusDays(1);
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    	return estadistiques(request, ayer.format(formato));
    }
    
    @GetMapping("/estadistiques/of/{data}")
    public RegistresEstadistics estadistiques(HttpServletRequest request, @PathVariable String data) throws Exception {
    	autenticaAmbRolTothom();
        LocalDate date = LocalDate.parse(data, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        if (!segonPlaService.existeixenEstadistiques(date)) {
    		Date dateJava = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
    		segonPlaService.generarEstadistiquesDiaries(dateJava);
        }
        return segonPlaService.consultaEstadistiques(date);
    }

    @GetMapping("/estadistiques/from/{dataInici}/to/{dataFi}")
    public List<RegistresEstadistics> estadistiques(HttpServletRequest request, @PathVariable String dataInici, @PathVariable String dataFi) throws Exception {
    	autenticaAmbRolTothom();
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
    
    private void autenticaAmbRolTothom() {
        User user = new User("$comanda_ripea", "comanda_ripea", Collections.singletonList(new SimpleGrantedAuthority("tothom")));
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
