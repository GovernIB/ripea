<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<html>
<head>
	<title><spring:message code="contingut.pinbal.form.titol"/></title>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/webjars/autoNumeric/1.9.30/autoNumeric.js"/>"></script>
	<rip:modalHead/>
<script>
const metaDocumentServeiScsp = [];
const metaDocumentFinalitat = [];
<c:forEach var="metaDocument" items="${metaDocuments}">
metaDocumentServeiScsp[${metaDocument.id}] = "${metaDocument.pinbalServei}";
metaDocumentFinalitat[${metaDocument.id}] = "${metaDocument.pinbalFinalitat}";
</c:forEach>
$(document).ready(function() {
	$('#metaDocumentId').on('change', function() {
		if (metaDocumentServeiScsp[$(this).val()] === "SVDCCAACPASWS01") { // SVDCCAACPASWS01 - Estar al corriente de obligaciones tributarias para solicitud de subvenciones y ayudas de la CCAA
			$('#bloc-datos-especificos').show();
			$('#divComunitatAutonomaCodi').show();
			$('#divProvinciaCodi').show();
			$('#divMunicipiCodi').hide();
			$('#divDataConsulta').hide();
			$('#divDataNaixement').hide();
			$('#divConsentimentTipusDiscapacitat').hide();
			$('#divNumeroTitol').hide();

			$('#divPaisNaixament').hide();
			$('#divCodiNacionalitat').hide();
			$('#divProvinciaNaixament').hide();
			$('#divPoblacioNaixament').hide();
			$('#divCodiPoblacioNaixament').hide();
			$('#divSexe').hide();
			$('#divNomPare').hide();
			$('#divNomMare').hide();
			$('#divTelefon').hide();
			$('#divEmail').hide();
			$('#divDataNaixementObligatori').hide();
			
			$('#divAnysHistoric').hide();
			
			
		} else if (metaDocumentServeiScsp[$(this).val()] === "SVDSCDDWS01") { // SVDSCDDWS01 - Servei de consulta de dades de discapacitat
			$('#bloc-datos-especificos').show();
			$('#divComunitatAutonomaCodi').show();
			$('#divProvinciaCodi').show();
			$('#divMunicipiCodi').hide();
			$('#divDataConsulta').show();
			$('#divDataNaixement').show();
			$('#divConsentimentTipusDiscapacitat').show();
			$('#divNumeroTitol').hide();

			$('#divPaisNaixament').hide();
			$('#divCodiNacionalitat').hide();
			$('#divProvinciaNaixament').hide();
			$('#divPoblacioNaixament').hide();
			$('#divCodiPoblacioNaixament').hide();
			$('#divSexe').hide();
			$('#divNomPare').hide();
			$('#divNomMare').hide();
			$('#divTelefon').hide();
			$('#divEmail').hide();
			$('#divDataNaixementObligatori').hide();
			
			$('#divAnysHistoric').hide();
				
				
		} else if (metaDocumentServeiScsp[$(this).val()] === "SCDCPAJU") { // SCDCPAJU - Servei de consulta de padró de convivència
			$('#bloc-datos-especificos').show();
			$('#divComunitatAutonomaCodi').hide();
			$('#divProvinciaCodi').show();
			$('#divMunicipiCodi').show();
			$('#divDataConsulta').hide();
			$('#divDataNaixement').hide();
			$('#divConsentimentTipusDiscapacitat').hide();
			$('#divNumeroTitol').hide();

			$('#divPaisNaixament').hide();
			$('#divCodiNacionalitat').hide();
			$('#divProvinciaNaixament').hide();
			$('#divPoblacioNaixament').hide();
			$('#divCodiPoblacioNaixament').hide();
			$('#divSexe').hide();
			$('#divNomPare').hide();
			$('#divNomMare').hide();
			$('#divTelefon').hide();
			$('#divEmail').hide();
			$('#divDataNaixementObligatori').hide();
			
			$('#divAnysHistoric').hide();
			
			
		} else if (metaDocumentServeiScsp[$(this).val()] === "SVDSCTFNWS01") { // SVDSCTFNWS01 - Servei de consulta de família nombrosa
			$('#bloc-datos-especificos').show();
			$('#divComunitatAutonomaCodi').show();
			$('#divProvinciaCodi').hide();
			$('#divMunicipiCodi').hide();
			$('#divDataConsulta').show();
			$('#divDataNaixement').show();
			$('#divConsentimentTipusDiscapacitat').hide();
			$('#divNumeroTitol').show();

			$('#divPaisNaixament').hide();
			$('#divCodiNacionalitat').hide();
			$('#divProvinciaNaixament').hide();
			$('#divPoblacioNaixament').hide();
			$('#divCodiPoblacioNaixament').hide();
			$('#divSexe').hide();
			$('#divNomPare').hide();
			$('#divNomMare').hide();
			$('#divTelefon').hide();
			$('#divEmail').hide();
			$('#divDataNaixementObligatori').hide();
			
			$('#divAnysHistoric').hide();
			
			
		} else if (metaDocumentServeiScsp[$(this).val()] === "SVDCCAACPCWS01") { // SVDCCAACPCWS01 - Estar al corriente de obligaciones tributarias para contratación con la CCAA
			$('#bloc-datos-especificos').show();
			$('#divComunitatAutonomaCodi').show();
			$('#divProvinciaCodi').show();
			$('#divMunicipiCodi').hide();
			$('#divDataConsulta').hide();
			$('#divDataNaixement').hide();
			$('#divConsentimentTipusDiscapacitat').hide();
			$('#divNumeroTitol').hide();

			$('#divPaisNaixament').hide();
			$('#divCodiNacionalitat').hide();
			$('#divProvinciaNaixament').hide();
			$('#divPoblacioNaixament').hide();
			$('#divCodiPoblacioNaixament').hide();
			$('#divSexe').hide();
			$('#divNomPare').hide();
			$('#divNomMare').hide();
			$('#divTelefon').hide();
			$('#divEmail').hide();
			$('#divDataNaixementObligatori').hide();
			
			$('#divAnysHistoric').hide();
			
			
		} else if (metaDocumentServeiScsp[$(this).val()] === "SVDDELSEXWS01") { // SVDDELSEXWS01 - Consulta de inexistencia de delitos sexuales por datos de filiación
			$('#bloc-datos-especificos').show();
			$('#divComunitatAutonomaCodi').hide();
			$('#divProvinciaCodi').hide();
			$('#divMunicipiCodi').hide();
			$('#divDataConsulta').hide();
			$('#divDataNaixement').hide();
			$('#divConsentimentTipusDiscapacitat').hide();
			$('#divNumeroTitol').hide();

			$('#divPaisNaixament').show();
			$('#divCodiNacionalitat').show();
			$('#divProvinciaNaixament').show();
			$('#divPoblacioNaixament').show();
			$('#divCodiPoblacioNaixament').show();
			$('#divSexe').show();
			$('#divNomPare').show();
			$('#divNomMare').show();
			$('#divTelefon').show();
			$('#divEmail').show();
			$('#divDataNaixementObligatori').show();
			
			$('#divAnysHistoric').hide();
			
		} else if (metaDocumentServeiScsp[$(this).val()] === "SCDHPAJU") { // SCDHPAJU - Servei de consulta de padró històric
			$('#bloc-datos-especificos').show();
			$('#divComunitatAutonomaCodi').hide();
			$('#divProvinciaCodi').show();
			$('#divMunicipiCodi').show();
			$('#divDataConsulta').hide();
			$('#divDataNaixement').hide();
			$('#divConsentimentTipusDiscapacitat').hide();
			$('#divNumeroTitol').hide();

			$('#divPaisNaixament').hide();
			$('#divCodiNacionalitat').hide();
			$('#divProvinciaNaixament').hide();
			$('#divPoblacioNaixament').hide();
			$('#divCodiPoblacioNaixament').hide();
			$('#divSexe').hide();
			$('#divNomPare').hide();
			$('#divNomMare').hide();
			$('#divTelefon').hide();
			$('#divEmail').hide();
			$('#divDataNaixementObligatori').hide();
			
			$('#divAnysHistoric').show();
														
		} else {
			$('#bloc-datos-especificos').hide();
		}
		$('#finalitat').val(metaDocumentFinalitat[$(this).val()]);

		
		const metaDocumentId = $(this).val();
		$.get("<c:url value="/contingut"/>" + "/${pinbalConsultaCommand.pareId}/pinbal/titulars/" + metaDocumentId)
			.done(function(data) {
				
				$('#interessatId').select2('val', '', true);
				$('#interessatId option[value!=""]').remove();
				for (var i = 0; i < data.length; i++) {
					$('#interessatId').append('<option value="' + data[i].id + '">' + data[i].identificador + '</option>');
				}
			})
			.fail(function() {
				alert("<spring:message code="error.jquery.ajax"/>");
			});
		
	});
	$('#metaDocumentId').trigger('change');

	
});
</script>
</head>
<body>
	<c:set var="formAction"><rip:modalUrl value="/contingut/${pinbalConsultaCommand.pareId}/pinbal/new"/></c:set>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="pinbalConsultaCommand">
		<form:hidden path="entitatId"/>
		<form:hidden path="pareId"/>
		<rip:inputSelect name="metaDocumentId" textKey="contingut.pinbal.form.camp.metanode" required="true" optionItems="${metaDocuments}" optionValueAttribute="id" optionTextAttribute="nom"/>
		<rip:inputSelect name="interessatId" textKey="contingut.pinbal.form.camp.interessat" required="true" optionItems="${interessats}" optionValueAttribute="id" optionTextAttribute="identificador"/>
		<rip:inputSelect name="consentiment" textKey="contingut.pinbal.form.camp.consentiment" required="true" optionItems="${consentimentOptions}" optionValueAttribute="value" optionTextKeyAttribute="text"/>
		<rip:inputTextarea name="finalitat" textKey="contingut.pinbal.form.camp.finalitat" required="true" maxlength="256"/>
		<div id="bloc-datos-especificos">
			<ul class="nav nav-tabs" role="tablist">
				<li role="presentation" class="active">
					<a href="#datos-especificos" aria-controls="fitxer" role="tab" data-toggle="tab"><spring:message code="contingut.pinbal.form.datos.especificos"/></a>
				</li>
			</ul>
			<br/>
			<div class="tab-content">
				<div role="tabpanel" class="tab-pane active" id="datos-especificos">
					<div id="divComunitatAutonomaCodi"><rip:inputSelect name="comunitatAutonomaCodi" textKey="contingut.pinbal.form.camp.comunitat.autonoma" optionItems="${comunitats}" optionValueAttribute="value" optionTextAttribute="text"/></div>
					<div id="divProvinciaCodi"><rip:inputSelect name="provinciaCodi" textKey="contingut.pinbal.form.camp.provincia" optionItems="${provincies}" optionValueAttribute="value" optionTextAttribute="text"/></div>
					<div id="divMunicipiCodi"><rip:inputSelect name="municipiCodi" textKey="interessat.form.camp.organ.filtre.municipi" optionItems="${municipis}" optionValueAttribute="codi" optionTextAttribute="nom"/></div>
					<div id="divDataConsulta"><rip:inputDate name="dataConsulta" textKey="contingut.pinbal.form.camp.data.consulta" /></div>
					<div id="divDataNaixement"><rip:inputDate name="dataNaixement" textKey="contingut.pinbal.form.camp.data.naixement" /></div>
					<div id="divConsentimentTipusDiscapacitat"><rip:inputSelect name="consentimentTipusDiscapacitat" textKey="contingut.pinbal.form.camp.consentiment.tipus.discapacitat" optionEnum="SiNoEnumDto"/></div>
					<div id="divNumeroTitol"><rip:inputText name="numeroTitol" textKey="contingut.pinbal.form.camp.numero.titol" /></div>
					
				  	<div id="divCodiNacionalitat"><rip:inputSelect name="codiNacionalitat" textKey="contingut.pinbal.form.camp.pais.nacionalitat" optionItems="${paisos}" optionValueAttribute="codi" optionTextAttribute="nom"/></div>
				  	<div id="divSexe"><rip:inputSelect name="sexe" textKey="contingut.pinbal.form.camp.sexe" optionEnum="SexeEnumDto" emptyOption="true"/></div>
				  	<div id="divPaisNaixament">	<rip:inputSelect name="paisNaixament" textKey="contingut.pinbal.form.camp.pais.naixament" optionItems="${paisos}" optionValueAttribute="codi" optionTextAttribute="nom"/></div>
				  	<div id="divProvinciaNaixament"><rip:inputSelect name="provinciaNaixament" textKey="contingut.pinbal.form.camp.provincia.naixament" optionItems="${provincies}" optionValueAttribute="value" optionTextAttribute="text" comment="contingut.pinbal.form.camp.provincia.naixament.comment"/></div>
				  	<div id="divPoblacioNaixament"><rip:inputText name="poblacioNaixament" textKey="contingut.pinbal.form.camp.poblacio.naixament" comment="contingut.pinbal.form.camp.poblacio.naixament.comment"/></div>
				  	<div id="divCodiPoblacioNaixament"><rip:inputSelect name="municipiNaixament" textKey="contingut.pinbal.form.camp.municipi.naixament" optionItems="${municipis}" optionValueAttribute="codi" optionTextAttribute="nom" emptyOption="true" comment="contingut.pinbal.form.camp.codi.poblacio.naixament.comment" optionMinimumResultsForSearch="0"/></div>
					<div id="divNomPare"><rip:inputText name="nomPare" textKey="contingut.pinbal.form.camp.nom.pare" comment="contingut.pinbal.form.camp.nom.pare.comment"/></div>
					<div id="divNomMare"><rip:inputText name="nomMare" textKey="contingut.pinbal.form.camp.nom.mare" comment="contingut.pinbal.form.camp.nom.mare.comment"/></div>
					<div id="divDataNaixementObligatori"><rip:inputDate name="dataNaixementObligatori" textKey="contingut.pinbal.form.camp.data.naixement" required="true"/></div>
					<div id="divTelefon"><rip:inputText name="telefon" textKey="contingut.pinbal.form.camp.telefon"/></div>
					<div id="divEmail"><rip:inputText name="email" textKey="contingut.pinbal.form.camp.email"/></div>
					
					<div id="divAnysHistoric"><rip:inputNumber name="nombreAnysHistoric" textKey="contingut.pinbal.form.camp.data.nombre.anys.historic" nombreDecimals="0"/></div>
			
					
				</div>
			</div>
		</div>
		<div id="modal-botons" class="well">
			<button type="submit" class="btn btn-success"><span class="fa fa-save"></span> <spring:message code="comu.boto.enviar"/></button>
			<a href="<c:url value="/contingut/${documentCommand.pareId}"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>
