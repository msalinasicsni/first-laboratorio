var SearchMx = function () {
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
            var table1 = $('#mx-results').dataTable({
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
                        "className": 'movemx',
                        "orderable": false
                    },
                    {
                        "className": 'override',
                        "orderable": false
                    }
                ],
                "preDrawCallback": function () {
                    // Initialize the responsive datatables helper once.
                    if (!responsiveHelper_dt_basic) {
                        responsiveHelper_dt_basic = new ResponsiveDatatablesHelper($('#mx-results'), breakpointDefinition);
                    }
                },
                "rowCallback": function (nRow) {
                    responsiveHelper_dt_basic.createExpandIcon(nRow);
                },
                "drawCallback": function (oSettings) {
                    responsiveHelper_dt_basic.respond();
                },
                fnDrawCallback: function () {
                    $('.movemx')
                        .off("click", moveHandler)
                        .on("click", moveHandler);
                    $('.override')
                        .off("click", overrideHandler)
                        .on("click", overrideHandler);
                }
            });

            <!-- formulario de búsqueda de mx -->
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
                    }
                );
            }

            function unBlockUI() {
                setTimeout($.unblockUI, 500);
            }

            function getMxs(showAll) {
                var mxFiltros = {};
                if (showAll) {
                    mxFiltros['nombreApellido'] = '';
                    mxFiltros['fechaInicioRecep'] = '';
                    mxFiltros['fechaFinRecepcion'] = '';
                    mxFiltros['codSilais'] = '';
                    mxFiltros['codUnidadSalud'] = '';
                    mxFiltros['codTipoMx'] = '';
                    mxFiltros['esLab'] = $('#txtEsLaboratorio').val();
                    mxFiltros['codigoUnicoMx'] = '';
                    mxFiltros['codTipoSolicitud'] = '';
                    mxFiltros['nombreSolicitud'] = '';
                    mxFiltros['solicitudAprobada'] = '';
                } else {
                    mxFiltros['nombreApellido'] = $('#txtfiltroNombre').val();
                    mxFiltros['fechaInicioRecep'] = $('#fecInicioTomaMx').val();
                    mxFiltros['fechaFinRecepcion'] = $('#fecFinTomaMx').val();
                    mxFiltros['codSilais'] = $('#codSilais').find('option:selected').val();
                    mxFiltros['codUnidadSalud'] = $('#codUnidadSalud').find('option:selected').val();
                    mxFiltros['codTipoMx'] = $('#codTipoMx').find('option:selected').val();
                    mxFiltros['esLab'] = $('#txtEsLaboratorio').val();
                    mxFiltros['codigoUnicoMx'] = $('#txtCodUnicoMx').val();
                    mxFiltros['codTipoSolicitud'] = $('#tipo option:selected').val();
                    mxFiltros['nombreSolicitud'] = $('#nombreSoli').val();
                    mxFiltros['solicitudAprobada'] = $('#aprobado').val();
                }
                blockUI();
                $.getJSON(parametros.searchUrl, {
                    strFilter: JSON.stringify(mxFiltros),
                    ajax: 'true'
                }, function (dataToLoad) {
                    table1.fnClearTable();
                    var len = Object.keys(dataToLoad).length;
                    if (len > 0) {

                        for (var i = 0; i < len; i++) {

                            var btnMove = '<button title="Mover" type="button" class="btn btn-primary btn-xs" disabled> <i class="fa fa-random"></i></button>';
                            var btnOverride = '<button title="Anular" type="button" disabled class="btn btn-danger btn-xs"> <i class="fa fa-times"></i></button>';
                            if ($('#nivelCentral').val()=='true' && dataToLoad[i].anulada === '0'){
                                btnMove = '<button title="Mover" type="button" class="btn btn-primary btn-xs" data-id="' + dataToLoad[i].codigoUnicoMx + '" > <i class="fa fa-random"></i></button>';
                                btnOverride = '<button title="Anular" type="button" class="btn btn-danger btn-xs" data-id="' + dataToLoad[i].codigoUnicoMx + '" > <i class="fa fa-times"></i></button>';
                            }

                            table1.fnAddData(
                                [dataToLoad[i].codigoUnicoMx + " <input type='hidden' value='" + dataToLoad[i].idTomaMx + "'/>", dataToLoad[i].fechaTomaMx, dataToLoad[i].estadoMx, dataToLoad[i].fechaAnulacion, dataToLoad[i].codSilais,
                                    dataToLoad[i].codUnidadSalud, dataToLoad[i].laboratorio, dataToLoad[i].persona, " <input type='hidden' value='" + dataToLoad[i].solicitudes + "'/>",btnMove, btnOverride]);

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
                getMxs(true);
            });

            /*PARA MOSTRAR TABLA DETALLE DX*/
            function format(d, indice) {
                // `d` is the original data object for the row
                var texto = d[indice]; //indice donde esta el input hidden
                var diagnosticos = $(texto).val();

                var json = JSON.parse(diagnosticos);
                var len = Object.keys(json).length;
                var childTable = '<table style="padding-left:20px;border-collapse: separate;border-spacing:  10px 3px;">' +
                    '<tr><td style="font-weight: bold">' + $('#text_dx').val() + '</td><td style="font-weight: bold">' + $('#text_cc').val() + '</td><td style="font-weight: bold">' + $('#text_dx_date').val() + '</td><td style="font-weight: bold">' + $('#res_aprob').val() + '</td><td style="font-weight: bold">' + $('#fec_aprob').val() + '</td> </tr>';
                for (var i = 1; i <= len; i++) {
                    childTable = childTable +
                        '<tr><td>' + json[i].nombre + '</td>' +
                        '<td>' + json[i].cc + '</td>' +
                        '<td>' + json[i].fechaSolicitud + '</td>' +
                        '<td>' + json[i].estado + '</td>' +
                        '<td>' + json[i].fechaAprobacion + '</td></tr>';
                }
                childTable = childTable + '</table>';
                return childTable;
            }

            $('#mx-results tbody').on('click', 'td.details-control', function () {
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

            function moveHandler() {
                var id = $(this.innerHTML).data('id');
                if (id != null) {
                    $("#codigoMx").val(id);
                    $("#lblCodigoMx").text(id);
                    showModalMoveMx();
                }
            }

            function showModalMoveMx() {
                clearFilterPopUp();
                $("#modalMoveMx").modal({
                    show: true
                });
            }

            function hideModalMoveMx() {
                $('#modalMoveMx').modal('hide');
            }

            var table2 = $('#noti-results').dataTable({
                "sDom": "<'dt-toolbar'<'col-xs-12 col-sm-6'><'col-sm-6 col-xs-12 hidden-xs'>r>"+
                    "t"+
                    "<'dt-toolbar-footer'<'col-sm-6 col-xs-12 hidden-xs'i><'col-xs-12 col-sm-6'p>>",
                "autoWidth" : true,
                "preDrawCallback" : function() {
                    // Initialize the responsive datatables helper once.
                    if (!responsiveHelper_dt_basic) {
                        responsiveHelper_dt_basic = new ResponsiveDatatablesHelper($('#noti-results'), breakpointDefinition);
                    }
                },
                "rowCallback" : function(nRow) {
                    responsiveHelper_dt_basic.createExpandIcon(nRow);
                },
                "drawCallback" : function(oSettings) {
                    responsiveHelper_dt_basic.respond();
                },
                "pageLength": 5,
                "columns": [
                    null, null, null, null, null, null, null, null,
                    {
                        "className": 'selecNoti',
                        "orderable": false
                    }
                ],
                fnDrawCallback: function () {
                    $('.selecNoti')
                        .off("click", selectNotiHandler)
                        .on("click", selectNotiHandler);
                }

            });

            <!-- formulario de búsqueda de notificaciones a mover muestra -->
            $('#search-noti-form').validate({
                // Rules for form validation
                rules: {
                    fecFinNotiPopUp: {required: function () {
                        return $('#fecInicioNotiPopUp').val().length > 0;
                    }},
                    fecInicioNotiPopUp: {required: function () {
                        return $('#fecFinNotiPopUp').val().length > 0;
                    }}
                },
                // Do not change code below
                errorPlacement: function (error, element) {
                    error.insertAfter(element.parent());
                },
                submitHandler: function (form) {
                    table2.fnClearTable();
                    //add here some ajax code to submit your form or just call form.submit() if you want to submit the form without ajax
                    getNotifications(false)
                }
            });

            <!-- al seleccionar SILAIS en POP mover mx-->
            $('#codSilaisPopUp').change(function () {
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
                        $('#codUnidadSaludPopUp').html(html);
                    }).fail(function (jqXHR) {
                        setTimeout($.unblockUI, 10);
                        validateLogin(jqXHR);
                    });
                } else {
                    var html = '<option value="">' + $("#text_opt_select").val() + '...</option>';
                    $('#codUnidadSaludPopUp').html(html);
                }
                $('#codUnidadSaludPopUp').val('').change();
                unBlockUI();
            });

            function selectNotiHandler() {
                var id = $(this.innerHTML).data('id');
                if (id != null) {
                    confirmarAccionMover(id);
                }
            }

            function confirmarAccionMover(idNotificacion){
                var opcSi = $("#confirm_msg_opc_yes").val();
                var opcNo = $("#confirm_msg_opc_no").val();
                $.SmartMessageBox({
                    title: $("#msg_confirm_title").val(),
                    content: $("#msg_confirm_content").val().replace('%s',$("#lblCodigoMx").text()),
                    buttons: '[' + opcSi + '][' + opcNo + ']'
                }, function (ButtonPressed) {
                    if (ButtonPressed === opcSi) {
                        moveSample(idNotificacion);
                    }
                    if (ButtonPressed === opcNo) {
                        $.smallBox({
                            title: $("#msg_action_canceled").val(),
                            content: "<i class='fa fa-clock-o'></i> <i>" + $("#smallBox_content").val() + "</i>",
                            color: "#3276B1",
                            iconSmall: "fa fa-times fa-2x fadeInRight animated",
                            timeout: 3000
                        });
                    }
                })
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
                }else {
                    notificacionesFiltro['nombreApellido'] = $('#txtfiltroNombrePopUp').val();
                    notificacionesFiltro['fechaInicioNoti'] = $('#fecInicioNotiPopUp').val();
                    notificacionesFiltro['fechaFinNoti'] = $('#fecFinNotiPopUp').val();
                    notificacionesFiltro['codSilais'] = $('#codSilaisPopUp').find('option:selected').val();
                    notificacionesFiltro['codUnidadSalud'] = $('#codUnidadSaludPopUp').find('option:selected').val();
                    notificacionesFiltro['tipoNotificacion'] = $('#codTipoNoti').find('option:selected').val();
                }
                blockUI();
                $.getJSON(parametros.notificacionesUrl, {
                    strFilter: JSON.stringify(notificacionesFiltro),
                    ajax : 'true'
                }, function(dataToLoad) {
                    table2.fnClearTable();
                    var len = Object.keys(dataToLoad).length;
                    if (len > 0) {
                        for (var i = 0; i < len; i++) {
                            table2.fnAddData(
                                [dataToLoad[i].persona, dataToLoad[i].edad, dataToLoad[i].sexo,dataToLoad[i].silais, dataToLoad[i].unidad,dataToLoad[i].tipoNoti,
                                    dataToLoad[i].fechaRegistro, dataToLoad[i].fechaInicioSintomas,
                                    '<button title="Seleccionar" type="button" class="btn btn-success btn-xs" data-id="' + dataToLoad[i].idNotificacion + '"> <i class="fa fa-check-circle"></i></button>']);
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

            function clearFilterPopUp(){
                $("#txtfiltroNombrePopUp").val('');
                $("#fecInicioNotiPopUp").val('');
                $("#fecFinNotiPopUp").val('');
                $("#codSilaisPopUp").val('').change();
                $("#codUnidadSaludPopUp").val('').change();
                $("#codTipoNoti").val('').change();
            }

            function moveSample(idNotificacion) {
                var valueObj = {};
                valueObj['mensaje'] = '';
                valueObj['idNotificacion'] = idNotificacion;
                valueObj['codigoMx'] = $('#codigoMx').val();
                blockUI();
                $.ajax(
                    {
                        url: parametros.moveMxUrl,
                        type: 'POST',
                        dataType: 'json',
                        data: JSON.stringify(valueObj),
                        contentType: 'application/json',
                        mimeType: 'application/json',
                        success: function (data) {
                            if (data.mensaje.length > 0) {
                                $.smallBox({
                                    title: unicodeEscape(data.mensaje),
                                    content: $("#smallBox_content").val(),
                                    color: "#C46A69",
                                    iconSmall: "fa fa-warning",
                                    timeout: 4000
                                });
                            } else {
                                var msg = $("#msg_successful").val();
                                $.smallBox({
                                    title: msg,
                                    content: $("#smallBox_content").val(),
                                    color: "#739E73",
                                    iconSmall: "fa fa-success",
                                    timeout: 4000
                                });
                                hideModalMoveMx();
                                clearFilterPopUp();
                                table1.fnClearTable();
                                table2.fnClearTable();
                            }
                            unBlockUI()
                        },
                        error: function (jqXHR) {
                            unBlockUI();
                            validateLogin(jqXHR);
                        }
                    });
            }

            //ANULAR MUESTRA
            <!-- formulario para anular muestra -->
            $('#override-mx-form').validate({
                // Rules for form validation
                rules: {
                    causaAnulacion: {required: true}
                },
                // Do not change code below
                errorPlacement: function (error, element) {
                    error.insertAfter(element.parent());
                },
                submitHandler: function (form) {
                    anularMuestra($("#codigoMx").val());
                }
            });

            function overrideHandler() {
                var id = $(this.innerHTML).data('id');
                if (id != null) {
                    $("#codigoMx").val(id);
                    $("#lblCodigoMx2").text(id);
                    showModalOverride();
                }
            }

            function showModalOverride() {
                $("#causaAnulacion").val('');
                $("#modalOverride").modal({
                    show: true
                });
            }

            function hideModalOverride() {
                $('#modalOverride').modal('hide');
            }

            function anularMuestra(codigoMx) {
                var anulacionObj = {};
                anulacionObj['codigoMx'] = codigoMx;
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

