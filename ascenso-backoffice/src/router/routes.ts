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
        component: () => import('@/views/admin/CatalogoLayout.vue'),
        meta: { requiresAuth: true, permission: 'CATALOGO_VER' },
        children: [
          { path: '', redirect: { name: 'catalogo-grupos' } },
          {
            path: 'grupos',
            name: 'catalogo-grupos',
            component: () => import('@/views/admin/catalogo/GruposView.vue'),
            meta: { title: 'Catálogo · Grupos' },
          },
          {
            path: 'grupos/:grupoId',
            component: () => import('@/views/admin/catalogo/GrupoDetalleLayout.vue'),
            props: (route) => ({ grupoId: Number(route.params.grupoId) }),
            children: [
              { path: '', redirect: (to) => ({ name: 'catalogo-grupo-anios', params: to.params }) },
              {
                path: 'anios-programa',
                name: 'catalogo-grupo-anios',
                component: () => import('@/views/admin/catalogo/AniosProgramaView.vue'),
                props: (route) => ({ grupoId: Number(route.params.grupoId) }),
                meta: { title: 'Catálogo · Años de programa' },
              },
              {
                path: 'libros-biblicos',
                name: 'catalogo-grupo-libros',
                component: () => import('@/views/admin/catalogo/LibrosBiblicosView.vue'),
                props: (route) => ({ grupoId: Number(route.params.grupoId) }),
                meta: { title: 'Catálogo · Libros bíblicos' },
              },
              {
                path: 'destrezas',
                name: 'catalogo-grupo-destrezas',
                component: () => import('@/views/admin/catalogo/DestrezasView.vue'),
                props: (route) => ({ grupoId: Number(route.params.grupoId) }),
                meta: { title: 'Catálogo · Destrezas' },
              },
              {
                path: 'liderazgo',
                name: 'catalogo-grupo-liderazgo',
                component: () => import('@/views/admin/catalogo/LiderazgoView.vue'),
                props: (route) => ({ grupoId: Number(route.params.grupoId) }),
                meta: { title: 'Catálogo · Liderazgo' },
              },
              {
                path: 'pasos-requeridos',
                name: 'catalogo-grupo-pasos',
                component: () => import('@/views/admin/catalogo/PasosRequeridosView.vue'),
                props: (route) => ({ grupoId: Number(route.params.grupoId) }),
                meta: { title: 'Catálogo · Pasos requeridos' },
              },
            ],
          },
          {
            path: 'reglas-asistencia',
            name: 'catalogo-reglas',
            component: () => import('@/views/admin/catalogo/ReglasAsistenciaView.vue'),
            meta: { title: 'Catálogo · Reglas de asistencia' },
          },
        ],
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
