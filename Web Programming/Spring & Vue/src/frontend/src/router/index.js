import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '@/views/LoginView.vue'
import ShotView from '@/views/ShotView.vue'
import { isLoggedIn } from '../assets/js/auth'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'login',
      component: LoginView
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('@/views/RegisterView.vue')
    },
    {
      path: '/shot',
      name: 'shot',
      component: ShotView
    },
    {
      path :'/:pathMatch(.*)*',
      name: 'not-found',
      component: LoginView
    }
  ]
})

router.beforeEach((to, from) =>{
  if(to.name === "shot"){
    if(!isLoggedIn())return { name: 'login' };
    else return true;
  }
  else{
    return true;
  }
})

export default router
