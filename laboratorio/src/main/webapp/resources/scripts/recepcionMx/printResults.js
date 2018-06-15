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
            var table1 = $('#list_result').dataTable({
                "sDom": "<'dt-toolbar'<'col-xs-12 col-sm-6'f><'col-sm-6 col-xs-12 hidden-xs'l>r>" +
                    "t" +
                    "<'dt-toolbar-footer'<'col-sm-6 col-xs-12 hidden-xs'i><'col-xs-12 col-sm-6'p>>",
                "autoWidth": true, //"T<'clear'>"+
                "columns": [
                    null, null, null, null, null, null, null,
                    {
                        "className": 'fPdf',
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
                }
            });

            <!-- formulario de búsqueda de resultados finales -->
            $('#searchResults-form').validate({
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
                    getResultadosAprobados(false);
                }
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
                            var btnPdf = '<button title="Resultado en Pdf" type="button" class="btn btn-success btn-xs" data-id="' + dataToLoad[i].codigoUnicoMx +
                                '" > <i class="fa fa-file-pdf-o"></i>';
                            table1.fnAddData(
                                [dataToLoad[i].codigoUnicoMx,dataToLoad[i].fechaTomaMx, dataToLoad[i].tipoMuestra, dataToLoad[i].tipoNotificacion,
                                    dataToLoad[i].codSilais, dataToLoad[i].persona, dataToLoad[i].solicitudes, btnPdf ]);
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
                    exportPDF(id);
                }
            }

            function exportPDF(id) {
                bloquearUI("");
                $.ajax(
                    {
                        url: parametros.pdfUrl,
                        type: 'GET',
                        dataType: 'text',
                        data: {codes: id},
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


            }

        }
    };

}();