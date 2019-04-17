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

                    // ===========================> CODE FROM ex05-code-to-paste-01.txt SHOULD BE PASTED BELOW <===========================

                });
            }


        },
        init:function () {
            /**
             * -----------------------------------------------------------------------
             * --
             * --                   Functions
             * --
             * -----------------------------------------------------------------------
             */

            // ===========================> CODE FROM ex05-code-to-paste-00.txt SHOULD BE PASTED BELOW <===========================



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