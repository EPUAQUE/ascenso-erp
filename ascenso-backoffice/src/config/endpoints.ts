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
    porDestacamento: (destacamentoId: number) => `/api/v1/destacamentos/${destacamentoId}/ninos`,
    porId: (destacamentoId: number, id: number) => `/api/v1/destacamentos/${destacamentoId}/ninos/${id}`,
    promover: (destacamentoId: number, id: number) =>
      `/api/v1/destacamentos/${destacamentoId}/ninos/${id}/promover`,
    activar: (destacamentoId: number, id: number) =>
      `/api/v1/destacamentos/${destacamentoId}/ninos/${id}/activar`,
    desactivar: (destacamentoId: number, id: number) =>
      `/api/v1/destacamentos/${destacamentoId}/ninos/${id}/desactivar`,
  },
  progresoLibros: {
    porNino: (destacamentoId: number, ninoId: number) =>
      `/api/v1/destacamentos/${destacamentoId}/ninos/${ninoId}/progreso-libros`,
  },
  progresoDestrezas: {
    porNino: (destacamentoId: number, ninoId: number) =>
      `/api/v1/destacamentos/${destacamentoId}/ninos/${ninoId}/progreso-destrezas`,
  },
  progresoLiderazgos: {
    porNino: (destacamentoId: number, ninoId: number) =>
      `/api/v1/destacamentos/${destacamentoId}/ninos/${ninoId}/progreso-liderazgos`,
  },
  progresoRequisitos: {
    porNino: (destacamentoId: number, ninoId: number) =>
      `/api/v1/destacamentos/${destacamentoId}/ninos/${ninoId}/progreso-requisitos`,
  },
  medallas: {
    porNino: (destacamentoId: number, ninoId: number) =>
      `/api/v1/destacamentos/${destacamentoId}/ninos/${ninoId}/medallas`,
  },
  logroMayor: {
    porNino: (destacamentoId: number, ninoId: number) =>
      `/api/v1/destacamentos/${destacamentoId}/ninos/${ninoId}/logro-mayor`,
  },
  catalogo: {
    grupos: '/api/v1/catalogo/grupos',
    grupoPorId: (id: number) => `/api/v1/catalogo/grupos/${id}`,
    aniosPrograma: '/api/v1/catalogo/anios-programa',
    anioProgramaPorId: (id: number) => `/api/v1/catalogo/anios-programa/${id}`,
    librosBiblicos: '/api/v1/catalogo/libros-biblicos',
    libroBiblicoPorId: (id: number) => `/api/v1/catalogo/libros-biblicos/${id}`,
    libroBiblicoActivar: (id: number) => `/api/v1/catalogo/libros-biblicos/${id}/activar`,
    libroBiblicoDesactivar: (id: number) => `/api/v1/catalogo/libros-biblicos/${id}/desactivar`,
    destrezas: '/api/v1/catalogo/destrezas',
    destrezaPorId: (id: number) => `/api/v1/catalogo/destrezas/${id}`,
    destrezaActivar: (id: number) => `/api/v1/catalogo/destrezas/${id}/activar`,
    destrezaDesactivar: (id: number) => `/api/v1/catalogo/destrezas/${id}/desactivar`,
    liderazgos: '/api/v1/catalogo/liderazgos',
    liderazgoPorId: (id: number) => `/api/v1/catalogo/liderazgos/${id}`,
    liderazgoActivar: (id: number) => `/api/v1/catalogo/liderazgos/${id}/activar`,
    liderazgoDesactivar: (id: number) => `/api/v1/catalogo/liderazgos/${id}/desactivar`,
    pasosRequeridos: '/api/v1/catalogo/pasos-requeridos',
    pasoRequeridoPorId: (id: number) => `/api/v1/catalogo/pasos-requeridos/${id}`,
    reglasAsistencia: '/api/v1/catalogo/reglas-asistencia',
    reglaAsistenciaVigente: '/api/v1/catalogo/reglas-asistencia/vigente',
    reglaAsistenciaPorId: (id: number) => `/api/v1/catalogo/reglas-asistencia/${id}`,
  },
  trimestres: {
    base: '/api/v1/trimestres',
    porId: (id: number) => `/api/v1/trimestres/${id}`,
  },
  asistencia: {
    porNino: (destacamentoId: number, ninoId: number) =>
      `/api/v1/destacamentos/${destacamentoId}/ninos/${ninoId}/asistencias`,
    porId: (destacamentoId: number, ninoId: number, id: number) =>
      `/api/v1/destacamentos/${destacamentoId}/ninos/${ninoId}/asistencias/${id}`,
  },
  actividades: {
    porDestacamento: (destacamentoId: number) => `/api/v1/destacamentos/${destacamentoId}/actividades`,
    porId: (destacamentoId: number, id: number) =>
      `/api/v1/destacamentos/${destacamentoId}/actividades/${id}`,
  },
  anuncios: {
    base: '/api/v1/anuncios',
    porId: (id: number) => `/api/v1/anuncios/${id}`,
    activar: (id: number) => `/api/v1/anuncios/${id}/activar`,
    desactivar: (id: number) => `/api/v1/anuncios/${id}/desactivar`,
  },
  ninoPadre: {
    porNino: (destacamentoId: number, ninoId: number) =>
      `/api/v1/destacamentos/${destacamentoId}/ninos/${ninoId}/padres`,
    porUsuario: (destacamentoId: number, ninoId: number, usuarioId: number) =>
      `/api/v1/destacamentos/${destacamentoId}/ninos/${ninoId}/padres/${usuarioId}`,
  },
}
