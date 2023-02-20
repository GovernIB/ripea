<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
    <title><spring:message code="config.titol"/></title>
    <script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
    <script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
    <link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
    <link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
    <link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
    <script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
    <script src="<c:url value="/webjars/select2/4.0.5/dist/js/i18n/${requestLocale}.js"/>"></script>
    <script src="<c:url value="/webjars/jquery-ui/1.12.1/jquery-ui.min.js"/>"></script>
    <link href="<c:url value="/webjars/jquery-ui/1.12.1/jquery-ui.css"/>" rel="stylesheet"></link>
    <link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
    <script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
    <script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>
    <script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
    <script src="<c:url value="/js/webutil.common.js"/>"></script>
    <script src="<c:url value="/js/webutil.datatable.js"/>"></script>
    <script src="<c:url value="/js/webutil.modal.js"/>"></script>
<%--    <script src="<c:url value="/js/jquery.fileDownload.js"/>"></script>--%>
    <script src="<c:url value="/js/configProperties/configProperties.js"/>"></script>
</head>
<body>

<style>
.new-conf-organ {
    padding-top: 4px;
    padding-bottom: 4px;
    margin-bottom: -30px;
}
.configurable {
	cursor: pointer;
}

.table-organ-key {
	font-size: 11px;
	color: #aaa;
}

</style>

<script>



$(document).ready(function() {

    $(".btnDesplegable").click(e => {

        e.stopPropagation();

        var configForm = $(e.currentTarget).closest('form');
        var configFormKey = configForm.attr('id');

        
        var isEntitatConfigSelected = isNowChecked(configFormKey, true);
        var isOrganConfigSelected = isNowChecked(configFormKey, false);



        let formGroupEntitats = configForm.find('.entitats-config');
        let formGroupOrgans = configForm.find('.organs-config');
        
        let span = $(e.currentTarget).find("span");
        var isExpanded = span.attr('class') === 'fa fa-caret-up';

        if (isExpanded) {
            
            span.removeClass("fa-caret-up");
            span.addClass("fa-caret-down");
            addSeparador(e.target);
            formGroupEntitats.hide();
            formGroupOrgans.hide();
            
		} else {

	        // ############################################# ENTITATS ###########################################
	        if (isEntitatConfigSelected) {

	            $.ajax({
	                type: "GET",
	                url: "config/entitat",
					data: { 
						key: e.currentTarget.id
					 },
	                success: entitats => {

	                    if (!entitats) {
	                        return;
	                    }

	                    expand(configFormKey, isEntitatConfigSelected, isOrganConfigSelected, true);

	                    for (let entitat of entitats) {
	                        let keyReplaced = entitat.key.replaceAll('.', '-');
	                        let string = '<div>';
	                        let disabled = entitat.jbossProperty || !entitat.value ? 'disabled' : '';
	                        let textGray = disabled ? "text-gray" : "";
	                        string += '<label id="' + keyReplaced + '_codi_entitat" for="entitat_config_' + keyReplaced + '" class="col-sm-3 control-label margin-bottom ' + textGray + '" style="word-wrap: break-word;">- ' + entitat.entitatCodi + '</label>';
	                        string += '<div class="col-sm-7 margin-bottom div-form-control">';
	                        let placeHolder = "placeholder=" + entitat.key;
	                        let configurable = "";
	                        if (entitat.typeCode === "INT") {
	                            string += '<input id="' + keyReplaced + '" class="form-control entitat-input" type="number" maxlength="2048" value="' + entitat.value + '"' + disabled + ' ' + placeHolder + '>';
	                        } else if(entitat.typeCode === "FLOAT") {
	                            string += '<input id="' + keyReplaced + '" class="form-control entitat-input" type="number" step="0.01" maxlength="2048" value="' + entitat.value + '"' + disabled + ' ' + placeHolder + '>';
	                        } else if(entitat.typeCode === "CREDENTIALS") {
	                            string += '<input id="' + keyReplaced + '" class="form-control entitat-input" type="password" maxlength="2048" value="' + entitat.value + '"' + disabled + ' ' + placeHolder + '>';
	                        } else if(entitat.typeCode === "BOOL") {
	                            let checked = entitat.value === "true" ? 'checked' : '';
	                            string += '<input id="' + keyReplaced + '" name="booleanValue" class="visualitzar entitat-input" type="checkbox" ' + disabled + ' ' + checked + '>';
	                        } else if (entitat.validValues && entitat.validValues.length > 2) {
	                            string += '<select id="' + keyReplaced + '" class="form-control" ' + disabled + '>';
	                            let selected = "";
	                            string += '<option value=""></option>';
	                            entitat.validValues.map(x => {
	                                selected = x === entitat.value ? "selected" : "";
	                                string += '<option value="' + x + '"' + ' ' + selected + '>' + x + '</option>';
	                            });
	                            string += '<select>';
	                        } else if (entitat.validValues && entitat.validValues.length === 2) {
	                            let checked = entitat.validValues[0] === entitat.value ? 'checked="checked"' : "";
	                            let checked2 = entitat.validValues[1] === entitat.value ? 'checked="checked"' : "";
	                            string += '<div id="' + keyReplaced + '" class="visualitzar entitat-input radio-div">'
	                                + '<label id="' + keyReplaced+ '_radio_1" for="' + keyReplaced + '_1" class="radio-inline ' + textGray + '">'
	                                + '<input id="' + keyReplaced + '_1" name="' + keyReplaced + '" type=radio value="' + entitat.validValues[0] + '"' + ' ' + checked + ' ' + disabled + '>'
	                                + entitat.validValues[0]
	                                + '</label>'
	                                + '<label id="' + keyReplaced+ '_radio_2" for="' + keyReplaced+ '_2" class="radio-inline ' + textGray + '">'
	                                + '<input id="' + keyReplaced + '_2" name="' + keyReplaced + '" type=radio value="' + entitat.validValues[1] + '"' + ' ' + checked2 + ' ' + disabled + '>'
	                                + entitat.validValues[1]
	                                + '</label></div>';
	                        } else {
	                            string += '<input id="' + keyReplaced + '" class="form-control" type="text" maxlength="2048" value="'
	                                + (entitat.value ? entitat.value : "" )+ '"' + disabled + ' ' + placeHolder + '>';
	                        }
	                        string +='<div><div id="'+ keyReplaced + '_key" class="display-inline"><span id="' + keyReplaced+ '_key_entitat" class="help-block display-inline ' + textGray + '"> ' + entitat.key + '</span></div>';
	                        string += '</div></div>'
	                        string += '<div class="col-sm-1 margin-bottom flex-space-between">';
	                        if (!entitat.jbossProperty) {
	                            let saveDelete = entitat.value ? "" : "no-display";
	                            let config = !entitat.value ? "" : "no-display";
	                            string += '<button id="' + keyReplaced + '_button_save" name="' + entitat.entitatCodi + '" type="button" class="btn btn-success entitat-save ' + saveDelete + '"><i class="fa fa-save"></i></button>';
	                            string += '<button id="' + keyReplaced + '_button_trash" name="' + entitat.entitatCodi + '" type="button" class="btn btn-danger entitat-trash ' + saveDelete + '"><i class="fa fa-trash"></i></button>';
	                            string += '<button id="' + keyReplaced + '_button_config" name="' + entitat.entitatCodi + '" type="button" class="btn btn-success entitat-config ' + config + '"><i class="fa fa-pencil"></i></button>';
	                        }
	                        string += '</div></div>';
	                        formGroupEntitats.append(string);
	                    }

	                    $("select", formGroupEntitats).select2({
	                        theme: "bootstrap",
	                        allowClear: true,
	                        minimumResultsForSearch: -1,
	                        placeholder: ""
	                    });

	                    $(".entitat-save").click(e =>  entitatSave(e));
	                    $(".entitat-trash").click(e => entitatTrash(e));
	                    $(".entitat-config").click(e => entitatConfig(e));
	                }
	            });
	        }



	        
	     	// ############################################# ORGANS #############################################
	        if (isOrganConfigSelected) {
		                

                expand(configFormKey, isEntitatConfigSelected, isOrganConfigSelected, false);

                var keyUnderscore = configFormKey.replace("config_", "");
                var tableId = 'taulaOrgans_' + keyUnderscore;

                let btnNewConfOrgan = '<div class="text-right">';
                btnNewConfOrgan += '<a href="<c:url value="/config/organ/new?key=' + e.currentTarget.id + '"/>" data-toggle="modal" data-refresh-datatable="true" data-datatable-id="' + tableId + '" class="btn btn-default new-conf-organ"><span class="fa fa-plus"></span> <spring:message code="config.propietats.btn.new.conf.organ"/></a>';
                btnNewConfOrgan += '</div>';
                
                formGroupOrgans.append(btnNewConfOrgan);

       			$('[data-toggle="modal"]', formGroupOrgans).each(function() {
       				if (!$(this).attr('data-modal-eval')) {
       					$(this).webutilModal();
       					$(this).attr('data-modal-eval', 'true');
       				}
       			});

        		
        		var url = '<c:url value="/config/' + keyUnderscore + '/datatable"/>';
        		var scriptAccionsId = 'cellAccionsTemplate_' + keyUnderscore;
        		var scriptValueId = 'cellValueTemplate_' + keyUnderscore;

        		var tableHtml ='';
        		tableHtml += '<table '
        		tableHtml += 'id="'+tableId+'" '
        		tableHtml += 'data-toggle="datatable" ' 
        		tableHtml += 'data-url="'+url+'" ' 
        		tableHtml += 'class="table table-bordered table-striped table-hover" '
        		tableHtml += 'data-default-order="2" '
        		tableHtml += 'data-default-dir="desc" '
	        	tableHtml += 'data-paging-enabled="false" '		        		
        		tableHtml += 'style="width:100%"> '
        		tableHtml += '<thead> '
        		tableHtml += '	<tr> '
            	tableHtml += '		<th data-col-name="typeCode" data-visible="false"></th> '            		
                tableHtml += '		<th data-col-name="key" data-visible="false"></th> '                   	
        		tableHtml += '		<th data-col-name="organGestorCodiNom" data-orderable="true" width="45%"><spring:message code="metaexpedient.form.camp.organgestor"/></th> '
        		tableHtml += '		<th data-col-name="value" data-template="#' + scriptValueId + '" data-orderable="true" width="45%"><spring:message code="config.propietats.form.camp.value"/> '
        		tableHtml += '			<script id="' + scriptValueId + '" type="text/x-jsrender"> '  
        		tableHtml += '				<div style="min-height: 20px;"> '        		     	
        		tableHtml += '				{{if typeCode == "BOOL"}} '
                tableHtml += '					{{if value == "true"}}<span class="fa fa-check"></span>{{/if}} '  	            		
            	tableHtml += '				{{else typeCode == "PASSWORD"}} '    	
                tableHtml += '					{{:value}} '                       	
                tableHtml += '				{{else}} '  	
                tableHtml += '					{{:value}} '                      
                tableHtml += '				{{/if}} '   
                tableHtml += '				</div> '                     
                tableHtml += '				<div class="table-organ-key">{{:key}}</div> '                                             						
            	tableHtml += '				</div> '                	          		
        		tableHtml += '			</'+'script> '
       			tableHtml += '		</th> '        		
        		tableHtml += '		<th data-col-name="key" data-template="#' + scriptAccionsId + '" data-orderable="false" width="1%"> '
        		tableHtml += '			<script id="' + scriptAccionsId + '" type="text/x-jsrender"> '       	
        		tableHtml += '				<div class="flex-space-between"> '
        		tableHtml += '					<a href="<c:url value="/config/organ/update?key={{:key}}"/>" data-toggle="modal" data-refresh-datatable="true" data-datatable-id="' + tableId + '" class="btn btn-success"><i class="fa fa-pencil"></i></a> '
            	tableHtml += '					<a href="<c:url value="/config/organ/delete?key={{:key}}"/>" data-toggle="ajax" data-confirm="<spring:message code="config.propietats.confirmacio.esborrar"/>" class="btn btn-danger"><i class="fa fa-trash"></i></a> '  
            	tableHtml += '				</div> '                	          		
        		tableHtml += '			</'+'script> '
       			tableHtml += '		</th> '
       			tableHtml += '	</tr> '
       			tableHtml += '</thead> '
       			tableHtml += '</table> '
        			

				formGroupOrgans.append(tableHtml);
                   
				$('table[data-toggle="datatable"]', formGroupOrgans).each(function() {
					$(this).webutilDatatable();
				});
	            
	        }

		}
        
    });

	
       
    $("#btn-sync").on("click", function () {
        $.get('<c:url value="/config/sync"/>', function( data ) {
            $('#syncModal-body').html(
                '<div class="datatable-dades-carregant" style="text-align: center; padding-bottom: 100px;">' +
                '	<span class="fa fa-circle-o-notch fa-spin fa-3x"></span> <br>' +
                '   Sincronitzant propietats de l\'aplicació ' +
                '</div>');
            if (data.status) {
                let message = "S'han actualitzat satisfactoriament les següents propietats: ";
                data.editedProperties.forEach( element => message += element + ", ");
                alert(message);
                document.location.reload();
            } else {
                alert("Error actualitzant les propietats desde JBoss.");
            }
        });
    });

    
     <c:url var="urlEdit" value="/config/update"/>
     $(".form-update-config").submit(function(e) {

         e.preventDefault();
         let formData = new FormData(this);
         let id = "config_" + formData.get("key");
         let spinner = addSpinner(id);

         $.ajax({
             url: "${urlEdit}",
             type: "post",
             processData: false,
             contentType: false,
             enctype: "multipart/form-data",
             data: formData,
             success: data => {
                 removeSpinner(spinner);
                 mostrarMissatge(id, data);
             }
         });
     });
     $('.a-config-group:first').tab('show');





	$(".configurable").on("click", function () {

       	
       	var configFormKey = $(this).closest('form').attr('id');

       	var key = configFormKey.replace("config_", "").replaceAll("-", ".");

       	let spinner = addSpinner('config_' + key);

       	var isClickedEntitat;
       	if ($(this).attr('class').includes('entitat')) {
       		isClickedEntitat = true;
		} else {
			isClickedEntitat = false;
		}
       	
       	
       	var clickedCheckboxValue = isNowChecked(configFormKey, isClickedEntitat);

       	var anotherCheckboxValue = isNowChecked(configFormKey, !isClickedEntitat);


       	var url; 
		if (isClickedEntitat) {
			url = "<c:url value='/config/configurableEntitat'/>";
		} else {
			url = "<c:url value='/config/configurableOrgan'/>";
		}
       	
   		$.ajax({
   			type: 'GET',
   			url: url,
			data: { 
				key: key, 
				configurable: !clickedCheckboxValue
			 },    			
   			success: function(data) {
				if (isClickedEntitat) {
					$('#' + configFormKey).find('a.configurable.entitat').find('span').attr('class', !clickedCheckboxValue ? faChecked : faUnchecked);
				} else {
					$('#' + configFormKey).find('a.configurable.organ').find('span').attr('class', !clickedCheckboxValue ? faChecked : faUnchecked);
				}
				
				if (!clickedCheckboxValue || anotherCheckboxValue) {
					$('#' + configFormKey).find('.btnDesplegable').show();
				} else {
					$('#' + configFormKey).find('.btnDesplegable').hide();
				}

				 removeSpinner(spinner);
				 mostrarMissatge('config_' + key, data);


				 let span = $('#' + configFormKey).find(".btnDesplegable span");
				 var isExpanded = span.attr('class') === 'fa fa-caret-up';
				 if (isExpanded) {
					 $('#' + configFormKey).find(".btnDesplegable").click();
				}
   			}
   		});
	});


       
       
});

function expand(configFormKey, isEntitatConfigSelected, isOrganConfigSelected, entitat) {

	let formGroupGeneral = $('#' + configFormKey).children('.form-group').eq(0);
	let formGroupEntitats = $('#' + configFormKey).children('.form-group').eq(1);
	let formGroupOrgans = $('#' + configFormKey).children('.form-group').eq(2);


	formGroupGeneral.removeClass("separador");
	if (isEntitatConfigSelected && isOrganConfigSelected) {
		formGroupEntitats.removeClass("separador");
		formGroupOrgans.addClass("separador");
	} else if (isEntitatConfigSelected && !isOrganConfigSelected) {
		formGroupEntitats.addClass("separador");
		formGroupOrgans.removeClass("separador");
	} else if (!isEntitatConfigSelected && isOrganConfigSelected) {
		formGroupEntitats.removeClass("separador");
		formGroupOrgans.addClass("separador");
	}

	if (entitat) {
		formGroupEntitats.empty()
	    formGroupEntitats.show();
	} else {
	    formGroupOrgans.empty()
	    formGroupOrgans.show();
	}

	let span = $('#' + configFormKey).find(".btnDesplegable span");
    span.removeClass("fa-caret-down");
    span.addClass("fa-caret-up");
}  




var faChecked = 'fa fa-check-square-o';
var faUnchecked = 'fa fa-square-o';

  
function isNowChecked(configFormKey, checkEntitat) {
	var faClass;
	if(checkEntitat){
		faClass = $('#' + configFormKey).find('a.configurable.entitat').find('span').attr('class');
	} else {
		faClass = $('#' + configFormKey).find('a.configurable.organ').find('span').attr('class');
	}

	var isChecked;
   	if (faClass === faChecked) {
   		isChecked = true;
	} else if (faClass === faUnchecked){
		isChecked = false;
	}   	   	

  	return isChecked;
}    
   

</script>
<div class="text-right" data-toggle="botons-titol">
    <a id="btn-sync" class="btn btn-default" data-toggle="modal" data-target="#syncModal"><span class="fa fa-refresh"></span>&nbsp;Sincronitzar amb JBoss</a>
</div>
<div id="syncModal" class="modal fade" role="dialog">
    <div class="modal-dialog">

        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">Sincronitzant propietats</h4>
            </div>
            <div id="syncModal-body" class="modal-body">
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Tanca</button>
            </div>
        </div>
    </div>
</div>
<div class="row">
    <div class="col-md-3">
        <ul id="tab-list" class="nav nav-pills nav-stacked">
            <c:forEach items="${config_groups}" var="group" varStatus="status_group">
                <li role="presentation">
                    <a class="a-config-group" data-toggle="tab" href="#group-${group.key}">${group.description}</a>
                </li>
            </c:forEach>
        </ul>
    </div>
    <div class="col-md-9">
        <div class="tab-content">
            <c:forEach items="${config_groups}" var="group" varStatus="status_group">
                <c:set var="group" value="${group}" scope="request"/>
                <c:set var="level" value="0" scope="request"/>
                <div id="group-${group.key}" class="tab-pane fade">
                    <jsp:include page="includes/configGroup.jsp"/>
                </div>
            </c:forEach>
        </div>
    </div>
</div>
</body>
</html>
