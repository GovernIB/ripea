<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
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
<%@ attribute name="isError" type="java.lang.Boolean"%>
<%@ attribute name="height" required="false" rtexprvalue="true"%>

<c:if test="${empty isArbreSeleccionable and empty isFullesSeleccionable}"><c:set var="isArbreSeleccionable" value="${true}"/><c:set var="isFullesSeleccionable" value="${true}"/></c:if>
<c:if test="${empty isOcultarCounts}"><c:set var="isOcultarCounts" value="${false}"/></c:if>

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
							<rip:arbreFills pare="${pare.arrel}" fills="${pare.arrel.fills}" atributId="${atributId}" atributNom="${atributNom}" seleccionatId="${seleccionatId}"/>
						</li>
					</ul>
			</c:if>
		</c:forEach>
	</div>
</ul>
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
		"plugins": ["conditionalselect", "conditionalhover", "types", "contextmenu", "crrm"],
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
				},
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
				},
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
			}
		}
	})
	.on('ready.jstree', function (e, data) {
		$('#${id}').jstree('open_all');
		var json = $('#${id}').data().jstree.get_json()
		var jsonString = JSON.stringify(json);
		$('#estructuraCarpetesJson').val(jsonString);
	})
	<c:if test="${not empty deletedCallback}">
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
	})</c:if><c:if test="${not empty deselectAllCallback}">
	.on('deselect_all.jstree', function (e, data) {
		//console.log('>>> deselect_all.jstree');
		//return ${changedCallback}(e, data);
	})</c:if>;	
</script>
</ul>