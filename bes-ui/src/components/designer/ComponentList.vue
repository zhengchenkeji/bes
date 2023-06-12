<template>
  <div class="attr-container">
    <!--        <p class="title">画布属性</p>-->
    <div class="v-common-attr">
      <el-collapse v-model="activeName" accordion>
        <el-collapse-item title="组件" name="component" class="componentList-container">
          <div class="component-list" @dragstart="handleDragStart">
            <div
              v-for="(item, index) in componentList"
              :key="index"
              class="list"
              draggable
              :data-index="index"
            >
              <el-tooltip class="item" effect="dark" :content="item.label" placement="left-start">
                <span class="iconfont" :class="'icon-' + item.icon"></span>
              </el-tooltip>
            </div>
          </div>
        </el-collapse-item>
      </el-collapse>
    </div>
  </div>

</template>

<script>
  import componentList from '@/custom-component/component-list'

  export default {
    data() {
      return {
        componentList,
        activeName: '',
      }
    },
    methods: {
      handleDragStart(e) {
        e.dataTransfer.setData('index', e.target.dataset.index)
      },
    },
  }
</script>

<style lang="scss" scoped>
  .component-list {
    height: 65%;
    padding: 10px;
    display: grid;
    grid-gap: 10px 19px;
    grid-template-columns: repeat(auto-fill, 60px);
    grid-template-rows: repeat(auto-fill, 40px);

    .title {
      text-align: center;
      margin-bottom: 10px;
      height: 40px;
      line-height: 40px;
      border-bottom: 2px solid #e4e7ed;
      font-size: 14px;
      font-weight: 500;
      color: #303133;
    }

    .list {
      width: 50px;
      height: 40px;
      border: 1px solid #ddd;
      cursor: grab;
      text-align: center;
      color: #333;
      padding: 2px 5px;
      display: flex;
      align-items: center;
      justify-content: center;

      &:active {
        cursor: grabbing;
      }

      .iconfont {
        margin-right: 4px;
        font-size: 32px;
      }
      .icon-a-tiaodengkaiqi_huaban1 {
        color: #29CE93;
      }
      .icon-wenben,
      .icon-biaoge {
        font-size: 32px;
      }

      .icon-tupian {
        font-size: 32px;
      }
    }
  }

  .componentList-container {
    .linkage-component {
      margin: 10px 0;
      border: 1px solid #ddd;
      padding: 10px;
      position: relative;
      padding-top: 24px;

      .div-guanbi {
        cursor: pointer;
        position: absolute;
        right: 10px;
        top: 3px;
        color: #888;
        border: 1px solid #ddd;
        border-radius: 50%;
        width: 18px;
        height: 18px;
        display: flex;
        align-items: center;
        justify-content: center;

        .iconfont {
          font-size: 10px;
        }
      }
    }

    .el-select {
      margin-bottom: 10px;
    }

    .attr-container {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin: 10px 0;

      .el-select {
        margin-bottom: 0;
      }

      & > div {
        width: 97px;
      }
      .icon-guanbi {
        cursor: pointer;
      }
    }
  }

  .v-common-attr {
    overflow: auto;
    padding: 10px 14px;
    padding-top: 0;
    height: 100%;
  }
</style>
