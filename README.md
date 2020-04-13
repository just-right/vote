# 一、简易投票介绍

&emsp;&emsp;基于Spring Boot+MyBatis+Redis+MySQL实现简易投票功能.

- 使用EasyCode插件快速生成通用代码
- 设置投票开始日期 & 结束日期
- 添加投票次数限制
- 添加投票时间间隔限制
- 使用ProtostuffIOUtil序列化对象
- 使用Jmeter工具测试秒杀

# 二、前期准备

## 2.1 MySQL数据库准备:

- 对应的SQL见 /src/main/resources/sql/CreateDataBase.sql
- 创建数据库mybatis
- 建表 - vote_activity(投票活动)/vote_option(投票选项)
- MySQL配置信息见 /src/main/resources/application.properties

## 2.2 Redis数据库准备:

- Redis配置信息见 /src/main/resources/application.properties

## 2.3 PostMan测试工具准备:

- [下载地址](https://www.postman.com/)


## 2.4 Jmeter测试工具准备:

- [下载地址](http://jmeter.apache.org/download_jmeter.cgi)

# 三、测试步骤

## 3.1 创建投票活动 

&emsp;&emsp;使用PostMan发出创建投票活动请求:

> http://127.0.0.1:8081/voteActivity/createVote

&emsp;&emsp;请求体为:

```
{
	"id":"",
	"name":"画画比赛-投票活动",
	"begindatetime":"2020-04-13 21:26:20",
	"enddatetime":"2020-04-14 23:59:20",
	"optionList":[
		{
			"id":"",
			"aid":"",
			"serialnumber": 1,
			"name": "张三",
			"score":"0",
		},
		{
			"id":"",
			"aid":"",
			"serialnumber": 2,
			"name": "李四",
			"score":"0",
		},
		{
			"id":"",
			"aid":"",
			"serialnumber": 3,
			"name": "王五",
			"score":"0",
		},
		{
			"id":"",
			"aid":"",
			"serialnumber": 4,
			"name": "赵六",
			"score":"0",
		},
	]
}
```

```
//返回投票活动信息
{
    "msg": "投票活动创建成功",
    "data": {
        "begindatetime": "2020-04-13 21:26:20",
        "enddatetime": "2020-04-14 23:59:20",
        "id": 0,
        "name": "画画比赛-投票活动",
        "optionList": [
            {
                "aid": 39,
                "id": 81,
                "name": "张三",
                "score": 0,
                "serialnumber": 1
            },
            {
                "aid": 39,
                "id": 82,
                "name": "李四",
                "score": 0,
                "serialnumber": 2
            },
            {
                "aid": 39,
                "id": 83,
                "name": "王五",
                "score": 0,
                "serialnumber": 3
            },
            {
                "aid": 39,
                "id": 84,
                "name": "赵六",
                "score": 0,
                "serialnumber": 4
            }
        ]
    }
}
```
## 3.2 访问投票界面 

&emsp;&emsp;查看数据库中由3.1新增的投票活动ID,使用PostMan发出请求:

> http://127.0.0.1:8081/voteActivity/showVoteDetail/39

```
//返回投票活动信息
{
    "msg": "投票活动进行中",
    "data": {
        "begindatetime": "2020-04-13 21:26:20",
        "enddatetime": "2020-04-14 23:59:20",
        "id": 0,
        "name": "画画比赛-投票活动",
        "optionList": [
            {
                "aid": 39,
                "id": 81,
                "name": "张三",
                "score": 0,
                "serialnumber": 1
            },
            {
                "aid": 39,
                "id": 82,
                "name": "李四",
                "score": 0,
                "serialnumber": 2
            },
            {
                "aid": 39,
                "id": 83,
                "name": "王五",
                "score": 0,
                "serialnumber": 3
            },
            {
                "aid": 39,
                "id": 84,
                "name": "赵六",
                "score": 0,
                "serialnumber": 4
            }
        ]
    }
}
```

## 3.3 开始投票

&emsp;&emsp;使用Jmeter测试工具进行测试。


## 3.4 查看结果


