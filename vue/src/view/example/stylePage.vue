<template>
    <div>
        <el-row class="tip-row">
            对element的样式做了些修改
        </el-row>
        <el-card>
            <el-row type="flex">
                <div class="el-message el-message--success" role="alert">
                    <i class="el-message__icon el-icon-success"/>
                    <p class="el-message__content">成功提示</p>
                </div>
                <div class="el-message el-message--info" role="alert">
                    <i class="el-message__icon el-icon-info"/>
                    <p class="el-message__content">信息提示</p>
                </div>
                <div class="el-message el-message--warning" role="alert">
                    <i class="el-message__icon el-icon-warning"/>
                    <p class="el-message__content">警告提示</p>
                </div>
                <div class="el-message el-message--error" role="alert">
                    <i class="el-message__icon el-icon-error"/>
                    <p class="el-message__content">错误提示</p>
                </div>
            </el-row>
            <el-row>
                <el-button size="small" @click="dialog=true">默认按钮（点击打开dialog</el-button>
                <el-button size="small" plain type="dashed">dashed按钮</el-button>
                <el-button size="small" type="primary">主要按钮</el-button>
                <el-button size="small" type="success" @click="$message.success('成功消息')">成功按钮</el-button>
                <el-button size="small" type="info" @click="$message.info('信息消息')">信息按钮</el-button>
                <el-button size="small" type="warning" @click="$message.warning('警告消息')">警告按钮</el-button>
                <el-button size="small" type="danger" @click="$message.error('错误消息')">危险按钮</el-button>
            </el-row>
            <el-row class="table-container">
                <liner-progress show/>
                <abstract-table :data="tableData">
                    <el-table-column label="姓名" prop="name"/>
                    <el-table-column label="住址" prop="address"/>
                    <el-table-column label="电话" prop="tel"/>
                    <el-table-column label="年龄" prop="age"/>
                </abstract-table>
            </el-row>
        </el-card>

        <form-dialog v-model="dialog" :loading="false" title="封装的dialog">
            <p v-for="i in 50" :key="i">超出部分滚动...</p>
            <template v-slot:footer>
                <el-button plain size="small" @click="dialog=false">取 消</el-button>
                <el-button size="small" type="primary" @click="dialog=false">保 存</el-button>
            </template>
        </form-dialog>
    </div>
</template>

<script>
import AbstractTable from '@/component/AbstractTable'
import LinerProgress from '@/component/LinerProgress'
import FormDialog from "@/component/FormDialog"

export default {
    name: "stylePage",

    components: {AbstractTable, LinerProgress, FormDialog},

    data() {
        return {
            dialog: false,
            tableData: [
                {name: '老王', address: '中国', tel: '13232423232', age: 18},
                {name: '老王', address: '中国', tel: '13232423232', age: 18},
                {name: '老王', address: '中国', tel: '13232423232', age: 18}
            ]
        }
    },

    mounted() {
        const f = (type, time = 0) => {
            setTimeout(() => {
                this.$notify({
                    title: type,
                    message: '提示信息',
                    type
                })
            }, time)
        }
        let i = 0;
        ['success', 'info', 'warning', 'error'].forEach(t => {
            f(t, i)
            i += 200
        })

        this.$once('hook:beforeDestroy', this.$notify.closeAll)
    }
}
</script>

<style lang="scss" scoped>
.el-message {
    position: static;
    transform: translateX(0);

    & + .el-message {
        margin-left: 20px;
    }
}

.el-row + .el-row {
    margin-top: 20px;
}

.el-tag + .el-tag {
    margin-left: 20px;
}
</style>
