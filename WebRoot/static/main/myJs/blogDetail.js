/**
 * blogList.js
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 5/28/2017 11:05 AM
 */

/**
 * 初始化博客列表
 */
function contentInit() {

    /**
     * 从查询字符串中提取数据作为查询条件
     * @type {Object}
     */
    var params = getParamsFromUrl(window.location.href)
    if (params.id === '') {
        location.href = '/'
    }

    var app = new Vue({
        el: '#bodyContent',
        data: {
            blog: {},
            originalHeadImgUrl: "",
            userInfo: {
                name: "",
                email: "",
                headImgUrl: "",
                systemUser: false
            },
            senseVal : -1,
            currentUserSenseFromServer : -1,
            /**
             * 防止 systemUser 的页面刷新的 第一次发送sense请求
             */
            senseInitialized : false,
            goodAvgScore : 0.00,
            comments: [],
            headImages: [],
            replyInfo: {
                blogId: "",
                floorId: "",
                commentId: "",
                toUser: ""
            },
            pageInfo: {},
            pagination: []
        },
        mounted: function () {
            var that = this

            that.initBlogInfo(that)
            that.initHeadImages(that)
            SyntaxHighlighter.all()
            that.initEmoji()
            that.contentEventInit()
        },
        methods: {
            replyFunc: function (event) {
                var floorInfo = $(event.currentTarget).parents(".replyDiv").find(".floorInfo")

                this.replyInfo.floorId = floorInfo.attr("floorId")
                this.replyInfo.commentId = floorInfo.attr("commentId")
                this.replyInfo.toUser = floorInfo.attr("name")
                console.log(this.replyInfo)
                $("[name='comment']").text("[reply]" + floorInfo.attr("name") + "[/reply]\r\n")
                $("html,body").animate({scrollTop: $("#topOfResp").offset().top}, 1000);
            },
            initEmoji: function () {
                $("[name='comment']").emoji({
                    button: "[name='looks']",
                    showTab: true,
                    animation: 'fade',
                    icons: [{
                        name: "qq",
                        path: "/static/main/images/qq/",
                        maxNum: 91,
                        file: ".gif",
                        placeholder: "#qq_{alias}#"
                    }]
                });
                this.parse();
            },
            parse: function () {
                $(".sourceText").emojiParse({
                    icons: [{
                        path: "/static/main/images/qq/",
                        file: ".gif",
                        placeholder: "#qq_{alias}#"
                    }]
                });
            },
            updateHeadImg: function (event) {
                this.userInfo.headImgUrl = $(event.target).find("option:selected").attr("value")
            },
            initBlogInfo: function (that) {
                ajax({
                    url: reqMap.blog.get,
                    data: params,
                    success: function (resp) {
                        if (resp.success) {
                            that.blog = resp.data
                            that.senseVal = resp.data.goodSensed
                            that.currentUserSenseFromServer = that.senseVal
                            that.goodAvgScore = Number(resp.data.goodAvgScore)

                            $("#blogContent").html(that.blog.content)
                            that.replyInfo.blogId = that.blog.id
                            that.replyInfo.toUser = that.blog.author

                            var userInfo = resp.extra
                            if (userInfo !== null) {
                                that.userInfo = userInfo
                                that.originalHeadImgUrl = userInfo.headImgUrl
                                if (userInfo.systemUser) {
                                    $("[name='name']").attr("readonly", "readonly")
                                    $("[name='email']").attr("readonly", "readonly")
                                }
                            }
                        } else {
                            layer.alert("拉取博客列表失败 !")
                        }
                    }
                });

                var prevUrl = sessionStorageGet(location.href)
                if(isEmpty(prevUrl)) {
                    prevUrl = that.getUrlWithPage(1)
                }
                that.updateComment(prevUrl)
            },
            initHeadImages: function (that) {
                ajax({
                    url: reqMap.image.headImgList,
                    data: {},
                    type: "GET",
                    success: function (resp) {
                        if (resp.success) {
                            that.headImages = resp.data
                            if (isEmpty(that.userInfo.headImgUrl)) {
                                that.userInfo.headImgUrl = that.headImages[0].url
                            }
                        } else {
                            layer.alert("拉取头像列表失败 !")
                        }
                    }
                });
            },
            toType: function (event) {
                var btn = $(event.target)
                location.href = "/static/main/blogList.html?typeId=" + btn.attr("value") + "&typeName=" + btn.text();
            },
            toTag: function (event) {
                var btn = $(event.target)
                location.href = "/static/main/blogList.html?tagId=" + btn.attr("value") + "&tagName=" + btn.text();
            },
            reUseMyHeadImg: function (event) {
                if (this.userInfo !== null) {
                    this.userInfo.headImgUrl = this.originalHeadImgUrl
                }
            },
            addComment: function () {
                var that = this
                var replyForm = $("#replyForm")
                var params = {
                    blogId: that.blog.id,
                    floorId: that.replyInfo.floorId,
                    commentId: that.replyInfo.commentId,
                    name: that.userInfo.name,
                    email: that.userInfo.email,
                    headImgUrl: that.userInfo.headImgUrl,
                    toUser: that.replyInfo.toUser,
                    comment: replyForm.find("[name='comment']").html(),
                }
                if(isEmpty(params.toUser)) {
                    params.toUser = that.blog.author
                }
                if (isEmpty(params.name)) {
                    layer.tips('请输入用户名', "[name='name']");
                    return;
                }
                if (isEmpty(params.comment)) {
                    layer.tips('请输入评论内容', "[name='comment']");
                    return;
                }

                ajax({
                    url: reqMap.comment.add,
                    data: params,
                    type: "POST",
                    success: function (resp) {
                        if (resp.success) {
                            var addedComment = {
                                "blogId": params.blogId,
                                "floorId": 1,
                                "commentId": 1,
                                "name": params.name,
                                "email": params.email,
                                "headImgUrl": params.headImgUrl,
                                "toUser": params.toUser,
                                "role": isEmpty(that.userInfo.title) ? "guest" : that.userInfo.title,
                                "comment": params.comment,
                                "createdAt": new Date().format("yyyy-MM-dd hh:mm:ss")
                            }

                            var endReplyFlag = "[/reply]"
                            if (isEmpty(params.floorId) || (addedComment.comment.indexOf(endReplyFlag) < 0)) {
                                if (that.comments.length < pageSize) {
                                    if(that.comments.length === 0) {
                                        addedComment.floorId = 1
                                    } else {
                                        addedComment.floorId = parseInt(that.comments[that.comments.length - 1].floorComment.floorId) + 1
                                    }
                                    that.comments.push({
                                        floorComment: addedComment,
                                        replies: []
                                    })
                                }
                            } else {
                                var floorId = params.floorId
                                var floorComments = that.locateFloorComment(that.comments, floorId)
                                if (floorComments !== null) {
                                    addedComment.floorId = floorId
                                    addedComment.commentId = floorComments.replies.length + 2
                                    var endOfReply = addedComment.comment.indexOf(endReplyFlag) + endReplyFlag.length
                                    addedComment.comment = addedComment.comment.substr(endOfReply).trim()

                                    floorComments.replies.push(addedComment)
                                }
                            }

                            replyForm.find("[name='comment']").html("")
                            layer.msg("添加评论成功 !")
                        } else {
                            layer.msg("添加评论失败[" + resp.data + "] !")
                        }
                    }
                });
            },
            // 如果 切换前后的 值相同, 是不会触发这个事件的 !
            updateSense : function(newVal) {
                if(! this.senseInitialized) {
                    this.senseInitialized = true
                    return
                }
                if((this.currentUserSenseFromServer > 0) && (! this.userInfo.systemUser)) {
                    layer.tips("您已经点过了哦, 游客只能选择一次, 会员可以更新评分, 请联系管理员吧 !", "[name='rateSelect']", {
                        tips: [1, '#00ff00'],
                        time: 1000
                    })
                    return
                }

                var senseParams = copyOf(this.userInfo)
                senseParams.blogId = params.id
                senseParams.sense = "1"
                senseParams.score = newVal
                ajax({
                    url: reqMap.blog.sense,
                    data: senseParams,
                    type: "POST",
                    success: function (resp) {

                    }
                })
                layer.tips("感谢您的支持 !", "[name='rateSelect']", {
                    tips: [1, '#00ff00'],
                    time: 1000
                })
            },
            locateFloorComment: function (comments, floorId) {
                var floorIdInt = parseInt(floorId)
                if (floorIdInt < 0) {
                    return null;
                }
                if (floorIdInt >= comments.length) {
                    floorIdInt = comments.length - 1
                }

                for (i = floorIdInt; i >= 0; i--) {
                    if (comments[i].floorComment.floorId === floorId) {
                        return comments[i]
                    }
                }
                return null;
            },
            getUrlWithPage: function (pageNow) {
                var baseUrl = "/comment/list"
                params.pageNow = pageNow
                return encapGetUrl(baseUrl, params)
            },
            updateComment: function (url) {
                var that = this
                var params = getParamsFromUrl(url)
                sessionStorageSet(location.href, url)

                var loadCommentIdx = layer.load(0, {shade: false})
                ajax({
                    url: reqMap.comment.list,
                    data: params,
                    success: function (resp) {
                        if (resp.success) {
                            that.comments = []
                            that.pagination = []

                            that.pageInfo = resp.data
                            var comments = resp.data.list

                            for (idx in comments) {
                                that.comments.push({
                                    floorComment: comments[idx][0],
                                    replies: comments[idx].slice(1),
                                    fillingDivMargin: comments[idx].length > 1 ? "100" : "50"
                                })
                            }

                            var pageNow = params.pageNow
                            collectPagination(that.pagination, that.pageInfo, pageNow, that.getUrlWithPage)
                        } else {
                            layer.alert("拉取评论列表失败 !")
                        }
                        layer.close(loadCommentIdx)
                    }
                });
            },
            contentEventInit : function() {
                $("[name='comment']").keydown(function(e){
                    if((e.keyCode === 46) || (e.keyCode === 8) ) {
                        return
                    }

                    var commentEle = $(this)
                    var comment = commentEle.html()
                    var delta = commentMaxLen - comment.length
                    if(delta < 0) {
                        commentEle.html(comment.substr(0, commentMaxLen))
                        delta = 0
                    }
                    if(delta < commentLenThreshold) {
                        layer.tips("还可以输入" + delta + "个字符", "[name='comment']", {
                            tips: [1, '#ff0000'],
                            time: 1000
                        })
                    }
                    if(delta <= 0) {
                        return false
                    }
                });
            }

        }
    })

}
