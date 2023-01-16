<template>
    <div
        ref="boxRef"
        style="boxsizing: border-box; width: 100%; height: 100%; display: flex"
    >
        <canvas ref="canvasRef">您的浏览器版本过低，请更新浏览器</canvas>
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
