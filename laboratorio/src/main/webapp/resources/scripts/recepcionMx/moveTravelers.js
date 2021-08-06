var MoveTravelers = function () {
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
            var text_selected_all = $("#text_selected_all").val();
            var text_selected_none = $("#text_selected_none").val();
            var responsiveHelper_dt_basic = undefined;
            var breakpointDefinition = {
                tablet: 1024,
                phone: 480
            };
            var table1 = $('#receptions').dataTable({
                "sDom": "<'dt-toolbar'<'col-xs-12 col-sm-6'f><'col-sm-6 col-xs-12 hidden-xs'l>r>" +
                    "T" +
                    "t" +
                    "<'dt-toolbar-footer'<'col-sm-6 col-xs-12 hidden-xs'i><'col-xs-12 col-sm-6'p>>",
                "autoWidth": true, //"T<'clear'>"+
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
                        responsiveHelper_dt_basic = new ResponsiveDatatablesHelper($('#receptions'), breakpointDefinition);
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

            /*PARA MOSTRAR TABLA DETALLE DX*/
            function format(d, indice) {
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
                        '<td>' + json[i].fechaSolicitud + '</td>' +
                        '<td>' + json[i].tipo + '</td></tr>';
                }
                childTable = childTable + '</table>';
                return childTable;
            }

            $('#receptions tbody').on('click', 'td.details-control', function () {
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

            //FIN
            <!-- formulario de b�squeda de recepciones -->
            $('#searchResults-form').validate({
                // Rules for form validation
                rules: {
                    fechaFin: {required: function () {
                        return $('#fechaInicio').val().length > 0;
                    }},
                    fechaInicio: {required: function () {
                        return $('#fechaFin').val().length > 0;
                    }},
                    txtCodUnicoMx: {required: function () {
                        return $('#fechaFin').val().length <= 0 && $('#fechaInicio').val().length <= 0;
                    }}
                },
                // Do not change code below
                errorPlacement: function (error, element) {
                    error.insertAfter(element.parent());
                },
                submitHandler: function (form) {
                    table1.fnClearTable();
                    //add here some ajax code to submit your form or just call form.submit() if you want to submit the form without ajax
                    getReceptions();
                }
            });

            function getReceptions() {
                var filtros = {};
                filtros['fechaInicioRecep'] = $('#fechaInicio').val();
                filtros['fechaFinRecep'] = $('#fechaFin').val();
                filtros['codigoUnicoMx'] = $('#txtCodUnicoMx').val();

                bloquearUI(parametros.blockMess);
                $.getJSON(parametros.sSearchUrl, {
                    strFilter: JSON.stringify(filtros),
                    ajax: 'true'
                }, function (dataToLoad) {
                    table1.fnClearTable();
                    var len = Object.keys(dataToLoad).length;
                    if (len > 0) {
                        for (var i = 0; i < len; i++) {
                            table1.fnAddData(
                                [dataToLoad[i].codigoUnicoMx+" <input type='hidden' value='" + dataToLoad[i].idRecepcion + "'/>", dataToLoad[i].tipoMuestra, dataToLoad[i].fechaTomaMx,
                                    dataToLoad[i].codSilais, dataToLoad[i].codUnidadSalud, dataToLoad[i].persona, dataToLoad[i].fechaRecepcion, " <input type='hidden' value='" + dataToLoad[i].solicitudes + "'/>"]);
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


            $('#move-samples-form').validate({
                // Rules for form validation
                rules: {
                    labDestino: {required: true}
                },
                // Do not change code below
                errorPlacement: function (error, element) {
                    error.insertAfter(element.parent());
                },
                submitHandler: function (form) {
                    moveTravelers();
                }
            });

            function moveTravelers() {
                var oTT = TableTools.fnGetInstance('receptions');
                console.log(oTT);
                var aSelectedTrs = oTT.fnGetSelected();
                console.log(oTT);
                var len = aSelectedTrs.length;
                console.log(len);
                var opcSi = $("#confirm_msg_opc_yes").val();
                var opcNo = $("#confirm_msg_opc_no").val();
                if (len > 0) {
                    $.SmartMessageBox({
                        title: $("#msg_confirm_t").val(),
                        content: $("#msg_confirm_c").val(),
                        buttons: '[' + opcSi + '][' + opcNo + ']'
                    }, function (ButtonPressed) {
                        if (ButtonPressed === opcSi) {
                            bloquearUI(parametros.blockMess);
                            var idRecepciones = {};
                            //el input hidden debe estar siempre en la primera columna
                            for (var i = 0; i < len; i++) {
                                var texto = aSelectedTrs[i].firstChild.innerHTML;
                                var input = texto.substring(texto.lastIndexOf("<"), texto.length);
                                idRecepciones[i] = $(input).val();
                            }
                            var ordenesObj = {};
                            ordenesObj['strRecepciones'] = idRecepciones;
                            ordenesObj['cantRecepciones'] = len;
                            ordenesObj['codigolab'] = $('#labDestino').find('option:selected').val();
                            ordenesObj['cantProcesadas'] = '';
                            ordenesObj['mensaje'] = '';
                            ordenesObj['numeroHoja'] = '';

                            $.ajax(
                                {
                                    url: parametros.sMoveTravelers,
                                    type: 'POST',
                                    dataType: 'json',
                                    data: JSON.stringify(ordenesObj),
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
                                            var msg = $("#msg_move_succes").val();
                                            msg = msg.replace(/\{0\}/, data.cantProcesadas);
                                            $.smallBox({
                                                title: msg,
                                                content: $("#smallBox_content").val(),
                                                color: "#739E73",
                                                iconSmall: "fa fa-success",
                                                timeout: 4000
                                            });
                                            getReceptions();
                                            imprimirHojaTrabajo(data.numeroHoja);
                                        }
                                    },
                                    error: function (jqXHR) {
                                        desbloquearUI();
                                        validateLogin(jqXHR);
                                    }
                                });

                        }
                        if (ButtonPressed === opcNo) {
                            $.smallBox({
                                title: $("#msg_move_cancel").val(),
                                content: "<i class='fa fa-clock-o'></i> <i>" + $("#smallBox_content").val() + "</i>",
                                color: "#C79121",
                                iconSmall: "fa fa-times fa-2x fadeInRight animated",
                                timeout: 4000
                            });
                        }

                    });
                } else {
                    $.smallBox({
                        title: $("#msg_select_order").val(),
                        content: "<i class='fa fa-clock-o'></i> <i>" + $("#smallBox_content").val() + "</i>",
                        color: "#C79121",
                        iconSmall: "fa fa-times fa-2x fadeInRight animated",
                        timeout: 4000
                    });
                }

            }

            function imprimirHojaTrabajo(hoja) {
                $.ajax(
                    {
                        url: parametros.sPrintUrl,
                        type: 'GET',
                        dataType: 'text',
                        data: {hojas: hoja},
                        contentType: 'application/json',
                        mimeType: 'application/json',
                        success: function (data) {

                            var blob = blobData(data, 'application/pdf');
                            showBlob(blob);
                        },
                        error: function (jqXHR) {
                            desbloquearUI();
                            validateLogin(jqXHR);
                        }
                    });
            }
        }
    };

}();