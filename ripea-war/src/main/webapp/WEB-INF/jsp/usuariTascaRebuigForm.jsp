<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:set var="titol"><spring:message code="expedient.tasca.rebutjar.form.titol"/></c:set>
<html>
<head>
	<title>${titol}</title>
	<rip:modalHead/>
</head>
<body>
    <c:set var="formAction"><rip:modalUrl value="/usuariTasca/rebutjar"/></c:set>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="usuariTascaRebuigCommand">
		<form:hidden path="id"/>

		<rip:inputTextarea name="motiu" textKey="expedient.tasca.rebutjar.form.camp.motiu" required="true"/>
		<div id="modal-botons" class="well">
			<button type="submit" class="btn btn-success"><span class="fa fa-save"></span> <spring:message code="comu.boto.rebutjar"/></button>
			<a href="<c:url value="/usuariTasca"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>
