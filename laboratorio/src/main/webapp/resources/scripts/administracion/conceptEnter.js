/**
 * Created by souyen-ics on 02-03-15.
 */

var Concepts  = function () {

    return {
        init: function (parametros) {
            getConcepts();

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
                    },
                    baseZ: 1051 // para que se muestre bien en los modales
                });
            }

            function unBlockUI() {
                setTimeout($.unblockUI, 500);
            }

            var responsiveHelper_dt_basic = undefined;
            var breakpointDefinition = {
                tablet : 1024,
                phone : 480
            };
            var conceptsTable = $('#concepts-records').dataTable({
                "sDom": "<'dt-toolbar'<'col-xs-12 col-sm-6'f><'col-sm-6 col-xs-12 hidden-xs'l>r>"+
                    "t"+
                    "<'dt-toolbar-footer'<'col-sm-6 col-xs-12 hidden-xs'i><'col-xs-12 col-sm-6'p>>",
                "autoWidth" : true,

                "columns": [
                    null, null,
                    {
                        "className":      'editConcept',
                        "orderable":      false
                    },
                    {
                        "className":      'addList',
                        "orderable":      false
                    },

                    {
                        "className":      'overrideConcept',
                        "orderable":      false
                    }
                ],

                "preDrawCallback" : function() {
                    // Initialize the responsive datatables helper once.
                    if (!responsiveHelper_dt_basic) {
                        responsiveHelper_dt_basic = new ResponsiveDatatablesHelper($('#concepts-records'), breakpointDefinition);
                    }
                },
                "rowCallback" : function(nRow) {
                    responsiveHelper_dt_basic.createExpandIcon(nRow);
                },
                "drawCallback" : function(oSettings) {
                    responsiveHelper_dt_basic.respond();
                },


                fnDrawCallback : function() {


                    $('.editConcept')
                        .off("click", editCHandler)
                        .on("click", editCHandler);
                    $('.addList')
                        .off("click", addListHandler)
                        .on("click", addListHandler);
                    $('.overrideConcept')
                        .off("click", overrideCHandler)
                        .on("click", overrideCHandler);

                }


            });

            var valuesTable = $('#values-records').dataTable({
                "sDom": "<'dt-toolbar'<'col-xs-12 col-sm-6'f><'col-sm-6 col-xs-12 hidden-xs'l>r>"+
                    "t"+
                    "<'dt-toolbar-footer'<'col-sm-6 col-xs-12 hidden-xs'i><'col-xs-12 col-sm-6'p>>",
                "autoWidth" : true,

                "pageLength": 4,
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
                        .on("click", editHandler);
                }
            });

            function overrideHandler(){
                var idCatalogoLista = $(this.innerHTML).data('id');
                if (idCatalogoLista != null) {
                    var disabled = this.innerHTML;
                    var n2 = (disabled.indexOf("disabled") > -1);
                    if (!n2) {
                        overrideValue(idCatalogoLista);
                    }
                }
            }

            function editHandler(){
                var data = $(this.innerHTML).data('id');
                var detalle = data.split(",");
                $('#idCatalogoLista').val(detalle[0]);
                $('#idC').val(detalle[1]);
                $('#valor').val(detalle[2]);
                $('#etiqueta').val(detalle[3]);

            }

            function overrideCHandler(){
                var idConcepto = $(this.innerHTML).data('id');
                overrideConcept(idConcepto)


            }

            function editCHandler(){
                var data =  $(this.innerHTML).data('id');
                var detalle = data.split(",");
                $('#idConcepto').val(detalle[0]);
                $('#nombre').val(detalle[1]);
                $('#tipo').val(detalle[2]).change();
                showModalConcept();
            }


            function showModalConcept(){
                $("#myModal").modal({
                    show: true
                });
            }

            function showModalValues(){
                $("#myModal2").modal({
                    show: true
                });
            }

            function addListHandler(){
                var data =  $(this.innerHTML).data('id');
                if (data != null) {
                    var detalle = data.split(",");
                    var codigo = detalle[0];
                    var name = detalle[1];
                    var disabled = this.innerHTML;
                    var n2 = (disabled.indexOf("disabled") > -1);
                    if (!n2) {
                        $('#concName').html(name);
                        $('#idC').val(codigo);
                        $('#valor').val('');
                        $('#etiqueta').val('');
                        getValues(codigo);
                        showModalValues();
                    }
                }

            }

            function getConcepts() {
                $.getJSON(parametros.getConcepts, {
                    ajax: 'true'
                }, function (data) {
                    conceptsTable.fnClearTable();
                    var len = data.length;
                    for (var i = 0; i < len; i++) {

                        var btnEditC = '<button type="button" title="Editar" class="btn btn-default btn-xs btn-primary" data-id="'+data[i].idConcepto+ "," + data[i].nombre + "," + data[i].tipo.codigo+''+
                            '"> <i class="fa fa-edit"></i>' ;

                        var btnAddList = ' <button type="button" title="Valores de lista" class="btn btn-primary btn-xs " data-id="'+data[i].idConcepto + "," + data[i].nombre +
                            '"> <i class="fa fa-list-ol"></i>';


                        var btnAddListDisabled = ' <button type="button" title="Valores de lista" disabled class="btn btn-primary btn-xs " data-id="'+data[i].idConcepto+'' +
                            '"> <i class="fa fa-list-ol"></i>';


                        var btnOverrideC = ' <button type="button" title="Anular" class="btn btn-default btn-xs btn-danger" data-id="'+data[i].idConcepto+'' +
                            '"> <i class="fa fa-times"></i>';


                        if(data[i].tipo.valor == "Lista"){
                            conceptsTable.fnAddData(
                                [data[i].nombre, data[i].tipo.valor,btnEditC , btnAddList , btnOverrideC ]);
                        }else{
                            conceptsTable.fnAddData(
                                [data[i].nombre, data[i].tipo.valor, btnEditC, btnAddListDisabled  ,  btnOverrideC ]);
                        }

                    }
                }).fail(function(jqXHR) {
                    unBlockUI();
                    validateLogin(jqXHR);
                });
            }


            <!-- Validacion formulario de generacion de alicuotas -->
            var $validator = $("#concepts-form").validate({
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

            $("#btnAdds").click(function(){
                $("#idConcepto").val('');
                $("#nombre").val('');
                $("#tipo").val('').change();
                showModalConcept();
            });

            $('#btnAdd').click(function() {
                var $validarModal = $("#concepts-form").valid();
                if (!$validarModal) {
                    $validator.focusInvalid();
                    return false;
                } else {
                    addUpdateConcept();

                }
            });



            function addUpdateConcept() {
                blockUI(parametros.blockMess);
                var conceptObj = {};
                conceptObj['mensaje'] = '';
                conceptObj['nombre'] = $('#nombre').val();
                conceptObj['tipo'] = $('#tipo').val();
                conceptObj['idConcepto'] = $('#idConcepto').val();
                conceptObj['pasivo'] = '';
                $.ajax(
                    {
                        url: parametros.addUpdateUrl,
                        type: 'POST',
                        dataType: 'json',
                        data: JSON.stringify(conceptObj),
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

                              getConcepts();
                                var msg = $("#msjSuccessful").val();
                                $.smallBox({
                                    title: msg,
                                    content: $("#disappear").val(),
                                    color: "#739E73",
                                    iconSmall: "fa fa-success",
                                    timeout: 4000
                                });
                                conceptObj['tipo'] = $('#tipo').val('').change();
                                conceptObj['nombre'] = $('#nombre').val('');
                            }
                            unBlockUI();
                        },
                        error: function (jqXHR) {
                            unBlockUI();
                            validateLogin(jqXHR);
                        }
                    });

            }



            <!-- Validacion formulario de valores de una lista -->
            var $validator1 = $("#values-form").validate({
                // Rules for form validation
                rules: {
                    valor: {required : true},
                    etiqueta: {required : true}

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

            function getValues(idConcepto) {
                $.getJSON(parametros.getValues, {
                    idConcepto: idConcepto,
                    ajax: 'true'
                }, function (data) {
                    valuesTable.fnClearTable();
                    var len = data.length;
                    for (var i = 0; i < len; i++) {
                        var btnEdit = '<button type="button" title="Editar" class="btn btn-default btn-xs btn-primary" data-id="'+data[i].idCatalogoLista+ ","+ data[i].idConcepto.idConcepto
                            +"," +data[i].valor+"," +data[i].etiqueta +'" > <i class="fa fa-edit"></i>' ;

                        var btnOverride = '<button type="button" title="Anular" class="btn btn-default btn-xs btn-danger" data-id="'+data[i].idCatalogoLista+ '' +
                            '" > <i class="fa fa-times"></i>' ;

                          valuesTable.fnAddData(

                                [data[i].valor, data[i].etiqueta, btnEdit, btnOverride ]);
                    }

                }).fail(function(jqXHR) {
                    unBlockUI();
                    validateLogin(jqXHR);
                });
            }


            function addUpdateValue() {
                blockUI(parametros.blockMess);
                var valueObj = {};
                valueObj['mensaje'] = '';
                valueObj['pasivo']='';
                valueObj['valor'] = $('#valor').val();
                valueObj['etiqueta'] = $('#etiqueta').val();
                valueObj['idConcepto'] = $('#idC').val();
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

                              getValues( valueObj['idConcepto']);
                                var msg = $("#msjSuccessful1").val();
                                $.smallBox({
                                    title: msg,
                                    content: $("#disappear").val(),
                                    color: "#739E73",
                                    iconSmall: "fa fa-success",
                                    timeout: 4000
                                });
                                $('#valor').val('');
                                $('#etiqueta').val('');
                                $('#idCatalogoLista').val('');
                            }
                            unBlockUI();
                        },
                        error: function (jqXHR) {
                            unBlockUI();
                            validateLogin(jqXHR);
                        }
                    });

            }

            function overrideValue(idCatalogoLista) {
                var valueObj = {};
                valueObj['mensaje'] = '';
                valueObj['pasivo']= 'true';
                valueObj['valor'] = '';
                valueObj['etiqueta'] = '';
                valueObj['idConcepto'] = $('#idC').val();
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
                                getValues(data.idConcepto);
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
                        error: function (jqXHR) {
                            unBlockUI();
                            validateLogin(jqXHR);
                        }
                    });
            }

            function overrideConcept(idConcepto) {
                var conceptObj = {};
                conceptObj['mensaje'] = '';
                conceptObj['nombre'] = $('#nombre').val();
                conceptObj['tipo'] = $('#tipo').val();
                conceptObj['idConcepto'] = idConcepto;
                conceptObj['pasivo'] = 'true';
                blockUI(parametros.blockMess);
                $.ajax(
                    {
                        url: parametros.addUpdateUrl,
                        type: 'POST',
                        dataType: 'json',
                        data: JSON.stringify(conceptObj),
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
                               getConcepts();
                                var msg = $("#msg_conc_cancel").val();
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
                        error: function (jqXHR) {
                            unBlockUI();
                            validateLogin(jqXHR);
                        }
                    });
            }


        }
};

}();