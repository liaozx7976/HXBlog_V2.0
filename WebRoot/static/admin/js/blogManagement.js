﻿
// 加载类型, 标签, 同步加载, 否则 可能 layui 绑定不了事件
initTypeAndTags()

layui.define(['element', 'laypage', 'layer', 'form', 'pagesize'], function (exports) {
    var $ = layui.jquery;
    var layer = layui.layer;
    var form = layui.form();
    var laypage = layui.laypage;
    var element = layui.element();
    var laypageId = 'pageNav';

    form.on('submit(formSearch)', function (data) {
        var index = layer.load(1);
        doSearch(layer, index, 1)
        return false;
    });

    initilData(1);
    //页数据初始化
    function initilData(pageNow) {
        var index = layer.load(1);

        //模拟数据加载
        setTimeout(doSearch(layer, index, pageNow), 1000);
    }

    function doSearch(layer, index, pageNow) {
        layer.close(index);
        var params = $("#blogSearchForm").serialize()
        params += "&pageNow=" + pageNow
        params += "&pageSize=" + pageSize

        ajax({
            url: reqMap.blog.adminList,
            type: "GET",
            data: params,
            success: function (resp) {
                if (resp.success) {
                    var html = '';
                    for (var i in resp.data.list) {
                        var item = resp.data.list[i];
                        html += '<tr>';
                        html += '<td>' + item.id + '</td>';
                        html += '<td><img src="' + item.blogCreateTypeImgUrl + '" /></td>';
                        html += '<td> <a href="/static/main/blogDetail.html?id=' + item.id + '" target="_blank" >' + item.title + '</a></td>';
                        html += '<td>' + item.author + '</td>';
                        html += '<td>' + item.summary + '</td>';
                        html += '<td>' + item.createdAt + '</td>';
                        html += '<td>' + item.blogTypeName + '</td>';
                        html += '<td>' + item.blogTagNames + '</td>';
                        html += '<td><button class="layui-btn layui-btn-small layui-btn-normal" onclick="layui.funcs.editData(' + item.id + ')"><i class="layui-icon">&#xe642;</i></button></td>';
                        html += '<td><button class="layui-btn layui-btn-small layui-btn-danger" onclick="layui.funcs.deleteData(' + item.id + ')"><i class="layui-icon">&#xe640;</i></button></td>';
                        html += '</tr>';
                    }
                    $('#dataContent').html(html);
                    element.init();

                    form.render('checkbox');  //重新渲染CheckBox，编辑和添加的时候
                    laypage({
                        cont: laypageId,
                        pages: resp.data.totalPage,
                        groups: 5,
                        skip: true,
                        curr: pageNow,
                        jump: function (obj, first) {
                            var pageNow = obj.curr;
                            if (! first) {
                                initilData(pageNow);
                            }
                        }
                    });
                } else {
                    layer.alert("拉取博客列表失败[" + resp.data + "] !", {icon: 5});
                }
            }
        });

    }

    //输出接口，主要是两个函数，一个删除一个编辑
    var funcs = {
        editData: function (id) {
            parent.switchTab(parent.$, parent.element, '修改博客', '/static/admin/addBlog.html?id=' + id, 'blog-' + id);
        },
        deleteData: function (id) {
            layer.confirm('同时会删除对应评论，确定删除？', {
                btn: ['确定', '取消'] //按钮
            }, function () {
                ajax({
                    url: reqMap.blog.remove,
                    data: {"id": id},
                    type: 'POST',
                    success: function (resp) {
                        if (resp.success) {
                            layer.alert('删除成功!', {
                                closeBtn: 0,
                                icon: 1
                            }, function () {
                                refresh()
                            });
                        } else {
                            layer.alert("删除博客失败[" + resp.data + "] !", {icon: 5});
                        }
                    }
                });
            }, function () {

            });
        }
    };
    exports('funcs', funcs);
});

/**
 * 加载类型 和标签列表
 */
function initTypeAndTags() {
    ajax({
        url: reqMap.composite.typeAndTags,
        type: "GET",
        success: function (resp) {
            if (resp.success) {
                var types = resp.data.types
                var typeIdEle = $("#typeId")
                for (idx in types) {
                    typeIdEle.append("<option value='" + types[idx].id + "'> " + types[idx].name + " </option>")
                }

                var tags = resp.data.tags
                var tagIdEle = $("#tagId")
                for (idx in tags) {
                    tagIdEle.append("<option value='" + tags[idx].id + "'> " + tags[idx].name + " </option>")
                }
            } else {
                layer.alert("拉取类型/标签列表失败[" + resp.data + "] !", {icon: 5});
            }
        }
    });
}

