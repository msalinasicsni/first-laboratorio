var TrasladoMx = function () {
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

    var clickConfirm = 0;
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
            var text_selected_all = $("#text_selected_all").val();
            var text_selected_none = $("#text_selected_none").val();
            var table1 = $('#muestras_result').dataTable({
                "sDom": "<'dt-toolbar'<'col-xs-12 col-sm-6'f><'col-sm-6 col-xs-12 hidden-xs'l>r>" +
                    "T" +
                    "t" +
                    "<'dt-toolbar-footer'<'col-sm-6 col-xs-12 hidden-xs'i><'col-xs-12 col-sm-6'p>>",
                "autoWidth": true,
                "columns": [
                    null, null, null, null, null, null, null,
                    {
                        "className": 'details-control',
                        "orderable": false,
                        "data": null,
                        "defaultContent": ''
                    }
                ],
                "preDrawCallback": function () {
                    // Initialize the responsive datatables helper once.
                    if (!responsiveHelper_dt_basic) {
                        responsiveHelper_dt_basic = new ResponsiveDatatablesHelper($('#muestras_result'), breakpointDefinition);
                    }
                },
                "rowCallback": function (nRow) {
                    responsiveHelper_dt_basic.createExpandIcon(nRow);
                },
                "drawCallback": function (oSettings) {
                    responsiveHelper_dt_basic.respond();
                },
                "oTableTools": {
                    "sSwfPath": parametros.sTableToolsPath,
                    "sRowSelect": "multi",
                    "aButtons": [
                        {"sExtends": "select_all", "sButtonText": text_selected_all},
                        {"sExtends": "select_none", "sButtonText": text_selected_none}
                    ]
                }
            });

            var table2 = $('#mx_result').dataTable({
                "sDom": "<'dt-toolbar'<'col-xs-12 col-sm-6'f><'col-sm-6 col-xs-12 hidden-xs'l>r>" +
                "T" +
                "t" +
                "<'dt-toolbar-footer'<'col-sm-6 col-xs-12 hidden-xs'i><'col-xs-12 col-sm-6'p>>",
                "autoWidth": true,
                "columns": [
                    null, null, null, null, null
                ],
                "preDrawCallback": function () {
                    // Initialize the responsive datatables helper once.
                    if (!responsiveHelper_dt_basic) {
                        responsiveHelper_dt_basic = new ResponsiveDatatablesHelper($('#mx_result'), breakpointDefinition);
                    }
                },
                "rowCallback": function (nRow) {
                    responsiveHelper_dt_basic.createExpandIcon(nRow);
                },
                "drawCallback": function (oSettings) {
                    responsiveHelper_dt_basic.respond();
                },
                "oTableTools": {
                    "sSwfPath": parametros.sTableToolsPath,
                    "sRowSelect": "multi",
                    "aButtons": [
                        {"sExtends": "select_all", "sButtonText": text_selected_all},
                        {"sExtends": "select_none", "sButtonText": text_selected_none}
                    ]
                }
            });

            <!-- formulario de búsqueda de ordenes -->
            $('#searchMx-form').validate({
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
                    getMxs(false)
                }
            });

            <!-- formulario de búsqueda de Ordenes de Control de Calidad-->
            $('#searchMxCC-form').validate({
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
                    table2.fnClearTable();
                    //add here some ajax code to submit your form or just call form.submit() if you want to submit the form without ajax
                    getMxCC(false)
                }
            });

            <!-- formulario de recepción general -->
            $('#transfer-form').validate({
                // Rules for form validation
                rules: {
                    rdTransferType: {required: true},
                    txtNombreTransporta: {required: true},
                    txtTemperatura: {required: true},
                    idDxSolicitado: {required: true},
                    labDestino: {required: true},
                    idExamenes: {required: true}
                },
                // Do not change code below
                errorPlacement: function (error, element) {
                    if (element.attr("name") === "rdTransferType") {
                        $("#dErrorTransferType").fadeIn('slow');
                    } else {
                        error.insertAfter(element.parent());
                    }
                },
                submitHandler: function (form) {
                    clickConfirm =0;
                    //add here some ajax code to submit your form or just call form.submit() if you want to submit the form without ajax
                    trasladarMx();
                }
            });

            /*PARA MOSTRAR TABLA DETALLE rutinas*/
            function format(d, indice) {
                // `d` is the original data object for the row
                var texto = d[indice]; //indice donde esta el input hidden
                var diagnosticos = $(texto).val();
                var json = JSON.parse(diagnosticos);
                var len = Object.keys(json).length;
                var childTable = '<table style="padding-left:20px;border-collapse: separate;border-spacing:  10px 3px;">' +
                    '<tr><td style="font-weight: bold">' + $('#text_request').val() + '</td><td style="font-weight: bold">' + $('#text_request_date').val() + '</td><td style="font-weight: bold">' + $('#text_request_type').val() + '</td><td style="font-weight: bold">' + $('#label_tests').val() + '</td></tr>';
                for (var i = 1; i <= len; i++) {
                    childTable = childTable +
                        '<tr><td>' + json[i].nombre + '</td>' +
                        '<td>' + json[i].fechaSolicitud + '</td>' +
                        '<td>' + json[i].tipo + '</td>' +
                        '<td>' + json[i].examenes + '</td>' +
                        '</tr>';
                }
                childTable = childTable + '</table>';
                return childTable;
            }

            $('#muestras_result tbody').on('click', 'td.details-control', function () {
                var tr = $(this).closest('tr');
                var row = table1.api().row(tr);
                if (row.child.isShown()) {
                    // This row is already open - close it
                    row.child.hide();
                    tr.removeClass('shown');
                }
                else {
                    // Open this row
                    row.child(format(row.data(), 7)).show();
                    tr.addClass('shown');
                }
            });

            function getMxs(showAll) {
                var mxFiltros = {};
                if (showAll) {
                    mxFiltros['nombreApellido'] = '';
                    mxFiltros['fechaInicioTomaMx'] = '';
                    mxFiltros['fechaFinTomaMx'] = '';
                    mxFiltros['codSilais'] = '';
                    mxFiltros['codUnidadSalud'] = '';
                    mxFiltros['codTipoMx'] = '';
                    mxFiltros['esLab'] = '';
                    mxFiltros['codTipoSolicitud'] = '';
                    mxFiltros['nombreSolicitud'] = '';
                    mxFiltros['tipoTraslado'] = $("#tipoTraslado").val();
                } else {
                    mxFiltros['nombreApellido'] = $('#txtfiltroNombre').val();
                    if ($("#tipoTraslado").val()==='interno'){
                        mxFiltros['fechaInicioRecep'] = $('#fecInicioTomaMx').val();
                        mxFiltros['fechaFinRecepcion'] = $('#fecFinTomaMx').val();
                    }else {
                        mxFiltros['fechaInicioTomaMx'] = $('#fecInicioTomaMx').val();
                        mxFiltros['fechaFinTomaMx'] = $('#fecFinTomaMx').val();
                    }
                    mxFiltros['codSilais'] = $('#codSilais').find('option:selected').val();
                    mxFiltros['codUnidadSalud'] = $('#codUnidadSalud').find('option:selected').val();
                    mxFiltros['codTipoMx'] = $('#codTipoMx').find('option:selected').val();
                    mxFiltros['esLab'] = '';
                    mxFiltros['codigoUnicoMx'] = $('#txtCodUnicoMx').val();
                    mxFiltros['codTipoSolicitud'] = '';
                    mxFiltros['nombreSolicitud'] = $('#nombreSoli').val();
                    mxFiltros['tipoTraslado'] = $("#tipoTraslado").val();
                }
                bloquearUI(parametros.blockMess);
                $.getJSON(parametros.sOrdersUrl, {
                    strFilter: JSON.stringify(mxFiltros),
                    ajax: 'true'
                }, function (dataToLoad) {
                    table1.fnClearTable();
                    var len = Object.keys(dataToLoad).length;
                    if (len > 0) {
                        for (var i = 0; i < len; i++) {
                            table1.fnAddData(
                                [dataToLoad[i].codigoUnicoMx + " <input type='hidden' value='" + dataToLoad[i].idTomaMx + "'/>", dataToLoad[i].tipoMuestra, dataToLoad[i].fechaTomaMx, dataToLoad[i].fechaInicioSintomas,
                                    dataToLoad[i].codSilais, dataToLoad[i].codUnidadSalud, dataToLoad[i].persona, " <input type='hidden' value='" + dataToLoad[i].solicitudes + "'/>"]);

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

            /*PARA MOSTRAR detalle de solicitudes*/
            function format1(d, indice) {
                // `d` is the original data object for the row
                var texto = d[indice]; //indice donde esta el input hidden
                var diagnosticos = $(texto).val();
                var json = JSON.parse(diagnosticos);
                var len = Object.keys(json).length;
                var childTable = '<table style="padding-left:20px;border-collapse: separate;border-spacing:  10px 3px;">' +
                    '<tr><td style="font-weight: bold">' + $('#text_request').val() + '</td><td style="font-weight: bold">' + $('#text_request_date').val() + '</td><td style="font-weight: bold">' + $('#text_request_type').val() + '</td></tr>';
                for (var i = 1; i <= len; i++) {
                    childTable = childTable +
                        '<tr><td>' + json[i].nombre + '</td>' +
                        '<td>' + json[i].detResultado + '</td>' +
                        '<td>' + json[i].fechaAprobacion + '</td>' +
                        '</tr>';
                }
                childTable = childTable + '</table>';
                return childTable;
            }


            //Filtro modificado para busqueda por fechas de aprobacion
            function getMxCC(showAll) {
                var mxFiltros = {};
                if (showAll) {
                    mxFiltros['nombreApellido'] = '';
                    mxFiltros['fechaInicioAprob'] = '';
                    mxFiltros['fechaFinAprob'] = '';
                    mxFiltros['codSilais'] = '';
                    mxFiltros['codUnidadSalud'] = '';
                    mxFiltros['codTipoMx'] = '';
                    mxFiltros['esLab'] = '';
                    mxFiltros['codTipoSolicitud'] = '';
                    mxFiltros['nombreSolicitud'] = '';
                    mxFiltros['tipoTraslado'] = $("#tipoTraslado").val();
                } else {
                    mxFiltros['nombreApellido'] = $('#txtfiltroNombre').val();
                    mxFiltros['fechaInicioTomaMx'] = $('#fecInicioTomaMx').val();
                    mxFiltros['fechaFinTomaMx'] = $('#fecFinTomaMx').val();

                    mxFiltros['codSilais'] = $('#codSilais').find('option:selected').val();
                    mxFiltros['codUnidadSalud'] = $('#codUnidadSalud').find('option:selected').val();
                    mxFiltros['codTipoMx'] = $('#codTipoMx').find('option:selected').val();
                    mxFiltros['esLab'] = '';
                    mxFiltros['codigoUnicoMx'] = $('#txtCodUnicoMx').val();
                    mxFiltros['codTipoSolicitud'] = '';
                    mxFiltros['nombreSolicitud'] = $('#nombreSoli').val();
                    mxFiltros['tipoTraslado'] = $("#tipoTraslado").val();
                }
                bloquearUI(parametros.blockMess);
                $.getJSON(parametros.sOrdersUrl, {
                    strFilter: JSON.stringify(mxFiltros),
                    ajax: 'true'
                }, function (dataToLoad) {
                    table2.fnClearTable();
                    var len = Object.keys(dataToLoad).length;
                    if (len > 0) {
                        for (var i = 0; i < len; i++) {
                            var json = JSON.parse(dataToLoad[i].solicitudes);
                            var len2 = Object.keys(json).length;
                            var childTable = '<table style="padding-left:20px;border-collapse: separate;border-spacing:  10px 3px;">' +
                                '<tr><td style="font-weight: bold; width: 40%">' + $('#text_request').val() + '</td><td style="font-weight: bold; width: 40%">' + $('#text_final_result').val() + '</td><td style="font-weight: bold; width: 20%">' + $('#text_approve_date').val() + '</td></tr>';
                            for (var j = 1; j <= len2; j++) {
                                childTable = childTable +
                                    '<tr><td style="width: 40%">' + json[j].nombre + '</td>' +
                                    '<td style="width: 40%">' + json[j].detResultado + '</td>' +
                                    '<td style="width: 20%">' + json[j].fechaAprobacion + '</td>' +
                                    '</tr>';
                            }
                            childTable = childTable + '</table>';
                        table2.fnAddData(
                            [dataToLoad[i].codigoUnicoMx + " <input type='hidden' value='" + dataToLoad[i].idTomaMx + "'/>", dataToLoad[i].tipoMuestra, dataToLoad[i].fechaInicioSintomas,
                                dataToLoad[i].codSilais, childTable]);
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

            $("#all-ordersCC").click(function () {
                getMxCC(true);
            });

            function trasladarMx() {
                var oTT = TableTools.fnGetInstance('muestras_result');
                var aSelectedTrs = oTT.fnGetSelected();
                var len = aSelectedTrs.length;
                var opcSi = $("#confirm_msg_opc_yes").val();
                var opcNo = $("#confirm_msg_opc_no").val();
                if (len > 0) {
                    $.SmartMessageBox({
                        title: $("#msg_confirm_title").val(),
                        content: $("#msg_confirm_content").val(),
                        buttons: '[' + opcSi + '][' + opcNo + ']'
                    }, function (ButtonPressed) {
                        if (ButtonPressed === opcSi) {
                            clickConfirm = clickConfirm +1;
                            if (clickConfirm == 1) {
                                //bloquearUI(parametros.blockMess);
                                var idMuestras = {};
                                //el input hidden debe estar siempre en la primera columna
                                for (var i = 0; i < len; i++) {
                                    var texto = aSelectedTrs[i].firstChild.innerHTML;
                                    var input = texto.substring(texto.lastIndexOf("<"), texto.length);
                                    idMuestras[i] = $(input).val();
                                }
                                var muestrasObj = {};
                                muestrasObj['strMuestras'] = idMuestras;
                                muestrasObj['mensaje'] = '';
                                muestrasObj['cantMuestras'] = len;
                                muestrasObj['cantMxProc'] = '';
                                //muestrasObj['codigosUnicosMx'] = '';
                                muestrasObj['tipoTraslado'] = $("#tipoTraslado").val();
                                muestrasObj['idRutina'] = $('#idDxSolicitado').find('option:selected').val();
                                muestrasObj['nombreTransporta'] = $("#txtNombreTransporta").val();
                                muestrasObj['temperaturaTermo'] = $("#txtTemperatura").val();
                                muestrasObj['labDestino'] = $('#labDestino').find('option:selected').val();
                                muestrasObj['idExamenes'] = $('#idExamenes').val();
                                $.ajax(
                                    {
                                        url: parametros.sTrasladoUrl,
                                        type: 'POST',
                                        dataType: 'json',
                                        data: JSON.stringify(muestrasObj),
                                        contentType: 'application/json',
                                        mimeType: 'application/json',
                                        async: false,
                                        success: function (data) {
                                            if (data.mensaje.length > 0) {
                                                $.smallBox({
                                                    title: data.mensaje,
                                                    content: $("#smallBox_content").val(),
                                                    color: "#C46A69",
                                                    iconSmall: "fa fa-warning",
                                                    timeout: 4000
                                                });
                                            } else {
                                                var msg = $("#msg_reception_lab_success").val();
                                                msg = msg.replace(/\{0\}/, data.cantMxProc);
                                                $.smallBox({
                                                    title: msg,
                                                    content: $("#smallBox_content").val(),
                                                    color: "#739E73",
                                                    iconSmall: "fa fa-success",
                                                    timeout: 4000
                                                });
                                                table1.fnClearTable();
                                                //getMxs(false);
                                            }
                                            desbloquearUI();
                                        },
                                        error: function (jqXHR) {
                                            desbloquearUI();
                                            validateLogin(jqXHR);
                                        }
                                    });
                            }
                        }
                        if (ButtonPressed === opcNo) {
                            $.smallBox({
                                title: $("#msg_reception_cancel").val(),
                                content: "<i class='fa fa-clock-o'></i> <i>" + $("#smallBox_content").val() + "</i>",
                                color: "#C46A69",
                                iconSmall: "fa fa-times fa-2x fadeInRight animated",
                                timeout: 4000
                            });
                        }

                    });
                } else {
                    $.smallBox({
                        title: $("#msg_select_mx").val(),
                        content: "<i class='fa fa-clock-o'></i> <i>" + $("#smallBox_content").val() + "</i>",
                        color: "#C46A69",
                        iconSmall: "fa fa-times fa-2x fadeInRight animated",
                        timeout: 4000
                    });
                }
            }

            <!--al seleccionar calidad de la muestra -->
            $('#codCalidadMx').change(function () {
                $('#causaRechazo').val('');
                if ($(this).val().length > 0) {
                    if ($(this).val() == 'CALIDMX|IDC') {
                        $("#dvCausa").show();
                    } else {
                        $("#dvCausa").hide();
                    }
                } else {
                    $("#dvCausa").hide();
                }
            });

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
                    getMxs(false);

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
                    getMxs(false);
                    $('#txtCodUnicoMx').val('');
                }
            });


            $('#cmbTipoTraslado').change(function () {
                var valSeleccionado = $(this).val();
                $('#tipoTraslado').val(valSeleccionado);
                if (valSeleccionado != 'cc')
                    $('#divLabDestino').fadeIn("fast");
                else
                    $('#divLabDestino').fadeOut("fast");
            });

            <!-- Al seleccionar diagnóstico-->
            $('#idDxSolicitado').change(function () {
                if ($(this).val().length > 0 && parametros.sExamenesURL!=null) {
                    $.getJSON(parametros.sExamenesURL, {
                        idDx: $(this).val(),
                        ajax: 'true'
                    }, function (data) {
                        var html = null;
                        var len = data.length;
                        for (var i = 0; i < len; i++) {
                            html += '<option value="' + data[i].idExamen + '">'
                                + data[i].nombre
                                + '</option>';
                        }
                        $('#idExamenes').html(html);
                    })
                        .fail(function (jqXHR) {
                            setTimeout($.unblockUI, 10);
                            validateLogin(jqXHR);
                        });
                } else {
                    var html = '<option value="">' + $("#text_opt_select").val() + '...</option>';
                    $('#idExamenes').html(html);
                }
            });
        }
    };

}();

