<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:choose>
	<c:when test="${empty documentNotificacionsCommand.id}"><c:set var="titol"><spring:message code="notificacio.form.titol.crear"/></c:set></c:when>
	<c:otherwise><c:set var="titol"><spring:message code="notificacio.form.titol.modificar"/></c:set></c:otherwise>
</c:choose>
<html>
<head>
	<title>${titol}</title>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>
	<script src="<c:url value="/webjars/autoNumeric/1.9.30/autoNumeric.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<rip:modalHead/>
	
<style type="text/css">

.title {
	margin-top: 2%;
	font-size: larger;
}
.title > label {
	color: #ff9523;
}
.title > hr {
	margin-top: 0%;
}
.title-envios {
	color: #ffffff;
	margin-top: 1%;
	font-size: larger;
}
.title-envios > hr {
	margin-top: 0%;
	height: 0.4px;
	background-color: #696666;
}

.title-container {
	text-align: center;
	background-color: #696666;
	width: 12%;
}
</style>

<script>
	$(document).ready(function() {
		$('form').on('submit', function(){
			window.parent.addLoading();
		});
		$('#tipus').val("NOTIFICACIO");
		$('#tipus').trigger('change');

		//select and checkbox elements dont have readonly attribute that allows elements to be greyed out but submitted
		//in order to send disabled values in POST we need to enable them on submit
		$('#notificacioForm').on('submit', function () {
		  $(this).find('select').prop('disabled', false);
		  $(this).find( ".checkbox input" ).prop('disabled', false);
		});
	});



</script>
</head>
<body>
	<c:choose>
		<c:when test="${empty documentNotificacionsCommand.id}"><c:set var="formAction"><rip:modalUrl value="/document/${documentNotificacionsCommand.documentId}/notificar"/></c:set></c:when>
		<c:otherwise><c:set var="formAction"><rip:modalUrl value="/expedient/${expedientId}/notificacio/${documentNotificacionsCommand.id}"/></c:set></c:otherwise>
	</c:choose>
	<form:form id="notificacioForm" action="${formAction}" method="post" cssClass="form-horizontal" commandName="documentNotificacionsCommand" role="form">
		<c:if test="${empty documentNotificacionsCommand.id || documentNotificacionsCommand.tipus == 'ELECTRONICA'}">
			<ul class="nav nav-tabs" role="tablist">
				<li role="presentation" class="active"><a href="#dades" aria-controls="dades" role="tab" data-toggle="tab"><spring:message code="notificacio.form.camp.tab.dades"/></a></li>
				<%--li role="presentation"><a href="#annexos" aria-controls="annexos" role="tab" data-toggle="tab"><spring:message code="notificacio.form.camp.tab.annexos"/></a></li--%>
			</ul>
			<br/>
		</c:if>
		<rip:inputHidden name="id"/>
		<rip:inputHidden name="documentId"/>
		<div class="tab-content">
			<div role="tabpanel" class="tab-pane active" id="dades">
				<!---  TIPUS (NOTIFICACIO / COMUNICACIO) ---->
				<c:choose>
					<c:when test="${empty documentNotificacionsCommand.id}">
						<rip:inputSelect labelSize="2" name="tipus" textKey="notificacio.form.camp.tipus" optionItems="${notificacioTipusEnumOptions}" optionValueAttribute="value" optionTextKeyAttribute="text" required="true"/>
					</c:when>
					<c:otherwise>
						<rip:inputHidden name="tipus"/>
					</c:otherwise>
				</c:choose>
				<!----  ESTAT   ------->
				<rip:inputSelect disabled="true" labelSize="2" name="estat" textKey="notificacio.form.camp.estat" optionItems="${notificacioEstatEnumOptions}" optionValueAttribute="value" optionTextKeyAttribute="text" required="true"/>
				<!---  CONCEPTE   ---->
				<rip:inputText labelSize="2" name="assumpte" textKey="notificacio.form.camp.concepte" required="true"/>
				<!---  OBSERVACIONS   --->
				<rip:inputTextarea labelSize="2" name="observacions" textKey="notificacio.form.camp.descripcio"/>
				<!----  DATA PROGRAMADA   ----->
				<rip:inputDate labelSize="2" name="dataProgramada" textKey="notificacio.form.camp.data.programada" comment="notificacio.form.camp.data.programada.comment"/>
				<!----  DATA CADUCITAT  ------->
				<rip:inputDate labelSize="2" name="dataCaducitat" textKey="notificacio.form.camp.data.caducitat" comment="notificacio.form.camp.data.caducitat.comment"/>
				<!---  RETARD  ------->
				<rip:inputNumber labelSize="2" name="retard" textKey="notificacio.form.camp.retard" nombreDecimals="0" comment="notificacio.form.camp.retard.comment"/>


				<!--------------------------------------------------------  ENVIAMENTS  ------------------------------------------------------------>
				<div class="container-fluid">
					<div class="title">
						<span class="fa fa-vcard"></span> <label><spring:message
								code="notificacio.form.camp.enviaments" /></label>
						<hr />
					</div>

					<div class="container-envios">
						<c:forEach items="${documentNotificacionsCommand.enviaments}" var="enviament"
							varStatus="status">
							<c:set var="i" value="${status.index}" />
							<div class="row enviamentsForm formEnviament enviamentForm_${i}" style="margin-bottom: 30px">
								<div class="col-md-12">
									<label class="envio[${i+1}] badge badge-light"><spring:message code="notificacio.form.label.notificacio"/> ${i+1}</label>
								</div>
									
								<!-----------------------------------  TIPUS DE SERVEI  --------------------------------->	
								<rip:inputSelect labelSize="2" required="true" name="enviaments[${i}].serveiTipusEnum" optionItems="${serveiTipusEstats}" optionValueAttribute="value" optionTextKeyAttribute="text" textKey="notificacio.form.camp.serveiTipus" />


								<!---------------------------------------  TITULAR  --------------------------------------->	
								<div class="titular">
									<div class="col-md-12 title-envios">
										<div class="title-container">
											<label><spring:message code="enviament.label.titular"/></label>
										</div>
										<hr/>
									</div>
									<div class="personaForm">
										<div>
											<rip:inputHidden name="enviaments[${i}].titular.id" />
											<!----  TIPUS INTERESSAT ---->
											<div class="col-md-6">
												<rip:inputSelect disabled="true" name="enviaments[${i}].titular.tipus" textKey="interessat.form.camp.tipus" labelSize="4" optionItems="${interessatTipus}" optionValueAttribute="value" optionTextKeyAttribute="text" />
												<input type="hidden" name="enviaments[${i}].titular.tipus" />
											</div>
											<!---- NIF ---->
											<div class="col-md-6">
												<rip:inputText readonly="true" name="enviaments[${i}].titular.documentNum" textKey="interessat.nifCifDni"/>
											</div>
											<!---- NOM / RAÓ SOCIAL ---->
											<div class="col-md-6">
												<c:choose>
												<c:when test="${enviament.titular.tipus=='PERSONA_FISICA'}">
													<rip:inputText readonly="true" name="enviaments[${i}].titular.nom" textKey="interessat.nomRaoSocial" required="true" />
												</c:when>
												<c:when test="${enviament.titular.tipus=='PERSONA_JURIDICA'}">
													<rip:inputText readonly="true" name="enviaments[${i}].titular.raoSocial" textKey="interessat.nomRaoSocial" required="true" />
												</c:when>
												<c:when test="${enviament.titular.tipus=='ADMINISTRACIO'}">
													<rip:inputText readonly="true" name="enviaments[${i}].titular.organNom" textKey="interessat.nomRaoSocial" required="true" />
												</c:when>													
												</c:choose>
											</div>
											<c:if test="${enviament.titular.tipus=='PERSONA_FISICA'}">
												<!---- PRIMER LLINATGE ---->										
												<div class="col-md-6 llinatge1">
													<rip:inputText readonly="true" name="enviaments[${i}].titular.llinatge1" textKey="interessat.form.camp.llinatge1" required="true" />
												</div>
												<!---- SEGON LLINATGE ---->
												<div class="col-md-6 llinatge2">
													<rip:inputText readonly="true" name="enviaments[${i}].titular.llinatge2" textKey="interessat.form.camp.llinatge2" />
												</div>
											</c:if>
											<!---- EMAIL ---->
											<div class="col-md-6">
												<rip:inputText readonly="true" name="enviaments[${i}].titular.email" textKey="interessat.form.camp.email" />
											</div>
											<!---- TELÈFON ---->
											<div class="col-md-6">
												<rip:inputText readonly="true" name="enviaments[${i}].titular.telefon" textKey="interessat.form.camp.telefon" />
											</div>
											<!---- CODI DIR3 ---->
											<c:if test="${enviament.titular.tipus=='ADMINISTRACIO'}">
												<div class="col-md-6">
													<rip:inputText readonly="true" name="enviaments[${i}].titular.organCodi" textKey="interessat.dir3codi" required="true"/>
												</div>
											</c:if>
											<!---- INCAPACITAT ---->
											<c:if test="${enviament.titular.tipus=='PERSONA_FISICA'}">
												<div class="col-md-6">
													<rip:inputCheckbox disabled="true" name="enviaments[${i}].titular.incapacitat" textKey="interessat.form.camp.incapacitat" />
													<c:if test="${enviament.titular.incapacitat==true && empty enviament.destinatari}">
														<div class="alert alert-danger">
															<spring:message code="interessat.form.camp.incapacitat.error.nodestinatari"/>
														</div>
													</c:if>
												</div>
											</c:if>
										</div>
									</div>
								</div>
								
								<!---------------------------------------  DESTINATARI  --------------------------------------->	
								<c:if test="${not empty enviament.destinatari}">
									<div class="destinatari">
										<div class="col-md-12 title-envios">
											<div class="title-container">
												<label><spring:message code="enviament.label.destinatari"/></label>
											</div>
											<hr/>
										</div>
										<div class="personaForm">
											<div>
												<rip:inputHidden name="enviaments[${i}].destinatari.id" />
												<!----  TIPUS INTERESSAT ---->
												<div class="col-md-6">
													<rip:inputSelect disabled="true" name="enviaments[${i}].destinatari.tipus" textKey="interessat.form.camp.tipus" labelSize="4" optionItems="${interessatTipus}" optionValueAttribute="value" optionTextKeyAttribute="text" />
												</div>
												<!---- NIF ---->
												<div class="col-md-6">
													<rip:inputText readonly="true" name="enviaments[${i}].destinatari.documentNum" textKey="interessat.nifCifDni"/>
												</div>
												<!---- NOM / RAÓ SOCIAL ---->
												<div class="col-md-6">
													<c:choose>
													<c:when test="${enviament.destinatari.tipus=='PERSONA_FISICA'}">
														<rip:inputText readonly="true" name="enviaments[${i}].destinatari.nom" textKey="interessat.nomRaoSocial" required="true" />
													</c:when>
													<c:when test="${enviament.destinatari.tipus=='PERSONA_JURIDICA'}">
														<rip:inputText readonly="true" name="enviaments[${i}].destinatari.raoSocial" textKey="interessat.nomRaoSocial" required="true" />
													</c:when>
													<c:when test="${enviament.destinatari.tipus=='ADMINISTRACIO'}">
														<rip:inputText readonly="true" name="enviaments[${i}].destinatari.organNom" textKey="interessat.nomRaoSocial" required="true" />
													</c:when>													
													</c:choose>
												</div>
												<c:if test="${enviament.destinatari.tipus=='PERSONA_FISICA'}">
													<!---- PRIMER LLINATGE ---->										
													<div class="col-md-6 llinatge1">
														<rip:inputText readonly="true" name="enviaments[${i}].destinatari.llinatge1" textKey="interessat.form.camp.llinatge1" required="true" />
													</div>
													
													<!---- SEGON LLINATGE ---->
													<div class="col-md-6 llinatge2">
														<rip:inputText readonly="true" name="enviaments[${i}].destinatari.llinatge2" textKey="interessat.form.camp.llinatge2" />
													</div>
												</c:if>
												<!---- EMAIL ---->
												<div class="col-md-6">
													<rip:inputText readonly="true" name="enviaments[${i}].destinatari.email" textKey="interessat.form.camp.email" />
												</div>
												
												<!---- TELÈFON ---->
												<div class="col-md-6">
													<rip:inputText readonly="true" name="enviaments[${i}].destinatari.telefon" textKey="interessat.form.camp.telefon" />
												</div>
												<!---- CODI DIR3 ---->
												<c:if test="${enviament.destinatari.tipus=='ADMINISTRACIO'}">
													<div class="col-md-6">
														<rip:inputText readonly="true" name="enviaments[${i}].destinatari.organNom" textKey="interessat.dir3codi" required="true"/>
													</div>
												</c:if>
												<!---- INCAPACITAT ---->
												<c:if test="${enviament.destinatari.tipus=='PERSONA_FISICA'}">
													<div class="col-md-6">
														<rip:inputCheckbox disabled="true" name="enviaments[${i}].destinatari.incapacitat" textKey="interessat.form.camp.incapacitat" />
													</div>
												</c:if>
											</div>
										</div>
									</div>								
								</c:if>
								
								<!-------------------------------------  MÈTODE ENVIO  ----------------------------------->	
								<c:if test="${enviament.titular.tipus=='PERSONA_FISICA'}">
									<div class="metodeEntrega">
										<div class="col-md-12 title-envios">
											<div class="title-container entrega">
												<label><spring:message code="enviament.label.metodeEnvio"/></label>
											</div>
											<hr />
										</div>
										<div class="col-md-12">
											<rip:inputCheckbox labelSize="2" name="enviaments[${i}].entregaPostal"
												textKey="notificacio.form.camp.entregaPostal" />
										</div>
									</div>
								</c:if>
							</div>
						</c:forEach>
					</div>
				</div>
			</div>
			
			
			
<!-- 			<div role="tabpanel" class="tab-pane" id="annexos"> -->
<%-- 				<rip:inputSelect name="annexos" textKey="notificacio.form.camp.annexos" optionItems="${annexos}" emptyOption="true" optionValueAttribute="id" optionTextAttribute="nom" placeholderKey="notificacio.form.camp.annexos"/> --%>
<!-- 			</div> -->
		</div>
		<c:choose>
			<c:when test="${empty document}"><c:set var="urlTancar"><c:url value="/contingut/${expedientId}"/></c:set></c:when>
			<c:otherwise><c:set var="urlTancar"><c:url value="/contingut/${document.id}"/></c:set></c:otherwise>
		</c:choose>
		<div id="modal-botons" class="well">
			<button type="submit" class="btn btn-success"><span class="fa fa-floppy-o"></span> <spring:message code="comu.boto.notificar"/></button>
			<a href="${urlTancar}" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>
