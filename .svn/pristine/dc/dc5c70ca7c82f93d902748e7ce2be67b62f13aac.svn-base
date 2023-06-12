<template>
  <div class="app-container">
    <el-row :gutter="20" class="mb20">
      <!-- 分组数据 -->
      <el-col :span="4" :xs="24">
       <div class="head-container">
          <el-input
            v-model="grounpName"
            placeholder="请输入分组名称"
            clearable
            size="small"
            prefix-icon="el-icon-search"
            style="margin-bottom: 20px"
          />
        </div>
        <div class="head-container">
          <el-tree
            class="filter-tree"
            :data="grounpOptions"
            :props="defaultProps"
            :expand-on-click-node="false"
            :filter-node-method="filterNode"
            ref="tree"
            default-expand-all
            @node-click="handleNodeClick"
          />
        </div>
      </el-col>
      <!-- 设备数据 -->
      <el-col :span="20" :xs="24">
        <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="68px">
          <el-form-item label="所属产品" prop="productKey">
            <el-input ref="productName" size="small" clearable v-model="queryParams.productName" @click.native="handleCategoryFocus()" placeholder="请选择所属产品" />
          </el-form-item>
          <el-form-item label="设备名称" prop="deviceName">
            <el-input
              v-model="queryParams.deviceName"
              placeholder="请输入设备名称"
              clearable
              size="small"
              @keyup.enter.native="handleQuery"
            />
          </el-form-item>
          <el-form-item label="设备编号" prop="deviceCode">
            <el-input
              v-model="queryParams.deviceCode"
              placeholder="请输入设备编号"
              clearable
              size="small"
              @keyup.enter.native="handleQuery"
            />
          </el-form-item>
          <el-form-item label="在线状态" prop="onlineState">
            <el-select v-model="queryParams.onlineState" placeholder="请选择在线状态" clearable size="small">
              <el-option
                v-for="dict in dict.type.iot_device_online_state"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="启用状态" prop="enabled">
            <el-select v-model="queryParams.enabled" placeholder="请选择启用状态" clearable size="small">
              <el-option
                v-for="dict in dict.type.iot_device_enabled"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
                size="small"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="IP地址" prop="ipAddr">
            <el-input
              v-model="queryParams.ipAddr"
              placeholder="请输入IP地址"
              clearable
              size="small"
              @keyup.enter.native="handleQuery"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
            <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>
        <el-row type="flex"  :gutter="10" class="mb8" style="justify-content: space-between;align-items: center;">
          <div>
            <el-col :span="1.5">
              <el-button
                type="primary"
                plain
                icon="el-icon-plus"
                size="mini"
                @click="handleAdd"
                v-hasPermi="['iot:device:add']"
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
                v-hasPermi="['iot:device:edit']"
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
                v-hasPermi="['iot:device:remove']"
              >删除</el-button>
            </el-col>
            <el-col :span="1.5">
              <el-button
                type="warning"
                plain
                icon="el-icon-download"
                size="mini"
                @click="handleExport"
                v-hasPermi="['iot:device:export']"
              >导出</el-button>
            </el-col>
          </div>
          <div class="addDom" :span="15">
            <el-col class="text_white" :span="5">
              <p>
                设备总数
                <el-tooltip class="item noborder" effect="dark" content="当前设备总数" placement="top">
                  <el-button type="text"><i class="el-icon-question"></i></el-button>
                </el-tooltip>
                <span class="cusNumber">{{equipmentState.deviceCount}}</span>
              </p>

            </el-col>
            <el-col class="text_white" :span="5">
              <p>
                在线数
                <el-tooltip class="item noborder" effect="dark" content="当前在线设备" placement="top">
                  <el-button type="text"><i class="el-icon-question"></i></el-button>
                </el-tooltip>
                <span class="cusNumber">{{equipmentState.onlineCount}}</span>
              </p>

            </el-col>
            <el-col class="text_white" :span="5">
              <p>
                设备启用数
                <el-tooltip class="item noborder" effect="dark" content="当前激活设备" placement="top">
                  <el-button type="text"><i class="el-icon-question"></i></el-button>
                </el-tooltip>
                <span class="cusNumber">{{equipmentState.enabledCount}}</span>
              </p>

            </el-col>
          </div>

          <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>

        <el-table v-loading="loading" :data="deviceList" @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="55" align="center" />
          <el-table-column label="序号" width="55" type="index" align="center" />
          <el-table-column label="所属产品" align="center" prop="productKey">
            <template slot-scope="scope">
              {{ scope.row.productName}}
            </template>
          </el-table-column>
          <el-table-column label="所属分组" align="center" prop="groupName" />
          <el-table-column label="设备名称" align="center" prop="deviceName" />
          <el-table-column label="设备编号" align="center" prop="deviceCode" />
          <el-table-column label="启用状态" align="center" prop="enabled" width="80">
            <template slot-scope="scope">
              <el-switch
                v-model="scope.row.enabled"
                active-color="#2176eb"
                inactive-color="#ff4949"
                :active-value="1"
                :inactive-value="0"
                @change="swichChange($event,scope.row)">
              </el-switch>
            </template>
          </el-table-column>
          <el-table-column label="在线状态" align="center" prop="onlineState">
            <template slot-scope="scope">
              <div class="dot">
                <i :class="scope.row.onlineState == 1?'greenDot point':'redDot point'"></i>
                {{ scope.row.onlineState == 1?'在线':'离线'}}
              </div>
            </template>
          </el-table-column>
          <el-table-column label="IP地址" align="center" prop="ipAddr" />
          <el-table-column label="位置" align="center" prop="location" />
          <el-table-column label="操作" width="240" align="center" class-name="small-padding fixed-width">
            <template slot-scope="scope">
              <el-button
                size="mini"
                type="text"
                icon="el-icon-search"
                @click="handleLook(scope.row)"
                v-hasPermi="['iot:device:query']"
              >详细</el-button>
              <el-button
                size="mini"
                type="text"
                icon="el-icon-search"
                @click="handleDebug(scope.row)"
                v-hasPermi="['iot:device:edit']"
              >调试</el-button>
              <el-button
                size="mini"
                type="text"
                icon="el-icon-edit"
                @click="handleUpdate(scope.row)"
                v-hasPermi="['iot:device:edit']"
              >修改</el-button>
              <el-button
                size="mini"
                type="text"
                icon="el-icon-delete"
                @click="handleDelete(scope.row)"
                v-hasPermi="['iot:device:remove']"
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

        <!-- 添加或修改设备对话框 -->
        <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
          <el-form ref="form" :model="form" :rules="rules" label-width="80px">
            <el-form-item label="所属产品" prop="productKey">
              <el-input ref="productName" v-model="form.productName" @click.native="handleCategoryFocus()" placeholder="请选择所属产品" />
            </el-form-item>
            <!-- {{form.gatewayDevice}}  -->
             <!-- gatewayIdgatewayDevice -->
            <el-form-item label="网关设备" prop="gatewayId" v-if="gateway.nodeType == 2">
              <el-input ref="gatewayName" v-model="form.gatewayDeviceName"  @focus="handleGatewayFocus()" placeholder="请选择所属网关设备" />
            </el-form-item>
            <el-form-item label="所属分组" prop="groupKey">
              <el-select v-model="form.groupKey" placeholder="请选择所属分组" clearable size="medium" style="width: 100%;">
                <el-option
                  v-for="item in grounpList"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="设备名称" prop="deviceName">
              <el-input v-model="form.deviceName" placeholder="请输入设备名称" />
            </el-form-item>
            <el-form-item label="设备编号" prop="deviceCode">
              <el-input v-model="form.deviceCode" placeholder="请输入设备编号" />
            </el-form-item>
            <el-form-item label="启用状态" prop="enabled">
              <el-select v-model="form.enabled" placeholder="请选择启用状态" class="select-width">
                <el-option
                  v-for="dict in dict.type.iot_device_enabled"
                  :key="dict.value"
                  :label="dict.label"
                  :value="parseInt(dict.value)"
                ></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="IP地址" prop="ipAddr">
              <el-input v-model="form.ipAddr" placeholder="请输入IP地址" />
            </el-form-item>
            <el-form-item label="位置" prop="location">
              <el-input v-model="form.location" placeholder="请输入位置" />
            </el-form-item>
          </el-form>
          <div slot="footer" class="dialog-footer">
            <el-button type="primary" @click="submitForm">确 定</el-button>
            <el-button @click="cancel">取 消</el-button>
          </div>
        </el-dialog>
        <!-- 查看产品抽屉 -->
        <el-drawer
          title="选择产品"
          :visible.sync="drawerProduct.visible"
          direction="rtl"
          :modal="drawerProduct.modal"
          :before-close="closeProduct"
          @open="handleDrawerCategoryOpen">
          <div style="margin-left: 15px; margin-right: 15px">
            <div style="margin-bottom: 15px;">
              <!-- 选择下拉框和搜索 -->
              <el-row>
                <el-col>
                  <el-input placeholder="请输入产品名称" v-model="product.queryParams.productName">
                    <el-button slot="append" icon="el-icon-search" @click="handleProductNameClick"></el-button>
                  </el-input>
                </el-col>
              </el-row>
            </div>
            <el-table
              :data="product.productList"
              style="width: 100%"
              v-loading="loading">
              <el-table-column label="序号" width="55" align="center" type="index" />
              <el-table-column label="所属产品" align="center" prop="id">
                <template slot-scope="scope">
                  {{scope.row.productName}}
                  <el-button style="color: #CCCCCCFF" type="text"></el-button>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="200" align="center" class-name="small-padding fixed-width">
                <template slot-scope="scope">
                  <el-button :disabled="form.productKey === scope.row.id || queryParams.productKey === scope.row.id"
                  @click="categoryHandleClick(scope.row)"
                  type="text" size="small">选择</el-button>
                </template>
              </el-table-column>
            </el-table>
              <!-- 产品列表分页 -->
            <pagination
              v-show="product.total>0"
              :total="product.total"
              :page.sync="product.queryParams.pageNum"
              :limit.sync="product.queryParams.pageSize"
              @pagination="getProductList"
              layout="prev, pager, next"
            />
          </div>
        </el-drawer>
        <!-- 调试抽屉 -->
        <el-drawer
          title="在线调试"
          :visible.sync="debug_drawer"
          direction="rtl"
          :before-close="handleClose">
          <el-tabs v-model="activeName" @tab-click="debugTabs" v-loading="fun_loading" type="border-card" element-loading-text="拼命加载中" style="box-shadow:none;padding-left:0px;">
            <el-tab-pane label="属性调试" name="first">
              <div v-for="(item,index) in functionArray" :key="index">
                <el-col :span="24">
                  <p class="input-with-select_title">{{item.name}}</p>
                </el-col>
                <div class="attribute">
                  <!-- 如果是枚举类型就遍历选择，如果不是就按类型输入 -->
                  <template v-if="item.children && item.children.length >= 1">
                    <el-select  v-model="item.v_id"
                    :placeholder="item.v_placeholder?item.v_placeholder:'请选择参数(enum)'"
                    :disabled="item.v_disabled" style="width:100%;">
                        <el-option
                          v-for="itm in item.children"
                          :key="itm.idex"
                          :label="itm.name"
                          :value="itm.value">
                        </el-option>
                    </el-select>
                  </template>
                  <template v-else>
                    <el-input v-model="item.v_input" :disabled="item.v_disabled"
                    :placeholder="item.v_placeholder?item.v_placeholder:'请输入'" style="width:100%;" />
                  </template>
                </div>
                <div class="attributeDropdown">
                  <el-dropdown trigger="click" @command="handleCommand">
                    <span class="el-dropdown-link text_white">
                      调试<i class="el-icon-arrow-down"></i>
                    </span>
                    <el-dropdown-menu slot="dropdown">
                      <template v-if="item.readWriteType == 1">
                        <el-dropdown-item :command="setAttribute(item,'1-1')">读取</el-dropdown-item>
                        <el-dropdown-item :command="setAttribute(item,'1-2')">写入</el-dropdown-item>
                      </template>
                      <template v-if="item.readWriteType == 2">
                        <el-dropdown-item :command="setAttribute(item,'2')">读取</el-dropdown-item>
                      </template>
                      <template v-if="item.readWriteType == 3">
                        <el-dropdown-item :command="setAttribute(item,'3')">写入</el-dropdown-item>
                      </template>
                    </el-dropdown-menu>
                  </el-dropdown>
                </div>
              </div>
              <el-col style="margin-top:15px;padding-left:0px;">
                <el-button type="primary" size="mini" @click="submitFunctionData" v-hasPermi="['iot:device:debug']">设置</el-button>
                <el-button type="primary" size="mini" @click="getFunctionData" v-hasPermi="['iot:device:debug']">获取</el-button>
              </el-col>
            </el-tab-pane>
            <el-tab-pane label="服务调用" name="second">
              <el-select v-model="serviceValue" placeholder="请选择服务" style="margin-bottom:15px;">
                <el-option
                  v-for="item in functionList"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id">
                </el-option>
              </el-select>
              <!-- showBtns是否显示保存按钮 -->
              <vue-json-editor
                v-model="resultInfo"
                :showBtns="false"
                :mode="'code'"
                lang="zh"
                @json-change="onJsonChange"
                @json-save="onJsonSave"
                @has-error="onError"
              />
              <el-button type="primary" style="margin-top:15px;" @click="onJsonSave">发送指令</el-button>
            </el-tab-pane>
          </el-tabs>
        </el-drawer>
        <!-- 设备抽屉 -->
        <el-drawer
          :title="drawer_title"
          :visible.sync="drawer"
          direction="rtl"
          :before-close="handleClose">
          <el-tabs type="border-card">
            <el-tab-pane>
              <span slot="label">设备信息</span>
              <el-descriptions class="margin-top" :column="1" border>
                <el-descriptions-item>
                  <template slot="label">
                    设备名称
                  </template>
                  {{formLook.deviceName}}
                </el-descriptions-item>
                <el-descriptions-item>
                  <template slot="label">
                    所属分组
                  </template>
                  {{formLook.grounpName}}
                </el-descriptions-item>
                <el-descriptions-item>
                  <template slot="label">
                    所属产品
                  </template>
                  {{ productMap.get(formLook.productKey) }}
                </el-descriptions-item>
                <el-descriptions-item>
                  <template slot="label">
                    设备编号
                  </template>
                  {{formLook.deviceCode}}
                </el-descriptions-item>
                <el-descriptions-item>
                  <template slot="label">
                    启用状态
                  </template>
                  <span :style="formLook.enabled == '1'?'color:#09dd09;':'color:red;'">
                    {{formLook.enabled == 1?'已启用':'已禁用'}}
                  </span>
                </el-descriptions-item>
                <el-descriptions-item>
                  <template slot="label">
                    创建时间
                  </template>
                  {{formLook.createTime}}
                </el-descriptions-item>
                <el-descriptions-item>
                  <template slot="label">
                    位置
                  </template>
                  {{formLook.location}}
                </el-descriptions-item>
                <el-descriptions-item>
                  <template slot="label">
                    IP地址
                  </template>
                  {{formLook.ipAddr}}
                </el-descriptions-item>
                <el-descriptions-item>
                  <template slot="label">
                    更新时间
                  </template>
                  {{formLook.updateTime}}
                </el-descriptions-item>
              </el-descriptions>
            </el-tab-pane>
            <el-tab-pane label="物模型数据">
              <template>
                <el-descriptions class="margin-top" :column="1" border v-for="(item,index) in functionData" :key="index">
                  <el-descriptions-item label-class-name="descriptions-item">
                    <template slot="label">
                      功能名称
                    </template>
                    {{item.name}}
                  </el-descriptions-item>
                  <el-descriptions-item label-class-name="descriptions-item">
                    <template slot="label">
                      标识符
                    </template>
                    {{item.identifier}}
                  </el-descriptions-item>
                  <el-descriptions-item label-class-name="descriptions-item">
                    <template slot="label">
                      实际值
                    </template>
                    <div v-for="(items,idx) in item.children" :key="idx">
                      <div v-show="item.dataType == items.value">
                        {{items.name}}
                      </div>
                    </div>
                  </el-descriptions-item>
                  <el-descriptions-item label-class-name="descriptions-item">
                    <template slot="label">
                      单位
                    </template>
                    {{item.unit}}
                  </el-descriptions-item>
                </el-descriptions>
              </template>
            </el-tab-pane>
          </el-tabs>
        </el-drawer>
        <!-- 网关设备抽屉 -->
        <el-drawer
          title="选择网关设备"
          :visible.sync="drawerDevice.visible"
          direction="rtl"
          :before-close="handleDeviceClose">
            <div style="margin-left: 15px; margin-right: 15px">
              <div style="margin-bottom: 15px;">
                <!-- 选择下拉框和搜索 -->
                <el-row>
                  <el-col>
                    <el-input placeholder="请输入设备名称" v-model="gateway.queryParams.deviceName">
                      <el-button slot="append" icon="el-icon-search" @click="handleDeviceNameClick"></el-button>
                    </el-input>
                  </el-col>
                </el-row>
              </div>
              <el-table
                :data="gateway.deviceList"
                style="width: 100%"
                v-loading="loading">
                <el-table-column label="序号" width="55" align="center" type="index" />
                <el-table-column label="所属设备" align="center" prop="id">
                  <template slot-scope="scope">
                    {{scope.row.deviceName}}
                    <el-button style="color: #CCCCCCFF" type="text"></el-button>
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="200" align="center" class-name="small-padding fixed-width">
                  <template slot-scope="scope">
                    <el-button :disabled="form.gatewayId === scope.row.id"
                    @click="handleDeviceFocus(scope.row)"
                    type="text" size="small">选择</el-button>
                  </template>
                </el-table-column>
              </el-table>
                <!-- 产品列表分页 -->
              <pagination
                v-show="gateway.total>0"
                :total="gateway.total"
                :page.sync="gateway.queryParams.pageNum"
                :limit.sync="gateway.queryParams.pageSize"
                @pagination="gatewayList"
                layout="prev, pager, next"
              />
            </div>
        </el-drawer>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import {listDevice, getDevice, delDevice, addDevice, updateDevice,getDeviceFunction,
getAttribute,listDeviceGateway,statistics,invokeService,getAttributeBatch,setAttributeBatch} from "@/api/iot/device";
import {getProduct} from "@/api/iot/product";
import { listProduct} from "@/api/iot/product";
import {listGroup} from "@/api/iot/group";
import {listValue} from "@/api/iot/value";
import {listFunction} from "@/api/iot/function";
import Treeselect from "@riophae/vue-treeselect";
import "@riophae/vue-treeselect/dist/vue-treeselect.css";
import vueJsonEditor from 'vue-json-editor';

export default {
  name: "Device",
  dicts: [
    'iot_product_node_type', //节点状态
    'iot_device_online_state', // 在线状态字典
    'iot_device_enabled' // 启用状态字典
  ],
  components: { Treeselect,vueJsonEditor},
  data() {
    return {
      resultInfo:{
        "firstName": "Brett", "lastName":"McLaughlin", "email": "brett@newInstance.com",
      },
      // 遮罩层
      loading: true,
      fun_loading:true,
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
      // 设备表格数据
      deviceList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 分组参数
      groundQuery:{
        name: null,
        description: null,
      },
      // 分组树选项
      grounpOptions: undefined,
      // 分组名称
      grounpName: undefined,
      defaultProps: {
        children: "children",
        label: "name"
      },
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        productKey: null,
        groupKey: null,
        deviceName: null,
        deviceCode: null,
        onlineState: null,
        enabled: null,
        version: null,
        ipAddr: null,
        productName:null,
      },
      drawerQueryParams: {
        pageNum: 1,
        pageSize: 10,
        productKey: null,
        groupKey: null,
        deviceName: null,
        deviceCode: null,
        onlineState: null,
        enabled: null,
        version: null,
        ipAddr: null,
        productName:null,
      },
      // 表单参数
      form: {
        // productKey:null, // 所属产品
        // gatewayDevice:null, // 网关设备
        // groupKey:null, // 所属分组
        // deviceName:null, //
        // deviceCode:null, //
        // enabled:null, // qiy启用状态
        // ipAddr:null, // ip地址
        // location:null,// 位置
        // gatewayDeviceName:null,
      },
      // 表单校验
      rules: {
        productKey:[
          { required: true, message: "所属产品不能为空", trigger: "change" }
        ],
        gatewayId:[
          { required: true, message: "所属网关设备不能为空", trigger: "change" }
        ],
        categoryName:[
          { required: true, message: "所属产品不能为空", trigger: "blur" }
        ],
        groupKey: [
          { required: true, message: "所属分组不能为空", trigger: "blur" }
        ],
        deviceName: [
          { required: true, message: "设备名称不能为空", trigger: "blur" }
        ],
        deviceCode: [
          { required: true, message: "设备编号不能为空", trigger: "blur" }
        ],
        enabled: [
          { required: true, message: "启用状态不能为空", trigger: "change" }
        ],
      },
      // 抽屉产品列表
      drawerProduct: {
        visible: false,
        direction: 'rtl',
        modal: false
      },
      product: {
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          field: undefined,
          productName: undefined
        },
        total: 0,
        // 品类表格数据
        productList: []
      },
      // 网关设备抽屉
      drawerDevice: {
        visible: false,
        direction: 'rtl',
        modal: false
      },
      // 网关设备参数
      gateway:{
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          field: undefined,
          deviceName: undefined
        },
        total: 0,
        nodeType:'',
        // 品类表格数据
        deviceList: []
      },
      // 分组列表
      grounpList:[],
      // 设备抽屉开关
      drawer:false,
      // 所属产品抽屉开关
      product_drawer:false,
      // 调试抽屉开关
      debug_drawer:false,
      // 查看设备信息
      formLook:[],
      //设备名称
      drawer_title:'',
      // 物模型数据
      functionData:[],
      // 物模式属性
      functionArray:null,
      activeName:'first',
      // 选择产品搜索框关键词
      productName:null,
      addProductName:'',
      // 产品数据
      productMap: new Map(),
      functionDebugData:[],
      hasJsonFlag:true, //Json是否验证通过
      functionList:[],// 功能定义列表
      serviceValue:null,
      equipment:[],//当前调试设备数据
      equipmentState:{},
    };
  },
  watch: {
    grounpName(val) {
      this.$refs.tree.filter(val);
    }
  },
  async created() {
    await this.getProductList();
    await this.getTreeSelect();
    await this.getStatistics();
    this.getList();
  },
  methods: {
    // 获取设备统计数
    getStatistics(){
      statistics().then(response =>{
        this.equipmentState = response.data;
      })
    },
    /** 获取产品列表 */
    getProductList() {
      listProduct(this.product.queryParams).then(response => {
        this.product.productList = response.rows;
        this.product.total = response.total;
      });
    },
    /** 查询分组 */
    async getTreeSelect() {
      this.loading = true;
      await listGroup(this.groundQuery).then(response => {
        this.grounpList = response.data;
        this.grounpOptions = [];
        const data = { id: 0, name: '分组名称', children: [] };
        data.children = this.handleTree(response.data, "id", "parentId");
        this.grounpOptions.push(data);
        this.loading = false;
      });
    },
    /** 查询设备列表 */
    getList() {
      this.loading = true;
      listDevice(this.queryParams).then(response => {
        this.deviceList = response.rows;
        console.log(this.deviceList);
        for(let i = 0; i < this.deviceList.length; i++) {
          var groupName = this.grounpList.filter((item) => {
            return item.id == this.deviceList[i].groupKey;
          });
          this.deviceList[i].groupName = groupName[0].name;
          var arr = this.product.productList.filter((item) => {
            return item.id == this.deviceList[i].productKey;
          });
          if(arr[0] != undefined){
            this.deviceList[i].productName = arr[0].productName;
          }
        }
        this.total = response.total;
        this.loading = false;
      });
    },
    /** 选择产品点击事件 **/
    categoryHandleClick(item){
      if(this.open){
        this.form.productKey = item.id;
        this.form.productName = item.productName;
        // 获取对应节点类型
        this.getNodetype();
      }else{
        this.queryParams.productKey = item.id;
        this.queryParams.productName = item.productName;
      }
      this.$forceUpdate();
    },
    // 添加设备时获取节点信息
    getNodetype(){
      getProduct(this.form.productKey).then(response=>{
        this.gateway.nodeType = response.data['nodeType'];
      });
    },
    // 新增时选择所属产品
    handleCategoryFocus(){
      if (!this.drawerProduct.visible) {
        // 去除所属品类焦点
        this.$refs.productName.blur();
        // 显示“选择品类”抽屉
        this.drawerProduct.visible = true;
      }
    },
    handleGatewayFocus(){
      if (!this.drawerDevice.visible) {
        // 去除所属网关设备焦点
        this.$refs.gatewayName.blur();
        // 显示抽屉
        this.drawerDevice.visible = true;
      }
    },
    /*品类选择抽屉关闭方法*/
    category_handleClose(done){
      done();
      this.product.queryParams['productName'] = '';
      this.getProductList();
    },
    // 节点单击事件
    handleNodeClick(data) {
      this.queryParams.groupKey = data.id;
      this.getList();
    },
    // 筛选节点
    filterNode(value, data) {
      if (!value) return true;
      return data.label.indexOf(value) !== -1;
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        productKey: null,
        productName: null,
        groupKey: null,
        deviceName: null,
        deviceCode: null,
        ipAddr: null,
        location: null,
        deviceKey:null,
        gatewayId:null,
        gatewayName: null,
      };
      this.queryParams = {
        productKey: null,
        productName: null,
        groupKey: null,
        deviceName: null,
        deviceCode: null,
        ipAddr: null,
        location: null,
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
      this.reset();
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
      this.gatewayList();
      this.open = true;
      this.title = "添加设备";
      this.reset();
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      const id = row.id || this.ids
      //修改的时候先获取产品，判断是否网关设备，决定网关设备是否显示并赋值
      // 获取到修改设备的id,然后查询
      getDevice(id).then(response => {
        this.open = true;
        this.title = "修改设备";
        this.form = response.data;
        var arr = this.product.productList.filter((item) => {
          return item.id == this.form.productKey;
        });
        if(arr[0] != undefined){
          this.form.productName = arr[0].productName;
        }
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateDevice(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.reset();
              this.getList();
            });
          } else {
            addDevice(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.reset();
              this.getList();
            });
          }
        }
      });
    },
    // 调试按钮
    handleDebug(row){
      this.fun_loading = true;
      this.debugging(row);
      // 获取属于服务的功能类型
      this.getServiceData();
      this.debug_drawer = true;
    },
    // 获取属性调试数据
    debugging(row){
      this.equipment = row;
      getDeviceFunction(row.id).then(async (response) => {
        let functionArray = response.data;
        for(let i = 0;i < functionArray.length;i++){
          let arr = functionArray[i];
          let query = {
            pageNum: null,
            functionKey: arr.id,
          };
          let childrenData = await this.getListValue(query);
          arr.children = null;
          arr.v_value = null;
          arr.v_name = null;
          arr.v_id = null;
          arr.v_input = null;
          this.$set(arr,'children',childrenData)
          // 读写属性为只读，则禁用input
          if(arr.readWriteType == 2){
            arr.v_disabled = true
            arr.v_placeholder = '点击获取参数'
          }else{
            arr.v_disabled = false
          }
        }
        this.functionArray = functionArray;
        this.fun_loading = false;
      })
    },
    getServiceData(){
      listFunction().then((response)=>{
        this.functionList = response.rows;
        // functionType == 2  服务类型
        this.functionList = this.functionList.filter((item)=>{
          return item.functionType == 2;
        });
        this.functionList['functionId'] = null;
      })
    },
    getListValue(query){
      return new Promise((resolve,reject)=>{
        listValue(query).then((result) => {
          resolve(result.rows);
        })
      })
    },
    // 详细按钮
    async handleLook(row){
      this.loading = true;
      const id = row.id || this.ids
      // 获取到修改设备的id,然后查询
      getDevice(id).then(response => {
        this.formLook = response.data;
        // 取所属分组数据
        let grounpData = this.grounpList.filter((item) => {
          return item.id == row.groupKey;
        });
        // this.formLook.productName = proData[0].productName;
        this.formLook.grounpName = grounpData[0].name;
        this.drawer = true;
        this.drawer_title = response.data.deviceName;
        this.loading = false;
      });
      // 获取物模型数据
      await getDeviceFunction(row.id).then((response) => {
        this.functionArray = response.data;
      })
      for(let i = 0;i < this.functionArray.length;i++){
          await this.getFvList(this.functionArray[i].id);
      }
      this.functionData = this.functionArray;
    },
    // 获取详细功能定义
    async getFvList(id){
      let query = {
        pageNum: null,
        functionKey: id,
      };
      await listValue(query).then(response => {
        let list = response.rows;
        this.functionArray.forEach(function(item){
          if(item.id == id){
            item.children = list;
          }
        });
      });
    },
    // 关闭抽屉
    handleClose(done) {
      done();
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$confirm('是否确认删除？', "警告", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }).then(function() {
          return delDevice(ids);
        }).then(() => {
          this.getList();
          this.$modal.msgSuccess("删除成功");
        }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('/iot/device/export', {
        ...this.queryParams
      }, `log_${new Date().getTime()}.xlsx`)
    },
    swichChange(status,item){
       updateDevice(item).then(response => {
        this.$modal.msgSuccess("状态修改成功");
        this.getList();
       })
    },
    debugTabs(tab, event) {
    },
    /** 领域选中值发生变化时触发 **/
    handleCategoryFieldChange() {
      this.ProductList.pageNum = 1;
      this.getProductList();
    },
    // 点击抽屉中产品
    handleCheckFunctionClick(){

    },
        /** Drawer 打开的回调 **/
    handleDrawerCategoryOpen() {
      this.product.queryParams['productName'] = '';
      this.getProductList();
    },
    // 产品选择搜索框
    handleProductNameClick(item){
      this.getProductList();
    },
    closeProduct(done){
      console.log('关闭了');
      this.form.productKey = null;
      this.queryParams.productKey = null;
      this.getProductList();
      done();
    },
    // 属性调试批量设置按钮
    submitFunctionData(){
      // 设备ID
      console.log(this.equipment['id']);
      console.log('要提交数据了');
      let data = [];
      const equ = true;
      this.functionArray.forEach(item=>{
        // 读写属性不是只读并且v_input有值
        if(item.readWriteType != 2){
          if(item.v_input != '' && item.v_input != null){
            if(parseFloat(item.v_input).toString() == "NaN"){
              console.log('请填写数字类型')
              this.$modal.msgError('请填写数字类型');
              equ = false;
            }else{
              data.push({'deviceId':this.equipment['id'],'functionId':item.id,'value':item.v_input});
            }
          }
        }
      })
      if(equ == false){
        return false;
      }
      setAttributeBatch(data).then(response=>{
        console.log(response);
        this.$modal.msgSuccess(response.msg);
      })
    },
    // 属性调试批量获取按钮
    getFunctionData(){
      // 设备的id
      let data = [];
      this.functionArray.forEach(item=>{
        if(item.readWriteType == 2 || item.readWriteType == 1){
          data.push({'deviceId':this.equipment['id'],'functionId':item.id});
        }
      })
      console.log(this.functionArray);
      getAttributeBatch(data).then(response=>{
        console.log(response,'后端返回的结果');
        this.$modal.msgSuccess(response.msg);
        let result = response.data;
        // 数组长度不一样，判断依据为functionArray的id == 后端返回数据的functionId
        for(let x = 0;x< this.functionArray.length;x++){
          for(let i=0;i<result.length;i++){
            // 只比较读写属性为可读或只读的
            if(this.functionArray[x].readWriteType == 2 || this.functionArray[x].readWriteType == 1){
              if(this.functionArray[x].id == result[i].functionId){
                if(result[i].value == null){
                  this.functionArray[x].v_input = '暂无数据';
                  this.functionArray[x].v_id = null;
                  this.functionArray[x].v_placeholder = "暂无数据";

                  this.functionArray[x].children.forEach(item=>{
                    if(item.id == this.functionArray[x].v_id){
                      this.functionArray[x].v_placeholder = item.name
                    }
                  })
                }else{
                  // 判断是否为枚举属性，枚举属性的key键和input的key键不一样
                  if(this.functionArray[x].children.length > 0){
                    console.log(this.functionArray[x].children);
                    console.log(this.functionArray[x].name);
                    this.functionArray[x].v_id = result[i].value;
                  }else{
                    this.functionArray[x].v_input == result[i].value;
                  }
                }
              }
            }
          }
        }
        console.log(this.functionArray)
      })
    },
    // json改变时执行的方法
    onJsonChange(value){
      // 实时保存
      this.onJsonSave(value)
    },
    // json保存时的方法
    onJsonSave(value){
      console.log(value);
      // this.resultInfo = value
      if(this.serviceValue == null){
        this.$modal.msgError('请选择调用服务');
        return false
      }
      this.hasJsonFlag = true
      let data = {'deviceId':this.equipment['id'],'functionId':this.serviceValue,'value':this.resultInfo}
      invokeService(data).then(response=>{
        if(response.code == 200){
          this.$modal.msgSuccess(response.msg);
        }
      })
    },
    // json错误
    onError(){
      this.hasJsonFlag = false
    },
    // 检查json
    checkJson(){
      if (this.hasJsonFlag == false){
        this.$modal.msgError("格式有错,请修改后重新提交");
        return false
      } else {
        console.log("json验证成功")
        return true
      }
    },

    // 获取网关相关设备
    gatewayList(){
      listDeviceGateway(this.gateway.queryParams).then(response=>{
        this.gateway.deviceList = response.rows;
        this.gateway.total = response.total;
      })
    },
    // 搜索按钮
    handleDeviceNameClick(){
      this.gatewayList();
    },
    // 点击选择网关设备
    handleDeviceFocus(item){
      this.form.gatewayDeviceName = item.deviceName;
      this.form.gatewayId = item.id;
      this.$forceUpdate();
    },
    // 网关设备关闭
    handleDeviceClose(done){
      done();
      this.gateway.queryParams.deviceName = '';
      this.gatewayList();
    },
    // 调试下拉
    handleClickDropdown(item,index){
      console.log(item,index);
    },
    // 把用到的参当做对象return回去供command调用
    setAttribute(item,index){
      return {'deviceId':this.equipment['id'],'functionId':item.id,'command':item,'index':index}
    },
    // 属性调试，单次操作
    handleCommand(command){
      switch(command.index){
        // 读取
        case '2':
          let data = {'deviceId':command.deviceId,'functionId':command.functionId}
          getAttribute(data).then(response=>{
            // response.data = {value:'2'};
            console.log(command)
            command.command['v_id'] = 2;
            command.command['children'].forEach(item=>{
              if(item.id == 2){
                command.command['v_name'] = item.name;
              }
            })
          })
        break;
      }
    }
  }
};
</script>
<style scoped>
  #pane-1 .el-descriptions{margin-bottom: 25px;}
  .el-scrollbar__wrap{overflow-x: hidden;}
  .el-drawer__wrapper >>> .el-descriptions-item__cell{width: 50%;}
  .el-drawer__wrapper >>> .input-with-select .el-input__inner {
    width: 100%;
  }
  .el-drawer__wrapper >>> .input-with-select .el-input-group__append{
    width: 25%;
  }
  .theme-blue .text_white{color:white;}
  .theme-white .text_white{color:#999;}
  .input-with-select_title{margin:15px 0;color:#000;}
  .addDom{width:61%;}
  .attributeDropdown{display: flex;justify-content: center;align-items: center;height: 3.35vh;}
  .theme-blue .addDom .el-icon-question{color:white;}
  .theme-light,.theme-dark .addDom .el-icon-question{color:rgb(204, 204, 204);}
  .attributeDropdown{width:20%;float:left;}
  .attribute{width:80%;float:left;}
  .cusNumber{padding:0 15px;color:#4cbdff;font-size: 17px;font-weight:bold;}
</style>

