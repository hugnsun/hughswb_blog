<template>
    <div>
        <!-- banner -->
        <div class="home-banner" :style="cover">
            <div class="banner-container">
                <!-- 联系方式 -->
                <h1 class="blog-title animated zoomIn">
                    {{ blogInfo.websiteConfig.websiteName }}
                </h1>
                <!-- 一言 -->
                <div class="blog-intro">
                    {{ obj.output }} <span class="typed-cursor">|</span>
                </div>
                <!-- 联系方式 -->
                <div class="blog-contact">
                    <a
                        v-if="isShowSocial('qq')"
                        class="mr-5 iconfont iconqq"
                        target="_blank"
                        :href="
                            'http://wpa.qq.com/msgrd?v=3&uin=' +
                            blogInfo.websiteConfig.qq +
                            '&site=qq&menu=yes'
                        "
                    />
                    <a
                        v-if="isShowSocial('github')"
                        target="_blank"
                        :href="blogInfo.websiteConfig.github"
                        class="mr-5 iconfont icongithub"
                    />
                    <a
                        v-if="isShowSocial('gitee')"
                        target="_blank"
                        :href="blogInfo.websiteConfig.gitee"
                        class="iconfont icongitee-fill-round"
                    />
                </div>
            </div>
            <!-- 向下滚动 -->
            <div class="scroll-down" @click="scrollDown">
                <v-icon color="#fff" class="scroll-down-effects">
                    mdi-chevron-down
                </v-icon>
            </div>
        </div>

        <!-- 主页文章 -->
        <v-row class="home-container">
            <!-- 目录 -->
            <v-col md="3" cols="4">
                <div class="blog-category">
                    <el-container
                        style="margin: 2%; margin-top: 5%; height: 80vh"
                    >
                        <el-header class="el-header-class"
                            ><span class="head_text">文章分类</span></el-header
                        >
                        <el-main style="padding: 10px">
                            <el-card
                                id="header-style"
                                shadow="hover"
                                v-for="(categoryItem, id) of categoryData"
                                :key="id"
                            >
                                <template slot="header" class="clearfix">
                                    <p
                                        class="titleStyle"
                                        :style="
                                            categoryItem.encryptionOrNot == 1
                                                ? 'color: #F56C6C'
                                                : 'color: #375ed1'
                                        "
                                    >
                                        {{
                                            categoryItem.encryptionOrNot
                                                ? "加密"
                                                : "公开"
                                        }}
                                    </p>
                                </template>
                                <router-link
                                    :to="'/categories/' + categoryItem.id"
                                >
                                    <span class="category-name">{{
                                        categoryItem.categoryName
                                    }}</span>
                                    <span class="category-count"
                                        >({{ categoryItem.articleCount }})</span
                                    >
                                </router-link>
                            </el-card>
                        </el-main>
                    </el-container>
                </div>
            </v-col>
            <v-col md="6" cols="8" id="cart">
                <!-- 说说轮播 -->
                <v-card class="animated zoomIn" v-if="talkList.length > 0">
                    <Swiper :list="talkList" />
                </v-card>
                <el-tabs v-model="tagId" @tab-click="tableClick">
                    <el-tab-pane
                        v-for="(tag, index) of tagList"
                        :key="index"
                        :label="tag.tagName"
                        :name="tag.id"
                    >
                        <v-card
                            class="animated zoomIn article-card"
                            style="border-radius: 12px 8px 8px 12px"
                            v-for="(item, index) of articleList"
                            :key="item.id"
                        >
                            <!-- 文章封面图 -->
                            <div :class="isRight(index)">
                                <router-link :to="'/articles/' + item.id">
                                    <v-img
                                        class="on-hover"
                                        width="100%"
                                        height="100%"
                                        :src="item.articleCover"
                                    />
                                </router-link>
                            </div>
                            <!-- 文章信息 -->
                            <div class="article-wrapper">
                                <div style="line-height: 1.4">
                                    <router-link :to="'/articles/' + item.id">
                                        {{ item.articleTitle }}
                                    </router-link>
                                </div>
                                <div class="article-info">
                                    <!-- 是否置顶 -->
                                    <span v-if="item.isTop == 1">
                                        <span style="color: #ff7242">
                                            <i class="iconfont iconzhiding" />
                                            置顶
                                        </span>
                                        <span class="separator">|</span>
                                    </span>
                                    <!-- 发表时间 -->
                                    <v-icon size="14"
                                        >mdi-calendar-month-outline</v-icon
                                    >
                                    {{ item.createTime | date }}
                                    <span class="separator">|</span>
                                    <!-- 文章分类 -->
                                    <router-link
                                        :to="'/categories/' + item.categoryId"
                                    >
                                        <v-icon size="14"
                                            >mdi-inbox-full</v-icon
                                        >
                                        {{ item.categoryName }}
                                    </router-link>
                                    <span class="separator">|</span>
                                    <!-- 文章标签 -->
                                    <router-link
                                        style="display: inline-block"
                                        :to="'/tags/' + tag.id"
                                        class="mr-1"
                                        v-for="tag of item.tagDTOList"
                                        :key="tag.id"
                                    >
                                        <v-icon size="14"
                                            >mdi-tag-multiple</v-icon
                                        >{{ tag.tagName }}
                                    </router-link>
                                </div>
                                <!-- 文章内容 -->
                                <div class="article-content">
                                    {{ item.articleContent }}
                                </div>
                            </div>
                        </v-card>
                    </el-tab-pane>
                </el-tabs>

                <!-- 无限加载 -->
                <infinite-loading @infinite="infiniteHandler">
                    <div slot="no-more" />
                </infinite-loading>
            </v-col>
            <!-- 博主信息 -->
            <v-col md="3" cols="10" class="d-md-block d-none">
                <div class="blog-wrapper">
                    <v-card class="animated zoomIn blog-card mt-5 card">
                        <div class="author-wrapper">
                            <!-- 博主头像 -->
                            <v-avatar size="110">
                                <img
                                    class="author-avatar"
                                    :src="blogInfo.websiteConfig.websiteAvatar"
                                />
                            </v-avatar>
                            <div
                                style="
                                    font-size: 1.375rem;
                                    margin-top: 0.625rem;
                                "
                            >
                                {{ blogInfo.websiteConfig.websiteAuthor }}
                            </div>
                            <div style="font-size: 0.875rem">
                                {{ blogInfo.websiteConfig.websiteIntro }}
                            </div>
                        </div>
                        <!-- 博客信息 -->
                        <div class="blog-info-wrapper">
                            <div class="blog-info-data">
                                <router-link to="/archives">
                                    <div style="font-size: 0.875rem">文章</div>
                                    <div style="font-size: 1.25rem">
                                        {{ blogInfo.articleCount }}
                                    </div>
                                </router-link>
                            </div>
                            <div class="blog-info-data">
                                <router-link to="/categories">
                                    <div style="font-size: 0.875rem">分类</div>
                                    <div style="font-size: 1.25rem">
                                        {{ blogInfo.categoryCount }}
                                    </div>
                                </router-link>
                            </div>
                            <div class="blog-info-data">
                                <router-link to="/tags">
                                    <div style="font-size: 0.875rem">标签</div>
                                    <div style="font-size: 1.25rem">
                                        {{ blogInfo.tagCount }}
                                    </div>
                                </router-link>
                            </div>
                        </div>
                        <!-- 收藏按钮 -->
                        <a class="collection-btn" @click="tip = true">
                            <v-icon color="#fff" size="18" class="mr-1"
                                >mdi-bookmark</v-icon
                            >
                            加入书签
                        </a>
                        <!-- 社交信息 -->
                        <div class="card-info-social">
                            <a
                                v-if="isShowSocial('qq')"
                                class="mr-5 iconfont iconqq"
                                target="_blank"
                                :href="
                                    'http://wpa.qq.com/msgrd?v=3&uin=' +
                                    blogInfo.websiteConfig.qq +
                                    '&site=qq&menu=yes'
                                "
                            />
                            <a
                                v-if="isShowSocial('github')"
                                target="_blank"
                                :href="blogInfo.websiteConfig.github"
                                class="mr-5 iconfont icongithub"
                            />
                            <a
                                v-if="isShowSocial('gitee')"
                                target="_blank"
                                :href="blogInfo.websiteConfig.gitee"
                                class="iconfont icongitee-fill-round"
                            />
                        </div>
                    </v-card>
                    <!-- 网站信息 -->
                    <v-card class="blog-card animated zoomIn mt-5 big card">
                        <div class="web-info-title">
                            <v-icon size="18">mdi-bell</v-icon>
                            公告
                        </div>
                        <div style="font-size: 0.875rem">
                            {{ blogInfo.websiteConfig.websiteNotice }}
                        </div>
                    </v-card>

                    <!-- 网站信息 -->
                    <v-card class="blog-card animated zoomIn mt-5 card">
                        <div class="web-info-title">
                            <v-icon size="18">mdi-chart-line</v-icon>
                            网站资讯
                        </div>
                        <div class="web-info">
                            <div style="padding: 4px 0 0">
                                运行时间:<span class="float-right">{{
                                    time
                                }}</span>
                            </div>
                            <div style="padding: 4px 0 0">
                                总访问量:<span class="float-right">
                                    {{ blogInfo.viewsCount }}
                                </span>
                            </div>
                        </div>
                    </v-card>
                </div>
            </v-col>
        </v-row>
        <!-- 提示消息 -->
        <v-snackbar v-model="tip" top color="#49b1f5" :timeout="2000">
            按CTRL+D 键将本页加入书签
        </v-snackbar>
    </div>
</template>

<script>
import Swiper from "../../components/Swiper.vue";
import EasyTyper from "easy-typer-js";
export default {
    components: {
        Swiper,
    },
    created() {
        this.init();
        this.listHomeTalks();
        this.getTagDic();
        this.allCategory();
        this.timer = setInterval(this.runTime, 1000);
    },
    data: function () {
        return {
            tip: false,
            time: "",
            obj: {
                output: "",
                isEnd: false,
                speed: 300,
                singleBack: false,
                sleep: 0,
                type: "rollback",
                backSpeed: 40,
                sentencePause: true,
            },
            items: [
                {
                    tab: "推荐文章",
                    value: 2,
                    content: "精华文章测试",
                },
                {
                    tab: "RabbitMQ",
                    value: 2,
                    content: "RabbitMQ",
                },
            ],
            articleList: [],
            talkList: [],
            categoryData: [],
            current: 1,
            tagId: 35,
            tagList: [],
        };
    },
    methods: {
        // 初始化
        init() {
            document.title = this.blogInfo.websiteConfig.websiteName;
            // 一言Api进行打字机循环输出效果
            fetch("https://v1.hitokoto.cn?c=i")
                .then((res) => {
                    return res.json();
                })
                .then(({ hitokoto }) => {
                    this.initTyped(hitokoto);
                });
        },
        //根据条件更改卡片颜色
        classType(data) {
            if (data.encryptionOrNot == 1) {
                return "background-color: red";
            }
        },
        allCategory() {
            this.axios.get("/api/allClassifications").then(({ data }) => {
                this.categoryData = data.data;
            });
        },
        listHomeTalks() {
            this.axios.get("/api/home/talks").then(({ data }) => {
                this.talkList = data.data;
            });
        },
        initTyped(input, fn, hooks) {
            const obj = this.obj;
            // eslint-disable-next-line no-unused-vars
            const typed = new EasyTyper(obj, input, fn, hooks);
        },
        scrollDown() {
            window.scrollTo({
                behavior: "smooth",
                top: document.documentElement.clientHeight,
            });
        },
        tableClick() {},
        runTime() {
            var timeold =
                new Date().getTime() -
                new Date(
                    this.blogInfo.websiteConfig.websiteCreateTime
                ).getTime();
            var msPerDay = 24 * 60 * 60 * 1000;
            var daysold = Math.floor(timeold / msPerDay);
            var str = "";
            var day = new Date();
            str += daysold + "天";
            str += day.getHours() + "时";
            str += day.getMinutes() + "分";
            str += day.getSeconds() + "秒";
            this.time = str;
        },
        getTagDic() {
            this.axios.get("/api/admin/tagDictionary").then(({ data }) => {
                this.tagList = data.data;
            });
        },
        infiniteHandler($state) {
            console.log("123");
            let md = require("markdown-it")();
            let params = {
                current: this.current,
                tagId: this.tagId,
            };
            this.axios.post("/api/articles", params).then(({ data }) => {
                if (data.data.length) {
                    // 去除markdown标签
                    data.data.forEach((item) => {
                        item.articleContent = md
                            .render(item.articleContent)
                            .replace(/<\/?[^>]*>/g, "")
                            .replace(/[|]*\n/, "")
                            .replace(/&npsp;/gi, "");
                    });
                    this.articleList.push(...data.data);
                    this.current++;
                    $state.loaded();
                } else {
                    $state.complete();
                }
            });
        },
    },

    computed: {
        isRight() {
            return function (index) {
                if (index % 2 == 0) {
                    return "article-cover left-radius";
                }
                return "article-cover right-radius";
            };
        },
        blogInfo() {
            return this.$store.state.blogInfo;
        },
        isShowSocial() {
            return function (social) {
                return (
                    this.blogInfo.websiteConfig.socialUrlList.indexOf(social) !=
                    -1
                );
            };
        },
        cover() {
            var cover = "";
            this.$store.state.blogInfo.pageList.forEach((item) => {
                if (item.pageLabel == "home") {
                    cover = item.pageCover;
                }
            });
            return (
                "background: url(" + cover + ") center center / cover no-repeat"
            );
        },
    },
};
</script>

<style lang="stylus">
.typed-cursor {
    opacity: 1;
    animation: blink 0.7s infinite;
}

@keyframes blink {
    0% {
        opacity: 1;
    }

    50% {
        opacity: 0;
    }

    100% {
        opacity: 1;
    }
}
</style>

<style scoped>
.titleStyle {
    font-weight: 500;
    text-align: center;
    background: url(https://oursdream-oss-ram.oss-cn-shanghai.aliyuncs.com/2022-10-30/9213053a-e26e-4f89-8aff-7cbcb986de27/%E7%9F%A9%E5%BD%A2%206.png);
    text-align: left;
    background-repeat: no-repeat;
    width: 100px;
    height: 40px;
    background-size: 100%;
    line-height: 30px;
    text-align: center;
    font-size: 14px;
    margin-left: -20px;
    margin-top: -18px;
}
.card {
    position: relative;
    box-shadow: 0 0 20px rgba(213, 68, 68, 0.895);
    animation: shadow-pulse 2s ease-in-out infinite;
}
@keyframes shadow-pulse {
    0% {
        box-shadow: 0 0 10px rgba(233, 72, 72, 0.2);
        transform: scale(1);
    }
    50% {
        box-shadow: 0 0 20px rgba(233, 72, 72, 0.2),
            0 0 20px rgba(233, 72, 72, 0.2) inset;
        transform: scale(1.04);
    }
    100% {
        box-shadow: 0 0 10px rgba(231, 55, 55, 0.2);
        transform: scale(1);
    }
}

.el-card {
    transition: transform 0.3s ease-in-out;
    transition: border 0.3s, box-shadow 0.3s;
    width: 90%;
    margin-top: 8%;
    text-align: center;
    font-weight: 500;
    font-size: 14px;
    text-transform: uppercase;
    cursor: pointer;
    background: #fff;
    background-size: 100%;
    border-radius: 15px 15px 15px 15px;
    color: #fff;
}
@keyframes wiggle {
    0% {
        transform: translate(0);
    }
    50% {
        transform: translate(-5px, 5px);
    }
    100% {
        transform: translate(0);
    }
}
.el-card :active {
    animation: wiggle 0.1s;
}
.el-card:hover {
    border: 2px solid rgb(240, 240, 245);
    box-shadow: 0 0 20px rgba(255, 255, 255, 0.5),
        0 0 40px rgba(255, 255, 255, 0.3), 0 0 60px rgba(255, 255, 255, 0.1);
    animation: breathe 2s ease-in infinite;
}

@keyframes breathe {
    0% {
        opacity: 0.7;
    }
    100% {
        opacity: 1;
    }
}
.category_name {
    animation: type 1s steps(50);
}
.category_name::before {
    content: "";
    width: 100%;
    animation: type 1s steps(50) infinite;
}
@keyframes type {
    0% {
        width: 0;
    }
    100% {
        width: 100%;
    }
}
.home-banner {
    position: absolute;
    top: -60px;
    left: 0;
    right: 0;
    height: 100vh;
    background-attachment: fixed;
    text-align: center;
    color: #fff !important;
    animation: header-effect 1s;
}
.banner-container {
    margin-top: 43vh;
    line-height: 1.5;
    color: #eee;
}
.blog-contact a {
    color: #fff !important;
}
.card-info-social {
    line-height: 40px;
    text-align: center;
    margin: 6px 0 -6px;
}
.card-info-social a {
    font-size: 1.5rem;
}
.left-radius {
    border-radius: 8px 0 0 8px !important;
    order: 0;
}
.right-radius {
    border-radius: 0 8px 8px 0 !important;
    order: 1;
}
.article-wrapper {
    font-size: 14px;
}
@media (min-width: 760px) {
    .blog-title {
        font-size: 2.5rem;
    }
    .blog-intro {
        font-size: 1.5rem;
    }
    .blog-contact {
        display: none;
    }
    .home-container {
        max-width: 2000px;
        margin: calc(100vh - 48px) auto 28px auto;
        padding: 0 5px;
    }
    .classify-container {
        max-width: 300px;
        margin: calc(100vh - 48px) auto 28px auto;
        padding: 0 5px;
    }
    .article-card {
        display: flex;
        align-items: center;
        height: 280px;
        width: 100%;
        margin-top: 20px;
    }
    .article-cover {
        overflow: hidden;
        height: 100%;
        width: 45%;
    }
    .on-hover {
        transition: all 0.6s;
    }
    .article-card:hover .on-hover {
        transform: scale(1.1);
    }
    .article-wrapper {
        padding: 0 2.5rem;
        width: 55%;
    }
    .article-wrapper a {
        font-size: 1.5rem;
        transition: all 0.3s;
    }
}

@media (max-width: 759px) {
    .blog-title {
        font-size: 26px;
    }
    .blog-contact {
        font-size: 1.25rem;
        line-height: 2;
    }
    .home-container {
        width: 100%;
        margin: calc(100vh - 66px) auto 0 auto;
    }
    .article-card {
        margin-top: 1rem;
    }
    .article-cover {
        border-radius: 8px 8px 0 0 !important;
        height: 230px !important;
        width: 100%;
    }
    .article-cover div {
        border-radius: 8px 8px 0 0 !important;
    }
    .article-wrapper {
        padding: 1.25rem 1.25rem 1.875rem;
    }
    .article-wrapper a {
        font-size: 1.25rem;
        transition: all 0.3s;
    }
}
.scroll-down {
    cursor: pointer;
    position: absolute;
    bottom: 0;
    width: 100%;
}
.scroll-down i {
    font-size: 2rem;
}
.article-wrapper a:hover {
    color: #8e8cd8;
}
.article-info {
    font-size: 95%;
    color: #858585;
    line-height: 2;
    margin: 0.375rem 0;
}
.article-info a {
    font-size: 95%;
    color: #858585 !important;
}
.article-content {
    line-height: 2;
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-line-clamp: 3;
    -webkit-box-orient: vertical;
}
.blog-wrapper {
    position: sticky;
    top: 10px;
    max-width: 350px;
    margin-top: 1%;
}

.blog-category {
    position: sticky;
    top: 10px;
    max-width: 350px;
}
.blog-card {
    line-height: 2;
    padding: 1.25rem 1.5rem;
}
.author-wrapper {
    text-align: center;
}
.blog-info-wrapper {
    display: flex;
    justify-self: center;
    padding: 0.875rem 0;
}
.blog-info-data {
    flex: 1;
    text-align: center;
}
.blog-info-data a {
    text-decoration: none;
}
.collection-btn {
    text-align: center;
    z-index: 1;
    font-size: 14px;
    position: relative;
    display: block;
    background-color: #49b1f5;
    color: #fff !important;
    height: 32px;
    line-height: 32px;
    transition-duration: 1s;
    transition-property: color;
}
.collection-btn:before {
    position: absolute;
    top: 0;
    right: 0;
    bottom: 0;
    left: 0;
    z-index: -1;
    background: #ff7242;
    content: "";
    transition-timing-function: ease-out;
    transition-duration: 0.5s;
    transition-property: transform;
    transform: scaleX(0);
    transform-origin: 0 50%;
}
.collection-btn:hover:before {
    transition-timing-function: cubic-bezier(0.45, 1.64, 0.47, 0.66);
    transform: scaleX(1);
}
.author-avatar {
    transition: all 0.5s;
}
.el-header-class {
    height: 5px;
    text-align: center;
    font-size: 16px;
    font-weight: 500;
    margin-right: 40px;
}
.author-avatar:hover {
    transform: rotate(360deg);
}
.web-info {
    padding: 0.25rem;
    font-size: 0.875rem;
}
.category-count {
    margin-left: 5px;
    color: #409eff;
    font-weight: 600;
    font-size: 16px;
}
.scroll-down-effects {
    color: #eee !important;
    text-align: center;
    text-shadow: 0.1rem 0.1rem 0.2rem rgba(0, 0, 0, 0.15);
    line-height: 1.5;
    display: inline-block;
    text-rendering: auto;
    -webkit-font-smoothing: antialiased;
    animation: scroll-down-effect 1.5s infinite;
}
@keyframes scroll-down-effect {
    0% {
        top: 0;
        opacity: 0.4;
        filter: alpha(opacity=40);
    }
    50% {
        top: -16px;
        opacity: 1;
        filter: none;
    }
    100% {
        top: 0;
        opacity: 0.4;
        filter: alpha(opacity=40);
    }
}
.big i {
    color: #f00;
    animation: big 0.8s linear infinite;
}
@keyframes big {
    0%,
    100% {
        transform: scale(1);
    }
    50% {
        transform: scale(1.2);
    }
}
</style>
<style lang="scss">
#header-style {
    .el-card__header {
        padding: 18px 20px;
        box-sizing: border-box;
        height: 30px;
    }
}
</style>