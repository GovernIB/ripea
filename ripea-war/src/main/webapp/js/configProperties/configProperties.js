"use strict";

let getValueRadio = elem => {
    let inputs = $(elem).find("input");
    if (!inputs || (inputs && inputs.length !== 2)) {
        return null;
    }
    return $(inputs[0]).is(":checked") ? inputs[0].value : $(inputs[1]).is(":checked") ? inputs[1].value : null;
};

let removeValueRadio = elem => $(elem).find('input:radio').attr("checked", false);

let addSpinner = id => {

    let spinner;
    if (!document.getElementById(id + "_spinner")) {
        spinner = document.createElement("span");
        spinner.setAttribute("aria-hidden", true);
        spinner.className = "fa fa-circle-o-notch fa-spin fa-1x spinner-config";
        spinner.setAttribute("id", id + "_spinner");
        let elem = document.getElementById(id + "_key");
        elem.append(spinner);
    }
    return spinner;
};

let removeSpinner = spinner =>  {
    if (spinner) {
        spinner.remove();
    }
};

let addSeparador = tag => $(tag).closest(".form-group").addClass("separador");

let removeSeparador = tag => $(tag).closest(".form-group").removeClass("separador");

let mostrarMissatge = (id, data) => {

    if (!data) {
        return;
    }
    let elem = document.getElementById(id);
    elem = !elem ? document.getElementById(id + "_1") : elem;
    let tagId = elem.getAttribute("id") + "_msg";
    let msg = document.getElementById(tagId);
    if (msg) {
        let el = document.getElementById(msg);
        if (el) {
            el.remove();
        }
    }
    let div = document.createElement("div");
    div.setAttribute("id", tagId);
    div.className = "flex-space-between alert-config " +  (data.status === 1 ?  "alert-config-ok" : "alert-config-error");
    div.append(data.message);
    let span = document.createElement("span");
    span.className = "fa fa-times alert-config-boto";
    div.append(span);
    elem.closest(".col-sm-7").append(div);
    span.addEventListener("click", () => div.remove());
    window.setTimeout(() => div ? div.remove() : "", data.status === 1 ? 2250 : 4250);
};

let getInputValue = elem =>  ($(elem).is(':checkbox') ? $(elem).is(":checked") : $(elem).is("div") ? getValueRadio(elem) : $(elem).val());

let guardarPropietat = (configKey, natejar) => {

    let configKeyReplaced = configKey.replaceAll("_",".");
    let spinner = addSpinner(configKey);
    let elem = $("#" + configKey);
    let value = !natejar ? getInputValue(elem) : null;
    let formData = new FormData();
    formData.append("key", configKeyReplaced);
    formData.append("value", value);
    $.ajax({
        url: "/ripea/config/update",
        type: "post",
        processData: false,
        contentType: false,
        enctype: "multipart/form-data",
        data: formData,
        success: data => {
            removeSpinner(spinner);
            mostrarMissatge(configKey + "_key", data);
        }
    });
};

let entitatConfig = e => {

    let current = e.currentTarget;
    let tag = $("#" + current.id.replace("_button_config", ""));
    if ($(tag).hasClass("radio-div")) {
        $("#" + current.id.replace("_button_config", "_1")).first().removeAttr("disabled");
        $("#" + current.id.replace("_button_config", "_2")).first().removeAttr("disabled");
        $("#" + current.id.replace("_button_config", "_radio_1")).removeClass("text-gray");
        $("#" + current.id.replace("_button_config", "_radio_2")).removeClass("text-gray");
    } else {
        $("#" + current.id.replace("_button_config", "")).removeAttr("disabled");
    }
    $(current).addClass("no-display");
    $("#" + current.id.replace("_button_config", "_button_save")).removeClass("no-display");
    $("#" + current.id.replace("_button_config", "_button_trash")).removeClass("no-display");
    $("#" + current.id.replace("_button_config", "_key_entitat")).removeClass("text-gray");
    $("#" + current.id.replace("_button_config", "_codi_entitat")).removeClass("text-gray");
}

let entitatSave = e =>  {
    let configKey = e.currentTarget.id.replace("_button_save", "");
    guardarPropietat(configKey);
};

let entitatTrash = e => {
    let configKey = e.currentTarget.id.replace("_button_trash", "");
    let tag = $("#" + configKey);
    if ($(tag).hasClass("radio-div")) {
        $("#" + configKey + "_1").attr("disabled", true);
        $("#" + configKey + "_2").attr("disabled", true);
        $("#" + configKey + "_radio_1").addClass("text-gray");
        $("#" + configKey + "_radio_2").addClass("text-gray");
    }
    $("#" + configKey + "_button_trash").addClass("no-display");
    $("#" + configKey + "_button_save").addClass("no-display");
    $("#" + configKey + "_button_config").removeClass("no-display");
    $("#" + configKey + "_key_entitat").addClass("text-gray");
    $("#" + configKey + "_codi_entitat").addClass("text-gray");
    let elem = $("#" + configKey);
    if (elem.is(':checkbox')) {
        $(elem).prop("checked", false);
    } else if ($(elem).is("div") ) {
        removeValueRadio(elem);
    } else if ($(elem).is("select")) {
        $(elem).val("").trigger("change");
    } else {
        elem.val("");
    }
    if (!$(elem).hasClass("radio-div")) {
        $(elem).attr("disabled", true);
    }
    guardarPropietat(configKey, true);
};