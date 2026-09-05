import { apiClient } from '@/services/http/ApiClient'
import { API_ENDPOINTS } from '@/config/endpoints'
import type { ProgresoLibro, RegistrarProgresoLibroRequest } from '@/types/ninos'

class ProgresoLibroService {
  listar(destacamentoId: number, ninoId: number) {
    return apiClient.get<ProgresoLibro[]>(API_ENDPOINTS.progresoLibros.porNino(destacamentoId, ninoId))
  }

  registrar(destacamentoId: number, ninoId: number, request: RegistrarProgresoLibroRequest) {
    return apiClient.post<ProgresoLibro>(
      API_ENDPOINTS.progresoLibros.porNino(destacamentoId, ninoId),
      request,
    )
  }
}

export const progresoLibroService = new ProgresoLibroService()
