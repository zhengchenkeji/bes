//选则组件时递归遍历树 是灯光则开启禁用，不是则关闭禁用
export function forEachChildren(data, type) {
  data.forEach((item, i) => {
    if (item.children != null && item.children != undefined && item.children.length > 0) {
      // 调用递归函数
      forEachChildren(item.children, type)
    } else {
      if (type == 'light') {
        //灯光  只有读写类型且只有一个寄存器的功能可以选择
        if (item.readType == '1' && item.haveParams == '0') {
          item['disabled'] = false;
        } else {
          //禁用复选框
          item['disabled'] = true;
        }
      } else if (type == 'button') {
        //按钮   只有包含写类型的功能可以选择
        if (item.readType == '1' || item.readType == '2') {
          item['disabled'] = false;
        } else {
          //禁用复选框
          item['disabled'] = true;
        }
      } else if(type == 'view') {
        item['disabled'] = true;
      }else{
        item['disabled'] = false;
      }

    }
  })
  return data;
}

