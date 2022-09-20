<template>
  <a-layout>
    <a-layout-content :style="{ background:'fff',padding:'24px',margin:'0',minHeight:'280px'}">
      <div class="doc">
        <a-row>
          <a-col :span="6">
            <a-tree
                v-if="level1.length >0"
              :tree-data="level1"
              @select="onSelect"
              :replaceFields="{title: 'name',key: 'id',value: 'id'}"
              :defaultExpandAll="true"
            >

            </a-tree>
          </a-col>
          <a-col :span="18">


          </a-col>
        </a-row>
      </div>
    </a-layout-content>
  </a-layout>
</template>
<script lang="ts">
import {defineComponent, onMounted, ref , createVNode} from 'vue';//写上onMounted VUE3.0 setup集成了 导入ref 做响应式数据
import axios from 'axios';
import {message , Modal} from 'ant-design-vue';
import {Tool} from "@/utils/tool";
import {useRoute} from "vue-router";
import { ExclamationCircleOutlined } from '@ant-design/icons-vue'; //想做警告来着的图标 无法解决怎么调用二次确认时的有参函数
import E from 'wangeditor';


export default defineComponent({
  name: 'Doc',
  setup() {
    const route=useRoute();

    const docs = ref();//响应式数据 获取的书籍实时反馈到页面上

    /**
     * 一级文档树，children属性就是二级文档
     * [{
     *  id:"",
     *  name:"",
     *  children:[{
     *    id:"",
     *  name:"",
     *  }]
     * }]}
     */
    const level1=ref();
    level1.value=[];

    /**
     * 数据查询
     **/
    const handleQuery = () => {
      axios.get("/doc/all", ).then((response) => {
        const data = response.data;
        if (data.success){
          docs.value = data.content;
          console.log("原始数组:",docs.value);
          level1.value=[];
          level1.value=Tool.array2Tree(docs.value,0);

          console.log("树形数组:",level1.value)
        }else{
          message.error(data.message);
        }
      });
    };



    onMounted(() => {
      handleQuery();
    });

    return {
      level1,

    }
  }
});
</script>