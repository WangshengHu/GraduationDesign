# apiSearch
待存储的类：apiSearch.intermediate.JSON，或直接在抽象基类apiSearch.intermediate.InterRep中实现对应的read和write方法也可

-------------
git使用：
1.git clone https://github.com/iewrer/apiSearch.git 克隆代码库
2.git checkout -b PuChun 创建并切换到新分支PuChun
3.修改代码...
4.git add . 追踪所有变更
  或者git add filename 追踪某个文件的变更
5.git commit -a -m "message of the commit" 创建commit
6.git push origin PuChun 推送到GitHub的对应远程分支

-------------
项目相关介绍：


我现在在写工具框架的时候是这样考虑的，把工具划分为若干个组件，包括：
	1.中间形式组件
	2.Parser组件
	3.API搜索算法
	4.输出格式组件
	5.工具其他组件，包括Main函数，输入和策略选择
现在考虑的方法是在1-4等四个组件重分别定义了相关的抽象基类，可以根据不同的需要再派生子类进行具体实现，采用这些子类提供的方法组合成一套针对某种语言项目的API搜索方法。
以Java为例，可以通过配置输入参数分别选择其组件为：
	1.中间形式：JSON
	2.Parser：JDT Core
	3.API搜索算法：JDT AST搜索算法
	4.输出格式：简单输出
相应API搜索策略：根据输入参数配置1-4组件，查找所有相应语言的项目，每次不断循环读取某个项目待分析的代码文件，然后每次采用Parser对单个源文件进行parse，然后使用搜索算法在编译得到的AST上进行搜索，将找到的API信息存储在中间形式对象中，最后以某种格式将API信息输出，同时输出为某种中间形式的文件进行持久化存储。


