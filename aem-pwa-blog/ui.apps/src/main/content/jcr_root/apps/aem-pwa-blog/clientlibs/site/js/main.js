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

/**
 * Registering service worker
 * */

var deferredPrompt;

if (!window.Promise) {
    window.Promise = Promise;
}

if ('serviceWorker' in navigator) {
    console.log('[TL30-PWA] >>>>> Start service worker registering...');
    navigator.serviceWorker
        .register('/etc.clientlibs/aem-pwa-blog/clientlibs/sw.js')
        .then(function() {
            console.log('[TL30-PWA] <<<<<< Service worker registered!');
        });
}

window.addEventListener('beforeinstallprompt', function(event) {
    console.log('[TL30-PWA] >>>>> beforeinstallprompt has been  fired !');
    event.preventDefault();
    deferredPrompt = event;
    return false;
});

var promise = new Promise(function(resolve, reject) {
    setTimeout(function() {
        //resolve('This is executed once the timer is done!');
        reject({code: 500, message: 'An error occurred!'});
        //console.log('This is executed once the timer is done!');
    }, 3000);
});

var xhr = new XMLHttpRequest();
xhr.open('GET', 'https://httpbin.org/ip');
xhr.responseType = 'json';

xhr.onload = function() {
    console.log(xhr.response);
};

xhr.onerror = function() {
    console.log('Error!');
};

xhr.send();

fetch('https://httpbin.org/ip')
    .then(function(response) {
        console.log(response);
        return response.json();
    })
    .then(function(data) {
        console.log(data);
    })
    .catch(function(err) {
        console.log(err);
    });

fetch('https://httpbin.org/post', {
    method: 'POST',
    headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
    },
    mode: 'cors',
    body: JSON.stringify({message: 'Does this work?'})
})
    .then(function(response) {
        console.log(response);
        return response.json();
    })
    .then(function(data) {
        console.log(data);
    })
    .catch(function(err) {
        console.log(err);
    });

// promise.then(function(text) {
//   return text;
// }, function(err) {
//   console.log(err.code, err.message)
// }).then(function(newText) {
//   console.log(newText);
// });

promise.then(function(text) {
    return text;
}).then(function(newText) {
    console.log(newText);
}).catch(function(err) {
    console.log(err.code, err.message);
});

console.log('This is executed right after setTimeout()');
