/*!
 *  Adobe Summit EMEA 2019 - TL30 : Building a PWA with AEM
 *
 * @description :
 *                  This description must be updated
 *
 * @file : /apps/aem-pwa-blog/clientlibs/clientlib-exercises/scripts/ex04_push-notifications.js
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

    /**
     * ---------------------------------------------
     *
     *          EXERCISE 06
     *
     * ---------------------------------------------
     *
     *
     * @type {{init: AdobeSummit.Exercise04.init}}
     */
    window.AdobeSummit.Exercise04 =  {

        init:function (swRegistration,channel) {

            /**
             *  When the service worker has received a push notification,
             *  it will then send the informations to the page using a channel
             *  with the event data from AEM.
             */
            if(channel){
                channel.addEventListener('message', function (event) {
                    if(event.data.type == "web-push-received"){
                        var $webPush = $("#webpush-received");
                        var data = JSON.parse(event.data.content.notification.body);
                        $webPush.find("#notification-content").text( data.message);
                        $webPush.find("#notification-image").prop("src", data.path);
                        $webPush.modal("show");
                    }
                } );
            }


            /**
             *  When the service worker has been activated, we register a token from
             *  Firebase Cloud Messaging server to AEM, in order to send piush notifications
             *  later on.
             */

            firebase.initializeApp(config);

            var messaging = firebase.messaging();
            messaging.useServiceWorker(swRegistration);

            var $pushButton = $('.js-push-btn');

            if($pushButton){
                $pushButton.click( function() {

                    /**
                     ======================================================================================================

                     Exercise 04 :  Push notifications
                     -----------
                     Copy the code from this file : /apps/aem-pwa-blog/code-snippets/exercise-04/ex04-code-to-paste-00.txt
                     below this commented block  :

                     =======================================================================================================
                     **/

                });
            }

            messaging.onTokenRefresh(function () {
                messaging.getToken()
                    .then(function (refreshedToken) {
                        console.log('[TL30-PWA][pushNotification] Token refreshed : '+refreshedToken);
                        //Send the new token to AEM
                        fetch('/bin/aem-pwa-blog/notifications.json', {
                            'method': 'POST',
                            'headers': {
                                'Content-Type': 'application/json'
                            },
                            'body': JSON.stringify({'token': refreshedToken})
                        }).then(function(response) {
                            console.log("[TL30-PWA][pushNotification] Subscription to web push notifications has responded with :"+response);
                        }).catch(function(error) {
                            console.error("[TL30-PWA][pushNotification] Subscription to web push notifications failed" +error);
                        })
                    }).catch(function (err) {
                    console.log('[TL30-PWA][pushNotification] Unable to retrieve refreshed token ', err);
                    alert('Error when trying to retrieve refreshed token from Firebase!');
                });
            });

        }
    }

}(window, navigator, document));