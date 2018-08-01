<%@ attribute name="modalId" required="true"%>
<%@ attribute name="jquerySelector" required="false"%>
<%@ attribute name="refrescarAlertes" required="false"%>
<%@ attribute name="refrescarTaula" required="false"%>
<%@ attribute name="refrescarTaulaId" required="false"%>
<%@ attribute name="refrescarPagina" required="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<div class="modal fade" id="modal-${modalId}" tabindex="-1" role="dialog" aria-labelledby="" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
				<h4 class="modal-title"></h4>
			</div>
			<div class="modal-body">
				<iframe frameborder="0" height="100" width="99.6%"></iframe>
			</div>
			<div class="modal-footer">
			</div>
		</div>
	</div>
</div>
<script>
	function modalCanviarTitol(iframe, titol) {
		$('.modal-header h4', $(iframe).parent().parent()).html(titol);
	}
	function modalCopiarBotons(iframe, elements) {
		$('.modal-footer *', $(iframe).parent().parent()).remove();
		elements.each(function(index) {
			var element = $(this);
			var clon = element.clone();
			if (clon.hasClass('btn-tancar')) {
				clon.on('click', function () {
					$(iframe).parent().parent().parent().parent().modal('hide');
					return false;
				});
			} else {
				clon.on('click', function () {
					element.click();
					return false;
				});
			}
			$('.modal-footer', $(iframe).parent().parent()).append(clon);
		});
	}
	function modalTancarIRefrescar(iframe) {
		var modalobj = $(iframe).parent().parent().parent().parent();
		modalobj.modal('hide');
		if (modalobj.data('modal-refrescar-taula')) {
			var refrescat = false;
			$('.dataTables_paginate li', $("#" + modalobj.data('modal-refrescar-taula-id')).parent()).each(function() {
				if ($(this).hasClass('active')) {
					$('a', this).click();
					refrescat = true;
				}
			});
			if (!refrescat)
				$("#" + modalobj.data('modal-refrescar-taula-id')).dataTable().fnDraw();
		}
		if (modalobj.data('modal-refrescar-pagina')) {
			window.location.href = window.location.href;
		}
		if (modalobj.data('modal-refrescar-alertes')) {
			$.ajax({
				"url": "<c:url value="/nodeco/util/alertes"/>",
				"success": function (data) {
					$('#contingut-missatges *').remove();
					$('#contingut-missatges').append(data);
				},
				error: function(xhr, textStatus, errorThrown) {
					console.log("<spring:message code="peticio.ajax.error"/>: " + xhr.responseText);
					if (textStatus == 'timeout')
						alert("<spring:message code="peticio.ajax.timeout"/>");
					else
						alert("<spring:message code="peticio.ajax.error"/>: " + errorThrown);
				}
		    });
		}
	}
	function modalAjustarTamany(iframe, height, width) {
		$(iframe).height(height + 'px');
		if (width) {
			var modalobj = $(iframe).parent().parent().parent();
			modalobj.css('width', width + 'px');
		}
    }
<c:if test="${refrescarTaula == 'true'}">$('#modal-${modalId}').data('modal-refrescar-taula', true);</c:if>
<c:if test="${refrescarTaula == 'true'}">$('#modal-${modalId}').data('modal-refrescar-taula-id', '${refrescarTaulaId}');</c:if>
<c:if test="${refrescarPagina == 'true'}">$('#modal-${modalId}').data('modal-refrescar-pagina', true);</c:if>
<c:if test="${refrescarAlertes == 'true'}">$('#modal-${modalId}').data('modal-refrescar-alertes', true);</c:if>
<c:if test="${not empty jquerySelector}">
	$('${jquerySelector}').on('click', function() {
		var modalUrl = $(this).attr("href");
		if (modalUrl.indexOf("../") != -1)
			modalUrl = modalUrl.substr(0, modalUrl.lastIndexOf("../") + "../".length) + "modal/" + modalUrl.substr(modalUrl.lastIndexOf("../") + "../".length);
		else
			modalUrl = "modal/" + modalUrl;
		var modalobj = $('#modal-${modalId}');
		modalobj.on('show.bs.modal', function () {
			$('iframe', modalobj).empty();
			$('iframe', modalobj).attr(
					"src",
					modalUrl);
		});
		modalobj.modal({show:true});
		return false;
	});
</c:if>
</script>
