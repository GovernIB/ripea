<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:choose>
	<c:when test="${empty metaExpedientCommand.id}"><c:set var="titol"><spring:message code="metaexpedient.form.titol.crear"/></c:set></c:when>
	<c:otherwise><c:set var="titol"><spring:message code="metaexpedient.form.titol.modificar"/></c:set></c:otherwise>
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
		$(document).ready(function() {
			var selectOrganGestorContainer = $("select#organGestorId").parent().parent(); 
			$( "#comu" ).change(function () {
				if(this.checked) {
					selectOrganGestorContainer.hide();
					$("select#organGestorId").val(null);
					$("select#organGestorId").trigger("change");
			    } else {
			    	selectOrganGestorContainer.show();
			    }
		  	});
			if ($("#comu").is(":checked")) {
				selectOrganGestorContainer.hide();
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
		$(document).ready(function(){
			$('#importMetaExpedient').on('click', function() {
				$('#metaExpedientCommand').hide();
				$("#loading").show();
				var codiSia = $('#classificacioSia').val();
				
				if (codiSia != null && codiSia != "") {
					$.get("<c:url value="/metaExpedient/importMetaExpedient/"/>" + codiSia)
					.done(function(json){
						if (json.error) {
							$('#contingut-missatges').append('<div class="alert alert-danger"><button type="button" class="close-alertes" data-dismiss="alert" aria-hidden="true"><span class="fa fa-times"></span></button>' + json.errorMsg + '</div>');

						} else {
							if (json.data){
								data = json.data;
								$('#nom').val(data.nom);
								$('#descripcio').val(data.resum);
								
								if ($("#comu:checked").val() != data.comu) {
									$("#comu").click();
								}
								if (!data.comu) {
									$('#organGestorId').data('currentValue', data.organId);
									$('#organGestorId').webutilInputSuggest();
								}	
							} else {
								$('#contingut-missatges').append('<div class="alert alert-warning"><button type="button" class="close-alertes" data-dismiss="alert" aria-hidden="true"><span class="fa fa-times"></span></button><spring:message code="metaexpedient.form.import.rolsac.no.results"/></div>');
							}
						}
					})
					.fail(function() {
						alert("<spring:message code="error.jquery.ajax"/>");
					})
					.always(function() {
						$("#loading").hide();
						$('#metaExpedientCommand').show();
					});
				} else {
					$("#loading").hide();
					$('#metaExpedientCommand').show();
				}
			});	
					

			$('#revisioEstat').on('change', function() {
				var estat = $(this).val();
				if (estat=='REBUTJAT') {
	            	$("label[for='revisioComentari']").append( " *" );
				} else {
					$("label[for='revisioComentari']").text( $("label[for='revisioComentari']").text().replace(' *', '') );
				}
			});
					
		});
	
	</script>	
	
	
	
	
	
</head>
<body>

	<div id="loading" style="display: none;"><div style="text-align: center; padding-bottom: 100px; color: #666666; margin-top: 100px;"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div></div>
	<c:set var="formAction"><rip:modalUrl value="/metaExpedient"/></c:set>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="metaExpedientCommand">
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
		<form:hidden path="id"/>
		<form:hidden path="entitatId"/>
		<form:hidden path="RolAdminOrgan"/>
		<br/>
		<div class="tab-content">
			<div role="tabpanel" class="tab-pane active" id="dades">
			
				<rip:inputText name="codi" textKey="metaexpedient.form.camp.codi" required="true" readonly="${bloquejarCamps}"/>
				
				<c:set var="campErrors"><form:errors path="classificacioSia"/></c:set>
				<div class="form-group<c:if test="${not empty campErrors}"> has-error</c:if>"<c:if test="${multiple}"> data-toggle="multifield"</c:if>>
					<label class="control-label col-xs-4" for="classificacioSia"><spring:message code="metaexpedient.form.camp.classificacio.sia"/> *</label>
					<div class="col-xs-6">
						<form:input path="classificacioSia" cssClass="form-control" id="classificacioSia" disabled="false" readonly="${bloquejarCamps}"/>	
						<c:if test="${not empty campErrors}"><p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;<form:errors path="classificacioSia"/></p></c:if>
					</div>
					<div class="col-xs-2">
						<button id="importMetaExpedient" type="button" class="btn btn-info" <c:if test="${bloquejarCamps}">disabled</c:if>><span class="fa fa-upload"></span> <spring:message code="comu.boto.importar"/></button>
					</div>
				</div>
				
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
				<rip:inputSuggest 
 					name="organGestorId"  
 					urlConsultaInicial="${urlConsultaInicial}"
 					urlConsultaLlistat="${urlConsultaLlistat}"
 					textKey="metaexpedient.form.camp.organgestor"
 					suggestValue="id"
 					suggestText="codiINom"
 					required="true"
 					disabled="${bloquejarCamps}"/>
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
					<rip:inputTextarea name="revisioComentari" textKey="metaexpedient.revisio.form.camp.comentari" required="${metaExpedientRevisioCommand.revisioEstat=='REBUTJAT'}"/>
				</c:if>
				<c:if test="${isRolAdminOrgan}">
					<rip:inputSelect name="revisioEstat" optionEnum="MetaExpedientRevisioEstatPerAdminOrganEnumDto" textKey="metaexpedient.revisio.form.camp.estatRevisio" disabled="${bloquejarCamps}"/>
					<rip:inputTextarea name="revisioComentari" textKey="metaexpedient.revisio.form.camp.comentari" required="false" disabled="${bloquejarCamps}"/>
				</c:if>
			</div>
		</div>
		<div id="modal-botons">
			<button type="submit" class="btn btn-success" <c:if test="${bloquejarCamps}">disabled</c:if>><span class="fa fa-save"></span> <spring:message code="comu.boto.guardar"/></button>
			<a href="<c:url value="/metaExpedient"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>
