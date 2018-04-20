/**
 * Created by souyen-ics on 08-11-15.
 */
var BuscarNotificacion = function () {

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
                    "t" +
                    "<'dt-toolbar-footer'<'col-sm-6 col-xs-12 hidden-xs'i><'col-xs-12 col-sm-6'p>>",
                "autoWidth": true,
                "columns": [
                    null, null, null, null, null, null, null, null,
                    {
                        "className": 'details-control',
                        "orderable": false,
                        "data": null,
                        "defaultContent": ''
                    },
                    {
                        "className": 'override',
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
                    $('.override')
                        .off("click", overrideHandler)
                        .on("click", overrideHandler);
                }
            });

            function overrideHandler() {
                var id = $(this.innerHTML).data('id');
                if (id != null) {
                    $('#idOverride').val(id);
                    $('#d_confirmacion').modal('show');
                }
            }

            function hideModalOverride() {
                $('#d_confirmacion').modal('hide');
            }

            <!-- formulario para anular muestra -->
            $('#override-noti-form').validate({
                // Rules for form validation
                rules: {
                    causaAnulacion: {required: true}
                },
                // Do not change code below
                errorPlacement: function (error, element) {
                    error.insertAfter(element.parent());
                },
                submitHandler: function (form) {
                    anularNotificacion($("#idOverride").val());
                }
            });

            $('#searchOrders-form').validate({
                // Rules for form validation
                rules: {
                    fechaInicioNoti: {required: function () {
                        return $('#fechaFinNoti').val().length > 0;
                    }},
                    fechaFinNoti: {required: function () {
                        return $('#fechaInicioNoti').val().length > 0;
                    }}
                },
                // Do not change code below
                errorPlacement: function (error, element) {
                    error.insertAfter(element.parent());
                },
                submitHandler: function (form) {
                    table1.fnClearTable();
                    //add here some ajax code to submit your form or just call form.submit() if you want to submit the form without ajax
                    getNotifications(false)
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

            function getMxs(showAll) {
                var filtros = {};
                if (showAll) {
                    filtros['nombreApellido'] = '';
                    filtros['fechaInicioNoti'] = '';
                    filtros['fechaFinNoti'] = '';
                    filtros['codSilais'] = '';
                    filtros['codUnidadSalud'] = '';
                    filtros['tipoNotificacion'] = '';

                } else {
                    filtros['nombreApellido'] = $('#txtfiltroNombre').val();
                    filtros['fechaInicioNoti'] = $('#fechaInicioNoti').val();
                    filtros['fechaFinNoti'] = $('#fechaFinNoti').val();
                    filtros['codSilais'] = $('#codSilais option:selected').val();
                    filtros['codUnidadSalud'] = $('#codUnidadSalud option:selected').val();
                    filtros['codigoUnicoMx'] = $('#txtCodUnicoMx').val();
                    filtros['tipoNotificacion'] = $('#tiponoti option:selected').val();

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
                            var btnOverride = '<button title="Anular" type="button" class="btn btn-danger btn-xs" data-id="' + dataToLoad[i].idNotificacion + '" > <i class="fa fa-times"></i>';

                            table1.fnAddData(
                                [dataToLoad[i].codigoUnicoMx + " <input type='hidden' value='" + json[1].idSolicitud + "'/>", dataToLoad[i].fechaTomaMx, dataToLoad[i].tipoNoti, dataToLoad[i].fechaNotificacion,
                                    dataToLoad[i].codSilais, dataToLoad[i].codUnidadSalud, dataToLoad[i].persona, dataToLoad[i].fechaInicioSintomas, " <input type='hidden' value='" + dataToLoad[i].diagnosticos + "'/>", btnOverride]);

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

            function getNotifications(showAll) {
                var notificacionesFiltro = {};
                if (showAll){
                    notificacionesFiltro['nombreApellido'] = '';
                    notificacionesFiltro['fechaInicioNoti'] = '';
                    notificacionesFiltro['fechaFinNoti'] = '';
                    notificacionesFiltro['codSilais'] = '';
                    notificacionesFiltro['codUnidadSalud'] = '';
                    notificacionesFiltro['tipoNotificacion'] = '';
                    notificacionesFiltro['codigoUnicoMx'] = '';
                }else {
                    notificacionesFiltro['nombreApellido'] = $('#txtfiltroNombre').val();
                    notificacionesFiltro['fechaInicioNoti'] = $('#fechaInicioNoti').val();
                    notificacionesFiltro['fechaFinNoti'] = $('#fechaFinNoti').val();
                    notificacionesFiltro['codSilais'] = $('#codSilais').find('option:selected').val();
                    notificacionesFiltro['codUnidadSalud'] = $('#codUnidadSalud').find('option:selected').val();
                    notificacionesFiltro['tipoNotificacion'] = $('#codTipoNoti').find('option:selected').val();
                    notificacionesFiltro['codigoUnicoMx'] = $('#txtCodUnicoMx').val();
                }
                blockUI();
                $.getJSON(parametros.notificacionesUrl, {
                    strFilter: JSON.stringify(notificacionesFiltro),
                    ajax : 'true'
                }, function(dataToLoad) {
                    table1.fnClearTable();
                    var len = Object.keys(dataToLoad).length;
                    if (len > 0) {
                        for (var i = 0; i < len; i++) {
                            table1.fnAddData(
                                [dataToLoad[i].persona, dataToLoad[i].edad, dataToLoad[i].sexo,dataToLoad[i].silais, dataToLoad[i].unidad,dataToLoad[i].tipoNoti,
                                    dataToLoad[i].fechaRegistro, dataToLoad[i].fechaInicioSintomas, " <input type='hidden' value='" + dataToLoad[i].solicitudes + "'/>",
                                        '<button title="Anular" type="button" class="btn btn-danger btn-xs" data-id="' + dataToLoad[i].idNotificacion + '"> <i class="fa fa-times fa-fw"></i></button>']);
                        }
                    }else{
                        $.smallBox({
                            title: $("#msg_no_results_found").val() ,
                            content: $("#smallBox_content").val(),
                            color: "#C79121",
                            iconSmall: "fa fa-warning",
                            timeout: 4000
                        });
                    }
                    unBlockUI();
                })
                    .fail(function (XMLHttpRequest, textStatus, errorThrown) {
                        unBlockUI();
                        $.smallBox({
                            title: "FAIL" ,
                            content: errorThrown,
                            color: "#C46A69",
                            iconSmall: "fa fa-warning",
                            timeout: 8000
                        });
                    });
            }

            $("#all-orders").click(function () {
                getNotifications(true);
            });

            /*PARA MOSTRAR TABLA DETALLE DX*/
            function format(d, indice) {
                // `d` is the original data object for the row
                var texto = d[indice]; //indice donde esta el input hidden
                var diagnosticos = $(texto).val();

                var json = JSON.parse(diagnosticos);
                var len = Object.keys(json).length;
                var childTable = '<table style="padding-left:20px;border-collapse: separate;border-spacing:  10px 3px;">' +
                    '<tr>' +
                    '<td style="font-weight: bold">' + $('#text_codmx').val() + '</td>' +
                    '<td style="font-weight: bold">' + $('#text_dx_date').val() + '</td>' +
                    '<td style="font-weight: bold">' + $('#text_dx').val() + '</td>' +
                    '<td style="font-weight: bold">' + $('#text_conres').val() + '</td>' +
                    '<td style="font-weight: bold">' + $('#text_detres').val() + '</td>' +
                    '</tr>';
                for (var i = 1; i <= len; i++) {
                    childTable = childTable +
                        '<tr>' +
                        '<td>' + json[i].codigoUnicoMx + '</td>' +
                        '<td>' + json[i].fechaTomaMx + '</td>' +
                        '<td>' + json[i].diagnostico + '</td>' +
                        '<td>' + json[i].resultadoS + '</td>' +
                        '<td>' + json[i].detResultado + '</td>' +
                        '</tr>';
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
                    row.child(format(row.data(), 8)).show();
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
                    getNotifications(false);

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
                    getNotifications(false);
                    $('#txtCodUnicoMx').val('');
                }
            });

            function anularNotificacion(idNotificacion) {
                var anulacionObj = {};
                anulacionObj['idNotificacion'] = idNotificacion;
                anulacionObj['causaAnulacion'] = $("#causaAnulacion").val();
                anulacionObj['mensaje'] = '';
                blockUI();
                $.ajax(
                    {
                        url: parametros.sOverrideUrl,
                        type: 'POST',
                        dataType: 'json',
                        data: JSON.stringify(anulacionObj),
                        contentType: 'application/json',
                        mimeType: 'application/json',
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
                                table1.fnClearTable();
                                hideModalOverride();
                                var msg = $("#msg_override_success").val();
                                $.smallBox({
                                    title: msg,
                                    content: $("#smallBox_content").val(),
                                    color: "#739E73",
                                    iconSmall: "fa fa-success",
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