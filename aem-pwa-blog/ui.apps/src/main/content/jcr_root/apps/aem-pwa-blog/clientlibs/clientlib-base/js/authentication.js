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

    var host = 'http://'+window.location.host;
    function flushError() {
        $("#error").text('').addClass('hidden');
    }

    function displayError(message) {
        $("#error").text(message).removeClass('hidden');
    }

    function updateAppShell(cache,path) {
        cache.match(path).then(function(matchedResponse) {
            cache.put(path+"?appShell=true", matchedResponse);
            cache.delete(path);
        });
    }

    function updateCaches(cacheName,path) {
        if(cacheName.indexOf("dynamic-") > -1) {
            console.log(cacheName);
            caches.open(cacheName).then(function(cache) {
                cache.delete(path);
                cache.delete(host+path);
            });
        }

    }

    function getCookie(cname) {
        var name = cname + "=";
        var decodedCookie = decodeURIComponent(document.cookie);
        var ca = decodedCookie.split(';');
        for(var i = 0; i <ca.length; i++) {
            var c = ca[i];
            while (c.charAt(0) == ' ') {
                c = c.substring(1);
            }
            if (c.indexOf(name) == 0) {
                return c.substring(name.length, c.length);
            }
        }
        return "";
    }

    $( document ).ready(function() {
        var authenticated = getCookie("login-token");
        if(authenticated && authenticated !== ""){
            $("#logout").show();
        }else{
            $("#logout").hide();
        }
    });

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

                document.location = "/content/aem-pwa-blog/home.html";
            },
            error: function() {
                displayError(errorMessage);
                form.j_password.value="";
            }
        });
        return true;
    });
    $(".logout-link").on('click', function() {

        $.ajax({
            url: "/system/sling/logout?resource=/content/aem-pwa-blog/home.html",
            type: "POST",
            async: false,
            global: false,
            success: function (data, code, jqXHR){
                document.location = "/content/aem-pwa-blog/home.html";
            },
            error: function() {
                displayError("Error occured during logout");
            }
        });
    });

})();
