/*!
 *  Adobe Summit EMEA 2019 - TL30 : Building a PWA with AEM
 *
 * @description :
 *                  This description must be updated
 *
 * @file : /apps/aem-pwa-blog/clientlibs/clientlib-site/scripts/ex05_background-sync.js
 *
 *
 *
 * @project Adobe Summit EMEA - TL30
 * @date 2019-05-15
 * @author Olympe KASSA, Adobe <kassa@adobe.com>
 * @licensor Adobe
 * @site none
 *
 */
;
(function (window, navigator, document) {
    'use strict';

    var shareImageButton = document.querySelector('#share-image-button');
    var createPostArea = document.querySelector('#create-post');
    var viewPostArea = document.querySelector('#view-post');
    var closeCreatePostModalButton = document.querySelector('#close-create-post-modal-btn');
    var sharedMomentsArea = document.querySelector('#shared-moments');
    var form = document.querySelector('form');
    var titleInput = document.querySelector('#title');
    var locationInput = document.querySelector('#location');
    var videoPlayer = document.querySelector('#player');
    var canvasElement = document.querySelector('#canvas');
    var captureButton = document.querySelector('#capture-btn');
    var imagePicker = document.querySelector('#image-picker');
    var imagePickerArea = document.querySelector('#pick-image');
    var picture;
    var locationBtn = document.querySelector('#location-btn');
    var locationLoader = document.querySelector('#location-loader');
    var fetchedLocation = {lat: 0, lng: 0};


    window.AdobeSummit.Exercise05 = {


            init:function () {

                if(shareImageButton){
                    shareImageButton.addEventListener('click', window.AdobeSummit.openCreatePostModal(createPostArea,canvasElement,viewPostArea));
                }

                if(captureButton){
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

                if(createPostArea) {
                    createPostArea.style.display = 'none';
                }
                if (locationBtn){
                    locationBtn.addEventListener('click', function (event) {
                        if (!('geolocation' in navigator)) {
                            return;
                        }
                        var sawAlert = false;

                        locationBtn.style.display = 'none';
                        locationLoader.style.display = 'block';

                        navigator.geolocation.getCurrentPosition(function (position) {
                            locationBtn.style.display = 'inline';
                            locationLoader.style.display = 'none';
                            fetchedLocation = {lat: position.coords.latitude, lng: 0};
                            locationInput.value = 'In Munich';
                            document.querySelector('#manual-location').classList.add('is-focused');
                        }, function (err) {
                            console.log(err);
                            locationBtn.style.display = 'inline';
                            locationLoader.style.display = 'none';
                            if (!sawAlert) {
                                alert('Couldn\'t fetch location, please enter manually!');
                                sawAlert = true;
                            }
                            fetchedLocation = {lat: 0, lng: 0};
                        }, {timeout: 7000});
                    });
                }



                if(closeCreatePostModalButton){
                    closeCreatePostModalButton.addEventListener('click', window.AdobeSummit.closeCreatePostModal(imagePickerArea,videoPlayer,canvasElement,locationBtn,
                        locationLoader,captureButton,createPostArea,viewPostArea));
                }

                if(imagePicker){
                    imagePicker.addEventListener('change', function (event) {
                        picture = event.target.files[0];
                    });
                }


                if(form){
                    form.addEventListener('submit', function (event) {
                        event.preventDefault();

                        if (titleInput.value.trim() === '' || locationInput.value.trim() === '') {
                            alert('Please enter valid data!');
                            return;
                        }

                        window.AdobeSummit.closeCreatePostModal(imagePickerArea,videoPlayer,canvasElement,locationBtn,
                            locationLoader,captureButton,createPostArea,viewPostArea);

                        if ('serviceWorker' in navigator && 'SyncManager' in window) {
                            navigator.serviceWorker.ready
                                .then(function (sw) {
                                    var post = {
                                        id: new Date().toISOString(),
                                        title: titleInput.value,
                                        location: locationInput.value,
                                        picture: picture,
                                        rawLocation: fetchedLocation
                                    };
                                    writeData('sync-posts', post)
                                        .then(function () {
                                            return sw.sync.register('sync-new-posts');
                                        })
                                        .then(function () {
                                            var snackbarContainer = document.querySelector('#confirmation-toast');
                                            var data = {message: 'Your Post was saved for syncing!'};
                                            snackbarContainer.MaterialSnackbar.showSnackbar(data);
                                        })
                                        .catch(function (err) {
                                            console.log(err);
                                        });
                                });
                        } else {
                            this.sendData();
                        }
                    });
                }
            }
        }

}(window, navigator, document));