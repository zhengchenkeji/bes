<template>
  <div class="app-container" style="padding:0">
    <div class="data-trees-style data-trees-style-hasvy">
      <ul id="treeDemo" class="ztree" style="padding-top:13px"></ul>
    </div>
  </div>
  <!-- <div class="panel-bottom">

   </div>-->
  <!--ul为存放树结构的位置，同一个页面如果有多个树结构，id不能重复（id本身也不能重复），否则就会遇到一个问题是你加载第二个树的时候，会发现第二个树没有渲染出来，因为他把第一个替换掉了
-->
</template>

<script>
  import 'jquery'
  import "../../../public/static/js/jquery.ztree.core"
  import '../../../public/static/js/jquery.ztree.all'
  import '../../../public/static/css/zTreeStyle/zTreeStyle.css'
  // import "../../../public/static/css/demo.css"
  export default {
    name: 'ZTree',
    data() {
      return {
        ztreeObj: null,
        setting: {
          treeId: 'id',
          data: {
            simpleData: {
              enable: true,
              idKey: 'id',
              pIdKey: 'pId',
              rootPId: '-'
            },
            key: {
              isParent: 'parent',
              name: 'name',
              title: 'name'
            }
          },
          callback: {
            // 树的点击事件
            // beforeClick: this.beforeClick,
            // onClick: this.zTreeOnClick,
            // onAsyncSuccess: this.zTreeOnAsyncSuccess,//异步加载成功的fun
            beforeAsync: this.zTreeBeforeAsync,//异步加载前的回调
            onExpand: this.expandNode//节点展开回调
          },
          edit: {//是否支持拖拽，enable我改成了false,代表此功能禁用，也可以直接删除，为了防止后续他们提这个需求，所以我还是写上了
            drag: {
              isMove: true,
              prev: true,
              next: true,
              inner: true
            },
            enable: false,
            showRemoveBtn: false,
            showRenameBtn: false
          },
          view: {
            addHoverDom: this.addhoverdom,//ztree提供的可以自定义添加dom
            removeHoverDom: this.removehoverdom,//和addHoverDom成对出现，离开节点时需要移除自定义的dom
            fontCss: function(treeId, treeNode) {//设置所有节点的样式，我这里的代码的意思是，当前节点是否高亮（树节点搜索的时候会高亮命中的节点），高亮就设置节点高亮样式，否则就是普通样式
              return (!!treeNode.highlight) ? {
                'backgroundColor': '#F6F7F8',
                'display': 'inline-block',
                'width': '95%',
                'min-width': '225px',
                'padding': '3px 0'
              } : {
                color: '#000000', 'font-weight': 'normal'
              }
            },
            showLine: false,
            showIcon: false,
            selectedMulti: false,
            dblClickExpand: false,
            // addDiyDom: addDiyDom,
            showTitle: true //是否显示titie属性（就是鼠标放到节点上是否显示html元素的title属性）
          }
          // async:{//节点很多的情况下设置懒加载
          //   enable:false,//是否开启异步加载模式
          //   contentType: "application/json",//Ajax 提交参数的数据类型
          //   dataType: "json",//Ajax 获取的数据类型
          //   url:'/aa/bbb/ccc/loadTree',//点击树的展开节点，会重新加载子节点，这里是请求的url地址
          //   type:'POST',//当前的请求类型
          //   // autoParam:['id=parentId'],//将节点的pId值作为参数传递
          //   // otherParam:{'userId':()=>{return this.userId;},'userName':()=>{return this.userName;},'tenantId':()=>{return this.tenantId;}}
          //   otherParam:{'userId':this.userId,'userName':this.userName,'tenantId':this.tenantId,'parentId':'-'},//每次异步请求携带的参数
          //   dataFilter:function(treeId, parentNode, resp){
          //     sessionStorage.setItem('tongbunodes',JSON.stringify(resp.jsSubjects.children));
          //     return resp.subjects;
          //   }//对 Ajax 返回数据进行预处理的函数,就是异步加载返回的数据你可以处理一下再用
          // }
        },
        zNodes: [
          {
            name: 'test1', open: true, children: [
              { name: 'test1_1' }, { name: 'test1_2' }]
          },
          {
            name: 'test2', open: true, children: [
              { name: 'test2_1' }, { name: 'test2_2' }]
          }
        ]
      }
    },
    mounted() {
      this.ztreeObj = $.fn.zTree.init($('#treeDemo'), this.setting, this.zNodes)
      // this.ClientRect = $('#panel-top')[0].getBoundingClientRect()
//getBoundingClientRect获得dom元素在页面上的位置，无关定位，卷曲高度等，目的是为了获得“专题目录”这个固定定位的元素在页面中的位置，而当树节点滚动加载的时候，也可以使用同样的方法获得当前树节点在页面的位置，两者之间的差值是不变的，就可以将当前位置的第一个（并非dom结构中的第一个位置）的功能提示信息放到下面
    },
    methods: {
      beforeClick(treeId, treeNode) {
        if (treeNode.level == 0) {
          var zTree = $.fn.zTree.getZTreeObj('treeDemo')
          zTree.expandNode(treeNode)
          return false
        }
        return true
      },
      zTreeBeforeAsync(treeId, treeNode) {
        if (this.ztreeObj) {//树节点对象是否存在，如果存在，说明已经加载了根节点，通过点击展开节点，开启加载下一层级节点信息，这个时候传递的上级节点id的参数需要发生变化
          if (treeNode) {
            this.ztreeObj.setting.async.otherParam['parentId'] = treeNode.id
          } else {
//说明加载的是第一级根节点，无上级节点信息，和后台约束好传递什么，这里我传‘-’
            this.ztreeObj.setting.async.otherParam['parentId'] = '-'
          }
        }
        return true
      },
      addhoverdom(treeId, treeNode) {
        //this.removehoverdom();
        let _this = this
        // treeId 对应的是当前 tree dom 元素的 id
        // treeNode 是当前节点的数据
        var aObj = $('#' + treeNode.tId + '_a') // 获取节点 dom
        let spanObj = $('#' + treeNode.tId + '_a').find('.node_name')
        if ($('#diyBtnGroup').length > 0) return
        // 查看是否存在自定义的按钮组，因为 addHoverDom 会触发多次                          <p class="toolnames" id='diyBtn_${treeNode.id}_names'>${treeNode.name}</p>      <li id='diyBtn_space_${treeNode.id}'> </li>
        // var editStr = `<div id='diyBtnGroup'>
        //
        //                     <ol>
        //                         <li class='mydiydiv hot' id='diyBtn_${treeNode.id}_hot' οnfοcus='this.blur();'><span class="tooltips hothover">热点</span></li>
        //                         <li class='mydiydiv del'  id='diyBtn_${treeNode.id}_delete' οnfοcus='this.blur();'><span class="tooltips deletehover">删除</span></li>
        //                         <li class='mydiydiv mod' id='diyBtn_${treeNode.id}_modify' οnfοcus='this.blur();'><span class="tooltips edithover">编辑</span></li>
        //                         <li class='mydiydiv offline'  id='diyBtn_${treeNode.id}_offline' οnfοcus='this.blur();'><span class="tooltips offhover">下线</span></li>
        //                         <li class='mydiydiv online' id='diyBtn_${treeNode.id}_online' οnfοcus='this.blur();'><span class="tooltips onhover">上线</span></li>
        //                         <li class='mydiydiv add'  id='diyBtn_${treeNode.id}_add' οnfοcus='this.blur();'><span class="tooltips addhover">新建</span></li>
        //                     </ol>
        //                 </div>`;

        var oBtn = document.createElement('button')
        oBtn.setAttribute('id', 'diyBtnGroup')
        oBtn.setAttribute('class', 'btn sbtreeNodeBtn')
        oBtn.setAttribute('value', '11')
        oBtn.setAttribute('nodeType', '1')
        oBtn.setAttribute('onclick', 'basedatamanage_eqmanage_eqconfiguration_sbdy.btnMenuClick(this)')
        oBtn.innerText = '测试'
        spanObj.append(oBtn)

        // spanObj.append(editStr);
        if (treeNode.isHover) {//这里是核心代码，遇到一个贼大的坑，搞了两天，hover上去之后，图标可以显示出来，但是如果是点击某一个节点，
          // 再将鼠标移入到别的dom结构，会发现小图标不会出现，isHover是节点自带属性，hover上去的时候，给a标签设置hover属性，
          // 然后给a绑定一个鼠标离开事件， 设置鼠标从节点移出时，删除由 addHoverDom 增加的按钮
          aObj.css({ 'backgroundColor': '#F6F7F8', 'display': 'inline-block', 'width': '95%', 'min-width': '225px' })
          aObj.on('mouseleave', function() {
            _this.removehoverdom(null, treeNode)
          })
        }
        $('#diyBtnGroup').css({ 'backgroundColor': 'red' })
        var bObj = $('#' + treeNode.tId + '_icon') // 获取节点 dom
        bObj.css({ 'vertical-align': 'inherit' })//避免鼠标浮上去之后，发生抖动（巨大一个坑，也搞了两天）
        var btnHot = $('#diyBtn_' + treeNode.id + '_hot')
        var btnDelete = $('#diyBtn_' + treeNode.id + '_delete')
        var btnModify = $('#diyBtn_' + treeNode.id + '_modify')
        var btnAdd = $('#diyBtn_' + treeNode.id + '_add')
        var btnOffline = $('#diyBtn_' + treeNode.id + '_offline')
        var btnOnline = $('#diyBtn_' + treeNode.id + '_online')
        // 小图标点击事件
        if (btnDelete) {
          btnDelete.bind('click', function() {
            _this.delete(treeNode)
          })
        }
        if (btnAdd) {
          btnAdd.bind('click', function() {
            _this.add(treeNode)
          })
        }
        if (btnModify) {
          btnModify.bind('click', function() {
            _this.modify(treeNode)
          })
        }
        if (btnHot) {
          btnHot.bind('click', function() {
            _this.hot(treeNode)
          })
        }
        if (btnOffline) {
          btnOffline.bind('click', function() {
            _this.offline(treeNode)
          })
        }
        if (btnOnline) {
          btnOnline.bind('click', function() {
            _this.online(treeNode)
          })
        }
        // 小图标hover事件
        if (btnDelete) {
          btnDelete.bind('mouseover', function(e) {
            _this.deleteiconhover(treeNode, e)
          })
        }
        if (btnAdd) {
          btnAdd.bind('mouseover', function(e) {
            _this.addiconhover(treeNode, e)
          })
        }
        if (btnModify) {
          btnModify.bind('mouseover', function(e) {
            _this.modifyiconhover(treeNode, e)
          })
        }
        if (btnHot) {
          btnHot.bind('mouseover', function(e) {
            _this.hoticonhover(treeNode, e)
          })
        }
        if (btnOffline) {
          btnOffline.bind('mouseover', function(e) {
            _this.offlineiconhover(treeNode, e)
          })
        }
        if (btnOnline) {
          btnOnline.bind('mouseover', function(e) {
            _this.onlineiconhover(treeNode, e)
          })
        }
        // 鼠标离开事件
        if (btnDelete) {
          btnDelete.bind('mouseleave', function() {
            _this.deleteiconleave(treeNode)
          })
        }
        if (btnAdd) {
          btnAdd.bind('mouseleave', function() {
            _this.addiconleave(treeNode)
          })
        }
        if (btnModify) {
          btnModify.bind('mouseleave', function() {
            _this.modifyiconleave(treeNode)
          })
        }
        if (btnHot) {
          btnHot.bind('mouseleave', function() {
            _this.hoticonleave(treeNode)
          })
        }
        if (btnOffline) {
          btnOffline.bind('mouseleave', function() {
            _this.offlineiconleave(treeNode)
          })
        }
        if (btnOnline) {
          btnOnline.bind('mouseleave', function() {
            _this.onlineiconleave(treeNode)
          })
        }
      },
      removehoverdom(treeId, treeNode) {
        // 为了方便删除整个 button 组，上面我用 #diyBtnGroup 这个包了起来，这里直接删除外层即可，不用挨个找了。
        $('#diyBtnGroup').unbind().remove()
        var aObj = $('#' + treeNode.tId + '_a')
        if (!treeNode.highlight) {
          aObj.css({ 'backgroundColor': '#fff' })
        }

      },
      deleteiconhover(treeNode, event) {
        let domname = $('.deletehover')
        domname.css({ 'display': 'inline-block' })
        this.measurement(domname, event)
        this.isHovering = treeNode.name || treeNode.text//记录当前鼠标所在的节点的title,原因是鼠标移入节点，我希望是弹出title，移到小图标上的时候，由于它属于这个dom结构，也会出现title提示和功能提示框，会发生遮挡
        this.operNode = treeNode
        $('#' + treeNode.tId + '_a').attr('title', '')//鼠标移入小图标的时候移除title属性
      },
      deleteiconleave() {
        $('.deletehover').css({ 'display': 'none' })
        $('#' + this.operNode.tId + '_a').attr('title', this.isHovering)//鼠标离开的时候加上title属性（为我聪明的头脑骄傲）
      },
//上面提到的，如果是靠近“专题目录最近的一个，将功能提示移到下方”
//       measurement(domname,event){
//         // let coordinates = $('#diyBtnGroup')[0].getBoundingClientRect()
//         if(coordinates.bottom - this.ClientRect.bottom < 35){
//           domname.css({'display':'inline-block','top':'25px'})
//         } else {
//           domname.css({'display':'inline-block','top':'-35px'})
//         }
//       },
    }
  }
</script>

<style scoped>
</style>

