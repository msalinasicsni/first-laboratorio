/**
 * Created by FIRSTICT on 7/7/2015.
 */
    function unicodeEscape(cadena) {
    var r = /\\u([\d\w]{4})/gi;
    cadena = cadena.replace(r, function (match, grp) {
        return String.fromCharCode(parseInt(grp, 16));
    });
    return decodeURIComponent(cadena);
}