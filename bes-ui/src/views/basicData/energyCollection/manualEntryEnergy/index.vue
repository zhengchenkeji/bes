<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="能耗节点" prop="name">
        <el-input v-model="queryParams.deviceName" placeholder="请选择能耗节点" >
          <template slot="append">
            <el-button type="primary"  @click="queryDevice">
              能耗节点
              <i class="el-icon-time el-icon--right"></i>
            </el-button>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item label="能耗类型" prop="company">
        <el-select  style="width: 100%"
                    v-model="queryParams.energyType">
          <el-option
            v-for="item in energyList"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="日期" prop="company" >
        <el-select  style="width: 35%"
                    v-model="queryParams.datetype">
          <el-option
            v-for="item in dataOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>

        <el-date-picker style="width: 60%;margin-left: 10px" v-if="queryParams.datetype==0"
          v-model="queryParams.energyCjsj"  value-format="yyyy-MM-dd"
          type="year"
          placeholder="选择年">
        </el-date-picker>


        <el-date-picker style="width: 60%;margin-left: 10px"  value-format="yyyy-MM-dd"  v-if="queryParams.datetype==1"
          v-model="queryParams.energyCjsj"
          type="month"
          placeholder="选择月">
        </el-date-picker>

        <el-date-picker style="width: 60%;margin-left: 10px"   value-format="yyyy-MM-dd" v-if="queryParams.datetype==2"
          v-model="queryParams.energyCjsj"
          type="date"
          placeholder="选择日期">
        </el-date-picker>

      </el-form-item>

      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['safetyWarning:AlarmNotifier:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
<!--        <el-button-->
<!--          type="success"-->
<!--          plain-->
<!--          icon="el-icon-edit"-->
<!--          size="mini"-->
<!--          :disabled="single"-->
<!--          @click="handleUpdate"-->
<!--          v-hasPermi="['safetyWarning:AlarmNotifier:edit']"-->
<!--        >修改</el-button>-->
        <el-button
          type="info"
          plain
          icon="el-icon-upload2"
          size="mini"
          @click="handleImport"
          v-hasPermi="['basicData:deviceTree:import']"
        >导入</el-button>

      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['safetyWarning:AlarmNotifier:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['safetyWarning:AlarmNotifier:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>
    <el-table v-loading="loading" :data="manualEntryEnergyList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="采集器" align="center" prop="controllerName" />
      <el-table-column label="能耗节点" align="center" prop="pointName" />
      <el-table-column label="能耗详情" align="center" >
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="getEnergyDetail(scope.row)"
          >查看详情</el-button>
        </template>
      </el-table-column>
      <el-table-column label="能源类型" align="center" prop="energyType" :formatter="energyTypeFormatter" >

      </el-table-column>
      <el-table-column label="采集时间" align="center" prop="energyCjsj" />

      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改告警接收组对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" :close-on-click-modal="false" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="110px">
<!--        <el-form-item label="能耗节点" prop="name">-->
<!--          <el-input v-model="form.name" placeholder="请选择能耗节点" />-->
<!--        </el-form-item>-->
        <el-form-item label="能耗节点" prop="deviceName">
          <el-input v-model="form.deviceName" readonly placeholder="请选择能耗节点" >
            <template slot="append">
              <el-button type="primary"  @click="selectDevice">
                能耗节点
                <i class="el-icon-time el-icon--right"></i>
              </el-button>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="所属能源"  prop="energyType">
          <el-select  style="width: 100%"
            v-model="form.energyType"
            @change="energyTypeChange"
          >
            <el-option
              v-for="item in energyList"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="采集参数" prop="paramsList">
          <el-select style="width: 100%" @change="paramChange"
            v-model="form.paramsList"
                     multiple
          >
            <el-option
              v-for="item in electricparamsList"
              :key="item.code"
              :label="item.name+'('+item.code+')'"
              :value="item.code"
            />
          </el-select>
        </el-form-item>

        <div v-for="(item,index) in form.electricparamsNameList" :key="index" >
          <el-form-item v-bind:label="item.name" :prop="`electricparamsNameList[${index}].energyValue`" :rules="[{required:true,message:'请输入能耗值',trigger:'blur'}]" >
            <el-input v-bind:name="item.paramCode" v-model="form.electricparamsNameList[index].energyValue"    style="width: 200px;"   placeholder="请输入能耗值" />
          </el-form-item>
<!--          {{item.code}}-->
<!--          {{item.name}}-->
        </div>
        <el-form-item label="采集时间" prop="energyCjsj">
          <el-date-picker value-format="yyyy-MM-dd HH:mm:ss"
            v-model="form.energyCjsj"
            type="datetime"
            placeholder="选择日期时间">
          </el-date-picker>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 用户导入对话框 -->
    <el-dialog :title="upload.title" :visible.sync="upload.open" width="400px" append-to-body>
      <el-upload
        ref="upload"
        :limit="1"
        accept=".xlsx, .xls"
        :headers="upload.headers"
        :action="upload.url"
        :disabled="upload.isUploading"
        :on-progress="handleFileUploadProgress"
        :on-success="handleFileSuccess"
        :auto-upload="false"
        drag
      >
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <div class="el-upload__tip text-center" slot="tip">
<!--          <div class="el-upload__tip" slot="tip">-->
<!--            <el-checkbox v-model="upload.updateSupport" /> 是否更新已经存在的用户数据-->
<!--          </div>-->
          <span>仅允许导入xls、xlsx格式文件。</span>
          <br/>
          <span>导入时请下载模板：</span>
          <el-link type="primary" :underline="false" style="font-size:12px;vertical-align: baseline;" @click="importTemplate">下载模板</el-link>

        </div>
      </el-upload>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitFileForm">确 定</el-button>
        <el-button @click="upload.open = false">取 消</el-button>
      </div>
    </el-dialog>
    <!--能耗值-->
    <el-dialog title="能耗详情" :visible.sync="pointTableVisible">
      <el-table :data="paramValueDataList">
        <el-table-column property="paramName" label="采集参数" ></el-table-column>
        <el-table-column property="energyValue" label="能耗值" ></el-table-column>
      </el-table>
<!--      <pagination-->
<!--        v-show="PointTotal>0"-->
<!--        :total="PointTotal"-->
<!--        :page.sync="pointqueryParams.pageNum"-->
<!--        :limit.sync="pointqueryParams.pageSize"-->
<!--        @pagination="pointListData"-->
<!--      />-->
    </el-dialog>

    <el-drawer size='20%'  :title="drawertitle" :visible.sync="visible"  direction="rtl">
      <!--支路,分户,分项-->
      <div id="tree-drawer" style="height: 600px;overflow: scroll;">
        <el-tree
          :data="treedata"
          :props="props"
          show-checkbox
          node-key="id"
          :check-strictly='true'
          :default-checked-keys="checkedkeys"
          ref="tree"
          @check="handleCheckChange">
        </el-tree>
      </div>
      <div style="text-align: center;margin-top: 50px;">
        <el-button type="primary" @click="savetree" >保存</el-button>
      </div>
    </el-drawer>
  </div>
</template>
<script>
  import { TreeSelect,listType,getElectricParams,addmanualEntryEnergy,getmanualEntryEnergyData,getEnergyDetailData,delEnergyDetailData } from "@/api/basicData/energyCollection/manualEntryEnergy/manualEntryEnergy";
  import {getToken} from "@/utils/auth";

  export default {
    name: "AlarmNotifier",
    data() {
      return {
        checkedkeys:[],
        queryVisible:false,
        pointTableVisible:false,//能耗列表 是否开启
        paramValueDataList:[],
        dataOptions: [{
          value: 0,
          label: '年'
        }, {
          value: 1,
          label: '月'
        }, {
          value: 2,
          label: '日'
        }],

        // 用户导入参数
        upload: {
          // 是否显示弹出层（用户导入）
          open: false,
          // 弹出层标题（用户导入）
          title: "",
          // 是否禁用上传
          isUploading: false,
          // 是否更新已经存在的用户数据
          updateSupport: 0,
          // 设置上传的请求头部
          headers: { Authorization: "Bearer " + getToken() },
          // 上传的地址
          url: process.env.VUE_APP_BASE_API + "/basicData/manualEntryEnergy/importData"
        },
        treedata:[],//树结构数据
        /**树结构*/
        props: {
          children: 'children',
          label: 'label'
        },
        //能源列表
        energyList: [],
        //所有能源类型列表
        energyAllList: [],

        manualEntryEnergyList:[],
        //采集参数列表
        electricparamsList: [],
        // electricparamsNameList:[],
        /**抽屉开关*/
        visible:false,
        /**选中设备id*/
        checkedId:"",
        /**选中设备名称*/
        checkname:"",
        drawertitle:"",//树结构名称


        // 遮罩层
        loading: true,
        // 非单个禁用
        single: true,
        // 非多个禁用
        multiple: true,
        // 显示搜索条件
        showSearch: true,
        // 总条数
        total: 0,
        // 弹出层标题
        title: "",
        // 是否显示弹出层
        open: false,
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          datetype:0,
          energyType:null,
          energyCjsj:null
        },
        // 表单参数
        form: {
          electricparamsNameList:[]
        },
        // 表单校验
        rules: {
          deviceName: [
            { required: true, message: "能耗节点不能为空" }
          ],
          energyType: [
            { required: true, message: "能源类型不能为空", trigger: "blur" }
          ],
          paramsList: [
            { required: true, message: "采集参数不能为空", trigger: "blur" }
          ],
          energyCjsj: [
            { required: true, message: "采集时间不能为空", trigger: "blur" }
          ],

        }
      };
    },
    created() {
      this.getEnergyType();
      this.TreeSelect();
      this.getList();
    },
    methods: {
      /** 下载模板操作 */
      importTemplate() {
        const iframe = document.createElement("iframe");
        //考虑线上环境是否能找到文件所在路径，
        //就得拿到路由的base（配置单页应用的基本路径）来进行判断
        if (this.$router.history.base) {
          iframe.src =
            this.$router.history.base + "/static/excel/手动录入能耗.xls";
        } else {
          iframe.src = "/static/excel/手动录入能耗.xls";
        }
        iframe.style.display = "none"; // 防止影响页面
        iframe.style.height = 0; // 防止影响页面
        document.body.appendChild(iframe); // 必写，iframe挂在到dom树上才会发请求
        // 定时删除节点
        setTimeout(() => {
          document.body.removeChild(iframe);
        }, 2000);
      },
      /**获取所有能源类型*/
      getenergyTypeAllList(){
      },
      queryDevice(){
        this.queryVisible=true;
        this.visible=true;
        this.$refs.tree.setCheckedKeys([]);

        // if(this.queryParams.pointTreeid!=null&&this.form.pointTreeid!=null){
        //
        // }else{
        //
        // }
        this.checkedkeys=[];
        this.checkedkeys.push(this.queryParams.pointTreeid);

      },
      energyTypeFormatter(row, column, cellValue, index){
        var label="";
        this.energyList.forEach(item=>{
          if(item.value==cellValue){
            label=item.label;
          }
        })
        return label;
      },
      getEnergyDetail(row){
        this.getEnergyDetailData(row.id);
      },
      getEnergyDetailData(id){
        getEnergyDetailData(id).then(response=>{
          this.paramValueDataList=response.rows;
          this.pointTableVisible=true;
        })
      },

      submitFileForm(){
        this.$refs.upload.submit();
      },
      // 文件上传中处理
      handleFileUploadProgress(event, file, fileList) {
        this.upload.isUploading = true;
      },
      // 文件上传成功处理
      handleFileSuccess(response, file, fileList) {
        this.upload.open = false;

        this.upload.open = false;
        this.upload.isUploading = false;
        this.$refs.upload.clearFiles();
        this.$alert("<div style='overflow: auto;overflow-x: hidden;max-height: 70vh;padding: 10px 20px 0;'>" + response.msg + "</div>", "导入结果", { dangerouslyUseHTMLString: true });
        this.getList();

      },

      /** 导入按钮操作 */
      handleImport() {
        this.upload.title = "用户导入";
        this.upload.open = true;
      },
      /**变更采集参数时触发*/
      paramChange(tag){

        const that = this;
        var showlist=[];
        if(this.form.electricparamsNameList.length>0){
          showlist=JSON.parse(JSON.stringify(this.form.electricparamsNameList))
        }
        that.form.electricparamsNameList=[];
        this.form.paramsList.forEach(function(item) {

          let save = true;
          showlist.forEach(function (showitem) {
            if (showitem.code == item) {
              that.form.electricparamsNameList.push(showitem);
              save=false;
            }
          })
          if(save){
            that.form.electricparamsNameList.push({paramCode:item,name:that.getElectricParamName(item),energyValue:""});
          }
        })

      },
      /**根据选中的采集参数处理*/
      getElectricParamName(code){
        let name = "";
        this.electricparamsList.forEach(function(item) {

          if(code==item.code){
            name=item.name;
          }
        });
        return name;
      },
      /**变更能源类型触发*/
      energyTypeChange(){
        if(this.checkedId==null||this.checkedId==""){
          this.$alert('当前未选择能耗节点！', '警告', {
            confirmButtonText: '确定',
            callback: action => {

            }
          })
          this.form.energyType=null;
          return;
        }
        this.getElectricParamslist();

      },
      // 根据所选的电表获取相对应的采集参数
      getElectricParamslist(meterid){
        const params={
          meterid:this.checkedId
        }
        getElectricParams(params).then(response=>{
          this.electricparamsList=response.data;
          /**清空采集参数集合值*/
          this.form.electricparamsNameList=[];
          this.form.paramsList=[];
        })
      },
      /** 获取园区列表及能源列表 */
       async getEnergyType() {
         await listType().then(response => {
          response.data.forEach(item => {
            this.energyList.push({value: item.code, label: item.name})
          });
        });
      },
      /**选择设备*/
      selectDevice(){
        this.visible=true;
        this.queryVisible=false;
        this.$refs.tree.setCheckedKeys([]);
        this.checkedkeys=[];

        this.checkedkeys.push(this.form.pointTreeid);


      },
      /**选择节点*/
      handleCheckChange(node, tree) {
        if (tree.checkedKeys.length == 0) {
          this.$refs.tree.setCheckedKeys([]);
          this.node = node;
          this.nodeisChecked = false;

        } else {
          this.$refs.tree.setCheckedKeys([]);
          this.$refs.tree.setCheckedKeys([node.id]);
          this.node = node;
          this.nodeisChecked = true;

        }
        this.form.energyType=null;

        this.checkedId=node.id
        this.checkname=node.label
      },
      /**获取设备树*/
       TreeSelect(){
         TreeSelect().then(response=>{
          response.data.forEach(val => {
            if (!val.energyNode) {
              val.disabled = true
              val.label=val.sysName;

            }else{
              val.label=val.sysName+"("+val.deviceTreeId+")";

            }
            val.id=val.deviceTreeId;
          })
          this.treedata=this.handleTree(response.data, "deviceTreeId", "deviceTreeFatherId");

        })
      },
      /**选择设备保存方法*/
      savetree(){

        if(this.checkedId==null||this.checkedId==""){
          this.$alert('当前未选择设备，无法保存！', '警告', {
            confirmButtonText: '确定',
            callback: action => {

            }
          })
          return;
        }

        if(this.queryVisible==true){
          this.$set(this.queryParams,'deviceName',this.checkname);
          this.$set(this.queryParams,'pointTreeid',this.checkedId);
          this.queryVisible=false;
          this.visible=false;

        }else {
          this.$set(this.form,'deviceName',this.checkname);
          this.$set(this.form,'pointTreeid',this.checkedId);
          this.visible=false;
        }


      },

      /** 查询告警接收组列表 */
      getList() {
        this.loading = true;
        getmanualEntryEnergyData(this.queryParams).then(response => {
          this.manualEntryEnergyList = response.rows;
          this.total = response.total;
          this.loading = false;
        });

      },

      // 取消按钮
      cancel() {
        this.open = false;
        this.reset();
      },
      // 表单重置
      reset() {
        this.form = {
          electricparamsNameList:[],
          pointTreeid:null,
          // id: null,
          // name: null,
          // homeAddress: null,
          // tel: null,
          // email: null,
          // company: null,
          // companyTel: null,
          // post: null,
          // groupId: null,
          // createTime: null,
          // updateTime: null,

        };
        this.resetForm("form");
      },
      /** 搜索按钮操作 */
      handleQuery() {
        console.log(this.queryParams)

        this.queryParams.pageNum = 1;
        this.getList();
      },
      /** 重置按钮操作 */
      resetQuery() {
        this.queryParams.deviceName=null;
        this.queryParams.energyCjsj=null;
        this.queryParams.dataType=1;
        this.queryParams.energyType=null;
        this.queryParams.pointTreeid=null;

        this.checkedId=null;
        this.checkedkeys=[];
        this.resetForm("queryParams");
        this.handleQuery();
      },
      // 多选框选中数据
      handleSelectionChange(selection) {
        this.ids = selection.map(item => item.id)
        this.single = selection.length!==1
        this.multiple = !selection.length
      },
      /** 新增按钮操作 */
      handleAdd() {
        this.reset();
        this.open = true;
        this.title = "添加告警接收组";
      },
      /** 修改按钮操作 */
      handleUpdate(row) {
        this.reset();
        const id = row.id || this.ids
        getAlarmNotifier(id).then(response => {
          this.form = response.data;
          var postarray= this.form.post.split(',')
          // console.log(this.form.post)
          for (let index = 0; index < postarray.length; index++) {
            postarray[index] =  postarray[index]*1;
          }
          this.form.post=postarray;
          this.open = true;
          this.title = "修改告警接收组";
        });
      },
      /** 提交按钮 */
      submitForm() {

        addmanualEntryEnergy(this.form).then(response=>{
          this.$modal.msgSuccess("新增成功");
          this.open = false;
          this.getList();

        })
        // console.log(this.energyValue);
        // console.log(this.form.electricparamsNameList)


        // // 字符串转为数组
        // this.form.post=this.form.post.join(",")
        //
        // this.$refs["form"].validate(valid => {
        //   if (valid) {
        //     if (this.form.id != null) {
        //       updateAlarmNotifier(this.form).then(response => {
        //         this.$modal.msgSuccess("修改成功");
        //         this.open = false;
        //         this.getList();
        //       });
        //     } else {
        //       addAlarmNotifier(this.form).then(response => {
        //         this.$modal.msgSuccess("新增成功");
        //         this.open = false;
        //         this.getList();
        //       });
        //     }
        //   }
        // });
      },
      /** 删除按钮操作 */
      handleDelete(row) {
        const ids = row.id || this.ids;
        this.$modal.confirm('是否确认删除选中手动录入能耗信息').then(function() {
          return delEnergyDetailData(ids);
        }).then(() => {
          this.getList();
          this.$modal.msgSuccess("删除成功");
        }).catch(() => {});
      },
      /** 导出按钮操作 */
      handleExport() {
        this.download('basicData/manualEntryEnergy/export', {
          ...this.queryParams
        }, `手动输入能耗数据.xlsx`)
      }
    }
  };
</script>
