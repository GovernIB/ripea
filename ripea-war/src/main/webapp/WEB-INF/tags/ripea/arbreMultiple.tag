<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ attribute name="name" required="false"%>
<%@ attribute name="id" required="true"%>
<%@ attribute name="arbre" required="true" type="java.lang.Object"%>
<%@ attribute name="atributId" required="true"%>
<%@ attribute name="atributNom" required="true"%>
<%@ attribute name="seleccionatId"%>
<%@ attribute name="changedCallback"%>
<%@ attribute name="deletedCallback"%>
<%@ attribute name="renamedCallback"%>
<%@ attribute name="deselectAllCallback"%>
<%@ attribute name="fulles" type="java.lang.Object"%>
<%@ attribute name="fullesAtributId"%>
<%@ attribute name="fullesAtributNom"%>
<%@ attribute name="fullesAtributPare"%>
<%@ attribute name="fullesIcona"%>
<%@ attribute name="fullesAtributInfo"%>
<%@ attribute name="fullesAtributInfoText"%>
<%@ attribute name="isArbreSeleccionable" type="java.lang.Boolean"%>
<%@ attribute name="isFullesSeleccionable" type="java.lang.Boolean"%>
<%@ attribute name="isOcultarCounts" type="java.lang.Boolean"%>
<%@ attribute name="isContextMenuEnabled" type="java.lang.Boolean"%>
<%@ attribute name="isCheckboxEnabled" type="java.lang.Boolean"%>
<%@ attribute name="isError" type="java.lang.Boolean"%>
<%@ attribute name="height" required="false" rtexprvalue="true"%>
<%@ attribute name="withlabel" type="java.lang.Boolean"%>
<%@ attribute name="required" required="false" rtexprvalue="true"%>
<%@ attribute name="text" required="false" rtexprvalue="true"%>
<%@ attribute name="textKey" required="false" rtexprvalue="true"%>
<%@ attribute name="labelSize" required="false" rtexprvalue="true"%>
<%@ attribute name="inputSize" required="false" rtexprvalue="true"%>

<c:set var="campPath" value="${name}"/>
<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
<c:set var="campLabelText"><c:choose><c:when test="${not empty textKey}"><spring:message code="${textKey}"/></c:when><c:when test="${not empty text}">${text}</c:when><c:otherwise>${campPath}</c:otherwise></c:choose><c:if test="${required}"> *</c:if></c:set>
<c:set var="campLabelSize"><c:choose><c:when test="${not empty labelSize}">${labelSize}</c:when><c:otherwise>4</c:otherwise></c:choose></c:set>
<c:set var="campInputSize"><c:choose><c:when test="${not empty inputSize}">${inputSize}</c:when><c:otherwise>${12 - campLabelSize}</c:otherwise></c:choose></c:set>

<c:if test="${empty isArbreSeleccionable and empty isFullesSeleccionable}"><c:set var="isArbreSeleccionable" value="${true}"/><c:set var="isFullesSeleccionable" value="${true}"/></c:if>
<c:if test="${empty isOcultarCounts}"><c:set var="isOcultarCounts" value="${false}"/></c:if>
<c:if test="${empty isContextMenuEnabled}"><c:set var="isContextMenuEnabled" value="${false}"/></c:if>
<c:if test="${empty inicialitzar}"><c:set var="inicialitzar" value="${true}"/></c:if>
<c:if test="${empty isCheckboxEnabled}"><c:set var="isCheckboxEnabled" value="${false}"/></c:if>

<form:hidden path="${name}"/>

<c:if test="${withlabel}">
<div class="form-group<c:if test="${not empty campErrors}"> has-error</c:if>"<c:if test="${multiple}"> data-toggle="multifield"</c:if>>
<label class="control-label col-xs-${campLabelSize}" for="${campPath}">${campLabelText}</label>
	<div class="col-xs-${campInputSize}">

</c:if>
	<ul class="well" style="width: 100%; overflow: auto;">
		<c:if test="${empty arbre}">
			<div class="arbre-emtpy"><spring:message code='metaexpedient.form.camp.estructura.arbre.empty'/></div>
		</c:if>
		<div id="${id}">
			<c:forEach items="${arbre}" var="pare">
				<c:if test="${not empty pare and not empty pare.arrel}">
						<c:set var="arrel" value="${pare.arrel}"/>
						<ul>
							<li id="${pare.arrel.dades[atributId]}" class="jstree-close" data-jstree='{"icon":"fa fa-folder fa-lg"<c:if test="${not empty seleccionatId and pare.arrel.dades[atributId] == seleccionatId}">, "selected": true</c:if>}'>
								${pare.arrel.dades[atributNom]}
								<rip:arbreFills pare="${pare.arrel}" fills="${pare.arrel.fills}" atributId="${atributId}" atributNom="${atributNom}" 
								seleccionatId="${seleccionatId}" fulles="${fulles}" fullesIcona="${fullesIcona}" fullesAtributId="${fullesAtributId}" 
								fullesAtributNom="${fullesAtributNom}" fullesAtributPare="${fullesAtributPare}" />
							</li>
						</ul>
				</c:if>
			</c:forEach>
		</div>
	</ul>
<c:if test="${not empty campErrors}"><p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;<form:errors path="${campPath}"/></p></c:if>
<c:if test="${withlabel}">
	</div>
</div>
</c:if>

<script>
	(function ($) {
		$.jstree.defaults.conditionalselect = function () { return true; };
		$.jstree.defaults.conditionalhover = function () { return true; };
		$.jstree.plugins.conditionalselect = function (options, parent) {
			this.select_node = function (obj, supress_event, prevent_open) {
				if (this.settings.conditionalselect.call(this, this.get_node(obj))) {
					parent.select_node.call(this, obj, supress_event, prevent_open);
				} else {
					parent.deselect_all.call(this, obj, supress_event, prevent_open);
				}
			};
		};
		$.jstree.plugins.conditionalhover = function (options, parent) {
			this.hover_node = function (obj, supress_event, prevent_open) {
				if (this.settings.conditionalhover.call(this, this.get_node(obj))) {
					parent.hover_node.call(this, obj, supress_event, prevent_open);
				}
			};
		};
	})(jQuery);
	$('#${id}').jstree({
		"conditionalselect": function(node) {
			<c:choose>
				<c:when test="${isArbreSeleccionable and isFullesSeleccionable}">return true;</c:when>
				<c:when test="${not isArbreSeleccionable and isFullesSeleccionable}">return node.icon.indexOf('${fullesIcona}') != -1;</c:when>
				<c:when test="${isArbreSeleccionable and not isFullesSeleccionable}">return node.icon.indexOf('${fullesIcona}') == -1;</c:when>
				<c:when test="${not isArbreSeleccionable and not isFullesSeleccionable}">return false;</c:when>
			</c:choose>
		},
		"conditionalhover": function(node) {
			<c:choose>
				<c:when test="${isArbreSeleccionable and isFullesSeleccionable}">return true;</c:when>
				<c:when test="${not isArbreSeleccionable and isFullesSeleccionable}">return node.icon.indexOf('${fullesIcona}') != -1;</c:when>
				<c:when test="${isArbreSeleccionable and not isFullesSeleccionable}">return node.icon.indexOf('${fullesIcona}') == -1;</c:when>
				<c:when test="${not isArbreSeleccionable and not isFullesSeleccionable}">return false;</c:when>
			</c:choose>
		},
		"plugins": ["conditionalselect", "conditionalhover", "types", ${isContextMenuEnabled} ? "contextmenu" : "", "crrm", ${isCheckboxEnabled} ? "checkbox" : ""],
		"core": {
			"check_callback": true,
			strings : {
	            'New node': '<spring:message code="metaexpedient.form.camp.estructura.newnode"/>'
	        }
		},
		"types" : {
			"default" : {
				"icon" : "fa fa-folder fa-lg"
				}
			},
		"contextmenu" : {
			"items" : {
				"create" : {
					"separator_before"  : false,
				    "separator_after"   : true,
				    "label"             : '<spring:message code="metaexpedient.form.camp.estructura.subcarpeta"/>',
				    "icon" 				: "fa fa-plus",
				    "action"            : function (data) {
									    	var ref = $.jstree.reference(data.reference);
						                    sel = ref.get_selected();
						                    if(!sel.length) { return false; }
						                    sel = sel[0];
						                    sel = ref.create_node(sel, {"type":"file"});
						                    if(sel) {
						                        ref.edit(sel);
						                    }
										}
				}
			<c:if test="${not empty renamedCallback}">,
				"rename" : {
					"separator_before"  : false,
				    "separator_after"   : false,
				    "label"             : '<spring:message code="metaexpedient.form.camp.estructura.renombrar"/>',
				    "icon" 				: "fa fa-pencil",
				    "action"            : function (data) {
									    	var inst = $.jstree.reference(data.reference);
					                    	obj = inst.get_node(data.reference);
					                    	inst.edit(obj);
					                    }
				}
			</c:if>
			<c:if test="${not empty deletedCallback}">,
                "Delete": {
                    "label"	: '<spring:message code="metaexpedient.form.camp.estructura.esborrar"/>',
                    "icon" 	: "fa fa-trash",
                    "action": function (data) {
                        var ref = $.jstree.reference(data.reference),
                            sel = ref.get_selected();
                        if(!sel.length) { return false; }
                        ref.delete_node(sel);
						
                    }
                }
			</c:if>
			}
		}
	})
	.on('ready.jstree', function (e, data) {
		$('#${id}').jstree('open_all');
		var json = $('#${id}').data().jstree.get_json()
		var jsonString = JSON.stringify(json);
		$('#estructuraCarpetesJson').val(jsonString);
		webutilModalAdjustHeight();
	})
	<c:if test="${not empty renamedCallback}">
	.on('rename_node.jstree', function (e, data) {
		//console.log('>>> rename.jstree');
		return ${renamedCallback}(e, data);
	})
	</c:if>
	<c:if test="${not empty deletedCallback}">
	.on('delete_node.jstree', function (e, data) {
		//console.log('>>> deleted.jstree');
		return ${deletedCallback}(e, data);
	})
	</c:if>
	.on('after_open.jstree', function (e, data) {
		// var iframe = $('.modal-body iframe', window.parent.document);
		// var height = $('html').height();
		// iframe.height(height + 'px');
	})
	.on('after_close.jstree', function (e, data) {
		// var iframe = $('.modal-body iframe', window.parent.document);
		// var height = $('html').height();
		// iframe.height(height + 'px');
	})<c:if test="${not empty changedCallback}">
	.on('create_node.jstree', function (e, data) {
		//console.log('>>> changed.jstree');
		return ${changedCallback}(e, data);
	})
	.on('changed.jstree', function (e, data) {
		//console.log('>>> changed.jstree');
		return ${changedCallback}(e, data);
	})</c:if><c:if test="${not empty deselectAllCallback}">
	.on('deselect_all.jstree', function (e, data) {
		//console.log('>>> deselect_all.jstree');
		//return ${changedCallback}(e, data);
	})</c:if>;	
</script>
</ul>