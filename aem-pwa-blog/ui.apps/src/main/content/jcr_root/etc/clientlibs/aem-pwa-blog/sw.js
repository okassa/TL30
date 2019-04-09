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

self.addEventListener('install', function(event) {
    console.log('[TL30-PWA] >>>>> Installing Service Worker ...', event);
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

self.addEventListener('push', function(event) {
    console.log('[Service Worker] Push Received.');
    console.log('[Service Worker] Push had this data:'+ event.data.text());

    const title = 'Adobe Experience Manager <3 PWA';
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

self.addEventListener('pushsubscriptionchange', function(event) {
    console.log('[Service Worker]: \'pushsubscriptionchange\' event fired.');
    const applicationServerKey = urlB64ToUint8Array(applicationServerPublicKey);
    event.waitUntil(
        self.registration.pushManager.subscribe({
            userVisibleOnly: true,
            applicationServerKey: applicationServerKey
        })
            .then(function(newSubscription) {
                // TODO: Send to application server
                console.log('[Service Worker] New subscription: ', newSubscription);
            })
    );
});

/*
 Retrieve an instance of Firebase Messaging so that it can handle background messages.
 */
messaging.setBackgroundMessageHandler(function (payload) {
    console.log('[firebase-messaging-sw.js] Received background message ', payload);
    const notification = JSON.parse(payload.data.notification);
    // Customize notification here
    const notificationTitle = notification.title;
    const notificationOptions = {
        body: notification.body,
        icon: notification.icon
    };

    return self.registration.showNotification(notificationTitle,
        notificationOptions);
});