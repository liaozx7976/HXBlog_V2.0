/**
 * header - footer
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 5/27/2017 7:58 PM
 */

var templateUrl = "/static/main/templates.html"
var jq = $

/**
 * 加载header, footer
 */
function importHeaderFooter() {
    ajax({
        url: reqMap.other.templateUrl,
        type: "GET",
        async: false,
        data: {},
        success: function (resp) {
            var template = $(resp)
            // resp 不能为 <html> 开头
            jq("#headerNav").html(template.find("#headerNav").html())
            jq("#bannerNav").html(template.find("#bannerNav").html())
            jq("#rightNav").html(template.find("#rightNav").html())
            jq("#replyNav").html(template.find("#replyNav").html())
            jq("#footerNav").html(template.find("#footerNav").html())
            jq("#copyrightNav").html(template.find("#copyrightNav").html())
        }
    })
}

/**
 * 初始化头部, 右边栏, 底部
 */
function headerFooterInit() {
    ajax({
        url: reqMap.index.index,
        async : false,
        success: function (resp) {
            if (resp.success) {
                var data = resp.data
                var userInfo = resp.extra

                $("[name='title']").text(data.title)
                $("[name='subTitle']").text(data.subTitle)

                var typesEle = $("[name='typesEle']")
                for (idx in data.types) {
                    var type = data.types[idx]
                    typesEle.append("<option name='type' value='/static/main/blogList.html?typeId=" + type.id + "&typeName=" + type.name + "' >" + type.name + "</option>")
                }

                var linksEle = $("[name='linksEle']")
                for (idx in data.links) {
                    var link = data.links[idx]
                    linksEle.append(
                        "<div class='sim-button button5'>" +
                        "<a href='" + link.url + "'><span> " + link.name + " </span></a>" +
                        "</div>")
                }

                var tags = data.tags
                initTags(tags)

                var hotBlogsEle = $("[name='hotBlogsEle']")
                reverse(data.hotBlogs)
                for (var idx in data.hotBlogs) {
                    var blog = data.hotBlogs[idx]
                    hotBlogsEle.append(
                        "<div class='blog-grids wow fadeInDown' data-wow-duration='.8s' data-wow-delay='.2s'> " +
                        "<div class='blog-grid-left'> " +
                        "<a href='/static/main/blogDetail.html?id=" + blog.id + "'> " +
                        "<img src='" + blog.coverUrl + "' class='img-responsive' width='40px' height='40px'>" +
                        "</a>" +
                        "</div>" +
                        "<div class='blog-grid-right' name='hotBlogTitle' data-blog-title='" + blog.title + "' >" +
                        "<h5>" +
                        "<img src='" + blog.blogCreateTypeImgUrl + "' />" +
                        "<a href='/static/main/blogDetail.html?id=" + blog.id + "'> " + trimIfExceed(blog.title, 20, "...") + " </a>" +
                        "</h5>" +
                        "</div>" +
                        "<div class='clearfix'></div>" +
                        "</div>")
                }

                var facetByMonthEle = $("[name='facetByMonthEle']")
                var lenOfFacetByMonth = data.facetByMonth.length
                var hiddenIfOver = monthFacetHideIfOver
                if (lenOfFacetByMonth < hiddenIfOver) {
                    addMonthFacet(facetByMonthEle, data.facetByMonth, lenOfFacetByMonth - 1, 0)
                } else {
                    addMonthFacet(facetByMonthEle, data.facetByMonth, lenOfFacetByMonth - 1, i - hiddenIfOver + 1)
                    facetByMonthEle.append("<a name='facetMore' > ( more ) </a>")

                    $("[name='facetMore']").click(function () {
                        facetByMonthEle.html("")
                        addMonthFacet(facetByMonthEle, data.facetByMonth, lenOfFacetByMonth - 1, 0)
                    })
                }


                var latestComments = $("[name='latestCommentsEle']")
                reverse(data.latestComments)
                for (var idx in data.latestComments) {
                    var comment = data.latestComments[idx]
                    latestComments.append(
                        "<span>" +
                        "<span name='latestCommentTitle' ><a href='/static/main/blogDetail.html?id=" + comment.blogId + "'>" + comment.name + "@" + comment.toUser + " </a> - <span>" + comment.createdAt + "</span> </span> " +
                        "</span>" +
                        "<p>" +
                        "<span class='sourceText' style='word-break: break-all;' >" + comment.comment + "</span>" +
                        "</p>"
                    )
                }

                $("[name='projLikeCntEle']").text(data.goodSensed)
                $("[name='todayVisitedEle']").text(data.todayVisited)
                $("[name='totalVisitedEle']").text(data.totalVisited)

                heartInit("[name='projHeartEle']", "[name='projLikeCntEle']", data.goodCnt, data.goodSensed)
                heartClick("[name='projHeartEle']", "[name='projLikeCntEle']", function () {
                    return (!data.goodSensed) || (userInfo && (userInfo.systemUser))
                }, function (isPrise) {
                    var senseParams = {
                        blogId: "-1",
                        score: isPrise ? 1 : 0,
                        sense: "1"
                    }

                    ajax({
                        url: reqMap.blog.sense,
                        data: senseParams,
                        type: "POST",
                        success: function (resp) {

                        }
                    })
                })

                selectHeader()
                $("[name='hotBlogTitle']").hover(function () {
                    layer.tips($(this).attr("data-blog-title"), this,
                        {tips: [3], time: 1000});
                }, function () {

                })

                $("[name='searchBtn']").click(function() {
                    var keywords = $(this).parent().find("[name='keywords']").val()
                    location.href = "/static/main/blogList.html?keywords=" + keywords
                    return false
                })
            }
        }
    });

    /**
     * 初始化标签云的数据
     * @param tags
     */
    function initTags(tags) {
        var tagEntries = [];
        for (var idx in tags) {
            var entry = {
                label: tags[idx].name,
                url: '/static/main/blogList.html?tagId=' + tags[idx].id + '&tagName=' + tags[idx].name,
                target : "_blank"
            }
            tagEntries.push(entry);
        }

        // var settings = {
        //     entries: tagEntries,
        //     width: '100%',
        //     height: '100%',
        //     radius: '73%',
        //     radiusMin: '50%',
        //     bgDraw: true,
        //     bgColor: '#FFF',
        //     opacityOver: 1.00,
        //     opacityOut: 0.13,
        //     opacitySpeed: 6,
        //     fov: 800,
        //     speed: 0.5,
        //     fontFamily: 'Oswald, Arial, sans-serif',
        //     fontSize: '15',
        //     fontColor: '#000',
        //     fontWeight: 'normal',//bold
        //     fontStyle: 'normal',//italic
        //     fontStretch: 'normal',//wider, narrower, ultra-condensed, extra-condensed, condensed, semi-condensed, semi-expanded, expanded, extra-expanded, ultra-expanded
        //     fontToUpperCase: false
        // };
        var settings = {
            entries: tagEntries,
            width: '100%',
            height: '100%',
            radius: '73%',
            radiusMin: '50%',
            bgDraw: true,
            bgColor: '#FFF',
            opacityOver: 1.00,
            opacityOut: 0.13,
            opacitySpeed: 6,
            fov: 800,
            speed: 0.5,
            fontFamily: 'Oswald, Arial, sans-serif',
            fontSize: '15',
            fontColor: '#000',
            fontWeight: 'normal',//bold
            fontStyle: 'normal',//italic
            fontStretch: 'normal',//wider, narrower, ultra-condensed, extra-condensed, condensed, semi-condensed, semi-expanded, expanded, extra-expanded, ultra-expanded
            fontToUpperCase: false
        };

        //var svg3DTagCloud = new SVG3DTagCloud( document.getElementById( 'holder'  ), settings );
        $("[name='tagCloudEle']").svg3DTagCloud(settings);
        parse()
    }

    function parse() {
        $(".sourceText").emojiParse({
            icons: [{
                path: "/foreground/images/qq/",
                file: ".gif",
                placeholder: "#qq_{alias}#"
            }]
        });
    }

}

/**
 * 点赞, 踩的逻辑处理
 * @param heart
 * @param likeCnt
 * @param shouldResp
 * @param callback
 */
function heartClick(heart, likeCnt, shouldResp, callback) {
    var heartEle = $(heart)
    heartEle.click(function () {
        var likeCntEle = $(likeCnt)
        var cntNow = parseInt(likeCntEle.html())

        if (shouldResp && shouldResp()) {
            var isPrise = (heartEle.attr("liked") !== "like")
            if (isPrise) {
                likeCntEle.html(cntNow + 1)
                heartEle.addClass("heartAnimation").attr("liked", "like")
                // 我擦嘞, 因为 这个问题, 找了 半个小时了 ..
                heartEle.css("background-position", "right")
            } else {
                likeCntEle.html(cntNow - 1)
                heartEle.removeClass("heartAnimation").attr("liked", "unlike")
                heartEle.css("background-position", "left")
            }
            if (callback) {
                callback(isPrise)
            }
        }
    })
}

/**
 * 点赞, 踩的初始化
 * @param heart
 * @param likeCnt
 * @param likeCntSum
 * @param isPriseNow
 */
function heartInit(heart, likeCnt, likeCntSum, isPriseNow) {
    var heartEle = $(heart)
    var likeCntEle = $(likeCnt)
    likeCntEle.html(likeCntSum)
    if (isPriseNow) {
        heartEle.addClass("heartAnimation").attr("liked", "like");
    } else {
        heartEle.removeClass("heartAnimation").attr("liked", "unlike");
        heartEle.css("background-position", "left");
    }
}

/**
 * 选择顶部导航
 */
function selectHeader() {
    var url = location.href

    var allA = $("[name='headerNav0'] li a")
    for (idx in allA) {
        if (url.indexOf(allA[idx].href) >= 0) {
            $(allA[idx]).parent().addClass("active act")
            break;
        }
    }
}

/**
 * 增加 monthFacet
 * @param facetByMonthEle
 * @param monthFacet
 */
function addMonthFacet(facetByMonthEle, monthFacet, start, min) {
    for (var i = start; i >= min; i--) {
        var facetByMonth = monthFacet[i]
        facetByMonthEle.append("<a href='/static/main/blogList.html?createdAtMonth=" + facetByMonth.month + "' >" + facetByMonth.month + " (" + facetByMonth.cnt + ")</a>")
    }
}

