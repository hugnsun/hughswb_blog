<template>
    <v-dialog
        v-model="registerFlag"
        transition="dialog-top-transition"
        :fullscreen="isMobile"
        max-width="500"
    >
        <v-card class="login-container" style="border-radius: 4px">
            <v-icon class="float-right" @click="registerFlag = false">
                mdi-close
            </v-icon>
            <div class="login-wrapper">
                <!-- 用户名 -->
                <v-text-field
                    v-model="username"
                    :rules="nameRules"
                    label="用户名"
                    placeholder="请输入您的用户名"
                    clearable
                    required
                    @keyup.enter="register"
                />
                <!-- 邮箱 -->
                <v-text-field
                    v-model="email"
                    label="邮箱号"
                    placeholder="请输入您的邮箱号"
                    clearable
                    required
                    :rules="emailRules"
                    @keyup.enter="register"
                />
                <!-- 验证码 -->
                <div class="mt-7 send-wrapper">
                    <v-text-field
                        maxlength="6"
                        v-model="code"
                        label="验证码"
                        placeholder="请输入6位验证码"
                        required
                        :rules="codeRules"
                        @keyup.enter="register"
                    />
                    <el-button type="text" :disabled="flag" @click="sendCode">{{
                        codeMsg
                    }}</el-button>
                </div>
                <!-- 密码 -->
                <v-text-field
                    v-model="password"
                    class="mt-7"
                    label="密码"
                    placeholder="请输入您的密码"
                    @keyup.enter="register"
                    :append-icon="show ? 'mdi-eye' : 'mdi-eye-off'"
                    :type="show ? 'text' : 'password'"
                    required
                    :rules="passwordRules"
                    @click:append="show = !show"
                />
                <v-text-field
                    v-model="confirmPassword"
                    class="mt-7"
                    label="确认密码"
                    placeholder="请再次输入密码"
                    @keyup.enter="register"
                    :append-icon="show ? 'mdi-eye' : 'mdi-eye-off'"
                    :type="show ? 'text' : 'password'"
                    required
                    :rules="confirmPasswordRules"
                    @click:append="show = !show"
                />
                <!-- 注册按钮 -->
                <v-btn
                    class="mt-7"
                    block
                    color="red"
                    style="color: #fff"
                    @click="register"
                >
                    注册
                </v-btn>
                <!-- 登录 -->
                <div class="mt-10 login-tip">
                    已有账号？<span @click="openLogin">登录</span>
                </div>
            </div>
        </v-card>
    </v-dialog>
</template>

<script>
export default {
    data: function () {
        return {
            username: "",
            confirmPassword: "",
            email: "",
            code: "",
            password: "",
            flag: true,
            codeMsg: "发送",
            time: 60,
            show: false,
            // 验证
            nameRules: [(v) => !!v || "请输入名称"],
            emailRules: [
                (v) => !!v || "请输入邮箱",
                (v) => /.+@.+\..+/.test(v) || "请注意邮箱规范",
            ],
            codeRules: [
                (v) => !!v || "请输入验证码",
                (v) => v.trim().length == 6 || "验证码为六位 请重新输入",
            ],
            passwordRules: [
                (v) => !!v || "请输入密码",
                (v) => v.trim().length == 8 || "密码需要不少于八位 请重新输入",
            ],
            confirmPasswordRules: [
                (v) => !!v || "请输入密码",
                (v) => v.trim().length == 8 || "密码需要不少于八位 请重新输入",
                (v) => v == this.password || "两次密码不同  请重新输入",
            ],
        };
    },
    methods: {
        openLogin() {
            this.$store.state.registerFlag = false;
            this.$store.state.loginFlag = true;
        },
        sendCode() {
            //发送邮件
            this.countDown();
            this.axios
                .get("/api/users/code", {
                    params: { email: this.email },
                })
                .then(({ data }) => {
                    if (data.flag) {
                        this.$toast({
                            type: "success",
                            message: "发送成功",
                        });
                    } else {
                        this.$toast({
                            type: "error",
                            message: data.message,
                        });
                    }
                });
        },
        countDown() {
            this.flag = true;
            this.timer = setInterval(() => {
                this.time--;
                this.codeMsg = this.time + "s";
                if (this.time <= 0) {
                    clearInterval(this.timer);
                    this.codeMsg = "发送";
                    this.time = 60;
                    this.flag = false;
                }
            }, 1000);
        },
        register() {
            const user = {
                username: this.username,
                email: this.email,
                password: this.password,
                code: this.code,
            };
            this.axios.post("/api/register", user).then(({ data }) => {
                if (data.flag) {
                    let param = new URLSearchParams();
                    param.append("username", user.username);
                    param.append("password", user.password);
                    this.axios.post("/api/login", param).then(({ data }) => {
                        this.username = "";
                        this.password = "";
                        this.$store.commit("login", data.data);
                        this.$store.commit("closeModel");
                    });
                    this.$toast({ type: "success", message: "登录成功" });
                } else {
                    this.$toast({ type: "error", message: data.message });
                }
            });
        },
    },
    computed: {
        registerFlag: {
            set(value) {
                this.$store.state.registerFlag = value;
            },
            get() {
                return this.$store.state.registerFlag;
            },
        },
        isMobile() {
            const clientWidth = document.documentElement.clientWidth;
            if (clientWidth > 960) {
                return false;
            }
            return true;
        },
    },
    watch: {
        email(value) {
            var reg =
                /^[A-Za-z0-9\u4e00-\u9fa5]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/;
            if (reg.test(value)) {
                this.flag = false;
            } else {
                this.flag = true;
            }
        },
    },
};
</script>
<style scoped>
.login-container {
    min-height: 650px;
}
</style>