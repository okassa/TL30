/*
 *  Copyright 2018 Adobe Systems Incorporated
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

var deferredPrompt;

if (!window.Promise) {
    window.Promise = Promise;
}

// Initialize Firebase
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

//Push notification button
var enableNotificationsButtons = document.querySelectorAll('.enable-notifications');

/**
 * Step one: run a function on load (or whenever is appropriate for you)
 * Function run on load sets up the service worker if it is supported in the
 * browser. Requires a serviceworker in a `sw.js`. This file contains what will
 * happen when we receive a push notification.
 * If you are using webpack, see the section below.
 */
if ('serviceWorker' in navigator) {
    window.addEventListener('load', function() {
        navigator.serviceWorker.register('/content/aem-pwa-blog/sw.js',{ scope: '/content/aem-pwa-blog/en' })
            .then(function(registration) {
            // Registration was successful
            console.log('[TL30-PWA] >>>>> ServiceWorker registration successful with scope: ', registration.scope);
        }, function(err) {
            // registration failed :(
            console.log('[TL30-PWA] >>>>> ServiceWorker registration failed: ', err);
        })
            // Intializing the notifications

    });
}else {
    console.warn('Service workers are not supported in this browser.');
}

window.addEventListener('beforeinstallprompt', function(event) {
    console.log('[TL30-PWA] >>>>> beforeinstallprompt has been  fired !');
    event.preventDefault();
    deferredPrompt = event;
    return false;
});



function displayConfirmNotification() {
    if ('serviceWorker' in navigator) {
        var options = {
            body: 'You successfully subscribed to our Notification service!',
            icon: '/etc/clientlibs/aem-pwa-blog/icons/summit-icon-96x96.png',
            image: '/src/images/image-to-upload.jpg',
            dir: 'ltr',
            lang: 'en-US', // BCP 47,
            vibrate: [100, 50, 200],
            badge: '/etc/clientlibs/aem-pwa-blog/icons/summit-icon-96x96.png',
            tag: 'confirm-notification',
            renotify: true,
            actions: [
                { action: 'confirm', title: 'Okay', icon: '/etc/clientlibs/aem-pwa-blog/icons/summit-icon-96x96.png' },
                { action: 'cancel', title: 'Cancel', icon: '/etc/clientlibs/aem-pwa-blog/icons/summit-icon-96x96.png' }
            ]
        };

        navigator.serviceWorker.ready
            .then(function(swreg) {
                swreg.showNotification('Successfully subscribed!', options);
            });
    }
}

function configurePushSub() {
    if (!('serviceWorker' in navigator)) {
        return;
    }

    var reg;
    navigator.serviceWorker.ready
        .then(function(swreg) {
            reg = swreg;
            return swreg.pushManager.getSubscription();
        })
        .then(function(sub) {
            if (sub === null) {
                // Create a new subscription
                var vapidPublicKey = 'AAAARHhd_lA:APA91bFLeYH7sTFNEi8n2ZbJWF3GknElELMiOGGKeELz6X7IunwtFiMWW8tiGGXJkT_g2GumufAEzj3ksdi5J59NMdUDo38tfY_Y_Lp9AKfSORkTllDjVzZqF1EgIehMs86aPitwFgSd';
                var convertedVapidPublicKey = urlBase64ToUint8Array(vapidPublicKey);
                return reg.pushManager.subscribe({
                    userVisibleOnly: true,
                    applicationServerKey: vapidPublicKey
                });
            } else {
                // We have a subscription
            }
        })
        .then(function(newSub) {
            return fetch('/content/aem-pwa-blog/en/_jcr_content.subscription.json', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                },
                body: JSON.stringify(newSub)
            })
        })
        .then(function(res) {
            if (res.ok) {
                displayConfirmNotification();
            }
        })
        .catch(function(err) {
            console.log(err);
        });
}

function askForNotificationPermission() {
    Notification.requestPermission(function(result) {
        console.log('User Choice', result);
        if (result !== 'granted') {
            console.log('No notification permission granted!');
        } else {
            configurePushSub();
            // displayConfirmNotification();
        }
    });
}

if ('Notification' in window && 'serviceWorker' in navigator) {
    for (var i = 0; i < enableNotificationsButtons.length; i++) {
        enableNotificationsButtons[i].style.display = 'inline-block';
        enableNotificationsButtons[i].addEventListener('click', askForNotificationPermission);
    }
}