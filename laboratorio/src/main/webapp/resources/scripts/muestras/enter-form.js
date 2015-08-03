/**
 * Created by souyen-ics on 11-05-14.
 */
var bloquearUI = function(mensaje){
    var loc = window.location;
    var pathName = loc.pathname.substring(0,loc.pathname.indexOf('/', 1)+1);
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
        }
    });
};

var desbloquearUI = function() {
    setTimeout($.unblockUI, 500);
};

var EnterFormTomaMx = function () {

    return {
        init: function (parametros) {

            $('.datetimepicker').datetimepicker({
                language: 'es',
               format: 'DD/MM/YYYY h:m A'

            });

            $('#horaRefrigeracion').datetimepicker({
                pickDate: false
            });

            $('#codTipoMx').change(function() {
                $.getJSON(parametros.dxUrl, {
                    codMx: $('#codTipoMx').val(),
                    tipoNoti: $('#tipoNoti').val(),
                    ajax: 'true'
                }, function (data) {
                    var len = data.length;
                    var html = null;
                    for (var i = 0; i < len; i++) {
                       html += '<option value="' + data[i].diagnostico.idDiagnostico + '">'
                            + data[i].diagnostico.nombre
                            + '</option>';
                    }

                    $('#dx').html(html);
                });
            });


         var $validator = $("#registroMx").validate({
                rules: {
                    fechaHTomaMx: {
                        required: true

                    },

                    codTipoMx:{
                        required:true
                    },

                    examenes:{
                        required:true
                    }

                },

                errorPlacement: function (error, element) {
                    error.insertAfter(element.parent());

                }

              /*  submitHandler: function (form) {
                    //add here some ajax code to submit your form or just call form.submit() if you want to submit the form without ajax
                   save();
                }*/
            });


            $('#submit').click(function() {
                var $validarForm = $("#registroMx").valid();
                if (!$validarForm) {
                    $validator.focusInvalid();
                    return false;
                }else{
                   save();

                }

            });

            function save() {
                var objetoTomaMx = {};
                objetoTomaMx['idNotificacion'] = $("#idNotificacion").val();
                objetoTomaMx['fechaHTomaMx'] = $("#fechaHTomaMx").val();
                objetoTomaMx['canTubos'] = $("#canTubos").val();
                objetoTomaMx['volumen'] = $("#volumen").val();
                objetoTomaMx['horaRefrigeracion'] = $("#horaRefrigeracion").val();
                objetoTomaMx['codTipoMx'] = $('#codTipoMx').find('option:selected').val();
                //objetoTomaMx['dx'] = $('#dx').val();
                objetoTomaMx['mensaje'] = '';
                var valores = $('#dx').val();
                var strValores ='';
                bloquearUI(parametros.blockMess);
                var objDetalle = {};
                var cantRespuestas = 0;
                for (var i = 0; i < valores.length; i++) {
                    if (i == 0)
                        strValores = + valores[i];
                    else
                        strValores = strValores + ',' + valores[i];
                }
                //console.log(strValores);
                objetoTomaMx['dx'] = strValores;
                $.getJSON(parametros.todoDatosUrl, {
                    solicitudes: strValores,
                    ajax : 'false'
                }, function(dataToLoad) {
                    var len = Object.keys(dataToLoad).length;
                    if (len > 0) {
                        for (var i = 0; i < len; i++) {
                            var idControlRespuesta;
                            var valorControlRespuesta;
                            var idConcepto = dataToLoad[i].concepto.idConcepto;
                            switch (dataToLoad[i].concepto.tipo.codigo) {
                                case 'TPDATO|LOG':
                                    idControlRespuesta = dataToLoad[i].idConceptoSol;
                                    valorControlRespuesta = $('#'+idControlRespuesta).is(':checked');
                                    break;
                                case 'TPDATO|LIST':
                                    idControlRespuesta = dataToLoad[i].idConceptoSol;
                                    valorControlRespuesta = $('#'+idControlRespuesta).find('option:selected').val();
                                    break;
                                case 'TPDATO|TXT':
                                    idControlRespuesta = dataToLoad[i].idConceptoSol;
                                    valorControlRespuesta = $('#'+idControlRespuesta).val();
                                    break;
                                case 'TPDATO|NMRO':
                                    idControlRespuesta = dataToLoad[i].idConceptoSol;
                                    valorControlRespuesta = $('#'+idControlRespuesta).val();
                                    break;
                                case 'TPDATO|FCH':
                                    idControlRespuesta = dataToLoad[i].idConceptoSol;
                                    valorControlRespuesta = $('#'+idControlRespuesta).val();
                                    break;
                                default:
                                    break;

                            }
                            var objConcepto = {};
                            objConcepto["idRespuesta"] = idControlRespuesta;
                            objConcepto["valor"]=valorControlRespuesta;
                            objConcepto["idConcepto"] = idConcepto;
                            //console.log(objConcepto);
                            objDetalle[i] = objConcepto;
                            cantRespuestas ++;
                        }
                        /*
                        var idSoliDx = $("#idSolicitud").val();
                        var idSoliE = $("#idSolicitudE").val();
                        if(idSoliDx != ""){
                            objResultado["idSolicitud"] = idSoliDx;
                        }else{
                            objResultado["idSolicitud"] = idSoliE;
                        }*/

                        objetoTomaMx["strRespuestas"] = objDetalle;
                        objetoTomaMx["cantRespuestas"] = cantRespuestas;
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
                                    $.smallBox({
                                        title: $('#msjSuccessful').val(),
                                        content: $('#disappear').val(),
                                        color: "#739E73",
                                        iconSmall: "fa fa-check-circle",
                                        timeout: 4000
                                    });
                                    setTimeout(function () {
                                        window.location.href = parametros.searchUrl;
                                    }, 4000);
                                }

                            },
                            error: function (data, status, er) {
                                desbloquearUI();
                                $.smallBox({
                                    title: $('#msjErrorSaving').val() + " error: " + data + " status: " + status + " er:" + er,
                                    content: $('#disappear').val(),
                                    color: "#C46A69",
                                    iconSmall: "fa fa-warning",
                                    timeout: 5000
                                });
                            }
                        });
                    }else{
                        desbloquearUI();
                        $.smallBox({
                            title: $("#msg_no_results_found").val() ,
                            content: $("#disappear").val(),
                            color: "#C79121",
                            iconSmall: "fa fa-warning",
                            timeout: 4000
                        });
                    }
                }).fail(function(er) {
                    desbloquearUI();
                    alert( "error "+er );
                });
            }

            $('#dx').change(function(){
                bloquearUI(parametros.blockMess);
                var valor = $(this).val();
                var divDatos = $("#datosSolicitud");
                if (valor!=null){
                    for (var i = 0; i < valor.length; i++) {
                        //si ya existe el div, significa que se habia seleccionado el dx con anterioridad, si de deselecciona entonces hay que quitarlo
                        if (!$('#DX'+valor[i]).length) {
                            fillRespuestasExamen(valor[i], divDatos);
                        }
                    }
                    //validar si se deseleccionó un dx y existen datos agregados de él en la página, si es asi se deben eliminar
                    var dxAgregados = $("#dxAgregados").val();
                    if (dxAgregados!=""){
                        var arrayDxAgregados = dxAgregados.split(',');
                        for (var ii = 0; ii < arrayDxAgregados.length; ii++){
                            var eliminar = true;
                            for (var j = 0; j < valor.length; j++) {
                                if (arrayDxAgregados[ii]==valor[j]){
                                    eliminar = false;
                                }
                            }
                            if (eliminar){
                                $('#DX'+arrayDxAgregados[ii]).remove();
                            }

                        }
                    }
                    $("#dxAgregados").val(valor);
                }else{
                    divDatos.html("");
                    $("#dxAgregados").val("");
                }
                desbloquearUI();
            });

            function fillRespuestasExamen(idDx,divDatos){
                bloquearUI(parametros.blockMess);
                var valoresListas = {};
                var detaResultados = {};
                var lenListas = 0;
                var lenDetRes = 0;
                //var idDx = $("#idDx").val();
                var idSolicitud = '-';
                divDatos.append('<div id="DX'+idDx+'"></div>');
                var divResultado = $('#DX'+idDx);
                //primero se obtienen los valores de las listas asociadas a las datos del dx

                $.getJSON(parametros.listasUrl, {
                    idDx: idDx ,
                    ajax : 'false'
                }, function(dataToLoad) {
                    lenListas = Object.keys(dataToLoad).length;
                    valoresListas = dataToLoad;

                }).fail(function(er) {
                    desbloquearUI();
                    alert( "error "+er );
                });

                //se obtienen los detalles de las respuestas contestadas de la solicitud
                $.getJSON(parametros.detalleUrl, {
                    idSolicitud: idSolicitud,
                    ajax : 'false'
                }, function(data) {
                    lenDetRes = data.length;
                    detaResultados = data;
                    //var divResultado= $("#datosSolicitud");
                    //divResultado.html("");
                    //obteniendo las respuestas configuradas para el dx o estudio


                    $.getJSON(parametros.datosUrl, {
                        idSolicitud: idDx ,
                        ajax : 'false'
                    }, function(dataToLoad) {
                        var contenidoControl='';
                        var len = Object.keys(dataToLoad).length;
                        if (len > 0) {
                            var encabezado = '<legend class="text-left txt-color-blue font-md">'+
                                dataToLoad[0].diagnostico.nombre +
                                '</legend>';
                            divResultado.append(encabezado);
                            for (var i = 0; i < len; i++) {
                                var idControlRespuesta;
                                var descripcionRespuesta = '';
                                if(dataToLoad[i].descripcion!=null){
                                    descripcionRespuesta =dataToLoad[i].descripcion;
                                }
                                var seccionDescripcion = '<section class="col col-sm-4 col-md-6 col-lg-6">'+
                                    '<label class="text-left txt-color-blue font-md">'+
                                    '</label>' +
                                    '<div class="note font-sm">' +
                                    '<strong>'+descripcionRespuesta + '</strong>'+
                                    '</div>'+
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
                                        contenidoControl ='<div class="row">'+
                                            '<section class="col col-sm-4 col-md-6 col-lg-6">'+
                                            '<label class="text-left txt-color-blue font-md">'+
                                            dataToLoad[i].nombre +
                                            '</label>'+
                                            '<label class="checkbox">';
                                        if(lenDetRes <= 0) {
                                            contenidoControl = contenidoControl +'<input type="checkbox" name="' + idControlRespuesta + '" id="' + idControlRespuesta + '" >';
                                        }else{
                                            if (valor=='true'){
                                                contenidoControl = contenidoControl +'<input type="checkbox" name="' + idControlRespuesta + '" id="' + idControlRespuesta + '" checked >';
                                            }else{
                                                contenidoControl = contenidoControl +'<input type="checkbox" name="' + idControlRespuesta + '" id="' + idControlRespuesta + '" >';
                                            }
                                        }
                                        contenidoControl = contenidoControl + '<i></i>'+
                                            '</label>'+
                                            '</section>'+
                                            seccionDescripcion +
                                            '</div>';
                                        divResultado.append(contenidoControl);
                                        break;
                                    case 'TPDATO|LIST':

                                        idControlRespuesta = dataToLoad[i].idConceptoSol;
                                        contenidoControl =  '<div class="row"><section class="col col-sm-12 col-md-6 col-lg-6"><label class="text-left txt-color-blue font-md">';
                                        if (dataToLoad[i].requerido) {
                                            contenidoControl = contenidoControl +'<i class="fa fa-fw fa-asterisk txt-color-red font-sm"></i>';
                                        }
                                        contenidoControl = contenidoControl + dataToLoad[i].nombre +'</label>'+
                                            '<div class="input-group">'+
                                            '<span class="input-group-addon"><i class="fa fa-location-arrow fa-fw"></i></span>';

                                        //si la respuesta es requerida
                                        if (dataToLoad[i].requerido) {
                                            contenidoControl = contenidoControl +'<select id="'+idControlRespuesta+'" name="'+idControlRespuesta+'" class="requiredConcept" style="width: 100%;" >';
                                        }
                                        else{
                                            contenidoControl = contenidoControl +'<select id="'+idControlRespuesta+'" name="'+idControlRespuesta+'" class="" style="width: 100%;" >';
                                        }
                                        contenidoControl = contenidoControl + '<option value="">...</option>';
                                        for (var ii = 0; ii < lenListas; ii++) {
                                            if (valoresListas[ii].idConcepto.idConcepto==dataToLoad[i].concepto.idConcepto){
                                                //console.log(valoresListas[ii].idCatalogoLista +" == "+ valor);
                                                if (valoresListas[ii].idCatalogoLista == valor){
                                                    contenidoControl = contenidoControl + '<option  value="'+valoresListas[ii].idCatalogoLista+'" selected >'+valoresListas[ii].valor+'</option>';
                                                }else{
                                                    contenidoControl = contenidoControl + '<option  value="'+valoresListas[ii].idCatalogoLista+'">'+valoresListas[ii].valor+'</option>';
                                                }
                                            }
                                        }
                                        contenidoControl = contenidoControl +'</select></div></section>' +
                                            seccionDescripcion +
                                            '</div>';
                                        divResultado.append(contenidoControl);
                                        $("#"+idControlRespuesta).select2();
                                        break;
                                    case 'TPDATO|TXT':
                                        idControlRespuesta = dataToLoad[i].idConceptoSol;
                                        contenidoControl = '<div class="row"><section class="col col-sm-12 col-md-12 col-lg-6"><label class="text-left txt-color-blue font-md">';
                                        if (dataToLoad[i].requerido){
                                            contenidoControl = contenidoControl + '<i class="fa fa-fw fa-asterisk txt-color-red font-sm"></i>';
                                        }
                                        contenidoControl = contenidoControl + dataToLoad[i].nombre+'</label>' +
                                            '<div class="">'+
                                            '<label class="input"><i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-sort-alpha-asc fa-fw"></i>';
                                        if (dataToLoad[i].requerido){
                                            contenidoControl = contenidoControl + '<input class="form-control requiredConcept" type="text"  id="'+idControlRespuesta+'" name="'+idControlRespuesta+'" value="'+valor+'" placeholder="'+dataToLoad[i].nombre+'" >';
                                        }else{
                                            contenidoControl = contenidoControl + '<input class="form-control" type="text"  id="'+idControlRespuesta+'" name="'+idControlRespuesta+'" value="'+valor+'" placeholder="'+dataToLoad[i].nombre+'" >';
                                        }

                                        contenidoControl = contenidoControl +'<b class="tooltip tooltip-bottom-right"> <i class="fa fa-warning txt-color-pink"></i>'+ dataToLoad[i].nombre+'</b></label>' +
                                            '</div></section>' +
                                            seccionDescripcion +
                                            '</div>';
                                        divResultado.append(contenidoControl);
                                        break;
                                    case 'TPDATO|NMRO':
                                        idControlRespuesta = dataToLoad[i].idConceptoSol;
                                        contenidoControl = '<div class="row"><section class="col col-sm-12 col-md-12 col-lg-6"><label class="text-left txt-color-blue font-md">';
                                        if (dataToLoad[i].requerido){
                                            contenidoControl = contenidoControl + '<i class="fa fa-fw fa-asterisk txt-color-red font-sm"></i>';
                                        }
                                        contenidoControl = contenidoControl + dataToLoad[i].nombre+'</label>' +
                                            '<div class="">'+
                                            '<label class="input"><i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-sort-numeric-asc fa-fw"></i>';
                                        if (dataToLoad[i].requerido){
                                            contenidoControl = contenidoControl + '<input class="form-control decimal requiredConcept" type="text"  id="'+idControlRespuesta+'" name="'+idControlRespuesta+'" value="'+valor+'" placeholder="'+dataToLoad[i].nombre+'" >';
                                        }else{
                                            contenidoControl = contenidoControl + '<input class="form-control decimal" type="text"  id="'+idControlRespuesta+'" name="'+idControlRespuesta+'" value="'+valor+'" placeholder="'+dataToLoad[i].nombre+'" >';
                                        }

                                        contenidoControl = contenidoControl +'<b class="tooltip tooltip-bottom-right"> <i class="fa fa-warning txt-color-pink"></i>'+ dataToLoad[i].nombre+'</b></label>' +
                                            '</div></section>' +
                                            seccionDescripcion +
                                            '</div>';
                                        divResultado.append(contenidoControl);
                                        $("#"+idControlRespuesta).inputmask("decimal",{
                                            allowMinus: false,
                                            radixPoint: ".",
                                            digits: 2
                                        });
                                        break;
                                    case 'TPDATO|FCH':
                                        idControlRespuesta = dataToLoad[i].idConceptoSol;
                                        contenidoControl = '<div class="row"><section class="col col-sm-12 col-md-12 col-lg-6"><label class="text-left txt-color-blue font-md">';
                                        if (dataToLoad[i].requerido){
                                            contenidoControl = contenidoControl + '<i class="fa fa-fw fa-asterisk txt-color-red font-sm"></i>';
                                        }
                                        contenidoControl = contenidoControl + dataToLoad[i].nombre+'</label>' +
                                            '<div class="">'+
                                            '<label class="input"><i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-calendar fa-fw"></i>';
                                        if (dataToLoad[i].requerido){
                                            contenidoControl = contenidoControl + '<input class="form-control date-picker requiredConcept" type="text"  id="'+idControlRespuesta+'" name="'+idControlRespuesta+'" value="'+valor+'" placeholder="'+dataToLoad[i].nombre+'" >';
                                        }else{
                                            contenidoControl = contenidoControl + '<input class="form-control date-picker" type="text"  id="'+idControlRespuesta+'" name="'+idControlRespuesta+'" value="'+valor+'" placeholder="'+dataToLoad[i].nombre+'" >';
                                        }

                                        contenidoControl = contenidoControl +'<b class="tooltip tooltip-bottom-right"> <i class="fa fa-warning txt-color-pink"></i>'+ dataToLoad[i].nombre+'</b></label>' +
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
                        }else{
                            desbloquearUI();
                            $.smallBox({
                                title: $("#study_not_answers").val() ,
                                content: $("#disappear").val(),
                                color: "#C46A69",
                                iconSmall: "fa fa-warning",
                                timeout: 4000
                            });
                        }

                    }).fail(function(er) {
                        desbloquearUI();
                        alert( "error "+er );
                    });

                }).fail(function(er) {
                    desbloquearUI();
                    alert( "error "+er );
                });
            }

            jQuery.validator.addClassRules("requiredConcept", {
                required: true
            });

        }
    }



}();
