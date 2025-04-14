<template>
  <ContentWrap>
    <el-table :data="business.products" :show-overflow-tooltip="true" :stripe="true">
      <el-table-column
        align="center"
        fixed="left"
        label="产品名称"
        min-width="160"
        prop="productName"
      >
        <template #default="scope">
          {{ scope.row.productName }}
        </template>
      </el-table-column>
      <el-table-column align="center" label="产品条码" min-width="120" prop="productNo" />
      <el-table-column align="center" label="产品单位" min-width="160" prop="productUnit">
        <template #default="{ row }">
          <dict-tag :type="DICT_TYPE.CRM_PRODUCT_UNIT" :value="row.productUnit" />
        </template>
      </el-table-column>
      <el-table-column
        :formatter="erpPriceTableColumnFormatter"
        align="center"
        label="产品价格（元）"
        min-width="140"
        prop="productPrice"
      />
      <el-table-column
        :formatter="erpPriceTableColumnFormatter"
        align="center"
        label="商机价格（元）"
        min-width="140"
        prop="businessPrice"
      />
      <el-table-column
        :formatter="erpPriceTableColumnFormatter"
        align="center"
        label="数量"
        min-width="100px"
        prop="count"
      />
      <el-table-column
        :formatter="erpPriceTableColumnFormatter"
        align="center"
        label="合计金额（元）"
        min-width="140"
        prop="totalPrice"
      />
    </el-table>
    <el-row class="mt-10px" justify="end">
      <el-col :span="3"> 整单折扣：{{ erpPriceInputFormatter(business.discountPercent) }}% </el-col>
      <el-col :span="4">
        产品总金额：{{ erpPriceInputFormatter(business.totalProductPrice) }} 元
      </el-col>
    </el-row>
  </ContentWrap>
</template>
<script lang="ts" setup>
import * as BusinessApi from '@/api/crm/business'
import { erpPriceInputFormatter, erpPriceTableColumnFormatter } from '@/utils'
import { DICT_TYPE } from '@/utils/dict'

const { business } = defineProps<{
  business: BusinessApi.BusinessVO
}>()
</script>
