-------------
项目相关介绍：

现在在写工具框架的时候是这样考虑的，把工具划分为若干个组件，包括：
	1.中间形式组件
	2.Parser组件
	3.API搜索算法
	4.输出格式组件
	5.工具其他组件，包括Main函数，输入和策略选择
现在考虑的方法是在1-4等四个组件重分别定义了相关的抽象基类，可以根据不同的需要再派生子类进行具体实现，采用这些子类提供的方法组合成一套针对某种语言项目的API搜索方法。
以Java为例，可以通过配置输入参数分别选择其组件为：
	1.中间形式：默认
	2.Parser：JDT Core
	3.API搜索算法：JDT AST搜索算法
	4.输出格式：简单输出
相应API搜索策略：根据输入参数配置1-4组件，查找所有相应语言的项目，每次不断循环读取某个项目待分析的代码文件，然后每次采用Parser对单个源文件进行parse，然后使用搜索算法在编译得到的AST上进行搜索，将找到的所有API信息存储在中间形式对象中，最后以某种格式将API信息输出，同时输出为某种中间形式的文件进行持久化存储。

-------------
项目使用：

现在已将项目打包为jar包。
使用形式形如"java -jar apiSearch_fat.jar [...]"

参数列表：
-language [java|...] //现在仅支持java
-api [api name] //输入待搜索的api名字
-codePath [codehouse path] //代码仓库位置
-savePath [result path] //结果保存位置
-search [jdt|...] //目前仅支持jdt编译结果的搜索
-parser [jdt|...] //目前仅支持jdt进行Parse
-output [simple|...] //目前仅支持简单输出模式
-intermediate [] //目前仅支持默认的中间保存形式
[-debug] //是否输出调试信息
[-deleteResult] //是否删除上次的结果并重新创建搜索

使用案例：
1.运行命令：java -jar apiSearch_fat.jar -language java -api java.util.Collection.size -codePath ./codehouse -savePath ./result -search jdt -parser jdt -output simple -intermediate

此时会初始化搜索并将所有api的信息保存于savePath，并输出指定api的搜索结果

2.若再次运行命令：java -jar apiSearch_fat.jar -language java -api java.util.Collection.size -codePath ./codehouse -savePath ./result -search jdt -parser jdt -output simple -intermediate

此时会直接从savePath读取上次输出的全部api调用信息，并过滤出指定api的调用信息进行输出


updated version by v-wahu@microsoft.com

arguments;

-generateApi [generate Api mode on]
-auto [automode on]
-output [simple/format]
-codePath [project dir that will be parsed]
-savePath [dir that will be used to save result]
-libPath [dir that will be searched for APIs]
-apiPath [dir that will be used to save apis]
-apiFile [file that will be read for apis]
-logFile [file that will be used for logging]
-api [single api]
-debug [debug on]


-generateApi -libPath D:\JavaAPI -apiPath .\result\apis -codePath ./codehouse -savePath ./result/data -output format

-auto -apiFile .\result\apis\all.txt -codePath ./codehouse -savePath ./result/data -output format
