/**
 * 自定义 WebSocket 扩展对象
 */
class Socket extends WebSocket {
  /**
   * 要连接的URL；这应该是WebSocket服务器将响应的URL。
   */
  url;
  /**
   *  可选
   * 一个协议字符串或者一个包含协议字符串的数组。这些字符串用于指定子协议，这样单个服务器可以实现多个WebSocket子协议
   * （例如，您可能希望一台服务器能够根据指定的协议（protocol）处理不同类型的交互）。如果不指定协议字符串，则假定为空字符串。
   */
  protocols;
  /**
   *  令牌（作为身份识别）
   */
  tokenSN;
  /**
   * 密码（验证是否合法）
   */
  password;
  /**
   * 心跳周期(单位：秒)
   */
  heartRate;
  /**
   * 心跳状态
   * 0：没有心跳
   * 1：存在心跳
   */
  lifeState;

  /**
   * 有心跳的
   * @type {number}
   */
  ALIVE = 1;
  /**
   * 无心跳的
   * @type {number}
   */
  DIE = 0;

  constructor(options) {

    super(options.url, options.protocols);
    this.url = options.url;
    this.protocols = options.protocols;

    const tokenSN = options.tokenSN;

    if (!tokenSN || typeof tokenSN !== 'string') {
      throw new TypeError('参数 tokenSN 错误！tokenSN 必须是一个有效字符串');
    }

    this.tokenSN = tokenSN;

    const password = options.password;

    if (!password) {
      throw new TypeError('参数 password 错误！参数 password 不存在');
    }

    this.password = password;

    const heartRate = options.heartRate;

    if (isNaN(heartRate)) {
      throw new TypeError('参数 heartRate 错误！参数 heartRate 必须是数字');
    }

    this.heartRate = heartRate;

  }

  /**
   * 心跳
   */
  heartbeat() {

    if (this.lifeState === this.ALIVE) {
      return;
    }

    const engine = setInterval(() => {

      // 如果连接处于连接状态则发送心跳
      if (this.readyState === this.OPEN) {

        try {
          this.send(JSON.stringify({
            method : 'heartbeat'
          }));
          this.lifeState = this.ALIVE;
        }catch (e) {}
      }

      // 连接关闭状态时，关闭心跳
      if (this.readyState === this.CLOSED) {
        clearInterval(engine);
        this.lifeState = this.DIE;
      }

    }, this.heartRate * 1000 * 0.8);

  }

  /**
   * 登录认证
   */
  login() {

    if (this.readyState !== this.OPEN) {
      return;
    }

    var loginInfo = {
      method : 'login',
      params : {
        tokenSN : this.tokenSN,
        password : this.password
      }

    };
    this.send(JSON.stringify(loginInfo));
  }

  /**
   * 初始化
   * @param options
   */
  static initialize(options) {

    Socket.instance = new Socket(options);

    // 连接打开时事件
    Socket.instance.addEventListener('open', (event) => {

      const onopen = Socket.onopen;

      if (typeof onopen === 'function') {
        onopen(event);
      }

      // 登录认证
      Socket.instance.login();
      // 心跳保活
      Socket.instance.heartbeat();

    });

    Socket.instance.addEventListener('close', (event) => {

      const onclose = Socket.onclose;

      if (typeof onclose === 'function') {
        onclose(event);
      }

      // 10 秒尝试重连
      setTimeout(() => {
        console.warn('websocket 已断开连接，尝试再次连接');
        Socket.initialize(options);
      }, 10 * 1000)

    });

    Socket.instance.addEventListener('error', (event) => {

      const onerror = Socket.onerror;

      if (typeof onerror === 'function') {
        onerror(event);
      }
    });

    Socket.instance.addEventListener('message', (event) => {

      const onmessage = Socket.onmessage;

      if (typeof onmessage === 'function') {
        onmessage(event.data);
      }
    });

  }

  static send(data) {

    const socket = Socket.instance

    if (socket === null) return;

    socket.send(JSON.stringify(data));

  }

}

/**
 * socket 实例对象
 * @type {null}
 */
Socket.instance = null;

/**
 * 用于指定连接关闭后的回调函数。
 * @type {null}
 */
Socket.onclose = null;
/**
 * 用于指定连接失败后的回调函数。
 * @type {null}
 */
Socket.onerror = null;
/**
 * 用于指定当从服务器接受到信息时的回调函数。
 * @type {null}
 */
Socket.onmessage = null;
/**
 * 用于指定当从服务器接受到信息时的回调函数。
 * @type {null}
 */
Socket.onopen = null;

export { Socket };
