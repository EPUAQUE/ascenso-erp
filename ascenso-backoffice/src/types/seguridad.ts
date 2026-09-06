export type EstadoUsuario = 'ACTIVO' | 'INACTIVO' | 'BLOQUEADO'

export interface Usuario {
  id: number
  username: string
  estado: EstadoUsuario
  nombre: string
  telefono: string | null
  correo: string | null
}

export interface CrearUsuarioRequest {
  username: string
  password: string
  nombre: string
  telefono: string | null
  correo: string | null
}

export interface UsuarioDestacamento {
  id: number
  destacamentoId: number
  rolId: number
  rolNombre: string
}

export interface AsignarDestacamentoRolRequest {
  destacamentoId: number
  rolId: number
}

export interface RestablecerPasswordResponse {
  passwordTemporal: string
}

export interface Rol {
  id: number
  nombre: string
  alcanceGlobal: boolean
}

export interface UsuarioNombre {
  id: number
  nombre: string
}
