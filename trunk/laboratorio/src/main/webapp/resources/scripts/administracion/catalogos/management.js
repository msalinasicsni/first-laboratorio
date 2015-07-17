var Managament = function () {

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
            var table1 = $('#management-list').dataTable({
                "sDom": "<'dt-toolbar'<'col-xs-12 col-sm-6'f><'col-sm-6 col-xs-12 hidden-xs'l>r>" +
                    "t" +
                    "<'dt-toolbar-footer'<'col-sm-6 col-xs-12 hidden-xs'i><'col-xs-12 col-sm-6'p>>",
                "autoWidth": true,
                "columns": [
                    null,null,
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
                        responsiveHelper_dt_basic = new ResponsiveDatatablesHelper($('#management-list'), breakpointDefinition);
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
                }
            });

            cargarDirecciones();
            /****************************************************/
            /******************GUARDAR DIRECCION*********************/
            /****************************************************/

            var $validator3 = $("#direccion-form").validate({
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
                    saveManagement();
                }
            });

            function showModalDireccion(){
                $("#modalDireccion").modal({
                    show: true
                });
            }

            $("#btnAddDireccion").click(function(){
                $("#idDireccion").val('');
                $("#nombre").val('');
                $("#checkbox-enable").prop('checked',true);
                showModalDireccion();
            });

            function saveManagement() {
                var valueObj = {};
                valueObj['mensaje'] = '';
                valueObj['nombre'] = $('#nombre').val();
                valueObj['habilitado']= ($('#checkbox-enable').is(':checked'));
                valueObj['idDireccion'] = $('#idDireccion').val();
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
                                cargarDirecciones();
                            }
                            desbloquearUI();
                        },
                        error: function (data, status, er) {
                            desbloquearUI();
                            alert("error: " + data + " status: " + status + " er:" + er);
                        }
                    });
            }

            function cargarDirecciones(){
                $.getJSON(parametros.direccionesUrl, {
                    ajax: 'false'
                }, function (data) {
                    var len = data.length;
                    table1.fnClearTable();
                    for (var i = 0; i < len; i++) {
                        var btnOverride = ' <button type="button" class="btn btn-default btn-xs btn-danger" data-id='+data[i].idDireccion+' ' +
                            '> <i class="fa fa-times"></i>';
                        var btnEditar = ' <button type="button" class="btn btn-default btn-xs btn-primary" data-id='+data[i].idDireccion+' ' +
                            '> <i class="fa fa-edit"></i>';
                        var pasivo = '<span class="label label-success"><i class="fa fa-thumbs-up fa-lg"></i></span>';
                        if (data[i].pasivo==true){
                            pasivo = '<span class="label label-danger"><i class="fa fa-thumbs-down fa-lg"></i></span>';
                            btnOverride = ' <button type="button" disabled class="btn btn-default btn-xs btn-danger" data-id='+data[i].idDireccion+' ' +
                                '> <i class="fa fa-times"></i>';
                        }

                        table1.fnAddData(
                            [data[i].nombre, pasivo, btnEditar, btnOverride ]);
                    }

                });
            }

            function cargarDireccion(idDireccion){
                $.getJSON(parametros.direccionUrl, {
                    idDireccion: idDireccion,
                    ajax: 'false'
                }, function (data) {
                    $("#idDireccion").val(data.idDireccion);
                    $("#nombre").val(data.nombre);
                    $("#checkbox-enable").prop('checked',!data.pasivo);
                    showModalDireccion();
                });
            }

            function editHandler(){
                var idDireccion = $(this.innerHTML).data('id');
                if (idDireccion != null) {
                   cargarDireccion(idDireccion);
                }
            }

            function overrideHandler(){
                var idDireccion = $(this.innerHTML).data('id');
                if (idDireccion != null) {
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
                                deshabilitarDireccion(idDireccion);
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

            function deshabilitarDireccion(idDireccion) {
                var valueObj = {};
                valueObj['mensaje'] = '';
                valueObj['idDireccion'] = idDireccion;
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
                                cargarDirecciones();
                            }
                            desbloquearUI();
                        },
                        error: function (data, status, er) {
                            desbloquearUI();
                            alert("error: " + data + " status: " + status + " er:" + er);
                        }
                    });
            }
        }
    };
}();