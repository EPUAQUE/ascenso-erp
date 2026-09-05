import { apiClient } from '@/services/http/ApiClient'
import { API_ENDPOINTS } from '@/config/endpoints'
import type { ActualizarNinoRequest, CrearNinoRequest, Nino, PromoverNinoRequest } from '@/types/ninos'

class NinoService {
  listar(destacamentoId: number) {
    return apiClient.get<Nino[]>(API_ENDPOINTS.ninos.porDestacamento(destacamentoId))
  }

  obtener(destacamentoId: number, id: number) {
    return apiClient.get<Nino>(API_ENDPOINTS.ninos.porId(destacamentoId, id))
  }

  crear(destacamentoId: number, request: CrearNinoRequest) {
    return apiClient.post<Nino>(API_ENDPOINTS.ninos.porDestacamento(destacamentoId), request)
  }

  actualizar(destacamentoId: number, id: number, request: ActualizarNinoRequest) {
    return apiClient.put<Nino>(API_ENDPOINTS.ninos.porId(destacamentoId, id), request)
  }

  promover(destacamentoId: number, id: number, request: PromoverNinoRequest) {
    return apiClient.post<Nino>(API_ENDPOINTS.ninos.promover(destacamentoId, id), request)
  }

  activar(destacamentoId: number, id: number) {
    return apiClient.post<void>(API_ENDPOINTS.ninos.activar(destacamentoId, id))
  }

  desactivar(destacamentoId: number, id: number) {
    return apiClient.post<void>(API_ENDPOINTS.ninos.desactivar(destacamentoId, id))
  }
}

export const ninoService = new NinoService()
