<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:set var="titol">
	<spring:message code="contingut.expedient.tancar.form.titol" />
</c:set>
<html>
<head>
<title>${titol}</title>
<rip:modalHead />
</head>
<body>
	<c:set var="formAction">
		<rip:modalUrl value="/expedientPeticio/rebutjar" />
	</c:set>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal"
		commandName="expedientPeticioRebutjarCommand">
		<form:hidden path="id" />



		<rip:inputSelect name="metaExpedientId" textKey="expedientPeticio.form.acceptar.camp.metaExpedient"
			required="true" optionItems="${metaExpedients}" optionValueAttribute="id"
			optionTextAttribute="nom" disabled="true" labelSize="2" style="display:none" />

		<rip:inputSelect name="expedientId" textKey="expedientPeticio.form.acceptar.camp.expedient"
			required="true" optionItems="${metaExpedients}" optionValueAttribute="id"
			optionTextAttribute="nom" disabled="true" labelSize="2" />

		<rip:inputText name="newExpedientTitol" textKey=expedientPeticio.form.acceptar.camp.newExpedientTitol"
			required="true" labelSize="2" style="display:none"/>

		<rip:inputCheckbox name="associarInteressats"
			textKey="expedientPeticio.form.acceptar.camp.associarInteressats" />
			
		<rip:inputCheckbox name="associarDocuments"
			textKey="expedientPeticio.form.acceptar.camp.associarDocuments" />


		<rip:inputText name="any" textKey="expedientPeticio.form.acceptar.camp.any" required="true"
			labelSize="2" style="display:none" />

		<div id="modal-botons" class="well">
			<button type="submit" class="btn btn-success">
				<span class="fa fa-save"></span>
				<spring:message code="comu.boto.guardar" />
			</button>
			<a href="<c:url value="/expedientPeticio"/>" class="btn btn-default"
				data-modal-cancel="true"><spring:message code="comu.boto.cancelar" /></a>
		</div>
	</form:form>
</body>
</html>

