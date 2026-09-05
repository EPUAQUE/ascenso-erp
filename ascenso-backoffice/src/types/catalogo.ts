export type Medalla = 'BRONCE' | 'PLATA' | 'ORO'
export type UnidadAsistencia = 'PORCENTAJE' | 'SESIONES'

export interface Grupo {
  id: number
  nombre: string
  edadMin: number
  edadMax: number
  orden: number
  descripcion: string | null
}

export interface ActualizarGrupoRequest {
  nombre: string
  edadMin: number
  edadMax: number
  orden: number
  descripcion: string | null
}

export interface AnioPrograma {
  id: number
  grupoId: number
  numero: number
  medalla: Medalla | null
  minimoLibros: number
  minimoDestrezas: number
  minimoLiderazgo: number
  esAnioGracia: boolean
}

export interface GuardarAnioProgramaRequest {
  grupoId: number
  numero: number
  medalla: Medalla | null
  minimoLibros: number
  minimoDestrezas: number
  minimoLiderazgo: number
  esAnioGracia: boolean
}

export interface LibroBiblico {
  id: number
  grupoId: number
  titulo: string
  ordenSugerido: number | null
  activo: boolean
}

export interface GuardarLibroBiblicoRequest {
  grupoId: number
  titulo: string
  ordenSugerido: number | null
}

export interface Destreza {
  id: number
  grupoId: number
  nombre: string
  categoria: string | null
  activo: boolean
}

export interface GuardarDestrezaRequest {
  grupoId: number
  nombre: string
  categoria: string | null
}

export interface Liderazgo {
  id: number
  grupoId: number
  nombre: string
  categoria: string | null
  activo: boolean
}

export interface GuardarLiderazgoRequest {
  grupoId: number
  nombre: string
  categoria: string | null
}

export interface PasoRequerido {
  id: number
  anioProgramaId: number
  descripcion: string
  orden: number
}

export interface GuardarPasoRequeridoRequest {
  anioProgramaId: number
  descripcion: string
  orden: number
}

export interface ReglaAsistencia {
  id: number
  unidad: UnidadAsistencia
  minimoRequerido: number
  vigenteDesde: string
}

export interface GuardarReglaAsistenciaRequest {
  unidad: UnidadAsistencia
  minimoRequerido: number
  vigenteDesde: string
}
