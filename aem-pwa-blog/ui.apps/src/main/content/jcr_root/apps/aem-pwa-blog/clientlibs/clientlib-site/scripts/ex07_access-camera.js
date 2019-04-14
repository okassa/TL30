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
    var viewPostArea = document.querySelector('#view-post');
    var closeCreatePostModalButton = document.querySelector('#close-create-post-modal-btn');
    var sharedMomentsArea = document.querySelector('#shared-moments');
    var titleInput = document.querySelector('#title');
    var tagsInput = document.querySelector('#tags');
    var imagePickerArea = document.querySelector('#pick-image');
    var locationBtn = document.querySelector('#location-btn');
    var locationLoader = document.querySelector('#location-loader');
    var tiles = document.querySelector('.aem-pwa-blog__tiles');
    var fetchedLocation = {lat: 0, lng: 0};
    var postButton = document.querySelector('#post-btn');



window.AdobeSummit.Exercise07 =  {

    init:function () {

        if (captureButton){
            captureButton.addEventListener('click', function (event) {
                canvasElement.style.display = 'block';
                videoPlayer.style.display = 'none';
                captureButton.style.display = 'none';
                var context = canvasElement.getContext('2d');
                //context.drawImage(videoPlayer, 0, 0, canvas.width, videoPlayer.videoHeight / (videoPlayer.videoWidth / canvas.width));
                videoPlayer.srcObject.getVideoTracks().forEach(function (track) {
                    track.stop();
                });
                picture = dataURItoBlob(canvasElement.toDataURL());
                //picture = canvasElement.toDataURL('image/png');
            });
        }

        if(createPostArea) {
            createPostArea.style.display = 'none';
        }

        if(postButton) {
            postButton.addEventListener('click', function(event) {

                var path = $("#formPost").attr("path");
                var title= titleInput.value;
                var tags= tagsInput.value;
                var file= picture;

                /*$.post(path, { 'title': title, 'tags': tags, 'file': file})
                    .done(function(msg) {
                        alert( "success"+msg.responseText );
                    })
                    .fail(function(msg) {
                        alert( "error"+msg.responseText );
                    })
                    .always(function(msg) {
                        alert( "complete"+msg.responseText );
                    });*/
                    
                var data = new FormData();
                data.append('title', titleInput.value);
                data.append('tags', tagsInput.value);
                data.append('file', file);

                $.ajax({
                    type: "POST",
                    url: path,
                    data: data,
                    processData: false,
                    contentType: false,
                    mimeType:"multipart/form-data"
                })
                .done(function(msg) {
                    alert( "success"+msg.responseText );
                })
                .fail(function(msg) {
                    alert( "error"+msg.responseText );
                })
                .always(function(msg) {
                    alert( "complete"+msg.responseText );
                });
            });
        }

        if(locationBtn) {
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

        if(createProfileButton) {
            createProfileButton.addEventListener('click', window.AdobeSummit.initializeMedia());
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

                $.ajax({
                    url: path,
                    type: 'POST',
                    data: 'firstName='+ firstName+'&lastName='+ lastName+'&email='+ email+'&password='+ password+'&hobbies='+ hobbies,
                    success: function(msg){
                        alert(msg.responseText); //display the data returned by the servlet
                    },
                    error: function(msg){
                        alert(msg.responseText); //display the data returned by the servlet
                    },
                })
                    .done(function(msg) {
                        alert( "success"+msg.responseText );
                    })
                    .fail(function(msg) {
                        alert( "error"+msg.responseText );
                    })
                    .always(function(msg) {
                        alert( "complete"+msg.responseText );
                    });
            });
        }
    }
}
}(window, navigator, document));