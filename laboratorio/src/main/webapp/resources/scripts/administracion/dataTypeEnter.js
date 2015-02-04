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
            var dataTypesTable = $('#dataTypes-records').dataTable({
                "sDom": "<'dt-toolbar'<'col-xs-12 col-sm-6'f><'col-sm-6 col-xs-12 hidden-xs'l>r>"+
                    "t"+
                    "<'dt-toolbar-footer'<'col-sm-6 col-xs-12 hidden-xs'i><'col-xs-12 col-sm-6'p>>",
                "autoWidth" : true,

                "preDrawCallback" : function() {
                    // Initialize the responsive datatables helper once.
                    if (!responsiveHelper_dt_basic) {
                        responsiveHelper_dt_basic = new ResponsiveDatatablesHelper($('#dataTypes-records'), breakpointDefinition);
                    }
                },
                "rowCallback" : function(nRow) {
                    responsiveHelper_dt_basic.createExpandIcon(nRow);
                },
                "drawCallback" : function(oSettings) {
                    responsiveHelper_dt_basic.respond();
                }
            });


            function getDataTypes() {
                $.getJSON(parametros.getDataTypes, {
                    ajax: 'true'
                }, function (data) {
                    dataTypesTable.fnClearTable();
                    var len = data.length;
                    for (var i = 0; i < len; i++) {
                        var overrideUrl = parametros.overrideDataType + data[i].idTipoDato;
                       dataTypesTable.fnAddData(
                            [data[i].nombre, data[i].tipo, ' <button type="button" id="btnAddAliquot" class="btn btn-primary btn-xs evento" data-toggle="modal" data-id='+data[i].idOrdenExamen+' ' +
                                ' data-target="#myModal"> <i class="fa fa-plus icon-white"></i>'+
                                '</button>', '<a href=' + overrideUrl + ' class="btn btn-default btn-xs btn-danger"><i class="fa fa-times"></i></a>' ]);
                    }

                    $(".evento").on("click", function(){
                        $('#idOrden').val($(this).data('id'));
                    });
                })
            }



            function addUpdate() {
                blockUI(parametros.blockMess);
                var dataTypeObj = {};
                dataTypeObj['mensaje'] = '';
                dataTypeObj['nombre'] = $('#etiqueta').val();
                dataTypeObj['tipo'] = $('#volumen').val();
                dataTypeObj['idTipoDato'] = $('#idTipoDato').val();


                $.ajax(
                    {
                        url: parametros.addUpdateDataType,
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





        }
};

}();