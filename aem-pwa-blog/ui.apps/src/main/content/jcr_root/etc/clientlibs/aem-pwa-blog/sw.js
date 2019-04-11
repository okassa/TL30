importScripts('/etc.clientlibs/aem-pwa-blog/clientlibs/clientlib-firebase.js')

var CACHE_STATIC_NAME = 'static-v4';
var CACHE_DYNAMIC_NAME = 'dynamic-v2';

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
/**
 * The install event is fired only once
 */
self.addEventListener('install', function(event) {
    // don't wait if there is a newer version of the service worker.
    self.skipWaiting();

    console.log('[TL30-PWA] >>>>> Installing Service Worker ...', event);
    /**
     * The event.waitUntil function in the push event tells the service worker not to close until the event is finished.
     */
    event.waitUntil(
        caches.open(CACHE_STATIC_NAME)
            .then(function(cache) {
                console.log('[TL30-PWA][install] >>>>> Precaching App Shell');
                cache.addAll([
                    '/content/aem-pwa-blog/home.html',
                    '/content/aem-pwa-blog/post.html',
                    '/content/aem-pwa-blog/profile.html',
                    '/content/aem-pwa-blog/login.html',
                    '/etc.clientlibs/aem-pwa-blog/clientlibs/clientlib-vendor.js',
                    '/etc.clientlibs/aem-pwa-blog/clientlibs/clientlib-firebase.js',
                    '/etc.clientlibs/aem-pwa-blog/clientlibs/clientlib-base.js',
                    '/etc/clientlibs/aem-pwa-blog/fonts-awesome/fontawesome-webfont.woff2?v=4.7.0',
                    '/etc.clientlibs/aem-pwa-blog/clientlibs/clientlib-base.css',
                    '/etc.clientlibs/aem-pwa-blog/clientlibs/clientlib-vendor.css',
                    '/etc.clientlibs/core-components-examples/clientlibs/clientlib-themes/core-components-clean.css',
                    '/etc/clientlibs/aem-pwa-blog/logos/summit-logo-m.png',
                    '/etc/clientlibs/aem-pwa-blog/logos/summit-logo.png',
                    '/etc/clientlibs/aem-pwa-blog/icons/summit-icon-144x144.png',
                    '/content/aem-pwa-blog/home.offline.html',
                    '/etc/clientlibs/aem-pwa-blog/icons/favicon.ico'
                ]);
                console.log('[TL30-PWA][install] <<<<< The App Shell has been cached....');
            })
    )
});

self.addEventListener('activate', function(event) {
    console.log('[TL30-PWA][activate] >>>>> Activating Service Worker ....', event);
    event.waitUntil(
        caches.keys()
            .then(function(keyList) {
                return Promise.all(keyList.map(function(key) {
                    if (key !== CACHE_STATIC_NAME && key !== CACHE_DYNAMIC_NAME) {
                        console.log('[TL30-PWA][activate] >>>>> Removing old cache.', key);
                        return caches.delete(key);
                        console.log('[TL30-PWA][activate] <<<<< Removed the old cache....', key);
                    }
                }));
            })
    );
    console.log('[TL30-PWA][activate] <<<<< The Service Worker has been activated....', event);
    return self.clients.claim();
});

self.addEventListener('fetch', function(event) {
    event.respondWith(
        caches.match(event.request)
            .then(function(response) {
                console.log('[TL30-PWA][fetch] >>>>> Catching an HTTP request by the Service Worker ....');
                if (response) {
                    console.log('[TL30-PWA][fetch] Catching an HTTP request by the Service Worker ....');
                    return response;
                } else {
                    console.log('[TL30-PWA][fetch] Executing the real HTTP request by the Service Worker ....');
                    return fetch(event.request)
                        .then(function(res) {
                            return caches.open(CACHE_DYNAMIC_NAME)
                                .then(function(cache) {
                                    cache.put(event.request.url, res.clone());
                                    return res;
                                })
                        })
                        .catch(function(err) {
                            console.error('[TL30-PWA][fetch] HTTP request execution by the Service Worker failed ....');
                        });
                }
                console.log('[TL30-PWA][fetch] <<<<< The HTTP request has been handled by the Service Worker properly ....');
            })
    );
});

/*
 Retrieve an instance of Firebase Messaging so that it can handle background messages.
 */
self.addEventListener('push', function(event) {
    console.log('[Service Worker] Push Received.');
    console.log('[Service Worker] Push had this data:'+ event.data.text());

    const title = 'Adobe Experience Manager <3 PWA';

    /**
     *
     var options = {
        "body":   "Time is up!",
        "icon":   "/images/manifest/icon-96x96.png",
        "badge":  "/images/notification/badge-96x96.png",
        "vibrate": [500,100,500,100,500,100,500],
        "tag": 'renotify',
        "renotify": true
      };
     *
     *
     * **/
    const options = {
        body: event.data.text(),
        icon: '/etc/clientlibs/aem-pwa-blog/images/aem-logo-6.3.png',
        badge: '/etc/clientlibs/aem-pwa-blog/images/aem-logo-6.3.png'
    };

    event.waitUntil(self.registration.showNotification(title, options));
});

self.addEventListener('notificationclick', function(event) {
    console.log('[Service Worker] Notification click Received.');

    event.notification.close();

    event.waitUntil(
        clients.openWindow('https://developers.google.com/web/')
    );
});



