<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="建筑名称" prop="buildName">
        <el-input
          v-model="queryParams.buildName"
          placeholder="请输入建筑名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="所属数据中心" prop="dataCenterId" label-width="100px">
        <el-select v-model="queryParams.dataCenterId">
          <el-option
            v-for="item in dataCenterBaseInfoList"
            :key="item.id"
            :label="item.dataCenterName"
            :value="item.id">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="所属建筑群" prop="buildGroupId" label-width="85px">
        <el-select v-model="queryParams.buildGroupId">
          <el-option
            v-for="item in buildGroupList"
            :key="item.id"
            :label="item.buildGroupName"
            :value="item.id">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="所属园区" prop="parkId">
        <el-select v-model="queryParams.parkId">
          <el-option
            v-for="item in parkList"
            :key="item.code"
            :label="item.name"
            :value="item.code">
          </el-option>
        </el-select>
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
          v-hasPermi="['basicData:buildBaseInfo:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['basicData:buildBaseInfo:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['basicData:buildBaseInfo:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['basicData:buildBaseInfo:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="buildBaseInfoList" @selection-change="handleSelectionChange" style="overflow-y: auto;max-height: 72vh;">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="建筑名称" align="center" prop="buildName" />
      <el-table-column label="建筑字母别名" align="center" prop="aliasName" />
      <el-table-column label="所属数据中心" align="center" prop="dataCenterId" >
        <template slot-scope="scope">
          {{ getDataCenterName(scope.row.dataCenterId,dataCenterBaseInfoList) }}
        </template>
      </el-table-column>
      <el-table-column label="所属建筑群" align="center" prop="buildGroupId" >
        <template slot-scope="scope">
          {{ getBuildGroupName(scope.row.buildGroupId,buildGroupList) }}
        </template>
      </el-table-column>
      <el-table-column label="所属园区" align="center" prop="parkId" >
        <template slot-scope="scope">
          {{ getParkName(scope.row.parkId,parkList) }}
        </template>
      </el-table-column>
      <el-table-column label="业主" align="center" prop="buildOwner" />
      <el-table-column label="监测状态" align="center" prop="state" >
        <template slot-scope="scope">
          <span v-if="scope.row.state == 0">停用监测</span>
          <span v-if="scope.row.state == 1">启用监测</span>
        </template>
      </el-table-column>
      <el-table-column label="所属行政区划" align="center" prop="districtCode" />
      <el-table-column label="建筑地址" align="center" prop="buildAddr" />
      <el-table-column label="监测方案设计单位" align="center" prop="designDept" />
      <el-table-column label="监测工程实施单位" align="center" prop="workDept" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['basicData:buildBaseInfo:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['basicData:buildBaseInfo:remove']"
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

    <!-- 添加或修改建筑基本项数据对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="800px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="150px" inline>
        <el-form-item label="建筑名称" prop="buildName">
          <el-input v-model="form.buildName" placeholder="请输入建筑名称" maxlength="24" show-word-limit  style="width: 202px"/>
        </el-form-item>
        <el-form-item label="建筑字母别名" prop="aliasName">
          <el-input v-model="form.aliasName" placeholder="请输入建筑字母别名"  maxlength="24" show-word-limit />
        </el-form-item>
        <el-form-item label="所属数据中心" prop="dataCenterId">
          <el-select v-model="form.dataCenterId" :disabled="form.id !== null && form.id !== undefined && form.id !== ''"  style="width: 202px">
            <el-option
              v-for="item in dataCenterBaseInfoList"
              :key="item.id"
              :label="item.dataCenterName"
              :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="所属建筑群" prop="buildGroupId">
          <el-select v-model="form.buildGroupId" :disabled="form.id !== null && form.id !== undefined && form.id !== ''"  style="width: 202px">
            <el-option
              v-for="item in buildGroupList"
              :key="item.id"
              :label="item.buildGroupName"
              :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="所属园区" prop="parkId">
          <el-select v-model="form.parkId" :disabled="form.id !== null && form.id !== undefined && form.id !== ''"  style="width: 202px">
            <el-option
              v-for="item in parkList"
              :key="item.code"
              :label="item.name"
              :value="item.code">
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="业主" prop="buildOwner">
          <el-input v-model="form.buildOwner" placeholder="请输入业主"  maxlength="12" show-word-limit  />
        </el-form-item>
        <el-form-item label="监测状态" prop="state">
          <el-radio-group v-model="form.state" style="width: 200px">
            <el-radio v-for="(item,index) in stateList" :label="item.value">{{item.label}}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="建设年代" prop="buildYear">
          <!--<el-date-picker
            v-model="form.buildYear"
            type="year"
            value-format="yyyy"
            placeholder="请选择建设年代"
            style="width: 200px">
          </el-date-picker>-->
          <el-input v-model="form.buildYear" placeholder="请输入建设年代" type="year"  maxlength="12" show-word-limit  />
        </el-form-item>
        <el-form-item label="所属行政区划" prop="districtCode">
          <el-input v-model="form.districtCode" placeholder="请输入所属行政区划"  maxlength="12" show-word-limit />
        </el-form-item>
        <el-form-item label="建筑地址" prop="buildAddr">
          <el-input v-model="form.buildAddr" placeholder="请输入建筑地址"  maxlength="24" show-word-limit  />
        </el-form-item>
        <el-form-item label="建筑坐标-经度" prop="buildLong">
          <el-input v-model="form.buildLong" placeholder="请输入建筑坐标-经度"  maxlength="12" show-word-limit  />
        </el-form-item>
        <el-form-item label="建筑坐标-纬度" prop="buildLat">
          <el-input v-model="form.buildLat" placeholder="请输入建筑坐标-纬度"  maxlength="12" show-word-limit  />
        </el-form-item>
        <el-form-item label="地上建筑层数" prop="upFloor">
          <el-input v-model="form.upFloor" placeholder="请输入地上建筑层数"  maxlength="12" show-word-limit  />
        </el-form-item>
        <el-form-item label="地下建筑层数" prop="downFloor">
          <el-input v-model="form.downFloor" placeholder="请输入地下建筑层数"  maxlength="12" show-word-limit />
        </el-form-item>
        <el-form-item label="建筑功能" prop="buildFunc">
          <el-select v-model="form.buildFunc"   style="width: 202px">
            <el-option
              v-for="item in buildFuncList"
              :key="item.value"
              :label="item.label"
              :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="建筑总面积" prop="totalArea">
          <el-input v-model="form.totalArea" placeholder="请输入建筑总面积"  maxlength="24" show-word-limit  />
        </el-form-item>
        <el-form-item label="空调面积" prop="airArea">
          <el-input v-model="form.airArea" placeholder="请输入空调面积"  maxlength="24" show-word-limit  />
        </el-form-item>
        <el-form-item label="采暖面积" prop="heatArea">
          <el-input v-model="form.heatArea" placeholder="请输入采暖面积"  maxlength="24" show-word-limit  />
        </el-form-item>
        <el-form-item label="空调系统形式" prop="airType">
          <el-select v-model="form.airType"  style="width: 202px">
            <el-option
              v-for="item in airTypeList"
              :key="item.value"
              :label="item.label"
              :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="采暖系统形式" prop="heatType">
          <el-select v-model="form.heatType"   style="width: 202px">
            <el-option
              v-for="item in heatTypeList"
              :key="item.value"
              :label="item.label"
              :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="建筑结构形式" prop="struType">
          <el-select v-model="form.struType" style="width: 202px">
            <el-option
              v-for="item in struTypeList"
              :key="item.value"
              :label="item.label"
              :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="外墙材料形式" prop="wallMatType">
          <el-select v-model="form.wallMatType"  style="width: 202px">
            <el-option
              v-for="item in wallMatTypeList"
              :key="item.value"
              :label="item.label"
              :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="外墙保温形式" prop="wallWarmType">
          <el-select v-model="form.wallWarmType" style="width: 202px">
            <el-option
              v-for="item in wallWarmTypeList"
              :key="item.value"
              :label="item.label"
              :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="外窗类型" prop="wallWinType">
          <el-select v-model="form.wallWinType" style="width: 202px">
            <el-option
              v-for="item in wallWinTypeList"
              :key="item.value"
              :label="item.label"
              :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="玻璃类型" prop="glassType">
          <el-select v-model="form.glassType"  style="width: 202px">
            <el-option
              v-for="item in glassTypeList"
              :key="item.value"
              :label="item.label"
              :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="窗框材料类型" prop="winFrameType">
          <el-select v-model="form.winFrameType" style="width: 202px">
            <el-option
              v-for="item in winFrameTypeList"
              :key="item.value"
              :label="item.label"
              :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="建筑体型系数" prop="bodyCoef">
          <el-input v-model="form.bodyCoef" placeholder="请输入建筑体型系数"  maxlength="24" show-word-limit />
        </el-form-item>
        <el-form-item label="是否标杆建筑" prop="isStandard">
          <el-radio-group v-model="form.isStandard" style="width: 202px">
            <el-radio label="true">是</el-radio>
            <el-radio label="false">否</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="监测方案设计单位" prop="designDept">
          <el-input v-model="form.designDept" placeholder="请输入监测方案设计单位"  maxlength="24" show-word-limit />
        </el-form-item>
        <el-form-item label="监测工程实施单位" prop="workDept">
          <el-input v-model="form.workDept" placeholder="请输入监测工程实施单位"  maxlength="24" show-word-limit />
        </el-form-item>
        <el-form-item label="开始监测日期" prop="monitorDate">
          <el-date-picker clearable  style="width: 200px"
            v-model="form.monitorDate"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择开始监测日期">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="工程验收日期" prop="acceptDate">
          <el-date-picker clearable  style="width: 200px"
            v-model="form.acceptDate"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择工程验收日期">
          </el-date-picker>
        </el-form-item>

      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listBuildBaseInfo, getBuildBaseInfo, delBuildBaseInfo, addBuildBaseInfo, updateBuildBaseInfo,
  getAllDataCenterBaseInfo,getAllBuildGroup,getAllPark } from "@/api/basicData/energyDataReport/buildBaseInfo/buildBaseInfo";

export default {
  name: "BuildBaseInfo",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 建筑基本项数据表格数据
      buildBaseInfoList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        dataCenterId: null,
        buildGroupId: null,
        buildName: null,
        parkId: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        dataCenterId: [
          { required: true, message: "所属数据中心不能为空", trigger: "change" }
        ],
        buildGroupId: [
          { required: true, message: "所属建筑群不能为空", trigger: "change" }
        ],
        buildName: [
          { required: true, message: "建筑名称不能为空", trigger: "blur" },],
        aliasName: [
          { required: true, message: "建筑字母别名不能为空", trigger: "blur" }
        ],
        buildOwner: [
          { required: true, message: "业主不能为空", trigger: "blur" }
        ],
        state: [
          { required: true, message: "监测状态不能为空", trigger: "change" }
        ],
        districtCode: [
          { required: true, message: "所属行政区划不能为空", trigger: "blur" }
        ],
        buildAddr: [
          { required: true, message: "建筑地址不能为空", trigger: "blur" }
        ],
        buildLong: [
          { required: true, message: "建筑坐标-经度不能为空", trigger: "blur" },
          {
            pattern: /^(\-|\+)?(((\d|[1-9]\d|1[0-7]\d|0{1,3})\.\d{0,6})|(\d|[1-9]\d|1[0-7]\d|0{1,3})|180\.0{0,6}|180)$/,
            message: '经度整数为0-180,小数为0到6位!'
          }
        ],
        buildLat: [
          { required: true, message: "建筑坐标-纬度不能为空", trigger: "blur" },
          {
            pattern: /^(\-|\+)?([0-8]?\d{1}\.\d{0,6}|90\.0{0,6}|[0-8]?\d{1}|90)$/,
            message: '纬度整数为0-90,小数为0到6位!'
          }
        ],
        buildYear: [
          { required: true, message: "建设年代不能为空", trigger: "blur", },
          {
            pattern: /^\d{3}(\-|\/|.)$/,
            message: '请输入4位数字年份!'
          }
        ],
        upFloor: [
          { required: true, message: "地上建筑层数不能为空", trigger: "blur" }
        ],
        downFloor: [
          { required: true, message: "地下建筑层数不能为空", trigger: "blur" }
        ],
        buildFunc: [
          { required: true, message: "建筑功能不能为空", trigger: "blur" }
        ],
        totalArea: [
          { required: true, message: "建筑总面积不能为空", trigger: "blur" }
        ],
        airArea: [
          { required: true, message: "空调面积不能为空", trigger: "blur" }
        ],
        heatArea: [
          { required: true, message: "采暖面积不能为空", trigger: "blur" }
        ],
        airType: [
          { required: true, message: "建筑空调系统形式不能为空", trigger: "change" }
        ],
        heatType: [
          { required: true, message: "建筑采暖系统形式不能为空", trigger: "change" }
        ],
        struType: [
          { required: true, message: "建筑结构形式不能为空", trigger: "change" }
        ],
        wallMatType: [
          { required: true, message: "外墙材料形式不能为空", trigger: "change" }
        ],
        wallWarmType: [
          { required: true, message: "外墙保温形式不能为空", trigger: "change" }
        ],
        wallWinType: [
          { required: true, message: "外窗类型不能为空", trigger: "change" }
        ],
        glassType: [
          { required: true, message: "玻璃类型不能为空", trigger: "change" }
        ],
        winFrameType: [
          { required: true, message: "窗框材料类型不能为空", trigger: "change" }
        ],
        isStandard: [
          { required: true, message: "是否标杆建筑不能为空", trigger: "change" }
        ],
        designDept: [
          { required: true, message: "监测方案设计单位不能为空", trigger: "blur" }
        ],
        workDept: [
          { required: true, message: "监测工程实施单位不能为空", trigger: "blur" }
        ],
        createTime: [
          { required: true, message: "录入时间不能为空", trigger: "blur" }
        ],
        parkId: [
          { required: true, message: "所属园区不能为空", trigger: "change" }
        ]
      },
      //数据中心列表
      dataCenterBaseInfoList:[],
      //建筑群列表
      buildGroupList:[],
      //园区列表
      parkList:[],
      //监测状态列表
      stateList:[{value:1,label:'启用监测'},{value:0,label:'停用监测'}],
      //建筑功能列表
      buildFuncList:[{value:'A',label:'办公建筑'},{value:'B',label:'商场建筑'},{value:'C',label:'宾馆饭店建筑'},
        {value:'D',label:'文化教育建筑'},{value:'E',label:'医疗卫生建筑'},{value:'F',label:'体育建筑'},
        {value:'G',label:'综合建筑'},{value:'H',label:' 其它建筑'}],
      //空调系统形式列表
      airTypeList:[{value:'A',label:'集中式全空气系统'}, {value:'B',label:'风机盘管+风机系统'},
        {value:'C',label:'分体式空调或VRV的局部式机组系统'}, {value:'D',label:'其他'}
      ],
      //采暖系统形式列表
      heatTypeList:[{value:'A',label:'散热器采暖'}, {value:'B',label:'地板辐射采暖'}, {value:'C',label:'电辐射采暖'},
        {value:'D',label:'空调系统集中采暖'}, {value:'E',label:'其他'},
      ],
      //建筑结构形式列表
      struTypeList:[{value:'A',label:'框架结构'}, {value:'B',label:'框-剪结构'}, {value:'C',label:'剪力墙结构'},
        {value:'D',label:'砖-混结构'}, {value:'E',label:'钢结构'}, {value:'F',label:'简体结构'},
        {value:'G',label:'木结构'}, {value:'H',label:'其他'},
      ],
      //外墙材料列表
      wallMatTypeList:[{value:'A',label:'砖'}, {value:'B',label:'建筑砌块'}, {value:'C',label:'板材墙体'},
        {value:'D',label:'复合墙板和墙体'}, {value:'E',label:'玻璃幕墙'}, {value:'F',label:'其他'},
      ],
      //外墙保温形式列表
      wallWarmTypeList:[{value:'A',label:'内保温'}, {value:'B',label:'外保温'}, {value:'C',label:'夹心保温'}, {value:'D',label:'其他'},
      ],
      //外窗类型列表
      wallWinTypeList:[{value:'A',label:'单玻单层窗'}, {value:'B',label:'单玻双层窗'},
        {value:'C',label:'单玻单层窗+单玻双层窗'}, {value:'D',label:'中空双层玻璃窗'}, {value:'E',label:'中空三层玻璃窗'},
        {value:'F',label:'中空充惰性气体'}, {value:'G',label:'其他'},
      ],
      //玻璃类型列表
      glassTypeList:[{value:'A',label:'普通玻璃'}, {value:'B',label:'镀膜玻璃'}, {value:'C',label:'Low-e玻璃'}, {value:'D',label:'其他'},
      ],
      //窗框材料类型列表
      winFrameTypeList:[{value:'A',label:'钢窗'}, {value:'B',label:'铝合金'}, {value:'C',label:'木窗'},
        {value:'D',label:'断热窗框'}, {value:'E',label:'塑窗'}, {value:'F',label:'其他'},
      ],
    };
  },
  created() {
    this.getList();
    //获取所有数据中心
    this.getAllDataCenterBaseInfo();
    //获取所有建筑群
    this.getAllBuildGroup();
    //获取所有园区
    this.getAllPark();
  },
  methods: {
    //获取所有数据中心
    getAllDataCenterBaseInfo(){
      getAllDataCenterBaseInfo().then(response => {
        this.dataCenterBaseInfoList = response;
      })
    },
    //获取所有建筑群
    getAllBuildGroup(){
      getAllBuildGroup().then(response => {
        this.buildGroupList = response;
      })
    },
    //获取所有园区
    getAllPark(){
      getAllPark().then(response => {
        this.parkList = response;
      })
    },
    /** 查询建筑基本项数据列表 */
    getList() {
      this.loading = true;
      listBuildBaseInfo(this.queryParams).then(response => {
        this.buildBaseInfoList = response.rows;
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
        id: null,
        dataCenterId: null,
        buildGroupId: null,
        buildName: null,
        aliasName: null,
        buildOwner: null,
        state: null,
        districtCode: null,
        buildAddr: null,
        buildLong: null,
        buildLat: null,
        buildYear: null,
        upFloor: null,
        downFloor: null,
        buildFunc: null,
        totalArea: null,
        airArea: null,
        heatArea: null,
        airType: null,
        heatType: null,
        bodyCoef: null,
        struType: null,
        wallMatType: null,
        wallWarmType: null,
        wallWinType: null,
        glassType: null,
        winFrameType: null,
        isStandard: null,
        designDept: null,
        workDept: null,
        createTime: null,
        createUser: null,
        monitorDate: null,
        acceptDate: null,
        parkId: null
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
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
      this.title = "添加建筑基本项数据";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getBuildBaseInfo(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改建筑基本项数据";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateBuildBaseInfo(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addBuildBaseInfo(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除建筑基本项数据编号为"' + ids + '"的数据项？').then(function() {
        return delBuildBaseInfo(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('basicData/buildBaseInfo/export', {
        ...this.queryParams
      }, `建筑基本项信息_${new Date().getTime()}.xlsx`)
    },
    //根据数据中心id显示数据中心名称
    getDataCenterName(value,list){
      let label = '';
      if(value){
        if(list){
          list.forEach((item)=>{
            if(item.id == value){
              label = item.dataCenterName;
            }
          });
        }
      }
      return label;
    },

    //根据建筑群id显示建筑群名称
    getBuildGroupName(value,list){
      let label = '';
      if(value){
        if(list){
          list.forEach((item)=>{
            if(item.id == value){
              label = item.buildGroupName;
            }
          });
        }
      }
      return label;
    },
    //根据园区code显示园区名称
    getParkName(value,list){
      let label = '';
      if(value){
        if(list){
          list.forEach((item)=>{
            if(item.code == value){
              label = item.name;
            }
          });
        }
      }
      return label;
    },
  }
};
</script>
<style lang="scss" scoped>

  .el-table::before {
    height: 0px;
  }
</style>
