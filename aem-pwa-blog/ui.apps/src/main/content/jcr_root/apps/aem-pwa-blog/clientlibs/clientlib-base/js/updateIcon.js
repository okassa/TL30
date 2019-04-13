/*!
 *  Adobe Summit EMEA 2019 - TL30 : Building a PWA with AEM
 *
 * @description :
 *                  This file contains the code for displaying the cross when clicking
 *                  onto the nav toggle button
 *
 * @file : /apps/aem-pwa-blog/clientlibs/clientlib-base/js/updateIcon.js
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
(function() {
    "use strict";

    $('.navbar-toggler').on('click', function() {
        // if bars then removes
       if($(".aem-pwa-blog__navTogglerIcon").get(0).classList.contains("fa-bars")){
           $(".aem-pwa-blog__navTogglerIcon").removeClass( "fa-bars" ).addClass( "fa-times" );
        }else{
           // if times then removes bars
           $(".aem-pwa-blog__navTogglerIcon").removeClass( "fa-times" ).addClass( "fa-bars" );
       }
    });

})();


