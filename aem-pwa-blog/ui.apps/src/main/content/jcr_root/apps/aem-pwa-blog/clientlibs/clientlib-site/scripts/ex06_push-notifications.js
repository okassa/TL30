/*!
 *  Adobe Summit EMEA 2019 - TL30 : Building a PWA with AEM
 *
 * @description :
 *                  This description must be updated
 *
 * @file : /apps/aem-pwa-blog/clientlibs/clientlib-site/scripts/ex06_push-notifications.js
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
(function (window, navigator, document) {
    'use strict';
    var config = {
        apiKey: "AIzaSyDSJsppoDjFCppABijYv5IXiEADtbdp_tM",
        authDomain: "aem-pwa-blog.firebaseapp.com",
        databaseURL: "https://aem-pwa-blog.firebaseio.com",
        projectId: "aem-pwa-blog",
        storageBucket: "aem-pwa-blog.appspot.com",
        messagingSenderId: "294077202000"
    };
    firebase.initializeApp(config);

    var messaging = firebase.messaging();

    window.AdobeSummit.Exercise06 =  {

        initializeUI : function (swRegistration) {

            messaging.useServiceWorker(swRegistration);

            var pushButton = document.querySelector('.js-push-btn');

            if(pushButton){
                pushButton.addEventListener('click', function() {
                    messaging
                        .requestPermission()
                        .then(function () {
                            console.log("[TL30-PWA][pushNotification] Got notification permission");
                            // Send the token to the server to check it with validate_only
                            return messaging.getToken();
                        })
                        .then(function (token) {
                            // print the token on the HTML page
                            console.log("[TL30-PWA][pushNotification] Token", token);
                            // Retrieve user preferences from a dataLayer
                            var topic = "sport";
                            //Suscribe to user topics
                            fetch('/bin/aem-pwa-blog/notifications.json', {
                                'method': 'POST',
                                'headers': {
                                    'Content-Type': 'application/json'
                                },
                                'body': JSON.stringify({'token': token})
                            }).then(function(response) {
                                console.log("[TL30-PWA][pushNotification] Subscription to "+topic+" has responded with :"+response);
                            }).catch(function(error) {
                                console.error("[TL30-PWA][pushNotification] Subscription to the topic "+topic+" failed" +error);
                            })
                        })
                        .catch(function (err) {
                            console.log("[TL30-PWA][pushNotification] Didn't get notification permission", err);
                        });
                });
            }


        },
        init:function (channel) {
            /**
             * -----------------------------------------------------------------------
             * --
             * --                   Functions
             * --
             * -----------------------------------------------------------------------
             */

            navigator.serviceWorker.ready
                .then(function (sw) {
                    window.AdobeSummit.Exercise06.initializeUI(sw);
                });


            channel.addEventListener('message', function (event) {
                if(event.data.type == "web-push-received"){
                    //alert("The service worker has synchronized the post");
                    var $webPush = $("#webpush-received");
                    $webPush.find("#notification-content").text( event.data.content.notification.body);
                    $webPush.modal("show");
                }

            } );


            messaging.onTokenRefresh(function () {
                messaging.getToken()
                    .then(function (refreshedToken) {
                        console.log('[TL30-PWA][pushNotification] Token refreshed : '+refreshedToken);
                    }).catch(function (err) {
                    console.log('[TL30-PWA][pushNotification] Unable to retrieve refreshed token ', err);
                });
            });
        }
    }

}(window, navigator, document));