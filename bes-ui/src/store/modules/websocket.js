const websocket = {
  state: {
    // 支付数据
    payment: undefined,
    // 设备实时数据 例：{"deviceId":1,"functionValue":{"1":2.3,"2":5}}
    iotDeviceRealtimeData: undefined,
    deviceTreeControllerStstus: undefined,
    deviceTreeStatus: undefined,
    /***************************************************qindehua  EDC*****************************************************/

    controllerAdd: undefined,
    controllerParamSet: undefined,
    controllerDelete: undefined,
    controllerParamGet: undefined,
    remoteUpgrade: undefined,
    controllerTimeSet: undefined,
    controllerTimeGet: undefined,
    controllerRestart: undefined,
    controllerReset: undefined,
    ammeterAdd: undefined,
    ammeterDelete: undefined,
    ammeterGet: undefined,
    ammeterRealTimeDataGet: undefined,
    ammeterHistoryDataGet: undefined,

    /***************************************************qindehua  DDC*****************************************************/

    controllerAddDDC: undefined,
    controllerParamSetDDC: undefined,
    controllerDeleteDDC: undefined,
    controllerParamGetDDC: undefined,
    remoteUpgradeDDC: undefined,
    controllerTimeSetDDC: undefined,
    controllerTimeGetDDC: undefined,
    controllerRestartDDC: undefined,
    controllerResetDDC: undefined,
    besModule: undefined,
    besModuleState: undefined,
    besPoint: undefined,
    besPointDebugger: undefined,
    besPointState: undefined,
    besPlandata: undefined,
    besPlandelete: undefined,
    sceneModelData: undefined,

    /***************************************************qindehua  LDC*****************************************************/

    controllerAddLDC: undefined,
    controllerParamSetLDC: undefined,
    controllerDeleteLDC: undefined,
    controllerParamGetLDC: undefined,
    remoteUpgradeLDC: undefined,
    controllerTimeSetLDC: undefined,
    controllerTimeGetLDC: undefined,
    controllerRestartLDC: undefined,
    controllerResetLDC: undefined,
    moduleParamSetLDC: undefined,
    moduleParamGetLDC: undefined,
    moduleAddLDC: undefined,
    pointAddLDC: undefined,
    pointParamSetLDC: undefined,
    pointParamGetLDC: undefined,
    besPlandataLDC: undefined,
    besPlandeleteLDC: undefined,
    sceneModelDataLDC: undefined,

    /***************************************************qindehua  报警个数*****************************************************/

    alarmCount: undefined,
    alarmList: undefined,
    alarmMsg: undefined,

    /***************************************************wanghongjie  modbus*****************************************************/
    modbusDeviceControllerStstus: undefined,
    modbusServerDeviceStstus: undefined,
    modbusServerDeviceRealTimeData: undefined,
  },

  mutations: {
    // 支付数据
    PAYMENT: (state, data) => {
      state.payment = data
    },
    // 设备实时数据
    IOT_DEVICE_REALTIME_DATA: (state, data) => {
      state.iotDeviceRealtimeData = data
    },
    DEVICE_TREE_CONTROLLER_STSTUS: (state, data) => {
      state.deviceTreeControllerStstus = data
    },
    //设备树状态
    DEVICE_TREE_STSTUS: (state, data) => {
      state.deviceTreeStatus = data
    },
    /***************************************************qindehua EDC*****************************************************/
    //新增一个控制器 EDC
    CONTROLLER_ADD: (state, data) => {
      state.controllerAdd = data
    },
    //设置一个控制器 EDC
    CONTROLLER_PARAM_SET: (state, data) => {
      state.controllerParamSet = data
    },
    //删除一个控制器，并删除和它相关的模块和点 EDC
    CONTROLLER_DELETE: (state, data) => {
      state.controllerDelete = data
    },
    //获取控制器的所有配置参数 EDC
    CONTROLLER_PARAM_GET: (state, data) => {
      state.controllerParamGet = data
    },
    //远程升级 EDC
    REMOTE_UPGRADE: (state, data) => {
      state.remoteUpgrade = data
    },
    //设置控制器的时间 EDC
    CONTROLLER_TIME_SET: (state, data) => {
      state.controllerTimeSet = data
    },
    //获取控制器的时间 EDC
    CONTROLLER_TIME_GET: (state, data) => {
      state.controllerTimeGet = data
    },
    //重启控制器，相当于重启复位 EDC
    CONTROLLER_RESTART: (state, data) => {
      state.controllerRestart = data
    },
    //重置控制器，恢复出厂设置，并重启 EDC
    CONTROLLER_RESET: (state, data) => {
      state.controllerReset = data
    },
    //新增加一个电表信息
    AMMETER_ADD: (state, data) => {
      state.ammeterAdd = data
    },
    //删除一个电表
    AMMETER_DELETE: (state, data) => {
      state.ammeterDelete = data
    },
    //获取一个电表的所有配置信息
    AMMETER_GET: (state, data) => {
      state.ammeterGet = data
    },
    //电表获取实时数据
    AMMETER_REALTIME_DATA_GET: (state, data) => {
      state.ammeterRealTimeDataGet = data
    },
    //电表获取历史数据
    AMMETER_HISTORY_DATA_GET: (state, data) => {
      state.ammeterHistoryDataGet = data
    },

    /***************************************************qindehua DDC*****************************************************/
    //新增一个DDC
    CONTROLLER_ADD_DDC: (state, data) => {
      state.controllerAddDDC = data
    },
    //设置一个DDC
    CONTROLLER_PARAM_SET_DDC: (state, data) => {
      state.controllerParamSetDDC = data
    },
    // 删除一个控制器，并删除和它相关的模块和点 DDC
    CONTROLLER_DELETE_DDC: (state, data) => {
      state.controllerDeleteDDC = data
    },
    // 获取控制器的所有配置参数DDC
    CONTROLLER_PARAM_GET_DDC: (state, data) => {
      state.controllerParamGetDDC = data
    },
    // 远程升级 DDC
    REMOTE_UPGRADE_DDC: (state, data) => {
      state.remoteUpgradeDDC = data
    },
    // 设置控制器的时间 DDC
    CONTROLLER_TIME_SET_DDC: (state, data) => {
      state.controllerTimeSetDDC = data
    },
    // 获取控制器的时间 DDC
    CONTROLLER_TIME_GET_DDC: (state, data) => {
      state.controllerTimeGetDDC = data
    },
    // 重启控制器，相当于重启复位 DDC
    CONTROLLER_RESTART_DDC: (state, data) => {
      state.controllerRestartDDC = data
    },
    // 重置控制器，恢复出厂设置，并重启 DDC
    CONTROLLER_RESET_DDC: (state, data) => {
      state.controllerResetDDC = data
    },
    //楼控模块同步状态
    MODULE_ADD: (state, data) => {
      state.besModuleState = data
    },
    //楼控模块信息
    MODULE_PARAM_GET: (state, data) => {
      state.besModule = data
    },
    //楼控点同步状态
    POINT_ADD: (state, data) => {
      state.besPointState = data
    },
    //楼控点信息
    POINT_PARAM_GET: (state, data) => {
      state.besPoint = data
    },
    //调试回调修改
    POINT_VALUE_SET: (state, data) => {
      state.besPointDebugger = data
    },
    //获取一条计划所有参数
    PLAN_PARAM_GET: (state, data) => {
      state.besPlandata = data
    },
    //删除一条计划
    PLAN_DELETE: (state, data) => {
      state.besPlandelete = data
    },
    //获取场景下的单个模式信息
    SCENE_MODE_PARAM_GET_DDC: (state, data) => {
      state.sceneModelData = data
    },
    /***************************************************qindehua LDC*****************************************************/
    //新增一个LDC
    CONTROLLER_ADD_LDC: (state, data) => {
      state.controllerAddLDC = data
    },
    //设置一个LDC
    CONTROLLER_PARAM_SET_LDC: (state, data) => {
      state.controllerParamSetLDC = data
    },
    // 删除一个控制器，并删除和它相关的模块和点 LDC
    CONTROLLER_DELETE_LDC: (state, data) => {
      state.controllerDeleteLDC = data
    },
    // 获取控制器的所有配置参数LDC
    CONTROLLER_PARAM_GET_LDC: (state, data) => {
      state.controllerParamGetLDC = data
    },
    // 远程升级 LDC
    REMOTE_UPGRADE_LDC: (state, data) => {
      state.remoteUpgradeLDC = data
    },
    // 设置控制器的时间 LDC
    CONTROLLER_TIME_SET_LDC: (state, data) => {
      state.controllerTimeSetLDC = data
    },
    // 获取控制器的时间 LDC
    CONTROLLER_TIME_GET_LDC: (state, data) => {
      state.controllerTimeGetLDC = data
    },
    // 重启控制器，相当于重启复位 LDC
    CONTROLLER_RESTART_LDC: (state, data) => {
      state.controllerRestartLDC = data
    },
    // 重置控制器，恢复出厂设置，并重启 LDC
    CONTROLLER_RESET_LDC: (state, data) => {
      state.controllerResetLDC = data
    },
    //照明模块新增
    MODULE_ADD_LDC: (state, data) => {
      state.moduleAddLDC = data
    },
    //照明模块同步状态
    MODULE_PARAM_SET_LDC: (state, data) => {
      state.moduleParamSetLDC = data
    },
    //照明模块获取信息
    MODULE_PARAM_GET_LDC: (state, data) => {
      state.moduleParamGetLDC = data
    },
    //照明逻辑点新增
    POINT_ADD_LDC: (state, data) => {
      state.pointAddLDC = data
    },
    //照明逻辑点同步状态
    POINT_PARAM_SET_LDC: (state, data) => {
      state.pointParamSetLDC = data
    },
    //照明逻辑点获取信息
    POINT_PARAM_GET_LDC: (state, data) => {
      state.pointParamGetLDC = data
    },
    //获取一条计划所有参数
    PLAN_PARAM_GET_LDC: (state, data) => {
      state.besPlandataLDC = data
    },
    //删除一条计划
    PLAN_DELETE_LDC: (state, data) => {
      state.besPlandeleteLDC = data
    },
    //获取场景下的单个模式信息
    SCENE_MODE_PARAM_GET_LDC: (state, data) => {
      state.sceneModelDataLDC = data
    },
    ALARM_COUNT: (state, data) => {
      state.alarmCount = data
    },
    ALARM_LIST: (state, data) => {
      state.alarmList = data
    },
    ALARMMSG: (state, data) => {
      state.alarmMsg = data
    },
    /***************************************************wanghongjie modbus*****************************************************/
    MODBUS_DEVICE_CONTROLLER_STSTUS: (state, data) => {
      state.modbusDeviceControllerStstus = data
    },
    MODBUS_SERVER_DEVICE_STSTUS: (state, data) => {
      state.modbusServerDeviceStstus = data;
    },
    MODBUS_SERVER_DEVICE_REAL_TIME_DATA: (state, data) => {
      state.modbusServerDeviceRealTimeData = data;
    }
  },


  actions: {}
}

export default websocket
