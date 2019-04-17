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
(function (window, navigator, document) {
    'use strict';

    // Init AdobeSummit namespace if not available.
    var deferredPrompt;

    window.AdobeSummit.Device = {
        deferredPrompt:false,
        startDeviceInstall : function () {
            if (this.deferredPrompt) {
                this.deferredPrompt.prompt();

                this.deferredPrompt.userChoice.then(function (choiceResult) {
                    console.log(choiceResult.outcome);

                    if (choiceResult.outcome === 'dismissed') {
                        console.log('User cancelled installation');
                    } else {
                        console.log('User added to home screen');
                    }
                });

                this.deferredPrompt = null;
            }
        },
    }

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
                 * browser. Requires a serviceworker in a `sw.js`. This file contains what will
                 * happen when we receive a push notification.
                 *
                 */
                $(window).load(function() {
                    /**
                     *
                     * Register the service worker.
                     *
                     *      SW_PATH:"/content/sw.js",
                     *      SW_SCOPE:"/content/aem-pwa-blog"
                     *
                     *      What does service worker scope do ? It will restrict the service worker
                     *      to be used only with pages beneath /content/aem-pwa-blog
                     */

                    // ===========================> CODE SHOULD BE PASTED BELOW <===========================


                });
                /**
                 * Step two: When the beforeinstallprompt event has fired,
                 * save a reference to the event,and update your user interface
                 * to indicate that the user can add your app to their home screen.
                 *
                 */
                $(window).on('beforeinstallprompt',function(event) {
                    console.log('[TL30-PWA] >>>>> beforeinstallprompt has been  fired !');
                    event.preventDefault();
                     deferredPrompt = event;
                    return false;
                });
            }
        }

    }
}(window, navigator, document));




