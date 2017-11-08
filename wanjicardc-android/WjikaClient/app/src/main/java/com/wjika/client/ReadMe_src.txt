/**
 * Created by songxudong
 * on 2015/6/24.
 * src说明
 */
 1.项目各目录
 analysis 行为分析

 base 基础组件
   ui —— BaseActivity 基本activity 无标题；ToolBarActivity有标题；BaseTabsDrawerActivity 有tabbar；WebViewActivity Html加载页；BaseFragment fragment基类；

 network
   entities —— Entity，为所有实体类的父类；根据模块存放网络数据的实体类；
   Constants类 —— 所有网络请求Url；
   parser —— 解析类；

 launcher 开机启动

 person 个人中心

 login 登陆

 card  卡包模块

 2.Activity
 BaseActivity和ToolBarActivity中选择一个继承，如果有标题使用ToolBarActivity
 2.Fragment
 继承BaseFragment
 3.Entity
 继承BaseEntity
 4.项目目录以模块命名，目录下以组件命令
 结构为
 -模块
  --ui   存放fragment activity
  --adapter 存放Adapter
  --widget 自定义组件
  --broadcast 广播
  --service  服务
  --util  模块独享工具类
  --dialog
  --inter 接口

