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
        path: 'grupos',
        name: 'grupos',
        component: () => import('@/views/admin/GruposView.vue'),
        meta: { requiresAuth: true, title: 'Grupos', permission: 'CATALOGO_VER' },
      },
      {
        path: 'grupos/:grupoId',
        component: () => import('@/views/admin/grupos/GrupoDetalleLayout.vue'),
        props: (route) => ({ grupoId: Number(route.params.grupoId) }),
        meta: { requiresAuth: true, permission: 'CATALOGO_VER' },
        children: [
          { path: '', redirect: (to) => ({ name: 'grupo-anios', params: to.params }) },
          {
            path: 'anios-programa',
            name: 'grupo-anios',
            component: () => import('@/views/admin/grupos/AniosProgramaView.vue'),
            props: (route) => ({ grupoId: Number(route.params.grupoId) }),
            meta: { title: 'Años de programa' },
          },
          {
            path: 'libros-biblicos',
            name: 'grupo-libros',
            component: () => import('@/views/admin/grupos/LibrosBiblicosView.vue'),
            props: (route) => ({ grupoId: Number(route.params.grupoId) }),
            meta: { title: 'Libros bíblicos' },
          },
          {
            path: 'destrezas',
            name: 'grupo-destrezas',
            component: () => import('@/views/admin/grupos/DestrezasView.vue'),
            props: (route) => ({ grupoId: Number(route.params.grupoId) }),
            meta: { title: 'Destrezas' },
          },
          {
            path: 'liderazgo',
            name: 'grupo-liderazgo',
            component: () => import('@/views/admin/grupos/LiderazgoView.vue'),
            props: (route) => ({ grupoId: Number(route.params.grupoId) }),
            meta: { title: 'Liderazgo' },
          },
          {
            path: 'pasos-requeridos',
            name: 'grupo-pasos',
            component: () => import('@/views/admin/grupos/PasosRequeridosView.vue'),
            props: (route) => ({ grupoId: Number(route.params.grupoId) }),
            meta: { title: 'Pasos requeridos' },
          },
        ],
      },
      {
        path: 'reglas-asistencia',
        name: 'reglas-asistencia',
        component: () => import('@/views/admin/ReglasAsistenciaView.vue'),
        meta: { requiresAuth: true, title: 'Reglas de asistencia', permission: 'CATALOGO_VER' },
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
