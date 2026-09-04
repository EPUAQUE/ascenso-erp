import type { RouteRecordRaw } from 'vue-router'

declare module 'vue-router' {
  interface RouteMeta {
    requiresAuth?: boolean
    permission?: string
    title?: string
  }
}

export const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/LoginView.vue'),
    meta: { requiresAuth: false },
  },
  {
    path: '/olvide-password',
    name: 'olvide-password',
    component: () => import('@/views/ForgotPasswordView.vue'),
    meta: { requiresAuth: false },
  },
  {
    path: '/restablecer-password',
    name: 'restablecer-password',
    component: () => import('@/views/ResetPasswordView.vue'),
    meta: { requiresAuth: false },
  },
  {
    path: '/forbidden',
    name: 'forbidden',
    component: () => import('@/views/ForbiddenView.vue'),
    meta: { requiresAuth: false },
  },
  {
    path: '/',
    component: () => import('@/components/layout/AdminLayout.vue'),
    meta: { requiresAuth: true },
    children: [
      { path: '', name: 'app-home', redirect: '/ninos' },
      {
        path: 'ninos',
        name: 'ninos',
        component: () => import('@/views/admin/ProximamenteView.vue'),
        meta: { requiresAuth: true, title: 'Niños' },
      },
      {
        path: 'catalogo',
        name: 'catalogo',
        component: () => import('@/views/admin/ProximamenteView.vue'),
        meta: { requiresAuth: true, title: 'Catálogo' },
      },
      {
        path: 'asistencia',
        name: 'asistencia',
        component: () => import('@/views/admin/ProximamenteView.vue'),
        meta: { requiresAuth: true, title: 'Asistencia' },
      },
      {
        path: 'actividades',
        name: 'actividades',
        component: () => import('@/views/admin/ProximamenteView.vue'),
        meta: { requiresAuth: true, title: 'Actividades' },
      },
    ],
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/',
  },
]
