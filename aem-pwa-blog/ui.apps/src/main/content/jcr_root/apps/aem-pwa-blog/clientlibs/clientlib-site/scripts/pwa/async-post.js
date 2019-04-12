var form = document.querySelector('form');
var sharedMomentsArea = document.querySelector('#shared-moments');

function clearCards() {
    while (sharedMomentsArea.hasChildNodes()) {
        sharedMomentsArea.removeChild(sharedMomentsArea.lastChild);
    }
}

function sendData() {
    var id = new Date().toISOString();
    var postData = new FormData();
    postData.append('id', id);
    postData.append('title', titleInput.value);
    postData.append('location', locationInput.value);
    postData.append('rawLocationLat', fetchedLocation.lat);
    postData.append('rawLocationLng', fetchedLocation.lng);
    postData.append('file', picture, id + '.png');

    fetch('/', {
        method: 'POST',
        body: postData
    })
        .then(function (res) {
            console.log('Sent data', res);
            updateUI();
        })
}

function createTeaser(data) {
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
    // var cardSaveButton = document.createElement('button');
    // cardSaveButton.textContent = 'Save';
    // cardSaveButton.addEventListener('click', onSaveButtonClicked);
    // cardSupportingText.appendChild(cardSaveButton);
    cardWrapper.appendChild(cardSupportingText);
    //componentHandler.upgradeElement(cardWrapper);
    sharedMomentsArea.appendChild(cardWrapper);
}

function updateUI(data) {
    clearCards();
    for (var i = 0; i < data.length; i++) {
        createTeaser(data[i]);
    }
}

if(tiles){
    var url = tiles.attributes["data-async-url"].value;
    var networkDataReceived = false;

    fetch(url)
        .then(function (res) {
            return res.json();
        })
        .then(function (data) {
            networkDataReceived = true;
            console.log('From web', data);
            var dataArray = [];
            for (var key in data) {
                dataArray.push(data[key]);
            }
            updateUI(dataArray);
        });

    if ('indexedDB' in window) {
        readAllData('posts')
            .then(function (data) {
                if (!networkDataReceived) {
                    console.log('From cache', data);
                    updateUI(data);
                }
            });
    }
}

if(titleInput && locationInput) {
    if (form){
	    form.addEventListener('submit', function (event) {
	        event.preventDefault();
	
	        if (titleInput.value.trim() === '' || locationInput.value.trim() === '') {
	            alert('Please enter valid data!');
	            return;
	        }
	
	        closeCreatePostModal();
	
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
	            sendData();
	        }
	    })
	}
}
