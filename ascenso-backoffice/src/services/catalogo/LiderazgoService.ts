import { apiClient } from '@/services/http/ApiClient'
import { API_ENDPOINTS } from '@/config/endpoints'
import type { GuardarLiderazgoRequest, Liderazgo } from '@/types/catalogo'

class LiderazgoService {
  listar(grupoId?: number) {
    return apiClient.get<Liderazgo[]>(API_ENDPOINTS.catalogo.liderazgos, {
      params: grupoId ? { grupoId } : undefined,
    })
  }

  crear(request: GuardarLiderazgoRequest) {
    return apiClient.post<Liderazgo>(API_ENDPOINTS.catalogo.liderazgos, request)
  }

  actualizar(id: number, request: GuardarLiderazgoRequest) {
    return apiClient.put<Liderazgo>(API_ENDPOINTS.catalogo.liderazgoPorId(id), request)
  }

  activar(id: number) {
    return apiClient.post<void>(API_ENDPOINTS.catalogo.liderazgoActivar(id))
  }

  desactivar(id: number) {
    return apiClient.post<void>(API_ENDPOINTS.catalogo.liderazgoDesactivar(id))
  }
}

export const liderazgoService = new LiderazgoService()
