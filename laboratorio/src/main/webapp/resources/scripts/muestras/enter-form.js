/**
 * Created by souyen-ics on 11-05-14.
 */
var bloquearUI = function (mensaje) {
    var loc = window.location;
    var pathName = loc.pathname.substring(0, loc.pathname.indexOf('/', 1) + 1);
    var mess = '<img src=' + pathName + 'resources/img/ajax-loading.gif>' + mensaje;
    $.blockUI({ message: mess,
        css: {
            border: 'none',
            padding: '15px',
            backgroundColor: '#000',
            '-webkit-border-radius': '10px',
            '-moz-border-radius': '10px',
            opacity: .5,
            color: '#fff'
        },
        baseZ: 1051 // para que se muestre bien en los modales
    });
};

var desbloquearUI = function () {
    setTimeout($.unblockUI, 500);
};

var EnterFormTomaMx = function () {

    return {
        init: function (parametros) {

            $('#horaRefrigeracion').datetimepicker({
                format: 'LT'
            });

            $('#horaTomaMx').datetimepicker({
                format: 'LT'
            });

            $('#codTipoNoti').change(function () {
                var codigo = $(this).val();
                $.getJSON(parametros.tipoMxUrl, {
                    codigo: codigo,
                    ajax: 'true'
                }, function (data) {
                    var len = data.length;
                    var html = '<option value="">...</option>';
                    for (var i = 0; i < len; i++) {
                        html += '<option value="' + data[i].tipoMx.idTipoMx + '">'
                            + data[i].tipoMx.nombre
                            + '</option>';
                    }

                    $('#codTipoMx').html(html);
                    $('#codTipoMx').val('').change();
                }).fail(function (jqXHR) {
                    setTimeout($.unblockUI, 10);
                    validateLogin(jqXHR);
                });
            });

            $('#codTipoMx').change(function () {
                $.getJSON(parametros.dxUrl, {
                    codMx: $('#codTipoMx').val(),
                    tipoNoti: $('#codTipoNoti').find('option:selected').val(),
                    ajax: 'true'
                }, function (data) {
                    var len = data.length;
                    var html = null;// '<option value="">...</option>';
                    for (var i = 0; i < len; i++) {
                        html += '<option value="' + data[i].diagnostico.idDiagnostico + '">'
                            + data[i].diagnostico.nombre
                            + '</option>';
                    }

                    $('#dx').html(html);
                    $('#dx').val('').change();
                }).fail(function (jqXHR) {
                    setTimeout($.unblockUI, 10);
                    validateLogin(jqXHR);
                });
            });

            $('#embarazada').change(function () {
                if ($('#embarazada').val() != "") {
                    if ($('#embarazada').val() == "RESP|S") {
                        $('#sihayemb').fadeIn('slow');
                    } else {
                        $('#sihayemb').fadeOut('slow');
                        $('#semanasEmbarazo').val(0);
                    }
                }
            });

            jQuery.validator.addMethod("greaterOrEqualThan",
                function(value, element, params) {
                    var fecha1 = value.split("/");
                    if (!/Invalid|NaN/.test(new Date(fecha1[2], fecha1[1]-1, fecha1[0]))) {
                        var fecha2 = $(params).val().split("/");
                        return new Date(fecha1[2], fecha1[1]-1, fecha1[0]) >= new Date(fecha2[2], fecha2[1]-1, fecha2[0]);
                    }

                    return isNaN(value) && isNaN($(params).val())
                        || (Number(value) >= Number($(params).val()));
                },'Fecha debe ser mayor o igual a {0}.');

            var $validator = $("#registroMx").validate({
                rules: {
                    fechaHTomaMx: {
                        required: true,
                        greaterOrEqualThan: "#fechaInicioSintomas"
                    },
                    codTipoMx: {
                        required: true
                    },
                    dx: {
                        required: true
                    }
                },
                errorPlacement: function (error, element) {
                    error.insertAfter(element.parent());
                }
            });

            var $validator2 = $("#datos-noti").validate({
                rules: {
                    fechaInicioSintomas:{required: function () {
                        return $('#codTipoNoti').find('option:selected').val() === 'TPNOTI|SINFEB' || $('#codTipoNoti').find('option:selected').val() === 'TPNOTI|IRAG' ;
                    }},
                    embarazada:{
                        required: true
                    },
                    semanasEmbarazo:{
                        required: true
                    }
                },
                errorPlacement: function (error, element) {
                    error.insertAfter(element.parent());
                }
            });

            $('#submit').click(function () {
                var $validarFormNoti = $("#datos-noti").valid();
                if (!$validarFormNoti) {
                    $validator2.focusInvalid();
                    return false;
                } else {
                    var $validarForm = $("#registroMx").valid();
                    if (!$validarForm) {
                        $validator.focusInvalid();
                        return false;
                    } else {
                        var mensaje = '';
                        if ($('#codSilaisAtencion').find('option:selected').val().length<=0 && $('#codUnidadAtencion').find('option:selected').val().length<=0){
                            mensaje = $("#msg_sin_SILAIS_US").val();
                        }else if ($('#codSilaisAtencion').find('option:selected').val()===undefined || $('#codSilaisAtencion').find('option:selected').val().length<=0){
                            mensaje = $("#msg_sin_SILAIS").val();
                        }else if ($('#codSilaisAtencion').find('option:selected').val().length>0 &&
                            ($('#codUnidadAtencion').find('option:selected').val()===undefined || $('#codUnidadAtencion').find('option:selected').val().length<=0)){
                            mensaje = $("#msg_sin_US").val();
                        }
                        if (mensaje.length<=0)
                        {
                            validateFechaToma();

                        }else{
                            var opcSi = $("#yes").val();
                            var opcNo = $("#no").val();
                            $.SmartMessageBox({
                                title: $("#msg_confirm_title").val(),
                                content: mensaje,
                                buttons: '[' + opcSi + '][' + opcNo + ']'
                            }, function (ButtonPressed) {
                                if (ButtonPressed === opcSi) {
                                    validateFechaToma();
                                }
                                if (ButtonPressed === opcNo) {
                                    $.smallBox({
                                        title: $("#msg_action_canceled").val(),
                                        content: "<i class='fa fa-clock-o'></i> <i>" + $("#disappear").val() + "</i>",
                                        color: "#C46A69",
                                        iconSmall: "fa fa-times fa-2x fadeInRight animated",
                                        timeout: 4000
                                    });
                                }

                            });
                        }
                    }
                }
            });

            function validateFechaToma() {
                bloquearUI(parametros.blockMess);
                var valores = $('#dx').val();
                var strValores = '';
                for (var i = 0; i < valores.length; i++) {
                    if (i == 0)
                        strValores = +valores[i];
                    else
                        strValores = strValores + ',' + valores[i];
                }
                $.getJSON(parametros.validateUrl, {
                    idNotificacion : $('#idNotificacion').val(),
                    fechaHTomaMx: $('#fechaHTomaMx').val(),
                    dxs: strValores,
                    ajax : 'true'
                }, function(data) {
                    if (data.respuesta==="OK"){
                        save();
                    }else{
                        $.smallBox({
                            title: data.respuesta ,
                            content:  $('#disappear').val(),
                            color: "#C79121",
                            iconSmall: "fa fa-check-circle",
                            timeout: 4000
                        });
                    }
                });
                desbloquearUI();
            }

            function save() {

                var objetoTomaMx = {};
                objetoTomaMx['idNotificacion'] = $("#idNotificacion").val();
                objetoTomaMx['codSilais'] = $('#codSilaisAtencion').find('option:selected').val();
                objetoTomaMx['codUnidadSalud'] = $('#codUnidadAtencion').find('option:selected').val();
                objetoTomaMx['codTipoNoti'] = $('#codTipoNoti').find('option:selected').val();
                if (document.getElementById('fechaInicioSintomas')){
                    objetoTomaMx['fechaInicioSintomas'] = $("#fechaInicioSintomas").val();
                }else{
                    objetoTomaMx['fechaInicioSintomas'] = '';
                }
                if (document.getElementById('urgente')){
                    objetoTomaMx['urgente'] = $('#urgente').find('option:selected').val();
                }else{
                    objetoTomaMx['urgente'] = '';
                }
                if (document.getElementById('embarazada')){
                    objetoTomaMx['embarazada'] = $('#embarazada').find('option:selected').val();
                }else{
                    objetoTomaMx['embarazada'] = '';
                }
                if (document.getElementById('semanasEmbarazo')){
                    objetoTomaMx['semanasEmbarazo'] = $('#semanasEmbarazo').val();
                }else{
                    objetoTomaMx['semanasEmbarazo'] = '';
                }

                objetoTomaMx['fechaHTomaMx'] = $("#fechaHTomaMx").val();
                objetoTomaMx['horaTomaMx'] = $("#horaTomaMx").val();
                objetoTomaMx['canTubos'] = $("#canTubos").val();
                objetoTomaMx['volumen'] = $("#volumen").val();
                objetoTomaMx['horaRefrigeracion'] = $("#horaRefrigeracion").val();
                objetoTomaMx['codTipoMx'] = $('#codTipoMx').find('option:selected').val();
                objetoTomaMx['mensaje'] = '';
                var valores = $('#dx').val();
                var strValores = '';
                bloquearUI(parametros.blockMess);
                var objDetalle = {};
                var cantRespuestas = 0;
                for (var i = 0; i < valores.length; i++) {
                    if (i == 0)
                        strValores = +valores[i];
                    else
                        strValores = strValores + ',' + valores[i];
                }
                //console.log(strValores);
                objetoTomaMx['dx'] = strValores;

                $.getJSON(parametros.todoDatosUrl, {
                    solicitudes: strValores,
                    ajax: 'false'
                }, function (dataToLoad) {
                    var len = Object.keys(dataToLoad).length;
                    if (len > 0) {
                        for (var i = 0; i < len; i++) {
                            var idControlRespuesta;
                            var valorControlRespuesta;
                            var idConcepto = dataToLoad[i].concepto.idConcepto;
                            switch (dataToLoad[i].concepto.tipo.codigo) {
                                case 'TPDATO|LOG':
                                    idControlRespuesta = dataToLoad[i].idConceptoSol;
                                    valorControlRespuesta = $('#' + idControlRespuesta).is(':checked');
                                    break;
                                case 'TPDATO|LIST':
                                    idControlRespuesta = dataToLoad[i].idConceptoSol;
                                    valorControlRespuesta = $('#' + idControlRespuesta).find('option:selected').val();
                                    break;
                                case 'TPDATO|TXT':
                                    idControlRespuesta = dataToLoad[i].idConceptoSol;
                                    valorControlRespuesta = $('#' + idControlRespuesta).val();
                                    break;
                                case 'TPDATO|NMRO':
                                    idControlRespuesta = dataToLoad[i].idConceptoSol;
                                    valorControlRespuesta = $('#' + idControlRespuesta).val();
                                    break;
                                case 'TPDATO|FCH':
                                    idControlRespuesta = dataToLoad[i].idConceptoSol;
                                    valorControlRespuesta = $('#' + idControlRespuesta).val();
                                    break;
                                default:
                                    break;

                            }
                            var objConcepto = {};
                            objConcepto["idRespuesta"] = idControlRespuesta;
                            objConcepto["valor"] = valorControlRespuesta;
                            objConcepto["idConcepto"] = idConcepto;
                            //console.log(objConcepto);
                            objDetalle[i] = objConcepto;
                            cantRespuestas++;
                        }

                        objetoTomaMx["strRespuestas"] = objDetalle;
                        objetoTomaMx["cantRespuestas"] = cantRespuestas;

                    }
                    $.ajax({
                        url: parametros.saveTomaUrl,
                        type: 'POST',
                        dataType: 'json',
                        data: JSON.stringify(objetoTomaMx),
                        contentType: 'application/json',
                        mimeType: 'application/json',
                        success: function (data) {
                            desbloquearUI();
                            if (data.mensaje.length > 0) {
                                $.smallBox({
                                    title: data.mensaje,
                                    content: $("#disappear").val(),
                                    color: "#C46A69",
                                    iconSmall: "fa fa-warning",
                                    timeout: 4000
                                });
                            } else {
                                imprimir2(data.codigoLab+"*"+data.areaPrc);
                                $.smallBox({
                                    title: $('#msjSuccessful').val(),
                                    content: $('#disappear').val(),
                                    color: "#739E73",
                                    iconSmall: "fa fa-check-circle",
                                    timeout: 4000
                                });
                                if ($("#mostrarPopUpMx").val()==='true') {
                                    var wnd = window.open("about:blank", "", "top=200,left=300,width=600,height=450,_blank");
                                    var textoHtml = '<table style="width:100%;border: 1px solid black; border-collapse: collapse;">' +
                                        '<tr><th style="border: 1px solid black;padding: 10px;border-collapse: collapse;">' + $("#lblPersona").val() + '</th>' +
                                        '<th style="border: 1px solid black;padding: 10px;border-collapse: collapse;">' + $("#lblCodigo").val() + '</th>' +
                                        '<th style="border: 1px solid black;padding: 10px;border-collapse: collapse;">' + $("#lblArea").val() + '</th></tr>' +
                                        '<tr><td style="border: 1px solid black;padding: 10px;border-collapse: collapse;">' + $("#primerNombre").val() + ' ' + $("#segundoNombre").val() + ' ' + $("#primerApellido").val() + ' ' + $("#segundoApellido").val() + '</br>' + $("#fechaNac").val() + '</td>' +
                                        '<td style="border: 1px solid black;padding: 10px;border-collapse: collapse;">' + data.codigoLab + '</td>' +
                                        '<td style="border: 1px solid black;padding: 10px; border-collapse: collapse;">' + unicodeEscape(data.areaPrc) + '</td></tr></table>';
                                    wnd.document.write(textoHtml);
                                }
                                setTimeout(function () {
                                    window.location.href = parametros.searchUrl;
                                }, 4000);
                            }

                        },

                        error: function (jqXHR) {
                            desbloquearUI();
                            validateLogin(jqXHR);
                        }

                    });
                }).fail(function (jqXHR) {
                    setTimeout($.unblockUI, 10);
                    validateLogin(jqXHR);
                });

            }

            function imprimir2(strBarCodes){
                $.getJSON("http://localhost:13001/print", {
                    barcodes: unicodeEscape(strBarCodes),
                    copias: 2,
                    ajax:'false'
                }, function (data) {
                    console.log(data);
                    $.smallBox({
                        title: "etiquetas impresas",
                        content: $("#disappear").val(),
                        color: "#739E73",
                        iconSmall: "fa fa-success",
                        timeout: 4000
                    });
                }).fail(function (jqXHR) {
                    console.log(jqXHR);
                    setTimeout($.unblockUI, 10);
                    validateLogin(jqXHR);
                });
            }

            $('#dx').change(function () {
                bloquearUI(parametros.blockMess);
                var valor = $(this).val();
                var divDatos = $("#datosSolicitud");
                if (valor != null) {
                    for (var i = 0; i < valor.length; i++) {
                        //si ya existe el div, significa que se habia seleccionado el dx con anterioridad, si de deselecciona entonces hay que quitarlo
                        if (!$('#DX' + valor[i]).length) {
                            fillDatosRecepcionDx(valor[i], divDatos);
                        }
                    }
                    //validar si se deseleccionó un dx y existen datos agregados de él en la página, si es asi se deben eliminar
                    var dxAgregados = $("#dxAgregados").val();
                    if (dxAgregados != "") {
                        var arrayDxAgregados = dxAgregados.split(',');
                        for (var ii = 0; ii < arrayDxAgregados.length; ii++) {
                            var eliminar = true;
                            for (var j = 0; j < valor.length; j++) {
                                if (arrayDxAgregados[ii] == valor[j]) {
                                    eliminar = false;
                                }
                            }
                            if (eliminar) {
                                $('#DX' + arrayDxAgregados[ii]).remove();
                            }

                        }
                    }
                    $("#dxAgregados").val(valor);
                } else {
                    divDatos.html("");
                    $("#dxAgregados").val("");
                }
                desbloquearUI();
            });

            function fillDatosRecepcionDx(idDx, divDatos) {
                bloquearUI(parametros.blockMess);
                var valoresListas = {};
                var detaResultados = {};
                var lenListas = 0;
                var lenDetRes = 0;
                //var idDx = $("#idDx").val();
                var idSolicitud = '-';
                divDatos.append('<div id="DX' + idDx + '"></div>');
                var divResultado = $('#DX' + idDx);
                //primero se obtienen los valores de las listas asociadas a las datos del dx

                $.getJSON(parametros.listasUrl, {
                    idDx: idDx,
                    ajax: 'false'
                }, function (dataToLoad) {
                    lenListas = Object.keys(dataToLoad).length;
                    valoresListas = dataToLoad;

                }).fail(function (jqXHR) {
                    setTimeout($.unblockUI, 10);
                    validateLogin(jqXHR);
                });

                //se obtienen los detalles de las respuestas contestadas de la solicitud
                $.getJSON(parametros.detalleUrl, {
                    idSolicitud: idSolicitud,
                    ajax: 'false'
                }, function (data) {
                    lenDetRes = data.length;
                    detaResultados = data;
                    //var divResultado= $("#datosSolicitud");
                    //divResultado.html("");
                    //obteniendo las respuestas configuradas para el dx o estudio


                    $.getJSON(parametros.datosUrl, {
                        idSolicitud: idDx,
                        ajax: 'false'
                    }, function (dataToLoad) {
                        var contenidoControl = '';
                        var len = Object.keys(dataToLoad).length;
                        if (len > 0) {
                            var encabezado = '<legend class="text-left txt-color-blue font-md">' +
                                dataToLoad[0].diagnostico.nombre +
                                '</legend>';
                            divResultado.append(encabezado);
                            for (var i = 0; i < len; i++) {
                                var idControlRespuesta;
                                var descripcionRespuesta = '';
                                if (dataToLoad[i].descripcion != null) {
                                    descripcionRespuesta = dataToLoad[i].descripcion;
                                }
                                var seccionDescripcion = '<section class="col col-sm-4 col-md-6 col-lg-6">' +
                                    '<label class="text-left txt-color-blue font-md">' +
                                    '</label>' +
                                    '<div class="note font-sm">' +
                                    '<strong>' + descripcionRespuesta + '</strong>' +
                                    '</div>' +
                                    '</section>';
                                //se busca si existe valor registrado para la respuesta
                                var valor = '';
                                if (lenDetRes > 0) {
                                    for (var j = 0; j < lenDetRes; j++) {
                                        //console.log(detaResultados[j]);
                                        //console.log(dataToLoad[i]);
                                        if (detaResultados[j].datoSolicitud.concepto.idConcepto == dataToLoad[i].concepto.idConcepto) {
                                            valor = detaResultados[j].valor;
                                            break;
                                        }
                                    }
                                }
                                switch (dataToLoad[i].concepto.tipo.codigo) {
                                    case 'TPDATO|LOG':
                                        idControlRespuesta = dataToLoad[i].idConceptoSol;
                                        contenidoControl = '<div class="row">' +
                                            '<section class="col col-sm-4 col-md-6 col-lg-6">' +
                                            '<label class="text-left txt-color-blue font-md">' +
                                            dataToLoad[i].nombre +
                                            '</label>' +
                                            '<label class="checkbox">';
                                        if (lenDetRes <= 0) {
                                            contenidoControl = contenidoControl + '<input type="checkbox" name="' + idControlRespuesta + '" id="' + idControlRespuesta + '" >';
                                        } else {
                                            if (valor == 'true') {
                                                contenidoControl = contenidoControl + '<input type="checkbox" name="' + idControlRespuesta + '" id="' + idControlRespuesta + '" checked >';
                                            } else {
                                                contenidoControl = contenidoControl + '<input type="checkbox" name="' + idControlRespuesta + '" id="' + idControlRespuesta + '" >';
                                            }
                                        }
                                        contenidoControl = contenidoControl + '<i></i>' +
                                            '</label>' +
                                            '</section>' +
                                            seccionDescripcion +
                                            '</div>';
                                        divResultado.append(contenidoControl);
                                        break;
                                    case 'TPDATO|LIST':

                                        idControlRespuesta = dataToLoad[i].idConceptoSol;
                                        contenidoControl = '<div class="row"><section class="col col-sm-12 col-md-6 col-lg-6"><label class="text-left txt-color-blue font-md">';
                                        if (dataToLoad[i].requerido) {
                                            contenidoControl = contenidoControl + '<i class="fa fa-fw fa-asterisk txt-color-red font-sm"></i>';
                                        }
                                        contenidoControl = contenidoControl + dataToLoad[i].nombre + '</label>' +
                                            '<div class="input-group">' +
                                            '<span class="input-group-addon"><i class="fa fa-location-arrow fa-fw"></i></span>';

                                        //si la respuesta es requerida
                                        if (dataToLoad[i].requerido) {
                                            contenidoControl = contenidoControl + '<select id="' + idControlRespuesta + '" name="' + idControlRespuesta + '" class="requiredConcept" style="width: 100%;" >';
                                        }
                                        else {
                                            contenidoControl = contenidoControl + '<select id="' + idControlRespuesta + '" name="' + idControlRespuesta + '" class="" style="width: 100%;" >';
                                        }
                                        contenidoControl = contenidoControl + '<option value="">...</option>';
                                        for (var ii = 0; ii < lenListas; ii++) {
                                            if (valoresListas[ii].idConcepto.idConcepto == dataToLoad[i].concepto.idConcepto) {
                                                //console.log(valoresListas[ii].idCatalogoLista +" == "+ valor);
                                                if (valoresListas[ii].idCatalogoLista == valor) {
                                                    contenidoControl = contenidoControl + '<option  value="' + valoresListas[ii].idCatalogoLista + '" selected >' + valoresListas[ii].valor + '</option>';
                                                } else {
                                                    contenidoControl = contenidoControl + '<option  value="' + valoresListas[ii].idCatalogoLista + '">' + valoresListas[ii].valor + '</option>';
                                                }
                                            }
                                        }
                                        contenidoControl = contenidoControl + '</select></div></section>' +
                                            seccionDescripcion +
                                            '</div>';
                                        divResultado.append(contenidoControl);
                                        $("#" + idControlRespuesta).select2();
                                        break;
                                    case 'TPDATO|TXT':
                                        idControlRespuesta = dataToLoad[i].idConceptoSol;
                                        contenidoControl = '<div class="row"><section class="col col-sm-12 col-md-12 col-lg-6"><label class="text-left txt-color-blue font-md">';
                                        if (dataToLoad[i].requerido) {
                                            contenidoControl = contenidoControl + '<i class="fa fa-fw fa-asterisk txt-color-red font-sm"></i>';
                                        }
                                        contenidoControl = contenidoControl + dataToLoad[i].nombre + '</label>' +
                                            '<div class="">' +
                                            '<label class="input"><i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-sort-alpha-asc fa-fw"></i>';
                                        if (dataToLoad[i].requerido) {
                                            contenidoControl = contenidoControl + '<input class="form-control requiredConcept" type="text"  id="' + idControlRespuesta + '" name="' + idControlRespuesta + '" value="' + valor + '" placeholder="' + dataToLoad[i].nombre + '" >';
                                        } else {
                                            contenidoControl = contenidoControl + '<input class="form-control" type="text"  id="' + idControlRespuesta + '" name="' + idControlRespuesta + '" value="' + valor + '" placeholder="' + dataToLoad[i].nombre + '" >';
                                        }

                                        contenidoControl = contenidoControl + '<b class="tooltip tooltip-bottom-right"> <i class="fa fa-warning txt-color-pink"></i>' + dataToLoad[i].nombre + '</b></label>' +
                                            '</div></section>' +
                                            seccionDescripcion +
                                            '</div>';
                                        divResultado.append(contenidoControl);
                                        break;
                                    case 'TPDATO|NMRO':
                                        idControlRespuesta = dataToLoad[i].idConceptoSol;
                                        contenidoControl = '<div class="row"><section class="col col-sm-12 col-md-12 col-lg-6"><label class="text-left txt-color-blue font-md">';
                                        if (dataToLoad[i].requerido) {
                                            contenidoControl = contenidoControl + '<i class="fa fa-fw fa-asterisk txt-color-red font-sm"></i>';
                                        }
                                        contenidoControl = contenidoControl + dataToLoad[i].nombre + '</label>' +
                                            '<div class="">' +
                                            '<label class="input"><i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-sort-numeric-asc fa-fw"></i>';
                                        if (dataToLoad[i].requerido) {
                                            contenidoControl = contenidoControl + '<input class="form-control decimal requiredConcept" type="text"  id="' + idControlRespuesta + '" name="' + idControlRespuesta + '" value="' + valor + '" placeholder="' + dataToLoad[i].nombre + '" >';
                                        } else {
                                            contenidoControl = contenidoControl + '<input class="form-control decimal" type="text"  id="' + idControlRespuesta + '" name="' + idControlRespuesta + '" value="' + valor + '" placeholder="' + dataToLoad[i].nombre + '" >';
                                        }

                                        contenidoControl = contenidoControl + '<b class="tooltip tooltip-bottom-right"> <i class="fa fa-warning txt-color-pink"></i>' + dataToLoad[i].nombre + '</b></label>' +
                                            '</div></section>' +
                                            seccionDescripcion +
                                            '</div>';
                                        divResultado.append(contenidoControl);
                                        $("#" + idControlRespuesta).inputmask("decimal", {
                                            allowMinus: false,
                                            radixPoint: ".",
                                            digits: 2
                                        });
                                        break;
                                    case 'TPDATO|FCH':
                                        idControlRespuesta = dataToLoad[i].idConceptoSol;
                                        contenidoControl = '<div class="row"><section class="col col-sm-12 col-md-12 col-lg-6"><label class="text-left txt-color-blue font-md">';
                                        if (dataToLoad[i].requerido) {
                                            contenidoControl = contenidoControl + '<i class="fa fa-fw fa-asterisk txt-color-red font-sm"></i>';
                                        }
                                        contenidoControl = contenidoControl + dataToLoad[i].nombre + '</label>' +
                                            '<div class="">' +
                                            '<label class="input"><i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-calendar fa-fw"></i>';
                                        if (dataToLoad[i].requerido) {
                                            contenidoControl = contenidoControl + '<input class="form-control date-picker requiredConcept" type="text"  id="' + idControlRespuesta + '" name="' + idControlRespuesta + '" value="' + valor + '" placeholder="' + dataToLoad[i].nombre + '" >';
                                        } else {
                                            contenidoControl = contenidoControl + '<input class="form-control date-picker" type="text"  id="' + idControlRespuesta + '" name="' + idControlRespuesta + '" value="' + valor + '" placeholder="' + dataToLoad[i].nombre + '" >';
                                        }

                                        contenidoControl = contenidoControl + '<b class="tooltip tooltip-bottom-right"> <i class="fa fa-warning txt-color-pink"></i>' + dataToLoad[i].nombre + '</b></label>' +
                                            '</div></section>' +
                                            seccionDescripcion +
                                            '</div>';
                                        divResultado.append(contenidoControl);
                                        break;
                                    default:
                                        break;

                                }
                            }
                            handleDatePickers("${pageContext.request.locale.language}");
                            desbloquearUI();
                        } else {
                            desbloquearUI();
                            $.smallBox({
                                title: $("#msg_no_results_found").val(),
                                content: $("#disappear").val(),
                                color: "#C79121",
                                iconSmall: "fa fa-warning",
                                timeout: 4000
                            });
                        }

                    }).fail(function (jqXHR) {
                        setTimeout($.unblockUI, 10);
                        validateLogin(jqXHR);
                    });

                }).fail(function (jqXHR) {
                    setTimeout($.unblockUI, 10);
                    validateLogin(jqXHR);
                });
            }

            jQuery.validator.addClassRules("requiredConcept", {
                required: true
            });

        }
    }


}();
