/**
 * xiepufeng
 * 事件发布订阅
 * @type {{subscribe: (function(*, *=): PubSub), publish: PubSub.publish, unsubscribe: PubSub.unsubscribe}}
 */
import {subscribe, subscribeList, unsubscribe, unsubscribeList} from '@/api/basicData/pubsubmanage/pubsubmanage'

//发布订阅模式
 class pubSub {
   // 发布者首先提供一些窗口让订阅者可以订阅某些事件 例如：addEventListener
   // 发布者可以触发订阅的事件,也就是通知订阅者
   // 发布者可以解除某些订阅
   on(event,callback,childEvent){  //订阅的事件:发布者首先提供一些窗口让订阅者可以订阅某些事件    第一个形参：订阅的事件名字，第二个形参：触发的回调
     var calls = this._callbacks || (this._callbacks = {});

     let boo = false;

     boo = Array.isArray(event);

     if (boo) {

       let itemList = [];
       for(var item of event) {
         if(!calls[item]){ //判断是否有多个订阅者，因为可能同一个事件，有多个订阅者，他们的回调个不同。
           // 声明一个对象映射表。 如果隐射表中没有相应的事件名称的话，就把回调直接撒进一个数组，否则push进去数组。
           // this.obj[event] = [callback]
           calls[item] = new Map();

           itemList.push(item.toString());

         }
         calls[item].put(childEvent || callback.name, callback);
       }

       if (typeof subscribeEventList === 'function')
       {
         subscribeEventList(itemList);
       }

     } else {
       if(!calls[event]){ //判断是否有多个订阅者，因为可能同一个事件，有多个订阅者，他们的回调个不同。
         // 声明一个对象映射表。 如果隐射表中没有相应的事件名称的话，就把回调直接撒进一个数组，否则push进去数组。
         // this.obj[event] = [callback]
         calls[event] = new Map();

         if (typeof subscribeEvent === 'function')
         {
           subscribeEvent(event);
         }
       }
       calls[event].put(childEvent || callback.name, callback);
     }


   }

   trigger(event,data){ //发布者可以触发订阅的事件,也就是通知订阅者。 第一个形参是事件名称
     // 将arguments 对象，转换为真正的数组
     var args = Array.prototype.slice.call(arguments, 0);
// 拿出第一个参数，即信道名称（事件名称）
     var ev = args.shift();
     var map;
     var calls = this._callbacks;
     // 如果不存在 _callbacks 对象则返回
     if (!calls)
     {
       return this;
     }
     //  如果不包含给定事件所对应的数组，同样返回
     if (!(map = calls[ev]))
     {
       return this;
     }

     // 依次触发事件对应的回调函数
     map.values().forEach(v => {
       v.apply(null, args);
     })

     return this;

   }
   remove(event, childEvent) {

     let calls = this._callbacks;

     if (typeof calls == 'undefined') {
       return
     }

     if (!event && !childEvent)
     {

       for (var key in calls)
       {
         delete calls[key];

         if (typeof unsubscribeEvent === 'function')
         {
           unsubscribeEvent(event);
         }
       }

       return true;

     }
     else if (calls[event])
     {
       if (!childEvent)
       {
         delete calls[event];

         if (typeof unsubscribeEvent === 'function')
         {
           unsubscribeEvent(event);
         }

         return true;
       }

       let eventMap = calls[event];

       let childEventName = childEvent.name || childEvent;

       if (!eventMap || eventMap.size === 0)
       {
         delete calls[event];

         if (typeof unsubscribeEvent === 'function')
         {
           unsubscribeEvent(event);
         }

         return true;
       }

       if (eventMap.get(childEventName))
       {
         eventMap.remove(childEventName);
       }

       if (eventMap.isEmpty())
       {
         delete calls[event];

         if (typeof unsubscribeEvent === 'function')
         {
           unsubscribeEvent(event);
         }
       }

       return true;

     }
     else if (!event && childEvent)
     {

       let keyList = [];

       let boo = false;

       boo = Array.isArray(childEvent);

       if (boo) {
         for(var item of childEvent) {

           let childEventName = item.name || item;

           for (var key in calls)
           {
             let eventMap = calls[key];

             let childEvent = eventMap.get(childEventName);

             if (childEvent)
             {
               eventMap.remove(childEventName);
             }

             if (eventMap.isEmpty())
             {
               delete calls[key];

               keyList.push(key.toString());
             }
           }

         }

         if (typeof unsubscribeEventList === 'function')
         {
           unsubscribeEventList(keyList);
         }

       } else {
         let childEventName = childEvent.name || childEvent;

         for (var key in calls)
         {
           let eventMap = calls[key];

           let childEvent = eventMap.get(childEventName);

           if (childEvent)
           {
             eventMap.remove(childEventName);
           }

           if (eventMap.isEmpty())
           {
             delete calls[key];

             if (typeof unsubscribeEvent === 'function')
             {
               unsubscribeEvent(key);
             }
           }
         }
       }



       // if (typeof unsubscribeEventList === 'function')
       // {
       //   unsubscribeEventList(keyList);
       // }

       return true;

     }
   }

   //销毁设计器所有组件订阅
   destructionAllDesignerSub() {
     let websocketName = [];
     websocketName.push('VtextWebsocket');
     websocketName.push('VtextWebsocket1');
     websocketName.push('VLightWebsocket');
     websocketName.push('VLightWebsocket1');

     this.remove(null, websocketName);
     // setTimeout(() => {
     //   this.remove(null, 'VLightWebsocket');
     //   setTimeout(() => {
     //     this.remove(null, 'VtextWebsocket1');
     //     setTimeout(() => {
     //       this.remove(null, 'VLightWebsocket1');
     //     },1100);
     //   },1100);
     // },1100);




   }
}

function subscribeEvent(event) {
  subscribe({
    event:event.toString()
  }).then(res => {

  })
};

function unsubscribeEvent(event) {
  unsubscribe({
    event:event.toString()
  }).then(res => {

  })
};

function subscribeEventList(eventList) {
  subscribeList(JSON.stringify(eventList)).then(res => {

  })
};

function unsubscribeEventList(eventList) {
  unsubscribeList(eventList).then(res => {

  })
};

function Map(){
  //定义长度
  var length = 0;
  //创建一个对象
  var obj = new Object();

  /**
   * 判断Map是否为空
   */
  this.isEmpty = function(){
    return length == 0;
  };

  /**
   * 判断对象中是否包含给定Key
   */
  this.containsKey=function(key){
    return (key in obj);
  };

  /**
   * 判断对象中是否包含给定的Value
   */
  this.containsValue=function(value){
    for(var key in obj){
      if(obj[key] == value){
        return true;
      }
    }
    return false;
  };

  /**
   *向map中添加数据
   */
  this.put=function(key,value){
    if(!this.containsKey(key)){
      length++;
    }
    obj[key] = value;
  };

  /**
   * 根据给定的Key获得Value
   */
  this.get=function(key){
    return this.containsKey(key)?obj[key]:null;
  };

  /**
   * 根据给定的Key删除一个值
   */
  this.remove=function(key){
    if(this.containsKey(key)&&(delete obj[key])){
      length--;
    }
  };

  /**
   * 获得Map中的所有Value
   */
  this.values=function(){
    var _values= new Array();
    for(var key in obj){
      _values.push(obj[key]);
    }
    return _values;
  };

  /**
   * 获得Map中的所有Key
   */
  this.keySet=function(){
    var _keys = new Array();
    for(var key in obj){
      _keys.push(key);
    }
    return _keys;
  };

  /**
   * 获得Map的长度
   */
  this.size = function(){
    return length;
  };

  /**
   * 清空Map
   */
  this.clear = function(){
    length = 0;
    obj = new Object();
  };
}

export default new pubSub();
