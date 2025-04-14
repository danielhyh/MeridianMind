<template>
  <div class="end-node-wrapper">
    <div :class="`${useTaskStatusClass(currentNode?.activityStatus)}`" class="end-node-box cursor-pointer" @click="nodeClick">
      <span class="node-fixed-name" title="结束">结束</span>
    </div>
  </div>
  <el-dialog v-model="dialogVisible" append-to-body title="审批信息" width="1000px">
      <el-row>
        <el-table
          :data="processInstanceInfos"
          border
          header-cell-class-name="table-header-gray"
          size="small"
        >
          <el-table-column
            align="center"
            header-align="center"
            label="序号"
            type="index"
            width="50"
          />
          <el-table-column
            align="center"
            label="发起人"
            min-width="100"
            prop="assigneeUser.nickname"
          />
          <el-table-column align="center" label="部门" min-width="100">
            <template #default="scope">
              {{ scope.row.assigneeUser?.deptName || scope.row.ownerUser?.deptName }}
            </template>
          </el-table-column>
          <el-table-column
            :formatter="dateFormatter"
            align="center"
            label="开始时间"
            min-width="140"
            prop="createTime"
          />
          <el-table-column
            :formatter="dateFormatter"
            align="center"
            label="结束时间"
            min-width="140"
            prop="endTime"
          />
          <el-table-column align="center" label="审批状态" min-width="90" prop="status">
            <template #default="scope">
              <dict-tag :type="DICT_TYPE.BPM_PROCESS_INSTANCE_STATUS" :value="scope.row.status" />
            </template>
          </el-table-column>
         
          <el-table-column align="center" label="耗时" prop="durationInMillis" width="100">
            <template #default="scope">
              {{ formatPast2(scope.row.durationInMillis) }}
            </template>
          </el-table-column>
        </el-table>
      </el-row>
    </el-dialog>
</template>
<script lang="ts" setup>
import { SimpleFlowNode } from '../consts'
import { useWatchNode, useTaskStatusClass } from '../node'
import { dateFormatter, formatPast2 } from '@/utils/formatTime'
import { DICT_TYPE } from '@/utils/dict'
defineOptions({
  name: 'EndEventNode'
})
const props = defineProps({
  flowNode: {
    type: Object as () => SimpleFlowNode,
    default: () => null
  }
})
// 监控节点变化
const currentNode = useWatchNode(props)
// 是否只读
const readonly = inject<Boolean>('readonly')
const processInstance = inject<Ref<any>>('processInstance', ref({}))
// 审批信息的弹窗显示，用于只读模式
const dialogVisible = ref(false) // 弹窗可见性
const processInstanceInfos = ref<any[]>([]) // 流程的审批信息

const nodeClick = () => {
  if (readonly) { 
    if(processInstance && processInstance.value){
      processInstanceInfos.value = [
      {
        assigneeUser: processInstance.value.startUser,
        createTime: processInstance.value.startTime,
        endTime: processInstance.value.endTime,
        status: processInstance.value.status,
        durationInMillis: processInstance.value.durationInMillis
      }
    ]
      dialogVisible.value = true
    }
  }
}
</script>
<style lang="scss" scoped></style>
