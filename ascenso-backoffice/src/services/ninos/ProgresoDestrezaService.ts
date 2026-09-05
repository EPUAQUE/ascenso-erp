import { apiClient } from '@/services/http/ApiClient'
import { API_ENDPOINTS } from '@/config/endpoints'
import type { ProgresoDestreza, RegistrarProgresoDestrezaRequest } from '@/types/ninos'

class ProgresoDestrezaService {
  listar(destacamentoId: number, ninoId: number) {
    return apiClient.get<ProgresoDestreza[]>(API_ENDPOINTS.progresoDestrezas.porNino(destacamentoId, ninoId))
  }

  registrar(destacamentoId: number, ninoId: number, request: RegistrarProgresoDestrezaRequest) {
    return apiClient.post<ProgresoDestreza>(
      API_ENDPOINTS.progresoDestrezas.porNino(destacamentoId, ninoId),
      request,
    )
  }
}

export const progresoDestrezaService = new ProgresoDestrezaService()
