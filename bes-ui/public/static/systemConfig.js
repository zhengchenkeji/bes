/**
 * @author Athena-YangChao
 * 0 若依管理系统
 * 1 综合能源(碳管理)平台

名称用到得文件
 Logo.vue
 login.vue
 register.vue
 index.html

 * 每次更换 1.系统名称 2.系统页面 3.navBarShow显隐  只需要更改 systemType即可
 * navBarShow0==>bigScreen-大屏(暂时没有)---frontPage-前台(暂时没有)---doc-文档
 */
const systemConfig = {

  systemType: 1,

  title0: '若依管理系统',
  url0: 'ry',
  navBarShow0: {bigScreen: 'false', frontPage: 'true', doc: 'true',weatherView:'true',breadcrumb:'true'},
  title1: '综合能源(碳管理)平台',
  url1: "athena",
  navBarShow1: {bigScreen: 'false', frontPage: 'false', doc: 'false',weatherView:'false',breadcrumb:'false'},


  title: function (type) {
    if (type === 0) {
      return this.title0
    } else if (type === 1) {
      return this.title1
    }
  },
  url: function (type) {
    if (type === 0) {
      return this.url0
    } else if (type === 1) {
      return this.url1
    }
  },

  navBarShow: function (type) {
    if (type === 0) {
      return this.navBarShow0
    } else if (type === 1) {
      return this.navBarShow1
    }
  }
}

