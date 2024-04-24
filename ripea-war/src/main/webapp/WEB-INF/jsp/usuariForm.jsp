<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
	<style>
		.panel-body .control-label { text-align: left; }
	</style>
</head>
<body>

<c:set var="formAction"><rip:modalUrl value="/usuari/configuracio"/></c:set>
<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="usuariCommand" role="form">
	<form:hidden path="codi"/>
	<rip:inputText name="nom" textKey="usuari.form.camp.nom" readonly="true"/>
	<rip:inputText name="nif" textKey="usuari.form.camp.nif" readonly="true"/>
	<rip:inputText name="email" textKey="usuari.form.camp.email" readonly="true"/>
	<rip:inputText name="emailAlternatiu" textKey="usuari.form.camp.email.alternatiu"/>
	<rip:inputSelect name="rols" textKey="usuari.form.camp.rols" optionItems="${usuariCommand.rols}" disabled="true"/>
	<form:hidden path="rols"/>
	<rip:inputSelect name="idioma" optionItems="${idiomaEnumOptions}" textKey="usuari.form.camp.idioma" optionValueAttribute="value" optionTextKeyAttribute="text" disabled="false"/>

	<div class="row">
		<div class="col-md-offset-4 col-md-8">
			<div class="panel panel-success">
				<div class="panel-heading">
					<h3 class="panel-title">Enviament de correus</h3>
				</div>
				<div class="panel-body">
					<rip:inputIosCheckbox name="rebreEmailsAgrupats" labelSize="10" textKey="usuari.form.camp.rebre.emails.agrupats" icon="fa-envelope-o"/>
					<rip:inputIosCheckbox name="rebreAvisosNovesAnotacions" labelSize="10" textKey="usuari.form.camp.rebre.avisos.noves.anotacions" icon="fa-bell-o"/>
				</div>
			</div>
		</div>
	</div>
	<div class="row">
		<div class="col-md-offset-4 col-md-8">
			<div class="panel panel-info">
				<div class="panel-heading">
					<h3 class="panel-title">Configuració genèriques</h3>
				</div>
				<div class="panel-body">
					<rip:inputSelect name="numElementsPagina" optionItems="${numElementsPagina}" optionValueAttribute="id" optionTextAttribute="nom" textKey="usuari.form.camp.numElementsPagina"/>
					<c:if test="${fn:length(procediments) > 1 }">
						<rip:inputSelect name="procedimentId" optionItems="${procediments}" optionMinimumResultsForSearch="1" optionValueAttribute="id" emptyOption="true" optionTextAttribute="codiSiaINom" textKey="usuari.form.camp.procedimentPerDefecte"/>
					</c:if>
					<c:if test="${fn:length(entitats) > 1 }">
						<rip:inputSelect name="entitatPerDefecteId" optionItems="${entitats}" optionMinimumResultsForSearch="1" optionValueAttribute="id" emptyOption="true" optionTextAttribute="nom" textKey="usuari.form.camp.entitatPerDefecte"/>
					</c:if>
				</div>
			</div>
		</div>
	</div>
	<div class="row">
		<div class="col-md-offset-4 col-md-8">
			<div class="panel panel-warning">
				<div class="panel-heading">
					<h3 class="panel-title">Configuració de columnes del llistat d'expedients</h3>
				</div>
				<div class="panel-body">
					<rip:inputIosCheckbox name="expedientListDataDarrerEnviament" labelSize="10" textKey="usuari.form.camp.expedientListDataDarrerEnviament" icon="fa-calendar"/>
					<rip:inputIosCheckbox name="expedientListAgafatPer" labelSize="10" textKey="usuari.form.camp.expedientListAgafatPer" icon="fa-lock"/>
					<rip:inputIosCheckbox name="expedientListInteressats" labelSize="10" textKey="usuari.form.camp.expedientListInteressats" icon="fa-users"/>
					<rip:inputIosCheckbox name="expedientListComentaris" labelSize="10" textKey="usuari.form.camp.expedientListComentaris" icon="fa-comments"/>
					<rip:inputIosCheckbox name="expedientListGrup" labelSize="10" textKey="usuari.form.camp.expedientListGrup" icon="fa-user-circle"/>
				</div>
			</div>
		</div>
	</div>
	<div class="row">
		<div class="col-md-offset-4 col-md-8">
			<div class="panel panel-default">
				<div class="panel-heading">
					<h3 class="panel-title">Configuració vista de documents d'expedients</h3>
				</div>
				<div class="panel-body">
					<rip:inputSelect name="vistaActual" optionEnum="ContingutVistaEnumDto" textKey="usuari.form.camp.tipusVistaPerDefecte"/>
					<rip:inputIosCheckbox name="expedientExpandit" labelSize="10" textKey="usuari.form.camp.expedientExpandit" icon="fa-expand"/>
				</div>
			</div>
		</div>
	</div>

	<div id="modal-botons">
		<button id="btnSubmit" type="submit" class="btn btn-success"><span class="fa fa-save"></span> <spring:message code="comu.boto.guardar"/></button>
		<a href="<c:url value="/usuari/configuracio"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
	</div>
</form:form>
</body>
<head>
	<title><spring:message code="usuari.form.titol"/></title>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<rip:modalHead/>
<script type="text/javascript">
$(document).ready(function() {

	let oldNumElementsPagina = $('#numElementsPagina').val();
	$('#btnSubmit').click(function() {
	    let newNumElementsPagina = $('#numElementsPagina').val();
	    if (oldNumElementsPagina != newNumElementsPagina) {
	        for (var key in sessionStorage) {
	            if (key.startsWith("DataTables_")) {
	                sessionStorage.removeItem(key);
	            }
	        }
	    }
	});

});
</script>
</head>
</html>
