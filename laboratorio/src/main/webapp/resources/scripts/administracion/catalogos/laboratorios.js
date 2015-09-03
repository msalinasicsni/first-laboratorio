var Laboratorio = function () {

    var bloquearUI = function(mensaje){
        var loc = window.location;
        var pathName = loc.pathname.substring(0,loc.pathname.indexOf('/', 1)+1);
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

    var desbloquearUI = function() {
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
            var table1 = $('#laboratorio-list').dataTable({
                "sDom": "<'dt-toolbar'<'col-xs-12 col-sm-6'f><'col-sm-6 col-xs-12 hidden-xs'l>r>" +
                    "t" +
                    "<'dt-toolbar-footer'<'col-sm-6 col-xs-12 hidden-xs'i><'col-xs-12 col-sm-6'p>>",
                "autoWidth": true,
                "columns": [
                    null,null,null,null,
                    {
                        "className":      'listSILAIS',
                        "orderable":      false
                    },
                    {
                        "className":      'editValue',
                        "orderable":      false
                    },
                    {
                        "className":      'overrideValue',
                        "orderable":      false
                    }
                ],
                "preDrawCallback": function () {
                    // Initialize the responsive datatables helper once.
                    if (!responsiveHelper_dt_basic) {
                        responsiveHelper_dt_basic = new ResponsiveDatatablesHelper($('#laboratorio-list'), breakpointDefinition);
                    }
                },
                "rowCallback": function (nRow) {
                    responsiveHelper_dt_basic.createExpandIcon(nRow);
                },
                "drawCallback": function (oSettings) {
                    responsiveHelper_dt_basic.respond();
                },
                fnDrawCallback : function() {
                    $('.overrideValue')
                        .off("click", overrideHandler)
                        .on("click", overrideHandler);
                    $('.editValue')
                        .off("click", editHandler)
                        .on("click", editHandler);
                    $('.listSILAIS')
                        .off("click", listSILAISHandler)
                        .on("click", listSILAISHandler);
                }
            });

            var table2 = $('#SILAIS-list').dataTable({
                "sDom": "<'dt-toolbar'<'col-xs-12 col-sm-6'f><'col-sm-6 col-xs-12 hidden-xs'l>r>" +
                    "t" +
                    "<'dt-toolbar-footer'<'col-sm-6 col-xs-12 hidden-xs'i><'col-xs-12 col-sm-6'p>>",
                "autoWidth": true,
                "columns": [
                    null,
                    {
                        "className":      'overrideSILAISLab',
                        "orderable":      false
                    }
                ],
                "preDrawCallback": function () {
                    // Initialize the responsive datatables helper once.
                    if (!responsiveHelper_dt_basic) {
                        responsiveHelper_dt_basic = new ResponsiveDatatablesHelper($('#SILAIS-list'), breakpointDefinition);
                    }
                },
                "rowCallback": function (nRow) {
                    responsiveHelper_dt_basic.createExpandIcon(nRow);
                },
                "drawCallback": function (oSettings) {
                    responsiveHelper_dt_basic.respond();
                },
                fnDrawCallback : function() {
                    $('.overrideSILAISLab')
                        .off("click", overrideSIALISHandler)
                        .on("click", overrideSIALISHandler);
                }
            });

            loadLaboratorios();
            /****************************************************/
            /******************GUARDAR LABORATORIO Y ASOCIAR SILAIS*********************/
            /****************************************************/

            var $validator3 = $("#laboratorio-form").validate({
                rules: {
                    nombre : {
                        required : true
                    },
                    codigo : {
                        required : true
                    }
                },
                errorPlacement: function (error, element) {
                    error.insertAfter(element.parent());

                },
                submitHandler: function (form) {
                    //add here some ajax code to submit your form or just call form.submit() if you want to submit the form without ajax
                    saveLaboratorio();
                }
            });

            var $validator1 = $("#SILAIS-form").validate({
                rules: {
                    idSILAIS : {
                        required : true
                    }
                },
                errorPlacement: function (error, element) {
                    error.insertAfter(element.parent());

                },
                submitHandler: function (form) {
                    //add here some ajax code to submit your form or just call form.submit() if you want to submit the form without ajax
                    saveSILAISLaboratorio();
                }
            });

            function showModalLaboratorio(){
                $("#modalLab").modal({
                    show: true
                });
            }

            function showModalSILAIS(){
                $("#modalSILAIS").modal({
                    show: true
                });
            }

            $("#btnAddLab").click(function(){
                $("#codigo").val("");
                $("#nombre").val("");
                $("#descripcion").val("");
                $("#direccion").val("");
                $("#telefono").val("");
                $("#fax").val("");
                $("#edicion").val("no");
                $("#checkbox-enable").prop('checked',true);
                $("#codigo").prop('disabled',false);
                showModalLaboratorio();
            });

            function saveLaboratorio() {
                var valueObj = {};
                valueObj['mensaje'] = '';
                valueObj['nombre'] = $('#nombre').val();
                valueObj['habilitado']= ($('#checkbox-enable').is(':checked'));
                valueObj['codigo'] = $('#codigo').val();
                valueObj['descripcion'] = $('#descripcion').val();
                valueObj['direccion'] = $('#direccion').val();
                valueObj['telefono'] = $('#telefono').val();
                valueObj['fax'] = $('#fax').val();
                valueObj['edicion'] = $("#edicion").val();
                bloquearUI(parametros.blockMess);
                $.ajax(
                    {
                        url: parametros.saveUrl,
                        type: 'POST',
                        dataType: 'json',
                        data: JSON.stringify(valueObj),
                        contentType: 'application/json',
                        mimeType: 'application/json',
                        success: function (data) {
                            if (data.mensaje.length > 0){
                                $.smallBox({
                                    title: data.mensaje ,
                                    content: $("#disappear").val(),
                                    color: "#C46A69",
                                    iconSmall: "fa fa-warning",
                                    timeout: 3000
                                });
                            }else{
                                var msg = $("#msgSave").val();
                                $.smallBox({
                                    title: msg ,
                                    content: $("#disappear").val(),
                                    color: "#739E73",
                                    iconSmall: "fa fa-success",
                                    timeout: 3000
                                });
                                loadLaboratorios();
                            }
                            desbloquearUI();
                        },
                        error: function (jqXHR) {
                            desbloquearUI();
                            validateLogin(jqXHR);
                        }
                    });
            }

            function loadLaboratorios(){
                $.getJSON(parametros.laboratoriosUrl, {
                    ajax: 'false'
                }, function (data) {
                    var len = data.length;
                    table1.fnClearTable();
                    for (var i = 0; i < len; i++) {
                        var btnOverride = ' <button type="button" class="btn btn-default btn-xs btn-danger" data-id='+data[i].codigo+' ' +
                            '> <i class="fa fa-times"></i>';
                        var btnEditar = ' <button type="button" class="btn btn-default btn-xs btn-primary" data-id='+data[i].codigo+' ' +
                            '> <i class="fa fa-edit"></i>';
                        var btnSILAIS = ' <button type="button" class="btn btn-default btn-xs btn-primary" data-id='+data[i].codigo+' ' +
                            '> <i class="fa fa-list"></i>';
                        var pasivo = '<span class="label label-success"><i class="fa fa-thumbs-up fa-lg"></i></span>';
                        if (data[i].pasivo==true){
                            pasivo = '<span class="label label-danger"><i class="fa fa-thumbs-down fa-lg"></i></span>';
                            btnOverride = ' <button type="button" disabled class="btn btn-default btn-xs btn-danger" data-id='+data[i].codigo+' ' +
                                '> <i class="fa fa-times"></i>';
                        }

                        table1.fnAddData(
                            [data[i].codigo, data[i].nombre, data[i].descripcion, pasivo, btnSILAIS, btnEditar, btnOverride ]);
                    }

                }).fail(function(jqXHR) {
                    desbloquearUI();
                    validateLogin(jqXHR);
                });
            }

            function loadLaboratorio(codigo){
                $.getJSON(parametros.laboratorioUrl, {
                    codigo: codigo,
                    ajax: 'false'
                }, function (data) {
                    $("#edicion").val("si");
                    $("#codigo").val(data.codigo);
                    $("#nombre").val(data.nombre);
                    $("#descripcion").val(data.descripcion);
                    $("#direccion").val(data.direccion);
                    $("#telefono").val(data.telefono);
                    $("#fax").val(data.telefax);
                    $("#codigo").prop('disabled',true);
                    $("#checkbox-enable").prop('checked',!data.pasivo);
                    showModalLaboratorio();
                }).fail(function(jqXHR) {
                    desbloquearUI();
                    validateLogin(jqXHR);
                });
            }

            function editHandler(){
                var codigo = $(this.innerHTML).data('id');
                if (codigo != null) {
                    loadLaboratorio(codigo);
                }
            }

            function overrideHandler(){
                var codigo = $(this.innerHTML).data('id');
                if (codigo != null) {
                    var disabled = this.innerHTML;
                    var n2 = (disabled.indexOf("disabled") > -1);
                    if (!n2) {
                        var opcSi = $("#confirm_msg_opc_yes").val();
                        var opcNo = $("#confirm_msg_opc_no").val();
                        $.SmartMessageBox({
                            title: $("#msgConfirmTitle").val(),
                            content: $("#msgConfirmOverride").val(),
                            buttons: '[' + opcSi + '][' + opcNo + ']'
                        }, function (ButtonPressed) {
                            if (ButtonPressed === opcSi) {
                                overrideLaboratorio(codigo);
                            }
                            if (ButtonPressed === opcNo) {
                                $.smallBox({
                                    title: $("#msgOverrideCanceled").val(),
                                    content: "<i class='fa fa-clock-o'></i> <i>" + $("#disappear").val() + "</i>",
                                    color: "#3276B1",
                                    iconSmall: "fa fa-times fa-2x fadeInRight animated",
                                    timeout: 3000
                                });
                            }
                        })
                    }
                }
            }

            function overrideLaboratorio(codigo) {
                var valueObj = {};
                valueObj['mensaje'] = '';
                valueObj['codigo'] = codigo;
                bloquearUI(parametros.blockMess);
                $.ajax(
                    {
                        url: parametros.overrideUrl,
                        type: 'POST',
                        dataType: 'json',
                        data: JSON.stringify(valueObj),
                        contentType: 'application/json',
                        mimeType: 'application/json',
                        success: function (data) {
                            if (data.mensaje.length > 0){
                                $.smallBox({
                                    title: data.mensaje ,
                                    content: $("#disappear").val(),
                                    color: "#C46A69",
                                    iconSmall: "fa fa-warning",
                                    timeout: 3000
                                });
                            }else{
                                $.smallBox({
                                    title: $("#msgOverride").val(),
                                    content: $("#disappear").val(),
                                    color: "#739E73",
                                    iconSmall: "fa fa-success",
                                    timeout: 3000
                                });
                                loadLaboratorios();
                            }
                            desbloquearUI();
                        },
                        error: function (jqXHR) {
                            desbloquearUI();
                            validateLogin(jqXHR);
                        }
                    });
            }

            function listSILAISHandler(){
                var codigo = $(this.innerHTML).data('id');
                if (codigo != null) {
                    $("#codigoLab").val(codigo);
                    getSILAISDisponibles();
                    getSILAISAsociados();
                    showModalSILAIS();
                }else{
                    $("#codigoLab").val('');
                }
            }

            function overrideSIALISHandler(){
                var idEntidadLab = $(this.innerHTML).data('id');
                if (idEntidadLab != null) {
                    var disabled = this.innerHTML;
                    var n2 = (disabled.indexOf("disabled") > -1);
                    if (!n2) {
                        var opcSi = $("#confirm_msg_opc_yes").val();
                        var opcNo = $("#confirm_msg_opc_no").val();
                        $.SmartMessageBox({
                            title: $("#msgConfirmTitle").val(),
                            content: $("#msgConfirmOverride").val(),
                            buttons: '[' + opcSi + '][' + opcNo + ']'
                        }, function (ButtonPressed) {
                            if (ButtonPressed === opcSi) {
                                overrideSILAISLaboratorio(idEntidadLab);
                            }
                            if (ButtonPressed === opcNo) {
                                $.smallBox({
                                    title: $("#msgOverrideCanceled").val(),
                                    content: "<i class='fa fa-clock-o'></i> <i>" + $("#disappear").val() + "</i>",
                                    color: "#3276B1",
                                    iconSmall: "fa fa-times fa-2x fadeInRight animated",
                                    timeout: 3000
                                });
                            }
                        })
                    }
                }
            }

            function overrideSILAISLaboratorio(idEntidadLab) {
                var valueObj = {};
                valueObj['mensaje'] = '';
                valueObj['idEntidadLab'] = idEntidadLab;
                bloquearUI(parametros.blockMess);
                $.ajax(
                    {
                        url: parametros.overrideSILAISUrl,
                        type: 'POST',
                        dataType: 'json',
                        data: JSON.stringify(valueObj),
                        contentType: 'application/json',
                        mimeType: 'application/json',
                        success: function (data) {
                            if (data.mensaje.length > 0){
                                $.smallBox({
                                    title: data.mensaje ,
                                    content: $("#disappear").val(),
                                    color: "#C46A69",
                                    iconSmall: "fa fa-warning",
                                    timeout: 3000
                                });
                            }else{
                                $.smallBox({
                                    title: $("#msgOverrideS").val(),
                                    content: $("#disappear").val(),
                                    color: "#739E73",
                                    iconSmall: "fa fa-success",
                                    timeout: 3000
                                });
                                getSILAISAsociados();
                                getSILAISDisponibles();
                            }
                            desbloquearUI();
                        },
                        error: function (jqXHR) {
                            desbloquearUI();
                            validateLogin(jqXHR);
                        }
                    });
            }

            function getSILAISAsociados(){
                $.getJSON(parametros.SILAISUrl, {
                    codigo: $("#codigoLab").val(),
                    ajax: 'false'
                }, function (data) {
                    var len = data.length;
                    table2.fnClearTable();
                    for (var i = 0; i < len; i++) {
                        var btnOverride = ' <button type="button" class="btn btn-default btn-xs btn-danger" data-id='+data[i].idEntidadAdtvaLab+' ' +
                            '> <i class="fa fa-times"></i>';

                        table2.fnAddData(
                            [data[i].entidadAdtva.nombre, btnOverride ]);
                    }
                }).fail(function(jqXHR) {
                    desbloquearUI();
                    validateLogin(jqXHR);
                });
            }

            function getSILAISDisponibles(){
                $.getJSON(parametros.SILAISDisponiblesUrl, {
                    codigo: $("#codigoLab").val(),
                    ajax: 'false'
                }, function (data) {
                    var html = null;
                    var len = data.length;
                    html += '<option value="">' + $("#text_opt_select").val() + '...</option>';
                    for (var i = 0; i < len; i++) {
                        html += '<option value="' + data[i].codigo + '">'
                            + data[i].nombre
                            + '</option>';
                    }
                    $('#idSILAIS').html(html).val("").change();
                }).fail(function(jqXHR) {
                    desbloquearUI();
                    validateLogin(jqXHR);
                });
            }

            function saveSILAISLaboratorio() {
                var valueObj = {};
                valueObj['mensaje'] = '';
                valueObj['codigo'] = $('#codigoLab').val();
                valueObj['codigoSILAIS'] = $('#idSILAIS').find('option:selected').val();
                bloquearUI(parametros.blockMess);
                $.ajax(
                    {
                        url: parametros.saveSILAISUrl,
                        type: 'POST',
                        dataType: 'json',
                        data: JSON.stringify(valueObj),
                        contentType: 'application/json',
                        mimeType: 'application/json',
                        success: function (data) {
                            if (data.mensaje.length > 0){
                                $.smallBox({
                                    title: data.mensaje ,
                                    content: $("#disappear").val(),
                                    color: "#C46A69",
                                    iconSmall: "fa fa-warning",
                                    timeout: 3000
                                });
                            }else{
                                var msg = $("#msgSaveS").val();
                                $.smallBox({
                                    title: msg ,
                                    content: $("#disappear").val(),
                                    color: "#739E73",
                                    iconSmall: "fa fa-success",
                                    timeout: 3000
                                });
                                getSILAISAsociados();
                                getSILAISDisponibles();
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