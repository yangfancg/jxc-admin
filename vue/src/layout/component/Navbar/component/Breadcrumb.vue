<template>
    <el-breadcrumb class="app-breadcrumb">
        <transition-group name="breadcrumb">
            <el-breadcrumb-item v-for="item in data" :key="item.path">
                <span class="no-redirect">{{ item.meta.title }}</span>
            </el-breadcrumb-item>
        </transition-group>
    </el-breadcrumb>
</template>

<script>
export default {
    data: () => ({data: []}),

    watch: {
        $route(route) {
            if (route.path.startsWith('/redirect')) return
            this.getBreadcrumb()
        }
    },

    methods: {
        getBreadcrumb() {
            // only show routes with meta.title
            this.data = this.$route.matched.filter(item => item.meta.title)
        }
    },

    mounted() {
        this.getBreadcrumb()
    }
}
</script>

<style lang="scss" scoped>
@import "~@/asset/style/variables.scss";

.app-breadcrumb {
    margin-left: 8px;
    line-height: $nav-height;

    .no-redirect {
        color: $--color-gray;
        cursor: text;
    }
}
</style>
