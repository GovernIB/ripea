<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:set var="expedientPare" value="${contingut.expedientPare}"/>
<c:if test="${empty expedientPare and contingut.expedient}"><c:set var="expedientPare" value="${contingut}"/></c:if>
<c:set var="potModificarContingut" value="${empty expedientPare.metaNode or expedientPare.metaNode.usuariActualWrite or expedientPare.usuariActualWrite}"/>
<c:set var="expedientAgafatPerUsuariActual" value="${false}"/>
<c:if test="${expedientPare.agafatPer.codi == pageContext.request.userPrincipal.name}"><c:set var="expedientAgafatPerUsuariActual" value="${true}"/></c:if>
<c:set var="expedientTancat" value="${false}"/>
<c:set var="isTasca" value="${not empty tascaId}"/>
<c:choose>
	<c:when test="${contingut.expedient}">
		<c:if test="${contingut.estat == 'TANCAT'}"><c:set var="expedientTancat" value="${true}"/></c:if>
	</c:when>
	<c:otherwise>
		<c:if test="${contingut.expedientPare.estat == 'TANCAT'}"><c:set var="expedientTancat" value="${true}"/></c:if>
	</c:otherwise>
</c:choose>
<c:set var="htmlIconaCarpeta6em"><span class="fa-stack" style="font-size:.6em"><i class="fa fa-folder fa-stack-2x"></i><i class="fa fa-clock-o fa-stack-1x fa-inverse"></i></span></c:set>
<rip:blocIconaContingutNoms/>
<html>
<head>
	<rip:modalHead/>
	<title>
		<c:choose>
			<c:when test="${isTasca}">&nbsp;<span>${tascaNom}&nbsp;</span><div title="${tascaDescripcio}" style="max-width: 60%; display: inline-block; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; margin-bottom: -3px; font-size: 20px; color: #666666;"> ${tascaDescripcio}</div></c:when>
			<c:when test="${contingut.expedient}">&nbsp;${contingut.nom}</c:when>
			<c:when test="${contingut.carpeta}">&nbsp;${contingut.expedientPare.nom}</c:when>
			<c:when test="${contingut.document}">&nbsp;${contingut.expedientPare.nom}</c:when>
		</c:choose>

	</title>
	
	<c:set var="titleIconClass"><rip:blocIconaContingut contingut="${contingut.expedient ? contingut : contingut.expedientPare}" nomesIconaNom="true"/></c:set>
	<c:set var="titleIconClass" value="${fn:trim(titleIconClass)}"/>
	<c:if test="${not empty titleIconClass}"><meta name="title-icon-class" content="fa ${titleIconClass}"/></c:if>
		
    <script src="<c:url value="/webjars/jquery-ui/1.12.1/jquery-ui.min.js"/>"></script>
	<link href="<c:url value="/webjars/jquery-ui/1.12.1/jquery-ui.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/js/jquery.filedrop.js"/>"></script>
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
	<c:if test="${isContingutDetail}">
		<script src="<c:url value="/webjars/jquery/1.12.0/dist/jquery.min.js"/>"></script>
		<link href="<c:url value="/webjars/bootstrap/3.3.6/dist/css/bootstrap.min.css"/>" rel="stylesheet"/>
		<link href="<c:url value="/webjars/font-awesome/4.7.0/css/font-awesome.min.css"/>" rel="stylesheet"/>
		<link href="<c:url value="/css/estils.css"/>" rel="stylesheet">
		<link rel="shortcut icon" href="<c:url value="/img/favicon.png"/>" type="image/x-icon" />
		<!-- Llibreria per a compatibilitat amb HTML5 -->
		<!--[if lt IE 9]>
	      <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
	    <![endif]-->
	    <script src="<c:url value="/webjars/bootstrap/3.3.6/dist/js/bootstrap.min.js"/>"></script>
	</c:if>
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
#contenidor-contingut {
	margin-left: 0;
	margin-right: -11px;
}
#contenidor-contingut li.element-contingut {
	margin: 0 0 0px 0;
	padding: 0 10px 0 0;
	min-height: 140px;
	display: -moz-inline-stack;
	display: inline-block;
	vertical-align: top;
	zoom: 1;
	*display: inline;
	_height: 140px;
}
#contenidor-contingut .thumbnail {
	margin-bottom: 0 !important;
	border: 2px solid #f9f9f9;
}
#contenidor-contingut .thumbnail:hover {
	border: 2px solid #ddd;
	background-color: #f5f5f5;
	cursor: pointer;
}
#contenidor-contingut .thumbnail h4 {
	margin-top: 4px;
}
#contenidor-contingut .thumbnail a {
	text-decoration: none;
}
#contenidor-contingut .caption p {
	
}
#contenidor-contingut .caption .dropdown-menu {
	text-align: left;
}
#contenidor-contingut .caption .dropdown-menu li {
	width: 100%;
	margin: 0;
	padding: 0;
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

#tasca-info {
	margin-bottom: 10px;
}
#tasca-info dl {
	margin-bottom: 0px;
}
#tasca-info h3 {
	font-weight: bold;
	margin-top: 0;
	border-bottom: 1px solid #e3e3e3;
	padding-bottom: .6em;
	padding-bottom: 6px;
}
	
#tasca-info h4 {
	font-weight: bold;
	margin-top: 0;
	border-bottom: 1px solid #f5f5f5;
	padding-bottom: .6em;
}
#tasca-info dt {
	color: #999;
	font-size: small;
	font-style: italic;
	font-weight: normal;
}
#tasca-info #botons-accions-info button{
    padding: 1px 6px;
	margin-bottom: 4px;    
}
#tasca-info dd {
	font-size: medium;
	font-weight: bold;
	margin-bottom: 0.4em;
}
#contingut-botons {
	margin-bottom: .8em;
}
h4.interessats {
	padding-bottom: 0 !important;
	margin-bottom: 4px !important;
}
ul.interessats {
	padding-left: 1em !important;
}
.element-hover .thumbnail {
	border: 2px solid #ddd !important;
	background-color: #f5f5f5;
}
.right {
	float: right;
}
#nodeDades .form-group {
	margin-bottom: 6px;
}
#nodeDades input.form-control {
	width: 322px;
}
#nodeDades input.multiple {
	width: 280px;
	!
	important
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

.drag_activated {
	border: 4px dashed #ffd351;
	height: 200px;
	width: 100%;
	background-color: #f5f5f5;
	display: flex;
	justify-content: center;
	align-items: center;
	flex-direction: column;
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
.popover{
	max-width: none;
    z-index: 100;
    cursor: default;
	width: 500px;
}
.esborranys {
	text-align: center;
}
.esborranys.alert.alert-warning {
	color: #734b29;
	background-color: #ffab66;
	border-color: #ff8d30;
}
.icona-esborrany {
	color: #ffab66;
	font-size: 16px;
	vertical-align: text-top;
}
.definitiu.fa.fa-check-square {
	color: #02cda2;
	font-size: 16px;
	vertical-align: text-top;
}
.firmat.fa.fa-pencil-square {
	color: #02cda2;
	font-size: 16px;
	vertical-align: text-top;
}
.pendent.fa.fa-pencil-square {
	color: #67bdff;
	font-size: 16px;
	vertical-align: text-top;
}
.parcial.fa.fa-pencil-square {
	color: #FFAB66;
	font-size: 16px;
	vertical-align: text-top;
}
.error.fa.fa-pencil-square {
	color: #ffab66;
	font-size: 16px;
	vertical-align: text-top;
}
.pendent.fa.fa-envelope-square {
	color: #67bdff;
	font-size: 16px;
	vertical-align: text-top;
}
.enviada.fa.fa-envelope-square {
	color: #67bdff;
	font-size: 16px;
	vertical-align: text-top;
}
.processada.fa.fa-envelope-square {
	color: #02cda2;
	font-size: 16px;
	vertical-align: text-top;
}
.error.fa.fa-envelope-square {
	color: #ffab66;
	font-size: 16px;
	vertical-align: text-top;
}
.importat.fa.fa-info-circle {
	color: #02CDA2;
}
.dominis ~ span > .selection {
	width: 100%;
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


.viewer-content {
	width: 100%;
	padding-top: 1% !important;
}
.viewer-padding {
	padding: 0% 2% 0% 2%;
}

.rmodal_loading {
    background: rgba( 255, 255, 255, .8 ) 
                url('<c:url value="../img/loading.gif"/>') 
                50% 50% 
                no-repeat;
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
.ordre-col {
    cursor: move;
    vertical-align: middle !important;
}
.popover .close {
	position: relative;
	top: -3px;
}

.disabledMsg:hover {
    cursor: not-allowed;
}

#detallSignantsPreview .alert {
	padding: 10px 15px !important;
}
#detallSignantsPreview button.close-alertes {
    background: none repeat scroll 0 0 transparent;
    border: 0 none;
    cursor: pointer;
    padding: 0;
}
#detallSignantsPreview .close-alertes {
    color: #000000;
    float: right;
    font-weight: bold;
    opacity: 0.2;
    text-shadow: 0 1px 0 #FFFFFF;
}
#detallSignants .alert {
	padding: 10px 15px !important;
}
#detallSignants button.close-alertes {
    background: none repeat scroll 0 0 transparent;
    border: 0 none;
    cursor: pointer;
    padding: 0;
}
#detallSignants .close-alertes {
    color: #000000;
    float: right;
    font-weight: bold;
    opacity: 0.2;
    text-shadow: 0 1px 0 #FFFFFF;
}

.rowinfo-interessat h5 {
	font-weight: bold;
}
.interessaat-info dl {
	columns: 2;
	margin: 0;
}
.interessaat-info dl div {
	display: flex;
}
.interessaat-info dl div dt {
	text-align: right;
	width: 160px;
}
.interessaat-info dl div dd {
	grid-column: 2;
	margin-left: 10px;
}
.breadcrumb {
    float: left;
}
#contingut-botons #botons-accions-info {
	float: left;
}


</style>
<!-- edicioOnlineActiva currently doesnt exist in application --> 
<c:if test="${edicioOnlineActiva and contingut.document and contingut.metaNode.usuariActualWrite}">
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


<script>

var notificacioEnviamentEstats = new Array();
<c:forEach var="estat" items="${notificacioEnviamentEstats}">
notificacioEnviamentEstats["${estat.value}"] = "<spring:message code="${estat.text}"/>";
</c:forEach>

var registreTipusText = new Array();
<c:forEach var="option" items="${registreTipusEnumOptions}">
registreTipusText["${option.value}"] = "<spring:message code="${option.text}"/>";
</c:forEach>
var interessatTipusText = new Array();
<c:forEach var="option" items="${interessatTipusEnumOptions}">
interessatTipusText["${option.value}"] = "<spring:message code="${option.text}"/>";
</c:forEach>
var notificacioEstatText = new Array();
<c:forEach var="option" items="${notificacioEstatEnumOptions}">
	notificacioEstatText["${option.value}"] = "<spring:message code="${option.text}"/>";
</c:forEach>
var publicacioEstatText = new Array();
<c:forEach var="option" items="${publicacioEstatEnumOptions}">
publicacioEstatText["${option.value}"] = "<spring:message code="${option.text}"/>";
</c:forEach>

let pageSizeDominis = 20;
$(document).ready(function() {
	
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


	$('.disabledMsg').tooltip();
	
	removeLoading();
	$("a.fileDownload").on("click", function() {
		$("body").addClass("loading");
		checkLoadingFinished();
    });
	
	$("#document-new-empty-metadocuments").click(function(e){
	    alert("<spring:message code="contingut.document.alerta.max"/>");
	    e.preventDefault();
	});
	
	var iconaIdx = $('.esborranys > p').text().indexOf('(B)');
	if (iconaIdx != -1) {
		var newValidacioTxt = $('.esborranys > p').text().replace('B', '<i class="fa fa-bold" />');
		$('.esborranys > p').html(newValidacioTxt);
	} 
	$("#tascaBtn").appendTo(".panel-heading h2");
	<c:if test="${isTasca}"> $('title').html("Ripea - ${tascaNom}");</c:if>

	$("#mostraDetallSignants").click(function(){

		let contingutId = ${contingut.id}; 

		getDetallsSignants($('#detallSignants'), contingutId, false);
	});

	<c:if test="${contingut.arxiuUuid == null}">
		var arxiu = '<span class="fa fa-exclamation-triangle text-danger" title="<spring:message code="contingut.icona.estat.pendentGuardarArxiu"/>"></span>';
		$(".container-main .panel-heading h2").append(arxiu);
	</c:if>
	

	$('#contenidor-contingut li').mouseover(function() {
		$('a.btn', this).removeClass('hidden');
	});
	$('#contenidor-contingut li').mouseout(function() {
		$('a.btn', this).addClass('hidden');
	});
	$('#contenidor-info li a.confirm-delete').click(function() {
		return confirm('<spring:message code="contingut.confirmacio.esborrar.node"/>');
	});
	$('#contenidor-contingut li a.confirm-delete').click(function() {
		return confirm('<spring:message code="contingut.confirmacio.esborrar.node"/>');
	});
	$('li.element-contingut .caption p').each(function() {
		$clamp(this, {
			clamp: 2,
			useNativeClamp: true
		});
	});
	
	//ordenacio habilitada
	if (${isOrdenacioPermesa}) {
		$('.table-hover > tbody > tr > td:not(:last-child, :first-child, :nth-child(4), :nth-child(7))').css('cursor','pointer');
		$('.table-hover > tbody > tr > td:not(:last-child, :first-child, :nth-child(4), :nth-child(7))').click(function(event) {
			event.stopPropagation();
			$('a:first', $(this).parent())[0].click();
		});
	} else {
		//ordenacio per defecte (createdDate)
		$('.table-hover > tbody > tr > td:not(:last-child):not(:first-child, :nth-child(4))').css('cursor','pointer');
		$('.table-hover > tbody > tr > td:not(:last-child):not(:first-child, :nth-child(4))').click(function(event) {
			event.stopPropagation();
			$('a:first', $(this).parent())[0].click();
		});
	}
	$('ul.interessats li').hover(function() {
		$('a', this).removeClass('hidden');contingut
	},
	function() {
		$('a', this).addClass('hidden');
	});
	$('a.interessat-esborrar').click(function() {
		return confirm('<spring:message code="contingut.confirmacio.esborrar.interessat"/>');
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
	$('.element-draggable').draggable({
		containment: 'parent',
		helper: 'clone',
		revert: true,
		revertDuration: 200,
		opacity: 0.50,
		zIndex: 100,
		start: function() {
			$('div.element-noclick', this).addClass('noclick');
			$('div.element-noclick', this).tooltip('hide');
			$('div.element-noclick', this).tooltip('disable');
		},
		stop: function() {
			$('div.element-noclick', this).tooltip('enable');
		}
	});
	$('.element-droppable').children(":not('.ordre-col')").droppable({
		accept: '.element-draggable',
		tolerance: 'pointer',
		activeClass: 'element-target',
		hoverClass: 'element-hover',
		drop: function(event, ui) {
			showLoadingModal('<spring:message code="contingut.moure.processant"/>');
			var origenId = ui.draggable.data('contenidor-id');
			var destiId = $(this).parent().data('contenidor-id');
			window.location = origenId + "/moure/" + destiId;
			dropped = true;
            $(event.target).addClass('dropped');
		}
	});
	
	$('.ordre-col').on('mouseover', function() {
		$('.element-draggable').draggable({ disabled: true });
		$('.element-draggable').droppable({ disabled: true });
		$('#table-documents tbody').sortable({
			handle: ".ordre-col",
			refreshPositions: true,
            helper : 'clone',
			cursor: "move",
			cursorAt: { left: 5 },
			opacity: 0.65,
			placeholder: "sortable-dest",
			start: function (event, ui) {
				$(this).attr('data-previndex', ui.item[0].rowIndex);
	        },
	        update: function (event, ui) {
				//showLoadingModal('<spring:message code="contingut.moure.processant"/>');
				var tableDocuments = document.getElementById('table-documents');
				$(tableDocuments).addClass("disabled");
	            $('#loading').removeClass('hidden');
				var idsInOrder = $('#table-documents tbody').sortable("toArray", {attribute: 'data-contenidor-id'});
	            var filtered = idsInOrder.filter(function (el) {
	           		return el != '';
	            });
	            var orderedElements = new Map();
				var idx = 1;
	            filtered.forEach(function(row) {
	            	orderedElements[idx] = row;
		            idx++;
	            });

	            $.ajax({
			        url: '<c:url value="/contingut/${expedientPare.id}/ordenar"/>',
			        type: "POST",
			        contentType: "application/json",
			        data: JSON.stringify(orderedElements),
			        success: function (data) {
			        	location.reload();
			        }
				});
	        }
	    }).disableSelection();
	});
	
	$('.ordre-col').on('mouseleave', function() {
		$('.element-draggable').draggable("enable");
		$('.element-draggable').droppable("enable");
	});
	
	var nodeDadesInputChange = function() {
		var $pare = $(this).parent();
		$pare.removeClass('has-success');
		$pare.removeClass('has-warning');
		$pare.removeClass('has-error');
		$pare.addClass('has-warning has-feedback');
		$(this).next().removeClass().addClass('glyphicon glyphicon-pencil form-control-feedback');
		$(this).attr('title', 'Valor modificat pendent de guardar');
	}
	
	function setCheckboxFalse($checkbox, isDisabled)
	{
		var hiddenCheckbox = $checkbox.clone(true);
		hiddenCheckbox.attr('type', 'hidden');
		hiddenCheckbox.attr('value', 'false');
		hiddenCheckbox.removeAttr('data-toggle');
		hiddenCheckbox.insertAfter($checkbox);
		if (!isDisabled)
			hiddenCheckbox.removeAttr('disabled')
		
		$checkbox.removeAttr('checked')
		$checkbox.attr('value', 'false');
	}
	
	function setCheckboxTrue($checkbox)
	{
		$checkbox.attr('value', 'true');
		$checkbox.attr('checked', 'checked');
		console.log("set to true");
		console.log($checkbox.next());
		$checkbox.next().remove();
	}
	
	$('input[data-toggle="checkbox"]', this).each(function() {
		$(this).attr('type', 'checkbox');
		var isDisabled = $(this).closest('div.form-group').data('toggle') == "multifield";
		if($(this).attr('value') == 'true'){
			$(this).attr('checked', 'checked');
			
		}else{
			setCheckboxFalse($(this), isDisabled);
		}
	});
	
	$('form#nodeDades').on('change', 'input[data-toggle="checkbox"]', function() {
		if($(this).attr('value') == 'true'){
			setCheckboxFalse($(this), false);
			
		} else{
			setCheckboxTrue($(this));
		}
		
	});
	
	$('form#nodeDades').on('DOMNodeInserted', 'div[data-multifield-clon="true"]', function () {
		$(this).find('input').prop('disabled', '');
	});
	
	$('#nodeDades input').change(nodeDadesInputChange);
	$('#dades').on('submit', 'form#nodeDades', function() {
		showLoadingModal('<spring:message code="contingut.dades.form.processant"/>');
		$.post(
				'../ajax/contingutDada/${contingut.expedient ? contingut.id : contingut.expedientPare.id}/save',
				$(this).serialize(),
				function (data) {
					if (data.estatOk) {
						$('#nodeDades input').each(function() {
							var $pare = $(this).parent();
							if ($pare.hasClass('has-warning') || $pare.hasClass('has-error')) {
								$pare.removeClass('has-success');
								$pare.removeClass('has-warning');
								$pare.removeClass('has-error');
								$pare.addClass('has-success has-feedback');
								$(this).next().removeClass().addClass('glyphicon glyphicon-ok form-control-feedback');
								$(this).attr('title', 'Valor guardat correctament');
							} else {
								$pare.removeClass('has-success');
								$pare.removeClass('has-feedback');
								$(this).next().removeClass();
								$(this).removeAttr('title');
							}
						});
// 						$.get(
// 								'../ajax/contingutDada/${contingut.id}/count',
// 								function (data) 
<%-- 									<meta name="subtitle" content="${serveiPerTitol}"/>{ --%>
// 									$('#dades-count').html(data);
// 								});
						
					} else {
						$('#nodeDades input').each(function() {
							for (var i = 0; i < data.errorsCamps.length; i++) {
								var error = data.errorsCamps[i];
								if (error.camp == $(this).attr('name')) {
									var $pare = $(this).parent();
									$pare.removeClass('has-success');
									$pare.removeClass('has-warning');
									$pare.removeClass('has-error');
									$pare.addClass('has-error has-feedback');
									$(this).next().removeClass().addClass('glyphicon glyphicon-warning-sign form-control-feedback');
									$(this).attr('title', error.missatge);
									break;
								}
							}
						});
					}
// 					webutilRefreshMissatges();
					location.reload();
				});
		return false;
	});
	$('form#nodeDades td .form-group').on('clone.multifield', function(event, clon) {
		$('input', clon).change(nodeDadesInputChange);
// 		var url = '<c:url value="/contingutDada/' + $('#contingutId').val() + '/' + $('input', clon).attr('id') + '"/>';
// 		$.ajax({
// 	        type: "GET",
// 	        url: url,
// 	        success: function (result) {
// 	        	$('input', clon).val(result);
// 	        	$('input', clon).trigger("focusout");
// 	        }
// 		});
	});
	if (${pipellaAnotacionsRegistre}) {
		$('#contingut').removeClass( "active in" );
		$('#peticions').addClass( "active in" );
		$('#pipella-contingut').removeClass( "active" );
		$('#pipella-peticions').addClass( "active" );
	}
	$('#drag_container').filedrop({
		maxfiles: 1,
		fallback_dropzoneClick : false,
		error: function(err, file) {
			switch(err) {
			case 'BrowserNotSupported':
				alert('browser does not support HTML5 drag and drop')
				break;
			case 'TooManyFiles':
				alert('Només es pemet adjuntar un document a la vegada!')
				break;
			case 'FileTooLarge':
				break;
			case 'FileTypeNotAllowed':
				break;
			case 'FileExtensionNotAllowed':
				break;
			default:
				break;
			}
		},
		dragOver: function() {
			$('#drag_container').css('background-color', '#e6e3e3');
		},
		dragLeave: function() {
			$('#drag_container').css('background-color', '#f5f5f5');
		},
		drop: function(e) {
			if (e.originalEvent.dataTransfer != null) {
				let files = e.originalEvent.dataTransfer.files;
				$('#drag_container').css('background-color', '#f5f5f5');
				if (!(files.length > 1)) {
					document.querySelector('#dropped-files').files = files;
					$('#document-new').trigger('click');
				}
			}
		}
	});

	var tableDocuments = document.getElementById('table-documents');

	<c:if test="${vistaIcones}">
		var checkItAll = document.getElementById('checkItAll');
		$('.checkItAll').addClass('disabled');
	</c:if>

	<c:if test="${!contingut.document}"> 
	$('#habilitar-mult').on('click', function() {
		var contenidorContingut = document.getElementById('contenidor-contingut');
		var inputs = contenidorContingut.querySelectorAll('li>div');
		
		if ($(contenidorContingut).hasClass('multiple')) {
			<c:if test="${vistaIcones}">
				$('.checkItAll').addClass('disabled');
			</c:if>
			$(contenidorContingut).removeClass('multiple');
			$(this).removeClass('active');
			//Inicialitzar contador i array
			docsIdx = [];
			var multipleUrl = '<c:url value="/contingut/${contingut.id}/inicialitzar/seleccio"/>';
			$.get(
					multipleUrl,
					function(data) {
						$(".seleccioCount").html(data);
					}
			);
			enableDisableButton();
			inputs.forEach(function(element) {
				if ($(element).hasClass('selectd')) {
					$(element).removeClass('selectd');
				}
			}); 
		} else {
			<c:if test="${vistaIcones}">
				$('.checkItAll').removeClass('disabled');
			</c:if>
			$(contenidorContingut).addClass('multiple');
			$(this).addClass('active');
		}
	});
	
	enableDisableButton();
	if (tableDocuments != null) {
		//Vista llista
		var inputs = tableDocuments.querySelectorAll('tbody>tr>td>input');
		if (typeof checkItAll !== 'undefined') {
			checkItAll.addEventListener('change', function() {
				if (checkItAll.checked) {
					inputs.forEach(function(input) {
						/*var comprovacioUrl = '<c:url value="/contingut/${contingut.id}/comprovarContingut/' + input.id + '"/>';
						$.ajax({
					        type: "GET",
					        url: comprovacioUrl,
					        success: function (isDocument) {*/
					    input.checked = true;
						var index = docsIdx.indexOf(parseInt(input.id));
						if (index < 0) {
							docsIdx.push(parseInt(input.id));
						}
						enableDisableButton();
						selectAll();
					        /*}
						});*/
				    });  
				} else {
					inputs.forEach(function(input) {
						input.checked = false;
						var index = docsIdx.indexOf(parseInt(input.id));
						if (index > -1) {
							docsIdx.splice(index, 1);
						}
				    });  
					enableDisableButton();
					deselectAll();
				}
			});
		}
	} else {
		//Vista icones
		$(checkItAll).on('click', function(){
			var listDocuments = document.getElementById('contenidor-contingut');
			var elements = listDocuments.querySelectorAll('li>div');
			$(checkItAll).toggleClass('active');

			if ($(checkItAll).hasClass('active') && $(listDocuments).hasClass('multiple')) {
				elements.forEach(function(child) {
					var childParent = $(child.parentElement);
					var isCarpeta = childParent.hasClass('element-droppable');
					if (!isCarpeta) {
						$(child).addClass('selectd');
						var index = docsIdx.indexOf(parseInt(child.id));
						if (index < 0) {
							docsIdx.push(parseInt(child.id));
						}
					}
				}); 
				enableDisableButton();
				selectAll();
			} else if ($(listDocuments).hasClass('multiple')) {
				elements.forEach(function(input) {
					$(input).removeClass('selectd');
					var index = docsIdx.indexOf(parseInt(input.id));
					if (index > -1) {
						docsIdx.splice(index, 1);
					}
			    });  
				enableDisableButton();
				deselectAll();
			}
		});
	}
	</c:if>
	
	$("span[class*='popover-']").popover({
		html: true,
	    content: function () {
	    	return getEnviamentsDocument($(this));   
	  	}
	}).on('mouseenter', function () {
	    $(this).popover("show");
	   
	    $(".popover").on('mouseleave', function () {
	        $(this).popover('hide');
	    });
	}).on('mouseleave', function () {
	   	if (!$('.popover:hover').length) {
	    	$(this).popover('hide');
	    }
	});
	
	//======================= enviament list on clicking desplegable in notificacio table =============================
	$('#taulaInteressats').on('rowinfo.dataTable', function(e, td, rowData) {
		var getUrl = "<c:url value="/expedient/interessat/"/>" + rowData.id + "?dadesExternes=true";
		$.get(getUrl).done(function(data) {
			$(td).empty();
			var rowinfo_content = 
				    			'<div class="rowinfo-interessat">' + 
								'<h5><spring:message code="contingut.interessat.info.interessat"/>:</h5>' + 
				    			'<div class="interessaat-info">' +
				    				'<dl>' +
				    					showInteressatFieldInfo('<spring:message code="contingut.interessat.info.pais"/>', data.paisNom) +
				    					showInteressatFieldInfo('<spring:message code="contingut.interessat.info.email"/>', data.email) +
				    				'</dl>' +
					    			'<dl>' +
					    				showInteressatFieldInfo('<spring:message code="contingut.interessat.info.provincia"/>', data.provinciaNom) +
					    				showInteressatFieldInfo('<spring:message code="contingut.interessat.info.telefon"/>', data.telefon) +
								    '</dl>'+
								    '<dl>' +
								    	showInteressatFieldInfo('<spring:message code="contingut.interessat.info.municipi"/>', data.municipiNom) +
				    					showInteressatFieldInfo('<spring:message code="contingut.interessat.info.observacions"/>', data.observacions) +
							    	'</dl>'+
							    	'<dl>' +
							    		showInteressatFieldInfo('<spring:message code="contingut.interessat.info.adresa"/>', data.adresa) +
									'</dl>'+
									'<dl>' +
										showInteressatFieldInfo('<spring:message code="contingut.interessat.info.codipostal"/>', data.codiPostal) +
									'</dl>'+
								'</div>';
								if (data.representant) {
									rowinfo_content += '<h5><spring:message code="contingut.interessat.info.representant"/>:</h5>' +
														'<div class="interessaat-info">' +
										    				'<dl>' +
										    					showInteressatFieldInfo('<spring:message code="contingut.interessat.info.pais"/>', data.representant.paisNom) +
										    					showInteressatFieldInfo('<spring:message code="contingut.interessat.info.email"/>', data.representant.email) +
										    				'</dl>' +
											    			'<dl>' +
											    				showInteressatFieldInfo('<spring:message code="contingut.interessat.info.provincia"/>', data.representant.provinciaNom) +
											    				showInteressatFieldInfo('<spring:message code="contingut.interessat.info.telefon"/>', data.representant.telefon) +
														    '</dl>'+
														    '<dl>' +
														    	showInteressatFieldInfo('<spring:message code="contingut.interessat.info.municipi"/>', data.representant.municipiNom) +
										    					showInteressatFieldInfo('<spring:message code="contingut.interessat.info.observacions"/>', data.representant.observacions) +
													    	'</dl>'+
													    	'<dl>' +
													    		showInteressatFieldInfo('<spring:message code="contingut.interessat.info.adresa"/>', data.representant.adresa) +
															'</dl>'+
															'<dl>' +
																showInteressatFieldInfo('<spring:message code="contingut.interessat.info.codipostal"/>', data.representant.codiPostal) +
															'</dl>'+
														'</div>';
								}
								rowinfo_content += '</div>';
	    	$(td).append(rowinfo_content);
		});
	});
	//======================= enviament list on clicking desplegable in notificacio table =============================
	$('#taulaNotificacions').on('rowinfo.dataTable', function(e, td, rowData) {
		var getUrl = "<c:url value="/expedient/${contingut.id}"/>" + "/enviaments/" + rowData.id;
	    $.get(getUrl).done(function(data) {
			var notificacio = data;
		    var enviaments = notificacio.documentEnviamentInteressats;
	    	$(td).empty();
	    	$(td).append(
	    			'<table class="table teble-striped table-bordered">' +
	    			'<caption><spring:message code="notificacio.list.enviament.list.titol"/></caption>' +
	    			'<thead>' +
	    			'<tr>' +
					'<th><spring:message code="notificacio.list.enviament.list.titular"/></th>' + 
	    			'<th><spring:message code="notificacio.list.enviament.list.representants"/></th>' +
	    			'<th><spring:message code="notificacio.list.enviament.list.estat"/></th>' +
	    			'<th></th>' +
	    			'</tr>' +
					'</thead><tbody></tbody></table>');

	    	var tableBody = '';
			for (i = 0; i < enviaments.length; i++) {
				tableBody += '<tr>';
				
				//====== titular/interessat ========
				var nomTitular = '',
				    llinatge1 = '',
				    llinatge2 = '',
				    nif = '';
				if (enviaments[i].interessat.nom != null) {
				    nomTitular = enviaments[i].interessat.nom;
				} else if (enviaments[i].interessat.raoSocial != null) {
				    nomTitular = enviaments[i].interessat.raoSocial;
				} else {
				    nomTitular = enviaments[i].interessat.organNom;
				}
				if (enviaments[i].interessat.llinatge1 != null) {
				    llinatge1 = enviaments[i].interessat.llinatge1;
				}
				if (enviaments[i].interessat.llinatge2 != null) {
				    llinatge2 = enviaments[i].interessat.llinatge2;
				}
				if (enviaments[i].interessat.nif != null) {
				    nif = enviaments[i].interessat.nif;
				} else if (enviaments[i].interessat.dir3Codi != null) {
				    nif = enviaments[i].interessat.dir3Codi;
				} else {
				    nif = enviaments[i].interessat.documentNum;
				}
				tableBody += '<td>' + nomTitular + ' ' + llinatge1 + ' ' + llinatge2 + '('+ nif +') </td>';


				//====== destinatari/representant ========
				var representants = '';
				var representant = enviaments[i].interessat.representant;
				if (representant) {
				    var nomDest = '',
				        llinatge1Dest = '',
				        llinatge2Dest = '',
				        nifDest = '';
				    if (representant.nom != null) {
				        nomDest = representant.nom;
				    } else if (representant.raoSocial != null) {
				        nomDest = representant.raoSocial;
				    } else {
				        nomDest = representant.organNom;
				    }
				    if (representant.llinatge1 != null) {
				        llinatge1Dest = representant.llinatge1;
				    }
				    if (representant.llinatge2 != null) {
				        llinatge2Dest = representant.llinatge2;
				    }
				    if (representant.nif != null) {
				        nifDest = representant.nif;
				    } else if (representant.dir3Codi != null) {
				        nifDest = representant.dir3Codi;
				    } else {
				        nifDest = representant.documentNum;
				    }
				    representants += nomDest + ' ' + llinatge1Dest + ' ' + llinatge2Dest + ' (' + nifDest + '), ';
				}
				if (representants != ''){
					//Remove last white space
					representants = representants.substr(0, representants.length-1);
					//Remove last comma
					representants = representants.substr(0, representants.length-1);
				} else {
					representants = '<spring:message code="notificacio.list.enviament.list.senserepresentants"/>';
				}
				tableBody += '<td>' + representants + '</td>';

				
				//============== estat/errors =========================
				tableBody += '<td>';
				tableBody += (enviaments[i].enviamentDatatEstat) ? notificacioEnviamentEstats[enviaments[i].enviamentDatatEstat] : '';
				if (notificacio.error) {
					var errorTitle = '';
					if (notificacio.errorDescripcio) {
						errorTitle = notificacio.errorDescripcio;
					}
					var escaped = notificacio.errorDescripcio.replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;');
					tableBody += ' <span class="fa fa-warning text-danger" title="' + escaped + '"></span>';
				}
				tableBody += '</td>';

				
				//============== buttons =========================
				tableBody += '<td width="114px">';
// 				if (enviaments[i].notificaCertificacioData != null) {
// 					tableBody += '<a href="<c:url value="/notificacio/' + rowData.id + '/enviament/' + enviaments[i].id + '/certificacioDescarregar"/>" class="btn btn-default btn-sm fileDownloadSimpleRichExperience" title="<spring:message code="enviament.info.accio.descarregar.certificacio"/>"><span class="fa fa-download"></span></a>';
// 				} else if (enviaments[i].notificacio.estat == 'REGISTRADA' &&
// 						(enviaments[i].registreEstat == 'DISTRIBUIT' || enviaments[i].registreEstat == 'OFICI_EXTERN' || enviaments[i].registreEstat == 'OFICI_SIR')) {
// 					tableBody += '<a href="<c:url value="/notificacio/' + rowData.id + '/enviament/' + enviaments[i].id + '/justificantDescarregar"/>" class="btn btn-default btn-sm fileDownloadSimpleRichExperience" title="<spring:message code="enviament.info.accio.descarregar.justificant"/>"><span class="fa fa-download"></span></a>';
// 				}
 				tableBody += '<a href="<c:url value="/expedient/${contingut.id}/enviamentDetails/' + rowData.id + '/enviamentInfo/' + enviaments[i].id + '"/>" data-toggle="modal" class="btn btn-default btn-sm"><span class="fa fa-info-circle"></span>&nbsp;&nbsp;<spring:message code="comu.boto.detalls"/></a>';
				tableBody += '</td>';
				tableBody += '</tr>';
			}
			
			$('table tbody', td).append(tableBody);
			$('table tbody td').webutilModalEval();
		});
	});
	
	
	//=== assignar tipus document a document ====
	var selTipusDocument = $('.select-tipus-document');
	var select2Options = {
			theme: 'bootstrap', 
			width: 'auto', 
			minimumResultsForSearch: "0"};
	selTipusDocument.select2(select2Options);
	
	selTipusDocument.on('change', function(event){

		var tipusDocumentId = $(':selected', $(this)).attr('id');
		if (tipusDocumentId) {
			showLoadingModal('<spring:message code="contingut.info.document.tipusdocument.processant"/>');
			var documentId = $(this).attr('id');
			
			var updateUrl = '<c:url value="/contingut/' + documentId + '/document/updateTipusDocument/"/>' + tipusDocumentId;
			$.ajax({
				type: 'GET',
		        url: updateUrl,
		        success: function(json) {
		        	if (json.error) {
		        		$('div.modal').modal('hide');
						$('#contingut-missatges').append('<div class="alert alert-danger"><button type="button" class="close-alertes" data-dismiss="alert" aria-hidden="true"><span class="fa fa-times"></span></button>' + 'Hi ha hagut un error actualitzant el document amb el nou tipus de document: ' + json.errorMsg + '</div>');
		        	} else {
						location.reload();
					}
		        },
		        error: function(e) {
		        	alert("Hi ha hagut un error actualitzant el document amb el nou tipus de document");
		        	location.reload();
		        }
		    });	
		}
	});

						
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
	
	//### canvi tipus documental massiu
	var $botoTipusDocumental = $('#tipusdocumental-mult');
	$botoTipusDocumental.on('click', function() {
		$botoTipusDocumental.popover("show");
	});
		
	$botoTipusDocumental.popover({
		html: true,
		placement: 'bottom',
		title: '<spring:message code="massiu.canvi.tipus.document.select"/> <a href="#" class="close" data-dismiss="alert">&times;</a>',
	    content: function () {
	    	return showTipusDocumentals($(this));   
	  	}
	}).on('shown.bs.popover', function () {
		var $selTipusDocument = $('.select-tipus-massiu');
		var select2Options = {
				theme: 'bootstrap', 
				width: 'auto', 
				minimumResultsForSearch: "0"};
		$selTipusDocument.select2(select2Options);
		$selTipusDocument.on('change', function(event) {
			var tipusDocumentId = $(':selected', $(this)).attr('id');
			if (tipusDocumentId) {
				showLoadingModal('<spring:message code="contingut.info.document.tipusdocument.massiu.processant"/>');				
				var updateUrl = '<c:url value="/contingut/updateTipusDocumentMassiu/"/>' + tipusDocumentId;
				$.ajax({
					type: 'GET',
			        url: updateUrl,
			        success: function(json) {
			        	if (json.error) {
			        		$('div.modal').modal('hide');
							$('#contingut-missatges').append('<div class="alert alert-danger"><button type="button" class="close-alertes" data-dismiss="alert" aria-hidden="true"><span class="fa fa-times"></span></button>' + 'Hi ha hagut un error actualitzant el document amb el nou tipus de document: ' + json.errorMsg + '</div>');
			        	} else {
							location.reload();
						}
			        },
			        error: function(e) {
			        	alert("Hi ha hagut un error actualitzant algún dels documents seleccionats amb el nou tipus de document");
			        	location.reload();
			        }
			    });	
			}
		});
		
		var $popoverClose = $('.popover .close');
		$popoverClose.on('click', function() {
			$botoTipusDocumental.popover('hide');
		});
	});
	$(window).on('load', function() {
		var multipleUrl = '<c:url value="/contingut/${contingut.id}/inicialitzar/seleccio"/>';
		$.get(
				multipleUrl,
				function(data) {
					$(".seleccioCount").html(data);
				}
		);
	});
	
});

function showInteressatFieldInfo(fieldName, fieldValue) {
	if (fieldValue) {
		return '<div>' +
					'<dt>' + fieldName + '</dt>' +
					'<dd>' + fieldValue + '</dd>' +
			   '</div>';
	} else {
		return '';
	}
}

function showTipusDocumentals() {
	var content = '<div> \
						<select id="selectTipusMassiu" class="select-tipus-massiu"> \
							<option value=""><spring:message code="contingut.document.form.camp.nti.cap"/></option> \
								<c:forEach items="${metaDocumentsLeft}" var="metaDocument"> \
									<option id="${metaDocument.id}"> \
									${fn:escapeXml(metaDocument.nom)} \
									</option> \
								</c:forEach> \
						</select> \
				   <div>';
	return content;
}

function getEnviamentsDocument(document) {
	var content;
	var documentId = $(document).attr('id');
	if (documentId != undefined) {
		var enviamentsUrl = '<c:url value="/document/' + documentId + '/enviament/datatable"/>';
		$.ajax({
			type: 'GET',
	        url: enviamentsUrl,
	        async: false,
	        success: function(data){
	        	if (data && data.length > 0) {
	        	content =  "<table data-toggle='datatable' class='table table-bordered table-striped' style='width:100%'>";
	            content += "<thead>";
	        	content += "<tr>";
	        	content += "<th> <spring:message code='contingut.enviament.columna.tipus'/> </th>";
	        	content += "<th> <spring:message code='contingut.enviament.columna.data'/> </th>";
	        	content += "<th> <spring:message code='contingut.enviament.columna.estat'/> </th>";
	        	content += "</tr>";
	        	content += "</thead>";
		            $.each(data, function(i, val) {
		            	content += "<tbody>";
		             	content += "<tr>";
		             	content += "<td width='25%'>";
		             	if (val.tipus == "NOTIFICACIO") {
		             		content += "<spring:message code='contingut.enviament.notificacio.elec'/>";
		             	} else if (val.tipus == "COMUNICACIO") {
		             		content += "<spring:message code='contingut.enviament.comunicacio'/>";
		             	}
		             	content += "</td>";
		             	content += "<td width='20%'>" + new Date (val.createdDate).toLocaleString() + "</td>";
		             	content += "<td width='10%'>";
		             	if (val.notificacioEstat == 'PENDENT') {
		             		content += "<span class='label label-warning'><span class='fa fa-clock-o'></span> ";
		             		content += "<spring:message code='notificacio.notificacioEstat.enum.PENDENT'/></span> ";
		             		if (val.error) {
		             			content += "<span class='fa fa-warning text-danger' title='<spring:message code='contingut.enviament.error'/>'></span>";
		             		}
		             	} else if (val.notificacioEstat == 'ENVIADA') {
		             		content += "<span class='label label-info'><span class='fa fa-envelope-o'></span> ";
		             		content += "<spring:message code='notificacio.notificacioEstat.enum.ENVIADA'/></span>";
		             		if (val.error) {
		             			content += "<span class='fa fa-warning text-danger' title='<spring:message code='contingut.enviament.error'/>'></span>";
		             		}
		             	} else if (val.notificacioEstat == 'REGISTRADA') {
		             		content += "<span class='label label-success'><span class='fa fa-check'></span> ";
		             		content += "<spring:message code='notificacio.notificacioEstat.enum.REGISTRADA'/></span>";
		             	} else if (val.notificacioEstat == 'FINALITZADA') {
		             		content += "<span class='label label-success'><span class='fa fa-check'></span> ";
		             		content += "<spring:message code='notificacio.notificacioEstat.enum.FINALITZADA'/></span>";
		             		if (val.error) {
		             			content += "<span class='fa fa-warning text-danger' title='<spring:message code='contingut.enviament.error'/>'></span>";
		             		}
		             	} else if (val.notificacioEstat == 'PROCESSADA') {
		             		content += "<span class='label label-success'><span class='fa fa-check'></span> ";
		             		content += "<spring:message code='notificacio.notificacioEstat.enum.PROCESSADA'/></span>";
		             		if (val.error) {
		             			content += "<span class='fa fa-warning text-danger' title='<spring:message code='contingut.enviament.error'/>'></span>";
		             		}
		             		content += "<p class='estat_" + val.id +  "' style='display:inline'></p>";
		             		returnEnviamentsStatusDiv(val.id);
		             	}
		             	content += "</td>";
		             	content += "</tr>";
		             	content += "</tbody>";
		            });
	            content += "</table>";
	        	}
	        }
	    });	
	    return content;
	}
}

<c:if test="${!contingut.document}"> 
function enableDisableButton() {
	var isTotPdfFirmat = true;
	var isTotPdf = true;
	var isTotDocAdjuntGuardatEnArxiu = true;

	var comprovacioUrl = '<c:url value="/contingut/${contingut.id}/comprovarContingut"/>';
	$('#loading').removeClass('hidden');
	//lista
	var tableDocuments = document.getElementById('table-documents');
	$(tableDocuments).addClass("disabled");
	//icones
	var gridDocuments = document.getElementById('contenidor-contingut');
	$(gridDocuments).addClass("disabled");

		if (docsIdx != undefined && tableDocuments != undefined) {
			var inputs = tableDocuments.querySelectorAll('tbody>tr>td>input');
			inputs.forEach(function(input) {
				var documentId = parseInt(input.id);
				if (docsIdx.includes(documentId)) {
					var isFirmatCurrentDocument = $(input.closest('tr')).hasClass('firmat');
					var isPdfCurrentDocument = $(input.closest('tr')).hasClass('isPdf');
					var isDocAdjuntPendentGuardarArxiu = $(input.closest('tr')).hasClass('docAdjuntPendentGuardarArxiu');
					if (isDocAdjuntPendentGuardarArxiu) {
						isTotDocAdjuntGuardatEnArxiu = false;
					}
					
					if (!isFirmatCurrentDocument) {
						isTotPdfFirmat = false;
						return false;
					}
					if (!isPdfCurrentDocument) {
						isTotPdf = false;
					}
				}
			});

		} else if (docsIdx != undefined && gridDocuments != undefined) {
			var list = gridDocuments.querySelectorAll('li');
			list.forEach(function(child) {
				var childId = $(child).attr('data-contenidor-id');
				var documentId = parseInt(childId);
				if (docsIdx.includes(documentId)) {
					var isFirmatCurrentDocument = $(child).hasClass('firmat');
					var isPdfCurrentDocument = $(child).hasClass('isPdf');
					var isDocAdjuntPendentGuardarArxiu = $(child).hasClass('docAdjuntPendentGuardarArxiu');
					if (isDocAdjuntPendentGuardarArxiu) {
						isTotDocAdjuntGuardatEnArxiu = false;
					}
					
					if (!isFirmatCurrentDocument) {
						isTotPdfFirmat = false;
						return false;
					}
					if (!isPdfCurrentDocument) {
						isTotPdf = false;
					}
				}
			});
		}		

	
	if (isTotPdfFirmat && isTotPdf) {
		$('.nomaximized').addClass('hidden'); //zip
		$('.maximized').removeClass('hidden'); //concatenació
		$('#notificar-mult').removeClass("disabled");
		$('#definitiu-mult').addClass("disabled");
	} else if (isTotPdfFirmat && !isTotPdf) {
		$('.nomaximized').removeClass('hidden'); //zip
		$('.maximized').addClass('hidden'); //concatenació
		$('#notificar-mult').removeClass("disabled");
		$('#definitiu-mult').addClass("disabled");
	} else if (!isTotDocAdjuntGuardatEnArxiu) {
		$('#definitiu-mult').addClass("disabled");
		$('#tipusdocumental-mult').addClass("disabled");
	} else {
		$('#notificar-mult').addClass("disabled");
		$('#definitiu-mult').removeClass("disabled");
		$('#tipusdocumental-mult').removeClass("disabled");
	}
	
		if (docsIdx.length > 0) {
			$('#descarregar-mult').removeClass("disabled");
			$('#moure-mult').removeClass("disabled");
			$('#tipusdocumental-mult').removeClass("disabled");
		} else {
			$('#descarregar-mult').addClass("disabled");
			$('#notificar-mult').addClass("disabled");
			$('#moure-mult').addClass("disabled");
			$('#tipusdocumental-mult').addClass("disabled");
		}
	$('#contenidor-contingut ').removeClass("disabled");
	$('#table-documents').removeClass("disabled");
	$('#loading').addClass('hidden');
}

function selectAll() {
	var multipleUrl = '<c:url value="/contingut/${contingut.id}/select"/>';
	$.get(
			multipleUrl, 
			{docsIdx: docsIdx},
			function(data) {
				$(".seleccioCount").html(data);
			}
	);
}

function deselectAll() {
	var multipleUrl = '<c:url value="/contingut/${contingut.id}/deselect"/>';
	$.get(
			multipleUrl, 
			{docsIdx: docsIdx},
			function(data) {
				$(".seleccioCount").html(data);
			}
	);
}
</c:if>


function recuperarResultatDomini(
		metaExpedientId,
		metaDadaCodi,
		dadaValor) {
	var dadaValorUrl = '<c:url value="/metaExpedient/metaDada/domini/' + metaDadaCodi + '/valor"/>';
	var multipleUrl = '<c:url value="/metaExpedient/metaDada/domini/' + metaDadaCodi + '"/>';
	var selDomini = $("#" + metaDadaCodi);
	

	if (dadaValor != '') {
		$.ajax({
	        type: "GET",
	        url: dadaValorUrl,
	        data: {dadaValor: dadaValor},
	        success: function (resultat) {
	        	var newOption = new Option(resultat.text, resultat.id, false, false);
	        	selDomini.append(newOption);
	        	selDomini.val(resultat.id).trigger('change');
			}
	    });
	}
	
	selDomini.empty();
	var select2Options = {
			language: "${requestLocale}",
	        theme: 'bootstrap',
			allowClear: true,
	        ajax: {
	            url: multipleUrl,
	            dataType: 'json',
	            delay: 250,
                global: false,
	            data: function (params) {
	                params.page = params.page || 1;
	                return {
	                	filter: params.term ? params.term : '',
	                    pageSize: pageSizeDominis,
	                    page: params.page
	                };
	            },
	            processResults: function (data, params) {
	                params.page = params.page || 1;
	                var dominis = [];
	                // empty option
	                dominis.push({
                        id: "", 
                        text: ""
                    })
	                for (let i = 0; i < data.resultat.length; i++) {
	                	dominis.push({
	                        id: data.resultat[i].id, 
	                        text: data.resultat[i].text
	                    })
	                }
	                return {
	                    results: dominis,
	                    pagination: {
	                        more: params.term ? (params.page * data.totalElements < data.totalElements) : ((params.page * pageSizeDominis < data.totalElements) || (data.resultat.length > 0))
	                    }
	                };
	            },
	            cache: true
	        },
	        width: '100%',
	        minimumInputLength: 0
    };
	selDomini.select2(select2Options);
}

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

function modalCloseLoadingHandler() {
	$('body').addClass('loading');
}

function addLoading(idModal) {
	$('#' + idModal).on('hidden.bs.modal', modalCloseLoadingHandler)
}

function removeLoading(idModal) {
	if (idModal) {
		$('#' + idModal).off('hidden.bs.modal', modalCloseLoadingHandler)
	} else {
		$('body').removeClass('loading');
	}
}

function removeTransactionId(idModal) {
	if (idModal) {
		$('#' + idModal).on('hidden.bs.modal', function() {
			var idTransaccio = localStorage.getItem('transaccioId');
			if (idTransaccio) {
				$.ajax({
			    	type: 'GET',
					url: "<c:url value='/document/portafirmes/tancarTransaccio/" + idTransaccio + "'/>",
					success: function() {
						localStorage.removeItem('transaccioId');
					},
					error: function(err) {
						console.log("Error tancant la transacció");
					}
			    });
			}
		});
	}
}

function modalLoading(modalDivId, modalData, message){
	return  '<div id="' + modalDivId + '"' + modalData + '>' +
			'	<div class="modal" tabindex="-1" role="dialog" aria-labelledby="" aria-hidden="true">' +
			'		<div class="modal-dialog modal-sm" role="document">' +
		    '			<div class="modal-content" style="border-radius: 0px;box-shadow: 0 0 20px 8px rgba(0, 0, 0, 0.7);">' +
		    '				<div class="modal-body text-center">' +
		    '					<div class="loader"></div>' +
			'					<div clas="loader-txt">' +
			'						<p>' + message + '</p>' +
			'					</div>' +
			'				</div>' +
		    '			</div>' +
			'		</div>' +
			'	</div>' +
			'</div>';
}
function showLoadingModal(message) {
	var modalDivId = "modalLoading";
	
	modalData = "";
	if ($('#' + modalDivId).length == 0 ) {
		$('body').append(modalLoading(modalDivId, modalData, message));
	} 
	var modalobj = $('#' + modalDivId + ' > div.modal');
	modalobj.modal({
	      backdrop: "static", //remove ability to close modal with click
	      keyboard: false, //remove option to close with keyboard
	      show: true //Display loader!
	    });
}




// ------------------ VISOR ------------------------------
function showViewer(event, documentId, contingutNom, contingutCustodiat) {
	if (event.target.tagName.toLowerCase() !== 'a' && (event.target.cellIndex === undefined || event.target.cellIndex === 5 || event.target.cellIndex === 6)) return;
    var resumViewer = $('#resum-viewer');
	// Mostrar/amagar visor
	if (!resumViewer.is(':visible')) {
		resumViewer.slideDown(500);
	} else if (previousDocumentId == undefined || previousDocumentId == documentId) {
		closeViewer();
		previousDocumentId = documentId;
		return;
	}
	previousDocumentId = documentId;
	
    // Mostrar contingut capçalera visor
    resumViewer.find('*').not('#container').remove();
    var signantsViewerContent = '<div style="padding: 0% 2% 2% 2%; margin-top: -8px;">\
									<table style="width: 453px;">\
										<tbody id="detallSignantsPreview">\
										</tbody>\
									</table>\
								 </div>';
    var viewerContent = '<div class="panel-heading"><spring:message code="contingut.previsualitzacio"/> \
    					 <span class="fa fa-close" style="float: right; cursor: pointer;" onClick="closeViewer()"></span>\
    					 </div>\
    					 <div class="viewer-content viewer-padding">\
    						<dl class="dl-horizontal">\
	        					<dt style="text-align: left;"><spring:message code="contingut.info.nom"/> </dt><dd>' + contingutNom + '</dd>\
        					</dl>\
    					 </div>';
    					 
    if (contingutCustodiat) {
    	viewerContent += signantsViewerContent;
    }
    resumViewer.prepend(viewerContent);
    if (contingutCustodiat) {
    	getDetallsSignants($("#detallSignantsPreview"), documentId, true);
    }
    

    // Recuperar i mostrar document al visor
	var urlDescarrega = "<c:url value="/contingut/${contingut.id}/document/"/>" + documentId + "/returnFitxer";
	$('#container').attr('src', '');
	$('#container').addClass('rmodal_loading');
	showDocument(urlDescarrega);
	
	$([document.documentElement, document.body]).animate({
        scrollTop: $("#resum-viewer").offset().top - 110
    }, 500);
}

function showDocument(arxiuUrl) {
	// Fa la petició a la url de l'arxiu
	$.ajax({
		type: 'GET',
		url: arxiuUrl,
		responseType: 'arraybuffer',
		success: function(json) {
			
			if (json.error) {
				$('#container').removeClass('rmodal_loading');
				$("#resum-viewer .viewer-padding:last").before('<div class="viewer-padding"><div class="alert alert-danger"><spring:message code="contingut.previsualitzacio.error"/>: ' + json.errorMsg + '</div></div>');
			} else if (json.warning) {
				$('#container').removeClass('rmodal_loading');
				$("#resum-viewer .viewer-padding:last").before('<div class="viewer-padding"><div class="alert alert-warning"><spring:message code="contingut.previsualitzacio.warning"/>' + '</div></div>');
			} else {
				response = json.data;
				var blob = base64toBlob(response.contingut, response.contentType);
	            var file = new File([blob], response.contentType, {type: response.contentType});
	            link = URL.createObjectURL(file);
	            
	            var viewerUrl = "<c:url value="/webjars/pdf-js/2.5.207/web/viewer.html"/>" + '?file=' + encodeURIComponent(link);
			    $('#container').removeClass('rmodal_loading');
			    $('#container').attr('src', viewerUrl);
			}
		    
		},
		error: function(xhr, ajaxOptions, thrownError) {
			$('#container').removeClass('rmodal_loading');
			alert(thrownError);
		}
	});
}

// Amagar visor
function closeViewer() {
	$('#resum-viewer').slideUp(500, function(){
	});
}

function getDetallsSignants(idTbody, contingutId, header) {
	
	idTbody.html("");
	idTbody.append('<tr class="datatable-dades-carregant"><td colspan="7" style="margin-top: 2em; text-align: center"><img src="../img/loading.gif"/></td></tr>');
	$.get("../contingut/document/" + contingutId + "/mostraDetallSignants", function(json){
		if (json.error) {
			idTbody.html('<tr><td colspan="2" style="width:100%"><div class="alert alert-danger"><button type="button" class="close-alertes" data-dismiss="alert" aria-hidden="true"><span class="fa fa-times"></span></button><spring:message code="contingut.document.info.firma.error"/>: ' + json.errorMsg + '</div></td></tr>');
		} else {
			idTbody.html("");
			if(json.data != null && json.data.length > 0){
				json.data.forEach(function(firma){
					if(firma != null){
						var firmaDataStr = "";
						if(firma.responsableNom == null){
							firma.responsableNom = "";
						}
						if(firma.responsableNif == null){
							firma.responsableNif = "";
						}
						if(firma.data != null){
							firmaDataStr = new Date(firma.data);
						}
						if(firma.emissorCertificat == null){
							firma.emissorCertificat = "";
						}
						if (header){
							idTbody.append('<tr><th style="padding-bottom: 2px;"><strong>'
									+ '<u><spring:message code="contingut.document.info.firma"/></u>'
									+ "</strong></th><tr>");
						} 
						idTbody.append(
							  "<tr><th><strong>"
							+ '<spring:message code="contingut.document.camp.firma.responsable.nom"/>'
							+ "</strong></th><th>"
							+ firma.responsableNom
							+ "</th></tr><tr><td><strong>"
							+ '<spring:message code="contingut.document.camp.firma.responsable.nif"/>'
							+ "</strong></td><td>"
							+ firma.responsableNif
							+ "</td></tr><tr><td><strong>"
							+ '<spring:message code="contingut.document.camp.firma.responsable.data"/>'
							+ "</strong></td><td>"
							+ (firmaDataStr != "" ? firmaDataStr.toLocaleString() : "")
							+ "</td></tr><tr><td><strong>"
							+ '<spring:message code="contingut.document.camp.firma.emissor.certificat"/>'
							+ "</strong></td><td>"
							+ firma.emissorCertificat
							+ "</td></tr>");
					}
				})
			}
		}
		webutilRefreshMissatges();
	});
}
	
function returnEnviamentsStatusDiv(notificacioId) {
    var content = "";
    var getUrl = "<c:url value="/expedient/${contingut.id}"/>" + "/enviaments/" + notificacioId;

    $.getJSON({
        url: getUrl,
        success: (notificacio) => {
			var enviaments = notificacio.documentEnviamentInteressats;
            for (i = 0; i < enviaments.length; i++) {
                content += (enviaments[i].enviamentDatatEstat) ? notificacioEnviamentEstats[enviaments[i].enviamentDatatEstat] + ',' : '';
            }
            if (content !== undefined && content != '') {
                content = "("+content.replace(/,\s*$/, "")+")";
            }
            $('.estat_' + notificacioId).html("");
            $('.estat_' + notificacioId).append(content);
        },
        error: function(data){
        	console.log("No s'han pogut recuperar els enviaments de la notificació: " + notificacioId);
        }
    })
}

var myHelpers = {
recuperarEstatEnviament: returnEnviamentsStatusDiv,
};

$.views.helpers(myHelpers);

</script>

</head>
<body>
	<div class="rmodal"></div>
	<input id="contingutId" type="hidden" value="${contingut.id}">
	<c:if test="${(contingut.expedient or contingut.carpeta) and not empty expedientPare.agafatPer and !isTasca}">
		<div class="text-right" data-toggle="botons-titol">
			<ul class="list-group pull-right">
	  			<li class="list-group-item" style="padding: 5px 12px; margin-right: 4px">
	  				<spring:message code="contingut.info.agafat.per"/>:
	  				${expedientPare.agafatPer.codiAndNom}&nbsp;
	  				<c:if test="${expedientAgafatPerUsuariActual}">
	  					<a href="<c:url value="/expedient/${expedientPare.id}/alliberar?contingutId=${contingut.id}"/>" class="btn btn-default btn-xs" title="<spring:message code="comu.boto.alliberar"/>"><span class="fa fa-unlock"></span></a>
	  				</c:if>
	  			</li>
	  		</ul>
		</div>
	</c:if>
	<c:if test="${!isTasca && not expedientAgafatPerUsuariActual && expedientPare.metaNode.usuariActualRead}">
		<div id="alerta-no-agafat" class="alert well-sm alert-info alert-dismissable" style="min-height: 40px;">
			<c:if test="${!contingut.admin}">
				<span class="fa fa-info-circle"></span> 
				<spring:message code="contingut.alerta.no.agafat"/>
			</c:if>  
			<a href="<c:url value="../expedient/${expedientPare.id}/agafar?contingutId=${contingut.id}"/>" class="btn btn-xs btn-default pull-right"><span class="fa fa-lock"></span>&nbsp;&nbsp;<spring:message code="comu.boto.agafar"/></a>
		</div>
	</c:if>
	<c:if test="${!isTasca && !expedientPare.metaNode.usuariActualRead}">
		<div id="alerta-no-agafat" class="alert well-sm alert-info alert-dismissable">
			<span class="fa fa-info-circle"></span>
			<spring:message code="contingut.alerta.sense.permisos"/>
		</div>
	</c:if>
	<div>
		<c:if test="${contingut.expedient or contingut.carpeta}">
			<!------------------------------------------------------------------------- INFORMACIÓ BLOCK (LEFT SIDE OF THE PAGE) ------------------------------------------------------------------------>
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
							<dd>${contingut.expedientPare.numero}</dd>
							
							<dt><spring:message code="contingut.info.titol"/></dt>
							<dd>${contingut.expedientPare.nom}</dd>							
							
							<c:if test="${not empty contingut.expedientPare.metaNode}">
								<dt><spring:message code="contingut.info.meta.expedient"/></dt>
								<dd>${contingut.expedientPare.metaNode.nom}</dd>
							</c:if>
							
							<dt><spring:message code="contingut.info.nti.organ"/></dt>
							<dd>${contingut.expedientPare.organGestorText}</dd>

							<dt><spring:message code="contingut.info.nti.data.obertura"/></dt>
							<dd><fmt:formatDate value="${contingut.expedientPare.ntiFechaApertura}" pattern="dd/MM/yyyy HH:mm:ss"/></dd>
							
							<dt><spring:message code="contingut.info.estat"/></dt>
							<c:choose>
								<c:when test="${contingut.expedientPare.expedientEstat!=null}">
									<dd style="<c:if test='${not empty contingut.expedientPare.expedientEstat.color}'>border-left: solid 6px ${contingut.expedientPare.expedientEstat.color}; padding-left: 4px;</c:if>">${contingut.expedientPare.expedientEstat.nom}</dd>
								</c:when>
								<c:otherwise>
									<dd><spring:message code="expedient.estat.enum.${contingut.expedientPare.estat}"/></dd>
								</c:otherwise>
							</c:choose>	
								
							<dt><spring:message code="contingut.info.nti.classificacio"/></dt>
							<dd>${contingut.expedientPare.ntiClasificacionSia}</dd>

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
												<c:if test="${potModificarContingut}">
													<a href="<c:url value="/expedient/${contingut.expedient ? contingut.id : contingut.expedientPare.id}/relacio/${expedientRelacionat.id}/delete"/>" class="btn btn-default btn-xs" data-confirm="<spring:message code="contingut.info.relacio.esborrar.confirm"/>" style="float: right;">
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
							<rip:blocContingutAccions id="botons-accions-info" contingut="${contingut.expedient ? contingut : contingut.expedientPare}" modeLlistat="true" mostrarObrir="false"/>
						</c:if>
					</dl>
				</div>
			</div>
		</c:if>
		<!------------------------------------------------------------------------- CONTINGUT BLOCK (CENTER/RIGHT SIDE OF THE PAGE) ------------------------------------------------------------------------------->
		<div class="${contingut.document ? 'col-md-12' : 'col-md-9 col-sm-8'}" id="colContent">
			<%--c:if test="${contingut.expedient && !isTasca && !expedientTancat && contingut.hasEsborranys}">
				<c:choose>
					<c:when test="${convertirDefinitiu}">
						<div id="botons-errors-esborranys" class="esborranys alert well-sm alert-info alert-dismissable">
							<p><spring:message code="contingut.errors.expedient.conte.esborranys"/></p>
							<b><spring:message code="contingut.errors.expedient.conte.esborranys.bold"/></b>
						</div>
					</c:when>
					<c:otherwise>
						<div id="botons-errors-esborranys" class="alert well-sm alert-info alert-dismissable">
							<span class="fa fa-info-circle"></span>&nbsp;
							<spring:message code="contingut.errors.expedient.conte.esborranys.caib"/>
						</div>
					</c:otherwise>
				</c:choose>
			</c:if--%>
			<c:if test="${contingut.expedient && !isTasca && !expedientTancat && contingut.hasEsborranys && convertirDefinitiu}">
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
			<c:if test="${!isTasca && (((contingut.expedient or contingut.document) and not contingut.valid) or ((contingut.carpeta) and not contingut.expedientPare.valid))}">
				<div id="botons-errors-validacio" class="alert well-sm alert-warning alert-dismissable">
					<span class="fa fa-exclamation-triangle"></span>&nbsp;
					<c:choose>
						<c:when test="${contingut.expedient or contingut.carpeta}"><spring:message code="contingut.errors.expedient.validacio"/></c:when>
						<c:when test="${contingut.document}"><spring:message code="contingut.errors.document.validacio"/></c:when>
					</c:choose>
					<a href="<c:url value="/contingut/${contingut.carpeta ? contingut.expedientPare.id : contingut.id}/errors"/>" class="btn btn-xs btn-default pull-right" data-toggle="modal"><spring:message code="contingut.errors.consultar"/></a>
				</div>
			</c:if>
			<!---------------------------------------- TABLIST ------------------------------------------>
			
			<c:if test="${!isTasca}">
				<ul class="nav nav-tabs">
					<li class="active" id="pipella-contingut">
						<a href="#contingut" data-toggle="tab"><spring:message code="contingut.tab.contingut"/>&nbsp;<span class="badge">${isMostrarCarpetesPerAnotacions ? contingut.fillsHierarchicalCount : contingut.fillsFlatCount}</span></a>
					</li>
					<c:if test="${((contingut.document or contingut.expedient) and fn:length(contingut.metaNode.metaDades) gt 0) || ((contingut.carpeta) and fn:length(contingut.expedientPare.metaNode.metaDades) gt 0)}">
						<li>
							<a href="#dades" data-toggle="tab"><spring:message code="contingut.tab.dades"/>&nbsp;<span class="badge" id="dades-count">${contingut.carpeta ? contingut.expedientPare.dadesCount : contingut.dadesCount}</span></a>
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
						<c:if test="${(contingut.expedient && contingut.peticions ) || (contingut.carpeta && contingut.expedientPare.peticions)}">
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
					<c:if test="${contingut.document && pluginArxiuActiu}">
						<div class="dropdown" style="float: right;" id="documentDropdownAccions">
							<button data-toggle="dropdown" class="btn btn-primary dropdown-toggle"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret caret-white"></span></button>
							<ul class="dropdown-menu">
								<li><a href="<c:url value="/contingut/${contingut.id}/arxiu"/>" data-toggle="modal"><span class="fa fa-info-circle"></span>&nbsp;<spring:message code="comu.boto.arxiu"/></a></li>
							</ul>
						</div>
					</c:if>	
					<c:if test="${contingut.expedient || contingut.carpeta}">
						<a href="<c:url value="/expedient/${contingut.expedient ? contingut.id : contingut.expedientPare.id}/comentaris"/>" data-toggle="modal" data-refresh-tancar="true" class="btn btn-default pull-right ${potModificarContingut ? '' : 'disabled'}"><span class="fa fa-lg fa-comments"></span>&nbsp;<span class="badge">${contingut.expedient ? contingut.numComentaris : contingut.expedientPare.numComentaris}</span></a>
					</c:if>
				</ul>
			</c:if>
			<div class="tab-content">
				<!------------------------------ TABPANEL CONTINGUT ------------------------------------->
				<div class="tab-pane active in" id="contingut">
					<c:choose>
						<%--------------- WHEN CONTINGUT IS DOCUMENT (SHOWS DOCUMENT DETAILS) ---------------%>
						<c:when test="${contingut.document}">
							<table class="table table-bordered">
								<tbody>
									<c:choose>
										<c:when test="${contingut.documentTipus == 'DIGITAL'}">
											<tr>
												<td><strong><spring:message code="contingut.document.camp.arxiu"/></strong></td>
												<td>${contingut.fitxerNom}</td>
											</tr>
											<c:if test="${!empty contingut.descripcio}">
												<tr> 
													<td><strong><spring:message code="contingut.document.camp.descripcio"/></strong></td>
													<td>${contingut.descripcio}</td>
												</tr>
											</c:if>											
											<tr>
												<td><strong><spring:message code="contingut.document.camp.content.type"/></strong></td>
												<td>${contingut.fitxerContentType}</td>
											</tr>
										</c:when>
										<c:otherwise>
											<tr>
												<td><strong><spring:message code="contingut.document.camp.ubicacio"/></strong></td>
												<td>${contingut.ubicacio}</td>
											</tr>
										</c:otherwise>
									</c:choose>
									<c:if test="${not empty contingut.metaNode}">
										<tr>
											<td><strong><spring:message code="contingut.info.meta.document"/></strong></td>
											<td>${contingut.metaNode.nom}</td>
										</tr>
									</c:if>
									<tr>
										<td><strong><spring:message code="contingut.info.data"/></strong></td>
										<td><fmt:formatDate value="${contingut.data}" pattern="dd/MM/yyyy"/></td>
									</tr>
									<tr>
										<td><strong><spring:message code="contingut.info.estat"/></strong></td>
										<td><spring:message code="document.estat.enum.${contingut.estat}"/></td>
									</tr>									
									<tr>
										<td><strong><spring:message code="contingut.info.nti.data.captura"/></strong></td>
										<td><fmt:formatDate value="${contingut.dataCaptura}" pattern="dd/MM/yyyy"/></td>
									</tr>
									<tr>
										<td><strong><spring:message code="contingut.info.nti.origen"/></strong></td>
										<td><spring:message code="document.nti.origen.enum.${contingut.ntiOrigen}"/></td>
									</tr>
									<tr>
										<td><strong><spring:message code="contingut.info.nti.tipus.doc"/></strong></td>
										<c:choose>
											<c:when test="${not empty contingut.ntiTipoDocumentalNom}">
												<td>${contingut.ntiTipoDocumentalNom}</td>
											</c:when>
											<c:otherwise>
												<td><spring:message code="document.nti.tipdoc.enum.${contingut.ntiTipoDocumental}"/></td>
											</c:otherwise>
										</c:choose>
									</tr>																		
									<c:if test="${not empty contingut.ntiIdDocumentoOrigen}">
										<td><strong><spring:message code="contingut.info.nti.doc.origen.id"/></strong></td>
										<td>${contingut.ntiIdDocumentoOrigen}</td>
									</c:if>
									<c:if test="${!empty contingut.ntiCsv}">		
										<tr>
											<td><strong><spring:message code="contingut.document.camp.firma.csv" /></strong></td>
											<td>${contingut.ntiCsv}</td>
										</tr>	
									</c:if>										
								</tbody>
							</table>
							<c:if test="${contingut.custodiat}">
								<div class="panel panel-default">
									<div class="panel-heading">
										<h3 class="panel-title" style="height: 36px;">
											<span class="fa fa-bookmark" title="Document firmat" style="margin-top: 10px;"></span>
											<spring:message code="contingut.document.info.firma"/>
											<button id="mostraDetallSignants" class="btn btn-default pull-right"><span class="fa fa-info-circle"></span>&nbsp;<spring:message code="comu.boto.mostrar.info.signants"/></button>
										</h3>
									</div>
									<table class="table table-bordered">
										<tbody>
											<tr>
												<td><strong><spring:message code="contingut.document.camp.firma.tipus"/></strong></td>
												<td>
													<c:if test="${not empty contingut.ntiTipoFirma}">
														<spring:message code="document.nti.tipfir.enum.${contingut.ntiTipoFirma}"/>
													</c:if>
												</td>
											</tr>
											<tr>
												<td><strong><spring:message code="contingut.document.camp.firma.csv"/></strong></td>
												<td>${contingut.ntiCsv}</td>
											</tr>
											<tr>
												<td><strong><spring:message code="contingut.document.camp.firma.csv.regulacio"/></strong></td>
												<td>${contingut.ntiCsvRegulacion}</td>
											</tr>
										</tbody>
										<tbody id="detallSignants">
										</tbody>
									</table>
								</div>
							</c:if>
						</c:when>
						<%--------------- WHEN CONTINGUT IS EXPEDIENT OR CARPETA (SHOWS TABLE/GRID OF CONTINGUTS) ---------------%>
						<c:otherwise>
							<c:if test="${isTasca}">
								<div id="tascaBtn" style="float: right">
									<c:if test="${tascaEstat=='INICIADA'}">
										<a href="<c:url value="/usuariTasca/${tascaId}/finalitzar"/>" class="btn btn-default" style="float: right;" data-confirm="<spring:message code="expedient.tasca.finalitzar"/>"><span class="fa fa-check"></span>&nbsp;&nbsp;<spring:message code="comu.boto.finalitzarTasca" /></a>
									</c:if>
									<c:if test="${tascaEstat=='PENDENT'}">
										<a href="<c:url value="/usuariTasca/${tascaId}/iniciar?redirectATasca=true"/>" class="btn btn-default" style="float: right;"><span class="fa fa-play"></span>&nbsp;&nbsp;<spring:message code="comu.boto.iniciar"/></a>
									</c:if>									
									<a href="<c:url value="/usuariTasca"/>" class="btn btn-default pull-right" style="float: right; margin-right: 3px;"><span class="fa fa-arrow-left"></span>&nbsp;<spring:message code="comu.boto.tornar"/></a>
								</div>
							</c:if>							
							<%---- ACCION BUTTONS (CANVI VISTA, CREATE CONTINGUT) ----%>
							<div class="text-right" id="contingut-botons">
							
								<c:if test="${contingut.expedient or contingut.carpeta}">
									<rip:blocContenidorPath contingut="${contingut}"/>
								</c:if>
								
								<c:if test="${isTasca}">
									<a href="<c:url value="/expedientTasca/${tascaId}/comentaris"/>" data-toggle="modal" data-refresh-tancar="true" class="btn btn-default pull-left"><span class="fa fa-lg fa-comments"></span>&nbsp;<span class="badge">${tasca.numComentaris}</span></a>
								</c:if>
								<c:if test="${vistaIcones}">
									<div class="btn-group">
										<div data-toggle="tooltip" title="<spring:message code="contingut.boto.menu.seleccio.multiple.habilitar"/>" id="habilitar-mult" class="btn-group btn btn-default">
											<span class="glyphicon glyphicon-th"></span>
										</div>
									</div>
									<div class="btn-group">
										<div data-toggle="tooltip" title="<spring:message code="contingut.boto.menu.seleccio.multiple.seleccio"/>" id="checkItAll" class="btn-group btn btn-default checkItAll">
											<span class="fa fa-check"></span>
										</div>
									</div>
								</c:if>
								<%---- Button descarregar mult ----%>
								<div class="btn-group">
									<div data-toggle="tooltip" title="<spring:message code="contingut.boto.menu.seleccio.multiple.descarregar"/>" id="descarregar-mult" class="btn-group">
										<a href="<c:url value="/contingut/${contingut.id}/descarregarMultiples"/>" class="btn btn-default con-mult">
											<span class="fa fa-download"></span>
											
											<span class="badge seleccioCount">${fn:length(seleccio)}</span>
										</a>
									</div>
								</div>
								<c:if test="${(expedientAgafatPerUsuariActual or contingut.admin) and !expedientTancat}">
									<c:set var="definitiuConfirmacioMsg"><spring:message code="contingut.confirmacio.definitiu.multiple"/></c:set>
									<%---- Button notificar mult ----%>
									<div class="btn-group">
										<div data-toggle="tooltip" title="<spring:message code="contingut.boto.menu.seleccio.multiple.concatenar"/>" id="notificar-mult" class="btn-group">
											<a href="<c:url value="/contingut/${contingut.id}/notificar"/>" class="btn btn-default con-mult maximized hidden" data-toggle="modal" data-maximized="true">
												<span class="fa fa-envelope-o"></span>
												
												<span class="badge seleccioCount">${fn:length(seleccio)}</span>
											</a> 
											<a href="<c:url value="/contingut/${contingut.id}/notificar"/>" class="btn btn-default con-mult nomaximized" data-toggle="modal" data-missatge-loading="<spring:message code="concatenacio.zip.modal.missatge"/>">
												<span class="fa fa-envelope-o"></span>
												
												<span class="badge seleccioCount">${fn:length(seleccio)}</span>
											</a>
										</div>
										<div data-toggle="tooltip" title="<spring:message code="massiu.estat.definitiu"/>" id="definitiu-mult" class="btn-group">
											<a href="<c:url value="/contingut/${contingut.id}/defintiu"/>" class="btn btn-default con-mult hidden" data-confirm="${definitiuConfirmacioMsg}">
												<span class="fa fa-check-square"></span>
												
												<span class="badge seleccioCount">${fn:length(seleccio)}</span>
											</a> 
											<a href="<c:url value="/contingut/${contingut.id}/defintiu"/>" class="btn btn-default con-mult" data-confirm="${definitiuConfirmacioMsg}">
												<span class="fa fa-check-square"></span>
												
												<span class="badge seleccioCount">${fn:length(seleccio)}</span>
											</a>
										</div>
										<div data-toggle="tooltip" title="<spring:message code="massiu.moure.documents"/>" class="btn-group" id="moure-mult">
											<a href="<c:url value="/contingut/${contingut.id}/moure"/>" data-toggle="modal" class="btn btn-default con-mult">
												<span class="fa fa-arrows"></span>
												
												<span class="badge seleccioCount">${fn:length(seleccio)}</span>
											</a>
										</div>
										<div data-toggle="popover" class="btn btn-default" id="tipusdocumental-mult">
											<div data-toggle="tooltip" title="<spring:message code="massiu.canvi.tipus.document"/>">
												<span class="fa fa-edit"></span>
												<span class="badge seleccioCount">${fn:length(seleccio)}</span>
											</div>
										</div>
									</div>
									<%---- Button descarregar zip mult 
									<div class="btn-group">
										<div data-toggle="tooltip" title="<spring:message code="contingut.boto.menu.seleccio.multiple.concatenarzip"/>" id="notificar-mult" class="btn-group">
											<a href="<c:url value="/contingut/${contingut.id}/generarZip/new"/>" class="btn btn-default zip-mult" data-toggle="modal">
												<span class="glyphicon glyphicon-compressed"></span>
												<span class="badge seleccioCount">${fn:length(seleccio)}</span>
											</a>
										</div>
									</div>
									----%>
								</c:if>
								<div class="btn-group">
									<%---- Button llistat ----%>
									<c:choose>
										<c:when test="${isTasca}">
											<c:set var="llistatVistaUrl"><c:url value="/usuariTasca/${tascaId}/canviVista/llistat"/></c:set>
										</c:when>
										<c:otherwise>
											<c:set var="llistatVistaUrl"><c:url value="/contingut/${contingut.id}/canviVista/llistat"/></c:set>
										</c:otherwise>
									</c:choose>	
									<a href="${llistatVistaUrl}" class="btn btn-default ${vistaLlistat ? 'active' : ''}" draggable="false">
										<span class="fa fa-th-list"></span>
									</a>
									<%---- Button icones ----%>
									<c:choose>
										<c:when test="${isTasca}">
											<c:set var="iconesVistaUrl"><c:url value="/usuariTasca/${tascaId}/canviVista/icones"/></c:set>
										</c:when>
										<c:otherwise>
											<c:set var="iconesVistaUrl"><c:url value="/contingut/${contingut.id}/canviVista/icones"/></c:set>
										</c:otherwise>
									</c:choose>										
									<a href="${iconesVistaUrl}" class="btn btn-default ${vistaIcones ? 'active' : ''}" draggable="false"> 
										<span class="fa fa-th"></span>
									</a>	
									<script>
										var docsIdx = new Array();
									</script>							
								</div>
								<c:if test="${isTasca or ((expedientAgafatPerUsuariActual or contingut.admin) and ((contingut.carpeta && isCreacioCarpetesActiva) or (contingut.expedient and (potModificarContingut or contingut.admin) and (contingut.expedient ? contingut.estat != 'TANCAT' : contingut.expedientPare.estat != 'TANCAT'))))}">
									<div id="botons-crear-contingut" class="btn-group">
										<%---- Crear contingut ----%>
										<button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown"><span class="fa fa-plus"></span>&nbsp;<spring:message code="contingut.boto.crear.contingut"/>&nbsp;<span class="caret"></span></button>
										<ul class="dropdown-menu text-left" role="menu">
											<c:if test="${contingut.crearExpedients and not empty metaExpedients}">
												<li>
												<a href="<c:url value="/contingut/${contingut.id}/expedient/new"/>" data-toggle="modal" data-refresh-pagina="true">
													<span class="fa ${iconaExpedientTancat}"></span>&nbsp;<spring:message code="contingut.boto.crear.expedient"/>...
												</a>
												</li>
											</c:if>
											<%---- Document... ----%>
											<c:choose>
												<c:when test="${isTasca}">
													<li><a id="document-new" href="<c:url value="/usuariTasca/${tascaId}/pare/${contingut.id}/document/new"/>" data-toggle="modal" data-refresh-pagina="true"><span class="fa ${iconaDocument}"></span>&nbsp;&nbsp;<spring:message code="contingut.boto.crear.document"/>...</a></li>
												</c:when>
												<c:otherwise>
													<li>
													<c:choose>
  														<c:when test="${empty metaDocumentsLeft}">
															<a href="#" id="document-new-empty-metadocuments">
																<span class="fa ${iconaDocument}"></span>&nbsp;&nbsp;<spring:message code="contingut.boto.crear.document"/>...
															</a>
  														</c:when>
														<c:otherwise>
	   														<a id="document-new" href="<c:url value="/contingut/${contingut.id}/document/new"/>" data-toggle="modal" data-refresh-pagina="true">
																<span class="fa ${iconaDocument}"></span>&nbsp;&nbsp;<spring:message code="contingut.boto.crear.document"/>...
															</a>
														</c:otherwise>
													</c:choose>
													</li>
													<c:if test="${not empty metaDocumentsPinbalLeft}">
														<li>
															<a id="pinbal-new" href="<c:url value="/contingut/${contingut.id}/pinbal/new"/>" data-toggle="modal" data-refresh-pagina="true">
																<span class="fa ${iconaDocument}"></span>&nbsp;&nbsp;<spring:message code="contingut.boto.crear.pinbal"/>...
															</a>
														</li>
													</c:if>
												</c:otherwise>
											</c:choose>
											<c:if test="${!isTasca}">
												<%---- Carpeta... ----%>
												<c:if test="${isCreacioCarpetesActiva}">
													<li><a href="<c:url value="/contingut/${contingut.id}/carpeta/new"/>" data-toggle="modal" data-refresh-pagina="true"><span class="fa ${iconaCarpeta}"></span>&nbsp;&nbsp;<spring:message code="contingut.boto.crear.carpeta"/>...</a></li>
												</c:if>
												<c:if test="${isMostrarImportacio}">
													<li><a href="<c:url value="/contingut/${contingut.id}/importacio/new"/>" data-toggle="modal" data-refresh-pagina="true"><span class="fa ${iconaImportacio}"></span>&nbsp;&nbsp;<spring:message code="contingut.boto.crear.importacio"/>...</a></li>
												</c:if>
												<c:if test="${isImportacioRelacionatsActiva}">
													<li><a href="<c:url value="/expedient/${contingut.expedient ? contingut.id : contingut.expedientPare.id}/relacionats/${contingut.id}/list"/>" data-toggle="modal" data-refresh-pagina="true" data-maximized="true"><span class="fa fa-link"></span>&nbsp;&nbsp;<spring:message code="contingut.boto.crear.expedient.relacionat"/>...</a></li>
												</c:if>	
											</c:if>											
										</ul>
									</div>
								</c:if>
							</div>
							<%---- TABLE/GRID OF CONTINGUTS ----%>
							<div id="loading">
								<img src="<c:url value="/img/loading.gif"/>"/>
							</div>
							<rip:blocContingutContingut contingut="${contingut}" mostrarFillsFlat="${!isMostrarCarpetesPerAnotacions}"/>
							
							
							<div class="panel panel-default" id="resum-viewer" style="display: none; width: 100%;" >
								<iframe id="container" class="viewer-padding" width="100%" height="540" frameBorder="0"></iframe>
							</div>  
										
							
							<c:if test="${isTasca or ((expedientAgafatPerUsuariActual or contingut.admin) and ((contingut.carpeta and contingut.expedientPare.estat != 'TANCAT') or (contingut.expedient and (potModificarContingut or contingut.admin) and (contingut.expedient ? contingut.estat != 'TANCAT' : contingut.expedientPare.estat != 'TANCAT'))))}">
								<div id="drag_container" class="drag_activated">
									<span class="down fa fa-upload"></span>
									<p>
										<spring:message code="contingut.drag.info" />
									</p>
								</div>
							</c:if>
							<input class="hidden" id="dropped-files" type="file"/>
							
						</c:otherwise> 
 					</c:choose> 
				</div>
				<c:if test="${!isTasca}">
						<!------------------------------ TABPANEL DADES ------------------------------------->
						<div class="tab-pane" id="dades">
							<c:choose>
								<c:when test="${not empty metaDades}">
									<form:form id="nodeDades" commandName="dadesCommand" cssClass="form-inline">
										<c:if test="${((expedientAgafatPerUsuariActual && potModificarContingut) || contingut.admin) && !expedientTancat}">
											<button type="submit" class="btn btn-default pull-right" style="margin-bottom: 6px"><span class="fa fa-save"></span> <spring:message code="comu.boto.guardar"/></button>
										</c:if>
										<table class="table table-striped table-bordered" style="width:100%">
										<thead>
											<tr>
												<th><spring:message code="contingut.dades.columna.dada"/></th>
												<th width="340"><spring:message code="contingut.dades.columna.valor"/></th>
											</tr>
										</thead>
										<tbody>
											<c:forEach var="metaDada" items="${metaDades}">
												<c:set var="dadaValor"></c:set>
												<c:forEach var="dada" items="${contingut.expedient ? contingut.dades : contingut.expedientPare.dades}">
													<c:if test="${dada.metaDada.codi == metaDada.codi}">
														<c:set var="dadaValor">${dada.valorMostrar}</c:set>
													</c:if>
												</c:forEach>
												<c:set var="isMultiple" value="${metaDada.multiplicitat == 'M_0_N' or metaDada.multiplicitat == 'M_1_N'}"/>
												<c:set var="multipleClass" value=""/>
												<c:if test="${isMultiple}"><c:set var="multipleClass" value=" multiple"/></c:if>
												<tr>
													<td>${metaDada.nom}</td>
													<td>
														<c:choose>
															<c:when test="${((expedientAgafatPerUsuariActual && potModificarContingut) || contingut.admin) && !expedientTancat}">
																<div class="form-group ${metaDada.tipus == 'DOMINI' ? '' :''}" ${metaDada.tipus == 'DOMINI' ? 'style="width: 100%;margin-bottom: -10px;"' :''} <c:if test="${isMultiple}"> data-toggle="multifield" data-nou="true"</c:if>>
																	<label class="hidden" for="${metaDada.codi}"></label>
																	<div class="controls">
																		<c:choose>
																			<c:when test="${metaDada.tipus == 'SENCER'}">
																				<form:input path="${metaDada.codi}" id="${metaDada.codi}" data-toggle="autonumeric" data-a-dec="," data-a-sep="" data-m-dec="0" class="form-control text-right${multipleClass}"></form:input>
																			</c:when>
																			<c:when test="${metaDada.tipus == 'FLOTANT'}">
																				<form:input path="${metaDada.codi}" id="${metaDada.codi}" data-toggle="autonumeric" data-a-dec="," data-a-sep="" data-m-dec="10" data-a-pad="false" class="form-control text-right${multipleClass}"></form:input>
																			</c:when>
																			<c:when test="${metaDada.tipus == 'IMPORT'}">
																				<form:input path="${metaDada.codi}" id="${metaDada.codi}" data-toggle="autonumeric" data-a-dec="," data-a-sep="." data-m-dec="2" class="form-control text-right${multipleClass}"></form:input>
																			</c:when>
																			<c:when test="${metaDada.tipus == 'DATA'}">
																				<form:input path="${metaDada.codi}" id="${metaDada.codi}" data-toggle="datepicker" data-idioma="${requestLocale}" data-a-dec="," data-a-sep="." data-m-dec="2" cssClass="form-control text-right${multipleClass}"></form:input>
																			</c:when>
																			<c:when test="${metaDada.tipus == 'BOOLEA'}">
																			<label>
																				<form:input path="${metaDada.codi}" id="${metaDada.codi}" data-toggle="checkbox" data-a-dec="," data-a-sep="." data-m-dec="2" class="${multipleClass}"></form:input>
																			</label>
																			</c:when>
																			<c:when test="${metaDada.tipus == 'DOMINI'}">
																			
																				<form:select path="${metaDada.codi}" id="${metaDada.codi}" cssStyle="width: 100%" cssClass="form-control${multipleClass} dominis" multiple="false"/>
																				<script type="text/javascript">
																				recuperarResultatDomini(
																						"${contingut.metaNode.id}",
																						"${metaDada.codi}",
																						"${dadaValor}");
																				</script>
																			</c:when>
																			<c:otherwise>
																				<form:input path="${metaDada.codi}" id="${metaDada.codi}" cssClass="form-control${multipleClass}"></form:input>
																			</c:otherwise>
																		</c:choose>
																		<span class="" aria-hidden="true"></span>
																	</div>
																</div>
															</c:when>
															<c:when test="${expedientTancat && (metaDada.tipus == 'DOMINI')}">
																<form:select path="${metaDada.codi}" id="${metaDada.codi}" cssStyle="width: 100%" data-toggle="select2" cssClass="form-control${multipleClass} dominis" multiple="false" disabled="true"/>
																<script type="text/javascript">
																	recuperarResultatDomini(
																			"${contingut.metaNode.id}",
																			"${metaDada.codi}",
																			"${dadaValor}");
																	
																</script>
															</c:when>
															<c:otherwise>
																${dadaValor}
															</c:otherwise>
														</c:choose>
													</td>
												</tr>
											</c:forEach>
										</tbody>
										</table>
										<c:if test="${((expedientAgafatPerUsuariActual && potModificarContingut) || contingut.admin) && !expedientTancat}">
											<button type="submit" class="btn btn-default pull-right" style="margin-top: -14px"><span class="fa fa-save"></span> <spring:message code="comu.boto.guardar"/></button>
										</c:if>
									</form:form>
								</c:when>
								<c:otherwise>
								</c:otherwise>
							</c:choose>
						</div>
					<c:if test="${contingut.expedient || contingut.carpeta}">
						<!------------------------------ TABPANEL INTERESSATS ------------------------------------->	
						<div class="tab-pane" id="interessats">
							<table 
								id="taulaInteressats" 
								data-toggle="datatable" 
								data-url="<c:url value="/contingut/${contingut.expedient ? contingut.id : contingut.expedientPare.id}/interessat/datatable"/>" 
								data-paging-enabled="false"
								data-botons-template="#taulaInteressatsNouBoton" 
								class="table table-striped table-bordered" 
								style="width:100%"
								data-row-info="true">
								<thead>
									<tr>
										<th data-col-name="id" data-visible="false">#</th>
										<th data-col-name="representantId" data-visible="false">#</th>
										<th data-col-name="representant" data-visible="false">#</th>
										<th data-col-name="arxiuPropagat" data-visible="false"></th>
										<th data-col-name="representantArxiuPropagat" data-visible="false"></th>			
										<th data-col-name="expedientArxiuPropagat" data-visible="false"></th>
										<th data-col-name="tipus" data-template="#cellTipusInteressatTemplate" data-orderable="false" width="15%">
											<spring:message code="contingut.interessat.columna.tipus"/>
											<script id="cellTipusInteressatTemplate" type="text/x-jsrender">
											{{:~eval('interessatTipusText["' + tipus + '"]')}}
										</script>
										</th>
										<th data-col-name="documentNum" data-orderable="false" width="15%"><spring:message code="contingut.interessat.columna.document"/></th>
										<th data-col-name="identificador" data-orderable="false" width="35%" data-template="#cellIdentificadorTemplate"><spring:message code="contingut.interessat.columna.identificador"/>
											<script id="cellIdentificadorTemplate" type="text/x-jsrender">
												{{:identificador}} 
												{{if !arxiuPropagat}}
													<span class="fa fa-exclamation-triangle text-danger" title="<spring:message code="contingut.icona.estat.pendentGuardarArxiu"/>"></span>
												{{/if}}												
											</script>
										</th>
										<th data-col-name="representantIdentificador" data-orderable="false" width="25%" data-template="#cellRepresentantTemplate">
											<spring:message code="contingut.interessat.columna.representant"/>
											<script id="cellRepresentantTemplate" type="text/x-jsrender">
												{{:representantIdentificador}} 
												{{if representantId != null}}
													{{if !representantArxiuPropagat}}
														<span class="fa fa-exclamation-triangle text-danger" title="<spring:message code="contingut.icona.estat.pendentGuardarArxiu"/>"></span>
													{{/if}}	
												{{/if}}												
											</script>										
										</th>
										<c:if test="${((expedientAgafatPerUsuariActual && potModificarContingut) || contingut.admin) && (contingut.expedient ? contingut.estat != 'TANCAT' : contingut.expedientPare.estat != 'TANCAT')}">
										<th data-col-name="id" data-orderable="false" data-template="#cellAccionsInteressatTemplate" width="10%">
											<script id="cellAccionsInteressatTemplate" type="text/x-jsrender">
											<div class="dropdown">
												<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
												<ul class="dropdown-menu">
													{{if (!arxiuPropagat || !representantArxiuPropagat)}}
														{{if !expedientArxiuPropagat}}
															<li class="disabledMsg" title="<spring:message code="disabled.button.primerGuardarExpedientArxiu"/>"><a class="disabled"><span class="fa fa-refresh"></span>&nbsp;<spring:message code="comu.boto.guardarArxiu"/></a></li>
														{{else}}
															<li><a href="<c:url value="/expedient/${contingut.expedient ? contingut.id : contingut.expedientPare.id}/guardarInteressatsArxiu?origin=docDetail"/>"><span class="fa fa-refresh"></span>&nbsp;<spring:message code="comu.boto.guardarArxiu"/></a></li>
														{{/if}}
													{{/if}}	
													<li><a href="<c:url value="/expedient/${contingut.expedient ? contingut.id : contingut.expedientPare.id}/interessat/{{:id}}"/>" data-toggle="modal" data-refresh-pagina="false" class="btnModificarInteressat"><span class="fa fa-pencil"></span>&nbsp;&nbsp;<spring:message code="comu.boto.modificar"/></a></li>
													<li><a href="<c:url value="/expedient/${contingut.expedient ? contingut.id : contingut.expedientPare.id}/interessat/{{:id}}/delete"/>" data-toggle="ajax" data-refresh-pagina="false" data-confirm="<spring:message code="contingut.confirmacio.esborrar.interessat"/>"><span class="fa fa-trash-o"></span>&nbsp;&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
													{{if tipus != '<%=es.caib.ripea.core.api.dto.InteressatTipusEnumDto.ADMINISTRACIO%>'}}
														<li class="divider" role="separator"></li>
														{{if representantId}}
															<li><a href="<c:url value="/expedient/${contingut.expedient ? contingut.id : contingut.expedientPare.id}/interessat/{{:id}}/representant/{{:representantId}}"/>" data-toggle="modal"><span class="fa fa-pencil"></span>&nbsp;&nbsp;<spring:message code="contingut.interessat.modificar.prepresentant"/></a></li>
															<li><a href="<c:url value="/expedient/${contingut.expedient ? contingut.id : contingut.expedientPare.id}/interessat/{{:id}}/representant/{{:representantId}}/delete"/>" data-toggle="ajax" data-confirm="<spring:message code="contingut.confirmacio.esborrar.representant"/>"><span class="fa fa-trash-o"></span>&nbsp;&nbsp;<spring:message code="contingut.interessat.borrar.representant"/></a></li>
														{{else}}
															<li><a href="<c:url value="/expedient/${contingut.expedient ? contingut.id : contingut.expedientPare.id}/interessat/{{:id}}/representant/new"/>" data-toggle="modal"><span class="fa fa-plus"></span>&nbsp;&nbsp;<spring:message code="contingut.interessat.nou.prepresentant"/></a></li>														
														{{/if}}
													{{/if}}
												</ul>
											</div>
											</script>
										</th>
										</c:if>
									</tr>
								</thead>
							</table>
							<script id="taulaInteressatsNouBoton" type="text/x-jsrender">
							<c:if test="${((expedientAgafatPerUsuariActual && potModificarContingut) || contingut.admin) && (contingut.expedient ? (contingut.expedient ? contingut.estat != 'TANCAT' : contingut.expedientPare.estat != 'TANCAT') : contingut.expedientPare.estat != 'TANCAT')}">
								<p style="text-align:right"><a href="<c:url value="/expedient/${contingut.expedient ? contingut.id : contingut.expedientPare.id}/interessat/new"/>" id="addInteressatBtn" class="btn btn-default" data-toggle="modal" data-refresh-pagina="false"><span class="fa fa-plus"></span>&nbsp;<spring:message code="contingut.boto.nou.interessat"/></a></p>
							</c:if>
						</script>
						</div>
						<!------------------------------ TABPANEL REMESES ------------------------------------->
						<div class="tab-pane" id="notificacions">
							<table
								id="taulaNotificacions"
								data-toggle="datatable"
								data-url="<c:url value="/expedient/${contingut.expedient ? contingut.id : contingut.expedientPare.id}/enviament/NOTIFICACIO/datatable"/>"
								data-paging-enabled="false"
								data-agrupar="5"
								class="table table-bordered table-striped"
								style="width:100%"
								data-row-info="true">
								<thead>
									<tr>
										<th data-col-name="error" data-visible="false"></th>
										<th data-col-name="notificacio" data-visible="false"></th>
										<th data-col-name="publicacio" data-visible="false"></th>
										<th data-col-name="tipus" data-orderable="false" data-template="#cellNotficicacioTipusTemplate" width="15%">
											<spring:message code="contingut.enviament.columna.tipus"/>
											<script id="cellNotficicacioTipusTemplate" type="text/x-jsrender">
												{{if tipus == 'MANUAL'}}
													<spring:message code="contingut.enviament.notificacio.man"/>
												{{else tipus == 'COMUNICACIO'}}
													<spring:message code="contingut.enviament.comunicacio"/>
												{{else}}
													<spring:message code="contingut.enviament.notificacio.elec"/>
												{{/if}}
										</script>
										</th>
										<th data-col-name="createdDate" data-converter="datetime" data-orderable="false" width="20%"><spring:message code="contingut.enviament.columna.creadael"/></th>
										<th data-col-name="processatDataString" data-orderable="false" data-template="#cellProcessatDataTemplate" width="20%">
											<spring:message code="contingut.enviament.columna.dataFinalitzada"/>
											<script id="cellProcessatDataTemplate" type="text/x-jsrender">
												{{if notificacioEstat == 'FINALITZADA' or notificacioEstat == 'PROCESSADA'}}
													{{:processatDataString}}
												{{/if}}
											</script>										
										</th>
										<th data-col-name="assumpte" data-orderable="false" width="25%"><spring:message code="contingut.enviament.columna.concepte"/></th>
										<th data-col-name="destinatari" data-orderable="false" data-visible="false" width="20%">
											<spring:message code="contingut.enviament.columna.destinatari"/>
										</th>
										<th data-col-name="documentId" data-visible="false"/>
										<th data-col-name="documentNom" data-orderable="false" width="25%"><spring:message code="contingut.enviament.columna.document"/></th>
										
										<th data-col-name="notificacioEstat" data-visible="false"></th>
										<th data-col-name="estat" data-template="#cellNotificacioEstatTemplate" data-orderable="false" width="10%">
											<spring:message code="contingut.enviament.columna.estat"/>
											<script id="cellNotificacioEstatTemplate" type="text/x-jsrender">
												{{if notificacioEstat == 'PENDENT'}}
													<span class="label label-warning"><span class="fa fa-clock-o"></span> <spring:message code="notificacio.notificacioEstat.enum.PENDENT"/></span>
													{{if error}} <span class="fa fa-warning text-danger" title="<spring:message code="contingut.enviament.error"/>"></span> {{/if}}
												{{else notificacioEstat == 'ENVIADA'}}
													{{if error}}
														<span class="label label-danger"><span class="fa fa-warning"></span> <spring:message code="notificacio.notificacioEstat.enum.ENVIADA"/></span>
													{{else}}
														<span class="label label-info"><span class="fa fa-envelope-o"></span> <spring:message code="notificacio.notificacioEstat.enum.ENVIADA"/></span>
													{{/if}}
												{{else notificacioEstat == 'REGISTRADA'}}
													{{if error}}
														<span class="label label-danger"><span class="fa fa-warning"></span> <spring:message code="notificacio.notificacioEstat.enum.REGISTRADA"/></span>
													{{else}}
														<span class="label label-success"><span class="fa fa-check"></span> <spring:message code="notificacio.notificacioEstat.enum.REGISTRADA"/></span>
													{{/if}}
												{{else notificacioEstat == 'FINALITZADA'}}
													{{if error}}
															<span class="label label-danger"><span class="fa fa-warning"></span> <spring:message code="notificacio.notificacioEstat.enum.FINALITZADA"/></span>
														{{else}}
															<span class="label label-success"><span class="fa fa-check"></span> <spring:message code="notificacio.notificacioEstat.enum.FINALITZADA"/></span>
													{{/if}}
												{{else notificacioEstat == 'PROCESSADA'}}
													{{if error}}
															<span class="label label-danger"><span class="fa fa-warning"></span> <spring:message code="notificacio.notificacioEstat.enum.PROCESSADA"/></span>
														{{else}}
															<span class="label label-success"><span class="fa fa-check"></span> <spring:message code="notificacio.notificacioEstat.enum.PROCESSADA"/></span>
													{{/if}}
												{{/if}}
												{{if notificacioEstat == 'PROCESSADA'}}
												{{:~recuperarEstatEnviament(id)}}
												<p class="estat_{{:id}}"  style="display:inline"></p>
												{{/if}}
										</script>
										</th>
										<th data-col-name="id" data-orderable="false" data-template="#cellNotifiacioAccionsTemplate" width="10%">
											<script id="cellNotifiacioAccionsTemplate" type="text/x-jsrender">
											<div class="dropdown">
												<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
												<ul class="dropdown-menu">
													<li><a href="<c:url value="/document/{{:documentId}}/{{if notificacio}}notificacio{{else}}publicacio{{/if}}/{{:id}}/info"/>" data-toggle="modal"><span class="fa fa-info-circle"></span>&nbsp;&nbsp;<spring:message code="comu.boto.detalls"/></a></li>
													{{if tipus == 'MANUAL'}}
														<li><a href="<c:url value="/expedient/${contingut.expedient ? contingut.id : contingut.expedientPare.id}/notificacio/{{:id}}"/>" data-toggle="modal"><span class="fa fa-pencil"></span>&nbsp;&nbsp;<spring:message code="comu.boto.modificar"/></a></li>
														<li><a href="<c:url value="/expedient/${contingut.expedient ? contingut.id : contingut.expedientPare.id}/notificacio/{{:id}}/delete"/>" data-toggle="ajax" data-confirm="<spring:message code="contingut.confirmacio.esborrar.notificacio"/>"><span class="fa fa-trash-o"></span>&nbsp;&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
													{{/if}}
												</ul>
											</div>
										</script>
										</th>
									</tr>
								</thead>
							</table>
						</div>
						
						<!------------------------------ TABPANEL PUBLICACIONS ------------------------------------->
						<div class="tab-pane" id="publicacions">
							<table
								id="taulaEnviaments"
								data-toggle="datatable"
								data-url="<c:url value="/expedient/${contingut.expedient ? contingut.id : contingut.expedientPare.id}/enviament/PUBLICACIO/datatable"/>"
								data-paging-enabled="false"
								data-agrupar="5"
								class="table table-bordered table-striped"
								style="width:100%"
								data-row-info="true">
								<thead>
									<tr>
										<th data-col-name="error" data-visible="false"></th>
										<th data-col-name="notificacio" data-visible="false"></th>
										<th data-col-name="publicacio" data-visible="false"></th>
										<th data-col-name="tipus" data-orderable="false" data-template="#cellPublicacioTipusTemplate" width="15%">
											<spring:message code="contingut.enviament.columna.tipus"/>
											<script id="cellPublicacioTipusTemplate" type="text/x-jsrender">
												<spring:message code="contingut.enviament.publicacio"/>
											</script>
										</th>
										<th data-col-name="createdDate" data-converter="datetime" data-orderable="false" width="20%"><spring:message code="contingut.enviament.columna.data"/></th>
										<th data-col-name="processatData" data-converter="datetime" data-orderable="false" width="20%"><spring:message code="contingut.enviament.columna.dataFinalitzada"/></th>
										<th data-col-name="assumpte" data-orderable="false" width="25%"><spring:message code="contingut.enviament.columna.assumpte"/></th>
										<th data-col-name="destinatari" data-orderable="false" data-visible="false" width="20%">
											<spring:message code="contingut.enviament.columna.destinatari"/>
										</th>
										<th data-col-name="documentId" data-visible="false"/>
										<th data-col-name="documentNom" data-orderable="false" width="25%"><spring:message code="contingut.enviament.columna.document"/></th>
										
										<th data-col-name="notificacioEstat" data-visible="false"></th>
										<th data-col-name="estat" data-template="#cellPublicacioEstatTemplate" data-orderable="false" width="10%">
											<spring:message code="contingut.enviament.columna.estat"/>
											<script id="cellPublicacioEstatTemplate" type="text/x-jsrender">
												{{if estat == 'PENDENT'}}
													<span class="label label-warning"><span class="fa fa-clock-o"></span> {{:~eval('publicacioEstatText["' + estat + '"]')}}</span>
												{{else estat == 'ENVIAT'}}
													<span class="label label-info"><span class="fa fa-envelope-o"></span> {{:~eval('publicacioEstatText["' + estat + '"]')}}</span>
												{{else estat == 'REBUTJAT'}}
													<span class="label label-default"><span class="fa fa-times"></span> {{:~eval('publicacioEstatText["' + estat + '"]')}}</span>
												{{else estat == 'PROCESSAT'}}
													<span class="label label-danger"><span class="fa fa-check"></span> {{:~eval('publicacioEstatText["' + estat + '"]')}}</span>
												{{/if}}
											</script>
										</th>
										<th data-col-name="id" data-orderable="false" data-template="#cellPublicacioAccionsTemplate" width="10%">
											<script id="cellPublicacioAccionsTemplate" type="text/x-jsrender">
											<div class="dropdown">
												<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
												<ul class="dropdown-menu">
													<li><a href="<c:url value="/document/{{:documentId}}/{{if notificacio}}notificacio{{else}}publicacio{{/if}}/{{:id}}/info"/>" data-toggle="modal"><span class="fa fa-info-circle"></span>&nbsp;&nbsp;<spring:message code="comu.boto.detalls"/></a></li>
													<li><a href="<c:url value="/document/{{:documentId}}/publicacio/{{:id}}"/>" data-toggle="modal"><span class="fa fa-pencil"></span>&nbsp;&nbsp;<spring:message code="comu.boto.modificar"/></a></li>
													<li><a href="<c:url value="/document/{{:documentId}}/publicacio/{{:id}}/delete"/>" data-toggle="ajax" data-confirm="<spring:message code="contingut.confirmacio.esborrar.publicacio"/>"><span class="fa fa-trash-o"></span>&nbsp;&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
												</ul>
											</div>
										</script>
										</th>
									</tr>
								</thead>
							</table>
						</div>						
						
						<!--  If expedient came form DISTRIBUCIO and was created from peticion -->
						<c:if test="${(contingut.expedient && contingut.peticions ) || (contingut.carpeta && contingut.expedientPare.peticions)}">
							<!------------------------------ TABPANEL ANOTACIONS ------------------------------------->
							<div class="tab-pane" id="anotacions">
								<table 
									id="taulaAnotacions" 
									data-toggle="datatable"
									data-url="<c:url value="/expedientPeticio/${contingut.expedient ? contingut.id : contingut.expedientPare.id}/datatable"/>"
									data-paging-enabled="false" 
									data-default-order="3" 
									data-default-dir="desc"
									data-agrupar="5" 
									class="table table-bordered table-striped" 
									style="width: 100%">
									<thead>
										<tr>
											<th data-col-name="id" data-visible="false"></th>
											<th data-col-name="registre.extracte" data-orderable="false" width="25%"><spring:message code="contingut.anotacions.columna.extracte"/></th>
											<th data-col-name="registre.origenRegistreNumero" data-orderable="false" width="25%"><spring:message code="contingut.anotacions.columna.numero"/></th>
											<th data-col-name="registre.data" data-converter="datetime" data-orderable="false" width="20%"><spring:message code="contingut.anotacions.columna.data"/></th>
											<th data-col-name="registre.destiDescripcio" data-orderable="false" width="25%"><spring:message code="contingut.anotacions.columna.destiDescripcio"/></th>

											<th data-col-name="id" data-orderable="false" data-template="#cellAnotacioAccionsTemplate" width="10%">
												<script id="cellAnotacioAccionsTemplate" type="text/x-jsrender">
											<a href="<c:url value="/expedientPeticio/{{:id}}"/>" data-maximized="true" class="btn btn-primary" data-toggle="modal"><span class="fa fa-info"></span>&nbsp;&nbsp;<spring:message code="comu.boto.detalls"/></a>
										</script>
											</th>
										</tr>
									</thead>
								</table>
							</div>
						</c:if>
					</c:if>
					<c:if test="${contingut.document and fn:length(contingut.versions) gt 0}">
						<!------------------------------ TABPANEL VERSIONS ------------------------------------->
						<div class="tab-pane" id="versions">
							<div class="tab-pane" id="contingut">
								<div id="document-versions" class="panel-group" id="accordion">
									<table class="table table-bordered table-striped">
									<thead>
										<tr>
											<th>Id</th>
											<th>Data</th>
											<c:if test="${contingut.documentTipus != 'FISIC'}">
												<th width="1%"></th>
											</c:if>
										</tr>
									</thead>
									<tbody>
									<c:forEach var="versio" items="${contingut.versions}">
										<tr>
											<td>${versio.id}</td>
											<td><fmt:formatDate value="${versio.data}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
											<c:if test="${contingut.documentTipus != 'FISIC'}">
												<td>
													<a href="<c:url value="/contingut/${contingut.id}/document/${contingut.id}/versio/${versio.id}/descarregar"/>" class="btn btn-default">
														<span class="fa fa-download"></span>&nbsp;
														<spring:message code="comu.boto.descarregar"/>
													</a>
												</td>
											</c:if>
										</tr>
									</c:forEach>
									</tbody>
									</table>
								</div>
							</div>
						</div>
					</c:if>
					<c:if test="${contingut.expedient || contingut.carpeta}">
						<!------------------------------ TABPANEL TASQUES ------------------------------------->
						<div class="tab-pane" id="tasques">
							<table
								id="taulaTasques"
								data-toggle="datatable"
								data-url="<c:url value="/expedientTasca/${contingut.expedient ? contingut.id : contingut.expedientPare.id}/datatable"/>"
								data-paging-enabled="false"
								data-agrupar="6"
								class="table table-bordered table-striped"
								style="width:100%"
								data-botons-template="#taulaTasquesNouBoton">
								<thead>
									<tr>
										<th data-col-name="id" data-visible="false"></th>
										<th data-col-name="metaExpedientTasca.nom" data-orderable="false" width="15%"><spring:message code="expedient.tasca.list.columna.metaExpedientTasca"/></th>									
										<th data-col-name="dataInici" data-converter="datetime" data-orderable="false" width="20%"><spring:message code="expedient.tasca.list.columna.dataInici"/></th>
										<th data-col-name="dataFi" data-converter="datetime"data-orderable="false"  width="20%"><spring:message code="expedient.tasca.list.columna.dataFi"/></th>
										<th data-col-name="responsablesStr" data-orderable="false" width="15%"><spring:message code="expedient.tasca.list.columna.responsables"/></th>	
										<th data-col-name="responsableActual.codi" data-orderable="false" width="15%"><spring:message code="expedient.tasca.list.columna.responsable.actual"/></th>								
										<th data-col-name="estat" data-template="#cellTascaEstatTemplate" data-orderable="false" width="10%">
											<spring:message code="expedient.tasca.list.columna.estat"/>
											<script id="cellTascaEstatTemplate" type="text/x-jsrender">
											{{if estat == 'PENDENT'}}
												<spring:message code="expedient.tasca.estat.enum.PENDENT"/>
											{{else estat == 'INICIADA'}}
												<spring:message code="expedient.tasca.estat.enum.INICIADA"/>
											{{else estat == 'FINALITZADA'}}
												<spring:message code="expedient.tasca.estat.enum.FINALITZADA"/>
											{{else estat == 'CANCELLADA'}}
												<spring:message code="expedient.tasca.estat.enum.CANCELLADA"/>
											{{else estat == 'REBUTJADA'}}
												<spring:message code="expedient.tasca.estat.enum.REBUTJADA"/>
											{{/if}}
										</script>
										</th>
										<th data-col-name="numComentaris" data-orderable="false" data-template="#cellComentarisTemplate" width="1%">
											<script id="cellComentarisTemplate" type="text/x-jsrender">
												<a href='<c:url value="/expedientTasca/{{:id}}/comentaris"/>' data-toggle="modal" data-refresh-tancar="true" data-modal-id="comentaris{{:id}}" class="btn btn-default"><span class="fa fa-lg fa-comments"></span>&nbsp;<span class="badge">{{:numComentaris}}</span></a>
											</script>
										</th>
										<th data-col-name="id" data-orderable="false" data-template="#cellExpedientTascaTemplate" width="1%">
											<script id="cellExpedientTascaTemplate" type="text/x-jsrender">
											<div class="dropdown">
												<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
												<ul class="dropdown-menu">
													<li><a href="<c:url value="/expedientTasca/{{:id}}/detall"/>" data-maximized="true" data-toggle="modal"><span class="fa fa-info"></span>&nbsp;&nbsp;<spring:message code="comu.boto.detalls"/></a></li>
													{{if estat != 'FINALITZADA'}}
														<li><a href="<c:url value="/expedientTasca/{{:id}}/reassignar"/>" data-toggle="modal"><span class="fa fa-user"></span>&nbsp;&nbsp;<spring:message code="comu.boto.reassignar"/></a></li>
													{{/if}}
													<c:if test="${((expedientAgafatPerUsuariActual && potModificarContingut) || contingut.admin) && (contingut.expedient ? contingut.estat != 'TANCAT' : contingut.expedientPare.estat != 'TANCAT')}">
														{{if estat != 'CANCELLADA' && estat != 'FINALITZADA'}}
															<li><a href="<c:url value="/expedientTasca/{{:id}}/cancellar"/>" data-confirm="<spring:message code="expedient.tasca.confirmacio.cancellar"/>"><span class="fa fa-times"></span>&nbsp;&nbsp;<spring:message code="comu.boto.cancellar"/></a></li>
														{{/if}}
													</c:if>
												</ul>
											</div>
										</script>
										</th>										
									
									</tr>
								</thead>
							</table>
						</div>
						<script id="taulaTasquesNouBoton" type="text/x-jsrender">
						<c:if test="${((expedientAgafatPerUsuariActual && potModificarContingut) || contingut.admin) && (contingut.expedient ? contingut.estat != 'TANCAT' : contingut.expedientPare.estat != 'TANCAT')}">
							<p style="text-align:right"><a href="<c:url value="/expedientTasca/${contingut.expedient ? contingut.id : contingut.expedientPare.id}/new"/>" class="btn btn-default" data-toggle="modal"><span class="fa fa-plus"></span>&nbsp;<spring:message code="contingut.boto.nova.tasca"/></a></p>
						</c:if>	
					</script>					
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
