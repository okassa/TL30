
self.addEventListener('install', function(event) {
    console.log('[TL30-PWA] >>>>> Installing Service Worker ...', event);
});

self.addEventListener('activate', function(event) {
    console.log('[TL30-PWA] >>>>> Activating Service Worker ....', event);
    return self.clients.claim();
});

self.addEventListener('fetch', function(event) {
    console.log('[TL30-PWA] >>>>> Fetching something ....', event);
    event.respondWith(fetch(event.request));
});