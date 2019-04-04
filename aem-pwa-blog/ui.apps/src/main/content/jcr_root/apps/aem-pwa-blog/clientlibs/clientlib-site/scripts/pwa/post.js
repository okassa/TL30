var shareImageButton = document.querySelector('#share-image-button');
var createPostArea = document.querySelector('#create-post');
var closeCreatePostModalButton = document.querySelector('#close-create-post-modal-btn');

var form = document.querySelector('form');
var titleInput = document.querySelector('#title');
var locationInput = document.querySelector('#location');

var locationBtn = document.querySelector('#location-btn');
var locationLoader = document.querySelector('#location-loader');
var tiles = document.querySelector('.aem-pwa-blog__tiles');
var fetchedLocation = {lat: 0, lng: 0};



function openCreatePostModal() {
    setTimeout(function () {
        createPostArea.style.transform = 'translateY(0)';
    }, 1);


    if (deferredPrompt) {
        deferredPrompt.prompt();

        deferredPrompt.userChoice.then(function (choiceResult) {
            console.log(choiceResult.outcome);

            if (choiceResult.outcome === 'dismissed') {
                console.log('User cancelled installation');
            } else {
                console.log('User added to home screen');
            }
        });

        deferredPrompt = null;
    }
}

// Currently not in use, allows to save assets in cache on demand otherwise
function onSaveButtonClicked(event) {
    console.log('clicked');
    if ('caches' in window) {
        caches.open('user-requested')
            .then(function (cache) {
                cache.add('https://httpbin.org/get');
                cache.add('/src/images/sf-boat.jpg');
            });
    }
}

function closeCreatePostModal() {
    imagePickerArea.style.display = 'none';
    videoPlayer.style.display = 'none';
    canvasElement.style.display = 'none';
    locationBtn.style.display = 'inline';
    locationLoader.style.display = 'none';
    captureButton.style.display = 'inline';
    if (videoPlayer.srcObject) {
        videoPlayer.srcObject.getVideoTracks().forEach(function (track) {
            track.stop();
        });
    }
    setTimeout(function () {
        createPostArea.style.transform = 'translateY(100vh)';
    }, 1);
}

if(shareImageButton){
    shareImageButton.addEventListener('click', openCreatePostModal);
}

if(closeCreatePostModalButton){
    closeCreatePostModalButton.addEventListener('click', closeCreatePostModal);
}





