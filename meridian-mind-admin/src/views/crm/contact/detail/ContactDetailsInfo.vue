<template>
  <ContentWrap>
    <el-collapse v-model="activeNames">
      <el-collapse-item name="basicInfo">
        <template #title>
          <span class="text-base font-bold">基本信息</span>
        </template>
        <el-descriptions :column="4">
          <el-descriptions-item label="姓名">{{ contact.name }}</el-descriptions-item>
          <el-descriptions-item label="客户名称">{{ contact.customerName }}</el-descriptions-item>
          <el-descriptions-item label="手机">{{ contact.mobile }}</el-descriptions-item>
          <el-descriptions-item label="电话">{{ contact.telephone }}</el-descriptions-item>
          <el-descriptions-item label="邮箱">{{ contact.email }}</el-descriptions-item>
          <el-descriptions-item label="QQ">{{ contact.qq }}</el-descriptions-item>
          <el-descriptions-item label="微信">{{ contact.wechat }}</el-descriptions-item>
          <el-descriptions-item label="地址">
            {{ contact.areaName }} {{ contact.detailAddress }}
          </el-descriptions-item>
          <el-descriptions-item label="职务">{{ contact.post }}</el-descriptions-item>
          <el-descriptions-item label="直属上级">{{ contact.parentName }}</el-descriptions-item>
          <el-descriptions-item label="关键决策人">
            <dict-tag :type="DICT_TYPE.INFRA_BOOLEAN_STRING" :value="contact.master" />
          </el-descriptions-item>
          <el-descriptions-item label="性别">
            <dict-tag :type="DICT_TYPE.SYSTEM_USER_SEX" :value="contact.sex" />
          </el-descriptions-item>
          <el-descriptions-item label="下次联系时间">
            {{ formatDate(contact.contactNextTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="备注">{{ contact.remark }}</el-descriptions-item>
        </el-descriptions>
      </el-collapse-item>
      <el-collapse-item name="systemInfo">
        <template #title>
          <span class="text-base font-bold">系统信息</span>
        </template>
        <el-descriptions :column="4">
          <el-descriptions-item label="负责人">{{ contact.ownerUserName }}</el-descriptions-item>
          <el-descriptions-item label="最后跟进记录">
            {{ contact.contactLastContent }}
          </el-descriptions-item>
          <el-descriptions-item label="最后跟进时间">
            {{ formatDate(contact.contactLastTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="">&nbsp;</el-descriptions-item>
          <el-descriptions-item label="创建人">{{ contact.creatorName }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">
            {{ formatDate(contact.createTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="更新时间">
            {{ formatDate(contact.updateTime) }}
          </el-descriptions-item>
        </el-descriptions>
      </el-collapse-item>
    </el-collapse>
  </ContentWrap>
</template>
<script lang="ts" setup>
import * as ContactApi from '@/api/crm/contact'
import { DICT_TYPE } from '@/utils/dict'
import { formatDate } from '@/utils/formatTime'

const { contact } = defineProps<{
  contact: ContactApi.ContactVO
}>()

// 展示的折叠面板
const activeNames = ref(['basicInfo', 'systemInfo'])
</script>
