<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<html>
<head>
	<title><spring:message code="contingut.document.form.titol.importar"/></title>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<script src="<c:url value="/webjars/moment/2.15.1/min/moment.min.js"/>"></script>
	<script src="<c:url value="/webjars/moment/2.15.1/min/locales.min.js"/>"></script>
	<script src="<c:url value="/webjars/moment/2.15.1/locale/${requestLocale}.js"/>"></script>
	<link href="<c:url value="/webjars/eonasdan-bootstrap-datetimepicker/4.7.14/build/css/bootstrap-datetimepicker.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/eonasdan-bootstrap-datetimepicker/4.7.14/build//js/bootstrap-datetimepicker.min.js"/>"></script>
	<script src="<c:url value="/webjars/autoNumeric/1.9.30/autoNumeric.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/jquery.maskedinput.min.js"/>"></script>
	<link href="<c:url value="/webjars/jstree/3.2.1/dist/themes/default/style.min.css"/>" rel="stylesheet">
	<script src="<c:url value="/webjars/jstree/3.2.1/dist/jstree.min.js"/>"></script>
	<rip:modalHead/>
<script type="text/javascript">
$(document).ready(function() {
	$("#codiEni").mask("**_*********_9999_******************************",{ 
		placeholder:"_"
	});
	$("#dataPresentacio").mask("99/99/9999 99:99:99",{ 
		placeholder:"_"
	});
	
	// Amagar codi ENI per defecte
	$('.tipus-eni').hide();
	
	// Mostrar/amagar codi ENI
	$('input[type=radio][name=tipusImportacio]').on('change', function() {
		if ($(this).val() == 'CODI_ENI') {
			$('.tipus-eni').show();
			$('.tipus-registre').hide ();
		} else {
			$('.tipus-eni').hide();
			$('.tipus-registre').show();
		}
		webutilModalAdjustHeight();
	});
	$('input[type=radio][name=tipusImportacio][value=${importacioCommand.tipusImportacio}]').trigger('change');
		
	var arbre = $('#arbreCarpetes');
	
	arbre.on('show_contextmenu.jstree', function(e, reference, element) {
	    if ((!reference.node.original) 
	    		|| (reference.node.original.id != undefined && !isNaN(reference.node.original.id)) ) {
	        $('.vakata-context li:eq(2), .vakata-context li:eq(3)').remove();
	    }
	});
	
	console.log('${selectedCarpeta}');
		
	<c:if test="${not empty jstreeJson}">
		arbre.jstree(true).settings.core.data = ${jstreeJson};
		arbre.jstree(true).refresh();
		
		arbre.on("refresh.jstree", function(e) {
			  arbre.jstree('select_node', ${selectedCarpeta});
		});
	</c:if>
	
	
	
	$('form').on('submit', function(){
	    // Obtener la carpeta seleccionada en jsTree
	    var selectedNode = arbre.jstree('get_selected', true)[0]; // Puedes ajustar este selector según tu configuración
		var json = arbre.data().jstree.get_json()
		var jsonString = JSON.stringify(json);

		$('#estructuraCarpetesJson').val(jsonString);

		if (selectedNode) {
	    	$('#destiId').val(selectedNode.id);
	    }
	});
	

	
});


	var novesCarpetes = [];

	function changedCallback(e, data) {
		var arbre = $('#arbreCarpetes');
		var json = arbre.data().jstree.get_json()
		var jsonString = JSON.stringify(json);

		webutilModalAdjustHeight();
		$('#estructuraCarpetesJson').val(jsonString);
	}

	function deletedCallback(e, data) {
		var arbre = $('#arbreCarpetes');
		var expedientCarpetaId = data.node.id;
		if (!isNaN(expedientCarpetaId)) {
			arbre.closest('ul').addClass('positionRelative');
			arbre.closest('ul').append(
					"<div class='rmodal'></div></div>");
			var deleteUrl = '<c:url value="/metaExpedient/'+ expedientCarpetaId + '/deleteCarpeta"/>';
			$.ajax({
				type : "GET",
				url : deleteUrl,
				success : function(data) {
					arbre.closest('ul').removeClass(
							'positionRelative');
					arbre.next().remove();
				}
			});
		}
		var json = arbre.data().jstree.get_json()
		var jsonString = JSON.stringify(json);
		$('#estructuraCarpetesJson').val(jsonString);
		webutilModalAdjustHeight();

		if (jsonString === '[]') {
			if ($(".arbre-emtpy")[0]) {
				$('.arbre-emtpy').show();
			} else {
				$('#carpetes')
						.find('ul')
						.append(
								"<div class='arbre-emtpy'><spring:message code='metaexpedient.form.camp.estructura.arbre.empty'/></div>");
			}
		}
		
		var selectedNode = $('#destiId').val();
		if (selectedNode == expedientCarpetaId) {
	    	$('#destiId').val('');
	    }
	}
	
	function renamedCallback(e, data) {
		var arbre = $('#arbreCarpetes');
		// comprovar si existeix la carpeta
		var parent = data.node.parent;
		var childrens = arbre.jstree(true).get_node(parent).children;
			
		childrens.forEach(function(child) {
			var children = arbre.jstree(true).get_node(child);
			if (childrens.length > 1 && children.text.trim() === data.node.text.trim() && children.id != data.node.id) {
				alert("<spring:message code='metaexpedient.form.camp.estructura.exists'/>");
				var childAdded = arbre.jstree(true).get_node(data.node.id);
				arbre.jstree(true).delete_node(childAdded);
			}
		});
				
		var json = arbre.data().jstree.get_json()
		var jsonString = JSON.stringify(json);
		$('#estructuraCarpetesJson').val(jsonString);
	}
</script>
</head>
<body>
	<c:set var="formAction"><rip:modalUrl value="/contingut/${importacioCommand.pareId}/importacio/new"/></c:set>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="importacioCommand">
		<br/>
		<rip:inputRadio name="tipusImportacio" textKey="contingut.importacio.form.camp.tipus" botons="true" optionItems="${tipusImportacioOptions}" optionValueAttribute="value" optionTextKeyAttribute="text"/>
		<div class="tipus-registre">
			<rip:inputText name="numeroRegistre" textKey="contingut.importacio.form.camp.nom" required="true"/>
			<rip:inputText name="dataPresentacio" textKey="contingut.importacio.form.camp.data" required="true" placeholder="__/__/____  __:__:__"/>
		</div>
		<div class="tipus-eni">
			<rip:inputText name="codiEni" textKey="contingut.importacio.form.camp.eni" required="true" placeholder="ES _________ ____ ______________________________"/>
		</div>
		<%-- 
		<rip:inputRadio name="destiTipus" textKey="contingut.importacio.form.camp.desti" botons="true" optionItems="${tipusDestiOptions}" optionValueAttribute="value" optionTextKeyAttribute="text"/>
		<rip:inputText name="carpetaNom" textKey="contingut.importacio.form.camp.carpeta" required="true"/> 
		--%>
		<rip:arbreMultiple name="estructuraCarpetesJson" id="arbreCarpetes" withlabel="true" textKey="contingut.importacio.form.camp.desti" required="true" atributId="id" atributNom="nom" arbre="${carpetes}" changedCallback="changedCallback" renamedCallback="renamedCallback" deletedCallback="deletedCallback"/>				
		<form:hidden path="destiId"/>
				
		<%-- <rip:inputDateTime name="dataPresentacio" textKey="contingut.importacio.form.camp.data" required="true"/>--%>
		<br/>
		<div id="modal-botons" class="well">
			<button type="submit" class="btn btn-success" id="btnSubmit"><span class="fa fa-save"></span>&nbsp;<spring:message code="comu.boto.importar"/></button>
			<a href="<c:url value="/contingut/${carpetaCommand.pareId}"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>
