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

    var createPostArea = document.querySelector('#create-post');
    var closeCreatePostModalButton = document.querySelector('#close-create-post-modal-btn');
    var form = document.querySelector('form');
    var titleInput = document.querySelector('#title');
    var tagsInput = document.querySelector('#tags');
    var videoPlayer = document.querySelector('#player');
    var canvasElement = document.querySelector('#canvas');
    var captureButton = document.querySelector('#capture-btn');
    var imagePicker = document.querySelector('#image-picker');
    var imagePickerArea = document.querySelector('#pick-image');
    var picture;
    var tiles = document.querySelector('.aem-pwa-blog__tiles');
    var postButton = document.querySelector('#post-btn');



    window.AdobeSummit.Exercise05 = {


            init:function () {

                if(tiles){
                    var url = tiles.attributes["data-async-url"].value;
                    var networkDataReceived = false;

                    fetch(url)
                        .then(function (res) {
                            return res.json();
                        })
                        .then(function (data) {
                            networkDataReceived = true;
                            console.log('From web', data.teasers);
                            var dataArray = [];
                            for (var key in data.teasers) {
                                dataArray.push(data.teasers[key]);
                            }
                            AdobeSummit.updateUI(dataArray);
                        });

                    if ('indexedDB' in window) {
                        readAllData('posts')
                            .then(function (data) {
                                if (!networkDataReceived) {
                                    console.log('From cache', data);
                                    AdobeSummit.updateUI(data.teasers);
                                }
                            });
                    }
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

                if(closeCreatePostModalButton){
                    closeCreatePostModalButton.addEventListener('click', window.AdobeSummit.closeCreatePostModal());
                }

                if(imagePicker){
                    imagePicker.addEventListener('change', function (event) {
                        picture = event.target.files[0];
                    });
                }

                if(postButton) {
                    postButton.addEventListener('click', function(event) {

                        event.preventDefault();

                        if (titleInput.value.trim() === '' || tagsInput.value.trim() === '') {
                            alert('Please enter valid data!');
                            return;
                        }

                        window.AdobeSummit.closeCreatePostModal();

                        if ('serviceWorker' in navigator && 'SyncManager' in window) {
                            navigator.serviceWorker.ready
                                .then(function (sw) {
                                    var post = {
                                        id: new Date().toISOString(),
                                        title: titleInput.value,
                                        tags:tagsInput.value,
                                        file: canvasElement.toDataURL(),
                                    };
                                    writeData('sync-posts', post)
                                        .then(function () {
                                            return sw.sync.register('sync-new-posts');
                                        })
                                        .then(function () {
                                            $("#background-sync").modal("show");
                                            console.log("sync registered");
                                        })
                                        .catch(function (err) {
                                            console.log(err);
                                        });
                                    debugger;
                                });
                        } else {
                            AdobeSummit.sendData();
                        }
                    });
                }
                }


        }

}(window, navigator, document));