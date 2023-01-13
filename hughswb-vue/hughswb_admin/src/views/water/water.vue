<template>
    <div
        ref="boxRef"
        style="boxsizing: border-box; width: 100%; height: 100%; display: flex"
    >
        <canvas ref="canvasRef">
                    <div class="login-title">管理员登录</div>
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
                        prefix-icon="el-icon-user-solid"
                        placeholder="用户名"
                        @keyup.enter.native="login"
                    />
                </el-form-item>
                <!-- 密码输入框 -->
                <el-form-item prop="password">
                    <el-input
                        v-model="loginForm.password"
                        prefix-icon="iconfont el-icon-mymima"
                        show-password
                        placeholder="密码"
                        @keyup.enter.native="login"
                    />
                </el-form-item>
            </el-form>
            <!-- 登录按钮 -->
            <el-button type="primary" @click="login">登录</el-button>
        </div>
        </canvas>
    </div>
</template>

<script>
import WaterRipple from "./waterRipple";
import waterBg from "../../images/water.png";

let canvasWidth = 600;
let canvasHeight = 600;

export default {
    data() {
        return {
            waterRipple: null,
            timer: null,
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
};
</script>
