export const API_ENDPOINTS = {
  auth: {
    login: '/api/v1/auth/login',
    refresh: '/api/v1/auth/refresh',
    logout: '/api/v1/auth/logout',
    me: '/api/v1/auth/me',
    forgotPassword: '/api/v1/auth/forgot-password',
    resetPassword: '/api/v1/auth/reset-password',
  },
  usuarios: {
    base: '/api/v1/usuarios',
  },
  roles: {
    base: '/api/v1/roles',
  },
  destacamentos: {
    base: '/api/v1/destacamentos',
    porId: (id: number) => `/api/v1/destacamentos/${id}`,
    activar: (id: number) => `/api/v1/destacamentos/${id}/activar`,
    desactivar: (id: number) => `/api/v1/destacamentos/${id}/desactivar`,
  },
  ninos: {
    base: '/api/v1/ninos',
    porId: (id: number) => `/api/v1/ninos/${id}`,
    porDestacamento: (destacamentoId: number) => `/api/v1/destacamentos/${destacamentoId}/ninos`,
  },
  catalogo: {
    categorias: '/api/v1/catalogo/categorias',
    insignias: '/api/v1/catalogo/insignias',
  },
  asistencia: {
    porDestacamento: (destacamentoId: number) => `/api/v1/destacamentos/${destacamentoId}/asistencia`,
  },
  actividades: {
    base: '/api/v1/actividades',
    porDestacamento: (destacamentoId: number) => `/api/v1/destacamentos/${destacamentoId}/actividades`,
  },
}
