import { apiClient } from '@/services/http/ApiClient'
import { API_ENDPOINTS } from '@/config/endpoints'
import type { ProgresoRequisito, RegistrarProgresoRequisitoRequest } from '@/types/ninos'

class ProgresoRequisitoService {
  listar(destacamentoId: number, ninoId: number) {
    return apiClient.get<ProgresoRequisito[]>(
      API_ENDPOINTS.progresoRequisitos.porNino(destacamentoId, ninoId),
    )
  }

  registrar(destacamentoId: number, ninoId: number, request: RegistrarProgresoRequisitoRequest) {
    return apiClient.post<ProgresoRequisito>(
      API_ENDPOINTS.progresoRequisitos.porNino(destacamentoId, ninoId),
      request,
    )
  }
}

export const progresoRequisitoService = new ProgresoRequisitoService()
