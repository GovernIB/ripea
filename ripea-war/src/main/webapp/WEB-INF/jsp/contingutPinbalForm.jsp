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
		
		let serveiNom = metaDocumentServeiScsp[$(this).val()];
		showHideDatosEspecificos('#div' + serveiNom);
						

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

	function showHideDatosEspecificos(divToShow) {

		if ($(divToShow).length) {
			$('#bloc-datos-especificos').show();

			var divsToShow = [];
			var divsToHide = [];

			$('#datos-especificos').children().each(function () {
				let divId = "#" + this.id;
				if (divToShow == divId) {
					divsToShow.push(divId);
				} else {
					divsToHide.push(divId);
				}

			});

			divsToShow.forEach(function(divToShow) {
				$(divToShow).find(":input").prop("disabled", false);
				$(divToShow).show();
			});

			divsToHide.forEach(function(divToHide) {
				$(divToHide).find(":input").prop("disabled", true);
				$(divToHide).hide();
			});
			
		} else {
			$('#bloc-datos-especificos').hide();
			$('#bloc-datos-especificos').find(":input").prop("disabled", true);
		}
		
	}

	
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
				

				
					<div id="divSVDCCAACPASWS01">
						<rip:inputSelect name="comunitatAutonomaCodi" textKey="contingut.pinbal.form.camp.comunitat.autonoma" optionItems="${comunitats}" optionValueAttribute="value" optionTextAttribute="text"/>
						<rip:inputSelect name="provinciaCodi" textKey="contingut.pinbal.form.camp.provincia" optionItems="${provincies}" optionValueAttribute="value" optionTextAttribute="text"/>
					</div>
					
					<div id="divSVDSCDDWS01">
						<rip:inputSelect name="comunitatAutonomaCodi" textKey="contingut.pinbal.form.camp.comunitat.autonoma" optionItems="${comunitats}" optionValueAttribute="value" optionTextAttribute="text"/>
						<rip:inputSelect name="provinciaCodi" textKey="contingut.pinbal.form.camp.provincia" optionItems="${provincies}" optionValueAttribute="value" optionTextAttribute="text"/>
						<rip:inputDate name="dataConsulta" textKey="contingut.pinbal.form.camp.data.consulta" />
						<rip:inputDate name="dataNaixement" textKey="contingut.pinbal.form.camp.data.naixement" />
						<rip:inputSelect name="consentimentTipusDiscapacitat" textKey="contingut.pinbal.form.camp.consentiment.tipus.discapacitat" optionEnum="SiNoEnumDto"/>					
					</div>
					
					<div id="divSCDCPAJU">
						<rip:inputSelect name="provinciaCodi" textKey="contingut.pinbal.form.camp.provincia" optionItems="${provincies}" optionValueAttribute="value" optionTextAttribute="text"/>
						<rip:inputSelect name="municipiCodi" textKey="interessat.form.camp.organ.filtre.municipi" optionItems="${municipis}" optionValueAttribute="codi" optionTextAttribute="nom"/>
					</div>

					<div id="divSVDSCTFNWS01">
						<rip:inputSelect name="comunitatAutonomaCodi" textKey="contingut.pinbal.form.camp.comunitat.autonoma" optionItems="${comunitats}" optionValueAttribute="value" optionTextAttribute="text"/>
						<rip:inputDate name="dataConsulta" textKey="contingut.pinbal.form.camp.data.consulta" />
						<rip:inputDate name="dataNaixement" textKey="contingut.pinbal.form.camp.data.naixement"/>
						<rip:inputText name="numeroTitol" textKey="contingut.pinbal.form.camp.numero.titol"/>
					</div>
				
					<div id="divSVDCCAACPCWS01">
						<rip:inputSelect name="comunitatAutonomaCodi" textKey="contingut.pinbal.form.camp.comunitat.autonoma" optionItems="${comunitats}" optionValueAttribute="value" optionTextAttribute="text"/>
						<rip:inputSelect name="provinciaCodi" textKey="contingut.pinbal.form.camp.provincia" optionItems="${provincies}" optionValueAttribute="value" optionTextAttribute="text"/>
					</div>
					
					
					<div id="divSVDDELSEXWS01">
						<rip:inputSelect name="codiNacionalitat" textKey="contingut.pinbal.form.camp.pais.nacionalitat" optionItems="${paisos}" optionValueAttribute="codi" optionTextAttribute="nom"/>
					  	<rip:inputSelect name="sexe" textKey="contingut.pinbal.form.camp.sexe" optionEnum="SexeEnumDto" emptyOption="true"/>
					  	<rip:inputSelect name="paisNaixament" textKey="contingut.pinbal.form.camp.pais.naixament" optionItems="${paisos}" optionValueAttribute="codi" optionTextAttribute="nom"/>
					  	<rip:inputSelect name="provinciaNaixament" textKey="contingut.pinbal.form.camp.provincia.naixament" optionItems="${provincies}" optionValueAttribute="value" optionTextAttribute="text" comment="contingut.pinbal.form.camp.provincia.naixament.comment"/>
					  	<rip:inputText name="poblacioNaixament" textKey="contingut.pinbal.form.camp.poblacio.naixament" comment="contingut.pinbal.form.camp.poblacio.naixament.comment"/>
					  	<rip:inputSelect name="municipiNaixament" textKey="contingut.pinbal.form.camp.municipi.naixament" optionItems="${municipis}" optionValueAttribute="codi" optionTextAttribute="nom" emptyOption="true" comment="contingut.pinbal.form.camp.codi.poblacio.naixament.comment" optionMinimumResultsForSearch="0"/>
						<rip:inputText name="nomPare" textKey="contingut.pinbal.form.camp.nom.pare" comment="contingut.pinbal.form.camp.nom.pare.comment"/>
						<rip:inputText name="nomMare" textKey="contingut.pinbal.form.camp.nom.mare" comment="contingut.pinbal.form.camp.nom.mare.comment"/>
						<rip:inputDate name="dataNaixement" textKey="contingut.pinbal.form.camp.data.naixement" required="true"/>
						<rip:inputText name="telefon" textKey="contingut.pinbal.form.camp.telefon"/>
						<rip:inputText name="email" textKey="contingut.pinbal.form.camp.email"/>
					</div>
					
					<div id="divSCDHPAJU">
						<rip:inputSelect name="provinciaCodi" textKey="contingut.pinbal.form.camp.provincia" optionItems="${provincies}" optionValueAttribute="value" optionTextAttribute="text"/>
						<rip:inputSelect name="municipiCodi" textKey="interessat.form.camp.organ.filtre.municipi" optionItems="${municipis}" optionValueAttribute="codi" optionTextAttribute="nom"/>
						<rip:inputNumber name="nombreAnysHistoric" textKey="contingut.pinbal.form.camp.data.nombre.anys.historic" nombreDecimals="0"/>
					</div>

					<div id="divNIVRENTI">
						<rip:inputNumber name="exercici" textKey="contingut.pinbal.form.camp.data.exercici" nombreDecimals="0" required="true"/>
					</div>

					
					<div id="divSVDDGPRESIDENCIALEGALDOCWS01">
						<rip:inputText name="numeroSoporte" textKey="contingut.pinbal.form.camp.numero.soporte" comment="contingut.pinbal.form.camp.tipus.numero.soporte.passaport.comment"/>
						<rip:inputSelect name="tipusPassaport" textKey="contingut.pinbal.form.camp.tipus.passaport" comment="contingut.pinbal.form.camp.tipus.passaport.comment" optionEnum="TipusPassaportEnumDto" emptyOption="true"/>
						<rip:inputDate name="dataCaducidad" textKey="contingut.pinbal.form.camp.data.caducidad"/>
						<rip:inputSelect name="codiNacionalitat" textKey="contingut.pinbal.form.camp.pais.nacionalitat" optionItems="${paisos}" emptyOption="true" optionValueAttribute="codi" optionTextAttribute="nom"/>
						<rip:inputDate name="dataExpedicion" textKey="contingut.pinbal.form.camp.data.expedicion"/>
					</div>
					
					<div id="divSVDRRCCNACIMIENTOWS01">
					
						<legend><spring:message code="contingut.pinbal.form.legend.dadesRegistrals"/></legend>
						
						<rip:inputText name="registreCivil" textKey="contingut.pinbal.form.camp.registreCivil" required="true"/>
						<rip:inputText name="tom" textKey="contingut.pinbal.form.camp.tom" required="true"/>
						<rip:inputText name="pagina" textKey="contingut.pinbal.form.camp.pagina" required="true"/>
						
						<legend><spring:message code="contingut.pinbal.form.legend.fetRegistral"/></legend>
						<rip:inputDate name="dataRegistre" textKey="contingut.pinbal.form.camp.data" required="true"/>
						<rip:inputSelect name="municipiRegistre" textKey="contingut.pinbal.form.camp.municipi" optionItems="${municipis}" optionValueAttribute="codi" optionTextAttribute="nom" emptyOption="true" optionMinimumResultsForSearch="0"/>			
						
						<legend><spring:message code="contingut.pinbal.form.legend.naixement"/></legend>		
						<rip:inputDate name="dataNaixement" textKey="contingut.pinbal.form.camp.data"/>		
					  	<rip:inputSelect name="municipiNaixament" textKey="contingut.pinbal.form.camp.municipi" optionItems="${municipis}" optionValueAttribute="codi" optionTextAttribute="nom" emptyOption="true" optionMinimumResultsForSearch="0"/>		
					  	
					  	<legend><spring:message code="contingut.pinbal.form.legend.dadesAdicionals"/></legend>	
					  	<rip:inputCheckbox name="ausenciaSegundoApellido" textKey="contingut.pinbal.form.camp.ausenciaSegundoApellido"/>
					  	<rip:inputSelect name="sexe" textKey="contingut.pinbal.form.camp.sexe" optionEnum="SexeEnumDto" emptyOption="true"/>
					  	<rip:inputText name="nomPare" textKey="contingut.pinbal.form.camp.nom.pare"/>
						<rip:inputText name="nomMare" textKey="contingut.pinbal.form.camp.nom.mare"/>	
					</div>				
					
					<div id="divSVDRRCCMATRIMONIOWS01">
					
						<legend><spring:message code="contingut.pinbal.form.legend.dadesRegistrals"/></legend>
						
						<rip:inputText name="registreCivil" textKey="contingut.pinbal.form.camp.registreCivil" required="true"/>
						<rip:inputText name="tom" textKey="contingut.pinbal.form.camp.tom" required="true"/>
						<rip:inputText name="pagina" textKey="contingut.pinbal.form.camp.pagina" required="true"/>
						
						<legend><spring:message code="contingut.pinbal.form.legend.fetRegistral"/></legend>
						<rip:inputDate name="dataRegistre" textKey="contingut.pinbal.form.camp.data" required="true"/>
						<rip:inputSelect name="municipiRegistre" textKey="contingut.pinbal.form.camp.municipi" optionItems="${municipis}" optionValueAttribute="codi" optionTextAttribute="nom" emptyOption="true" optionMinimumResultsForSearch="0"/>			
						
						<legend><spring:message code="contingut.pinbal.form.legend.naixement"/></legend>		
						<rip:inputDate name="dataNaixement" textKey="contingut.pinbal.form.camp.data"/>		
					  	<rip:inputSelect name="municipiNaixament" textKey="contingut.pinbal.form.camp.municipi" optionItems="${municipis}" optionValueAttribute="codi" optionTextAttribute="nom" emptyOption="true" optionMinimumResultsForSearch="0"/>		
					  	
					  	<legend><spring:message code="contingut.pinbal.form.legend.dadesAdicionals"/></legend>	
					  	<rip:inputCheckbox name="ausenciaSegundoApellido" textKey="contingut.pinbal.form.camp.ausenciaSegundoApellido"/>
					  	<rip:inputSelect name="sexe" textKey="contingut.pinbal.form.camp.sexe" optionEnum="SexeEnumDto" emptyOption="true"/>
					  	<rip:inputText name="nomPare" textKey="contingut.pinbal.form.camp.nom.pare"/>
						<rip:inputText name="nomMare" textKey="contingut.pinbal.form.camp.nom.mare"/>	
					</div>					
					
					<div id="divSVDRRCCDEFUNCIONWS01">
					
						<legend><spring:message code="contingut.pinbal.form.legend.dadesRegistrals"/></legend>
						
						<rip:inputText name="registreCivil" textKey="contingut.pinbal.form.camp.registreCivil"/>
						<rip:inputText name="tom" textKey="contingut.pinbal.form.camp.tom"/>
						<rip:inputText name="pagina" textKey="contingut.pinbal.form.camp.pagina"/>
						
						<legend><spring:message code="contingut.pinbal.form.legend.fetRegistral"/></legend>
						<rip:inputDate name="dataRegistre" textKey="contingut.pinbal.form.camp.data"/>
						<rip:inputSelect name="municipiRegistre" textKey="contingut.pinbal.form.camp.municipi" optionItems="${municipis}" optionValueAttribute="codi" optionTextAttribute="nom" emptyOption="true" optionMinimumResultsForSearch="0"/>			
						
						<legend><spring:message code="contingut.pinbal.form.legend.naixement"/></legend>		
						<rip:inputDate name="dataNaixement" textKey="contingut.pinbal.form.camp.data"/>		
					  	<rip:inputSelect name="municipiNaixament" textKey="contingut.pinbal.form.camp.municipi" optionItems="${municipis}" optionValueAttribute="codi" optionTextAttribute="nom" emptyOption="true" optionMinimumResultsForSearch="0"/>		
					  	
					  	<legend><spring:message code="contingut.pinbal.form.legend.dadesAdicionals"/></legend>	
					  	<rip:inputCheckbox name="ausenciaSegundoApellido" textKey="contingut.pinbal.form.camp.ausenciaSegundoApellido"/>
					  	<rip:inputSelect name="sexe" textKey="contingut.pinbal.form.camp.sexe" optionEnum="SexeEnumDto" emptyOption="true"/>
					  	<rip:inputText name="nomPare" textKey="contingut.pinbal.form.camp.nom.pare"/>
						<rip:inputText name="nomMare" textKey="contingut.pinbal.form.camp.nom.mare"/>	
					</div>													
					
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
