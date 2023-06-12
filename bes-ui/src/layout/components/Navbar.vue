<template>
  <div class="navbar">
    <hamburger id="hamburger-container" :is-active="sidebar.opened" class="hamburger-container"
               @toggleClick="toggleSideBar"/>

    <breadcrumb id="breadcrumb-container" class="breadcrumb-container" v-if="!topNav"/>
    <top-nav id="topmenu-container" class="topmenu-container" v-if="topNav"/>

    <div class="right-menu">
      <template v-if="device !== 'mobile'">
        <search id="header-search" class="right-menu-item"/>

        <screenfull id="screenfull" class="right-menu-item hover-effect"/>

        <el-tooltip content="布局大小" effect="dark" placement="bottom">
          <size-select id="size-select" class="right-menu-item hover-effect"/>
        </el-tooltip>
      </template>

      <el-dropdown class="right-menu-item bell">
        <div>
          <el-badge :value="alarmNum" :max="99" class="bell_badge">
            <i class="el-icon-bell"></i>
          </el-badge>
        </div>
        <el-dropdown-menu slot="dropdown" class="el-dropdown-menu-bell">
          <!-- <el-tabs style="width: 400px;" v-model="activeName" @tab-click="handleClick" stretch>
             <el-tab-pane
               :key="item.title"
               v-for="(item, index) in tabeList"
               :label="item.name"
               :name="item.title"
               style="overflow: hidden"
             >
               <el-scrollbar>
                 <div class="warnItem" v-for="(item,index) in item.dataList" :key="index">
                   <div class="roundBg">
                     <i class="el-icon-message"></i>
                   </div>
                   <div class="description">
                     <p>{{item.time}}{{item.dir}}{{item.description}}</p>
                     <div class="desBottom">
                       <span>{{item.time}}</span>
                       <h5>{{item.status === 0?'未读':'未处理'}}</h5>
                     </div>
                   </div>
                 </div>
               </el-scrollbar>
             </el-tab-pane>
           </el-tabs>-->

          <el-tabs style="width: 400px;" v-model="activeName" @tab-click="handleClick" stretch>
            <el-tab-pane label="重大" name="first">
              <el-scrollbar class="scrollbar">
                <div class="warnItem" v-for="(item,index) in dataList" :key="index">
                  <div class="roundBg">
                    <i class="el-icon-message"></i>
                  </div>
                  <div class="description">
                    <p>报警名称:{{ item.alarmName }}</p>
                    <p>报警描述:{{ item.azwz }}</p>
                    <div class="desBottom">
                      <span>{{ item.lastTime }}</span>
                      <h5 style="cursor: pointer" @click="handlingAlarms">去处理</h5>
                    </div>
                  </div>
                </div>
              </el-scrollbar>
            </el-tab-pane>
            <el-tab-pane label="较大" name="second">
              <el-scrollbar class="scrollbar">
                <div class="warnItem" v-for="(item,index) in dataList" :key="index">
                  <div class="roundBg">
                    <i class="el-icon-message"></i>
                  </div>
                  <div class="description">
                    <p>报警名称:{{ item.alarmName }}</p>
                    <p>报警描述:{{ item.azwz }}</p>
                    <div class="desBottom">
                      <span>{{ item.lastTime }}</span>
                      <h5 style="cursor: pointer" @click="handlingAlarms">去处理</h5>
                    </div>
                  </div>
                </div>
              </el-scrollbar>
            </el-tab-pane>
            <el-tab-pane label="一般" name="third">
              <el-scrollbar >
                <div class="warnItem" v-for="(item,index) in dataList" :key="index">
                  <div class="roundBg">
                    <i class="el-icon-message"></i>
                  </div>
                  <div class="description">
                    <p>报警名称:{{ item.alarmName }}</p>
                    <p>报警描述:{{ item.azwz }}</p>
                    <div class="desBottom">
                      <span>{{ item.lastTime }}</span>
                      <h5 style="cursor: pointer" @click="handlingAlarms">去处理</h5>
                    </div>
                  </div>
                </div>
              </el-scrollbar>
            </el-tab-pane>
          </el-tabs>

        </el-dropdown-menu>
      </el-dropdown>

      <el-dropdown class="avatar-container right-menu-item hover-effect" trigger="click">
        <div class="avatar-wrapper">
          <img :src="avatar" class="user-avatar">
          <i class="el-icon-caret-bottom"/>
        </div>
        <el-dropdown-menu slot="dropdown">
          <router-link to="/user/profile">
            <el-dropdown-item>个人中心</el-dropdown-item>
          </router-link>
          <el-dropdown-item @click.native="setting = true">
            <span>布局设置</span>
          </el-dropdown-item>
          <el-dropdown-item divided @click.native="logout">
            <span>退出登录</span>
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>
  </div>
</template>

<script>
  import {mapGetters, mapState} from 'vuex'
  import {alarmCount, alarmInfo} from '@/api/basicData/index';
  import Breadcrumb from '@/components/Breadcrumb'
  import TopNav from '@/components/TopNav'
  import Hamburger from '@/components/Hamburger'
  import Screenfull from '@/components/Screenfull'
  import SizeSelect from '@/components/SizeSelect'
  import Search from '@/components/HeaderSearch'
  import RuoYiGit from '@/components/RuoYi/Git'
  import RuoYiDoc from '@/components/RuoYi/Doc'
  import TextBroadcast from '@/components/TextBroadcast/index'

  export default {
    components: {
      TextBroadcast,
      Breadcrumb,
      TopNav,
      Hamburger,
      Screenfull,
      SizeSelect,
      Search,
      RuoYiGit,
      RuoYiDoc
    },
    computed: {
      ...mapGetters([
        'sidebar',
        'avatar',
        'device'
      ]),
      ...mapState({
        //报警个数
        alarmCount: state => state.websocket.alarmCount,
        //报警播报
        alarmMsg: state => state.websocket.alarmMsg,
      }),
      setting: {
        get() {
          return this.$store.state.settings.showSettings
        },
        set(val) {
          this.$store.dispatch('settings/changeSetting', {
            key: 'showSettings',
            value: val
          })
        }
      },
      topNav: {
        get() {
          return this.$store.state.settings.topNav
        }
      },
      sideTheme: {
        get() {
          return this.$store.state.settings.sideTheme
        }
      }
    },
    data() {
      return {
        message: "",
        activeName: 'first',
        dataList: [],
        iconWhatColor: {},
        alarmNum: 0,
        level: 3, //优先查询重大报警
        soundType: null,
        soundToken: null,
        appkey: null,
        tabeList: [
          {
            name: '特大', title: 'first',
            dataList: [
              {time: '2021-11-25', description: '发生行人横穿(工作人员)事件', dir: '北京方向', PileNo: 'K500+120', status: 1,},
              {time: '2021-11-25', description: '发生行人横穿(工作人员)事件', dir: '北京方向', PileNo: 'K500+120', status: 1,},
              {time: '2021-11-25', description: '发生行人横穿(工作人员)事件', dir: '北京方向', PileNo: 'K500+120', status: 1,},
              {time: '2021-11-25', description: '发生行人横穿(工作人员)事件', dir: '北京方向', PileNo: 'K500+120', status: 1,},
              {time: '2021-11-25', description: '发生行人横穿(工作人员)事件', dir: '北京方向', PileNo: 'K500+120', status: 1,},
              {time: '2021-11-25', description: '发生行人横穿(工作人员)事件', dir: '北京方向', PileNo: 'K500+120', status: 1,},
              {time: '2021-11-25', description: '发生行人横穿(工作人员)事件', dir: '北京方向', PileNo: 'K500+120', status: 1,}
            ]
          },
          {
            name: '重大', title: 'second',
            dataList: [
              {time: '2021-11-25', description: '发生行人横穿(工作人员)事件', dir: '北京方向', PileNo: 'K500+120', status: 0,},
              {time: '2021-11-25', description: '发生行人横穿(工作人员)事件', dir: '北京方向', PileNo: 'K500+120', status: 0,},
              {time: '2021-11-25', description: '发生行人横穿(工作人员)事件', dir: '北京方向', PileNo: 'K500+120', status: 0,},
              {time: '2021-11-25', description: '发生行人横穿(工作人员)事件', dir: '北京方向', PileNo: 'K500+120', status: 0,},
              {time: '2021-11-25', description: '发生行人横穿(工作人员)事件', dir: '北京方向', PileNo: 'K500+120', status: 0,},
            ]
          },
          {
            name: '一般', title: 'third',
            dataList: [
              {time: '2021-11-25', description: '发生行人横穿(工作人员)事件', dir: '北京方向', PileNo: 'K500+120', status: 0,},
              {time: '2021-11-25', description: '发生行人横穿(工作人员)事件', dir: '北京方向', PileNo: 'K500+120', status: 0,},
              {time: '2021-11-25', description: '发生行人横穿(工作人员)事件', dir: '北京方向', PileNo: 'K500+120', status: 0,},
              {time: '2021-11-25', description: '发生行人横穿(工作人员)事件', dir: '北京方向', PileNo: 'K500+120', status: 0,},
              {time: '2021-11-25', description: '发生行人横穿(工作人员)事件', dir: '北京方向', PileNo: 'K500+120', status: 1,}
            ]
          },
          {
            name: '轻微', title: 'fourth',
            dataList: [
              {time: '2021-11-25', description: '发生行人横穿(工作人员)事件', dir: '北京方向', PileNo: 'K500+120', status: 1,},
              {time: '2021-11-25', description: '发生行人横穿(工作人员)事件', dir: '北京方向', PileNo: 'K500+120', status: 1,},
              {time: '2021-11-25', description: '发生行人横穿(工作人员)事件', dir: '北京方向', PileNo: 'K500+120', status: 1,},
              {time: '2021-11-25', description: '发生行人横穿(工作人员)事件', dir: '北京方向', PileNo: 'K500+120', status: 1,},
              {time: '2021-11-25', description: '发生行人横穿(工作人员)事件', dir: '北京方向', PileNo: 'K500+120', status: 1,},
              {time: '2021-11-25', description: '发生行人横穿(工作人员)事件', dir: '北京方向', PileNo: 'K500+120', status: 1,}
            ]
          },
        ],
      }
    },
    watch: {
      sideTheme(val) {
        this.watchTheme();
      },
      alarmCount(data) {
        if (data === null) {
          return
        }
        this.alarmNum = data
        //刷新为重大
        this.level = "3"
        this.activeName = "first"
        this.alarmInfo(this.level);
        // this.$refs.audio.currentTime = 0; //从头开始播放提示音
        // this.$refs.audio.play(); //播放提示音
        this.$store.commit('ALARM_COUNT', null)
      },
      /************报警播报 暂未完善************/
      alarmMsg(data) {
        // console.log("触发报警",data)
        var jsonObj = JSON.parse(data);

        let nowtime = this.getRealTime()

        if (!jsonObj.message) {
          return
        }
        this.soundToken = jsonObj.token;
        this.soundType = jsonObj.type;
        // this.message=jsonObj.message + " 告警时间：" + nowtime;
        this.message = jsonObj.message + "$" + Date.now();

        this.appkey = jsonObj.appkey;
        // this.$refs.textAudio.playoneAudio(jsonObj.token, jsonObj.message, jsonObj.type)

        // this.$refs.textAudio.playoneAudio(jsonObj.token, this.message, jsonObj.type)
      }
    },
    create() {
      this.watchTheme();
    },
    mounted() {
      this.alarmInfoCount();
      this.alarmInfo();
    },
    methods: {
      //格式化时间
      getRealTime() {
        let myDate = new Date();	//创建Date对象
        let Y = myDate.getFullYear();   //获取当前完整年份
        let M = myDate.getMonth() + 1;  //获取当前月份
        let D = myDate.getDate();   //获取当前日1-31
        let H = myDate.getHours();  //获取当前小时
        let i = myDate.getMinutes();    //获取当前分钟
        let s = myDate.getSeconds();    //获取当前秒数
        // 月份不足10补0
        if (M < 10) {
          M = '0' + M;
        }
        // 日不足10补0
        if (D < 10) {
          D = '0' + D;
        }
        // 小时不足10补0
        if (H < 10) {
          H = '0' + H;
        }
        // 分钟不足10补0
        if (i < 10) {
          i = '0' + i;
        }
        // 秒数不足10补0
        if (s < 10) {
          s = '0' + s;
        }
        // 拼接日期分隔符根据自己的需要来修改
        let nowDate = Y + '-' + M + '-' + D + ' ' + H + ':' + i + ':' + s;
        return nowDate;
      },
      /*******获取报警个数***********/
      alarmInfoCount() {
        alarmCount()
          .then((data) => {
            this.alarmNum = data.data;
          })
      },
      //报警处理
      handlingAlarms() {
        this.$router.push("/basicData/safetyWarning/alarmRealtime")
      },
      //查询报警信息
      alarmInfo(level) {
        if (level) {
          this.level = level;
        }
        alarmInfo({level: this.level})
          .then((data) => {
            this.dataList = data.data;
          })
      },
      watchTheme() {
        // blue主题下，topnav自动改为false
        if (this.topNav && this.sideTheme != 'theme-blue') {
          switch (this.sideTheme) {
            case 'theme-dark':
              this.iconWhatColor = {color: '#ffffff'}
              break;
            case 'theme-light':
              this.iconWhatColor = {color: '#000'}
              break;
          }
        } else if (this.topNav == true && this.sideTheme == 'theme-blue') {
          this.iconWhatColor = {color: '#ffffff'}
        } else {
          this.iconWhatColor = {color: '#666'}
        }
      },
      toggleSideBar() {
        this.$store.dispatch('app/toggleSideBar')
      },
      async logout() {
        this.$confirm('确定注销并退出系统吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$store.dispatch('LogOut').then(() => {
            location.href = '/index';
          })
        }).catch(() => {
        });
      },
      handleClick(tab) {
        var str = tab._props.name
        if (str == "first") {
          this.level = "3"
        } else if (str == "second") {
          this.level = "2"
        } else {
          this.level = "1"
        }
        alarmInfo({level: this.level})
          .then((data) => {
            this.dataList = data.data;
          })
      }
    }
  }
</script>

<style lang="scss" scoped>
  .navbar {
    height: 50px;
    overflow: hidden;
    position: relative;
    background: #fff;
    box-shadow: 0 1px 4px rgba(0, 21, 41, .08);

    .hamburger-container {
      line-height: 46px;
      height: 100%;
      float: left;
      cursor: pointer;
      transition: background .3s;
      -webkit-tap-highlight-color: transparent;

      &:hover {
        background: rgba(0, 0, 0, .025)
      }
    }

    .breadcrumb-container {
      float: left;
    }

    .topmenu-container {
      position: absolute;
      left: 50px;
    }

    .errLog-container {
      display: inline-block;
      vertical-align: top;
    }

    .right-menu {
      float: right;
      height: 100%;
      line-height: 50px;

      &:focus {
        outline: none;
      }

      .right-menu-item {
        display: inline-block;
        padding: 0 8px;
        height: 100%;
        font-size: 18px;
        color: #5a5e66;
        vertical-align: text-bottom;

        &.hover-effect {
          cursor: pointer;
          transition: background .3s;

          &:hover {
            background: rgba(0, 0, 0, .025)
          }
        }
      }

      .avatar-container {
        margin-right: 30px;

        .avatar-wrapper {
          margin-top: 5px;
          position: relative;

          .user-avatar {
            cursor: pointer;
            width: 40px;
            height: 40px;
            border-radius: 10px;
          }

          .el-icon-caret-bottom {
            cursor: pointer;
            position: absolute;
            right: -20px;
            top: 25px;
            font-size: 12px;
          }

        }
      }

      .bell {
        margin-right: 15px;

        .bell_badge {
          ::v-deep .el-badge__content {
            top: 13px !important;
            display: flex;
            align-items: center;
            color: white;
          }
        }

        .el-icon-bell {
          font-size: 21px;
        }

        .el-dropdown-menu-bell {
          background-color: rgba(255, 255, 255);

          .popper__arrow::after {
            border-bottom-color: #1d58a9e6;
          }
        }
      }
    }

  }

  .desBottom {
    display: flex;
    justify-content: space-between;
    align-items: center;
    color: #8c939d;
    margin-top: -10px;
  }

  .el-tabs__content {
    width: 350px;
  }

  .el-tab-pane {
    height: 400px;
    overflow-x: hidden;
  }

  .warnItem {
    margin-right: 17px;
    margin-left: 17px;
    display: -webkit-box;
    display: -ms-flexbox;
    display: flex;
    -webkit-box-pack: center;
    -ms-flex-pack: center;
    justify-content: center;
    -webkit-box-align: center;
    -ms-flex-align: center;
    align-items: center;
    color: #000000;
    -webkit-box-sizing: border-box;
    box-sizing: border-box;
    padding: 15px;
    height: 100px;

  }

  .roundBg {
    background-color: #3391e5;
    border-radius: 40px;
    padding: 3%;
    -webkit-border-radius: 40px;
    -moz-border-radius: 40px;
    -ms-border-radius: 40px;
    -o-border-radius: 40px;
    color: #f8f8f8;
  }

  .description {
    font-size: 14px;
    padding-left: 10%;
    -webkit-box-sizing: border-box;
    box-sizing: border-box;
    margin-bottom: -10px;
  }

  .el-scrollbar {
    height: 100%;

    ::v-deep .el-scrollbar__wrap {
      overflow-x: hidden;
    }
  }

</style>
