var PreRegistro = function () {
    return {
        //main function to initiate the module
        init: function (parametros) {
            var responsiveHelper_dt_basic = undefined;
            var breakpointDefinition = {
                tablet: 1024,
                phone: 480
            };
            $('.from_date').datepicker({
                language: parametros.idioma,
                format:'yyyy-mm-dd',
                autoclose: true
            })
                .on('changeDate', function (selected) {
                    //console.log('change fecha inicio');
                    startDate = new Date(selected.date.valueOf());
                    startDate.setDate(startDate.getDate(new Date(selected.date.valueOf())));
                    //si la fecha de inicio es mayor a la fecha de fin, entoces la fecha de fin se establece igual a la fecha de inicio
                    if (selected.date.getFullYear()>999) {
                        startDateCompare = new Date(selected.date.getFullYear(), selected.date.getMonth(), selected.date.getDate(), 0, 0, 0);
                        endDateCompare = $('.to_date').datepicker("getDate");
                        if (endDateCompare != null && endDateCompare != 'undefined') {
                            if (startDateCompare > endDateCompare) {
                                //console.log('fec inicio es mayor - from_date');
                                $('.to_date').datepicker('update', startDateCompare);
                            }
                        }
                    }
                    //se actualiza la fecha en que inicia el datetimepicker
                    $('.to_date').datepicker('setStartDate', startDate);
                });

            //Fecha Fin Rango
            $('.to_date')
                .datepicker({
                    language: parametros.idioma,
                    format:'yyyy-mm-dd',
                    autoclose: true
                })
                .on('changeDate', function (selected) {
                    //console.log('change fecha fin');
                    FromEndDate = new Date(selected.date.valueOf());
                    FromEndDate.setDate(FromEndDate.getDate(new Date(selected.date.valueOf())));
                    //si la fecha de inicio es mayor a la fecha de fin, entoces la fecha de inicio se establece igual a la fecha de fin
                    if (selected.date.getFullYear()>999) {
                        endDateCompare = new Date(selected.date.getFullYear(), selected.date.getMonth(), selected.date.getDate(), 0, 0, 0);
                        startDateCompare = $('.from_date').datepicker("getDate");
                        if (endDateCompare != null && endDateCompare != 'undefined') {
                            if (startDateCompare > endDateCompare) {
                                //console.log('fec fin es menor - to_date');
                                $('.from_date').datepicker('update', endDateCompare);
                            }
                        }
                    }
                    //se actualiza la fecha en que inicia el datetimepicker
                    $('.from_date').datepicker('setEndDate', FromEndDate);
                });

            var table1 = $('#preregistros-results').dataTable({
                "sDom": "<'dt-toolbar'<'col-xs-12 col-sm-6'f><'col-sm-6 col-xs-12 hidden-xs'l>r>" +
                    "t" +
                    "<'dt-toolbar-footer'<'col-sm-6 col-xs-12 hidden-xs'i><'col-xs-12 col-sm-6'p>>",
                "autoWidth": true,
                "columns": [
                    null, null, null, null, null, null,
                    {
                        "className": 'confirmar',
                        "orderable": false
                    }
                ],
                "preDrawCallback": function () {
                    // Initialize the responsive datatables helper once.
                    if (!responsiveHelper_dt_basic) {
                        responsiveHelper_dt_basic = new ResponsiveDatatablesHelper($('#preregistros-results'), breakpointDefinition);
                    }
                },
                "rowCallback": function (nRow) {
                    responsiveHelper_dt_basic.createExpandIcon(nRow);
                },
                "drawCallback": function (oSettings) {
                    responsiveHelper_dt_basic.respond();
                },
                fnDrawCallback: function () {
                    $('.confirmar')
                        .off("click", confirmHandler)
                        .on("click", confirmHandler);
                }
            });

            <!-- formulario de búsqueda de mx -->
            $('#search-form').validate({
                // Rules for form validation
                rules: {
                    identificacion : {required: function () {
                        return $('#fechaInicio').val().length == 0 && $('#fechaFin').val().length == 0;
                    }},
                    fechaFin: {required: function () {
                        return $('#fechaInicio').val().length > 0;
                    }},
                    fechaInicio: {required: function () {
                        return $('#fechaFin').val().length > 0;
                    }}
                },
                // Do not change code below
                errorPlacement: function (error, element) {
                    error.insertAfter(element.parent());
                },
                submitHandler: function (form) {
                    table1.fnClearTable();
                    //add here some ajax code to submit your form or just call form.submit() if you want to submit the form without ajax
                    getPreRegistros(false)
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

            function confirmHandler(){
                var id = $(this.innerHTML).data('id');
                if (id != null) {

                    var opcSi = $("#confirm_msg_opc_yes").val();
                    var opcNo = $("#confirm_msg_opc_no").val();
                    $.SmartMessageBox({
                        title: $("#msg_confirmation").val(),
                        content: $("#msg_confirm_preregistration").val(),
                        buttons: '[' + opcSi + '][' + opcNo + ']'
                    }, function (ButtonPressed) {
                        if (ButtonPressed === opcSi) {
                            confirmPreRegistration(id);
                        }
                        if (ButtonPressed === opcNo) {
                            $.smallBox({
                                title: $("#msg_confirm_preregistration_cancel").val(),
                                content: "<i class='fa fa-clock-o'></i> <i>" + $("#smallBox_content").val() + "</i>",
                                color: "#3276B1",
                                iconSmall: "fa fa-times fa-2x fadeInRight animated",
                                timeout: 3000
                            });
                        }
                    })
                }
            }

            function confirmPreRegistration(id) {
                var valueObj = {};
                valueObj['mensaje'] = '';
                valueObj['idPreregistro'] = id;
                valueObj['idNotificacion'] = '';
                valueObj['factura'] = '';
                valueObj['documentoViaje'] = '';
                blockUI();
                $.ajax(
                    {
                        url: parametros.confirmUrl,
                        type: 'POST',
                        dataType: 'json',
                        data: JSON.stringify(valueObj),
                        contentType: 'application/json',
                        mimeType: 'application/json',
                        success: function (data) {
                            if (data.mensaje.length > 0){
                                $.smallBox({
                                    title: data.mensaje ,
                                    content: $("#smallBox_content").val(),
                                    color: "#C46A69",
                                    iconSmall: "fa fa-warning",
                                    timeout: 3000
                                });
                            }else{
                                $.smallBox({
                                    title: $("#msg_preregistration_successfully_confirmed").val(),
                                    content: $("#smallBox_content").val(),
                                    color: "#739E73",
                                    iconSmall: "fa fa-success",
                                    timeout: 3000
                                });
                                setTimeout(function () {
                                    window.location.href = parametros.sNotificiacionUrl+data.idNotificacion+"?p1="+data.factura+"&p2="+data.documentoViaje;
                                }, 3000);
                            }
                            unBlockUI()
                        },
                        error: function (jqXHR) {
                            unBlockUI();
                            validateLogin(jqXHR);
                        }
                    });
            }

            function getPreRegistros() {
                var filtros = {};
                filtros['identificacion'] = $('#identificacion').val();
                filtros['fechainicial'] = $('#fechaInicio').val();
                filtros['fechafinal'] = $('#fechaFin').val();
                blockUI();
                $.getJSON(parametros.preRegistroUrl, {
                    strFilter: JSON.stringify(filtros),
                    ajax: 'true'
                }, function (dataToLoad) {
                    table1.fnClearTable();
                    console.log(dataToLoad);
                    var len = Object.keys(dataToLoad).length;
                    if (len > 0) {
                        for (var i = 0; i < len; i++) {
                            var btnConfirmar = '<button title="Confirmar preregistro" type="button" class="btn btn-success btn-xs" data-id="' + dataToLoad[i].id + '"> <i class="fa fa-check-square"></i></button>';
                            //   var actionUrl = parametros.sActionUrl+idLoad;
                            //'<a href='+ actionUrl + ' class="btn btn-default btn-xs"><i class="fa fa-mail-forward"></i></a>'
                            table1.fnAddData(
                                [dataToLoad[i].id, dataToLoad[i].fecharegistro, dataToLoad[i].persona.identificacion.numeroIdentificacion, dataToLoad[i].documentoviaje.numerodocumento, dataToLoad[i].persona.nombrecompleto, dataToLoad[i].detallepago.referencia, btnConfirmar ]);

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
        }
    };

}();

