<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:choose>
	<c:when test="${empty documentCommand.id}"><c:set var="titol"><spring:message code="contingut.document.form.titol.crear"/></c:set></c:when>
	<c:otherwise><c:set var="titol"><spring:message code="contingut.document.form.titol.modificar"/></c:set></c:otherwise>
</c:choose>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<c:set var="isTasca" value="${not empty tascaId}"/>
<c:set var="isCreate" value="${empty documentCommand.id}"/>

<html>
<head>
	<title>${titol}</title>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<link href="<c:url value="/css/jasny-bootstrap.min.css"/>" rel="stylesheet">
	<script src="<c:url value="/js/jasny-bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<rip:modalHead/>
<style type="text/css">

.rmodal {
    display:    none;
    position:   fixed;
    z-index:    1000;
    top:        0;
    left:       0;
    height:     100%;
    width:      100%;
    background: rgba( 255, 255, 255, .8 ) 
                url('<c:url value="/img/loading.gif"/>') 
                50% 50% 
                no-repeat;
}
body.loading {
    overflow: hidden;   
}
body.loading .rmodal {
    display: block;
}

.tooltip {
  font-family: "Helvetica Neue",Helvetica,Arial,sans-serif;
  font-size: 14px;
  display: block;
}
.tooltip-arrow {
}
.tooltip-inner {
  max-width: 700px;
  padding: 2% 1% 1% 1%;
  background-color: #fff;
  color: black;
  padding: 1%;
  border: 1px solid black;
  border-radius: 3px;
}
#escaneig {
	padding: 0 0 5% 0;
}
.iframe_container {
	position: relative;
	width: 100%;
	height: 0;
	padding-bottom: 50%;
}

.iframe_content {
	position: absolute;
	top: 0;
	left: 0;
	width: 100%;
}
.downloadLink {
	background-color: #f4faff;
	padding: 2%;
}
.scan-cancel-btn {
	margin-top: 3%;
}
.scan-profile > span {
	border-radius: 0px;
	color: #fff;
	background-color: #c4c4c4;
	border-color: #c4c4c4;
}
.scan-profile > span:hover {
	color: #fff;
} 
</style>
<script>
/*function mostrarDocument(fileName) {
	$fileinput = $('#arxiu').closest('.fileinput');
	$fileinput.removeClass('fileinput-new');
	$fileinput.addClass('fileinput-exists');
	$('.fileinput-filename', $fileinput).append(fileName);
}*/

$(document).ready(function() {
	let currentHeight = window.frameElement.contentWindow.document.body.scrollHeight;
	localStorage.setItem("currentIframeHeight", currentHeight);
	console.log(currentHeight);
	let rootIframe = window.frameElement;
	/*let fileName = "${nomDocument}";
	if (fileName !== '') {
		mostrarDocument(fileName);
	}*/

	// METADOCUMENT CHANGE
	$('#metaNodeId').on('change', function() {
		if ($(this).val()) {
			if ($('#id').val() == '') { // if creating new document
				$.get("/ripea/modal/contingut/${contingutId}/metaDocument/" +  $(this).val() + "/dadesnti")
				.done(function(data) {			
					$('#ntiOrigen').val(data.ntiOrigen).trigger('change');
					$('#ntiOrigen option[value='+ data.ntiOrigen +']').attr('selected','selected');
					$('#ntiEstadoElaboracion').val(data.ntiEstadoElaboracion).trigger('change');
					$('#ntiEstadoElaboracion option[value='+ data.ntiOrigen +']').attr('selected','selected');
					$('#ntiTipoDocumental').val(data.ntiTipoDocumental).trigger('change');
					$('#ntiTipoDocumental option[value='+ data.ntiOrigen +']').attr('selected','selected');
				})
			} else { // if modifying existing document 
				if(confirm("<spring:message code="contingut.document.misatge.avis"/>")){
					$.get("/ripea/modal/contingut/${contingutId}/metaDocument/" +  $(this).val() + "/dadesnti")
					.done(function(data) {			
						$('#ntiOrigen').val(data.ntiOrigen).trigger('change');
						$('#ntiOrigen option[value='+ data.ntiOrigen +']').attr('selected','selected');
						$('#ntiEstadoElaboracion').val(data.ntiEstadoElaboracion).trigger('change');
						$('#ntiEstadoElaboracion option[value='+ data.ntiOrigen +']').attr('selected','selected');
						$('#ntiTipoDocumental').val(data.ntiTipoDocumental).trigger('change');
						$('#ntiTipoDocumental option[value='+ data.ntiOrigen +']').attr('selected','selected');
					})
				}
			}
			$.get("/ripea/modal/contingut/${contingutId}/metaDocument/" +  $(this).val())
			.done(function(data) {
				if (data.plantillaNom) {
					$('#info-plantilla-si').removeClass('hidden');
					$('#info-plantilla-si a').attr('href', '../metaDocument/' + data.id + '/plantilla');
				} else {
					$('#info-plantilla-si').addClass('hidden');
				}
				webutilModalAdjustHeight();
			})
			.fail(function() {
				alert("<spring:message code="contingut.document.form.alert.plantilla"/>");
			});
		} else {
			$('#info-plantilla-si').addClass('hidden');
		}
	});
	$('input[type=checkbox][name=ambFirma]').on('change', function() {
		if($(this).prop("checked") == true){
			$('#input-firma').removeClass('hidden');
			if($('input[type=radio][name=tipusFirma]').val() != 'SEPARAT') {
				$('#input-firma-arxiu').addClass('hidden');
			}
		} else {
			$('#input-firma').addClass('hidden');
		}
		webutilModalAdjustHeight();
	});
	$('input[type=radio][name=tipusFirma]').on('change', function() {
		if ($(this).val() == 'SEPARAT') {
			$('#input-firma-arxiu').removeClass('hidden');
		} else {
			$('#input-firma-arxiu').addClass('hidden');
		}
		webutilModalAdjustHeight();
	});

	
	if($('#id').val() == '') {
		$('#metaNodeId').trigger('change');
	}
	$('input[type=radio][name=origen][value=${documentCommand.origen}]').trigger('change');
	$('input[type=checkbox][name=ambFirma').trigger('change');
	$('input[type=radio][name=tipusFirma][value=${documentCommand.tipusFirma}]').trigger('change');
	let droppedFiles = window.parent.document.getElementById('dropped-files');
	if (droppedFiles) {
		let droppedFilesFiles = droppedFiles.files;
		if (droppedFilesFiles && droppedFilesFiles.length == 1) {
			document.querySelector('#arxiu').files = droppedFilesFiles;
			mostrarDocument(droppedFilesFiles[0].name);
			
		}
	}
	
	var nom = $('#nom');
	var invalid = new RegExp('[\/:*?\"<>|]');
	$(nom).keypress(function(e) {
		var s = String.fromCharCode(e.which);
		//Comprovar cada lletra
	    if (invalid.test(s)) {
	    	$('#nom').tooltip("show");
	    }
	    //Comprovar títol
	    if (invalid.test($(nom).val())) {
	    	$('#nom').tooltip('enable');
	    	$('#nom').tooltip("show");
	    } else {
	    	$('#nom').tooltip('disable');
	    	$('#nom').tooltip("hide");
	    }
	});

	if($('#origen').val() == 'ESCANER') {
		$('#escaneig').addClass('active');
		$('#fitxer').removeClass('active');
		$('.escaneig').parent().addClass('active');
		$('.fitxer').parent().removeClass('active');
	} else {
		$('#fitxer').addClass('active');
		$('#escaneig').removeClass('active');
		$('.fitxer').parent().addClass('active');
		$('.escaneig').parent().removeClass('active');
	}
	$('a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
		 var pipella = $(e.target).attr("class");
		 if (pipella == 'fitxer') {
			 $('#origen').val('DISC');
			 webutilModalAdjustHeight();
		 } else {
			 $('#origen').val('ESCANER');
			 webutilModalAdjustHeight();
		 }
	});
	
	//Recuperar perfils disponibles en cas de no definir un per defecte
	$('.start-scan-btn').on('click', function(){
		$('#escaneig').find('.alert').remove();
		$('.start-scan-btn').hide();
		$.ajax({
			type: 'GET',
			url: "<c:url value='/digitalitzacio/perfils'/>",
			success: function(perfils) {
				for ( var i in perfils) {
					$('.scan-profile').append('<span class="btn btn-lg btn-block" id="' + perfils[i].codi + '"><small>' + perfils[i].nom + '</small></span>');
					$('.scan-profile').append('</br>');
				}
				$('.scan-profile').show();
				$('.scan-back-btn').removeClass('hidden');
				webutilModalAdjustHeight();
			},
			error: function(err) {
				console.log("Error tancant la transacció");
			},
			complete: function() {
				localStorage.removeItem('transaccioId');
			}
		});
	});
	
	//Iniciar procés digitalització després de triar perfil
	$(document).on('click', '.scan-profile', function(){
		$('.scan-profile').hide();
	    var codi_perfil = $('span', this).attr('id');
	    $(this).html('');
	    $.ajax({
	    	type: 'GET',
			url: "<c:url value='/digitalitzacio/iniciarDigitalitzacio/" + codi_perfil + "'/>",
			success: function(transaccioResponse) {
				if (transaccioResponse != null) {
					localStorage.setItem('transaccioId', transaccioResponse.idTransaccio);
					var iframeScan = '<div class="iframe_container"><iframe onload="removeLoading()" class="iframe_content" width="100%" height="100%" frameborder="0" allowtransparency="true" src="' + transaccioResponse.urlRedireccio + '"></iframe></div>'
					$('.scan-result').append(iframeScan);
					$('.scan-back-btn').addClass('hidden');
					webutilModalAdjustHeight();
					$body = $("body");
					$body.addClass("loading");
				}
			},
			error: function(err) {
				console.log("Error tancant la transacció");
			}
	    });
	});
	
	//Iniciar procés digitalització després de triar perfil
	$(document).on('click', '.scan-cancel-btn', function(){
		var idTransaccio = localStorage.getItem('transaccioId');
	    var codi_perfil = $('span', this).attr('id');
		$('.scan-profile').hide();
	    $.ajax({
	    	type: 'GET',
			url: "<c:url value='/digitalitzacio/tancarTransaccio/" + idTransaccio + "'/>",
			success: function(transaccioResponse) {
				$('.scan-result').html('');
				$('.start-scan-btn').show();
				localStorage.removeItem('transaccioId');
			},
			error: function(err) {
				console.log("Error tancant la transacció");
			}
	    });
	});
	
	$('.scan-back-btn').on('click', function(){
		$('.start-scan-btn').show();
		$('.scan-profile').empty().hide();
		$('.scan-back-btn').addClass('hidden');
	});
	
});

function removeLoading() {
	$body = $("body");
	$body.removeClass("loading");
}
</script>
</head>
<body>



	<c:choose>
		<c:when test="${isTasca}">
			<c:set var="formAction"><rip:modalUrl value="/usuariTasca/${tascaId}/pare/${documentCommand.pareId}/document${isCreate ? '/docNew' : '/docUpdate'}"/></c:set>
		</c:when>
		<c:otherwise>
			<c:set var="formAction"><rip:modalUrl value="/contingut/${documentCommand.pareId}/document${isCreate ? '/docNew' : '/docUpdate'}"/></c:set>
		</c:otherwise>
	</c:choose>
	
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="documentCommand" enctype="multipart/form-data">
		<div id="info-plantilla-si" class="alert well-sm alert-info hidden">
			<span class="fa fa-info-circle"></span>
			<spring:message code="contingut.document.form.info.plantilla.si"/>
			<a href="#" class="btn btn-xs btn-default pull-right"><spring:message code="comu.boto.descarregar"/></a>
		</div>
		<form:hidden path="id"/>
		<form:hidden path="entitatId"/>
		<form:hidden path="pareId"/>
		<form:hidden path="documentTipus"/>
		<form:hidden path="origen"/>

		<c:choose>
			<c:when test="${documentCommand.documentTipus == 'IMPORTAT'}">
				<c:set var="readOnlyValue" value="true"/>
			</c:when>
			<c:otherwise>
				<c:set var="readOnlyValue" value="false"/>
			</c:otherwise>
		</c:choose>

		<rip:inputText name="nom" textKey="contingut.document.form.camp.nom" required="true" tooltip="true" tooltipMsg="contingut.document.form.camp.nom.caracters"/>
		<rip:inputTextarea name="descripcio" textKey="contingut.document.form.camp.descripcio"/>
		<rip:inputDate name="data" textKey="contingut.document.form.camp.data" required="true" readonly="${readOnlyValue}"/>
		<rip:inputSelect name="metaNodeId" textKey="contingut.document.form.camp.metanode" optionItems="${metaDocuments}" optionValueAttribute="id" optionTextAttribute="nom"/>
		<rip:inputSelect name="ntiEstadoElaboracion" emptyOption="true" emptyOptionTextKey="contingut.document.form.camp.nti.cap" textKey="contingut.document.form.camp.nti.estela" required="true" optionItems="${ntiEstatElaboracioOptions}" optionValueAttribute="value" optionTextKeyAttribute="text"/>

		<ul class="nav nav-tabs" role="tablist">
			<li role="presentation" class="active"><a href="#fitxer" class="fitxer" aria-controls="fitxer" role="tab" data-toggle="tab"><spring:message code="contingut.document.form.camp.tab.fitxer"/></a></li>
			<li role="presentation"><a href="#escaneig" class="escaneig" aria-controls="escaneig" role="tab" data-toggle="tab"><spring:message code="contingut.document.form.camp.tab.escaneig"/></a></li>
		</ul>
		<br/>
		<div class="tab-content">
			<div role="tabpanel" class="tab-pane active" id="fitxer">
				<rip:inputFile name="arxiu" textKey="contingut.document.form.camp.arxiu" required="${empty documentCommand.id}" fileName="${nomDocument}"/>
				<rip:inputCheckbox name="ambFirma" textKey="contingut.document.form.camp.amb.firma"></rip:inputCheckbox>
				<div id="input-firma" class="hidden">
					<rip:inputRadio name="tipusFirma" textKey="contingut.document.form.camp.tipus.firma" botons="true" optionItems="${tipusFirmaOptions}" optionValueAttribute="value" optionTextKeyAttribute="text"/>
					<div id="input-firma-arxiu" class="hidden">
						<rip:inputFile name="firma" textKey="contingut.document.form.camp.firma" required="${empty documentCommand.id}"/>
					</div>
				</div>
			</div>
			<div role="tabpanel" class="tab-pane" id="escaneig">
			<c:if test="${not empty noFileScanned}">
				<div class="alert alert-danger" role="alert"><a class="close" data-dismiss="alert">×</a><span><spring:message code="contingut.document.form.camp.escaneig.buid"/></span></div>
			</c:if>
				<div class="steps">
					<div class="col-md-12 text-center">
						<span class="btn btn-default start-scan-btn btn-md"><spring:message code="contingut.document.form.camp.escaneig.iniciar"/> <i class="fa fa-play"></i></span>
					</div>
					<div class="col-md-12 text-center scan-profile"></div>
					<div class="col-md-12 text-center scan-result">
						<c:if test="${not empty nomDocument && empty documentCommand.id}">
							<script>
								$('.start-scan-btn').hide();
							</script>
							<a class="downloadLink" href="<c:url value="/digitalitzacio/descarregarResultat/${idTransaccio}"/>">${nomDocument}</a> <br>
							<span class='btn btn-default scan-cancel-btn'><spring:message code="contingut.document.form.camp.escaneig.cancelar"/></span>
						</c:if>
					</div>
					<div class="col-md-12 text-center scan-back-btn hidden">
						<span class="btn btn-default btn-lg"><spring:message code="contingut.document.form.camp.escaneig.tornar"/> <i class="fa fa-back"></i></span>
					</div>
				</div>
			</div>
		</div>
		<div class="rmodal"></div>
		<div id="modal-botons" class="well">
			<button type="submit" class="btn btn-success"><span class="fa fa-save"></span> <spring:message code="comu.boto.guardar"/></button>
			<a href="<c:url value="/contingut/${documentCommand.pareId}"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>
