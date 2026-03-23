package es.caib.ripea.api.interna.controller;

import java.io.IOException;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import es.caib.comanda.model.server.monitoring.AppInfo;
import es.caib.comanda.model.server.monitoring.SalutInfo;
import es.caib.comanda.ms.salut.helper.MonitorHelper;
import es.caib.ripea.api.interna.config.BaseApiInternaSecurityConfig;
import es.caib.ripea.service.intf.config.PropertyConfig;
import es.caib.ripea.service.intf.service.AplicacioService;
import es.caib.ripea.service.intf.service.SalutService;
import es.caib.ripea.service.intf.utils.DateUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping(BaseApiInternaSecurityConfig.VERSIO_API_COMANDA+"/salut")
@Tag(name = "Integració comanda - RIPEA", description = "Publicació de dades de salut i informació de l'aplicació")
public class SalutController extends BaseApiInternaController {

	private final SalutService salutService;
	private final AplicacioService aplicacioService;
	private ManifestInfo manifestInfo;
	
	protected ManifestInfo getManifestInfo() throws IOException {
        if (manifestInfo == null) {
            manifestInfo = buildManifestInfo();
        }
        return manifestInfo;
    }
	
    @GetMapping("/info")
    public AppInfo appInfo(HttpServletRequest request) throws IOException {
        var manifestInfo = getManifestInfo();
        return new AppInfo()
                .codi(aplicacioService.propertyFindByNom(PropertyConfig.COMANDA_APP_CODI))
                .nom("RIPEA")
                .data(DateUtil.toOffsetDateTime(manifestInfo.getBuildDate()))
                .versio(manifestInfo.getVersion())
                .revisio(manifestInfo.getBuildScmRevision())
                .jdkVersion(manifestInfo.getBuildJDK())
                .versioJboss(MonitorHelper.getApplicationServerInfo())
                .integracions(salutService.getIntegracions())
                .subsistemes(salutService.getSubsistemes())
                .contexts(salutService.getContexts(getBaseUrl(request)))
                .versioJboss(MonitorHelper.getApplicationServerInfo());
    }
    
    @GetMapping
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