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
        component: () => import('@/views/admin/NinosView.vue'),
        meta: { requiresAuth: true, title: 'Niños', permission: 'NINOS_VER' },
      },
      {
        path: 'ninos/:id',
        name: 'nino-detalle',
        component: () => import('@/views/admin/NinoDetalleView.vue'),
        meta: { requiresAuth: true, title: 'Ficha del niño', permission: 'NINOS_VER' },
      },
      {
        path: 'catalogo',
        name: 'catalogo',
        component: () => import('@/views/admin/CatalogoView.vue'),
        meta: { requiresAuth: true, title: 'Catálogo', permission: 'CATALOGO_VER' },
      },
      {
        path: 'asistencia',
        name: 'asistencia',
        component: () => import('@/views/admin/AsistenciaView.vue'),
        meta: { requiresAuth: true, title: 'Asistencia', permission: 'NINOS_VER' },
      },
      {
        path: 'actividades',
        name: 'actividades',
        component: () => import('@/views/admin/ActividadesView.vue'),
        meta: { requiresAuth: true, title: 'Actividades', permission: 'ACTIVIDADES_VER' },
      },
    ],
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/',
  },
]
