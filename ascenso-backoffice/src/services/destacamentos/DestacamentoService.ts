import { apiClient } from '@/services/http/ApiClient'
import { API_ENDPOINTS } from '@/config/endpoints'
import type {
  ActualizarDestacamentoRequest,
  CrearDestacamentoRequest,
  Destacamento,
} from '@/types/destacamentos'

class DestacamentoService {
  listar() {
    return apiClient.get<Destacamento[]>(API_ENDPOINTS.destacamentos.base)
  }

  crear(request: CrearDestacamentoRequest) {
    return apiClient.post<Destacamento>(API_ENDPOINTS.destacamentos.base, request)
  }

  actualizar(id: number, request: ActualizarDestacamentoRequest) {
    return apiClient.put<Destacamento>(API_ENDPOINTS.destacamentos.porId(id), request)
  }

  activar(id: number) {
    return apiClient.post<void>(API_ENDPOINTS.destacamentos.activar(id))
  }

  desactivar(id: number) {
    return apiClient.post<void>(API_ENDPOINTS.destacamentos.desactivar(id))
  }
}

export const destacamentoService = new DestacamentoService()
