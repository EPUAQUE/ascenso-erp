import { apiClient } from '@/services/http/ApiClient'
import { API_ENDPOINTS } from '@/config/endpoints'
import type { ProgresoLiderazgo, RegistrarProgresoLiderazgoRequest } from '@/types/ninos'

class ProgresoLiderazgoService {
  listar(destacamentoId: number, ninoId: number) {
    return apiClient.get<ProgresoLiderazgo[]>(
      API_ENDPOINTS.progresoLiderazgos.porNino(destacamentoId, ninoId),
    )
  }

  registrar(destacamentoId: number, ninoId: number, request: RegistrarProgresoLiderazgoRequest) {
    return apiClient.post<ProgresoLiderazgo>(
      API_ENDPOINTS.progresoLiderazgos.porNino(destacamentoId, ninoId),
      request,
    )
  }
}

export const progresoLiderazgoService = new ProgresoLiderazgoService()
