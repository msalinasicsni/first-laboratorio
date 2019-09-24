var Equipment = function () {

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
            var table1 = $('#equiposList').dataTable({
                "sDom": "<'dt-toolbar'<'col-xs-12 col-sm-6'f><'col-sm-6 col-xs-12 hidden-xs'l>r>" +
                    "t" +
                    "<'dt-toolbar-footer'<'col-sm-6 col-xs-12 hidden-xs'i><'col-xs-12 col-sm-6'p>>",
                "autoWidth": true,
                "columns": [
                    null,null,null,null,null,
                    {
                        "className":      'detail',
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
                        responsiveHelper_dt_basic = new ResponsiveDatatablesHelper($('#equiposList'), breakpointDefinition);
                    }
                },
                "rowCallback": function (nRow) {
                    responsiveHelper_dt_basic.createExpandIcon(nRow);
                },
                "drawCallback": function (oSettings) {
                    responsiveHelper_dt_basic.respond();
                },
                fnDrawCallback : function() {
                    $('.detail')
                        .off("click", detailHandler)
                        .on("click", detailHandler);
                    $('.overrideValue')
                        .off("click", overrideHandler)
                        .on("click", overrideHandler);
                    $('.editValue')
                        .off("click", editHandler)
                        .on("click", editHandler);
                }
            });

            var table2 = $('#testsList').dataTable({
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
                        responsiveHelper_dt_basic = new ResponsiveDatatablesHelper($('#testsList'), breakpointDefinition);
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
                        .off("click", overrideTestHandler)
                        .on("click", overrideTestHandler);
                }
            });

            getAllEquipments();
            /****************************************************/
            /******************GUARDAR EQUIPO*********************/
            /****************************************************/

            var $validator1 = $("#enterform").validate({
                rules: {
                    nombre : {
                        required : true
                    }
                },
                errorPlacement: function (error, element) {
                    error.insertAfter(element.parent());

                },
                submitHandler: function (form) {
                    //add here some ajax code to submit your form or just call form.submit() if you want to submit the form without ajax
                    save();
                }
            });

            var $validator2 = $("#testForm").validate({
                rules: {
                    idExamen : {
                        required : true
                    }
                },
                errorPlacement: function (error, element) {
                    error.insertAfter(element.parent());

                },
                submitHandler: function (form) {
                    //add here some ajax code to submit your form or just call form.submit() if you want to submit the form without ajax
                    saveTest();
                }
            });

            function showModalEquipo(){
                $("#modalEquipo").modal({
                    show: true
                });
            }

            function showModalExamenes(){
                $("#modalExamenes").modal({
                    show: true
                });
            }

            function detailHandler() {
                var data = $(this.innerHTML).data('id');
                if (data != null) {
                    var detalle = data.split(",");
                    var id = detalle[0];
                    var name = detalle[1];
                    $("#idEquipoDet").val(id);
                    $('#equipmentName').html($('#equipo').val() + " " + name);
                    getExamenesDisponibles();
                    getExamenesAsociados();
                    showModalExamenes();
                }else{
                    $("#idEquipoDet").val('');
                    $('#equipmentName').html('');
                }
            }

            function editHandler(){
                var idEquipo = $(this.innerHTML).data('id');
                if (idEquipo != null) {
                    getEquipo(idEquipo);
                }
            }

            function overrideHandler(){
                var idEquipo = $(this.innerHTML).data('id');
                if (idEquipo != null) {
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
                                override(idEquipo);
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

            function overrideTestHandler(){
                var idExamenEquipo = $(this.innerHTML).data('id');
                if (idExamenEquipo != null) {
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
                                overrideTest(idExamenEquipo);
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

            $("#btnAdd").click(function(){
                $("#idEquipo").val('');
                $("#nombre").val('');
                $("#marca").val('');
                $("#modelo").val('');
                $("#descripcion").val('');
                $("#checkbox-enable").prop('checked',true);
                showModalEquipo();
            });

            function getAllEquipments(){
                $.getJSON(parametros.equiposUrl, {
                    ajax: 'false'
                }, function (data) {
                    var len = data.length;
                    table1.fnClearTable();
                    for (var i = 0; i < len; i++) {
                        var btnAdd = '<button type="button" title="Examenes procesa" class="btn btn-primary btn-xs" data-id="' + data[i].idEquipo + "," + data[i].nombre +
                            '" > <i class="fa fa-list"></i>';
                        var btnOverride = ' <button type="button" title="Anular" class="btn btn-default btn-xs btn-danger" data-id='+data[i].idEquipo+' ' +
                            '> <i class="fa fa-times"></i>';
                        var btnEditar = ' <button type="button" title="Editar" class="btn btn-default btn-xs btn-primary" data-id='+data[i].idEquipo+' ' +
                            '> <i class="fa fa-edit"></i>';
                        var pasivo = '<span class="label label-success"><i class="fa fa-thumbs-up fa-lg"></i></span>';
                        if (data[i].pasivo==true){
                            pasivo = '<span class="label label-danger"><i class="fa fa-thumbs-down fa-lg"></i></span>';
                            btnOverride = ' <button type="button" title="Anular" disabled class="btn btn-default btn-xs btn-danger" data-id='+data[i].idEquipo+' ' +
                                '> <i class="fa fa-times"></i>';
                        }

                        table1.fnAddData(
                            [data[i].nombre, data[i].marca, data[i].modelo, data[i].descripcion, pasivo, btnAdd, btnEditar, btnOverride ]);
                    }

                }).fail(function(jqXHR) {
                    desbloquearUI();
                    validateLogin(jqXHR);
                });
            }

            function getEquipo(idEquipo){
                $.getJSON(parametros.equipoUrl, {
                    idEquipo: idEquipo,
                    ajax: 'false'
                }, function (data) {
                    $("#idEquipo").val(data.idEquipo);
                    $("#nombre").val(data.nombre);
                    $("#marca").val(data.marca);
                    $("#modelo").val(data.modelo);
                    $("#descripcion").val(data.descripcion);
                    $("#checkbox-enable").prop('checked',!data.pasivo);
                    showModalEquipo();
                }).fail(function(jqXHR) {
                    desbloquearUI();
                    validateLogin(jqXHR);
                });
            }

            function getExamenesAsociados(){
                $.getJSON(parametros.examenesUrl, {
                    idEquipo: $("#idEquipoDet").val(),
                    ajax: 'false'
                }, function (data) {
                    var len = data.length;
                    table2.fnClearTable();
                    for (var i = 0; i < len; i++) {
                        var btnOverride = ' <button type="button" title="Anular asociación" class="btn btn-default btn-xs btn-danger" data-id='+data[i].idExamenEquipo+' ' +
                            '> <i class="fa fa-times"></i>';

                        table2.fnAddData(
                            [data[i].examen.nombre, btnOverride ]);
                    }
                }).fail(function(jqXHR) {
                    desbloquearUI();
                    validateLogin(jqXHR);
                });
            }

            function getExamenesDisponibles(){
                $.getJSON(parametros.examenesDispUrl, {
                    idEquipo: $("#idEquipoDet").val(),
                    ajax: 'false'
                }, function (data) {
                    var html = null;
                    var len = data.length;
                    html += '<option value="">' + $("#text_opt_select").val() + '...</option>';
                    for (var i = 0; i < len; i++) {
                        html += '<option value="' + data[i].idExamen + '">'
                            + data[i].nombre
                            + '</option>';
                    }
                    $('#idExamen').html(html).val("").change();
                }).fail(function(jqXHR) {
                    desbloquearUI();
                    validateLogin(jqXHR);
                });
            }

            function save() {
                var valueObj = {};
                valueObj['mensaje'] = '';
                valueObj['nombre'] = $('#nombre').val();
                valueObj['marca'] = $('#marca').val();
                valueObj['modelo'] = $('#modelo').val();
                valueObj['descripcion'] = $('#descripcion').val();
                valueObj['habilitado']= ($('#checkbox-enable').is(':checked'));
                valueObj['idEquipo'] = $('#idEquipo').val();
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
                                getAllEquipments();
                            }
                            desbloquearUI();
                        },
                        error: function (jqXHR) {
                            desbloquearUI();
                            validateLogin(jqXHR);
                        }
                    });
            }

            function saveTest() {
                var valueObj = {};
                valueObj['mensaje'] = '';
                valueObj['idEquipo'] = $('#idEquipoDet').val();
                valueObj['idExamen'] = $('#idExamen').find('option:selected').val();
                bloquearUI(parametros.blockMess);
                $.ajax(
                    {
                        url: parametros.saveTestUrl,
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
                                var msg = $("#msgSaveTest").val();
                                $.smallBox({
                                    title: msg ,
                                    content: $("#disappear").val(),
                                    color: "#739E73",
                                    iconSmall: "fa fa-success",
                                    timeout: 3000
                                });
                                getExamenesDisponibles();
                                getExamenesAsociados();
                            }
                            desbloquearUI();
                        },
                        error: function (jqXHR) {
                            desbloquearUI();
                            validateLogin(jqXHR);
                        }
                    });
            }

            function override(idEquipo) {
                var valueObj = {};
                valueObj['mensaje'] = '';
                valueObj['idEquipo'] = idEquipo;
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
                                getAllEquipments();
                            }
                            desbloquearUI();
                        },
                        error: function (jqXHR) {
                            desbloquearUI();
                            validateLogin(jqXHR);
                        }
                    });
            }

            function overrideTest(idExamenEquipo) {
                var valueObj = {};
                valueObj['mensaje'] = '';
                valueObj['idExamenEquipo'] = idExamenEquipo;
                bloquearUI(parametros.blockMess);
                $.ajax(
                    {
                        url: parametros.overrideTestUrl,
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
                                    title: $("#msgOverrideTest").val(),
                                    content: $("#disappear").val(),
                                    color: "#739E73",
                                    iconSmall: "fa fa-success",
                                    timeout: 3000
                                });
                                getExamenesDisponibles();
                                getExamenesAsociados();
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