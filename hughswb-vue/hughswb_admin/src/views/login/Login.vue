<template>
    <div
        ref="boxRef"
        style="
            boxsizing: border-box;
            width: 100%;
            height: 100vh;
            position: relative;
        "
    >
        <canvas ref="canvasRef" style="z-index: 1; position: absolute"></canvas>
        <el-card class="login-card" v-if="cardType == 1">
            <div class="login-title">
                <p data-text="变成派大星后台管理">变成派大星后台管理</p>
            </div>
            <!-- 登录表单 -->
            <el-form
                status-icon
                :model="loginForm"
                :rules="rules"
                ref="ruleForm"
                class="login-form"
            >
                <!-- 用户名输入框 -->
                <el-form-item prop="username">
                    <el-input
                        v-model="loginForm.username"
                        prefix-icon="el-icon-aliyonghu"
                        placeholder="用户名"
                        @keyup.enter.native="login"
                    />
                </el-form-item>
                <!-- 密码输入框 -->
                <el-form-item prop="password">
                    <el-input
                        v-model="loginForm.password"
                        prefix-icon="el-icon-alimima2"
                        show-password
                        placeholder="密码"
                        @keyup.enter.native="login"
                    />
                </el-form-item>
                <!-- 图形验证码 -->
                <el-form-item prop="verifycode">
                    <div style="display: flex">
                        <el-input
                            v-model="loginForm.verifycode"
                            placeholder="请输入验证码"
                            @keyup.enter.native="login('loginForm')"
                        ></el-input>
                        <span @click="refreshCode"
                            ><s-identify
                                :identifyCode="identifyCode"
                            ></s-identify
                        ></span>
                    </div>
                </el-form-item>
            </el-form>
            <!-- 登录按钮 -->
            <el-footer class="footer-container">
                <el-button
                    class="login-button"
                    type="success"
                    round
                    @click="login"
                    >登录账号</el-button
                >
                <el-button
                    class="register-button"
                    type="info"
                    @click="cardType = 2"
                    round
                    >注册账号</el-button
                >
            </el-footer>
        </el-card>
        <!-- 注册界面-->
        <el-card class="login-card" v-if="cardType == 2">
            <div class="login-title">
                <p data-text="变成派大星后台管理">变成派大星后台管理</p>
            </div>
            <!-- 注册表单 -->
            <el-form
                status-icon
                :model="register"
                :rules="rules"
                ref="ruleForm"
                class="login-form"
            >
                <!-- 用户名输入框 -->
                <el-form-item prop="username">
                    <el-input
                        v-model="register.username"
                        prefix-icon="el-icon-aliyonghu"
                        placeholder="用户名"
                        @keyup.enter.native="login"
                    />
                </el-form-item>
                <!-- 邮箱 -->
                <el-form-item prop="email">
                    <el-input
                        v-model="register.email"
                        prefix-icon="el-icon-alialimailaliyouxiang"
                        placeholder="邮箱"
                        @keyup.enter.native="email"
                    />
                </el-form-item>
                <!-- 验证码 -->
                <el-form-item prop="验证码" class="form-item-flex">
                    <el-input
                        style="width: 75%"
                        v-model="register.code"
                        prefix-icon="el-icon-aliyanzhengma"
                        placeholder="验证码"
                        :controls="false"
                    />

                    <el-button
                        @click="sendCode"
                        style="width: 25%; border-radius: 15px; margin-top: 0"
                        type="success"
                        >{{ emailSendText }}</el-button
                    >
                </el-form-item>

                <!-- 密码输入框 -->
                <el-form-item prop="password">
                    <el-input
                        v-model="register.password"
                        prefix-icon="el-icon-alimima2"
                        show-password
                        placeholder="密码"
                        @keyup.enter.native="login"
                    />
                </el-form-item>
                <!-- 确认密码 -->
                <el-form-item prop="confirmPassword">
                    <el-input
                        v-model="register.confirmPassword"
                        prefix-icon="el-icon-alimima"
                        show-password
                        placeholder="确认密码"
                        @keyup.enter.native="login"
                    />
                </el-form-item>
            </el-form>

            <el-footer class="footer-container">
                <el-button
                    class="register-button"
                    type="success"
                    round
                    @click="registerUser"
                    >注册账号</el-button
                >
                <el-button
                    class="login-button"
                    type="info"
                    round
                    @click="backLogin"
                    >返回登录</el-button
                >
            </el-footer>
        </el-card>
    </div>
</template>


<script>
import { generaMenu } from "../../assets/js/menu";
import WaterRipple from "../water/waterRipple";
import waterBg from "../../images/water.png";
import SIdentify from "../../components/identify.vue";
let canvasWidth = 600;
let canvasHeight = 600;
export default {
    components: { SIdentify },
    data: function () {
        // 验证码自定义验证规则
        const validateVerifycode = (rule, value, callback) => {
            const newVal = value.toLowerCase();
            const identifyStr = this.identifyCode.toLowerCase();
            if (newVal === "") {
                callback(new Error("请输入验证码"));
            } else if (newVal !== identifyStr) {
                console.log("validateVerifycode:", value);
                callback(new Error("验证码不正确!"));
            } else {
                callback();
            }
        };
        return {
            identifyCodes: "3456789ABCDEFGHGKMNPQRSTUVWXY",
            identifyCode: "",
            loginForm: {
                username: "",
                password: "",
                verifycode: "",
            },
            register: {
                username: "",
                email: "",
                code: "",
                confirmPassword: "",
                password: "",
            },
            waterRipple: null,
            timer: null,
            sendTime: 60,
            cardType: 1,
            emailSendText: "发送",
            rules: {
                username: [
                    {
                        required: true,
                        message: "用户名不能为空",
                        trigger: "blur",
                    },
                ],
                password: [
                    {
                        required: true,
                        message: "密码不能为空",
                        trigger: "blur",
                    },
                ],
                verifycode: [
                    {
                        required: true,
                        trigger: "blur",
                        validator: validateVerifycode,
                    },
                ],
            },
        };
    },

    mounted() {
        if (this.$refs.boxRef && this.$refs.canvasRef) {
            const { offsetWidth, offsetHeight } = this.$refs.boxRef;
            canvasWidth = offsetWidth;
            canvasHeight = offsetHeight;
            this.$refs.canvasRef.width = canvasWidth;
            this.$refs.canvasRef.height = canvasHeight;

            const waterImg = new Image();
            waterImg.src = waterBg;
            this.waterRipple = new WaterRipple({
                canvas: this.$refs.canvasRef,
                background: waterImg,
                boxRef: this.$refs.boxRef,
            });

            this.$refs.boxRef.style.backgroundImage = `url(${this.$refs.canvasRef.toDataURL()})`;
            this.waterRipple.animate();

            this.timer = setInterval(() => {
                const x = Math.floor(canvasWidth * Math.random());
                const y = Math.floor(canvasHeight * Math.random());
                this.waterRipple.ripple(x, y);
            }, 1000);

            this.waterRipple.addMousemove();
        }

        // 验证码初始化
        this.identifyCode = "";
        this.makeCode(this.identifyCodes, 4);
    },
    beforeDestroy() {
        clearInterval(this.timer);
        this.waterRipple.stop();
    },
    methods: {
        backLogin() {
            this.cardType = 1;
        },
        sendCode() {
            //发送邮件
            this.countDown();
            this.axios
                .get("/api/users/code", {
                    params: { email: this.register.email },
                })
                .then(({ data }) => {
                    if (data.flag) {
                        this.$notify({
                            type: "success",
                            message: "发送成功",
                        });
                    } else {
                        this.$notify.error({
                            title: "发送验证码出现异常",
                            message: data.message,
                        });
                    }
                });
        },
        countDown() {
            this.flag = true;
            this.timer = setInterval(() => {
                this.sendTime--;
                this.emailSendText = this.sendTime + "s";
                if (this.sendTime <= 0) {
                    clearInterval(this.timer);
                    this.emailSendText = "发送";
                    this.sendTime = 60;
                    this.flag = false;
                }
            }, 1000);
        },
        login() {
            const that = this;
            //发送登录请求
            let param = new URLSearchParams();
            param.append("username", that.loginForm.username);
            param.append("password", that.loginForm.password);
            that.axios.post("/api/login", param).then(({ data }) => {
                if (data.flag) {
                    // 登录后保存用户信息
                    that.$store.commit("login", data.data);
                    localStorage.setItem("token", data.data.token);
                    // 加载用户菜单
                    generaMenu();
                    that.$message.success("登录成功");
                    that.$router.push({ path: "/" });
                } else {
                    that.$message.error(data.message);
                }
            });
        },
        // 生成随机数
        randomNum(min, max) {
            return Math.floor(Math.random() * (max - min) + min);
        },
        // 切换验证码
        refreshCode() {
            this.identifyCode = "";
            this.makeCode(this.identifyCodes, 4);
        },
        // 生成四位随机验证码
        makeCode(o, l) {
            for (let i = 0; i < l; i++) {
                this.identifyCode +=
                    this.identifyCodes[
                        this.randomNum(0, this.identifyCodes.length)
                    ];
            }
        },
        registerUser() {
            this.axios.post("/api/register", this.register).then(({ data }) => {
                if (data.flag) {
                    let param = new URLSearchParams();
                    param.append("username", this.register.username);
                    param.append("password", this.register.password);
                    this.axios.post("/api/login", param).then(({ data }) => {
                        this.username = "";
                        this.password = "";
                        this.$store.commit("login", data.data);
                        this.$store.commit("closeModel");
                    });
                    this.$notify({
                        type: "success",
                        message:
                            "用户：" + this.register.username + "  登录成功",
                    });
                    this.cardType = 1;
                    this.$router.push({ path: "/" });
                } else {
                    this.$notify.error({
                        title: "登录出现错误",
                        message: data.message,
                    });
                }
            });
        },
    },
};
</script>

<style scoped>
.login-card {
    position: absolute;
    top: 50%;
    left: 50%;
    width: 15%;
    padding: 1% 2% 2% 2%;
    border-radius: 15px;
    background-color: transparent;
    box-shadow: 0px 0px 10px rgba(0, 0, 0, 0.3);
    background: linear-gradient(to bottom, #67c23a, rgba(255, 255, 255, 0.6));
    transform: translate(-50%, -50%);
    z-index: 1;
}
.form-item-flex {
    display: flex;
    align-items: center;
}

.el-input /deep/ .el-input__inner {
    border-radius: 15px;
}
.footer-container {
    display: flex;
    justify-content: center;
    align-items: center;
}

.login-button {
    margin-right: 8px;
}
.el-card ::v-deep .el-card__body {
    padding: 0;
}
.register-button {
    margin-left: 8px;
}

.button-spacer {
    width: 8px;
}
.login-form {
    margin-top: 1.2rem;
    padding-top: 5%;
}
.login-card button {
    margin-top: 1rem;
    width: 100%;
}
.el-input__inner {
    border-radius: 15px;
}
</style>

<style lang = "scss" scoped>
.login-title {
    display: flex;
    justify-content: center;
    align-items: center;
    p {
        font-family: "阿里妈妈";
        color: #222;
        position: relative;
        &::before {
            content: attr(data-text);
            position: absolute;
            color: #fff;
            overflow: hidden;
            white-space: nowrap;
            border-right: 1px solid #fff;
            animation: move 6s linear infinite;
            filter: drop-shadow(0 0 20px #fff) drop-shadow(0 0 50px #fff);
        }

        @keyframes move {
            0%,
            10%,
            100% {
                width: 0;
            }
            70%,
            90% {
                width: 100%;
            }
        }
    }
}
</style>