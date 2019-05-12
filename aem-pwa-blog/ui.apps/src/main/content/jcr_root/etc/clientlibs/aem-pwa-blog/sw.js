importScripts('/etc.clientlibs/aem-pwa-blog/clientlibs/clientlib-utils.js');
importScripts('/etc.clientlibs/aem-pwa-blog/clientlibs/clientlib-firebase.js');

// ==============> TO INCREASE AFTER EACH AND EVERY MODIFICATION IN THE SERVICE WORKER <=================
var VERSION=001;

var CACHE_STATIC_NAME = 'static-v'+VERSION;
var CACHE_DYNAMIC_NAME = 'dynamic-v'+VERSION;

var STATIC_FILES = [
    '/content/aem-pwa-blog/home.html',
    '/content/aem-pwa-blog/home.offline.html',
    '/content/aem-pwa-blog/post.html',
    '/content/aem-pwa-blog/login.html',
    '/content/aem-pwa-blog/profile.html',
    '/etc.clientlibs/aem-pwa-blog/clientlibs/clientlib-vendor.js',
    '/etc.clientlibs/aem-pwa-blog/clientlibs/clientlib-vendor-pwa.js',
    '/etc.clientlibs/aem-pwa-blog/clientlibs/clientlib-firebase.js',
    '/etc.clientlibs/aem-pwa-blog/clientlibs/clientlib-utils.js',
    '/etc.clientlibs/aem-pwa-blog/clientlibs/clientlib-base.js',
    '/etc.clientlibs/aem-pwa-blog/clientlibs/clientlib-vendor/resources/fonts-awesome/fontawesome-webfont.eot?v=4.7.0',
    '/etc.clientlibs/aem-pwa-blog/clientlibs/clientlib-vendor/resources/fonts-awesome/fontawesome-webfont.eot?#iefix&v=4.7.0',
    '/etc.clientlibs/aem-pwa-blog/clientlibs/clientlib-vendor/resources/fonts-awesome/fontawesome-webfont.woff2?v=4.7.0',
    '/etc.clientlibs/aem-pwa-blog/clientlibs/clientlib-vendor/resources/fonts-awesome/fontawesome-webfont.woff?v=4.7.0',
    '/etc.clientlibs/aem-pwa-blog/clientlibs/clientlib-vendor/resources/fonts-awesome/fontawesome-webfont.ttf?v=4.7.0',
    '/etc.clientlibs/aem-pwa-blog/clientlibs/clientlib-vendor/resources/fonts-awesome/fontawesome-webfont.svg?v=4.7.0#fontawesomeregular',
    '/etc.clientlibs/aem-pwa-blog/clientlibs/clientlib-base.css',
    '/etc.clientlibs/aem-pwa-blog/clientlibs/clientlib-vendor.css',
    '/etc.clientlibs/aem-pwa-blog/clientlibs/clientlib-firebase.css',
    '/etc/clientlibs/aem-pwa-blog/icons/favicon.ico',
    '/etc/clientlibs/aem-pwa-blog/icons/summit-icon.png',
    '/etc/clientlibs/aem-pwa-blog/icons/summit-icon-48x48.png',
    '/etc/clientlibs/aem-pwa-blog/icons/summit-icon-96x96.png',
    '/etc/clientlibs/aem-pwa-blog/icons/summit-icon-144x144.png',
    '/etc/clientlibs/aem-pwa-blog/icons/summit-icon-192x192.png',
    '/etc/clientlibs/aem-pwa-blog/icons/summit-icon-256x256.png',
    '/etc/clientlibs/aem-pwa-blog/icons/summit-icon-384X384.png',
    '/etc/clientlibs/aem-pwa-blog/icons/summit-icon-512X512.png',
    '/etc/clientlibs/aem-pwa-blog/images/aem-logo-6.3.png',
    '/etc/clientlibs/aem-pwa-blog/images/pwa-logo.png',
    '/etc/clientlibs/aem-pwa-blog/logos/summit-logo-m.png',
    '/etc/clientlibs/aem-pwa-blog/logos/summit-logo.png',
];

function isInArray(string, array) {
    var cachePath;
    if (string.indexOf(self.origin) === 0) {
        console.log('matched ', string);
        cachePath = string.substring(self.origin.length);
    } else {
        cachePath = string;
    }
    return array.indexOf(cachePath) > -1;
}


var channel = new BroadcastChannel('sw-messages');


/*
 =============================================================================================

 Exercise 03 : Caching the App shell
 -----------
 Copy the code from this file : /apps/aem-pwa-blog/code-snippets/exercise-03/ex03-code-to-paste-static.txt
 below this commented block  :

 =============================================================================================
 */
self.addEventListener('install', function (event) {
    console.log('[TL30-PWA][install] Installing Service Worker ...', event);

});
/*
 =============================================================================================

 Exercise 03 : Deleting old caches when a newer version is being installed
 -----------
 Copy the code from this file : /apps/aem-pwa-blog/code-snippets/exercise-03/ex03-code-to-paste-delete.txt
 into the callback function : :

 =============================================================================================
 */
self.addEventListener('activate', function (event) {
    console.log('[TL30-PWA][activate] Activating Service Worker ....', event);

});


/*
 =============================================================================================

 Exercise 03 : Adding request to a dynamic cache
 -----------
 Copy the code from this file : /apps/aem-pwa-blog/code-snippets/exercise-03/ex03-code-to-paste-dynamic.txt
 into the callback function :

 =============================================================================================
 */
self.addEventListener('fetch', function (event) {
    console.log('[TL30-PWA][fetch] >>>>> Catching an HTTP  request ['+event.request.url+'] by the Service Worker ....');


});




/*
 =============================================================================================

 Exercise 04 : Adding request to a dynamic cache
 -----------
 Copy the code from this file : /apps/aem-pwa-blog/code-snippets/exercise-03/ex03-code-to-paste-dynamic.txt
 into the callback function :

 =============================================================================================
 */
self.addEventListener('push', function(event) {
    console.log('[TL30-PWA][push] Push Received.');
    /**
     ======================================================

     Exercise 04 : Push notifications
     -----------
     Copy the code from this file : /apps/aem-pwa-blog/code-snippets/exercise-04/ex04-code-to-paste-02.txt
     below this commented block  :

     ======================================================
     **/
    console.log('[TL30-PWA][push] Push had this data:'+ event.data.text());


});

/*
 =============================================================================================

 Exercise 05 : Using background synchronization
 -----------
 Copy the code from this file : /apps/aem-pwa-blog/code-snippets/exercise-05/ex05-code-to-paste-02.txt
 into the callback function :

 =============================================================================================
 */
self.addEventListener('sync', function(event) {
    console.log('[Service Worker] Background syncing', event);
// ===========================> CODE FROM ex04-code-to-paste-02.txt SHOULD BE PASTED BELOW <===========================

});




















