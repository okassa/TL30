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

var deferredPrompt;



/**
 * -----------------------------------------------------------------------
 * --
 * --                   Functions
 * --
 * -----------------------------------------------------------------------
 */

messaging.onMessage(function (payload) {
    console.log("[TL30-PWA][pushNotification] Message received. ", JSON.stringify(payload));
});
messaging.onTokenRefresh(function () {
    messaging.getToken()
        .then(function (refreshedToken) {
            console.log('[TL30-PWA][pushNotification] Token refreshed : '+refreshedToken);
        }).catch(function (err) {
        console.log('[TL30-PWA][pushNotification] Unable to retrieve refreshed token ', err);
    });
});

function pushNotificationInitializeUI(swRegistration) {

    messaging.useServiceWorker(swRegistration);

    var pushButton = document.querySelector('.js-push-btn');

    if(pushButton){
        pushButton.addEventListener('click', function() {
            messaging
                .requestPermission()
                .then(function () {
                    console.log("[TL30-PWA][pushNotification] Got notification permission");
                    // Send the token to the server to check it with validate_only
                    return messaging.getToken();
                })
                .then(function (token) {
                    // print the token on the HTML page
                    console.log("[TL30-PWA][pushNotification] Token", token);
                    // Retrieve user preferences from a dataLayer
                    var topic = "sport";
                    //Suscribe to user topics
                    fetch('/bin/aem-pwa-blog/notifications.json', {
                        'method': 'POST',
                        'headers': {
                            'Content-Type': 'application/json'
                        },
                        'body': JSON.stringify({'token': token})
                    }).then(function(response) {
                        console.log("[TL30-PWA][pushNotification] Subscription to "+topic+" has responded with :"+response);
                    }).catch(function(error) {
                        console.error("[TL30-PWA][pushNotification] Subscription to the topic "+topic+" failed" +error);
                    })
                })
                .catch(function (err) {
                    console.log("[TL30-PWA][pushNotification] Didn't get notification permission", err);
                });
        });
    }


}