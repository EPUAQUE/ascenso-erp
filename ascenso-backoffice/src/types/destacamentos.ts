export type ModoCorteAnio = 'CALENDARIO' | 'ANIVERSARIO_INGRESO'

export interface Destacamento {
  id: number
  numeroUnico: string
  nombre: string
  iglesiaNombre: string
  direccion: string
  modoCorteAnio: ModoCorteAnio
  activo: boolean
}

export interface CrearDestacamentoRequest {
  numeroUnico: string
  nombre: string
  iglesiaNombre: string
  direccion: string | null
  modoCorteAnio: ModoCorteAnio
}

export interface ActualizarDestacamentoRequest {
  nombre: string
  iglesiaNombre: string
  direccion: string | null
  modoCorteAnio: ModoCorteAnio
}
