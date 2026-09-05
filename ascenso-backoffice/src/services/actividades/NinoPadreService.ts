import { apiClient } from '@/services/http/ApiClient'
import { API_ENDPOINTS } from '@/config/endpoints'
import type { NinoPadre, VincularPadreRequest } from '@/types/actividades'

class NinoPadreService {
  listar(destacamentoId: number, ninoId: number) {
    return apiClient.get<NinoPadre[]>(API_ENDPOINTS.ninoPadre.porNino(destacamentoId, ninoId))
  }

  vincular(destacamentoId: number, ninoId: number, request: VincularPadreRequest) {
    return apiClient.post<NinoPadre>(API_ENDPOINTS.ninoPadre.porNino(destacamentoId, ninoId), request)
  }

  desvincular(destacamentoId: number, ninoId: number, usuarioId: number) {
    return apiClient.delete<void>(API_ENDPOINTS.ninoPadre.porUsuario(destacamentoId, ninoId, usuarioId))
  }
}

export const ninoPadreService = new NinoPadreService()
