import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import Home from '../views/home.vue'
import About from '../views/about.vue'
import AdminEbook from '../views/admin/admin-ebook.vue'
import AdminCategory from '../views/admin/admin-category.vue'
import AdminDoc from '../views/admin/admin-doc.vue'
import AdminUser from '../views/admin/admin-user.vue'
import Doc from '../views/doc.vue'


import Test from '../views/admin/test.vue'

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    name: 'Home',
    component: Home,
    meta:{
      flag:true,
    }
  },
  {
    path: '/about',
    name: 'About',
    component: About,
    meta:{
      flag:true,
    }
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    //component: () => import(/* webpackChunkName: "about" */ '../views/about.vue')
  },
  {
    path: '/doc',
    name: 'Doc',
    component:Doc,
    meta:{
      flag:true,
    }
  },
  {
    path: '/admin/ebook',
    name: 'AdminEbook',
    component: AdminEbook,
    meta:{
      flag:true,
    }
  },
  {
    path: '/admin/category',
    name: 'AdminCategory',
    component: AdminCategory,
    meta:{
      flag:true,
    }
  },
  {
    path: '/admin/doc',
    name: 'AdminDoc',
    component:AdminDoc,
    meta:{
      flag:true,
    }
  },
  {
    path: '/admin/user',
    name: 'AdminUser',
    component: AdminUser,
    meta:{
      flag:true,
    }
  },
  {
    path:'/test',
    name:'Test',
    component:Test,
    meta:{
      flag:false,
    }
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

router.beforeEach((to, from, next) => {
  //  从其他地方访问是否有这个地址
  if(to.matched.length === 0) { //没有匹配到当前路由
    next('/test')
  }
  next();
});

export default router
