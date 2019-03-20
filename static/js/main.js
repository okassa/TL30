
var deferredPrompt;

// Registering the service worker
if ('serviceWorker' in navigator){
    navigator.serviceWorker
        .register("/js/sw.js")
        .then(function (value) {
            console.log("[TL30-PWA] Service worker has been registered properly");
        }).catch(function (error) {
        console.log("[TL30-PWA] An error occured when registering the service worker - error "+error);
       });;
}

//
window.addEventListener("beforeinstallprompt",function (event) {
    console.log("[TL30-PWA] Before installing the application onto your smartphone, this event will be sent");
    event.preventDefault();
    deferredPrompt = event;
    return false;
});


var promise = new Promise(function (resolve,reject) {
    setTimeout(function () {
        reject({code:500,message:"An error occured!"});
    },3000);
});

promise
    .then(function (text) {
        return text;
    })
    .then(function (newText) {
        return newText;
    })
    .catch(function (error) {
        console.log("[TL30-PWA] An error occured - code "+error.code+" message : "+error.message);
    });