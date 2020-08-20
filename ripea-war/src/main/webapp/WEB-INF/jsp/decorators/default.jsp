<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip" %>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator"%>

<%

// 	pageContext.setAttribute(
// 			"sessionOrgansGestors",
// 			es.caib.ripea.war.helper.OrganGestorHelper.findOrganGestorsAccessibles(request));
// 	pageContext.setAttribute(
//   			"organGestorActual",
//   			es.caib.ripea.war.helper.OrganGestorHelper.getOrganGestorActual(request));
	pageContext.setAttribute(
			"sessionEntitats",
			es.caib.ripea.war.helper.EntitatHelper.findEntitatsAccessibles(request));
	pageContext.setAttribute(
			"entitatActual",
			es.caib.ripea.war.helper.EntitatHelper.getEntitatActual(request));
// 	pageContext.setAttribute(
//   			"requestParameterCanviOrganGestor",
//   			es.caib.ripea.war.helper.OrganGestorHelper.getRequestParameterCanviOrganGestor());
	pageContext.setAttribute(
			"requestParameterCanviEntitat",
			es.caib.ripea.war.helper.EntitatHelper.getRequestParameterCanviEntitat());
	pageContext.setAttribute(
			"dadesUsuariActual",
			es.caib.ripea.war.helper.SessioHelper.getUsuariActual(request));
	pageContext.setAttribute(
			"rolActual",
			es.caib.ripea.war.helper.RolHelper.getRolActual(request));
	pageContext.setAttribute(
			"rolsUsuariActual",
			es.caib.ripea.war.helper.RolHelper.getRolsUsuariActual(request));
	pageContext.setAttribute(
			"isRolActualSuperusuari",
			es.caib.ripea.war.helper.RolHelper.isRolActualSuperusuari(request));
	pageContext.setAttribute(
			"isRolActualAdministrador",
			es.caib.ripea.war.helper.RolHelper.isRolActualAdministrador(request));
	pageContext.setAttribute(
	  			"isRolActualAdministradorOrgan",
	  			es.caib.ripea.war.helper.RolHelper.isRolActualAdministradorOrgan(request));
	pageContext.setAttribute(
			"isRolActualUsuari",
			es.caib.ripea.war.helper.RolHelper.isRolActualUsuari(request));
	pageContext.setAttribute(
			"requestParameterCanviRol",
			es.caib.ripea.war.helper.RolHelper.getRequestParameterCanviRol());
	pageContext.setAttribute(
			"teAccesExpedients",
			es.caib.ripea.war.helper.ExpedientHelper.teAccesExpedients(request));
	pageContext.setAttribute(
			"countTasquesPendent",
			es.caib.ripea.war.helper.TasquesPendentsHelper.countTasquesPendents(request));
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
	<script src="<c:url value="/webjars/jquery/1.12.0/dist/jquery.min.js"/>"></script>
	<!-- Llibreria per a compatibilitat amb HTML5 -->
	<!--[if lt IE 9]>
		<script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
	<![endif]-->
	<script src="<c:url value="/webjars/bootstrap/3.3.6/dist/js/bootstrap.min.js"/>"></script>
	
	<script>var requestLocale = '${requestLocale}';</script>
	<decorator:head />
<style>
body {
	background-image:url(<c:url value="/img/background-pattern.png"/>);
	color:#666666;
	padding-top: 120px;
}
<%-- If capsaleraColorFons is defined for entitat use it, if not look if there is capsaleraColorFons defined for application and use it, if not default color is used 	--%>
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
</style>
</head>
<body>
	<div class="navbar navbar-default navbar-fixed-top navbar-app" role="navigation">
		<div class="container container-caib">
			<div class="navbar-header">
				<%--button class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
					<span class="sr-only">Toggle navigation</span>
					<span class="icon-bar"></span>
					<span class="icon-bar"></span>
					<span class="icon-bar"></span>
				</button--%>
				<div class="navbar-brand">
					<div id="govern-logo" class="pull-left">
						<%-- If logo is defined for application in properties file or for entitat in db then take the logo from there, in other case take default logo from the img folder --%>					
						<c:choose>
							<c:when test="${sessionScope['SessionHelper.capsaleraLogo']!=null  && not empty sessionScope['SessionHelper.capsaleraLogo'] || sessionScope['EntitatHelper.entitatActual'].logoImgBytes!=null && fn:length(sessionScope['EntitatHelper.entitatActual'].logoImgBytes)!=0}">
								<img src="<c:url value="/entitat/getEntitatLogo"/>"  height="65" alt="Govern de les Illes Balears" />
							</c:when>
							<c:otherwise>
								<img src="<c:url value="/img/govern-logo.png"/>"  height="65" alt="Govern de les Illes Balears" />
							</c:otherwise>
						</c:choose>
					</div>
					<div id="app-logo" class="pull-left">
						<img src="<c:url value="/img/logo.png"/>" alt="RIPEA" />
					</div>
				</div>
			</div>
			<div class="navbar-collapse collapse">
				<div class="nav navbar-nav navbar-right">
					<ul class="list-inline pull-right">
						<c:if test="${hiHaEntitats}">
							<li class="dropdown">
								<c:if test="${hiHaMesEntitats}"><a href="#" data-toggle="dropdown"></c:if>
		         				<span class="fa fa-institution"></span> ${entitatActual.nom} <c:if test="${hiHaMesEntitats}"><b class="caret caret-white"></b></c:if>
								<c:if test="${hiHaMesEntitats}"></a></c:if>
								<c:if test="${hiHaMesEntitats}">
									<ul class="dropdown-menu">
										<c:forEach var="entitat" items="${sessionEntitats}" varStatus="status">
											<c:if test="${entitat.id != entitatActual.id}">
												<c:url var="urlCanviEntitat" value="/index">
													<c:param name="${requestParameterCanviEntitat}" value="${entitat.id}"/>
												</c:url>
												<li><a href="${urlCanviEntitat}">${entitat.nom}</a></li>
											</c:if>
										</c:forEach>
									</ul>
								</c:if>
							</li>
						</c:if>
<%-- 						<c:if test="${hiHaOrgansGestors}"> --%>
<!-- 							<li class="dropdown"> -->
<%-- 								<c:if test="${hiHaMesOrgansGestors}"><a href="#" data-toggle="dropdown"></c:if> --%>
<%-- 								<c:if test="${null != organGestorActual}"> --%>
<%-- 		         				<span class="fa fa-cubes"></span> ${organGestorActual.nom} <c:if test="${hiHaMesOrgansGestors}"><b class="caret caret-white"></b></c:if> --%>
<%-- 		         				</c:if> --%>
<%-- 		         				<c:if test="${null == organGestorActual}"> --%>
<%-- 		         				<span class="fa fa-cubes"></span> <spring:message code="decorator.menu.organgestor.tots"/> <c:if test="${hiHaMesOrgansGestors}"><b class="caret caret-white"></b></c:if> --%>
<%-- 		         				</c:if> --%>
<%-- 								<c:if test="${hiHaMesOrgansGestors}"></a></c:if> --%>
<%-- 								<c:if test="${hiHaMesOrgansGestors}"> --%>
<!-- 									<ul class="dropdown-menu"> -->
<%-- 				         				<c:if test="${null != organGestorActual}"> --%>
<%-- 				         					<c:url var="urlCanviOrganGestor" value="/index"> --%>
<%-- 												<c:param name="${requestParameterCanviOrganGestor}" value="-1"/> --%>
<%-- 											</c:url> --%>
<%-- 				         					<li><a href="${urlCanviOrganGestor}"><spring:message code="decorator.menu.organgestor.tots"/></a></li> --%>
<%-- 				         				</c:if> --%>
<%-- 										<c:forEach var="og" items="${sessionOrgansGestors}" varStatus="status"> --%>
<%-- 											<c:if test="${og.id != organGestorActual.id}"> --%>
<%-- 												<c:url var="urlCanviOrganGestor" value="/index"> --%>
<%-- 													<c:param name="${requestParameterCanviOrganGestor}" value="${og.id}"/> --%>
<%-- 												</c:url> --%>
<%-- 												<li><a href="${urlCanviOrganGestor}">${og.nom}</a></li> --%>
<%-- 											</c:if> --%>
<%-- 										</c:forEach> --%>
<!-- 									</ul> -->
<%-- 								</c:if> --%>
<!-- 							</li> -->
<%-- 						</c:if> --%>
						<li class="dropdown">
							<c:choose>
								<c:when test="${fn:length(rolsUsuariActual) > 1}">
									<a href="#" data-toggle="dropdown">
										<span class="fa fa-id-card-o"></span>
										<spring:message code="decorator.menu.rol.${rolActual}"/>
										<span class="caret caret-white"></span>
									</a>
									<ul class="dropdown-menu">
										<c:forEach var="rol" items="${rolsUsuariActual}">
											<c:if test="${rol != rolActual}">
												<li>
													<c:url var="canviRolUrl" value="/index">
														<c:param name="${requestParameterCanviRol}" value="${rol}"/>
													</c:url>
													<a href="${canviRolUrl}"><spring:message code="decorator.menu.rol.${rol}"/></a>
												</li>
											</c:if>
										</c:forEach>
									</ul>
								</c:when>
								<c:otherwise>
									<c:if test="${not empty rolActual}"><span class="fa fa-id-card-o"></span>&nbsp;<spring:message code="decorator.menu.rol.${rolActual}"/></c:if>
								</c:otherwise>
							</c:choose>
						</li>
						<li class="dropdown">
							<a href="#" data-toggle="dropdown">
								<span class="fa fa-user"></span>
								<c:choose>
									<c:when test="${not empty dadesUsuariActual}">${dadesUsuariActual.nom}</c:when>
									<c:otherwise>${pageContext.request.userPrincipal.name}</c:otherwise>
								</c:choose>
								<span class="caret caret-white"></span>
							</a>
							<ul class="dropdown-menu">
								<li>
									<a href="<c:url value="/massiu/consulta/0"/>" data-toggle="modal" data-maximized="true">
										<spring:message code="decorator.menu.accions.massives.user"/>
									</a>
								</li>
								<li>
									<a href="<c:url value="/usuari/configuracio"/>" data-toggle="modal" data-maximized="true" data-refresh-pagina="true">
										<spring:message code="decorator.menu.configuracio.user"/>
									</a>
								</li>
							</ul>
						</li>
					</ul>
					<div class="clearfix"></div>
					
					<%------------------------ MENU BUTTONS ------------------------%>
					<div class="btn-group navbar-btn navbar-right">
						<c:choose>
							<c:when test="${isRolActualSuperusuari}">
								
								<%---- Entitats ----%>
								<a href="<c:url value="/entitat"/>" class="btn btn-primary"><spring:message code="decorator.menu.entitats"/></a>
								<div class="btn-group">
									<button data-toggle="dropdown" class="btn btn-primary dropdown-toggle"><spring:message code="decorator.menu.monitoritzar"/>&nbsp;<span class="caret caret-white"></span></button>
									<ul class="dropdown-menu">
										<li><a href="<c:url value="/integracio"/>"><spring:message code="decorator.menu.integracions"/></a></li>
										<li><a href="<c:url value="/excepcio"/>"><spring:message code="decorator.menu.excepcions"/></a></li>
									</ul>
								</div>
							</c:when>
							<c:when test="${isRolActualAdministrador}">
								<div class="btn-group">
									<button data-toggle="dropdown" class="btn btn-primary dropdown-toggle"><spring:message code="decorator.menu.configurar"/>&nbsp;<span class="caret caret-white"></span></button>
									<ul class="dropdown-menu">
										<li><a href="<c:url value="/metaExpedient"/>"><spring:message code="decorator.menu.metaexpedients"/></a></li>
										<li><a href="<c:url value="/metaDocument"/>"><spring:message code="decorator.menu.metadocuments"/></a></li>
										<li class="divider"></li>
										<li><a href="<c:url value="/tipusDocumental"/>"><spring:message code="decorator.menu.tipusdocumental"/></a></li>
										<li><a href="<c:url value="/domini"/>"><spring:message code="decorator.menu.domini"/></a></li>
										<li><a href="<c:url value="/organgestor"/>"><spring:message code="decorator.menu.organgestor"/></a></li>
										<li class="divider"></li>
										<li><a href="<c:url value="/permis"/>"><spring:message code="decorator.menu.permisos.entitat"/></a></li>
										<li><a href="<c:url value="/organgestor/permis"/>"><spring:message code="decorator.menu.permisos.organgestor"/></a></li>
									</ul>
								</div>
								<div class="btn-group">
									<button data-toggle="dropdown" class="btn btn-primary dropdown-toggle"><spring:message code="decorator.menu.consultar"/>&nbsp;<span class="caret caret-white"></span></button>
									<ul class="dropdown-menu">
										<li><a href="<c:url value="/contingutAdmin"/>"><spring:message code="decorator.menu.continguts"/></a></li>
										<li>
											<a href="<c:url value="/massiu/consulta/0"/>" data-toggle="modal" data-maximized="true">
												<spring:message code="decorator.menu.accions.massives.admin"/>
											</a>
										</li>
									</ul>
								</div>
							</c:when>
							<c:when test="${isRolActualAdministradorOrgan}">
								<div class="btn-group">
									<button data-toggle="dropdown" class="btn btn-primary dropdown-toggle"><spring:message code="decorator.menu.configurar"/>&nbsp;<span class="caret caret-white"></span></button>
									<ul class="dropdown-menu">
										<li><a href="<c:url value="/metaExpedient"/>"><spring:message code="decorator.menu.metaexpedients"/></a></li>
									</ul>
								</div>
							</c:when>
							<c:when test="${isRolActualUsuari}">
							
								<%---- Expedients ----%>
								<a href="<c:url value="/expedient"><c:param name="mantenirPaginacio" value="true" /></c:url>"class="btn btn-primary">
										<spring:message code="decorator.menu.expedients"/>
								</a>
								
								<%---- Expedients pendents ----%>
								<a href="<c:url value="/expedientPeticio"><c:param name="mantenirPaginacio" value="false"/></c:url>"class="btn btn-primary">
									<spring:message code="decorator.menu.expedientPeticions"/>
								</a>
								
								<%---- Tasques ----%>								
								<a href="<c:url value="/usuariTasca"><c:param name="mantenirPaginacio" value="false"/></c:url>"class="btn btn-primary">
									<spring:message code="decorator.menu.tasques"/>
									<span id="tasca-pendent-count" class="badge small">${countTasquesPendent}</span>
								</a>						
										
								<div class="btn-group">
									<button class="btn btn-primary dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
				  						 <spring:message code="massiu.portafirmes"/> <span class="caret"></span>
									</button>
									<ul class="dropdown-menu">
										<li><a href="<c:url value="/massiu/portafirmes"/>"><spring:message code="massiu.portafirmes.firma"/></a></li>
										<c:if test="${convertirDefinitiu}">
										<li><a href="<c:url value="/massiu/definitiu"/>"><spring:message code="massiu.estat.definitiu"/></a></li>
										</c:if>
									</ul>
								</div>								
							</c:when>
						</c:choose>
						<%--c:if test="${isRolActualUsuari or isRolActualAdministrador}">
							<div class="btn-group">
								<button data-toggle="dropdown" class="btn btn-primary dropdown-toggle"><spring:message code="execucions.massives.boto.user"/>&nbsp;<span class="caret caret-white"></span></button>
								<ul class="dropdown-menu">
									<c:if test="${isRolActualUsuari}">
										<li><a href="<c:url value="/massiu/portafirmes"/>"><span class="fa fa-file-o "></span> <spring:message code="execucions.massives.boto.option.portafirmes"/></a></li>
										<li role="separator" class="divider"></li>
									</c:if>
									<li><a href="<c:url value="/massiu/consulta/0"/>" data-toggle="modal" data-maximized="true"><span class="fa fa-tasks"></span> <spring:message code="execucions.massives.boto.option.consulta"/></a></li>
								</ul>
							</div>
						</c:if--%>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div class="container container-main container-caib">
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
