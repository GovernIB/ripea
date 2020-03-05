<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<html>
<head>
	<title><spring:message code="contingut.document.form.titol.importar"/></title>
	<rip:modalHead/>
</head>
<body>
	<c:set var="formAction"><rip:modalUrl value="/contingut/${importacioCommand.pareId}/importacio/new"/></c:set>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="importacioCommand">
		<br/>
		<rip:inputText name="numeroRegistre" textKey="contingut.importacio.form.camp.nom" required="true"/>
		<rip:inputSelect name="tipusRegistre" textKey="contingut.importacio.form.camp.tipus" optionItems="${tipusRegistreOptions}" optionValueAttribute="value" optionTextKeyAttribute="text"/>
		<br/>
		<div id="modal-botons" class="well">
			<button type="submit" class="btn btn-success"><span class="fa fa-save"></span>&nbsp;<spring:message code="comu.boto.importar"/></button>
			<a href="<c:url value="/contingut/${carpetaCommand.pareId}"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>
