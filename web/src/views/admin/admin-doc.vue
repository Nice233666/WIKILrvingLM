<template>
  <a-layout>
    <a-layout-content
        :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '80px'  }"
    >
      <p>
        <a-form
            layout="inline"
            :model="param"
        >
          <a-form-item>
            <a-button type="primary" @click="handleQuery()" >查询</a-button>
          </a-form-item>
          <a-form-item>
            <a-button type="primary" @click="add()">添加</a-button>
          </a-form-item>
        </a-form>
      </p>

      <!--列,key id,数据doc,分页,等待框,分页执行方法-->
      <a-table
          :columns="columns"
          :row-key="record=>record.id"
          :data-source="level1"
          :loading="loading"
          :pagination="false"
      >
        <template #cover="{text:cover}">
          <img class="img-wh" v-if="cover" :src="cover" alt="avatar"/> <!--渲染图片-->
        </template>
        <template v-slot:action="{ text, record }">
          <a-space size="small">
            <a-button type="primary" @click="edit(record)">
              编辑
            </a-button>
            <a-popconfirm
                title="删了就真的没有啦,您是不是点歪了"
                ok-text="真删不跟你闹"
                cancel-text="哎哟点错了"
                @confirm="handleDelete(record.id)"
            >
              <a-button type="primary">
                删除
              </a-button>
            </a-popconfirm>
          </a-space>
        </template>
      </a-table>
    </a-layout-content>

  </a-layout>
  <a-modal
      title="文档"
      v-model:visible="modalVisible"
      :confirm-loading="modalLoading"
      ok-text="我测,说藏话了"
      @ok="handleModalOk"
  >
    <!--弹出表单-->
    <a-form :model="doc" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
      <a-form-item label="名称">
        <a-input v-model:value="doc.name"/>
      </a-form-item>
      <a-form-item label="父文档">
        <a-tree-select
            v-model:value="doc.parent"
            style="width: 100%"
            :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
            :tree-data="treeSelectData"
            placeholder="请选择父文档"
            tree-default-expand-all
            :replaceFields="{title: 'name',key: 'id',value: 'id'}"
        >
        </a-tree-select>
      </a-form-item>
      <a-form-item label="顺序">
        <a-input v-model:value="doc.sort"/>
      </a-form-item>
    </a-form>
  </a-modal>

</template>

<script lang="ts">
import {defineComponent, onMounted, ref} from 'vue';//写上onMounted VUE3.0 setup集成了 导入ref 做响应式数据
import axios from 'axios';
import {message} from 'ant-design-vue';
import {Tool} from "@/utils/tool";
import {useRoute} from "vue-router";



export default defineComponent({
  name: 'AdminDoc',
  setup() {
    const route=useRoute();
    console.log("query"+route.query);
    console.log("path"+route.path);
    console.log("hash"+route.hash);
    const param = ref();
    param.value = {};
    const docs = ref();//响应式数据 获取的书籍实时反馈到页面上


    const loading = ref(false);

    const columns = [//页面的响应变量 不是数据的响应变量 代表就是这个表格里面有多少个数据 下面数据我们自己定义的
      {
        title: '名称',
        dataIndex: 'name',
      },
      {
        title: '父文档',
        dataIndex: 'parent',
      },
      {
        title: '排序',
        dataIndex: 'sort',
      },
      {
        title: 'Action',
        key: 'action',
        slots: {customRender: 'action'}
      }
    ];
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


    /**
     * 数据查询
     **/
    const handleQuery = () => {
      loading.value = true;
      axios.get("/doc/all", ).then((response) => {
        loading.value = false;
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

    /**表单*/
    //因为树选择组件的属性状态，会随当前编辑的节点而变化，所以单独声明一个响应式变量
    const treeSelectData=ref();
    treeSelectData.value=[];
    const doc=ref({});
    const modalVisible = ref(false);
    const modalLoading = ref(false);
    const handleModalOk = () => {
      modalLoading.value = true;
      axios.post("/doc/save",doc.value).then((response) => {
        modalLoading.value = false;
        const data = response.data;  //commonResp
        if(data.success){
          modalVisible.value = false;

          //重新加载列表
          handleQuery();
        }else{
          message.error(data.message);
        }
      });
    };

    /**
     * 将某节点及其子孙节点全部置为disabled
     */
    const setDisable=(treeSelectData: any,id: any)=>{
          // console.log("asdasdasd",treeSelectData,id);
          //遍历数组，即遍历某一层节点
          for(let i=0;i<treeSelectData.length;i++){
            const node=treeSelectData[i];
            if(node.id===id){
              //如果当前节点就是目标节点
              console.log("disabled   顶层父节点？",node);
              //将目标节点设置为disabled
              node.disabled=true;

              //遍历所有的子孙节点
              const children=node.children;
              if(Tool.isNotEmpty(children)){
                for (let j=0;j<children.length;j++){
                  setDisable(children,children[j].id);
                }
              }
            }else{
              //如果当前节点不是目标节点，则到其子节点再找找看
              const children=node.children;
              if(Tool.isNotEmpty(children)){
                setDisable(children,id);
              }
            }
          }
        };

    const delIds: Array<string> =[];
    /**
     * 递归获取到需要删除的父节点下所有子节点
     */
    const getDelID=(treeSelectData: any,id: any)=>{
      //遍历数组，即遍历某一层节点
      for(let i=0;i<treeSelectData.length;i++){
        const node=treeSelectData[i];
        if(node.id===id){
          //如果当前节点就是目标节点
          console.log("disabled   顶层父节点？",node);
          //把递归的idpush
          delIds.push(node.id);

          //遍历所有的子孙节点
          const children=node.children;
          if(Tool.isNotEmpty(children)){
            for (let j=0;j<children.length;j++){
              getDelID(children,children[j].id);
            }
          }
        }else{
          //如果当前节点不是目标节点，则到其子节点再找找看
          const children=node.children;
          if(Tool.isNotEmpty(children)){
            getDelID(children,id);
          }
        }
      }
    }


    /**
     * 编辑
     */
    const edit = ( record:any ) =>{
      modalVisible .value = true;
      doc.value = Tool.copy(record);  //通过JSON对象转换来生成新的对象，从而不会直接更改到原来所显示的对象

      //不能选择当前节点下的所有子孙节点,作为父节点，会使树断开
      treeSelectData.value=Tool.copy(level1.value);
      setDisable(treeSelectData.value,record.id);

      //为选择树添加一个“无”
      treeSelectData.value.unshift({id:0,name:'无'});
    };
    /**
     * 添加
     */
    const add = () =>{
      modalVisible .value = true;
      doc.value={
        ebookId:route.query.ebookId,
      };

      treeSelectData.value=Tool.copy(level1.value);
      //为选择树添加一个“无”
      treeSelectData.value.unshift({id:0,name:'无'});
    };


    /**
     * 删除
     */
    const handleDelete = ( id:number ) =>{
      getDelID(level1.value,id);
      axios.delete("/doc/delete/"+delIds.join(",")).then((response)=>{
        const data = response.data;  //commonResp
        if(data.success){
          //重新加载列表
          handleQuery();
        }
      })
    };


    onMounted(() => {
      handleQuery();

    });

    return {
      //docs,//表格
      columns,
      loading,
      level1,

      edit,
      add,
      handleDelete,
      handleQuery,

      param,
      doc,
      modalVisible,
      modalLoading,
      handleModalOk,

      treeSelectData,
    }
  }
});
</script>

<!-- #scoped表示当前组件才有用 -->
<style scoped>
.img-wh {
  width: 50px;
  height: 50px;
  line-height: 50px;
  border-radius: 8%;
  margin: 5px 0;
}

</style>