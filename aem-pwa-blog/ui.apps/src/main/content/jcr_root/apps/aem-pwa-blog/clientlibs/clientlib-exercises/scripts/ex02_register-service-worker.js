/*!
 *  Adobe Summit EMEA 2019 - TL30 : Building a PWA with AEM
 *
 * @description :
 *                  This file contains the code for registering a service worker.
 *
 * @file : /apps/aem-pwa-blog/clientlibs/clientlib-exercises/scripts/ex02_register-service-worker.js
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
    /**
     * ---------------------------------------------
     *
     *          EXERCISE 02
     *
     * ---------------------------------------------
     *
     *
     * @type {{init: AdobeSummit.Exercise02.init}}
     */
    window.AdobeSummit.Exercise02 =  {

        /**
         *
         *
         *
         */
        isBrowserPWACompliant : function () {
            if ('serviceWorker' in navigator && 'PushManager' in window) {
                console.log('[TL30-PWA] >>>>> Service Worker and Push is supported');
                return true;
            }else{
                console.warn('Service workers and Push messaging are not supported.');
                return false;
            }
        },
        init:function () {
            if(this.isBrowserPWACompliant()){
                /**
                 * Step one: run a function on load (or whenever is appropriate for you)
                 * Function run on load sets up the service worker if it is supported in the
                 * browser. Requires a serviceworker in a `/content/sw.js`.
                 *
                 */


                $(window).load(function() {
                    /**
                     =============================================================================================

                     Exercise 02 : Register the service worker
                     -----------
                     Copy the code from this file : /apps/aem-pwa-blog/config.exercise-02/ex02-code-to-paste.txt
                     below this commented block  :

                     =============================================================================================
                     **/
                    navigator.serviceWorker.register("/content/sw.js")
                        .then(function(registration) {

                            // Registration was successful
                            console.log('[TL30-PWA] >>>>> ServiceWorker registration successful with scope: ', registration.scope);

                            var channel = new BroadcastChannel('sw-messages');
                            window.AdobeSummit.Exercise04.init(registration,channel);
                            window.AdobeSummit.Exercise05.init(registration,channel);
                            window.AdobeSummit.Exercise06.init(channel);
                            window.AdobeSummit.initDynamicData()
                        }, function(err) {
                            // registration failed :(
                            console.log('[TL30-PWA] >>>>> ServiceWorker registration failed: ', err);
                        });

                });
            }
        }

    }
}(window, navigator, document));




