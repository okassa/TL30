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
    var submitProfileFormButton = document.querySelector('#submit');

window.AdobeSummit.Exercise06 =  {

    init:function () {


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