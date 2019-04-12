/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2018 Adobe Systems Incorporated
 ~
 ~ Licensed under the Apache License, Version 2.0 (the "License");
 ~ you may not use this file except in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~     http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing, software
 ~ distributed under the License is distributed on an "AS IS" BASIS,
 ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ~ See the License for the specific language governing permissions and
 ~ limitations under the License.
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
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

    /**
    $('form[name="createProfile"]').submit(function(){
        $.post($(this).attr('action'), $(this).serialize(), function(res){
            console.log(res);
            $("#subscription-message").textContent = res;
        });
        return false; // prevent default action
    });
     **/


})();


