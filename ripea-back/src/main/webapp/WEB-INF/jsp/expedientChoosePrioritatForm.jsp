<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:set var="titol"><spring:message code="expedient.prioritat.form.modificar.titol"/></c:set>

<html>
<head>
	<title>${titol}</title>
	<rip:modalHead/>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script type="text/javascript">
		$(document).ready(function(){
			changedPrioritat();
			$("#prioritat").change(function (event) {
				changedPrioritat();
			});
		});
	</script>	
</head>
<body>

	<c:set var="formAction"><rip:modalUrl value="/expedient/canviarPrioritat"/></c:set>

	<form:form action="${formAction}" method="post" cssClass="form-horizontal" modelAttribute="expedientCommand">
		<form:hidden path="id"/>
		<form:hidden path="entitatId"/>
		<form:hidden path="pareId"/>
		<rip:inputText name="nom" textKey="contingut.expedient.form.camp.nom" readonly="true"/>
		<rip:inputSelect name="prioritat" optionEnum="PrioritatEnumDto" emptyOption="false" textKey="contingut.expedient.form.camp.prioritat" templateResultFunction="showColorPriritats" />
		<rip:inputTextarea name="prioritatMotiu" textKey="expedient.form.prioritat.motiu" required="true"></rip:inputTextarea>
		<div class="h200" />
		<div id="modal-botons" class="well">
			<button type="submit" class="btn btn-success"><span class="fa fa-save"></span> <spring:message code="comu.boto.guardar"/></button>
			<a href="<c:url value="/expedient"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>
