<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>


<c:set var="titol"><spring:message code="expedient.estat.form.modificar.titol"/></c:set>

<html>
<head>
	<title>${titol}</title>
	<rip:modalHead/>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script type="application/javascript">
		var colorsEstats = {};
		<c:forEach items="${expedientEstats}" var="estat">
		colorsEstats['${estat.id}'] = '${estat.color}';
		</c:forEach>

		function showColor(element) {
			const id = element.id;
			const color = colorsEstats[id];
			if (!color) {
				return $('<span class="no-icon"></span><span>' + element.text + '</span>');
			}
			return $('<span class="color-icon" style="background-color: ' + color + '"></span><span>' + element.text + '</span>');
		}
	</script>
</head>
<body>


	<c:set var="formAction"><rip:modalUrl value="/expedient/canviarEstat"/></c:set>

	<form:form action="${formAction}" method="post" cssClass="form-horizontal" modelAttribute="expedientCommand">
		<form:hidden path="id"/>
		<form:hidden path="entitatId"/>
		<form:hidden path="pareId"/>
		<rip:inputText name="nom" textKey="contingut.expedient.form.camp.nom" disabled="true"/>
		<rip:inputSelect name="expedientEstatId" textKey="expedient.estat.form.camp"  optionItems="${expedientEstats}" optionValueAttribute="id" optionTextAttribute="nom" templateResultFunction="showColor"/>
		<div id="modal-botons" class="well">
			<button type="submit" class="btn btn-success"><span class="fa fa-save"></span> <spring:message code="comu.boto.guardar"/></button>
			<a href="<c:url value="/expedient"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>
