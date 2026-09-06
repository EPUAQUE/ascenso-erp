import { apiClient } from '@/services/http/ApiClient'
import { API_ENDPOINTS } from '@/config/endpoints'
import type {
  AsignarDestacamentoRolRequest,
  CrearUsuarioRequest,
  RestablecerPasswordResponse,
  Usuario,
  UsuarioDestacamento,
} from '@/types/seguridad'

class UsuarioService {
  listar() {
    return apiClient.get<Usuario[]>(API_ENDPOINTS.usuarios.base)
  }

  crear(request: CrearUsuarioRequest) {
    return apiClient.post<Usuario>(API_ENDPOINTS.usuarios.base, request)
  }

  listarDestacamentos(usuarioId: number) {
    return apiClient.get<UsuarioDestacamento[]>(API_ENDPOINTS.usuarios.destacamentos(usuarioId))
  }

  asignarDestacamento(usuarioId: number, request: AsignarDestacamentoRolRequest) {
    return apiClient.post<void>(API_ENDPOINTS.usuarios.destacamentos(usuarioId), request)
  }

  restablecerPassword(usuarioId: number) {
    return apiClient.post<RestablecerPasswordResponse>(API_ENDPOINTS.usuarios.restablecerPassword(usuarioId))
  }

  revocarSesiones(usuarioId: number) {
    return apiClient.post<void>(API_ENDPOINTS.usuarios.revocarSesiones(usuarioId))
  }

  desactivar(usuarioId: number) {
    return apiClient.post<Usuario>(API_ENDPOINTS.usuarios.desactivar(usuarioId))
  }

  bloquear(usuarioId: number) {
    return apiClient.post<Usuario>(API_ENDPOINTS.usuarios.bloquear(usuarioId))
  }

  activar(usuarioId: number) {
    return apiClient.post<Usuario>(API_ENDPOINTS.usuarios.activar(usuarioId))
  }
}

export const usuarioService = new UsuarioService()
