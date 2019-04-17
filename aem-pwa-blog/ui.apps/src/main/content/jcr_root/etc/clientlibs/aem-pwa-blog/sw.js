importScripts('/etc.clientlibs/aem-pwa-blog/clientlibs/clientlib-utils.js');
importScripts('/etc.clientlibs/aem-pwa-blog/clientlibs/clientlib-firebase.js');

/*
 ================================================== FOR ANY MODIFICATION INCREASE   ========================================================
 =
 =  The version number should be incremented
 =
 ================================================================================================================================================
 */
var version = "164220011211119";

var CACHE_STATIC_NAME = 'static-v'+version;
var CACHE_DYNAMIC_NAME = 'dynamic-v'+version;

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

/*
 ================================================== Exercise 02 : (2) Registering service worker  ========================================================
 =
 =  When window.AdobeSummit.Exercise02.init() method is called the service worker is registered and an install event is caught by the service
 =  worker.The install event is fired only once
 =
 ================================================================================================================================================
 */
self.addEventListener('install', function(event) {
    // don't wait if there is a newer version of the service worker.
    self.skipWaiting();

    console.log('[TL30-PWA] >>>>> Installing Service Worker ...', event);

    // ===========================> CODE SHOULD BE PASTED BELOW <===========================

});
/*
 ================================================== Exercise 02 : (2) Registering service worker  ========================================================
 =
 =  After the install event completion, there is an activate event that is sent by the browser to notify the service worker that everything went fine.
 =
 =
 ================================================================================================================================================
 */
self.addEventListener('activate', function(event) {

    // ===========================> CODE SHOULD BE PASTED BELOW <===========================

});

/*

 ================================================== Exercise 04 : (2) Caching app shell  ========================================================
 =
 =  We cache only dynamic  resources such html pages, json payload fron ajax requests...
 =
 ================================================================================================================================================
 */

self.addEventListener('fetch', function(event) {

    // ===========================> CODE SHOULD BE PASTED BELOW <===========================

});
/*
 ================================================== Exercise 05 :  Background sync  ========================================================
 =
 =  When window.AdobeSummit.Exercise05.init() we register the sync manager to check network availability, when a sync event occured because
 = the network is back then all post that have been store within indexdb will be executed.
 =
 ================================================================================================================================================
 */
self.addEventListener('sync', function(event) {
    console.log('[TL30-PWA][sync] Background syncing', event);

    // ===========================> CODE SHOULD BE PASTED BELOW <===========================

});

/*
 ================================================== Exercise 06 :  Background sync  ========================================================
 =
 =  When a user has allow his/her device to receive notifications, any notification will be caught by the service worker that will trigger a
 = notification.
 =
 ================================================================================================================================================
 */
self.addEventListener('push', function(event) {
    console.log('[Service Worker] Push Received.');

    // ===========================> CODE FROM ex05-code-to-paste-02.txt SHOULD BE PASTED BELOW <===========================

});


self.addEventListener('notificationclick', function(event) {
    var notification = event.notification;
    var action = event.action;

    console.log(notification);

    if (action === 'confirm') {
        console.log('Confirm was chosen');
        notification.close();
    } else {
        console.log(action);
        event.waitUntil(
            clients.matchAll()
                .then(function(clis) {
                    var client = clis.find(function(c) {
                        return c.visibilityState === 'visible';
                    });

                    if (client !== undefined) {
                        client.navigate(notification.data.url);
                        client.focus();
                    } else {
                        clients.openWindow(notification.data.url);
                    }
                    notification.close();
                })
        );
    }
});

self.addEventListener('notificationclose', function(event) {
    console.log('Notification was closed', event);
});







