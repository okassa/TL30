importScripts('/etc.clientlibs/aem-pwa-blog/clientlibs/clientlib-utils.js');
importScripts('/etc.clientlibs/aem-pwa-blog/clientlibs/clientlib-firebase.js');

var version = "160419";

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
    /**
     * The event.waitUntil function in the push event tells the service worker not to close until the event is finished.
     */
    event.waitUntil(
        caches.open(CACHE_STATIC_NAME)
            .then(function(cache) {
                /*

                 ================================================== Exercise 04 : (1) Caching app shell  ========================================================
                 =
                 =  We cache only static resources such as Js, CSS, icon, images and fonts
                 =
                 ================================================================================================================================================
                 */
                console.log('[TL30-PWA][install] >>>>> Precaching App Shell');
                cache.addAll([
                    '/etc.clientlibs/aem-pwa-blog/clientlibs/clientlib-vendor.js',
                    '/etc.clientlibs/aem-pwa-blog/clientlibs/clientlib-vendor-pwa.js',
                    '/etc.clientlibs/aem-pwa-blog/clientlibs/clientlib-firebase.js',
                    '/etc.clientlibs/aem-pwa-blog/clientlibs/clientlib-utils.js',
                    '/etc.clientlibs/aem-pwa-blog/clientlibs/clientlib-base.js',
                    '/etc/clientlibs/aem-pwa-blog/fonts-awesome/fontawesome-webfont.woff2?v=4.7.0',
                    '/etc.clientlibs/aem-pwa-blog/clientlibs/clientlib-base.css',
                    '/etc.clientlibs/aem-pwa-blog/clientlibs/clientlib-vendor.css',
                    '/etc.clientlibs/aem-pwa-blog/clientlibs/clientlib-firebase.css',
                    '/etc/clientlibs/aem-pwa-blog/logos/summit-logo-m.png',
                    '/etc/clientlibs/aem-pwa-blog/logos/summit-logo.png',
                    '/etc/clientlibs/aem-pwa-blog/icons/summit-icon-144x144.png',
                    '/etc/clientlibs/aem-pwa-blog/icons/favicon.ico'
                ]);
                console.log('[TL30-PWA][install] <<<<< The App Shell has been cached....');
            })
    )
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
    console.log('[TL30-PWA][activate] >>>>> Activating Service Worker ....', event);
    event.waitUntil(
        caches.keys()
            .then(function(keyList) {
                return Promise.all(keyList.map(function(key) {
                    /*
                     ================================================== Exercise 04 : (3) Caching app shell  ========================================================
                     =
                     =  We update the cache when a new version of the sw is being installed
                     =
                     ================================================================================================================================================
                     */
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

/*

 ================================================== Exercise 04 : (2) Caching app shell  ========================================================
 =
 =  We cache only dynamic  resources such html pages, json payload fron ajax requests...
 =
 ================================================================================================================================================
 */

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
                                    var url = event.request.url;
                                    if(url.indexOf("http://") > -1 || url.indexOf("https://") > -1){
                                        cache.put(event.request.url, res.clone());
                                        return res;
                                    }
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
 ================================================== Exercise 05 :  Background sync  ========================================================
 =
 =  When window.AdobeSummit.Exercise05.init() we register the sync manager to check network availability, when a sync event occured because
 = the network is back then all post that have been store within indexdb will be executed.
 =
 ================================================================================================================================================
 */
self.addEventListener('sync', function(event) {
    console.log('[TL30-PWA][sync] Background syncing', event);
    if (event.tag === 'sync-new-posts') {
        console.log('[TL30-PWA][sync]  Syncing new Posts');
        event.waitUntil(
            readAllData('sync-posts')
                .then(function(data) {
                    for (var dt in data) {
                        var postData = new FormData();
                        postData.append('id', data[dt].id);
                        postData.append('title', data[dt].title);
                        postData.append('tags', data[dt].tags);
                        postData.append('file', data[dt].file);

                        fetch('/bin/aem-pwa-blog/share-post.json', {
                            method: 'POST',
                            body: postData
                        })
                            .then(function(res) {
                                console.log('[TL30-PWA][sync] Sent data', res);
                                if (res.ok) {
                                    res.json()
                                        .then(function(resData) {
                                            deleteItemFromData('sync-posts', resData.id);
                                        });
                                }
                            })
                            .catch(function(err) {
                                console.log('[TL30-PWA][sync] Error while sending data', err);
                            });
                    }

                })
        );
    }
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
    console.log('[Service Worker] Push had this data:'+ event.data.text());

    var title = "AEM <3 PWA" ;
    var data = {title: 'Adobe Experience Manager is PWA-ready !', content: 'A notification has been retrieved new happened!', openUrl: '/'};

    if (event.data) {
        data.content = JSON.parse(event.data.text());
    }

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
        body: 'A notification has been retrieved new happened!',
        icon: '/etc/clientlibs/aem-pwa-blog/images/aem-logo-6.3.png',
        badge: '/etc/clientlibs/aem-pwa-blog/images/aem-logo-6.3.png',
        data: {
            url: data.openUrl
        }
    };

    event.waitUntil(self.registration.showNotification(title, options));
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





