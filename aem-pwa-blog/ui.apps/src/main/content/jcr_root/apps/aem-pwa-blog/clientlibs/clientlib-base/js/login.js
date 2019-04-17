/*!
 *  Adobe Summit EMEA 2019 - TL30 : Building a PWA with AEM
 *
 * @description :
 *                  This file contains the code for displaying the cross when clicking
 *                  onto the nav toggle button
 *
 * @file : /apps/aem-pwa-blog/clientlibs/clientlib-base/js/updateIcon.js
 *
 *
 *
 * @project Adobe Summit EMEA - TL30
 * @date 2019-05-15
 * @author Olympe KASSA, Adobe <kassa@adobe.com>
 * @licensor Adobe
 * @site none
 *
 */
;
(function() {
    "use strict";

function flushError() {
    $("#error").text('').addClass('hidden');
}

function displayError(message) {
    $("#error").text(message).removeClass('hidden');
}

// Bind an event listener on login form to make an ajax call
$("#login").submit(function(event) {
    event.preventDefault();
    var form = this;
    var path = form.action;
    var user = form.j_username.value;
    var pass = form.j_password.value;
    var errorMessage = form.errorMessage.value;
    var resource = form.resource.value;

    // if no user is given, avoid login request
    if (!user) {
        return true;
    }


    // send user/id password to check and persist
    $.ajax({
        url: path,
        type: "POST",
        async: false,
        global: false,
        dataType: "text",
        data: {
            _charset_: "utf-8",
            j_username: user,
            j_password: pass,
            j_validate: true
        },
        success: function (data, code, jqXHR){
            var u = resource;
            if (window.location.hash && u.indexOf('#') < 0) {
                u = u + window.location.hash;
            }
            document.location = "/content/aem-pwa-blog/home.html?ts="+Date.now();
        },
        error: function() {
            displayError(errorMessage);
            form.j_password.value="";
        }
    });
    return true;
});

})();