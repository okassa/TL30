
/*!
 *  Adobe Summit EMEA 2019 - TL30 : Building a PWA with AEM
 *
 * @description :
 *                  This needs to be updated
 *
 * @file : /apps/aem-pwa-blog/clientlibs/clientlib-site/js/ex00-global-init.js
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
// Init AdobeSummit namespace if not available.
var AdobeSummit = window.AdobeSummit || {
        /**
         *
         *
         *
         */
        Constants : {
            SW_PATH:"/content/aem-pwa-blog/sw.js",
            SW_SCOPE:"/content/aem-pwa-blog/en"
        },

        openCreatePostModal : function (createPostArea,canvasElement,viewPostArea) {
            createPostArea.style.display = 'block';
            canvasElement.style.display = 'none';
            viewPostArea.style.display = 'none';

            setTimeout(function () {
                createPostArea.style.transform = 'translateY(0)';
            }, 1);

            window.AdobeSummit.Device.startDeviceInstall();

        },
        closeCreatePostModal : function (imagePickerArea,videoPlayer,canvasElement,locationBtn,
                                         locationLoader,captureButton,createPostArea,viewPostArea) {
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
            createPostArea.style.display = 'none';
            viewPostArea.style.display = 'block';
        },
        clearCard : function (sharedMomentsArea) {
            if (sharedMomentsArea){
                while (sharedMomentsArea.hasChildNodes()) {
                    sharedMomentsArea.removeChild(sharedMomentsArea.lastChild);
                }
            }
        },

        createCard : function (data,componentHandler,sharedMomentsArea) {
            var cardWrapper = document.createElement('div');
            cardWrapper.className = 'shared-moment-card mdl-card mdl-shadow--2dp';
            var cardTitle = document.createElement('div');
            cardTitle.className = 'mdl-card__title';
            cardTitle.style.backgroundImage = 'url(' + data.image + ')';
            cardTitle.style.backgroundSize = 'cover';
            cardWrapper.appendChild(cardTitle);
            var cardTitleTextElement = document.createElement('h2');
            cardTitleTextElement.style.color = 'white';
            cardTitleTextElement.className = 'mdl-card__title-text';
            cardTitleTextElement.textContent = data.title;
            cardTitle.appendChild(cardTitleTextElement);
            var cardSupportingText = document.createElement('div');
            cardSupportingText.className = 'mdl-card__supporting-text';
            cardSupportingText.textContent = data.location;
            cardSupportingText.style.textAlign = 'center';
            cardWrapper.appendChild(cardSupportingText);
            componentHandler.upgradeElement(cardWrapper);
            sharedMomentsArea.appendChild(cardWrapper);
        },

        syncUpdateUI : function(data,componentHandler,sharedMomentsArea) {
            this.clearCard();
            for (var i = 0; i < data.length; i++) {
                this.createCard(data[i],componentHandler,sharedMomentsArea);
            }
        },
        updateUI : function (componentHandler,sharedMomentsArea) {
            clearCards(sharedMomentsArea);
            for (var i = 0; i < data.length; i++) {
                this.createCard(data[i],componentHandler,sharedMomentsArea);
            }
        },
        sendData: function (titleInput,locationInput,fetchedLocation,picture) {
            var id = new Date().toISOString();
            var postData = new FormData();
            postData.append('id', id);
            postData.append('title', titleInput.value);
            postData.append('location', locationInput.value);
            postData.append('rawLocationLat', fetchedLocation.lat);
            postData.append('rawLocationLng', fetchedLocation.lng);
            postData.append('file', picture, id + '.png');

            fetch('/bin/aem_pwa_blog/postData', {
                method: 'POST',
                body: postData
            })
                .then(function (res) {
                    console.log('Sent data', res);
                    this.updateUI();
                })
        },
        Exercise02:{
            init:function () {
                console.log("Exercise02 has not been implemented");
            }
        },
        Exercise03:{
            init:function () {
                console.log("Exercise03 has not been implemented");
            }
        },
        Exercise04:{
            init:function () {
                console.log("Exercise04 has not been implemented");
            }
        },
        Exercise05:{
            init:function () {
                console.log("Exercise05 has not been implemented");
            }
        },
        Exercise06:{
            init:function () {
                console.log("Exercise06 has not been implemented");
            }
        },
        Exercise07:{
            init:function () {
                console.log("Exercise07 has not been implemented");
            }
        },
        init:function () {
            this.Exercise02.init();
            this.Exercise03.init();
            this.Exercise04.init();
            this.Exercise05.init();
            this.Exercise06.init();
            this.Exercise07.init();

        }
    };

    window.AdobeSummit = AdobeSummit;

}(window, navigator, document));


