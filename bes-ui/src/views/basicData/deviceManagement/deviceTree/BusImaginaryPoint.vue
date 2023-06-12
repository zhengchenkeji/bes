  <template>
    <div class="white-body-view">
      <el-form :inline="true" ref="form" :model="form" label-width="80px" >
        <el-form-item label="名称"  >
          <el-input v-model="form.name"></el-input>
        </el-form-item>
      </el-form>
      <el-form v-if="add" :inline="true" label-width="80px" style="text-align: center">

        <el-form-item>
          <el-button type="primary" @click="onSubmit">新增</el-button>
        </el-form-item>
      </el-form>
    </div>
  </template>

  <script>
    export default {
      props: {
        //是否显示新增按钮
        add:{
          type: Boolean,
          default: false
        }
      },

      data () {
        return {
          form: {
            name: '',
            region: '',
            date1: '',
            date2: '',
            delivery: false,
            type: [],
            resource: '',
            desc: ''
          }
        }
      },
      watch: {
      },
      mounted () {
      },
      methods: {

      }
    }
  </script>
  <style lang="scss" scoped>
    .white-body-view {
      width: 100%;
      min-width: 320px;
    }
  </style>
