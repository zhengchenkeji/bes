export const calcDate = (date1, date2) => {
  let date3 = date2 - date1;

  let days = Math.floor(date3 / (24 * 3600 * 1000))

  let leave1 = date3 % (24 * 3600 * 1000) //计算天数后剩余的毫秒数
  let hours = Math.floor(leave1 / (3600 * 1000))

  let leave2 = leave1 % (3600 * 1000) //计算小时数后剩余的毫秒数
  let minutes = Math.floor(leave2 / (60 * 1000))

  let leave3 = leave2 % (60 * 1000) //计算分钟数后剩余的毫秒数
  let seconds = Math.round(date3 / 1000)
  return {
    leave1,
    leave2,
    leave3,
    days: days,
    hours: hours,
    minutes: minutes,
    seconds: seconds,
  }
}

/**
 * 日期格式化
 */
export function dateFormat(date, format) {
  format = format || 'yyyy-MM-dd hh:mm:ss';
  if (date !== 'Invalid Date') {
    let o = {
      "M+": date.getMonth() + 1, //month
      "d+": date.getDate(), //day
      "h+": date.getHours(), //hour
      "m+": date.getMinutes(), //minute
      "s+": date.getSeconds(), //second
      "q+": Math.floor((date.getMonth() + 3) / 3), //quarter
      "S": date.getMilliseconds() //millisecond
    }
    if (/(y+)/.test(format)) format = format.replace(RegExp.$1,
      (date.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (let k in o)
      if (new RegExp("(" + k + ")").test(format))
        format = format.replace(RegExp.$1,
          RegExp.$1.length === 1 ? o[k] :
            ("00" + o[k]).substr(("" + o[k]).length));
    return format;
  }
  return '';

}

export function parseTimeFormat(time, cFormat) {
  if (arguments.length === 0) {
    return null
  }
  const format = cFormat || '{y}-{m}-{d} {h}:{i}:{s}'
  let date
  if (typeof time === 'object') {
    date = time
  } else {
    if ((typeof time === 'string') && (/^[0-9]+$/.test(time))) {
      time = parseInt(time)
    }
    if ((typeof time === 'number') && (time.toString().length === 10)) {
      time = time * 1000
    }
    date = new Date(time)
  }
  const formatObj = {
    y: date.getFullYear(),
    m: date.getMonth() + 1,
    d: date.getDate(),
    h: date.getHours(),
    i: date.getMinutes(),
    s: date.getSeconds(),
    a: date.getDay()
  }
  const time_str = format.replace(/{(y|m|d|h|i|s|a)+}/g, (result, key) => {
    let value = formatObj[key]
    // Note: getDay() returns 0 on Sunday
    if (key === 'a') { return ['日', '一', '二', '三', '四', '五', '六'][value ] }
    if (result.length > 0 && value < 10) {
      value = '0' + value
    }
    return value || 0
  })
  return time_str
}

/**
 * 将日期格式化成指定格式的字符串
 * @param date 要格式化的日期，不传时默认当前时间，也可以是一个时间戳
 * @param fmt 目标字符串格式，支持的字符有：y,M,d,q,w,H,h,m,S，默认：yyyy-MM-dd HH:mm:ss
 * @returns 返回格式化后的日期字符串
 */
export function formatDate(date, fmt) {
  if (!date) {
    date = new Date()
  }
  if (!(date instanceof Date)) {
    date = new Date(Number(date))
  }
  fmt = fmt || 'yyyy-MM-dd HH:mm:ss'
  const obj =
    {
      'y': date.getFullYear(), // 年份，注意必须用getFullYear
      'M': date.getMonth() + 1, // 月份，注意是从0-11
      'd': date.getDate(), // 日期
      'q': Math.floor((date.getMonth() + 3) / 3), // 季度
      'w': date.getDay(), // 星期，注意是0-6
      'H': date.getHours(), // 24小时制
      'h': date.getHours() % 12 === 0 ? 12 : date.getHours() % 12, // 12小时制
      'm': date.getMinutes(), // 分钟
      's': date.getSeconds(), // 秒
      'S': date.getMilliseconds() // 毫秒
    }
  const week = ['天', '一', '二', '三', '四', '五', '六']
  for (let i in obj) {
    fmt = fmt.replace(new RegExp(i + '+', 'g'), (m) => {
      let val = obj[i] + ''
      if (i === 'w') return (m.length > 2 ? '星期' : '周') + week[val]
      for (let j = 0, len = val.length; j < m.length - len; j++) val = '0' + val
      return m.length === 1 ? val : val.substring(val.length - m.length)
    })
  }
  return fmt
}

// formatDate(); // 2016-09-02 13:17:13
// formatDate(new Date(), 'yyyy-MM-dd'); // 2016-09-02
// // 2016-09-02 第3季度 星期五 13:19:15:792
// formatDate(new Date(), 'yyyy-MM-dd 第q季度 www HH:mm:ss:SSS');
// formatDate(1472793615764); // 2016-09-02 13:20:15

/**
 * 将字符串解析成日期
 * @param str 输入的日期字符串，如'2014-09-13'
 * @param fmt 字符串格式，默认'yyyy-MM-dd'，支持如下：y、M、d、H、m、s、S，不支持w和q
 * @returns date 解析后的Date类型日期
 */
export function parseDate(str, fmt) {
  fmt = fmt || 'yyyy-MM-dd'
  const obj = { y: 0, M: 1, d: 0, H: 0, h: 0, m: 0, s: 0, S: 0 }
  fmt.replace(/([^yMdHmsS]*?)(([yMdHmsS])\3*)([^yMdHmsS]*?)/g, (m, $1, $2, $3, $4, idx, old) => {
    str = str.replace(new RegExp($1 + '(\\d{' + $2.length + '})' + $4), (_m, _$1) => {
      obj[$3] = parseInt(_$1)
      return ''
    })
    return ''
  })
  obj.M-- // 月份是从0开始的，所以要减去1
  const date = new Date(obj.y, obj.M, obj.d, obj.H, obj.m, obj.s)
  if (obj.S !== 0) date.setMilliseconds(obj.S) // 如果设置了毫秒
  return date
}

// parseDate('2016-08-11'); // Thu Aug 11 2016 00:00:00 GMT+0800
// parseDate('2016-08-11 13:28:43', 'yyyy-MM-dd HH:mm:ss') // Thu Aug 11 2016 13:28:43 GMT+0800

/**
 * 将一个日期格式化成友好格式，比如，1分钟以内的返回“刚刚”，
 * 当天的返回时分，当年的返回月日，否则，返回年月日
 * @param {Object} date
 */
export function formatDateToFriendly(date) {
  date = date || new Date()
  date = typeof date === 'number' ? new Date(date) : date
  let now = new Date()
  if ((now.getTime() - date.getTime()) < 60 * 1000) return '刚刚' // 1分钟以内视作“刚刚”
  let temp = this.formatDate(date, 'yyyy年M月d')
  if (temp === this.formatDate(now, 'yyyy年M月d')) return this.formatDate(date, 'HH:mm')
  if (date.getFullYear() === now.getFullYear()) return this.formatDate(date, 'M月d日')
  return temp
}

/**
 * 将一段时长转换成友好格式，如：
 * 147->“2分27秒”
 * 1581->“26分21秒”
 * 15818->“4小时24分”
 * @param {Object} second
 */
export function formatDurationToFriendly(second) {
  if (second < 60) {
    return second + '秒'
  } else if (second < 60 * 60) {
    return (second - second % 60) / 60 + '分' + second % 60 + '秒'
  } else if (second < 60 * 60 * 24) return (second - second % 3600) / 60 / 60 + '小时' + Math.round(second % 3600 / 60) + '分'
  return (second / 60 / 60 / 24).toFixed(1) + '天'
}

/**
 * 将时间转换成MM:SS形式
 */
export function formatTimeToFriendly(second) {
  let m = Math.floor(second / 60)
  m = m < 10 ? ('0' + m) : m
  let s = second % 60
  s = s < 10 ? ('0' + s) : s
  return m + ':' + s
}

/**
 * 判断某一年是否是闰年
 * @param year 可以是一个date类型，也可以是一个int类型的年份，不传默认当前时间
 */
export function isLeapYear(year) {
  if (year === undefined) year = new Date()
  if (year instanceof Date) year = year.getFullYear()
  return (year % 4 === 0 && year % 100 !== 0) || (year % 400 === 0)
}

/**
 * 获取某一年某一月的总天数，没有任何参数时获取当前月份的
 * 方式一：$.getMonthDays();
 * 方式二：$.getMonthDays(new Date());
 * 方式三：$.getMonthDays(2013, 12);
 */
export function getMonthDays(date, month) {
  let y, m
  if (!date) date = new Date()
  if (date instanceof Date) {
    y = date.getFullYear()
    m = date.getMonth()
  } else if (typeof date == 'number') {
    y = date
    m = month - 1
  }
  const days = [31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31] // 非闰年的一年中每个月份的天数
  //如果是闰年并且是2月
  if (m === 1 && this.isLeapYear(y)) return days[m] + 1
  return days[m]
}

/**
 * 计算2日期之间的天数，用的是比较毫秒数的方法
 * 传进来的日期要么是Date类型，要么是yyyy-MM-dd格式的字符串日期
 * @param date1 日期一
 * @param date2 日期二
 */
export function countDays(date1, date2) {
  const fmt = 'yyyy-MM-dd'
  // 将日期转换成字符串，转换的目的是去除“时、分、秒”
  if (date1 instanceof Date && date2 instanceof Date) {
    date1 = this.format(fmt, date1)
    date2 = this.format(fmt, date2)
  }
  if (typeof date1 === 'string' && typeof date2 === 'string') {
    date1 = this.parse(date1, fmt)
    date2 = this.parse(date2, fmt)
    return (date1.getTime() - date2.getTime()) / (1000 * 60 * 60 * 24)
  } else {
    console.error('参数格式无效！')
    return 0
  }
}

