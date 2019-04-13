function debounce(f, h, e) {
    var g;
    return function() {
        var j = this
            , i = arguments;
        clearTimeout(g);
        g = setTimeout(function() {
            g = null;
            if (!e) {
                f.apply(j, i)
            }
        }, h);
        if (e && !g) {
            f.apply(j, i)
        }
    }
}
;

function resizeHandler() {
    var e = debounce(function(f) {
        setHeights()
    }, 10);
    $(window).on("resize orientationchange", function(f) {
        e(f)
    });
    setTimeout(function() {
        $(window).trigger("resize")
    }, 1000)
}

function setHeights() {
    $(".comp-tiles").each(function(i, h) {
        var f = [], g = $(h), e;
        g.find(".tile .tile-inner > div").removeAttr("style");
        g.find(".col").each(function(k, l) {
            f[f.length] = $(l).find(".tile-inner > div").innerHeight()
        });
        function j(l, k) {
            return l - k
        }
        f.sort(j);
        e = f[f.length - 1];
        if (!g.hasClass("comp-tiles--usa-products")) {
            g.find(".tile .tile-inner > div").css("height", e);
            g.find(".tile .tile-inner > div").css("min-height", "211px")
        }
    })
}
;
function initTiles() {
    var e = $(".comp-tiles");
    e.find(".js-loadmore-btn").click(function(f) {
        f.preventDefault();
        e.find(".col:hidden").slice(0, 10).show();
        d.setHeights();
        if (e.find(".col:hidden").length === 0) {
            e.find(".js-loadmore-btn").hide()
        }
    })
};

function initMediaTiles() {
    var f = $(".media-tile")
        , g = f.closest(".section-inner")
        , h = $("body").hasClass("aem-edit");
    if (g.length) {
        g.prepend('<div class="grid-sizer col-md-6 col-xs-12"></div>');
        var e = g.masonry({
            itemSelector: ".grid-item",
            columnWidth: ".grid-sizer",
            percentPosition: true
        });
        e.imagesLoaded().progress(function() {
            e.masonry("layout")
        });
        $(".grid-item.media-tile--article").click(function(j) {
            var i = c(this).find("a");
            if (i.length) {
                b.location = i.attr("href")
            }
        })
    }
}
;

initTiles();
initMediaTiles();
resizeHandler();