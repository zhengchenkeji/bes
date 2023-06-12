<template>
  <div class="app-container">

    <!--    <div id="container" ></div>-->


    <!--    <vue-3d-model
          :obj-model="obj"
          :position="position"
          :rotation="rotation"
          :scale="scale"
        >
          <vue-3d-model
            v-for="(button, index) in buttons"
            :key="index"
            :obj-model="button.obj"
            :position="button.position"
            :rotation="button.rotation"
            :scale="button.scale"
            @click="handleButtonClick(index)"
          />
        </vue-3d-model>-->

    <!-- <model-obj :src="obj"
                z-index="-1"
                :position="position"
                :rotation="rotation"
                :scale="scale"
                :lights="lights"
                :controls-options="{
            enableRotate: true,  // 是否可旋转
            enableZoom: true,    // 是否可缩放
            enablePan: true      // 是否可移动（鼠标右键可以移动模型）
        }"
     >
     </model-obj>-->


    <div id="model" ref="model">

      <div id="btns1">
        111111wwwww
      </div>
      <!--      <div >canvas1</div>-->
      <div id="canvas1" class="BoxWrap">
        <div class="horn">
          开关:开
          <div class="lt"></div>
          <div class="rt"></div>
          <div class="rb"></div>
          <div class="lb"></div>
        </div>
      </div>

      <div id="canvas2">canvas2</div>
      <div id="canvas3">canvas3</div>
      <div id="canvas4" style="display: none">
        <span @click.stop="deviceDetail(1)">设备详情</span>
      </div>
      <div id="canvas5" style="display: none">
        <span @click="deviceDetail(2)">设备详情</span>
      </div>
      <div id="canvas6" style="display: none">
        <span @click.stop="deviceDetail(3)">设备详情</span>
      </div>
      <div id="btns">
        <span @click.stop="toView(1)">主视角</span>
        <span @click.stop="toView(2)">俯视角</span>
      </div>
    </div>


  </div>
</template>

<script>
  import {
    ModelObj,
    ModelThree,
    modelFbx,
    ModelCollada,
    ModelStl,
    ModelPly,
    ModelGltf
  } from "vue-3d-model";


  import * as THREE from 'three'
  import {OrbitControls} from 'three/examples/jsm/controls/OrbitControls'
  import {OBJLoader} from 'three/examples/jsm/loaders/OBJLoader'
  import { MTLLoader } from 'three/examples/jsm/loaders/MTLLoader'

  import {EffectComposer} from 'three/examples/jsm/postprocessing/EffectComposer.js'
  import {RenderPass} from 'three/examples/jsm/postprocessing/RenderPass.js'
  import {OutlinePass} from 'three/examples/jsm/postprocessing/OutlinePass.js'
  import TWEEN from '@tweenjs/tween.js'
  import $ from 'jquery'
  import {
    CSS3DRenderer,
    CSS3DObject,
  } from 'three/examples/jsm/renderers/CSS3DRenderer'

  export default {
    name: "ModulePoint",
    components: {
      ModelObj,
      ModelThree,
      modelFbx,
      ModelCollada,
      ModelStl,
      ModelPly,
      ModelGltf
    },
    data() {
      return {
        // obj: '/static/low_DEF.obj',
        // // mtl: 'path/to/mtl',
        // position: { x: 0, y: 0, z: 0 },
        // rotation: { x: 0, y: 0, z: 0 },
        // scale: { x: 1, y: 1, z: 1 },
        // lights: [
        //   {
        //     type: 'AmbientLight',
        //     position: { x: 500, y: 100, z: 100 },
        //     skyColor: 0xffffff,
        //     // groundColor: 0xFF0000, // 此代码为灯光后颜色
        //     intensity: 0.1,
        //   },
        //   {
        //     type: 'DirectionalLight',
        //     position: {x: 10, y: 10, z: 10},
        //     color: 0xffffff,
        //     intensity: 0.8,
        //   }],
        // camera: null,
        // scene: null,
        // renderer: null,
        // mesh: null,
        // raycaster: null,
        // mouse:null,
        // tag:0,
        // controls: null,

        scene: null,
        camera: null,
        renderer: null,
        controls: null,
        cssScene: null,
        cssRender: null,
        cssControls: null,
        light: null,
        light2: null,
        flowingLineTexture: null,
        group: new THREE.Group(),
        cardGroup: new THREE.Group(),
        composer: null, // 控制发光
        outlinePass: null,
        renderPass: null,
        // 选中的模型
        selectedObjects: [],
        mouse: new THREE.Vector2(),
        raycaster: new THREE.Raycaster(),
        tween: null,
        // 数据显示面板  1-3 数据    4-6为设备详情按钮
        dataPanel: {
          plane1: null,
          plane2: null,
          plane3: null,
          plane4: null,
          plane5: null,
          plane6: null,
        },
        // 管道纹理
        flowingLineTexture1: null,
        flowingLineTexture2: null,
      };
    },
    created() {
      this.getList();
    },
    mounted() {
      if (this.scene !== null) {
        this.scene = null
      }
      this.draw()
      //调用初始化和animate的函数效果
      // this.init();
      // this.animate();
    },
    destroyed() {
      this.scene.autoUpdate = false
      cancelAnimationFrame(this.animate)
      this.renderer.domElement.innerHTML = ''
      this.renderer.forceContextLoss()
      this.renderer.dispose()
      this.scene.children = []
    },
    methods: {
      initScene() {
        this.scene = new THREE.Scene()
        this.scene.background = new THREE.Color('skyblue');
        this.cssScene = new THREE.Scene()
      },
      initCamera() {
        this.camera = new THREE.PerspectiveCamera(
          45,
          // this.$refs.model.clientWidth / this.$refs.model.clientHeight,
          1676 / 1032,
          // window.innerWidth / window.innerHeight,
          0.1,
          50000
        )
        //相机位置
        this.camera.position.set(0, 1000, 2900)
        // this.camera.lookAt(new THREE.Vector3(0,0,0))
      },
      initLight() {
        //环境光
        var ambientLight = new THREE.AmbientLight(0x404040)
        this.scene.add(ambientLight)

        //平行光
        this.light = new THREE.DirectionalLight(0x333333)
        this.light.position.set(20, 20, 20)
        this.light2 = new THREE.DirectionalLight(0xdddddd)
        this.light2.position.set(-20, 20, -20)
        this.scene.add(this.light)
        this.scene.add(this.light2)
      },
      initRender() {
        //dom元素渲染器
        this.cssRender = new CSS3DRenderer({antialias: true})
        // this.cssRender.setSize(this.$refs.model.clientWidth, this.$refs.model.clientHeight)
        console.log(this.$refs.model.offsetWidth + "qqqq" + this.$refs.model.offsetHeight)


        this.cssRender.setSize(1676, 1032)
        // this.cssRender.setSize(window.innerWidth, window.innerHeight)
        this.cssRender.domElement.style.position = 'absolute'
        this.cssRender.domElement.style.top = '0'
        this.cssRender.domElement.style.outline = 'none'
        document.getElementById('model').appendChild(this.cssRender.domElement)

        this.renderer = new THREE.WebGLRenderer({antialias: true})
        // this.renderer.setSize(this.$refs.model.clientWidth, this.$refs.model.clientHeight)
        this.renderer.setSize(1676, 1032)
        // this.renderer.setSize(window.innerWidth, window.innerHeight)
        this.renderer.setClearColor(new THREE.Color(0x01050f))
        //window.devicePixelRatio 当前设备的物理分辨率与css分辨率之比
        this.renderer.setPixelRatio(window.devicePixelRatio)
        //按层级先后渲染
        this.renderer.sortObjects = true
        document.getElementById('model').appendChild(this.renderer.domElement)
      },
      addModel() {
        var loader = new OBJLoader();
        // 没有材质文件，系统自动设置Phong网格材质

        let that = this;
        const mtlLoader = new MTLLoader()

        mtlLoader.load('/static/car4.mtl', function( materials ) {

          materials.preload();

          loader.load('/static/car4.obj', function (obj) {
            // 加载后的一些编辑操作
            // obj.children[0].material.color.set(0xff0000);//设置材质颜色
            const geometry = obj.children[0].geometry;
            const py = new THREE.Mesh( geometry, obj.children[0].material);
            py.scale.set(30, 30, 30);//网格模型缩放
            py.position.x = -600;
            py.position.y = 340;
            py.position.z = 500;
            py.name = "cube4";
            that.scene.add(py);
          })

        });


      },
      initModel() {
        //坐标系
        var axes = new THREE.AxesHelper(4000)
        this.scene.add(axes)

        var planGeometry = new THREE.PlaneGeometry(30000, 20000)
        //网格Lambert材质
        var planeMaterial = new THREE.MeshLambertMaterial({
          color: 0xcccccc,
          side: THREE.DoubleSide,
          // wireframe:true
        })
        var plane = new THREE.Mesh(planGeometry, planeMaterial)
        plane.rotation.x = -Math.PI / 2
        plane.position.set(0, 0, 0)
        this.scene.add(plane)

        var cubeGeometry = new THREE.BoxGeometry(400, 200, 200)
        var cubeMaterial = new THREE.MeshLambertMaterial({
          color: 0x00ff00,
          transparent: true,//是否透明
          opacity: 0.8,//不透明度
        })
        var cube = new THREE.Mesh(cubeGeometry, cubeMaterial)
        cube.position.set(0, 120, 0)
        cube.name = 'cube1'
        this.scene.add(cube)

        var cubeGeometry2 = new THREE.BoxGeometry(400, 200, 200)
        var cubeMaterial2 = new THREE.MeshLambertMaterial({
          color: 0x00ffff,
          transparent: true,
          opacity: 0.8,
        })
        var cube2 = new THREE.Mesh(cubeGeometry2, cubeMaterial2)
        cube2.position.set(1000, 120, -500)
        cube2.name = 'cube2'
        this.scene.add(cube2)

        var cubeGeometry3 = new THREE.BoxGeometry(400, 200, 200)
        var cubeMaterial3 = new THREE.MeshLambertMaterial({
          color: 0xff0000,
          transparent: true,
          opacity: 0.8,
        })
        var cube3 = new THREE.Mesh(cubeGeometry3, cubeMaterial3)
        cube3.position.set(-1000, 100, 500)
        cube3.name = 'cube3'
        this.scene.add(cube3)
        // 生成管道
        this.generatePipe(1)
        this.generatePipe(2)

        //添加模型
        this.addModel();
        // 生成数据显示面板
        this.generateDataPanel(1, 0, 400, 0)
        this.generateDataPanel(2, 1000, 400, -500)
        this.generateDataPanel(3, -1000, 400, 500)
        this.generateDataPanel(4, 100, 0, 120)
        this.generateDataPanel(5, 1000, 0, -400)
        this.generateDataPanel(6, -1000, 100, 700)

        // 出现数据显示面板
        // for (var i = 1; i <= 2; i++) {
        //   $('#canvas' + i).css('display', 'block')
        // }

        // $('#btns').css('display', 'block')
      },
      //高亮显示模型（呼吸灯）
      outlineObj(selectedObjects) {
        // 创建一个EffectComposer（效果组合器）对象，然后在该对象上添加后期处理通道。
        this.composer = new EffectComposer(this.renderer)
        // 新建一个场景通道  为了覆盖到原理来的场景上
        this.renderPass = new RenderPass(this.scene, this.camera)
        this.composer.addPass(this.renderPass)
        // 物体边缘发光通道
        this.outlinePass = new OutlinePass(
          new THREE.Vector2(this.$refs.model.clientWidth, this.$refs.model.clientHeight),
          this.scene,
          this.camera,
          selectedObjects
        )

        this.outlinePass.edgeStrength = 5.0 // 高光边缘边框的亮度
        this.outlinePass.edgeGlow = 1 // 光晕[0,1]  边缘微光强度
        this.outlinePass.usePatternTexture = false // 是否使用父级的材质，纹理覆盖
        this.outlinePass.edgeThickness = 1 // 边框宽度，高光厚度
        this.outlinePass.downSampleRatio = 1 // 边框弯曲度
        this.outlinePass.pulsePeriod = 3 // 呼吸闪烁的速度，数值越大，律动越慢
        this.outlinePass.visibleEdgeColor.set(parseInt(0xff800)) // 呼吸显示的颜色
        this.outlinePass.hiddenEdgeColor = new THREE.Color(0, 0, 0) // 呼吸消失的颜色
        // this.outlinePass.clear = true
        this.composer.addPass(this.outlinePass) // 加入高光特效
        this.outlinePass.selectedObjects = selectedObjects // 需要高光的模型
      },
      // 鼠标点击模型
      onMouseClick(event) {
        //通过鼠标点击的位置计算出raycaster所需要的点的位置，以屏幕中心为原点，值的范围为-1到1
        // this.$refs.model.clientWidth
        // this.mouse.x = (event.clientX / this.$refs.model.clientWidth) * 2 - 1
        // this.mouse.y = -(event.clientY / this.$refs.model.clientHeight) * 2 + 1
        let getBoundingClientRect = this.$refs.model.getBoundingClientRect()
        this.mouse.x = ((event.clientX - getBoundingClientRect.left) / this.$refs.model.offsetWidth) * 2 - 1
        this.mouse.y = -((event.clientY - getBoundingClientRect.top) / this.$refs.model.offsetHeight) * 2 + 1
        // 通过鼠标点的位置和当前相机的矩阵计算出raycaster
        this.raycaster.setFromCamera(this.mouse, this.camera)
        // 获取raycaster直线和所有模型相交的数组集合
        var intersects = this.raycaster.intersectObjects(this.scene.children)
        if (intersects[0].object.geometry instanceof THREE.PlaneGeometry) {
          return
        } else {
          this.selectedObjects = []
          this.selectedObjects.push(intersects[0].object)
          this.outlineObj(this.selectedObjects)
          // if (intersects[0].object.name === 'cube1') {
          //   this.initTween(0, 850, 1000)
          //   // $('#canvas4').css('display', 'block')
          //
          // } else if (intersects[0].object.name === 'cube2') {
          //   this.initTween(1500, 500, -1000)
          //   // $('#canvas5').css('display', 'block')
          // } else if (intersects[0].object.name === 'cube3') {
          //   this.initTween(-1400, 400, 1000)
          //   // $('#canvas6').css('display', 'block')
          // }
        }
      },
      // 相机移动动画
      initTween(targetX, targetY, targetZ) {
        // 需要保留this
        var initPosition = {
          x: this.camera.position.x,
          y: this.camera.position.y,
          z: this.camera.position.z,
        }
        var tween = new TWEEN.Tween(initPosition)
          .to({x: targetX, y: targetY, z: targetZ}, 2000)
          .easing(TWEEN.Easing.Sinusoidal.InOut)
        var onUpdate = (pos) => {
          var x = pos.x
          var y = pos.y
          var z = pos.z
          this.camera.position.set(x, y, z)
        }
        tween.onUpdate(onUpdate)
        tween.start()
        this.controls.target.set(0, 0, 0)
        this.cssControls.target.set(0, 0, 0)
      },
      // 生成数据显示面板
      generateDataPanel(id, x, y, z) {
        // CSS3DObject实现3D卡片
        var dataplane = document.getElementById('canvas' + id)
        this.dataPanel['plane' + id] = new CSS3DObject(dataplane)
        this.dataPanel['plane' + id].scale.set(1.2, 1.2, 1.2)
        this.dataPanel['plane' + id].position.set(x, y, z)

        if (id == 1 || id == 2 || id == 3) {
          this.dataPanel['plane' + id].visible = false
        }
        this.cssScene.add(this.dataPanel['plane' + id])
      },
      // 生成管道
      generatePipe(id) {
        var curve
        var tubeGeometry
        var material
        if (id === 1) {
          curve = new THREE.CatmullRomCurve3([
            new THREE.Vector3(0, 200, 0),
            new THREE.Vector3(0, 200, -500),
            new THREE.Vector3(10, 200, -500),
            new THREE.Vector3(20, 200, -500),
            new THREE.Vector3(1000, 200, -500),
          ])
          tubeGeometry = new THREE.TubeGeometry(curve, 80, 10)
          this.flowingLineTexture1 = new THREE.TextureLoader().load(
            '/static/404.png'
          )
          this.flowingLineTexture1.wrapS = this.flowingLineTexture1.wrapT =
            THREE.RepeatWrapping
          this.flowingLineTexture1.repeat.set(10, 1)
          this.flowingLineTexture1.needsUpdate = true
          material = new THREE.MeshBasicMaterial({
            map: this.flowingLineTexture1,
            side: THREE.DoubleSide,
            transparent: true,
          })
        } else if (id === 2) {
          curve = new THREE.CatmullRomCurve3([
            new THREE.Vector3(0, 200, 0),
            new THREE.Vector3(0, 200, 500),
            new THREE.Vector3(-10, 200, 500),
            new THREE.Vector3(-500, 200, 500),
            new THREE.Vector3(-800, 200, 500),
          ])
          tubeGeometry = new THREE.TubeGeometry(curve, 80, 10)
          this.flowingLineTexture2 = new THREE.TextureLoader().load(
            '/static/404.png'
          )
          this.flowingLineTexture2.wrapS = this.flowingLineTexture2.wrapT =
            THREE.RepeatWrapping
          this.flowingLineTexture2.repeat.set(10, 1)
          this.flowingLineTexture2.needsUpdate = true
          material = new THREE.MeshBasicMaterial({
            map: this.flowingLineTexture2,
            side: THREE.DoubleSide,
            transparent: true,
          })
        }
        let tube = new THREE.Mesh(tubeGeometry, material)
        this.scene.add(tube)
      },
      // 视角改变
      toView(id) {
        if (id === 1) {
          this.initTween(0, 1000, 2900)
        } else if (id === 2) {
          this.initTween(0, 4000, 0)
        }
      },
      // 设备详情页面跳转
      deviceDetail(id) {
        for (let i = 1; i <= 3; i++) {
          if (id == i) {
            this.dataPanel['plane' + i].visible = true;
          } else {
            this.dataPanel['plane' + i].visible = false;
          }
        }

      },
      onWindowResize() {//
        this.cssRender.setSize(1676, 1032)
        this.renderer.setSize(1676, 1032)
        this.camera.aspect = 1676 / 1032
        this.camera.updateProjectionMatrix()
      },
      initControls() {
        //动态观察控件
        this.cssControls = new OrbitControls(
          this.camera,
          this.cssRender.domElement
        )
        // //动态阻尼系数 即鼠标拖拽旋转的灵敏度
        // this.cssControls.dampingFactor = 0.25
        // this.cssControls.target.set(0, 900, 0)
        // //摄像机距离原点的距离
        // this.cssControls.minDistance = 1
        // this.cssControls.maxDistance = 20000
        // //上下旋转范围
        this.cssControls.minPolarAngle = 0
        this.cssControls.maxPolarAngle = 1.5
        // //左右旋转范围
        // this.cssControls.minAzimuthAngle = -Math.PI * 2
        // this.cssControls.maxAzimuthAngle = Math.PI * 2
        //是否开启右键拖拽
        this.cssControls.enabledPan = true

        this.controls = new OrbitControls(this.camera, this.renderer.domElement)
        this.controls.dampingFactor = 0.25
        // // //缩放倍数
        this.controls.zoomSpeed = 1.0
        this.controls.minDistance = 1
        this.controls.maxDistance = 20000
        this.minPolarAngle = 0
        this.maxPolarAngle = 1.5
        this.minAzimuthAngle = -Math.PI * 2
        this.maxAzimuthAngle = Math.PI * 2
        this.controls.enabledPan = false
      },
      animate() {
        this.render()
        this.controls.update()
        this.cssControls.update()
        if (this.composer) {
          this.composer.render()
        }
        requestAnimationFrame(this.animate)
      },
      render() {
        // 数据显示面板的旋转角度与相机的旋转角度一致
        this.dataPanel.plane1.rotation.copy(this.camera.rotation)
        this.dataPanel.plane1.updateMatrix()
        this.dataPanel.plane2.rotation.copy(this.camera.rotation)
        this.dataPanel.plane2.updateMatrix()
        this.dataPanel.plane3.rotation.copy(this.camera.rotation)
        this.dataPanel.plane3.updateMatrix()
        this.dataPanel.plane4.rotation.copy(this.camera.rotation)
        this.dataPanel.plane4.updateMatrix()
        this.dataPanel.plane5.rotation.copy(this.camera.rotation)
        this.dataPanel.plane5.updateMatrix()
        this.dataPanel.plane6.rotation.copy(this.camera.rotation)
        this.dataPanel.plane6.updateMatrix()
        // 管道流动,更新管道纹理的偏移量
        // 管道流动速度
        var speed = 0.01
        this.flowingLineTexture1.offset.x += speed
        this.flowingLineTexture2.offset.x += speed
        // 一定要激活
        TWEEN.update()
        this.renderer.render(this.scene, this.camera)
        this.cssRender.render(this.cssScene, this.camera)
      },
      draw() {
        this.initScene()
        this.initCamera()
        this.initLight()
        this.initRender()
        this.initModel()
        this.initControls()
        this.animate()
        window.onresize = this.onWindowResize
        window.onclick = this.onMouseClick
        // window.addEventListener('pointermove', this.onPointerMove);
      },
// 获取鼠标坐标
      onPointerMove(event) {
        event.preventDefault();
        var vector = new THREE.Vector3(); //三维坐标对象
        vector.set(
          (event.clientX / window.innerWidth) * 2 - 1,
          -(event.clientY / window.innerHeight) * 2 + 1,
          0.5
        );
        vector.unproject(this.camera);
        var raycaster = new THREE.Raycaster(
          this.camera.position,
          vector.sub(this.camera.position).normalize()
        );
        var intersects = raycaster.intersectObjects(
          this.scene.children,
          true
        );
        if (intersects.length > 0) {
          var selected = intersects[0]; //取第一个物体
          console.log("x坐标:" + selected.point.x);
          console.log("y坐标:" + selected.point.y);
          console.log("z坐标:" + selected.point.z);
        }
      },


      handleButtonClick(index) {
        console.log(`Button ${index} clicked`);
      },

      /*init:function() {
        const camera = new Three.PerspectiveCamera( 75, this.$refs.container.clientWidth / this.$refs.container.clientHeight, 0.1, 1000 );
        camera.position.set( 0, 0, 5 );

        const raycaster = new Three.Raycaster();
        const mouse = new Three.Vector2();
        const zoomDistance = 1;

        // function onMouseMove( event ) {
        //   mouse.x = ( event.clientX / this.$refs.container.clientWidth ) * 2 - 1;
        //   mouse.y = - ( event.clientY / this.$refs.container.clientHeight ) * 2 + 1;
        //
        //   raycaster.setFromCamera( mouse, camera );
        //
        //   const plane = new Three.Plane( camera.getWorldDirection(), - zoomDistance );
        //   const intersection = new Three.Vector3();
        //   raycaster.ray.intersectPlane( plane, intersection );
        //   camera.position.copy( intersection );
        // }

        // document.addEventListener( 'mousemove', onMouseMove.bind(this), false );

        const renderer = new Three.WebGLRenderer();
        renderer.setSize( this.$refs.container.clientWidth, this.$refs.container.clientHeight );
        this.$refs.container.appendChild( renderer.domElement );

        const scene = new Three.Scene();

        const geometry = new Three.BoxGeometry();
        const material = new Three.MeshBasicMaterial( { color: 0x00ff00 } );
        const cube = new Three.Mesh( geometry, material );
        scene.add( cube );

        scene.add( camera );

          //添加正方体  参数分别是长  宽  高
          let geometry1 = new Three.BoxGeometry(60, 40, 40);
          //添加材质
          let material1 = new Three.MeshNormalMaterial();
          //将正方体以及材质放入网格中
          let mesh = new Three.Mesh(geometry1, material1);
          //在场景中添加网格
          scene.add(mesh);

          //点光源
          var point = new Three.PointLight(0x444444);
          point.position.set(100, 100, 100); //点光源位置
          scene.add(point); //点光源添加到场景中
          //环境光
          var ambient = new Three.AmbientLight(0x444444);
          scene.add(ambient);

          // 辅助坐标系  参数250表示坐标系大小，可以根据场景大小去设置
          var axisHelper = new Three.AxesHelper(550);
          scene.add(axisHelper);

        function animate() {
          requestAnimationFrame( animate );
          renderer.render( scene, camera );
        }

        animate();
      },*/
      /*init: function() {
        // 设置场景
        this.scene = new Three.Scene();

        //获取DOM容器
        let container = document.getElementById('container');
        //设置相机的角度  宽高比  近端面  远端面
        // this.camera = new Three.PerspectiveCamera(200, container.clientWidth / container.clientHeight, 0.1, 1000);
        var width = window.innerWidth; //窗口宽度
        var height = window.innerHeight; //窗口高度
        var k = width / height; //窗口宽高比
        var s = 50; //三维场景显示范围控制系数，系数越大，显示的范围越大
        //创建相机对象
        this.camera = new Three.OrthographicCamera(-s * k, s * k, s, -s, 1, 1000);
        //设置相机在三维坐标的位置
        this.camera.position.set(100, 100, 100)
        this.camera.lookAt(this.scene.position); //设置相机方向(指向的场景对象)
        //this.camera.position.z = 1;

        //添加正方体  参数分别是长  宽  高
        let geometry = new Three.BoxGeometry(60, 40, 40);
        //添加材质
        let material = new Three.MeshNormalMaterial();
        //将正方体以及材质放入网格中
        // this.mesh = new Three.Mesh(geometry, material);
        //在场景中添加网格
        // this.scene.add(this.mesh);

        var loader = new OBJLoader();
        // 没有材质文件，系统自动设置Phong网格材质

        let that = this;
        loader.load('/static/low_DEF.obj',function (obj) {
          // 控制台查看返回结构：包含一个网格模型Mesh的组Group
          console.log(obj);
          // 查看加载器生成的材质对象：MeshPhongMaterial
          console.log(obj.children[0].material);
          // 加载后的一些编辑操作
          obj.children[0].scale.set(5,5,5);//网格模型缩放
          obj.children[0].geometry.center();//网格模型的几何体居中


          // obj.children[0].material.color.set(0xff0000);//设置材质颜色
          that.scene.add(obj.translateX(40));
        })

        //开启反锯齿
        this.renderer = new Three.WebGLRenderer({
          antialias: true
        });

        //点光源
        var point = new Three.PointLight(0x444444);
        point.position.set(100, 100, 100); //点光源位置
        this.scene.add(point); //点光源添加到场景中
        //环境光
        var ambient = new Three.AmbientLight(0x444444);
        this.scene.add(ambient);

        // 辅助坐标系  参数250表示坐标系大小，可以根据场景大小去设置
        var axisHelper = new Three.AxesHelper(550);
        this.scene.add(axisHelper);

        //指定渲染器的高宽(和画布框的大小一致)
        this.renderer.setSize(container.clientWidth, container.clientHeight);

        this.renderer.setClearColor(0xb9d3ff, 1);
        //将指定的渲染器加入到Dom容器中
        container.appendChild(this.renderer.domElement);

        that.controls = new OrbitControls(this.camera,this.renderer.domElement);//创建控件对象
        that.controls.update();

        // controls.addEventListener('change', this.animate);//监听鼠标、键盘事件
      },

      //动画效果函数
      animate: function() {
        this.camera.aspect = window.innerWidth / window.innerHeight;
        this.camera.updateProjectionMatrix();
        //回调执行这个函数
        this.controls.update();
        //X轴,Y轴,Z轴旋转的弧度
        // this.mesh.rotation.x += 0.008;
        // this.mesh.rotation.y += 0.008;
        // this.mesh.rotation.z += 0.008;
        //渲染相机以及场景
        this.renderer.render(this.scene, this.camera);
        requestAnimationFrame(this.animate);
      },*/
    }
  };
</script>
<style scoped lang="scss">
  #container {
    font-size: 7vw;
    font-family: sans-serif;
    text-align: center;
    width: 100%;
    height: 100%;
    /*display: flex;*/
    justify-content: center;
    align-items: center;
  }

  #model {
    width: 100%;
    height: 100%;
    position: relative;
    overflow: hidden;

    #canvas2,
    #canvas3 {
      width: 150px;
      height: 200px;
      // background-color: rgba(8, 29, 54, 0.5);
      background-color: aquamarine;
      border-radius: 3px;
      // opacity: 0.5;
      color: red;
      font-size: 20px;
    }

    #canvas4, #canvas5, #canvas6 {
      span {
        display: block;
        width: 100px;
        height: 40px;
        background-color: cornflowerblue;
        color: #fff;
        position: absolute;
        text-align: center;
        line-height: 40px;
        cursor: pointer;
      }
    }

    #btns {
      position: absolute;
      bottom: 0px;
      width: 100%;
      padding: 10px;
      text-align: center;
      z-index: 9999;

      span {
        width: 100px;
        display: inline-block;
        height: 30px;
        background-color: red;
        margin-right: 20px;
        cursor: pointer;
      }
    }
    #btns1 {
      position: absolute;
      bottom: 0px;
      width: 100%;
      padding: 10px;
      text-align: center;
      z-index: 9999;

      span {
        width: 100px;
        display: inline-block;
        height: 30px;
        background-color: red;
        margin-right: 20px;
        cursor: pointer;
      }
    }

    .BoxWrap {
      width: 150px;
      height: 200px;
      position: relative;

    }

    .horn {
      background-color: white;
      position: absolute;
      width: 100%;
      height: 100%;
      border: 1px solid #00d3e7;
      margin: 10px;
    }

    .horn > div {
      width: 10px;
      height: 10px;
      position: absolute;
    }

    .horn .lt {
      border-top: 3px solid #00d3e7;
      border-left: 3px solid #00d3e7;
      left: -3px;
      top: -3px;
    }

    .horn .rt {
      border-top: 3px solid #00d3e7;
      border-right: 3px solid #00d3e7;
      right: -3px;
      top: -3px;
    }

    .horn .rb {
      border-bottom: 3px solid #00d3e7;
      border-right: 3px solid #00d3e7;
      right: -3px;
      bottom: -3px;
    }

    .horn .lb {
      border-bottom: 3px solid #00d3e7;
      border-left: 3px solid #00d3e7;
      left: -3px;
      bottom: -3px;
    }

  }
</style>
