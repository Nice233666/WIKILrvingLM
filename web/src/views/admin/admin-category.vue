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
            <a-input v-model:value="param.name" placeholder="想想你该查什么" />
          </a-form-item>
          <a-form-item>
            <a-button type="primary" @click="handleQuery({ page:1,size:pagination.pageSize})" >查询</a-button>
          </a-form-item>
          <a-form-item>
            <a-button type="primary" @click="add()">添加</a-button>
          </a-form-item>
        </a-form>
      </p>

      <!--列,key id,数据category,分页,等待框,分页执行方法-->
      <a-table
          :columns="columns"
          :row-key="record=>record.id"
          :data-source="categorys"
          :pagination="pagination"
          :loading="loading"
          @change="handleTableChange"
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
      title="分类"
      v-model:visible="modalVisible"
      :confirm-loading="modalLoading"
      ok-text="我测,说藏话了"
      @ok="handleModalOk"
  >
    <!--弹出表单-->
    <a-form :model="category" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
      <a-form-item label="名称">
        <a-input v-model:value="category.name"/>
      </a-form-item>
      <a-form-item label="父分类">
        <a-input v-model:value="category.parent"/>
      </a-form-item>
      <a-form-item label="顺序">
        <a-input v-model:value="category.sort"/>
      </a-form-item>
    </a-form>
  </a-modal>

</template>

<script lang="ts">
import {defineComponent, onMounted, ref} from 'vue';//写上onMounted VUE3.0 setup集成了 导入ref 做响应式数据
import axios from 'axios';
import {message} from 'ant-design-vue';
import {Tool} from "@/utils/tool";


export default defineComponent({
  name: 'AdminCategory',
  setup() {
    const param = ref();
    param.value = {};
    const categorys = ref();//响应式数据 获取的书籍实时反馈到页面上
    const pagination = ref({
      current: 1,//当前页
      pageSize: 10,//分页条数
      total: 0
    });

    const loading = ref(false);

    const columns = [//页面的响应变量 不是数据的响应变量 代表就是这个表格里面有多少个数据 下面数据我们自己定义的
      {
        title: '名称',
        dataIndex: 'name',
      },
      {
        title: '父分类',
        key:'parent',
        dateIndex:'parent',
      },
      {
        title: '排序',
        dataIndex: 'sort'
      },
      {
        title: 'Action',
        key: 'action',
        slots: {customRender: 'action'}
      }
    ];
    /**
     * 数据查询
     **/
    const handleQuery = (params: any) => {
      loading.value = true;
      axios.get("/category/list", {
        params:{
          page:params.page,
          size:params.size,
          name:param.value.name,//是否有value,没有就不传
        }
      }).then((response) => {
        loading.value = false;
        const data = response.data;
        if (data.success){
          categorys.value = data.content.list;
          //重置分页按钮
          pagination.value.current = params.page;//点第二页的按钮的时候前端 不会刷新 还是第一页的地方 实际我们以及到第二页了
          pagination.value.total=data.content.total;
        }else{
          message.error(data.message);
        }
      });
    };
    /**
     * 表格点击页码时触发
     */
    const handleTableChange = (pagination: any) => {
      console.log("看看自带的分页参数都有啥：" + pagination);
      handleQuery({
        page: pagination.current,
        size: pagination.pageSize
      });
    };

    /**表单*/
    const category=ref({});
    const modalVisible = ref(false);
    const modalLoading = ref(false);
    const handleModalOk = () => {
      modalLoading.value = true;
      axios.post("/category/save",category.value).then((response) => {
        modalLoading.value = false;
        const data = response.data;  //commonResp
        if(data.success){
          modalVisible.value = false;

          //重新加载列表
          handleQuery({
            page:pagination.value.current,  //查询当前所在的页
            size:pagination.value.pageSize
          });
        }else{
          message.error(data.message);
        }
      });
    };
    /**
     * 编辑
     */
    const edit = ( record:any ) =>{
      modalVisible .value = true;
      category.value = Tool.copy(record);  //通过JSON对象转换来生成新的对象，从而不会直接更改到原来所显示的对象
    };
    /**
     * 添加
     */
    const add = () =>{
      modalVisible .value = true;
      category.value={};
    };


    /**
     * 删除
     */
    const handleDelete = ( id:number ) =>{

      axios.delete("/category/delete/"+id).then((response)=>{
        const data = response.data;  //commonResp
        if(data.success){
          //重新加载列表
          handleQuery({
            page:pagination.value.current,  //查询当前所在的页
            size:pagination.value.pageSize
          });
        }
      })
    };


    onMounted(() => {
      handleQuery({
        page:1,
        size:pagination.value.pageSize
      });

    });

    return {
      categorys,//表格
      pagination,
      columns,
      loading,
      handleTableChange,


      edit,
      add,
      handleDelete,
      handleQuery,

      param,
      category,
      modalVisible,
      modalLoading,
      handleModalOk,
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