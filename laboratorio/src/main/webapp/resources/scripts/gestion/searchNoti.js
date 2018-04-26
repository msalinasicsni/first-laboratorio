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
                        "className": 'redefine',
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
                    $('.redefine')
                        .off("click", redefineHandler)
                        .on("click", redefineHandler);
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

            function redefineHandler() {
                $('#idRedefine').val('');
                $('#codTipoNotiRedef').val('').change();
                $('#codMunicipioNoti').val('');
                $('#codUnidadNoti').val('');
                $('#fechaInicioSintomas').val('').change();
                $('#codSilaisAtencion').val('').change();
                $('#codMunicipio').val('').change();
                $('#codUnidadAtencion').val('').change();
                var id = $(this.innerHTML).data('id');
                if (id != null) {
                    var codigos = id.split(",");
                    $('#idRedefine').val(codigos[0]);
                    if (codigos[1]!= 'ND'){
                        $('#codSilaisAtencion').val(codigos[1]).change();
                    }
                    if (codigos[2]!= 'ND'){
                        $('#codMunicipioNoti').val(codigos[2]);
                    }
                    if (codigos[3]!= 'ND'){
                        $('#codUnidadNoti').val(codigos[3]);
                    }
                    if (codigos[4]!= '') {
                        $('#fechaInicioSintomas').val(codigos[4]).change();
                    }
                    $('#lblPersona').text(codigos[5]);
                    $('#d_redefine').modal('show');
                }
            }

            function hideModalRedefine() {
                $('#d_redefine').modal('hide');
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

            <!-- formulario para anular muestra -->
            $('#redefine-noti-form').validate({
                // Rules for form validation
                rules: {
                    codTipoNotiRedef: {required: true},
                    fechaInicioSintomas: {required: true}
                },
                // Do not change code below
                errorPlacement: function (error, element) {
                    error.insertAfter(element.parent());
                },
                submitHandler: function (form) {
                   guardarNotificacion();
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
                            var btnRedefine = '<button title="Mover" type="button" class="btn btn-primary btn-xs" disabled> <i class="fa fa-repeat"></i></button>';
                            if (dataToLoad[i].tipoNoti==='PACIENTE'){
                                btnRedefine = '<button title="Mover" type="button" class="btn btn-primary btn-xs" data-id="' + dataToLoad[i].idNotificacion+ "," + dataToLoad[i].codSilais+ "," +
                                    dataToLoad[i].codMunicipio+ "," + dataToLoad[i].codUnidad+ "," + dataToLoad[i].fechaInicioSintomas+"," + dataToLoad[i].persona+'"> <i class="fa fa-repeat"></i></button>';
                            }
                            table1.fnAddData(
                                [dataToLoad[i].persona, dataToLoad[i].edad, dataToLoad[i].sexo,dataToLoad[i].silais, dataToLoad[i].unidad,dataToLoad[i].tipoNoti,
                                    dataToLoad[i].fechaRegistro, dataToLoad[i].fechaInicioSintomas, " <input type='hidden' value='" + dataToLoad[i].solicitudes + "'/>",
                                    btnRedefine,
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
                    $.getJSON(parametros.sUnidadesSILAISUrl, {
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

            $('#codSilaisAtencion').change(
                function() {
                    blockUI();
                    if ($(this).val().length > 0) {
                        $.getJSON(parametros.municipiosUrl, {
                            idSilais : $('#codSilaisAtencion').val(),
                            ajax : 'true'
                        }, function(data) {
                            $("#codMunicipio").select2('data',null);
                            $("#codUnidadAtencion").select2('data',null);
                            $("#codMunicipio").empty();
                            $("#codUnidadAtencion").empty();
                            var html='<option value="">' + $("#text_opt_select").val() + '...</option>';
                            var len = data.length;
                            for ( var i = 0; i < len; i++) {
                                html += '<option value="' + data[i].codigoNacional + '">'
                                    + data[i].nombre + '</option>';
                            }
                            html += '</option>';
                            $('#codMunicipio').html(html);
                            $('#codMunicipio').val($("#codMunicipioNoti").val()).change();
                        });
                    } else {
                        var html = '<option value="">' + $("#text_opt_select").val() + '...</option>';
                        $('#codMunicipio').html(html);
                    }
                    unBlockUI();
                });

            $('#codMunicipio').change(
                function() {
                    blockUI();
                    if ($(this).val().length > 0) {
                        $.getJSON(parametros.unidadesUrl, {
                            codMunicipio : $('#codMunicipio').val(),
                            codSilais: $('#codSilaisAtencion').val(),
                            ajax : 'true'
                        }, function(data) {
                            $("#codUnidadAtencion").select2('data',null);
                            $("#codUnidadAtencion").empty();
                            var html='<option value="">' + $("#text_opt_select").val() + '...</option>';
                            var len = data.length;
                            for ( var i = 0; i < len; i++) {
                                html += '<option value="' + data[i].codigo + '">'
                                    + data[i].nombre + '</option>';
                            }
                            html += '</option>';
                            $('#codUnidadAtencion').html(html);
                            $("#codUnidadAtencion").val($("#codUnidadNoti").val()).change();
                        });
                    } else {
                        var html = '<option value="">' + $("#text_opt_select").val() + '...</option>';
                        $('#codUnidadAtencion').html(html);
                    }
                    unBlockUI();
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

            function guardarNotificacion() {
                blockUI();
                var objetoNoti = {};
                objetoNoti['mensaje'] = '';
                objetoNoti['idNotificacion'] = $("#idRedefine").val();
                objetoNoti['codSilais'] = $('#codSilaisAtencion').find('option:selected').val();
                objetoNoti['codUnidadSalud'] = $('#codUnidadAtencion').find('option:selected').val();
                objetoNoti['codTipoNoti'] = $('#codTipoNotiRedef').find('option:selected').val();
                objetoNoti['fechaInicioSintomas'] = $("#fechaInicioSintomas").val();
                $.ajax({
                    url: parametros.updateUrl,
                    type: 'POST',
                    dataType: 'json',
                    data: JSON.stringify(objetoNoti),
                    contentType: 'application/json',
                    mimeType: 'application/json',
                    success: function (data) {
                        unBlockUI();
                        if (data.mensaje.length > 0) {
                            $.smallBox({
                                title: data.mensaje,
                                content: $("#smallBox_content").val(),
                                color: "#C46A69",
                                iconSmall: "fa fa-warning",
                                timeout: 4000
                            });
                        } else {
                            hideModalRedefine();
                            var msg = $("#msg_update_success").val();
                            $.smallBox({
                                title: msg,
                                content: $("#smallBox_content").val(),
                                color: "#739E73",
                                iconSmall: "fa fa-success",
                                timeout: 4000
                            });
                            getNotifications(false);
                        }

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