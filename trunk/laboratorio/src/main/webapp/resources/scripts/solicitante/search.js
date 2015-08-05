/**
 * Created by FIRSTICT on 8/4/2015.
 */
var SearchApplicant = function () {

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
            }
        });
    };

    return {
        //main function to initiate the module
        init: function (parametros) {
            var responsiveHelper_dt_basic = undefined;
            var breakpointDefinition = {
                tablet : 1024,
                phone : 480
            };
            var table1 = $('#applicants_result').dataTable({
                "sDom": "<'dt-toolbar'<'col-xs-12 col-sm-6'f><'col-sm-6 col-xs-12 hidden-xs'l>r>"+
                    "t"+
                    "<'dt-toolbar-footer'<'col-sm-6 col-xs-12 hidden-xs'i><'col-xs-12 col-sm-6'p>>",
                "autoWidth" : true,
                "preDrawCallback" : function() {
                    // Initialize the responsive datatables helper once.
                    if (!responsiveHelper_dt_basic) {
                        responsiveHelper_dt_basic = new ResponsiveDatatablesHelper($('#applicants_result'), breakpointDefinition);
                    }
                },
                "rowCallback" : function(nRow) {
                    responsiveHelper_dt_basic.createExpandIcon(nRow);
                },
                "drawCallback" : function(oSettings) {
                    responsiveHelper_dt_basic.respond();
                }
            });

            $('#search-form').validate({
                // Rules for form validation
                rules : {
                    filtro : {
                        required : true,
                        minlength: 3
                    }
                },
                // Do not change code below
                errorPlacement : function(error, element) {
                    error.insertAfter(element.parent());
                },
                submitHandler: function (form) {
                    table1.fnClearTable();
                    //add here some ajax code to submit your form or just call form.submit() if you want to submit the form without ajax
                    getApplicants();
                }
            });


            function getApplicants() {
                bloquearUI(parametros.blockMess);
                $.getJSON(parametros.searchUrl, {
                    strFilter : encodeURI($('#filtro').val()),
                    ajax : 'true'
                }, function(data) {
                    var len = data.length;
                    for ( var i = 0; i < len; i++) {
                        var actionUrl = parametros.sActionUrl + '/' + data[i].idSolicitante;
                        var habilitado = '<span class="label label-success"><i class="fa fa-thumbs-up fa-lg"></i></span>';
                        if (data[i].pasivo)
                        habilitado = '<span class="label label-danger"><i class="fa fa-thumbs-down fa-lg"></i></span>';
                        table1.fnAddData(
                            [data[i].nombre, data[i].telefono, habilitado, data[i].nombreContacto, data[i].correoContacto, data[i].telefonoContacto,'<a href='+ actionUrl + ' class="btn btn-primary btn-xs"><i class="fa fa-edit"></i></a>']);

                    }
                    setTimeout($.unblockUI, 500);
                })
                    .fail(function() {
                        alert( "error" );
                        setTimeout($.unblockUI, 5);
                        alert(" status: " + status + " er:" + er);
                    });
            }

            $("#create-person").click(function(){
                window.location.href = parametros.createUrl;
            });
        }
    };

}();
