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
        <div class="login-card">
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
                        prefix-icon="el-icon-alimima"
                        show-password
                        placeholder="密码"
                        @keyup.enter.native="login"
                    />
                </el-form-item>
            </el-form>
            <!-- 登录按钮 -->
            <el-button class="shape" type="primary" @click="login"
                >登录</el-button
            >
        </div>
    </div>
</template>


<script>
import { generaMenu } from "../../assets/js/menu";
import WaterRipple from "../water/waterRipple";
import waterBg from "../../images/water.png";

let canvasWidth = 600;
let canvasHeight = 600;
export default {
    data: function () {
        return {
            loginForm: {
                username: "",
                password: "",
            },
            waterRipple: null,
            timer: null,
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
    },
    beforeDestroy() {
        clearInterval(this.timer);
        this.waterRipple.stop();
    },
    methods: {
        login() {
            this.$refs.ruleForm.validate((valid) => {
                if (valid) {
                    const that = this;
                    // eslint-disable-next-line no-undef
                    var captcha = new TencentCaptcha(
                        this.config.TENCENT_CAPTCHA,
                        function (res) {
                            if (res.ret === 0) {
                                //发送登录请求
                                debugger;
                                let param = new URLSearchParams();
                                param.append(
                                    "username",
                                    that.loginForm.username
                                );
                                param.append(
                                    "password",
                                    that.loginForm.password
                                );
                                that.axios
                                    .post("/api/login", param)
                                    .then(({ data }) => {
                                        if (data.flag) {
                                            // 登录后保存用户信息
                                            that.$store.commit(
                                                "login",
                                                data.data
                                            );
                                            // 加载用户菜单
                                            generaMenu();
                                            that.$message.success("登录成功");
                                            that.$router.push({ path: "/" });
                                        } else {
                                            that.$message.error(data.message);
                                        }
                                    });
                            }
                        }
                    );
                    // 显示验证码
                    captcha.show();
                } else {
                    return false;
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
    background: linear-gradient(
        to bottom,
        rgba(255, 255, 255, 0.8),
        rgba(255, 255, 255, 0.6)
    );
    transform: translate(-50%, -50%);
    z-index: 1;
}
.shape {
    border-radius: 15px;
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
            color: #f56c6c;
            overflow: hidden;
            white-space: nowrap;
            border-right: 1px solid #f56c6c;
            animation: move 6s linear infinite;
            filter: drop-shadow(0 0 20px #f56c6c) drop-shadow(0 0 50px #f56c6c);
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