

if ('serviceWorker' in navigator){
    navigator.serviceWorker
        .register("/sw.js")
        .then(function (value) {
            console.log("[TL30-PWA] Service worker has been registered properly");
        });
}
