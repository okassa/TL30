
/*!
 *  Adobe Summit EMEA 2019 - TL30 : Building a PWA with AEM
 *
 * @description :
 *                  This needs to be updated
 *
 * @file : /apps/aem-pwa-blog/clientlibs/clientlib-site/js/global-init.js
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

    var viewPostArea = document.querySelector('#view-post');
    var sharedMomentsArea = document.querySelector('#shared-moments');
    var form = document.querySelector('form');
    var titleInput = document.querySelector('#title');
    var canvasElement = document.querySelector('#canvas');
    var createPostArea = document.querySelector('#create-post');
    var closeCreatePostModalButton = document.querySelector('#close-create-post-modal-btn');
    var videoPlayer = document.querySelector('#player');
    var captureButton = document.querySelector('#capture-btn');
    var shareImageButton = document.querySelector('#share-image-button');
    var submitProfileFormButton = document.querySelector('#submit');
    var picture;
    var tiles = document.querySelector('.aem-pwa-blog__tiles');
    var tileContent = document.querySelector('.aem-pwa-blog__tilesContent');

    // Init AdobeSummit namespace if not available.
    var AdobeSummit = window.AdobeSummit || {
            /**
             *
             *
             *
             */
            Constants : {
                SW_PATH:"/content/sw.js",
                SW_SCOPE:"/content/aem-pwa-blog"
            },
            initializeLocation : function(locationBtn) {
                if (!('geolocation' in navigator) && locationBtn) {
                    locationBtn.style.display = 'none';
                }
            },
            initializeMedia : function () {
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
                        if (videoPlayer){
                            videoPlayer.srcObject = stream;
                            videoPlayer.style.display = 'block';
                        }
                    })
                    .catch(function (err) {
                        console.log(err);
                    });
            },
            openCreatePostModal : function () {
                if(createPostArea){
                    createPostArea.style.display = 'block';
                }

                if(viewPostArea){
                    viewPostArea.style.display = 'none';
                }

                if(canvasElement){
                    canvasElement.style.display = 'none';
                    this.initializeMedia();
                }

                if(createPostArea){
                    setTimeout(function () {
                        createPostArea.style.transform = 'translateY(0)';
                    }, 1);
                }

            },
            closeCreatePostModal : function () {

                if(videoPlayer){
                    videoPlayer.style.display = 'none';
                }
                if(canvasElement){
                    canvasElement.style.display = 'none';
                }
                if(captureButton){
                    captureButton.style.display = 'inline';
                }


                if (videoPlayer && videoPlayer.srcObject) {
                    videoPlayer.srcObject.getVideoTracks().forEach(function (track) {
                        track.stop();
                    });
                }
                if(createPostArea){
                    setTimeout(function () {
                        createPostArea.style.transform = 'translateY(100vh)';
                    }, 1);
                    createPostArea.style.display = 'none';
                }

                if(viewPostArea) {
                    viewPostArea.style.display = 'block';
                }


            },
            clearCard : function () {
                if (sharedMomentsArea){
                    while (sharedMomentsArea.hasChildNodes()) {
                        sharedMomentsArea.removeChild(sharedMomentsArea.lastChild);
                    }
                }
            },

            createCard : function (data) {

                var cardWrapper = document.createElement('div');
                cardWrapper.className = 'col-xl-6 col-lg-6 col-md-6 col-sm-12 aem-pwa-blog_tile';

                var article = document.createElement('article');
                article.className = 'comp-tile tile hover-effect';

                var tileInner = document.createElement('a');
                tileInner.className = 'tile-inner';
                tileInner.setAttribute("href","/content/aem-pwa-blog/home.html");

                var clearfix = document.createElement('div');
                clearfix.className="bg-white clearfix";

                var bgDarkGrey = document.createElement('div');
                bgDarkGrey.className="image bg-dark-grey";

                var bgImage  = document.createElement('div');
                bgImage.className="bg-image bg-responsive-image";
                bgImage.style.backgroundImage = 'url(' + data.fileReference + ')';

                var copy = document.createElement('div');
                copy.className="copy";

                var title = document.createElement('h3');
                title.className = "title";
                title.textContent = data.title;

                var icon = document.createElement('span');
                icon.className = "fa fa-chevron-right icon icon-chevron";

                var description = document.createElement('p');
                description.className = "description";
                description.textContent = data.description;

                copy.appendChild(icon);
                copy.appendChild(title);
                copy.appendChild(description);

                clearfix.appendChild(bgDarkGrey);
                bgDarkGrey.appendChild(bgImage);

                tileInner.appendChild(clearfix);
                tileInner.appendChild(copy);

                article.appendChild(tileInner);
                cardWrapper.appendChild(article);

                tileContent.appendChild(cardWrapper);
            },

            syncUpdateUI : function(data) {
                this.clearCard();
                for (var i = 0; i < data.length; i++) {
                    this.createCard(data[i]);
                }
            },
            updateUI : function (data) {
                if(data){
                    for (var i = 0; i < data.length; i++) {
                        this.createCard(data[i]);
                    }
                }
            },
            sendData: function () {
                var id = new Date().toISOString();
                var postData = new FormData();
                postData.append('id', id);
                postData.append('title', titleInput.value);
                if(canvasElement && typeof canvasElement.toDataURL === "function"){
                    postData.append('file', canvasElement.toDataURL());
                }


                fetch('/content/aem-pwa-blog/notifications.json', {
                    method: 'POST',
                    body: postData
                })
                    .then(function (res) {
                        console.log('[TL30-PWA][sendData] Sent data', res);
                        this.updateUI();
                    })
            },
            initDynamicData : function(){

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
            },
            initUI : function () {

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


                if(shareImageButton) {
                    shareImageButton.addEventListener('click', function(){window.AdobeSummit.openCreatePostModal()});
                }

            },
            init:function () {
                this.initUI();
                this.Exercise02.init();

            }
        };

    window.AdobeSummit = AdobeSummit;

}(window, navigator, document));


