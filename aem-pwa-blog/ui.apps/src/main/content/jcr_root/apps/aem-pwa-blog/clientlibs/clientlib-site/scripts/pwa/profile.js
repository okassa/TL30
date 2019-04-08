var videoPlayer = document.querySelector('#player');
var canvasElement = document.querySelector('#canvas');
var captureButton = document.querySelector('#capture-btn');
var imagePicker = document.querySelector('#image-picker');
var imagePickerArea = document.querySelector('#pick-image');
var createProfileButton = document.querySelector('.aem-pwa-blog__navLink--Profile');
var picture;

function initializeMedia() {
    if (!('mediaDevices' in navigator)) {
        navigator.mediaDevices = {};
    }

    if (!('getUserMedia' in navigator.mediaDevices)) {
        navigator.mediaDevices.getUserMedia = function (constraints) {
            var getUserMedia = navigator.webkitGetUserMedia || navigator.mozGetUserMedia;

            if (!getUserMedia) {
                return Promise.reject(new Error('getUserMedia is not implemented!'));
            }

            return new Promise(function (resolve, reject) {
                getUserMedia.call(navigator, constraints, resolve, reject);
            });
        }
    }

    navigator.mediaDevices.getUserMedia({video: true})
        .then(function (stream) {
            videoPlayer.srcObject = stream;
            videoPlayer.style.display = 'block';
        })
        .catch(function (err) {
            imagePickerArea.style.display = 'block';
        });
}

if (captureButton){
    captureButton.addEventListener('click', function (event) {
        canvasElement.style.display = 'block';
        videoPlayer.style.display = 'none';
        captureButton.style.display = 'none';
        var context = canvasElement.getContext('2d');
        context.drawImage(videoPlayer, 0, 0, canvas.width, videoPlayer.videoHeight / (videoPlayer.videoWidth / canvas.width));
        videoPlayer.srcObject.getVideoTracks().forEach(function (track) {
            track.stop();
        });
        picture = dataURItoBlob(canvasElement.toDataURL());
    });
}

if (imagePicker){
    imagePicker.addEventListener('change', function (event) {
        picture = event.target.files[0];
    });
}

if(createProfileButton) {
  createProfileButton.addEventListener('click', initializeMedia);
}
