package es.caib.ripea.api.interna.controller;

import java.io.IOException;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import es.caib.comanda.ms.salut.model.AppInfo;
import es.caib.comanda.ms.salut.model.SalutInfo;
import es.caib.ripea.service.intf.service.SalutService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@Tag(name = "Integració comanda - RIPEA", description = "Publicació de dades de salut i informació de l'aplicació")
public class SalutController extends BaseApiInternaController {

	private final SalutService salutService;
	private ManifestInfo manifestInfo;
	
	protected ManifestInfo getManifestInfo() throws IOException {
        if (manifestInfo == null) {
            manifestInfo = buildManifestInfo();
        }
        return manifestInfo;
    }
	
    @GetMapping("/appInfo")
    public AppInfo appInfo(HttpServletRequest request) throws IOException {
    	autenticaAmbRolTothom();
        var manifestInfo = getManifestInfo();
        return AppInfo.builder()
                .codi("RIP")
                .nom("RIPEA")
                .data(manifestInfo.getBuildDate())
                .versio(manifestInfo.getVersion())
                .revisio(manifestInfo.getBuildScmRevision())
                .jdkVersion(manifestInfo.getBuildJDK())
                .integracions(salutService.getIntegracions())
                .subsistemes(salutService.getSubsistemes())
                .contexts(salutService.getContexts(getBaseUrl(request)))
                .build();
    }
    
    @GetMapping("/salut")
    public SalutInfo health(HttpServletRequest request) throws IOException {
    	autenticaAmbRolTothom();
        var manifestInfo = getManifestInfo();
        return salutService.checkSalut(manifestInfo.getVersion(), request.getRequestURL().toString() + "Performance");
    }
    
    // ---------------------------------------------------- //

    private void autenticaAmbRolTothom() {
        User user = new User("$comanda_ripea", "comanda_ripea", Collections.singletonList(new SimpleGrantedAuthority("tothom")));
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
    
    public String getBaseUrl(HttpServletRequest request) {
        return ServletUriComponentsBuilder
                .fromRequestUri(request)
                .replacePath(null) // elimina el context path "/comandaapi/..."
                .build()
                .toUriString();
    }
}