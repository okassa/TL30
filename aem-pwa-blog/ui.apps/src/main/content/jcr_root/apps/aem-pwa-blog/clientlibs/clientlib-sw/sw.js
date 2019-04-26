importScripts('/etc.clientlibs/aem-pwa-blog/clientlibs/clientlib-utils.js');
importScripts('/etc.clientlibs/aem-pwa-blog/clientlibs/clientlib-firebase.js');

// ==============> TO INCREASE AFTER EACH AND EVERY MODIFICATION IN THE SERVICE WORKER <=================
var VERSION=879;

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
 Copy the code from this file : /apps/aem-pwa-blog/config.exercise-03/ex03-code-to-paste-static.txt
 below this commented block  :

 =============================================================================================
 */
self.addEventListener('install', function (event) {
    console.log('[TL30-PWA][install] Installing Service Worker ...', event);
    event.waitUntil(
        caches.open(CACHE_STATIC_NAME)
            .then(function (cache) {
                console.log('[Service Worker] Precaching App Shell');
                cache.addAll(STATIC_FILES);
            })
    )
});
/*
 =============================================================================================

 Exercise 03 : Deleting old caches when a newer version is being installed
 -----------
 Copy the code from this file : /apps/aem-pwa-blog/config.exercise-03/ex03-code-to-paste-delete.txt
 into the callback function : :

 =============================================================================================
 */
self.addEventListener('activate', function (event) {
    console.log('[TL30-PWA][activate] Activating Service Worker ....', event);
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


/*
 =============================================================================================

 Exercise 03 : Adding request to a dynamic cache
 -----------
 Copy the code from this file : /apps/aem-pwa-blog/config.exercise-03/ex03-code-to-paste-dynamic.txt
 into the callback function :

 =============================================================================================
 */
self.addEventListener('fetch', function (event) {
    console.log('[TL30-PWA][fetch] >>>>> Catching an HTTP  request ['+event.request.url+'] by the Service Worker ....');
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
                                        if(event.request.url.indexOf(".model.json") <= -1){
                                            cache.put(event.request.url, res.clone());
                                        }

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
    )

});




/*
 =============================================================================================

 Exercise 04 : Adding request to a dynamic cache
 -----------
 Copy the code from this file : /apps/aem-pwa-blog/config.exercise-03/ex03-code-to-paste-dynamic.txt
 into the callback function :

 =============================================================================================
 */
self.addEventListener('push', function(event) {
    console.log('[TL30-PWA][push] Push Received.');
    /**
     ======================================================

     Exercise 04 : Push notifications
     -----------
     Copy the code from this file : /apps/aem-pwa-blog/code-snippets/exercise-04/ex04-code-to-paste-01.txt
     below this commented block  :

     ======================================================
     **/
    console.log('[TL30-PWA][push] Push had this data:'+ event.data.text());

    var title = "AEM <3 PWA" ;
    var data = {type:"web-push-received",title: 'Adobe Experience Manager is PWA-ready !', content: 'Your subscription to web push notifications is successful. You will then receive notifications from AEM.', openUrl: '/content/aem-pwa-blog/home.push.html'};

    if (event.data) {
        data.content = JSON.parse(event.data.text());
    }

    const options = {
        body: 'Your subscription to web push notifications is successful. You will then receive notifications from AEM.',
        icon: '/etc/clientlibs/aem-pwa-blog/images/aem-logo-6.3.png',
        badge: '/etc/clientlibs/aem-pwa-blog/images/aem-logo-6.3.png',
        data: {
            url: data.openUrl
        }
    };

    event.waitUntil(self.registration.showNotification(title, options));
    channel.postMessage(data);
});

/*
 =============================================================================================

 Exercise 05 : Using background synchronization
 -----------
 Copy the code from this file : /apps/aem-pwa-blog/config.exercise-04/ex05-code-to-paste-02.txt
 into the callback function :

 =============================================================================================
 */
self.addEventListener('sync', function(event) {
    console.log('[Service Worker] Background syncing', event);
// ===========================> CODE FROM ex04-code-to-paste-02.txt SHOULD BE PASTED BELOW <===========================
    if (event.tag === 'sync-new-posts') {
        console.log('[TL30-PWA][sync]  Syncing new Posts');
        event.waitUntil(
            readAllData('sync-posts')
                .then(function(data) {
                    for (var dt in data) {

                        fetch('/content/aem-pwa-blog/post.share-moment.json', {
                            method: 'POST',
                            body: JSON.stringify({
                                'id': data[dt].id,
                                'title': data[dt].title,
                                'tags': data[dt].tags,
                                'file': data[dt].file
                            })
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




















