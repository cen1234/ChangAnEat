import { createRouter, createWebHistory } from 'vue-router'
import layout from '../layout/layout.vue'
// import customer_Store from '../views/customer-store.vue'

const routes = [
  {
    path: '/api',
    name: 'layout',
    component: layout,
    children:[
      {
        path: '/employee',
        name: 'Employee',
        component: () => import( '../views/Employee')
      },
      {
        path: '/order',
        name: 'Order',
        component: () => import( '../views/Order')
      },
      {
        path: '/food',
        name: 'Food',
        component: () => import( '../views/Food')
      },
      {
        path: '/classification',
        name: 'Classification',
        component: () => import( '../views/Classification')
      },
      {
        path: '/package',
        name: 'Package',
        component: () => import( '../views/Package')
      },
      {
        path: '/managerperson',
        name: 'ManagerPerson',
        component: () => import( '../views/ManagerPerson')
      }
    ]
  },
  {
    path: '/',
    name: 'login',
    component: () => import( '../views/login.vue')
  },
  
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router
