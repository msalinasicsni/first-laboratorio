/**
 * Created by souyen-ics on 06-09-15.
 */
var SampleTypes  = function () {

    return {
        init: function (parametros) {
            getSampleTypes();

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
            var sampleTypesTable = $('#sample-types-records').dataTable({
                "sDom": "<'dt-toolbar'<'col-xs-12 col-sm-6'f><'col-sm-6 col-xs-12 hidden-xs'l>r>"+
                    "t"+
                    "<'dt-toolbar-footer'<'col-sm-6 col-xs-12 hidden-xs'i><'col-xs-12 col-sm-6'p>>",
                "autoWidth" : true,

                "columns": [
                    null, null,
                    {
                        "className":      'editSample',
                        "orderable":      false
                    },

                    {
                        "className":      'overrideSample',
                        "orderable":      false
                    }
                ],

                "preDrawCallback" : function() {
                    // Initialize the responsive datatables helper once.
                    if (!responsiveHelper_dt_basic) {
                        responsiveHelper_dt_basic = new ResponsiveDatatablesHelper($('#sample-types-records'), breakpointDefinition);
                    }
                },
                "rowCallback" : function(nRow) {
                    responsiveHelper_dt_basic.createExpandIcon(nRow);
                },
                "drawCallback" : function(oSettings) {
                    responsiveHelper_dt_basic.respond();
                },


                fnDrawCallback : function() {
                    $('.editSample')
                        .off("click", editCHandler)
                        .on("click", editCHandler);
                    $('.overrideSample')
                        .off("click", overrideCHandler)
                        .on("click", overrideCHandler);

                }


            });

            function overrideCHandler(){
                var idSample = $(this.innerHTML).data('id');
                overrideSampleType(idSample)


            }

            function editCHandler(){
                var data =  $(this.innerHTML).data('id');
                var detalle = data.split(",");
                $('#idTipoMx').val(detalle[0]);
                $('#nombre').val(detalle[1]);
                showModalSampleType();
            }


            function showModalSampleType(){
                $("#myModal").modal({
                    show: true
                });
            }

            function getSampleTypes() {
                $.getJSON(parametros.sampleTypesUrl, {
                    ajax: 'true'
                }, function (data) {
                    sampleTypesTable.fnClearTable();
                    var len = data.length;
                    for (var i = 0; i < len; i++) {

                        var btnEdit = '<button type="button" class="btn btn-default btn-xs" data-id="'+data[i].idTipoMx+ "," + data[i].nombre +
                            '" > <i class="fa fa-edit"></i>' ;

                       var btnOverride = ' <button type="button" class="btn btn-default btn-xs btn-danger" data-id="'+data[i].idTipoMx+
                            '"> <i class="fa fa-times"></i>';

                            sampleTypesTable.fnAddData(
                                [data[i].idTipoMx, data[i].nombre, btnEdit, btnOverride ]);


                    }
                })
            }


            <!-- Validacion formulario -->
            var $validator = $("#sample-type-form").validate({
                // Rules for form validation
                rules: {
                   nombre: {required : true}
                },
                // Do not change code below
                errorPlacement : function(error, element) {
                    error.insertAfter(element.parent());
                }
            });

            $('#btnAdd').click(function() {
                var $validarModal = $("#sample-type-form").valid();
                if (!$validarModal) {
                    $validator.focusInvalid();
                    return false;
                } else {
                    addUpdateSampleType();

                }
            });



            function addUpdateSampleType() {
                blockUI(parametros.blockMess);
                var obj = {};
                obj['mensaje'] = '';
                obj['nombre'] = $('#nombre').val();
                obj['idTipoMx'] = $('#idTipoMx').val();
                obj['pasivo'] = '';
                $.ajax(
                    {
                        url: parametros.addUpdateUrl,
                        type: 'POST',
                        dataType: 'json',
                        data: JSON.stringify(obj),
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

                                getSampleTypes();
                                var msg = $("#msjSuccessful").val();
                                $.smallBox({
                                    title: msg,
                                    content: $("#disappear").val(),
                                    color: "#739E73",
                                    iconSmall: "fa fa-success",
                                    timeout: 4000
                                });

                                obj['nombre'] = $('#nombre').val('');
                                obj['idTipoMx'] = $('#idTipoMx').val('');
                            }
                            unBlockUI();
                        },
                        error: function (data, status, er) {
                            unBlockUI();
                            alert("error: " + data + " status: " + status + " er:" + er);
                        }
                    });

            }

            function overrideSampleType(idTipoMx) {
                var sampleTypeObj = {};
                sampleTypeObj['mensaje'] = '';
                sampleTypeObj['nombre'] = $('#nombre').val();
                sampleTypeObj['idTipoMx'] = $('#idTipoMx').val();
                sampleTypeObj['pasivo'] = 'true';
                blockUI(parametros.blockMess);
                $.ajax(
                    {
                        url: parametros.addUpdateUrl,
                        type: 'POST',
                        dataType: 'json',
                        data: JSON.stringify(sampleTypeObj),
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
                                getSampleTypes();
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
                        error: function (data, status, er) {
                            unBlockUI();
                            alert("error: " + data + " status: " + status + " er:" + er);
                        }
                    });
            }


        }
    };

}();