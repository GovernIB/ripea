// Basat en http://stefangabos.ro/jquery/jquery-plugin-boilerplate-revisited/
(function($) {

	$.webutilDatatable = function(element, options) {
		var defaults = {
			pageLength: 10,
			lengthMenu: [10, 20, 50, 100, 250],
			infoEnabled: true,
			infoType: 'botons', // 'botons', 'search'
			searchEnabled: true,
			pagingEnabled: true,
			selectionEnabled: false,
			dragEnabled: false,
			pagingStyle: 'page', // 'page', 'scroll'
			scrollBuffer: 9,
			scrollOverflow: 'fixed', // 'fixed', 'adaptMax', 'adapt'
			scrollFixedHeight: 300,
			scrollMaxHeight: 200,
			scrollMinHeight: 100,
			rowInfo: false, 
			campsAddicionals: false,
			ordering: true,
			defaultDir: 'asc',
			bootstrapRowCols: 12
		}
		var $element = $(element), element = element;
		var $taula = $element;
		var plugin = this;
		plugin.settings = {}
		plugin.serverParams = [];
		plugin.init = function() {
			plugin.settings = $.extend(defaults, $element.data(), options);
			// Inicialització de les options per a crear la datatable
			if (plugin.settings.dragEnabled) {
				$('thead tr,tfoot tr', $taula).each(function() {
					$(this).prepend($('<th width="1">&nbsp;</th>'));
				});
				$('tbody tr', $taula).each(function() {
					$(this).prepend($('<td></td>'));
				});
			}
			if (plugin.settings.selectionEnabled) {
				$('thead tr,tfoot tr', $taula).each(function() {
					$(this).prepend($('<th>&nbsp;</th>'));
				});
				$('tbody tr', $taula).each(function() {
					$(this).prepend($('<td></td>'));
				});
			}
			if (plugin.settings.editable) {
				$('thead tr,tfoot tr', $taula).each(function() {
					if ($(this).is(plugin.settings.editableSampleRow)) {
						var newUrl = getBaseUrl() + '/new';
						$(this).append($('<th><a href="' + newUrl + '" class="btn btn-default btn-sm"><i class="fa fa-plus"></i></a></th>'));
					} else {
						$(this).append($('<th>&nbsp;</th>'));
					}
				});
				$('tbody tr', $taula).each(function() {
					$(this).append($('<td></td>'));
				});
			}
			if (plugin.settings.rowInfo) {
				$('thead tr,tfoot tr', $taula).each(function() {
					$(this).append($('<th width="1">&nbsp;</th>'));
				});
				$('tbody tr', $taula).each(function() {
					$(this).append($('<td></td>'));
				});
			}
			var colMd50p = plugin.settings.bootstrapRowCols / 2;
			var colMd25p = plugin.settings.bootstrapRowCols / 4;
			var colMd75p = colMd25p * 3;
			var domPrefix;
			if (plugin.settings.infoType == 'search' || plugin.settings.pagingStyle == 'scroll')
				domPrefix = '<"row"<"col-md-' + colMd50p + '"i><"col-md-' + colMd50p + '"f>>';
			else
				domPrefix = '<"row"<"col-md-' + colMd50p + '"i><"col-md-' + colMd50p + '"<"botons">>>';
			var language = window.navigator.userLanguage || window.navigator.language;
			// Només acceptam es i ca com a llengues //
			if (language.startsWith("es")) {
				language = "es";
			} else {
				language = "ca";
			}
			// ------------------------------------- //
			var dataTableOptions = {
				language: {
					url: webutilContextPath() + '/js/datatables/i18n/datatables.' + language + '.json'
				},
				info: plugin.settings.infoEnabled,
				serverSide: true,
				autoWidth: false,
				searching: plugin.settings.searchEnabled,
				ajax: {
					url: getBaseUrl() + '/datatable',
					data: function(data) {
						for (var key in plugin.serverParams) {
							data[key] = plugin.serverParams[key];
						}
						if (plugin.settings.filtre) {
							data = $.extend(
									data,
									JSON.stringify($(plugin.settings.filtre).serializeArray()));
							var valors = $(plugin.settings.filtre).serializeArray();
							for (i=0; i<valors.length; i++) {
								data[valors[i].name] = valors[i].value;
							}
						}
					}
				},
				rowCallback: function(row, data) {
					if (plugin.settings.selectionEnabled) {
						$(row).addClass('selectable');
						var $cell = $('td:first', row);
						$cell.empty().append('<span class="fa fa-square-o"></span>');
						$('span', $cell).click(function() {
							$(this).parent().trigger('click');
						});
					}
					if (plugin.settings.dragEnabled) {
						var $cell;
						if (plugin.settings.selectionEnabled) {
							$cell = $('td:eq(1)', row);
						} else {
							$cell = $('td:first', row);
						}
						$cell.empty().append('<a class="btn btn-default drag-handle" style="padding: 6px 4px;margin-right: 4px;cursor: move;cursor: -webkit-grabbing;">::</a>');
					}
					if (plugin.settings.editable) {
						var deleteUrl = getBaseUrl() + '/' + data['DT_Id'] + '/delete';
						$('td:last-child', row).html('<a href="' + deleteUrl + '" class="btn btn-default btn-sm"><i class="fa fa-trash-o"></i></a>')
					}
					if (plugin.settings.rowInfo) {
						$('td:last-child', row).html('<a href="#" class="btn btn-default btn-sm"><span class="fa fa-caret-down"></span></a>')
					}
					if (plugin.settings.rowhrefTemplate) {
						if (plugin.settings.rowhrefToggle) {
							$(row).attr('data-toggle', plugin.settings.rowhrefToggle);
						}
						$(row).attr(
								'data-href',
								$(plugin.settings.rowhrefTemplate).render(data));
						$(row).css('cursor', 'pointer');
					}
					if (data['DT_RowSelected']) {
						$taula.dataTable().api().row(row).select();
					}
				},
				preDrawCallback: function(settings_) {
					var currentLenthMenu = settings_._iDisplayLength;
					if (plugin.settings.botonsTemplate && plugin.settings.botonsTemplate.length > 0) {
						$.templates("templateNew", $(plugin.settings.botonsTemplate).html());
						var targetBotons = $('.botons', this.parent());
						if (!targetBotons.data('botons-creats')) {
							targetBotons.html($.render.templateNew());
							targetBotons.data('botons-creats', 'true');
						}
					}
					$('div.dataTables_length', $(this).closest('.dataTables_wrapper')).each(function() {
						if (!$('label', $(this)).data('processed')) {
							var label = $('label', $(this));
							var botons = $('<div class="btn-group"></div>');
							$('option', label).each(function() {
								var active = (currentLenthMenu != undefined ? ($(this).val() == currentLenthMenu) : ($(this).val() == plugin.settings.pageLength));
								botons.append('<button value="' + $(this).val() + '" class="btn btn-default' + ((active) ? ' active': '') + '">' + $(this).val() + '</button>')
							});
							label.css('display', 'none');
							label.data('processed', 'true');
							$(this).prepend(botons);
							var select = $('select', $(this));
							$('button', $(this)).on('click', function() {
								$('button', $(this).parent()).removeClass('active');
								$(this).addClass('active');
								select.val($(this).val()).trigger('change');
							});
						}
					});
					if (plugin.settings.filtre) {
						$(plugin.settings.filtre).webutilNetejarErrorsCamps();
					}
				},
				drawCallback: function(settings_) {
					if ($.fn.webutilModalEval)
						$(this).closest('.dataTables_wrapper').webutilModalEval();
					if ($.fn.webutilConfirmEval)
						$(this).closest('.dataTables_wrapper').webutilConfirmEval();
					if ($.fn.webutilAjaxEval)
						$(this).closest('.dataTables_wrapper').webutilAjaxEval();
					$('tr[data-href]', $taula).each(function() {
						$(this).mousedown(function(event) {
							if ($(this).prop("tagName") == 'TR' && event.target.tagName == 'TD') {
								if (event.which == 2) {
									window.open($(this).data('href'), '_blank');
									return false;
								} else if (event.which == 1) {
									if (!plugin.settings.rowhrefToggle) {
										window.location.href = $(this).data('href');
										return false;
									} else {
										return true;
									}
								}
							}
						});
					});
					if (plugin.settings.editableSampleRow && plugin.settings.editable !== false) {
						$('tbody tr', $taula).click(function(event) {
							var $parentTbody = $(this).closest('tbody');
							$parentTbody.attr('data-clicked', 'true');
						});
						$(document).click(function(event) {
							var ferBlur = $('tbody', $taula).has(event.target).length == 0;
							if (ferBlur) {
								ferBlur = $(plugin.settings.campsAddicionals).has(event.target).length == 0;
							}
							if (ferBlur) {
								ferBlur = $(event.target).closest('.modal').length == 0;
							}
							if (ferBlur) {
								$taula.dataTable().api().cell.blur();
								editableProcessarCanviRow($('tbody tr[data-editing]', $taula));
							}
						});
					}
					if (plugin.settings.selectionEnabled) {
						var $row = headerTrFunction();
						$row.addClass('selectable');
						var $cell = $('th:first', $row);
						$cell.off('click');
						$cell.on('click', function() {
							var api = $taula.dataTable().api();
							var numRows = api.data().length;
							var numSelected = api.rows({
								search: 'applied',
								selected: true}).data().length;
							if (numSelected < numRows) {
								api.rows({search: 'applied'}).select();
							} else {
								api.rows({search: 'applied'}).deselect();
							}
							return false;
						});
						$cell.empty().append('<span class="fa fa-square-o"></span>');
					}
					if (plugin.settings.pagingStyle == 'scroll') {
						recalcularDimensions();
					}
					if (plugin.settings.filtre) {
						var api = new $.fn.dataTable.Api(settings_);
						var ajaxResponse = api.ajax.json();
						if (ajaxResponse.filtreError) {
							$(plugin.settings.filtre).webutilMostrarErrorsCamps(
									ajaxResponse.filtreFormResponse.errorsCamps);
						}
					}
					if (plugin.settings.editable) {
						$('tbody tr td:last-child a', $taula).click(function(e) {
							if (confirm('Estau segur que voleu esborrar aquesta fila?')) {
								editableAccioDelete($(this).attr('href'));
							}
							return false;
						});
					}
					if (plugin.settings.rowInfo) {
						$('tbody tr td:last-child a', $taula).click(function(e) {
							$icon = $('span', this);
							$parentTr = $(this).closest('tr');
							var shown = $icon.hasClass('fa-caret-up');
							if (!shown) {
								$('<tr data-row-info="true"><td colspan="' + $parentTr.children().length + '"></td></tr>').insertAfter($parentTr);
								var rowData = $taula.dataTable().api().row($parentTr).data();
								$taula.trigger(
										'rowinfo.dataTable',
										[$('td', $parentTr.next()), rowData]);
							} else {
								$parentTr.next().remove();
							}
							$icon.toggleClass('fa-caret-up');
							$icon.toggleClass('fa-caret-down');
							return false;
						});
					}
				}
			}
			// Configuració de les columnes
			if ($('thead th[data-col-name]', $taula).length > 0) {
				var columns = [];
				var defaultOrder = plugin.settings.defaultOrder;
				if (typeof plugin.settings.defaultOrder != 'undefined' && plugin.settings.selectionEnabled) {
					defaultOrder++;
				}
				if (typeof plugin.settings.defaultOrder != 'undefined' && plugin.settings.dragEnabled) {
					defaultOrder++;
				}
				var defaultDir = plugin.settings.defaultDir;
				dataTableOptions['ordering'] = plugin.settings.ordering;
				$('thead th', $taula).each(function(index) {
					if (plugin.settings.selectionEnabled && index == 0) {
						columns.push({
							data: '<null>',
							orderable: false,
							visible: true});
					} else if ((!plugin.settings.selectionEnabled && plugin.settings.dragEnabled && index == 0) || (plugin.settings.selectionEnabled && plugin.settings.dragEnabled && index == 1)) {
						columns.push({
							data: '<null>',
							orderable: false,
							visible: true});
					} else if (plugin.settings.editable && $(this).is(':last-child')) {
						columns.push({
							data: '<null>',
							orderable: false,
							visible: true});
					} else if (plugin.settings.rowInfo && $(this).is(':last-child')) {
						columns.push({
							data: '<null>',
							orderable: false,
							visible: true});
					} else {
						var columnSettings = $.extend({
							orderable: true,
							visible: true,
							editable: true
						}, $(this).data());
						var orderable = columnSettings.orderable;
						var visible = columnSettings.visible;
						var className = '';
						if (columnSettings.renderer && columnSettings.renderer.indexOf('number') >= 0) {
							className = className + " text-right";
						}
						var colName = columnSettings.colName;
						if (colName) {
							columns.push({
								name: colName,
								data: colName,
								orderable: orderable,
								visible: visible,
								className: className,
								render: renderFunction,
								defaultContent: ''});
						} else {
							columns.push({
								data: '<null>',
								orderable: orderable,
								visible: visible,
								className: className,
								render: renderFunction});
						}
						if (orderable && typeof defaultOrder == 'undefined') {
							defaultOrder = index;
						}
					}
				});
				dataTableOptions['columns'] = columns;
				if (typeof defaultOrder != 'undefined') {
					dataTableOptions['order'] = [[defaultOrder, defaultDir]];
				}
			}
			$taula.on('processing.dt', function(e, settings_, processing) {
				if (!$('tbody .datatable-dades-carregant', $taula).length) {
					$('tbody', $taula).append('<tr class="datatable-dades-carregant"><td colspan="8"><div><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div></td></tr>');
				}
				if (processing) {
					$('tbody .datatable-dades-carregant', $taula).show();
				} else {
					$('tbody .datatable-dades-carregant', $taula).hide();
				}
			});
			if (plugin.settings.dragEnabled) {
				$taula.on('draw.dt', function () {
					Sortable.create(
							$('tbody', $taula)[0], {
								handle: ".drag-handle",
								onUpdate: function (event) {
									var item = event.item;
									var itemId = item.id.substring("row_".length);
									$taula.trigger(
											'dragupdate.dataTable',
											[itemId, $(item).index()]);
									$taula.dataTable().fnDraw();
						    }
						});
				});
			}
			// Configuració de la paginació
			if (plugin.settings.pagingEnabled) {
				if (plugin.settings.pagingStyle == 'page') {
					dataTableOptions = $.extend({
						paging: true,
						pageLength: plugin.settings.pageLength,
						lengthMenu: plugin.settings.lengthMenu,
						dom: domPrefix + 't<"row"<"col-md-' + colMd25p + '"l><"col-md-' + colMd75p + '"p>>'
					}, dataTableOptions);
				} else if (plugin.settings.pagingStyle == 'scroll') {
					var paramScrollY;
					if (plugin.settings.scrollOverflow === 'fixed') {
						paramScrollY = plugin.settings.scrollFixedHeight;
					} else if (plugin.settings.scrollOverflow === 'adaptMax') {
						paramScrollY = plugin.settings.scrollMaxHeight;
					} else if (plugin.settings.scrollOverflow === 'adapt') {
						paramScrollY = calcularAlcadaLliure(
								$('div.dataTables_scrollBody', $taula.closest('div.dataTables_wrapper')),
								plugin.settings.scrollMinHeight);
					}
					dataTableOptions = $.extend({
						paging: true,
						scrollY: paramScrollY,
						scroller: {
							displayBuffer: plugin.settings.scrollBuffer,
							loadingIndicator: false
						},
						dom: domPrefix + 'tS'
					}, dataTableOptions);
					$(window).bind('resize', function() {
						waitForFinalEvent(function() {
							if (plugin.settings.scrollOverflow === 'adapt') {
								var alcadaLliure = calcularAlcadaLliure(
										$('div.dataTables_scrollBody', $taula.closest('div.dataTables_wrapper')),
										plugin.settings.scrollMinHeight);
								$('div.dataTables_scrollBody', $taula.closest('div.dataTables_wrapper')).height(alcadaLliure);
								$taula.dataTable().api().scroller.measure(false);
							}
						}, 50, "webutilDataTable#" + $taula.attr('id'));
					});
				}
				if(plugin.settings.saveState){
					dataTableOptions = $.extend({
						stateSave: true,
						stateSaveCallback: function(settings, data) {
							sessionStorage.setItem( 'DataTables_' + settings.sInstance, JSON.stringify(data) )
						}
					}, dataTableOptions);
					if(plugin.settings.mantenirPaginacio){
						dataTableOptions = $.extend({
							stateLoadCallback: function(settings) {
								return JSON.parse( sessionStorage.getItem( 'DataTables_' + settings.sInstance ) )
							}
						}, dataTableOptions);
					}
				}
			} else {
				dataTableOptions = $.extend({
					paging: false,
					pageLength: Number.MAX_SAFE_INTEGER,
					dom: domPrefix + 't'
				}, dataTableOptions);
			}
			// Configuració de la selecció de files
			if (plugin.settings.selectionEnabled) {
				dataTableOptions = $.extend({
					select: {
						style: 'multi',
						selector: 'td:first-child',
						info: false
					}
				}, dataTableOptions);
				var triggerSelectionChangeFunction = function (accio, indexosAfectats) {
					var api = $taula.dataTable().api();
					var numRows = api.data().length;
					var selectedRowsData = api.rows({
						search: 'applied',
						selected: true}).data();
					var numSelected = selectedRowsData.length;
					var $row = headerTrFunction();
					var $cell = $('th:first', $row);
					if (numSelected == 0) {
						$cell.empty().append('<span class="fa fa-square-o"></span>');
					} else if (numSelected < numRows) {
						$cell.empty().append('<span class="fa fa-minus-square-o"></span>');
					} else {
						$cell.empty().append('<span class="fa fa-check-square-o"></span>');
					}
					var afectatIds = [];
					for (var d = 0; d < indexosAfectats.length; d++) {
						afectatIds.push(api.row(indexosAfectats[d]).data()['DT_Id']);
					}
					var selectedIds = [];
					for (var d = 0; d < selectedRowsData.length; d++) {
						selectedIds.push(selectedRowsData[d]['DT_Id']);
					}
					$taula.trigger(
							'selectionchange.dataTable',
							[accio, afectatIds, selectedIds]);
				};
				$taula.on('select.dt', function (e, dt, type, indexes) {
					if (indexes) {
						for (var i = 0; i < indexes.length; i++) {
							var $row = $taula.dataTable().api()[type](indexes[i]).nodes().to$();
							var $cell = $('td:first', $row);
							$cell.html('<span class="fa fa-check-square-o"></span>');
							$('span', $cell).click(function() {
								$(this).parent().trigger('click');
							});
						}
						triggerSelectionChangeFunction('select', indexes);
					}
				});
				$taula.on('deselect.dt', function (e, dt, type, indexes) {
					if (indexes) {
						for (var i = 0; i < indexes.length; i++) {
							var $row = $taula.dataTable().api()[type](indexes[i]).nodes().to$();
							var $cell = $('td:first', $row);
							$cell.html('<span class="fa fa-square-o"></span>');
							$('span', $cell).click(function() {
								$(this).parent().trigger('click');
							});
						}
						triggerSelectionChangeFunction('deselect', indexes);
					}
				});
			}
			// Configuració del filtre
			if (plugin.settings.filtre) {
				$(':input', $(plugin.settings.filtre)).on('change', function(e) {
					$taula.dataTable().fnDraw();
				});
			}
			if (plugin.settings.filter) {
				var filterForm = $(plugin.settings.filter);
				var clickedButton;
				$('button', filterForm).on('click', function() {
					clickedButton = $(this);
				});
				filterForm.on('submit', function(event) {
					if ($(clickedButton).val() === 'netejar')
						$(this).webutilNetejarInputs();
					var formData = $(this).serialize() + "&" + $(clickedButton).attr('name') + "=" + $(clickedButton).val();
					$.ajax({
						type: $(this).attr('method'),
						url: $(this).attr('action'),
						data: formData,
						success: function() {
							$taula.DataTable().ajax.reload();
						}
					});
//					if ($(clickedButton).val() === 'netejar')
//						$(this).webutilNetejarInputs();
					return false;
				});
			}
			// Configuració taula editable
			if (plugin.settings.editableSampleRow && plugin.settings.editable !== false) {
				dataTableOptions = $.extend({
					keys: {
						columns: (plugin.settings.selectionEnabled) ? ':not(:first-child)' : '',
						keys: [38, 40, 9, 13] // ARROW_LEFT = 37, ARROW_UP = 38, ARROW_RIGHT = 39, ARROW_DOWN = 40, ENTER = 13, ESC = 27, TAB = 9,
				    }
				}, dataTableOptions);
				$('tfoot tr th:last-child a', $taula).click(function(e) {
					editableAccioCreate($(this).attr('href'), $(this).closest('tr'));
					return false;
				});
				$taula.on('key-focus', function(e, datatable, cell) {
					var $parentTbody = $(cell.node()).closest('tbody');
					var $parentTr = $(cell.node()).closest('tr');
					var $lastRow = $('tr[data-editing]', $parentTbody);
					var currentRow = cell.index().row;
					if (currentRow != $lastRow.index()) {
						var triggerOk = editableProcessarCanviRow($lastRow);
						if (triggerOk) {
							editableSeleccionarRow($parentTr, cell.index().row);
							$taula.trigger(
									'editablerowfocus.dataTable',
									[$parentTr, datatable.row(currentRow).data()]);
						}
					}
					var $currentInput = $('*:input:enabled', cell.node());
					if ($currentInput.length) {
						$currentInput.first().focus().select();
					} else {
						var shiftKey = false;
						if ($parentTbody.attr('data-clicked')) {
							$parentTbody.removeAttr('data-clicked');
						} else {
							if (currentRow < $lastRow.index()) {
								shiftKey = true;
							} else if (currentRow == $lastRow.index()) {
								var lastCol = $parentTbody.attr('data-last-col');
								var currentCol = cell.index().column;
								shiftKey = currentCol < lastCol;
							}
						}
						var keydownEvent = jQuery.Event('keydown.keyTable');
						keydownEvent.keyCode = 9;
						if (shiftKey) {
							keydownEvent.shiftKey = 'left';
						}
						$taula.trigger(keydownEvent);
					}
				});
				$taula.on('key-blur', function(e, datatable, cell) {
					var currentRow = cell.index().row;
					var currentCol = cell.index().column;
					var $parentTbody = $(cell.node()).closest('tbody');
					$parentTbody.attr('data-last-row', currentRow);
					$parentTbody.attr('data-last-col', currentCol);
					var $cellNode = $(cell.node());
					var dataTableCell = $taula.dataTable().api().cell(cell.node());
					var rowEditat = $(currentRow).attr('data-edited');
					$taula.trigger(
							'editablerowblur.dataTable',
							[$(currentRow), editableGetRowData($(currentRow)), rowEditat]);
				});
				$taula.on('keydown', function(e) {
					if (e.keyCode == 13) {
						if ($(e.target).data('toggle') != 'lov') {
							e.preventDefault();
							e.stopPropagation();
						}
					} else if (e.keyCode == 27) {
						$taula.dataTable().api().cell.blur();
						var $currentRow = $(e.target).closest('tr');
						if ($currentRow.data('camps-addicionals'))
							$currentRow = $currentRow.prev();
						editableNetejarEdicioRow($currentRow, false);
					}
				});
				$(':input', getEditableSampleRow()).on('focus', function() {
					if ($(this).closest('tfoot').length > 0) {
						var $rowEditing = $($taula.dataTable().api().row('[data-editing]').node());
						if ($rowEditing.length > 0) {
							editableNetejarEdicioRow($rowEditing, false);
						}
					}
				});
				if (plugin.settings.campsAddicionals) {
					$(plugin.settings.campsAddicionals).css('display', 'none');
					$(':input', plugin.settings.campsAddicionals).each(function() {
						if (!$(this).prop('disabled')) {
							$(this).prop('disabled', true);
							$(this).data('datatable-disabled', 'true');
						}
					});
				}
			}
			// Creació del datatable
			$taula.dataTable(dataTableOptions);
		}
		// Mètodes públics
		plugin.refresh = function(serverParams) {
			if (serverParams) {
				plugin.serverParams = serverParams;
			}
			$taula.dataTable().fnStandingRedraw();
		};
		plugin.refreshUrl = function(url) {
			$taula.dataTable().api().ajax.url(url).load();
		}
		plugin.selectNone = function() {
			$taula.dataTable().api().rows({search: 'applied'}).deselect();
		};
		plugin.selectAll = function() {
			$taula.dataTable().api().rows({search: 'applied'}).select();
		};
		plugin.selection = function(ids) {
			// TODO
		}
		plugin.changeUrl = function(url) {
			$taula.dataTable().api().ajax.url(url).load();
		}
		// Mètodes privats
		var headerTrFunction = function() {
			return $('thead:first tr', $taula.closest('.dataTables_wrapper'));
		}
		var getBaseUrl = function() {
			var baseUrl = plugin.settings.url;
			if (/datatable$/.test(baseUrl) || /datatable\/$/.test(baseUrl)) {
				return baseUrl.substring(0, baseUrl.indexOf("/datatable"));
			}
			return baseUrl;
		}
		var renderEnums = [];
		var renderFunction = function (data, type, row, meta) {
			var api = new $.fn.dataTable.Api(meta.settings);
			var index = api.column(meta.col).index('visible');
			var $th = $('th:nth-child(' + (index + 1) + ')', headerTrFunction());
			var renderHtml = data;
			var template = $th.attr('data-template');
			if (template) {
				var renderHtml = $(template).render(
					api.row(meta.row).data(), {
						alert: function(text) {
							alert(text);
						},
						eval: function(expression) {
							return eval(expression);
						}
					}
				);
			} else if (data) {
				var converter = $th.attr('data-converter');
				var renderer = $th.attr('data-renderer');
				if (converter) {
					if (converter.indexOf('date') === 0 || converter.indexOf('time') != -1) {
						var date = new Date(data);
						var horaAmbFormat = "";
						var dataAmbFormat;
						if (converter.indexOf('time') != -1) {
							var hores = ("00" + date.getHours()).slice(-2);
							var minuts = ("00" + date.getMinutes()).slice(-2);
							var segons = ("00" + date.getSeconds()).slice(-2);
							horaAmbFormat = hores + ":" + minuts + ":" + segons;
						}
						if (converter.indexOf('date') === 0) {
							var dia = ("00" + date.getDate()).slice(-2);
							var mes = ("00" + (date.getMonth() + 1)).slice(-2);
							var any = date.getFullYear();
							dataAmbFormat = dia + "/" + mes + "/" + any;
							if (converter == 'datetime') {
								dataAmbFormat += " " + horaAmbFormat;
							}
						} else {
							dataAmbFormat = horaAmbFormat;
						}
						renderHtml = dataAmbFormat;
					}
				} else if (renderer) {
					if (renderer.indexOf('maxLength') >= 0) {
						var sufix = '...';
						var maxLength = 100;
						var inici = renderer.indexOf('(');
						var fin = renderer.indexOf(')');
						if (inici != -1 && fin != -1) {
							maxLength = renderer.substring(inici + 1, fin); 
						}
						if (data.length > maxLength) {
							renderHtml = data.substring(0, maxLength - sufix.length) + sufix;
						}
					} else if (renderer.indexOf('price') >= 0) {
						var numDecimals = 0;
						if (data.indexOf('.') >= 0) {
							var numDecimals = data.length - (data.indexOf('.') + 1)
						}
						renderHtml = $.number(data, numDecimals);
					} else if (renderer.indexOf('enum') >= 0) {
						var enumClass = '?'; 
						var inici = renderer.indexOf('(');
						var fin = renderer.indexOf(')');
						if (inici != -1 && fin != -1) {
							enumClass = renderer.substring(inici + 1, fin);
						}
						renderHtml =  'enum(' + enumClass + ', ' + data + ')';
						if (!renderEnums[enumClass]) {
							$.ajax({
								url: webutilAjaxEnumPath(enumClass),
								async: false,
								success: function(resposta) {
									renderEnums[enumClass] = resposta;
								}
							});
						}
						for (var i = 0; i < renderEnums[enumClass].length; i++) {
							var enumItem = renderEnums[enumClass][i];
							if (enumItem['value'] == data) {
								renderHtml = enumItem['text'];
								break;
							}
						}
					} else if (renderer.indexOf('column') >= 0) {
						var inici = renderer.indexOf('(');
						var fin = renderer.indexOf(')');
						var columnName;
						if (inici != -1 && fin != -1) {
							columnName = renderer.substring(inici + 1, fin); 
						}
						if (typeof columnName != 'undefined') {
							var rowData = api.row(meta.row).data();
							renderHtml = rowData[columnName];
						}
					}
				}
			}
			return renderHtml;
		}
		var getEditableSampleRow = function() {
			return $(plugin.settings.editableSampleRow).last();
		}
		var editableCellChange = function() {
			var $currentRow = $('tr[data-editing]', $('tbody', $taula));
			$currentRow.attr('data-edited', 'true');
			$taula.trigger(
					'editablecellchange.dataTable',
					[$(this), editableGetRowData($currentRow)]);
		}
		var editableSeleccionarRow = function($row, rowIndex) {
			$row.attr('data-editing', 'true');
			$row.children().each(function() {
				var $thCapcalera = $('thead th:nth-child(' + ($(this).index() + 1) + ')', $taula);
				if ($thCapcalera.data('col-name')) {
					var $sampleContent = $('th:nth-child(' + ($(this).index() + 1) + ')', getEditableSampleRow()).children();
					if ($sampleContent.length) {
						var $cellNode = $(this);
						var dataTableCell = $taula.dataTable().api().cell(this);
						var $sampleInput = ($sampleContent.is(':input')) ? $sampleContent : $(':input', $sampleContent);
						var $contingutClonat = $sampleContent.webutilClonarElementAmbInputs(
								$sampleInput.attr('id') + rowIndex,
								dataTableCell.data());
						var $inputClonat = ($contingutClonat.is(':input')) ? $contingutClonat : $(':input', $contingutClonat);
						var converter = $thCapcalera.attr('data-converter');
						if (converter) {
							$inputClonat.val(dataTableCell.render('display'));
						}
						$cellNode.empty().append($contingutClonat);
						$inputClonat.on('change', editableCellChange);
					}
				}
			});
			if (plugin.settings.campsAddicionals) {
				var $rowCampsAddicionals = $('<tr data-camps-addicionals="true"><td colspan="' + $row.children().length + '"></td></tr>').insertAfter($row);
				$('td:first', $rowCampsAddicionals).attr('class', 'form-inline');
				$(plugin.settings.campsAddicionals).children().appendTo($('td:first', $rowCampsAddicionals));
				var rowData = $taula.dataTable().api().row($row).data();
				$(':input', $rowCampsAddicionals).each(function() {
					if ($(this).data('datatable-disabled'))
						$(this).prop('disabled', false);
					var inputName = $(this).prop('name');
					var inputData = ($(this).data('field-path')) ?
							eval('rowData.' + $(this).data('field-path')) :
							rowData[inputName];
					if (!$(this).is('[type="checkbox"]')) {
						if (inputData)
							$(this).val(inputData);
						else
							$(this).val('');								
					} else {
						$(this).prop('checked', inputData);
					}
				});
				$(':input', $rowCampsAddicionals).on(
						'change',
						editableCellChange);
			}
		}
		var editableNetejarEdicioRow = function($row, actualitzarData) {
			$row.removeAttr('data-editing');
			$row.removeAttr('data-edited');
			$row.children().each(function() {
				var $cellNode = $(this);
				var $thCapcalera = $('thead th:nth-child(' + ($cellNode.index() + 1) + ')', $taula);
				if ($thCapcalera.data('col-name')) {
					var $sampleContent = $('th:nth-child(' + ($cellNode.index() + 1) + ')', getEditableSampleRow()).children();
					if ($sampleContent.length) {
						var dataTableCell = $taula.dataTable().api().cell(this);
						if (actualitzarData) {
							modificarDatatableDataAmbValorInput(
									$row,
									$(':input', $cellNode));
						}
						$cellNode.webutilDestroyInputComponents();
						$cellNode.empty().html(dataTableCell.render('display'));
					}
				}
			});
			if (plugin.settings.campsAddicionals) {
				var $rowCampsAddicionals = $row.next();
				$(':input', $rowCampsAddicionals).each(function() {
					if (actualitzarData) {
						modificarDatatableDataAmbValorInput(
								$row,
								$(this));
					}
					$(this).val('');
					if ($(this).data('datatable-disabled'))
						$(this).attr('disabled', true);
					$(this).off('change', editableCellChange);
				});
				$('td:first', $rowCampsAddicionals).children().appendTo($(plugin.settings.campsAddicionals));
				$rowCampsAddicionals.remove();
			}
		}
		var editableGetRowData = function($row, incloureCampsAddicionals) {
			var rowData = {};
			$('td,th', $row).each(function() {
				var $contingutInput = $(':input', this).first();
				if ($contingutInput.length) {
					var $inputPeu = $('th:nth-child(' + ($(this).index() + 1) + ') :input', getEditableSampleRow());
					rowData[$inputPeu.prop('name')] = ($contingutInput.is(':checkbox')) ? $contingutInput.prop('checked') : $contingutInput.val();
				} else {
					var $thCapcalera = $('thead th:nth-child(' + ($(this).index() + 1) + ')', $taula);
					var colName = $thCapcalera.data('col-name');
					if (colName) {
						rowData[colName] = $taula.dataTable().api().row($row).data()[colName];
					}
				}
			});
			if (incloureCampsAddicionals && plugin.settings.campsAddicionals) {
				var $rowCampsAddicionals = $row.next();
				$(':input', $rowCampsAddicionals).each(function() {
					if ($(this).val()) {
						var inputName = $(this).prop('name');
						rowData[inputName] = ($(this).is(':checkbox')) ? $(this).prop('checked') : $(this).val();
					}
				});
			}
			return rowData;
		}
		var editableProcessarCanviRow = function($row) {
			var triggerOk = true;
			var rowEditat = $row.attr('data-edited');
			if (rowEditat) {
				var rowData = editableGetRowData($row);
				var resultat = {resultat: false};
				var apiRowData = $taula.dataTable().api().row($row).data();
				editableAccioUpdate(
						getBaseUrl() + '/' + apiRowData['DT_Id'],
						$row,
						rowData,
						resultat);
				triggerOk = resultat.resultat;
				if (triggerOk) {
					var bgOrigen = $row.css('background-color');
					$row.animate(
							{backgroundColor: '#dff0d8'},
							800,
							"swing").animate(
							{backgroundColor: bgOrigen},
							600,
							"swing");
				}
			}
			if (triggerOk && $row.length > 0) {
				editableNetejarEdicioRow($row, true);
			} else {
				$taula.dataTable().api().cell($row.index(), 0).focus();
			}
			return triggerOk;
		}
		var editableAccioCreate = function(createUrl, $row) {
			$taula.trigger(
					'beforerowcreate.dataTable',
					[$row, rowData]);
			var rowData = editableGetRowData($row, false);
			var csrf = $('form input[name="_csrf"]:first').val();
			$row.webutilNetejarErrorsCamps();
			$.ajax({
				type: 'POST',
				url: createUrl,
				data: $.extend(rowData, {'_csrf': csrf}),
				async: true,
				success: function(resposta) {
					if (resposta.estatError) {
						$row.webutilMostrarErrorsCamps(
								resposta.errorsCamps);
					} else {
						$taula.trigger(
								'afterrowcreate.dataTable',
								[$row, rowData]);
						$row.webutilNetejarInputs();
						$taula.dataTable().fnDraw();
					}
				}
			});
		}
		var editableAccioUpdate = function(updateUrl, $row, rowData, resultat) {
			$taula.trigger(
					'beforerowchange.dataTable',
					[$row, rowData]);
			var csrf = $('form input[name="_csrf"]:first').val();
			$.ajax({
				type: 'POST',
				url: updateUrl,
				data: $.extend(rowData, {'_csrf': csrf}),
				async: false,
				success: function(resposta) {
					if (resposta.estatError) {
						$row.webutilMostrarErrorsCamps(
								resposta.errorsCamps);
					} else {
						resultat.resultat = true;
						$taula.trigger(
								'afterowchange.dataTable',
								[$row, rowData]);
						$row.webutilNetejarInputs();
						$taula.dataTable().fnDraw();
					}
				}
			});
		}
		var editableAccioDelete = function(deleteUrl) {
			$taula.trigger(
					'beforerowdelete.dataTable');
			$.ajax({
				type: 'GET',
				url: deleteUrl,
				async: true,
				success: function(resposta) {
					if (resposta.estatError) {
					} else {
						$taula.trigger(
								'afterrowdelete.dataTable');
						$taula.dataTable().fnDraw();
					}
				}
			});
		}
		var modificarDatatableDataAmbValorInput = function($row, $input) {
			var $headerRow = headerTrFunction();
			var $headerCell = $('th[data-col-name="' + $input.attr('name') + '"]', $headerRow);
			var rowData = $taula.dataTable().api().row($row).data();
			var cellData = rowData[$input.attr('name')];
			if ($input.attr('type') != 'checkbox') {
				var inputValConverter = $input.val();
				var converter = $headerCell.attr('data-converter');
				if (converter && converter.indexOf('date') === 0) {
					var parts = $input.val().split("/");
					var partsData = new Date(parts[2], parts[1] - 1, parts[0], 0, 0, 0, 0);
					inputValConverter = partsData.getTime();
				}
				if ((cellData || $input.val()) && cellData != inputValConverter) {
					rowData[$input.attr('name')] = inputValConverter;
				}
			} else {
				rowData[$input.attr('name')] = $input.prop('checked');
			}
		}
		// Inicialització del plugin
        plugin.init();
	}

	$.fn.webutilDatatable = function(options, param1) {
		var pluginName = 'webutilDatatable';
        return this.each(function() {
            if (undefined == $(this).data(pluginName)) {
                var plugin = new $.webutilDatatable(this, options);
                $(this).data(pluginName, plugin);
            } else if (options && typeof options !== 'object') {
            	if ('refresh' === options) {
            		$(this).data(pluginName).refresh(param1);
            	} else if ('refresh-url' === options) {
            		$(this).data(pluginName).changeUrl(param1);
            	} else if ('select-none' === options) {
            		$(this).data(pluginName).selectNone();
            	} else if ('select-all' === options) {
            		$(this).data(pluginName).selectAll();
            	} else if ('selection' === options) {
            		$(this).data(pluginName).selection(param1);
            	}
            }
        });
    }

	$(document).ready(function() {
		$('table[data-toggle="datatable"]').each(function() {
			$(this).webutilDatatable();
		});
	});
	
	$.fn.dataTableExt.oApi.fnStandingRedraw = function(oSettings) {
	    // redraw to account for filtering and sorting
	    // concept here is that (for client side) there is a row got inserted at the end (for an add)
	    // or when a record was modified it could be in the middle of the table
	    // that is probably not supposed to be there - due to filtering / sorting
	    // so we need to re process filtering and sorting
	    // BUT - if it is server side - then this should be handled by the server - so skip this step
	    if(oSettings.oFeatures.bServerSide === false){
	        var before = oSettings._iDisplayStart;
	        oSettings.oApi._fnReDraw(oSettings);
	        //iDisplayStart has been reset to zero - so lets change it back
	        oSettings._iDisplayStart = before;
	        oSettings.oApi._fnCalculateEnd(oSettings);
	    }
	    //draw the 'current' page
	    oSettings.oApi._fnDraw(oSettings);
	};

	$.fn.dataTableExt.oApi.removeFromSessionStorage = function(item) {
		sessionStorage.removeItem( 'DataTables_' + item);
	};
	

}(jQuery));