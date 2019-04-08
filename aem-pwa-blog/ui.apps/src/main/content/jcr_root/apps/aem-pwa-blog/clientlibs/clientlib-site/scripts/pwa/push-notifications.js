

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
    if ('Notification' in window && 'serviceWorker' in navigator) {
        Notification.requestPermission(function(result) {
            console.log('User Choice', result);
            if (result !== 'granted') {
                console.log('No notification permission granted!');
            } else {
                configurePushSub();
                displayConfirmNotification();
            }
        });
    }
}

