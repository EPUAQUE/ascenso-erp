import { apiClient } from '@/services/http/ApiClient'
import { API_ENDPOINTS } from '@/config/endpoints'
import type { Destreza, GuardarDestrezaRequest } from '@/types/catalogo'

class DestrezaService {
  listar(grupoId?: number) {
    return apiClient.get<Destreza[]>(API_ENDPOINTS.catalogo.destrezas, {
      params: grupoId ? { grupoId } : undefined,
    })
  }

  crear(request: GuardarDestrezaRequest) {
    return apiClient.post<Destreza>(API_ENDPOINTS.catalogo.destrezas, request)
  }

  actualizar(id: number, request: GuardarDestrezaRequest) {
    return apiClient.put<Destreza>(API_ENDPOINTS.catalogo.destrezaPorId(id), request)
  }

  activar(id: number) {
    return apiClient.post<void>(API_ENDPOINTS.catalogo.destrezaActivar(id))
  }

  desactivar(id: number) {
    return apiClient.post<void>(API_ENDPOINTS.catalogo.destrezaDesactivar(id))
  }
}

export const destrezaService = new DestrezaService()
