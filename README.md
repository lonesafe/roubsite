# RoubSite
## 介绍
RoubSite是一个简单、轻量、易上手的java web开发框架，项目研发的初衷是写一个可以让刚入行的程序猿快速掌握java web开发的这么一个框架。  
RoubSiteAdmin是基于RoubSite开发的一套后台权限管理框架，用户、角色、权限、菜单一整套都写好了，结合代码生成机，简单增删改查，通过配置就可以搞定。

## 演示地址
[RoubSiteAdmin后台管理框架](https://test.roubsite.com)
## 软件架构
整个框架分为Action（前端控制器）、Dao（数据访问层）、Bean（实体类）这三层；  
页面模板实现了类似PHP中的smarty框架的一个模板引擎，使用起来十分方便。

## RoubSite都包含那些模块

1.  RoubSiteFramework RoubSite框架，Action、Dao的代码实现都在里面。
2.  RoubSiteSecurity 权限管理，在Action中使用注解方式对方法进行权限控制，自己写权限管理可以不用它。
3.  RoubSiteSmarty4j 模板解释器，参考了github上的代码，原作者实在是没找到。
4.  RoubSiteCodeSign 代码生成机，这可是一个重头戏，它可以直接将数据库的表生成对应的增删改查的代码，页面也同时生成，减少工作量的首选。
5.  RoubSiteAdmin 一个完整的后台管理框架，可以直接拿来用，权限啥的都写好了（`VIP免费获取`）

## RoubSite框架讨论QQ群1022159442
群里提供使用手册和教程哦

<img src="data:image/png;base64,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%0ASWWn4g6VqWJQmVSmikFlUhkqJpWVyjsVq4pB5b9SsapYqUwVd1wcxwMujuMB%0A9oM/ojJVDCpTxUrlUxU7KquKSWVVMalMFSuVVcWkckfFpDJU3KWyqrhLZVXx%0AFy6O4wH2g5tUVhU7KquKSeU3KnZUdioGlaliUhkqPqXyqYp3VIaKd1R2KlYq%0Aq4q/cHEcD7g4jgfYD25S+VTFSmWq+A2VqWJQuaviUypDxaSyqphU7qiYVHYq%0A7lIZKnZUpopvXRzHAy6O4wEvvlQxqOyo3KUyVLyjMlTsVPyGylQxqEwVq4qV%0AyjsVg8qnKiaVqWKnYlCZKgaVv3ZxHA+wH3xBZVXxKZWp4lMqq4qVylQxqawq%0AJpVVxUplqliprCp+Q2VVMalMFYPKquKvXRzHAy6O4wEvfqliR2VVMVVMKkPF%0AjspUMakMKndVDCp3qQwV76gMFXepDBWTylTxqYpVxY7KVPGti+N4wMVxPMB+%0A8AWVnYp/UXmnYqWyU7FSWVVMKlPFSmWqGFRWFTsqU8VK5a6KO1T+WsWnLo7j%0AAfaDm1R2KnZU7qjYUdmpWKn8VyoGlU9VTCqrikllqhhUfqPiL1wcxwMujuMB%0A9oP/IZWpYlCZKiaVVcWkMlRMKkPFpDJVDCrvVPyLyjsVK5Wp4lMqn6rYUVlV%0A/IWL43jAxXE84MUHVP5CxVQxqaxUVhXvVKwqdlSGikllpTJVrCp2Ku5QmSp2%0AKlYq76gMFTsqq4pPXRzHA158qeJTKjsVK5WpYlDZqVip/EbFb6hMFSuVT6nc%0AVfEvFZPKVPGti+N4wMVxPODFL6nsVOyo3KWyqphUVipDxTsqOyp3qEwVg8pU%0AsVJZVexU7KhMKneovKMyVHzq4jge8OJ/rGKlslPxTsW/qLxTsVPxLypTxY7K%0AVDFUTCqfUpkqhopJZaoYVHYqJpVBZaq44+I4HnBxHA948f+Iyk7FjsqqYlCZ%0AKiaVVcWkMlRMKkPFOyo7KkPFVLFTMajcVbFTsVPxrYvjeMDFcTzgxS9V/IbK%0AqmJSeVLFTsWq4o6KSWWlslMxqeyoDBWTylSxUhkqJpVVxacujuMBL76k8hcq%0AJpUdlTsqJpWVyqpiR2VVMalMFYPKOxWDyqpiUllVvKMyqOxUfEplqrjj4jge%0AcHEcD7AfHMcfuziOB1wcxwMujuMBF8fxgIvjeMDFcTzg4jgecHEcD7g4jgf8%0AH4VLhmWp+ivMAAAAAElFTkSuQmCC">

