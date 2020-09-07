<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:choose>
	<c:when test="${empty metaExpedientCommand.id}"><c:set var="titol"><spring:message code="metaexpedient.form.titol.crear"/></c:set></c:when>
	<c:otherwise><c:set var="titol"><spring:message code="metaexpedient.form.titol.modificar"/></c:set></c:otherwise>
</c:choose>
<html>
<head>
	<title>${titol}</title>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<rip:modalHead/>
	
	<c:if test="${not isRolAdminOrgan}">
		<script type="text/javascript">
		$(document).ready(function() {
			var selectOrganGestorContainer = $("select#organGestorId").parent().parent(); 
			selectOrganGestorContainer.hide();
			$( "#select-grup-metaexpedient" ).change(function () {
			    var value = $(this).val();
			    console.log(value);
			    if (value == 0){
			    	selectOrganGestorContainer.hide();
			    } else if (value == 1) {
			    	selectOrganGestorContainer.show();
			    }
		  	});
		});
		</script>
	</c:if>
</head>
<body>
	<c:set var="formAction"><rip:modalUrl value="/metaExpedient"/></c:set>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="metaExpedientCommand">
		<ul class="nav nav-tabs" role="tablist">
			<li role="presentation" class="active"><a href="#dades" aria-controls="dades" role="tab" data-toggle="tab"><spring:message code="metaexpedient.form.camp.tab.dades"/></a></li>
			<li role="presentation"><a href="#notificacions" aria-controls="notificacions" role="tab" data-toggle="tab"><spring:message code="metaexpedient.form.camp.tab.notificacions"/></a></li>
		</ul>
		<form:hidden path="id"/>
		<form:hidden path="entitatId"/>
		<form:hidden path="RolAdminOrgan"/>
		<br/>
		<div class="tab-content">
			<div role="tabpanel" class="tab-pane active" id="dades">
				<rip:inputText name="codi" textKey="metaexpedient.form.camp.codi" required="true"/>
				<rip:inputText name="nom" textKey="metaexpedient.form.camp.nom" required="true"/>
				<rip:inputTextarea name="descripcio" textKey="metaexpedient.form.camp.descripcio"/>
				<rip:inputText name="classificacioSia" textKey="metaexpedient.form.camp.classificacio.sia" required="true"/>
				<rip:inputText name="serieDocumental" textKey="metaexpedient.form.camp.serie.doc" required="true"/>
				<c:if test="${not isRolAdminOrgan}">
					<div class="form-group">
						<label class="control-label col-xs-4" for="select-grup-metaexpedient"><spring:message code="metaexpedient.form.camp.gestor"/></label>
						<div class="controls col-xs-8">
						<select id="select-grup-metaexpedient" class="form-control" data-toggle="select2"
								data-minimumresults="-1">
							<option value="0"><spring:message code="metaexpedient.form.camp.gestor.entitat"/></option>
							<option value="1"><spring:message code="metaexpedient.form.camp.gestor.organgestor"/></option>
						</select>
						</div>
					</div>						
				</c:if>
				<c:url value="/organgestorajax/organgestor" var="urlConsultaInicial"/>
				<c:url value="/organgestorajax/organgestor" var="urlConsultaLlistat"/>
				<rip:inputSuggest 
 					name="organGestorId"  
 					urlConsultaInicial="${urlConsultaInicial}"
 					urlConsultaLlistat="${urlConsultaLlistat}"
 					textKey="metaexpedient.form.camp.organgestor"
 					suggestValue="id"
 					suggestText="nom"
 					required="${ isRolAdminOrgan }"/>		 
				<rip:inputText name="expressioNumero" textKey="metaexpedient.form.camp.expressio.numero" comment="metaexpedient.form.camp.expressio.numero.comentari"/>
				<rip:inputCheckbox name="permetMetadocsGenerals" textKey="metaexpedient.form.camp.metadocs.nolligats.permetre"/>
			</div>
			<div role="tabpanel" class="tab-pane" id="notificacions">
				<rip:inputCheckbox name="notificacioActiva" textKey="metaexpedient.form.camp.notificacio.activa"/>
			</div>
		</div>
		<div id="modal-botons">
			<button type="submit" class="btn btn-success"><span class="fa fa-save"></span> <spring:message code="comu.boto.guardar"/></button>
			<a href="<c:url value="/metaExpedient"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>
