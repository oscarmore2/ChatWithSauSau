# 融云

![Mou icon](http://www.rongcloud.cn/images/logo_1.png)


##融云 Demo 2.0 运行方式

###Eclipse 环境

#####1. 下载：
  下载融云 demo 2.0 到自己电脑。
#####2. 导入项目：
 操作步骤：打开 Eclipse 选择 file—>import—>General—>Existing Projects into WorkSpace
     选择融云 demo 目录，勾选 RongDemo、RongIMKit、android-support-v7-appcompat 项目，然后点finish按钮完成项目导入。
#####3. 设置Java Complier:
设置 JAVA 编译版本 jdk 1.7 以上。<br/>
操作步骤：在 Eclipse 中分别右击  RongDemo、RongIMKit、android-support-v7-appcompat 项目 在菜单中选择 Properties->java Compiler->compiler complinace level 选择 1.7 以上 JDK 版本。
 
#####4. 完成导入 build apk。
<font color="#0069d6">注：如果 clean 项目后发现还是不能正常运行，找到 Eclipse 下的 Problems 标签删除红色提示后方就正常运行。</font>


###Android studio 环境
#####1. 下载
 下载融云 demo 2.0 到自己电脑。
#####2. 导入项目
打开 Android studio 选择 open an existing Android Studio project 导入项目。
#####3. 删除引用
为了更方便的 Eclipse 开发者我们引用了 appcompat-v7 包，Android studio 开发者需要做两步操作：<BR/>
 &nbsp;&nbsp;&nbsp;1. 删除 settings.gradle 中的 " appcompat-v7 "。 <BR/>
 &nbsp;&nbsp;&nbsp;2. 删除 appcompat-v7 Module 。
#####4. 完成导入 build apk。

<BR/><BR/><BR/>
##融云 2.0升线文档

###1、删除项目中引入的 1.0 的 SDK
 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;需要删除的文件有：jar 包、so 文件、res 下以 rc 开头的 xml、图片、assets、以及 AndroidManifest 里面的配置文件。

###2、添加融云 2.0 SDK
  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;2.0 SDK 需要以项目依赖的形式引入，下面分别对 Android Studio 和 Eclipse 引用方式进行说明。

####2.1、使用 Android Stadio 添加项目依赖
 1. 新建 New Module...  命名 kit。
 2. 复制 “ 融云 SDK 2.0 ” 到 kit 下。
 3. 在你的主项目的 build.gradle 中的 dependencies 添加 compile project(':kit')。
 4. 复制 “ 融云 SDK 2.0 ” AndroidManifest.xml 中的配置文件到你主项目的 AndroidManifest.xml 中去。

####2.2 使用 Eclipse 添加项目依赖

1. 将  “ 融云 SDK 2.0 ” 导入到工程中，设置为 依赖项目。
2. 在你的主项目的 Library 下，添加  “ 融云 SDK 2.0 ” 为依赖项目。
3. 复制 “ 融云 SDK 2.0 ” AndroidManifest.xml 中的配置文件到你主项目的 AndroidManifest.xml 中去。


###3、修改调用方法
1. 修改引用： “ 融云 SDK 2.0 ” 主要修改了一些回调方法，导入 “ 融云 SDK 2.0 ” 后发现报错，去修复相应的回调方法既可。
2. “ 融云 SDK 2.0 ” 去掉了会话列表和会话页面的 Activity 依赖，Activity 需要开发者自己来写。详细见 “ 融云 SDK 2.0 ”  集成文档地址：http://docs.rongcloud.cn/android.html


<BR/><BR/>
#### 联系我们
商务合作
Email：<bd@rongcloud.cn>

新浪微博 [@融云RongCloud](http://weibo.com/rongcloud)

客服 QQ 2948214065

公众帐号
融云RongCloud RongCloud 公众账号二维码

![Smaller icon](http://www.rongcloud.cn/images/code1.png "RongCloud")
