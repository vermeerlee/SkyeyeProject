
##登录管理

###账户组成
- username:仅用于登录验证
- username_head:用于head模式登录
- username_uuid:用于eye模式登录,uuid为设备标识
 
###添加账户
- 添加username账户
- 添加username_head账户
 
###用户登录
[注释]:下面的流程图在这预览不出来，csdn上正常
```flow
st=>start: 登录开始
op_username=>operation: 用户输入username登录
cond_username=>condition: 登录成功?
cond_head=>condition: 用户选head
cond_eye=>condition: 用户选eye
op_head=>operation: 使用username_head登录
op_eye=>operation: 使用username_uuid登录
e_success=>end: 登录成功
e_fail=>end: 登录失败

st->op_username->cond_username
cond_username(yes)->cond_head
cond_username(no)->e_fail
cond_head(yes)->op_head->e_success
cond_head(no)->cond_eye
cond_eye(yes)->op_eye->e_success
cond_eye(no)->e_fail
```