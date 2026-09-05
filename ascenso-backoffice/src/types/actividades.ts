export interface Actividad {
  id: number
  destacamentoId: number
  titulo: string
  descripcion: string | null
  fechaInicio: string
  fechaFin: string | null
  creadoPor: number
}

export interface GuardarActividadRequest {
  titulo: string
  descripcion: string | null
  fechaInicio: string
  fechaFin: string | null
}

export interface Anuncio {
  id: number
  titulo: string
  descripcion: string | null
  imagenUrl: string | null
  enlaceUrl: string | null
  fechaInicioVisible: string
  fechaFinVisible: string | null
  activo: boolean
  creadoPor: number
}

export interface GuardarAnuncioRequest {
  titulo: string
  descripcion: string | null
  imagenUrl: string | null
  enlaceUrl: string | null
  fechaInicioVisible: string
  fechaFinVisible: string | null
}

export interface NinoPadre {
  ninoId: number
  usuarioId: number
}

export interface VincularPadreRequest {
  usuarioId: number
}
