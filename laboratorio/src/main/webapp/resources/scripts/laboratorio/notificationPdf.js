/**
 * Created by souyen-ics on 08-11-15.
 */
var VisualizarPdf = function () {

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
            var table1 = $('#orders_result').dataTable({
                "sDom": "<'dt-toolbar'<'col-xs-12 col-sm-6'f><'col-sm-6 col-xs-12 hidden-xs'l>r>" +
                    "T" +
                    "t" +
                    "<'dt-toolbar-footer'<'col-sm-6 col-xs-12 hidden-xs'i><'col-xs-12 col-sm-6'p>>",
                "autoWidth": true,
                "columns": [
                    null, null, null, null, null, null, null, null,null,
                    {
                        "className": 'details-control',
                        "orderable": false,
                        "data": null,
                        "defaultContent": ''
                    },
                    {
                        "className": 'fPdf',
                        "orderable": false
                    }
                ],
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
                },

                fnDrawCallback: function () {
                    $('.fPdf')
                        .off("click", pdfHandler)
                        .on("click", pdfHandler);
                }
            });

            function pdfHandler() {
                var id = $(this.innerHTML).data('id');
                if (id != null) {
                    exportPDF(id);
                }
            }

            $('#searchOrders-form').validate({
                // Rules for form validation
                rules: {
                    fecInicioRecepcionLab: {required: function () {
                        return $('#fecInicioRecepcionLab').val().length > 0;
                    }},
                    fecFinRecepcionLab: {required: function () {
                        return $('#fecFinRecepcionLab').val().length > 0;
                    }}
                },
                // Do not change code below
                errorPlacement: function (error, element) {
                    error.insertAfter(element.parent());
                },
                submitHandler: function (form) {
                    table1.fnClearTable();
                    //add here some ajax code to submit your form or just call form.submit() if you want to submit the form without ajax
                    getOrders(false)
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
                    },
                    baseZ: 1051 // para que se muestre bien en los modales
                });
            }

            function unBlockUI() {
                setTimeout($.unblockUI, 500);
            }

            function getOrders(showAll) {
                var filtros = {};
                if (showAll) {
                    filtros['nombreApellido'] = '';
                    filtros['fecInicioRecepcionLab'] = '';
                    filtros['fecFinRecepcionLab'] = '';
                    filtros['codSilais'] = '';
                    filtros['codUnidadSalud'] = '';
                    filtros['codTipoMx'] = '';
                    filtros['codTipoSolicitud'] = '';
                    filtros['nombreSolicitud'] = '';

                } else {
                    filtros['nombreApellido'] = $('#txtfiltroNombre').val();
                    filtros['fecInicioRecepcionLab'] = $('#fecInicioRecepcionLab').val();
                    filtros['fecFinRecepcionLab'] = $('#fecFinRecepcionLab').val();
                    filtros['codSilais'] = $('#codSilais option:selected').val();
                    filtros['codUnidadSalud'] = $('#codUnidadSalud option:selected').val();
                    filtros['codTipoMx'] = $('#codTipoMx option:selected').val();
                    filtros['codigoUnicoMx'] = $('#txtCodUnicoMx').val();
                    filtros['codTipoSolicitud'] = $('#tipo option:selected').val();
                    filtros['nombreSolicitud'] = $('#nombreSoli').val();

                }
                blockUI();
                $.getJSON(parametros.sOrdersUrl, {
                    strFilter: JSON.stringify(filtros),
                    ajax: 'true'
                }, function (dataToLoad) {
                    table1.fnClearTable();
                    var len = Object.keys(dataToLoad).length;
                    if (len > 0) {
                        for (var i = 0; i < len; i++) {
                            var json = JSON.parse(dataToLoad[i].diagnosticos);
                            var btnPdf = '<button type="button" class="btn btn-success btn-xs" data-id="' + dataToLoad[i].idNotificacion +
                                '" > <i class="fa fa-file-pdf-o"></i>';


                            table1.fnAddData(
                                [dataToLoad[i].codigoUnicoMx + " <input type='hidden' value='" + json[1].idSolicitud + "'/>", dataToLoad[i].tipoMuestra, dataToLoad[i].fechaTomaMx, dataToLoad[i].fechaInicioSintomas, dataToLoad[i].fechaRecepcionLab,
                                    dataToLoad[i].codSilais, dataToLoad[i].codUnidadSalud, dataToLoad[i].persona, dataToLoad[i].tipoNoti, " <input type='hidden' value='" + dataToLoad[i].diagnosticos + "'/>", btnPdf]);

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
                    unBlockUI();
                })
                    .fail(function (jqXHR) {
                        setTimeout($.unblockUI, 10);
                        validateLogin(jqXHR);
                    });
            }

            $("#all-orders").click(function () {
                getOrders(true);
            });

            /*PARA MOSTRAR TABLA DETALLE DX*/
            function format(d, indice) {
                // `d` is the original data object for the row
                var texto = d[indice]; //indice donde esta el input hidden
                var diagnosticos = $(texto).val();

                var json = JSON.parse(diagnosticos);
                var len = Object.keys(json).length;
                var childTable = '<table style="padding-left:20px;border-collapse: separate;border-spacing:  10px 3px;">' +
                    '<tr><td style="font-weight: bold">' + $('#text_dx').val() + '</td><td style="font-weight: bold">' + $('#text_dx_date').val() + '</td></tr>';
                for (var i = 1; i <= len; i++) {
                    childTable = childTable +
                        '<tr></tr><td>' + json[i].nombre + '</td>' +
                        '<td>' + json[i].fechaSolicitud + '</td></tr>';
                }
                childTable = childTable + '</table>';
                return childTable;
            }

            $('#orders_result tbody').on('click', 'td.details-control', function () {
                var tr = $(this).closest('tr');
                var row = table1.api().row(tr);
                if (row.child.isShown()) {
                    // This row is already open - close it
                    row.child.hide();
                    tr.removeClass('shown');
                }
                else {
                    // Open this row
                    row.child(format(row.data(), 9)).show();
                    tr.addClass('shown');
                }
            });

            //FIN


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
                    getOrders(false);

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
                    getOrders(false);
                    $('#txtCodUnicoMx').val('');
                }
            });

            function exportPDF(id) {
                blockUI();
                $.ajax(
                    {
                        url: parametros.pdfUrl,
                        type: 'GET',
                        dataType: 'text',
                        data: {idNotificacion: id},
                        contentType: 'application/json',
                        mimeType: 'application/json',
                        success: function (data) {
                            if (data.length != 0) {
                                var blob = blobData(data, 'application/pdf');
                                var blobUrl = showBlob(blob);

                            } else {
                                $.smallBox({
                                    title: $("#msjPdf").val(),
                                    content: "<i class='fa fa-clock-o'></i> <i>" + $("#smallBox_content").val() + "</i>",
                                    color: "#C79121",
                                    iconSmall: "fa fa-warning",
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