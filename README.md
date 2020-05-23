- 项目名称：File Search文件检索
- 项目描述：对指定的本地文件目录进行实时文件检索
- 涉及技术：多线程、汉语拼音工具类、项目的maven配置、JDBC编程
- 项目流程：
1.	初始化数据库，建表并启动监听事件，选择本地目录后就进行搜索文件
2.	启动线程池，使用多线程的方式对文件进行递归式遍历搜索，并等待结束
3.	可以再次选择文件目录，中断当前线程池中的所有线程，重新启动任务
4.	再次选择文件目录后，比对数据库存储信息和本地文件的异同，进行相应的操作
5.	将数据库中更新后的数据同步到客户端的表格中
6.	支持客户端的页面上，输入汉语拼音或者首字母来检索文件
