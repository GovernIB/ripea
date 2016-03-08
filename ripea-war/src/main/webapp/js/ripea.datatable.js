(function($) {
	$.fn.ripeaDataTable = function(options) {
		return this.filter("table").each(function() {
			var settings = $.extend({
				backgroundColor: "white",
				infoOcultar: false,
				ajaxRefrescarTaula: true,
				ajaxRefrescarAlertes: true,
				ajaxRefrescarPagina: false,
				modalRefrescarTaula: true,
				modalRefrescarAlertes: true,
				modalRefrescarPagina: false
			}, options);
			var taula = $(this);
			var aoColumns = [];
			var aaSorting = [];
			var aProps = [];
			$('thead th', this).each(function() {
				var sortable = ($(this).data("rdt-sortable") !== undefined) ? $(this).data("rdt-sortable") === true : true;
				var visible = ($(this).data("rdt-visible") !== undefined) ? $(this).data("rdt-visible") === true: true;
				aoColumns.push({"bSortable": sortable, "bVisible": visible});
				if ($(this).data("rdt-sorting"))
					aaSorting.push([aoColumns.length - 1, $(this).data("rdt-sorting")]);
				aProps.push($(this).data("rdt-property"));
				$("[data-rdt-content]", this).each(function() {
					$(this).css("display", "none");
				});
			});
			var seleccioActiva = $(this).data("rdt-seleccionable");
			var seleccioColumna = 0;
			var seleccioUrl;
			if (seleccioActiva && $(this).data("rdt-seleccionable-columna"))
				seleccioColumna = $(this).data("rdt-seleccionable-columna");
			if (seleccioActiva && settings.ajaxSourceUrl) {
				seleccioUrl = settings.ajaxSourceUrl;
				seleccioUrl = seleccioUrl.substring(0, seleccioUrl.lastIndexOf("/")) + "/selection";
			}
			var ordenacioActiva = $(this).data("rdt-ordenable");
			var ordenacioUrl;
			if (ordenacioActiva && settings.ajaxSourceUrl) {
				ordenacioUrl = settings.ajaxSourceUrl;
				ordenacioUrl = ordenacioUrl.substring(0, ordenacioUrl.lastIndexOf("/")) + "/move";
			}
			var paginacioActiva = true;
			if ($(this).attr("data-rdt-paginable")) {
				paginacioActiva = ($(this).data("rdt-paginable") == 'true');
			}
			var ajaxErrorFunction = function (jqXHR, exception) {
				if (jqXHR.status === 0) {
					alert('Not connected.\n Verify Network.');
				} else if (jqXHR.status == 404) {
					alert('Requested page not found. [404]');
				} else if (jqXHR.status == 500) {
					alert('Internal Server Error [500].');
				} else if (exception === 'parsererror') {
					alert('Requested JSON parse failed.');
				} else if (exception === 'timeout') {
					alert('Time out error.');
				} else if (exception === 'abort') {
					alert('Ajax request aborted.');
				} else {
					alert('Uncaught Error.\n' + jqXHR.responseText);
				}
			};
			var dataTableLinkClick = function() {
				var confirmat = true;
				if ($(this).data('rdt-link-confirm'))
					confirmat = confirm($(this).data('rdt-link-confirm'));
				var ajax = ($(this).data("rdt-link-ajax") !== undefined) ? $(this).data("rdt-link-ajax") === true : false;
				var modal = ($(this).data("rdt-link-modal") !== undefined) ? $(this).data("rdt-link-modal") === true : false;
				var refrescarTaulaFunction = function() {
					if (taula !== null) {
						var refrescat = false;
						$('.dataTables_paginate li', taula.parent()).each(function() {
							if ($(this).hasClass('active')) {
								$('a', this).click();
								refrescat = true;
							}
						});
						if (!refrescat)
							taula.dataTable().fnDraw();
					}
				};
				var refrescarPaginaFunction = function() {
					window.parent.location.reload();
				};
				var refrescarAlertesFunction = function() {
					$.ajax({
						url: settings.alertesRefreshUrl,
						async: false,
						timeout: 20000,
						success: function (data) {
							$('#contingut-missatges *').remove();
							$('#contingut-missatges').append(data);
						},
						error: ajaxErrorFunction
					});
				};
				if (confirmat) {
					if (ajax) {
						var ajaxUrl = $(this).attr("href");
						if (ajaxUrl.indexOf("../") != -1)
							ajaxUrl = ajaxUrl.substr(0, ajaxUrl.lastIndexOf("../") + "../".length) + "ajax/" + ajaxUrl.substr(ajaxUrl.lastIndexOf("../") + "../".length);
						else
							ajaxUrl = "ajax/" + ajaxUrl;
						$.ajax({
							type: "GET",
							url: ajaxUrl,
							async: false,
							timeout: 20000,
							success: function() {
								if (settings.ajaxRefrescarTaula)
									refrescarTaulaFunction();
								if (settings.ajaxRefrescarPagina)
									refrescarPaginaFunction();
								if (settings.ajaxRefrescarAlertes && settings.alertesRefreshUrl)
									refrescarAlertesFunction();
							},
							error: ajaxErrorFunction
						});
						return false;
					} else if (modal) {
						var modalCloseFunction = function() {
							if (settings.modalRefrescarTaula)
								refrescarTaulaFunction();
							if (settings.modalRefrescarPagina)
								refrescarPaginaFunction();
							if (settings.modalRefrescarAlertes && settings.alertesRefreshUrl)
								refrescarAlertesFunction();
						};
						var modalDivId = taula.attr('id') + "_modal";
						if ($('#' + modalDivId).length === 0)
							taula.parent().append('<div id="' + modalDivId + '"></div>');
						$('#' + modalDivId).ripeaModal({
							modalUrl: $(this).attr("href"),
							modalCloseFunction: modalCloseFunction
						});
						return false;
					} else {
						return true;
					}
				} else {
					return false;
				}
			};
			if ($(this).data("rdt-filtre-form-id")) {
				var filtreFormId = $(this).data("rdt-filtre-form-id");
				$('button[type=submit][name=accio]').click(function() {
					if ($('input[name=accio]', $(this).parent()).length === 0) {
						$(this).after('<input type="hidden" name="accio" value="' + $(this).val() + '"/>');
					} else {
						$('input[name=accio]', $(this).parent()).val($(this).val());
					}
					return true;
				});
				$('#' + filtreFormId).on('submit', function() {
					var accio = $('input[name=accio]', $('#' + filtreFormId)).val();
					if (accio == 'netejar') {
						$(':input', '#' + filtreFormId)
						.not(':button, :submit, :reset, :hidden')
						.val('')
						.removeAttr('checked')
						.removeAttr('selected');
						$(':input.select2-offscreen', '#' + filtreFormId).select2("val", "", true);
					}
					$.ajax({
						type: "POST",
						url: $('#' + filtreFormId).attr('action'),
						data: $('#' + filtreFormId).serialize(),
						async: false
					});
					taula.dataTable().fnDraw();
					return false;
				});
			}
			var dataTableParams = {
				"iDisplayLength": 10,
				"aLengthMenu": [[10, 50, 100], [10, 50, 100]],
				"aaSorting": aaSorting,
				"aoColumns": aoColumns,
				"bAutoWidth": false,
				"bProcessing": settings.ajaxSourceUrl !== null,
				"bServerSide": settings.ajaxSourceUrl !== null,
				"bPaginate": paginacioActiva,
				"oLanguage": {"sUrl": settings.localeUrl},
				"fnDrawCallback": function(oSettings) {
					$('.datatable-dades-carregant', this).hide();
					$('.dataTables_info', this.parent()).removeClass('hidden');
					if (oSettings.aoData.length === 0)
						$('.dataTables_info', this.parent()).addClass('hidden');
					if (settings.infoOcultar)
						$('.dataTables_info', this.parent()).addClass('hidden');
					$("a", this.parent()).on('click', dataTableLinkClick);
					if (seleccioActiva) {
						$("th:eq(" + seleccioColumna + ")", taula).html('<input type="checkbox"/>');
						var canviSeleccio = function(ids) {
							var seleccio = [];
							$.ajax({
								dataType: "json",
								type: "POST",
								url: seleccioUrl,
								data: {ids: ids},
								async: false,
								success: function(data) {
									$("td input.rdt-seleccio[type=checkbox]", taula).each(function(index) {
										this.checked = false;
										for (var i = 0; i < data.length; i++) {
											if (data[i] == $(this).val()) {
												this.checked = true;
												break;
											}
										}
										seleccio = data;
									});
								},
								timeout: 20000,
								error: ajaxErrorFunction
							});
							if ($("td input.rdt-seleccio[type=checkbox]", taula).length > 0) {
								var checkedAll = true;
								$("td input.rdt-seleccio[type=checkbox]", taula).each(function() {
									if (!this.checked)
										checkedAll = false;
								});
								if (checkedAll)
									$("th:eq(" + seleccioColumna + ") input[type=checkbox]", taula)[0].checked = true;
							}
							return seleccio;
						};
						$("th:eq(" + seleccioColumna + ") input[type=checkbox]", taula).on('click', function() {
							var ids = "";
							var checked = this.checked;
							var numChecks = $("td input.rdt-seleccio[type=checkbox]", taula).length;
							$("td input.rdt-seleccio[type=checkbox]", taula).each(function(index) {
								if (!checked)
									ids += "-";
								ids += $(this).val();
								if (index < numChecks - 1)
									ids += ",";
							});
							var seleccionat = canviSeleccio(ids);
							if (settings.seleccioCallback)
								settings.seleccioCallback(seleccionat);
						});
						$("td input.rdt-seleccio[type=checkbox]", taula).on('click', function() {
							var ids = (this.checked) ? $(this).val() : "-" + $(this).val();
							var seleccionat = canviSeleccio(ids);
							if (!this.checked)
								$("th:eq(" + seleccioColumna + ") input[type=checkbox]", taula)[0].checked = false;
							if (settings.seleccioCallback)
								settings.seleccioCallback(seleccionat);
						});
						var seleccionat = canviSeleccio();
						if (settings.seleccioCallback)
							settings.seleccioCallback(seleccionat);
					}
					if (ordenacioActiva) {
						$("tbody", taula).sortable({
							cursor: 'move',
							update: function(e, ui) {
								var metaDadaId = ui.item.data('id');
								var newIndex = ui.item.index();
								var sortUrl = ordenacioUrl + "/" + metaDadaId + "/" + newIndex;
								$.ajax({
									url: sortUrl,
									async: false,
									timeout: 20000,
									success: function (data) {
										taula.dataTable().fnDraw();
									},
									error: ajaxErrorFunction
								});
							},
							helper: function(e, ui) {
								ui.children().each(function() {
									$(this).width($(this).width());
								});
								return ui;
							}
						});
					}
					if (settings.drawCallback)
						settings.drawCallback(oSettings);
				},
				"fnRowCallback": function(nRow, aData, iDisplayIndex, iDisplayIndexFull) {
					$('th', this).each(function() {
						var index = $(this).index();
						var propName = $(this).data('rdt-property');
						var propIndex = aProps.indexOf(propName);
						if (propIndex === 0 && propName == 'id') {
							$(nRow).data('id', aData[propIndex]);
						}
						$('[data-rdt-content]', this).each(function() {
							$("td:eq(" + index + ")", nRow).html(this.innerHTML);
						});
					});
					$('th[data-rdt-template]', this).each(function() {
						var index = $(this).index();
						var dataObj = [];
						for (var i = 0; i < aData.length; i++) {
							var propName = aProps[i];
							dataObj[propName.replace(".", "_")] = aData[i];
						}
						var templateData = new Array(dataObj);
						$("td:eq(" + index + ")", nRow).html(
								$("#" + $(this).data('rdt-template')).render(
									templateData, {
										alert: function(text) {
											alert(text);
										},
										eval: function(expression) {
											return eval(expression);
										}
									}));
					});
					$('th[data-rdt-type]', this).each(function() {
						var index = $(this).index();
						var propIndex = aProps.indexOf($(this).data('rdt-property'));
						var property = aData[propIndex];
						if (property === null) {
							$("td:eq(" + index + ")", nRow).empty();
						} else {
							var type = $(this).data('rdt-type');
							if (type.indexOf('date') === 0 || type.indexOf('time') != -1) {
								var data = new Date(property);
								var horaAmbFormat = "";
								var dataAmbFormat;
								if (type.indexOf('time') != -1) {
									var hores = ("00" + data.getHours()).slice(-2);
									var minuts = ("00" + data.getMinutes()).slice(-2);
									var segons = ("00" + data.getSeconds()).slice(-2);
									horaAmbFormat = hores + ":" + minuts + ":" + segons;
								}
								if (type.indexOf('date') === 0) {
									var dia = ("00" + data.getDate()).slice(-2);
									var mes = ("00" + (data.getMonth() + 1)).slice(-2);
									var any = data.getFullYear();
									dataAmbFormat = dia + "/" + mes + "/" + any;
									if (type == 'datetime') {
										dataAmbFormat += " " + horaAmbFormat;
									}
								} else {
									dataAmbFormat = horaAmbFormat;
								}
								$("td:eq(" + index + ")", nRow).html(dataAmbFormat);
							} else if (type == 'time') {
								var data = new Date(property);
								var hores = ("00" + data.getHours()).slice(-2);
								var minuts = ("00" + data.getMinutes()).slice(-2);
								var segons = ("00" + data.getSeconds()).slice(-2);
								var dataAmbFormat = hores + ":" + minuts + ":" + segons;
								$("td:eq(" + index + ")", nRow).html(dataAmbFormat);
							}
						}
					});
					if (seleccioActiva) {
						$("td:eq(" + seleccioColumna + ")", nRow).html('<input class="rdt-seleccio" type="checkbox" value="' + aData[seleccioColumna] + '"/>');
					}
					if (settings.rowClickCallback) {
						$('td', nRow).each(function() {
							var clickable = $('input', this).length == 0 && $('button', this).length == 0;
							if (clickable) {
								$(this).mouseenter(function() {
									$(this).css('cursor', 'pointer');
								});
								$(this).mouseleave(function() {
									$(this).css('cursor', 'auto');
								});
								$(this).click(function() {
									settings.rowClickCallback($(this).closest('tr')[0]);
								});
							}
						});
					}
				},
				"fnServerParams": function(aoData) {
					if ($(this).data("rdt-button-template") && $(this).data("rdt-button-template").length > 0) {
						$.templates("templateNew", $("#" + $(this).data('rdt-button-template')).html());
						$('#dataTables_new', this.parent()).html($.render.templateNew());
					}
					$('.datatable-dades-carregant', this).show();
					if (settings.serverParamsCallback)
						settings.serverParamsCallback(aoData);
				}
			};
			if (settings.ajaxSourceUrl !== null) {
				dataTableParams.sAjaxSource = settings.ajaxSourceUrl;
				dataTableParams.fnServerData = function (sSource, aoData, fnCallback, oSettings) {
					for (var i = 0; i < aProps.length; i++) {
						aoData.push({"name": "aProp_" + i, "value": aProps[i]});
					}
					$.ajax({
						dataType: "json",
						type: "GET",
						url: sSource,
						data: aoData,
						success: fnCallback,
						timeout: 20000,
						error: function (xhr, textStatus, errorThrown) {
							$('.datatable-dades-carregant', this).hide();
							ajaxErrorFunction(xhr, textStatus, errorThrown);
						}
					});
				};
			}
			$(this).dataTable(dataTableParams);
		});
	};
}(jQuery));