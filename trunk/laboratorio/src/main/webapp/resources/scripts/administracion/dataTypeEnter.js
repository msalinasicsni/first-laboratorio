/**
 * Created by souyen-ics on 02-03-15.
 */

var DataTypes  = function () {

    return {
        init: function (parametros) {
            getDataTypes();

            function blockUI(){
                var loc = window.location;
                var pathName = loc.pathname.substring(0,loc.pathname.indexOf('/', 1)+1);
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

            var responsiveHelper_dt_basic = undefined;
            var breakpointDefinition = {
                tablet : 1024,
                phone : 480
            };
            var dataTypesTable = $('#datatypes-records').dataTable({
                "sDom": "<'dt-toolbar'<'col-xs-12 col-sm-6'f><'col-sm-6 col-xs-12 hidden-xs'l>r>"+
                    "t"+
                    "<'dt-toolbar-footer'<'col-sm-6 col-xs-12 hidden-xs'i><'col-xs-12 col-sm-6'p>>",
                "autoWidth" : true,

                "preDrawCallback" : function() {
                    // Initialize the responsive datatables helper once.
                    if (!responsiveHelper_dt_basic) {
                        responsiveHelper_dt_basic = new ResponsiveDatatablesHelper($('#datatypes-records'), breakpointDefinition);
                    }
                },
                "rowCallback" : function(nRow) {
                    responsiveHelper_dt_basic.createExpandIcon(nRow);
                },
                "drawCallback" : function(oSettings) {
                    responsiveHelper_dt_basic.respond();
                }


            });

            var valuesTable = $('#values-records').dataTable({
                "sDom": "<'dt-toolbar'<'col-xs-12 col-sm-6'f><'col-sm-6 col-xs-12 hidden-xs'l>r>"+
                    "t"+
                    "<'dt-toolbar-footer'<'col-sm-6 col-xs-12 hidden-xs'i><'col-xs-12 col-sm-6'p>>",
                "autoWidth" : true,

                "pageLength": 4,
                "columns": [
                    null,
                    {
                        "className":      'editValue',
                        "orderable":      false
                    },
                    {
                        "className":      'overrideValue',
                        "orderable":      false
                    }
                ],

                "preDrawCallback" : function() {
                    // Initialize the responsive datatables helper once.
                    if (!responsiveHelper_dt_basic) {
                        responsiveHelper_dt_basic = new ResponsiveDatatablesHelper($('#values-records'), breakpointDefinition);
                    }
                },
                "rowCallback" : function(nRow) {
                    responsiveHelper_dt_basic.createExpandIcon(nRow);
                },
                "drawCallback" : function(oSettings) {
                    responsiveHelper_dt_basic.respond();
                },

                fnDrawCallback : function() {
                    $('.overrideValue')
                        .off("click", overrideHandler)
                        .on("click", overrideHandler);
                    $('.editValue')
                        .off("click", editHandler)
                        .on("click", editHandler)
                }
            });

            function overrideHandler(){
                var idCatalogoLista = $(this.innerHTML).data('id');
                overrideValue(idCatalogoLista);


            }

            function editHandler(){
                var data = $(this.innerHTML).data('id');
                var detalle = data.split(",");
                $('#idCatalogoLista').val(detalle[0]);
                $('#idTipoD').val(detalle[1]);
                $('#valor').val(detalle[2]);

            }

            function getDataTypes() {
                $.getJSON(parametros.getDataTypes, {
                    ajax: 'true'
                }, function (data) {
                    dataTypesTable.fnClearTable();
                    var len = data.length;
                    for (var i = 0; i < len; i++) {

                        var overrideUrl = parametros.overrideUrl + data[i].idTipoDato;

                        if(data[i].tipo.valor == "Lista"){
                            dataTypesTable.fnAddData(
                                [data[i].nombre, data[i].tipo.valor, ' <button type="button" class="btn btn-default btn-xs evento" data-toggle="modal" data-id='+data[i].idTipoDato+ "," + data[i].nombre + "," + data[i].tipo.codigo+' ' +
                                    ' data-target="#myModal"> <i class="fa fa-edit"></i>'+
                                    '</button>', ' <button type="button" class="btn btn-default btn-xs ev" data-toggle="modal" data-id='+data[i].idTipoDato+' ' +
                                    ' data-target="#myModal2"> <i class="fa fa-list-ol"></i>'+
                                    '</button>', '<a href=' + overrideUrl + ' class="btn btn-default btn-xs btn-danger"><i class="fa fa-times"></i></a>' ]);
                        }else{
                            dataTypesTable.fnAddData(
                                [data[i].nombre, data[i].tipo.valor, ' <button type="button" class="btn btn-default btn-xs evento" data-toggle="modal" data-id='+data[i].idTipoDato+ "," + data[i].nombre + "," + data[i].tipo.codigo+' ' +
                                    ' data-target="#myModal"> <i class="fa fa-edit"></i>'+
                                    '</button>', ' <button type="button" disabled class="btn btn-default btn-xs ev" data-toggle="modal" data-id='+data[i].idTipoDato+' ' +
                                    ' data-target="#myModal2"> <i class="fa fa-list-ol"></i>'+
                                    '</button>', '<a href=' + overrideUrl + ' class="btn btn-default btn-xs btn-danger"><i class="fa fa-times"></i></a>' ]);
                        }

                    }

                    $(".evento").on("click", function(){
                        var data = $(this).data('id');
                        var detalle = data.split(",");

                        $('#idTipoDato').val(detalle[0]);
                        $('#nombre').val(detalle[1]);
                        $('#tipo').val(detalle[2]).change();
                    });

                    $(".ev").on("click", function(){
                        var data = $(this).data('id');
                        $('#idTipoD').val(data);
                        getValues(data);

                    });
                })
            }


            <!-- Validacion formulario de generacion de alicuotas -->
            var $validator = $("#dataType-form").validate({
                // Rules for form validation
                rules: {
                    tipo: {required : true},
                    nombre: {required : true}
                },
                // Do not change code below
                errorPlacement : function(error, element) {
                    error.insertAfter(element.parent());
                }
            });

            $('#btnAdd').click(function() {
                var $validarModal = $("#dataType-form").valid();
                if (!$validarModal) {
                    $validator.focusInvalid();
                    return false;
                } else {
                    addUpdateDataType();

                }
            });



            function addUpdateDataType() {
                blockUI(parametros.blockMess);
                var dataTypeObj = {};
                dataTypeObj['mensaje'] = '';
                dataTypeObj['nombre'] = $('#nombre').val();
                dataTypeObj['tipo'] = $('#tipo').val();
                dataTypeObj['idTipoDato'] = $('#idTipoDato').val();
                $.ajax(
                    {
                        url: parametros.addUpdateUrl,
                        type: 'POST',
                        dataType: 'json',
                        data: JSON.stringify(dataTypeObj),
                        contentType: 'application/json',
                        mimeType: 'application/json',
                        success: function (data) {
                            if (data.mensaje.length > 0) {
                                $.smallBox({
                                    title: data.mensaje,
                                    content: $("#disappear").val(),
                                    color: "#C46A69",
                                    iconSmall: "fa fa-warning",
                                    timeout: 4000
                                });
                            } else {

                              getDataTypes();
                                var msg = $("#msjSuccessful").val();
                                $.smallBox({
                                    title: msg,
                                    content: $("#disappear").val(),
                                    color: "#739E73",
                                    iconSmall: "fa fa-success",
                                    timeout: 4000
                                });
                                dataTypeObj['tipo'] = $('#tipo').val('').change();
                                dataTypeObj['nombre'] = $('#nombre').val('');
                            }
                            unBlockUI();
                        },
                        error: function (data, status, er) {
                            unBlockUI();
                            alert("error: " + data + " status: " + status + " er:" + er);
                        }
                    });

            }



            <!-- Validacion formulario de valores de una lista -->
            var $validator1 = $("#values-form").validate({
                // Rules for form validation
                rules: {
                    valor: {required : true}

                },
                // Do not change code below
                errorPlacement : function(error, element) {
                    error.insertAfter(element.parent());
                }
            });

            $('#btnAddValue').click(function() {
                var $validarModalV = $("#values-form").valid();
                if (!$validarModalV) {
                    $validator1.focusInvalid();
                    return false;
                } else {
                    addUpdateValue();

                }
            });

            function getValues(idTipoDato) {
                $.getJSON(parametros.getValues, {
                    idTipoDato: idTipoDato,
                    ajax: 'true'
                }, function (data) {
                    valuesTable.fnClearTable();
                    var len = data.length;
                    for (var i = 0; i < len; i++) {
                        var btnEdit = '<button type="button" class="btn btn-default btn-xs" data-id='+data[i].idCatalogoLista+ ","+ data[i].idTipoDato.idTipoDato +"," +data[i].valor+' ' +
                                    ' > <i class="fa fa-edit"></i>' ;

                        var btnOverride = '<button type="button" class="btn btn-default btn-xs btn-danger" data-id='+data[i].idCatalogoLista+ ' ' +
                            ' > <i class="fa fa-times"></i>' ;

                          valuesTable.fnAddData(

                                [data[i].valor, btnEdit, btnOverride ]);
                    }

                })
            }


            function addUpdateValue() {
                blockUI(parametros.blockMess);
                var valueObj = {};
                valueObj['mensaje'] = '';
                valueObj['pasivo']='';
                valueObj['valor'] = $('#valor').val();
                valueObj['idTipoDato'] = $('#idTipoD').val();
                valueObj['idCatalogoLista'] = $('#idCatalogoLista').val();
                $.ajax(
                    {
                        url: parametros.addUpdateValue,
                        type: 'POST',
                        dataType: 'json',
                        data: JSON.stringify(valueObj),
                        contentType: 'application/json',
                        mimeType: 'application/json',
                        success: function (data) {
                            if (data.mensaje.length > 0) {
                                $.smallBox({
                                    title: data.mensaje,
                                    content: $("#disappear").val(),
                                    color: "#C46A69",
                                    iconSmall: "fa fa-warning",
                                    timeout: 4000
                                });
                            } else {

                              getValues( valueObj['idTipoDato']);
                                var msg = $("#msjSuccessful1").val();
                                $.smallBox({
                                    title: msg,
                                    content: $("#disappear").val(),
                                    color: "#739E73",
                                    iconSmall: "fa fa-success",
                                    timeout: 4000
                                });
                                valueObj['valor'] = $('#valor').val('');
                            }
                            unBlockUI();
                        },
                        error: function (data, status, er) {
                            unBlockUI();
                            alert("error: " + data + " status: " + status + " er:" + er);
                        }
                    });

            }

            function overrideValue(idCatalogoLista) {
                var valueObj = {};
                valueObj['mensaje'] = '';
                valueObj['pasivo']= 'true';
                valueObj['valor'] = '';
                valueObj['idTipoDato'] = $('#idTipoD').val();
                valueObj['idCatalogoLista'] = idCatalogoLista;
                blockUI(parametros.blockMess);
                $.ajax(
                    {
                        url: parametros.addUpdateValue,
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
                                    timeout: 4000
                                });
                            }else{
                                getValues(data.idTipoDato);
                                var msg = $("#msg_value_cancel").val();
                                $.smallBox({
                                    title: msg ,
                                    content: $("#disappear").val(),
                                    color: "#739E73",
                                    iconSmall: "fa fa-success",
                                    timeout: 4000
                                });
                            }
                            unBlockUI();
                        },
                        error: function (data, status, er) {
                           unBlockUI();
                            alert("error: " + data + " status: " + status + " er:" + er);
                        }
                    });
            }
        }
};

}();