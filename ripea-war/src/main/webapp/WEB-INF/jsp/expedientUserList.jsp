<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<%
//pageContext.setAttribute(
//		"expedientEstatsOptdions",
//		es.caib.ripea.war.helper.EnumHelper.getOptionsForEnum(
//		es.caib.ripea.core.api.dto.ExpedientEstatEnumDto.class,
//		"expedient.estat.enum."));
%>
<html>
<head>
	<title><spring:message code="expedient.list.user.titol"/></title>
	<script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/webjars/datatables.net-select/1.3.1/js/dataTables.select.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-select-bs/1.2.3/css/select.bootstrap.min.css"/>" rel="stylesheet"></link>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
<style>
#expedientFiltreForm {
	margin-bottom: 15px;
}
table.dataTable tbody > tr.selected, table.dataTable tbody > tr > .selected {
	background-color: #fcf8e3;
	color: #666666;
}
table.dataTable thead > tr.selectable > :first-child, table.dataTable tbody > tr.selectable > :first-child {
	cursor: pointer;
}
.datepicker{
	padding-left: 12px;
	padding-right: 12px;
}
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
/* Adapt font color if the background is light or dark  */
/* .dark-color { color: black }
.light-color { color: white }	 */


table.dataTable tbody tr.selected a, table.dataTable tbody th.selected a, table.dataTable tbody td.selected a {
    color: #333;
}


table.dataTable tr > td:nth-child(6) {
	word-wrap: break-word; 
	max-width: 1px;
}
</style>
<script>

var mostrarMeusExpedients = '${meusExpedients}' === 'true';
var mostrarExpedientsFirmaPendent = '${firmaPendent}' === 'true';
var columnaAgafatPer = 19;
$(document).ready(function() {


	$('#taulaDades').on('selectionchange.dataTable', function (e, accio, ids) {
		$.get(
				"expedient/" + accio,
				{ids: ids},
				function(data) {
					$("#seleccioCount").html(data);
				}
		);
	});
	$('#taulaDades').on('draw.dt', function () {
		$('#seleccioAll').on('click', function() {
			$.get(
					"expedient/select",
					function(data) {
						$("#seleccioCount").html(data);
						$('#taulaDades').webutilDatatable('refresh');
					}
			);
			return false;
		});
		$('#seleccioNone').on('click', function() {
			$.get(
					"expedient/deselect",
					function(data) {
						$("#seleccioCount").html(data);
						$('#taulaDades').webutilDatatable('select-none');
						$('#taulaDades').webutilDatatable('refresh');
					}
			);
			return false;
		});
		$('#taulaDades').DataTable().column(columnaAgafatPer).visible(!mostrarMeusExpedients);
		$("span[class^='stateColor-']").each(function( index ) {
		    var fullClassNameString = this.className;
		    var colorString = fullClassNameString.substring(11);
			if (colorString != "") {
				$(this).parent().css( "background-color", colorString );
				if (adaptColor(colorString)) {
					$(this).parent().css( "color", "white" );
				}
				$(this).parent().parent().css( "box-shadow", "-6px 0 0 " + colorString );
			}
		});
		

		$("a.fileDownload").on("click", function() {
			$("body").addClass("loading");
			checkLoadingFinished();
	    });


		$('#agafar_lnk').on('click', function() {
			$("body").addClass("loading");
		});
		    
	});
	if (mostrarMeusExpedients) {
		$('#taulaDades').DataTable().column(columnaAgafatPer).visible(false);
	}
	$('#meusExpedientsBtn').click(function() {
		mostrarMeusExpedients = !$(this).hasClass('active');
		// Modifica el formulari
		$('#meusExpedients').val(mostrarMeusExpedients);
		$(this).blur();
		// Estableix el valor de la cookie
		setCookie("${nomCookieMeusExpedients}", mostrarMeusExpedients);
		// Amaga la columna i refresca la taula
		$('#taulaDades').webutilDatatable('refresh');
	})
	$('#ambFirmaPendentBtn').click(function() {
		mostrarExpedientsFirmaPendent = !$(this).hasClass('active');
		// Modifica el formulari
		$('#ambFirmaPendent').val(mostrarExpedientsFirmaPendent);
		$(this).blur();
		// Estableix el valor de la cookie
		setCookie("${nomCookieFirmaPendent}", mostrarExpedientsFirmaPendent);
		// Amaga la columna i refresca la taula
		$('#taulaDades').webutilDatatable('refresh');
	})
	$(".email-user").click(function(e) {
		e.preventDefault();
		e.stopPropagation();
		alert("Button Clicked");
	});
	
	// Mostrar els procediments al filtre
	var organGestorId = $("#organGestorId").val();
	if (organGestorId) {
		findActiusPerLectura(organGestorId);
	} else {
		findActiusPerLectura();
	}
	
	var metaExpedientId = "";
	var counter = 0;
	$('#metaExpedientId').on('change', function() {
		metaExpedientId = $(this).val();
		if (counter != 0) {
			if (metaExpedientId) {
				var baseUrl = "<c:url value="/expedient/estatValues/"/>";
				if (/;jsessionid/.test(baseUrl))
					baseUrl = baseUrl.substring(0, baseUrl.indexOf(";jsessionid"));
				$.get(baseUrl + metaExpedientId)
				.done(function(data) {
					
					$('#expedientEstatId').select2('val', '', true);
					$('#expedientEstatId option[value!=""]').remove();
					for (var i = 0; i < data.length; i++) {
						$('#expedientEstatId').append('<option value="' + data[i].id + '">' + data[i].nom + '</option>');
					}
				})
				.fail(function() {
					alert("<spring:message code="error.jquery.ajax"/>");
				});
			} else {
				setObertTancat();
			}
		}
		counter++;
	});

	var multipleUrl = '<c:url value="/metaExpedient/metaDadaPermisLectura/domini"/>';
	$.get(multipleUrl)
		.done(function(data) {
			var campDomini = $('#metaExpedientDominiCodi');
			campDomini.empty();
			campDomini.append("<option value=\"\"></option>");
			data.forEach(function(domini) {
				if(domini.codi == '${expedientFiltreCommand.metaExpedientDominiCodi}') {
					campDomini.append('<option value="' + domini.codi + '" selected>' + domini.nom + '</option>');
					$('#metaExpedientDominiCodi').trigger('change');
				} else {
					campDomini.append('<option value="' + domini.codi + '">' + domini.nom + '</option>');
				}
			});
		})
		.fail(function(e) {
			alert("<spring:message code="error.jquery.ajax"/>");
		});
						
	$('#metaExpedientId').trigger('change');
	
	let pageSizeDominis = 20;		
	$('#metaExpedientDominiCodi').on('change', function() {
		var selDomini = $("#metaExpedientDominiValor");
		var dominiCodi= $(this).val();
		var multipleUrl = '<c:url value="/metaExpedient/metaDada/domini/' + dominiCodi + '"/>';
		selDomini.empty();
		selDomini.append("<option value=\"\"></option>");
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
		selDomini.select2(select2Options);
		
		if ('${expedientFiltreCommand.metaExpedientDominiValor}') {
			$.get("<c:url value="/metaExpedient/metaDada/domini/${expedientFiltreCommand.metaExpedientDominiCodi}/valor?dadaValor=${expedientFiltreCommand.metaExpedientDominiValor}"/>")
			.done(function(data) {
				var $option = $('<option selected>' + data.text + '</option>').val(data.id);
				selDomini.append($option).trigger('change');
			})
			.fail(function() {
				alert("<spring:message code="error.jquery.ajax"/>");
			});
		}
		
	});
	
	$('#organGestorId').on('change', function() {
		var organGestorId = $(this).val();
		findActiusPerLectura(organGestorId);

		/*$('#metaExpedientId').val('').trigger('change')

		if (organGestorId) {
			$("#metaExpedientId").data('urlParamAddicional', organGestorId);
		} else {
			$("#metaExpedientId").data('urlParamAddicional', null);

		}*/

	});

					

	
});

function findActiusPerLectura(organId) {
	var findUrl;
	if (organId != undefined) {
		findUrl = '<c:url value="/metaExpedient/findPerLectura?organId="/>' + organId;
	} else {
		findUrl = '<c:url value="/metaExpedient/findPerLectura/"/>';
	}
	var selProcediments = $("#metaExpedientId");
	var previousValue = selProcediments.val();
	$.ajax({
        type: "GET",
        url: findUrl,
        success: function (data) {
        	selProcediments.empty();
    		selProcediments.append("<option value=\"\"></option>");
    		if (data) {

    		    var procedimentsComuns = [];
    		    var procedimentsOrgan = [];
    		    $.each(data, function(i, val) {
    		        if(val.procedimentComu) {
    		            procedimentsComuns.push(val);
    		        } else {
    		            procedimentsOrgan.push(val);
    		        }
    		    });

                console.info(procedimentsComuns);
                console.info(procedimentsOrgan);

				var previousProcedimentStillSelected = false;
                if (procedimentsComuns.length > 0) {
                    selProcediments.append("<optgroup label='<spring:message code='expedient.list.user.procediment.comuns'/>'>");
                    $.each(procedimentsComuns, function(index, val) {
        				if(val.id == previousValue || val.id == '${expedientFiltreCommand.metaExpedientId}') {
        					selProcediments.append("<option value='" + val.id + "' selected>" + val.nom + " (" + val.classificacio + ")</option>");
        					previousProcedimentStillSelected = true;
        				} else {
        					selProcediments.append("<option value='" + val.id + "'>" + val.nom + " (" + val.classificacio + ")</option>");
        				}
                    });
                    selProcediments.append("</optgroup>");
                }
                if (procedimentsOrgan.length > 0) {
                	 selProcediments.append("<optgroup label='<spring:message code='expedient.list.user.procediment.organs'/>'>");
                    $.each(procedimentsOrgan, function(index, val) {
        				if(val.id == previousValue || val.id == '${expedientFiltreCommand.metaExpedientId}') {
        					selProcediments.append("<option value='" + val.id + "' selected>" + val.nom + " (" + val.classificacio + ")</option>");
        					previousProcedimentStillSelected = true;
        				} else {
        					selProcediments.append("<option value='" + val.id + "'>" + val.nom + " (" + val.classificacio + ")</option>");
        				}
                    });
                    selProcediments.append("</optgroup>");
                }


                if (previousValue && !previousProcedimentStillSelected) {
                	setObertTancat();
				}
    		} else {
    			setObertTancat();
        	}
        }
	});
	var select2Options = {theme: 'bootstrap', width: '100%', minimumInputLength: 0, allowClear: true, language: "${requestLocale}"}
	selProcediments.select2(select2Options);
}


	function setObertTancat() {
		$.get("<c:url value="/expedient/estatValues/"/>"+0)
		.done(function(data) {
			$('#expedientEstatId').select2('val', '', true);
			$('#expedientEstatId option[value!=""]').remove();
			for (var i = 0; i < data.length; i++) {
				$('#expedientEstatId').append('<option value="' + data[i].id + '">' + data[i].nom + '</option>');
			}
		})
		.fail(function() {
			alert("<spring:message code="error.jquery.ajax"/>");
		});
	}


function checkLoadingFinished() {
	var cookieName = "contentLoaded";
	if (getCookie(cookieName) != "") {
		$("body").removeClass("loading");
        removeCookie(cookieName);
		return;
	}
    setTimeout(checkLoadingFinished, 100);
}
	
function setCookie(cname,cvalue) {
	var exdays = 30;
    var d = new Date();
    d.setTime(d.getTime() + (exdays*24*60*60*1000));
    var expires = "expires=" + d.toGMTString();
    document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/";
}
function getCookie(cname) {
    var name = cname + "=";
    var ca = document.cookie.split(';');
    for(var i = 0; i < ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0) == ' ') {
            c = c.substring(1);
        }
        if (c.indexOf(name) == 0) {
            return c.substring(name.length, c.length);
        }
    }
    return "";
}
function removeCookie(cname) {
    var expires = new Date(0).toUTCString();
    document.cookie = cname + "=; path=/; expires=" + expires + ";";
}

function adaptColor(hexColor) {
	let adapt = false;

	let rgb = hexToRgb(hexColor);
	if (rgb != null) {
		var hsp = Math.sqrt(
				0.299 * (rgb.r * rgb.r) +
				0.587 * (rgb.g * rgb.g) +
				0.114 * (rgb.b * rgb.b)
		);
	}
	if (hsp < 127.5) {
		adapt = true;
	}
	return adapt;
}

function hexToRgb(hex) {
	// Expand shorthand form (e.g. "03F") to full form (e.g. "0033FF")
	var shorthandRegex = /^#?([a-f\d])([a-f\d])([a-f\d])$/i;
	hex = hex.replace(shorthandRegex, function(m, r, g, b) {
		return r + r + g + g + b + b;
	});

	var result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);
	return result ? {
		r: parseInt(result[1], 16),
		g: parseInt(result[2], 16),
		b: parseInt(result[3], 16)
	} : null;
}

</script>
</head>
<body>
	<form:form id="expedientFiltreForm" action="" method="post" cssClass="well" commandName="expedientFiltreCommand">
		<div class="row">
			<%--
			<div class="col-md-3">
				<c:url value="/metaexpedientajax/metaexpedient" var="urlConsultaInicial"/>
				<c:url value="/metaexpedientajax/metaexpedients" var="urlConsultaLlistat"/>
				<rip:inputSuggest 
 					name="metaExpedientId"  
 					urlConsultaInicial="${urlConsultaInicial}"
 					urlConsultaLlistat="${urlConsultaLlistat}"
 					placeholderKey="expedient.list.user.placeholder.tipusExpedient"
 					suggestValue="id"
 					suggestText="nom"
 					suggestTextAddicional="classificacio"
 					inline="true"
 					urlParamAddicional="${expedientFiltreCommand.organGestorId}"
 					/>				
			</div>
			--%>

			<div class="col-md-2">
				<rip:inputText name="numero" inline="true" placeholderKey="expedient.list.user.placeholder.numero.expedient"/>
			</div>
			<div class="col-md-4">
				<rip:inputText name="nom" inline="true" placeholderKey="expedient.list.user.placeholder.titol"/>
			</div>
			<div class="col-md-3">
				<rip:inputSelect name="expedientEstatId" optionItems="${expedientEstatsOptions}" optionValueAttribute="id" emptyOption="true" optionTextAttribute="nom" placeholderKey="expedient.list.user.placeholder.estat" inline="true"/>
			</div>	
			<div class="col-md-3">
				<rip:inputText name="interessat" inline="true" placeholderKey="expedient.list.user.placeholder.creacio.interessat"/>
			</div>	
		</div>
		<div class="row">
			<div class="col-md-3">
				<c:url value="/organgestorajax/organgestor" var="urlConsultaInicial"/>
				<c:url value="/organgestorajax/organgestor" var="urlConsultaLlistat"/>
				<rip:inputSuggest 
 					name="organGestorId"  
 					urlConsultaInicial="${urlConsultaInicial}"
 					urlConsultaLlistat="${urlConsultaLlistat}"
 					placeholderKey="metaexpedient.form.camp.organgestor"
 					suggestValue="id"
 					suggestText="codiINom"
 					inline="true"/>	
			</div>				
			<div class="col-md-3">
				<rip:inputSelect name="metaExpedientId" inline="true" emptyOption="true" optionMinimumResultsForSearch="6" placeholderKey="expedient.list.user.placeholder.procediment"/>
			</div>		
	
			<div class="col-md-3">
				<rip:inputDate name="dataCreacioInici" inline="true" placeholderKey="expedient.list.user.placeholder.creacio.inici"/>
			</div>
			<div class="col-md-3">
				<rip:inputDate name="dataCreacioFi" inline="true" placeholderKey="expedient.list.user.placeholder.creacio.fi"/>
			</div>	
		</div>
		
		<div class="row">
			<button type="submit" name="accio" value="filtrar" class="btn btn-primary" style="display:none;"></button>
			<c:if test="${isDominisEnabled}">
				<div class="col-md-3">
				<!-- rip:inputSelect name="metaExpedientDominiId" optionItems="${metaExpedientDominisOptions}"  emptyOption="true" placeholderKey="expedient.list.user.placeholder.domini" optionValueAttribute="id" optionTextAttribute="nom" inline="true"/-->
					<rip:inputSelect name="metaExpedientDominiCodi" placeholderKey="expedient.list.user.placeholder.domini" emptyOption="true" inline="true"/>
				</div>
				<c:choose>
					<c:when test="${rolActual!='tothom'}">
						<div class="col-md-2">
							<rip:inputSelect name="metaExpedientDominiValor" placeholderKey="expedient.list.user.placeholder.domini.value" emptyOption="true" inline="true"/>
						</div>
				 	</c:when>
				 	<c:otherwise>
						<div class="col-md-3">
							<rip:inputSelect name="metaExpedientDominiValor" placeholderKey="expedient.list.user.placeholder.domini.value" emptyOption="true" inline="true"/>
						</div>	
				 	</c:otherwise>
			 	</c:choose>
			</c:if>
			<div class="col-md-2">
				<rip:inputText name="numeroRegistre" inline="true" placeholderKey="expedient.list.user.placeholder.numeroregistre"/>
			</div>
			
			<c:if test="${fn:length(grups) > 1 }">
				<div class="col-md-3">
					<rip:inputSelect 
						name="grupId" 
						optionItems="${grups}"
						optionMinimumResultsForSearch="1" 
						optionValueAttribute="id" 
						emptyOption="true"
						optionTextAttribute="descripcio"
						placeholderKey="expedient.list.user.placeholder.grup" 
						inline="true" />
				</div>			
			</c:if>		
				
			<c:choose>
			 	<c:when test="${rolActual!='tothom'}">
					<div class="col-md-2">
						<c:url value="/userajax/usuariDades" var="urlConsultaInicial"/>
						<c:url value="/userajax/usuarisDades" var="urlConsultaLlistat"/>
						<rip:inputSuggest 
							name="agafatPer" 
							urlConsultaInicial="${urlConsultaInicial}" 
							urlConsultaLlistat="${urlConsultaLlistat}" 
							placeholderKey="expedient.list.user.placeholder.agafatPer"
							suggestValue="codi"
							suggestText="codiAndNom"
							inline="true"
							required="true"/>
					</div>
			 	</c:when>
			 	<c:otherwise>
					<div class="col-md-2" style="width: auto;">
						<button id="meusExpedientsBtn" title="<spring:message code="expedient.list.user.meus"/>" class="btn btn-default <c:if test="${meusExpedients}">active</c:if>" data-toggle="button"><span class="fa fa-lock"></span> <spring:message code="expedient.list.user.meus"/></button>
						<button id="ambFirmaPendentBtn" title="<spring:message code="expedient.list.user.pendent"/>" class="btn btn-default <c:if test="${firmaPendent}">active</c:if>" data-toggle="button"><span class="fa fa-pencil-square"></span> <spring:message code="expedient.list.user.pendent"/></button>
					</div>		
			 	</c:otherwise>
			 </c:choose>

			<rip:inputHidden name="meusExpedients"/>
			<rip:inputHidden name="ambFirmaPendent"/>
			<div class="col-md-3 pull-right" style="${rolActual == 'tothom' ? 'width:auto;' : ''}">
				<c:if test="${rolActual!='tothom'}">
					<button id="ambFirmaPendentBtn" title="<spring:message code="expedient.list.user.pendent"/>" class="btn btn-default <c:if test="${firmaPendent}">active</c:if>" data-toggle="button"><span class="fa fa-pencil-square"></span> <spring:message code="expedient.list.user.pendent"/></button>
				</c:if>
				<div class="pull-right">
					<button type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.boto.netejar"/></button>
					<button type="submit" name="accio" value="filtrar" class="btn btn-primary"><span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/></button>
				</div>
			</div>
			
		</div>
	</form:form>
	<div class="rmodal"></div>
	<!-- TODO --> 
	
	<script id="botonsTemplate" type="text/x-jsrender">
		<div class="text-right">
			<div class="btn-group">
				<button id="seleccioAll" title="<spring:message code="expedient.list.user.seleccio.tots"/>" class="btn btn-default"><span class="fa fa-check-square-o"></span></button>
				<button id="seleccioNone" title="<spring:message code="expedient.list.user.seleccio.cap"/>" class="btn btn-default"><span class="fa fa-square-o"></span></button>
				<div class="btn-group">
					<button class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
  						<span id="seleccioCount" class="badge">${fn:length(seleccio)}</span> <spring:message code="expedient.list.user.opcions"/> <span class="caret"></span>
					</button>
					<ul class="dropdown-menu">
						<li><a href="expedient/agafar"><span class="fa fa-lock"></span>&nbsp;&nbsp;<spring:message code="expedient.list.user.agafar"/></a></li>							
						<li><a href="expedient/alliberar"><span class="fa fa-unlock"></span>&nbsp;&nbsp;<spring:message code="expedient.list.user.alliberar"/></a></li>
						<li><a href="expedient/follow" data-toggle="ajax"><span class="fa fa-user-plus"></span>&nbsp;&nbsp;<spring:message code="expedient.list.user.follow"/></a></li>		
						<li><a href="expedient/unfollow" data-toggle="ajax"><span class="fa fa-user-times"></span>&nbsp;&nbsp;<spring:message code="expedient.list.user.unfollow"/></a></li>
						<li><a href="expedient/delete" data-confirm="<spring:message code="contingut.confirmacio.esborrar.node.multiple"/>"><span class="fa fa-trash-o"></span>&nbsp;<spring:message code="expedient.list.user.esborrar"/></a></li>
						<li><a href="expedient/export/ODS"><span class="fa fa-download"></span>&nbsp;&nbsp;<spring:message code="expedient.list.user.exportar.ODS"/></a></li>
						<li><a href="expedient/export/CSV"><span class="fa fa-download"></span>&nbsp;&nbsp;<spring:message code="expedient.list.user.exportar.CSV"/></a></li>
						<li><a class="fileDownload" href="expedient/generarIndex/ZIP"><span class="fa fa-download"></span>&nbsp;&nbsp;<spring:message code="expedient.list.user.recuperar.index.zip"/></a></li>
						<li><a class="fileDownload" href="expedient/generarIndex/PDF"><span class="fa fa-download"></span>&nbsp;&nbsp;<spring:message code="expedient.list.user.recuperar.index.pdf"/></a></li>
						<li><a class="fileDownload" href="expedient/exportarEni"><span class="fa fa-download"></span>&nbsp;&nbsp;<spring:message code="expedient.list.user.recuperar.exportacio.eni"/></a></li>
					</ul>
				</div>
			</div>
			<c:if test="${hasCreatePermissionForAnyProcediment}">
				<a href="<c:url value="/expedient/new"/>" data-toggle="modal" data-maximized="true" class="btn btn-default"><span class="fa fa-plus"></span> <spring:message code="expedient.list.user.nou"/></a>
			</c:if>
		</div>
	</script>
	<script id="rowhrefTemplate" type="text/x-jsrender">contingut/{{:id}}</script>
	<table
		id="taulaDades"
		data-toggle="datatable" 
		data-url="<c:url value="/expedient/datatable"/>" 
		class="table table-bordered table-striped table-hover" 
		data-default-order="18" 
		data-default-dir="desc"
		data-botons-template="#botonsTemplate"
		data-rowhref-template="#rowhrefTemplate"
		data-selection-enabled="true"
		data-save-state="true"
		style="width:100%">
		<thead>
			<tr>
				<th data-col-name="usuariActualWrite" data-visible="false"></th>
				<th data-col-name="usuariActualDelete" data-visible="false"></th>
				<th data-col-name="seguidor" data-visible="false"></th>
				<th data-col-name="agafat" data-visible="false"></th>
				<th data-col-name="agafatPer.codi" data-visible="false"></th>
				<th data-col-name="expedientEstat" data-visible="false"></th>
				<th data-col-name="alerta" data-visible="false"></th>
				<th data-col-name="valid" data-visible="false"></th>
				<th data-col-name="errorLastEnviament" data-visible="false"></th>
				<th data-col-name="errorLastNotificacio" data-visible="false"></th>
				<th data-col-name="ambEnviamentsPendents" data-visible="false"></th>
				<th data-col-name="ambNotificacionsPendents" data-visible="false"></th>
				<th data-col-name="arxiuUuid" data-visible="false"></th>
				<th data-col-name="conteDocumentsDefinitius" data-visible="false"></th>			
				<th data-col-name="numero" width="${separadorDefinit ? '10%' : ''}"><spring:message code="expedient.list.user.columna.numero"/></th>	
				<th data-col-name="nom" data-template="#cellNomTemplate" width="30%">
					<spring:message code="expedient.list.user.columna.titol"/>
					<script id="cellNomTemplate" type="text/x-jsrender">
						{{:nom}} 
					</script>
				</th>
				<th data-col-name="id" data-template="#cellAvisosTemplate" width="5%">
					<spring:message code="expedient.list.user.columna.avisos"/>
					<script id="cellAvisosTemplate" type="text/x-jsrender">
						{{if !valid}}<span class="fa fa-exclamation-triangle text-warning" title="<spring:message code="contingut.errors.expedient.validacio"/>"></span>{{/if}}
						{{if errorLastEnviament }}<span class="fa fa-pencil-square text-danger" title="<spring:message code="contingut.errors.expedient.enviaments"/>"></span>{{/if}}
						{{if errorLastNotificacio }}<span class="fa fa-envelope-square text-danger" title="<spring:message code="contingut.errors.expedient.notificacions"/>"></span>{{/if}}
						{{if ambEnviamentsPendents }}<span class="fa fa-pencil-square text-primary" title="<spring:message code="contingut.pendents.expedient.enviaments"/>"></span>{{/if}}
						{{if ambNotificacionsPendents }}<span class="fa fa-envelope-square text-primary" title="<spring:message code="contingut.pendents.expedient.notificacions"/>"></span>{{/if}}
						{{if alerta}}<span class="fa fa-exclamation-circle text-danger" title="<spring:message code="contingut.errors.expedient.alertes"/>"></span>{{/if}}			
						{{if arxiuUuid == null}}
							<span class="fa fa-exclamation-triangle text-danger" title="<spring:message code="contingut.icona.estat.pendentGuardarArxiu"/>"></span>
						{{/if}}
					</script>
				</th>
				<th data-col-name="tipusStr" data-orderable="false" width="20%"><spring:message code="expedient.list.user.columna.procediment"/></th>								
				<th data-col-name="createdDate" data-type="datetime" data-converter="datetime" nowrap><spring:message code="expedient.list.user.columna.createl"/></th>
				<c:if test="${usuariActual.expedientListDataDarrerEnviament}">
					<th data-col-name="dataDarrerEnviament" data-type="datetime" data-converter="datetime" nowrap data-orderable="false"><spring:message code="expedient.list.user.columna.data.enviament"/></th>
				</c:if>				
				<th data-col-name="estat" data-template="#cellEstatTemplate" width="11%">
					<spring:message code="expedient.list.user.columna.estat"/>
					<script id="cellEstatTemplate" type="text/x-jsrender">
						{{if expedientEstat != null && estat != 'TANCAT'}}
							<span class="fa fa-folder-open"></span>&nbsp;{{:expedientEstat.nom}}
						{{else}}
							{{if estat == 'OBERT'}}
								<span class="fa fa-folder-open"></span>&nbsp;<spring:message code="expedient.estat.enum.OBERT"/>
							{{else}}
								<span class="fa fa-folder"></span>&nbsp;<spring:message code="expedient.estat.enum.TANCAT"/>
							{{/if}}
						{{/if}}

						{{if ambRegistresSenseLlegir}}
							<span class="fa-stack" aria-hidden="true">
								<i class="fa fa-certificate fa-stack-1x" style="color: darkturquoise; font-size: 20px;"></i>
								<i class="fa-stack-1x" style="color: white;font-style: normal;font-weight: bold;">N</i>
							</span>
						{{/if}}
						{{if expedientEstat != null && expedientEstat.color!=null}}
							<span class="stateColor-{{:expedientEstat.color}}"></span>
						{{/if}}
					</script>
				</th>
				<c:if test="${usuariActual.expedientListAgafatPer}">
					<th data-col-name="agafatPer.codiAndNom" data-orderable="false" width="10%"><spring:message code="expedient.list.user.columna.agafatper"/></th>
				</c:if>
				<c:if test="${usuariActual.expedientListInteressats}">
 					<th data-col-name="interessatsResum" data-orderable="false" width="10%"><spring:message code="expedient.list.user.columna.interessats"/></th>
 				</c:if>
				<c:if test="${usuariActual.expedientListGrup}">
 					<th data-col-name="grupNom" data-orderable="false" width="10%"><spring:message code="expedient.list.user.columna.grup"/></th>
 				</c:if> 				
				<c:if test="${usuariActual.expedientListComentaris}">
					<th data-col-name="numComentaris" data-orderable="false" data-template="#cellPermisosTemplate" width="1%">
						<script id="cellPermisosTemplate" type="text/x-jsrender">
							<a href="expedient/{{:id}}/comentaris" data-toggle="modal" data-refresh-tancar="true" data-modal-id="comentaris{{:id}}" class="btn btn-default {{if !usuariActualWrite}} disabled {{/if}}"><span class="fa fa-lg fa-comments"></span>&nbsp;<span class="badge">{{:numComentaris}}</span></a>
				
					</script>
					</th>	
				</c:if>
				<th data-col-name="numSeguidors" data-orderable="false" data-template="#cellSeguidorsTemplate" width="1%">
					<script id="cellSeguidorsTemplate" type="text/x-jsrender">
						{{if numSeguidors > 0}}
							<a href="expedient/{{:id}}/seguidors" data-toggle="modal" data-refresh-tancar="true" data-modal-id="seguidors{{:id}}" class="btn btn-default" title="&nbsp;<spring:message code="comu.boto.followers"/>&nbsp;"><span class="fa fa-lg fa-users"></span>&nbsp;<span class="badge">{{:numSeguidors}}</span></a>
						{{else}}
							<a href="expedient/{{:id}}/seguidors" data-toggle="modal" data-refresh-tancar="true" data-modal-id="seguidors{{:id}}" class="btn btn-default disabled"><span class="fa fa-lg fa-users"></span>&nbsp;<span class="badge">{{:numSeguidors}}</span></a>
						{{/if}}							
					</script>
				</th>			
				<th data-col-name="id" data-template="#cellAccionsTemplate" data-orderable="false" width="1%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<%---- Gestionar ----%>
								<li><a id="agafar_lnk" href="contingut/{{:id}}"><span class="fa fa-folder-open-o"></span>&nbsp;&nbsp;<spring:message code="comu.boto.gestionar"/></a></li>

								<%---- Seguir -----%>
								{{if usuariActualWrite && seguidor}}
									<li><a href="expedient/{{:id}}/unfollow" data-toggle="ajax"><span class="fa fa-user-times"></span>&nbsp;&nbsp;<spring:message code="comu.boto.unfollow"/></a></li>
								{{else usuariActualWrite && !seguidor}}					
									<li><a href="expedient/{{:id}}/follow" data-toggle="ajax"><span class="fa fa-user-plus"></span>&nbsp;&nbsp;<spring:message code="comu.boto.follow"/></a></li>		
								{{/if}}
								<%-----------------%>
								<li role="separator" class="divider"></li>

								<%---- Guardar en arxiu ----%>
								{{if arxiuUuid == null}}
									<li><a href="<c:url value="/expedient/{{:id}}/guardarExpedientArxiu?origin=expedientList"/>"><span class="fa fa-refresh"></span>&nbsp;<spring:message code="comu.boto.guardarArxiu"/></a></li>
								{{/if}}


								<%---- Assignar ----%>
								{{if rolActualAdminEntitatOAdminOrgan}}
									<li><a href="<c:url value="/expedient/{{:id}}/assignar"/>" data-toggle="modal" data-refresh-pagina="true"><span class="fa fa-user"></span>&nbsp;<spring:message code="comu.boto.assignar"/></a></li>
								{{/if}}

								<%---- Modificar... ----%>
								{{if potModificar}}
								 	<li><a href="<c:url value="/expedient/{{:id}}"/>" data-toggle="modal" data-refresh-pagina="true"><span class="fa fa-pencil"></span>&nbsp;<spring:message code="comu.boto.modificar"/>...</a></li>
								{{/if}}

								<%-----------------%>
								{{if arxiuUuid == null || rolActualAdminEntitatOAdminOrgan || potModificar}}
									<li role="separator" class="divider"></li>
								{{/if}}

								<%---- Agafar/Alliberar... ----%>
								{{if rolActualPermisPerModificarExpedient}}
									{{if !agafat}}
										<li><a  href="expedient/{{:id}}/agafar" data-toggle="ajax"><span class="fa fa-lock"></span>&nbsp;&nbsp;<spring:message code="comu.boto.agafar"/></a></li>
									{{else}}
										{{if !expedientAgafatPerUsuariActual}}
											<li><a href="expedient/{{:id}}/agafar" data-confirm="<spring:message code="expedient.list.user.agafar.confirm.1"/> {{:nomPropietariEscriptoriPare}}. <spring:message code="expedient.list.user.agafar.confirm.2"/>" data-toggle="ajax"><span class="fa fa-lock"></span>&nbsp;&nbsp;<spring:message code="comu.boto.agafar"/></a></li>
										{{else}}
											<li><a href="expedient/{{:id}}/alliberar" data-toggle="ajax"><span class="fa fa-unlock"></span>&nbsp;&nbsp;<spring:message code="comu.boto.alliberar"/></a></li>
										{{/if}}
									{{/if}}
								{{/if}}	



								{{if potModificar}}
									<%---- Canviar estat... ----%>
								 	{{if estat == 'OBERT'}}
										<li><a href="<c:url value="/expedient/{{:id}}/canviarEstat"/>" data-toggle="modal" data-refresh-pagina="true"><span class="fa fa-sign-out"></span>&nbsp;<spring:message code="comu.boto.canviarEstat"/>...</a></li>
									{{/if}}
									<%---- Relacionar... ----%>
									<li><a href="<c:url value="/expedient/{{:id}}/relacionarList"/>" data-toggle="modal" data-refresh-pagina="true" data-maximized="true"><span class="fa fa-link"></span>&nbsp;<spring:message code="comu.boto.relacionar"/>...</a></li>
								 	<%---- Tancar... ----%>
									{{if estat == 'OBERT'}}
										{{if potTancar}}
											<li><a href="<c:url value="/expedient/{{:id}}/tancar"/>" data-toggle="modal" data-refresh-pagina="true"><span class="fa fa-check"></span>&nbsp;<spring:message code="comu.boto.tancar"/>...</a></li>
										{{else}}
											<li class="disabled"><a href="#"/><span class="fa fa-check"></span>&nbsp;<spring:message code="comu.boto.tancar"/>...</a></li>
										{{/if}}
									{{/if}}
								{{/if}}
								<%---- Esborrar ----%>
								{{if usuariActualDelete && estat != 'TANCAT' && !conteDocumentsDefinitius}}
									<li><a href="contingut/{{:id}}/delete" data-confirm="<spring:message code="contingut.confirmacio.esborrar.node"/>"><span class="fa fa-trash-o"></span>&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
								{{/if}}

								<%-----------------%>
								<li role="separator" class="divider"></li>

								<%---- Hist�ric d'accions ----%>
								<li><a href="<c:url value="/contingut/{{:id}}/log"/>" data-toggle="modal"><span class="fa fa-list"></span>&nbsp;<spring:message code="comu.boto.historial"/></a></li>

								<%---- Descarregar fitxer comprimit ----%>
								{{if conteDocuments}}
									<li><a href="<c:url value="/contingut/{{:id}}/descarregarAllDocumentsOfExpedient?tascaId=${tascaId}"/>" ><span class="fa fa-download"></span>&nbsp;<spring:message code="expedient.boto.descarregar.fitxer.comprimit"/></a></li>
								{{/if}}

								{{if conteDocuments}}
									<%---- Exportar índex PDF... ----%>
									<li><a class="fileDownload" href="<c:url value="/expedient/{{:id}}/generarIndex/PDF"/>"><span class="fa fa-list-ol"></span>&nbsp;<spring:message code="expedient.list.user.recuperar.index.pdf"/>...</a></li>
									<c:if test="${isExportacioExcelActiva}">
										<li><a class="fileDownload" href="<c:url value="/expedient/{{:id}}/generarIndex/XLSX"/>"><span class="fa fa-th-list"></span>&nbsp;<spring:message code="expedient.list.user.recuperar.index.xlsx"/>...</a></li>
									</c:if>
									<%---- Índex PDF i exportació ENI... ----%>
									{{if conteDocumentsDefinitius}}
										<li><a class="fileDownload" href="<c:url value="/expedient/{{:id}}/generarExportarIndex"/>"><span class="fa fa-list-ol"></span>&nbsp;<span class="fa fa-file-code-o"></span>&nbsp;<spring:message code="expedient.list.user.recuperar.exportar.index"/>...</a></li>
										<li><a class="fileDownload" href="<c:url value="/expedient/{{:id}}/exportarEni"/>"><span class="fa fa-file-code-o"></span>&nbsp;<spring:message code="expedient.list.user.recuperar.exportacio.eni"/>...</a></li>
										<c:if test="${isExportacioInsideActiva}">
											<li><a class="fileDownload" href="<c:url value="/expedient/{{:id}}/exportarEni?ambDocuments=true"/>"><span class="fa fa-file-archive-o"></span>&nbsp;<spring:message code="expedient.list.user.recuperar.exportacio.eni.inside"/>...</a></li>
										</c:if>

									{{else}}
										<li class="disabled"><a href="#"><span class="fa fa-list-ol"></span>&nbsp;<span class="fa fa-file-code-o"></span>&nbsp;<spring:message code="expedient.list.user.recuperar.exportar.index"/>...</a></li>
										<c:if test="${isExportacioInsideActiva}">
											<li class="disabled"><a href="#"><span class="fa fa-file-archive-o"></span>&nbsp;<spring:message code="expedient.list.user.recuperar.exportacio.eni.inside"/>...</a></li>
										</c:if>
									{{/if}}
								{{else}}
									<li class="disabled"><a href="#"/><span class="fa fa-list-ol"></span>&nbsp;<spring:message code="comu.boto.index"/>...</a></li>
								{{/if}}

								{{if arxiuUuid != null}}
									<li><a href="<c:url value="/contingut/{{:id}}/arxiu"/>" data-toggle="modal"><span class="fa fa-info-circle"></span>&nbsp;<spring:message code="comu.boto.arxiu"/></a></li>
								{{else}}
									<li class="disabled"><a href="#"><span class="fa fa-info-circle"></span>&nbsp;<spring:message code="comu.boto.arxiu"/></a></li>
								{{/if}}
								<li><a href="<c:url value="/contingut/{{:id}}/sincronitzarAmbArxiu?llistat=true"/>"><span class="fa fa-refresh"></span>&nbsp;<spring:message code="expedient.sincronitzar.estat.arxiu"/></a></li>

							</ul>
						</div>
					</script>
				</th>
				<th data-col-name="rolActualAdminEntitatOAdminOrgan" data-visible="false"></th>
				<th data-col-name="potModificar" data-visible="false"></th>
				<th data-col-name="rolActualPermisPerModificarExpedient" data-visible="false"></th>
				<th data-col-name="expedientAgafatPerUsuariActual" data-visible="false"></th>
				<th data-col-name="potTancar" data-visible="false"></th>
				<th data-col-name="conteDocuments" data-visible="false"></th>	
				
			</tr>
		</thead>
	</table>

</body>