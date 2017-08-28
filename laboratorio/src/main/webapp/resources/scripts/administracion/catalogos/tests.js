var Tests = function () {

    function replaceAll( text, busca, reemplaza ){
        while (text.toString().indexOf(busca) != -1)
        text = text.toString().replace(busca,reemplaza);
        return text;
    }

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
            var table1 = $('#test-list').dataTable({
                "sDom": "<'dt-toolbar'<'col-xs-12 col-sm-6'f><'col-sm-6 col-xs-12 hidden-xs'l>r>" +
                    "t" +
                    "<'dt-toolbar-footer'<'col-sm-6 col-xs-12 hidden-xs'i><'col-xs-12 col-sm-6'p>>",
                "autoWidth": true,
                "columns": [
                    null,null,null,null,
                    {
                        "className":      'rules',
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
                        responsiveHelper_dt_basic = new ResponsiveDatatablesHelper($('#test-list'), breakpointDefinition);
                    }
                },
                "rowCallback": function (nRow) {
                    responsiveHelper_dt_basic.createExpandIcon(nRow);
                },
                "drawCallback": function (oSettings) {
                    responsiveHelper_dt_basic.respond();
                },
                fnDrawCallback : function() {
                    $('.rules')
                        .off("click", rulesHandler)
                        .on("click", rulesHandler);
                    $('.overrideValue')
                        .off("click", overrideHandler)
                        .on("click", overrideHandler);
                    $('.editValue')
                        .off("click", editHandler)
                        .on("click", editHandler);
                }
            });

            /****************************************************/
            /******************AGREGAR EXAMEN*********************/
            /****************************************************/

            var $validator3 = $("#add-test-form").validate({
                rules: {
                    nombre : {
                        required : true
                    },
                    area : {
                        required : true}
                },

                errorPlacement: function (error, element) {
                    error.insertAfter(element.parent());

                },
                submitHandler: function (form) {
                    //add here some ajax code to submit your form or just call form.submit() if you want to submit the form without ajax
                    addTest();
                }
            });

            loadTests();

            function showModalTest(){
                $("#modalTest").modal({
                    show: true
                });
            }

            $("#btnAddTest").click(function(){
                $("#idExamen").val('');
                $("#nombre").val('');
                $("#precio").val('');
                $("#checkbox-enable").prop('checked',true);
                $("#area").val('').change();
                showModalTest();
            });

            function loadTests(){
                bloquearUI(parametros.blockMess);
                $.getJSON(parametros.examenesUrl, {
                    ajax: 'false'
                }, function (data) {
                    var len = data.length;
                    table1.fnClearTable();
                    for (var i = 0; i < len; i++) {
                        var btnOverride = ' <button type="button" title="Anular" class="btn btn-default btn-xs btn-danger" data-id='+data[i].idExamen+'> <i class="fa fa-times"></i>';
                        var btnEditar = ' <button type="button" title="Editar" class="btn btn-default btn-xs btn-primary" data-id='+data[i].idExamen+'> <i class="fa fa-edit"></i>';
                        var nombreFormat = replaceAll(data[i].nombre,' ','*');
                        var btnRules = ' <button type="button" title="Ver reglas sugeridas" class="btn btn-default btn-xs btn-primary" data-id='+data[i].idExamen+','+nombreFormat+'> <i class="fa fa-ban"></i>';
                        var pasivo = '<span class="label label-success"><i class="fa fa-thumbs-up fa-lg"></i></span>';
                        if (data[i].pasivo==true){
                            pasivo = '<span class="label label-danger"><i class="fa fa-thumbs-down fa-lg"></i></span>';
                            btnOverride = ' <button type="button" disabled class="btn btn-default btn-xs btn-danger" data-id='+data[i].idExamen+' ' +
                                '> <i class="fa fa-times"></i>';
                            btnRules = ' <button type="button" disabled title="Ver reglas sugeridas" class="btn btn-default btn-xs btn-primary" data-id='+data[i].idExamen+' ' +
                                '> <i class="fa fa-ban"></i>';
                        }

                        table1.fnAddData(
                            [data[i].nombre, data[i].precio,pasivo, data[i].area.nombre, btnRules, btnEditar, btnOverride ]);
                    }

                }).fail(function(jqXHR) {
                    desbloquearUI();
                    validateLogin(jqXHR);
                });
                desbloquearUI();
            }

            function loadTest(idExamen){
                $.getJSON(parametros.examenUrl, {
                    idExamen: idExamen,
                    ajax: 'false'
                }, function (data) {
                    $("#idExamen").val(data.idExamen);
                    $("#nombre").val(data.nombre);
                    $("#precio").val(data.precio);
                    $("#area").val(data.area.idArea).change();
                    $("#checkbox-enable").prop('checked',!data.pasivo);
                    showModalTest();
                }).fail(function(jqXHR) {
                    desbloquearUI();
                    validateLogin(jqXHR);
                });
            }

            function addTest() {
                var valueObj = {};
                valueObj['mensaje'] = '';
                valueObj['nombre'] = $('#nombre').val();
                valueObj['precio'] = $('#precio').val();
                valueObj['habilitado']= ($('#checkbox-enable').is(':checked'));
                valueObj['idArea'] = $('#area').find('option:selected').val();
                valueObj['idExamen']= $("#idExamen").val();
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
                                var msg = $("#msjSuccessful").val();
                                $.smallBox({
                                    title: msg ,
                                    content: $("#disappear").val(),
                                    color: "#739E73",
                                    iconSmall: "fa fa-success",
                                    timeout: 3000
                                });
                                loadTests();
                            }
                            desbloquearUI();
                        },
                        error: function (jqXHR) {
                            desbloquearUI();
                            validateLogin(jqXHR);
                        }
                    });
            }

            function editHandler(){
                var idExamen = $(this.innerHTML).data('id');
                if (idExamen != null) {
                    loadTest(idExamen);
                }
            }

            function overrideHandler(){
                var idExamen = $(this.innerHTML).data('id');
                if (idExamen != null) {
                    var disabled = this.innerHTML;
                    var n2 = (disabled.indexOf("disabled") > -1);
                    if (!n2) {
                        var opcSi = $("#confirm_msg_opc_yes").val();
                        var opcNo = $("#confirm_msg_opc_no").val();
                        $.SmartMessageBox({
                            title: $("#msjConfirm").val(),
                            content: $("#msjOverride").val(),
                            buttons: '[' + opcSi + '][' + opcNo + ']'
                        }, function (ButtonPressed) {
                            if (ButtonPressed === opcSi) {
                                deshabilitarExamen(idExamen);
                            }
                            if (ButtonPressed === opcNo) {
                                $.smallBox({
                                    title: $("#msjOverrideC").val(),
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

            function deshabilitarExamen(idExamen) {
                var valueObj = {};
                valueObj['mensaje'] = '';
                valueObj['idExamen'] = idExamen;
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
                                var msg = $("#msjSuccessfulOverride").val();
                                $.smallBox({
                                    title: msg ,
                                    content: $("#disappear").val(),
                                    color: "#739E73",
                                    iconSmall: "fa fa-success",
                                    timeout: 3000
                                });
                                loadTests();
                            }
                            desbloquearUI();
                        },
                        error: function (jqXHR) {
                            desbloquearUI();
                            validateLogin(jqXHR);
                        }
                    });
            }

            /***************************************************/
            /***********************RULES***********************/
            /***************************************************/
            var table2 = $('#rules-list').dataTable({
                "sDom": "<'dt-toolbar'<'col-xs-12 col-sm-6'f><'col-sm-6 col-xs-12 hidden-xs'l>r>" +
                    "t" +
                    "<'dt-toolbar-footer'<'col-sm-6 col-xs-12 hidden-xs'i><'col-xs-12 col-sm-6'p>>",
                "autoWidth": true,
                "columns": [
                    null,
                    {
                        "className":      'editRule',
                        "orderable":      false
                    },
                    {
                        "className":      'overrideRule',
                        "orderable":      false
                    }
                ],
               "preDrawCallback": function () {
                    // Initialize the responsive datatables helper once.
                    if (!responsiveHelper_dt_basic) {
                        responsiveHelper_dt_basic = new ResponsiveDatatablesHelper($('#rules-list'), breakpointDefinition);
                    }
                },
                "rowCallback": function (nRow) {
                    responsiveHelper_dt_basic.createExpandIcon(nRow);
                },
                "drawCallback": function (oSettings) {
                    responsiveHelper_dt_basic.respond();
                },
                fnDrawCallback : function() {
                    $('.editRule')
                        .off("click", editRuleHandler)
                        .on("click", editRuleHandler);
                    $('.overrideRule')
                        .off("click", overrideRuleHandler)
                        .on("click", overrideRuleHandler);
                }
            });

            function showModalRules(){
                $("#modalRules").modal({
                    show: true
                });
            }

            function rulesHandler(){
                var data = $(this.innerHTML).data('id');
                console.log(data);
                if (data != null) {
                    var detalle = data.split(",");
                    showModalRules();
                    $("#headerRules").html('<i class="fa-fw fa fa-list"></i>'+parametros.headerModalRules+' <strong>'+replaceAll(detalle[1],'*',' ')+'</strong>');
                    $("#idExamenAsociado").val(detalle[0]);
                    $("#descripcion").val('');
                    loadRules();
                }
            }
            var $validator1 = $("#rules-form").validate({
                rules: {
                    descripcion : {
                        required : true
                    }
                },
                errorPlacement: function (error, element) {
                    error.insertAfter(element.parent());

                },
                submitHandler: function (form) {
                    //add here some ajax code to submit your form or just call form.submit() if you want to submit the form without ajax
                    saveRule();
                }
            });

            function loadRules(){
                bloquearUI(parametros.blockMess);
                $.getJSON(parametros.rulesUrl, {
                    idExamen : $("#idExamenAsociado").val(),
                    ajax: 'true'
                }, function (data) {
                    var len = data.length;
                    table2.fnClearTable();
                    for (var i = 0; i < len; i++) {
                        var btnOverride = ' <button type="button" title="Anular" class="btn btn-default btn-xs btn-danger" data-id='+data[i].idRegla+' ' +
                            '> <i class="fa fa-times"></i>';
                        var btnEditar = ' <button type="button" title="Editar" class="btn btn-default btn-xs btn-primary" data-id='+data[i].idRegla+' ' +
                            '> <i class="fa fa-edit"></i>';

                        table2.fnAddData(
                            [data[i].descripcion, btnEditar, btnOverride ]);
                    }

                }).fail(function(jqXHR) {
                    desbloquearUI();
                    validateLogin(jqXHR);
                });
                desbloquearUI();
            }

            function loadRule(){
                bloquearUI(parametros.blockMess);
                $.getJSON(parametros.ruleUrl, {
                    idExamen : $("#idRegla").val(),
                    ajax: 'true'
                }, function (data) {
                    var len = data.length;
                    $("#descripcion").val(data.descripcion);

                }).fail(function(jqXHR) {
                    desbloquearUI();
                    validateLogin(jqXHR);
                });
                desbloquearUI();
            }

            function saveRule() {
                var valueObj = {};
                valueObj['mensaje'] = '';
                valueObj['descripcion'] = $('#descripcion').val();
                valueObj['idRegla'] = $("#idRegla").val();
                valueObj['idExamen']= $("#idExamenAsociado").val();
                bloquearUI(parametros.blockMess);
                $.ajax(
                    {
                        url: parametros.saveRuleUrl,
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
                                var msg = $("#msjSuccessfulRule").val();
                                $.smallBox({
                                    title: msg ,
                                    content: $("#disappear").val(),
                                    color: "#739E73",
                                    iconSmall: "fa fa-success",
                                    timeout: 3000
                                });
                                $("#descripcion").val('');
                                loadRules();
                            }
                            desbloquearUI();
                        },
                        error: function (jqXHR) {
                            desbloquearUI();
                            validateLogin(jqXHR);
                        }
                    });
            }

            function overrideRuleHandler(){
                var idRegla = $(this.innerHTML).data('id');
                if (idRegla != null) {
                    var disabled = this.innerHTML;
                    var n2 = (disabled.indexOf("disabled") > -1);
                    if (!n2) {
                        var opcSi = $("#confirm_msg_opc_yes").val();
                        var opcNo = $("#confirm_msg_opc_no").val();
                        $.SmartMessageBox({
                            title: $("#msjConfirm").val(),
                            content: $("#msjOverride").val(),
                            buttons: '[' + opcSi + '][' + opcNo + ']'
                        }, function (ButtonPressed) {
                            if (ButtonPressed === opcSi) {
                                deshabilitarRegla(idRegla);
                            }
                            if (ButtonPressed === opcNo) {
                                $.smallBox({
                                    title: $("#msjOverrideC").val(),
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

            function editRuleHandler(){
                var idRegla = $(this.innerHTML).data('id');
                if (idRegla != null) {
                    $("#idRegla").val(idRegla);
                    loadRule();
                }
            }

            function deshabilitarRegla(idRegla) {
                var valueObj = {};
                valueObj['mensaje'] = '';
                valueObj['idRegla'] = idRegla;
                bloquearUI(parametros.blockMess);
                $.ajax(
                    {
                        url: parametros.overrideRuleUrl,
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
                                var msg = $("#msjOverrideRule").val();
                                $.smallBox({
                                    title: msg ,
                                    content: $("#disappear").val(),
                                    color: "#739E73",
                                    iconSmall: "fa fa-success",
                                    timeout: 3000
                                });
                                loadRules();
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