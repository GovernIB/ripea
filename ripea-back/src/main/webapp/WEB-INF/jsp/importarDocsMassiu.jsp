<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:set var="maxFileSize"><%=es.caib.ripea.back.config.WebMvcConfig.MAX_UPLOAD_SIZE%></c:set>
<html>
<head>
    <title><spring:message code="expedient.importar.docs.mass.documents" arguments="${importarDocsMassiuCommand.numExps}"/></title>
    <link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
    <link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
    <script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
    <script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
    <script src="<c:url value="/js/webutil.common.js"/>"></script>
	<link href="<c:url value="/css/jasny-bootstrap.min.css"/>" rel="stylesheet">
	<script src="<c:url value="/js/jasny-bootstrap.min.js"/>"></script>    
    <rip:modalHead/>
    
    <style>
        .document-row {
            margin-bottom: 15px;
            padding: 15px;
            border: 1px solid #ddd;
            border-radius: 4px;
            background-color: #f9f9f9;
        }
        .document-row .form-group {
            margin-bottom: 10px;
        }
        .btn-remove-document {
            margin-top: 25px;
        }
        #documents-container .document-row:first-child .btn-remove-document {
            display: none;
        }
		.error-border {
			border-color: #d9534f !important;
		}
		.error-message {
			color: #d9534f;
			font-size: 12px;
			margin-top: 5px;
		}        
    </style>
</head>
<body>
    <c:set var="formAction"><rip:modalUrl value="/expedient/importarDocsMassiu"/></c:set>
    
    <form:form action="${formAction}" method="post" cssClass="form-horizontal" modelAttribute="importarDocsMassiuCommand" enctype="multipart/form-data">
        
        <c:if test="${importarDocsMassiuCommand.allSameProcediment}">
        
        <div class="modal-body">
            
            <div id="documents-container">
                <c:forEach items="${importarDocsMassiuCommand.documents}" var="doc" varStatus="status">
                    <div class="document-row" data-index="${status.index}">
                        <div class="row">
                            <div class="col-sm-6">
                            
		                        <label><spring:message code="expedient.list.user.importar.docs.mass.file"/></label>
		    					<div class="fileinput fileinput-new input-group" data-provides="fileinput">
									<div class="form-control" data-trigger="fileinput"><i class="glyphicon glyphicon-file fileinput-exists"></i> <span class="fileinput-filename"></span></div>
									<span class="input-group-addon btn btn-default btn-file"><span class="fileinput-new">Seleccionar</span><span class="fileinput-exists">Canviar</span>
										<input type="file" id="documents[${status.index}].file" name="documents[${status.index}].file"/>
									</span>
									<a href="#" class="input-group-addon btn btn-default fileinput-exists" style="width:auto" data-dismiss="fileinput">Netejar</a>
								</div>

                            </div>
                            <div class="col-sm-5">
                                <div class="form-group">
			                        <label><spring:message code="expedient.list.user.importar.docs.mass.tipus"/></label>
			                        <select name="documents[${status.index}].tipusDocumentId" 
			                                id="documents${status.index}.tipusDocumentId"
			                                class="form-control select2-tipus-document">
			                            <option value=""></option>
			                            <c:forEach items="${tipusDocuments}" var="tipus">
			                                <option value="${tipus.id}">${tipus.nom}</option>
			                            </c:forEach>
			                        </select>
                                </div>
                            </div>
                            <div class="col-sm-1">
                                <button type="button" class="btn btn-danger btn-remove-document">
                                    <span class="fa fa-trash"></span>
                                </button>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
            
            <div class="form-group">
                <div class="col-sm-12">
                    <button type="button" id="btn-add-document" class="btn btn-primary">
                        <span class="fa fa-plus"></span>
                        <spring:message code="expedient.importar.docs.mass.afegir"/>
                    </button>
                </div>
            </div>
        </div>
        </c:if>
        <c:if test="${!importarDocsMassiuCommand.allSameProcediment}">
        	<div class="card avisCard  avisCardWarning ">
                <div class="card-header avisCardHeader">
                     <span class="fa fa-exclamation-triangle text-warning">&nbsp;</span><spring:message code="expedient.list.user.importar.docs.mass.warn"/>
                </div>
            </div>
        </c:if>
        
        <div id="modal-botons">
            <button type="submit" class="btn btn-success">
                <span class="fa fa-upload"></span><spring:message code="comu.boto.importar"/>
            </button>
            <a href="<c:url value="/expedient"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.tancar"/></a>
        </div>
    </form:form>
    
    <!-- Template para nuevos documentos -->
    <script id="document-template" type="text/template">
        <div class="document-row" data-index="__INDEX__">
            <div class="row">
                <div class="col-sm-6">
                        <label><spring:message code="expedient.list.user.importar.docs.mass.file"/></label>
    					<div class="fileinput fileinput-new input-group" data-provides="fileinput">
							<div class="form-control" data-trigger="fileinput"><i class="glyphicon glyphicon-file fileinput-exists"></i> <span class="fileinput-filename"></span></div>
							<span class="input-group-addon btn btn-default btn-file"><span class="fileinput-new">Seleccionar</span><span class="fileinput-exists">Canviar</span>
								<input type="file" id="documents[__INDEX__].file" name="documents[__INDEX__].file"/>
							</span>
							<a href="#" class="input-group-addon btn btn-default fileinput-exists" style="width:auto" data-dismiss="fileinput">Netejar</a>
						</div>
                </div>
                <div class="col-sm-5">
                    <div class="form-group">
                        <label><spring:message code="expedient.list.user.importar.docs.mass.tipus"/></label>
                        <select name="documents[__INDEX__].tipusDocumentId" 
                                id="documents__INDEX__.tipusDocumentId"
                                class="form-control select2-tipus-document">
                            <option value=""></option>
                            <c:forEach items="${tipusDocuments}" var="tipus">
                                <option value="${tipus.id}">${tipus.nom}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
                <div class="col-sm-1">
                    <button type="button" class="btn btn-danger btn-remove-document">
                        <span class="fa fa-trash"></span>
                    </button>
                </div>
            </div>
        </div>
    </script>
    
    <script>

        $(document).ready(function() {
        	
        	var MAX_FILE_SIZE = ${maxFileSize};
        	
        	let fileName = "${fileName}";
        	if (fileName) {
        		$fileinput = $('#${campPath}').closest('.fileinput');
        		$fileinput.removeClass('fileinput-new');
        		$fileinput.addClass('fileinput-exists');
        		$('.fileinput-filename', $fileinput).append(fileName);
        	}
        	
            var documentIndex = ${importarDocsMassiuCommand.documents.size()};
            
            // Inicializar select2 para los selects existentes
            $('.select2-tipus-document').select2({
                theme: 'bootstrap',
                language: '${requestLocale}',
                width: '100%'
            });
            
            // Añadir nuevo documento
            $('#btn-add-document').on('click', function() {
                var template = $('#document-template').html();
                var newDocument = template.replace(/__INDEX__/g, documentIndex);
                $('#documents-container').append(newDocument);
                
//                 // Inicializar select2 para el nuevo select
                $('#documents-container .document-row:last .select2-tipus-document').select2({
                    theme: 'bootstrap',
                    language: '${requestLocale}',
                    width: '100%'
                });
                
                documentIndex++;
                updateRemoveButtons();
                webutilModalAdjustHeight();
            });
            
            // Eliminar documento
            $(document).on('click', '.btn-remove-document', function() {
                $(this).closest('.document-row').remove();
                reindexDocuments();
                updateRemoveButtons();
            });
            
            // Reindexar los documentos después de eliminar
            function reindexDocuments() {
                $('#documents-container .document-row').each(function(index) {
                    $(this).attr('data-index', index);
                    $(this).find('input[type="file"]').attr('name', 'documents[' + index + '].file');
                    $(this).find('input[type="file"]').attr('id', 'documents' + index + '.file');
                    $(this).find('select').attr('name', 'documents[' + index + '].tipusDocumentId');
                    $(this).find('select').attr('id', 'documents' + index + '.tipusDocumentId');
                });
                documentIndex = $('#documents-container .document-row').length;
            }
            
            // Ocultar botón eliminar si solo hay un documento
            function updateRemoveButtons() {
                var documentCount = $('#documents-container .document-row').length;
                if (documentCount === 1) {
                    $('#documents-container .document-row:first .btn-remove-document').hide();
                } else {
                    $('#documents-container .btn-remove-document').show();
                }
            }
            
         // ========== VALIDACIÓN DEL FORMULARIO ==========
			$('#importarDocsMassiuCommand').on('submit', function(e) {

				debugger;
				
				// Limpiar errores previos
				$('.error-border').removeClass('error-border');
				$('.error-message').remove();
				
				var hasErrors = false;
				var firstErrorElement = null;
				
				// Validar cada fila de documento
				$('#documents-container .document-row').each(function(index) {
					var $row = $(this);
					var $fileInput = $row.find('input[type="file"]');
					var $selectTipus = $row.find('select');
					
					// Validar archivo
					var hasFile = false;
					var fileSize = 0;
					var fileName = '';
					
					if ($fileInput.length > 0) {
						// Para inputs de file normales
						hasFile = $fileInput[0].files && $fileInput[0].files.length > 0;
					}
					
					if (!hasFile) {
						var $fileGroup = $fileInput.closest('.form-group');
						if ($fileGroup.length === 0) {
							$fileGroup = $row.find('.col-sm-6');
						}
						$fileGroup.find('input[type="file"]').addClass('error-border');
						$fileGroup.append('<div class="error-message"><spring:message code="javax.validation.constraints.NotEmpty.message"/></div>');
						hasErrors = true;
						if (!firstErrorElement) {
							firstErrorElement = $fileGroup;
						}
					} else {
						// Validar tamaño del archivo
						fileSize = $fileInput[index].files[0].size;
						if (fileSize > MAX_FILE_SIZE) {
							$fileGroup.find('input[type="file"]').addClass('error-border');
							$fileGroup.append(
								'<div class="error-message">' +
									'<spring:message code="expedient.list.user.importar.docs.exceeded"/>' +
								'</div>'
							);
							hasErrors = true;
							if (!firstErrorElement) {
								firstErrorElement = $fileGroup;
							}
						}
					}
					
					// Validar tipo de documento
					var tipusValue = $selectTipus.val();
					if (!tipusValue || tipusValue === '') {
						$selectTipus.next('.select2-container').find('.select2-selection').addClass('error-border');
						$selectTipus.closest('.form-group').append('<div class="error-message"><spring:message code="javax.validation.constraints.NotEmpty.message"/></div>');
						hasErrors = true;
						if (!firstErrorElement) {
							firstErrorElement = $selectTipus.closest('.form-group');
						}
					}
				});
				
				// Si hay errores, prevenir el submit y mostrar mensaje
				if (hasErrors) {
					e.preventDefault();
					
					// Scroll al primer error
					if (firstErrorElement) {
						$('html, body').animate({
							scrollTop: firstErrorElement.offset().top - 100
						}, 300);
					}
					
					var modalBody = window.frameElement.parentNode;
					var modalFooter = window.frameElement.parentNode.nextElementSibling;
					$(".datatable-dades-carregant", modalBody).css('display', 'none');
					$("iframe", modalBody).css('display', 'block');
					$(".btn-success", modalFooter).removeAttr('disabled');					
					
					return false;
				}

				return true;
			});
            
			// Limpiar errores cuando el usuario interactúa
			$(document).on('change', 'input[type="file"]', function() {
				$(this).removeClass('error-border');
				$(this).closest('.form-group, .col-sm-6').find('.error-message').remove();
			});
			
			$(document).on('change', '.select2-tipus-document', function() {
				$(this).next('.select2-container').find('.select2-selection').removeClass('error-border');
				$(this).closest('.form-group').find('.error-message').remove();
			});
            
            updateRemoveButtons();
        });
    </script>
</body>
</html>