<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:set var="titol"><spring:message code="expedient.tasca.duracioData.titol"/></c:set>

<html>
<head>
	<title>${titol}</title>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>
	<rip:modalHead/>
	
	<script type="text/javascript">
		$(document).ready(function() {
			$('#duracio').on('blur', function(event) { 
				$('#dataInici').focus();
				event.preventDefault();
				return false;
			});			
			$('#duracio').on('change', function() {
				$.post("<c:url value="/expedientTasca/"/>" + $('#id').val() + "/changedDuracio",
				$("#expedientTascaDto").serialize())
				.done(function(data){
					$('#dataLimit').val(data.dataLimitString);
					remarcaElement($('#dataLimit'));
				})
				.fail(function() {
					alert("<spring:message code="error.jquery.ajax"/>");
				});
			});
		
			$('#dataLimit').on('change', function() {
				$.post("<c:url value="/expedientTasca/"/>" + $('#id').val() + "/changedDataLimit",
				$("#expedientTascaDto").serialize())
				.done(function(data){
					$('#duracio').val(data.duracio);
					remarcaElement($('#duracio'));
				})
				.fail(function() {
					alert("<spring:message code="error.jquery.ajax"/>");
				});
			});
		});
	</script>
</head>
<body>
	<form:form action="" method="post" cssClass="form-horizontal" commandName="expedientTascaDto">
		<form:hidden path="id"/>
		<form:hidden path="titol"/>
		
		<rip:inputDate
			name="dataInici"
			textKey="expedient.tasca.form.camp.dataInici"
			readonly="true"/>
		
		<rip:inputText
				name="duracio"
				textKey="tasca.list.column.duracio"
				tooltip="true"
				comment="tasca.list.column.duracio.tip"
				tooltipMsg="tasca.list.column.duracio.tip"/>

		<rip:inputDate
			name="dataLimit"
			textKey="expedient.tasca.list.boto.dataLimit"/>
		
		<div class="row" style="margin-bottom: 160px;"></div>
		
		<div id="modal-botons">
			<button type="submit" class="btn btn-success"><span class="fa fa-save"></span> <spring:message code="comu.boto.guardar"/></button>
			<a href="<c:url value="/metaDada"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>

	</form:form>
</body>
</html>