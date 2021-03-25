<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:set var="titol"><spring:message code="contenidor.document.viafirma.titol"/></c:set>
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
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<rip:modalHead/>
<style type="text/css">
.firstColumn {
	cursor: default;
}
.title > label {
	color: #757575;
}
.title > hr {
	margin-top: 0;
}
</style>
<script type="text/javascript">
$(document).ready(function() {
	var usuariViaFirma;
	$.ajax({
		type: 'GET',
		url: "<c:url value="/document/viafirma/usuaris"/>",
		success: function(data) {
			var selUsuaris = $('select#codisUsuariViaFirma');
			selUsuaris.empty();
			selUsuaris.append("<option value=\"\"></option>");
			if (data && data.length > 0) {
				if (data.length == 1) {
					$.each(data, function(i, val) {
						selUsuaris.attr('disabled', 'true');
						selUsuaris.append("<option value=\"" + val.codi + "\" selected='true'>" + val.descripcio + " [" + val.codi + "]"+ "</option>");
						usuariViaFirma = val.codi;
						$('#codiUsuariViaFirma').val(usuariViaFirma);
						$('select#codisUsuariViaFirma').trigger('change', [{usuariViaFirma:usuariViaFirma}]);
					});
				} else {
					$.each(data, function(i, val) {
						selUsuaris.append("<option value=\"" + val.codi + "\">" + val.codi + "</option>");
					});
				}
			}
			var select2Options = {
					theme: 'bootstrap',
					width: 'auto',
					minimumResultsForSearch: -1};
			selUsuaris.select2(select2Options);
		},
		error: function() {
			console.log("error recuperant els usuaris de viaFirma...");
		}
	});
	
	$(document).on('change','select#codisUsuariViaFirma', function(e, data) {
		var usuariViaFirma;
		if (data != undefined) {
			usuariViaFirma = data.usuariViaFirma;
		} else {
			usuariViaFirma = $(this).val();
			$('#codiUsuariViaFirma').val(usuariViaFirma);
		} 
		
		$.ajax({
			type: 'GET',
			url: "<c:url value="/document/viafirma/dispositius/"/>" + usuariViaFirma,
			success: function(data) {
				var selDispositius = $('#dispositiuViaFirma');
				selDispositius.empty();
				selDispositius.append("<option value=\"\"></option>");
				if (data && data.length > 0) {
					$.each(data, function(i, val) {
						var dispositius = [
							val.codi,
							val.codiUsuari,
							val.codiAplicacio,
							val.descripcio,
							val.local,
							val.estat,
							val.token,
							val.identificador,
							val.tipus,
							val.emailUsuari,
							val.identificadorNacional
						];
						var dispositiusAmostrar = [
							val.codi
						];
						selDispositius.append("<option value=\"" + dispositius.join("|") +"\">" + dispositiusAmostrar.join(" | ") + "</option>");
					});
				}
				var firstEmptySelect = true;
				var select2Options = {
						theme: 'bootstrap',
						width: 'auto',
						templateResult: formatSelect,
					    escapeMarkup: function(m) { return m; },
					    matcher: matcher,
					    minimumResultsForSearch: -1
				};
				selDispositius.select2(select2Options);
			},
			error: function() {
				console.log("error recuperant els usuaris de viaFirma...");
			}
		});
	});
	
	var firstEmptySelect = true;

	function formatSelect(result) {
		var r = result.text.split('|');
	    if (!result.id) {
	            return false;
	    }
	    return '<div class="row">' +
	                 '<div class="col-xs-3">' + r[0] + '</div>' +
	                 '</div>';      
	}
	function matcher(query, option) {
	    firstEmptySelect = true;
	    if (!query.term) {
	        return option;
	    }
	}
	
	$('#interessatId').on('change', function() {
		var interessatId = $(this).val();
		if (interessatId) {
			$.ajax({
				type: 'GET',
				url: "<c:url value="/expedient/interessat/"/>" + interessatId,
				success: function(data) {
					if (data) {
						$('#signantNom').val(data.llinatgesComaNom);
						$('#signantNif').val(data.documentNum);
					}
				},
				error: function() {
					console.log("error recuperant la informació de l'interessat: " + interessatId);
				}
			});
		} else {
			$('#signantNom').val('');
			$('#signantNif').val('');
		}
	});
	
	var firmaParcialSel = $('#firmaParcial');
	
	firmaParcialSel.empty();
	
	var data = {id: false, text: 'No'};
	var newOption = new Option(data.text, data.id, false, false);
	firmaParcialSel.append(newOption);
	var data = {id: true, text: 'Si'};
	var newOption = new Option(data.text, data.id, false, false);
	firmaParcialSel.append(newOption).trigger('change');
	
	var valdiateCodeEnabledInput = $('#validateCodeEnabled');
	var validateCodeField = $('#validateCode').closest('.form-group');
	validateCodeField.hide();
	valdiateCodeEnabledInput.on('change', function() {
		if($(this).is(':checked')) {
			validateCodeField.show();
		} else {
			validateCodeField.hide();
		}
	});
	valdiateCodeEnabledInput.trigger('change');
});

</script>
</head>
<body>
	<c:set var="formAction"><rip:modalUrl value="/document/${document.id}/viafirma/upload"/></c:set>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="viaFirmaEnviarCommand" role="form">
		<div class="title">
			<label><spring:message code="contenidor.document.biometrica.dades"/></label>
			<hr>
		</div>
		<rip:inputText name="titol" textKey="contenidor.document.biometrica.camp.motiu" />
		<rip:inputText name="descripcio" textKey="contenidor.document.biometrica.camp.descripcio" />
		<rip:inputHidden name="codiUsuariViaFirma"/>
		<rip:inputSelect name="codisUsuariViaFirma" textKey="contenidor.document.biometrica.camp.usuari"  required="true"/>
		<c:if test="${isDispositiusEnabled}">
			<rip:inputSelect name="dispositiuViaFirma" textKey="contenidor.document.biometrica.camp.dispositiu" required="true"/>
		</c:if>
		<div class="title">
			<label><spring:message code="contenidor.document.biometrica.dades.interessat"/></label>
			<hr>
		</div>
		<rip:inputSelect name="interessatId" textKey="contenidor.document.biometrica.camp.interessat" emptyOption="true" emptyOptionTextKey="contenidor.document.biometrica.camp.interessat.nou" optionItems="${interessats}" optionValueAttribute="id" optionTextAttribute="identificador"/>
		<rip:inputText name="signantNom" textKey="contenidor.document.biometrica.camp.interessat.nom" required="true"/>
		<rip:inputText name="signantNif" textKey="contenidor.document.biometrica.camp.interessat.nif" required="true"/>
		<div class="title">
			<label><spring:message code="contenidor.document.biometrica.dades.altres"/></label>
			<hr>
		</div>
		<rip:inputSelect name="firmaParcial" textKey="contenidor.document.biometrica.camp.firmaparcial"  required="true"/>
		<rip:inputCheckbox name="validateCodeEnabled" textKey="contenidor.document.biometrica.camp.validatecode.check"/>
		<rip:inputText name="validateCode" textKey="contenidor.document.biometrica.camp.validatecode" required="true"/>
		<rip:inputTextarea name="observacions" textKey="contenidor.document.biometrica.camp.observacions"/>
		<div id="modal-botons" class="well">
			<button type="submit" class="btn btn-success"><span class="fa fa-send"></span> <spring:message code="contenidor.document.biometrica.enviar"/></button>
			<a href="<c:url value="/contenidor/${document.id}"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>
