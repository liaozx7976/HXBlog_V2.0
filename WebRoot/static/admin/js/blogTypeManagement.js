/**
 * blogTypeManagement.js
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 5/24/2017 9:46 PM
 */

layui.define(['layer', 'form'], function (exports) {
    var $ = layui.jquery;
    var layer = layui.layer;
    var form = layui.form();

    initilData();

    function initilData() {
        var index = layer.load(1);
        //模拟数据加载
        setTimeout(function () {
            layer.close(index);
            ajax({
                url: reqMap.type.list,
                type: "GET",
                data: {},
                success: function (resp) {
                    var html = new StringBuilder();
                    if (resp.success) {
                        for (var i in resp.data) {
                            var item = resp.data[i];
                            html.append('<tr>')
                            html.append('<td>' + item.id + '</td>')
                            html.append('<td>' + item.name + '</td>')
                            html.append('<td>' + item.sort + '</td>')
                            html.append('<td><button class="layui-btn layui-btn-small layui-btn-normal" onclick="layui.funcs.editData(' + item.id + ',\'' + item.name + '\', ' + item.sort + ')"><i class="layui-icon">&#xe642;</i></button></td>')
                            html.append('<td><button class="layui-btn layui-btn-small layui-btn-danger" onclick="layui.funcs.deleteData(' + item.id + ')"><i class="layui-icon">&#xe640;</i></button></td>')
                            html.append('</tr>')
                        }
                    } else {
                        layer.alert("拉取类型列表失败[" + resp.data + "] !", {icon: 5});
                    }
                    $('#dataContent').html(html.toString());
                }
            });
        }, 500);
    }

    form.on('submit(addTypeSubmit)', function (data) {
        ajax({
            url: reqMap.type.add,
            type: "POST",
            data: $("#addTypeForm").serialize(),
            success: function (resp) {
                if (resp.success) {
                    layer.alert('添加类型成功!', {
                        closeBtn: 0,
                        icon: 1
                    }, function () {
                        refresh()
                    });
                } else {
                    layer.alert("添加类型失败[" + resp.data + "] !", {icon: 5});
                }
            }
        });

        return false;
    })

    form.on('submit(updateTypeSubmit)', function (data) {
        ajax({
            url: reqMap.type.update,
            type: "POST",
            data: $("#updateTypeForm").serialize(),
            success: function (resp) {
                if (resp.success) {
                    layer.alert('更新类型成功!', {
                        closeBtn: 0,
                        icon: 1
                    }, function () {
                        refresh()
                    });
                } else {
                    layer.alert("更新类型失败[" + resp.data + "] !", {icon: 5});
                }
            }
        });

        return false;
    })

    //输出接口，主要是两个函数，一个删除一个编辑
    var funcs = {
        addData: function () {
            var html = new StringBuilder();
            html.append('<form id="addTypeForm" action="/admin/type/add" class="layui-form layui-form-pane" >')
            html.append('<label class="layui-form-label" style="border: none" >名称:</label>')
            html.append('<input style="width:87%;margin: auto;color: #000!important;" name="name" lay-verify="required" class="layui-input" >')
            html.append('<label class="layui-form-label" style="border: none" >排序:</label>')
            html.append('<input style="width:87%;margin: auto;color: #000!important;" name="sort" lay-verify="required" class="layui-input" >')
            html.append('<div class="layui-form-item">')
            html.append('<div class="layui-input-inline" style="margin:10px auto 0 auto;display: block;float: none;">')
            html.append('<button class="layui-btn" id="submit"  lay-submit="" lay-filter="addTypeSubmit">添加</button>')
            html.append('<button type="reset" class="layui-btn layui-btn-primary">重置</button>')
            html.append('</div>')
            html.append('</div>')
            html.append('</form>')

            layer.open({
                type: 1,
                skin: 'layui-layer-rim', //加上边框
                area: '420px', //宽高
                title: '添加类别',
                content: html.toString()
            });
        },
        editData: function (id, typeName, sort) {
            var html = new StringBuilder();
            html.append('<form id="updateTypeForm" action="/admin/type/update" class="layui-form layui-form-pane" >')
            html.append('<input type="hidden" name="id" value="' + id + '"/>')
            html.append('<label class="layui-form-label" style="border: none" >名称:</label>')
            html.append('<textarea style="width:87%;margin: auto;color: #000!important;" name="name" class="layui-textarea" lay-verify="required" >' + typeName + '</textarea>')
            html.append('<label class="layui-form-label" style="border: none" name="content" >排序:</label>')
            html.append('<input style="width:87%;margin: auto;color: #000!important;" name="sort" value="' + sort + '" lay-verify="required" class="layui-input" >')
            html.append('<div class="layui-form-item">')
            html.append('<div class="layui-input-inline" style="margin:10px auto 0 auto;display: block;float: none;">')
            html.append('<button class="layui-btn" id="editSubmit" lay-submit="" lay-filter="updateTypeSubmit">立即提交</button>')
            html.append('<button type="reset" class="layui-btn layui-btn-primary">重置</button>')
            html.append('</div>')
            html.append('</div>')
            html.append('</form>')

            layer.open({
                type: 1,
                skin: 'layui-layer-rim', //加上边框
                area: '420px', //宽高
                title: '修改类别',
                content: html.toString()
            });
        },
        deleteData: function (id) {
            layer.confirm('您确定要删除吗？', {
                btn: ['确定', '取消'] //按钮
            }, function () {
                ajax({
                    url: reqMap.type.remove,
                    type: 'POST',
                    data: {
                        "id": id
                    },
                    success: function (resp) {
                        if (resp.success) {
                            layer.alert('删除成功!', {
                                closeBtn: 0,
                                icon: 1
                            }, function () {
                                refresh()
                            });
                        } else {
                            layer.alert("删除类型失败[" + resp.data + "] !", {icon: 5});
                        }
                    }
                });
            }, function () {

            });
        },
        reSort: function () {
            ajax({
                url: reqMap.type.reSort,
                type: "POST",
                data: {},
                success: function (resp) {
                    if (resp.success) {
                        layer.alert('刷新排序成功 !');
                    } else {
                        layer.alert('刷新排序失败[' + resp.data + '], 请联系管理人员!');
                    }
                }
            });
        }

    };
    exports('funcs', funcs);
});
