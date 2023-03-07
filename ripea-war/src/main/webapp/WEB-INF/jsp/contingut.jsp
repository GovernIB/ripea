<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>


<c:set var="expedientId" scope="request" value="${contingut.expedientId}" />
<c:set var="expedient" scope="request" value="${contingut.expedientObject}" />

<c:set var="expedientAgafatPerUsuariActual" scope="request">
	<c:choose>
		<c:when test="${expedient.agafatPer.codi == pageContext.request.userPrincipal.name}">true</c:when>
		<c:otherwise>false</c:otherwise>
	</c:choose>
</c:set>
<c:set var="expedientTancat" scope="request">
	<c:choose>
		<c:when test="${expedient.estat == 'TANCAT'}">true</c:when>
		<c:otherwise>false</c:otherwise>
	</c:choose>
</c:set>
<c:set var="expedientObert" scope="request" value="${expedientObert}"/>
<c:set var="isTasca" scope="request">
	<c:choose>
		<c:when test="${not empty tascaId}">true</c:when>
		<c:otherwise>false</c:otherwise>
	</c:choose>
</c:set>

<c:set var="permissionWrite" scope="request" value="${expedient.usuariActualWrite}"/>
<c:set var="potModificar" scope="request">
	<c:choose>
		<c:when test="${((expedientAgafatPerUsuariActual and permissionWrite) or isTasca or contingut.admin) and expedientObert}">true</c:when>
		<c:otherwise>false</c:otherwise>
	</c:choose>
</c:set>


<c:set var="htmlIconaCarpeta6em"><span class="fa-stack" style="font-size:.6em"><i class="fa fa-folder fa-stack-2x"></i><i class="fa fa-clock-o fa-stack-1x fa-inverse"></i></span></c:set>
<rip:blocIconaContingutNoms/>
<html>
<head>
	<rip:modalHead/>
	<title>
		<c:choose>
			<c:when test="${isTasca}">&nbsp;<span>${tascaNom}&nbsp;</span><div title="${tascaDescripcio}" style="max-width: 60%; display: inline-block; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; margin-bottom: -3px; font-size: 20px; color: #666666;"> ${tascaDescripcio}</div></c:when>
			<c:when test="${contingut.expedient}">&nbsp;${contingut.nom}</c:when>
			<c:when test="${contingut.carpeta}">&nbsp;${expedient.nom}</c:when>
			<c:when test="${contingut.document}">&nbsp;${expedient.nom}</c:when>
		</c:choose>

	</title>
	
	<c:set var="titleIconClass"><rip:blocIconaContingut contingut="${expedient}" nomesIconaNom="true"/></c:set>
	<c:set var="titleIconClass" value="${fn:trim(titleIconClass)}"/>
	<c:if test="${not empty titleIconClass}"><meta name="title-icon-class" content="fa ${titleIconClass}"/></c:if>
		
    <script src="<c:url value="/webjars/jquery-ui/1.12.1/jquery-ui.min.js"/>"></script>
	<link href="<c:url value="/webjars/jquery-ui/1.12.1/jquery-ui.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/js/jquery.filedrop.js"/>"></script>
	<script src="<c:url value="/js/jquery.treetable.js"/>"></script><!-- https://www.jqueryscript.net/table/Minimal-Collapsible-Tree-Table-Plugin-With-jQuery-treetable.html -->
	<script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>
	<script src="<c:url value="/webjars/autoNumeric/1.9.30/autoNumeric.js"/>"></script>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<script src="<c:url value="/js/clamp.js"/>"></script>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<script src="<c:url value="/webjars/pdf-js/2.5.207/build/pdf.js"/>"></script>

<style>





span {
	display: inline-block;
}
span.align-right {
	text-align: right;
}
span a {
	font-size: 14px;
}
.ellipsis {
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}
.tab-content {
	margin-top: .8em;
}

#contenidor-info {
	margin-bottom: 0;
	padding-bottom: 30px;
}
#contenidor-info h3 {
	font-weight: bold;
	margin-top: 0;
	border-bottom: 1px solid #e3e3e3;
	padding-bottom: .6em;
	padding-bottom: 6px;
}
	
#contenidor-info h4 {
	font-weight: bold;
	margin-top: 0;
	border-bottom: 1px solid #f5f5f5;
	padding-bottom: .6em;
}
#contenidor-info dt {
	color: #999;
	font-size: small;
	font-style: italic;
	font-weight: normal;
}
#contenidor-info dd {
	font-size: medium;
	font-weight: bold;
	margin-bottom: 0.4em;
}

.right {
	float: right;
}

#colInfo {
	padding-left: 0;
}
#colInfo #botons-accions-info {
	float: right;
}
#colContent {
	padding-right: 0;
}
#alerta-no-agafat {
	margin-bottom: 15px;
}


.disabled {
	pointer-events: none;
    opacity: 0.4; 
}
#loading {
	display: flex;
	justify-content: center;
	position: absolute;
	z-index: 100;
	width: 100%;
	margin-top: 10%;
}
.selectd {
	background-color: #c6c6c6
}
.down {
	font-size: 90px;
}




.rmodal {
    display:    none;
    position:   fixed;
    z-index:    1000;
    top:        0;
    left:       0;
    height:     100%;
    width:      100%;
    background: rgba( 255, 255, 255, .8 ) 
                url('<c:url value="/img/loading.gif"/>') 
                50% 50% 
                no-repeat;
}
body.loading {
    overflow: hidden;   
}
body.loading .rmodal {
    display: block;
}


/** SPINNER CREATION **/

.loader {
  position: relative;
  text-align: center;
  margin: 15px auto 35px auto;
  z-index: 9999;
  display: block;
  width: 80px;
  height: 80px;
  border: 10px solid rgba(0, 0, 0, .3);
  border-radius: 50%;
  border-top-color: #000;
  animation: spin 1s ease-in-out infinite;
  -webkit-animation: spin 1s ease-in-out infinite;
}

@keyframes spin {
  to {
    -webkit-transform: rotate(360deg);
  }
}

@-webkit-keyframes spin {
  to {
    -webkit-transform: rotate(360deg);
  }
}

.loader-txt {
  p {
    font-size: 13px;
    color: #666;
    small {
      font-size: 11.5px;
      color: #999;
    }
  }
}


#documentDropdownAccions ul.dropdown-menu {
	left: auto;
    right: 0;
    margin-right: -10px;
    border-radius: 4px;
    margin-top: 2px;
}
.select2-container .selection {
	width: 100% !important;
}




.btn-top {
	position: fixed;
	z-index: 1000;
	right: 60px;
	bottom: 50px;
	background-color: #FFF;
	padding: 0 5px 0 5px;
	border-radius: 5px;
	cursor: pointer;
	opacity: 0.1;
}
.sortable-dest {
	background-image: url('<c:url value="../img/background-pattern.png"/>');
}




.breadcrumb {
    float: left;
}
#contingut-botons #botons-accions-info {
	float: left;
}

.esborranys {
	text-align: center;
}
.esborranys.alert.alert-warning {
	color: #734b29;
	background-color: #ffab66;
	border-color: #ff8d30;
}


</style>




<script>


var notificacioEnviamentEstats = new Array();
<c:forEach var="estat" items="${notificacioEnviamentEstats}">
notificacioEnviamentEstats["${estat.value}"] = "<spring:message code="${estat.text}"/>";
</c:forEach>
var notificacioEstatText = new Array();
<c:forEach var="option" items="${notificacioEstatEnumOptions}">
	notificacioEstatText["${option.value}"] = "<spring:message code="${option.text}"/>";
</c:forEach>





//################################################## document ready START ##############################################################
$(document).ready(function() {

	$('.nav-tabs a[href$="#interessats"]').on('click', function() {
		$('#taulaInteressats').webutilDatatable();
	});
	$('.nav-tabs a[href$="#notificacions"]').on('click', function() {
		$('#taulaNotificacions').webutilDatatable();
	});
	if (/#contingut/.test(window.location.href)) {
		$('.nav-tabs a[href$="#contingut"]').trigger('click');	
	} else if (/#dades/.test(window.location.href)){
		$('.nav-tabs a[href$="#dades"]').trigger('click');
	} else if (/#interessats/.test(window.location.href)){
		$('.nav-tabs a[href$="#interessats"]').trigger('click');
	} else if (/#tasques/.test(window.location.href)){
		$('.nav-tabs a[href$="#tasques"]').trigger('click');
	} else if (/#notificacions/.test(window.location.href)){
		$('.nav-tabs a[href$="#notificacions"]').trigger('click');
	} else if (/#publicacions/.test(window.location.href)){
		$('.nav-tabs a[href$="#publicacions"]').trigger('click');
	} else if (/#anotacions/.test(window.location.href)){
		$('.nav-tabs a[href$="#anotacions"]').trigger('click');
	}
	$('a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
		  var target = $(e.target).attr("href")
		  window.history.replaceState('','', window.location.href.substr(0, window.location.href.indexOf("#")) + target);  
	});

	
	$('#taulaDades').on('draw.dt', function (e, settings) {
		var api = new $.fn.dataTable.Api(settings);
		$('#dades-count').html(api.page.info().recordsTotal);
	});
	$('#taulaInteressats').on('draw.dt', function (e, settings) {
		var api = new $.fn.dataTable.Api(settings);
		$('#interessats-count').html(api.page.info().recordsTotal);
		$('.disabledMsg').tooltip();
	});
	$('#taulaNotificacions').on('draw.dt', function (e, settings) {
		var api = new $.fn.dataTable.Api(settings);
		$('#notificacions-count').html(api.page.info().recordsTotal);
	});
	$('#taulaAnotacions').on('draw.dt', function (e, settings) {
		var api = new $.fn.dataTable.Api(settings);
		$('#anotacions-count').html(api.page.info().recordsTotal);
	});	
	$('#taulaTasques').on('draw.dt', function (e, settings) {
		var api = new $.fn.dataTable.Api(settings);
		$('#tasques-count').html(api.page.info().recordsTotal);
	});

	
	$("a.fileDownload").on("click", function() {
		$("body").addClass("loading");
		checkLoadingFinished();
    });
	
	$('#contenidor-info li a.confirm-delete').click(function() {
		return confirm('<spring:message code="contingut.confirmacio.esborrar.node"/>');
	});
	
	var iconaIdx = $('.esborranys > p').text().indexOf('(B)');
	if (iconaIdx != -1) {
		var newValidacioTxt = $('.esborranys > p').text().replace('B', '<i class="fa fa-bold" />');
		$('.esborranys > p').html(newValidacioTxt);
	} 


	<c:set var="isCarpetaPendentArxiu" value="${(contingut.carpeta && !isCreacioCarpetesLogica && contingut.arxiuUuid == null)}"/>
	<c:if test="${isCarpetaPendentArxiu || (!contingut.carpeta && contingut.arxiuUuid == null)}">
		var arxiu = '<span class="fa fa-exclamation-triangle text-danger" title="<spring:message code="contingut.icona.estat.pendentGuardarArxiu"/>"></span>';
		$(".container-main .panel-heading h2").append(arxiu);
	</c:if>
	


	if (${pipellaAnotacionsRegistre}) {
		$('#contingut').removeClass( "active in" );
		$('#peticions').addClass( "active in" );
		$('#pipella-contingut').removeClass( "active" );
		$('#pipella-peticions').addClass( "active" );
	}


						
	//scroll top
	$('.btn-top').on('click', function() {
		$([document.documentElement, document.body]).animate({
	        scrollTop: 0
	    }, 200);
	});
	$(document).scroll(function() {
		var scrollTop = $(document).scrollTop();
		var opacity = 0.4 + scrollTop / 1500;
		if (opacity > 0.9)
			opacity = 0.9;
		$('.btn-top').css({
			opacity: opacity
		});
	});



			
	
});//################################################## document ready END ##############################################################






function checkLoadingFinished() {
	var cookieName = "contentLoaded";
	if (getCookie(cookieName) != "") {
		$("body").removeClass("loading");
        removeCookie(cookieName);
		return;
	}
    setTimeout(checkLoadingFinished, 100);
}
function setCookie(cname,cvalue) {
	var exdays = 30;
    var d = new Date();
    d.setTime(d.getTime() + (exdays*24*60*60*1000));
    var expires = "expires=" + d.toGMTString();
    document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/";
}
function getCookie(cname) {
    var name = cname + "=";
    var ca = document.cookie.split(';');
    for(var i = 0; i < ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0) == ' ') {
            c = c.substring(1);
        }
        if (c.indexOf(name) == 0) {
            return c.substring(name.length, c.length);
        }
    }
    return "";
}
function removeCookie(cname) {
    var expires = new Date(0).toUTCString();
    document.cookie = cname + "=; path=/; expires=" + expires + ";";
}


	


</script>



<!-- edicioOnlineActiva currently doesnt exist in application --> 
<c:if test="${edicioOnlineActiva and contingut.document}">
	<script src="http://www.java.com/js/deployJava.js"></script>
	<script type="text/javascript">
	var officeExecAttributes = {
			id: 'officeExecApplet',
			code: 'es.caib.ripea.applet.OfficeExecApplet.class',
			archive: '<c:url value="/applet/ripea-applet.jar"/>',
			width: 1,
			height: 1};
	var officeExecParameters = {};
	
	$(document).ready(function() {
		$('.btn-document-modificar').click(function() {
			if (typeof officeExecApplet == 'undefined') {
				var intercepted = '';
				var dwBackup = document.write;
				document.write = function(arg) {intercepted += arg};
				deployJava.runApplet(
						officeExecAttributes,
						officeExecParameters,
						'1.5');
				document.write = dwBackup;
				$(document.body).append(intercepted);
			}
			if (officeExecApplet.openWithOffice) {
				officeExecApplet.openWithOffice(this.href)
				officeExecTimeout = function(){
					var exitCode = officeExecApplet.getExitValue();
					if (exitCode == 0) {
						location.reload(true);
					} else {
						setTimeout(officeExecTimeout, 500);
					}
				}
				setTimeout(officeExecTimeout, 500);
			} else {
				alert("<spring:message code="contingut.applet.no.trobat"/>");
			}
			return false;
		});
	});
	</script>
</c:if>


</head>
<body>
	<div class="rmodal"></div>
	<input id="contingutId" type="hidden" value="${contingut.id}">
	
	<!---------------------------------------- AGAFAR / ALLIBERAR  ------------------------------------------>
	<c:if test="${(contingut.expedient or contingut.carpeta) and not empty expedient.agafatPer and !isTasca}">
		<div class="text-right" data-toggle="botons-titol">
			<ul class="list-group pull-right">
	  			<li class="list-group-item" style="padding: 5px 12px; margin-right: 4px">
	  				<spring:message code="contingut.info.agafat.per"/>:
	  				${expedient.agafatPer.codiAndNom}&nbsp;
	  				<c:if test="${expedientAgafatPerUsuariActual}">
	  					<a href="<c:url value="/expedient/${expedient.id}/alliberar?contingutId=${contingut.id}"/>" class="btn btn-default btn-xs" title="<spring:message code="comu.boto.alliberar"/>"><span class="fa fa-unlock"></span></a>
	  				</c:if>
	  			</li>
	  		</ul>
		</div>
	</c:if>
	<c:if test="${!isTasca && not expedientAgafatPerUsuariActual}">
		<div id="alerta-no-agafat" class="alert well-sm alert-info alert-dismissable" style="min-height: 40px;">
			<c:if test="${!contingut.admin}">
				<span class="fa fa-info-circle"></span> 
				<spring:message code="contingut.alerta.no.agafat"/>
			</c:if>  
			<a href="<c:url value="../expedient/${expedient.id}/agafar?contingutId=${contingut.id}"/>" class="btn btn-xs btn-default pull-right"><span class="fa fa-lock"></span>&nbsp;&nbsp;<spring:message code="comu.boto.agafar"/></a>
		</div>
	</c:if>
	
	
	<div>
		<c:if test="${contingut.expedient or contingut.carpeta}">
			<!------------------------------------------------------------------------- INFORMACIÓ EXPEDIENT (LEFT SIDE OF THE PAGE) ------------------------------------------------------------------------>
			<div class="col-md-3 col-sm-4" id="colInfo">		
				<div id="contenidor-info" class="well">
					<h3>
						<c:choose>
							<c:when test="${isTasca}">
							<span style="width:100%" class="ellipsis" title="${contingut.nom}">
								${contingut.nom}
							</span>
							</c:when>
							<c:otherwise>
								<spring:message code="contingut.info.informacio"/>
							</c:otherwise>
						</c:choose>
					</h3>
					<dl>
<%-- 						<c:if test="${contingut.carpeta}"> --%>
<%-- 							<dt><spring:message code="contingut.info.nom" /></dt> --%>
<%-- 							<dd class="ellipsis">${contingut.nom}</dd> --%>
							
<%-- 							<dt><spring:message code="contingut.info.tipus"/></dt> --%>
<%-- 							<dd><spring:message code="contingut.tipus.enum.${contingut.tipus}"/></dd> --%>
<%-- 						</c:if> --%>
						<c:if test="${contingut.carpeta}">
							<dt><spring:message code="contingut.info.numero"/></dt>
							<dd>${expedient.numero}</dd>
							
							<dt><spring:message code="contingut.info.titol"/></dt>
							<dd>${expedient.nom}</dd>							
							
							<c:if test="${not empty expedient.metaNode}">
								<dt><spring:message code="contingut.info.meta.expedient"/></dt>
								<dd>${expedient.metaNode.nom}</dd>
							</c:if>
							
							<dt><spring:message code="contingut.info.nti.organ"/></dt>
							<dd>${expedient.organGestorText}</dd>

							<dt><spring:message code="contingut.info.nti.data.obertura"/></dt>
							<dd><fmt:formatDate value="${expedient.ntiFechaApertura}" pattern="dd/MM/yyyy HH:mm:ss"/></dd>
							
							<dt><spring:message code="contingut.info.estat"/></dt>
							<c:choose>
								<c:when test="${expedient.expedientEstat!=null}">
									<dd style="<c:if test='${not empty expedient.expedientEstat.color}'>border-left: solid 6px ${expedient.expedientEstat.color}; padding-left: 4px;</c:if>">${expedient.expedientEstat.nom}</dd>
								</c:when>
								<c:otherwise>
									<dd><spring:message code="expedient.estat.enum.${expedient.estat}"/></dd>
								</c:otherwise>
							</c:choose>	
								
							<dt><spring:message code="contingut.info.nti.classificacio"/></dt>
							<dd>${expedient.ntiClasificacionSia}</dd>

						</c:if>

						<c:if test="${contingut.expedient}">
							<dt><spring:message code="contingut.info.numero"/></dt>
							<dd>${contingut.numero}</dd>
							
							<dt><spring:message code="contingut.info.titol"/></dt>
							<dd>${contingut.nom}</dd>							
							
							<c:if test="${not empty contingut.metaNode}">
								<dt><spring:message code="contingut.info.meta.expedient"/></dt>
								<dd>${contingut.metaNode.nom}</dd>
							</c:if>
							
							<dt><spring:message code="contingut.info.nti.organ"/></dt>
							<dd>${contingut.organGestorText}</dd>

							<dt><spring:message code="contingut.info.nti.data.obertura"/></dt>
							<dd><fmt:formatDate value="${contingut.ntiFechaApertura}" pattern="dd/MM/yyyy HH:mm:ss"/></dd>
							
							<dt><spring:message code="contingut.info.estat"/></dt>
							<c:choose>
								<c:when test="${contingut.expedientEstat!=null}">
									<dd style="<c:if test='${not empty contingut.expedientEstat.color}'>border-left: solid 6px ${contingut.expedientEstat.color}; padding-left: 4px;</c:if>">${contingut.expedientEstat.nom}</dd>
								</c:when>
								<c:otherwise>
									<dd><spring:message code="expedient.estat.enum.${contingut.estat}"/></dd>
								</c:otherwise>
							</c:choose>	
								
							<dt><spring:message code="contingut.info.nti.classificacio"/></dt>
							<dd>${contingut.ntiClasificacionSia}</dd>

						</c:if>

						<c:if test="${!isTasca && not empty relacionats}">
							<h4 id="expedient-info-relacionats" style="padding-bottom: 0 !important;margin-bottom: 4px !important; border-bottom: 1px solid #e3e3e3">
								<spring:message code="contingut.info.relacionats"/>
							</h4>
							<ul class="list-unstyled">
								<c:forEach var="expedientRelacionat" items="${relacionats}">
									<c:if test="${!expedientRelacionat.esborrat}">
										<li style="font-size:14px; line-height: 25px;">
											<span style="width:10%" class="fa ${iconaExpedientObert}"></span>
											<span style="width:76%" class="ellipsis">
												<a href="${expedientRelacionat.id}">
													[${expedientRelacionat.sequencia}/${expedientRelacionat.any}] 
													${expedientRelacionat.nom} 
												</a>
											</span>
											 <span style="width:10%; height: 16px;" class="align-right qqq">
												<c:if test="${permissionWrite}">
													<a href="<c:url value="/expedient/${expedientId}/relacio/${expedientRelacionat.id}/delete"/>" class="btn btn-default btn-xs" data-confirm="<spring:message code="contingut.info.relacio.esborrar.confirm"/>" style="float: right;">
														<span class="fa fa-trash-o"></span>
													</a> 
												</c:if>										 
									        </span>	
										</li>
									</c:if>
								</c:forEach>
							</ul>
						</c:if> 
						
						<c:if test="${!isTasca}">
							<rip:blocContingutAccions id="botons-accions-info" contingut="${expedient}" modeLlistat="true" mostrarObrir="false" contingutNavigationId="${contingut.id}"/>
						</c:if>
					</dl>
				</div>
			</div>
		</c:if>
		<!------------------------------------------------------------------------- MAIN CONTENT (CENTER/RIGHT SIDE OF THE PAGE) ------------------------------------------------------------------------------->
		<div class="${contingut.document ? 'col-md-12' : 'col-md-9 col-sm-8'}" id="colContent">
			
			<!---------------------------------------- ALERTS ------------------------------------------>
			<c:if test="${contingut.expedient && !isTasca && expedientObert && contingut.hasEsborranys && convertirDefinitiu}">
				<div id="botons-errors-esborranys" class="esborranys alert well-sm alert-info alert-dismissable">
					<p><spring:message code="contingut.errors.expedient.conte.esborranys"/></p>
					<b><spring:message code="contingut.errors.expedient.conte.esborranys.bold"/></b>
				</div>
			</c:if>
			<c:if test="${!isTasca && contingut.node and contingut.alerta}">
				<div id="botons-errors-alerta" class="alert well-sm alert-warning alert-dismissable">
					<span class="fa fa-exclamation-circle text-danger"></span>&nbsp;
					<c:choose>
						<c:when test="${contingut.expedient}"><spring:message code="contingut.errors.expedient.alertes"/></c:when>
						<c:when test="${contingut.document}"><spring:message code="contingut.errors.document.alertes"/></c:when>
					</c:choose>
					<a href="<c:url value="/contingut/${contingut.id}/alertes"/>" class="btn btn-xs btn-default pull-right" data-toggle="modal"><spring:message code="contingut.alertes.consultar"/></a>
				</div>
			</c:if>
			<c:if test="${!isTasca && (((contingut.expedient or contingut.document) and not contingut.valid) or ((contingut.carpeta) and not expedient.valid))}">
				<div id="botons-errors-validacio" class="alert well-sm alert-warning alert-dismissable">
					<span class="fa fa-exclamation-triangle"></span>&nbsp;
					<c:choose>
						<c:when test="${contingut.expedient or contingut.carpeta}"><spring:message code="contingut.errors.expedient.validacio"/></c:when>
						<c:when test="${contingut.document}"><spring:message code="contingut.errors.document.validacio"/></c:when>
					</c:choose>
					<a href="<c:url value="/contingut/${expedientId}/errors"/>" class="btn btn-xs btn-default pull-right" data-toggle="modal"><spring:message code="contingut.errors.consultar"/></a>
				</div>
			</c:if>
			
			<!---------------------------------------- TABLIST ------------------------------------------>
			<c:if test="${!isTasca}">
				<ul class="nav nav-tabs">
					<li class="active" id="pipella-contingut">
						<a href="#contingut" data-toggle="tab"><spring:message code="contingut.tab.contingut"/>&nbsp;<span class="badge">${contingut.fillsHierarchicalCount}</span></a>
					</li>
					<c:if test="${((contingut.document or contingut.expedient) and fn:length(contingut.metaNode.metaDades) gt 0) || ((contingut.carpeta) and fn:length(expedient.metaNode.metaDades) gt 0)}">
						<li>
							<a href="#dades" data-toggle="tab"><spring:message code="contingut.tab.dades"/>&nbsp;<span class="badge" id="dades-count">${contingut.carpeta ? expedient.dadesCount : contingut.dadesCount}</span></a>
						</li>
					</c:if>
					<c:if test="${contingut.expedient or contingut.carpeta}">
						<li>
							<a href="#interessats" data-toggle="tab"><spring:message code="contingut.tab.interessats"/>&nbsp;<span class="badge" id="interessats-count">${interessatsCount}</span></a>
						</li>
						<c:if test="${notificacionsCount> 0}">
							<li>
								<a href="#notificacions" data-toggle="tab" id="notificacions-tab"><spring:message code="contingut.tab.remeses" />&nbsp;<span class="badge" id="notificacions-count">${notificacionsCount}</span></a>
							</li>
						</c:if>
						<c:if test="${publicacionsCount> 0}">
							<li>
								<a href="#publicacions" data-toggle="tab" id="publicacions-tab"><spring:message code="contingut.tab.publicacions" />&nbsp;<span class="badge" id="publicacions-count">${publicacionsCount}</span></a>
							</li>
						</c:if>
						<c:if test="${(contingut.expedient && contingut.peticions ) || (contingut.carpeta && expedient.peticions)}">
							<li>
								<a href="#anotacions" data-toggle="tab"><spring:message code="contingut.tab.anotacions"/>&nbsp;<span class="badge" id="anotacions-count"></span></a>
							</li>
						</c:if>
						<li>
							<a href="#tasques" data-toggle="tab"><spring:message code="contingut.tab.tasques"/>&nbsp;<span class="badge" id="tasques-count"></span></a>
						</li>					
					</c:if>
					<c:if test="${contingut.document and fn:length(contingut.versions) gt 0}">
						<li>
							<a href="#versions" data-toggle="tab"><spring:message code="contingut.tab.versions"/>&nbsp;<span class="badge" id="versions-count">${fn:length(contingut.versions)}</span></a>
						</li>
					</c:if>
					<c:if test="${contingut.document}">
						<div class="dropdown" style="float: right;" id="documentDropdownAccions">
							<button data-toggle="dropdown" class="btn btn-primary dropdown-toggle"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret caret-white"></span></button>
							<ul class="dropdown-menu">
								<c:choose>
									<c:when test="${contingut.arxiuUuid != null}">
										<li><a href="<c:url value="/contingut/${contingut.id}/arxiu"/>" data-toggle="modal"><span class="fa fa-info-circle"></span>&nbsp;<spring:message code="comu.boto.arxiu"/></a></li>
					 				</c:when>
					 				<c:otherwise>
					 					<li><a class="disabled" href="#"><span class="fa fa-info-circle"></span>&nbsp;<spring:message code="comu.boto.arxiu"/></a></li>
									</c:otherwise>
								</c:choose>									
							</ul>
						</div>
					</c:if>	
					<c:if test="${contingut.expedient || contingut.carpeta}">
						<a href="<c:url value="/expedient/${expedientId}/comentaris"/>" data-toggle="modal" data-refresh-tancar="true" class="btn btn-default pull-right ${permissionWrite ? '' : 'disabled'}"><span class="fa fa-lg fa-comments"></span>&nbsp;<span class="badge">${expedient.numComentaris}</span></a>
					</c:if>
				</ul>
			</c:if>
			
			<!---------------------------------------- TABPANELS ------------------------------------------>
			<div class="tab-content">
				<!------------------ Contingut --------------------->
				<div class="tab-pane active in" id="contingut">
					<jsp:include page="includes/contingutTabContingut.jsp"/>
				</div>
				<c:if test="${!isTasca}">
						<!------------------ Dades --------------------->
						<div class="tab-pane" id="dades">
							<jsp:include page="includes/contingutTabDades.jsp"/>
						</div>
					<c:if test="${contingut.expedient || contingut.carpeta}">
						<!------------------ Interessats --------------------->	
						<div class="tab-pane" id="interessats">
							<jsp:include page="includes/contingutTabInteressats.jsp"/>
						</div>
						<!------------------ Remeses --------------------->	
						<div class="tab-pane" id="notificacions">
							<jsp:include page="includes/contingutTabNotificacions.jsp"/>
						</div>
						<!------------------ Publicacions --------------------->	
						<div class="tab-pane" id="publicacions">
							<jsp:include page="includes/contingutTabPublicacions.jsp"/>
						</div>						
						<!--  If expedient was created from anotació -->
						<c:if test="${(contingut.expedient && contingut.peticions ) || (contingut.carpeta && expedient.peticions)}">
							<!------------------ Anotacions --------------------->
							<div class="tab-pane" id="anotacions">
								<jsp:include page="includes/contingutTabAnotacions.jsp"/>
							</div>
						</c:if>
					</c:if>
					<c:if test="${contingut.document and fn:length(contingut.versions) gt 0}">
						<!------------------ Versions --------------------->
						<div class="tab-pane" id="versions">
							<jsp:include page="includes/contingutTabVersions.jsp"/>
						</div>
					</c:if>
					<c:if test="${contingut.expedient || contingut.carpeta}">
						<!------------------ Tasques --------------------->
						<div class="tab-pane" id="tasques">
							<jsp:include page="includes/contingutTabTasques.jsp"/>
						</div>
					</c:if>	
				</c:if>	
			</div>
		</div>
	</div>
	<c:if test="${contingut.document}">
		<div id="modal-botons" class="well">
			<c:if test="${contingut.custodiat}">
				<a data-element-no-tancar="true" href="<c:url value="/contingut/${contingut.id}/document/${contingut.id}/descarregarImprimible"/>" class="btn btn-default pull-right"><span class="fa fa-download"></span>&nbsp;<spring:message code="comu.boto.descarregar.imprimible"/></a>
			</c:if>
			<c:if test="${contingut.documentTipus != 'FISIC'}">
				<a data-element-no-tancar="true" href="<c:url value="/contingut/${contingut.id}/document/${contingut.id}/descarregar"/>" <c:if test="${contingut.custodiat}">style="margin-right: 10px;"</c:if> class="btn btn-default pull-right"><span class="fa fa-download"></span>&nbsp;<spring:message code="comu.boto.descarregar"/></a>
			</c:if>
		</div>
	</c:if>
	<div class="btn-top">
		<span class="fa fa-arrow-up"></span>
	</div>
</body>
</html>

