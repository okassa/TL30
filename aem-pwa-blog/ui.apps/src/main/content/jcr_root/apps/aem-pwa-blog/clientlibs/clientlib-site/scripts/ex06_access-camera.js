/*!
 *  Adobe Summit EMEA 2019 - TL30 : Building a PWA with AEM
 *
 * @description :
 *                  This description must be updated
 *
 * @file : /apps/aem-pwa-blog/clientlibs/clientlib-site/scripts/ex07_access-camera.js
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
    var form = document.querySelector('form');
    var videoPlayer = document.querySelector('#player');
    var canvasElement = document.querySelector('#canvas');
    var captureButton = document.querySelector('#capture-btn');
    var imagePicker = document.querySelector('#image-picker');
    var picture;
    var createProfileButton = document.querySelector('.aem-pwa-blog__navLink--Profile');
    var submitProfileFormButton = document.querySelector('#submit');
    var shareImageButton = document.querySelector('#share-image-button');
    var createPostArea = document.querySelector('#create-post');
    var closeCreatePostModalButton = document.querySelector('#close-create-post-modal-btn');




window.AdobeSummit.Exercise07 =  {

    init:function () {

        if (captureButton){
            captureButton.addEventListener('click', function (event) {
                canvasElement.style.display = 'block';
                videoPlayer.style.display = 'none';
                captureButton.style.display = 'none';
                //var context = canvasElement.getContext('2d');
                //context.drawImage(videoPlayer, 0, 0, canvas.width, videoPlayer.videoHeight / (videoPlayer.videoWidth / canvas.width));
                videoPlayer.srcObject.getVideoTracks().forEach(function (track) {
                    track.stop();
                });
                picture = dataURItoBlob(canvasElement.toDataURL());
            });
        }

        if(createPostArea) {
            createPostArea.style.display = 'none';
        }

        if(shareImageButton) {
            shareImageButton.addEventListener('click', function(){window.AdobeSummit.openCreatePostModal()});
        }

        if(closeCreatePostModalButton) {
            closeCreatePostModalButton.addEventListener('click', function(){window.AdobeSummit.closeCreatePostModal()});
        }

        if (imagePicker){
            imagePicker.addEventListener('change', function (event) {
                picture = event.target.files[0];
            });
        }

        if(submitProfileFormButton) {
            submitProfileFormButton.addEventListener('click', function(event) {

                event.preventDefault();

                var path= $("#form").attr("path");
                var firstName= $('#firstName').val();
                var lastName= $('#lastName').val();
                var email= $('#email').val();
                var password= $('#password').val();
                var hobbies= $('#hobbies').val();

                //  @TODO use fetch instead

                fetch(path, {
                    method: 'POST',
                    body: JSON.stringify({
                        "firstName": firstName,
                        "lastName": lastName,
                        "email": email,
                        "password": firstName,
                        "hobbies": hobbies,
                    })
                })
                    .then(function (data) {
                        console.log('Request success: ', data);
                    })
                    .catch(function (error) {
                        console.log('Request failure: ', error);
                    });
            });
        }
    }
}
}(window, navigator, document));