<template>
    <form-dialog :loading="loading" title="编辑" :value="value" @close="cancel" @open="open">
        <abstract-form :model="form" :rules="rules" size="">
            <el-form-item label="名 称" prop="name">
                <el-input :value="form.fullname" readonly/>
            </el-form-item>
            <el-form-item label="访问路径" prop="url">
                <el-input :value="form.url" readonly/>
            </el-form-item>
            <el-form-item label="总频率" prop="totalRate">
                <el-input-number
                    v-model="form.totalRate"
                    controls-position="right"
                    :min="1"
                    :step="1"
                    step-strictly size="small"
                />
            </el-form-item>
            <el-form-item label="单个IP频率" prop="ipRate">
                <el-input-number
                    v-model="form.ipRate"
                    controls-position="right"
                    :min="1"
                    :step="1"
                    step-strictly size="small"
                />
            </el-form-item>
        </abstract-form>

        <template v-slot:footer>
            <el-button plain size="small" @click="closeDialog">取 消</el-button>
            <el-button size="small" type="primary" @click="confirm">确 定</el-button>
        </template>
    </form-dialog>
</template>

<script>
import dialogMixin from "@/mixin/dialogMixin"
import AbstractForm from "@/component/AbstractForm"
import FormDialog from '@/component/FormDialog'
import {updateResource} from "@/api/system/resource"
import {mergeObj, resetObj} from '@/util'

export default {
    name: "EditDialog",

    mixins: [dialogMixin],

    components: {AbstractForm, FormDialog},

    props: {
        value: {type: Boolean, default: false},
        data: {type: Object, default: () => ({})},
    },

    data() {
        return {
            loading: false,
            form: {
                id: null,
                fullname: null,
                url: null,
                totalRate: 0,
                ipRate: 0
            },
            rules: {
                totalRate: [{required: true, message: '总频率不能为空', trigger: 'change'}],
                ipRate: [{required: true, message: '单个ip的频率不能为空', trigger: 'change'}],
            }
        }
    },

    methods: {
        open() {
            this.$nextTick(() => mergeObj(this.form, this.data))
        },

        cancel() {
            this.closeDialog()
            resetObj(this.form)
            this.$nextTick(() => this.$refs.form.clearValidate())
        },

        confirm() {
            if (this.loading) return
            this.$refs.form.validate(v => {
                if (!v) return
                this.loading = true
                updateResource(this.form)
                    .then(({msg}) => this.$emit('success', msg))
                    .finally(() => this.loading = false)
            })
        }
    }
}
</script>
