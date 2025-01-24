<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<html>
<head>
    <title><spring:message code="user.tasca.list.titol"/></title>
    <script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
    <script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
    <link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>"
          rel="stylesheet"></link>
    <link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
    <link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>"
          rel="stylesheet"/>
    <script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
    <script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
    <link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>"
          rel="stylesheet"/>
    <script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
    <script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>
    <script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
    <script src="<c:url value="/js/webutil.common.js"/>"></script>
    <script src="<c:url value="/js/webutil.datatable.js"/>"></script>
    <script src="<c:url value="/js/webutil.modal.js"/>"></script>
    <script type="text/javascript">
    </script>

    <style type="text/css">
        li[class*="disabled"] {
            pointer-events: none;
        }

        .dataTables_length {
            display: flex;
        }

        .llegenda_paginador {
            display: flex;
            align-items: center;
            position: relative;
            left: 20px;
        }

        .item_llegenda {
            margin: 0 10px 0 10px;
            display: flex;
            align-items: center;
        }

        .item_llegenda span:nth-child(2) {
            margin-left: 4px;
        }

        .item_color {
            width: 10px;
            height: 10px;
        }

        .llegenda_coneixement .item_color {
            background-color: #75ce73;
        }

        .lleganda_delegacio .item_color {
            background-color: #36cfe8;
        }
    </style>
    <script type="text/javascript">
        var updateExpdientesList = true;
        $(document).ready(function () {
            $('#taulaDades').on('draw.dt', function (row, data) {

                $("span[class='usuariActualObservador']").each(function (index) {
                    var tr = $(this).parent().parent();
                    tr.attr("title", "<spring:message code="tasca.info.llegenda.observador"/>");
                    tr.css("box-shadow", "#75ce73 -4px 0px 0px");
                    tr.children().not(':nth-child(11)').css("opacity", "0.6");
                    tr.children().not(':nth-child(11)').css("cursor", "default");
                    tr.children().not(':nth-child(11)').css("pointer-events", "none");

                });

                $("span[class='usuariActualDelegat']").each(function (index) {
                    $(this).parent().parent().css("box-shadow", "#36cfe8 -4px 0px 0px");
                });


                //Llegenda paginador
                var $paginador = $('.dataTables_length');
                if ($paginador.find('.llegenda_paginador').length == 0) {
                    var $paginador_container = $paginador.closest('.row');
                    $paginador_container.find('div:first').addClass('col-md-6').removeClass('col-md-3');
                    $paginador_container.find('div:nth-child(2)').addClass('col-md-6').removeClass('col-md-9');
                    var llegenda = '<div class="llegenda_paginador">\
										<div class="item_llegenda">\
											<span><spring:message code="tasca.info.llegenda"/>:</span>\
										</div>\
										<div class="item_llegenda llegenda_coneixement">\
											<span class="item_color"></span>\
											<span><spring:message code="tasca.info.llegenda.observador"/></span>\
										</div>\
										<div class="item_llegenda lleganda_delegacio">\
											<span class="item_color"></span>\
											<span><spring:message code="tasca.info.llegenda.delegat"/></span>\
										</div>\
									</div>';
                    $paginador.append(llegenda);
                }
            });

            $('#metaExpedientId').on('change', function () {
                var metaExpedientId = $(this).val();

                if (updateExpdientesList) {
                    if (metaExpedientId) {
                        $("#expedientId").data('urlParamAddicional', metaExpedientId);
                    } else {
                        $("#expedientId").data('urlParamAddicional', null);
                    }

                    changeExpedientPlaceHolder();

                    $('#expedientId option[value!=""]').remove();
                    $('#expedientId').select2('val', '', true);
                } else {
                    updateExpdientesList = true;
                }

                $('#metaExpedientTascaId option[value!=""]').remove();
                $('#metaExpedientTascaId').select2('val', '', true);

                if (metaExpedientId != null && metaExpedientId != "") {
                    $.get("<c:url value="/usuariTasca/metaExpedientTasca/"/>" + metaExpedientId).done(function (data) {
                        for (var i = 0; i < data.length; i++) {
                            $('#metaExpedientTascaId').append('<option value="' + data[i].id + '">' + data[i].nom + '</option>');
                        }
                    }).fail(function () {
                        alert("<spring:message code="error.jquery.ajax"/>");
                    });
                }

            });

            $('#expedientId').on('change', function () {
                var expedientId = $(this).val();
                var metaExpedientId = $('#metaExpedientId').val();

                if (expedientId != null && !metaExpedientId) {
                    $.get("<c:url value="/usuariTasca/metaExpedient/"/>" + expedientId).done(function (data) {
                        updateExpdientesList = false;
                        $('#metaExpedientId').val(data.id).change();
                    }).fail(function () {
                        alert("<spring:message code="error.jquery.ajax"/>");
                    });
                }
            });

            changeExpedientPlaceHolder();
        });

        function changeExpedientPlaceHolder() {
            var procSelector = document.getElementById("metaExpedientId");
            var tipus = $("#metaExpedientId").val();
            if (tipus) {
                $('#select2-expedientId-container .select2-selection__placeholder').html('<spring:message code="accio.massiva.list.filtre.expsDelProc"/> ' + procSelector.options[procSelector.selectedIndex].text);
            } else {
                $('#select2-expedientId-container .select2-selection__placeholder').html('<spring:message code="contingut.admin.filtre.expedient"/>');
            }
        }
    </script>
</head>
<body>

<c:url value="usuariTasca/filtrar" var="formAction"/>
<form:form id="filtreForm" action="${ formAction }" method="post" cssClass="well defaultFilterEnter" commandName="usuariTascaFiltreCommand">
    <div class="row">
        <div class="col-md-3">
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
        <div class="col-md-3">
            <c:url value="/expedientajax/expedient" var="urlConsultaExpInicial"/>
            <c:url value="/expedientajax/expedient" var="urlConsultaExpLlistat"/>
            <rip:inputSuggest
                    name="expedientId"
                    urlConsultaInicial="${urlConsultaExpInicial}"
                    urlConsultaLlistat="${urlConsultaExpLlistat}"
                    usePathVariable="false"
                    placeholderKey="contingut.admin.filtre.expedient"
                    suggestValue="id"
                    suggestText="nomINumero"
                    inline="true"
                    urlParamAddicional="${contingutMassiuFiltreCommand.metaExpedientId}"/>
        </div>
        <div class="col-md-2">
            <rip:inputSelect
                    name="metaExpedientTascaId"
                    placeholderKey="expedient.tasca.form.camp.metaExpedientTasca"
                    optionItems="${metaexpTasques}"
                    optionValueAttribute="id"
                    optionTextAttribute="nom"
                    emptyOption="true"
                    inline="true"/>
        </div>
        <div class="col-md-2">
            <rip:inputText name="titol" inline="true" placeholderKey="expedient.list.user.placeholder.titol"/>
        </div>
        <div class="col-md-2">
            <rip:inputSelect name="prioritat" optionEnum="PrioritatEnumDto" emptyOption="true"
                             placeholderKey="accio.massiva.portafirmes.prioritat" inline="true"/>
        </div>
    </div>
    <div class="row">
        <div class="col-md-2">
            <rip:inputDate name="dataInici" inline="true" placeholderKey="accio.massiva.list.filtre.datainici"/>
        </div>
        <div class="col-md-2">
            <rip:inputDate name="dataFi" inline="true" placeholderKey="accio.massiva.list.filtre.datafi"/>
        </div>
        <div class="col-md-2">
            <rip:inputDate name="dataLimitInici" inline="true"
                           placeholderKey="accio.massiva.list.filtre.dataLimitInici"/>
        </div>
        <div class="col-md-2">
            <rip:inputDate name="dataLimitFi" inline="true" placeholderKey="accio.massiva.list.filtre.dataLimitFi"/>
        </div>
        <div class="col-md-4">
            <rip:inputSelect name="estats" optionEnum="TascaEstatEnumDto" emptyOption="true"
                             placeholderKey="expedient.tasca.list.columna.estat" multiple="true" inline="true"/>
        </div>
    </div>
    <div class="row">
        <div class="col-md-4 pull-right">
            <div class="pull-right">
                <button type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message
                        code="comu.boto.netejar"/></button>
                <button type="submit" name="accio" value="filtrar" class="btn btn-primary default"><span
                        class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/></button>
            </div>
        </div>
    </div>
</form:form>


<script id="rowhrefTemplate" type="text/x-jsrender">contingut/{{:expedient.id}}?tascaId={{:id}}</script>
<table
        id="taulaDades"
        data-toggle="datatable"
        data-url="<c:url value="/usuariTasca/datatable"/>"
        data-save-state="true"
        data-default-order="8"
        data-default-dir="desc"
        class="table table-striped table-bordered"
        data-rowhref-template="#rowhrefTemplate"
        style="width:100%">
    <thead>
    <tr>
        <th data-col-name="expedient.id" data-visible="false"></th>
        <th data-col-name="expedient.nomINumero" data-orderable="false" width="10%"><spring:message
                code="expedient.tasca.list.columna.expedient"/></th>
        <th data-col-name="metaExpedientTasca.nom" data-orderable="false" width="10%"><spring:message
                code="expedient.tasca.list.columna.metaExpedientTasca"/></th>
        <th data-col-name="metaExpedientTasca.descripcio" data-orderable="false" data-template="#cellTascaDescripcio">
            <spring:message code="expedient.tasca.list.columna.metaExpedientTascaDescripcio"/>
            <script id="cellTascaDescripcio" type="text/x-jsrender">
                <span title="{{:metaExpedientTasca.descripcio}}">{{:metaExpedientTascaDescAbrv}}</span>
            </script>
        </th>
        <th data-col-name="titol" width="10%"><spring:message code="expedient.tasca.list.columna.titol"/></th>
        <th data-col-name="observacions" width="15%"><spring:message
                code="expedient.tasca.list.columna.observacions"/></th>
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
        <th data-col-name="metaExpedientTascaDescAbrv" data-orderable="false" data-visible="false"></th>
        <th data-col-name="dataInici" data-converter="datetime" width="8%"><spring:message
                code="expedient.tasca.list.columna.dataInici"/></th>
        <th data-col-name="shouldNotifyAboutDeadline" data-visible="false"></th>
        <th data-col-name="dataLimitExpirada" data-visible="false"></th>
        <th data-col-name="duracioFormat" data-visible="false"></th>
        <th data-col-name="agafada" data-visible="false"></th>
        <th data-col-name="usuariActualObservador" data-visible="false"></th>
        <th data-col-name="delegada" data-visible="false"></th>
        <th data-col-name="usuariActualDelegat" data-visible="false"></th>
        <th data-col-name="responsableActual.codi" data-orderable="false" width="12%"><spring:message
                code="expedient.tasca.list.columna.responsable.actual"/></th>
        <th data-col-name="dataLimitString" width="8%" data-orderable="false"
            data-template="#cellTascaDeadlineTemplate">
            <spring:message code="expedient.tasca.list.columna.dataLimit"/>
            <script id="cellTascaDeadlineTemplate" type="text/x-jsrender">
                {{if dataLimitExpirada}}
                        <span style="color: red;" title="Duració estimada {{:duracioFormat}}">
                            {{:dataLimitString}}
                            <span class="fa fa-clock-o"></span>
                        </span>
                {{else}}
                    {{if shouldNotifyAboutDeadline}}
                        <span style="color: orange;" title="Duració estimada {{:duracioFormat}}">
                            {{:dataLimitString}}
                            <span class="fa fa-clock-o"></span>
                        </span>
                    {{else}}
                        <span title="Duració estimada {{:duracioFormat}}">
                            {{:dataLimitString}}
                        </span>
                    {{/if}}
                {{/if}}
            </script>
        </th>
        <th data-col-name="estat" data-template="#cellTascaEstatTemplate" data-orderable="false" width="8%">
            <spring:message code="expedient.tasca.list.columna.estat"/>
            <script id="cellTascaEstatTemplate" type="text/x-jsrender">
                {{if estat == 'PENDENT'}}
                <spring:message code="expedient.tasca.estat.enum.PENDENT"/>
                {{else estat == 'INICIADA'}}
                <spring:message code="expedient.tasca.estat.enum.INICIADA"/>
                {{else estat == 'FINALITZADA'}}
                <spring:message code="expedient.tasca.estat.enum.FINALITZADA"/>
                {{else estat == 'CANCELLADA'}}
                <spring:message code="expedient.tasca.estat.enum.CANCELLADA"/>
                {{else estat == 'REBUTJADA'}}
                <spring:message code="expedient.tasca.estat.enum.REBUTJADA"/>
                {{/if}}
            </script>
        </th>
        <th data-col-name="numComentaris" data-orderable="false" data-template="#cellComentarisTemplate" width="1%">
            <script id="cellComentarisTemplate" type="text/x-jsrender">
                <a href="expedientTasca/{{:id}}/comentaris" data-toggle="modal" data-refresh-tancar="true" data-modal-id="comentaris{{:id}}" class="btn btn-default"><span class="fa fa-lg fa-comments"></span>&nbsp;<span class="badge">{{:numComentaris}}</span></a>
            </script>
        </th>
        <th data-col-name="id" data-orderable="false" data-template="#cellAnotacioAccionsTemplate" width="1%">
            <script id="cellAnotacioAccionsTemplate" type="text/x-jsrender">
                {{if delegada && usuariActualDelegat}}
                    <span class="usuariActualDelegat"></span>
                {{/if}}
                {{if usuariActualObservador}}
                    <span class="usuariActualObservador"></span>
                    <div class="dropdown">
                        <button class="btn btn-primary" data-toggle="dropdown" disabled="disabled"><span class="fa fa-cog"></span>&nbsp;<spring:message
                    code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							</div>
						{{else}}
							<div class="dropdown">
								<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message
                    code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
								<ul class="dropdown-menu">
									<li><a href="<c:url value="/expedientTasca/{{:id}}/detall"/>" data-maximized="false" data-toggle="modal"><span class="fa fa-info"></span>&nbsp;&nbsp;<spring:message
                    code="comu.boto.detalls"/></a></li>
									{{if estat != 'CANCELLADA' && estat != 'REBUTJADA'}}
										<li class="divider"></li>
									{{/if}}
									{{if estat != 'CANCELLADA' && estat != 'FINALITZADA' && estat != 'REBUTJADA'}}
										<li {{if agafada && usuariActualResponsable}}class="disabled"{{/if}}><a href="<c:url
                    value="/contingut/{{:expedient.id}}?tascaId={{:id}}&origenTasques=true"/>"><span class="fa fa-folder-open-o"></span>&nbsp;&nbsp;<spring:message
                    code="comu.boto.tramitar"/></a></li>
									{{/if}}
									{{if estat == 'PENDENT'}}
										<li {{if agafada && usuariActualResponsable}}class="disabled"{{/if}}><a href="<c:url
                    value="/usuariTasca/{{:id}}/iniciar"/>" data-toggle="ajax"><span class="fa fa-play"></span>&nbsp;&nbsp;<spring:message
                    code="comu.boto.iniciar"/></a></li>
										<li {{if agafada && usuariActualResponsable}}class="disabled"{{/if}}><a href="<c:url
                    value="/usuariTasca/{{:id}}/rebutjar"/>" data-maximized="true" data-toggle="modal" data-refresh-pagina="true"><span class="fa fa-reply"></span>&nbsp;&nbsp;<spring:message
                    code="comu.boto.rebutjar"/></a></li>
									 {{/if}}
									{{if estat != 'CANCELLADA' && estat != 'FINALITZADA' && estat != 'REBUTJADA'}}
										<li {{if agafada && usuariActualResponsable}}class="disabled"{{/if}}><a href="<c:url
                    value="/expedientTasca/{{:id}}/cancellar"/>" data-toggle="ajax" data-confirm="<spring:message
                    code="expedient.tasca.confirmacio.cancellar"/>"><span class="fa fa-times"></span>&nbsp;&nbsp;<spring:message
                    code="comu.boto.cancellar"/></a></li>
										<li {{if agafada && usuariActualResponsable}}class="disabled"{{/if}}><a href="<c:url
                    value="/usuariTasca/{{:id}}/finalitzar"/>" data-toggle="ajax" data-confirm="<spring:message
                    code="expedient.tasca.finalitzar"/>"><span class="fa fa-check"></span>&nbsp;&nbsp;<spring:message
                    code="comu.boto.finalitzar"/></a></li>
										<li class="divider"></li>
										<li {{if agafada && usuariActualResponsable}}class="disabled"{{/if}}><a href="<c:url
                    value="/expedientTasca/{{:id}}/reassignar"/>" data-toggle="modal"><span class="fa fa-user"></span>&nbsp;&nbsp;<spring:message
                    code="comu.boto.reassignar"/></a></li>
										{{if !delegada}}
											<li {{if agafada && usuariActualResponsable}}class="disabled"{{/if}}><a href="<c:url
                    value="/usuariTasca/{{:id}}/delegar"/>" data-toggle="modal"><span class="fa fa-share"></span>&nbsp;&nbsp;<spring:message
                    code="comu.boto.delegar"/></a></li>
										{{else delegada && !usuariActualDelegat}}
											<li {{if agafada && usuariActualResponsable}}class="disabled"{{/if}}><a href="<c:url
                    value="/usuariTasca/{{:id}}/retomar"/>" data-toggle="modal"><span class="fa fa-remove"></span>&nbsp;&nbsp;<spring:message
                    code="comu.boto.delegacio.desfer"/></a></li>
										{{/if}}
										<li class="divider"></li>
										<li><a href="<c:url value="/expedientTasca/{{:id}}/datalimit"/>" data-toggle="modal"><span class="fa fa-clock-o"></span>&nbsp;&nbsp;<spring:message
                    code="expedient.tasca.list.boto.dataLimit"/></a></li>
										<li><a href="<c:url value="/expedientTasca/{{:id}}/canviarPrioritat"/>" data-toggle="modal" data-refresh-pagina="true"><span class="fa fa-sign-out"></span>&nbsp;<spring:message
                    code="comu.boto.canviarPrioritat"/>...</a></li>
									{{/if}}
									{{if estat == 'FINALITZADA'}}
										<li><a href="<c:url value="/expedientTasca/{{:id}}/reobrir"/>" data-toggle="modal"><span class="fa fa-undo"></span>&nbsp;&nbsp;<spring:message
                    code="comu.boto.reobrir"/></a></li>
									{{/if}}
								</ul>
							</div>
						{{/if}}
            </script>
        </th>

    </tr>
    </thead>
</table>
</body>