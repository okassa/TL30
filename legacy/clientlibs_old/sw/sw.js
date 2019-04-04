var CACHE_STATIC_NAME = 'static-v4';
var CACHE_DYNAMIC_NAME = 'dynamic-v2';

self.addEventListener('install', function(event) {
    console.log('[TL30-PWA] >>>>> Installing Service Worker ...', event);
    event.waitUntil(
        caches.open(CACHE_STATIC_NAME)
            .then(function(cache) {
                console.log('[TL30-PWA] >>>>> Precaching App Shell');
               /* cache.addAll([
                    '/',
                    '/content/aem-pwa-blog/en.html',
                    '/etc.clientlibs/aem-pwa-blog/clientlibs/vendor.js',
                    '/etc.clientlibs/aem-pwa-blog/clientlibs/vendor-sw.js',
                    '/etc.clientlibs/aem-pwa-blog/clientlibs/base.js',
                    '/etc.clientlibs/aem-pwa-blog/clientlibs/vendor.css',
                    '/etc.clientlibs/aem-pwa-blog/clientlibs/base.css',
                    '/content/dam/aem-pwa-blog/static/summit-logo-m.png',
                    '/etc/clientlibs/aem-pwa-blog/icons/favicon.ico'
                ]);*/
            })
    )
});

self.addEventListener('activate', function(event) {
    console.log('[TL30-PWA] >>>>> Activating Service Worker ....', event);
    event.waitUntil(
        caches.keys()
            .then(function(keyList) {
                return Promise.all(keyList.map(function(key) {
                    if (key !== CACHE_STATIC_NAME && key !== CACHE_DYNAMIC_NAME) {
                        console.log('[TL30-PWA] >>>>> Removing old cache.', key);
                        return caches.delete(key);
                    }
                }));
            })
    );
    return self.clients.claim();
});

self.addEventListener('fetch', function(event) {
    event.respondWith(
        caches.match(event.request)
            .then(function(response) {
                if (response) {
                    return response;
                } else {
                    return fetch(event.request)
                        .then(function(res) {
                            return caches.open(CACHE_DYNAMIC_NAME)
                                .then(function(cache) {
                                    //cache.put(event.request.url, res.clone());
                                    return res;
                                })
                        })
                        .catch(function(err) {

                        });
                }
            })
    );
});