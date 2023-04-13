<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>


<style>



#grid-documents {
	margin-left: 0;
	margin-right: -11px;
}
#grid-documents li.element-contingut {
	margin: 0 0 0px 0;
	padding: 0 10px 0 0;
	min-height: 140px;
	display: -moz-inline-stack;
	display: inline-block;
	vertical-align: top;
	zoom: 1;
	*display: inline;
	_height: 140px;
}
#grid-documents .thumbnail {
	margin-bottom: 0 !important;
	border: 2px solid #f9f9f9;
}
#grid-documents .thumbnail:hover {
	border: 2px solid #ddd;
	background-color: #f5f5f5;
	cursor: pointer;
}
#grid-documents .thumbnail h4 {
	margin-top: 4px;
}
#grid-documents .thumbnail a {
	text-decoration: none;
}
.element-hover .thumbnail {
	border: 2px solid #ddd !important;
	background-color: #f5f5f5;
}
#grid-documents .caption p {
	
}
#grid-documents .caption .dropdown-menu {
	text-align: left;
}
#grid-documents .caption .dropdown-menu li {
	width: 100%;
	margin: 0;
	padding: 0;
}

#contingut-botons {
	margin-bottom: .8em;
}

.drag_activated {
	border: 4px dashed #ffd351;
	height: 200px;
	width: 100%;
	background-color: #f5f5f5;
	display: flex;
	justify-content: center;
	align-items: center;
	flex-direction: column;
}

.ordre-col {
    cursor: move;
    vertical-align: middle !important;
}

.popover{
	max-width: none;
    z-index: 100;
    cursor: default;
	width: 500px;
}
.popover .close {
	position: relative;
	top: -3px;
}


#detallSignantsPreview .alert {
	padding: 10px 15px !important;
}
#detallSignantsPreview button.close-alertes {
    background: none repeat scroll 0 0 transparent;
    border: 0 none;
    cursor: pointer;
    padding: 0;
}
#detallSignantsPreview .close-alertes {
    color: #000000;
    float: right;
    font-weight: bold;
    opacity: 0.2;
    text-shadow: 0 1px 0 #FFFFFF;
}
#detallSignants .alert {
	padding: 10px 15px !important;
}
#detallSignants button.close-alertes {
    background: none repeat scroll 0 0 transparent;
    border: 0 none;
    cursor: pointer;
    padding: 0;
}
#detallSignants .close-alertes {
    color: #000000;
    float: right;
    font-weight: bold;
    opacity: 0.2;
    text-shadow: 0 1px 0 #FFFFFF;
}



.icona-esborrany {
	color: #ffab66;
	font-size: 16px;
	vertical-align: text-top;
}
.definitiu.fa.fa-check-square {
	color: #02cda2;
	font-size: 16px;
	vertical-align: text-top;
}
.firmat.fa.fa-pencil-square {
	color: #02cda2;
	font-size: 16px;
	vertical-align: text-top;
}
.pendent.fa.fa-pencil-square {
	color: #67bdff;
	font-size: 16px;
	vertical-align: text-top;
}
.parcial.fa.fa-pencil-square {
	color: #FFAB66;
	font-size: 16px;
	vertical-align: text-top;
}
.error.fa.fa-pencil-square {
	color: #ffab66;
	font-size: 16px;
	vertical-align: text-top;
}
.pendent.fa.fa-envelope-square {
	color: #67bdff;
	font-size: 16px;
	vertical-align: text-top;
}
.enviada.fa.fa-envelope-square {
	color: #67bdff;
	font-size: 16px;
	vertical-align: text-top;
}
.processada.fa.fa-envelope-square {
	color: #02cda2;
	font-size: 16px;
	vertical-align: text-top;
}
.error.fa.fa-envelope-square {
	color: #ffab66;
	font-size: 16px;
	vertical-align: text-top;
}
.importat.fa.fa-info-circle {
	color: #02CDA2;
}


.viewer-content {
	width: 100%;
	padding-top: 1% !important;
}
.viewer-padding {
	padding: 0% 2% 0% 2%;
}

.rmodal_loading {
    background: rgba( 255, 255, 255, .8 ) 
                url('<c:url value="../img/loading.gif"/>') 
                50% 50% 
                no-repeat;
}



.ui-droppable-hover{
	background: #999999 !important;
}


</style>





<script>


//################################################## document ready START ##############################################################
$(document).ready(function() {


	//------------------------- if contingut is not document START ----------------------------------	
	<c:if test="${!contingut.document}"> 

	
		clearSelection();
		enableDisableMultipleButtons();

		$("#tascaBtn").appendTo(".panel-heading h2");
		<c:if test="${isTasca}"> $('title').html("Ripea - ${tascaNom}");</c:if>
	
		$('#table-documents').treeTable();
		//remove treetable click events on unnecessary columns for all rows
		$('#table-documents > tbody > tr > td:is(:nth-child(1), :nth-child(7), :nth-child(8)').css('cursor','default').unbind('click');
		//remove treetable click events on all columns for document rows
		$('#table-documents > tbody > tr.isDocument > td').css('cursor','default').unbind('click');
		//add show viewer click events on all necessary columns for document rows
		$('#table-documents > tbody > tr.isDocument > td:is(:nth-child(2), :nth-child(3), :nth-child(4), :nth-child(5), :nth-child(6)').css('cursor','pointer').click(function(event) {
			event.stopPropagation();
			$('a:first', $(this).parent())[0].click();
		});
		

		// order by dragging
		$('.ordre-col').on('mouseover', function() {
			$('.element-draggable').draggable({ disabled: true });
			$('.element-draggable').droppable({ disabled: true });
			$('#table-documents tbody').sortable({
				handle: ".ordre-col",
				refreshPositions: true,
		        helper : 'clone',
				cursor: "move",
				cursorAt: { left: 5 },
				opacity: 0.65,
				placeholder: "sortable-dest",
				start: function (event, ui) {
					$(this).attr('data-previndex', ui.item[0].rowIndex);
		        },
		        update: function (event, ui) {
					//showLoadingModal('<spring:message code="contingut.moure.processant"/>');
					var tableDocuments = document.getElementById('table-documents');
					$(tableDocuments).addClass("disabled");
		            $('#loading').removeClass('hidden');
					var idsInOrder = $('#table-documents tbody').sortable("toArray", {attribute: 'id'});
		            var filtered = idsInOrder.filter(function (el) {
		           		return el != '';
		            });
		            var orderedElements = new Map();
					var idx = 1;
		            filtered.forEach(function(row) {
		            	orderedElements[idx] = row;
			            idx++;
		            });
		
		            $.ajax({
				        url: '<c:url value="/contingut/${expedientId}/ordenar"/>',
				        type: "POST",
				        contentType: "application/json",
				        data: JSON.stringify(orderedElements),
				        success: function (data) {
				        	location.reload();
				        }
					});
		        }
		    }).disableSelection();
		});
		$('.ordre-col').on('mouseleave', function() {
			$('.element-draggable').draggable("enable");
			$('.element-draggable').droppable("enable");
		});

		
		// add new document by dragging it to #drag_container 
		$('#drag_container').filedrop({
			maxfiles: 1,
			fallback_dropzoneClick : false,
			error: function(err, file) {
				switch(err) {
				case 'BrowserNotSupported':
					alert('browser does not support HTML5 drag and drop')
					break;
				case 'TooManyFiles':
					alert('Només es pemet adjuntar un document a la vegada!')
					break;
				case 'FileTooLarge':
					break;
				case 'FileTypeNotAllowed':
					break;
				case 'FileExtensionNotAllowed':
					break;
				default:
					break;
				}
			},
			dragOver: function() {
				$('#drag_container').css('background-color', '#e6e3e3');
			},
			dragLeave: function() {
				$('#drag_container').css('background-color', '#f5f5f5');
			},
			drop: function(e) {
				if (e.originalEvent.dataTransfer != null) {
					let files = e.originalEvent.dataTransfer.files;
					$('#drag_container').css('background-color', '#f5f5f5');
					if (!(files.length > 1)) {
						document.querySelector('#dropped-files').files = files;
						$('#document-new').trigger('click');
					}
				}
			}
		});

		
		// canvi tipus document multiple
		var $botoTipusDocumental = $('#tipusdocumental-mult');
		$botoTipusDocumental.popover({
			html: true,
			placement: 'bottom',
			title: '<spring:message code="massiu.canvi.tipus.document.select"/> <a href="#" class="close" data-dismiss="alert">&times;</a>',
		    content: function () {
		    	return showTipusDocumentals($(this));   
		  	}
		}).on('shown.bs.popover', function () {
			var $selTipusDocument = $('.select-tipus-massiu');
			var select2Options = {
					theme: 'bootstrap', 
					width: 'auto', 
					minimumResultsForSearch: "0"};
			$selTipusDocument.select2(select2Options);
			$selTipusDocument.on('change', function(event) {
				var tipusDocumentId = $(':selected', $(this)).attr('id');
				if (tipusDocumentId) {
					showLoadingModal('<spring:message code="contingut.info.document.tipusdocument.massiu.processant"/>');				
					var updateUrl = '<c:url value="/contingut/updateTipusDocumentMassiu/"/>' + tipusDocumentId;
					$.ajax({
						type: 'GET',
				        url: updateUrl,
				        success: function(json) {
				        	if (json.error) {
				        		$('div.modal').modal('hide');
								$('#contingut-missatges').append('<div class="alert alert-danger"><button type="button" class="close-alertes" data-dismiss="alert" aria-hidden="true"><span class="fa fa-times"></span></button>' + 'Hi ha hagut un error actualitzant el document amb el nou tipus de document: ' + json.errorMsg + '</div>');
				        	} else {
								location.reload();
							}
				        },
				        error: function(e) {
				        	alert("Hi ha hagut un error actualitzant algún dels documents seleccionats amb el nou tipus de document");
				        	location.reload();
				        }
				    });	
				}
			});
			
			var $popoverClose = $('.popover .close');
			$popoverClose.on('click', function() {
				$botoTipusDocumental.popover('hide');
			});
		});
		$botoTipusDocumental.on('click', function() {
			$botoTipusDocumental.popover("show");
		});
		// canvi tipus document single
		// TODO: revise, element select-tipus-document doesnt exist
		var selTipusDocument = $('.select-tipus-document'); 
		var select2Options = {
				theme: 'bootstrap', 
				width: 'auto', 
				minimumResultsForSearch: "0"};
		selTipusDocument.select2(select2Options);
		selTipusDocument.on('change', function(event){
			var tipusDocumentId = $(':selected', $(this)).attr('id');
				showLoadingModal('<spring:message code="contingut.info.document.tipusdocument.processant"/>');
				var documentId = $(this).attr('id');
				
				var updateUrl = '<c:url value="/contingut/' + documentId + '/document/updateTipusDocument"/>' +'?tipusDocumentId=' + tipusDocumentId;
				$.ajax({
					type: 'GET',
			        url: updateUrl,
			        success: function(json) {
			        	if (json.error) {
			        		$('div.modal').modal('hide');
							$('#contingut-missatges').append('<div class="alert alert-danger"><button type="button" class="close-alertes" data-dismiss="alert" aria-hidden="true"><span class="fa fa-times"></span></button>' + 'Hi ha hagut un error actualitzant el document amb el nou tipus de document: ' + json.errorMsg + '</div>');
			        	} else {
							location.reload();
						}
			        },
			        error: function(e) {
			        	alert("Hi ha hagut un error actualitzant el document amb el nou tipus de document");
			        	location.reload();
			        }
			    });	
		});
		

		// Ja has afegit tots els documents a l'expedient
		$("#document-new-empty-metadocuments").click(function(e){
		    alert("<spring:message code="contingut.document.alerta.max"/>");
		    e.preventDefault();
		});	

		
		// popover notificacions
		$("span[class*='popover-']").popover({
			html: true,
		    content: function () {
		    	return getEnviamentsDocument($(this));   
		  	}
		}).on('mouseenter', function () {
		    $(this).popover("show");
		   
		    $(".popover").on('mouseleave', function () {
		        $(this).popover('hide');
		    });
		}).on('mouseleave', function () {
		   	if (!$('.popover:hover').length) {
		    	$(this).popover('hide');
		    }
		});	







		let vistaActiva = $('#vistes').children("a.active").attr('id');
		//-------------------------------- VISTA GRID  -----------------------------------------﻿
		if (vistaActiva == 'vistaGrid') {
			// Habilitar selecció múltiple
			$('#habilitar-mult').on('click', function() {
				var contenidorContingut = document.getElementById('grid-documents');
				var inputs = contenidorContingut.querySelectorAll('li>div');
				
				if ($(contenidorContingut).hasClass('multiple')) {
					$('#checkItAll').addClass('disabled');
					$(contenidorContingut).removeClass('multiple');
					$(this).removeClass('active');
					clearSelection();
					enableDisableMultipleButtons();
					inputs.forEach(function(element) {
						if ($(element).hasClass('selectd')) {
							$(element).removeClass('selectd');
						}
					}); 
				} else {
					$('#checkItAll').removeClass('disabled');
					$(contenidorContingut).addClass('multiple');
					$(this).addClass('active');
				}
			});

			// Seleccionar tots
			$('#checkItAll').on('click', function(){
				let docsIdx = [];
				var listDocuments = document.getElementById('grid-documents');
				var elements = listDocuments.querySelectorAll('li>div');
				$('#checkItAll').toggleClass('active');
	
				if ($('#checkItAll').hasClass('active') && $(listDocuments).hasClass('multiple')) {
					elements.forEach(function(child) {
						var childParent = $(child.parentElement);
						var isCarpeta = childParent.hasClass('element-droppable');
						if (!isCarpeta) {
							$(child).addClass('selectd');
							var index = docsIdx.indexOf(parseInt(child.id));
							if (index < 0) {
								docsIdx.push(parseInt(child.id));
							}
						}
					}); 
					enableDisableMultipleButtons(docsIdx);
					selectAll(docsIdx);
				} else if ($(listDocuments).hasClass('multiple')) {
					elements.forEach(function(input) {
						$(input).removeClass('selectd');
						var index = docsIdx.indexOf(parseInt(input.id));
						if (index > -1) {
							docsIdx.splice(index, 1);
						}
				    });  
					enableDisableMultipleButtons(docsIdx);
					deselectAll();
				}
			});


			// move to another folder by drag and drop (jquery-ui widget) 
			// TODO: revise, is necessary for vista icones? 
			$('.element-draggable').draggable({
				containment: 'parent',
				helper: 'clone',
				revert: true,
				revertDuration: 200,
				opacity: 0.50,
				zIndex: 100,
				start: function() {
					$('div.element-noclick', this).addClass('noclick');
					$('div.element-noclick', this).tooltip('hide');
					$('div.element-noclick', this).tooltip('disable');
				},
				stop: function() {
					$('div.element-noclick', this).tooltip('enable');
				}
			});	
			$('.element-droppable').children(":not('.ordre-col')").droppable({
				accept: '.element-draggable',
				tolerance: 'pointer',
				activeClass: 'element-target',
				hoverClass: 'element-hover',
				drop: function(event, ui) {
					showLoadingModal('<spring:message code="contingut.moure.processant"/>');
					var origenId = ui.draggable.attr('id');
					var destiId = $(this).parent().attr('id');
					window.location = origenId + "/moure/" + destiId;
					dropped = true;
			        $(event.target).addClass('dropped');
				}
			});
			

			$('li.element-contingut .caption p').each(function() {
				$clamp(this, {
					clamp: 2,
					useNativeClamp: true
				});
			});

			
			$('#grid-documents li').mouseover(function() {
				$('a.btn', this).removeClass('hidden');
			});
			$('#grid-documents li').mouseout(function() {
				$('a.btn', this).addClass('hidden');
			});
			$('#grid-documents li a.confirm-delete').click(function() {
				return confirm('<spring:message code="contingut.confirmacio.esborrar.node"/>');
			});
			
		//--------------------------------------  VISTA NOT GRID  ------------------------------------
		} else {

			if (!$('#table-documents .treetable-expander').length) {
				$('#expandCollapseButtons').hide();
			}

			// Seleccionar tots
			$('#checkItAll').on('change', function(){

				let isChecked = $(this).prop('checked');
				if (isChecked) {
					$('#table-documents tbody .checkbox').each(function() {
						$(this).prop('checked', true);
					});
				} else {
					$('#table-documents tbody .checkbox').each(function() {
						$(this).prop('checked', false);
					});
				}

				let idsSelected = getIdsSelectedFromTable();
				enableDisableMultipleButtons(idsSelected);
				if (isChecked) {
					selectAll(idsSelected);
				} else {
					deselectAll();
				}
				
			});

			// Select one
			$('.checkbox').change(function() {
				
				let docsIdx = [];
				let selectedId = $(this).closest('tr').attr('id');
				docsIdx.push(parseInt(selectedId));
				
				if ($(this).prop('checked')) {
					var multipleUrl = '<c:url value="/contingut/${contingut.id}/select"/>';
					$.get(
							multipleUrl, 
							{docsIdx: docsIdx},
							function(data) {
								$(".seleccioCount").html(data);
							}
					);
				} else {
					var multipleUrl = '<c:url value="/contingut/${contingut.id}/deselect"/>';
					$.get(
							multipleUrl, 
							{docsIdx: docsIdx},
							function(data) {
								$(".seleccioCount").html(data);
							}
					);
				}

				let idsSelected = getIdsSelectedFromTable();
				enableDisableMultipleButtons(idsSelected);
			});


			if (vistaActiva == 'vistaTreetablePerCarpetes'){
				// move to another folder by drag and drop (jquery-ui widget) 
				$('.element-draggable').draggable({
					containment: 'parent',
					helper: 'clone',
					revert: true,
					revertDuration: 200,
					opacity: 0.50,
				});	
				$('.element-droppable').droppable({
					accept: '.element-draggable',
					tolerance: 'pointer',
					drop: function(event, ui) {
						showLoadingModal('<spring:message code="contingut.moure.processant"/>');
						let origenId = ui.draggable.attr('id');
						let destiId = $(this).attr('id');
						window.location = origenId + "/moure/" + destiId;
					}
				});

			} else if (vistaActiva == 'vistaTreetablePerTipusDocuments'){
				// change tipus de document by drag and drop (jquery-ui widget) 
				$('.element-draggable').draggable({
					containment: 'parent',
					helper: 'clone',
					revert: true,
					revertDuration: 200,
					opacity: 0.50,
				});	


				
				$('.element-droppable').droppable({
					accept: '.element-draggable',
					tolerance: 'pointer',
					drop: function(event, ui) {
						showLoadingModal('<spring:message code="contingut.canvi.tipus.document.processant"/>');
						let origenId = ui.draggable.attr('id');
						let destiId = $(this).attr('id');
						let updateUrl = '${pageContext.request.contextPath}/contingut/${isTasca}/${isTasca ? tascaId : expedientId}/updateTipusDocumentDragDrop/' + origenId + '/' + destiId
						window.location = updateUrl;
					}
				});
			}
			
				
			




			
		}


	

		
		
	</c:if>//------------------------- if contingut is not document END ----------------------------------



	$("#mostraDetallSignants").click(function(){
		let contingutId = ${contingut.id}; 
		getDetallsSignants($('#detallSignants'), contingutId, false);
	});
	
	
});//################################################## document ready END ##############################################################




//------------------------- if contingut is not document START ----------------------------------	
<c:if test="${!contingut.document}"> 


	function showLoadingModal(message) {
		var modalDivId = "modalLoading";
		
		modalData = "";
		if ($('#' + modalDivId).length == 0 ) {
			$('body').append(modalLoading(modalDivId, modalData, message));
		} 
		var modalobj = $('#' + modalDivId + ' > div.modal');
		modalobj.modal({
		      backdrop: "static", //remove ability to close modal with click
		      keyboard: false, //remove option to close with keyboard
		      show: true //Display loader!
		    });
	}
	function modalCloseLoadingHandler() {
		$('body').addClass('loading');
	}
	function addLoading(idModal) {
		$('#' + idModal).on('hidden.bs.modal', modalCloseLoadingHandler)
	}
	function removeLoading(idModal) {
		if (idModal) {
			$('#' + idModal).off('hidden.bs.modal', modalCloseLoadingHandler)
		} else {
			$('body').removeClass('loading');
		}
	}
	function modalLoading(modalDivId, modalData, message){
		return  '<div id="' + modalDivId + '"' + modalData + '>' +
				'	<div class="modal" tabindex="-1" role="dialog" aria-labelledby="" aria-hidden="true">' +
				'		<div class="modal-dialog modal-sm" role="document">' +
			    '			<div class="modal-content" style="border-radius: 0px;box-shadow: 0 0 20px 8px rgba(0, 0, 0, 0.7);">' +
			    '				<div class="modal-body text-center">' +
			    '					<div class="loader"></div>' +
				'					<div clas="loader-txt">' +
				'						<p>' + message + '</p>' +
				'					</div>' +
				'				</div>' +
			    '			</div>' +
				'		</div>' +
				'	</div>' +
				'</div>';
	}
	

	function removeTransactionId(idModal) {
		if (idModal) {
			$('#' + idModal).on('hidden.bs.modal', function() {
				var idTransaccio = localStorage.getItem('transaccioId');
				if (idTransaccio) {
					$.ajax({
				    	type: 'GET',
						url: "<c:url value='/document/portafirmes/tancarTransaccio/" + idTransaccio + "'/>",
						success: function() {
							localStorage.removeItem('transaccioId');
						},
						error: function(err) {
							console.log("Error tancant la transacció");
						}
				    });
				}
			});
		}
	}
	

	//------------------ VISOR ------------------------------
	function showViewer(event, documentId, contingutNom, contingutCustodiat) {
		if (event.target.tagName.toLowerCase() !== 'a' && (event.target.cellIndex === undefined || event.target.cellIndex === 5 || event.target.cellIndex === 6)) return;
	    var resumViewer = $('#resum-viewer');
		// Mostrar/amagar visor
		if (!resumViewer.is(':visible')) {
			resumViewer.slideDown(500);
		} else if (previousDocumentId == undefined || previousDocumentId == documentId) {
			closeViewer();
			previousDocumentId = documentId;
			return;
		}
		previousDocumentId = documentId;
		
	    // Mostrar contingut capçalera visor
	    resumViewer.find('*').not('#container').remove();
	    var signantsViewerContent = '<div style="padding: 0% 2% 2% 2%; margin-top: -8px; display: flex; flex-wrap: wrap;">\
										<table style="width: 453px; flex-basis: calc(100%/3); margin-bottom: 10px;">\
											<tbody id="detallSignantsPreview">\
											</tbody>\
										</table>\
									 </div>';
	    var viewerContent = '<div class="panel-heading"><spring:message code="contingut.previsualitzacio"/> \
	    					 <span class="fa fa-close" style="float: right; cursor: pointer;" onClick="closeViewer()"></span>\
	    					 </div>\
	    					 <div class="viewer-content viewer-padding">\
	    						<dl class="dl-horizontal">\
		        					<dt style="text-align: left;"><spring:message code="contingut.info.nom"/> </dt><dd>' + contingutNom + '</dd>\
	        					</dl>\
	    					 </div>';
	    					 
	    if (contingutCustodiat) {
	    	viewerContent += signantsViewerContent;
	    }
	    resumViewer.prepend(viewerContent);
	    if (contingutCustodiat) {
	    	getDetallsSignants($("#detallSignantsPreview"), documentId, true);
	    }
	    
	
	    // Recuperar i mostrar document al visor
		var urlDescarrega = "<c:url value="/contingut/${contingut.id}/document/"/>" + documentId + "/returnFitxer";
		$('#container').attr('src', '');
		$('#container').addClass('rmodal_loading');
		showDocument(urlDescarrega);
		
		$([document.documentElement, document.body]).animate({
	        scrollTop: $("#resum-viewer").offset().top - 110
	    }, 500);
	}
	
	function showDocument(arxiuUrl) {
		// Fa la petició a la url de l'arxiu
		$.ajax({
			type: 'GET',
			url: arxiuUrl,
			responseType: 'arraybuffer',
			success: function(json) {
				
				if (json.error) {
					$('#container').removeClass('rmodal_loading');
					$("#resum-viewer .viewer-padding:last").before('<div class="viewer-padding"><div class="alert alert-danger"><spring:message code="contingut.previsualitzacio.error"/>: ' + json.errorMsg + '</div></div>');
				} else if (json.warning) {
					$('#container').removeClass('rmodal_loading');
					$("#resum-viewer .viewer-padding:last").before('<div class="viewer-padding"><div class="alert alert-warning"><spring:message code="contingut.previsualitzacio.warning"/>' + '</div></div>');
				} else {
					response = json.data;
					var blob = base64toBlob(response.contingut, response.contentType);
		            var file = new File([blob], response.contentType, {type: response.contentType});
		            link = URL.createObjectURL(file);
		            
		            var viewerUrl = "<c:url value="/webjars/pdf-js/2.5.207/web/viewer.html"/>" + '?file=' + encodeURIComponent(link);
				    $('#container').removeClass('rmodal_loading');
				    $('#container').attr('src', viewerUrl);
				}
			    
			},
			error: function(xhr, ajaxOptions, thrownError) {
				$('#container').removeClass('rmodal_loading');
				alert(thrownError);
			}
		});
	}
	// Amagar visor
	function closeViewer() {
		$('#resum-viewer').slideUp(500, function(){
		});
	}


	function getEnviamentsDocument(document) {
		var content;
		var documentId = $(document).attr('id');
		if (documentId != undefined) {
			var enviamentsUrl = '<c:url value="/document/' + documentId + '/enviament/datatable"/>';
			$.ajax({
				type: 'GET',
		        url: enviamentsUrl,
		        async: false,
		        success: function(data){
		        	if (data && data.length > 0) {
		        	content =  "<table data-toggle='datatable' class='table table-bordered table-striped' style='width:100%'>";
		            content += "<thead>";
		        	content += "<tr>";
		        	content += "<th> <spring:message code='contingut.enviament.columna.tipus'/> </th>";
		        	content += "<th> <spring:message code='contingut.enviament.columna.data'/> </th>";
		        	content += "<th> <spring:message code='contingut.enviament.columna.estat'/> </th>";
		        	content += "</tr>";
		        	content += "</thead>";
			            $.each(data, function(i, val) {
			            	content += "<tbody>";
			             	content += "<tr>";
			             	content += "<td width='25%'>";
			             	if (val.tipus == "NOTIFICACIO") {
			             		content += "<spring:message code='contingut.enviament.notificacio.elec'/>";
			             	} else if (val.tipus == "COMUNICACIO") {
			             		content += "<spring:message code='contingut.enviament.comunicacio'/>";
			             	}
			             	content += "</td>";
			             	content += "<td width='20%'>" + new Date (val.createdDate).toLocaleString() + "</td>";
			             	content += "<td width='10%'>";
			             	if (val.notificacioEstat == 'PENDENT') {
			             		content += "<span class='label label-warning'><span class='fa fa-clock-o'></span> ";
			             		content += "<spring:message code='notificacio.notificacioEstat.enum.PENDENT'/></span> ";
			             		if (val.error) {
			             			content += "<span class='fa fa-warning text-danger' title='<spring:message code='contingut.enviament.error'/>'></span>";
			             		}
			             	} else if (val.notificacioEstat == 'ENVIADA') {
			             		content += "<span class='label label-info'><span class='fa fa-envelope-o'></span> ";
			             		content += "<spring:message code='notificacio.notificacioEstat.enum.ENVIADA'/></span>";
			             		if (val.error) {
			             			content += "<span class='fa fa-warning text-danger' title='<spring:message code='contingut.enviament.error'/>'></span>";
			             		}
			             	} else if (val.notificacioEstat == 'REGISTRADA') {
			             		content += "<span class='label label-success'><span class='fa fa-check'></span> ";
			             		content += "<spring:message code='notificacio.notificacioEstat.enum.REGISTRADA'/></span>";
			             	} else if (val.notificacioEstat == 'FINALITZADA') {
			             		content += "<span class='label label-success'><span class='fa fa-check'></span> ";
			             		content += "<spring:message code='notificacio.notificacioEstat.enum.FINALITZADA'/></span>";
			             		if (val.error) {
			             			content += "<span class='fa fa-warning text-danger' title='<spring:message code='contingut.enviament.error'/>'></span>";
			             		}
			             	} else if (val.notificacioEstat == 'PROCESSADA') {
			             		content += "<span class='label label-success'><span class='fa fa-check'></span> ";
			             		content += "<spring:message code='notificacio.notificacioEstat.enum.PROCESSADA'/></span>";
			             		if (val.error) {
			             			content += "<span class='fa fa-warning text-danger' title='<spring:message code='contingut.enviament.error'/>'></span>";
			             		}
			             		content += "<p class='estat_" + val.id +  "' style='display:inline'></p>";
			             		returnEnviamentsStatusDiv(val.id);
			             	}
			             	content += "</td>";
			             	content += "</tr>";
			             	content += "</tbody>";
			            });
		            content += "</table>";
		        	}
		        }
		    });	
		    return content;
		}
	}

	function enableDisableMultipleButtons(docsIdx) {
		var isTotPdfFirmat = true;
		var isTotPdf = true;
		var isTotDocAdjuntGuardatEnArxiu = true;
	
		var comprovacioUrl = '<c:url value="/contingut/${contingut.id}/comprovarContingut"/>';
		$('#loading').removeClass('hidden');
		//lista
		var tableDocuments = document.getElementById('table-documents');
		$(tableDocuments).addClass("disabled");
		//icones
		var gridDocuments = document.getElementById('grid-documents');
		$(gridDocuments).addClass("disabled");
	
			if (docsIdx != undefined && tableDocuments != undefined) {
				var inputs = tableDocuments.querySelectorAll('tbody>tr>td>input');
				inputs.forEach(function(input) {
					var documentId = parseInt(input.id);
					if (docsIdx.includes(documentId)) {
						var isFirmatCurrentDocument = $(input.closest('tr')).hasClass('firmat');
						var isPdfCurrentDocument = $(input.closest('tr')).hasClass('isPdf');
						var isDocAdjuntPendentGuardarArxiu = $(input.closest('tr')).hasClass('docAdjuntPendentGuardarArxiu');
						if (isDocAdjuntPendentGuardarArxiu) {
							isTotDocAdjuntGuardatEnArxiu = false;
						}
						
						if (!isFirmatCurrentDocument) {
							isTotPdfFirmat = false;
							return false;
						}
						if (!isPdfCurrentDocument) {
							isTotPdf = false;
						}
					}
				});
	
			} else if (docsIdx != undefined && gridDocuments != undefined) {
				var list = gridDocuments.querySelectorAll('li');
				list.forEach(function(child) {
					var childId = $(child).attr('id');
					var documentId = parseInt(childId);
					if (docsIdx.includes(documentId)) {
						var isFirmatCurrentDocument = $(child).hasClass('firmat');
						var isPdfCurrentDocument = $(child).hasClass('isPdf');
						var isDocAdjuntPendentGuardarArxiu = $(child).hasClass('docAdjuntPendentGuardarArxiu');
						if (isDocAdjuntPendentGuardarArxiu) {
							isTotDocAdjuntGuardatEnArxiu = false;
						}
						
						if (!isFirmatCurrentDocument) {
							isTotPdfFirmat = false;
							return false;
						}
						if (!isPdfCurrentDocument) {
							isTotPdf = false;
						}
					}
				});
			}		
	
		
		if (isTotPdfFirmat && isTotPdf) {
			$('.nomaximized').addClass('hidden'); //zip
			$('.maximized').removeClass('hidden'); //concatenació
			$('#notificar-mult').removeClass("disabled");
			$('#definitiu-mult').addClass("disabled");
		} else if (isTotPdfFirmat && !isTotPdf) {
			$('.nomaximized').removeClass('hidden'); //zip
			$('.maximized').addClass('hidden'); //concatenació
			$('#notificar-mult').removeClass("disabled");
			$('#definitiu-mult').addClass("disabled");
		} else if (!isTotDocAdjuntGuardatEnArxiu) {
			$('#definitiu-mult').addClass("disabled");
			$('#tipusdocumental-mult').addClass("disabled");
		} else {
			$('#notificar-mult').addClass("disabled");
			$('#definitiu-mult').removeClass("disabled");
			$('#tipusdocumental-mult').removeClass("disabled");
		}
		
		if (docsIdx != undefined && docsIdx.length > 0) {
			$('#descarregar-mult').removeClass("disabled");
			$('#moure-mult').removeClass("disabled");
			$('#tipusdocumental-mult').removeClass("disabled");
		} else {
			$('#descarregar-mult').addClass("disabled");
			$('#notificar-mult').addClass("disabled");
			$('#moure-mult').addClass("disabled");
			$('#tipusdocumental-mult').addClass("disabled");
		}
		$('#grid-documents ').removeClass("disabled");
		$('#table-documents').removeClass("disabled");
		$('#loading').addClass('hidden');
	}
	
	function selectAll(docsIdx) {
		var multipleUrl = '<c:url value="/contingut/${contingut.id}/select"/>';
		$.get(
				multipleUrl, 
				{docsIdx: docsIdx},
				function(data) {
					$(".seleccioCount").html(data);
				}
		);
	}
	
	function deselectAll() {
		clearSelection();
	}

	function clearSelection() {
		var multipleUrl = '<c:url value="/contingut/${contingut.id}/inicialitzar/seleccio"/>';
		$.get(
				multipleUrl,
				function(data) {
					$(".seleccioCount").html(data);
				}
		);
	}



	function getIdsSelectedFromTable() {
		let idsSelected = [];
		$('#table-documents tbody .checkbox').each(function() {
			if ($(this).prop('checked')) {
				let id = $(this).closest('tr').attr('id');
				idsSelected.push(parseInt(id));
			}
		});
		return idsSelected;
	}
	



	



	

	function showTipusDocumentals() {
		var content = '<div> \
							<select id="selectTipusMassiu" class="select-tipus-massiu"> \
								<option value=""><spring:message code="contingut.document.form.camp.nti.cap"/></option> \
									<c:forEach items="${metaDocumentsLeft}" var="metaDocument"> \
										<option id="${metaDocument.id}"> \
										${fn:escapeXml(metaDocument.nom)} \
										</option> \
									</c:forEach> \
							</select> \
					   <div>';
		return content;
	}


	function returnEnviamentsStatusDiv(notificacioId) {
	    var content = "";
	    var getUrl = "<c:url value="/expedient/${contingut.id}"/>" + "/enviaments/" + notificacioId;

	    $.getJSON({
	        url: getUrl,
	        success: (notificacio) => {
				var enviaments = notificacio.documentEnviamentInteressats;
	            for (i = 0; i < enviaments.length; i++) {
	                content += (enviaments[i].enviamentDatatEstat) ? notificacioEnviamentEstats[enviaments[i].enviamentDatatEstat] + ',' : '';
	            }
	            if (content !== undefined && content != '') {
	                content = "("+content.replace(/,\s*$/, "")+")";
	            }
	            $('.estat_' + notificacioId).html("");
	            $('.estat_' + notificacioId).append(content);
	        },
	        error: function(data){
	        	console.log("No s'han pogut recuperar els enviaments de la notificació: " + notificacioId);
	        }
	    })
	}
	var myHelpers = {
	recuperarEstatEnviament: returnEnviamentsStatusDiv,
	};
	$.views.helpers(myHelpers);
	
</c:if>//------------------------- if contingut is not document END ----------------------------------




function getDetallsSignants(idTbody, contingutId, header) {

	idTbody.html("");
	idTbody.append('<tr class="datatable-dades-carregant"><td colspan="7" style="margin-top: 2em; text-align: center"><img src="<c:url value="/img/loading.gif"/>"/></td></tr>');
	$.get("<c:url value="/contingut/document/"/>" + contingutId + "/mostraDetallSignants", function(json){
		if (json.error) {
			idTbody.html('<tr><td colspan="2" style="width:100%"><div class="alert alert-danger"><button type="button" class="close-alertes" data-dismiss="alert" aria-hidden="true"><span class="fa fa-times"></span></button><spring:message code="contingut.document.info.firma.error"/>: ' + json.errorMsg + '</div></td></tr>');
		} else {
			idTbody.html("");
			if(json.data != null && json.data.length > 0){
				json.data.forEach(function(firma){
					if(firma != null){
						var firmaDataStr = "";
						if(firma.responsableNom == null){
							firma.responsableNom = "";
						}
						if(firma.responsableNif == null){
							firma.responsableNif = "";
						}
						if(firma.data != null){
							firmaDataStr = new Date(firma.data);
						}
						if(firma.emissorCertificat == null){
							firma.emissorCertificat = "";
						}
						if (header){
							idTbody.append('<tr><th style="padding-bottom: 2px;"><strong>'
									+ '<u><spring:message code="contingut.document.info.firma"/></u>'
									+ "</strong></th><tr>");
						} 
						idTbody.append(
							  "<tr><th><strong>"
							+ '<spring:message code="contingut.document.camp.firma.responsable.nom"/>'
							+ "</strong></th><th>"
							+ firma.responsableNom
							+ "</th></tr><tr><td><strong>"
							+ '<spring:message code="contingut.document.camp.firma.responsable.nif"/>'
							+ "</strong></td><td>"
							+ firma.responsableNif
							+ "</td></tr><tr><td><strong>"
							+ '<spring:message code="contingut.document.camp.firma.responsable.data"/>'
							+ "</strong></td><td>"
							+ (firmaDataStr != "" ? firmaDataStr.toLocaleString() : "")
							+ "</td></tr><tr><td><strong>"
							+ '<spring:message code="contingut.document.camp.firma.emissor.certificat"/>'
							+ "</strong></td><td>"
							+ firma.emissorCertificat
							+ "</td></tr>");
					}
				})
			}
		}
		webutilRefreshMissatges();
	});
}	

</script>



<c:choose>
	<%--------------- WHEN CONTINGUT IS DOCUMENT (SHOWS DOCUMENT DETAILS) ---------------%>
	<c:when test="${contingut.document}">
		<c:if test="${not contingut.validacioFirmaCorrecte}">
			<div class="alert alert-danger alert-dismissible">
				<button type="button" class="close close-alertes" data-dismiss="alert" aria-hidden="true"><span class="fa fa-times"></span></button>
				<spring:message code="contingut.icona.estat.invalid.origen.form" arguments="${contingut.validacioFirmaErrorMsg}"/>
			</div>
		</c:if>
		<table class="table table-bordered">
			<tbody>
				<c:choose>
					<c:when test="${contingut.documentTipus == 'DIGITAL' || contingut.documentTipus == 'IMPORTAT'}">
						<tr>
							<td><strong><spring:message code="contingut.document.camp.arxiu"/></strong></td>
							<td>${contingut.fitxerNom}</td>
						</tr>
						<c:if test="${!empty contingut.descripcio}">
							<tr> 
								<td><strong><spring:message code="contingut.document.camp.descripcio"/></strong></td>
								<td>${contingut.descripcio}</td>
							</tr>
						</c:if>											
						<tr>
							<td><strong><spring:message code="contingut.document.camp.content.type"/></strong></td>
							<td>${contingut.fitxerContentType}</td>
						</tr>
					</c:when>
					<c:otherwise>
						<tr>
							<td><strong><spring:message code="contingut.document.camp.ubicacio"/></strong></td>
							<td>${contingut.ubicacio}</td>
						</tr>
					</c:otherwise>
				</c:choose>
				<c:if test="${not empty contingut.metaNode}">
					<tr>
						<td><strong><spring:message code="contingut.info.meta.document"/></strong></td>
						<td>${contingut.metaNode.nom}</td>
					</tr>
				</c:if>
				<tr>
					<td><strong><spring:message code="contingut.info.data"/></strong></td>
					<td><fmt:formatDate value="${contingut.data}" pattern="dd/MM/yyyy"/></td>
				</tr>
				<tr>
					<td><strong><spring:message code="contingut.info.estat"/></strong></td>
					<td><spring:message code="document.estat.enum.${contingut.estat}"/></td>
				</tr>									
				<tr>
					<td><strong><spring:message code="contingut.info.nti.data.captura"/></strong></td>
					<td><fmt:formatDate value="${contingut.dataCaptura}" pattern="dd/MM/yyyy"/></td>
				</tr>
				<tr>
					<td><strong><spring:message code="contingut.info.nti.origen"/></strong></td>
					<td><spring:message code="document.nti.origen.enum.${contingut.ntiOrigen}"/></td>
				</tr>
				<tr>
					<td><strong><spring:message code="contingut.info.nti.tipus.doc"/></strong></td>
					<c:choose>
						<c:when test="${not empty contingut.ntiTipoDocumentalNom}">
							<td>${contingut.ntiTipoDocumentalNom}</td>
						</c:when>
						<c:otherwise>
							<td><spring:message code="document.nti.tipdoc.enum.${contingut.ntiTipoDocumental}"/></td>
						</c:otherwise>
					</c:choose>
				</tr>																		
				<c:if test="${not empty contingut.ntiIdDocumentoOrigen}">
					<td><strong><spring:message code="contingut.info.nti.doc.origen.id"/></strong></td>
					<td>${contingut.ntiIdDocumentoOrigen}</td>
				</c:if>
				<c:if test="${!empty contingut.ntiCsv}">		
					<tr>
						<td><strong><spring:message code="contingut.document.camp.firma.csv" /></strong></td>
						<td>${contingut.ntiCsv}</td>
					</tr>	
				</c:if>										
			</tbody>
		</table>
		<c:if test="${contingut.custodiat}">
			<div class="panel panel-default">
				<div class="panel-heading">
					<h3 class="panel-title" style="height: 36px;">
						<span class="fa fa-bookmark" title="Document firmat" style="margin-top: 10px;"></span>
						<spring:message code="contingut.document.info.firma"/>
						<button id="mostraDetallSignants" class="btn btn-default pull-right"><span class="fa fa-info-circle"></span>&nbsp;<spring:message code="comu.boto.mostrar.info.signants"/></button>
					</h3>
				</div>
				<table class="table table-bordered">
					<tbody>
						<tr>
							<td><strong><spring:message code="contingut.document.camp.firma.tipus"/></strong></td>
							<td>
								<c:if test="${not empty contingut.ntiTipoFirma}">
									<spring:message code="document.nti.tipfir.enum.${contingut.ntiTipoFirma}"/>
								</c:if>
							</td>
						</tr>
						<tr>
							<td><strong><spring:message code="contingut.document.camp.firma.csv"/></strong></td>
							<td>${contingut.ntiCsv}</td>
						</tr>
						<tr>
							<td><strong><spring:message code="contingut.document.camp.firma.csv.regulacio"/></strong></td>
							<td>${contingut.ntiCsvRegulacion}</td>
						</tr>
					</tbody>
					<tbody id="detallSignants">
					</tbody>
				</table>
			</div>
		</c:if>
	</c:when>
	<%---------------------------- WHEN CONTINGUT IS EXPEDIENT OR CARPETA (SHOWS ARBRE / GRID OF CONTINGUTS) -------------------------%>
	<c:otherwise>
	
		<%----------- TASCA BUTTONS ----------%>
		<c:if test="${isTasca}">
			<div id="tascaBtn" style="float: right">
				<c:if test="${tascaEstat=='INICIADA'}">
					<a href="<c:url value="/usuariTasca/${tascaId}/finalitzar"/>" class="btn btn-default" style="float: right;" data-confirm="<spring:message code="expedient.tasca.finalitzar"/>"><span class="fa fa-check"></span>&nbsp;&nbsp;<spring:message code="comu.boto.finalitzarTasca" /></a>
				</c:if>
				<c:if test="${tascaEstat=='PENDENT'}">
					<a href="<c:url value="/usuariTasca/${tascaId}/iniciar?redirectATasca=true"/>" class="btn btn-default" style="float: right;"><span class="fa fa-play"></span>&nbsp;&nbsp;<spring:message code="comu.boto.iniciar"/></a>
				</c:if>									
				<a href="<c:url value="/usuariTasca"/>" class="btn btn-default pull-right" style="float: right; margin-right: 3px;"><span class="fa fa-arrow-left"></span>&nbsp;<spring:message code="comu.boto.tornar"/></a>
			</div>
		</c:if>	
								
		<%----------------------------------- ACCION BUTTONS --------------------------------%>
		<div class="text-right" id="contingut-botons">
		
			<%---- path ----%>
			<c:if test="${contingut.expedient or contingut.carpeta}">
				<rip:blocContenidorPath contingut="${contingut}"/>
			</c:if>
			
			<%---- expand/collapse tree ----%>
			<c:if test="${!vistaIcones}">
				<div id="expandCollapseButtons" style="float: left;display: inline-block;">
					<button class="btn btn-default" onclick="$('#table-documents').expandAll();"><span class="fa fa-caret-square-o-down"></span> <spring:message code="unitat.arbre.expandeix"/></button> 
					<button class="btn btn-default" onclick="$('#table-documents').collapseAll();"><span class="fa fa-caret-square-o-up"></span> <spring:message code="unitat.arbre.contreu"/></button> 
				</div>
			</c:if>
			<c:if test="${isTasca}">
				<a href="<c:url value="/expedientTasca/${tascaId}/comentaris"/>" data-toggle="modal" data-refresh-tancar="true" class="btn btn-default pull-left"><span class="fa fa-lg fa-comments"></span>&nbsp;<span class="badge">${tasca.numComentaris}</span></a>
			</c:if>
			
			
			
			<c:if test="${vistaIcones}">
				<%---- Habilitar selecció múltiple ----%>
				<div class="btn-group">
					<div data-toggle="tooltip" title="<spring:message code="contingut.boto.menu.seleccio.multiple.habilitar"/>" id="habilitar-mult" class="btn-group btn btn-default">
						<span class="glyphicon glyphicon-th"></span>
					</div>
				</div>
				<%---- Seleccionar tots ----%>
				<div class="btn-group">
					<div data-toggle="tooltip" title="<spring:message code="contingut.boto.menu.seleccio.multiple.seleccio"/>" id="checkItAll" class="btn-group btn btn-default disabled">
						<span class="fa fa-check"></span>
					</div>
				</div>
			</c:if>
			<%---- Button descarregar mult ----%>
			<div class="btn-group">
				<div data-toggle="tooltip" title="<spring:message code="contingut.boto.menu.seleccio.multiple.descarregar"/>" id="descarregar-mult" class="btn-group">
					<a href="<c:url value="/contingut/${contingut.id}/descarregarMultiples"/>" class="btn btn-default con-mult">
						<span class="fa fa-download"></span>
						
						<span class="badge seleccioCount">${fn:length(seleccio)}</span>
					</a>
				</div>
			</div>
			<c:if test="${(expedientAgafatPerUsuariActual or contingut.admin) and expedientObert}">
				<c:set var="definitiuConfirmacioMsg"><spring:message code="contingut.confirmacio.definitiu.multiple"/></c:set>
				
				
				
				<%---- Button notificar mult ----%>
				<div class="btn-group">
					<div data-toggle="tooltip" title="<spring:message code="contingut.boto.menu.seleccio.multiple.concatenar"/>" id="notificar-mult" class="btn-group">
						<a href="<c:url value="/contingut/${contingut.id}/${isNotificacioMultipleGenerarDocumentVisible ? 'chooseTipusDocument' : 'concatenarOGenerarZip'}"/>" class="btn btn-default maximized hidden" data-toggle="modal" data-maximized="true">
							<span class="fa fa-envelope-o"></span><span class="badge seleccioCount">${fn:length(seleccio)}</span>
						</a> 
						<a href="<c:url value="/contingut/${contingut.id}/${isNotificacioMultipleGenerarDocumentVisible ? 'chooseTipusDocument' : 'concatenarOGenerarZip'}"/>" class="btn btn-default nomaximized" data-toggle="modal" data-missatge-loading="<spring:message code="concatenacio.zip.modal.missatge"/>">
							<span class="fa fa-envelope-o"></span><span class="badge seleccioCount">${fn:length(seleccio)}</span>
						</a>
					</div>
					<c:if test="${convertirDefinitiu}">
						<div data-toggle="tooltip" title="<spring:message code="massiu.estat.definitiu"/>" id="definitiu-mult" class="btn-group">
							<a href="<c:url value="/contingut/${contingut.id}/defintiu"/>" class="btn btn-default con-mult hidden" data-confirm="${definitiuConfirmacioMsg}">
								<span class="fa fa-check-square"></span>
								
								<span class="badge seleccioCount">${fn:length(seleccio)}</span>
							</a> 
							<a href="<c:url value="/contingut/${contingut.id}/defintiu"/>" class="btn btn-default con-mult" data-confirm="${definitiuConfirmacioMsg}">
								<span class="fa fa-check-square"></span>
								
								<span class="badge seleccioCount">${fn:length(seleccio)}</span>
							</a>
						</div>
					</c:if>
					<div data-toggle="tooltip" title="<spring:message code="massiu.moure.documents"/>" class="btn-group" id="moure-mult">
						<a href="<c:url value="/contingut/${contingut.id}/moure"/>" data-toggle="modal" class="btn btn-default con-mult">
							<span class="fa fa-arrows"></span>
							
							<span class="badge seleccioCount">${fn:length(seleccio)}</span>
						</a>
					</div>
					<div data-toggle="popover" class="btn btn-default" id="tipusdocumental-mult">
						<div data-toggle="tooltip" title="<spring:message code="massiu.canvi.tipus.document"/>">
							<span class="fa fa-edit"></span>
							<span class="badge seleccioCount">${fn:length(seleccio)}</span>
						</div>
					</div>
				</div>
				<%---- Button descarregar zip mult 
				<div class="btn-group">
					<div data-toggle="tooltip" title="<spring:message code="contingut.boto.menu.seleccio.multiple.concatenarzip"/>" id="notificar-mult" class="btn-group">
						<a href="<c:url value="/contingut/${contingut.id}/generarZip/new"/>" class="btn btn-default zip-mult" data-toggle="modal">
							<span class="glyphicon glyphicon-compressed"></span>
							<span class="badge seleccioCount">${fn:length(seleccio)}</span>
						</a>
					</div>
				</div>
				----%>
			</c:if>
			<div class="btn-group" id="vistes">
				<c:if test="${!isTasca}">
					<%---- Button treetable per estats  ----%>
					<c:set var="llistatVistaUrl"><c:url value="/contingut/${isTasca}/${isTasca ? tascaId : expedientId}/canviVista/TREETABLE_PER_ESTAT"/></c:set>
					<a href="${llistatVistaUrl}" title="<spring:message code="contingut.boto.menu.vista.treetable.estat"/>" id="vistaTreetablePerTipusDocuments" class="btn btn-default ${vistaTreetablePerEstats ? 'active' : ''}" draggable="false">
						<span class="fa fa-ellipsis-v" style="padding-left: 5px; padding-right: 5px;"></span>
					</a>	
				</c:if>		
				<%---- Button treetable per tipus de documents  ----%>
				<c:set var="llistatVistaUrl"><c:url value="/contingut/${isTasca}/${isTasca ? tascaId : expedientId}/canviVista/TREETABLE_PER_TIPUS_DOCUMENT"/></c:set>
				<a href="${llistatVistaUrl}" title="<spring:message code="contingut.boto.menu.vista.treetable.tipus.document"/>" id="vistaTreetablePerTipusDocuments" class="btn btn-default ${vistaTreetablePerTipusDocuments ? 'active' : ''}" draggable="false">
					<span class="fa fa-bars"></span>
				</a>
				<%---- Button treetable per carpetes  ----%>
				<c:set var="llistatVistaUrl"><c:url value="/contingut/${isTasca}/${isTasca ? tascaId : contingut.id}/canviVista/TREETABLE_PER_CARPETA"/></c:set>
				<a href="${llistatVistaUrl}" title="<spring:message code="contingut.boto.menu.vista.treetable.carpeta"/>" id="vistaTreetablePerCarpetes" class="btn btn-default ${vistaLlistat ? 'active' : ''}" draggable="false">
					<span class="fa fa-th-list"></span>
				</a>
				<%---- Button grid ----%>
				<c:set var="iconesVistaUrl"><c:url value="/contingut/${isTasca}/${isTasca ? tascaId : contingut.id}/canviVista/GRID"/></c:set>					
				<a href="${iconesVistaUrl}" title="<spring:message code="contingut.boto.menu.vista.grid"/>" id="vistaGrid" class="btn btn-default ${vistaIcones ? 'active' : ''}" draggable="false"> 
					<span class="fa fa-th" ></span>
				</a>	
				<script>
					var docsIdx = new Array();
				</script>							
			</div>
			
			<c:if test="${potModificar && (!contingut.carpeta || isCreacioCarpetesActiva)}">
				<div id="botons-crear-contingut" class="btn-group">
					<%---- Crear contingut ----%>
					<button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown"><span class="fa fa-plus"></span>&nbsp;<spring:message code="contingut.boto.crear.contingut"/>&nbsp;<span class="caret"></span></button>
					<ul class="dropdown-menu text-left" role="menu">
						<c:if test="${contingut.crearExpedients and not empty metaExpedients}">
							<li>
							<a href="<c:url value="/contingut/${contingut.id}/expedient/new"/>" data-toggle="modal" data-refresh-pagina="true">
								<span class="fa ${iconaExpedientTancat}"></span>&nbsp;<spring:message code="contingut.boto.crear.expedient"/>...
							</a>
							</li>
						</c:if>
						<%---- Document... ----%>
						<c:choose>
							<c:when test="${isTasca}">
								<li><a id="document-new" href="<c:url value="/usuariTasca/${tascaId}/pare/${contingut.id}/document/new"/>" data-toggle="modal" data-refresh-pagina="true"><span class="fa ${iconaDocument}"></span>&nbsp;&nbsp;<spring:message code="contingut.boto.crear.document"/>...</a></li>
							</c:when>
							<c:otherwise>
								<li>
								<c:choose>
									<c:when test="${empty metaDocumentsLeft}">
										<a href="#" id="document-new-empty-metadocuments">
											<span class="fa ${iconaDocument}"></span>&nbsp;&nbsp;<spring:message code="contingut.boto.crear.document"/>...
										</a>
											</c:when>
									<c:otherwise>
										<a id="document-new" href="<c:url value="/contingut/${contingut.id}/document/new"/>" data-toggle="modal" data-refresh-pagina="true">
											<span class="fa ${iconaDocument}"></span>&nbsp;&nbsp;<spring:message code="contingut.boto.crear.document"/>...
										</a>
									</c:otherwise>
								</c:choose>
								</li>
								<c:if test="${not empty metaDocumentsPinbalLeft}">
									<li>
										<a id="pinbal-new" href="<c:url value="/contingut/${contingut.id}/pinbal/new"/>" data-toggle="modal" data-refresh-pagina="true">
											<span class="fa ${iconaDocument}"></span>&nbsp;&nbsp;<spring:message code="contingut.boto.crear.pinbal"/>...
										</a>
									</li>
								</c:if>
							</c:otherwise>
						</c:choose>
						<c:if test="${!isTasca}">
							<%---- Carpeta... ----%>
							<c:if test="${isCreacioCarpetesActiva}">
								<li><a href="<c:url value="/contingut/${contingut.id}/carpeta/new"/>" data-toggle="modal" data-refresh-pagina="true"><span class="fa ${iconaCarpeta}"></span>&nbsp;&nbsp;<spring:message code="contingut.boto.crear.carpeta"/>...</a></li>
							</c:if>
							<c:if test="${isMostrarImportacio}">
								<li><a href="<c:url value="/contingut/${contingut.id}/importacio/new"/>" data-toggle="modal" data-refresh-pagina="true"><span class="fa ${iconaImportacio}"></span>&nbsp;&nbsp;<spring:message code="contingut.boto.crear.importacio"/>...</a></li>
							</c:if>
							<c:if test="${isImportacioRelacionatsActiva}">
								<li><a href="<c:url value="/expedient/${expedientId}/relacionats/${contingut.id}/list"/>" data-toggle="modal" data-refresh-pagina="true" data-maximized="true"><span class="fa fa-link"></span>&nbsp;&nbsp;<spring:message code="contingut.boto.crear.expedient.relacionat"/>...</a></li>
							</c:if>	
						</c:if>											
					</ul>
				</div>
			</c:if>
		</div>
		
		<%---- TABLE/GRID OF CONTINGUTS ----%>
		<div id="loading">
			<img src="<c:url value="/img/loading.gif"/>"/>
		</div>
		

		<c:choose>
			<c:when test="${!isMostrarCarpetesPerAnotacions}"><c:set var="fills" value="${contingut.fillsFlat}"/></c:when>
			<c:otherwise><c:set var="fills" value="${contingut.fillsHierarchical}"/></c:otherwise>
		</c:choose>
		
		<c:choose>
			<%--########################################################### VIEW TREETABLE PER ESTATS ###########################################################--%>
			<c:when test="${vistaTreetablePerEstats}">
			
				<c:choose>
					<c:when test="${not empty contingut.mapPerEstat}">
						<%--------------------- TREETABLE  -------------------%>
						<table class="table table-striped table-bordered table-hover" id="table-documents">
							<thead>
								<tr>
									<th><input type="checkbox" id="checkItAll" autocomplete="off"/></th>
									<th><spring:message code="contingut.info.nom"/></th>
									<th><spring:message code="contingut.info.descirpcio"/></th>
									<th><spring:message code="contingut.info.ruta"/></th>
									<th><spring:message code="contingut.info.createl"/></th>
									<th><spring:message code="contingut.info.creatper"/></th>
									<th width="5%">&nbsp;</th>
									<c:if test="${expedientPareAgafatPerUsuariActual && isOrdenacioPermesa}">
										<th width="1%">&nbsp;</th>
									</c:if>
								</tr>
							</thead>
							<tbody>
								<c:forEach items="${contingut.mapPerEstat}" var="entry">
									<%------------------------------- Tipus de document ------------------------%>
									<tr id="${entry.key.id}"
										data-node="treetable-${entry.key.id}"
										data-pnode="treetable-${contingut.id}">
										<%------------ checkbox ----------%>
										<td></td>
										<%------------ Nom ----------%>
										<td><b>${entry.key.nom}</b> 
										
											<span class="badge">${fn:length(entry.value)}</span>
											

										</td>
										<%------------ Descripció ----------%>
										<td></td>
										<%------------ Tipus -----------%>
										<td width="25%"></td>
										<%------------ Creat el -----------%>
										<td></td>
										<%------------ Creat per -----------%>
										<td></td>
										<%------------ Accions -----------%>
										<td></td>
										<%------------ sort ----------%>
										<c:if test="${expedientPareAgafatPerUsuariActual && isOrdenacioPermesa}">
											<td></td>
										</c:if>
									</tr>								
					   				
					   				 <%------------------------------- Documents ----------------------------%>
					   				 <c:choose>
					   				 	<c:when test="${!empty entry.value}">
						   				     <c:forEach items="${entry.value}" var="fill">
						   				     
												<c:if test="${fill.carpeta or (fill.document && fill.documentTipus != 'VIRTUAL')}">
												
													<c:set var="firmat" value="true"/>
													<c:if test="${fill.document}">
														<c:if test="${(fill.estat != 'FIRMAT' || fill.estat == 'CUSTODIAT') && (fill.estat == 'FIRMAT' || fill.estat != 'CUSTODIAT') && fill.estat != 'DEFINITIU'}"><!-- TODO: revise, condition never true -->
															<c:set var="firmat" value="false"/> 
														</c:if>
													</c:if>
											
													<tr id="${fill.id}"
														class="isDocument<c:if test="${fill.document && firmat}"> firmat</c:if><c:if test="${fill.document && fill.pdf}"> isPdf</c:if> <c:if test="${fill.document && fill.arxiuUuid == null}"> docAdjuntPendentGuardarArxiu</c:if>"
														data-expedient-id="${expedientId}" 
														data-node="treetable-${fill.id}"
														data-pnode="treetable-${entry.key.id}">
														
											
														<%------------ checkbox ----------%>
														<td><c:if test="${fill.document}">
															<input type="checkbox" class="checkbox" autocomplete="off" />
														</c:if></td>
											
														<%------------ Nom ----------%>
														<td>
															<rip:blocIconaContingut contingut="${fill}" /> 
															<rip:blocContingutNomAmbIcons contingut="${fill}" /> 
														</td>
											
														<%------------ Descripció ----------%>
														<td><c:if test="${fill.document}">
															&nbsp;${fill.descripcio}
														</c:if></td>
											
														<%------------ Ruta -----------%>
														<td width="25%">
															<c:forEach var="contingutPath" items="${fill.path}" varStatus="status">
																<c:if test="${!status.first}">/${contingutPath.nom}</c:if>
															</c:forEach>
															<c:if test="${fn:length(fill.path) == 1}">/</c:if>
														</td>
											
														<%------------ Creat el -----------%>
														<td><fmt:formatDate value="${fill.createdDate}" pattern="dd/MM/yyyy HH:mm" /></td>
											
														<%------------ Creat per -----------%>
														<td>${fill.createdBy.codiAndNom}</td>
											
														<%------------ Accions -----------%>
														<td><rip:blocContingutAccions className="botons-accions-element"
																modeLlistat="true" contingut="${fill}" nodeco="${nodeco}" contingutNavigationId="${contingut.id}"/></td>
											
														<%------------ sort ----------%>
														<c:if test="${expedientPareAgafatPerUsuariActual && isOrdenacioPermesa}">
															<td class="ordre-col" title="<spring:message code="contingut.sort.titol"/>"><span
																class="fa fa-sort"></span></td>
														</c:if>
													</tr>
												</c:if>
						   				     
					    					</c:forEach>
					   					 </c:when>
					   					 <c:otherwise>
					   						<tr	data-node="treetable-0"
												data-pnode="treetable-${entry.key.id}">
												<td></td><td colspan="6"><spring:message code="contingut.info.table.tree.estat.no.documents"/></td>
											</tr>
					   					 </c:otherwise>
					   				 </c:choose>
								</c:forEach>							
							

							
							</tbody>
						</table>			
					</c:when>
					<c:otherwise>
						<%--------------------- EMPTY TREETABLE -------------------%>
						<table class="table table-striped table-bordered table-hover" id="table-documents">
								<thead>
									<tr>
										<th></th>
										<th><spring:message code="contingut.info.nom"/></th>
										<th><spring:message code="contingut.info.descirpcio"/></th>
										<th><spring:message code="contingut.info.tipus" /></th>
										<th><spring:message code="contingut.info.createl" /></th>
										<th><spring:message code="contingut.info.creatper" /></th>
										<th width="10%">&nbsp;</th>
									</tr>
								</thead>
								<tbody>
									<tr class="odd">
										<td colspan="9" valign="top">
											<h1 style="opacity: .1; text-align: center;">
												<rip:blocIconaContingut contingut="${fill}" tamanyEnorme="false" />
												<strong><spring:message code="contingut.sense.contingut" /></strong>
											</h1>
										</td>
									</tr>
								</tbody>
							</table>
					</c:otherwise>
				</c:choose>	
			</c:when>
			
			
			
					
			<%--########################################################### VIEW TREETABLE PER TIPUS DE DOCUMENTS ###########################################################--%>
			<c:when test="${vistaTreetablePerTipusDocuments}">
			
				<c:choose>
					<c:when test="${not empty contingut.mapPerTipusDocument}">
						<%--------------------- TREETABLE  -------------------%>
						<table class="table table-striped table-bordered table-hover" id="table-documents">
							<thead>
								<tr>
									<th><input type="checkbox" id="checkItAll" autocomplete="off"/></th>
									<th><spring:message code="contingut.info.nom"/></th>
									<th><spring:message code="contingut.info.descirpcio"/></th>
									<th><spring:message code="contingut.info.ruta"/></th>
									<th><spring:message code="contingut.info.createl"/></th>
									<th><spring:message code="contingut.info.creatper"/></th>
									<th width="5%">&nbsp;</th>
									<c:if test="${expedientPareAgafatPerUsuariActual && isOrdenacioPermesa}">
										<th width="1%">&nbsp;</th>
									</c:if>
								</tr>
							</thead>
							<tbody>
								<c:forEach items="${contingut.mapPerTipusDocument}" var="entry">
									<%------------------------------- Tipus de document ------------------------%>
									<tr id="${entry.key.id}"
										class="<c:if test="${entry.key.permetMultiple || fn:length(entry.value) == 0}"> element-droppable</c:if>"
										data-node="treetable-${entry.key.id}"
										data-pnode="treetable-${contingut.id}">
										<%------------ checkbox ----------%>
										<td></td>
										<%------------ Nom ----------%>
										<td><b>${entry.key.nom}</b> 
										
											<span class="badge">${fn:length(entry.value)}</span>
											
											<span class="label label-info" style="float: right;">
												<spring:message code="multiplicitat.enum.${entry.key.multiplicitat}"/> 
											</span>
										</td>
										<%------------ Descripció ----------%>
										<td></td>
										<%------------ Tipus -----------%>
										<td width="25%"></td>
										<%------------ Creat el -----------%>
										<td></td>
										<%------------ Creat per -----------%>
										<td></td>
										<%------------ Accions -----------%>
										<td></td>
										<%------------ sort ----------%>
										<c:if test="${expedientPareAgafatPerUsuariActual && isOrdenacioPermesa}">
											<td></td>
										</c:if>
									</tr>								
					   				
					   				 <%------------------------------- Documents ----------------------------%>
					   				 <c:choose>
					   				 	<c:when test="${!empty entry.value}">
						   				     <c:forEach items="${entry.value}" var="fill">
						   				     
												<c:if test="${fill.carpeta or (fill.document && fill.documentTipus != 'VIRTUAL')}">
												
													<c:set var="firmat" value="true"/>
													<c:if test="${fill.document}">
														<c:if test="${(fill.estat != 'FIRMAT' || fill.estat == 'CUSTODIAT') && (fill.estat == 'FIRMAT' || fill.estat != 'CUSTODIAT') && fill.estat != 'DEFINITIU'}"><!-- TODO: revise, condition never true -->
															<c:set var="firmat" value="false"/> 
														</c:if>
													</c:if>
											
													<tr id="${fill.id}"
														class="<c:if test="${fill.arxiuEstat == 'ESBORRANY'}"> element-draggable</c:if> isDocument<c:if test="${fill.document && firmat}"> firmat</c:if><c:if test="${fill.document && fill.pdf}"> isPdf</c:if> <c:if test="${fill.document && fill.arxiuUuid == null}"> docAdjuntPendentGuardarArxiu</c:if>"
														data-expedient-id="${expedientId}" 
														data-node="treetable-${fill.id}"
														data-pnode="treetable-${entry.key.id}">
														
											
														<%------------ checkbox ----------%>
														<td><c:if test="${fill.document}">
															<input type="checkbox" class="checkbox" autocomplete="off" />
														</c:if></td>
											
														<%------------ Nom ----------%>
														<td>
															<rip:blocIconaContingut contingut="${fill}" /> 
															<rip:blocContingutNomAmbIcons contingut="${fill}" /> 
														</td>
											
														<%------------ Descripció ----------%>
														<td><c:if test="${fill.document}">
															&nbsp;${fill.descripcio}
														</c:if></td>
											
														<%------------ Ruta -----------%>
														<td width="25%">
															<c:forEach var="contingutPath" items="${fill.path}" varStatus="status">
																<c:if test="${!status.first}">/${contingutPath.nom}</c:if>
															</c:forEach>
															<c:if test="${fn:length(fill.path) == 1}">/</c:if>
														</td>
											
														<%------------ Creat el -----------%>
														<td><fmt:formatDate value="${fill.createdDate}" pattern="dd/MM/yyyy HH:mm" /></td>
											
														<%------------ Creat per -----------%>
														<td>${fill.createdBy.codiAndNom}</td>
											
														<%------------ Accions -----------%>
														<td><rip:blocContingutAccions className="botons-accions-element"
																modeLlistat="true" contingut="${fill}" nodeco="${nodeco}" contingutNavigationId="${contingut.id}" /></td>
											
														<%------------ sort ----------%>
														<c:if test="${expedientPareAgafatPerUsuariActual && isOrdenacioPermesa}">
															<td class="ordre-col" title="<spring:message code="contingut.sort.titol"/>"><span
																class="fa fa-sort"></span></td>
														</c:if>
													</tr>
												</c:if>
						   				     
					    					</c:forEach>
					   					 </c:when>
					   					 <c:otherwise>
					   						<tr	data-node="treetable-0"
												data-pnode="treetable-${entry.key.id}">
												<td></td><td colspan="6"><spring:message code="contingut.info.table.tree.tipus.document.no.documents"/></td>
											</tr>
					   					 </c:otherwise>
					   				 </c:choose>
								</c:forEach>							
							

							
							</tbody>
						</table>			
					</c:when>
					<c:otherwise>
						<%--------------------- EMPTY TREETABLE -------------------%>
						<table class="table table-striped table-bordered table-hover" id="table-documents">
								<thead>
									<tr>
										<th></th>
										<th><spring:message code="contingut.info.nom"/></th>
										<th><spring:message code="contingut.info.descirpcio"/></th>
										<th><spring:message code="contingut.info.tipus" /></th>
										<th><spring:message code="contingut.info.createl" /></th>
										<th><spring:message code="contingut.info.creatper" /></th>
										<th width="10%">&nbsp;</th>
									</tr>
								</thead>
								<tbody>
									<tr class="odd">
										<td colspan="9" valign="top">
											<h1 style="opacity: .1; text-align: center;">
												<rip:blocIconaContingut contingut="${fill}" tamanyEnorme="false" />
												<strong><spring:message code="contingut.sense.contingut" /></strong>
											</h1>
										</td>
									</tr>
								</tbody>
							</table>
					</c:otherwise>
				</c:choose>	
			</c:when>
			
		
			<%--############################################################### VIEW TREETABLE PER CARPETES ###############################################################--%>
			<c:when test="${vistaLlistat}">
				<c:choose>
					<c:when test="${fn:length(fills) > 0}">
						<%--------------------- TREETABLE -------------------%>
						<table class="table table-striped table-bordered table-hover" id="table-documents">
							<thead>
								<tr>
									<th><input type="checkbox" id="checkItAll" autocomplete="off"/></th>
									<th><spring:message code="contingut.info.nom"/></th>
									<th><spring:message code="contingut.info.descirpcio"/></th>
									<th><spring:message code="contingut.info.tipus"/></th>
									<th><spring:message code="contingut.info.createl"/></th>
									<th><spring:message code="contingut.info.creatper"/></th>
									<th width="5%">&nbsp;</th>
									<c:if test="${expedientPareAgafatPerUsuariActual && isOrdenacioPermesa}">
										<th width="1%">&nbsp;</th>
									</c:if>
								</tr>
							</thead>
							<tbody>
								<rip:blocContingutTreeTableFills contingut="${contingut}" mostrarFillsFlat="${!isMostrarCarpetesPerAnotacions}" contingutNavigationId="${contingut.id}"/>
							</tbody>
						</table>			
					</c:when>
					<c:otherwise>
						<%--------------------- EMPTY TREETABLE -------------------%>
						<table class="table table-striped table-bordered table-hover" id="table-documents">
								<thead>
									<tr>
										<th></th>
										<th><spring:message code="contingut.info.nom" /></th>
										<th><spring:message code="contingut.info.tipus" /></th>
										<th><spring:message code="contingut.info.descirpcio"/></th>
										<th><spring:message code="contingut.info.createl" /></th>
										<th><spring:message code="contingut.info.creatper" /></th>
										<th width="10%">&nbsp;</th>
									</tr>
								</thead>
								<tbody>
									<tr class="odd">
										<td colspan="9" valign="top">
											<h1 style="opacity: .1; text-align: center;">
												<rip:blocIconaContingut contingut="${fill}" tamanyEnorme="false" />
												<strong><spring:message code="contingut.sense.contingut" /></strong>
											</h1>
										</td>
									</tr>
								</tbody>
							</table>
					</c:otherwise>
				</c:choose>	
			</c:when>
			
			
			<%--################################################################# VIEW GRID ################################################################--%>
			<c:when test="${vistaIcones}">
				<c:choose>
					<c:when test="${fn:length(fills) > 0}">
		
						<%--------------------- GRID -------------------%>
						<ul id="grid-documents" class="list-inline row">
							<c:forEach var="fill" items="${fills}">
							<%--  && fill.documentTipus != 'VIRTUAL' --%>
								<c:if test="${fill.carpeta or (fill.document && fill.documentTipus != 'VIRTUAL')}">
								
									<c:set var="firmat" value="true"/>
									<c:if test="${fill.document}">
										<c:if test="${(fill.estat != 'FIRMAT' || fill.estat == 'CUSTODIAT') && (fill.estat == 'FIRMAT' || fill.estat != 'CUSTODIAT') && fill.estat != 'DEFINITIU'}"><!-- TODO: revise, condition never true -->
											<c:set var="firmat" value="false"/> 
										</c:if>
									</c:if>

									<li id="${fill.id}"
										class="col-md-2 element-contingut element-draggable<c:if test="${not fill.document}"> element-droppable</c:if><c:if test="${fill.document && firmat}"> firmat</c:if><c:if test="${fill.document && fill.pdf}"> isPdf</c:if> <c:if test="${fill.document && fill.arxiuUuid == null}"> docAdjuntPendentGuardarArxiu</c:if>">
										<div id="${fill.id}" class="thumbnail element-noclick">
											<div class="text-center">
												<rip:blocIconaContingut contingut="${fill}" tamanyDoble="true" />
											</div>
											<div class="caption">
												<p class="text-center">
													<rip:blocContingutNomAmbIcons contingut="${fill}" />
												</p>
												<rip:blocContingutAccions id="accions-fill-${fill.id}"
													className="botons-accions-element" modeLlistat="false" contingut="${fill}"
													nodeco="${nodeco}" contingutNavigationId="${contingut.id}"/>
											</div>
										</div> <script>
											$('#${fill.id}').click(function(e) {
												var contenidorContingut = document.getElementById('grid-documents');
												
												if ($(this).hasClass('noclick')) {
													$(this).removeClass('noclick');
												} else {
													if ($('#accions-fill-${fill.id}').has(e.target).length == 0) {
														$('#${fill.id}').tooltip('destroy');
														if ($(contenidorContingut).hasClass('multiple') && ${fill.document}) {
															var index = docsIdx.indexOf(${fill.id});
															var multipleUrl;
															
															if (index > -1) {
																docsIdx.splice(index, 1);
																$(this).removeClass('selectd');
																var multipleUrl = '<c:url value="/contingut/${contingut.id}/deselect"/>';
																$.get(
																		multipleUrl, 
																		{docsIdx: docsIdx},
																		function(data) {
																			$(".seleccioCount").html(data);
																		}
																);
															} else {
																var multipleUrl = '<c:url value="/contingut/${contingut.id}/select"/>';
																$(this).addClass('selectd');
																docsIdx.push(${fill.id});
																$.get(
																		multipleUrl, 
																		{docsIdx: docsIdx},
																		function(data) {
																			$(".seleccioCount").html(data);
																		}
																);
															}
															enableDisableMultipleButtons();
														} else {
															window.location = $('#${fill.id} a:first').attr('href');
														}
													}
												}
											});
											$('#${fill.id} li a').click(function(e) {
												e.stopPropagation();
											});
										</script>
									</li>
								</c:if>
							</c:forEach>
						</ul>			
					</c:when>
					<c:otherwise>
						<%--------------------- EMPTY GRID -------------------%>
						<h1 style="opacity: .1; text-align: center; margin-bottom: 1em;"><rip:blocIconaContingut contingut="${fill}" tamanyEnorme="false"/><strong><spring:message code="contingut.sense.contingut"/></strong></h1>		
					</c:otherwise>
				</c:choose>
			</c:when>	
		</c:choose>


		
		
		
		
		
		<div class="panel panel-default" id="resum-viewer" style="display: none; width: 100%;" >
			<iframe id="container" class="viewer-padding" width="100%" height="540" frameBorder="0"></iframe>
		</div>  
					
		
		<c:if test="${potModificar}">
			<div id="drag_container" class="drag_activated">
				<span class="down fa fa-upload"></span>
				<p>
					<spring:message code="contingut.drag.info" />
				</p>
			</div>
		</c:if>
		<input class="hidden" id="dropped-files" type="file"/>
		
	</c:otherwise> 
</c:choose> 
