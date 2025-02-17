<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<style>

#nodeDades .form-group {
	margin-bottom: 6px;
}
#nodeDades input.form-control {
	width: 322px;
}
#nodeDades input.multiple {
	width: 280px;
	!
	important
}
.dominis ~ span > .selection {
	width: 100%;
}
</style>

<script type="text/javascript">

$(document).ready(function() {

	$('form#nodeDades').on('change', 'input[data-toggle="checkbox"]', function() {
		if($(this).attr('value') == 'true'){
			setCheckboxFalse($(this), false);
		} else{
			setCheckboxTrue($(this));
		}
		
	});
	
	$('form#nodeDades').on('DOMNodeInserted', 'div[data-multifield-clon="true"]', function () {
		$(this).find('input').prop('disabled', '');
	});
	
	$('#nodeDades input').change(nodeDadesInputChange);

	$('#dades').on('submit', 'form#nodeDades', function() {
		showLoadingModal('<spring:message code="contingut.dades.form.processant"/>');
		$.post(
				'../ajax/contingutDada/${expedientId}/save',
				$(this).serialize(),
				function (data) {
					if (data.estatOk) {
						$('#nodeDades input').each(function() {
							var $pare = $(this).parent();
							if ($pare.hasClass('has-warning') || $pare.hasClass('has-error')) {
								$pare.removeClass('has-success');
								$pare.removeClass('has-warning');
								$pare.removeClass('has-error');
								$pare.addClass('has-success has-feedback');
								$(this).next().removeClass().addClass('glyphicon glyphicon-ok form-control-feedback');
								$(this).attr('title', 'Valor guardat correctament');
							} else {
								$pare.removeClass('has-success');
								$pare.removeClass('has-feedback');
								$(this).next().removeClass();
								$(this).removeAttr('title');
							}
						});
// 						$.get(
// 								'../ajax/contingutDada/${contingut.id}/count',
// 								function (data) 
<%-- 									<meta name="subtitle" content="${serveiPerTitol}"/>{ --%>
// 									$('#dades-count').html(data);
// 								});
						
					} else {
						$('#nodeDades input').each(function() {
							for (var i = 0; i < data.errorsCamps.length; i++) {
								var error = data.errorsCamps[i];
								if (error.camp == $(this).attr('name')) {
									var $pare = $(this).parent();
									$pare.removeClass('has-success');
									$pare.removeClass('has-warning');
									$pare.removeClass('has-error');
									$pare.addClass('has-error has-feedback');
									$(this).next().removeClass().addClass('glyphicon glyphicon-warning-sign form-control-feedback');
									$(this).attr('title', error.missatge);
									break;
								}
							}
						});
					}
// 					webutilRefreshMissatges();
					location.reload();
				});
		return false;
	});	
	
	$('form#nodeDades td .form-group').on('clone.multifield', function(event, clon) {
		$('input', clon).change(nodeDadesInputChange);
// 		var url = '<c:url value="/contingutDada/' + $('#contingutId').val() + '/' + $('input', clon).attr('id') + '"/>';
// 		$.ajax({
// 	        type: "GET",
// 	        url: url,
// 	        success: function (result) {
// 	        	$('input', clon).val(result);
// 	        	$('input', clon).trigger("focusout");
// 	        }
// 		});
	});

	$('input[data-toggle="checkbox"]', this).each(function() {
		$(this).attr('type', 'checkbox');
		var isDisabled = $(this).closest('div.form-group').data('toggle') == "multifield";
		if($(this).attr('value') == 'true'){
			$(this).attr('checked', 'checked');
			
		}else{
			setCheckboxFalse($(this), isDisabled);
		}
	});
	
});//################################################## document ready END ##############################################################

var nodeDadesInputChange = function() {
	var $pare = $(this).parent();
	$pare.removeClass('has-success');
	$pare.removeClass('has-warning');
	$pare.removeClass('has-error');
	$pare.addClass('has-warning has-feedback');
	$(this).next().removeClass().addClass('glyphicon glyphicon-pencil form-control-feedback');
	$(this).attr('title', 'Valor modificat pendent de guardar');
}

function recuperarResultatDomini(
		metaExpedientId,
		metaDadaCodi,
		dadaValor,
		noAplica) {
	let pageSizeDominis = 20;
	
	var dadaValorUrl = '<c:url value="/metaExpedient/metaDada/domini/' + metaDadaCodi + '/valor"/>';
	var multipleUrl = '<c:url value="/metaExpedient/metaDada/domini/' + metaDadaCodi + '"/>';
	var selDomini = $("#" + metaDadaCodi);

	if (dadaValor != '' && dadaValor != 'NO_APLICA') {
		if (dadaValor.includes(',')) {
			var dades = dadaValor.split(',');
			dades.forEach(function(dada) {
				$.ajax({
			        type: "GET",
			        url: dadaValorUrl,
			        data: {dadaValor: dada},
			        async: false,
			        success: function (resultat) {
			        	var newOption = new Option(resultat.text, resultat.id, false, false);
			        	selDomini.append(newOption);
					}
			    });
			});
			selDomini.val(dades).trigger('change');
		} else {
		
			$.ajax({
		        type: "GET",
		        url: dadaValorUrl,
		        data: {dadaValor: dadaValor},
		        success: function (resultat) {
		        	var newOption = new Option(resultat.text, resultat.id, false, false);
		        	selDomini.append(newOption);
		        	selDomini.val(resultat.id).trigger('change');
				}
		    });
		}
	}
	//selDomini.empty();
	var select2Options = {
			language: "${requestLocale}",
	        theme: 'bootstrap',
			allowClear: true,
	        ajax: {
	            url: multipleUrl,
	            dataType: 'json',
	            delay: 250,
                global: false,
	            data: function (params) {
	                params.page = params.page || 1;
	                return {
	                	filter: params.term ? params.term : '',
	                    pageSize: pageSizeDominis,
	                    page: params.page
	                };
	            },
	            processResults: function (data, params) {
	                params.page = params.page || 1;
	                var dominis = [];
	                // empty option
	                dominis.push({
                        id: "", 
                        text: ""
                    })
                    if (JSON.parse(noAplica)) {
                    	dominis.push({
	                        id: "NO_APLICA", 
	                        text: "<spring:message code="contingut.dada.form.valor.noaplica"/>"
	                    })
					}
	                for (let i = 0; i < data.resultat.length; i++) {
	                	dominis.push({
	                        id: data.resultat[i].id, 
	                        text: data.resultat[i].text
	                    })
	                }
	                return {
	                    results: dominis,
	                    pagination: {
	                        more: params.term ? (params.page * data.totalElements < data.totalElements) : ((params.page * pageSizeDominis < data.totalElements) || (data.resultat.length > 0))
	                    }
	                };
	            },
	            cache: true
	        },
	        width: '100%',
	        minimumInputLength: 0
    };
	
	if (dadaValor == 'NO_APLICA') {
    	var newOption = new Option('<spring:message code="contingut.dada.form.valor.noaplica"/>', 'NO_APLICA', false, false);
    	selDomini.append(newOption);
    	selDomini.val('NO_APLICA').trigger('change');
	}
	
	selDomini.select2(select2Options);
}

function setCheckboxFalse($checkbox, isDisabled) {
	var hiddenCheckbox = $checkbox.clone(true);
	hiddenCheckbox.attr('type', 'hidden');
	hiddenCheckbox.attr('value', 'false');
	hiddenCheckbox.removeAttr('data-toggle');
	hiddenCheckbox.insertAfter($checkbox);
	if (!isDisabled)
		hiddenCheckbox.removeAttr('disabled')
	
	$checkbox.removeAttr('checked')
	$checkbox.attr('value', 'false');
}

function setCheckboxTrue($checkbox) {
	$checkbox.attr('value', 'true');
	$checkbox.attr('checked', 'checked');
	console.log("set to true");
	console.log($checkbox.next());
	$checkbox.next().remove();
}

</script>

<c:choose>
	<c:when test="${not empty metaDades}">
		<form:form id="nodeDades" modelAttribute="dadesCommand" cssClass="form-inline">
			<c:if test="${potModificar}">
				<button type="submit" class="btn btn-default pull-right" style="margin-bottom: 6px"><span class="fa fa-save"></span> <spring:message code="comu.boto.guardar"/></button>
			</c:if>
			<table class="table table-striped table-bordered" style="width:100%">
			<thead>
				<tr>
					<th><spring:message code="contingut.dades.columna.dada"/></th>
					<th width="340"><spring:message code="contingut.dades.columna.valor"/></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="metaDada" items="${metaDades}">
					<c:set var="dadaValor"></c:set>
					<c:forEach var="dada" items="${expedient.dades}">
						<c:if test="${dada.metaDada.codi == metaDada.codi}">
							<c:set var="dadaValor">${dada.valorMostrar}</c:set>
						</c:if>
					</c:forEach>
					<c:set var="isMultiple" value="${metaDada.multiplicitat == 'M_0_N' or metaDada.multiplicitat == 'M_1_N'}"/>
					<c:set var="multipleClass" value=""/>
					<c:if test="${isMultiple}"><c:set var="multipleClass" value=" multiple"/></c:if>
					<tr>
						<td>${metaDada.nom}</td>
						<td>
							<c:choose>
								<c:when test="${potModificar}">
									<div class="form-group ${metaDada.tipus == 'DOMINI' ? '' :''}" ${metaDada.tipus == 'DOMINI' ? 'style="width: 100%;margin-bottom: -10px;"' :''} <c:if test="${isMultiple}"> data-toggle="${metaDada.tipus != 'DOMINI' ? 'multifield' : ''}" data-nou="true"</c:if>>
										<label class="hidden" for="${metaDada.codi}"></label>
										<div class="controls">
											<c:choose>
												<c:when test="${metaDada.tipus == 'SENCER'}">
													<form:input path="${metaDada.codi}" id="${metaDada.codi}" data-toggle="autonumeric" data-a-dec="," data-a-sep="" data-m-dec="0" class="form-control text-right${multipleClass}"></form:input>
												</c:when>
												<c:when test="${metaDada.tipus == 'FLOTANT'}">
													<form:input path="${metaDada.codi}" id="${metaDada.codi}" data-toggle="autonumeric" data-a-dec="," data-a-sep="" data-m-dec="10" data-a-pad="false" class="form-control text-right${multipleClass}"></form:input>
												</c:when>
												<c:when test="${metaDada.tipus == 'IMPORT'}">
													<form:input path="${metaDada.codi}" id="${metaDada.codi}" data-toggle="autonumeric" data-a-dec="," data-a-sep="." data-m-dec="2" class="form-control text-right${multipleClass}"></form:input>
												</c:when>
												<c:when test="${metaDada.tipus == 'DATA'}">
													<form:input path="${metaDada.codi}" id="${metaDada.codi}" data-toggle="datepicker" data-idioma="${requestLocale}" data-a-dec="," data-a-sep="." data-m-dec="2" cssClass="form-control text-right${multipleClass}"></form:input>
												</c:when>
												<c:when test="${metaDada.tipus == 'BOOLEA'}">
												<label>
													<form:input path="${metaDada.codi}" id="${metaDada.codi}" data-toggle="checkbox" data-a-dec="," data-a-sep="." data-m-dec="2" class="${multipleClass}"></form:input>
												</label>
												</c:when>
												<c:when test="${metaDada.tipus == 'DOMINI'}">
												
													<form:select path="${metaDada.codi}" id="${metaDada.codi}" cssStyle="width: 100%" cssClass="form-control${multipleClass} dominis" multiple="${isMultiple ? true : false}"/>
													<script type="text/javascript">
													recuperarResultatDomini(
															"${contingut.metaNode.id}",
															"${metaDada.codi}",
															"${dadaValor}",
															"${metaDada.noAplica}");
													</script>
												</c:when>
												<c:otherwise>
													<form:input path="${metaDada.codi}" id="${metaDada.codi}" cssClass="form-control${multipleClass}"></form:input>
												</c:otherwise>
											</c:choose>
											<span class="" aria-hidden="true"></span>
										</div>
									</div>
								</c:when>
								<c:when test="${expedientTancat && (metaDada.tipus == 'DOMINI')}">
									<form:select path="${metaDada.codi}" id="${metaDada.codi}" cssStyle="width: 100%" data-toggle="select2" cssClass="form-control${multipleClass} dominis" multiple="${isMultiple ? true : false }" disabled="true"/>
									<script type="text/javascript">
										recuperarResultatDomini(
												"${contingut.metaNode.id}",
												"${metaDada.codi}",
												"${dadaValor}",
												"${metaDada.noAplica}");
										
									</script>
								</c:when>
								<c:otherwise>
									<c:choose>
										<c:when test="${metaDada.tipus == 'BOOLEA' && not empty dadaValor}">
											<spring:message code="comu.${dadaValor}"/>
										</c:when>
										<c:otherwise>${dadaValor}</c:otherwise>
									</c:choose>
								</c:otherwise>
							</c:choose>
						</td>
					</tr>
				</c:forEach>
			</tbody>
			</table>
			<c:if test="${potModificar}">
				<button type="submit" class="btn btn-default pull-right" style="margin-top: -14px"><span class="fa fa-save"></span> <spring:message code="comu.boto.guardar"/></button>
			</c:if>
		</form:form>
	</c:when>
	<c:otherwise>
	</c:otherwise>
</c:choose>