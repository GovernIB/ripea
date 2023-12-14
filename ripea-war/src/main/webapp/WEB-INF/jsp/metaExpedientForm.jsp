<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:choose>
	<c:when test="${empty metaExpedientCommand.id}">
		<c:set var="titol"><spring:message code="metaexpedient.form.titol.crear" /></c:set>
	</c:when>
	<c:otherwise>
		<c:choose>
			<c:when test="${consultar}">
				<c:set var="titol"><spring:message code="metaexpedient.form.titol.consultar" /></c:set>
			</c:when>
			<c:otherwise>
				<c:set var="titol"><spring:message code="metaexpedient.form.titol.modificar" /></c:set>
			</c:otherwise>
		</c:choose>
	</c:otherwise>
</c:choose>






<html>
<head>
	<title>${titol}</title>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<link href="<c:url value="/webjars/jstree/3.2.1/dist/themes/default/style.min.css"/>" rel="stylesheet">
	<script src="<c:url value="/webjars/jstree/3.2.1/dist/jstree.min.js"/>"></script>
	<rip:modalHead/>
	
	<style type="text/css">
		.rmodal {
		    display:    none;
		    position:   absolute;
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
		ul .rmodal {
		    display: block;
		}
		#botons_container {
			width: 100%;
			overflow: auto;
			margin-bottom:10px;
			text-align: right;
		}
		.positionRelative {
			position: relative;
		}
		.arbre-emtpy {
			text-align: center;
		}
	</style>
	<c:if test="${hasPermisAdmComu}">
		<script type="text/javascript">
			var unicOrganGestorId = null;
			<c:if test="${not empty organDisponible}">unicOrganGestorId = ${organDisponible.id};</c:if>
			$(document).ready(function() {
				var organGestorContainer = $("#organGestorContainer");
				$( "#comu" ).change(function () {
					if(this.checked) {
						organGestorContainer.hide();
						$("#organGestorId").val(null);
						$("select#organGestorId").trigger("change");
					} else {
						if (unicOrganGestorId != null) {
							$("#organGestorId").val(unicOrganGestorId);
						}
						organGestorContainer.show();
					}
				});
				if ($("#comu").is(":checked")) {
					organGestorContainer.hide();
				}
			});
		</script>
	</c:if>

	<c:if test="${not isRolAdminOrgan}">
		<script type="text/javascript">
		var novesCarpetes = [];

		function addFolder() {
			var arbre = $('#arbreCarpetes');
			var selectedNode = arbre.jstree("get_selected");
			var position = 'inside';
			if (selectedNode.length != 0) {
			var selectedFolder = $('ul').find('li#' + selectedNode[0]);
			} else {
				alert("<spring:message code='metaexpedient.form.camp.estructura.empty'/>");
			}
			arbre.jstree('create_node', selectedNode , false, "last", function (node) {
		        this.edit(node);
		    });
			$('.arbre-emtpy').hide();
		}
		
		function addParentFolder() {
			var arbre = $('#arbreCarpetes');
			
			arbre.jstree('create_node', "#" , false, "last", function (node) {
		        this.edit(node);
		    });
			$('.arbre-emtpy').hide();
		}
		
		function changedCallback(e, data) {
			var arbre = $('#arbreCarpetes');
			var json = arbre.data().jstree.get_json()
			var jsonString = JSON.stringify(json);
			
			webutilModalAdjustHeight();
			$('#estructuraCarpetesJson').val(jsonString);
		}
		
		function deletedCallback(e, data) {
			var metaExpedientCarpetaId = data.node.id;
			if (!isNaN(metaExpedientCarpetaId)) {
				$('#arbreCarpetes').closest('ul').addClass('positionRelative');
				$('#arbreCarpetes').closest('ul').append("<div class='rmodal'></div></div>");
				var deleteUrl = '<c:url value="/metaExpedient/'+ metaExpedientCarpetaId + '/deleteCarpeta"/>';
				$.ajax({
			        type: "GET",
			        url: deleteUrl,
			        success: function (data) {
			        	$('#arbreCarpetes').closest('ul').removeClass('positionRelative');
			        	$('#arbreCarpetes').next().remove();
			        }
				});
			}
			var json = $('#arbreCarpetes').data().jstree.get_json()
			var jsonString = JSON.stringify(json);
			$('#estructuraCarpetesJson').val(jsonString);
			webutilModalAdjustHeight();
			
			if(jsonString === '[]') {
				if ($(".arbre-emtpy")[0]) {
					$('.arbre-emtpy').show();
				} else {
					$('#carpetes').find('ul').append("<div class='arbre-emtpy'><spring:message code='metaexpedient.form.camp.estructura.arbre.empty'/></div>");
				}
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
	</c:if>
	
	
<script>
//################################################## document ready START ##############################################################	
$(document).ready(function(){
	<c:if test="${isRolActualAdminOrgan}">	
		$('#revisioEstat').on('change', function() {
			var revEst = $(this).val();
			if (revEst == 'PENDENT' || revEst == 'DISSENY') {
				$("#revisioEstat option[value='REVISAT']").remove();
				$("#revisioEstat option[value='REBUTJAT']").remove();
			} else if (revEst == 'REBUTJAT') {
				$("#revisioEstat option[value='REVISAT']").remove();
				$("#revisioEstat option[value='PENDENT']").remove();
			}
			
		});
		$("#revisioEstat").trigger('change');
	</c:if>


	let bloquejarCamps = ${not empty bloquejarCamps};

	if (bloquejarCamps) {
		showHideClassificacioInput();
		disableClassificacioButtons();
		$('.comentariSia').hide();
		$('#classificacioSia').attr("readonly", true);
		
		
	} else {
		$('input[type=radio][name=tipusClassificacio]').on('change', function() {
			showHideClassificacioInput();
		})
		showHideClassificacioInput();
		$('#organGestorId').on('change', function() {
			$('input[name="tipusClassificacio"][value="ID"]').removeClass('focus');
			disableEnableClassificacioIdButton();
			calculateClassificacioId();
		})

		disableEnableClassificacioIdButton();
		if (!$('#id').val()) {
			calculateClassificacioId();
		}

		// on submit show alert if codi SIA doesn't exist in rolsac
		$('form').submit(function(e) {
		    let selected = $('input[name="tipusClassificacio"]:checked').val();
		    if (selected == 'SIA') {
		        let codiSia = $('#classificacioSia').val();

				if (codiSia) {
					
			        let iframe = window.frameElement;
	                $(iframe).hide();
	                $('.modal-body .datatable-dades-carregant', parent.document).show();
	                $('.modal-footer', parent.document).find('button[type="submit"]').attr('disabled', true);
					
			        $.ajax({
			            type: 'GET',
			            async: false,
			            url: '<c:url value="/metaExpedient/checkIfExistsInRolsac"/>/' + codiSia,
			            success: function(json) {
							let showConfirmDialog;
							let confirmDialogMsg;
				        	if (json.error) {
				        		showConfirmDialog = true;
				        		${fn:escapeXml(contingut.nom)}
				        		confirmDialogMsg = "<spring:message code='metaexpedient.form.submit.sia.no.comprovat'/>";

				        	} else {
				        		let exists = json.data;
				        		showConfirmDialog = !exists;
				        		confirmDialogMsg = '<spring:message code="metaexpedient.form.submit.sia.no.existe"/>';
							}

			                if (showConfirmDialog) {
			                   
			                    if (confirm(confirmDialogMsg)) {
			                        return true
			                    } else {
			                        e.preventDefault(); 
			                        let iframe = window.frameElement;
			                        $(iframe).show();
			                        $('.modal-body .datatable-dades-carregant', parent.document).hide();
			                        $('.modal-footer', parent.document).find('button[type="submit"]').attr('disabled', false);
			                        
			                        return false;
			                    }
			                }
			            }
			        });
				}
		    }
		});
	}
	
			
});//################################################## document ready END ##############################################################


function showHideClassificacioInput() {

	let selected = $('input[name="tipusClassificacio"]:checked').val();

	if (selected == 'SIA') {
		$('#classificacioSia').parent().show();
		$('#classificacioId').parent().hide();
	} else {
		$('#classificacioSia').val('');
		$('#classificacioSia').parent().hide();
		$('#classificacioId').parent().show();
		if ($('#organGestorId').val() && !$('#classificacioId').val()) {
			calculateClassificacioId();
		}
	}
}

function disableEnableClassificacioIdButton(){
	if ($('#organGestorId').val()) {
		$('input[name="tipusClassificacio"][value="ID"]').parent().attr("disabled", false);
		$('input[name="tipusClassificacio"][value="ID"]').parent().attr("title", "");
	} else {
		$('#classificacioId').val('');
		$('input[name="tipusClassificacio"][value="ID"]').parent().attr("disabled", true);
		let disabledTitle = '<spring:message code="metaexpedient.form.camp.classificacio.id.disabled.title"/>';
		$('input[name="tipusClassificacio"][value="ID"]').parent().attr("title", disabledTitle);
		$('input[name="tipusClassificacio"][value="SIA"]').click();
		
	}
}

function disableClassificacioButtons(){
	$('input[name="tipusClassificacio"][value="SIA"]').parent().attr("disabled", true);
	$('input[name="tipusClassificacio"][value="ID"]').parent().attr("disabled", true);
}


function calculateClassificacioId() {
	let organGestorId = $('#organGestorId').val();
	$.ajax({
		type: 'GET',
		url: '<c:url value="/metaExpedient/calculateClassificacioId"/>/' + organGestorId,
		success: function(id) {
			$('#classificacioId').val(id);
		}
	});
	
}



</script>	
	
	
	
</head>
<body>


	<c:if test="${metaExpedientDto!=null && metaExpedientDto.organsNous != null && !empty metaExpedientDto.organsNous}">

		<div class="panel panel-danger">
			<div class="panel-heading">
				<span class="fa fa-warning text-danger"></span>
				<spring:message code="metaexpedient.form.organ.obsolet"/> 
			</div>
			<div class="panel-body">
				<div class="row">
					<label class="col-xs-4 text-right"><spring:message
							code="metaexpedient.form.organ.nou" /></label>
					<div class="col-xs-8">
						<ul style="padding-left: 17px;">
							<c:forEach items="${metaExpedientDto.organsNous}"
								var="newUnitat" varStatus="loop">
								<li>${newUnitat.nom} (${newUnitat.codi})</li>
							</c:forEach>
						</ul>
					</div>
				</div>
			</div>
		</div>
	</c:if>


	<div id="loading" style="display: none;"><div style="text-align: center; padding-bottom: 100px; color: #666666; margin-top: 100px;"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div></div>
	<c:set var="formAction"><rip:modalUrl value="/metaExpedient"/></c:set>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="metaExpedientCommand">
		<c:if test="${isCarpetaDefecte || (metaExpedientCommand.revisioEstat!=null && isRevisioActiva)}">
			<ul class="nav nav-tabs" role="tablist">
				<li role="presentation" class="active"><a href="#dades" aria-controls="dades" role="tab" data-toggle="tab"><spring:message code="metaexpedient.form.camp.tab.dades"/></a></li>
<%-- 			<li role="presentation"><a href="#notificacions" aria-controls="notificacions" role="tab" data-toggle="tab"><spring:message code="metaexpedient.form.camp.tab.notificacions"/></a></li> --%>
				<c:if test="${isCarpetaDefecte}">
					<li role="presentation"><a href="#carpetes" aria-controls="notificacions" role="tab" data-toggle="tab"><spring:message code="metaexpedient.form.camp.tab.carpetes"/></a></li>
				</c:if>
				<c:if test="${metaExpedientCommand.revisioEstat!=null && isRevisioActiva}">
					<li role="presentation"><a href="#revisioEstatTab" aria-controls="revisioEstatTab" role="tab" data-toggle="tab"><spring:message code="metaexpedient.form.camp.tab.revisioEstat"/></a></li>
				</c:if>
			</ul>
		</c:if>
		<form:hidden path="id"/>
		<form:hidden path="entitatId"/>
		<form:hidden path="RolAdminOrgan"/>
		<br/>
		<div class="tab-content">
			<div role="tabpanel" class="tab-pane active" id="dades">
			
				<rip:inputText name="codi" textKey="metaexpedient.form.camp.codi" required="true" readonly="${bloquejarCamps}"/>
				
				<c:set var="campErrors"><form:errors path="classificacioSia"/></c:set>
				<div class="form-group<c:if test="${not empty campErrors}"> has-error</c:if>">
					<label class="control-label col-xs-4" for="tipusClassificacio">
						<spring:message code="metaexpedient.form.camp.classificacio"/> *
					</label>
					<div class="col-xs-8">
						<div class="controls btn-group col-xs-2" style="padding-right: 0px; padding-left: 0px" data-toggle="buttons">
							<c:forEach var="opt" items="${tipus}">
								<c:set var="labelClassActive" value=""/>
								<spring:bind path="tipusClassificacio"><c:set var="campValue" value="${status.value}"/></spring:bind>
								<c:if test="${campValue == opt.value}"><c:set var="labelClassActive" value=" active"/></c:if>
								<c:set var="labelClass" value="btn btn-default${labelClassActive}"/>
								<button class="${labelClass}" onclick="this.blur();">
									<form:radiobutton path="tipusClassificacio" value="${opt.value}"/> <spring:message code="${opt.text}"/>
								</button>
							</c:forEach>
						</div>
						<div class="col-xs-10" style="padding-right: 0px; padding-left: 10px">
							<div style="display:none;">
								<form:input path="classificacioSia" cssClass="form-control"/>
								<c:if test="${not empty campErrors}">
									<p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;<form:errors path="classificacioSia"/></p>
								</c:if>	
	 						   <p class="comentari col-xs-12 comentariSia"><spring:message code="metaexpedient.form.camp.classificacio.sia.comment"/></p>	
							</div>	
							<div style="display:none;">
								<form:input path="classificacioId" cssClass="form-control" readonly="true"/>
	 						   <p class="comentari col-xs-12"><spring:message code="metaexpedient.form.camp.classificacio.id.comment"/></p>	
							</div>							
						</div>					
					</div>
				</div>
				
				<c:if test="${empty metaExpedientCommand.id && isRolActualAdministrador}">
					<rip:inputCheckbox name="crearReglaDistribucio" textKey="metaexpedient.form.camp.crear.regla.distribucio" disabled="${bloquejarCamps}" />
				</c:if>
				<rip:inputTextarea name="nom" textKey="metaexpedient.form.camp.nom" required="true" disabled="${bloquejarCamps}"/>
				<rip:inputTextarea name="descripcio" textKey="metaexpedient.form.camp.descripcio" disabled="${bloquejarCamps}"/>
				
				<rip:inputText name="serieDocumental" textKey="metaexpedient.form.camp.serie.doc" required="true" readonly="${bloquejarCamps}"/>
				<c:choose>
					<c:when test="${hasPermisAdmComu}">
						<rip:inputCheckbox name="comu" textKey="metaexpedient.form.camp.comu"/>
					</c:when>
					<c:otherwise>
						<form:hidden path="comu"/>
					</c:otherwise>
				</c:choose>
				<c:url value="/organgestorajax/organgestor" var="urlConsultaInicial"/>
				<c:url value="/organgestorajax/organgestor" var="urlConsultaLlistat"/>

				<div id="organGestorContainer">
					<c:choose>
						<c:when test="${numOrgansDisponibles == 1}">
							<div class="form-group">
								<input type="hidden" id="organGestorId" name="organGestorId" value="${organDisponible.id}"/>
								<label class="control-label col-xs-4" for="organGestorNom"><spring:message code="metaexpedient.form.camp.organgestor"/> *</label>
								<div class="col-xs-8">
									<input type="text" class="form-control" id="organGestorNom" name="organGestorNom" disabled="disabled" value="${organDisponible.codiINom}">
								</div>
							</div>
						</c:when>
						<c:when test="${numOrgansDisponibles < 32 }">
							<rip:inputSelect name="organGestorId" optionItems="${organsGestors}" optionValueAttribute="id" optionTextAttribute="codiINom" optionMinimumResultsForSearch="1" textKey="metaexpedient.form.camp.organgestor" required="true" emptyOption="true"  disabled="${bloquejarCamps}"/>
						</c:when>
						<c:otherwise>
							<rip:inputSuggest name="organGestorId" urlConsultaInicial="${urlConsultaInicial}" urlConsultaLlistat="${urlConsultaLlistat}" textKey="metaexpedient.form.camp.organgestor" suggestValue="id" suggestText="codiINom" required="true" disabled="${bloquejarCamps}"/>
						</c:otherwise>
					</c:choose>
				</div>
				<rip:inputText name="expressioNumero" textKey="metaexpedient.form.camp.expressio.numero" comment="metaexpedient.form.camp.expressio.numero.comentari" readonly="${bloquejarCamps}"/>
				
				<c:if test="${isDocumentsGeneralsEnabled}">
					<rip:inputCheckbox name="permetMetadocsGenerals" textKey="metaexpedient.form.camp.metadocs.nolligats.permetre" disabled="${bloquejarCamps}"/>
				</c:if>
				<rip:inputCheckbox name="gestioAmbGrupsActiva" textKey="metaexpedient.form.camp.gestioAmbGrupsActiva" disabled="${bloquejarCamps}"/>
			</div>
			<%-- <div role="tabpanel" class="tab-pane" id="notificacions">
				<rip:inputCheckbox name="notificacioActiva" textKey="metaexpedient.form.camp.notificacio.activa" disabled="${bloquejarCamps}"/>
			</div> --%>
			<c:if test="${isCarpetaDefecte}">
				<div role="carpetes" class="tab-pane" id="carpetes">
					
					<rip:arbreMultiple id="arbreCarpetes" atributId="id" atributNom="nom" arbre="${carpetes}" changedCallback="changedCallback" renamedCallback="renamedCallback" deletedCallback="deletedCallback"/>				
					<form:hidden path="estructuraCarpetesJson"/>
					<div id="botons_container" class="well">
						<input id="add_folder" onclick="addFolder();" type="button" class="btn btn-default" value="<spring:message code="metaexpedient.form.camp.estructura.subcarpeta"/>" readonly="${bloquejarCamps}">
						<input id="add_parent_folder" onclick="addParentFolder();" type="button" class="btn btn-info" value="<spring:message code="metaexpedient.form.camp.estructura.carpeta"/>" readonly="${bloquejarCamps}">
					</div>
				</div>
			</c:if>
			<div role="revisioEstatTab" class="tab-pane" id="revisioEstatTab">
				<c:if test="${isRolActualAdministrador}">
					<rip:inputSelect name="revisioEstat" optionEnum="MetaExpedientRevisioEstatEnumDto" textKey="metaexpedient.revisio.form.camp.estatRevisio"/>
				</c:if>
				<c:if test="${isRolAdminOrgan}">
					<rip:inputSelect name="revisioEstat" optionEnum="MetaExpedientRevisioEstatEnumDto" textKey="metaexpedient.revisio.form.camp.estatRevisio" disabled="${bloquejarCamps}"/>
				</c:if>
				<c:if test="${isRolActualRevisor}">
					<rip:inputSelect name="revisioEstat" optionEnum="MetaExpedientRevisioEstatEnumDto" textKey="metaexpedient.revisio.form.camp.estatRevisio" disabled="${bloquejarCamps}"/>
				</c:if>				
			</div>
		</div>
		<div id="modal-botons">
			<c:if test="${!consultar}">
				<button type="submit" class="btn btn-success" <c:if test="${bloquejarCamps}">disabled</c:if>><span class="fa fa-save"></span>
					<c:choose>
						<c:when test="${empty metaExpedientCommand.id}"><spring:message code="comu.boto.crear"/></c:when>
						<c:otherwise><spring:message code="comu.boto.modificar"/></c:otherwise>
					</c:choose>
				</button>	
			</c:if>		
			<a href="<c:url value="/metaExpedient"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>
