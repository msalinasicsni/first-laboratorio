var PrintResults = function () {
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
            var text_selected_all = $("#text_selected_all").val();
            var text_selected_none = $("#text_selected_none").val();
            var table1 = $('#list_result').dataTable({
                "sDom": "<'dt-toolbar'<'col-xs-12 col-sm-6'f><'col-sm-6 col-xs-12 hidden-xs'l>r>" +
                    "T" +
                    "t" +
                    "<'dt-toolbar-footer'<'col-sm-6 col-xs-12 hidden-xs'i><'col-xs-12 col-sm-6'p>>",
                "autoWidth": true, //"T<'clear'>"+
                "columns": [
                    null, null, null, null, null, null, null,
                    {
                        "className": 'fPdf',
                        "orderable": false
                    },
                    {
                        "className": 'fPdfEn',
                        "orderable": false
                    }
                ],
                "preDrawCallback": function () {
                    // Initialize the responsive datatables helper once.
                    if (!responsiveHelper_dt_basic) {
                        responsiveHelper_dt_basic = new ResponsiveDatatablesHelper($('#list_result'), breakpointDefinition);
                    }
                },
                "rowCallback": function (nRow) {
                    responsiveHelper_dt_basic.createExpandIcon(nRow);
                },
                "drawCallback": function (oSettings) {
                    responsiveHelper_dt_basic.respond();
                },
                fnDrawCallback: function () {
                    $('.fPdf')
                        .off("click", pdfHandler)
                        .on("click", pdfHandler);
                    $('.fPdfEn')
                        .off("click", pdfHandlerEn)
                        .on("click", pdfHandlerEn);
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

            <!-- formulario de b�squeda de resultados finales -->
            $('#searchResults-form').validate({
                // Rules for form validation
                rules: {
                    fecFinTomaMx: {required: function () {
                        return $('#fecInicioTomaMx').val().length > 0;
                    }},
                    fecInicioTomaMx: {required: function () {
                        return $('#fecFinTomaMx').val().length > 0;
                    }},
                    fechaFinAprob: {required: function () {
                            return $('#fechaInicioAprob').val().length > 0;
                        }},
                    fechaInicioAprob: {required: function () {
                            return $('#fechaFinAprob').val().length > 0;
                        }}
                },
                // Do not change code below
                errorPlacement: function (error, element) {
                    error.insertAfter(element.parent());
                },
                submitHandler: function (form) {
                    table1.fnClearTable();
                    //add here some ajax code to submit your form or just call form.submit() if you want to submit the form without ajax
                    getResultadosAprobados(false);
                }
            });

            $('#btnExport').click(function () {
                exportSelected();
            });

            function getResultadosAprobados(showAll) {
                var filtros = {};
                if (showAll) {
                    filtros['nombreApellido'] = '';
                    filtros['fechaInicioTomaMx'] = '';
                    filtros['fechaFinTomaMx'] = '';
                    filtros['codSilais'] = '';
                    filtros['codUnidadSalud'] = '';
                    filtros['codTipoMx'] = '';
                    filtros['esLab'] = $('#txtEsLaboratorio').val();
                    filtros['codTipoSolicitud'] = '';
                    filtros['nombreSolicitud'] = '';
                    filtros['conResultado'] = 'Si';
                    filtros['solicitudAprobada'] = 'true';
                    filtros['fechaInicioAprob'] = '';
                    filtros['fechaFinAprob'] = '';
                    filtros['idioma'] = '';
                    filtros['modalidad'] = '';
                } else {
                    filtros['nombreApellido'] = $('#txtfiltroNombre').val();
                    filtros['fechaInicioTomaMx'] = $('#fecInicioTomaMx').val();
                    filtros['fechaFinTomaMx'] = $('#fecFinTomaMx').val();
                    filtros['codSilais'] = $('#codSilais').find('option:selected').val();
                    filtros['codUnidadSalud'] = $('#codUnidadSalud').find('option:selected').val();
                    filtros['codTipoMx'] = $('#codTipoMx').find('option:selected').val();
                    filtros['esLab'] = $('#txtEsLaboratorio').val();
                    filtros['codigoUnicoMx'] = $('#txtCodUnicoMx').val();
                    filtros['codTipoSolicitud'] = $('#tipo').find('option:selected').val();
                    filtros['nombreSolicitud'] = $('#nombreSoli').val();
                    filtros['conResultado'] = 'Si';
                    filtros['solicitudAprobada'] = 'true';
                    filtros['fechaInicioAprob'] = $('#fechaInicioAprob').val();
                    filtros['fechaFinAprob'] = $('#fechaFinAprob').val();
                    filtros['idioma'] = $('#idioma').find('option:selected').val();
                    filtros['modalidad'] = $('#modalidad').find('option:selected').val();
                }
                bloquearUI(parametros.blockMess);
                $.getJSON(parametros.sSearchUrl, {
                    strFilter: JSON.stringify(filtros),
                    ajax: 'true'
                }, function (dataToLoad) {
                    table1.fnClearTable();
                    var len = Object.keys(dataToLoad).length;
                    if (len > 0) {
                        for (var i = 0; i < len; i++) {
                            //console.log(dataToLoad[i].resultados);
                            var btnPdf = '<button title="Resultado en Pdf Espa\u00F1ol" type="button" class="btn btn-default btn-xs" data-id="' + dataToLoad[i].codigoUnicoMx +
                                '" > <span class="flag-icon flag-icon-squared flag-icon-ni"></span> <i class="fa fa-file-pdf-o"></i>';
                            var btnPdfEn = ' ';
                            if (dataToLoad[i].solicitudes.indexOf("Covid19") !== -1 )
                                btnPdfEn = '<button title="Resultado en Pdf Ingl\u00E9s" type="button" class="btn btn-default btn-xs" data-id="' + dataToLoad[i].codigoUnicoMx +
                                    '" > <span class="flag-icon flag-icon-squared flag-icon-us"></span> <i class="fa fa-file-pdf-o"></i>';
                            table1.fnAddData(
                                [dataToLoad[i].codigoUnicoMx,dataToLoad[i].fechaTomaMx, dataToLoad[i].tipoMuestra, dataToLoad[i].tipoNotificacion,
                                    dataToLoad[i].codSilais, dataToLoad[i].persona, dataToLoad[i].solicitudes, btnPdf, btnPdfEn ]);
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

            function pdfHandler() {
                var id = $(this.innerHTML).data('id');
                if (id != null) {
                    exportPDF(id, 'ES');
                }
            }

            function pdfHandlerEn() {
                var id = $(this.innerHTML).data('id');
                if (id != null) {
                    exportPDF(id, 'EN');
                }
            }

            /*function exportPDF(codes, languaje) {
                bloquearUI(parametros.blockMess);
                $.ajax(
                    {
                        url: parametros.pdfUrl,
                        type: 'GET',
                        dataType: 'text',
                        data: {codes: codes, languaje: languaje},
                        contentType: 'application/json',
                        mimeType: 'application/json',
                        success: function (data) {
                            if (data.length != 0) {
                                var blob = blobData(data, 'application/pdf');
                                var blobUrl = showBlob(blob);

                            } else {
                                $.smallBox({
                                    title: $("#msg_no_results_found").val(),
                                    content: "<i class='fa fa-clock-o'></i> <i>" + $("#smallBox_content").val() + "</i>",
                                    color: "#C79121",
                                    iconSmall: "fa fa-warning",
                                    timeout: 4000
                                });
                            }

                            desbloquearUI();
                        },
                        error: function (jqXHR) {
                            desbloquearUI();
                            validateLogin(jqXHR);
                        }
                    });
            }*/

            function exportPDF(codes, languaje) {
                var valueObj = {};
                valueObj['codes'] = codes;
                valueObj['languaje'] = languaje;
                valueObj['strB64Response'] = '';
                bloquearUI(parametros.blockMess);
                $.ajax(
                    {
                        url: parametros.pdfUrl,
                        type: 'POST',
                        dataType: 'json',
                        data: JSON.stringify(valueObj),
                        contentType: 'application/json',
                        mimeType: 'application/json',
                        success: function (data) {
                            if (data.strB64Response.length != 0) {
                                var blob = blobData(data.strB64Response, 'application/pdf');
                                var blobUrl = showBlob(blob);
                            } else {
                                $.smallBox({
                                    title: $("#msg_no_results_found").val(),
                                    content: "<i class='fa fa-clock-o'></i> <i>" + $("#smallBox_content").val() + "</i>",
                                    color: "#C79121",
                                    iconSmall: "fa fa-warning",
                                    timeout: 4000
                                });
                            }
                            desbloquearUI()
                        },
                        error: function (jqXHR) {
                            desbloquearUI();
                            validateLogin(jqXHR);
                        }
                    });
            }

            function exportSelected() {
                var oTT = TableTools.fnGetInstance('list_result');
                var aSelectedTrs = oTT.fnGetSelected();
                console.log('fnGetSelected');
                console.log(aSelectedTrs);

                var len = aSelectedTrs.length;
                var opcSi = $("#yes").val();
                var opcNo = $("#no").val();
                if (len > 0) {
                    $.SmartMessageBox({
                        title: $("#msg_confirm_print").val(),
                        content: $("#msg_print_confirm_c").val(),
                        buttons: '[' + opcSi + '][' + opcNo + ']'
                    }, function (ButtonPressed) {
                        if (ButtonPressed === opcSi) {
                            var codesLab = [];
                            for (var i = 0; i < len; i++) {
                                var texto = aSelectedTrs[i].firstChild.innerHTML;
                                if (i + 1 < len) {
                                    codesLab += texto+ ",";
                                } else {
                                    codesLab += texto;
                                }

                                //desbloquearUI();
                            }
                            //codesLab = reemplazar(codesLab, ".", "*");
                            exportPDF(codesLab, "");

                        }
                        if (ButtonPressed === opcNo) {
                            $.smallBox({
                                title: $("#msg_print_canceled").val(),
                                content: "<i class='fa fa-clock-o'></i> <i>" + $("#smallBox_content").val() + "</i>",
                                color: "#3276B1",
                                iconSmall: "fa fa-times fa-2x fadeInRight animated",
                                timeout: 4000
                            });
                        }

                    });
                } else {
                    $.smallBox({
                        title: $("#msg_print_select").val(),
                        content: "<i class='fa fa-clock-o'></i> <i>" + $("#smallBox_content").val() + "</i>",
                        color: "#C46A69",
                        iconSmall: "fa fa-times fa-2x fadeInRight animated",
                        timeout: 4000
                    });
                }

            }

        }
    };

}();