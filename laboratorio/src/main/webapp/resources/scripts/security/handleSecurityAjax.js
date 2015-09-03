/**
 * Created by FIRSTICT on 9/3/2015.
 */
function validateLogin(jqXHR){
    if (jqXHR.status===401){
        $.smallBox({
            title: $("#txtNotLoginAjax").val() ,
            content: "error: "+ jqXHR.status + "-" + jqXHR.statusText ,
            color: "#C46A69",
            iconSmall: "fa fa-warning",
            timeout: 4000
        });
        var loc = window.location;
        var path = loc.pathname;
        var pathParts = path.split('/');
        var login = loc.protocol + "//" + loc.host + "/"+pathParts[1];
        setTimeout(function(){window.location.href = login;},2000);
    }else{
        $.smallBox({
            title: $("#txtErrorAjax").val() ,
            content: "error: "+ jqXHR.status + "-" + jqXHR.statusText ,
            color: "#C46A69",
            iconSmall: "fa fa-warning",
            timeout: 4000
        });
    }
}