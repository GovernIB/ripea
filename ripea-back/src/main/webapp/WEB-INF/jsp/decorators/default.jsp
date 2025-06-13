<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip" %>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator"%>

<%
	pageContext.setAttribute("sessionEntitats", es.caib.ripea.back.helper.EntitatHelper.findEntitatsAccessibles(request));
	pageContext.setAttribute("entitatActual", es.caib.ripea.back.helper.EntitatHelper.getEntitatActual(request));
	pageContext.setAttribute("isRevisioActiva", es.caib.ripea.back.helper.MetaExpedientHelper.getRevisioActiva(request));
	pageContext.setAttribute("sessionOrgansGestors", es.caib.ripea.back.helper.EntitatHelper.findOrganGestorsAccessibles(request));
	pageContext.setAttribute("organGestorActual", es.caib.ripea.back.helper.EntitatHelper.getOrganGestorActual(request));
	pageContext.setAttribute("requestParameterCanviOrganGestor", es.caib.ripea.back.helper.EntitatHelper.getRequestParameterCanviOrganGestor());
	pageContext.setAttribute("requestParameterCanviEntitat", es.caib.ripea.back.helper.EntitatHelper.getRequestParameterCanviEntitat());
	pageContext.setAttribute("dadesUsuariActual", es.caib.ripea.back.helper.SessioHelper.getUsuariActual(request));
	pageContext.setAttribute("rolActual", es.caib.ripea.back.helper.RolHelper.getRolActual(request));
	pageContext.setAttribute("rolsUsuariActual", es.caib.ripea.back.helper.RolHelper.getRolsUsuariActual(request));
	pageContext.setAttribute("isRolActualSuperusuari", es.caib.ripea.back.helper.RolHelper.isRolActualSuperusuari(request));
	pageContext.setAttribute("isRolActualAdministrador", es.caib.ripea.back.helper.RolHelper.isRolActualAdministrador(request), PageContext.SESSION_SCOPE);
	pageContext.setAttribute("isRolActualAdministradorOrgan", es.caib.ripea.back.helper.RolHelper.isRolActualAdministradorOrgan(request));
	pageContext.setAttribute("isRolActualDissenyadorOrgan", es.caib.ripea.back.helper.RolHelper.isRolActualDissenyadorOrgan(request));
	pageContext.setAttribute("isRolActualRevisor", es.caib.ripea.back.helper.RolHelper.isRolActualRevisor(request));
	pageContext.setAttribute("isRolActualUsuari", es.caib.ripea.back.helper.RolHelper.isRolActualUsuari(request));
	pageContext.setAttribute("requestParameterCanviRol", es.caib.ripea.back.helper.RolHelper.getRequestParameterCanviRol());
	pageContext.setAttribute("teAccesExpedients", es.caib.ripea.back.helper.ExpedientHelper.teAccesExpedients(request));
	pageContext.setAttribute("countTasquesPendent", es.caib.ripea.back.helper.TasquesPendentsHelper.countTasquesPendents(request));
	pageContext.setAttribute("countAnotacionsPendents", es.caib.ripea.back.helper.AnotacionsPendentsHelper.countAnotacionsPendents(request));
	pageContext.setAttribute("teAccesEstadistiques", es.caib.ripea.back.helper.ExpedientHelper.teAccesEstadistiques(request));
	pageContext.setAttribute("avisos", es.caib.ripea.back.helper.AvisHelper.getAvisos(request));
	pageContext.setAttribute("isConvertirDefinitiuActiu", es.caib.ripea.back.helper.ExpedientHelper.isConversioDefinitiuActiva(request));
	pageContext.setAttribute("isUrlValidacioDefinida", es.caib.ripea.back.helper.ExpedientHelper.isUrlValidacioDefinida(request));
	pageContext.setAttribute("organsNoSincronitzats", es.caib.ripea.back.helper.MetaExpedientHelper.getOrgansNoSincronitzats(request));
	pageContext.setAttribute("isUrlsInstruccioActiu", es.caib.ripea.back.helper.ExpedientHelper.isUrlsInstruccioActiu(request));
	pageContext.setAttribute("isCreacioFluxUsuariActiu", es.caib.ripea.back.helper.FluxFirmaHelper.isCreacioFluxUsuariActiu(request));
	pageContext.setAttribute("isMostrarSeguimentEnviamentsUsuariActiu", es.caib.ripea.back.helper.SeguimentEnviamentsUsuariHelper.isMostrarSeguimentEnviamentsUsuariActiu(request));
%>
<c:set var="hiHaEntitats" value="${fn:length(sessionEntitats) > 0}"/>
<c:set var="hiHaMesEntitats" value="${fn:length(sessionEntitats) > 1}"/>
<c:set var="hiHaOrgansGestors" value="${fn:length(sessionOrgansGestors) > 0}"/>
<c:set var="hiHaMesOrgansGestors" value="${fn:length(sessionOrgansGestors) > 1}"/>
<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="utf-8">
	<title>Ripea - <decorator:title default="Benvinguts" /></title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0"/>
	<meta name="description" content=""/>
	<meta name="author" content=""/>
	<!-- Estils CSS -->
	<link href="<c:url value="/webjars/bootstrap/3.3.6/dist/css/bootstrap.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/font-awesome/4.7.0/css/font-awesome.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/estils.css"/>" rel="stylesheet">
	<link rel="shortcut icon" href="<c:url value="/img/favicon.png"/>" type="image/x-icon" />
	<script src="<c:url value="/webjars/jquery/1.12.4/dist/jquery.min.js"/>"></script>
	<!-- Llibreria per a compatibilitat amb HTML5 -->
	<!--[if lt IE 9]>
		<script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
	<![endif]-->
	<script src="<c:url value="/webjars/bootstrap/3.3.6/dist/js/bootstrap.min.js"/>"></script>
	<script src="<c:url value="/webjars/jquery-ui/1.12.1/jquery-ui.min.js"/>"></script>
	<link href="<c:url value="/webjars/jquery-ui/1.12.1/jquery-ui.css"/>" rel="stylesheet"></link>
	
	<script src="<c:url value="/js/toastr.min.js"/>"></script>
	<link href="<c:url value="/css/toastr.min.css"/>" rel="stylesheet"></link>
	
	<script>
		var requestLocale = '${requestLocale}';
		var contextAddress = '${pageContext.request.contextPath}';

		$(window).bind("pageshow", function(event) {
		    $('body').removeClass('loading');
		});

		$(document).ready(function() {
			$('button[value="filtrar"]').click(function() {
				$('table').dataTable().api().state.clear();
			});
			$('button[value="netejar"]').click(function() {
				$('table').dataTable().api().state.clear();
			});
			$('table').data("page-length", ${dadesUsuariActual.numElementsPagina});
			
			$('.circular-avatar').each(function (index) {
				this.style.backgroundColor=stringToColor('${dadesUsuariActual.nom}');
				this.innerHTML=obtenerIniciales('${dadesUsuariActual.nom}');
			});

			$("a.capMenuDesplegable").click(function() {
				let flecha = $(this).prev("span");
				let menu = $(this).next(".ul_capMenuDesplegable")[0];
			    if (menu.style.display === "block") {
			        menu.style.display = "none";
			        flecha.removeClass("fa-caret-down");
			        flecha.addClass("fa-caret-right");
			    } else {
			        menu.style.display = "block";
			        flecha.removeClass("fa-caret-right");
			        flecha.addClass("fa-caret-down");
			    }
			});
		});
		
		function mostrarMenuUsuari(event) {
			debugger;
		    let popup = document.getElementById("popupMenuUsuari");
		    if (popup.style.display === "none") {
			    // Obtener coordenadas del clic
			    let x = event.clientX; // Posición horizontal del clic
			    let y = event.clientY; // Posición vertical del clic
	
			    // Posicionar el div 200px a la izquierda y hacia abajo
			    popup.style.left = (x-250) + "px";
			    popup.style.top = "70px"; // Ajusta este valor según necesites
			    popup.style.display = "block"; // Mostrar el div
		    } else {
		    	popup.style.display = "none";
			}
		}
		
		document.addEventListener("click", function(event) {
			if (event.srcElement.className!="circular-avatar") {
			    let popup = document.getElementById("popupMenuUsuari");
			    // Verificar si el clic NO fue dentro del popup
			    if (!popup.contains(event.target)) {
			        popup.style.display = "none"; // Ocultar el div
			    }
			}
		});
		
		function stringToColor(str) {
		    let hash = 0;
		    for (let i = 0; i < str.length; i++) {
		        hash = str.charCodeAt(i) + ((hash << 5) - hash);
		    }
		    let color = '#';
		    for (let i = 0; i < 3; i++) {
		        let value = (hash >> (i * 8)) & 0xff;
		        let hex = value.toString(16);
		        // Asegurar que siempre tenga 2 caracteres (añadir '0' si es necesario)
		        if (hex.length === 1) {
		            hex = '0' + hex;
		        }
		        color += hex;
		    }
		    return color;
		}
		
		function obtenerIniciales(nombre) {
		    let palabras = nombre.trim().split(/\s+/); // Divide por espacios
		    let iniciales = '';
		    if (palabras.length > 1) {
		        // Tomar la primera letra de cada palabra, con un máximo de 2 palabras
		        iniciales = palabras.slice(0, 2).map(palabra => palabra.charAt(0)).join('');
		    } else {
		        // Si hay una sola palabra, tomar las dos primeras letras
		        iniciales = palabras[0].substring(0, 2);
		    }
		    return iniciales.toUpperCase();
		}
		
		function toggleMenuPrincipal() {
		    let menu = document.getElementById("menuPrincipal");
		    let overlay = document.getElementById("overlayMenu");
		    if (menu.style.display === "block") {
		        menu.style.display = "none";
		        overlay.style.display = "none";
		    } else {
		        menu.style.display = "block";
		        overlay.style.display = "block";
		    }
		}
		
	</script>
	<decorator:head />
<style>
body {
	background-image:url(<c:url value="/img/background-pattern.png"/>);
	color:#666666;
	padding-top: 100px;
}
.select2-container--bootstrap .select2-results__option { min-height: 30px; }
<%-- If capsaleraColorFons is defined for entitat use it, if not look if there is capsaleraColorFons defined for application and use it, if not default color is used 	--%>
<c:if test="${not isRolActualSuperusuari}">
	<c:choose>
		<c:when test="${sessionScope['EntitatHelper.entitatActual'].capsaleraColorFons!=null  && not empty sessionScope['EntitatHelper.entitatActual'].capsaleraColorFons}">
			.navbar-app {
				background-color: ${sessionScope['EntitatHelper.entitatActual'].capsaleraColorFons};
			}
		</c:when>
		<c:otherwise>
			<c:if test="${sessionScope['SessionHelper.capsaleraColorFons']!=null  && not empty sessionScope['SessionHelper.capsaleraColorFons']}">
				.navbar-app {
					background-color: ${sessionScope['SessionHelper.capsaleraColorFons']};
				}
			</c:if>
		</c:otherwise>
	</c:choose>
	<%-- If capsaleraColorLletra is defined for entitat use it, if not look if there is capsaleraColorLletra defined for application and use it, if not default color is used 	--%>
	<c:choose>
		<c:when test="${sessionScope['EntitatHelper.entitatActual'].capsaleraColorLletra!=null  && not empty sessionScope['EntitatHelper.entitatActual'].capsaleraColorLletra}">
			.navbar-app .list-inline li.dropdown>a {
				color: ${sessionScope['EntitatHelper.entitatActual'].capsaleraColorLletra};
			}
		</c:when>
		<c:otherwise>
			<c:if test="${sessionScope['SessionHelper.capsaleraColorLletra']!=null  && not empty sessionScope['SessionHelper.capsaleraColorLletra']}">
				.navbar-app .list-inline li.dropdown>a {
					color: ${sessionScope['SessionHelper.capsaleraColorLletra']};
				}
			</c:if>
		</c:otherwise>
	</c:choose>
</c:if>
</style>
</head>
<body>
	<div id="popupMenuUsuari" class="popupMenuUsuari">
		<div style="display: flex;">
			<div class="circular-avatar"></div>
			<div>
				<c:choose>
					<c:when test="${not empty dadesUsuariActual}">${dadesUsuariActual.nom} ${dadesUsuariActual.codi}</c:when>
					<c:otherwise>${pageContext.request.userPrincipal.name}</c:otherwise>
				</c:choose>
			</div>
		</div>
		<div>Perfil i manuals</div>
		<div>Selectors</div>
		<div>Tancar sessio</div>
	</div>
	<div id="overlayMenu" class="overlayMenu" onclick="toggleMenuPrincipal()"></div>
	<div id="menuPrincipal" class="menuPrincipal">
		<div class="menuPrincipalCapcalera">
			<div style="width: 20%;padding-left: 11px;"><img src="<c:url value="/img/goib_escut_logo.png"/>" height="40"/></div>
			<div style="width: 70%;padding-top: 8px;">Menú</div>
			<div style="width: 10%; cursor:pointer;font-weight:lighter;padding-top:5px;" onclick="toggleMenuPrincipal();"><span class="fa fa-times"></span></div>
		</div>
		<div class="menuPrincipalCos">
			<c:choose>
				<c:when test="${isRolActualSuperusuari}">
					
					<%---- Entitats ----%>
					<a href="<c:url value="/entitat"/>" ><spring:message code="decorator.menu.entitats"/></a>
					<div class="btn-group">
						<a data-toggle="dropdown" class="dropdown-toggle"><spring:message code="decorator.menu.monitoritzar"/>&nbsp;<span class="caret caret-white"></span></a>
						<ul class="dropdown-menu">
							<%---- Integracions ----%>
							<li><a href="<c:url value="/integracio"/>"><spring:message code="decorator.menu.integracions"/></a></li>
							<%---- Excepcions ----%>
							<li><a href="<c:url value="/excepcio"/>"><spring:message code="decorator.menu.excepcions"/></a></li>				
							<li><a href="<c:url value="/monitor"/>" data-toggle="modal" data-maximized="true"><spring:message code="decorator.menu.monitor"/></a></li>											
						</ul>
					</div>
					<div class="btn-group">
						<a data-toggle="dropdown" class="dropdown-toggle"><spring:message code="decorator.menu.config"/>&nbsp;<span class="caret caret-white"></span></a>
						<ul class="dropdown-menu">
							<li><a href="<c:url value="/config"/>" title="<spring:message code="decorator.menu.config.properties"/>"><spring:message code="decorator.menu.config.properties"/></a></li>
							<li><a href="<c:url value="/pinbalServei"/>"><spring:message code="decorator.menu.pinbal.servei"/></a></li>
							<li><a href="<c:url value="/scheduled"/>" data-toggle="modal" data-maximized="true"><spring:message code="decorator.menu.reinici.scheduler"/> ...</a></li>
							<li><a href="<c:url value="/plugin"/>" data-toggle="modal"><spring:message code="decorator.menu.reinici.plugin"/> ...</a></li>
						</ul>
					</div>
					
					<a href="<c:url value="/avis"/>"><spring:message code="decorator.menu.avisos"/></a>
				</c:when>
				<c:when test="${isRolActualAdministrador}">
					<%---- Expedients ----%>
					<ul>
					<li><span class="fa fa-folder-o"></span><a href="<c:url value="/expedient"></c:url>"><spring:message code="decorator.menu.expedients"/></a></li>							
					<%---- Annotacions pendents ----%>
					<li><span class="fa fa-envelope"></span><a href="<c:url value="/expedientPeticio"></c:url>"><spring:message code="decorator.menu.expedientPeticions"/><span id="anotacio-pendent-count" class="badge small">${countAnotacionsPendents}</span></a></li>
					<li style="display: inline-table;width: 100%;">
						<span class="fa fa-caret-right"></span>
						<a href="#" class="capMenuDesplegable"><spring:message code="decorator.menu.configurar"/></a>
						<ul class="ul_capMenuDesplegable" style="display:none;">
							<li><a href="<c:url value="/metaExpedient"/>"><spring:message code="decorator.menu.metaexpedients"/><c:if test="${organsNoSincronitzats > 0}"><span class="badge small" title="<spring:message code='metaexpedient.actualitzacio.organs.no.sync'/>" style="background-color: #a94442; float: right;">${organsNoSincronitzats}</span></c:if></a></li>
							<c:if test="${sessionScope['SessionHelper.isDocumentsGeneralsEnabled']!=null  && sessionScope['SessionHelper.isDocumentsGeneralsEnabled']}">
								<li><a href="<c:url value="/metaDocument"/>"><spring:message code="decorator.menu.metadocuments"/></a></li>
							</c:if>
							<c:if test="${sessionScope['SessionHelper.isTipusDocumentsEnabled']!=null  && sessionScope['SessionHelper.isTipusDocumentsEnabled']}">
								<li><a href="<c:url value="/tipusDocumental"/>"><spring:message code="decorator.menu.tipusdocumental"/></a></li>
							</c:if>
							<c:if test="${sessionScope['SessionHelper.isDominisEnabled']!=null  && sessionScope['SessionHelper.isDominisEnabled']}">
								<li><a href="<c:url value="/domini"/>"><spring:message code="decorator.menu.domini"/></a></li>
							</c:if>								
							<li><a href="<c:url value="/grup"/>"><spring:message code="decorator.menu.grups"/></a></li>
							<li><a href="<c:url value="/organgestor"/>"><spring:message code="decorator.menu.organgestor"/></a></li>
							<c:if test="${isUrlsInstruccioActiu}">
								<li><a href="<c:url value="/urlInstruccio"/>"><spring:message code="decorator.menu.urlinstruccio"/></a></li>
							</c:if>
							<li><a href="<c:url value="/permis"/>"><spring:message code="decorator.menu.permisos.entitat"/></a></li>
						</ul>
					</li>
					<li style="display: inline-table;width: 100%;">
						<span class="fa fa-caret-right"></span>
						<a href="#" class="capMenuDesplegable"><spring:message code="decorator.menu.consultar"/></a>
						<ul class="ul_capMenuDesplegable" style="display:none;">
							<li><a href="<c:url value="/contingutAdmin"/>"><spring:message code="decorator.menu.continguts"/></a></li>
							<li>
								<a href="<c:url value="/historic"/>" data-maximized="true">
									<spring:message code="decorator.menu.accions.estadistiques"/>
								</a>
							</li>
							<c:if test="${isRevisioActiva}">
								<li>
									<a href="<c:url value="/metaExpedientRevisio"/>">
											<spring:message code="decorator.menu.revisioProcediments"/>
									</a>
								</li>	
							</c:if>	
							<li>
								<a href="<c:url value="/seguimentPortafirmes"/>" data-maximized="true">
									<spring:message code="decorator.menu.seguiment.documentsEnviatsPortafib"/>
								</a>
							</li>
							<li>
								<a href="<c:url value="/seguimentNotificacions"/>" data-maximized="true">
									<spring:message code="decorator.menu.seguiment.notificacionsEnviatsNotib"/>
								</a>
							</li>
							<li>
								<a href="<c:url value="/seguimentPinbal"/>" data-maximized="true">
									<spring:message code="decorator.menu.seguiment.consultesPinbal"/>
								</a>
							</li>								
							<li>
								<a href="<c:url value="/seguimentTasques"/>" data-maximized="true">
									<spring:message code="decorator.menu.seguiment.assignacioTasques"/>
								</a>
							</li>		
							<li>
								<a href="<c:url value="/seguimentExpedientsPendents"/>" data-maximized="true">
									<spring:message code="decorator.menu.seguiment.expedientsPendents"/>
								</a>
							</li>															
							<li>
								<a href="<c:url value="/expedientPeticioComunicades"></c:url>">
									<spring:message code="decorator.menu.anotacionsComunicades"/>
								</a>
							</li>	
						</ul>
					</li>
				</ul>
				</c:when>
				<c:when test="${isRolActualAdministradorOrgan}">
					<ul>
					<li><span class="fa fa-folder-o"></span><a href="<c:url value="/expedient"></c:url>"><spring:message code="decorator.menu.expedients"/></a></li>
					<li>
						<span class="fa fa-envelope"></span>
						<a href="<c:url value="/expedientPeticio"></c:url>">
						<spring:message code="decorator.menu.expedientPeticions"/>
						<span id="anotacio-pendent-count" class="badge small">${countAnotacionsPendents}</span>
					</a></li>
					<li style="display: inline-table;width: 100%;">
						<span class="fa fa-caret-right"></span>
						<a href="#" class="capMenuDesplegable"><spring:message code="decorator.menu.configurar"/></a>
						<ul class="ul_capMenuDesplegable" style="display:none;">
							<li><a href="<c:url value="/metaExpedient"/>"><spring:message code="decorator.menu.metaexpedients"/><c:if test="${organsNoSincronitzats > 0}"><span class="badge small" title="<spring:message code='metaexpedient.actualitzacio.organs.no.sync'/>" style="background-color: #a94442; float: right;">${organsNoSincronitzats}</span></c:if></a></li>
							<li><a href="<c:url value="/grup"/>"><spring:message code="decorator.menu.grups"/></a></li>
						</ul>
					</li>
					</ul>
				</c:when>
				<c:when test="${isRolActualDissenyadorOrgan}">
				<ul>
					<li><a href="<c:url value="/metaExpedient"></c:url>"><spring:message code="decorator.menu.metaexpedients"/></a></li>
					<li><a href="<c:url value="/grup"></c:url>"><spring:message code="decorator.menu.grups"/></a></li>
				</ul>
				</c:when>
				<c:when test="${isRolActualUsuari}">
				<ul>
					<li>
						<span class="fa fa-folder-o">
						</span><a href="<c:url value="/expedient"></c:url>"><spring:message code="decorator.menu.expedients"/></a>
					</li>
					<li>
						<span class="fa fa-envelope"></span>
						<a href="<c:url value="/expedientPeticio"></c:url>"><spring:message code="decorator.menu.expedientPeticions"/> <span id="anotacio-pendent-count" class="badge small">${countAnotacionsPendents}</span></a>
					</li>
					<li>
						<span class="fa fa-pencil-square-o"></span>
						<a href="<c:url value="/usuariTasca"></c:url>"><spring:message code="decorator.menu.tasques"/> <span id="tasca-pendent-count" class="badge small">${countTasquesPendent}</span></a>
					</li>
					<c:if test="${isCreacioFluxUsuariActiu}">
						<li><a href="<c:url value="/fluxusuari"></c:url>"><spring:message code="decorator.menu.fluxosusuari"/></a></li>
					</c:if>
					<c:if test="${teAccesEstadistiques or isMostrarSeguimentEnviamentsUsuariActiu}">
						<li style="display: inline-table;width: 100%;">
							<span class="fa fa-caret-right"></span>
							<a href="#" class="capMenuDesplegable"><spring:message code="decorator.menu.consultar"/></a>
							<ul class="ul_capMenuDesplegable" style="display:none;">
								<c:if test="${teAccesEstadistiques}">
									<li><a href="<c:url value="/historic"/>" data-maximized="true"><spring:message code="decorator.menu.accions.estadistiques"/></a></li>
								</c:if>
								<c:if test="${isMostrarSeguimentEnviamentsUsuariActiu}">
									<li><a href="<c:url value="/seguimentPortafirmes"/>" data-maximized="true"><spring:message code="decorator.menu.seguiment.documentsEnviatsPortafib"/></a></li>
									<li><a href="<c:url value="/seguimentNotificacions"/>" data-maximized="true"><spring:message code="decorator.menu.seguiment.notificacionsEnviatsNotib"/></a></li>
								</c:if>
							</ul>
						</li>
					</c:if>
				</ul>
				</c:when>
			</c:choose>
			<c:if test="${isRolActualAdministrador or isRolActualAdministradorOrgan or isRolActualUsuari}">
				<ul>
					<li style="display: inline-table;width: 100%;">
						<span class="fa fa-caret-right"></span>
						<a href="#" class="capMenuDesplegable"><spring:message code="massiu.portafirmes"/></a>
						<ul class="ul_capMenuDesplegable" style="display:none;">

							<li><a href="<c:url value="/massiu/portafirmes"/>"><spring:message code="massiu.portafirmes.firma"/></a></li>
							<li><a href="<c:url value="/massiu/firmasimpleweb"/>"><spring:message code="massiu.firmasimpleweb"/></a></li>
							<c:if test="${isConvertirDefinitiuActiu}">
								<li><a href="<c:url value="/massiu/definitiu"/>"><spring:message code="massiu.estat.definitiu"/></a></li>
							</c:if>
							<li><a href="<c:url value="/massiu/canviEstat"/>"><spring:message code="massiu.canviEstat"/></a></li>
							<li><a href="<c:url value="/massiu/tancament"/>"><spring:message code="massiu.tancament"/></a></li>
							<li><a href="<c:url value="/seguimentArxiuPendents"/>"><spring:message code="decorator.menu.pendents.arxiu"/></a></li>
							<c:if test="${isUrlValidacioDefinida}">
								<li><a href="<c:url value="/massiu/csv"/>"><spring:message code="massiu.documents.csv"/></a></li>
							</c:if>
							<li><a href="<c:url value="/massiu/procesarAnnexosPendents"/>"><spring:message code="massiu.procesar.annexos.pendents"/></a></li>
							<c:if test="${isRolActualAdministrador}">
								<li><a href="<c:url value="/massiu/expedientPeticioCanviEstatDistribucio"/>"><spring:message code="massiu.anotacions.pendents.canvi.estat"/></a></li>
							</c:if>
							<li><a href="<c:url value="/massiu/canviPrioritats"/>"><spring:message code="expedient.massiu.prioritat.modificar.titol"/></a></li>
							<li><a href="<c:url value="/massiu/consulta/0"/>" data-toggle="modal" data-maximized="true">
									<spring:message code="decorator.menu.accions.massives.user"/>
							</a>
							</li>												
						</ul>
					</li>
				</ul>
			</c:if>
			<c:if test="${isRolActualRevisor}">
				<ul><li><a href="<c:url value="/metaExpedientRevisio"/>"><spring:message code="decorator.menu.revisioProcediments"/></a></li></ul>
			</c:if>
		</div>
	</div>
	<div class="navbar navbar-default navbar-fixed-top navbar-app" role="navigation">
		<div class="container container-caib">

			<div class="navbar-header" style="width: 25%;">
				<div class="navbar-collapse collapse">
					<div class="navbar-brand" style="padding: 0px !important;width: 100%;">
						<div id="govern-logo" class="pull-left">
							<%-- If logo is defined for application in properties file or for entitat in db then take the logo from there, in other case take default logo from the img folder --%>					
							<c:choose>
								<c:when test="${not isRolActualSuperusuari && (sessionScope['SessionHelper.capsaleraLogo']!=null  && not empty sessionScope['SessionHelper.capsaleraLogo'] || sessionScope['EntitatHelper.entitatActual'].logoImgBytes!=null && fn:length(sessionScope['EntitatHelper.entitatActual'].logoImgBytes)!=0)}">
									<img src="<c:url value="/entitat/getEntitatLogo"/>"  height="60" alt="Govern de les Illes Balears" />
								</c:when>
								<c:otherwise>
									<img src="<c:url value="/img/goib_logo.svg"/>"  height="60" alt="Govern de les Illes Balears" />
								</c:otherwise>
							</c:choose>
						</div>
						<div id="app-logo" class="pull-left">
							<img src="<c:url value="/img/Drassana_RIP_DRA_COL.svg"/>" height="80" alt="RIPEA" />
						</div>
					</div>
				</div>
			</div>
	
			<div class="navbar-header" style="width: 70%;">
				<div class="navbar-collapse collapse">
					<div class="navbar-brand" style="padding: 0px !important; width: 100%;">
						<ul class="navbar-brand-ul pull-right">
							<c:choose>
								<c:when test="${isRolActualSuperusuari}">
									<li><a href="<c:url value="/entitat"/>"><spring:message code="decorator.menu.entitats"/></a></li>
									<li><a href="<c:url value="/integracio"/>"><spring:message code="decorator.menu.integracions"/></a></li>
									<li><a href="<c:url value="/excepcio"/>"><spring:message code="decorator.menu.excepcions"/></a></li>
								</c:when>
								<c:when test="${isRolActualAdministrador or isRolActualAdministradorOrgan}">
									<li><a href="<c:url value="/expedient"></c:url>"><spring:message code="decorator.menu.expedients"/></a></li>
									<li>
										<a href="<c:url value="/expedientPeticio"></c:url>">
											<spring:message code="decorator.menu.expedientPeticions"/>
											<span id="anotacio-pendent-count" class="badge small" style="background-color: #337ab6;">${countAnotacionsPendents}</span>
										</a>
									</li>
									<li>
										<a href="<c:url value="/metaExpedient"/>"><spring:message code="decorator.menu.metaexpedients"/><c:if test="${organsNoSincronitzats > 0}"><span class="badge small" title="<spring:message code='metaexpedient.actualitzacio.organs.no.sync'/>" style="background-color: #a94442; float: right;">${organsNoSincronitzats}</span></c:if></a>
									</li>
								</c:when>
								<c:when test="${isRolActualUsuari}">
									<li><a href="<c:url value="/expedient"></c:url>"><spring:message code="decorator.menu.expedients"/></a></li>
									<li>
										<a href="<c:url value="/expedientPeticio"></c:url>">
											<spring:message code="decorator.menu.expedientPeticions"/>
											<span id="anotacio-pendent-count" class="badge small" style="background-color: #337ab6;">${countAnotacionsPendents}</span>
										</a>
									</li>
									<li>
										<a href="<c:url value="/usuariTasca"></c:url>">
											<spring:message code="decorator.menu.tasques"/>
											<span id="tasca-pendent-count" class="badge small" style="background-color: #337ab6;">${countTasquesPendent}</span>
										</a>
									</li>								
								</c:when>
							</c:choose>
							<li>&nbsp;</li>
							<li>
								<div class="llista-menu-desplegable" onclick="toggleMenuPrincipal()">
									<span class="fa fa-bars"></span>
								</div>
							</li>
						</ul>
					</div>
				</div>
			</div>

			<div class="navbar-header" style="width: 5%;">
				<div class="navbar-collapse collapse">
					<div class="navbar-brand" style="padding: 0px !important; width: 100%;">
						<div class="circular-avatar" onclick="mostrarMenuUsuari(event)"></div>
					</div>
				</div>
			</div>

		</div>
	</div>

	<div class="container container-main container-caib">	
		<c:if test="${not empty avisos}">
			<div id="accordion">
				<c:forEach var="avis" items="${avisos}" varStatus="status">
						<div class="card avisCard ${avis.avisNivell == 'INFO' ? 'avisCardInfo':''} ${avis.avisNivell == 'WARNING' ? 'avisCardWarning':''} ${avis.avisNivell == 'ERROR' ? 'avisCardError':''}">
	
							<div data-toggle="collapse" data-target="#collapse${status.index}" class="card-header avisCardHeader">
								${avis.avisNivell == 'INFO' ? '<span class="fa fa-info-circle text-info"></span>':''} ${avis.avisNivell == 'WARNING' ? '<span class="fa fa-exclamation-triangle text-warning"></span>':''} ${avis.avisNivell == 'ERROR' ? '<span class="fa fa-warning text-danger"></span>':''} ${avis.assumpte}
							<button class="btn btn-default btn-xs pull-right"><span class="fa fa-chevron-down "></span></button>										
							</div>
	
							<div id="collapse${status.index}" class="collapse" data-parent="#accordion">
								<div class="card-body avisCardBody" >${avis.missatge}</div>
							</div>
						</div>
				</c:forEach>
			</div>
		</c:if>

		<div class="panel panel-default">
			<div class="panel-heading">
				<h2>
					<c:set var="metaTitleIconClass"><decorator:getProperty property="meta.title-icon-class"/></c:set>
					<c:if test="${not empty metaTitleIconClass}"><span class="${metaTitleIconClass}"></span></c:if>
					<decorator:title />
<%-- 					<small><decorator:getProperty property="meta.subtitle"/></small> --%>
				</h2>
			</div>
			<div class="panel-body">
				<div id="contingut-missatges"><rip:missatges/></div>
    			<decorator:body />
			</div>
		</div>
	</div>
    <div class="container container-foot container-caib">
    	<div class="pull-left app-version"><p>RIPEA <rip:versio/></p></div>
        <div class="pull-right govern-footer">
        	<p>
	        	<img src="<c:url value="/img/uenegroma.png"/>"	     hspace="5" height="50" alt="<spring:message code='decorator.logo.ue'/>" />
	        	<img src="<c:url value="/img/feder7.png"/>" 	     hspace="5" height="35" alt="<spring:message code='decorator.logo.feder'/>" />
	        	<img src="<c:url value="/img/una_manera.png"/>" 	 hspace="5" height="30" alt="<spring:message code='decorator.logo.manera'/>" />
	        	<%--img src="<c:url value="/img/govern-logo-neg.png"/>" hspace="5" height="30" alt="<spring:message code='decorator.logo.govern'/>" /--%>
        	</p>
        </div>
    </div>
</body>
</html>