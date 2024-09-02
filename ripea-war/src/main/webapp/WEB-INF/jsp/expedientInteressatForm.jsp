<%@page import="es.caib.ripea.war.helper.EnumHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<%
java.util.List<EnumHelper.HtmlOption> tipusEnum = EnumHelper.getOptionsForEnum(
		es.caib.ripea.core.api.dto.InteressatTipusEnumDto.class, "interessat.tipus.enum.");
Boolean esRepresentant = (Boolean) request.getAttribute("esRepresentant");
if (esRepresentant != null && esRepresentant) {
	tipusEnum.remove(2);
}
pageContext.setAttribute("tipusEnumOptions", tipusEnum);
pageContext.setAttribute("documentTipusEnumOptions", es.caib.ripea.war.helper.EnumHelper.getOptionsForEnum(
		es.caib.ripea.core.api.dto.InteressatDocumentTipusEnumDto.class, "interessat.document.tipus.enum."));
pageContext.setAttribute("idiomaEnumOptions", es.caib.ripea.war.helper.EnumHelper
		.getOptionsForEnum(es.caib.ripea.core.api.dto.InteressatIdiomaEnumDto.class, "interessat.idioma.enum."));
%>

<c:set var="potModificar">${potModificar == null || potModificar == true ? true : false}</c:set>


<c:set var="titol">
	<c:choose>
		<c:when test="${esRepresentant}">
			<c:choose>
				<c:when test="${not empty interessatCommand.id}">
					<c:choose>
						<c:when test="${potModificar}">
							<spring:message code="representant.form.edit.titol" />
						</c:when>
						<c:otherwise>
							<spring:message code="representant.form.detalls.titol" />
						</c:otherwise>
					</c:choose>
				</c:when>
				<c:otherwise>
					<spring:message code="representant.form.titol" />
				</c:otherwise>
			</c:choose>
		</c:when>
		<c:otherwise>
			<c:choose>
				<c:when test="${not empty interessatCommand.id}">
					<c:choose>
						<c:when test="${potModificar}">
							<spring:message code="interessat.form.edit.titol" />
						</c:when>
						<c:otherwise>
							<spring:message code="interessat.form.detalls.titol" />
						</c:otherwise>
					</c:choose>
				</c:when>
				<c:otherwise>
					<spring:message code="interessat.form.titol" />
				</c:otherwise>
			</c:choose>
		</c:otherwise>
	</c:choose>
</c:set>


<html>
<head>
<title>${titol}</title>
<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet" />
<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet" />
<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
<script src="<c:url value="/js/webutil.common.js"/>"></script>
<rip:modalHead />
<style>
	.tab-pane {	margin-top: 1em; }
	body { font-size: 13px; }
	.control-label { padding-right: 5px !important; }
	.organ-btn { position: absolute; right: 0px; }
	#filtre-btn { float: right; }
	.rmodal { display: none; position: fixed; z-index: 1000; top: 0; left: 0; height: 100%; width: 100%; background: rgba(255, 255, 255, .8) url('<c:url value="/img/loading.gif"/>') 50% 50% no-repeat; }
	body.loading { overflow: hidden; }
	body.loading .rmodal { display: block; }
	#organTitol { font-weight: bold; margin-bottom: 10px; padding-bottom: 2px; margin-top: -6px; border-bottom: 1px solid #DDD; }
	#editar-warn { width: 100%; }
</style>

<script type="text/javascript">
	var interessatNoms = [];
	var interessatLlinatges = [];
	<c:forEach var="intFis" items="${interessats}">
	interessatNoms['${intFis.id}'] = "${intFis.nom}";
	<c:if test="${intFis.personaFisica}">
		interessatLlinatges['${intFis.id}'] = "${intFis.llinatges}";
	</c:if>
	</c:forEach>
	const esRepresentant = <c:choose><c:when test="${esRepresentant}">true</c:when><c:otherwise>false</c:otherwise></c:choose>;

	const isPropertyNotEmpty = (obj, prop) => {
		return obj.hasOwnProperty(prop) && obj[prop] !== null && obj[prop] !== undefined;
	}

	$(document).ready(function() {
		var munOrgan = '';
		var edicio = <c:out value="${not empty interessatCommand.id}"/>;
		var idEdicio = '${interessatCommand.id}';
		var netejar = <c:out value="${empty interessatCommand.id and empty netejar}"/>;
		var organsCarregats = <c:out value="${not empty unitatsOrganitzatives}"/>;
		$body = $("body");
		$(document).on({
			ajaxStart: function() { $body.addClass("loading");    },
			ajaxStop: function() { $body.removeClass("loading"); }
		});
		// Posicionam el botó de l'administració
		$('select#organCodi').closest(".controls").css('width', 'calc(83.33333333% - 50px)');
		$('form').submit(function() {
			$('form input').removeAttr('disabled');
			return true;
		});

		$('select#tipus').change(function() {
			munOrgan = '';
			$('#pais').prop("disabled", false);
			$('#provincia').prop("disabled", false);
			$('#municipi').prop("disabled", false);
			$('#codiPostal').prop("readonly", false);
			$('#adresa').prop("readonly", false);
			$('#documentTipus').prop("disabled", false);
			$('#documentNum').prop("readonly", false);
			if (netejar) {
				$('input#documentNum').val('');
				$('input#nom').val('');
				$('input#llinatge1').val('');
				$('input#llinatge2').val('');
				$('input#raoSocial').val('');
				$('select#organCodi').val('');
				$('select#organCodi').change();
				$('input#email').val('');
				$('input#telefon').val('');
				$('textarea#observacions').val('');
				$('#pais').val("724");
				$('#pais').change();
				$('#provincia').val("07");
				$('#provincia').change();
			}
			netejar = true;
			var tipusInt = 1;
			if (this.value == '<%=es.caib.ripea.core.api.dto.InteressatTipusEnumDto.PERSONA_FISICA%>') {
				tipusInt = 1;
			} else if (this.value == '<%=es.caib.ripea.core.api.dto.InteressatTipusEnumDto.PERSONA_JURIDICA%>') {
				tipusInt = 2;
				$('#documentTipus').val("NIF");
				$('#documentTipus').change();
				$('#documentTipus').prop("disabled", true);
			} else {
				tipusInt = 3;
				if (organsCarregats == false) {
					$.ajax({
						type: 'GET',
						url: "<c:url value="/expedient/organs"/>",
						success: function(data) {
							var selOrgan = $('#organCodi');
							selOrgan.empty();
							selOrgan.append("<option value=\"\"></option>");
							if (data && data.length > 0) {
								var items = [];
								$.each(data, function(i, val) {
									items.push({
										"id": val.codi,
										"text": val.denominacio
									});
									selOrgan.append("<option value=\"" + val.codi + "\">" + val.denominacio + "</option>");
								});
							}
							var select2Options = {theme: 'bootstrap', minimumResultsForSearch: "6"};
							selOrgan.select2("destroy");
							selOrgan.select2(select2Options);
						}
					});
					organsCarregats = true
				}
				$('#pais').prop("disabled", true);
				$('#provincia').prop("disabled", true);
				$('#municipi').prop("disabled", true);
				$('#codiPostal').prop("readonly", true);
				$('#adresa').prop("readonly", true);
				$('#documentTipus').val("CODI_ORIGEN");
				$('#documentTipus').change();
				$('#documentTipus').prop("disabled", true);
				$('#documentNum').prop("readonly", true);

			}
			canviVisibilitat(tipusInt);
		});
		$('select#tipus').change();


		$('input#documentNum').on('keydown', function(evt) {
			$(this).val(function (_, val) {
				return val.toUpperCase();
			});
		});

		$('input#documentNum').change((event) => {
			$('#id').val(edicio ? idEdicio : '');
			$('#editar-warn').hide();
			const documentNum = $(event.target).val();
			console.log('Element has changed:', documentNum);

			if (esRepresentant && documentNum.trim() !== "") {
				$.ajax({
					type: 'GET',
					url: "<c:url value="/expedient/${expedientId}/representant/"/>" + documentNum,
					success: function(data) {
						if (data !== null && isPropertyNotEmpty(data, 'id')) {
							$('#id').val(data.id);
							$('#editar-warn').show();
							if (isPropertyNotEmpty(data, 'tipus')) {
								// netejar = false;
								$('select#tipus').val(data.tipus).change();
								if (data.tipus == 'PERSONA_FISICA') {
									if (isPropertyNotEmpty(data, 'nom')) $('#nom').val(data.nom);
									if (isPropertyNotEmpty(data, 'llinatge1')) $('#llinatge1').val(data.llinatge1);
									if (isPropertyNotEmpty(data, 'llinatge2')) $('#llinatge2').val(data.llinatge2);
								} else if (data.tipus == 'PERSONA_JURIDICA') {
									if (isPropertyNotEmpty(data, 'raoSocial')) $('#raoSocial').val(data.raoSocial);
								}
							}
							if (isPropertyNotEmpty(data, 'documentTipus')) $('#documentTipus').val(data.documentTipus).change();
							if (isPropertyNotEmpty(data, 'documentNum')) $('#documentNum').val(data.documentNum);
							if (isPropertyNotEmpty(data, 'pais')) $('#pais').val(data.pais).change();
							munOrgan = data.municipi;
							if (isPropertyNotEmpty(data, 'provincia')) $('#provincia').val(data.provincia).change();
							// if (isPropertyNotEmpty(data, 'municipi')) $('#municipi').val(data.municipi).change();
							if (isPropertyNotEmpty(data, 'adresa')) $('#adresa').val(data.adresa);
							if (isPropertyNotEmpty(data, 'codiPostal')) $('#codiPostal').val(data.codiPostal);
							if (isPropertyNotEmpty(data, 'email')) $('#email').val(data.email);
							if (isPropertyNotEmpty(data, 'telefon')) $('#telefon').val(data.telefon);
							if (isPropertyNotEmpty(data, 'observacions')) $('#observacions').val(data.observacions);
							if (isPropertyNotEmpty(data, 'preferenciaIdioma')) $('#preferenciaIdioma').val(data.preferenciaIdioma);
						}
					}
				});
			}
		});

		$('select#organCodi').change(function() {
			munOrgan = '';
			if ($(this).val() != "") {
				let optionSelected = $("option:selected", this);
				$.ajax({
					type: 'GET',
					url: "<c:url value="/expedient/organ/"/>" + $(this).val(),
					success: function(data) {
						$('#pais').val(data.codiPais);
						$('#pais').change();
						$('#provincia').val(data.codiProvincia);
						$('#provincia').change();
						$('#municipi').val(data.localitat);
						munOrgan = data.localitat;
						$('#municipi').change();
						$('#codiPostal').val(data.codiPostal);
						$('#adresa').val(data.adressa);
						$('#documentNum').val(data.nifCif);
						$('#ambOficinaSir').val(optionSelected.hasClass('ambOficinaSir'));

						$('input#email').val('');
						$('input#telefon').val('');
						$('textarea#observacions').val('');

						if (isPropertyNotEmpty(data, 'nifCif')) {
							$.ajax({
								type: 'GET',
								url: "<c:url value="/expedient/${expedientId}/representant/"/>" + data.nifCif,
								success: function(data) {
									if (data !== null && isPropertyNotEmpty(data, 'id')) {
										$('#id').val(data.id);
										$('#editar-warn').show();
										if (isPropertyNotEmpty(data, 'tipus')) {
											if (isPropertyNotEmpty(data, 'email')) $('#telefon').val(data.email);
											if (isPropertyNotEmpty(data, 'telefon')) $('#telefon').val(data.telefon);
											if (isPropertyNotEmpty(data, 'observacions')) $('#observacions').val(data.observacions);
											if (isPropertyNotEmpty(data, 'preferenciaIdioma')) $('#preferenciaIdioma').val(data.preferenciaIdioma);
										}
									}
								}
							});
						}
					}
				});
			} else {
				$('#pais').val("");
				$('#pais').change();
				$('#provincia').val("");
				$('#provincia').change();
				$('#municipi').val("");
				$('#municipi').change();
				$('#codiPostal').val("");
				$('#adresa').val("");
				$('#documentNum').val("");
			}
		});

		$('select#pais').change(function() {
			if ($(this).val() == '724') {
				if ($('select#tipus').val() != '<%=es.caib.ripea.core.api.dto.InteressatTipusEnumDto.ADMINISTRACIO%>') {
					$('#provincia').prop("disabled", false);
					$('#municipi').prop("disabled", false);
				} else {
					$('#provincia').prop("disabled", true);
					$('#municipi').prop("disabled", true);
				}
			} else {
				$('#provincia').val("");
				$('#provincia').change();
				$('#provincia').prop("disabled", true);
				$('#municipi').val("");
				$('#municipi').change();
				$('#municipi').prop("disabled", true);
			}
		});
		var municipiActual = $('#municipi').val();
		$('select#provincia').change(function(valor) {
			if ($(this).val() != '') {
				$.ajax({
					type: 'GET',
					url: "<c:url value="/expedient/municipis/"/>" + $(this).val(),
					success: function(data) {
						var selMunicipi = $('#municipi');
						selMunicipi.empty();
						selMunicipi.append("<option value=\"\"></option>");
						if (data && data.length > 0) {
							var items = [];
							$.each(data, function(i, val) {
								items.push({
									"id": val.codi,
									"text": val.nom
								});
								selMunicipi.append("<option value=\"" + val.codi + "\">" + val.nom + "</option>");
							});
						}
						var select2Options = {theme: 'bootstrap', minimumResultsForSearch: "6"};
						selMunicipi.select2("destroy");
						selMunicipi.select2(select2Options);
						if (munOrgan != '') {
							selMunicipi.val(munOrgan);
							selMunicipi.change();
						}

						if (municipiActual)
							selMunicipi.val(municipiActual);
						else
							selMunicipi.val("407");
						selMunicipi.change();
					}
				});
			} else {
				var select2Options = {theme: 'bootstrap', minimumResultsForSearch: "6"};
				$('#municipi').select2("destroy");
				$('#municipi').select2(select2Options);
			}
		});

		$('select#provincia').trigger('change');

		$('select#filtreComunitat').change(function(valor) {
			var select2Options = {theme: 'bootstrap', minimumResultsForSearch: "6"};
			if ($(this).val() != '') {
				$.ajax({
					type: 'GET',
					url: "<c:url value="/expedient/provincies/"/>" + $(this).val(),
					success: function(data) {
						var selProvincia = $('#filtreProvincia');
						selProvincia.empty();
						selProvincia.append("<option value=\"\"></option>");
						if (data && data.length > 0) {
							var items = [];
							$.each(data, function(i, val) {
								items.push({
									"id": val.codi,
									"text": val.nom
								});
								selProvincia.append("<option value=\"" + val.codi + "\">" + val.nom + "</option>");
							});
						}
						selProvincia.select2("destroy");
						selProvincia.select2(select2Options);
						if (munOrgan != '')
							selProvincia.val(munOrgan);
						selProvincia.trigger("change");
					}
				});
			} else {
				$('#filtreProvincia').select2("destroy");
				$('#filtreProvincia').select2(select2Options);
			}
			$('#filtreLocalitat').select2("destroy");
			$('#filtreLocalitat').select2(select2Options);
		});

		$('select#filtreProvincia').change(function(valor) {
			if ($(this).val() != '') {
				$.ajax({
					type: 'GET',
					url: "<c:url value="/expedient/municipis/"/>" + $(this).val(),
					success: function(data) {
						var selMunicipi = $('#filtreLocalitat');
						selMunicipi.empty();
						selMunicipi.append("<option value=\"\"></option>");
						if (data && data.length > 0) {
							var items = [];
							$.each(data, function(i, val) {
								items.push({
									"id": val.codi + "-" + val.codiEntitatGeografica,
									"text": val.nom
								});
								selMunicipi.append("<option value=\"" + val.codi + "-" + val.codiEntitatGeografica + "\">" + val.nom + "</option>");
							});
						}
						var select2Options = {theme: 'bootstrap', minimumResultsForSearch: "6"};
						selMunicipi.select2("destroy");
						selMunicipi.select2(select2Options);
						if (munOrgan != '')
							selMunicipi.val(munOrgan);
						selMunicipi.trigger("change");
					}
				});
			} else {
				var select2Options = {theme: 'bootstrap', minimumResultsForSearch: "6"};
				$('#filtreLocalitat').select2("destroy");
				$('#filtreLocalitat').select2(select2Options);
			}
		});

		$('#filtre-btn').click(function(){
			var cod = $('#filtreCodiDir3').val();
			var den = $('#filtreDenominacio').val();
			var niv = $('#filtreNivellAdministracio').val();
			var com = $('#filtreComunitat').val();
			var pro = $('#filtreProvincia').val();
			var loc = $('#filtreLocalitat').val();
			var arr = $('#filtreArrel').prop('checked');
			$.ajax({
				type: 'POST',
				url: "<c:url value="/expedient/organ/filtre"/>",
				dataType: "json",
				data: {	codiDir3: cod,
						denominacio: den,
						nivellAdm: niv,
						comunitat: com,
						provincia: pro,
						localitat: loc,
						arrel: arr},
				success: function(data) {
					var selOrgan = $('#organCodi');
					selOrgan.empty();
					selOrgan.append("<option value=\"\"></option>");
					if (data && data.length > 0) {
						var items = [];
						$.each(data, function(i, val) {
							items.push({
								"id": val.codi,
								"text": val.denominacio
							});
							//Afegim la classe per identificar si és SIR, la cridada a dir3 (obtenerUnidad) al seleccionar un òrgan no retorna cap camp que ho indiqui
							selOrgan.append("<option value=\"" + val.codi + "\" class=\"" + (val.ambOficinaSir ? "ambOficinaSir" : '') + "\">" + val.denominacio + "</option>");
						});
					}
					var select2Options = {theme: 'bootstrap', minimumResultsForSearch: "6"};
					selOrgan.select2("destroy");
					selOrgan.select2(select2Options);
					selOrgan.change();
					selOrgan.select2("open");
				}
			});
		});

		$('#btnSave').click(function(){
			$('#tipus').prop( "disabled", false );
			$('#pais').prop("disabled", false);
			$('#provincia').prop("disabled", false);
			$('#municipi').prop("disabled", false);
			$('#codiPostal').prop("disabled", false);
			$('#adresa').prop("readonly", false);
			$('#documentTipus').prop("disabled", false);
			$('#documentNum').prop("readonly", false);
			$('#interessatform').submit();
		});

		<c:if test="${!esRepresentant && dehActiu}">
			$('input[type=checkbox][name=entregaDeh]').on('change', function() {
				if($(this).prop("checked") == true){
					$('#entregaDehObligatDiv').removeClass('hidden');

					$("label[for='email']").append(" *");
				} else {
					$('#entregaDehObligatDiv').addClass('hidden');
					$("label[for='email']").text($("label[for='email']").text().replace(/ \*/g, ""));
				}
				webutilModalAdjustHeight();
			});
			$('input[type=checkbox][name=entregaDeh').trigger('change');

			$('input[type=checkbox][name=entregaDehObligat]').on('change', function() {
				if($(this).prop("checked") == true){
					$('#entregaDeh').attr('disabled', 'disabled');
				} else {
					$('#entregaDeh').removeAttr('disabled');
				}
				webutilModalAdjustHeight();

				<c:if test="${!potModificar}">
					$('#entregaDeh').prop( "disabled", true );
					$('#entregaDehObligat').prop( "disabled", true );
				</c:if>


			});
			$('input[type=checkbox][name=entregaDehObligat').trigger('change');
		</c:if>

	});

	function canviVisibilitat(tipus) {
		$('input#nom').prop( "disabled", (tipus != 1) );
		$('input#llinatge1').prop( "disabled", (tipus != 1) );
		$('input#llinatge2').prop( "disabled", (tipus != 1) );
		if (tipus == 1) {
			$('input#nom').closest(".form-group").removeClass('ocult');
			$('input#llinatge1').closest(".form-group").removeClass('ocult');
			$('input#llinatge2').closest(".form-group").removeClass('ocult');
		} else {
			$('input#nom').closest(".form-group").addClass('ocult');
			$('input#llinatge1').closest(".form-group").addClass('ocult');
			$('input#llinatge2').closest(".form-group").addClass('ocult');
		}
		$('input#raoSocial').prop( "disabled", (tipus != 2) );
		if (tipus == 2) {
			$('input#raoSocial').closest(".form-group").removeClass('ocult');
		} else {
			$('input#raoSocial').closest(".form-group").addClass('ocult');
		}
		$('select#organCodi').prop( "disabled", (tipus != 3) );
		if (tipus == 3) {
			$('select#organCodi').closest(".form-group").removeClass('ocult');
			$('.organ-btn').removeClass('ocult');
		} else {
			$('select#organCodi').closest(".form-group").addClass('ocult');
			$('.organ-btn').addClass('ocult');
			$('#organ-filtre').removeClass('in');
		}
		$("label[for='pais']").text("<spring:message code="interessat.form.camp.pais"/>")
		$("label[for='provincia']").text("<spring:message code="interessat.form.camp.provincia"/>")
		$("label[for='municipi']").text("<spring:message code="interessat.form.camp.municipi"/>")
		$("label[for='adresa']").text("<spring:message code="interessat.form.camp.adresa"/>")
		$("label[for='codiPostal']").text("<spring:message code="interessat.form.camp.codiPostal"/>")


		<c:if test="${!potModificar}">
			$('#organCodi').prop( "disabled", true );
			$('#nom').prop( "disabled", true );
			$('#llinatge1').prop( "disabled", true );
			$('#llinatge2').prop( "disabled", true );
			$('#pais').prop( "disabled", true );
			$('#provincia').prop( "disabled", true );
			$('#municipi').prop( "disabled", true );
			$('#documentTipus').prop("disabled", true);
			$('#raoSocial').prop("disabled", true);

		</c:if>

	}
</script>
</head>
<body>

	<c:choose>
		<c:when test="${not empty esRepresentant}">
			<c:set var="formAction">
				<rip:modalUrl
					value="/expedient/${expedientId}/interessat/${interessatId}/representant" />
			</c:set>
		</c:when>
		<c:otherwise>
			<c:set var="formAction">
				<rip:modalUrl value="/expedient/${expedientId}/interessat" />
			</c:set>
		</c:otherwise>
	</c:choose>

	<form:form 
		id="interessatform" action="${formAction}" method="post"
		cssClass="form-horizontal" commandName="interessatCommand">
		<form:hidden path="entitatId" />
		<form:hidden path="id" />
		<form:hidden path="expedientId" />
		<form:hidden path="formulariAnterior" />
		<input type="hidden" id="id" />
		
		<!-- Tipus d'interessat -->
		<rip:inputSelect name="tipus" textKey="interessat.form.camp.tipus"
			optionItems="${tipusEnumOptions}" optionTextKeyAttribute="text"
			optionValueAttribute="value" labelSize="2" disabled="${!potModificar || not empty id}"/>
			
		<!-- Filtre de administració -->
		<div class="row ">
			<div id="organ-filtre" class="col-xs-10 collapse pull-right">
				<div class="panel panel-default ">
					<div class="panel-heading">
						<h3 class="panel-title">
							<spring:message code="interessat.form.organ.filtre.titol" />
						</h3>
					</div>
					<div class="panel-body">
						<div class="col-xs-6">
							<rip:inputSelect name="filtreNivellAdministracio"
								textKey="interessat.form.camp.organ.filtre.nivell"
								optionItems="${nivellAdministracions}"
								optionTextAttribute="descripcio" optionValueAttribute="codi"
								emptyOption="true" optionMinimumResultsForSearch="6" />
						</div>
						<div class="col-xs-6">
							<rip:inputSelect name="filtreComunitat"
								textKey="interessat.form.camp.organ.filtre.comunitat"
								optionItems="${comunitats}" optionTextAttribute="nom"
								optionValueAttribute="codi" emptyOption="true"
								optionMinimumResultsForSearch="6" />
						</div>
						<div class="col-xs-6">
							<rip:inputSelect name="filtreProvincia"
								textKey="interessat.form.camp.organ.filtre.provincia"
								optionItems="${provincies}" optionTextAttribute="nom"
								optionValueAttribute="codi" emptyOption="true"
								optionMinimumResultsForSearch="6" />
						</div>
						<div class="col-xs-6">
							<rip:inputSelect name="filtreLocalitat"
								textKey="interessat.form.camp.organ.filtre.municipi"
								optionItems="${municipis}" optionTextAttribute="nom"
								optionValueAttribute="codiDir3" emptyOption="true"
								optionMinimumResultsForSearch="6" />
						</div>
						<div class="col-xs-6">
							<rip:inputText name="filtreCodiDir3"
								textKey="interessat.form.camp.organ.filtre.codi" />
						</div>
						<div class="col-xs-6">
							<rip:inputText name="filtreDenominacio"
								textKey="interessat.form.camp.organ.filtre.denominacio" />
						</div>
						<div class="col-xs-6">
							<rip:inputCheckbox name="filtreArrel"
								textKey="interessat.form.camp.organ.filtre.arrel" labelSize="4" />
						</div>
						<div class="col-xs-6">
							<button id="filtre-btn" type="button" class="btn btn-default">
								<span class="fa fa-download"></span>
								<spring:message code="interessat.form.organ.filtre.actualitzar" />
							</button>
						</div>
					</div>
				</div>
			</div>
		</div>
		<!--Selector d'administració i botó per obrir filtre -->
		<div class="col-xs-11 organ">
			<rip:inputSelect name="organCodi"
				textKey="interessat.form.camp.organCodi"
				optionItems="${unitatsOrganitzatives}"
				optionTextAttribute="denominacio" optionValueAttribute="codi"
				emptyOption="true" required="true" optionMinimumResultsForSearch="6"
				labelSize="2" disabled="${!potModificar}"/>
		</div>
		<c:if test="${potModificar}">
			<div class="col-xs-1 organ-btn">
				<button type="button" class="btn btn-default" data-toggle="collapse"
					data-target="#organ-filtre">
					<span class="fa fa-bars"></span>
				</button>
			</div>
		</c:if>
		<form:hidden path="ambOficinaSir"/>
		
		<div class="row">
			<!-- Tipus de document -->
			<div class="col-xs-12">
				<rip:inputSelect name="documentTipus"
					textKey="interessat.form.camp.document.tipus"
				 	labelSize="2"
					optionItems="${documentTipusEnumOptions}"
					optionTextKeyAttribute="text" optionValueAttribute="value" disabled="${!potModificar}"/>
			</div>
			<!-- Número de document -->
			<div class="col-xs-12">
				<div id="editar-warn" class="alert alert-warning" style="display: none">
					<spring:message code="interessat.form.editar.warning"/>
				</div>
			</div>
			<c:choose>
				<c:when test='${esRepresentant}'><c:set var="comentari_docnum" value="interessat.form.camp.document.numero.comment.representant" /></c:when>
				<c:otherwise><c:set var="comentari_docnum" value="interessat.form.camp.document.numero.comment.interessat" /></c:otherwise>
			</c:choose>
			<div class="col-xs-12">
				<rip:inputText name="documentNum" textKey="interessat.form.camp.document.numero" required="true" labelSize="2" disabled="${!potModificar}" comment="${comentari_docnum}" />
			</div>
		</div>
		
		<!-- Nom --> 
		<rip:inputText name="nom" textKey="interessat.form.camp.nom"
			required="true" labelSize="2" disabled="${!potModificar}"/>

		<div class="row">
			<!-- Llinatge1 -->
			<div class="col-xs-6">
				<rip:inputText name="llinatge1"
					textKey="interessat.form.camp.llinatge1" required="true" disabled="${!potModificar}"/>
			</div>
			<!-- Llinatge2 -->
			<div class="col-xs-6">
				<rip:inputText name="llinatge2"
					textKey="interessat.form.camp.llinatge2" disabled="${!potModificar}"/>
			</div>
		</div>
		<!-- Raó social -->
		<rip:inputText name="raoSocial"
			textKey="interessat.form.camp.raoSocial" required="true"
			labelSize="2" disabled="${!potModificar}"/>
		
		<div class="row">
			<!-- País -->
			<div class="col-xs-6">
				<rip:inputSelect name="pais" textKey="interessat.form.camp.pais"
					optionItems="${paisos}" optionTextAttribute="nom"
					optionValueAttribute="codi" emptyOption="true"
					optionMinimumResultsForSearch="6" disabled="${!potModificar}"/>
			</div>
			<!-- Província -->
			<div class="col-xs-6">
				<rip:inputSelect name="provincia"
					textKey="interessat.form.camp.provincia"
					optionItems="${provincies}" optionTextAttribute="nom"
					optionValueAttribute="codi" emptyOption="true"
					optionMinimumResultsForSearch="6" disabled="${!potModificar}"/>
			</div>
		</div>
		
		<div class="row">
			<!-- Municipi -->
			<div class="col-xs-6">
				<rip:inputSelect name="municipi"
					textKey="interessat.form.camp.municipi" optionItems="${municipis}"
					optionTextAttribute="nom" optionValueAttribute="codi"
					emptyOption="true" optionMinimumResultsForSearch="6" disabled="${!potModificar}"/>
			</div>
			<!-- Codi postal -->
			<div class="col-xs-6">
				<rip:inputText name="codiPostal"
					textKey="interessat.form.camp.codiPostal" disabled="${!potModificar}"/>
			</div>
		</div>
		
		<!-- Adressa -->
		<rip:inputTextarea name="adresa" textKey="interessat.form.camp.adresa"
			labelSize="2" disabled="${!potModificar}"/>
		
		<div class="row">
			<!-- Correu electrònic -->
			<div class="col-xs-6">
				<rip:inputText name="email" textKey="interessat.form.camp.email" disabled="${!potModificar}"/>
			</div>
			<!-- Telèfon -->
			<div class="col-xs-6">
				<rip:inputText name="telefon" textKey="interessat.form.camp.telefon" disabled="${!potModificar}"/>
			</div>
		</div>
		
		<!-- Observacions -->
		<rip:inputTextarea name="observacions"
			textKey="interessat.form.camp.observacions" labelSize="2" disabled="${!potModificar}"/>
			
		<!-- Idioma preferent -->
		<div class="row">
			<div class="col-xs-6">
				<rip:inputSelect 
					name="preferenciaIdioma"
					textKey="interessat.form.camp.idioma"
					optionItems="${idiomaEnumOptions}" 
					optionTextKeyAttribute="text"
					optionValueAttribute="value" 
					disabled="${!potModificar}"/>
			</div>
		</div>
		<c:if test="${!esRepresentant && dehActiu}">
			<!-- Entrega a la Direcció Electrònica Hablitada (DEH) -->
			<div class="row">
				<div class="col-xs-6" style="float: right;">
					<rip:inputCheckbox name="entregaDeh"
						textKey="interessat.form.camp.entregaDeh" labelSize="10" disabled="${!potModificar}"/>
				</div>
			</div>
			
			<!-- Obligat -->
			<div class="row" class="hidden">
				<div class="col-xs-6" style="float: right;"
					id="entregaDehObligatDiv">
					<rip:inputCheckbox name="entregaDehObligat"
						textKey="interessat.form.camp.entregaDehObligat" labelSize="10" disabled="${!potModificar}"/>
				</div>
			</div>
		</c:if>


		<div id="modal-botons" class="well">
			<c:if test="${potModificar}">
				<button id="btnSave" type="button" class="btn btn-success"><span class="fa fa-save"></span>
					<c:choose>
						<c:when test="${empty interessatCommand.id}"><spring:message code="comu.boto.crear"/></c:when>
						<c:otherwise><spring:message code="comu.boto.modificar"/></c:otherwise>
					</c:choose>
				</button>			
			</c:if>
			<a href="<c:url value="/contingut/${expedientId}"/>" class="btn btn-default" data-modal-cancel="true">
				<spring:message code="comu.boto.${potModificar ? 'cancelar' : 'tancar'}" />
			</a>
		</div>
	</form:form>

	<div class="rmodal"></div>
</body>
</html>
