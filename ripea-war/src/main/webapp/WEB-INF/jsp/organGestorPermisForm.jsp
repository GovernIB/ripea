<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:choose>
	<c:when test="${empty permisCommand.id}"><c:set var="titol"><spring:message code="organgestor.permis.form.titol.crear"/></c:set></c:when>
	<c:otherwise><c:set var="titol"><spring:message code="organgestor.permis.form.titol.modificar"/></c:set></c:otherwise>
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
<script>
	$(document).ready(function() {
		
		$("#modal-botons button[type='submit']").on('click', function() {
			$("form#permisOrganGestorCommand *:disabled").attr('readonly', 'readonly');
			$("form#permisOrganGestorCommand *:disabled").removeAttr('disabled');
		});

		$("#selectAll").on('change', function() {
			if ($(this).prop("checked"))
				$("#permisosExpMarc :checkbox").prop('checked', true);
			else
				$("#permisosExpMarc :checkbox").prop('checked', false);
		});
		$("#permisosExpMarc :checkbox").on('change', function() {
			var totsSeleccionats = true;
			$("#permisosExpMarc :checkbox").each(function() {
				  if(!$(this).prop('checked'))
					  totsSeleccionats = false;
			});
			$("#selectAll").prop('checked', totsSeleccionats);
		});
		$("#administration").change(() => {
			if (!$("#administration").prop('checked')) {
				$("#administrationComuns").prop("checked", false);
				habilitarTotsPermisosBase();
			} else {
				marcarTotsPermisosBase();
			}
		});

		$("#administrationComuns").change(() => {
			if ($("#administrationComuns").prop("checked")) {
				$("#administration").prop("checked", true);
				marcarTotsPermisosBase();
			}
		});

		if ($("#administration").prop('checked')) {
			marcarTotsPermisosBase();
		}
	});

	function marcarTotsPermisosBase() {
		$("#permisosExpMarc :checkbox").each(function() {
			  $(this).prop("checked", true);
			  $(this).attr("disabled", "disabled");
		});
		$("#selectAll").prop('checked', true);
		$("#selectAll").attr("disabled", "disabled");
	}

	function habilitarTotsPermisosBase() {
		$("#permisosExpMarc :checkbox").each(function() {
			  $(this).removeAttr("disabled");
		});
		$("#selectAll").removeAttr("disabled");
	}
	
</script>
<style>
	.permisosInput {margin-left: 45px}
</style>
</head>
<body>
	<c:set var="formAction"><rip:modalUrl value="/organgestor/permis"/></c:set>
	<c:if test="${not empty permisOrganGestorCommand.organGestorId}">
		<c:set var="formAction"><rip:modalUrl value="/organgestor/${ permisOrganGestorCommand.organGestorId }/permis"/></c:set>
 	</c:if>	
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="permisOrganGestorCommand">
		<form:hidden path="id"/>
		<rip:inputSelect 
			name="organGestorId" 
			textKey="organgestor.permis.form.camp.organ" 
			disabled="${not empty permisOrganGestorCommand.organGestorId}"
			emptyOption="true"
			emptyOptionTextKey="organgestor.permis.form.camp.organ.opcio.cap"
			optionItems="${ organsGestors }"
			optionValueAttribute="id"
			optionTextAttribute="nom"
			required="true"
			labelSize="3"
			optionMinimumResultsForSearch="5"/>
			
		<c:if test="${not empty permisOrganGestorCommand.organGestorId}">
			<form:hidden path="organGestorId"/>
	 	</c:if>
	 			 
		<rip:inputSelect 
			name="principalTipus"
			textKey="organgestor.permis.form.camp.tipus"
			disabled="${not empty permisOrganGestorCommand.id}"
			optionEnum="PrincipalTipusEnumDto"
			labelSize="3"/>
		
		<c:choose>
			<c:when test="${empty permisOrganGestorCommand.id}">
				<rip:inputText 
					name="principalNom"
					textKey="organgestor.permis.form.camp.principal"
					required="true"
					placeholderKey="organgestor.permis.form.camp.principal"
					labelSize="3"/>
			</c:when>
			<c:otherwise>
				<rip:inputText
					name="principalCodiNom"
					textKey="organgestor.permis.form.camp.principal"
					disabled="true"
					required="true"
					placeholderKey="organgestor.permis.form.camp.principal"
					labelSize="3"/>
				<form:hidden path="principalNom"/>
			</c:otherwise>
		</c:choose>
		
		<div class="row">

			<label class="control-label col-xs-3">Permisos</label>
			
			<div class="col-xs-5">
			
				<rip:inputCheckbox name="selectAll"
					textKey="organgestor.permis.form.camp.all"
					labelSize="5"/>
				
				<div id="permisosExpMarc" style="border: 1px solid lightgray;">
					<rip:inputCheckbox name="read" 
						textKey="organgestor.permis.form.camp.consulta"
						faClassInfoIcon="fa-info-circle"
						comment="organgestor.permis.form.info.a"/>
					<rip:inputCheckbox name="create"
						textKey="organgestor.permis.form.camp.creacio"
						faClassInfoIcon="fa-info-circle"
						comment="organgestor.permis.form.info.c"/>					
					<rip:inputCheckbox name="write" 
						textKey="organgestor.permis.form.camp.modificacio"
						faClassInfoIcon="fa-info-circle"
						comment="organgestor.permis.form.info.u"/>
					<rip:inputCheckbox name="delete" 
						textKey="organgestor.permis.form.camp.eliminacio"
						faClassInfoIcon="fa-info-circle"
						comment="organgestor.permis.form.info.d"/>
				</div>			
			
			</div>
			
			<div class="col-xs-4 pull-right" style="padding-top: 45px;">
				<rip:inputCheckbox
					name="procedimentsComuns" 
					labelSize="10"
					inputClass="pull-right"
					textKey="organgestor.permis.form.camp.procedimentsComuns"
					faClassInfoIcon="fa-info-circle"
					comment="organgestor.permis.form.info.comuns"/>
				<rip:inputCheckbox 
					name="administration"
					labelSize="10"
					inputClass="pull-right" 
					textKey="organgestor.permis.form.camp.administracio"
					faClassInfoIcon="fa-info-circle"
					comment="organgestor.permis.form.info.adm"/>
				<rip:inputCheckbox 
					name="administrationComuns" 
					labelSize="10"
					inputClass="pull-right"
					textKey="organgestor.permis.form.camp.administracio.comuns"
					faClassInfoIcon="fa-info-circle"
					comment="organgestor.permis.form.info.admCom"/>
				<rip:inputCheckbox 
					name="disseny" 
					labelSize="10"
					inputClass="pull-right"
					textKey="organgestor.permis.columna.disseny"
					faClassInfoIcon="fa-info-circle"
					comment="organgestor.permis.form.info.diss"/>
			</div>
		</div>
		
		<div id="modal-botons">
			<button type="submit" class="btn btn-success"><span class="fa fa-save"></span>
				<c:choose>
					<c:when test="${empty permisOrganGestorCommand.id}"><spring:message code="comu.boto.crear"/></c:when>
					<c:otherwise><spring:message code="comu.boto.modificar"/></c:otherwise>
				</c:choose>
			</button>	
			<a href="<c:url value="organgestor/permis"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>