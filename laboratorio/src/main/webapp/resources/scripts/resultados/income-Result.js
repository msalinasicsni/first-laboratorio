var IncomeResult = function () {
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
    return {
        //main function to initiate the module
        init: function (parametros) {
            var responsiveHelper_dt_basic = undefined;
            var breakpointDefinition = {
                tablet: 1024,
                phone: 480
            };
            var table1 = $('#orders_result').dataTable({
                "sDom": "<'dt-toolbar'<'col-xs-12 col-sm-6'f><'col-sm-6 col-xs-12 hidden-xs'l>r>" +
                    "t" +
                    "<'dt-toolbar-footer'<'col-sm-6 col-xs-12 hidden-xs'i><'col-xs-12 col-sm-6'p>>",
                "autoWidth": true, //"T<'clear'>"+
                "order": [ 3, 'asc' ],
                "preDrawCallback": function () {
                    // Initialize the responsive datatables helper once.
                    if (!responsiveHelper_dt_basic) {
                        responsiveHelper_dt_basic = new ResponsiveDatatablesHelper($('#orders_result'), breakpointDefinition);
                    }
                },
                "rowCallback": function (nRow) {
                    responsiveHelper_dt_basic.createExpandIcon(nRow);
                },
                "drawCallback": function (oSettings) {
                    responsiveHelper_dt_basic.respond();
                }
            });

            <!-- formulario de búsqueda de ordenes -->
            $('#searchOrders-form').validate({
                // Rules for form validation
                rules: {
                    fecFinTomaMx: {required: function () {
                        return $('#fecInicioTomaMx').val().length > 0;
                    }},
                    fecInicioTomaMx: {required: function () {
                        return $('#fecFinTomaMx').val().length > 0;
                    }}
                },
                // Do not change code below
                errorPlacement: function (error, element) {
                    error.insertAfter(element.parent());
                },
                submitHandler: function (form) {
                    table1.fnClearTable();
                    //add here some ajax code to submit your form or just call form.submit() if you want to submit the form without ajax
                    getAlicuotas(false)
                }
            });
            <!-- formulario de registro y edición de resultado-->
            $('#addResult-form').validate({
                // Rules for form validation
                rules: {
                    codResultado: {required: true},
                    codSerotipo: {required: true}
                },
                // Do not change code below
                errorPlacement: function (error, element) {
                    error.insertAfter(element.parent());
                },
                submitHandler: function (form) {
                    guardarResultado();
                }
            });

            //formulario de anulación de resultado
            $('#override-result-form').validate({
                // Rules for form validation
                rules: {
                    causaAnulacion: {required: true}
                },
                // Do not change code below
                errorPlacement: function (error, element) {
                    error.insertAfter(element.parent());
                },
                submitHandler: function (form) {
                    anularResultado();
                }
            });

            function getAlicuotas(showAll) {
                var filtros = {};
                if (showAll) {
                    filtros['nombreApellido'] = '';
                    filtros['fechaInicioRecep'] = '';
                    filtros['fechaFinRecepcion'] = '';
                    filtros['codSilais'] = '';
                    filtros['codUnidadSalud'] = '';
                    filtros['codTipoMx'] = '';
                    filtros['esLab'] = $('#txtEsLaboratorio').val();
                    filtros['codTipoSolicitud'] = '';
                    filtros['nombreSolicitud'] = '';
                    filtros['examenResultado'] = '';
                } else {
                    filtros['nombreApellido'] = $('#txtfiltroNombre').val();
                    filtros['fechaInicioRecep'] = $('#fecInicioTomaMx').val();
                    filtros['fechaFinRecepcion'] = $('#fecFinTomaMx').val();
                    filtros['codSilais'] = $('#codSilais option:selected').val();
                    filtros['codUnidadSalud'] = $('#codUnidadSalud option:selected').val();
                    filtros['codTipoMx'] = $('#codTipoMx option:selected').val();
                    filtros['esLab'] = $('#txtEsLaboratorio').val();
                    filtros['codigoUnicoMx'] = $('#txtCodUnicoMx').val();
                    filtros['codTipoSolicitud'] = $('#tipo option:selected').val();
                    filtros['nombreSolicitud'] = $('#nombreSoli').val();
                    filtros['examenResultado'] = $('#testWRes option:selected').val();

                }
                bloquearUI(parametros.blockMess);
                $.getJSON(parametros.sOrdersUrl, {
                    strFilter: JSON.stringify(filtros),
                    ajax: 'true'
                }, function (dataToLoad) {
                    table1.fnClearTable();
                    var len = Object.keys(dataToLoad).length;
                    if (len > 0) {
                        for (var i = 0; i < len; i++) {
                            var actionUrl = parametros.sActionUrl + dataToLoad[i].idOrdenExamen;
                            table1.fnAddData(
                                [/*dataToLoad[i].idAlicuota,dataToLoad[i].etiquetaPara,*/ dataToLoad[i].examen, dataToLoad[i].fechaHoraOrden, dataToLoad[i].tipoDx, dataToLoad[i].fechaHoraDx, dataToLoad[i].codigoUnicoMx, dataToLoad[i].fechaInicioSintomas,
                                    dataToLoad[i].codUnidadSalud, dataToLoad[i].persona, dataToLoad[i].resultadoExamen, '<a target="_blank" title="Ingresar resultado" href=' + actionUrl + ' class="btn btn-primary btn-xs"><i class="fa fa-mail-forward"></i></a>']);
                        }
                    } else {
                        $.smallBox({
                            title: $("#msg_no_results_found").val(),
                            content: $("#smallBox_content").val(),
                            color: "#C79121",
                            iconSmall: "fa fa-warning",
                            timeout: 4000
                        });
                    }
                    desbloquearUI();
                })
                    .fail(function (jqXHR) {
                        setTimeout($.unblockUI, 10);
                        validateLogin(jqXHR);
                    });
            }

            function guardarResultado() {
                bloquearUI(parametros.blockMess);
                var objResultado = {};
                var objDetalle = {};
                var cantRespuestas = 0;
                $.getJSON(parametros.sConceptosUrl, {
                    idExamen: $("#idExamen").val(),
                    ajax: 'false'
                }, function (dataToLoad) {
                    var len = Object.keys(dataToLoad).length;
                    if (len > 0) {
                        for (var i = 0; i < len; i++) {
                            var idControlRespuesta;
                            var valorControlRespuesta;
                            switch (dataToLoad[i].concepto.tipo.codigo) {
                                case 'TPDATO|LOG':
                                    idControlRespuesta = dataToLoad[i].idRespuesta;
                                    valorControlRespuesta = $('#' + idControlRespuesta).is(':checked');
                                    break;
                                case 'TPDATO|LIST':
                                    idControlRespuesta = dataToLoad[i].idRespuesta;
                                    valorControlRespuesta = $('#' + idControlRespuesta).find('option:selected').val();
                                    break;
                                case 'TPDATO|TXT':
                                    idControlRespuesta = dataToLoad[i].idRespuesta;
                                    valorControlRespuesta = $('#' + idControlRespuesta).val();
                                    break;
                                case 'TPDATO|NMRO':
                                    idControlRespuesta = dataToLoad[i].idRespuesta;
                                    valorControlRespuesta = $('#' + idControlRespuesta).val();
                                    break;
                                default:
                                    console.log('respuesta sin concepto');
                                    break;
                            }
                            var objConcepto = {};
                            objConcepto["idRespuesta"] = idControlRespuesta;
                            objConcepto["valor"] = valorControlRespuesta;
                            objDetalle[i] = objConcepto;
                            cantRespuestas++;
                        }
                        objResultado["idOrdenExamen"] = $("#idOrdenExamen").val();
                        objResultado["strRespuestas"] = objDetalle;
                        objResultado["mensaje"] = '';
                        objResultado["cantRespuestas"] = cantRespuestas;
                        objResultado["examenAgregado"] = '';
                        if ($("#solicitar_resultado").val() == 'true') {
                            desbloquearUI();
                            var opcSi = $("#confirm_msg_opc_yes").val();
                            var opcNo = $("#confirm_msg_opc_no").val();
                            $.SmartMessageBox({
                                title: $("#msg_confirm_title").val(),
                                content: $("#msg_confirm_content").val(),
                                buttons: '[' + opcSi + '][' + opcNo + ']'
                            }, function (ButtonPressed) {
                                if (ButtonPressed === opcSi) {
                                    objResultado["esResFinal"] = 'SI';
                                    ajaxGuardarResultado(objResultado);
                                }
                                if (ButtonPressed === opcNo) {
                                    objResultado["esResFinal"] = 'NO';
                                    $.smallBox({
                                        title: $("#msg_result_final_cancel").val(),
                                        content: "<i class='fa fa-clock-o'></i> <i>" + $("#smallBox_content").val() + "</i>",
                                        color: "#3276B1",
                                        iconSmall: "fa fa-times fa-2x fadeInRight animated",
                                        timeout: 3000
                                    });
                                    ajaxGuardarResultado(objResultado);
                                }
                            });
                        } else {
                            objResultado["esResFinal"] = 'NP'; //no se preguntó
                            ajaxGuardarResultado(objResultado);
                        }
                        bloquearUI(parametros.blockMess);
                    } else {
                        desbloquearUI();
                        $.smallBox({
                            title: $("#msg_no_results_found").val(),
                            content: $("#smallBox_content").val(),
                            color: "#C79121",
                            iconSmall: "fa fa-warning",
                            timeout: 4000
                        });
                    }

                }).fail(function (jqXHR) {
                    setTimeout($.unblockUI, 10);
                    validateLogin(jqXHR);
                });

            }

            jQuery.validator.addClassRules("requiredConcept", {
                required: true
            });

            $("#all-orders").click(function () {
                getAlicuotas(true);
            });

            function ajaxGuardarResultado(objResultado) {
                $.ajax(
                    {
                        url: parametros.sSaveResult,
                        type: 'POST',
                        dataType: 'json',
                        data: JSON.stringify(objResultado),
                        contentType: 'application/json',
                        mimeType: 'application/json',
                        async: false,
                        success: function (data) {
                            desbloquearUI();
                            if (data.mensaje.length > 0) {
                                $.smallBox({
                                    title: data.mensaje,
                                    content: $("#smallBox_content").val(),
                                    color: "#C46A69",
                                    iconSmall: "fa fa-warning",
                                    timeout: 4000
                                });
                            } else {
                                desbloquearUI();
                                var msg = $("#msg_result_added").val();
                                $.smallBox({
                                    title: msg,
                                    content: $("#smallBox_content").val(),
                                    color: "#739E73",
                                    iconSmall: "fa fa-success",
                                    timeout: 4000
                                });

                                if (data.examenAgregado == "true") {
                                    msg = $("#msg_test_added").val();
                                    $.smallBox({
                                        title: msg,
                                        content: $("#smallBox_content").val(),
                                        color: "#739E73",
                                        iconSmall: "fa fa-success",
                                        timeout: 4000
                                    });
                                }
                                limpiarDatosRecepcion();
                                setTimeout(function () {
                                    window.location.href = parametros.sResultadosUrl
                                }, 3000);
                            }
                        },
                        error: function (jqXHR) {
                            desbloquearUI();
                            validateLogin(jqXHR);
                        }
                    });
            }

            function limpiarDatosRecepcion() {
                //$("#txtNombreTransporta").val('');
                //$("#txtTemperatura").val('');
                //$("#codLaboratorioProce").val('').change();
            }

            //sólo para demo, no funcional
            <!--al seleccionar calidad de la muestra -->
            $('#codResultado').change(function () {
                $('#codSerotipo').val('').change();
                if ($(this).val().length > 0) {
                    if ($(this).val() == '1') {
                        $("#divSerotipo").show();
                    } else {
                        $("#divSerotipo").hide();
                    }
                } else {
                    $("#divSerotipo").hide();
                }
            });

            <!-- para buscar código de barra -->
            var timer;
            var iniciado = false;
            var contador;
            //var codigo;
            function tiempo() {
                console.log('tiempo');
                contador++;
                if (contador >= 10) {
                    clearInterval(timer);
                    iniciado = false;
                    //codigo = $.trim($('#codigo').val());
                    console.log('consulta con tiempo');
                    getAlicuotas(false);

                }
            }

            $('#txtCodUnicoMx').keypress(function (event) {
                if (!iniciado) {
                    timer = setInterval(tiempo(), 100);
                    iniciado = true;
                }
                contador = 0;

                if (event.keyCode == '13') {
                    clearInterval(timer);
                    iniciado = false;
                    event.preventDefault();
                    //codigo = $.trim($(this).val());
                    getAlicuotas(false);
                    $('#txtCodUnicoMx').val('');
                }
            });

            function fillRespuestasExamen() {
                bloquearUI(parametros.blockMess);
                var valoresListas = {};
                var detaResultados = {};
                var lenListas = 0;
                var lenDetRes = 0;
                //primero se obtienen los valores de las listas asociadas a las respuestas del examen
                $.getJSON(parametros.sListasUrl, {
                    idExamen: $("#idExamen").val(),
                    ajax: 'false'
                }, function (dataToLoad) {
                    lenListas = Object.keys(dataToLoad).length;
                    valoresListas = dataToLoad;

                    //se obtienen los detalles de las respuestas contestadas de la orden de exámen
                    $.getJSON(parametros.sDetResultadosUrl, {
                        idOrdenExamen: $("#idOrdenExamen").val(),
                        ajax: 'false'
                    }, function (data) {
                        lenDetRes = data.length;
                        detaResultados = data;
                        var divResultado = $("#resultados");
                        divResultado.html("");
                        //obteniendo las respuestas configuradas para el examen
                        $.getJSON(parametros.sConceptosUrl, {
                            idExamen: $("#idExamen").val(),
                            ajax: 'false'
                        }, function (dataToLoad) {
                            var contenidoControl = '';
                            var len = Object.keys(dataToLoad).length;
                            if (len > 0) {
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
                                            if (detaResultados[j].respuesta.concepto.idConcepto == dataToLoad[i].concepto.idConcepto) {
                                                valor = detaResultados[j].valor;
                                                break;
                                            }
                                        }
                                    }
                                    switch (dataToLoad[i].concepto.tipo.codigo) {
                                        case 'TPDATO|LOG':
                                            idControlRespuesta = dataToLoad[i].idRespuesta;
                                            contenidoControl = '<div class="row">' +
                                                '<section class="col col-sm-4 col-md-6 col-lg-6">' +
                                                '<label class="text-left txt-color-blue font-md">' +
                                                dataToLoad[i].nombre +
                                                '</label>' +
                                                '<label class="checkbox">';
                                            if (lenDetRes <= 0) {
                                                contenidoControl = contenidoControl + '<input type="checkbox" name="' + idControlRespuesta + '" id="' + idControlRespuesta + '">';
                                            } else {
                                                if (valor == 'true') {
                                                    contenidoControl = contenidoControl + '<input type="checkbox" name="' + idControlRespuesta + '" id="' + idControlRespuesta + '" checked>';
                                                } else {
                                                    contenidoControl = contenidoControl + '<input type="checkbox" name="' + idControlRespuesta + '" id="' + idControlRespuesta + '">';
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
                                            idControlRespuesta = dataToLoad[i].idRespuesta;
                                            contenidoControl = '<div class="row"><section class="col col-sm-12 col-md-6 col-lg-6"><label class="text-left txt-color-blue font-md">';
                                            if (dataToLoad[i].requerido) {
                                                contenidoControl = contenidoControl + '<i class="fa fa-fw fa-asterisk txt-color-red font-sm"></i>';
                                            }
                                            contenidoControl = contenidoControl + dataToLoad[i].nombre + '</label>' +
                                                '<div class="input-group">' +
                                                '<span class="input-group-addon"><i class="fa fa-location-arrow fa-fw"></i></span>';

                                            //si la respuesta es requerida
                                            if (dataToLoad[i].requerido) {
                                                contenidoControl = contenidoControl + '<select id="' + idControlRespuesta + '" name="' + idControlRespuesta + '" class="requiredConcept" style="width: 100%;">';
                                            }
                                            else {
                                                contenidoControl = contenidoControl + '<select id="' + idControlRespuesta + '" name="' + idControlRespuesta + '" class="" style="width: 100%;">';
                                            }
                                            contenidoControl = contenidoControl + '<option value="">...</option>';
                                            console.log("consumen valores lista");
                                            for (var ii = 0; ii < lenListas; ii++) {
                                                if (valoresListas[ii].idConcepto.idConcepto == dataToLoad[i].concepto.idConcepto) {
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
                                            idControlRespuesta = dataToLoad[i].idRespuesta;
                                            contenidoControl = '<div class="row"><section class="col col-sm-12 col-md-12 col-lg-6"><label class="text-left txt-color-blue font-md">';
                                            if (dataToLoad[i].requerido) {
                                                contenidoControl = contenidoControl + '<i class="fa fa-fw fa-asterisk txt-color-red font-sm"></i>';
                                            }
                                            contenidoControl = contenidoControl + dataToLoad[i].nombre + '</label>' +
                                                '<div class="">' +
                                                '<label class="input"><i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-sort-alpha-asc fa-fw"></i>';
                                            if (dataToLoad[i].requerido) {
                                                contenidoControl = contenidoControl + '<input class="form-control requiredConcept" type="text"  id="' + idControlRespuesta + '" name="' + idControlRespuesta + '" value="' + valor + '" placeholder="' + dataToLoad[i].nombre + '">';
                                            } else {
                                                contenidoControl = contenidoControl + '<input class="form-control" type="text"  id="' + idControlRespuesta + '" name="' + idControlRespuesta + '" value="' + valor + '" placeholder="' + dataToLoad[i].nombre + '">';
                                            }

                                            contenidoControl = contenidoControl + '<b class="tooltip tooltip-bottom-right"> <i class="fa fa-warning txt-color-pink"></i>' + dataToLoad[i].nombre + '</b></label>' +
                                                '</div></section>' +
                                                seccionDescripcion +
                                                '</div>';
                                            divResultado.append(contenidoControl);
                                            break;
                                        case 'TPDATO|NMRO':
                                            idControlRespuesta = dataToLoad[i].idRespuesta;
                                            contenidoControl = '<div class="row"><section class="col col-sm-12 col-md-12 col-lg-6"><label class="text-left txt-color-blue font-md">';
                                            if (dataToLoad[i].requerido) {
                                                contenidoControl = contenidoControl + '<i class="fa fa-fw fa-asterisk txt-color-red font-sm"></i>';
                                            }
                                            contenidoControl = contenidoControl + dataToLoad[i].nombre + '</label>' +
                                                '<div class="">' +
                                                '<label class="input"><i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-sort-numeric-asc fa-fw"></i>';
                                            if (dataToLoad[i].requerido) {
                                                contenidoControl = contenidoControl + '<input class="form-control decimal requiredConcept" type="text"  id="' + idControlRespuesta + '" name="' + idControlRespuesta + '" value="' + valor + '" placeholder="' + dataToLoad[i].nombre + '">';
                                            } else {
                                                contenidoControl = contenidoControl + '<input class="form-control decimal" type="text"  id="' + idControlRespuesta + '" name="' + idControlRespuesta + '" value="' + valor + '" placeholder="' + dataToLoad[i].nombre + '">';
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
                                        default:
                                            console.log('respuesta sin concepto');
                                            break;

                                    }
                                }
                                desbloquearUI();
                            } else {
                                desbloquearUI();
                                $.smallBox({
                                    title: $("#msg_no_results_found").val(),
                                    content: $("#smallBox_content").val(),
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

                }).fail(function (jqXHR) {
                    setTimeout($.unblockUI, 10);
                    validateLogin(jqXHR);
                });


            }

            if (parametros.sEsIngreso == 'true') {
                fillRespuestasExamen();
            }

            function showModalOverride() {
                $("#myModal").modal({
                    show: true
                });
            }

            function hideModalOverride() {
                $('#myModal').modal('hide');
            }

            $("#override-result").click(function () {
                $("#causaAnulacion").val("");
                showModalOverride();
            });


            function anularResultado() {
                var objResultado = {};
                objResultado["idOrdenExamen"] = $("#idOrdenExamen").val();
                objResultado["causaAnulacion"] = $("#causaAnulacion").val();
                objResultado["mensaje"] = '';
                objResultado["solicitarResFinal"] = '';
                bloquearUI(parametros.blockMess);
                $.ajax(
                    {
                        url: parametros.sOverrideResult,
                        type: 'POST',
                        dataType: 'json',
                        data: JSON.stringify(objResultado),
                        contentType: 'application/json',
                        mimeType: 'application/json',
                        success: function (data) {
                            desbloquearUI();
                            if (data.mensaje.length > 0) {
                                $.smallBox({
                                    title: data.mensaje,
                                    content: $("#smallBox_content").val(),
                                    color: "#C46A69",
                                    iconSmall: "fa fa-warning",
                                    timeout: 4000
                                });
                            } else {
                                var msg = $("#msg_result_override").val();
                                $("#solicitar_resultado").val(data.solicitarResFinal);
                                $.smallBox({
                                    title: msg,
                                    content: $("#smallBox_content").val(),
                                    color: "#739E73",
                                    iconSmall: "fa fa-success",
                                    timeout: 4000
                                });
                                $("#causaAnulacion").val("");
                                fillRespuestasExamen();
                                hideModalOverride();
                            }
                        },
                        error: function (jqXHR) {
                            desbloquearUI();
                            validateLogin(jqXHR);
                        }
                    });
            }

            <!-- al seleccionar SILAIS -->
            $('#codSilais').change(function () {
                bloquearUI(parametros.blockMess);
                if ($(this).val().length > 0) {
                    $.getJSON(parametros.sUnidadesUrl, {
                        codSilais: $(this).val(),
                        ajax: 'true'
                    }, function (data) {
                        var html = null;
                        var len = data.length;
                        html += '<option value="">' + $("#text_opt_select").val() + '...</option>';
                        for (var i = 0; i < len; i++) {
                            html += '<option value="' + data[i].codigo + '">'
                                + data[i].nombre
                                + '</option>';
                            // html += '</option>';
                        }
                        $('#codUnidadSalud').html(html);
                    }).fail(function (jqXHR) {
                        setTimeout($.unblockUI, 10);
                        validateLogin(jqXHR);
                    });
                } else {
                    var html = '<option value="">' + $("#text_opt_select").val() + '...</option>';
                    $('#codUnidadSalud').html(html);
                }
                $('#codUnidadSalud').val('').change();
                desbloquearUI();
            });
        }
    };

}();

