importScripts('/etc.clientlibs/aem-pwa-blog/clientlibs/clientlib-utils.js');
importScripts('/etc.clientlibs/aem-pwa-blog/clientlibs/clientlib-firebase.js');

var CACHE_STATIC_NAME = 'static-v40';
var CACHE_DYNAMIC_NAME = 'dynamic-v3';
var STATIC_FILES = [
    '/content/aem-pwa-blog/post.html',
    '/content/aem-pwa-blog/login.html',
    '/content/aem-pwa-blog/home.html',
    '/content/aem-pwa-blog/home.offline.html',
    '/content/aem-pwa-blog/profile.html',
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
];

var channel = new BroadcastChannel('sw-messages');


/*
 ================================================== Exercise 02 : (2) Registering service worker  ========================================================
 =
 =  When window.AdobeSummit.Exercise02.init() method is called the service worker is registered and an install event is caught by the service
 =  worker.The install event is fired only once
 =
 ================================================================================================================================================
 */
self.addEventListener('install', function (event) {
    console.log('[Service Worker] Installing Service Worker ...', event);
    event.waitUntil(
        caches.open(CACHE_STATIC_NAME)
            .then(function (cache) {
                console.log('[Service Worker] Precaching App Shell');
                cache.addAll(STATIC_FILES);
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
self.addEventListener('activate', function (event) {
    console.log('[Service Worker] Activating Service Worker ....', event);
    event.waitUntil(
        caches.keys()
            .then(function (keyList) {
                return Promise.all(keyList.map(function (key) {
                    if (key !== CACHE_STATIC_NAME && key !== CACHE_DYNAMIC_NAME) {
                        console.log('[Service Worker] Removing old cache.', key);
                        return caches.delete(key);
                    }
                }));
            })
    );
    return self.clients.claim();
});

function isInArray(string, array) {
    var cachePath;
    if (string.indexOf(self.origin) === 0) { // request targets domain where we serve the page from (i.e. NOT a CDN)
        console.log('matched ', string);
        cachePath = string.substring(self.origin.length); // take the part of the URL AFTER the domain (e.g. after localhost:8080)
    } else {
        cachePath = string; // store the full request (for CDNs)
    }
    return array.indexOf(cachePath) > -1;
}
/*

 ================================================== Exercise 04 : (2) Caching app shell  ========================================================
 =
 =  We cache only dynamic  resources such html pages, json payload fron ajax requests...
 =
 ================================================================================================================================================
 */
self.addEventListener('fetch', function (event) {
    console.log('[TL30-PWA][fetch] >>>>> Catching an HTTP  request ['+event.request.url+'] by the Service Worker ....');
    if (isInArray(event.request.url, STATIC_FILES)) {
        console.log('[TL30-PWA][fetch] The HTTP request ['+event.request.url+'] is available in '+CACHE_STATIC_NAME+', it will then be used by the Service Worker ....');
        event.respondWith(
            caches.match(event.request)
        );
    } else {
        event.respondWith(
            caches.match(event.request)
                .then(function (response) {
                    if (response) {
                        console.log('[TL30-PWA][fetch] The HTTP request ['+event.request.url+'] has been looked for in all caches by the Service Worker and a match has been found....');
                        return response;
                    } else {
                            return fetch(event.request)
                                .then(function (res) {
                                    if(event.request.url.indexOf("authenticated") <= -1){
                                        return caches.open(CACHE_DYNAMIC_NAME)
                                            .then(function (cache) {
                                                console.log('[TL30-PWA][fetch] The  HTTP request ['+event.request.url+'] has been added to '+CACHE_DYNAMIC_NAME+' by the Service Worker ....');

                                                cache.put(event.request.url, res.clone());
                                                return res;


                                            })
                                    }else{
                                        if (!res.ok) {
                                            return cache.match('/content/aem-pwa-blog/home.offline.html');
                                        }else{
                                            return res;
                                        }
                                    }
                                })
                                .catch(function (err) {
                                    console.log('[TL30-PWA][fetch] The real HTTP request ['+event.request.url+'] failed, the Service Worker will display the offline page....');
                                    return caches.open(CACHE_STATIC_NAME)
                                        .then(function (cache) {
                                            if (event.request.headers.get('accept').includes('text/html')) {
                                                return cache.match('/content/aem-pwa-blog/home.offline.html');
                                            }
                                        });
                                });


                    }
                })
        );
    }
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

    // ===========================> CODE FROM ex04-code-to-paste-02.txt SHOULD BE PASTED BELOW <===========================
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
                                            channel.postMessage({type:"synced-post"});
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

    // ===========================> CODE FROM ex05-code-to-paste-02.txt SHOULD BE PASTED BELOW <===========================
    console.log('[Service Worker] Push had this data:'+ event.data.text());

    var title = "AEM <3 PWA" ;
    var data = {type:"web-push-received",title: 'Adobe Experience Manager is PWA-ready !', content: 'A notification has been retrieved new happened!', openUrl: '/content/aem-pwa-blog/home.push.html'};

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
    channel.postMessage(data);

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



















