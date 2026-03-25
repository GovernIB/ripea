package es.caib.ripea.back.resourcecontroller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.caib.ripea.back.base.controller.BaseMutableResourceController;
import es.caib.ripea.back.helper.EntitatHelper;
import es.caib.ripea.back.helper.ExpedientHelper;
import es.caib.ripea.back.helper.FluxFirmaHelper;
import es.caib.ripea.back.helper.MetaExpedientHelper;
import es.caib.ripea.back.helper.RolHelper;
import es.caib.ripea.back.helper.SeguimentEnviamentsUsuariHelper;
import es.caib.ripea.service.intf.base.permission.UserPermissionInfo;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.config.PropertyConfig;
import es.caib.ripea.service.intf.dto.EntitatDto;
import es.caib.ripea.service.intf.dto.GrupDto;
import es.caib.ripea.service.intf.dto.OrganGestorDto;
import es.caib.ripea.service.intf.model.UsuariResource;
import es.caib.ripea.service.intf.resourceservice.UsuariResourceService;
import es.caib.ripea.service.intf.service.AplicacioService;
import es.caib.ripea.service.intf.service.EntitatService;
import es.caib.ripea.service.intf.service.EventService;
import es.caib.ripea.service.intf.service.GrupService;
import es.caib.ripea.service.intf.service.MetaExpedientService;
import es.caib.ripea.service.intf.service.OrganGestorService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * Servei REST de gestió d'usuaris.
 *
 * @author Límit Tecnologies
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(BaseConfig.API_PATH + "/usuari")
@Tag(name = "Usuaris", description = "Servei de gestió d'usuaris")
public class UsuariResourceController extends BaseMutableResourceController<UsuariResource, String> {

    @Value("${es.caib.ripea.develope.mode:false}")
    private boolean developmentMode;
    @Value("${es.caib.ripea.develope.user:rip_admin}")
    private String developmentUser;
    @Value("${es.caib.ripea.develope.roles:IPA_SUPER,IPA_ADMIN,IPA_ORGAN_ADMIN,tothom}")
    private String developmentRoles;

    private final MetaExpedientService metaExpedientService;
    private final EntitatService entitatService;
    private final OrganGestorService organGestorService;
    private final AplicacioService aplicacioService;
    private final EventService eventService;
    private final GrupService grupService;

    @Hidden
    @GetMapping("/actual/securityInfo")
    @PreAuthorize("this.isPublic() or hasPermission(null, this.getResourceClass().getName(), this.getOperation('FIND'))")
    public ResponseEntity<UserPermissionInfo> getUsuariActualSecurityInfo(HttpServletRequest request) throws MethodArgumentNotValidException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (developmentMode && (auth == null || "anonymousUser".equals(auth.getName()))) {

            // 1. Crear un Authentication personalitzat
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    developmentUser,
                    null,
                    Arrays.stream(developmentRoles.split(","))
                            .map(String::trim)
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList())
            );

            // 2. Assignar el Authentication al SecurityContext
            SecurityContext securityContext = SecurityContextHolder.getContext();
            securityContext.setAuthentication(authentication);

            // 3. Sincronitzar el SecurityContext amb la sessió HTTP
            request.getSession(true).setAttribute("SPRING_SECURITY_CONTEXT", securityContext);
        }

        if (SecurityContextHolder.getContext().getAuthentication() == null || SecurityContextHolder.getContext().getAuthentication().getName() == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        EntitatDto entitatActual = EntitatHelper.getEntitatActual(request, entitatService);
        entitatActual.setOrgansGestors(EntitatHelper.findOrganismesEntitatAmbPermisCacheByRol(request, organGestorService, entitatActual));
        String rolActual = RolHelper.getRolActual(request);
        aplicacioService.actualitzarRolThreadLocal(rolActual);
        entitatService.setConfigEntitat(entitatActual);
        OrganGestorDto organActual = EntitatHelper.getOrganGestorActual(request);
        
        List<String> roles = RolHelper.getRolsUsuariActual(request);
        List<String> rolesAuth = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .filter(role -> role.startsWith("IPA_") || role.equals("tothom"))
                .collect(Collectors.toList());

        UserPermissionInfo userPermissionInfo = ((UsuariResourceService) readonlyResourceService).getCurrentUserPermissionInfo();
        userPermissionInfo.setEntitatActualId(entitatActual != null ? entitatActual.getId() : null);
        userPermissionInfo.setOrganActualId(organActual != null ? organActual.getId() : null);
        userPermissionInfo.setRolActual(rolActual);
        userPermissionInfo.setRols(roles);
        userPermissionInfo.setAuth(rolesAuth);
        userPermissionInfo.setSessionScope(getUsuariActualAdditionalInfo(request, userPermissionInfo, organActual));
        
        return ResponseEntity.ok(userPermissionInfo);
    }

    @Hidden
    @PostMapping("/actual/changeInfo")
    @PreAuthorize("this.isPublic() or hasPermission(null, this.getResourceClass().getName(), this.getOperation('FIND'))")
    public ResponseEntity<UserPermissionInfo> postActualInfo(HttpServletRequest request, @RequestBody Map<String, Object> response) throws MethodArgumentNotValidException {

        EntitatHelper.processarCanviEntitats(request, String.valueOf(response.get("canviEntitat")), entitatService, aplicacioService);
        EntitatHelper.findOrganismesEntitatAmbPermisCache(request, organGestorService);
        EntitatHelper.processarCanviOrganGestor(request, String.valueOf(response.get("canviOrganGestor")), aplicacioService);
        EntitatHelper.findEntitatsAccessibles(request, entitatService);
        RolHelper.processarCanviRols(request, String.valueOf(response.get("canviRol")), aplicacioService, organGestorService, eventService);
        RolHelper.setRolActualFromDb(request, aplicacioService);

        return getUsuariActualSecurityInfo(request);
    }

    @Hidden
    private Map<String, Object> getUsuariActualAdditionalInfo(
    		HttpServletRequest request,
    		UserPermissionInfo userPermissionInfo,
    		OrganGestorDto organActual) {
    	
        Map<String, Object> response = new HashMap<>();

        response.put("organsNoSincronitzats", MetaExpedientHelper.getOrgansNoSincronitzats(request));
        response.put("urlsInstruccioActiu", ExpedientHelper.isUrlsInstruccioActiu(request));
        response.put("isCreacioFluxUsuariActiu", FluxFirmaHelper.isCreacioFluxUsuariActiu(request));
        response.put("teAccesEstadistiques", ExpedientHelper.teAccesEstadistiques(request));
        response.put("isMostrarSeguimentEnviamentsUsuariActiu", SeguimentEnviamentsUsuariHelper.isMostrarSeguimentEnviamentsUsuariActiu(request));
        response.put("isConvertirDefinitiuActiu", ExpedientHelper.isConversioDefinitiuActiva(request));
        response.put("isUrlValidacioDefinida", aplicacioService.propertyFindByNom(PropertyConfig.VALIDACIO_URL_IMPRIMIBLES)!=null);
        response.put("isDocumentsGeneralsEnabled", Boolean.parseBoolean(aplicacioService.propertyFindByNom(PropertyConfig.DOCUMENTS_GENERALS_ACTIUS)));
        response.put("isTipusDocumentsEnabled", Boolean.parseBoolean(aplicacioService.propertyFindByNom(PropertyConfig.TIPUS_DOCUMENT_ACTIUS)));       
        response.put("isUrlInstruccioEnabled", Boolean.parseBoolean(aplicacioService.propertyFindByNom(PropertyConfig.GENERAR_URL_INSTRUCCIO)));
        response.put("maxUploadFileSize", aplicacioService.propertyFindByNom(PropertyConfig.MAX_UPLOAD_FILE));
        response.put("isDominisEnabled", Boolean.parseBoolean(aplicacioService.propertyFindByNom(PropertyConfig.DOMINIS_HABILITATS)));
        response.put("isExportacioExcelActiva", Boolean.parseBoolean(aplicacioService.propertyFindByNom(PropertyConfig.EXPORTACIO_EXCEL)));
        response.put("isExportacioInsideActiva", Boolean.parseBoolean(aplicacioService.propertyFindByNom(PropertyConfig.EXPORTACIO_INSIDE)));
        response.put("imprimibleNoFirmats", Boolean.parseBoolean(aplicacioService.propertyFindByNom(PropertyConfig.IMPRIMIBLE_NO_FIRMAT_ACTIU)));
        response.put("isMostrarPublicar", Boolean.parseBoolean(aplicacioService.propertyFindByNom(PropertyConfig.PUBLICAR_DOCUMENTS_ACTIVA)));
        response.put("isMostrarCopiar", Boolean.parseBoolean(aplicacioService.propertyFindByNom(PropertyConfig.MOURER_DOCUMENTS_ACTIU)));
        response.put("isMostrarVincular", Boolean.parseBoolean(aplicacioService.propertyFindByNom(PropertyConfig.VINCULAR_DOCUMENTS_ACTIU)));
        response.put("isReobrirPermes", aplicacioService.propertyBooleanFindByKey(PropertyConfig.REOBRIR_EXPEDIENT_TANCAT, true));
        response.put("isTancamentLogicActiu", Boolean.parseBoolean(aplicacioService.propertyFindByNom(PropertyConfig.TANCAMENT_LOGIC)));
        response.put("isCreacioCarpetesLogica", Boolean.parseBoolean(aplicacioService.propertyFindByNom(PropertyConfig.CARPETES_LOGIQUES_ACTIVES)));
        response.put("isPermesModificarCustodiats", aplicacioService.propertyBooleanFindByKey(PropertyConfig.MODIFICAR_DOCUMENTS_CUSTODIATS, false));
        response.put("isCreacioCarpetesActiva", aplicacioService.propertyBooleanFindByKey(PropertyConfig.CARPETES_CREACIO_ACTIVA, false));
        response.put("isMostrarImportacio", Boolean.parseBoolean(aplicacioService.propertyFindByNom(PropertyConfig.IMPORTACIO_ACTIVA)));
        response.put("isIncorporacioJustificantActiva", Boolean.parseBoolean(aplicacioService.propertyFindByNom(PropertyConfig.INCORPORAR_JUSTIFICANT)));
        response.put("isImportacioRelacionatsActiva", Boolean.parseBoolean(aplicacioService.propertyFindByNom(PropertyConfig.IMPORTACIO_RELACIONATS_ACTIVA)));
        response.put("isWsUsuariEntitatActiu", Boolean.parseBoolean(aplicacioService.propertyFindByNom(PropertyConfig.PORTAFIB_PLUGIN_USUARISPF_WS)));
        response.put("ordenacioContingutPermesa", Boolean.parseBoolean(aplicacioService.propertyFindByNom(PropertyConfig.ORDENACIO_CONTINGUT_ACTIU)));
        response.put("moureMateixExpedients", Boolean.parseBoolean(aplicacioService.propertyFindByNom(PropertyConfig.MOURE_MATEIX_EXPEDIENTS)));
        response.put("permesEsborrarFinals", Boolean.parseBoolean(aplicacioService.propertyFindByNom(PropertyConfig.PERMATRE_ESBORRAR_FINAL)));
        response.put("isRevisioActiva", Boolean.parseBoolean(aplicacioService.propertyFindByNom(PropertyConfig.METAEXPEDIENT_REVISIO_ACTIVA)));
        response.put("isExpedientMoureTotActiu", Boolean.parseBoolean(aplicacioService.propertyFindByNom(PropertyConfig.MOURE_TOT)));
        response.put("isPropagarMetadades", Boolean.parseBoolean(aplicacioService.propertyFindByNom(PropertyConfig.PROPAGAR_METADADES)));
        response.put("isCarpetesDefecte", Boolean.parseBoolean(aplicacioService.propertyFindByNom(PropertyConfig.CARPETES_PER_DEFECTE)));
        response.put("isRestringirCarpetesActiu", Boolean.parseBoolean(aplicacioService.propertyFindByNom(PropertyConfig.CARPETES_RESTRINGIR_ACTIU)));

        if ("IPA_ADMIN".equals(userPermissionInfo.getRolActual()) && userPermissionInfo.getEntitatActualId()!=null) {
        	try {
        		response.put("numProcsPendentsRevisio", metaExpedientService.countMetaExpedientsPendentRevisar(userPermissionInfo.getEntitatActualId()));
        	} catch (Exception ex) {}
        }
        		
        List<GrupDto> grupsPermesos = grupService.findGrupsPermesosProcedimentsGestioActiva(
	        		userPermissionInfo.getEntitatActualId(),
	        		userPermissionInfo.getRolActual(),
	        		organActual!=null ? organActual.getId() : null);
        
        response.put("isFiltreGrupsVisible", (grupsPermesos!=null && !grupsPermesos.isEmpty()));
        
        return response;
    }

}