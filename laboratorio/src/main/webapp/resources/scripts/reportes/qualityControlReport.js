/**
 * Created by souyen-ics on 04-24-15.
 */


var QualityReport = function () {
    return {

        //main function to initiate the module
        init: function (parametros) {

            var codigos = "";


            var responsiveHelper_dt_basic = undefined;
            var breakpointDefinition = {
                tablet: 1024,
                phone: 480
            };


            var table1 = $('#result-samples').dataTable({
                "sDom": "<'dt-toolbar'<'col-xs-12 col-sm-6'f><'col-sm-6 col-xs-6 hidden-xs'T>r>" +
                    "t" +
                    "<'dt-toolbar-footer'<'col-sm-6 col-xs-12 hidden-xs'i><'col-sm-6 col-xs-12'p>>",
                "autoWidth": true,
                "aaSorting": [],
                /*"columns": [
                    null, null, null, null, null, null, null, null,
                    {
                        "className": 'details-control',
                        "orderable": false,
                        "data": null,
                        "defaultContent": ''
                    }
                ],*/
                "preDrawCallback": function () {
                    // Initialize the responsive datatables helper once.
                    if (!responsiveHelper_dt_basic) {
                        responsiveHelper_dt_basic = new ResponsiveDatatablesHelper($('#result-samples'), breakpointDefinition);
                    }
                },
                "rowCallback": function (nRow) {
                    responsiveHelper_dt_basic.createExpandIcon(nRow);
                },
                "drawCallback": function (oSettings) {
                    responsiveHelper_dt_basic.respond();
                },
                "oTableTools": {
                    "aButtons": [
                        {
                            "sExtends": "pdf",
                            "sTitle": "Solicitudes aprobadas control de calidad",
                            "sPdfSize": "A4",
                            "sPdfOrientation": "landscape",
                            "fnClick": function () {
                                ExportarPDF();
                            }
                        }
                    ],
                    "sSwfPath": parametros.sTableToolsPath
                }
            });

            <!-- filtro Mx -->
            $('#received-samples-form').validate({
                // Rules for form validation
                rules: {
                    fecFinMx: {required: function () {
                        return $('#fecInicioMx').val().length > 0;
                    }},
                    fecInicioMx: {required: function () {
                        return $('#fecFinMx').val().length > 0;
                    }}
                },
                // Do not change code below
                errorPlacement: function (error, element) {
                    error.insertAfter(element.parent());
                },
                submitHandler: function (form) {
                    table1.fnClearTable();
                    //add here some ajax code to submit your form or just call form.submit() if you want to submit the form without ajax
                    codigos = "";
                    getMxs(false)
                }
            });

            function blockUI() {
                var loc = window.location;
                var pathName = loc.pathname.substring(0, loc.pathname.indexOf('/', 1) + 1);
                //var mess = $("#blockUI_message").val()+' <img src=' + pathName + 'resources/img/loading.gif>';
                var mess = '<img src=' + pathName + 'resources/img/ajax-loading.gif> ' + parametros.blockMess;
                $.blockUI({ message: mess,
                    css: {
                        border: 'none',
                        padding: '15px',
                        backgroundColor: '#000',
                        '-webkit-border-radius': '10px',
                        '-moz-border-radius': '10px',
                        opacity: .5,
                        color: '#fff'
                    }});
            }

            function unBlockUI() {
                setTimeout($.unblockUI, 500);
            }

            function getMxs(showAll) {
                var mxFiltros = {};
                if (showAll) {

                    mxFiltros['fechaInicioRecepcion'] = '';
                    mxFiltros['fechaFinRecepcion'] = '';
                    mxFiltros['codSilais'] = '';
                    mxFiltros['codUnidadSalud'] = '';
                    mxFiltros['codTipoMx'] = '';
                    mxFiltros['codTipoSolicitud'] = '';
                    mxFiltros['nombreSolicitud'] = '';
                    mxFiltros['laboratorio'] = '';

                } else {

                    mxFiltros['fechaInicioRecepcion'] = $('#fecInicioRecepcion').val();
                    mxFiltros['fechaFinRecepcion'] = $('#fecFinRecepcion').val();
                    mxFiltros['codSilais'] = $('#codSilais').find('option:selected').val();
                    mxFiltros['codUnidadSalud'] = $('#codUnidadSalud').find('option:selected').val();
                    mxFiltros['codTipoMx'] = $('#codTipoMx').find('option:selected').val();
                    mxFiltros['codTipoSolicitud'] = $('#tipo').find('option:selected').val();
                    mxFiltros['nombreSolicitud'] = encodeURI($('#nombreSoli').val());
                    mxFiltros['laboratorio'] = $('#codLaboratorioOri').find('option:selected').val();

                }
                blockUI();
                $.getJSON(parametros.searchUrl, {
                    strFilter: JSON.stringify(mxFiltros),
                    ajax: 'true'
                }, function (dataToLoad) {
                    table1.fnClearTable();
                    var len = Object.keys(dataToLoad).length;

                    if (len > 0) {
                        codigos = "";
                        for (var i = 0; i < len; i++) {
                            table1.fnAddData(
                                [dataToLoad[i].solicitud, dataToLoad[i].fechaSolicitud, dataToLoad[i].fechaAprobacion, dataToLoad[i].codigoLab,
                                    //dataToLoad[i].tipoMuestra, dataToLoad[i].tipoNotificacion, dataToLoad[i].persona,
                                    dataToLoad[i].laboratorio, dataToLoad[i].resultado,dataToLoad[i].resultadocc,dataToLoad[i].coincide]);
                                        //" <input type='hidden' value='" + dataToLoad[i].resultados + "'/>"]);
                            if (i + 1 < len) {
                                codigos += dataToLoad[i].codigoUnicoMx + ",";
                            } else {
                                codigos += dataToLoad[i].codigoUnicoMx;
                            }
                        }
                        codigos = reemplazar(codigos, "-", "*");
                    } else {
                        $.smallBox({
                            title: $("#msg_no_results_found").val(),
                            content: $("#smallBox_content").val(),
                            color: "#C79121",
                            iconSmall: "fa fa-warning",
                            timeout: 4000
                        });
                    }
                    unBlockUI();
                })
                    .fail(function (jqXHR) {
                        setTimeout($.unblockUI, 10);
                        validateLogin(jqXHR);
                    });
            }


            $("#all-orders").click(function () {
                codigos = "";
                getMxs(true);
            });

            /*PARA MOSTRAR TABLA DETALLE RESULTADO*/
            function format(d, indice) {
                // `d` is the original data object for the row
                var texto = d[indice]; //indice donde esta el input hidden
                var resultado = $(texto).val();
                var json = JSON.parse(resultado);
                var len = Object.keys(json).length;
                var childTable = '<table style="padding-left:20px;border-collapse: separate;border-spacing:  10px 3px;">' +
                    '<tr><td style="font-weight: bold">' + $('#text_response').val() + '</td><td style="font-weight: bold">' + $('#text_value').val() + '</td><td style="font-weight: bold">' + $('#text_date').val() + '</td></tr>';
                for (var i = 1; i <= len; i++) {
                    childTable = childTable +
                        '<tr></tr><tr><td>' + json[i].respuesta + '</td><td>' + json[i].valor + '</td><td>' + json[i].fechaResultado + '</td></tr>';
                }
                childTable = childTable + '</table>';
                return childTable;
            }

            $('#result-samples tbody').on('click', 'td.details-control', function () {
                var tr = $(this).closest('tr');
                var row = table1.api().row(tr);
                if (row.child.isShown()) {
                    // This row is already open - close it
                    row.child.hide();
                    tr.removeClass('shown');
                }
                else {
                    // Open this row
                    row.child(format(row.data(), 5)).show();
                    tr.addClass('shown');
                }
            });

            <!-- al seleccionar SILAIS -->
            $('#codSilais').change(function () {
                blockUI();
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
                unBlockUI();
            });


            function reemplazar(texto, buscar, nuevo) {
                var temp = '';
                var long = texto.length;
                for (j = 0; j < long; j++) {
                    if (texto[j] == buscar) {
                        temp += nuevo;
                    } else
                        temp += texto[j];
                }
                return temp;
            }

            function fn_get_rep_table() {
                var oSettings = table1.fnSettings();
                var colTitles = $.map(oSettings.aoColumns, function (node) {
                    return node.sTitle;
                });

                var $str_return = '<thead><tr>';

                jQuery.each(colTitles, function () {
                    console.log(this);
                    $str_return += '<th>' + this + '</th>';
                });

                $str_return += '</tr></thead><tbody>';

                var $rep_data = table1.fnGetData();

                $.each($rep_data, function (key1, value1) {
                    $str_return += '<tr>';
                    $.each(value1, function (key2, value2) {
                        console.log(value2);
                        $str_return += '<td>' + value2 + '</td>';
                    });
                    $str_return += '</tr>';
                });

                $str_return += '</tbody>';
                return $str_return;
            }

            function ExportarPDF() {
                $.ajax(
                    {
                        url: parametros.exportUrl,
                        type: 'GET',
                        dataType: 'text',
                        data: {codigos: codigos},
                        contentType: 'application/json',
                        mimeType: 'application/json',
                        success: function (data) {
                            if (data.length != 0) {
                                var blob = blobData(data, 'application/pdf');
                                showBlob(blob);
                            } else {
                                $.smallBox({
                                    title: $("#msg_select").val(),
                                    content: "<i class='fa fa-clock-o'></i> <i>" + $("#smallBox_content").val() + "</i>",
                                    color: "#C46A69",
                                    iconSmall: "fa fa-times fa-2x fadeInRight animated",
                                    timeout: 4000
                                });
                            }

                            unBlockUI();
                        },
                        error: function (jqXHR) {
                            unBlockUI();
                            validateLogin(jqXHR);
                        }
                    });
            }

        }
    };

}();

