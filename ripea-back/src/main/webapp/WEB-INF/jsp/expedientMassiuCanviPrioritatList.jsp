<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<rip:blocIconaContingutNoms/>
<html>
<head>
    <title><spring:message code="accio.massiva.titol.prioritats"/></title>
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
        table.dataTable tbody > tr.selected, table.dataTable tbody > tr > .selected {
            background-color: #fcf8e3;
            color: #666666;
        }
        table.dataTable thead > tr.selectable > :first-child, table.dataTable tbody > tr.selectable > :first-child {
            cursor: pointer;
        }
    </style>
    
    <script type="text/javascript">

		var colorsEstats = {};
		<c:forEach items="${expedientEstatsOptions}" var="estat">
		colorsEstats['${estat.id}'] = '${estat.color}';
		</c:forEach>
    
        $(document).ready(function() {

        	$('#metaExpedientId').on('change', function() {
        		metaExpedientId = $(this).val();

        		if (metaExpedientId) {
        			$("#expedientId").data('urlParamAddicional', metaExpedientId);
        		} else {
        			$("#expedientId").data('urlParamAddicional', null);
        			metaExpedientId = 0;
        		}
        		
        		$.get("<c:url value="/expedient/estatValues/"/>" + metaExpedientId)
        		.done(function(data) {
        			
        			$('#expedientEstatId').select2('val', '', true);
        			$('#expedientEstatId option[value!=""]').remove();

        			let listSize = data.length > 1 ? data.length - 1 : data.length; // don't add last estat 'TANCAT'
        			colorsEstats = {}
        			for (var i = 0; i < listSize; i++) {
        				$('#expedientEstatId').append('<option value="' + data[i].id + '">' + data[i].nom + '</option>');
        				colorsEstats[data[i].id] = data[i].color;
        			}
        		})
        		.fail(function() {
        			alert("<spring:message code="error.jquery.ajax"/>");
        		});
        	});
        	
            $('#taulaDades').on('selectionchange.dataTable', function (e, accio, ids) {
                $.get(
                    "canviPrioritats/" + accio,
                    {ids: ids},
                    function(data) {
                        $("#seleccioCount").html(data);
                    }
                );
            });

            $('#taulaDades').on('draw.dt', function () {
                $('#seleccioAll').on('click', function() {
                    $.get(
                        "canviPrioritats/select",
                        function(data) {
                            $("#seleccioCount").html(data);
                            $('#taulaDades').webutilDatatable('refresh');
                        }
                    );
                    return false;
                });
                $('#seleccioNone').on('click', function() {
                    $.get(
                        "canviPrioritats/deselect",
                        function(data) {
                            $("#seleccioCount").html(data);
                            $('#taulaDades').webutilDatatable('select-none');
                            $('#taulaDades').webutilDatatable('refresh');
                        }
                    );
                    return false;
                });

                $("span[class^='stateColor-']").each(function( index ) {
                    var fullClassNameString = this.className;
                    var colorString = fullClassNameString.substring(11);
                    if (colorString == "" || colorString == 'OBERT' || colorString == 'ESTAT') {
                        $(this).parent().css( "border", "dashed 1px #AAA" );
                        $(this).parent().css( "color", '#666666' );
                    } else if (colorString == 'TANCAT') {
                        $(this).parent().css( "background-color", "#777" );
                    } else {
                        $(this).parent().css( "background-color", colorString );
                        const textColor = getTextColorOnBackground('#666666', '#ffffff', colorString);
                        $(this).parent().css( "color", textColor );
                        // $(this).parent().css( "fontSize", 'small' );
                        $(this).parent().parent().parent().css( "box-shadow", "-6px 0 0 " + colorString );
                    }
                });
            });

        });

      	function showColor(element) {
      		const id = element.id;
      		const color = colorsEstats[id];
      		if (!color) {
      			return $('<span class="no-icon"></span><span>' + element.text + '</span>');
      		}
      		return $('<span class="color-icon" style="background-color: ' + color + '"></span><span>' + element.text + '</span>');
      	}
                
    </script>

</head>
<body>
<form:form action="" method="post" cssClass="well" modelAttribute="contingutMassiuFiltreCommand">
    <div class="row">
        <div class="col-md-4">
            <rip:inputSelect
            	name="metaExpedientId"
            	optionItems="${metaExpedients}"
            	optionValueAttribute="id"
            	optionTextAttribute="codiSiaINom"
            	optionMinimumResultsForSearch="3"
            	emptyOption="true"
            	placeholderKey="accio.massiva.list.filtre.tipusexpedient"
            	inline="true"/>
        </div>
        <div class="col-md-4">
            <rip:inputText name="nom" inline="true" placeholderKey="accio.massiva.list.filtre.nom"/>
        </div>
        <div class="col-md-2">
            <rip:inputDate name="dataInici" inline="true" placeholderKey="accio.massiva.list.filtre.dataCreacioDesde"/>
        </div>
        <div class="col-md-2">
            <rip:inputDate name="dataFi" inline="true" placeholderKey="accio.massiva.list.filtre.dataCreacioFins"/>
        </div>
    </div>
    <div class="row">

		<div class="col-md-4">
			<rip:inputSelect 
				name="expedientEstatId" 
				optionItems="${expedientEstatsOptions}"
				optionValueAttribute="id"
				emptyOption="true" 
				optionTextAttribute="nom"
				placeholderKey="expedient.list.user.placeholder.estat"
				templateResultFunction="showColor"
				inline="true" />
		</div>

		<div class="col-md-4">
			<rip:inputSelect 
				name="prioritat" 
				optionItems="${prioritatsExpedient}"
				emptyOption="true"
				optionValueAttribute="value"
				optionTextKeyAttribute="text"
				placeholderKey="contingut.expedient.form.camp.prioritat"
				templateResultFunction="showColorPriritats"
				inline="true" />
		</div>

        <div class="col-md-4 pull-right">
            <div class="pull-right">
                <button style="display:none" type="submit" name="accio" value="filtrar" ><span class="fa fa-filter"></span></button>
                <button type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.boto.netejar"/></button>
                <button type="submit" name="accio" value="filtrar" class="btn btn-primary"><span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/></button>
            </div>
        </div>
    </div>

</form:form>

<script id="botonsTemplate" type="text/x-jsrender">
    <div class="btn-group pull-right">
        <button type="button" id="seleccioAll" title="<spring:message code="expedient.list.user.seleccio.tots"/>" class="btn btn-default"><span class="fa fa-check-square-o"></span></a>
			<button type="button" id="seleccioNone" title="<spring:message code="expedient.list.user.seleccio.cap"/>" class="btn btn-default"><span class="fa fa-square-o"></span></a>
			<button type="button" class="btn btn-default" href="./canviPrioritats/canviar" data-toggle="modal" data-refresh-pagina="true">
				<span id="seleccioCount" class="badge">${fn:length(seleccio)}</span> <spring:message code="comu.boto.actualitzar"/>
    </button>
</div>
</script>

<table id="taulaDades"
       data-toggle="datatable"
       data-url="<c:url value="/massiu/canviPrioritats/datatable"/>"
       class="table table-bordered table-striped"
       data-default-order="7"
       data-default-dir="desc"
       data-botons-template="#botonsTemplate"
       data-selection-enabled="true"
       style="width:100%">
    <thead>
    <tr>
        <th data-col-name="id" data-visible="false"></th>
        <th data-col-name="expedient" data-visible="false"></th>
        <th data-col-name="expedientEstat" data-visible="false"></th>
        <th data-col-name="metaExpedient.nom" data-orderable="true" width="15%"><spring:message code="accio.massiva.list.column.metaexpedient"/></th>
        <th data-col-name="nom" data-ordenable="true"><spring:message code="accio.massiva.list.column.nom"/></th>
        <th data-col-name="estat" data-template="#cellEstatTemplate" width="11%">
            <spring:message code="expedient.list.user.columna.estat"/>
            <script id="cellEstatTemplate" type="text/x-jsrender">
                <span class="label">
                {{if expedientEstat != null && estat != 'TANCAT'}}
                    <span class="fa fa-folder-open"></span>&nbsp;{{:expedientEstat.nom}}
                {{else}}
                    {{if estat == 'OBERT'}}
                        <span class="fa fa-folder-open"></span>&nbsp;<spring:message code="expedient.estat.enum.OBERT"/>
                {{else}}
                    <span class="fa fa-folder"></span>&nbsp;<spring:message code="expedient.estat.enum.TANCAT"/>
                {{/if}}
            {{/if}}

            {{if expedientEstat != null && expedientEstat.color!=null}}
                <span class="stateColor-{{:expedientEstat.color}}"></span>
            {{else expedientEstat != null && estat != 'TANCAT'}}
                <span class="stateColor-ESTAT"></span>
            {{else expedientEstat == null && estat == 'OBERT'}}
                <span class="stateColor-OBERT"></span>
            {{else}}
                <span class="stateColor-TANCAT"></span>
            {{/if}}
            </span>
            </script>
        </th>
        <th data-col-name="prioritat" data-template="#cellPrioritatTemplate" width="8%">
            <spring:message code="contingut.expedient.form.camp.prioritat"/>
            <script id="cellPrioritatTemplate" type="text/x-jsrender">
                <span class="label label-{{:prioritat}}">
                {{if prioritat == 'MOLT_BAIXA'}}
                <spring:message code="prioritat.enum.MOLT_BAIXA"/>
                {{else prioritat == 'A_BAIXA'}}
                <spring:message code="prioritat.enum.A_BAIXA"/>
                {{else prioritat == 'C_ALTA'}}
                <spring:message code="prioritat.enum.C_ALTA"/>
                {{else prioritat == 'D_MOLT_ALTA'}}
                <spring:message code="prioritat.enum.D_MOLT_ALTA"/>
                {{else prioritat == 'CRITICA'}}
                <spring:message code="prioritat.enum.CRITICA"/>
                {{else}}
                <spring:message code="prioritat.enum.B_NORMAL"/>
                {{/if}}
                </span>
            </script>
        </th>
        <th data-col-name="createdDate" data-ordenable="true" data-converter="datetime" width="15%"><spring:message code="accio.massiva.list.column.datacreacio"/></th>
        <th data-col-name="createdBy.codiAndNom" data-ordenable="true" width="15%"><spring:message code="accio.massiva.list.column.creatper"/></th>
    </tr>
    </thead>
</table>

</body>
</html>