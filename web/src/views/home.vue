<template>
  <a-layout>
    <a-layout-sider width="200" style="background: #fff">
      <a-menu
          mode="inline"
          :style="{ height: '100%', borderRight: 0 }"
          @click="handleClick"
      >
        <a-menu-item key="welcome">
          <MailOutlined />
          <span>欢迎</span>
        </a-menu-item>
        <a-sub-menu v-for="item in level1" :key="item.id">
          <template v-slot:title>
              <span>
                <user-outlined />{{item.name}}
              </span>
          </template>
          <a-menu-item v-for="child in item.children" :key="child.id">
            <span>
              <MailOutlined />{{child.name}}
            </span>
          </a-menu-item>
        </a-sub-menu>
      </a-menu>
    </a-layout-sider>
    <a-layout-content
        :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }">
      <div class="welcome" v-show="isShowWelcome">
        <p>欢迎欢迎热烈欢迎</p>
      </div>
      <a-list v-show="!isShowWelcome" item-layout="vertical" size="large" :grid="{gutter : 20,column : 3}"  :data-source="ebooks">
        <template #renderItem="{ item }">
          <a-list-item key="item.title">
            <template #actions>
          <span v-for="{ type, text } in actions" :key="type">
            <component v-bind:is="type" style="margin-right: 8px" />
            {{ text }}
          </span>
            </template>

            <a-list-item-meta :description="item.description">
              <template #title>
                <router-link :to="'/doc?ebookId=' + item.id">
                  {{ item.name }}
                </router-link>
              </template>
              <template #avatar><a-avatar :src="item.cover" /></template>
            </a-list-item-meta>
          </a-list-item>
        </template>
      </a-list>
    </a-layout-content>
  </a-layout>>
</template>

<script lang="ts">
import { defineComponent ,onMounted,ref} from 'vue';
import axios from "axios";
import { StarOutlined, LikeOutlined, MessageOutlined } from '@ant-design/icons-vue';
import {Tool} from "@/utils/tool";
import {message} from "ant-design-vue";

const listData:any = [];

for (let i = 0; i < 23; i++) {
  listData.push({
    href: 'https://www.antdv.com/',
    title: `ant design vue part ${i}`,
    avatar: 'https://zos.alipayobjects.com/rmsportal/ODTLcjxAfvqbxHnVXCYX.png',
    description:
        'Ant Design, a design language for background applications, is refined by Ant UED Team.',
    content:
        'We supply a series of design principles, practical patterns and high quality design resources (Sketch and Axure), to help people create their product prototypes beautifully and efficiently.',
  });
}

export default defineComponent({
  name: 'Home',
  setup(){//初始化函数

    const ebooks=ref();//实现响应式数据 方式1



    const pagination = {
      onChange: (page:any) => {
        console.log(page);
      },
      pageSize: 3,
    };
    const actions:any = [
      { type: 'StarOutlined', text: '156' },
      { type: 'LikeOutlined', text: '156' },
      { type: 'MessageOutlined', text: '2' },
    ];


    const level1=ref();
    let categorys: any;
    /**
     * 数据查询
     **/
    const handleQueryCategory = () => {
      axios.get("/category/all", ).then((response) => {
        const data = response.data;
        if (data.success){
          categorys = data.content;
          console.log("原始数组:",categorys);

          level1.value=[];
          level1.value=Tool.array2Tree(categorys,0);
          console.log("树形数组:",level1.value)
        }else{
          message.error(data.message);
        }
      });
    };
    const isShowWelcome=ref(true);
    let category2Id=0;

    /**
     * 通过key来改变欢迎列表显示与隐藏
     */
    const handleClick = (value:any)=>{
      // console.log("menu click  ",value);
      if(value.key==="welcome"){
        isShowWelcome.value=true;
      }else{
        category2Id=value.key;
        queryAllList();
        isShowWelcome.value=false;
      }
      // isShowWelcome.value = value.key === "welcome";
    };

    const queryAllList = ()=>{
      axios.get( "/ebook/list",{
        params:{
          page:1,
          size:500,
          category2Id:category2Id,
        }
      }).then((response)=>{ //默认会有个参数 这个参数名是自个起的
        const data=response.data;//后端的commonResp的数据
        ebooks.value=data.content.list;
      });
    };

    onMounted(()=>{//页面加载完后的才执行的生命周期函数
      handleQueryCategory();

    });

    return{
      ebooks,

      listData,
      pagination,
      actions,
      level1,
      handleClick,
      isShowWelcome,
    };
  },

  components: {
    StarOutlined,
    LikeOutlined,
    MessageOutlined,
  },
});
</script>
<style scoped>
.ant-avatar {
  width: 50px;
  height: 50px;
  line-height: 50px;
  border-radius: 8%;
  margin: 5px 0;
}
</style>
