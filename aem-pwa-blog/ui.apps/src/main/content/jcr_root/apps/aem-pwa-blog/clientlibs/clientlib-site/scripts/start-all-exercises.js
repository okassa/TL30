
/*!
 *  Adobe Summit EMEA 2019 - TL30 : Building a PWA with AEM
 *
 * @description :
 *                  This needs to be updated
 *
 * @file : /apps/aem-pwa-blog/clientlibs/clientlib-site/js/start-all-exercises.js
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
    /**
     * Step zero : Make sure we have promise available to be used
     * service workers need extensively Promises.
     */
    if (!window.Promise) {
        window.Promise = Promise;
    }

    /**
     * -----------------------------------------------------------------------
     * --
     * --                   Main
     * --
     * -----------------------------------------------------------------------
     */
    var tl30 = window.AdobeSummit;
        tl30.init();
}(window, navigator, document));


